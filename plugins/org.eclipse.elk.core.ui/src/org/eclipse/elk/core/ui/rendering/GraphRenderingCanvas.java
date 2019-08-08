/*******************************************************************************
 * Copyright (c) 2008, 2019 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.core.ui.rendering;

import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.graph.ElkBendPoint;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkLabel;
import org.eclipse.elk.graph.ElkNode;
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
 */
public class GraphRenderingCanvas extends Canvas implements PaintListener {

    /** the painted layout graph. */
    private ElkNode layoutGraph;
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
    
    @Override
    public void dispose() {
        graphRenderer.dispose();
        backgroundColor.dispose();
    }

    /**
     * Sets the given layout graph as the painted graph.
     * 
     * @param graph layout graph to be painted
     */
    public void setLayoutGraph(final ElkNode graph) {
        KVector baseOffset = calculateRequiredCanvasSizeAndBaseOffset(graph);
        this.layoutGraph = graph;
        graphRenderer.setBaseOffset(baseOffset);
        redraw();
    }
    
    /**
     * Sets size of the canvas by looking up the biggest distances between graph elements in x- and y-direction
     * and return a KVector with the required offset of the origin coordinates to fit all elements on the canvas.
     * 
     * @param graph to be painted
     */
    private KVector calculateRequiredCanvasSizeAndBaseOffset(final ElkNode graph) {
        if (graph != null) {
            double minX = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;
            double minY = Double.MAX_VALUE;
            double maxY = Double.MIN_VALUE;

            // check all nodes for their coordinates
            for (ElkNode node : graph.getChildren()) {
                minX = Math.min(minX, node.getX());
                maxX = Math.max(maxX, node.getX() + node.getWidth());
                minY = Math.min(minY, node.getY());
                maxY = Math.max(maxY, node.getY() + node.getHeight());
                
                // check node labels
                for (ElkLabel nodeLabel : node.getLabels()) {
                    minX = Math.min(minX, node.getX() + nodeLabel.getX());
                    maxX = Math.max(maxX, node.getX() + nodeLabel.getX() + nodeLabel.getWidth());
                    minY = Math.min(minY, node.getY() + nodeLabel.getY());
                    maxY = Math.max(maxY, node.getY() + nodeLabel.getY() + nodeLabel.getHeight());
                }
            }
            
            for (ElkEdge edge : graph.getContainedEdges()) {
                // check all sections of the edges for their coordinates
                for (ElkEdgeSection edgeSection : edge.getSections()) {
                    minX = Math.min(minX, edgeSection.getStartX());
                    minY = Math.min(minY, edgeSection.getStartY());
                    maxX = Math.max(maxX, edgeSection.getStartX());
                    maxY = Math.max(maxY, edgeSection.getStartY());

                    minX = Math.min(minX, edgeSection.getEndX());
                    minY = Math.min(minY, edgeSection.getEndY());
                    maxX = Math.max(maxX, edgeSection.getEndX());
                    maxY = Math.max(maxY, edgeSection.getEndY());

                    for (ElkBendPoint bendPoint : edgeSection.getBendPoints()) {
                        minX = Math.min(minX, bendPoint.getX());
                        minY = Math.min(minY, bendPoint.getY());
                        maxX = Math.max(maxX, bendPoint.getX());
                        maxY = Math.max(maxY, bendPoint.getY());
                    }
                }
                
                // check edge labels
                for (ElkLabel edgeLabel : edge.getLabels()) {
                    minX = Math.min(minX, edgeLabel.getX());
                    maxX = Math.max(maxX, edgeLabel.getX() + edgeLabel.getWidth());
                    minY = Math.min(minY, edgeLabel.getY());
                    maxY = Math.max(maxY, edgeLabel.getY() + edgeLabel.getHeight());
                }
            }
            
            int x = ((int) (Math.max(graph.getWidth(), maxX) - Math.min(0, minX))) + 1;
            int y = ((int) (Math.max(graph.getHeight(), maxY) - Math.min(0, minY))) + 1;
            
            setSize(new Point(x, y));
            return new KVector((-Math.min(0, minX)), (-Math.min(0, minY)));
        }
        
        return new KVector();
    }

    /**
     * Returns the currently painted layout graph.
     * 
     * @return the painted layout graph
     */
    public ElkNode getLayoutGraph() {
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
