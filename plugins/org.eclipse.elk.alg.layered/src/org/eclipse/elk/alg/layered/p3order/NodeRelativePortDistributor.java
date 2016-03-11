/*******************************************************************************
 * Copyright (c) 2010, 2015 Kiel University and others.
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
 * Calculates port ranks in a node-relative way.
 * 
 * <p>Port ranks are calculated by giving each node {@code i} a range of values {@code [i,i+1]} to
 * distribute their port values in. This effectively gives a node with many ports the same weight as
 * a node with few ports as opposed to a strategy that just numbers ports from top to bottom.</p>
 * 
 * @author cds
 * @author msp
 * @author ima
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public final class NodeRelativePortDistributor extends AbstractPortDistributor {
    
    private final boolean assumePortOrderFixed;

    /**
     * Constructs a node-relative port distributor with the given array of ranks.
     * All ports are required to be assigned ids in the range of the given array.
     * 
     * @param portRanks
     *            The array of port ranks
     */
    private NodeRelativePortDistributor(final float[] portRanks, final boolean assumePortOrderFixed,
            final int[][] nodePositions) {
        super(portRanks, nodePositions);
        this.assumePortOrderFixed = assumePortOrderFixed;
    }

    /**
     * Constructs a node-relative port distributor with the given array of ranks. All ports are
     * required to be assigned ids in the range of the given array.
     * 
     * @param portRanks
     *            The array of port ranks
     * @param nodePositions
     */
    private NodeRelativePortDistributor(final float[] portRanks,
            final boolean assumePortOrderFixed) {
        super(portRanks);
        this.assumePortOrderFixed = assumePortOrderFixed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected float calculatePortRanks(final LNode node, final float rankSum, final PortType type) {
        float[] portRanks = getPortRanks();

        if (isPortOrderFixedOn(node)) {

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
                float incr = 1.0f / (inputCount + 1);
                float northPos = rankSum + northInputCount * incr;
                float restPos = rankSum + 1 - incr;
                for (LPort port : node.getPorts(PortType.INPUT)) {
                    if (port.getSide() == PortSide.NORTH) {
                        portRanks[port.id] = northPos;
                        northPos -= incr;
                    } else {
                        portRanks[port.id] = restPos;
                        restPos -= incr;
                    }
                }
                break;
            }
                
            case OUTPUT: {
                // Count the number of output ports
                int outputCount = 0;
                for (LPort port : node.getPorts()) {
                    if (!port.getOutgoingEdges().isEmpty()) {
                        outputCount++;
                    }
                }

                // Iterate output ports in their natural order, that is north - east - south - west
                float incr = 1.0f / (outputCount + 1);
                float pos = rankSum + incr;
                for (LPort port : node.getPorts(PortType.OUTPUT)) {
                    portRanks[port.id] = pos;
                    pos += incr;
                }
                break;
            }
            
            default:
                // this means illegal input to the method
                throw new IllegalArgumentException("Port type is undefined");
            }
            
        } else {
            for (LPort port : node.getPorts(type)) {
                portRanks[port.id] = rankSum + getPortIncr(type, port.getSide());
            }
        }
        
        // the consumed rank is always 1
        return 1;
    }

    private boolean isPortOrderFixedOn(final LNode node) {
        return assumePortOrderFixed
                || node.getProperty(CoreOptions.PORT_CONSTRAINTS).isOrderFixed();
    }

    private static final float INCR_ONE = 0.3f;
    private static final float INCR_TWO = 0.5f;
    private static final float INCR_THREE = 0.7f;
    private static final float INCR_FOUR = 0.9f;

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
    public static NodeRelativePortDistributor create(final float[] portRanks) {
        return new NodeRelativePortDistributor(portRanks, false);
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
    public static NodeRelativePortDistributor createPortOrderFixedInOtherLayers(
            final float[] portRanks, final int[][] nodePositions) {
        return new NodeRelativePortDistributor(portRanks, true, nodePositions);
    }

}
