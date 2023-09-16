/*******************************************************************************
 * Copyright (c) 2023 claas and others.
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
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.math.ElkMargin;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.properties.IProperty;

/**
 * @author claas
 *
 */
public class RelativeXPlacer implements ILayoutPhase<YconstreeLayoutPhases, ElkNode> {
    

    private IElkProgressMonitor pm;
    private double spacingNodeNode = 0.0;
    
    // a constant for moving every Outline to a minimal y-pos
    private double minimalY = -100.0;
    
    /* (non-Javadoc)
     * @see org.eclipse.elk.core.alg.ILayoutProcessor#process(java.lang.Object, org.eclipse.elk.core.util.IElkProgressMonitor)
     */
    @Override
    public void process(ElkNode graph, IElkProgressMonitor progressMonitor) {
        // TODO Auto-generated method stub
        pm = progressMonitor;
        pm.begin("XPlacer", 1);
        
        spacingNodeNode = graph.getProperty(CoreOptions.SPACING_NODE_NODE);
        
        try {
            if (!graph.getChildren().isEmpty()){
                ElkNode parent = graph.getProperty(InternalProperties.ROOT_NODE);
                
                yConsTreeStep(parent);
                // for debugging: print outlines
                //parent.getProperty(InternalProperties.RIGHT_OUTLINE).printFullOutline();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        pm.done();
    }
    
    private void testfunction() {
        OutlineNode outline1 = new OutlineNode(20.0, 0.0, new OutlineNode(0.0, 30.0, new OutlineNode(30.0, 30.0, new OutlineNode(0.0, 40.0, new OutlineNode(-20.0, 0.0, new OutlineNode(0.0, 20.0, null))))));
        OutlineNode outline3 = new OutlineNode(0.0, 0.0, new OutlineNode(0.0, 40.0, null));

        //System.out.println("Hello There");
        double dist = outlineDistance(outline1, outline3);
        //System.out.println(dist);
    }
    
    private double outlineDistance(OutlineNode outline1, OutlineNode outline2) {
        
        OutlineNode changed_outline1 = new OutlineNode(outline1.getRelativeX(), minimalY, outline1.getNext());
        OutlineNode changed_outline2 = new OutlineNode(outline2.getRelativeX(), minimalY, outline2.getNext());
        
        // the return value
        double dist = changed_outline1.getRelativeX() - changed_outline2.getRelativeX();
        
        OutlineNode o1;
        OutlineNode o2;
        double x1;
        double x2;
        double deltaX, deltaY;
        double newdist;
        
        
        // fist run (compare points of o1  with o2)
        o1 = changed_outline1;
        o2 = changed_outline2;
        x1 = o1.getRelativeX();
        x2 = o2.getRelativeX();
        while (o1 != null && !o2.isLast()) {
            if (o2.getNext().getAbsoluteY() > o1.getAbsoluteY()) {
             // now we compare
                deltaX = o2.getNext().getRelativeX();
                deltaY = o2.getNext().getAbsoluteY() - o2.getAbsoluteY();
                newdist = x1 - x2 - ((o1.getAbsoluteY() - o2.getAbsoluteY()) * deltaX) / deltaY;
                
                dist = max(dist, newdist);
                
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
        o1 = changed_outline1;
        o2 = changed_outline2;
        x1 = o1.getRelativeX();
        x2 = o2.getRelativeX();
        while (o2 != null && !o1.isLast()) {
            if (o1.getNext().getAbsoluteY() > o2.getAbsoluteY()) {
                // now we compare
                deltaX = o1.getNext().getRelativeX();
                deltaY = o1.getNext().getAbsoluteY() - o1.getAbsoluteY();
                newdist = x1 - x2 + ((o2.getAbsoluteY() - o1.getAbsoluteY()) * deltaX) / deltaY;
                
                dist = max(dist, newdist);
                
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
     * Just a rudimentary function to 
     * @param d
     * @param e
     * @return
     */
    private double min(double d, double e) {
        // TODO Auto-generated method stub
        return d < e ? d : e;
    }

    /**
     * @param d
     * @param e
     * @return
     */
    private double max(double d, double e) {
        // TODO Auto-generated method stub
        return d > e ? d : e;
    }
    
    /**
     * This is the recursive function that calculates the layout for one node and it's children.
     * @param graph 
     */
    private void yConsTreeStep(ElkNode graph) {
        
        ElkMargin margins = graph.getProperty(CoreOptions.MARGINS);
        
        makeSimpelOutlines(graph);
        
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
                putThemTogether(children.get(0), children.get(i), children.get(i + 1));
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
                moveRoot = (children.get(maxDepthStartPos).getX() + children.get(pos-1).getX() + children.get(pos-1).getWidth()) / 2.0 - graph.getX();
            }
            
            double betterMoveRoot = (children.get(0).getX() + children.get(children.size()-1).getX() + children.get(children.size()-1).getWidth() - graph.getWidth()) / 2.0 - graph.getX();
            double newMoveRoot;
            
            //if better moveRoote is left of moveRoot
            if (betterMoveRoot < moveRoot) {
                OutlineNode Rol;
                double RolX, posX;
                for (int i=0; i < maxDepthStartPos; i++) {
                    for (int j = i+1; j < maxDepthStartPos + 1; j++) {
                        Rol = children.get(i).getProperty(InternalProperties.RIGHT_OUTLINE);
                        RolX = children.get(i).getX() + Rol.getRelativeX();
                        posX = children.get(j).getX() + children.get(j).getWidth() / 2.0;
                        while (Rol != null && Rol.getAbsoluteY() < maxDepth) {
                            // new moveRoot
                            newMoveRoot = posX - graph.getWidth() / 2.0 + (posX - RolX) * ((graph.getX() + graph.getHeight()) - maxDepth) / (maxDepth - Rol.getAbsoluteY());
                            betterMoveRoot = max(betterMoveRoot, newMoveRoot);
                            
                            // update Rol and RolX
                            Rol = Rol.getNext();
                            if (Rol != null) {
                                RolX += Rol.getRelativeX();
                            }
                        }
                    }
                }
                moveRoot = betterMoveRoot;
            }
            
            //if better moveRoote is right of moveRoot
            if (betterMoveRoot > moveRoot) {
                OutlineNode Lol;
                double LolX, posX;
                for (int i=pos; i < children.size(); i++) {
                    for (int j = pos-1; j < i; j++) {
                        Lol = children.get(i).getProperty(InternalProperties.LEFT_OUTLINE);
                        LolX = children.get(i).getX() + Lol.getRelativeX();
                        posX = children.get(j).getX() + children.get(j).getWidth() / 2.0;
                        while (Lol != null && Lol.getAbsoluteY() < maxDepth) {
                            // new moveRoot
                            newMoveRoot = posX - graph.getWidth() / 2.0 + (posX - LolX) * ((graph.getX() + graph.getHeight()) - maxDepth) / (maxDepth - Lol.getAbsoluteY());
                            betterMoveRoot = min(betterMoveRoot, newMoveRoot);

                            // update Rol and RolX
                            Lol = Lol.getNext();
                            if (Lol != null) {
                                LolX += Lol.getRelativeX();
                            }
                        }
                    }
                }
                moveRoot = betterMoveRoot;
            }
            
            for(ElkNode child: children) {
                child.setX(child.getX() - moveRoot);
            }
            
            /**
            //-------------------------------------------------------------------------
         // now we need to move the root to the middle of the nodes.
            // we calculate the point of the child with the lowest y-position to avoid overlapping.
            // if there is more than one lowest child, the root will be positioned in the middle of them.
            int lowestChildPos = 0;
            double lowestChildY = 0.0;
            double lowestChildStart = 0.0; 
            //**
            while (lowestChildPos < children.size() && children.get(lowestChildPos).getY() >= lowestChildY) {
                if (children.get(lowestChildPos).getY() > lowestChildY) {
                    lowestChildStart = children.get(lowestChildPos).getX();
                    lowestChildY = children.get(lowestChildPos).getY();
                }
                lowestChildPos++;
            }
            
            double moveRoot = 0.0;
            if (lowestChildPos > 0) {
                moveRoot = (children.get(lowestChildPos - 1).getX() + children.get(lowestChildPos - 1).getWidth() + lowestChildStart - graph.getWidth()) / 2 - graph.getX();
            }
            
            // move children
            for(ElkNode child: children) {
                child.setX(child.getX() - moveRoot);
            }
            //----------------------------------------------------------------------------
            **/
            
            double newX;
            // lol update
            newX = children.get(0).getX() + children.get(0).getProperty(InternalProperties.LEFT_OUTLINE).getRelativeX() - graph.getProperty(InternalProperties.LEFT_OUTLINE).getRelativeX();
            graph.getProperty(InternalProperties.LEFT_OUTLINE).getNext().setNext(new OutlineNode(newX, children.get(0).getProperty(InternalProperties.LEFT_OUTLINE).getAbsoluteY(), children.get(0).getProperty(InternalProperties.LEFT_OUTLINE).getNext()));
            // rol update
            newX = children.get(children.size()-1).getX() + children.get(children.size()-1).getProperty(InternalProperties.RIGHT_OUTLINE).getRelativeX() - graph.getProperty(InternalProperties.RIGHT_OUTLINE).getRelativeX();
            graph.getProperty(InternalProperties.RIGHT_OUTLINE).getNext().setNext(new OutlineNode(newX, children.get(children.size()-1).getProperty(InternalProperties.RIGHT_OUTLINE).getAbsoluteY(), children.get(children.size()-1).getProperty(InternalProperties.RIGHT_OUTLINE).getNext()));
            
            // update outlineMaxY
            // update min und max for x and y
            for (ElkNode child: children) {
                graph.setProperty(InternalProperties.OUTLINE_MAX_DEPTH, max(graph.getProperty(InternalProperties.OUTLINE_MAX_DEPTH), child.getProperty(InternalProperties.OUTLINE_MAX_DEPTH)));
                graph.setProperty(InternalProperties.MIN_X, min(graph.getProperty(InternalProperties.MIN_X), child.getX() + child.getProperty(InternalProperties.MIN_X)));
                graph.setProperty(InternalProperties.MAX_X, max(graph.getProperty(InternalProperties.MAX_X), child.getX() + child.getProperty(InternalProperties.MAX_X)));
            }
            graph.setProperty(InternalProperties.MAX_Y, graph.getProperty(InternalProperties.OUTLINE_MAX_DEPTH));
            
        }
    }
    
    /**
     * sorts the subTrees in a half circle, uses mergesort.
     * @param graph
     */
    private void sortSubTrees(List<ElkNode> children) {
        
        // fist, we sort the SubTrees by the Y-coordinate of their root.
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
    
    
    private void makeSimpelOutlines(ElkNode graph) {
        ElkMargin margins = graph.getProperty(CoreOptions.MARGINS);
        
        // set the properties for left and right outlines
        graph.setProperty(InternalProperties.LEFT_OUTLINE, new OutlineNode((-margins.left), graph.getY() - margins.top, new OutlineNode(0.0, graph.getY() + graph.getHeight() + margins.bottom, null)));
        graph.setProperty(InternalProperties.RIGHT_OUTLINE, new OutlineNode(graph.getWidth() + margins.right, graph.getY() - margins.top, new OutlineNode(0.0, graph.getY() + graph.getHeight() + margins.bottom, null)));
        
        // set min and max values
        graph.setProperty(InternalProperties.MIN_X, graph.getX() - margins.left);
        graph.setProperty(InternalProperties.MAX_X, graph.getX() + margins.right + graph.getWidth());
        graph.setProperty(InternalProperties.MIN_Y, graph.getY() - margins.top);
        graph.setProperty(InternalProperties.MAX_Y, graph.getY() + margins.bottom + graph.getHeight());
        graph.setProperty(InternalProperties.OUTLINE_MAX_DEPTH, graph.getProperty(InternalProperties.LEFT_OUTLINE).getNext().getAbsoluteY());
        
    }
    
    
    private void putThemTogether(ElkNode leftSubtree, ElkNode a, ElkNode b) {
        
        double deltaX, deltaY, change;
        
        // calculate distance between the two parts
        double dist = outlineDistance(a.getProperty(InternalProperties.RIGHT_OUTLINE), b.getProperty(InternalProperties.LEFT_OUTLINE));
        b.setX(a.getX() + dist);
        
        // enhance the left outline
        if (leftSubtree.getProperty(InternalProperties.OUTLINE_MAX_DEPTH) < b.getProperty(InternalProperties.OUTLINE_MAX_DEPTH)) {
            OutlineNode lastL = leftSubtree.getProperty(InternalProperties.LEFT_OUTLINE);
            double LabsX = lastL.getRelativeX() + leftSubtree.getX();
            
            // move to the end of leftSubtree
            while (!lastL.isLast()) {
                lastL = lastL.getNext();
                LabsX += lastL.getRelativeX();
            }
            // find fiting position in the lol of b
            OutlineNode bItterator = new OutlineNode(b.getProperty(InternalProperties.LEFT_OUTLINE).getRelativeX(), minimalY, b.getProperty(InternalProperties.LEFT_OUTLINE).getNext());
            double BabsX = bItterator.getRelativeX() + b.getX();
            while (bItterator.getNext().getAbsoluteY() <= lastL.getAbsoluteY()) {
                bItterator = bItterator.getNext();
                BabsX += bItterator.getRelativeX();
            }
            // now we calculate the change
            deltaX = bItterator.getNext().getRelativeX();
            deltaY = bItterator.getNext().getAbsoluteY() - bItterator.getAbsoluteY();
            change = ((lastL.getAbsoluteY() - bItterator.getAbsoluteY()) * deltaX) / deltaY;
            
            // now we calculate the new points
            double newX = -LabsX + BabsX + change;
            OutlineNode newNext = new OutlineNode(bItterator.getNext().getRelativeX() - change, bItterator.getNext().getAbsoluteY(), bItterator.getNext().getNext());
            lastL.setNext(new OutlineNode(newX, lastL.getAbsoluteY(), newNext));
            // now update outline_max_depth
            leftSubtree.setProperty(InternalProperties.OUTLINE_MAX_DEPTH, b.getProperty(InternalProperties.OUTLINE_MAX_DEPTH));
        }
        
        // enhance the right outline
        if (b.getProperty(InternalProperties.OUTLINE_MAX_DEPTH) < a.getProperty(InternalProperties.OUTLINE_MAX_DEPTH)) {
            OutlineNode lastB = b.getProperty(InternalProperties.RIGHT_OUTLINE);
            double BabsX = lastB.getRelativeX() + b.getX();
            
            // move to the end of b
            while (!lastB.isLast()) {
                lastB = lastB.getNext();
                BabsX += lastB.getRelativeX();
            }
            // find fitting position in the rol of a
            OutlineNode aItterator = new OutlineNode(a.getProperty(InternalProperties.RIGHT_OUTLINE).getRelativeX(), minimalY, a.getProperty(InternalProperties.RIGHT_OUTLINE).getNext());
            double AabsX = aItterator.getRelativeX() + a.getX();
            while (aItterator.getNext().getAbsoluteY() <= lastB.getAbsoluteY()) {
                aItterator = aItterator.getNext();
                AabsX += aItterator.getRelativeX();
            }
            // now we calculate the change
            deltaX = aItterator.getNext().getRelativeX();
            deltaY = aItterator.getNext().getAbsoluteY() - aItterator.getAbsoluteY();
            change = ((lastB.getAbsoluteY() - aItterator.getAbsoluteY()) * deltaX) / deltaY;
            
            // now we calculate the new points
            double newX = AabsX - BabsX + change;
            OutlineNode newNext = new OutlineNode(aItterator.getNext().getRelativeX() - change, aItterator.getNext().getAbsoluteY(), aItterator.getNext().getNext());
            lastB.setNext(new OutlineNode(newX, lastB.getAbsoluteY(), newNext));
            // now update outline_max_depth
            b.setProperty(InternalProperties.OUTLINE_MAX_DEPTH, a.getProperty(InternalProperties.OUTLINE_MAX_DEPTH));
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.elk.core.alg.ILayoutPhase#getLayoutProcessorConfiguration(java.lang.Object)
     */
    @Override
    public LayoutProcessorConfiguration<YconstreeLayoutPhases, ElkNode> getLayoutProcessorConfiguration(ElkNode graph) {
        // TODO Auto-generated method stub
        return null;
    }

}
