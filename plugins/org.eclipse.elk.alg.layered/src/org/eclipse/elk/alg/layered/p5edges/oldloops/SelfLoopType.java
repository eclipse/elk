/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.oldloops;

import org.eclipse.elk.core.options.PortSide;

/**
 * The possible types of self loops.
 */
public enum SelfLoopType {

    /** Edge connects two different nodes. */
    NON_LOOP,
    /** Edge connects ports which are placed on the same side of a node. */
    SIDE,
    /** Edge connects two ports which are placed on adjacent node sides. */
    CORNER,
    /** Edge connects two ports which are placed on opposing node sides. */
    OPPOSING,
    /** Edge connects two ports with an edge path routing around three corners. */
    THREE_CORNER,
    /** Edge connects two ports on the same side with an edge path routing around four corners. */
    FOUR_CORNER;

    
    /**
     * Compute the type of self loop.
     */
    public static SelfLoopType getEdgeType(final SelfLoopEdge edge, final SelfLoopNode nodeRep) {
        SelfLoopPort source = edge.getSource();
        PortSide sourceSide = source.getPortSide();
        SelfLoopPort target = edge.getTarget();
        
        // If there is no target, there is no self loop
        if (target == null) {
            return NON_LOOP;
        }
        
        PortSide targetSide = target.getPortSide();
        
        boolean rightDir = source.getDirection() == SelfLoopRoutingDirection.RIGHT;
        boolean leftDir = source.getDirection() == SelfLoopRoutingDirection.LEFT;
        
        if (source.getLPort().getNode() != target.getLPort().getNode()) {
            // If the source's and the target's nodes differ, there is no self loop
            return NON_LOOP;
            
        } else if (sourceSide == targetSide) {
            // Source and target are on the same side. We either have a same-side self loop, or we route the thing
            // around its whole node
            SelfLoopNodeSide nodeRepside = nodeRep.getNodeSide(sourceSide);
            int sourceIndex = nodeRepside.getPorts().indexOf(source);
            int targetIndex = nodeRepside.getPorts().indexOf(target);
            
            if (leftDir && sourceIndex < targetIndex || rightDir && targetIndex < sourceIndex) {
                return FOUR_CORNER;
            } else {
                return SIDE;
            }
            
        } else if (sourceSide.areAdjacent(targetSide)) {
            // Source and target are on adjacent sides. This is either a corner self-loop, or we route it the long way
            if (rightDir && source.getPortSide().left() == target.getPortSide()
                    || leftDir && source.getDirection() == SelfLoopRoutingDirection.LEFT) {
                
                return THREE_CORNER;
            } else {
                return CORNER;
            }
            
        } else {
            return OPPOSING;
        }
    }
    
}
