/*******************************************************************************
 * Copyright (c) 2019 Obeo and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.core.debug.views.graph;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * This adapter provides a drop support for the tree viewer of the Layout Graph view.
 */
public class LayoutGraphViewDropAdapter extends ViewerDropAdapter {
    /**
     * The Layout Graph view containing the viewer to which this drop support has been added.
     */
    private LayoutGraphView layoutGraphView;
    
    /**
     * Creates a new drop adapter for the given viewer.
     *
     * @param layoutGraphView the Layout Graph view containing the viewer
     * @param viewer the viewer
     */
    public LayoutGraphViewDropAdapter(final LayoutGraphView layoutGraphView, final Viewer viewer) {
        super(viewer);
        this.layoutGraphView = layoutGraphView;
    }
    
    public boolean validateDrop(final Object target, final int operation, final TransferData transferType) {
        return FileTransfer.getInstance().isSupportedType(transferType);
    }
    
    @Override
    public boolean performDrop(final Object data) {
        boolean result = true;
        String[] filePathsToDrop = (String[])data;
        if (filePathsToDrop.length == 0) {
            result = false;
        }
        for (String filePathToDrop : filePathsToDrop) {
            LoadGraphAction.run(filePathToDrop, layoutGraphView);
        }
        return result;
    }
}
