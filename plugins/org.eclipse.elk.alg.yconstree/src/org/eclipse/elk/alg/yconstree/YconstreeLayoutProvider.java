/*******************************************************************************
 * Copyright (c) 2011, 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.yconstree;

import java.util.List;

import org.eclipse.elk.alg.yconstree.p1treechecking.TreeCheckerStrategy;
import org.eclipse.elk.alg.yconstree.p2yplacement.NodeYPlacerStrategy;
import org.eclipse.elk.alg.yconstree.p3relative.RelativeXPlacerStrategy;
import org.eclipse.elk.alg.yconstree.p4absolute.AbsoluteXPlacerStrategy;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.alg.AlgorithmAssembler;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

/**
 * Layout provider for the force layout algorithms.
 */
public final class YconstreeLayoutProvider extends AbstractLayoutProvider {
    
    
    private final AlgorithmAssembler<YconstreeLayoutPhases, ElkNode> algorithmAssembler =
        AlgorithmAssembler.<YconstreeLayoutPhases, ElkNode>create(YconstreeLayoutPhases.class);

    @Override
    public void layout(ElkNode graph, IElkProgressMonitor progressMonitor) {
        List<ILayoutProcessor<ElkNode>> algorithm = assembleAlgorithm(graph);

        progressMonitor.begin("Tree layout", algorithm.size());

        for (ILayoutProcessor<ElkNode> processor : algorithm) {
            processor.process(graph, progressMonitor.subTask(1));
        }

        progressMonitor.done();
    }
    
    public List<ILayoutProcessor<ElkNode>> assembleAlgorithm(ElkNode graph) {
        algorithmAssembler.reset();

        // Configure phases
        algorithmAssembler.setPhase(YconstreeLayoutPhases.P2_NODE_Y_PLACEMENT,
                NodeYPlacerStrategy.SIMPLE_YPLACING);
        algorithmAssembler.setPhase(YconstreeLayoutPhases.P3_NODE_RELATIV_PLACEMENT,
                RelativeXPlacerStrategy.SIMPLE_XPLACING);
        algorithmAssembler.setPhase(YconstreeLayoutPhases.P4_NODE_ABSOLUTE_PLACEMENT,
                AbsoluteXPlacerStrategy.ABSOLUTE_XPLACING);

        // Assemble the algorithm
        return algorithmAssembler.build(graph);
    }

}
