/*******************************************************************************
 * Copyright (c) 2019 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.intermediate.loops.SelfLoopHolder;
import org.eclipse.elk.alg.layered.intermediate.loops.ordering.PortRestorer;
import org.eclipse.elk.alg.layered.intermediate.loops.ordering.PortSideAssigner;
import org.eclipse.elk.alg.layered.intermediate.loops.ordering.RoutingDirector;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Restores self loop ports and computes everything required to decide where the self loops will be routed. The
 * information will later be used to compute the actual routes.
 * 
 * <dl>
 * <dt>Preconditions:</dt>
 * <dd>A layered graph whose self-loop nodes have been preprocessed by {@link SelfLoopPreprocessor}.</dd>
 * <dt>Postconditions:</dt>
 * <dd>All ports previously removed are restored.</dd>
 * <dd>All ports are ordered such that they yield the best possible self-loop placement.</dd>
 * <dt>Slots:</dt>
 * <dd>Before phase 4.</dd>
 * <dt>Same-slot dependencies:</dt>
 * <dd>None</dd>
 * </dl>
 */
public class SelfLoopOrderer implements ILayoutProcessor<LGraph> {
    
    /** This thing will assign hidden ports to port sides if port constraints were initially free. */
    private final PortSideAssigner portSideAssigner = new PortSideAssigner();
    /** Port restorer we'll use for fixed side port constraints. */
    private final PortRestorer portRestorer = new PortRestorer();
    /** Routing director we'll use to compute how the loops are routed around the node. */
    private final RoutingDirector routingDirector = new RoutingDirector();

    @Override
    public void process(final LGraph graph, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Self-Loop ordering", 1);
        
        // Process every node that actually has self loops
        graph.getLayers().stream()
            .flatMap(layer -> layer.getNodes().stream())
            .filter(lNode -> lNode.getType() == NodeType.NORMAL)
            .filter(lNode -> lNode.hasProperty(InternalProperties.SELF_LOOP_HOLDER))
            .map(lNode -> lNode.getProperty(InternalProperties.SELF_LOOP_HOLDER))
            .forEach(slHolder -> processNode(slHolder, progressMonitor));
        
        progressMonitor.done();
    }

    private void processNode(final SelfLoopHolder slHolder, final IElkProgressMonitor monitor) {
        // Restore and order pure self loop ports if they were previously hidden
        if (slHolder.arePortsHidden()) {
            switch (slHolder.getLNode().getProperty(InternalProperties.ORIGINAL_PORT_CONSTRAINTS)) {
            case UNDEFINED:
            case FREE:
                // We need to assign port sides first and then fall through to restore ports
                portSideAssigner.assignPortSides(slHolder);
                
            case FIXED_SIDE:
                // Restore ports (which by now have port sides assigned to them). After this call, arePortsHidden() will
                // report false
                portRestorer.restorePorts(slHolder, monitor);
                
            default:
                // This should not happen. If ports were hidden this must have been because their order was not fixed
                assert false;
            }
        }
        
        // Compute how each hyper loop is routed around the node (from a "leftmost" to a "rightmost" port involved in
        // the loop, but those must be computed and will influence the number of crossings)
        routingDirector.determineLoopRoutes(slHolder);
        
        // Find out which port side each hyper loop appears on and assign routing slots such that the self loop "trunks"
        // (the pieces of the self loop that seem to run around the node, parallel to the respective node boundary) do
        // not intersect
        // TODO Do it!
        
        // TODO Can we also find out where to place labels? If so, can we even invoke label management now?
    }

}
