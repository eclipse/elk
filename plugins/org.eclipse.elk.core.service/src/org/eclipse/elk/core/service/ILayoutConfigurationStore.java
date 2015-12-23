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
package org.eclipse.elk.core.service;

import java.util.Collection;
import java.util.Set;

import org.eclipse.elk.core.service.data.LayoutOptionData;
import org.eclipse.emf.edit.domain.EditingDomain;

/**
 * Persistence layer for layout configuration. Implementations are used by the Layout view to modify
 * values of layout options for selected diagram elements.
 *
 * @author msp
 */
public interface ILayoutConfigurationStore {
    
    /**
     * Return the editing domain for applying changes to the stored option values, or {@code null}
     * if no editing domain is necessary.
     */
    EditingDomain getEditingDomain();
    
    /**
     * Get the current value for a layout option in the associated context. The return value can be
     * either a literal value or a serialized form as a String.
     * 
     * @param optionId a layout option identifier
     * @return the layout option value, or {@code null} if no value is associated with the context
     */
    Object getOptionValue(String optionId);
    
    /**
     * Set a new value for a layout option in the associated context.
     * 
     * @param optionId a layout option identifier
     * @param value the new layout option value, or {@code null} if the current value shall be removed
     */
    void setOptionValue(String optionId, String value);
    
    /**
     * Retrieve all options that are affected by this configuration store.
     */
    Collection<String> getAffectedOptions();
    
    /**
     * Determine the applicable layout option targets for the associated context.
     * 
     * @return the applicable layout option targets
     */
    Set<LayoutOptionData.Target> getOptionTargets();
    
    /**
     * Return a configuration store for the parent element of the associated context, if any.
     * This should result in a container element that includes the current context.
     * 
     * @return a configuration store for the parent element or {@code null}
     */
    ILayoutConfigurationStore getParent();

}
