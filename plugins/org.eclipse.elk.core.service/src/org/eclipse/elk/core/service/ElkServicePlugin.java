/*******************************************************************************
 * Copyright (c) 2008, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.service;

import org.eclipse.elk.core.service.data.LayoutAlgorithmData;
import org.eclipse.elk.core.service.internal.EclipseLayoutMetaDataServiceRegistry;
import org.eclipse.elk.core.util.DefaultFactory;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public class ElkServicePlugin extends AbstractUIPlugin {

    /** The plug-in ID. */
    public static final String PLUGIN_ID = "org.eclipse.elk.core.service";

    /** The shared instance. */
    private static ElkServicePlugin plugin;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        
        // Initialize the layout meta data service (see LayoutDataService.getInstance())
        LayoutMetaDataService.setRegistryFactory(new DefaultFactory<LayoutMetaDataService.Registry>(
                EclipseLayoutMetaDataServiceRegistry.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(final BundleContext context) throws Exception {
        LayoutMetaDataService layoutDataService = LayoutMetaDataService.getInstance();
        for (LayoutAlgorithmData algoData : layoutDataService.getAlgorithmData()) {
            algoData.getInstancePool().clear();
        }
        
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance.
     * 
     * @return the shared instance
     */
    public static ElkServicePlugin getDefault() {
        return plugin;
    }
    
}
