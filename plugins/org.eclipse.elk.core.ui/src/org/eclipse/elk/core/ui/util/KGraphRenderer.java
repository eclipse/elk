/*******************************************************************************
 * Copyright (c) 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.ui.util;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.elk.core.klayoutdata.KEdgeLayout;
import org.eclipse.elk.core.klayoutdata.KInsets;
import org.eclipse.elk.core.klayoutdata.KPoint;
import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.math.ElkMath;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.EdgeType;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KLabel;
import org.eclipse.elk.graph.KNode;
import org.eclipse.elk.graph.KPort;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

/**
 * Utility class that is able to render a KGraph instance. This is primarily a debug tool.
 *
 * @author msp
 * @author cds
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow 2012-07-10 msp
 */
public class KGraphRenderer {
    
    /** default font size for nodes. */
    private static final int NODE_FONT_SIZE = 9;
    /** default font size for ports. */
    private static final int PORT_FONT_SIZE = 6;
    /** default font size for edges. */
    private static final int EDGE_FONT_SIZE = 8;
    /** default length of edge arrows. */
    private static final double ARROW_LENGTH = 8.0f;
    /** default width of edge arrows. */
    private static final double ARROW_WIDTH = 7.0f;

    /** the minimal font height for displaying labels. */
    private static final int MIN_FONT_HEIGHT = 3;
    
    /** mapping of each layout graph element to its computed bounds. */
    private final Map<Object, PaintRectangle> boundsMap = new LinkedHashMap<Object, PaintRectangle>();
    /** border color for nodes. */
    private Color nodeBorderColor;
    /** fill color for nodes. */
    private Color nodeFillColor;
    /** font used for node labels. */
    private Font nodeFont;
    /** border color for labels. */
    private Color labelBorderColor;
    /** text color for labels. */
    private Color labelTextColor;
    /** color used for ports. */
    private Color portColor;
    /** font used for port labels. */
    private Font portFont;
    /** color used for edges. */
    private Color edgeColor;
    /** font used for edge labels. */
    private Font edgeFont;
    
    /** the scale factor for all coordinates. */
    private double scale;
    /** the base offset for all coordinates. */
    private KVector baseOffset;
    
    // CHECKSTYLEOFF MagicNumber
    
    /**
     * Creates a KGraph renderer.
     * 
     * @param display the display for which to create colors and fonts
     */
    public KGraphRenderer(final Display display) {
        this(display, 1.0, new KVector());
    }
    
    /**
     * Creates a KGraph renderer with scaling.
     * 
     * @param display the display for which to create colors and fonts
     * @param thescale the scale factor for all coordinates
     * @param thebaseOffset the base offset for all coordinates
     */
    public KGraphRenderer(final Display display, final double thescale, final KVector thebaseOffset) {
        this.scale = thescale;
        this.baseOffset = thebaseOffset;

        int nodeFontSize = Math.max((int) Math.round(NODE_FONT_SIZE * thescale), 2);
        nodeFont = new Font(display, "sans", nodeFontSize, SWT.NORMAL);
        nodeBorderColor = new Color(display, 2, 15, 3);
        nodeFillColor = new Color(display, 168, 220, 190);
        
        labelBorderColor = new Color(display, 63, 117, 67);
        labelTextColor = new Color(display, 2, 15, 3);
        
        int portFontSize = Math.max((int) Math.round(PORT_FONT_SIZE * thescale), 2);
        portFont = new Font(display, "sans", portFontSize, SWT.NORMAL);
        portColor = new Color(display, 2, 9, 40);
        
        int edgeFontSize = Math.max((int) Math.round(EDGE_FONT_SIZE * thescale), 2);
        edgeFont = new Font(display, "sans", edgeFontSize, SWT.NORMAL);
        edgeColor = new Color(display, 23, 36, 54);
    }

    /**
     * Dispose all created resources such as colors and fonts.
     */
    public void dispose() {
        clear();
        nodeBorderColor.dispose();
        nodeFillColor.dispose();
        nodeFont.dispose();
        labelBorderColor.dispose();
        labelTextColor.dispose();
        portColor.dispose();
        portFont.dispose();
        edgeColor.dispose();
        edgeFont.dispose();
    }
    
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
     * Clear all internally cached data on painted graphs.
     */
    public void clear() {
        boundsMap.clear();
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
    
    /**
     * Paints the contained layout graph onto the given graphics object.
     * 
     * @param parentNode the parent node that shall be rendered
     * @param graphics the graphics context used to paint
     * @param area the area to fill
     */
    public void render(final KNode parentNode, final GC graphics, final Rectangle area) {
        // activate interpolation
        graphics.setInterpolation(SWT.HIGH);
        
        // determine an overall alpha value for nodes, depending on the maximal node depth
        int maxDepth = Math.max(maxDepth(parentNode), 1);
        int nodeAlpha = 200 / maxDepth + 55;
        
        // render the nodes and ports
        Set<KEdge> edgeSet = new HashSet<KEdge>();
        renderNode(parentNode, graphics, area, baseOffset, edgeSet, nodeAlpha);
        
        // render the edges
        graphics.setForeground(edgeColor);
        graphics.setBackground(edgeColor);
        graphics.setAlpha(255);
        graphics.setFont(edgeFont);
        for (KEdge edge : edgeSet) {
            renderEdge(parentNode, edge, graphics, area, nodeAlpha);
        }
    }
    
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
     * Paints a node for the given dirty area.
     * 
     * @param node the node to paint
     * @param graphics the graphics context used to paint
     * @param area dirty area that needs painting
     * @param offset offset to be added to relative coordinates
     * @param edgeSet set to be filled with edges that are found on the way
     * @param nodeAlpha alpha value for nodes
     */
    private void renderNode(final KNode node, final GC graphics, final Rectangle area,
            final KVector offset, final Set<KEdge> edgeSet, final int nodeAlpha) {
        for (KNode child : node.getChildren()) {
            PaintRectangle rect = boundsMap.get(child);
            if (rect == null) {
                rect = new PaintRectangle(child.getData(KShapeLayout.class), offset, scale);
                boundsMap.put(child, rect);
            }
            KVector childOffset = new KVector(rect.x, rect.y);
            
            // render the child node and its content
            graphics.setForeground(nodeBorderColor);
            graphics.setBackground(nodeFillColor);
            if (!rect.painted && rect.intersects(area)) {
                graphics.setAlpha(nodeAlpha);
                graphics.fillRectangle(rect.x, rect.y, rect.width, rect.height);
                graphics.drawRectangle(rect.x, rect.y, rect.width, rect.height);
                rect.painted = true;
                KVector contentOffset = new KVector(childOffset);
                KInsets insets = child.getData(KShapeLayout.class).getInsets();
                contentOffset.add(insets.getLeft() * scale, insets.getTop() * scale);
                renderNode(child, graphics, area, contentOffset, edgeSet, nodeAlpha);
            }

            graphics.setAlpha(255);
            graphics.setFont(nodeFont);
            // render node labels
            for (KLabel label : child.getLabels()) {
                renderLabel(label, graphics, area, childOffset, nodeAlpha);
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
     * Paints a label for the given dirty area.
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
                // render the border
                graphics.setAlpha(labelAlpha);
                graphics.setForeground(labelBorderColor);
                graphics.drawRectangle(rect.x, rect.y, rect.width, rect.height);
                
                // render the text
                String text = label.getText();
                if (text != null && text.length() > 0) {
                    graphics.setAlpha(255);
                    graphics.setForeground(labelTextColor);
                    
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
        graphics.setForeground(portColor);
        graphics.setBackground(portColor);
        graphics.setFont(portFont);
        graphics.setAlpha(255);
        PaintRectangle rect = boundsMap.get(port);
        if (rect == null) {
            rect = new PaintRectangle(port.getData(KShapeLayout.class), offset, scale);
            boundsMap.put(port, rect);
        }
        if (!rect.painted && rect.intersects(area)) {
            graphics.fillRectangle(rect.x, rect.y, rect.width, rect.height);
            graphics.drawRectangle(rect.x, rect.y, rect.width, rect.height);
            rect.painted = true;
        }
        
        // paint port labels
        KVector portOffset = new KVector(rect.x, rect.y);
        for (KLabel label : port.getLabels()) {
            renderLabel(label, graphics, area, portOffset, labelAlpha);
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
        for (KLabel label : edge.getLabels()) {
            renderLabel(label, graphics, area, offset, labelAlpha);
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

}
