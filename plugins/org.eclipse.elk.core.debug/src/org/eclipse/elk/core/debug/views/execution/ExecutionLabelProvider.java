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
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

/**
 * Label provider for executions.
 */
final class ExecutionLabelProvider extends LabelProvider implements IStyledLabelProvider {
    
    /** What the label provider can display. */
    public static enum DisplayMode { NAME, TIME_TOTAL, TIME_LOCAL };
    

    /** Path to the image used for elements. */
    private static final String IMAGE_PATH = "/icons/execution.gif";
    /** The image used for each element. */
    private Image elementImage;
    /** What we should display. */
    private DisplayMode displayMode;
    

    /**
     * Creates an execution label provider.
     * 
     * @param displayMode
     *            What this label provider should display.
     */
    public ExecutionLabelProvider(final DisplayMode displayMode) {
        elementImage = ElkDebugPlugin.imageDescriptorFromPlugin(ElkDebugPlugin.PLUGIN_ID, IMAGE_PATH).createImage();
        this.displayMode = displayMode;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Image getImage(final Object element) {
        if (displayMode == DisplayMode.NAME && element instanceof Execution) {
            return elementImage;
        } else {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider#getStyledText(java.lang.Object)
     */
    @Override
    public StyledString getStyledText(Object element) {
        if (element instanceof Execution) {
            Execution execution = (Execution) element;
            
            switch (displayMode) {
            case NAME:
                return new StyledString(execution.getName());
            case TIME_TOTAL:
                return new StyledString(timeToString(execution.getExecutionTimeIncludingChildren()));
            case TIME_LOCAL:
                return execution.getChildren().isEmpty()
                        ? new StyledString("")
                        : new StyledString(timeToString(execution.getExecutionTimeLocal()));
            default:
                return null;
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
    private String timeToString(final double time) {
        // SUPPRESS CHECKSTYLE NEXT MagicNumber
        return String.format("%1$.3f", time * 1000);
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
