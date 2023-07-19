/*******************************************************************************
 * Copyright (c) 2023 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.radial.intermediate.rotation;

import org.eclipse.elk.alg.radial.InternalProperties;
import org.eclipse.elk.alg.radial.options.RadialOptions;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.graph.ElkNode;

/**
 * Rotates the entire layout around the origin to a set target angle.
 *
 */
public class AngleRotation implements IRadialRotator {

    /* (non-Javadoc)
     * @see org.eclipse.elk.alg.radial.intermediate.rotation.IRadialRotator#rotate(org.eclipse.elk.graph.ElkNode)
     */
    @Override
    public void rotate(ElkNode graph) {
        double targetAngle = graph.getProperty(RadialOptions.TARGET_ANGLE);
        
        if (graph.getProperty(RadialOptions.COMPUTE_ADDITIONAL_WEDGE_SPACE)) {
            // additionally we want to get half the angle of the first wedge to leave space
            // take first two nodes and compute angle between their incident edges
            ElkNode root = graph.getProperty(InternalProperties.ROOT_NODE);
            if (root.getOutgoingEdges().size() <= 1) {
                // leave angle untouched
                if (targetAngle < 0) {
                    targetAngle += 2*Math.PI;
                }
                ElkNode firstNode = (ElkNode) root.getOutgoingEdges().get(0).getTargets().get(0);
                KVector firstVector = new KVector(firstNode.getX() + firstNode.getWidth() / 2, firstNode.getY() + firstNode.getHeight() / 2);
                
                double alignmentAngle = Math.atan2(firstVector.y, firstVector.x);
                if (alignmentAngle < 0) {
                    alignmentAngle += 2*Math.PI;
                }
                
                targetAngle = Math.PI - (alignmentAngle - targetAngle + Math.PI);
            } else {
                ElkNode firstNode = (ElkNode) root.getOutgoingEdges().get(0).getTargets().get(0);
                ElkNode secondNode = (ElkNode) root.getOutgoingEdges().get(1).getTargets().get(0);
                KVector firstVector = new KVector(firstNode.getX() + firstNode.getWidth() / 2, firstNode.getY() + firstNode.getHeight() / 2);
                KVector secondVector = new KVector(secondNode.getX() + secondNode.getWidth() / 2, secondNode.getY() + secondNode.getHeight() / 2);

                double alpha = targetAngle;
                if (alpha < 0) {
                    alpha += 2*Math.PI;
                }
                
                double wedgeAngle = firstVector.angle(secondVector);
                if (wedgeAngle < 0) {
                    wedgeAngle += 2*Math.PI;
                }
                
                double alignmentAngle = Math.atan2(firstVector.y, firstVector.x);
                if (alignmentAngle < 0) {
                    alignmentAngle += 2*Math.PI;
                }
                
                targetAngle = Math.PI - (alignmentAngle - alpha + wedgeAngle / 2);

            }
        }

        // rotate all nodes around the origin, because the root node is positioned at the origin
        // nodes are positioned with their center on the radius so use that for the rotation
        for (ElkNode node : graph.getChildren()) {
            KVector pos = new KVector(node.getX() + node.getWidth() / 2, node.getY() + node.getHeight() / 2);
            pos.rotate(targetAngle);
            node.setLocation(pos.x - node.getWidth() / 2, pos.y - node.getHeight() / 2);
        }

    }

}
