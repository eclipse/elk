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

import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.nodespacing.LabelSide;
import org.eclipse.elk.core.util.nodespacing.Spacing.Insets;
import org.eclipse.elk.core.util.nodespacing.Spacing.Margins;
import org.eclipse.elk.graph.properties.IProperty;

/**
 * A set of generic interfaces that provide access to graph structures. Some generic algorithms provided
 * by KIML work with these interfaces to be usable for any layout algorithm, regardless what kind of
 * specialized graph structure it uses internally.
 * 
 * @author uru
 */
public interface GraphAdapters {

    /**
     * A generic adapter for graph elements that have a position and dimension. Can be used, for
     * instance, to implement node, port, and label adapters.
     * 
     * <h2>Remark</h2>
     * <p>When using these adapters keep in mind to explicitly use the <emph>set</emph>
     * methods, e.g. for insets. Some API's (e.g. the {@link KVector}) allow to directly set their
     * values, for instance {@code node.getPosition().x = 3}. However, as {@code node.getPosition()}
     * most likely returns an intermediate object, this change will never be applied to the original
     * graph's element.</p>
     * 
     * @param <T>
     *            the type of the underlying graph element.
     */
    public interface GraphElementAdapter<T> {

        /**
         * @return the size of the graph element, the {@code x} value of the {@link KVector} defines
         *         the width, the {@code y} value the height.
         */
        KVector getSize();

        /**
         * @param size
         *            the new size to be set for the graph element.
         */
        void setSize(final KVector size);

        /**
         * @return the current position of the graph element.
         */
        KVector getPosition();

        /**
         * @param pos
         *            the new position of the graph element.
         */
        void setPosition(final KVector pos);

        /**
         * @param prop
         *            the property to retrieve.
         * @param <P>
         *            the contained type of the property.
         * @return the value of the requested property.
         * @see org.eclipse.elk.core.options.CoreOptions
         * 
         */
        <P> P getProperty(final IProperty<P> prop);

        /**
         * Returns the volatile ID for this element that can be used arbitrarily by layout
         * algorithms. No assumptions about the returned value must be made. If the ID is to be
         * used, it must be considered mandatory to first set a valid ID by calling
         * {@link #setVolatileId(int)}. Failing to adhere to this rule should be punishable through
         * means we dare not speak of!
         * 
         * @return the ID.
         */
        int getVolatileId();

        /**
         * Sets the ID for this element for later internal use.
         * 
         * @param volatileId
         *            the new ID.
         */
        void setVolatileId(final int volatileId);
    }

    /**
     * Adapter for graph element, provides children of the graph.
     */
    public interface GraphAdapter<T> extends GraphElementAdapter<T> {
        
        /**
         * @return all child nodes of this graph wrapped in an {@link NodeAdapter}.
         */
        Iterable<NodeAdapter<?>> getNodes();
    }

    /**
     * Adapter for a node, provides labels, ports, and insets.
     */
    public interface NodeAdapter<T> extends GraphElementAdapter<T> {
        
        /**
         * @return the labels of the node wrapped in adapters.
         */
        Iterable<LabelAdapter<?>> getLabels();

        /**
         * @return the ports of the node wrapped in adapter.
         */
        Iterable<PortAdapter<?>> getPorts();

        /**
         * @return a collection of the port's incoming edges wrapped in an adapter.
         */
        Iterable<EdgeAdapter<?>> getIncomingEdges();

        /**
         * @return a collection of the port's outgoing edges wrapped in an adapter.
         */
        Iterable<EdgeAdapter<?>> getOutgoingEdges();

        /**
         * Sort the port list according to a default order of the implementing graph adapter for
         * every node with {@link org.eclipse.elk.core.options.PortConstraints} at least
         * {@link org.eclipse.elk.core.options.PortConstraints#FIXED_ORDER}.
         */
        void sortPortList();

        /**
         * Sort the port list using the specified comparator for every node with
         * {@link org.eclipse.elk.core.options.PortConstraints} at least
         * {@link org.eclipse.elk.core.options.PortConstraints#FIXED_ORDER}.
         * 
         * @param comparator
         *            an implementation of {@link Comparator} for the type of the implementing graph
         *            adapter. Note that the comparator must support the correct type, e.g. KPort.
         */
        void sortPortList(final Comparator<?> comparator);

        /**
         * Whether the node an is a compound node or not, i.e if it has child nodes. This might
         * influence certain layout decisions, such as where to place inside port labels so that
         * they don't overlap edges.
         * 
         * @return {@code true} if it is a compound node.
         */
        boolean isCompoundNode();

        /**
         * Returns the node's insets. The insets describe the area inside the node that is used by
         * ports, port labels, and node labels.
         * 
         * @return the node's insets.
         */
        Insets getInsets();

        /**
         * @param insets
         *            sets the new insets of this node.
         */
        void setInsets(final Insets insets);

        /**
         * Returns the node's margin. The margin is the space around the node that is to be reserved
         * for ports and labels.
         * 
         * @return the node's margin.
         */
        Margins getMargin();

        /**
         * @param margin
         *            the new margin to be set.
         */
        void setMargin(final Margins margin);
    }

    /**
     * Adapter for a port element, provides access to the port's side, margin, and labels.
     */
    public interface PortAdapter<T> extends GraphElementAdapter<T> {
        
        /**
         * @return the port's side.
         */
        PortSide getSide();

        /**
         * @return the port's labels wrapped in adapters.
         */
        Iterable<LabelAdapter<?>> getLabels();

        /**
         * Returns the port's margin. The margin is the space around the node that is to be reserved
         * for ports and labels.
         * 
         * @return the ports's margin.
         */
        Margins getMargin();

        /**
         * @param margin
         *            the new margin to be set.
         */
        void setMargin(final Margins margin);

        /**
         * @return a collection of the port's incoming edges wrapped in an adapter.
         */
        Iterable<EdgeAdapter<?>> getIncomingEdges();

        /**
         * @return a collection of the port's outgoing edges wrapped in an adapter.
         */
        Iterable<EdgeAdapter<?>> getOutgoingEdges();
        
        /**
         * Checks if the port has any incident edges the come from or go to descendants of its node. If
         * the node is not a compound node, the result is always {@code false}.
         * 
         * @return {@code true} if the port has any connections to descendants of its node, {@code false}
         *         otherwise.
         */
        boolean hasCompoundConnections();
    }

    /**
     * Adapter for a label.
     */
    public interface LabelAdapter<T> extends GraphElementAdapter<T> {
        
        /**
         * @return the side of the label.
         */
        LabelSide getSide();
    }

    /**
     * Adapter for an edge.
     */
    public interface EdgeAdapter<T> {
        
        /**
         * @return the edge's labels wrapped in an adapter.
         */
        Iterable<LabelAdapter<?>> getLabels();
    }
}
