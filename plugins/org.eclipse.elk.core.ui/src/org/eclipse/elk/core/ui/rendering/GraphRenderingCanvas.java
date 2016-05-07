/*******************************************************************************
 * Copyright (c) 2008, 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.ui.rendering;

import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.graph.KNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * A canvas that is able to paint ELK layout graphs. Colors and fonts used for painting can be
 * customized by supplying a subclass of {@link GraphRenderingConfigurator}.
 * 
 * @author msp
 */
public class GraphRenderingCanvas extends Canvas implements PaintListener {

    /** the painted layout graph. */
    private KNode layoutGraph;
    /** the graph renderer used for painting. */
    private GraphRenderer graphRenderer;
    
    /** background color. */
    private Color backgroundColor;

    /**
     * Creates a layout graph canvas using the default drawing style.
     * 
     * @param parent
     *            the parent widget
     */
    public GraphRenderingCanvas(final Composite parent) {
        super(parent, SWT.NONE);
        addPaintListener(this);
        graphRenderer = new GraphRenderer(new GraphRenderingConfigurator(parent.getDisplay()));
        
        // SUPPRESS CHECKSTYLE NEXT MagicNumber
        backgroundColor = new Color(parent.getDisplay(), 255, 255, 255);
        setBackground(backgroundColor);
    }

    /**
     * Creates a layout graph canvas.
     * 
     * @param parent
     *            the parent widget
     * @param configurator
     *            the rendering configurator that defines the colors and fonts used for drawing. The
     *            configurator must have been initialized with the same display this component is
     *            created for.
     * @throws IllegalArgumentException if the displays are not the same.
     */
    public GraphRenderingCanvas(final Composite parent, final GraphRenderingConfigurator configurator) {
        super(parent, SWT.NONE);
        
        if (!configurator.getDisplay().equals(parent.getDisplay())) {
            throw new IllegalArgumentException(
                    "configurator and parent composite must use the same display.");
        }
        
        addPaintListener(this);
        graphRenderer = new GraphRenderer(configurator);
        
        // SUPPRESS CHECKSTYLE NEXT MagicNumber
        backgroundColor = new Color(parent.getDisplay(), 255, 255, 255);
        setBackground(backgroundColor);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        graphRenderer.dispose();
        backgroundColor.dispose();
    }

    /**
     * Sets the given layout graph as the painted graph.
     * 
     * @param thelayoutGraph layout graph to be painted
     */
    public void setLayoutGraph(final KNode thelayoutGraph) {
        // set new size values for the canvas
        if (thelayoutGraph != null) {
            KShapeLayout shapeLayout = thelayoutGraph.getData(KShapeLayout.class);
            setSize(new Point((int) shapeLayout.getWidth() + 1, (int) shapeLayout.getHeight() + 1));
        }

        this.layoutGraph = thelayoutGraph;
        redraw();
    }

    /**
     * Returns the currently painted layout graph.
     * 
     * @return the painted layout graph
     */
    public KNode getLayoutGraph() {
        return layoutGraph;
    }
    
    /**
     * Returns the KGraph renderer used for painting.
     * 
     * @return the KGraph renderer
     */
    public GraphRenderer getRenderer() {
        return graphRenderer;
    }

    /**
     * This method is called when the canvas is requested to paint.
     * 
     * @param event
     *            paint event
     */
    public void paintControl(final PaintEvent event) {
        if (layoutGraph != null) {
            Rectangle area = new Rectangle(event.x, event.y, event.width, event.height);
            // reset paint information
            graphRenderer.markDirty(area);
            // paint the top layout nodes with their children
            graphRenderer.render(layoutGraph, event.gc, area);
        }
    }

}
