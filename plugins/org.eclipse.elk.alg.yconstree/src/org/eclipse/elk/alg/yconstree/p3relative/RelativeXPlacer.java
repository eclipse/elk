/*******************************************************************************
 * Copyright (c) 2023 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.yconstree.p3relative;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.elk.alg.yconstree.InternalProperties;
import org.eclipse.elk.alg.yconstree.YconstreeLayoutPhases;
import org.eclipse.elk.alg.yconstree.options.YconstreeOptions;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.math.ElkMargin;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

/**
 * Node placer that positions nodes horizontally using coordinates that are relative to parent nodes.
 *
 */
public class RelativeXPlacer implements ILayoutPhase<YconstreeLayoutPhases, ElkNode> {
    

    private IElkProgressMonitor myProgressMonitor;
    private double spacingNodeNode;

    // a constant for moving every Outline to a minimal y-pos
    private final static double MINIMAL_Y = -100.0;
    
    @Override
    public void process(final ElkNode graph, final IElkProgressMonitor progressMonitor) {
        myProgressMonitor = progressMonitor;
        myProgressMonitor.begin("XPlacer", 1);
        
        spacingNodeNode = graph.getProperty(CoreOptions.SPACING_NODE_NODE);
        
        if (!graph.getChildren().isEmpty()) {
            ElkNode parent = graph.getProperty(InternalProperties.ROOT_NODE);
            
            switch (graph.getProperty(YconstreeOptions.LAYOUT_STRATEGY)) {
                case STRAIGHT:
                    yConsTreeStep(parent);
                    break;
                case BEND:
                    alternativeYConsTreeStep(parent);
                    break;
                default:
                    break;   
            }
        }

        myProgressMonitor.done();
    }
    
    private double outlineDistance(final OutlineNode outline1, final OutlineNode outline2) {
        
        OutlineNode changedOutline1 = new OutlineNode(outline1.getRelativeX(), MINIMAL_Y, 
                new OutlineNode(0.0, outline1.getAbsoluteY(), outline1.getNext()));
        OutlineNode changedOutline2 = new OutlineNode(outline2.getRelativeX(), MINIMAL_Y, 
                new OutlineNode(0.0, outline2.getAbsoluteY(), outline2.getNext()));
        
        // the return value
        double dist = changedOutline1.getRelativeX() - changedOutline2.getRelativeX();
        
        OutlineNode o1;
        OutlineNode o2;
        double x1;
        double x2;
        double deltaX, deltaY;
        double newdist;
        
        
        // first run (compare points of o1  with o2)
        o1 = changedOutline1;
        o2 = changedOutline2;
        x1 = o1.getRelativeX();
        x2 = o2.getRelativeX();
        while (o1 != null && !o2.isLast()) {
            if (o2.getNext().getAbsoluteY() > o1.getAbsoluteY()) {
             // now we compare
                deltaX = o2.getNext().getRelativeX();
                deltaY = o2.getNext().getAbsoluteY() - o2.getAbsoluteY();
                newdist = x1 - x2 - ((o1.getAbsoluteY() - o2.getAbsoluteY()) * deltaX) / deltaY;
                
                dist = Math.max(dist, newdist);
                
                // now change o1
                o1 = o1.getNext();
                if (o1 != null) {
                    x1 += o1.getRelativeX();
                }
            } else {
                o2 = o2.getNext();
                x2 += o2.getRelativeX();
            }
        }
        
        // second run (compare points of o2  with o1)
        o1 = changedOutline1;
        o2 = changedOutline2;
        x1 = o1.getRelativeX();
        x2 = o2.getRelativeX();
        while (o2 != null && !o1.isLast()) {
            if (o1.getNext().getAbsoluteY() > o2.getAbsoluteY()) {
                // now we compare
                deltaX = o1.getNext().getRelativeX();
                deltaY = o1.getNext().getAbsoluteY() - o1.getAbsoluteY();
                newdist = x1 - x2 + ((o2.getAbsoluteY() - o1.getAbsoluteY()) * deltaX) / deltaY;
                
                dist = Math.max(dist, newdist);
                
                // now change o2
                o2 = o2.getNext();
                if (o2 != null) {
                    x2 += o2.getRelativeX();
                }
            } else {
                o1 = o1.getNext();
                x1 += o1.getRelativeX();
            }
      
        }
        
        return dist;
    }
    
    /**
     * This is the recursive function that calculates the layout for one node and it's children.
     * @param graph 
     */
    private void yConsTreeStep(final ElkNode graph) {
        
        makeSimpleOutlines(graph);
        
        if (!graph.getOutgoingEdges().isEmpty()) {
            
            // get all children
            List<ElkNode> children = new ArrayList<>();
            for (int i = 0; i < graph.getOutgoingEdges().size(); i++) {
                ElkNode child = (ElkNode) graph.getOutgoingEdges().get(i).getTargets().get(0);
                yConsTreeStep(child);
                children.add(child);
            }
            // now the children of this node get sorted to form a semi-circle.
            sortSubTrees(children);
            
            // now the children get stuffed together, using the outlines.
            for (int i = 0; i < children.size() - 1; i++) {
                bundleChildren(children.get(0), children.get(i), children.get(i + 1));
            }
            
            // now we need to move the root to the middle of the nodes.
            // we calculate the point of the child with the lowest y-position to avoid overlapping.
            // if there is more than one lowest child, the root will be positioned in the middle of them.
            int pos = 0;
            double maxDepth = 0.0;
            int maxDepthStartPos = 0;
            while (pos < children.size() && children.get(pos).getY() >= maxDepth) {
                if (children.get(pos).getY() > maxDepth) {
                    maxDepthStartPos = pos;
                    maxDepth = children.get(pos).getY();
                }
                pos += 1;
            }
            double moveRoot = 0.0;
            if (pos > 0) {
                moveRoot = (children.get(maxDepthStartPos).getX() + children.get(pos - 1).getX() 
                        + children.get(pos - 1).getWidth()) / 2.0 - graph.getX();
            }
            
            double betterMoveRoot = (children.get(0).getX() + children.get(children.size() - 1).getX() 
                    + children.get(children.size() - 1).getWidth() - graph.getWidth()) / 2.0 - graph.getX();
            double newMoveRoot;

            if (betterMoveRoot < moveRoot) {
                OutlineNode rol;
                double rolX, posX;
                for (int i = 0; i < maxDepthStartPos; i++) {
                    for (int j = i + 1; j < maxDepthStartPos + 1; j++) {
                        rol = children.get(i).getProperty(InternalProperties.RIGHT_OUTLINE);
                        rolX = children.get(i).getX() + rol.getRelativeX();
                        posX = children.get(j).getX() + children.get(j).getWidth() / 2.0;
                        while (rol != null && rol.getAbsoluteY() < maxDepth) {
                            // new moveRoot
                            newMoveRoot = posX - graph.getWidth() / 2.0 + (posX - rolX) * ((graph.getY() 
                                    + graph.getHeight()) - maxDepth) / (maxDepth - rol.getAbsoluteY());
                            betterMoveRoot = Math.max(betterMoveRoot, newMoveRoot);
                            
                            // update Rol and RolX
                            rol = rol.getNext();
                            if (rol != null) {
                                rolX += rol.getRelativeX();
                            }
                        }
                    }
                }
                moveRoot = betterMoveRoot;
            }

            if (betterMoveRoot > moveRoot) {
                OutlineNode lol;
                double lolX, posX;
                for (int i = pos; i < children.size(); i++) {
                    for (int j = pos - 1; j < i; j++) {
                        lol = children.get(i).getProperty(InternalProperties.LEFT_OUTLINE);
                        lolX = children.get(i).getX() + lol.getRelativeX();
                        posX = children.get(j).getX() + children.get(j).getWidth() / 2.0;
                        while (lol != null && lol.getAbsoluteY() < maxDepth) {
                            // new moveRoot
                            newMoveRoot = posX - graph.getWidth() / 2.0 + (posX - lolX) * ((graph.getY() 
                                    + graph.getHeight()) - maxDepth) / (maxDepth - lol.getAbsoluteY());
                            betterMoveRoot = Math.min(betterMoveRoot, newMoveRoot);

                            // update Rol and RolX
                            lol = lol.getNext();
                            if (lol != null) {
                                lolX += lol.getRelativeX();
                            }
                        }
                    }
                }
                moveRoot = betterMoveRoot;
            }
            
            for (ElkNode child: children) {
                child.setX(child.getX() - moveRoot);
            }
            
            
            double newX;
            // lol update
            newX = children.get(0).getX() + children.get(0).getProperty(InternalProperties.LEFT_OUTLINE).getRelativeX() 
                    - graph.getProperty(InternalProperties.LEFT_OUTLINE).getRelativeX();
            graph.getProperty(InternalProperties.LEFT_OUTLINE).getNext().getNext().getNext().setNext(
                    new OutlineNode(newX, children.get(0).getProperty(InternalProperties.LEFT_OUTLINE).getAbsoluteY(), 
                            children.get(0).getProperty(InternalProperties.LEFT_OUTLINE).getNext()));
            // rol update
            newX = children.get(children.size() - 1).getX() 
                    + children.get(children.size() - 1).getProperty(InternalProperties.RIGHT_OUTLINE).getRelativeX() 
                    - graph.getProperty(InternalProperties.RIGHT_OUTLINE).getRelativeX();
            graph.getProperty(InternalProperties.RIGHT_OUTLINE).getNext().getNext().getNext().setNext(
                    new OutlineNode(newX, children.get(children.size() - 1).getProperty(
                            InternalProperties.RIGHT_OUTLINE).getAbsoluteY(), children.get(children.size() - 1).
                            getProperty(InternalProperties.RIGHT_OUTLINE).getNext()));
            
            // update outlineMaxY
            // update min und max for x and y
            for (ElkNode child: children) {
                graph.setProperty(InternalProperties.OUTLINE_MAX_DEPTH, Math.max(graph.getProperty(InternalProperties.
                        OUTLINE_MAX_DEPTH), child.getProperty(InternalProperties.OUTLINE_MAX_DEPTH)));
                graph.setProperty(InternalProperties.MIN_X, Math.min(graph.getProperty(InternalProperties.MIN_X), 
                        child.getX() + child.getProperty(InternalProperties.MIN_X)));
                graph.setProperty(InternalProperties.MAX_X, Math.max(graph.getProperty(InternalProperties.MAX_X), 
                        child.getX() + child.getProperty(InternalProperties.MAX_X)));
            }
            graph.setProperty(InternalProperties.MAX_Y, graph.getProperty(InternalProperties.OUTLINE_MAX_DEPTH));
            
        }
    }

    private void alternativeYConsTreeStep(final ElkNode graph) {
        
        makeSimpleOutlines(graph);
        
        if (!graph.getOutgoingEdges().isEmpty()) {
            
            // get all children
            List<ElkNode> children = new ArrayList<>();
            for (int i = 0; i < graph.getOutgoingEdges().size(); i++) {
                ElkNode child = (ElkNode) graph.getOutgoingEdges().get(i).getTargets().get(0);
                alternativeYConsTreeStep(child);
                children.add(child);
            }
            
            int cs = children.size();
            
            
            // now the children get stuffed together, using the outlines.
            for (int i = 0; i < children.size() - 1; i++) {
                bundleChildren(children.get(0), children.get(i), children.get(i + 1));
            }
            
            double moveRoot = (children.get(0).getX() + children.get(0).getWidth() / 2.0 
                    + children.get(children.size() - 1).getX() + children.get(children.size() - 1).getWidth() / 2.0 
                    - graph.getWidth()) / 2.0 - graph.getX();
            
            for (ElkNode child: children) {
                child.setX(child.getX() - moveRoot);
                child.setProperty(InternalProperties.EDGE_BEND_HEIGHT, 
                        child.getProperty(InternalProperties.LEFT_OUTLINE).getAbsoluteY());
            }
            
            // set bendHeights for children right of the parent
            int i = 0;
            while (i < cs - 1 && children.get(i).getX() + children.get(i).getWidth() 
                    + children.get(i).getProperty(CoreOptions.MARGINS).right - graph.getWidth() / 2.0 <= 0.0) {
                i++;
            }
            
            double globalBendHeight = children.get(i).getProperty(InternalProperties.EDGE_BEND_HEIGHT);
            for (int a = 0; a < cs; a++) {
                if (globalBendHeight < children.get(a).getProperty(InternalProperties.EDGE_BEND_HEIGHT)) {
                    children.get(a).setProperty(InternalProperties.EDGE_BEND_HEIGHT, globalBendHeight);
                } else {
                    globalBendHeight = children.get(a).getProperty(InternalProperties.EDGE_BEND_HEIGHT);
                }
            }
            
            // set bendHeights for children left of the parent
            i = cs - 1;
            while (i > 0 && children.get(i).getX() - children.get(i).getProperty(CoreOptions.MARGINS).left 
                    - graph.getWidth() / 2.0 >= 0.0) {
                i--;
            }
            
            if (i < cs) {
                for (int a = i; a >= 0; a--) {
                    
                    if (globalBendHeight < children.get(a).getProperty(InternalProperties.EDGE_BEND_HEIGHT)) {
                        children.get(a).setProperty(InternalProperties.EDGE_BEND_HEIGHT, globalBendHeight);
                    } else {
                        globalBendHeight = children.get(a).getProperty(InternalProperties.EDGE_BEND_HEIGHT);
                    }
                }
            }
            
            double newX;
            OutlineNode newOutlinepart;
            // lol update
            newX = children.get(0).getX() + children.get(0).getProperty(InternalProperties.LEFT_OUTLINE).getRelativeX() 
                    - graph.getProperty(InternalProperties.LEFT_OUTLINE).getRelativeX();
            newOutlinepart = new OutlineNode(0.0, children.get(0).getProperty(InternalProperties.LEFT_OUTLINE)
                    .getAbsoluteY(), children.get(0).getProperty(InternalProperties.LEFT_OUTLINE).getNext());
            graph.getProperty(InternalProperties.LEFT_OUTLINE).getNext().getNext().getNext()
                    .setNext(new OutlineNode(newX, children.get(0).getProperty(InternalProperties.EDGE_BEND_HEIGHT), 
                            newOutlinepart));
            
            newX = children.get(cs - 1).getX() + children.get(cs - 1).getProperty(InternalProperties.RIGHT_OUTLINE)
                    .getRelativeX() - graph.getProperty(InternalProperties.RIGHT_OUTLINE).getRelativeX();
            newOutlinepart = new OutlineNode(0.0, children.get(cs - 1).getProperty(InternalProperties.RIGHT_OUTLINE)
                    .getAbsoluteY(), children.get(cs - 1).getProperty(InternalProperties.RIGHT_OUTLINE).getNext());
            graph.getProperty(InternalProperties.RIGHT_OUTLINE).getNext().getNext().getNext()
                    .setNext(new OutlineNode(newX, children.get(cs - 1).getProperty(InternalProperties
                            .EDGE_BEND_HEIGHT), newOutlinepart));
            
            // update outlineMaxY
            // update min und max for x and y
            for (ElkNode child: children) {
                graph.setProperty(InternalProperties.OUTLINE_MAX_DEPTH, Math.max(graph.getProperty(InternalProperties
                        .OUTLINE_MAX_DEPTH), child.getProperty(InternalProperties.OUTLINE_MAX_DEPTH)));
                graph.setProperty(InternalProperties.MIN_X, Math.min(graph.getProperty(InternalProperties.MIN_X), 
                        child.getX() + child.getProperty(InternalProperties.MIN_X)));
                graph.setProperty(InternalProperties.MAX_X, Math.max(graph.getProperty(InternalProperties.MAX_X), 
                        child.getX() + child.getProperty(InternalProperties.MAX_X)));
            }
            graph.setProperty(InternalProperties.MAX_Y, graph.getProperty(InternalProperties.OUTLINE_MAX_DEPTH));
            
        }
        
    }
    
    /**
     * sorts the subTrees in a half circle, uses mergesort.
     * @param graph
     */
    private void sortSubTrees(final List<ElkNode> children) {
        
        // first, we sort the SubTrees by the Y-coordinate of their root.
        Collections.sort(children, new NodeComparator());
        
        // now we need to put them in a V-shape 
        // the deepest element gets ignored in the calculation of the widths.
        List<ElkNode> a = new ArrayList<>();
        a.add(children.get(children.size() - 1));
        List<ElkNode> b = new ArrayList<>();
        double widthA = 0.0, widthB = 0.0;
        for (int i = 1; i < children.size(); i++) {
            if (widthA <= widthB) {
                a.add(children.get(children.size() - 1 - i));
                widthA += children.get(children.size() - 1 - i).getWidth();
            } else {
                b.add(children.get(children.size() - 1 - i));
                widthB += children.get(children.size() - 1 - i).getWidth();
            }
        }
        Collections.reverse(a);
        a.addAll(b);
        for (int i = 0; i < children.size(); i++) {
            children.set(i, a.get(i));
        }
        
    }
    
    
    private void makeSimpleOutlines(final ElkNode graph) {
        ElkMargin margins = graph.getProperty(CoreOptions.MARGINS);
        
        // set the properties for left and right outlines
        OutlineNode endpart;
        endpart = new OutlineNode(0.0, graph.getY() + graph.getHeight() + margins.bottom + spacingNodeNode / 2, 
                new OutlineNode(graph.getWidth() / 2.0, 
                        graph.getY() + graph.getHeight() + margins.bottom + spacingNodeNode / 2, null));
        
        graph.setProperty(InternalProperties.LEFT_OUTLINE, new OutlineNode((-margins.left - spacingNodeNode / 2) 
                + graph.getWidth() / 2.0, graph.getY() - margins.top - spacingNodeNode / 2, 
                new OutlineNode(-graph.getWidth() / 2.0, graph.getY() - margins.top, endpart)));
        
        endpart = new OutlineNode(0.0, graph.getY() + graph.getHeight() + margins.bottom,
                new OutlineNode(-graph.getWidth() / 2.0, 
                        graph.getY() + graph.getHeight() + margins.bottom + spacingNodeNode / 2, null));
        
        graph.setProperty(InternalProperties.RIGHT_OUTLINE, new OutlineNode(graph.getWidth() / 2.0 
                + margins.right + spacingNodeNode / 2, graph.getY() - margins.top,
                new OutlineNode(graph.getWidth() / 2.0, graph.getY() - margins.top - spacingNodeNode / 2, endpart)));
        
        // TODO double check whether this is the best way and place to calculate this
        // set min and max values
        graph.setProperty(InternalProperties.MIN_X, graph.getX() - margins.left);
        graph.setProperty(InternalProperties.MAX_X, 
                graph.getX() + margins.right + graph.getWidth());
        graph.setProperty(InternalProperties.MIN_Y, graph.getY() - margins.top);
        graph.setProperty(InternalProperties.MAX_Y,
                graph.getY() + margins.bottom + graph.getHeight());
        graph.setProperty(InternalProperties.OUTLINE_MAX_DEPTH, graph.getProperty(InternalProperties.LEFT_OUTLINE)
                .getNext().getNext().getAbsoluteY());
        
    }
    
    
    private void bundleChildren(final ElkNode leftSubtree, final ElkNode a, final ElkNode b) {
        
        double deltaX, deltaY, change;
        
        // calculate distance between the two parts
        double dist = outlineDistance(a.getProperty(InternalProperties.RIGHT_OUTLINE), 
                b.getProperty(InternalProperties.LEFT_OUTLINE));
        b.setX(a.getX() + dist);
        
        // enhance the left outline
        if (leftSubtree.getProperty(InternalProperties.OUTLINE_MAX_DEPTH) 
                < b.getProperty(InternalProperties.OUTLINE_MAX_DEPTH)) {
            OutlineNode lastL = leftSubtree.getProperty(InternalProperties.LEFT_OUTLINE);
            double lAbsX = lastL.getRelativeX() + leftSubtree.getX();
            
            // move to the end of leftSubtree
            while (!lastL.isLast()) {
                lastL = lastL.getNext();
                lAbsX += lastL.getRelativeX();
            }
            // find fiting position in the lol of b
            OutlineNode bItterator = new OutlineNode(b.getProperty(InternalProperties.LEFT_OUTLINE).getRelativeX(),
                    MINIMAL_Y, b.getProperty(InternalProperties.LEFT_OUTLINE).getNext());
            double rAbsX = bItterator.getRelativeX() + b.getX();
            while (bItterator.getNext().getAbsoluteY() <= lastL.getAbsoluteY()) {
                bItterator = bItterator.getNext();
                rAbsX += bItterator.getRelativeX();
            }
            // now we calculate the change
            deltaX = bItterator.getNext().getRelativeX();
            deltaY = bItterator.getNext().getAbsoluteY() - bItterator.getAbsoluteY();
            change = ((lastL.getAbsoluteY() - bItterator.getAbsoluteY()) * deltaX) / deltaY;
            
            // now we calculate the new points
            double newX = -lAbsX + rAbsX + change;
            OutlineNode newNext = new OutlineNode(bItterator.getNext().getRelativeX() - change, bItterator.getNext()
                    .getAbsoluteY(), bItterator.getNext().getNext());
            lastL.setNext(new OutlineNode(newX, lastL.getAbsoluteY(), newNext));
            // now update outline_max_depth
            leftSubtree.setProperty(InternalProperties.OUTLINE_MAX_DEPTH, 
                    b.getProperty(InternalProperties.OUTLINE_MAX_DEPTH));
        }
        
        // enhance the right outline
        if (b.getProperty(InternalProperties.OUTLINE_MAX_DEPTH) < a.getProperty(InternalProperties.OUTLINE_MAX_DEPTH)) {
            OutlineNode lastB = b.getProperty(InternalProperties.RIGHT_OUTLINE);
            double rAbsX = lastB.getRelativeX() + b.getX();
            
            // move to the end of b
            while (!lastB.isLast()) {
                lastB = lastB.getNext();
                rAbsX += lastB.getRelativeX();
            }
            // find fitting position in the rol of a
            OutlineNode aItterator = new OutlineNode(a.getProperty(InternalProperties.RIGHT_OUTLINE).getRelativeX(), 
                    MINIMAL_Y, a.getProperty(InternalProperties.RIGHT_OUTLINE).getNext());
            double aAbsX = aItterator.getRelativeX() + a.getX();
            while (aItterator.getNext().getAbsoluteY() <= lastB.getAbsoluteY()) {
                aItterator = aItterator.getNext();
                aAbsX += aItterator.getRelativeX();
            }
            // now we calculate the change
            deltaX = aItterator.getNext().getRelativeX();
            deltaY = aItterator.getNext().getAbsoluteY() - aItterator.getAbsoluteY();
            change = ((lastB.getAbsoluteY() - aItterator.getAbsoluteY()) * deltaX) / deltaY;
            
            // now we calculate the new points
            double newX = aAbsX - rAbsX + change;
            OutlineNode newNext = new OutlineNode(aItterator.getNext().getRelativeX() - change, 
                    aItterator.getNext().getAbsoluteY(), aItterator.getNext().getNext());
            lastB.setNext(new OutlineNode(newX, lastB.getAbsoluteY(), newNext));
            // now update outline_max_depth
            b.setProperty(InternalProperties.OUTLINE_MAX_DEPTH, a.getProperty(InternalProperties.OUTLINE_MAX_DEPTH));
        }
    }

    @Override
    public LayoutProcessorConfiguration<YconstreeLayoutPhases, ElkNode> 
    getLayoutProcessorConfiguration(final ElkNode graph) {
        return null;
    }

}
