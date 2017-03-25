/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.util.nodespacing.internal;

import org.eclipse.elk.core.math.ElkMargin;
import org.eclipse.elk.core.math.ElkRectangle;
import org.eclipse.elk.core.util.adapters.GraphAdapters.PortAdapter;

/**
 * Data holder class to be passed around to avoid having too much state in the size calculation classes. Port contexts
 * are part of {@link NodeContext node contexts}.
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


    /////////////////////////////////////////////////////////////////////////////////
    // Calculated Things
    
    /**
     * Margin aroung the port to assume when placing the port. If node labels are taken into consideration, this will
     * for example include the label space. When placing the ports, this is the size the port will be assumed to have.
     */
    public ElkMargin portMargin = new ElkMargin();
    /** Amount of space required for all of the port's labels, and the position. */
    public ElkRectangle labelSpace = new ElkRectangle();
    /** How labels are aligned inside the label space. */
    public LabelAlignment labelAlignment = null;
    
    
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
    }
    
}
