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
package org.eclipse.elk.core.ui.views;

import java.util.Collection;
import java.util.Map;

import org.eclipse.elk.core.config.IMutableLayoutConfig;
import org.eclipse.elk.core.config.LayoutContext;
import org.eclipse.elk.core.service.DiagramLayoutEngine;
import org.eclipse.elk.core.service.EclipseLayoutConfig;
import org.eclipse.elk.core.service.IDiagramLayoutManager;
import org.eclipse.elk.core.service.LayoutManagersService;
import org.eclipse.elk.core.service.LayoutOptionManager;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;

import com.google.common.collect.Maps;

/**
 * A property source provider used by the layout view. This provider queries the
 * {@link LayoutOptionManager} in order to obtain the valid options for the current selection.
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating yellow 2012-10-26 review KI-29 by cmot, sgu
 */
public class LayoutPropertySourceProvider implements IPropertySourceProvider {

    /** property sources that have been created for the current selection. */
    private final Map<Object, LayoutPropertySource> propertySources = Maps.newHashMap();
    /** the workbench part containing the current selection. */
    private IWorkbenchPart workbenchPart;
    
    /**
     * Reset the cached content of this property source provider, so new
     * property sources will be created on request.
     */
    public void resetContext() {
        propertySources.clear();
    }
    
    /**
     * Reset the cached content and set up for a new workbench part.
     * 
     * @param theworkbenchPart a workbench part, or {@code null}
     */
    public void resetContext(final IWorkbenchPart theworkbenchPart) {
        this.workbenchPart = theworkbenchPart;
        resetContext();
    }
    
    /**
     * {@inheritDoc}
     * A property source is only created if a valid diagram layout manager is found for the given
     * object in the {@link LayoutManagersService}.
     */
    public IPropertySource getPropertySource(final Object object) {
        if (propertySources.containsKey(object)) {
            return propertySources.get(object);
        }
        IDiagramLayoutManager<?> manager = LayoutManagersService.getInstance().getManager(
                workbenchPart, object);
        if (manager != null) {
            IMutableLayoutConfig diagramConfig = manager.getDiagramConfig();
            if (diagramConfig != null) {
                // create a layout context and enrich it with basic information
                LayoutContext context = new LayoutContext();
                context.setProperty(EclipseLayoutConfig.WORKBENCH_PART, workbenchPart);
                context.setProperty(LayoutContext.DIAGRAM_PART, object);
                
                // create a compound layout configurator using the layout option manager
                LayoutOptionManager optionManager = DiagramLayoutEngine.INSTANCE.getOptionManager();
                Object domainElement = diagramConfig.getContextValue(LayoutContext.DOMAIN_MODEL,
                        context);
                IMutableLayoutConfig compoundConfig = optionManager.createConfig(domainElement,
                        diagramConfig);
                
                LayoutPropertySource propSource = new LayoutPropertySource(compoundConfig, context);
                
                propertySources.put(object, propSource);
                return propSource;
            }
        }
        return null;
    }
    
    /**
     * Return the currently tracked workbench part.
     * 
     * @return the current workbench part
     */
    public IWorkbenchPart getWorkbenchPart() {
        return workbenchPart;
    }
    
    /**
     * Return a layout context for the current selection. This context will merge
     * information of all objects in the selection, but most values will be overridden
     * by the last element of the selection.
     * 
     * @return a layout context for the selection
     */
    public LayoutContext getContext() {
        Collection<LayoutPropertySource> sources = propertySources.values();
        LayoutContext context = new LayoutContext();
        for (LayoutPropertySource s : sources) {
            context.copyProperties(s.getContext());
        }
        return context;
    }
    
    /**
     * Determine whether the property source provider has any cached content.
     * 
     * @return true if there is cached content
     */
    public boolean hasContent() {
        return !propertySources.isEmpty();
    }

}
