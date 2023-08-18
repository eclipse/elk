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
                ElkNode parent = graph.getChildren().get(0);
                
                yConsTreeStep(parent);
                // for debugging: print outlines
                parent.getProperty(InternalProperties.RIGHT_OUTLINE).printFullOutline();
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

        System.out.println("Hello There");
        double dist = outlineDistance(outline1, 0.0, outline3, 0.0, true);
        System.out.println(dist);
    }
    
    private double outlineDistance(OutlineNode outline1, double start1, OutlineNode outline2, double start2, boolean UseMaxDisp) {
        OutlineNode o1 = outline1;
        OutlineNode o2 = outline2;
        double y1 = o1.getRelativeY() + start1;
        double y2 = o2.getRelativeY() + start2;
        double x1 = o1.getRelativeX();
        double x2 = o2.getRelativeX();
        double maxO1 = o1.getRelativeX();
        double minO2 = o2.getRelativeX();
        double dist = o1.getRelativeX() - o2.getRelativeX();
        while(!o1.isLast() && !o2.isLast()) {
            if (y1 <= y2 && y1 + o1.getNext().getRelativeY() > y2) {
                dist = max(dist, (x1 + (y2 - y1) * o1.getNext().getRelativeX() / o1.getNext().getRelativeY()) - x2);
            }
            if (y2 <= y1 && y2 + o2.getNext().getRelativeY() > y1) {
                dist = max(dist, x1 - (x2 + (y1 - y2) * o2.getNext().getRelativeX() / o2.getNext().getRelativeY()));
            }
            if (y2 + o2.getNext().getRelativeY() < y1 + o1.getNext().getRelativeY()) {
                y2 += o2.getNext().getRelativeY();
                x2 += o2.getNext().getRelativeX();
                o2 = o2.getNext();
                minO2 = min(minO2, x2);
            } else {
                y1 += o1.getNext().getRelativeY();
                x1 += o1.getNext().getRelativeX();
                o1 = o1.getNext();
                maxO1 = max(maxO1, x1);
            }
        }
        if (!o1.isLast()){
            if (y1 <= y2 && y1 + o1.getNext().getRelativeY() > y2) {
                dist = max(dist, (x1 + (y2 - y1) * o1.getNext().getRelativeX() / o1.getNext().getRelativeY()) - x2);
            } else {
                if (UseMaxDisp){
                    dist = max(dist,-minO2 + o1.getRelativeX());
                }    
            }
        } 
        if (!o2.isLast()) {
            if (y2 <= y1) {
                dist = max(dist, x1 - (x2 + (y1 - y2) * o2.getNext().getRelativeX() / o2.getNext().getRelativeY()));
            } else {
                if (UseMaxDisp){
                    dist = max(dist,maxO1);
                }    
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
        
        // set the properties for left and right outlines
        graph.setProperty(InternalProperties.LEFT_OUTLINE, new OutlineNode((-margins.left), (-margins.top), new OutlineNode(0.0, graph.getHeight() + margins.top + margins.bottom, null)));
        graph.setProperty(InternalProperties.RIGHT_OUTLINE, new OutlineNode(graph.getWidth() + margins.right, -margins.top, new OutlineNode(0.0, graph.getHeight() + margins.top + margins.bottom, null)));
        System.out.println(graph.getProperty(InternalProperties.RIGHT_OUTLINE));
        // checks if this node is a leaf
        if (graph.getOutgoingEdges().isEmpty()) {
            System.out.println("bin ein Blatt");
            graph.setProperty(InternalProperties.MAX_DEPTH, graph.getY() + graph.getHeight());
            graph.setX(0);
        } else {
            System.out.println("bin ein Knoten");
            graph.setProperty(InternalProperties.MAX_DEPTH, 0.0);
            // create a list with references to all children of this node.
            List<ElkNode> children = new ArrayList<>();
            for (int i = 0; i < graph.getOutgoingEdges().size(); i++) {
                ElkNode child = (ElkNode) graph.getOutgoingEdges().get(i).getTargets().get(0);
                yConsTreeStep(child);
                graph.setProperty(InternalProperties.MAX_DEPTH, max((double) graph.getProperty(InternalProperties.MAX_DEPTH), (double) child.getProperty(InternalProperties.MAX_DEPTH))); 
                // ein Test
                //graph.setX((double) graph.getProperty(MAX_DEPTH));
                children.add(child);
            }
            
            // now the children of this node get sorted to form a semi-circle.
            sortSubTrees(children);
            System.out.println("bis hier klappts noch");
            
            // now the children get stuffed together, using the outlines.
            for (int i = 0; i < children.size() - 1; i++) {
                putThemTogether(children.get(0), children.get(i), children.get(i + 1));
            }
            
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
            for(ElkNode child: children) {
                child.setX(child.getX() - moveRoot);
            }
            
            // now we need to combine the outlines of the root with the outlines of the most outer children.
            graph.getProperty(InternalProperties.LEFT_OUTLINE).getNext().setNext(new OutlineNode(-moveRoot, children.get(0).getY() - graph.getHeight() - graph.getY(), children.get(0).getProperty(InternalProperties.LEFT_OUTLINE).getNext()));
            double newX = children.get(children.size() - 1).getX() + children.get(children.size() - 1).getWidth() - graph.getWidth();
            double newY = children.get(children.size() - 1).getY() - graph.getHeight() - graph.getY();
            OutlineNode newNext = children.get(children.size() - 1).getProperty(InternalProperties.RIGHT_OUTLINE).getNext();
            graph.getProperty(InternalProperties.RIGHT_OUTLINE).getNext().setNext(new OutlineNode(newX, newY, newNext));
            //**/
            
            
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
    
    private void putThemTogether(ElkNode leftSubtree, ElkNode a, ElkNode b) {
        // calculate distance between the two parts
        double dist = outlineDistance(a.getProperty(InternalProperties.RIGHT_OUTLINE), a.getY(), b.getProperty(InternalProperties.LEFT_OUTLINE), b.getY(), true);
        b.setX(a.getX() + dist);
        
        // update the left outline, if needed
        if (leftSubtree.getProperty(InternalProperties.MAX_DEPTH) < b.getProperty(InternalProperties.MAX_DEPTH)) {
            
            // needed to calculate the position of the intercept between the two outlines.
            OutlineNode leftSubtreeLast = leftSubtree.getProperty(InternalProperties.LEFT_OUTLINE);
            double leftSubtreeX = leftSubtreeLast.getRelativeX();
            
            // jump to last position of the outline of leftSubtree
            while (!leftSubtreeLast.isLast()) {
                leftSubtreeLast = leftSubtreeLast.getNext();
                leftSubtreeX += leftSubtreeLast.getRelativeX();
            }
            
            // move on the outline of b to the position of the intercept.
            OutlineNode bLolIterator = b.getProperty(InternalProperties.LEFT_OUTLINE);
            double bAbsY = b.getY() + bLolIterator.getRelativeY();
            double bX = bLolIterator.getRelativeX();
            while (bAbsY < leftSubtree.getProperty(InternalProperties.MAX_DEPTH)) {
                bLolIterator = bLolIterator.getNext();
                bAbsY += bLolIterator.getRelativeY();
                bX += bLolIterator.getRelativeX();
            }
            double deltaY = b.getProperty(InternalProperties.MAX_DEPTH) - leftSubtree.getProperty(InternalProperties.MAX_DEPTH);
            double deltaX = 0.0;
            if (bLolIterator.getRelativeY() != 0.0) {
                deltaX = bLolIterator.getRelativeX() * deltaY / bLolIterator.getRelativeY();
            }
            double newX = b.getX() + bX - leftSubtreeX - deltaX;
            leftSubtreeLast.setNext(new OutlineNode(newX, 0.0, new OutlineNode(deltaX, deltaY, bLolIterator.getNext())));
            leftSubtree.setProperty(InternalProperties.MAX_DEPTH, b.getProperty(InternalProperties.MAX_DEPTH));
        }
        //**
        // update the right outline, if needed
        if (a.getProperty(InternalProperties.MAX_DEPTH) > b.getProperty(InternalProperties.MAX_DEPTH)) {
            
            // needed to calculate the position of the intercept between the two outlines.
            OutlineNode bLast = b.getProperty(InternalProperties.RIGHT_OUTLINE);
            double bX = bLast.getRelativeX();
            
            // jump to last position of the outline of b
            while (!bLast.isLast()) {
                bLast = bLast.getNext();
                bX += bLast.getRelativeX();
            }
            
            // move on the outline of a to the position of the intercept.
            OutlineNode aRolIterator = a.getProperty(InternalProperties.RIGHT_OUTLINE);
            double aAbsY = a.getY() + aRolIterator.getRelativeY();
            double aX = aRolIterator.getRelativeX();
            while (aAbsY < b.getProperty(InternalProperties.MAX_DEPTH)) {
                aRolIterator = aRolIterator.getNext();
                aAbsY += aRolIterator.getRelativeY();
                aX += aRolIterator.getRelativeX();
            }
            double deltaY = aAbsY - b.getProperty(InternalProperties.MAX_DEPTH);
            double deltaX = 0.0;
            if (aRolIterator.getRelativeY() != 0.0) {
                deltaX = aRolIterator.getRelativeX() * deltaY / aRolIterator.getRelativeY();
            }
            double newX = aX + a.getX() - deltaX - bX - b.getX();
            bLast.setNext(new OutlineNode(newX, 0.0, new OutlineNode(deltaX, deltaY, aRolIterator.getNext())));
            b.setProperty(InternalProperties.MAX_DEPTH, a.getProperty(InternalProperties.MAX_DEPTH));
        }
        //**/ 
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
