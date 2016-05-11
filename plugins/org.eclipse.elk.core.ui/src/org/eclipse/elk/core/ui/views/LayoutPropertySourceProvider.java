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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.elk.core.LayoutOptionValidator;
import org.eclipse.elk.core.service.ILayoutConfigurationStore;
import org.eclipse.elk.core.service.ILayoutSetup;
import org.eclipse.elk.core.service.LayoutConfigurationManager;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * A property source provider used by the layout view.
 * 
 * <p>Subclasses of this class can be bound in an {@link ILayoutSetup} injector for customization.</p>
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating yellow 2012-10-26 review KI-29 by cmot, sgu
 */
public class LayoutPropertySourceProvider implements IPropertySourceProvider {
    
    /**
     * The layout configuration store provider interacting with the created property sources.
     */
    @Inject(optional = true)
    private ILayoutConfigurationStore.Provider configurationStoreProvider;
    
    /**
     * The layout configuration manager used to handle configuration stores.
     */
    @Inject
    private LayoutConfigurationManager configManager;
    
    /**
     * Provider for validators used to check bounds of layout option values.
     */
    @Inject
    private Provider<LayoutOptionValidator> layoutOptionValidatorProvider;

    /**
     * The workbench part containing the current selection.
     */
    private IWorkbenchPart workbenchPart;
    
    /**
     * The requested configuration stores.
     */
    private final List<ILayoutConfigurationStore> configStores = new LinkedList<ILayoutConfigurationStore>();
    
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
        this.workbenchPart = theworkbenchPart;
    }
    
    /**
     * Return the list of requested layout configuration stores.
     */
    public List<ILayoutConfigurationStore> getConfigurationStores() {
        return configStores;
    }
    
    /**
     * Return the active layout configuration manager.
     */
    public LayoutConfigurationManager getConfigurationManager() {
        return configManager;
    }
    
    /**
     * {@inheritDoc}
     */
    public IPropertySource getPropertySource(final Object object) {
        if (configurationStoreProvider != null) {
            ILayoutConfigurationStore config = configurationStoreProvider.get(workbenchPart, object);
            if (config != null) {
                configStores.add(config);
                return createPropertySource(config);
            }
        }
        return null;
    }
    
    /**
     * Create a property source for the given configuration store.
     */
    protected IPropertySource createPropertySource(final ILayoutConfigurationStore config) {
        LayoutPropertySource result = new LayoutPropertySource(config, getConfigurationManager());
        result.setValidator(layoutOptionValidatorProvider.get());
        return result;
    }
    
}
