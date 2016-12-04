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
package org.eclipse.elk.alg.layered.p5edges;

import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.eclipse.elk.alg.layered.ILayoutPhase;
import org.eclipse.elk.alg.layered.IntermediateProcessingConfiguration;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LGraphUtil;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.intermediate.IntermediateProcessorStrategy;
import org.eclipse.elk.alg.layered.properties.GraphProperties;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.layered.properties.LayeredOptions;
import org.eclipse.elk.alg.layered.properties.Spacings;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.collect.Iterables;

/**
 * Edge routing implementation that creates orthogonal bend points. Inspired by
 * <ul>
 *   <li>Georg Sander, Layout of directed hypergraphs with orthogonal hyperedges. In
 *     <i>Proceedings of the 11th International Symposium on Graph Drawing (GD '03)</i>,
 *     LNCS vol. 2912, pp. 381-386, Springer, 2004.</li>
 *   <li>Giuseppe di Battista, Peter Eades, Roberto Tamassia, Ioannis G. Tollis,
 *     <i>Graph Drawing: Algorithms for the Visualization of Graphs</i>,
 *     Prentice Hall, New Jersey, 1999 (Section 9.4, for cycle breaking in the
 *     hyperedge segment graph)
 * </ul>
 * 
 * <dl>
 *   <dt>Precondition:</dt><dd>the graph has a proper layering with
 *     assigned node and port positions; the size of each layer is
 *     correctly set; edges connected to ports on strange sides were
 *     processed</dd>
 *   <dt>Postcondition:</dt><dd>each node is assigned a horizontal coordinate;
 *     the bend points of each edge are set; the width of the whole graph is set</dd>
 * </dl>
 *
 * @author msp
 * @author cds
 * @author jjc
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public final class OrthogonalEdgeRouter implements ILayoutPhase {
    
    /* The basic processing strategy for this phase is empty. Depending on the graph features,
     * dependencies on intermediate processors are added dynamically as follows:
     * 
     * Before phase 1:
     *   - None.
     * 
     * Before phase 2:
     *   - For center edge labels:
     *      - LABEL_DUMMY_INSERTER
     * 
     * Before phase 3:
     *   - For non-free ports:
     *     - NORTH_SOUTH_PORT_PREPROCESSOR
     *     - INVERTED_PORT_PROCESSOR
     *   
     *   - For self-loops:
     *     - SELF_LOOP_PROCESSOR
     *   
     *   - For hierarchical ports:
     *     - HIERARCHICAL_PORT_CONSTRAINT_PROCESSOR
     *   
     *   - For center edge labels:
     *     - LABEL_DUMMY_SWITCHER
     * 
     * Before phase 4:
     *   - For hyperedges:
     *     - HYPEREDGE_DUMMY_MERGER
     *   
     *   - For hierarchical ports:
     *     - HIERARCHICAL_PORT_DUMMY_SIZE_PROCESSOR
     *     
     *   - For edge labels:
     *     - LABEL_SIDE_SELECTOR
     * 
     * Before phase 5:
     * 
     * After phase 5:
     *   - For non-free ports:
     *     - NORTH_SOUTH_PORT_POSTPROCESSOR
     *   
     *   - For hierarchical ports:
     *     - HIERARCHICAL_PORT_ORTHOGONAL_EDGE_ROUTER
     *     
     *   - For center edge labels:
     *     - LABEL_DUMMY_REMOVER
     *     
     *   - For end edge labels:
     *     - END_LABEL_PROCESSOR
     */
    
    /** additional processor dependencies for graphs with hyperedges. */
    private static final IntermediateProcessingConfiguration HYPEREDGE_PROCESSING_ADDITIONS =
        IntermediateProcessingConfiguration.createEmpty()
            .addBeforePhase4(IntermediateProcessorStrategy.HYPEREDGE_DUMMY_MERGER);
    
    /** additional processor dependencies for graphs with possible inverted ports. */
    private static final IntermediateProcessingConfiguration INVERTED_PORT_PROCESSING_ADDITIONS =
        IntermediateProcessingConfiguration.createEmpty()
            .addBeforePhase3(IntermediateProcessorStrategy.INVERTED_PORT_PROCESSOR);
    
    /** additional processor dependencies for graphs with northern / southern non-free ports. */
    private static final IntermediateProcessingConfiguration NORTH_SOUTH_PORT_PROCESSING_ADDITIONS =
        IntermediateProcessingConfiguration.createEmpty()
            .addBeforePhase3(IntermediateProcessorStrategy.NORTH_SOUTH_PORT_PREPROCESSOR)
            .addAfterPhase5(IntermediateProcessorStrategy.NORTH_SOUTH_PORT_POSTPROCESSOR);
    
    /** additional processor dependencies for graphs with hierarchical ports. */
    private static final IntermediateProcessingConfiguration HIERARCHICAL_PORT_PROCESSING_ADDITIONS =
        IntermediateProcessingConfiguration.createEmpty()
            .addBeforePhase3(IntermediateProcessorStrategy.HIERARCHICAL_PORT_CONSTRAINT_PROCESSOR)
            .addBeforePhase4(IntermediateProcessorStrategy.HIERARCHICAL_PORT_DUMMY_SIZE_PROCESSOR)
            .addAfterPhase5(IntermediateProcessorStrategy.HIERARCHICAL_PORT_ORTHOGONAL_EDGE_ROUTER);
    
    /** additional processor dependencies for graphs with self-loops. */
    private static final IntermediateProcessingConfiguration SELF_LOOP_PROCESSING_ADDITIONS =
        IntermediateProcessingConfiguration.createEmpty()
            .addBeforePhase3(IntermediateProcessorStrategy.SELF_LOOP_PROCESSOR);
    
    /** additional processor dependencies for graphs with hypernodes. */
    private static final IntermediateProcessingConfiguration HYPERNODE_PROCESSING_ADDITIONS =
        IntermediateProcessingConfiguration.createEmpty()
            .addAfterPhase5(IntermediateProcessorStrategy.HYPERNODE_PROCESSOR);
    
    /** additional processor dependencies for graphs with center edge labels. */
    private static final IntermediateProcessingConfiguration CENTER_EDGE_LABEL_PROCESSING_ADDITIONS =
        IntermediateProcessingConfiguration.createEmpty()
            .addBeforePhase2(IntermediateProcessorStrategy.LABEL_DUMMY_INSERTER)
            .addBeforePhase3(IntermediateProcessorStrategy.LABEL_DUMMY_SWITCHER)
            .addBeforePhase4(IntermediateProcessorStrategy.LABEL_SIDE_SELECTOR)
            .addAfterPhase5(IntermediateProcessorStrategy.LABEL_DUMMY_REMOVER);
    
    /** additional processor dependencies for graphs with head or tail edge labels. */
    private static final IntermediateProcessingConfiguration END_EDGE_LABEL_PROCESSING_ADDITIONS =
        IntermediateProcessingConfiguration.createEmpty()
            .addBeforePhase4(IntermediateProcessorStrategy.LABEL_SIDE_SELECTOR)
            .addAfterPhase5(IntermediateProcessorStrategy.END_LABEL_PROCESSOR);
    
    /**
     * {@inheritDoc}
     */
    public IntermediateProcessingConfiguration getIntermediateProcessingConfiguration(
            final LGraph graph) {
        
        Set<GraphProperties> graphProperties = graph.getProperty(InternalProperties.GRAPH_PROPERTIES);
        
        // Basic configuration
        IntermediateProcessingConfiguration configuration =
                IntermediateProcessingConfiguration.createEmpty();
        
        // Additional dependencies
        if (graphProperties.contains(GraphProperties.HYPEREDGES)) {
            configuration.addAll(HYPEREDGE_PROCESSING_ADDITIONS);
            configuration.addAll(INVERTED_PORT_PROCESSING_ADDITIONS);
        }
        
        if (graphProperties.contains(GraphProperties.NON_FREE_PORTS)
                || graph.getProperty(LayeredOptions.FEEDBACK_EDGES)) {
            
            configuration.addAll(INVERTED_PORT_PROCESSING_ADDITIONS);

            if (graphProperties.contains(GraphProperties.NORTH_SOUTH_PORTS)) {
                configuration.addAll(NORTH_SOUTH_PORT_PROCESSING_ADDITIONS);
            }
        }

        if (graphProperties.contains(GraphProperties.EXTERNAL_PORTS)) {
            configuration.addAll(HIERARCHICAL_PORT_PROCESSING_ADDITIONS);
        }

        if (graphProperties.contains(GraphProperties.SELF_LOOPS)) {
            configuration.addAll(SELF_LOOP_PROCESSING_ADDITIONS);
        }
        
        if (graphProperties.contains(GraphProperties.HYPERNODES)) {
            configuration.addAll(HYPERNODE_PROCESSING_ADDITIONS);
        }
        
        if (graphProperties.contains(GraphProperties.CENTER_LABELS)) {
            configuration.addAll(CENTER_EDGE_LABEL_PROCESSING_ADDITIONS);
        }
        
        if (graphProperties.contains(GraphProperties.END_LABELS)) {
            configuration.addAll(END_EDGE_LABEL_PROCESSING_ADDITIONS);
        }
        
        return configuration;
    }
    
    /**
     * {@inheritDoc}
     */
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Orthogonal edge routing", 1);
        
        // Retrieve some generic values
        Spacings spacings = layeredGraph.getProperty(InternalProperties.SPACINGS);
        boolean debug = layeredGraph.getProperty(LayeredOptions.DEBUG_MODE);
        
        // Prepare for iteration!
        OrthogonalRoutingGenerator routingGenerator = new OrthogonalRoutingGenerator(
                OrthogonalRoutingGenerator.RoutingDirection.WEST_TO_EAST,
                spacings.edgeEdgeSpacing, debug ? "phase5" : null);
        float xpos = 0.0f;
        ListIterator<Layer> layerIter = layeredGraph.getLayers().listIterator();
        Layer leftLayer = null;
        Layer rightLayer = null;
        List<LNode> leftLayerNodes = null;
        List<LNode> rightLayerNodes = null;
        int leftLayerIndex = -1;
        int rightLayerIndex = -1;
        
        // Iterate!
        do {
            int slotsCount;
            
            // Fetch the next layer, if any
            rightLayer = layerIter.hasNext() ? layerIter.next() : null;
            rightLayerNodes = rightLayer == null ? null : rightLayer.getNodes();
            rightLayerIndex = layerIter.previousIndex();
            
            // Place the left layer's nodes, if any
            if (leftLayer != null) {
                LGraphUtil.placeNodesHorizontally(leftLayer, xpos);
                xpos += leftLayer.getSize().x;
            }
            
            // Route edges between the two layers
            double startPos = leftLayer == null ? xpos : xpos + spacings.edgeNodeSpacing;
            slotsCount = routingGenerator.routeEdges(layeredGraph, leftLayerNodes, leftLayerIndex,
                    rightLayerNodes, startPos);
            
            boolean isLeftLayerExternal = leftLayer == null || Iterables.all(leftLayerNodes,
                    PolylineEdgeRouter.PRED_EXTERNAL_WEST_OR_EAST_PORT);
            boolean isRightLayerExternal = rightLayer == null || Iterables.all(rightLayerNodes,
                    PolylineEdgeRouter.PRED_EXTERNAL_WEST_OR_EAST_PORT);
            
            if (slotsCount > 0) {
                // The space between each pair of edge segments, and between nodes and edges
                double increment =
                        spacings.edgeNodeSpacing + (slotsCount - 1) * spacings.edgeEdgeSpacing;
                if (rightLayer != null) {
                    increment += spacings.edgeNodeSpacing;
                }
                
                // If we are between two layers, make sure their minimal spacing is preserved
                if (increment < spacings.nodeNodeSpacing && !isLeftLayerExternal && !isRightLayerExternal) {
                    increment = spacings.nodeNodeSpacing;
                }
                xpos += increment;
            } else if (!isLeftLayerExternal && !isRightLayerExternal) {
                // If all edges are straight, use the usual spacing 
                xpos += spacings.nodeNodeSpacing;
            }
            
            leftLayer = rightLayer;
            leftLayerNodes = rightLayerNodes;
            leftLayerIndex = rightLayerIndex;
        } while (rightLayer != null);
        
        layeredGraph.getSize().x = xpos;
        
        monitor.done();
    }
    
}
