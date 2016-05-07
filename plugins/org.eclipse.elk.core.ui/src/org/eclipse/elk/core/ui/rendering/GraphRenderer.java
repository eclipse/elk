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

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.elk.core.klayoutdata.KEdgeLayout;
import org.eclipse.elk.core.klayoutdata.KInsets;
import org.eclipse.elk.core.klayoutdata.KPoint;
import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.math.ElkMath;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.EdgeType;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KLabel;
import org.eclipse.elk.graph.KNode;
import org.eclipse.elk.graph.KPort;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Utility class that is able to render an ELK graph instance. This is primarily a debug tool.
 *
 * @author msp
 * @author cds
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow 2012-07-10 msp
 */
public class GraphRenderer {
    
    /** default length of edge arrows. */
    private static final double ARROW_LENGTH = 8.0f;
    /** default width of edge arrows. */
    private static final double ARROW_WIDTH = 7.0f;
    /** the minimal font height for displaying labels. */
    private static final int MIN_FONT_HEIGHT = 3;
    
    /** mapping of each layout graph element to its computed bounds. */
    private final Map<Object, PaintRectangle> boundsMap = new LinkedHashMap<Object, PaintRectangle>();
    /** configurator used to configure what the drawing looks like. */
    private GraphRenderingConfigurator configurator;
    /** the scale factor for all coordinates. */
    private double scale = 1.0;
    /** the base offset for all coordinates. */
    private KVector baseOffset = new KVector();
    /**
     * The most recently drawn graph; if the next graph to be drawn is different, we will automatically
     * flush our cache.
     */
    private KNode mostRecentlyDrawnGraph = null;
    
    // CHECKSTYLEOFF MagicNumber
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////
    // Initialization / Cleanup
    
    /**
     * Creates new renderer that uses the given configurator to define the way drawings look and a
     * default scaling of 1.0.
     * 
     * @param configurator the rendering configurator.
     */
    public GraphRenderer(final GraphRenderingConfigurator configurator) {
        this(configurator, 1.0);
    }
    
    /**
     * Creates new renderer that uses the given configurator to define the way drawings look.
     * 
     * @param configurator the rendering configurator.
     * @param scale the scaling factor to draw graphs with.
     */
    public GraphRenderer(final GraphRenderingConfigurator configurator, final double scale) {
        this.scale = scale;
        this.configurator = configurator;
        this.configurator.initialize(scale);
    }

    /**
     * Dispose all created resources such as colors and fonts.
     */
    public void dispose() {
        flushCache();
        mostRecentlyDrawnGraph = null;
        configurator.dispose();
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////
    // Getters / Setters
    
    /**
     * Returns the base offset to be added to all element coordinates when drawing them.
     */
    public KVector getBaseOffset() {
        return baseOffset;
    }

    /**
     * Sets the base offset to be added to all element coordinates when drawing them.
     * 
     * @param baseOffset the new base offset.
     */
    public void setBaseOffset(final KVector baseOffset) {
        if (baseOffset == null) {
            this.baseOffset = new KVector();
        } else {
            this.baseOffset = baseOffset;
        }
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////
    // Drawing Control
    
    /**
     * Mark all objects in the given area as dirty.
     * 
     * @param area the area to mark as dirty, or {@code null} if all objects shall be marked
     */
    public void markDirty(final Rectangle area) {
        for (PaintRectangle rectangle : boundsMap.values()) {
            if (area == null || rectangle.intersects(area)) {
                rectangle.painted = false;
            }
        }
    }
    
    /**
     * Clear all internally cached data on painted graphs. Call this method if the graph to be drawn
     * next changes.
     */
    private void flushCache() {
        boundsMap.clear();
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////
    // Rendering Code
    
    /**
     * Paints the contained layout graph onto the given graphics object.
     * 
     * @param parentNode the parent node that shall be rendered
     * @param graphics the graphics context used to paint
     * @param area the area to fill
     */
    public void render(final KNode parentNode, final GC graphics, final Rectangle area) {
        if (mostRecentlyDrawnGraph != parentNode) {
            flushCache();
            mostRecentlyDrawnGraph = parentNode;
        }
        
        // activate interpolation
        graphics.setInterpolation(SWT.HIGH);
        
        // determine an overall alpha value for nodes, depending on the maximal node depth
        int maxDepth = Math.max(maxDepth(parentNode), 1);
        int nodeAlpha = 200 / maxDepth + 55;
        
        // render the nodes and ports
        Set<KEdge> edgeSet = new HashSet<KEdge>();
        renderNodeChildren(parentNode, graphics, area, baseOffset, edgeSet, nodeAlpha);
        
        // render the edges
        for (KEdge edge : edgeSet) {
            renderEdge(parentNode, edge, graphics, area, nodeAlpha);
        }
    }

    /**
     * Paints all children of the given parent node that fall into the given dirty area.
     * 
     * @param parent the node whose children to paint
     * @param graphics the graphics context used to paint
     * @param area dirty area that needs painting
     * @param offset offset to be added to relative coordinates
     * @param edgeSet set to be filled with edges that are found on the way
     * @param nodeAlpha alpha value for nodes
     */
    private void renderNodeChildren(final KNode parent, final GC graphics, final Rectangle area,
            final KVector offset, final Set<KEdge> edgeSet, final int nodeAlpha) {
        
        for (KNode child : parent.getChildren()) {
            PaintRectangle rect = boundsMap.get(child);
            if (rect == null) {
                rect = new PaintRectangle(child.getData(KShapeLayout.class), offset, scale);
                boundsMap.put(child, rect);
            }
            KVector childOffset = new KVector(rect.x, rect.y);
            
            // render the child node and its content
            if (!rect.painted && rect.intersects(area)) {
                // paint this node
                graphics.setAlpha(nodeAlpha);
                
                if (configurator.getNodeFillColor() != null) {
                    graphics.setBackground(configurator.getNodeFillColor());
                    graphics.fillRectangle(rect.x, rect.y, rect.width, rect.height);
                }
                
                if (configurator.getNodeBorderColor() != null) {
                    graphics.setForeground(configurator.getNodeBorderColor());
                    graphics.drawRectangle(rect.x, rect.y, rect.width, rect.height);
                }
                
                rect.painted = true;
                
                // compute the offset required to make the children's coordinates absolute
                KVector contentOffset = new KVector(childOffset);
                KInsets insets = child.getData(KShapeLayout.class).getInsets();
                contentOffset.add(insets.getLeft() * scale, insets.getTop() * scale);
                
                renderNodeChildren(child, graphics, area, contentOffset, edgeSet, nodeAlpha);
            }
            
            // render node labels
            if (configurator.getNodeLabelFont() != null) {
                graphics.setFont(configurator.getNodeLabelFont());
                for (KLabel label : child.getLabels()) {
                    renderLabel(label, graphics, area, childOffset, nodeAlpha);
                }
            }

            // render ports
            for (KPort port : child.getPorts()) {
                renderPort(port, graphics, area, childOffset, nodeAlpha);
            }

            // store all incident edges to render them later
            edgeSet.addAll(child.getIncomingEdges());
            edgeSet.addAll(child.getOutgoingEdges());
        }
    }
    
    /**
     * Paints a label for the given dirty area. Expects the correct font to be already set.
     * 
     * @param label the label to paint
     * @param graphics the graphics context used to paint
     * @param area dirty area that needs painting
     * @param offset offset to be added to relative coordinates
     * @param labelAlpha alpha value for labels
     */
    private void renderLabel(final KLabel label, final GC graphics, final Rectangle area,
            final KVector offset, final int labelAlpha) {
        
        if (graphics.getFont().getFontData()[0].getHeight() >= MIN_FONT_HEIGHT) {
            PaintRectangle rect = boundsMap.get(label);
            KShapeLayout labelLayout = label.getData(KShapeLayout.class);
            if (rect == null) {
                rect = new PaintRectangle(labelLayout, offset, scale);
                boundsMap.put(label, rect);
            }
            
            if (!rect.painted && rect.intersects(area)) {
                // render the border and filling
                graphics.setAlpha(labelAlpha);
                
                if (configurator.getLabelFillColor() != null) {
                    graphics.setBackground(configurator.getLabelFillColor());
                    graphics.fillRectangle(rect.x, rect.y, rect.width, rect.height);
                }
                
                if (configurator.getLabelBorderColor() != null) {
                    graphics.setForeground(configurator.getLabelBorderColor());
                    graphics.drawRectangle(rect.x, rect.y, rect.width, rect.height);
                }
                
                // render the text
                String text = label.getText();
                if (text != null && text.length() > 0) {
                    graphics.setAlpha(255);
                    graphics.setForeground(configurator.getLabelTextColor());
                    
                    Rectangle oldClip = graphics.getClipping();
                    graphics.setClipping(rect.x, rect.y, rect.width, rect.height);
                    graphics.drawString(text, rect.x, rect.y, true);
                    graphics.setClipping(oldClip);
                }
                
                rect.painted = true;
            }
        }
    }
    
    /**
     * Paints a port for the given dirty area.
     * 
     * @param port the port to paint
     * @param graphics the graphics context used to paint
     * @param area dirty area that needs painting
     * @param offset offset to be added to relative coordinates
     * @param labelAlpha alpha value for labels
     */
    private void renderPort(final KPort port, final GC graphics, final Rectangle area,
            final KVector offset, final int labelAlpha) {
        
        PaintRectangle rect = boundsMap.get(port);
        if (rect == null) {
            rect = new PaintRectangle(port.getData(KShapeLayout.class), offset, scale);
            boundsMap.put(port, rect);
        }
        
        if (!rect.painted && rect.intersects(area)) {
            graphics.setAlpha(255);
            
            if (configurator.getPortFillColor() != null) {
                graphics.setBackground(configurator.getPortFillColor());
                graphics.fillRectangle(rect.x, rect.y, rect.width, rect.height);
            }
            
            if (configurator.getPortBorderColor() != null) {
                graphics.setForeground(configurator.getPortBorderColor());
                graphics.drawRectangle(rect.x, rect.y, rect.width, rect.height);
            }
            
            rect.painted = true;
        }
        
        // paint port labels
        if (configurator.getPortLabelFont() != null) {
            graphics.setFont(configurator.getPortLabelFont());
            KVector portOffset = new KVector(rect.x, rect.y);
            for (KLabel label : port.getLabels()) {
                renderLabel(label, graphics, area, portOffset, labelAlpha);
            }
        }
    }

    /**
     * Paints an edge for the given dirty area.
     * 
     * @param graph the top-level node of the graph
     * @param edge the edge to paint
     * @param graphics the graphics context used to paint
     * @param area dirty area that needs painting
     * @param labelAlpha alpha value for labels
     */
    private void renderEdge(final KNode graph, final KEdge edge, final GC graphics,
            final Rectangle area, final int labelAlpha) {
        
        if (configurator.getEdgeColor() == null) {
            return;
        }
        
        if (!ElkUtil.isDescendant(edge.getSource(), graph)
                || !ElkUtil.isDescendant(edge.getTarget(), graph)) {
            
            // the edge points to some node outside of the rendered subgraph
            return;
        }
        
        // calculate an offset for edge coordinates
        KNode parent = edge.getSource();
        if (!ElkUtil.isDescendant(edge.getTarget(), parent)) {
            parent = parent.getParent();
        }
        KNode node = parent;
        KVector offset = new KVector();
        while (node != graph) {
            KShapeLayout nodeLayout = node.getData(KShapeLayout.class);
            KInsets insets = nodeLayout.getInsets();
            offset.add(nodeLayout.getXpos() + insets.getLeft(),
                    nodeLayout.getYpos() + insets.getTop());
            node = node.getParent();
        }
        offset.scale(scale).add(baseOffset);
        
        KEdgeLayout edgeLayout = edge.getData(KEdgeLayout.class);
        PaintRectangle rect = boundsMap.get(edge);
        
        if (rect == null) {
            rect = new PaintRectangle(edgeLayout, offset, scale);
            boundsMap.put(edge, rect);
        }
        
        if (!rect.painted && rect.intersects(area)) {
            graphics.setAlpha(255);
            
            // The background color is required to fill the arrow of directed edges
            graphics.setForeground(configurator.getEdgeColor());
            graphics.setBackground(configurator.getEdgeColor());
            
            KVectorChain bendPoints = edgeLayout.createVectorChain();
            if (edgeLayout.getProperty(CoreOptions.EDGE_ROUTING) == EdgeRouting.SPLINES) {
                bendPoints = ElkMath.approximateBezierSpline(bendPoints);
            }
            bendPoints.scale(scale).offset(offset);
            KVector point1 = bendPoints.getFirst();
            for (KVector point2 : bendPoints) {
                graphics.drawLine((int) Math.round(point1.x), (int) Math.round(point1.y),
                        (int) Math.round(point2.x), (int) Math.round(point2.y));
                point1 = point2;
            }
            
            if (edgeLayout.getProperty(CoreOptions.EDGE_TYPE) != EdgeType.UNDIRECTED) {
                // draw an arrow at the last segment of the connection
                int[] arrowPoly = makeArrow(bendPoints.get(bendPoints.size() - 2), bendPoints.getLast());
                if (arrowPoly != null) {
                    graphics.fillPolygon(arrowPoly);
                }
            }
            
            rect.painted = true;
        }
        
        // paint junction points
        KVectorChain vc = edgeLayout.getProperty(CoreOptions.JUNCTION_POINTS);
        if (vc != null) {
            for (KVector v : vc) {
                KVector center = v.clone().scale(scale).add(offset).sub(2, 2);
                graphics.fillOval((int) center.x, (int) center.y, 6, 6);
            }
        }
        
        // paint the edge labels
        if (configurator.getEdgeLabelFont() != null) {
            graphics.setFont(configurator.getEdgeLabelFont());
            for (KLabel label : edge.getLabels()) {
                renderLabel(label, graphics, area, offset, labelAlpha);
            }
        }
    }

    /**
     * Constructs a polygon that forms an arrow.
     * 
     * @param point1 source point
     * @param point2 target point
     * @return array of coordinates for the arrow polygon, or null if the given source and target
     *         points are equal
     */
    private int[] makeArrow(final KVector point1, final KVector point2) {
        if (!(point1.x == point2.x && point1.y == point2.y) && ARROW_WIDTH * scale >= 2) {
            int[] arrow = new int[6];
            arrow[0] = (int) Math.round(point2.x);
            arrow[1] = (int) Math.round(point2.y);

            double vectX = point1.x - point2.x;
            double vectY = point1.y - point2.y;
            double length = Math.sqrt(vectX * vectX + vectY * vectY);
            double normX = vectX / length;
            double normY = vectY / length;
            double neckX = point2.x + ARROW_LENGTH * normX * scale;
            double neckY = point2.y + ARROW_LENGTH * normY * scale;
            double orthX = normY * ARROW_WIDTH / 2 * scale;
            double orthY = -normX * ARROW_WIDTH / 2 * scale;

            arrow[2] = (int) Math.round(neckX + orthX);
            arrow[3] = (int) Math.round(neckY + orthY);
            arrow[4] = (int) Math.round(neckX - orthX);
            arrow[5] = (int) Math.round(neckY - orthY);
            return arrow;
        } else {
            return null;
        }
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////
    // Utility Things
    
    /**
     * Determine the maximal depth of the given graph.
     * 
     * @param parent the parent node of the graph
     * @return the maximal depth of contained nodes
     */
    private int maxDepth(final KNode parent) {
        int maxDepth = 0;
        for (KNode child : parent.getChildren()) {
            int depth = maxDepth(child) + 1;
            if (depth > maxDepth) {
                maxDepth = depth;
            }
        }
        return maxDepth;
    }

    /**
     * Rectangle class used to mark painted objects.
     */
    private static class PaintRectangle {
        private int x, y, width, height;
        private boolean painted = false;

        /**
         * Creates a paint rectangle from a shape layout object.
         * 
         * @param shapeLayout shape layout from which values shall be taken
         * @param offset offset to be added to coordinate values
         */
        PaintRectangle(final KShapeLayout shapeLayout, final KVector offset, final double scale) {
            this.x = (int) Math.round(shapeLayout.getXpos() * scale + offset.x);
            this.y = (int) Math.round(shapeLayout.getYpos() * scale + offset.y);
            this.width = Math.max((int) Math.round(shapeLayout.getWidth() * scale), 1);
            this.height = Math.max((int) Math.round(shapeLayout.getHeight() * scale), 1);
        }

        /**
         * Creates a paint rectangle from an edge layout object.
         * 
         * @param edgeLayout edge layout from which the values shall be determined
         * @param offset offset to be added to coordinate values
         * @param scale the scale to apply to all coordinates
         */
        PaintRectangle(final KEdgeLayout edgeLayout, final KVector offset, final double scale) {
            float minX = edgeLayout.getSourcePoint().getX(), minY = edgeLayout.getSourcePoint()
                    .getY();
            float maxX = minX, maxY = minY;
            for (KPoint point : edgeLayout.getBendPoints()) {
                minX = Math.min(minX, point.getX());
                minY = Math.min(minY, point.getY());
                maxX = Math.max(maxX, point.getX());
                maxY = Math.max(maxY, point.getY());
            }
            minX = Math.min(minX, edgeLayout.getTargetPoint().getX());
            minY = Math.min(minY, edgeLayout.getTargetPoint().getY());
            maxX = Math.max(maxX, edgeLayout.getTargetPoint().getX());
            maxY = Math.max(maxY, edgeLayout.getTargetPoint().getY());
            this.x = (int) Math.round(minX * scale + offset.x);
            this.y = (int) Math.round(minY * scale + offset.y);
            this.width = (int) Math.round((maxX - minX) * scale);
            this.height = (int) Math.round((maxY - minY) * scale);
        }
        
        /**
         * Determines whether the given rectangle intersects with the receiver.
         * 
         * @param other the rectangle to check for intersection
         * @return true if the other rectangle intersects with this one
         */
        public boolean intersects(final Rectangle other) {
            return (other.x < this.x + this.width) && (other.y < this.y + this.height)
                    && (other.x + other.width > this.x) && (other.y + other.height > this.y);
        }
    }

}
