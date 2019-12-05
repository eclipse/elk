/*******************************************************************************
 * Copyright (c) 2014, 2015 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.options.NodeLabelPlacement;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;

/**
 * Utility class that contains methods to split and distribute long labels on
 * {@link org.eclipse.elk.alg.layered.options.NodeType.BIG_NODE}s.
 * 
 * @author uru
 */
public final class BigNodesLabelHandler {

    /**
     * Utility class.
     */
    private BigNodesLabelHandler() {
    }

    /**
     * Distribute, if necessary, the labels of the passed node.
     * 
     * @param node
     *            the original node
     * @param dummies
     *            the list of the dummy nodes to which to distribute the label. Note that the list
     *            should <b>include</b> the initial node.
     * @param chunkWidth
     *            the width of each dummy node
     */
    public static void handle(final LNode node, final List<LNode> dummies, final double chunkWidth) {
        new Handler(node, dummies, chunkWidth);
    }

    /**
     * Internal class storing a state for each node and its labels.
     */
    private static final class Handler {
        private LNode node;
        private int chunks;
        private double minWidth;

        /** The dummy nodes created for this big node (include the node itself at index 0). */
        private ArrayList<LNode> dummies;
        /** Last dummy node, stored for quick access. */
        private LNode lastDummy = null;

        /**
         * A multimap holding the dummies created for a label. It is important to use a
         * LinkedListMultimap in order to retain the order of the dummies.
         */
        private ArrayListMultimap<LLabel, LLabel> dumLabs = ArrayListMultimap.create();

        /** A list of post processing functions to be applied during {@link BigNodesPostProcessor}. */
        private List<Function<Void, Void>> postProcs = Lists.newArrayList();

        /**
         * 
         * @param node
         *            the original node
         * @param dummies
         *            the created dummy nodes
         */
        Handler(final LNode node, final List<LNode> dummies, final double chunkWidth) {
            this.node = node;
            this.chunks = dummies.size();

            // we require fast access to the elements
            if (dummies instanceof ArrayList<?>) {
                this.dummies = (ArrayList<LNode>) dummies;
            } else {
                this.dummies = Lists.newArrayList(dummies);
            }

            lastDummy = this.dummies.get(this.dummies.size() - 1);
            this.minWidth = chunkWidth;

            handleLabels();
        }

        /**
         * Labels require special treatment depending on their value of {@link NodeLabelPlacement}.
         */
        public void handleLabels() {

            // remember the original labels, they will be restored during post processing
            // otherwise the graph exporter is not able to write back the new label positions
            node.setProperty(InternalProperties.BIGNODES_ORIG_LABELS,
                    Lists.newLinkedList(node.getLabels()));

            // assign V_CENTER, H_CENTER node placement to all middle dummy node
            // this is necessary to avoid unnecessary spacing to be introduced due to
            // outside label placement
            for (int i = 1; i < dummies.size() - 1; ++i) {
                dummies.get(i).setProperty(LayeredOptions.NODE_LABELS_PLACEMENT,
                        NodeLabelPlacement.insideCenter());
            }

            // handle every label of the node
            for (final LLabel l : Lists.newLinkedList(node.getLabels())) {

                EnumSet<NodeLabelPlacement> placement =
                        node.getProperty(LayeredOptions.NODE_LABELS_PLACEMENT);

                // we handle two cases differently where labels are placed outside, horizontally
                // left or right and vertically centered
                // apart from that split the label and distribute it among the dummy nodes

                // CHECKSTYLEOFF EmptyBlock
                if (placement.containsAll(EnumSet.of(NodeLabelPlacement.OUTSIDE,
                        NodeLabelPlacement.H_LEFT, NodeLabelPlacement.V_CENTER))) {
                    // leave the label unsplit on the left most node
                    // spacing will be introduced on the left side of
                    // the first big node dummy

                } else if (placement.containsAll(EnumSet.of(NodeLabelPlacement.OUTSIDE,
                        NodeLabelPlacement.H_RIGHT, NodeLabelPlacement.V_CENTER))) {

                    // assign to last node
                    // spacing will be introduced on the right side of
                    // the last dummy node
                    lastDummy.getLabels().add(l);
                    node.getLabels().remove(l);

                    // however, the label's position is calculated relatively to the
                    // last dummy, thus we have to add an offset afterwards
                    Function<Void, Void> postProcess = new Function<Void, Void>() {
                        public Void apply(final Void v) {
                            l.getPosition().x += (minWidth * (chunks - 1));
                            return null;
                        }
                    };
                    node.setProperty(InternalProperties.BIGNODES_POST_PROCESS, postProcess);

                } else {
                    // this case includes NO placement data at all

                    splitAndDistributeLabel(l);

                    // post processing is generated within the method above
                    postProcs.add(funRemoveLabelDummies);
                    node.setProperty(InternalProperties.BIGNODES_POST_PROCESS,
                            CompoundFunction.of(postProcs));
                }
            }
        }

        /**
         * Splits the label in consecutive chunks while the number of chunks corresponds to the
         * number of dummy nodes (including the first node).
         * 
         */
        private void splitAndDistributeLabel(final LLabel lab) {

            // double labelChunkWidth = lab.getSize().x / chunks;

            // split into equal sized chunks
            int length = lab.getText().length();
            int labelChunkSize = (int) Math.ceil(length / (double) chunks);

            String text = lab.getText();
            int lPos = 0, rPos = labelChunkSize;

            for (int i = 0; i < chunks; ++i) {
                String subLabel =
                        text.substring(Math.min(Math.max(0, lPos), length),
                                Math.max(0, Math.min(rPos, length)));
                lPos = rPos;
                rPos += labelChunkSize;

                LNode dummy = dummies.get(i);
                LLabel dumLab = new LLabel(subLabel);
                // TODO as soon as SizeConstraints are to be supported this should be used
                // dumLab.getSize().x = labelChunkWidth;
                dumLab.getSize().y = lab.getSize().y;
                dumLabs.put(lab, dumLab);

                dummy.getLabels().add(dumLab);
            }
            // remove original label
            node.getLabels().remove(lab);

            postProcs.add(getPostProcFunctionForLabel(lab));
        }

        /**
         * Creates a function that will be executed during the {@link BigNodesPostProcessor}.
         * 
         * The position of the split label has to be adapted depending on the specified node label
         * placement.
         */
        private Function<Void, Void> getPostProcFunctionForLabel(final LLabel label) {

            Function<Void, Void> fun = new Function<Void, Void>() {

                public Void apply(final Void v) {
                    EnumSet<NodeLabelPlacement> placement =
                            node.getProperty(LayeredOptions.NODE_LABELS_PLACEMENT);

                    // CHECKSTYLEOFF EmptyBlock
                    if (placement.equals(NodeLabelPlacement.fixed())) {
                        // FIXED label positions
                        // leave as they are

                    } else if (placement.containsAll(EnumSet.of(NodeLabelPlacement.H_LEFT))) {
                        // INSIDE || OUTSIDE
                        // H_LEFT
                        LLabel dumLab = dumLabs.get(label).get(0);
                        label.getPosition().x = dumLab.getPosition().x;
                        label.getPosition().y = dumLab.getPosition().y;

                    } else if (placement.containsAll(EnumSet.of(NodeLabelPlacement.H_RIGHT))) {
                        // INSIDE || OUTSIDE
                        // H_RIGHT
                        LNode rightMostDum = dummies.get(dummies.size() - 1);
                        LLabel rightMostLab = dumLabs.get(label).get(dumLabs.get(label).size() - 1);

                        // get offset on the right side
                        double rightOffset =
                                (rightMostDum.getSize().x)
                                        - (rightMostLab.getPosition().x + rightMostLab.getSize().x);

                        // now get the offset on the left side and use it as the label's position
                        label.getPosition().x = node.getSize().x - rightOffset - label.getSize().x;
                        label.getPosition().y = rightMostLab.getPosition().y;

                    } else if (placement.containsAll(EnumSet.of(NodeLabelPlacement.V_CENTER,
                            NodeLabelPlacement.H_CENTER))) {
                        // V_CENTER && H_CENTER

                        // use any calculated y pos, center manually for x
                        LLabel dumLab = dumLabs.get(label).get(0);

                        // now get the offset on the left side and use it as the label's position
                        label.getPosition().x = (node.getSize().x - label.getSize().x) / 2f;
                        label.getPosition().y = dumLab.getPosition().y;

                    } else if (placement.containsAll(EnumSet.of(NodeLabelPlacement.V_CENTER))) {

                        LLabel dumLab = dumLabs.get(label).get(0);
                        label.getPosition().y = dumLab.getPosition().y;

                    } else if (placement.containsAll(EnumSet.of(NodeLabelPlacement.H_CENTER))) {

                        LLabel dumLab = dumLabs.get(label).get(0);
                        label.getPosition().x = (node.getSize().x - label.getSize().x) / 2f;
                        label.getPosition().y = dumLab.getPosition().y;
                    }

                    return null;
                }
            };

            return fun;
        }

        /**
         * Post processing function removing all created label dummies, i.e labels that do not have
         * an {@link org.eclipse.elk.alg.layered.options.InternalProperties#ORIGIN
         * InternalProperties.ORIGIN}.
         */
        private Function<Void, Void> funRemoveLabelDummies = new Function<Void, Void>() {

            public Void apply(final Void v) {
                // remove all the dummies again
                for (LNode dummy : dummies) {
                    for (LLabel l : Lists.newLinkedList(dummy.getLabels())) {
                        if (l.getProperty(InternalProperties.ORIGIN) == null) {
                            dummy.getLabels().remove(l);
                        }
                    }
                }
                return null;
            }
        };

    }

    /**
     * Class to combine and execution multiple {@link Function} instances.
     * 
     * @author uru
     */
    private static final class CompoundFunction implements Function<Void, Void> {
        private Function<Void, Void>[] funs;

        @SuppressWarnings("unchecked")
        CompoundFunction(final Function<Void, Void>... funs) {
            this.funs = funs;
        }

        public static CompoundFunction of(final List<Function<Void, Void>> funs) {
            @SuppressWarnings("unchecked")
            Function<Void, Void>[] funsArr = new Function[funs.size()];
            int i = 0;
            for (Function<Void, Void> f : funs) {
                funsArr[i++] = f;
            }
            return new CompoundFunction(funsArr);
        }

        public Void apply(final Void v) {
            for (Function<Void, Void> f : funs) {
                f.apply(null);
            }
            return null;
        }
    }

}
