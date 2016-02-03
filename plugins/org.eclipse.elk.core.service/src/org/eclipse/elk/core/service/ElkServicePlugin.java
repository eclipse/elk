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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.elk.core.service.util.MonitoredOperation;
import org.eclipse.elk.core.util.Pair;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
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
    private final Multimap<Pair<IWorkbenchPart, Object>, MonitoredOperation> runningOperations
            = HashMultimap.create();
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
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
    
}
