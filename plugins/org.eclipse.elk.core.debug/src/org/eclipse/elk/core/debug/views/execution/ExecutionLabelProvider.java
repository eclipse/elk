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
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Label provider for executions.
 */
final class ExecutionLabelProvider extends LabelProvider {

    /** Path to the image used for elements. */
    private static final String IMAGE_PATH = "/icons/execution.gif";
    /** The image used for each element. */
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
        if (element instanceof Execution) {
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
        if (element instanceof Execution) {
            Execution execution = (Execution) element;
            
            StringBuilder result = new StringBuilder(execution.getName())
                    .append(": ")
                    .append(timeToString(execution.getExecutionTimeIncludingChildren()));
            
            if (!execution.getChildren().isEmpty()) {
                result
                    .append(" [")
                    .append(timeToString(execution.getExecutionTimeLocal()))
                    .append(" local]");
            }
            
            return result.toString();
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
    private String timeToString(final double time) {
        if (time >= 1.0) {
            return String.format("%1$.3fs", time);
        } else {
            // SUPPRESS CHECKSTYLE NEXT MagicNumber
            return String.format("%1$.3fms", time * 1000);
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
