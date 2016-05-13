/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.debug;

import org.eclipse.elk.core.debug.views.execution.ExecutionView;
import org.eclipse.elk.core.debug.views.graph.LayoutGraphView;
import org.eclipse.elk.core.service.ILayoutListener;
import org.eclipse.elk.core.service.LayoutConnectorsService;
import org.eclipse.elk.core.service.LayoutMapping;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ElkDebugPlugin extends AbstractUIPlugin {

	/** The plug-in ID. */
	public static final String PLUGIN_ID = "org.eclipse.elk.core.debug"; //$NON-NLS-1$
	/** The shared instance. */
	private static ElkDebugPlugin plugin;
	
	/** The layout listener we will be using to update the view we are contributing. */
	private ILayoutListener layoutListener = new ILayoutListener() {
        @Override
        public void layoutAboutToStart(final LayoutMapping mapping, final IElkProgressMonitor progressMonitor) {
        }
        
        @Override
        public void layoutDone(final LayoutMapping mapping, final IElkProgressMonitor progressMonitor) {
            // Update our views
            LayoutGraphView.updateWithGraph(mapping.getLayoutGraph());
            ExecutionView.addExecution(progressMonitor);
        }
    };
	
	/**
	 * The constructor
	 */
	public ElkDebugPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

        LayoutConnectorsService.getInstance().addLayoutListener(layoutListener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		
		LayoutConnectorsService.getInstance().removeLayoutListener(layoutListener);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ElkDebugPlugin getDefault() {
		return plugin;
	}

}
