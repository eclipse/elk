/*******************************************************************************
 * Copyright (c) 2022 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0  
 *******************************************************************************/
package org.eclipse.elk.alg.topdownpacking;

import java.util.List;

import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.alg.AlgorithmAssembler;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

/**
 * TODO: improve this documentation
 * A simple box packing algorithm that places nodes as evenly sized rectangles. This algorithm uses fixed sizes and 
 * therefore requires the option 'Topdown Layout to be set to true to result in a correct layout.
 * 
 * New more specific usage idea: for sccharts in topdown layout instead of rectpacking. parent state should already
 * be sized to fit this layout. Size can be predicted. This layout should NOT be scaled down, instead only its child layout,
 * which in the case of SCCharts is a layered layout.
 */
public class TopdownpackingLayoutProvider extends AbstractLayoutProvider {

    private final AlgorithmAssembler<TopdownPackingPhases, GridElkNode> algorithmAssembler =
            AlgorithmAssembler.<TopdownPackingPhases, GridElkNode>create(TopdownPackingPhases.class);
    
    @Override
    public void layout(ElkNode layoutGraph, IElkProgressMonitor progressMonitor) {
        
        // convert graph to GridElkNode
        GridElkNode wrappedGraph = new GridElkNode(layoutGraph);
        
        // assemble algorithm
        List<ILayoutProcessor<GridElkNode>> algorithm = assembleAlgorithm(wrappedGraph);
        
        
        progressMonitor.begin("Topdown Packing", algorithm.size());
        
        for (ILayoutProcessor<GridElkNode> processor : algorithm) {
            processor.process(wrappedGraph, progressMonitor.subTask(1));
        }
        
        progressMonitor.done();
        
    }
    
    public List<ILayoutProcessor<GridElkNode>> assembleAlgorithm(GridElkNode graph) {
        algorithmAssembler.reset();
        
        algorithmAssembler.setPhase(TopdownPackingPhases.P1_NODE_PLACEMENT, NodePlacementStrategy.NODE_PLACER);
        algorithmAssembler.setPhase(TopdownPackingPhases.P2_WHITESPACE_ELIMINATION, WhitespaceEliminationStrategy.WHITESPACE_ELIMINATOR);
        
        return algorithmAssembler.build(graph);
    }
}
