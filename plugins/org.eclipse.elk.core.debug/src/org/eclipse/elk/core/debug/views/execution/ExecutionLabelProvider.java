/*******************************************************************************
 * Copyright (c) 2009, 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.debug.views.execution;

import org.eclipse.elk.core.debug.ElkDebugPlugin;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Label provider for execution times retrieved from ELK progress monitors.
 */
public class ExecutionLabelProvider extends LabelProvider {

    /** path to the image used for elements. */
    private static final String IMAGE_PATH = "/icons/execution.gif";

    /** the image used for each element. */
    private Image elementImage;

    /**
     * Creates an execution label provider.
     */
    public ExecutionLabelProvider() {
        elementImage = ElkDebugPlugin.imageDescriptorFromPlugin(ElkDebugPlugin.PLUGIN_ID, IMAGE_PATH).createImage();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Image getImage(final Object element) {
        if (element instanceof IElkProgressMonitor) {
            return elementImage;
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText(final Object element) {
        if (element instanceof IElkProgressMonitor) {
            IElkProgressMonitor monitor = (IElkProgressMonitor) element;
            String baseText = monitor.getTaskName() + ": ";
            double time = monitor.getExecutionTime();
            if (monitor.getSubMonitors().isEmpty()) {
                return baseText + toString(time);
            } else {
                double childrenTime = 0;
                for (IElkProgressMonitor child : monitor.getSubMonitors()) {
                    childrenTime += child.getExecutionTime();
                }
                double localTime = Math.max(time - childrenTime, 0);
                return baseText + toString(time) + " [" + toString(localTime) + " local]";
            }
        } else {
            return null;
        }
    }
    
    /**
     * Convert the given time (in seconds) into a string.
     * 
     * @param time time in seconds
     * @return a string representation
     */
    private String toString(final double time) {
        if (time >= 1.0) {
            return String.format("%1$.3f s", time);
        } else {
            // SUPPRESS CHECKSTYLE NEXT MagicNumber
            return String.format("%1$.3f ms", time * 1000);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();
        if (elementImage != null) {
            elementImage.dispose();
            elementImage = null;
        }
    }

}
