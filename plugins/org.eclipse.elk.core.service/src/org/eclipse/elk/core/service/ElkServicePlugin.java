/*******************************************************************************
 * Copyright (c) 2008, 2015 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.core.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutMetaDataService;
import org.eclipse.elk.core.service.util.MonitoredOperation;
import org.eclipse.elk.core.util.Pair;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.framework.BundleContext;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author msp
 */
public final class ElkServicePlugin extends AbstractUIPlugin {

    /** The plug-in ID. */
    public static final String PLUGIN_ID = "org.eclipse.elk.core.service";

    /** The shared instance. */
    private static ElkServicePlugin plugin;

    /**
     * Returns the shared instance.
     * 
     * @return the shared instance
     */
    public static ElkServicePlugin getInstance() {
        return plugin;
    }
    
    /** The executor service used to perform layout operations. */
    private ExecutorService executorService;
    
    /** map of currently running layout operations. */
    private final Multimap<Pair<IWorkbenchPart, Object>, MonitoredOperation> runningOperations = HashMultimap.create();
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        
        loadLayoutProviders();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(final BundleContext context) throws Exception {
        LayoutMetaDataService.unload();
        LayoutConnectorsService.unload();
        if (executorService != null) {
            executorService.shutdown();
            executorService = null;
        }
        
        plugin = null;
        super.stop(context);
    }
    
    /**
     * Return the executor service to use for automatic layout operations.
     */
    public synchronized ExecutorService getExecutorService() {
        if (executorService == null && plugin != null) {
            executorService = Executors.newCachedThreadPool();
        }
        return executorService;
    }
    
    /**
     * Return the map of currently running layout operations.
     */
    public Multimap<Pair<IWorkbenchPart, Object>, MonitoredOperation> getRunningOperations() {
        return runningOperations;
    }
    
    
    /** identifier of the extension point for layout providers. */
    protected static final String EXTP_ID_LAYOUT_PROVIDERS = "org.eclipse.elk.core.layoutProviders";
    /** name of the 'provider' element in the 'layout providers' extension point. */
    protected static final String ELEMENT_PROVIDER = "provider";
    /** name of the 'class' attribute in the extension points. */
    protected static final String ATTRIBUTE_CLASS = "class";
    
    /**
     * Creates a new instance, loading the extension point information in the process.
     */
    private void loadLayoutProviders() {
        IConfigurationElement[] extensions = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(EXTP_ID_LAYOUT_PROVIDERS);
        LayoutMetaDataService service = LayoutMetaDataService.getInstance();
        
        for (IConfigurationElement element : extensions) {
            try {
                if (ELEMENT_PROVIDER.equals(element.getName())) {
                    ILayoutMetaDataProvider provider = (ILayoutMetaDataProvider)
                            element.createExecutableExtension(ATTRIBUTE_CLASS);
                    
                    if (provider != null) {
                        service.registerLayoutMetaDataProviders(provider);
                    }
                }
            } catch (CoreException exception) {
                StatusManager.getManager().handle(exception, ElkServicePlugin.PLUGIN_ID);
            }
        }
    }
    
}
