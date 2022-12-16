/*******************************************************************************
 * Copyright (c) 2022 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.alg.layered.p4nodes.NetworkSimplexPlacer;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.MergeRange;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkLabel;
import org.eclipse.elk.graph.ElkNode;

/**
 * @author jep
 *
 */
public final class AdjustNodeLabels implements ILayoutProcessor<LGraph> {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.elk.core.alg.ILayoutProcessor#process(java.lang.Object,
     * org.eclipse.elk.core.util.IElkProgressMonitor)
     */
    @Override
    public void process(final LGraph graph, final IElkProgressMonitor monitor) {
        monitor.begin("Label adjustment", 0);

        // combine node labels if possible
        Direction direction = graph.getProperty(LayeredOptions.DIRECTION);
        if (direction == Direction.DOWN || direction == Direction.UP) {
            // node sizes and label sizes and positions are transposed here
            down(graph);
        } else if (direction == Direction.RIGHT || direction == Direction.LEFT) {
            right(graph);
        }

        // recalculate size of labels and nodes
        LabelManagementProcessor labelManager = new LabelManagementProcessor(false);
        labelManager.process(graph, monitor);
        LabelAndNodeSizeProcessor nodeSizeProcessor = new LabelAndNodeSizeProcessor();
        nodeSizeProcessor.process(graph, monitor);
        NetworkSimplexPlacer placer = new NetworkSimplexPlacer();
        placer.process(graph, monitor);

        monitor.done();
    }

    /**
     * Combines labels if the node size allows it.
     * 
     * @param graph
     *            The graph.
     */
    private void down(final LGraph graph) {
        for (Layer layer : graph) {
            for (LNode node : layer) {
                KVector nodeSize = node.getSize();
                ElkPadding padding = graph.getProperty(LayeredOptions.NODE_LABELS_PADDING);
                // the space that can be used by a label
                double usableSpace = nodeSize.y - padding.top - padding.bottom;

                // determines which labels are tried to merge
                MergeRange range =
                        node.getProperty(LayeredOptions.NODE_PLACEMENT_NETWORK_SIMPLEX_NODE_FLEXIBILITY_LABEL_MERGING);

                List<LLabel> labels = node.getLabels();
                if (range.start != 0 && range.end != 0 && labels.size() >= range.start) {
                    int current = range.start - 1;
                    // needed to delete elkLabels corresponding to the llabels that will be deleted
                    ElkNode elkNode = (ElkNode) node.getProperty(InternalProperties.ORIGIN);
                    List<ElkLabel> originLabels = elkNode.getLabels();

                    // get the values of the first label
                    LLabel lastLabel = labels.get(current);
                    String currentText = lastLabel.getText();
                    double currentWidth = lastLabel.getSize().y;
                    labels.remove(current);

                    // try to merge labels that are in the given range
                    for (int i = range.start; i < range.end && current < labels.size(); i++) {
                        LLabel label = labels.get(current);
                        if (currentWidth + label.getSize().y <= usableSpace) {
                            // labels can be combined
                            currentText += " " + label.getText();
                            currentWidth += label.getSize().y;
                            // original elklabel must be removed
                            originLabels.remove(current + 1);
                        } else {
                            // add a new label with the text of the previous labels
                            LLabel newLabel = addLabel(currentText, lastLabel);
                            labels.add(current, newLabel);
                            // values of the current label
                            currentText = label.getText();
                            currentWidth = label.getSize().y;
                            lastLabel = label;
                            current++;
                        }

                        labels.remove(current);
                    }
                    // add a label for the remaining (combined) labels
                    LLabel newLabel = addLabel(currentText, lastLabel);
                    labels.add(current, newLabel);
                }
            }
        }
    }

    /**
     * Creates a new label with the given attributes and updates the original elkLabel.
     * 
     * @param text
     *            The text of the new label.
     * @param label
     *            The original label from which the properties are copied.
     * @return a new label with the given attributes.
     */
    private LLabel addLabel(final String text, final LLabel label) {
        // create a label with the given values
        LLabel newLabel = new LLabel(text);
        // copy the properties of the original label
        newLabel.copyProperties(label);
        // set the text of the original elk label
        Object origin = newLabel.getProperty(InternalProperties.ORIGIN);
        if (origin instanceof ElkLabel) {
            ElkLabel elkLabel = (ElkLabel) origin;
            elkLabel.setText(newLabel.getText());
        }
        return newLabel;
    }

    private void right(final LGraph graph) {
        // TODO: implement / rework code
        // for (Layer layer : graph) {
        // for (LNode node : layer) {
        // KVector nodeSize = node.getSize();
        // ElkPadding padding = graph.getProperty(LayeredOptions.NODE_LABELS_PADDING);
        // double usableSpace = nodeSize.x - padding.left - padding.right;
        //
        // List<LLabel> labels = node.getLabels();
        // List<LLabel> newLabels = new ArrayList<>();
        // if (labels.size() > 1) {
        // ElkNode elkNode = (ElkNode) node.getProperty(InternalProperties.ORIGIN);
        // List<ElkLabel> originLabels = elkNode.getLabels();
        //
        // LLabel firstLabel = labels.get(0);
        // double labelHeight = labels.get(0).getSize().y;
        // String currentText = labels.get(0).getText();
        // double currentWidth = labels.get(0).getSize().x;
        // labels.remove(0);
        // int counter = 1;
        //
        // while (!labels.isEmpty()) {
        // LLabel label = labels.get(0);
        // if (currentWidth + label.getSize().x < usableSpace) {
        // currentText += " " + label.getText();
        // currentWidth += label.getSize().x;
        // originLabels.remove(counter);
        // } else {
        // LLabel test = new LLabel(currentText);
        // test.getSize().x = currentWidth;
        // test.getSize().y = labelHeight;
        // test.getPosition().x = (node.getSize().x - test.getSize().x) / 2.0;
        // test.getPosition().y = (node.getSize().y - test.getSize().y) / 2.0 + test.getSize().y;
        // newLabels.add(test);
        // currentText = label.getText();
        // currentWidth = label.getSize().x;
        // test.copyProperties(label);
        // counter++;
        // Object origin = test.getProperty(InternalProperties.ORIGIN);
        // if (origin instanceof ElkLabel) {
        // ElkLabel elkLabel = (ElkLabel) origin;
        // elkLabel.setText(test.getText());
        // }
        // }
        //
        // labels.remove(0);
        // }
        //
        // LLabel test = new LLabel(currentText);
        // test.getSize().x = currentWidth;
        // test.getSize().y = labelHeight;
        // test.getPosition().x = (node.getSize().x - test.getSize().x) / 2.0;
        // test.getPosition().y = (node.getSize().y - test.getSize().y) / 2.0 + test.getSize().y;
        // newLabels.add(test);
        // test.copyProperties(firstLabel);
        // Object origin = test.getProperty(InternalProperties.ORIGIN);
        // if (origin instanceof ElkLabel) {
        // ElkLabel elkLabel = (ElkLabel) origin;
        // elkLabel.setText(test.getText());
        // }
        //
        // labels.addAll(newLabels);
        // }
        // }
        // }
    }

}
