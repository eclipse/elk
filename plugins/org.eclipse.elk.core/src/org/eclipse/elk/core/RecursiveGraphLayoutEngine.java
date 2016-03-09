/*******************************************************************************
 * Copyright (c) 2008, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core;

import java.util.Collections;
import java.util.List;
import java.util.Queue;

import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.data.LayoutMetaDataService;
import org.eclipse.elk.core.klayoutdata.KEdgeLayout;
import org.eclipse.elk.core.klayoutdata.KLayoutData;
import org.eclipse.elk.core.klayoutdata.KPoint;
import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.GraphFeature;
import org.eclipse.elk.core.options.HierarchyHandling;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KNode;

import com.google.common.collect.Lists;

/**
 * Performs layout on a graph with hierarchy by executing a layout algorithm on each level of the
 * hierarchy. This is done recursively from the leafs to the root of the nodes in the graph, using
 * size information from lower levels in the levels above.
 * 
 * @kieler.design 2011-03-14 reviewed by cmot, cds
 * @kieler.rating yellow 2012-08-10 review KI-23 by cds, sgu
 * @author ars
 * @author msp
 */
public class RecursiveGraphLayoutEngine implements IGraphLayoutEngine {
    
    /**
     * Performs recursive layout on the given layout graph.
     * 
     * @param layoutGraph top-level node of the graph to be laid out
     * @param progressMonitor monitor to which progress of the layout algorithms is reported
     */
    public void layout(final KNode layoutGraph, final IElkProgressMonitor progressMonitor) {
        int nodeCount = countNodesRecursively(layoutGraph, true);
        progressMonitor.begin("Recursive Graph Layout", nodeCount);
        
        // perform recursive layout of the whole substructure of the given node
        layoutRecursively(layoutGraph, progressMonitor);
        
        progressMonitor.done();
    }

    /**
     * Recursive function to enable layout of hierarchy. The leafs are laid out first to use their
     * layout information in the levels above.
     * 
     * <p>This method returns self loops routed inside the given layout node. Those will have
     * coordinates relative to the node's top left corner, which is incorrect. Once the node's
     * final coordinates in its container are determined, any inside self loops will have to be offset
     * by the node's position.</p>
     * 
     * @param layoutNode the node with children to be laid out
     * @param progressMonitor monitor used to keep track of progress
     * @return list of self loops routed inside the node.
     */
    protected List<KEdge> layoutRecursively(final KNode layoutNode,
            final IElkProgressMonitor progressMonitor) {
        
        if (progressMonitor.isCanceled()) {
            return Collections.emptyList();
        }
        
        final KShapeLayout layoutNodeShapeLayout = layoutNode.getData(KShapeLayout.class);
        
        // Check if the node should be laid out at all
        if (layoutNodeShapeLayout.getProperty(CoreOptions.NO_LAYOUT)) {
            return Collections.emptyList();
        }
        
        // We have to process the node if it has children...
        final boolean hasChildren = !layoutNode.getChildren().isEmpty();
        
        // ...or if inside self loop processing is enabled and it actually has inside self loops
        final List<KEdge> insideSelfLoops = gatherInsideSelfLoops(layoutNode);
        final boolean hasInsideSelfLoops = !insideSelfLoops.isEmpty();
        
        if (hasChildren || hasInsideSelfLoops) {
            // this node has children and is thus a compound node;
            // fetch the layout algorithm that should be used to compute a layout for its content
            final LayoutAlgorithmData algorithmData = getAlgorithm(layoutNode);
            final boolean supportsInsideSelfLoops = algorithmData.supportsFeature(GraphFeature.INSIDE_SELF_LOOPS);
           
            // Persist the Hierarchy Handling in the nodes by querying the parent node
            evaluateHierarchyHandlingInheritance(layoutNode);
           
            
            // If the node contains inside self loops, but no regular children and if the layout
            // algorithm doesn't actually support inside self loops, we cancel
            if (!hasChildren && hasInsideSelfLoops && !supportsInsideSelfLoops) {
                return Collections.emptyList();
            }
            
            // We collect inside self loops of children and post-process them later
            List<KEdge> childrenInsideSelfLoops = Lists.newArrayList();
            
            // if the layout provider supports hierarchy, it is expected to layout the node's compound
            // node children as well
            int nodeCount;
            if (layoutNodeShapeLayout.getProperty(CoreOptions.HIERARCHY_HANDLING)
                        == HierarchyHandling.INCLUDE_CHILDREN && (algorithmData.supportsFeature(GraphFeature.COMPOUND)
                    || algorithmData.supportsFeature(GraphFeature.CLUSTERS))) {
                
                // the layout algorithm will compute a layout for multiple levels of hierarchy under the current one
                nodeCount = countNodesWithHierarchy(layoutNode);
                
                // Look for nodes that stop the hierarchy handling, evaluating the inheritance on the way
                final Queue<KNode> kNodeQueue = Lists.newLinkedList();
                kNodeQueue.addAll(layoutNode.getChildren());
                
                while (!kNodeQueue.isEmpty()) {
                    KNode knode = kNodeQueue.poll();
                    // Persist the Hierarchy Handling in every case. (Won't hurt with nodes that are
                    // evaluated in the next recursion)
                    evaluateHierarchyHandlingInheritance(knode);
                    final KShapeLayout knodeLayout = knode.getData(KShapeLayout.class);
                    final boolean stopHierarchy = knodeLayout.getProperty(
                            CoreOptions.HIERARCHY_HANDLING) == HierarchyHandling.SEPARATE_CHILDREN;

                    if (stopHierarchy || !getAlgorithm(knode).equals(algorithmData)) {
                        // Hierarchical layout is stopped by explicitly disabling or switching
                        // algorithm. Separate recursive call is used for child nodes
                        List<KEdge> childLayoutSelfLoops = layoutRecursively(knode, progressMonitor);
                        childrenInsideSelfLoops.addAll(childLayoutSelfLoops);
                        // Explicitly disable hierarchical layout for the child node. Simplifies the
                        // handling of switching algorithms in the layouter.
                        knodeLayout.setProperty(CoreOptions.HIERARCHY_HANDLING,
                                HierarchyHandling.SEPARATE_CHILDREN);

                        // apply the CoreOptions.SCALE_FACTOR if present
                        ElkUtil.applyConfiguredNodeScaling(knode);
                    } else {
                        // Child should be included in current layout, possibly adding its own
                        // children
                        kNodeQueue.addAll(knode.getChildren());
                    }
                }

            } else {
                // layout each compound node contained in this node separately
                nodeCount = layoutNode.getChildren().size();
                for (KNode child : layoutNode.getChildren()) {
                    List<KEdge> childLayoutSelfLoops = layoutRecursively(child, progressMonitor); 
                    childrenInsideSelfLoops.addAll(childLayoutSelfLoops);
                    
                    // apply the CoreOptions.SCALE_FACTOR if present
                    ElkUtil.applyConfiguredNodeScaling(child);
                }
            }

            if (progressMonitor.isCanceled()) {
                return Collections.emptyList();
            }
            
            // Before running layout on our node, we need to exclude any inside self loops of children
            // from being laid out again
            for (final KEdge selfLoop : childrenInsideSelfLoops) {
                KEdgeLayout edgeLayout = selfLoop.getData(KEdgeLayout.class);
                edgeLayout.setProperty(CoreOptions.NO_LAYOUT, true);
            }

            // get an instance of the layout provider
            AbstractLayoutProvider layoutProvider = algorithmData.getInstancePool().fetch();
            try {
                // perform layout on the current hierarchy level
                layoutProvider.layout(layoutNode, progressMonitor.subTask(nodeCount));
                algorithmData.getInstancePool().release(layoutProvider);
            } catch (RuntimeException exception) {
                // the layout provider has failed - destroy it slowly and painfully
                layoutProvider.dispose();
                throw exception;
            }
            
            // Post-process the inner self loops we collected
            postProcessInsideSelfLoops(childrenInsideSelfLoops);
            
            // Return our own inside self loops to be processed later
            if (hasInsideSelfLoops && supportsInsideSelfLoops) {
                return insideSelfLoops;
            } else {
                return Collections.emptyList();
            }
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Returns the most appropriate layout algorithm for the given node.
     * 
     * @param layoutNode node for which a layout provider is requested
     * @return a layout algorithm that fits the layout hints for the given node
     */
    protected LayoutAlgorithmData getAlgorithm(final KNode layoutNode) {
        KShapeLayout nodeLayout = layoutNode.getData(KShapeLayout.class);
        String algorithmId = nodeLayout.getProperty(CoreOptions.ALGORITHM);
        LayoutAlgorithmData result = LayoutMetaDataService.getInstance().getAlgorithmDataOrDefault(
                algorithmId, getDefaultLayoutAlgorithmID());
        
        if (result == null) {
            if (algorithmId == null || algorithmId.isEmpty()) {
                throw new UnsupportedConfigurationException("No layout algorithm has been specified ("
                        + layoutNode + ").");
            } else {
                throw new UnsupportedConfigurationException("Layout algorithm not found: " + algorithmId);
            }
        }
        return result;
    }
    
    /**
     * Returns the ID of the layout algorithm to be used by default.
     * 
     * @return the default layout algorithm's ID.
     */
    public String getDefaultLayoutAlgorithmID() {
        return "org.eclipse.elk.layered";
    }

    /**
     * Determines the total number of layout nodes in the given layout graph.
     * 
     * @param layoutNode parent layout node to examine
     * @param countAncestors if true, the nodes on the ancestors path are also counted
     * @return total number of child layout nodes
     */
    protected int countNodesRecursively(final KNode layoutNode, final boolean countAncestors) {
        // count the content of the given node
        int count = layoutNode.getChildren().size();
        for (KNode childNode : layoutNode.getChildren()) {
            if (!childNode.getChildren().isEmpty()) {
                count += countNodesRecursively(childNode, false);
            }
        }
        // count the ancestors path
        if (countAncestors) {
            KNode parent = layoutNode.getParent();
            while (parent != null) {
                count += parent.getChildren().size();
                parent = parent.getParent();
            }
        }
        return count;
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////////////
    // Hierarchy Handling
    
    /**
     * Evaluates one level of inheritance for property {@link CoreOptions#HIERARCHY_HANDLING}.
     * Additionally provides legacy support for property {@link CoreOptions#LAYOUT_HIERARCHY} and
     * replaces it with the new property. If the root node is evaluated and it is set to inherit (or
     * not set at all) the property is set to {@link HierarchyHandling#SEPARATE_CHILDREN}.
     * 
     * @param layoutNode
     *            The current node which should be evaluated
     */
    private void evaluateHierarchyHandlingInheritance(final KNode layoutNode) {
        KLayoutData layoutNodeShapeLayout = layoutNode.getData(KShapeLayout.class);
        // Pre-process the hierarchy handling by replacing the deprecated LAYOUT_HIERARCHY
        // property with the new hierarchy handling property
        boolean hasLayoutHierarchy = layoutNodeShapeLayout.getProperty(CoreOptions.LAYOUT_HIERARCHY);
        if (hasLayoutHierarchy) {
            layoutNodeShapeLayout.setProperty(CoreOptions.HIERARCHY_HANDLING,
                    HierarchyHandling.INCLUDE_CHILDREN);
        }
        
        // Pre-process the hierarchy handling to substitute inherited handling by the parent
        // value. If the root node is set to inherit, it is set to separate the children.
        if (layoutNodeShapeLayout.getProperty(CoreOptions.HIERARCHY_HANDLING) == HierarchyHandling.INHERIT) {
            if (layoutNode.getParent() == null) {
                // Set root node to separate children handling
                layoutNodeShapeLayout.setProperty(CoreOptions.HIERARCHY_HANDLING,
                        HierarchyHandling.SEPARATE_CHILDREN);
            } else {
                // Set hierarchy handling to the value of the parent. 
                // It is safe to assume that the parent has been handled before and is not set to
                // INHERIT anymore.
                HierarchyHandling parentHandling = layoutNode.getParent().getData(KShapeLayout.class)
                        .getProperty(CoreOptions.HIERARCHY_HANDLING);
                layoutNodeShapeLayout.setProperty(CoreOptions.HIERARCHY_HANDLING, parentHandling);
            }
        }
    }

    /**
     * Determines the number of layout nodes in the given layout graph across multiple levels of
     * hierarchy. Counting is stopped at nodes which disable the hierarchical layout or are
     * configured to use a different layout algorithm.
     * 
     * @param layoutNode
     *            parent layout node to examine
     * @return total number of child layout nodes
     */
    private int countNodesWithHierarchy(final KNode layoutNode) {
        // count the content of the given node
        int count = layoutNode.getChildren().size();
        for (KNode childNode : layoutNode.getChildren()) {
            if (childNode.getData(KShapeLayout.class).getProperty(CoreOptions.HIERARCHY_HANDLING) 
                    != HierarchyHandling.SEPARATE_CHILDREN
                    && getAlgorithm(layoutNode).equals(getAlgorithm(childNode))) {
                
                // Only count nodes that don't abort the hierarchical layout
                if (!childNode.getChildren().isEmpty()) {
                    count += countNodesWithHierarchy(childNode);
                }
            }
        }
        return count;
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////////////
    // Inside Self Loops
    
    /**
     * Returns a list of self loops of the given node that should be routed inside that node instead
     * of around it. For the node to even be considered for inside self loop processing, its
     * {@link CoreOptions#SELF_LOOP_INSIDE} property must be set to {@code true}. The returned
     * list will then consist of those of its outgoing edges that are self loops and that have that
     * property set to {@code true} as well.
     * 
     * @param node
     *            the node whose inside self loops to return.
     * @return possibly empty list of inside self loops.
     */
    protected List<KEdge> gatherInsideSelfLoops(final KNode node) {
        KShapeLayout nodeLayout = node.getData(KShapeLayout.class);
        
        if (nodeLayout.getProperty(CoreOptions.INSIDE_SELF_LOOPS_ACTIVATE)) {
            List<KEdge> insideSelfLoops = Lists.newArrayListWithCapacity(node.getOutgoingEdges().size());
            
            for (KEdge edge : node.getOutgoingEdges()) {
                if (edge.getTarget() == node) {
                    final KEdgeLayout edgeLayout = edge.getData(KEdgeLayout.class);
                    if (edgeLayout.getProperty(CoreOptions.INSIDE_SELF_LOOPS_YO)) {
                        insideSelfLoops.add(edge);
                    }
                }
            }
            
            return insideSelfLoops;
        } else {
            return Collections.emptyList();
        }
    }
    
    /**
     * Post-processes self loops routed inside by offsetting their coordinates by the coordinates of
     * their parent node. The post processing is necessary since the self loop coordinates are
     * relative to their parent node's upper left corner since, at that point, the parent node's
     * final coordinates are not determined yet.
     * 
     * @param insideSelfLoops
     *            list of inside self loops to post-process.
     */
    protected void postProcessInsideSelfLoops(final List<KEdge> insideSelfLoops) {
        for (final KEdge selfLoop : insideSelfLoops) {
            final KNode node = selfLoop.getSource();
            final KShapeLayout nodeLayout = node.getData(KShapeLayout.class);
            final KEdgeLayout edgeLayout = selfLoop.getData(KEdgeLayout.class);
            
            final float xOffset = nodeLayout.getXpos();
            final float yOffset = nodeLayout.getYpos();
            
            // Offset the edge coordinates by the node's position
            applyOffset(edgeLayout.getSourcePoint(), xOffset, yOffset);
            applyOffset(edgeLayout.getTargetPoint(), xOffset, yOffset);
            
            for (final KPoint bend : edgeLayout.getBendPoints()) {
                applyOffset(bend, xOffset, yOffset);
            }
        }
    }
    
    /**
     * Offsets a point by the given amount.
     * 
     * @param point
     *            point to offset.
     * @param xOffset
     *            horizontal offset.
     * @param yOffset
     *            vertical offset.
     */
    protected void applyOffset(final KPoint point, final float xOffset, final float yOffset) {
        point.setX(point.getX() + xOffset);
        point.setY(point.getY() + yOffset);
    }

}
