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

import java.util.List;

import org.eclipse.elk.core.math.ElkRectangle;
import org.eclipse.elk.core.util.adapters.GraphAdapters.LabelAdapter;

import com.google.common.collect.Lists;

/**
 * Data holder class to be passed around to avoid having too much state in the size calculation classes. Some of the
 * most relevant settings are copied into variables for convenience.
 */
public class LabelLocationContext {
   
    // CHECKSTYLEOFF Visibility
    // This is a purely internal data holder class, so we ditch setters for public fields.
    
    /////////////////////////////////////////////////////////////////////////////////
    // Convenience Access to Things
    
    /** The node that owns this context. */
    public final NodeContext nodeContext;
    /** The label location represented by this context. */
    public final LabelLocation labelLocation;


    /////////////////////////////////////////////////////////////////////////////////
    // Calculated Things
    
    /** List of labels to be placed at this location. */
    public final List<LabelAdapter<?>> labels = Lists.newArrayList();
    /** Rectangle that represents the space occupied by the labels at this location. */
    public ElkRectangle labelSpace;
    
    
    /////////////////////////////////////////////////////////////////////////////////
    // Creation
    
    /**
     * Creates a new context object for the given label location of the given node.
     * 
     * @param nodeContext the node.
     * @param labelLocation the label location represented by this context.
     */
    public LabelLocationContext(final NodeContext nodeContext, final LabelLocation labelLocation) {
        this.nodeContext = nodeContext;
        this.labelLocation = labelLocation;
    }

}
