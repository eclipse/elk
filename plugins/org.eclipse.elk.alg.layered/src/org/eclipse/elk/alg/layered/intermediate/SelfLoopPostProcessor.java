/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import org.eclipse.elk.alg.common.structure.ILayoutProcessor;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * After the nodes got coordinates the bend points, junction points and label positions are still relative. The
 * {@link SelfLoopPostProcessor} adds the position of the node to all of the aforementioned.
 * 
 * <dl>
 *   <dt>Precondition:</dt>
 *     <dd>The nodes do have coordinates.</dd>
 *   <dt>Postcondition:s</dt>
 *     <dd>Self-Loop edge bend points, junction points and labels are placed as intended by the relative positioning
 *         beforehand.</dd>
 *   <dt>Slots:</dt>
 *     <dd>After phase 5.</dd>
 *   <dt>Same-slot dependencies:</dt>
 *     <dd>None.</dd>
 * </dl>
 */
public class SelfLoopPostProcessor implements ILayoutProcessor<LGraph> {

    @Override
    public void process(final LGraph graph, final IElkProgressMonitor monitor) {
        monitor.begin("Self-Loop post-processing", 1);
        
        for (Layer layer : graph) {
            for (LNode node : layer.getNodes()) {
                for (LEdge edge : node.getOutgoingEdges()) {
                    if (edge.isSelfLoop()) {
                        // offset all edge bend points
                        final KVector offset = edge.getSource().getNode().getPosition();
                        edge.getBendPoints().offset(offset);

                        // offset all junction points of the edges
                        KVectorChain junctionPoints = edge.getProperty(LayeredOptions.JUNCTION_POINTS);
                        junctionPoints.offset(offset);

                        // offset all label positions
                        for (final LLabel label : edge.getLabels()) {
                            label.getPosition().add(offset);
                        }
                    }
                }
            }
        }
        
        monitor.done();
    }

}
