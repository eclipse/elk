/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Miro Spönemann - initial API and implementation
 *     Christoph Daniel Schulze - Adaptation to ELK
 *******************************************************************************/
package org.eclipse.elk.graph.text.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.GraphIdentifierGenerator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * A command handler that can be used to convert a graph from the textual format to the XMI format
 * and back. The default implementation only copies input models and makes certain modifications if
 * the target format is the textual format.
 * 
 * <p>Note that plug-ins containing subclasses of this handler must define their own convert graphs
 * command. Only then can they declare menu contributions that reference their new command, which
 * in turn defines their new subclass as command handler.</p>
 * 
 * @author msp
 */
public class ConvertGraphHandler extends AbstractHandler {

    /** parameter id for the target file extension. */
    private static final String PARAM_TARGET_EXT = "org.eclipse.elk.graph.text.convert.targetExtension";
    /** textual graph file extension. */
    private static final String EXT_ELK_TEXT = "elkt";
    /** XMI graph file extension. */
    private static final String EXT_ELK_XMI = "elkg";
    
    /** the target file extension. */
    private String targetExtension;
    
    /**
     * {@inheritDoc}
     */
    public final Object execute(final ExecutionEvent event) throws ExecutionException {
        targetExtension = event.getParameter(PARAM_TARGET_EXT);
        if (!isValidTargetExtension(targetExtension)) {
        	IStatus status = new Status(IStatus.ERROR, ElkGraphUiModule.PLUGIN_ID,
                    "Invalid target extension: " + targetExtension);
            StatusManager.getManager().handle(status, StatusManager.SHOW | StatusManager.LOG);
        	return null;
        }
        
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        
        if (selection instanceof IStructuredSelection) {
            final Object[] elements = ((IStructuredSelection) selection).toArray();

            // put the conversion into a job with a progress bar,
            // because the conversion may take a while :-/ 
            Job job = new Job("convert model") {
                protected IStatus run(final IProgressMonitor monitor) {
                    monitor.beginTask("Convert model", elements.length);
                    for (Object object : elements) {
                        if (monitor.isCanceled()) {
                            break;
                        }
                        
                        if (object instanceof IFile) {
                            convert((IFile) object);
                        }
                        monitor.worked(1);
                    }
                    monitor.done();
                    return Status.OK_STATUS;
                }
            };
            job.setUser(true);
            job.schedule();
        }
        
        return null;
    }

    /**
     * 
     * @param targetExtension
     * @return
     */
    private boolean isValidTargetExtension(final String targetExtension) {
		return targetExtension != null &&
				(EXT_ELK_TEXT.equals(targetExtension) || EXT_ELK_XMI.equals(targetExtension));
	}

	/**
     * Process a source file.
     * 
     * @param file a source file
     */
    public final void convert(final IFile file) {
        try {
            List<EObject> sourceModels = readModel(URI.createPlatformResourceURI(
                    file.getFullPath().toString(), false));
            List<EObject> targetModels = new ArrayList<EObject>(sourceModels.size());
            for (EObject model : sourceModels) {
                EObject transformed = transform(model);
                targetModels.add(transformed);
            }
            saveModel(targetModels, createTarget(file));
        } catch (Exception exception) {
            IStatus status = new Status(IStatus.ERROR, ElkGraphUiModule.PLUGIN_ID,
                    "Error while converting the selected graph.", exception);
            StatusManager.getManager().handle(status, StatusManager.SHOW | StatusManager.LOG);
        }
    }
    
    /**
     * Transform the graph before it is written to the new file format.
     * 
     * @param model a graph instance
     * @return the transformed model
     */
    protected EObject transform(final EObject model) {
        EObject copy = EcoreUtil.copy(model);
        
        if (targetExtension.equals(EXT_ELK_TEXT) && copy instanceof ElkNode) {
            // we want to convert to the textual format, so write missing identifiers into the graph
            GraphIdentifierGenerator.generate((ElkNode) copy);
        }
        
        return copy;
    }
    
    /**
     * Create a target file URI from a source file.
     * 
     * @param file a source file
     * @return a target file URI with the specified extension
     */
    private URI createTarget(final IFile file) {
        IPath basePath = file.getFullPath();
        String name = basePath.removeFileExtension().lastSegment();
        IPath targetPath = new Path(name + "." + targetExtension);
        int i = 0;
        IContainer parent = file.getParent();
        while (parent.exists(targetPath)) {
            targetPath = new Path(name + (++i) + "." + targetExtension);
        }
        targetPath = parent.getFullPath().append(targetPath);
        return URI.createPlatformResourceURI(targetPath.toString(), false);
    }

    /**
     * Read the model contents from the given URI.
     * 
     * @param uri the source file URI
     * @return a collection of models that were read
     */
    private List<EObject> readModel(final URI uri) {
        // Create a resource set.
        ResourceSet resourceSet = new ResourceSetImpl();
        // Demand load the resource for this file.
        Resource resource = resourceSet.getResource(uri, true);
        return resource.getContents();
    }

    /**
     * Save a collection of models to the given URI.
     * 
     * @param models the models to store in the new file
     * @param uri the target file URI
     * @throws IOException if an error occurs while saving
     */
    private void saveModel(final List<EObject> models, final URI uri) throws IOException {
        // Create a resource set.
        ResourceSet resourceSet = new ResourceSetImpl();
        // Create a resource for this file.
        Resource resource = resourceSet.createResource(uri);
        // Add the model objects to the contents.
        resource.getContents().addAll(models);
        // Save the contents of the resource to the file system.
        resource.save(Collections.EMPTY_MAP);
    }

}
