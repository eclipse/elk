/*******************************************************************************
 * Copyright (c) 2011, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.gmf;

import org.eclipse.draw2d.Animation;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.service.IDiagramLayoutManager;
import org.eclipse.elk.core.service.LayoutMapping;
import org.eclipse.elk.graph.KNode;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.render.editparts.RenderedDiagramRootEditPart;

/**
 * An abstract diagram layout manager for GEF-based implementations.
 * This variant is tuned for GMF diagram editors, since it does not provide automatic zooming
 * for other diagram editors.
 *
 * @kieler.design proposed by msp
 * @kieler.rating yellow 2012-07-19 review KI-20 by cds, jjc
 * @param <T> the type of diagram part that is handled by this diagram layout manager
 * @author msp
 */
public abstract class GefDiagramLayoutManager<T> implements IDiagramLayoutManager<T> {

    /** the animation time used for layout. */
    public static final IProperty<Integer> ANIMATION_TIME = new Property<Integer>(
            "gef.animationTime", 0);
    
    /**
     * {@inheritDoc}
     */
    public void applyLayout(final LayoutMapping<T> mapping, final boolean zoomToFit,
            final int animationTime) {
        mapping.setProperty(ANIMATION_TIME, animationTime);
        Object layoutGraphObj = mapping.getParentElement();
        if (zoomToFit && layoutGraphObj instanceof EditPart) {
            // determine pre- or post-layout zoom
            DiagramEditPart diagramEditPart = GmfDiagramLayoutManager.getDiagramEditPart(
                    (EditPart) layoutGraphObj);
            if (diagramEditPart == null) {
                applyLayout(mapping, animationTime);
                return;
            }
            ZoomManager zoomManager = ((RenderedDiagramRootEditPart) diagramEditPart.getRoot())
                    .getZoomManager();
            KNode parentNode = mapping.getLayoutGraph();
            KShapeLayout parentLayout = parentNode.getData(KShapeLayout.class);
            Dimension available = zoomManager.getViewport().getClientArea().getSize();
            float desiredWidth = parentLayout.getWidth();
            double scaleX = Math.min(available.width / desiredWidth, zoomManager.getMaxZoom());
            float desiredHeight = parentLayout.getHeight();
            double scaleY = Math.min(available.height / desiredHeight, zoomManager.getMaxZoom());
            final double scale = Math.min(scaleX, scaleY);
            final double oldScale = zoomManager.getZoom();

            if (scale < oldScale) {
                // we're zooming out, so do it before layout is applied
                zoomManager.setViewLocation(new Point(0, 0));
                zoomManager.setZoom(scale);
                zoomManager.setViewLocation(new Point(0, 0));
            }
            
            applyLayout(mapping, animationTime);
            
            if (scale > oldScale) {
                // we're zooming in, so do it after layout is applied
                zoomManager.setViewLocation(new Point(0, 0));
                zoomManager.setZoom(scale);
                zoomManager.setViewLocation(new Point(0, 0));
            }
        } else {
            applyLayout(mapping, animationTime);
        }
    }
    
    /**
     * Apply the computed layout to the original diagram.
     * 
     * @param mapping a layout mapping that was created by this layout manager
     * @param animationTime the animation time in milliseconds, or 0 for no animation
     */
    private void applyLayout(final LayoutMapping<T> mapping, final int animationTime) {
        // transfer layout to the diagram
        transferLayout(mapping);
        if (animationTime > 0) {
            // apply the layout with animation
            Animation.markBegin();
            applyLayout(mapping);
            Animation.run(animationTime);
        } else {
            // apply the layout without animation
            applyLayout(mapping);
        }
    }

    /**
     * Transfer all layout data from the last created KGraph instance to the original diagram.
     * The diagram is not modified yet, but all required preparations are performed. This is
     * separated from {@link #applyLayout(LayoutMapping)} to allow better code modularization.
     * 
     * @param mapping a layout mapping that was created by this layout manager
     */
    protected abstract void transferLayout(LayoutMapping<T> mapping);
    
    /**
     * Apply the transferred layout to the original diagram. This final step is where the actual
     * change to the diagram is done. This method is always called after
     * {@link #transferLayout(LayoutMapping)} has been done.
     * 
     * @param mapping a layout mapping that was created by this layout manager
     */
    protected abstract void applyLayout(LayoutMapping<T> mapping);
    
    /**
     * {@inheritDoc}
     */
    public void undoLayout(final LayoutMapping<T> mapping) {
        int animationTime = mapping.getProperty(ANIMATION_TIME);
        if (animationTime > 0) {
            // undo the layout with animation
            Animation.markBegin();
            performUndo(mapping);
            Animation.run(animationTime);
        } else {
            // undo the layout without animation
            performUndo(mapping);
        }
    }
    
    /**
     * Perform undo in the original diagram (optional operation).
     * This implementation throws an {@code UnsupportedOperationException}.
     *
     * @param mapping a layout mapping that was created by this layout manager
     */
    protected void performUndo(final LayoutMapping<T> mapping) {
        throw new UnsupportedOperationException("Undo is not supported by this layout manager.");
    }

}
