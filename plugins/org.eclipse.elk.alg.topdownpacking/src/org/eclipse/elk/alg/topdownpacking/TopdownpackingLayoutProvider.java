package org.eclipse.elk.alg.topdownpacking;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.alg.topdownpacking.options.TopdownpackingOptions;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.alg.AlgorithmAssembler;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

/**
 * A simple box packing algorithm that places nodes as evenly sized rectangles. This algorithm uses fixed sizes and 
 * therefore requires the option 'Topdown Layout to be set to true to result in a correct layout.
 * 
 * New more specific usage idea: for sccharts in topdown layout instead of rectpacking. parent state should already
 * be sized to fit this layout. Size can be predicted. This layout should NOT be scaled down, instead only its child layout,
 * which in the case of SCCharts is a layered layout.
 */
public class TopdownpackingLayoutProvider extends AbstractLayoutProvider {

    private final AlgorithmAssembler<TopdownPackingPhases, ElkNode> algorithmAssembler =
            AlgorithmAssembler.<TopdownPackingPhases, ElkNode>create(TopdownPackingPhases.class);
    
    @Override
    public void layout(ElkNode layoutGraph, IElkProgressMonitor progressMonitor) {
        
        List<ILayoutProcessor<ElkNode>> algorithm = assembleAlgorithm(layoutGraph);
        
        progressMonitor.begin("Topdown Packing", algorithm.size());
        
        for (ILayoutProcessor<ElkNode> processor : algorithm) {
            processor.process(layoutGraph, progressMonitor.subTask(1));
        }
        
        progressMonitor.done();
        
    }
    
    public List<ILayoutProcessor<ElkNode>> assembleAlgorithm(ElkNode graph) {
        algorithmAssembler.reset();
        
        algorithmAssembler.setPhase(TopdownPackingPhases.P1_NODE_PLACEMENT, NodePlacementStrategy.NODE_PLACER);
        algorithmAssembler.setPhase(TopdownPackingPhases.P2_WHITESPACE_ELIMINATION, WhitespaceEliminationStrategy.WHITESPACE_ELIMINATOR);
        
        return algorithmAssembler.build(graph);
    }
}
