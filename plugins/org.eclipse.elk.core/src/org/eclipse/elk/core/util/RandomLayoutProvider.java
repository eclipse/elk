/*******************************************************************************
 * Copyright (c) 2010, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.util;

import java.util.Random;

import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.klayoutdata.KEdgeLayout;
import org.eclipse.elk.core.klayoutdata.KInsets;
import org.eclipse.elk.core.klayoutdata.KLayoutDataFactory;
import org.eclipse.elk.core.klayoutdata.KPoint;
import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KNode;

/**
 * Layout provider that computes random layouts. Can be useful to demonstrate the difference
 * between a good layout and an extremely bad one.
 *
 * @kieler.rating yellow 2012-08-10 review KI-23 by cds, sgu
 * @kieler.design proposed by msp
 * @author msp
 */
public class RandomLayoutProvider extends AbstractLayoutProvider {
    
    /** the layout provider id. */
    public static final String ID = "org.eclipse.elk.alg.random";

    /** default value for aspect ratio. */
    private static final float DEF_ASPECT_RATIO = 1.6f;
    /** default value for object spacing. */
    private static final float DEF_SPACING = 15.0f;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void layout(final KNode parentNode, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Random Layout", 1);
        if (parentNode.getChildren().isEmpty()) {
            progressMonitor.done();
            return;
        }
        KShapeLayout parentLayout = parentNode.getData(KShapeLayout.class);
        
        // initialize random seed
        Random random;
        Integer randomSeed = parentLayout.getProperty(LayoutOptions.RANDOM_SEED);
        if (randomSeed != null && randomSeed != 0) {
            random = new Random(randomSeed);
        } else {
            random = new Random();
        }
        
        // get aspect ratio
        float aspectRatio = parentLayout.getProperty(LayoutOptions.ASPECT_RATIO);
        if (aspectRatio <= 0) {
            aspectRatio = DEF_ASPECT_RATIO;
        }
        
        // get spacing values
        float spacing = parentLayout.getProperty(LayoutOptions.SPACING);
        if (spacing <= 0) {
            spacing = DEF_SPACING;
        }
        float offset = parentLayout.getProperty(LayoutOptions.BORDER_SPACING);
        if (offset <= 0) {
            offset = DEF_SPACING;
        }
        
        // randomize the layout
        randomize(parentNode, random, aspectRatio, spacing, offset);

        progressMonitor.done();
    }
    
    /**
     * Randomize the given graph.
     * 
     * @param parent the parent node of the graph
     * @param random the random number generator
     * @param aspectRatio desired aspect ratio (must not be 0)
     * @param spacing desired object spacing
     * @param offset offset to the border
     */
    private void randomize(final KNode parent, final Random random, final float aspectRatio,
            final float spacing, final float offset) {
        // determine width and height of the drawing and count the number of edges
        float nodesArea = 0.0f, maxWidth = 0.0f, maxHeight = 0.0f;
        int m = 1;
        for (KNode node : parent.getChildren()) {
            m += node.getOutgoingEdges().size();
            KShapeLayout nodeLayout = node.getData(KShapeLayout.class);
            float width = nodeLayout.getWidth();
            maxWidth = Math.max(maxWidth, width);
            float height = nodeLayout.getHeight();
            maxHeight = Math.max(maxHeight, height);
            nodesArea += width * height;
        }
        int n = parent.getChildren().size();
        // a heuristic formula that determines an area in which nodes are randomly distributed
        float drawArea = nodesArea + 2 * spacing * spacing * m * n;
        float areaSqrt = (float) Math.sqrt(drawArea);
        float drawWidth = Math.max(areaSqrt * aspectRatio, maxWidth);
        float drawHeight = Math.max(areaSqrt / aspectRatio, maxHeight);
        
        // randomize node positions
        for (KNode node : parent.getChildren()) {
            KShapeLayout nodeLayout = node.getData(KShapeLayout.class);
            float x = offset + random.nextFloat() * (drawWidth - nodeLayout.getWidth());
            float y = offset + random.nextFloat() * (drawHeight - nodeLayout.getHeight());
            nodeLayout.setPos(x, y);
        }
        
        // randomize edge positions
        float totalWidth = drawWidth + 2 * offset;
        float totalHeight = drawHeight + 2 * offset;
        for (KNode source : parent.getChildren()) {
            for (KEdge edge : source.getOutgoingEdges()) {
                KNode target = edge.getTarget();
                if (source.getParent() == target.getParent()) {
                    randomize(edge, source, target, random, totalWidth, totalHeight);
                }
            }
        }
        
        KShapeLayout parentLayout = parent.getData(KShapeLayout.class);
        KInsets insets = parentLayout.getInsets();
        totalWidth += insets.getLeft() + insets.getRight();
        totalHeight += insets.getTop() + insets.getBottom();
        ElkUtil.resizeNode(parent, totalWidth, totalHeight, false, true);
    }
    
    /** the maximal number of generated bend points for each edge. */
    private static final int MAX_BENDS = 5;
    /** a factor for the distance between source and target node, determines how much edge
     *  bend point may deviate from the straight line between those nodes. */
    private static final float RAND_FACT = 0.2f;
    
    /**
     * Randomize the given edge by adding bend points in the area between the source and target node.
     * 
     * @param edge an edge
     * @param source the source node
     * @param target the target node
     * @param random the random number generator
     * @param drawWidth the total width of the drawing
     * @param drawHeight the total Height of the drawing
     */
    private void randomize(final KEdge edge, final KNode source, final KNode target,
            final Random random, final float drawWidth, final float drawHeight) {
        KEdgeLayout edgeLayout = edge.getData(KEdgeLayout.class);
        
        // determine position and size of source element
        KShapeLayout sourceLayout = source.getData(KShapeLayout.class);
        float sourceX = sourceLayout.getXpos();
        float sourceY = sourceLayout.getYpos();
        float sourceWidth = sourceLayout.getWidth() / 2;
        float sourceHeight = sourceLayout.getHeight() / 2;
        if (edge.getSourcePort() != null) {
            KShapeLayout portLayout = edge.getSourcePort().getData(KShapeLayout.class);
            sourceWidth = portLayout.getWidth() / 2;
            sourceHeight = portLayout.getHeight() / 2;
            sourceX += portLayout.getXpos();
            sourceY += portLayout.getYpos();
        }
        sourceX += sourceWidth;
        sourceY += sourceHeight;
        
        // determine position and size of target element
        KShapeLayout targetLayout = target.getData(KShapeLayout.class);
        float targetX = targetLayout.getXpos();
        float targetY = targetLayout.getYpos();
        float targetWidth = targetLayout.getWidth() / 2;
        float targetHeight = targetLayout.getHeight() / 2;
        if (edge.getTargetPort() != null) {
            KShapeLayout portLayout = edge.getTargetPort().getData(KShapeLayout.class);
            targetWidth = portLayout.getWidth() / 2;
            targetHeight = portLayout.getHeight() / 2;
            targetX += portLayout.getXpos();
            targetY += portLayout.getYpos();
        }
        targetX += targetWidth;
        targetY += targetHeight;
        
        // set the source point onto the border of the source element
        float sourcePX = targetX;
        if (targetX > sourceX + sourceWidth) {
            sourcePX = sourceX + sourceWidth;
        } else if (targetX < sourceX - sourceWidth) {
            sourcePX = sourceX - sourceWidth;
        }
        float sourcePY = targetY;
        if (targetY > sourceY + sourceHeight) {
            sourcePY = sourceY + sourceHeight;
        } else if (targetY < sourceY - sourceHeight) {
            sourcePY = sourceY - sourceHeight;
        }
        if (sourcePX > sourceX - sourceWidth && sourcePX < sourceX + sourceWidth
                && sourcePY > sourceY - sourceHeight && sourcePY < sourceY + sourceHeight) {
            sourcePX = sourceX + sourceWidth;
        }
        KPoint sourcePoint = edgeLayout.getSourcePoint();
        sourcePoint.setPos(sourcePX, sourcePY);
        
        // set the target point onto the border of the target element
        float targetPX = sourceX;
        if (sourceX > targetX + targetWidth) {
            targetPX = targetX + targetWidth;
        } else if (sourceX < targetX - targetWidth) {
            targetPX = targetX - targetWidth;
        }
        float targetPY = sourceY;
        if (sourceY > targetY + targetHeight) {
            targetPY = targetY + targetHeight;
        } else if (sourceY < targetY - targetHeight) {
            targetPY = targetY - targetHeight;
        }
        if (targetPX > targetX - targetWidth && targetPX < targetX + targetWidth
                && targetPY > targetY - targetHeight && targetPY < targetY + targetHeight) {
            targetPY = targetY + targetHeight;
        }
        KPoint targetPoint = edgeLayout.getTargetPoint();
        targetPoint.setPos(targetPX, targetPY);
        
        // add a random number of bend points
        edgeLayout.getBendPoints().clear();
        int bendsNum = random.nextInt(MAX_BENDS);
        if (source == target) {
            bendsNum++;
        }
        float xdiff = targetPX - sourcePX;
        float ydiff = targetPY - sourcePY;
        float totalDist = (float) Math.sqrt(xdiff * xdiff + ydiff * ydiff);
        float maxRand = totalDist * RAND_FACT;
        float xincr = xdiff / (bendsNum + 1);
        float yincr = ydiff / (bendsNum + 1);
        float x = sourcePX, y = sourcePY;
        for (int i = 0; i < bendsNum; i++) {
            // determine coordinates that deviate from the straight connection by a random amount
            x += xincr;
            y += yincr;
            float randx = x + random.nextFloat() * maxRand - maxRand / 2;
            if (randx < 0) {
                randx = 1;
            } else if (randx > drawWidth) {
                randx = drawWidth - 1;
            }
            float randy = y + random.nextFloat() * maxRand - maxRand / 2;
            if (randy < 0) {
                randy = 1;
            } else if (randy > drawHeight) {
                randy = drawHeight - 1;
            }
            KPoint bendPoint = KLayoutDataFactory.eINSTANCE.createKPoint();
            bendPoint.setX(randx);
            bendPoint.setY(randy);
            edgeLayout.getBendPoints().add(bendPoint);
        }
    }

}
