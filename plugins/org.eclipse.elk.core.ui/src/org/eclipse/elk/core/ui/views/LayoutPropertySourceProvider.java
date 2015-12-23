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

import java.util.Map;

import org.eclipse.elk.core.service.IDiagramLayoutManager;
import org.eclipse.elk.core.service.ILayoutConfigurationStore;
import org.eclipse.elk.core.service.LayoutManagersService;
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
    /** the last selected object. */
    private Object lastSelection;
    
    /**
     * Clear the internal cache of property sources.
     */
    public void clearCache() {
        lastSelection = null;
        propertySources.clear();
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
     * Set the currently tracked workbench part.
     * 
     * @param theworkbenchPart a workbench part, or {@code null}
     */
    public void setWorkbenchPart(final IWorkbenchPart theworkbenchPart) {
        if (this.workbenchPart != theworkbenchPart) {
            propertySources.clear();
            this.workbenchPart = theworkbenchPart;
        }
    }
    
    /**
     * Return the currently active configuration store, or {@code null} if none is active.
     */
    public ILayoutConfigurationStore getConfigurationStore() {
        LayoutPropertySource source = propertySources.get(lastSelection);
        if (source != null) {
            return source.getConfigurationStore();
        }
        return null;
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
            ILayoutConfigurationStore diagramConfig = manager.getConfigurationStore(workbenchPart, object);
            if (diagramConfig != null) {
                LayoutPropertySource propSource = new LayoutPropertySource(diagramConfig);
                propertySources.put(object, propSource);
                lastSelection = object;
                return propSource;
            }
        }
        return null;
    }

}
