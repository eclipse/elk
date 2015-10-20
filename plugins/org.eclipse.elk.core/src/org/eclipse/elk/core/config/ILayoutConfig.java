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
package org.eclipse.elk.core.config;

import java.util.Collection;

import org.eclipse.elk.core.LayoutOptionData;
import org.eclipse.elk.graph.properties.IProperty;

/**
 * Layout option configurator interface. Implementations are used to determine the
 * <em>abstract layout</em>, which consists of a mapping of layout options to specific values for
 * each graph element. The available layout configurators are managed by
 * {@link org.eclipse.elk.core.service.LayoutOptionManager}. There the available configurators
 * are first used to enrich the context of a graph element with required information,
 * then the actual layout option values are transferred to the graph element data holder.
 * Enrichment is done by querying context properties from
 * {@link #getContextValue(IProperty, LayoutContext)}.
 * A context property is a property assigned to the {@link LayoutContext}.
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating yellow 2012-07-05 review KI-18 by cmot, sgu
 */
public interface ILayoutConfig {
    
    /**
     * Return the priority of this layout configurator, which is relevant when multiple configurators
     * are applied. A greater number means higher priority.
     * 
     * @return the priority
     * @see CompoundLayoutConfig
     */
    int getPriority();
    
    /**
     * Get the current value for a context property. The class {@link LayoutContext} itself declares
     * some standard properties that should be considered. Further properties are defined in
     * {@link DefaultLayoutConfig}.
     * 
     * @param property a context property
     * @param context the context from which basic information can be extracted
     * @return the property value, or {@code null} if no value can be derived from the given context
     */
    Object getContextValue(IProperty<?> property, LayoutContext context);
    
    /**
     * Get the current value for a layout option in the given context.
     * 
     * @param optionData a layout option descriptor
     * @param context a context for layout configuration
     * @return the layout option value, or {@code null} if no value can be derived from the context
     */
    Object getOptionValue(LayoutOptionData optionData, LayoutContext context);
    
    /**
     * Determine the layout options that are affected by this layout configurator. For all returned
     * options, the {@link #getOptionValue(LayoutOptionData, LayoutContext)} method must not return
     * {@code null} when called with the same context.
     * 
     * @param context a context for layout configuration
     * @return a collection of layout options affected by this configurator
     */
    Collection<IProperty<?>> getAffectedOptions(LayoutContext context);

}
