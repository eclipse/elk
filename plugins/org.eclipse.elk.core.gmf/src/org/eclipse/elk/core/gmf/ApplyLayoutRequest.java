/*******************************************************************************
 * Copyright (c) 2009, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.gmf;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.elk.core.klayoutdata.KLayoutData;
import org.eclipse.elk.core.util.Pair;
import org.eclipse.elk.graph.KGraphElement;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;

/**
 * Request for automatic layout on a set of edit parts of a diagram.
 * 
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public class ApplyLayoutRequest extends Request {

    /** the request type used to apply layout. */
    public static final String REQ_APPLY_LAYOUT = "apply layout";
    
    /** list of layout graph elements and the corresponding edit parts. */
    private List<Pair<KGraphElement, GraphicalEditPart>> mappingList
            = new LinkedList<Pair<KGraphElement, GraphicalEditPart>>();
    /** the upper bound for horizontal coordinates. */
    private float boundx = Float.MAX_VALUE;
    /** the upper bound for vertical coordinates. */
    private float boundy = Float.MAX_VALUE;
    /** the scale factor for all coordinates. */
    private float scale = 1.0f;
    
    /**
     * Creates a request to apply layout.
     */
    public ApplyLayoutRequest() {
        super(REQ_APPLY_LAYOUT);
    }

    /**
     * Adds the given graph element and edit part to the request, if it has been modified by
     * a layout algorithm.
     * 
     * @param element graph element with layout data
     * @param editPart the corresponding edit part
     */
    public void addElement(final KGraphElement element, final GraphicalEditPart editPart) {
        if (element.getData(KLayoutData.class).isModified()) {
            mappingList.add(new Pair<KGraphElement, GraphicalEditPart>(element, editPart));
        }
    }
    
    /**
     * Returns a list of the graph elements and edit parts of this request.
     * 
     * @return a list with graph elements and corresponding edit parts
     */
    public List<Pair<KGraphElement, GraphicalEditPart>> getElements() {
        return mappingList;
    }
    
    /**
     * Set an upper bound on the coordinates of the layout graph.
     * 
     * @param x the upper bound for horizontal coordinates
     * @param y the upper bound for vertical coordinates
     */
    public void setUpperBound(final float x, final float y) {
        this.boundx = x;
        this.boundy = y;
    }
    
    /**
     * Returns the upper bound for horizontal coordinates.
     * 
     * @return the x bound
     */
    public float getXBound() {
        return boundx;
    }
    
    /**
     * Returns the upper bound for vertical coordinates.
     * 
     * @return the y bound
     */
    public float getYBound() {
        return boundy;
    }
    
    /**
     * Returns the factor to use for scaling coordinates.
     * 
     * @return the scale factor
     */
    public float getScale() {
        return scale;
    }
    
    /**
     * Sets the factor to use for scaling coordinates.
     * 
     * @param thescale the scale factor
     */
    public void setScale(final float thescale) {
        this.scale = thescale;
    }
    
}
