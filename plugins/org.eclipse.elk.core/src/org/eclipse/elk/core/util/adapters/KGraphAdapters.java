/*******************************************************************************
 * Copyright (c) 2014, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.util.adapters;

import java.util.Comparator;
import java.util.List;

import org.eclipse.elk.core.klayoutdata.KInsets;
import org.eclipse.elk.core.klayoutdata.KLayoutData;
import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.KimlUtil;
import org.eclipse.elk.core.util.adapters.GraphAdapters.EdgeAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.GraphAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.GraphElementAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.LabelAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.NodeAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.PortAdapter;
import org.eclipse.elk.core.util.nodespacing.LabelSide;
import org.eclipse.elk.core.util.nodespacing.Spacing.Insets;
import org.eclipse.elk.core.util.nodespacing.Spacing.Margins;
import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KGraphElement;
import org.eclipse.elk.graph.KLabel;
import org.eclipse.elk.graph.KNode;
import org.eclipse.elk.graph.KPort;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;
import org.eclipse.emf.common.util.ECollections;

import com.google.common.collect.Lists;

/**
 * Contains implementations of the {@link GraphAdapters} interfaces for the KGraph. To obtain an
 * adapter for a full KGraph, simply call {@link #adapt(KGraph)}. To obtain an adapter only for a
 * single node, call {@link #adaptSingleNode(KNode)}.
 * 
 * @author uru
 */
public final class KGraphAdapters {

    private KGraphAdapters() {
        throw new IllegalStateException("Private constructor instantiation! Bad!");
    }

    /**
     * Creates the necessary adapters for the KGraph rooted at the given node.
     * 
     * @param graph
     *            the graph that should be wrapped in an adapter
     * @return an {@link KGraphAdapter} for the passed graph.
     */
    public static KGraphAdapter adapt(final KNode graph) {
        return new KGraphAdapter(graph);
    }

    /**
     * Creates a single node adapter for the given node.
     * 
     * @param node
     *            the node that should be wrapped in an adapter
     * @return an {@link KNodeAdapter} for the passed node.
     */
    public static KNodeAdapter adaptSingleNode(final KNode node) {
        return new KNodeAdapter(node);
    }

    /**
     * Implements basic adpater functionality for {@link KGraphElement}s.
     */
    private abstract static class AbstractKGraphElementAdapter<T extends KGraphElement> implements
            GraphElementAdapter<T> {
        
        private static final IProperty<Float> OFFSET_PROXY = new Property<Float>(
                LayoutOptions.OFFSET, 0.0f);
        
        // let the elements be accessed by extending classes
        // CHECKSTYLEOFF VisibilityModifier
        /** The wrapped element. */
        protected T element;
        /** The layout data of the wrapped element. */
        protected KShapeLayout layout;
        // CHECKSTYLEON VisibilityModifier
        /**
         * Internally used versatile data field. Can be used for arbitrary information.
         * No assumptions about its value or validity should be made.
         */
        private int id;
        
        
        /**
         * Creates a new adapter for the given graph element.
         * 
         * @param element
         *            the element to be wrapped in this adapter.
         */
        protected AbstractKGraphElementAdapter(final T element) {
            this.element = element;
            
            try {
                layout = element.getData(KShapeLayout.class);
            } catch (ClassCastException cce) {
                throw new RuntimeException(
                        "Graph adapters are only supported for shape-full types.");
            }
        }


        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        public <P> P getProperty(final IProperty<P> prop) {
            // the nodespacing implementation requires a default value for the offset property
            if (prop.equals(LayoutOptions.OFFSET)) {
                return (P) layout.getProperty(OFFSET_PROXY);
            }
            
            return layout.getProperty(prop);
        }

        /**
         * {@inheritDoc}
         */
        public KVector getPosition() {
            return new KVector(layout.getXpos(), layout.getYpos());
        }

        /**
         * {@inheritDoc}
         */
        public KVector getSize() {
            return new KVector(layout.getWidth(), layout.getHeight());
        }

        /**
         * {@inheritDoc}
         */
        public void setSize(final KVector size) {
            layout.setWidth((float) size.x);
            layout.setHeight((float) size.y);
        }

        /**
         * {@inheritDoc}
         */
        public void setPosition(final KVector pos) {
            layout.setXpos((float) pos.x);
            layout.setYpos((float) pos.y);
        }

        /**
         * {@inheritDoc}
         */
        public Insets getInsets() {
            KInsets kinsets = layout.getInsets();
            Insets insets =
                    new Insets(kinsets.getTop(), kinsets.getLeft(), kinsets.getBottom(),
                            kinsets.getRight());
            return insets;
        }

        /**
         * {@inheritDoc}
         */
        public void setInsets(final Insets insets) {
            layout.getInsets().setLeft((float) insets.left);
            layout.getInsets().setTop((float) insets.top);
            layout.getInsets().setRight((float) insets.right);
            layout.getInsets().setBottom((float) insets.bottom);
        }

        /**
         * {@inheritDoc}
         */
        public Margins getMargin() {
            Margins margins = layout.getProperty(LayoutOptions.MARGINS);
            if (margins == null) {
                margins = new Margins();
            }
            return margins;
        }

        /**
         * {@inheritDoc}
         */
        public void setMargin(final Margins margin) {
            // analog to the insets case, we copy the margins object here
            Margins newMargin = new Margins(margin); 
            layout.setProperty(LayoutOptions.MARGINS, newMargin);
        }
        
        /**
         * {@inheritDoc}
         */
        public int getVolatileId() {
            return id;
        }

        /**
         * {@inheritDoc}
         */
        public void setVolatileId(final int volatileId) {
            this.id = volatileId;
        }
    }

    /**
     * Adapter for KGraphs rooted at a given node.
     */
    public static final class KGraphAdapter extends AbstractKGraphElementAdapter<KNode> implements
            GraphAdapter<KNode> {
        
        /** cached list of child node adapters. */
        private List<NodeAdapter<?>> childNodes = null;
        
        /**
         * Creates a new adapter for the KGraph rooted at the given node.
         * 
         * @param node root of the KGraph to be adapted.
         */
        private KGraphAdapter(final KNode node) {
            super(node);
        }

        /**
         * {@inheritDoc}
         */
        public Iterable<NodeAdapter<?>> getNodes() {
            if (childNodes == null) {
                childNodes = Lists.newArrayListWithExpectedSize(element.getChildren().size());
                for (KNode n : element.getChildren()) {
                    childNodes.add(new KNodeAdapter(n));
                }
            }
            return childNodes;
        }
    }

    /**
     * Adapter for {@link KNode}s.
     */
    public static final class KNodeAdapter extends AbstractKGraphElementAdapter<KNode> implements
            NodeAdapter<KNode> {
        
        /** Cached list of label adapters. */
        private List<LabelAdapter<?>> labelAdapters = null;
        /** Cached list of port adapters. */
        private List<PortAdapter<?>> portAdapters = null;
        /** Cached list of edge adapters for incoming edges. */
        private List<EdgeAdapter<?>> incomingEdgeAdapters = null;
        /** Cached list of edge adapters for outgoing edges. */
        private List<EdgeAdapter<?>> outgoingEdgeAdapters = null;
        
        
        /**
         * Creates a new adapter for the given node.
         * 
         * @param node
         *            the node to adapt.
         */
        private KNodeAdapter(final KNode node) {
            super(node);
        }
        

        /**
         * {@inheritDoc}
         */
        public List<LabelAdapter<?>> getLabels() {
            if (labelAdapters == null) {
                labelAdapters = Lists.newArrayListWithExpectedSize(element.getLabels().size());
                for (KLabel l : element.getLabels()) {
                    labelAdapters.add(new KLabelAdapter(l));
                }
            }
            return labelAdapters;
        }

        /**
         * {@inheritDoc}
         */
        public List<PortAdapter<?>> getPorts() {
            if (portAdapters == null) {
                portAdapters = Lists.newArrayListWithExpectedSize(element.getPorts().size());
                for (KPort p : element.getPorts()) {
                    portAdapters.add(new KPortAdapter(p));
                }
            }
            return portAdapters;
        }
        
        /**
         * {@inheritDoc}
         */
        public Iterable<EdgeAdapter<?>> getIncomingEdges() {
            if (incomingEdgeAdapters == null) {
                incomingEdgeAdapters = Lists.newArrayListWithExpectedSize(
                        element.getIncomingEdges().size());
                for (KEdge e : element.getIncomingEdges()) {
                    incomingEdgeAdapters.add(new KEdgeAdapter(e));
                }
            }
            return incomingEdgeAdapters;
        }
        
        /**
         * {@inheritDoc}
         */
        public Iterable<EdgeAdapter<?>> getOutgoingEdges() {
            if (outgoingEdgeAdapters == null) {
                outgoingEdgeAdapters = Lists.newArrayListWithExpectedSize(
                        element.getOutgoingEdges().size());
                for (KEdge e : element.getOutgoingEdges()) {
                    outgoingEdgeAdapters.add(new KEdgeAdapter(e));
                }
            }
            return outgoingEdgeAdapters;
        }

        /**
         * {@inheritDoc}
         */
        public void sortPortList() {
            sortPortList(DEFAULT_PORTLIST_SORTER);
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        public void sortPortList(final Comparator<?> comparator) {
            // Iterate through the nodes of all layers
            KLayoutData layout = element.getData(KLayoutData.class);
            if (layout.getProperty(LayoutOptions.PORT_CONSTRAINTS).isOrderFixed()) {
                ECollections.sort(element.getPorts(), (Comparator<KPort>) comparator);
            }
        }

        /**
         * {@inheritDoc}
         */
        public boolean isCompoundNode() {
            return !element.getChildren().isEmpty();
        }
    }

    /**
     * Adapter for {@link KLabel}s.
     */
    private static final class KLabelAdapter extends AbstractKGraphElementAdapter<KLabel> implements
            LabelAdapter<KLabel> {

        /**
         * Creates a new adapter for the given label.
         * 
         * @param label
         *            the label to adapt.
         */
        private KLabelAdapter(final KLabel label) {
            super(label);
        }
        

        /**
         * {@inheritDoc}
         */
        public LabelSide getSide() {
            return layout.getProperty(LabelSide.LABEL_SIDE);
        }
    }

    /**
     * Adapter for {@link KPort}s.
     */
    private static final class KPortAdapter extends AbstractKGraphElementAdapter<KPort>
            implements PortAdapter<KPort> {
        
        /** Cached list of label adapters. */
        private List<LabelAdapter<?>> labelAdapters = null;
        /** Cached list of edge adapters for incoming edges. */
        private List<EdgeAdapter<?>> incomingEdgeAdapters = null;
        /** Cached list of edge adapters for outgoing edges. */
        private List<EdgeAdapter<?>> outgoingEdgeAdapters = null;

        
        /**
         * Creates a new adapter for the given port.
         * 
         * @param port the port to adapt.
         */
        private KPortAdapter(final KPort port) {
            super(port);
        }

        
        /**
         * {@inheritDoc}
         */
        public PortSide getSide() {
            return layout.getProperty(LayoutOptions.PORT_SIDE);
        }

        /**
         * {@inheritDoc}
         */
        public List<LabelAdapter<?>> getLabels() {
            if (labelAdapters == null) {
                labelAdapters = Lists.newArrayListWithExpectedSize(element.getLabels().size());
                for (KLabel l : element.getLabels()) {
                    labelAdapters.add(new KLabelAdapter(l));
                }
            }
            return labelAdapters;
        }

        /**
         * {@inheritDoc}
         */
        public Iterable<EdgeAdapter<?>> getIncomingEdges() {
            if (incomingEdgeAdapters == null) {
                // This overestimates the required list size
                incomingEdgeAdapters = Lists.newArrayListWithCapacity(element.getEdges().size());
                for (KEdge e : element.getEdges()) {
                    if (e.getTarget().equals(element)) {
                        incomingEdgeAdapters.add(new KEdgeAdapter(e));
                    }
                }
            }
            return incomingEdgeAdapters;
        }

        /**
         * {@inheritDoc}
         */
        public Iterable<EdgeAdapter<?>> getOutgoingEdges() {
            if (outgoingEdgeAdapters == null) {
                outgoingEdgeAdapters = Lists.newArrayListWithCapacity(element.getEdges().size());
                for (KEdge e : element.getEdges()) {
                    if (e.getSource().equals(element)) {
                        outgoingEdgeAdapters.add(new KEdgeAdapter(e));
                    }
                }
            }
            return outgoingEdgeAdapters;
        }

        /**
         * {@inheritDoc}
         */
        public boolean hasCompoundConnections() {
            KNode node = element.getNode();
            
            for (KEdge edge : element.getEdges()) {
                if (edge.getSource() == node) {
                    // check if the edge's target is a descendant of its source node
                    if (KimlUtil.isDescendant(edge.getTarget(), node)) {
                        return true;
                    }
                } else {
                    // check if the edge's source is a descendant of its source node
                    if (KimlUtil.isDescendant(edge.getSource(), node)) {
                        return true;
                    }
                }
            }
            
            return false;
        }
    }

    /**
     * Adapter for {@link KEdge}s.
     */
    private static final class KEdgeAdapter implements EdgeAdapter<KEdge> {
        
        /** The wrapped edge. */
        private KEdge element;
        /** Cached list of label adapters. */
        private List<LabelAdapter<?>> labelAdapters = null;

        
        /**
         * Creates a new adapter for the given edge.
         * 
         * @param edge
         *            the edge to adapt.
         */
        private KEdgeAdapter(final KEdge edge) {
            this.element = edge;
        }

        /**
         * {@inheritDoc}
         */
        public Iterable<LabelAdapter<?>> getLabels() {
            if (labelAdapters == null) {
                labelAdapters = Lists.newArrayListWithExpectedSize(element.getLabels().size());
                for (KLabel l : element.getLabels()) {
                    labelAdapters.add(new KLabelAdapter(l));
                }
            }
            return labelAdapters;
        }
    }
    
    /**
     * The default comparator for ports. Ports are sorted by side (north, east, south, west) in
     * clockwise order, beginning at the top left corner.
     */
    public static final PortComparator DEFAULT_PORTLIST_SORTER = new PortComparator();
    
    /**
     * A comparator for ports. Ports are sorted by side (north, east, south, west) in clockwise order,
     * beginning at the top left corner.
     */
    public static class PortComparator implements Comparator<KPort> {
        /**
         * {@inheritDoc}
         */
        public int compare(final KPort port1, final KPort port2) {
            KShapeLayout layout1 = port1.getData(KShapeLayout.class);
            KShapeLayout layout2 = port2.getData(KShapeLayout.class);
            int ordinalDifference =
                    layout1.getProperty(LayoutOptions.PORT_SIDE).ordinal()
                            - layout2.getProperty(LayoutOptions.PORT_SIDE).ordinal();

            // Sort by side first
            if (ordinalDifference != 0) {
                return ordinalDifference;
            }

            // In case of equal sides, sort by port index property
            Integer index1 = layout1.getProperty(LayoutOptions.PORT_INDEX);
            Integer index2 = layout2.getProperty(LayoutOptions.PORT_INDEX);
            if (index1 != null && index2 != null) {
                int indexDifference = index1 - index2;
                if (indexDifference != 0) {
                    return indexDifference;
                }
            }

            // In case of equal index, sort by position
            switch (layout1.getProperty(LayoutOptions.PORT_SIDE)) {
            case NORTH:
                // Compare x coordinates
                return Double.compare(layout1.getXpos(), layout2.getXpos());

            case EAST:
                // Compare y coordinates
                return Double.compare(layout1.getYpos(), layout2.getYpos());

            case SOUTH:
                // Compare x coordinates in reversed order
                return Double.compare(layout2.getXpos(), layout1.getXpos());

            case WEST:
                // Compare y coordinates in reversed order
                return Double.compare(layout2.getYpos(), layout1.getYpos());

            default:
                // Port sides should not be undefined
                throw new IllegalStateException("Port side is undefined");
            }
        }
    }
}
