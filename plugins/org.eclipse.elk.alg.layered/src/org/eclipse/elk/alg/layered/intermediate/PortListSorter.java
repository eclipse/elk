/*******************************************************************************
 * Copyright (c) 2011, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import java.util.Collections;
import java.util.Comparator;

import org.eclipse.elk.alg.layered.ILayoutProcessor;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Sorts the port lists of nodes with fixed port orders or fixed port positions. 
 * The node's list of ports is sorted beginning at the leftmost northern port, going clockwise.
 * This order of ports may be used during crossing minimization for calculating port ranks.
 * 
 * In case of {@link PortConstraints#FIXED_SIDE FIXED_SIDE} the ports are order by side
 * according to the order north - east - south - west.
 * In case of {@link PortConstraints#FIXED_ORDER FIXED_ORDER} the side and
 * {@link LayeredOptions#PORT_INDEX PORT_INDEX} are used if specified. Otherwise the order is inferred
 * from specified port positions. For {@link PortConstraints#FIXED_POS FIXED_POS} solely the position of
 * the ports determines the order.
 * 
 * <dl>
 *   <dt>Precondition:</dt><dd>a layered graph.</dd>
 *   <dt>Postcondition:</dt><dd>the port lists of nodes with fixed port orders are sorted.</dd>
 *   <dt>Slots:</dt><dd>Before phase 3. May additionally be used before phase 4 as well.</dd>
 *   <dt>Same-slot dependencies:</dt><dd>None.</dd>
 * </dl>
 * 
 * @see LNode#getPorts()
 * @author cds
 * @author uru
 * @kieler.design 2012-08-10 chsch grh
 * @kieler.rating proposed yellow by msp
 */
public final class PortListSorter implements ILayoutProcessor {

    /**
     * A comparer for ports. Ports are sorted by side (north, east, south, west) in clockwise order,
     * beginning at the top left corner.
     */
    public static class PortComparator implements Comparator<LPort> {

        /**
         * {@inheritDoc}
         */
        public int compare(final LPort port1, final LPort port2) {
            PortConstraints port1Constraint = 
                    port1.getNode().getProperty(LayeredOptions.PORT_CONSTRAINTS);
            // Sort by side first (if the comparison ends here, the ports were on different sides;
            // otherwise, the ports must be on the same side)
            int ordinalDifference = port1.getSide().ordinal() - port2.getSide().ordinal();
            if (ordinalDifference != 0 || port1Constraint == PortConstraints.FIXED_SIDE) {
                return ordinalDifference;
            }

            // If the ports are on the same side and the node has FIXED_ORDER port constraints (that is,
            // the coordinates of the ports don't necessarily make sense), we check if the port index
            // has been explicitly set
            if (port1Constraint == PortConstraints.FIXED_ORDER) {
                // In case of equal sides, sort by port index property
                Integer index1 = port1.getProperty(LayeredOptions.PORT_INDEX);
                Integer index2 = port2.getProperty(LayeredOptions.PORT_INDEX);
                if (index1 != null && index2 != null) {
                    int indexDifference = index1 - index2;
                    if (indexDifference != 0) {
                        return indexDifference;
                    }
                }
            }

            // In case of equal index (or FIXED_POS), sort by position
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


    /**
     * {@inheritDoc}
     */
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Port order processing", 1);
        PortComparator portComparator = new PortComparator();

        // Iterate through the nodes of all layers
        for (Layer layer : layeredGraph) {
            for (LNode node : layer) {
                if (node.getProperty(LayeredOptions.PORT_CONSTRAINTS).isSideFixed()) {
                    // We need to sort the port list accordingly
                    Collections.sort(node.getPorts(), portComparator);
                    node.cachePortSides();
                }
            }
        }

        monitor.done();
    }

}