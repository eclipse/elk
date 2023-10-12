/*******************************************************************************
 * Copyright (c) 2023 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.yconstree;

import java.util.List;

import org.eclipse.elk.alg.yconstree.p2yplacement.NodeYPlacerStrategy;
import org.eclipse.elk.alg.yconstree.p3relative.RelativeXPlacerStrategy;
import org.eclipse.elk.alg.yconstree.p4absolute.AbsoluteXPlacerStrategy;
import org.eclipse.elk.alg.yconstree.p5edgerouting.EdgerouterStrategy;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.alg.AlgorithmAssembler;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

/**
 * Layout provider for the y constraint tree layout algorithms.
 */
public final class YconstreeLayoutProvider extends AbstractLayoutProvider {
    
    
    private final AlgorithmAssembler<YconstreeLayoutPhases, ElkNode> algorithmAssembler =
        AlgorithmAssembler.<YconstreeLayoutPhases, ElkNode>create(YconstreeLayoutPhases.class);

    @Override
    public void layout(final ElkNode graph, final IElkProgressMonitor progressMonitor) {
        List<ILayoutProcessor<ElkNode>> algorithm = assembleAlgorithm(graph);

        progressMonitor.begin("Tree layout", algorithm.size());
        
        // pre calculate the root node and save it
        ElkNode root = YconstreeUtil.findRoot(graph);
        graph.setProperty(InternalProperties.ROOT_NODE, root);
        if (root == null) {
            throw new IllegalArgumentException("The given graph is not a tree!");
        }
        
        for (ElkNode child : graph.getChildren()) {
            int numberOfParents;
            numberOfParents = child.getIncomingEdges().size();
            if (numberOfParents > 1) {
                throw new IllegalArgumentException("The given graph is not an acyclic tree!");
            }
        }

        for (ILayoutProcessor<ElkNode> processor : algorithm) {
            processor.process(graph, progressMonitor.subTask(1));
        }

        progressMonitor.done();
    }
    
    /**
     * Configure the layout provider by assembling different layout processors.
     * 
     * @param graph The graph which shall be layout.
     * @return The list of assembled layout processors.
     */
    public List<ILayoutProcessor<ElkNode>> assembleAlgorithm(final ElkNode graph) {
        algorithmAssembler.reset();

        // Configure phases
        algorithmAssembler.setPhase(YconstreeLayoutPhases.P1_NODE_Y_PLACEMENT,
                NodeYPlacerStrategy.SIMPLE_YPLACING);
        algorithmAssembler.setPhase(YconstreeLayoutPhases.P2_NODE_RELATIVE_PLACEMENT,
                RelativeXPlacerStrategy.SIMPLE_XPLACING);
        algorithmAssembler.setPhase(YconstreeLayoutPhases.P3_NODE_ABSOLUTE_PLACEMENT,
                AbsoluteXPlacerStrategy.ABSOLUTE_XPLACING);
        algorithmAssembler.setPhase(YconstreeLayoutPhases.P4_EDGE_ROUTING,
                EdgerouterStrategy.DIRECT_ROUTING);

        // Assemble the algorithm
        return algorithmAssembler.build(graph);
    }

}
