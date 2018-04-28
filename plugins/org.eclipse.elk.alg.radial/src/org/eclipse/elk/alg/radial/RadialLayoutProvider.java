/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.radial;

import java.util.List;

import org.eclipse.elk.alg.common.nodespacing.NodeMicroLayout;
import org.eclipse.elk.alg.radial.intermediate.IntermediateProcessorStrategy;
import org.eclipse.elk.alg.radial.options.CompactionStrategy;
import org.eclipse.elk.alg.radial.options.RadialOptions;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.alg.AlgorithmAssembler;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.core.util.adapters.ElkGraphAdapters;
import org.eclipse.elk.graph.ElkNode;

/**
 * The RadialLayoutProvider provides an interface for radial layout algorithms. It expects the input graph to be a tree
 * and divides the layout task into the phase of node placement and edge routing.
 * 
 *  It can configured using a number of layout options that can be found in the {@link RadialOptions} classs.
 */
public class RadialLayoutProvider extends AbstractLayoutProvider {
    
    private final AlgorithmAssembler<RadialLayoutPhases, ElkNode> algorithmAssembler =
            AlgorithmAssembler.<RadialLayoutPhases, ElkNode> create(RadialLayoutPhases.class);

    @Override
    public void layout(final ElkNode layoutGraph, final IElkProgressMonitor progressMonitor) {

        List<ILayoutProcessor<ElkNode>> algorithm = assembleAlgorithm(layoutGraph);
        progressMonitor.begin("Radial layout", algorithm.size());

        // pre calculate the root node and save it
        ElkNode root = RadialUtil.findRoot(layoutGraph);
        if (root == null) {
            throw new IllegalArgumentException("The given graph is not a tree!");
        }
        layoutGraph.setProperty(InternalProperties.ROOT_NODE, root);
        
        // Calculate the radius or take the one given by the user.
        double layoutRadius = layoutGraph.getProperty(RadialOptions.RADIUS);
        if (layoutRadius == 0) {
            layoutRadius = RadialUtil.findLargestNodeInGraph(layoutGraph);
        }
        layoutGraph.setProperty(RadialOptions.RADIUS, layoutRadius);
        
        // position labels, ports, and the like ...O
        NodeMicroLayout.executeAll(ElkGraphAdapters.adapt(layoutGraph));
                
        // execute the different phases
        for (ILayoutProcessor<ElkNode> processor : assembleAlgorithm(layoutGraph)) {
            processor.process(layoutGraph, progressMonitor.subTask(1));
        }

        progressMonitor.done();
    }

    /**
     * Configure the layout provider by assembling different layout processors.
     * 
     * @param layoutGraph The graph which shall be layout.
     * @return The list of assembled layout processors.
     */
    private List<ILayoutProcessor<ElkNode>> assembleAlgorithm(final ElkNode layoutGraph) {
        algorithmAssembler.reset();

        // Configure phases
        algorithmAssembler.setPhase(RadialLayoutPhases.P1_NODE_PLACEMENT, RadialLayoutPhases.P1_NODE_PLACEMENT);
        algorithmAssembler.setPhase(RadialLayoutPhases.P2_EDGE_ROUTING, RadialLayoutPhases.P2_EDGE_ROUTING);

        // Configure processors
        LayoutProcessorConfiguration<RadialLayoutPhases, ElkNode> configuration =
                LayoutProcessorConfiguration.<RadialLayoutPhases, ElkNode> create();
        configuration.addBefore(RadialLayoutPhases.P2_EDGE_ROUTING, IntermediateProcessorStrategy.OVERLAP_REMOVAL);

        if (layoutGraph.getProperty(RadialOptions.COMPACTOR) != CompactionStrategy.NONE) {
            configuration.addBefore(RadialLayoutPhases.P2_EDGE_ROUTING, IntermediateProcessorStrategy.COMPACTION);
        }
        configuration.addBefore(RadialLayoutPhases.P2_EDGE_ROUTING,
                IntermediateProcessorStrategy.GRAPH_SIZE_CALCULATION);

        algorithmAssembler.addProcessorConfiguration(configuration);

        // Build the algorithm
        List<ILayoutProcessor<ElkNode>> algorithm = algorithmAssembler.build(layoutGraph);
        return algorithm;
    }

}