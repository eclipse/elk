/*******************************************************************************
 * Copyright (c) 2018, 2019 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.intermediate.loops.SelfLoopHolder;
import org.eclipse.elk.alg.layered.intermediate.loops.SelfLoopPort;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Finds regular nodes with self loops and preprocesses those loops.
 *
 * <dl>
 *   <dt>Precondition:</dt>
 *     <dd>A layered graph.</dd>
 *   <dt>Postcondition:</dt>
 *     <dd>Each node with self loops has a {@link SelfLoopHolder} created for it and stored under the
 *         {@link InternalProperties#SELF_LOOP_HOLDER} property.
 *     <dd>All self loop edges are removed from their ports to not confuse subsequent phases.</dd>
 *     <dd>Unless port orders are fixed, all ports with only self loop edges connected to them are removed from their
 *         nodes in order to not confuse subsequent phases.</dd>
 *   <dt>Slots:</dt>
 *     <dd>Before phase 2.</dd>
 *   <dt>Same-slot dependencies:</dt>
 *     <dd>None.</dd>
 * </dl>
 */
public class SelfLoopPreProcessor implements ILayoutProcessor<LGraph> {

    @Override
    public void process(final LGraph graph, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Self-Loop pre-processing", 1);

        for (LNode lnode : graph.getLayerlessNodes()) {
            if (SelfLoopHolder.needsSelfLoopProcessing(lnode)) {
                SelfLoopHolder slHolder = SelfLoopHolder.install(lnode);
                hideSelfLoops(slHolder);
                hidePorts(slHolder);
            }
        }

        progressMonitor.done();
    }

    /**
     * Hides all self loop edges. These would confuse subsequent phases and are thus simply removed from the graph, to
     * be restored once edge routing has finished.
     */
    private void hideSelfLoops(final SelfLoopHolder slHolder) {
        slHolder.getSLHyperLoops().stream()
            .flatMap(slLoop -> slLoop.getSLEdges().stream())
            .map(slEdge -> slEdge.getLEdge())
            .forEach(lEdge -> hideSelfLoop(lEdge));
    }

    /**
     * Hides the given edge by removing it from its ports.
     */
    private void hideSelfLoop(final LEdge lEdge) {
        lEdge.setSource(null);
        lEdge.setTarget(null);
    }

    /**
     * Possibly hides all ports whose only incident edges are self loops. This is only done if port constraints are not
     * at least {@link PortConstraints#FIXED_ORDER}.
     */
    private void hidePorts(final SelfLoopHolder slHolder) {
        if (slHolder.getLNode().getProperty(LayeredOptions.PORT_CONSTRAINTS).isOrderFixed()) {
            // No need to hide any ports
            return;
        }
        
        for (SelfLoopPort slPort : slHolder.getSLPortMap().values()) {
            if (slPort.hadOnlySelfLoops()) {
                // Hide the port
                LPort lPort = slPort.getLPort();
                lPort.setNode(null);
                
                // Remember that we actually did so
                slPort.setHidden(true);
                slHolder.setPortsHidden(true);
                
                // Remove external port dummy this port belongs to, if any (#352)
                LNode dummy = lPort.getProperty(InternalProperties.PORT_DUMMY);
                if (dummy != null) {
                    Layer layer = dummy.getLayer();
                    if (layer == null) {
                        dummy.getGraph().getLayerlessNodes().remove(dummy);
                    } else {
                        layer.getNodes().remove(dummy);
                        if (layer.getNodes().isEmpty()) {
                            layer.getGraph().getLayers().remove(layer);
                        }
                    }
                }
            }
        }
    }

}
