/*******************************************************************************
 * Copyright (c) 2011, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.config;

import org.eclipse.elk.core.LayoutOptionData;

/**
 * An extension of the layout configuration interface for configurations that can be altered.
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating yellow 2013-07-01 review KI-38 by cds, uru
 */
public interface IMutableLayoutConfig extends ILayoutConfig {
    
    /**
     * Set a new value for a layout option in the given context.
     * 
     * @param optionData a layout option descriptor
     * @param context a context for layout configuration
     * @param value the new layout option value, or {@code null} if the current value shall be removed
     */
    void setOptionValue(LayoutOptionData optionData, LayoutContext context, Object value);
    
    /**
     * Clear all layout option values that have been set for the given context.
     * 
     * @param context a context for layout configuration
     */
    void clearOptionValues(LayoutContext context);
    
    /**
     * Determine whether the given layout option is set, not considering any default values.
     * 
     * @param optionData a layout option descriptor
     * @param context a context for layout configuration
     * @return true if the option is set
     */
    boolean isSet(LayoutOptionData optionData, LayoutContext context);

}
