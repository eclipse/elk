/*******************************************************************************
 * Copyright (c) 2011, 2019 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.debug.views.graph;

import java.io.IOException;

import org.eclipse.elk.core.IGraphLayoutEngine;
import org.eclipse.elk.core.RecursiveGraphLayoutEngine;
import org.eclipse.elk.core.debug.ElkDebugPlugin;
import org.eclipse.elk.core.debug.model.ExecutionInfo;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.service.DiagramLayoutEngine;
import org.eclipse.elk.core.service.ElkServicePlugin;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

/**
 * An action for loading an ELK graph file and performing layout on it.
 * 
 * TODO Introduce view option to switch of automatic layout upon loading graphs.
 */
public class LoadGraphAction extends Action {
    
    /** identifier string for this action. */
    private static final String ACTION_ID = "org.eclipse.elk.debug.loadGraph";
    
    /** relative path to the icon to use for this action. */
    private static final String ICON_PATH = "icons/import.gif";
    /** preference identifier for the last used file name. */
    private static final String LAST_FILE_NAME_PREF = "loadGraphAction.lastGraphFile";
    
    public LoadGraphAction() {
        setId(ACTION_ID);
        setText("Load Graph");
        setToolTipText("Load, layout, and display a saved ELK graph.");
        setImageDescriptor(ElkDebugPlugin.imageDescriptorFromPlugin(ElkDebugPlugin.PLUGIN_ID, ICON_PATH));
    }
    
    @Override
    public void run() {
        IPreferenceStore prefStore = ElkDebugPlugin.getDefault().getPreferenceStore();

        // Create and configure file dialog
        FileDialog fileDialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
        fileDialog.setFilterExtensions(new String[] { "*.elkg", "*.elkt", "*.xmi", "*.*" });
        fileDialog.setText("Select Graph File");
        fileDialog.setFileName(prefStore.getString(LAST_FILE_NAME_PREF));

        // Open the file dialog and wait for file name
        String fileName = fileDialog.open();
        
        if (fileName != null) {
            prefStore.setValue(LAST_FILE_NAME_PREF, fileName);
            
            // Load the file content
            ResourceSet resourceSet = new ResourceSetImpl();
            URI uri = URI.createFileURI(fileName);
            Resource resource = resourceSet.createResource(uri);
            
            try {
                resource.load(null);
                ElkNode content = (ElkNode) resource.getContents().get(0);
                layout(fileName, content);
            } catch (IOException exception) {
                throw new WrappedException(exception);
            }
        }
    }
    
    /**
     * Perform layout. Since we do that through the layout service, our plugin will find out once layout is complete
     * and add an entry to the debug view tree viewers.
     */
    private void layout(final String fileName, final ElkNode graph) {
        IPreferenceStore prefStore = ElkServicePlugin.getInstance().getPreferenceStore();
        IElkProgressMonitor monitor = new BasicProgressMonitor(0)
                .withLogging(prefStore.getBoolean(DiagramLayoutEngine.PREF_DEBUG_LOGGING))
                .withExecutionTimeMeasurement(prefStore.getBoolean(DiagramLayoutEngine.PREF_DEBUG_EXEC_TIME));
        monitor.begin(fileName, 1);
        
        // perform layout using a graph layout engine
        if (!graph.getProperty(CoreOptions.NO_LAYOUT)) {
            IGraphLayoutEngine layoutEngine = new RecursiveGraphLayoutEngine();
            layoutEngine.layout(graph, monitor.subTask(1));
        }
        
        monitor.done();
        
        // We're not going through the DiagramLayoutEngine, but directly through the RecursiveGraphLayoutEngine, which
        // means that not layout events will be fired. We'll have to update our model manually.
        monitor.logGraph(graph, "Result");
        ElkDebugPlugin.getDefault().getModel().addExecution(ExecutionInfo.fromProgressMonitor(monitor));
    }

}
