/*******************************************************************************
 * Copyright (c) 2011, 2016 Kiel University and others.
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
import java.util.Iterator;

import org.eclipse.elk.core.IGraphLayoutEngine;
import org.eclipse.elk.core.RecursiveGraphLayoutEngine;
import org.eclipse.elk.core.data.LayoutMetaDataService;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.core.debug.ElkDebugPlugin;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.EMapPropertyHolder;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.ElkPersistentEntry;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

/**
 * An action for loading a KGraph and performing layout on it.
 */
public class LoadGraphAction extends Action {
    
    /** identifier string for this action. */
    private static final String ACTION_ID = "org.eclipse.elk.debug.loadGraph";
    /** relative path to the icon to use for this action. */
    private static final String ICON_PATH = "icons/import.gif";
    /** preference identifier for the last used file name. */
    private static final String LAST_FILE_NAME_PREF = "loadGraphAction.lastGraphFile";
    
    /**
     * Creates a load graph action.
     */
    public LoadGraphAction() {
        setId(ACTION_ID);
        setText("Load Graph");
        setToolTipText("Load, layout, and display a saved ELK graph.");
        setImageDescriptor(ElkDebugPlugin.imageDescriptorFromPlugin(ElkDebugPlugin.PLUGIN_ID, ICON_PATH));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        IPreferenceStore preferenceStore = ElkDebugPlugin.getDefault().getPreferenceStore();

        // create and configure file dialog
        FileDialog fileDialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
        fileDialog.setFilterExtensions(new String[] { "*.kgraph", "*.kgx", "*.kgt", "*.xmi", "*.*" });
        fileDialog.setText("Select Graph File");
        fileDialog.setFileName(preferenceStore.getString(LAST_FILE_NAME_PREF));

        // open the file dialog and wait for file name
        String fileName = fileDialog.open();
        
        if (fileName != null) {
            preferenceStore.setValue(LAST_FILE_NAME_PREF, fileName);
            
            // load the file content
            ResourceSet resourceSet = new ResourceSetImpl();
            URI uri = URI.createFileURI(fileName);
            Resource resource = resourceSet.createResource(uri);
            try {
                resource.load(null);
                ElkNode content = (ElkNode) resource.getContents().get(0);
                layout(content);
            } catch (IOException exception) {
                throw new WrappedException(exception);
            }
        }
    }
    
    /**
     * Perform layout and draw the resulting graph.
     * 
     * @param graph a graph
     */
    private void layout(final ElkNode graph) {
        // deserialize layout options
        LayoutMetaDataService dataService = LayoutMetaDataService.getInstance();
        Iterator<EObject> contentIter = graph.eAllContents();
        while (contentIter.hasNext()) {
            EObject obj = contentIter.next();
            if (obj instanceof EMapPropertyHolder) {
                EMapPropertyHolder propertyHolder = (EMapPropertyHolder) obj;
                for (ElkPersistentEntry entry : propertyHolder.getPersistentEntries()) {
                    LayoutOptionData optionData = dataService.getOptionData(entry.getKey());
                    
                    if (optionData != null) {
                        Object value = optionData.parseValue(entry.getValue());
                        
                        if (value != null) {
                            propertyHolder.setProperty(optionData, value);
                        }
                    }
                }
            }
        }
        
        // perform layout using a graph layout engine
        IElkProgressMonitor monitor = new BasicProgressMonitor();
        if (!graph.getProperty(CoreOptions.NO_LAYOUT)) {
            IGraphLayoutEngine layoutEngine = new RecursiveGraphLayoutEngine();
            layoutEngine.layout(graph, monitor);
        }
        
        // draw the resulting layout on the canvas
        LayoutGraphView.updateWithGraph(graph);
    }

}
