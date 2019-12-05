/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.common.nodespacing.internal;

import org.eclipse.elk.alg.common.nodespacing.cellsystem.LabelCell;
import org.eclipse.elk.core.math.ElkMargin;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.util.adapters.GraphAdapters.PortAdapter;

/**
 * Data holder class to be passed around to avoid having too much state in the size calculation classes. Port contexts
 * are part of {@link NodeContext node contexts}. The position of a port calculated as part of the algorithm should 
 * first be stored in {@link #portPosition} and only be applied at the end of the algorithm, if required.
 */
public final class PortContext {
    
    // CHECKSTYLEOFF Visibility
    // This is a purely internal data holder class, so we ditch setters for public fields.
    
    /////////////////////////////////////////////////////////////////////////////////
    // Convenience Access to Things
    
    /** The node the port belongs to. */
    public final NodeContext parentNodeContext;
    /** The port we calculate stuff for. */
    public final PortAdapter<?> port;
    /** The port's position, to be modified by the algorithm and possibly applied later. */
    public final KVector portPosition;


    /////////////////////////////////////////////////////////////////////////////////
    // Calculated Things
    
    /**
     * Margin aroung the port to assume when placing the port. If node labels are taken into consideration, this will
     * for example include the label cell. When placing the ports, this is the size the port will be assumed to have.
     */
    public ElkMargin portMargin = new ElkMargin();
    /** The cell we place our port labels in. */
    public LabelCell portLabelCell;
    
    
    /////////////////////////////////////////////////////////////////////////////////
    // Creation
    
    /**
     * Creates a new context object for the given port, fully initialized with the port's settings.
     * 
     * @param parentNodeContext the port's parent node context.
     * @param port the port to create the context for.
     */
    public PortContext(final NodeContext parentNodeContext, final PortAdapter<?> port) {
        this.parentNodeContext = parentNodeContext;
        this.port = port;
        this.portPosition = new KVector(port.getPosition());
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////
    // Application
    
    /**
     * Applies the port position stored in this context to the actual port.
     */
    public void applyPortPosition() {
        port.setPosition(portPosition);
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////
    // Utility Methods
    
    /**
     * Checks whether the port represented by this context has connections to the inside. It has connections to the
     * inside if one of its edges leads to or comes from a descendant if the port's node, or if the node has inside
     * self loops enabled and 
     * @return
     */
    public boolean hasConnectionsToTheInside() {
        return port.getOutgoingEdges().iterator().hasNext() || port.getIncomingEdges().iterator().hasNext();
    }
}
