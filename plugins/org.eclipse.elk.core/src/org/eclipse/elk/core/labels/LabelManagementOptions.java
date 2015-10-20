/*******************************************************************************
 * Copyright (c) 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.labels;

import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * Layout options specifically concerned with label management.
 * 
 * @author cds
 */
public final class LabelManagementOptions {
    
    /**
     * The label manager responsible for a given part of the graph. A label manager can either be
     * attached to a compound node (in which case it is responsible for all labels inside) or to specific
     * labels. The label manager can then be called by layout algorithms to modify labels that are too
     * wide to try and shorten them to a given target width.
     */
    public static final IProperty<ILabelManager> LABEL_MANAGER =
            new Property<ILabelManager>("org.eclipse.elk.labels.labelManager", null);
    
    
    /**
     * Not supposed to be instantiated.
     */
    private LabelManagementOptions() {
        throw new IllegalStateException("not supposed to be instantiated.");
    }
    
}
