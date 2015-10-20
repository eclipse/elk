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
package org.eclipse.elk.layered.graph;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.adapters.GraphAdapters.EdgeAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.GraphAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.GraphElementAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.LabelAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.NodeAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.PortAdapter;
import org.eclipse.elk.core.util.nodespacing.LabelSide;
import org.eclipse.elk.core.util.nodespacing.Spacing.Insets;
import org.eclipse.elk.core.util.nodespacing.Spacing.Margins;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.layered.properties.InternalProperties;

import com.google.common.collect.Lists;

/**
 * Provides implementations of the {@link org.eclipse.elk.core.util.adapters.GraphAdapters
 * GraphAdapters} interfaces for the LGraph. The adapted graph can then be fed to KIML's node size
 * calculation code, for example. To obtain an adapter for an {@link LGraph}, simply call
 * {@link #adapt(LGraph)}.
 * 
 * @author uru
 */
public final class LGraphAdapters {

    private LGraphAdapters() {
        throw new IllegalStateException("Private constructor, not to be instantiated!");
    }
    
    
    /**
     * Adapts the given {@link LGraph}.
     * 
     * @param graph
     *            the graph that should be wrapped in an adapter
     * @return an {@link LGraphAdapter} for the passed graph.
     */
    public static LGraphAdapter adapt(final LGraph graph) {
        return new LGraphAdapter(graph);
    }
    
    
    /**
     * Basic base class for adapters that adapt {@link LGraphElement}s.
     */
    private abstract static class AbstractLShapeAdapter<T extends LShape> implements
            GraphElementAdapter<T> {

        // CHECKSTYLEOFF VisibilityModifier
        /** The wrapped element. */
        protected T element;
        // CHECKSTYLEON VisibilityModifier
        
        
        /**
         * Creates a new adapter for the given element.
         * 
         * @param element
         *            the element to be adapted.
         */
        public AbstractLShapeAdapter(final T element) {
            this.element = element;
        }
        

        /**
         * {@inheritDoc}
         */
        public KVector getSize() {
            return element.getSize();
        }

        /**
         * {@inheritDoc}
         */
        public void setSize(final KVector size) {
            element.getSize().x = size.x;
            element.getSize().y = size.y;
        }

        /**
         * {@inheritDoc}
         */
        public KVector getPosition() {
            return element.getPosition();
        }

        /**
         * {@inheritDoc}
         */
        public void setPosition(final KVector pos) {
            element.getPosition().x = pos.x;
            element.getPosition().y = pos.y;
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        public <P> P getProperty(final IProperty<P> prop) {
            // handle some special cases
            if (prop.equals(LayoutOptions.SPACING)) {
                // cast is ok, as both properties are Floats
                return (P) element.getProperty(InternalProperties.SPACING);
            } else if (prop.equals(LayoutOptions.OFFSET)) {
                return (P) element.getProperty(InternalProperties.OFFSET);
            }

            return element.getProperty(prop);
        }

        /**
         * {@inheritDoc}
         */
        public int getVolatileId() {
            return element.id;
        }

        /**
         * {@inheritDoc}
         */
        public void setVolatileId(final int volatileId) {
            element.id = volatileId;
        }
    }

    /**
     * Adapter for {@link LGraph}s.
     */
    public static final class LGraphAdapter implements GraphAdapter<LGraph> {
        // CHECKSTYLEOFF VisibilityModifier
        /** The wrapped element. */
        protected LGraph element;
        // CHECKSTYLEON VisibilityModifier
        /** List of cached node adapters. */
        private List<NodeAdapter<?>> nodeAdapters = null;

        
        /**
         * Creates a new adapter for the given graph.
         * 
         * @param element
         *            the graph to be adapted.
         */
        private LGraphAdapter(final LGraph element) {
            this.element = element;
        }

        
        /**
         * {@inheritDoc}
         */
        public KVector getSize() {
            return element.getSize();
        }

        /**
         * {@inheritDoc}
         */
        public void setSize(final KVector size) {
            element.getSize().x = size.x;
            element.getSize().y = size.y;
        }

        /**
         * {@inheritDoc}
         */
        public KVector getPosition() {
            throw new UnsupportedOperationException("Not supported by LGraph");
        }

        /**
         * {@inheritDoc}
         */
        public void setPosition(final KVector pos) {
            throw new UnsupportedOperationException("Not supported by LGraph");
        }

        /**
         * {@inheritDoc}
         */
        public <P> P getProperty(final IProperty<P> prop) {
            return element.getProperty(prop);
        }

        /**
         * {@inheritDoc}
         */
        public Iterable<NodeAdapter<?>> getNodes() {
            if (nodeAdapters == null) {
                nodeAdapters = Lists.newArrayList();
                // We completely ignore layerless nodes here since they are currently not of interest
                // to anyone using these adapters
                for (Layer l : element.getLayers()) {
                    for (LNode n : l.getNodes()) {
                        nodeAdapters.add(new LNodeAdapter(n));
                    }
                }
            }
            return nodeAdapters;
        }

        /**
         * {@inheritDoc}
         */
        public int getVolatileId() {
            return element.id;
        }

        /**
         * {@inheritDoc}
         */
        public void setVolatileId(final int volatileId) {
            element.id = volatileId;
        }
    }

    /**
     * Adapter for {@link LNode}s.
     */
    static final class LNodeAdapter extends AbstractLShapeAdapter<LNode> implements NodeAdapter<LNode> {
        /** List of cached label adapters. */
        private List<LabelAdapter<?>> labelAdapters = null;
        /** List of cached port adapters. */
        private List<PortAdapter<?>> portAdapters = null;
        
        
        /**
         * Creates a new adapter for the given node.
         * 
         * @param element
         *            the node to be adapted.
         */
        public LNodeAdapter(final LNode element) {
            super(element);
        }
        

        /**
         * {@inheritDoc}
         */
        public Iterable<LabelAdapter<?>> getLabels() {
            if (labelAdapters == null) {
                labelAdapters = Lists.newArrayListWithCapacity(element.getLabels().size());
                for (LLabel l : element.getLabels()) {
                    labelAdapters.add(new LLabelAdapter(l));
                }
            }
            return labelAdapters;
        }

        /**
         * {@inheritDoc}
         */
        public Iterable<PortAdapter<?>> getPorts() {
            if (portAdapters == null) {
                portAdapters = Lists.newArrayListWithCapacity(element.getPorts().size());
                for (LPort p : element.getPorts()) {
                    portAdapters.add(new LPortAdapter(p));
                }
            }
            return portAdapters;
        }
        
        /**
         * {@inheritDoc}
         */
        public Iterable<EdgeAdapter<?>> getIncomingEdges() {
            // we have no directly connected edges
            return Collections.emptyList();
        }

        /**
         * {@inheritDoc}
         */
        public Iterable<EdgeAdapter<?>> getOutgoingEdges() {
         // we have no directly connected edges
            return Collections.emptyList();
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
            if (element.getProperty(LayoutOptions.PORT_CONSTRAINTS).isOrderFixed()) {
                // We need to sort the port list accordingly
                Collections.sort(element.getPorts(), (Comparator<LPort>) comparator);
            }
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean isCompoundNode() {
            return element.getProperty(InternalProperties.COMPOUND_NODE);
        }

        /**
         * {@inheritDoc}
         */
        public Insets getInsets() {
            LInsets linsets = element.getInsets();
            return new Insets(linsets.top, linsets.left, linsets.bottom, linsets.right);
        }

        /**
         * {@inheritDoc}
         */
        public void setInsets(final Insets insets) {
            element.getInsets().left = insets.left;
            element.getInsets().top = insets.top;
            element.getInsets().right = insets.right;
            element.getInsets().bottom = insets.bottom;
        }

        /**
         * {@inheritDoc}
         */
        public Margins getMargin() {
            LInsets lmargins = element.getMargin();
            return new Margins(lmargins.top, lmargins.left, lmargins.bottom, lmargins.right);
        }

        /**
         * {@inheritDoc}
         */
        public void setMargin(final Margins margin) {
            element.getMargin().left = margin.left;
            element.getMargin().top = margin.top;
            element.getMargin().right = margin.right;
            element.getMargin().bottom = margin.bottom;
        }
    }

    /**
     * Adapter for {@link LPort}s.
     */
    static final class LPortAdapter extends AbstractLShapeAdapter<LPort> implements PortAdapter<LPort> {
        /** List of cached label adapters. */
        private List<LabelAdapter<?>> labelAdapters = null;
        /** List of cached edge adapters for incoming edges. */
        private List<EdgeAdapter<?>> incomingEdgeAdapters = null;
        /** List of cached edge adapters for outgoing edges. */
        private List<EdgeAdapter<?>> outgoingEdgeAdapters = null;
        
        
        /**
         * Creates a new adapter for the given port.
         * 
         * @param element
         *            the port to be adapted.
         */
        public LPortAdapter(final LPort element) {
            super(element);
        }
        

        /**
         * {@inheritDoc}
         */
        public PortSide getSide() {
            return element.getSide();
        }

        /**
         * {@inheritDoc}
         */
        public Iterable<LabelAdapter<?>> getLabels() {
            if (labelAdapters == null) {
                labelAdapters = Lists.newArrayListWithCapacity(element.getLabels().size());
                for (LLabel l : element.getLabels()) {
                    labelAdapters.add(new LLabelAdapter(l));
                }
            }
            return labelAdapters;
        }

        /**
         * {@inheritDoc}
         */
        public Margins getMargin() {
            LInsets lmargins = element.getMargin();
            return new Margins(lmargins.top, lmargins.left, lmargins.bottom, lmargins.right);
        }

        /**
         * {@inheritDoc}
         */
        public void setMargin(final Margins margin) {
            element.getMargin().left = margin.left;
            element.getMargin().top = margin.top;
            element.getMargin().right = margin.right;
            element.getMargin().bottom = margin.bottom;
        }

        /**
         * {@inheritDoc}
         */
        public Iterable<EdgeAdapter<?>> getIncomingEdges() {
            if (incomingEdgeAdapters == null) {
                incomingEdgeAdapters = Lists.newArrayList();
                for (LEdge e : element.getIncomingEdges()) {
                    incomingEdgeAdapters.add(new LEdgeAdapter(e));
                }
            }
            return incomingEdgeAdapters;
        }

        /**
         * {@inheritDoc}
         */
        public Iterable<EdgeAdapter<?>> getOutgoingEdges() {
            if (outgoingEdgeAdapters == null) {
                outgoingEdgeAdapters = Lists.newArrayList();
                for (LEdge e : element.getOutgoingEdges()) {
                    outgoingEdgeAdapters.add(new LEdgeAdapter(e));
                }
            }
            return outgoingEdgeAdapters;
        }

        /**
         * {@inheritDoc}
         */
        public boolean hasCompoundConnections() {
            return element.getProperty(InternalProperties.INSIDE_CONNECTIONS);
        }
    }

    /**
     * Adapter for {@link LLabel}s.
     */
    static final class LLabelAdapter extends AbstractLShapeAdapter<LLabel> implements
        LabelAdapter<LLabel> {

        /**
         * Creates a new adapter for the given label.
         * 
         * @param element
         *            the label to be adapted.
         */
        public LLabelAdapter(final LLabel element) {
            super(element);
        }

        /**
         * {@inheritDoc}
         */
        public LabelSide getSide() {
            return element.getProperty(LabelSide.LABEL_SIDE);
        }
    }

    /**
     * Adapter for {@link LEdge}s.
     */
    static final class LEdgeAdapter implements EdgeAdapter<LEdge> {
        /** The wrapped edge. */
        private LEdge element;
        /** List of cached label adapters. */
        private List<LabelAdapter<?>> labelAdapters = null;

        
        /**
         * Creates a new adapter for the given edge.
         * 
         * @param edge
         *            the edge to adapt.
         */
        public LEdgeAdapter(final LEdge edge) {
            this.element = edge;
        }
        

        /**
         * {@inheritDoc}
         */
        public Iterable<LabelAdapter<?>> getLabels() {
            if (labelAdapters == null) {
                labelAdapters = Lists.newArrayListWithCapacity(element.getLabels().size());
                for (LLabel l : element.getLabels()) {
                    labelAdapters.add(new LLabelAdapter(l));
                }
            }
            return labelAdapters;
        }
    }

    /**
     * A comparer for ports. Ports are sorted by side (north, east, south, west) in
     * clockwise order, beginning at the top left corner.
     */
    public static final PortComparator DEFAULT_PORTLIST_SORTER = new PortComparator();
    
    /**
     * A comparer for ports. Ports are sorted by side (north, east, south, west) in
     * clockwise order, beginning at the top left corner.
     */
    public static class PortComparator implements Comparator<LPort> {
        /**
         * {@inheritDoc}
         */
        public int compare(final LPort port1, final LPort port2) {
            int ordinalDifference = port1.getSide().ordinal() - port2.getSide().ordinal();
            
            // Sort by side first
            if (ordinalDifference != 0) {
                return ordinalDifference;
            }
            
            // In case of equal sides, sort by port index property
            Integer index1 = port1.getProperty(LayoutOptions.PORT_INDEX);
            Integer index2 = port2.getProperty(LayoutOptions.PORT_INDEX);
            if (index1 != null && index2 != null) {
                int indexDifference = index1 - index2;
                if (indexDifference != 0) {
                    return indexDifference;
                }
            }
            
            // In case of equal index, sort by position
            switch (port1.getSide()) {
            case NORTH:
                // Compare x coordinates
                return Double.compare(port1.getPosition().x, port2.getPosition().x);
            
            case EAST:
                // Compare y coordinates
                return Double.compare(port1.getPosition().y, port2.getPosition().y);
            
            case SOUTH:
                // Compare x coordinates in reversed order
                return Double.compare(port2.getPosition().x, port1.getPosition().x);
            
            case WEST:
                // Compare y coordinates in reversed order
                return Double.compare(port2.getPosition().y, port1.getPosition().y);
                
            default:
                // Port sides should not be undefined
                throw new IllegalStateException("Port side is undefined");
            }
        }
    }
}
