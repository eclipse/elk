/*******************************************************************************
 * Copyright (c) 2012, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p3order;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.properties.PortType;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.PortSide;

/**
 * Calculates port ranks in a layer-total way.
 * 
 * <p>Port ranks are calculated by giving each node {@code i} a range of values {@code [i,i+x]},
 * where {@code x} is the number of input ports or output ports. Hence nodes with many ports are
 * assigned a broader range of ranks than nodes with few ports.</p>
 *
 * @author msp
 */
public final class LayerTotalPortDistributor extends AbstractBarycenterPortDistributor {

    private final boolean assumePortOrderFixed;
    /**
     * Constructs a layer-total port distributor with the given array of ranks.
     * All ports are required to be assigned ids in the range of the given array.
     * 
     * @param portRanks
     *            The array of port ranks comment
     * @param nodePositions
     */
    private LayerTotalPortDistributor(final float[] portRanks, final boolean assumePortOrderFixed,
            final int[][] nodePositions) {
        super(portRanks, nodePositions);
        this.assumePortOrderFixed = assumePortOrderFixed;
    }
    
    /**
     * Constructs a layer-total port distributor with the given array of ranks. All ports are
     * required to be assigned ids in the range of the given array.
     *            The array of port ranks
     */
    private LayerTotalPortDistributor(final float[] portRanks, final boolean assumePortOrderFixed) {
        super(portRanks);
        this.assumePortOrderFixed = assumePortOrderFixed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected float calculatePortRanks(final LNode node, final float rankSum, final PortType type) {
        float[] portRanks = getPortRanks();

        if (portOrderFixedOn(node)) {

            switch (type) {
            case INPUT: {
                // Count the number of input ports, and additionally the north-side input ports
                int inputCount = 0, northInputCount = 0;
                for (LPort port : node.getPorts()) {
                    if (!port.getIncomingEdges().isEmpty()) {
                        inputCount++;
                        if (port.getSide() == PortSide.NORTH) {
                            northInputCount++;
                        }
                    }
                }
                
                // Assign port ranks in the order north - west - south - east
                float northPos = rankSum + northInputCount;
                float restPos = rankSum + inputCount;
                for (LPort port : node.getPorts(PortType.INPUT)) {
                    if (port.getSide() == PortSide.NORTH) {
                        portRanks[port.id] = northPos;
                        northPos--;
                    } else {
                        portRanks[port.id] = restPos;
                        restPos--;
                    }
                }
                
                // the consumed rank corresponds to the number of input ports
                return inputCount;
            }
                
            case OUTPUT: {
                // Iterate output ports in their natural order, that is north - east - south - west
                int pos = 0;
                for (LPort port : node.getPorts(PortType.OUTPUT)) {
                    pos++;
                    portRanks[port.id] = rankSum + pos;
                }
                return pos;
            }
            
            default:
                // this means illegal input to the method
                throw new IllegalArgumentException();
            }
            
        } else {
            // determine the minimal and maximal increment depending on port sides
            float minIncr = INCR_FOUR;
            float maxIncr = 0;
            for (LPort port : node.getPorts(type)) {
                float incr = getPortIncr(type, port.getSide());
                minIncr = Math.min(minIncr, incr - 1);
                maxIncr = Math.max(maxIncr, incr);
            }

            if (maxIncr > minIncr) {
                // make sure that ports on different sides get different ranks
                for (LPort port : node.getPorts(type)) {
                    portRanks[port.id] = rankSum + getPortIncr(type, port.getSide()) - minIncr;
                }
            
                return maxIncr - minIncr;
            }
            // no ports of given type, so no rank is consumed
            return 0;
        }
    }

    private boolean portOrderFixedOn(final LNode node) {
        return assumePortOrderFixed
                || node.getProperty(CoreOptions.PORT_CONSTRAINTS).isOrderFixed();
    }

    private static final float INCR_ONE = 1;
    private static final float INCR_TWO = 2;
    private static final float INCR_THREE = 3;
    private static final float INCR_FOUR = 4;

    /**
     * Return an increment value for the position of a port with given type and side.
     * 
     * @param type
     *            the port type
     * @param side
     *            the port side
     * @return a position increment for the port
     */
    private static float getPortIncr(final PortType type, final PortSide side) {
        switch (type) {
        case INPUT:
            switch (side) {
            case NORTH:
                return INCR_ONE;
            case WEST:
                return INCR_TWO;
            case SOUTH:
                return INCR_THREE;
            case EAST:
                return INCR_FOUR;
            }
            break;
        case OUTPUT:
            switch (side) {
            case NORTH:
                return INCR_ONE;
            case EAST:
                return INCR_TWO;
            case SOUTH:
                return INCR_THREE;
            case WEST:
                return INCR_FOUR;
            }
            break;
            
        default:
            // this means illegal input to the method
            throw new IllegalArgumentException("Port type is undefined");
        }
        return 0;
    }

    /**
     * Create Port Distributor.
     * 
     * @param portRanks
     *            port rank values: length is amount of ports addressed by their id.
     * @return new port distributor.
     */
    public static LayerTotalPortDistributor create(final float[] portRanks) {
        return new LayerTotalPortDistributor(portRanks, false);
    }

    /**
     * Create Port Distributor which for calculation of port ranks assumes all port order to be
     * fixed.
     * 
     * @param portRanks
     *            port rank values: length is amount of ports addressed by their id.
     * @param nodePositions
     *            An array showing the current node positions.
     * @return new port distributor.
     */
    public static LayerTotalPortDistributor createPortOrderFixedInOtherLayers(
            final float[] portRanks, final int[][] nodePositions) {
        return new LayerTotalPortDistributor(portRanks, true, nodePositions);
    }

}
