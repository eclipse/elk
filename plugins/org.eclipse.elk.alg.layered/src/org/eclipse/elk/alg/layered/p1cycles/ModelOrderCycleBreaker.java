/*******************************************************************************
 * Copyright (c) 2021 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p1cycles;

import java.util.List;

import org.eclipse.elk.alg.layered.LayeredPhases;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.intermediate.IntermediateProcessorStrategy;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.alg.layered.options.OrderingStrategy;
import org.eclipse.elk.alg.layered.options.PortType;
import org.eclipse.elk.core.UnsupportedConfigurationException;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.collect.Lists;

/**
 * A cycle breaker that reverses all edges that go against the model order,
 * i.e. edges from high model order to low model order.
 * Requires considerModelOrder to be a non NONE value.
 * 
 * <dl>
 *   <dt>Precondition:</dt><dd>none</dd>
 *   <dt>Postcondition:</dt><dd>the graph has no cycles</dd>
 * </dl>
 * 
 * @author sdo
 */
public final class ModelOrderCycleBreaker implements ILayoutPhase<LayeredPhases, LGraph> {

    /** intermediate processing configuration. */
    private static final LayoutProcessorConfiguration<LayeredPhases, LGraph> INTERMEDIATE_PROCESSING_CONFIGURATION =
        LayoutProcessorConfiguration.<LayeredPhases, LGraph>create()
            .addAfter(LayeredPhases.P5_EDGE_ROUTING, IntermediateProcessorStrategy.REVERSED_EDGE_RESTORER);

    @Override
    public LayoutProcessorConfiguration<LayeredPhases, LGraph> getLayoutProcessorConfiguration(final LGraph graph) {
        return INTERMEDIATE_PROCESSING_CONFIGURATION;
    }

    @Override
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Model order cycle breaking", 1);
        
        if (layeredGraph.getProperty(LayeredOptions.CONSIDER_MODEL_ORDER) != OrderingStrategy.NONE) {
            // gather edges that point to the wrong direction
            List<LEdge> revEdges = Lists.newArrayList();
            for (LNode source : layeredGraph.getLayerlessNodes()) {
                int modelOrderSource = source.getProperty(InternalProperties.MODEL_ORDER);
                for (LPort port : source.getPorts(PortType.OUTPUT)) {
                    for (LEdge edge : port.getOutgoingEdges()) {
                        LNode target = edge.getTarget().getNode();
                        if (target != source) {
                            int modelOrderTarget = target.getProperty(InternalProperties.MODEL_ORDER);
                            if (modelOrderTarget < modelOrderSource) {
                                revEdges.add(edge);
                            }
                        }
                    }
                }
            }
            
            // reverse the gathered edges
            for (LEdge edge : revEdges) {
                edge.reverse(layeredGraph, true);
                layeredGraph.setProperty(InternalProperties.CYCLIC, true);
            }
            revEdges.clear();
        } else {
            throw new UnsupportedConfigurationException(
                    "Model order has to be considered to use the model order cycle breaker " + this.toString());
        }
        monitor.done();
    }
}
