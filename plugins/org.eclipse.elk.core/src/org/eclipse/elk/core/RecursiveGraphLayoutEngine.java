/*******************************************************************************
 * Copyright (c) 2008, 2018 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.core;

import java.util.Collections;
import java.util.List;
import java.util.Queue;

import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.data.LayoutAlgorithmResolver;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.HierarchyHandling;
import org.eclipse.elk.core.testing.TestController;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkBendPoint;
import org.eclipse.elk.graph.ElkConnectableShape;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.properties.GraphFeature;
import org.eclipse.elk.graph.util.ElkGraphUtil;

import com.google.common.collect.Lists;

/**
 * Performs layout on a graph with hierarchy by executing a layout algorithm on each level of the
 * hierarchy. This is done recursively from the leafs to the root of the nodes in the graph, using
 * size information from lower levels in the levels above.
 * 
 * <p>
 * The actual layout algorithms to execute are determined with the {@link CoreOptions#RESOLVED_ALGORITHM}
 * option. This option is configured by the {@link LayoutAlgorithmResolver}. If it is not yet set on
 * the top-level node, this implementation applies the default algorithm resolver by running
 * <pre>
 * ElkUtil.applyVisitors(layoutGraph, new LayoutAlgorithmResolver());
 * </pre>
 * If you need to customize the algorithm resolution or apply validation, you should run the algorithm
 * resolver before invoking this class.
 * </p>
 * 
 * <p>
 * Layout can be performed either as usual ({@link #layout(ElkNode, IElkProgressMonitor)}) or as part of
 * a unit test ({@link #layout(ElkNode, TestController, IElkProgressMonitor)}). The latter should
 * usually not be called directly, but is used by ELK's unit test framework for layout algorithms.
 * </p>
 * 
 * <p>
 * MIGRATE Extend the graph layout engine to offset edge coordinates properly
 * </p> 
 * 
 * @author ars
 * @author msp
 */
public class RecursiveGraphLayoutEngine implements IGraphLayoutEngine {
    
    /**
     * Performs recursive layout on the given layout graph.
     * 
     * @param layoutGraph top-level node of the graph to be laid out.
     * @param progressMonitor monitor to which progress of the layout algorithms is reported.
     */
    public void layout(final ElkNode layoutGraph, final IElkProgressMonitor progressMonitor) {
        layout(layoutGraph, null, progressMonitor);
    }
    
    /**
     * Performs recursive layout on the given layout graph, possibly in a test setting.
     * 
     * @param layoutGraph top-level node of the graph to be laid out.
     * @param testController an optional test controller if the layout run is performed as part of a unit test.
     * @param progressMonitor monitor to which progress of the layout algorithms is reported.
     */
    public void layout(final ElkNode layoutGraph, final TestController testController,
            final IElkProgressMonitor progressMonitor) {
        
        int nodeCount = countNodesRecursively(layoutGraph, true);
        progressMonitor.begin("Recursive Graph Layout", nodeCount);
        
        if (!layoutGraph.hasProperty(CoreOptions.RESOLVED_ALGORITHM)) {
            // Apply the default algorithm resolver to the graph in order to obtain algorithm meta data
            ElkUtil.applyVisitors(layoutGraph, new LayoutAlgorithmResolver());
        }
        
        // Perform recursive layout of the whole substructure of the given node
        layoutRecursively(layoutGraph, testController, progressMonitor);
        
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
     * @param testController an optional test controller if this layout run is part of a unit test
     * @param progressMonitor monitor used to keep track of progress
     * @return list of self loops routed inside the node.
     */
    protected List<ElkEdge> layoutRecursively(final ElkNode layoutNode, final TestController testController,
            final IElkProgressMonitor progressMonitor) {
        
        if (progressMonitor.isCanceled()) {
            return Collections.emptyList();
        }
        
        // Check if the node should be laid out at all
        if (layoutNode.getProperty(CoreOptions.NO_LAYOUT)) {
            return Collections.emptyList();
        }
        
        // We have to process the node if it has children...
        final boolean hasChildren = !layoutNode.getChildren().isEmpty();
        
        // ...or if inside self loop processing is enabled and it actually has inside self loops
        final List<ElkEdge> insideSelfLoops = gatherInsideSelfLoops(layoutNode);
        final boolean hasInsideSelfLoops = !insideSelfLoops.isEmpty();
        
        if (hasChildren || hasInsideSelfLoops) {
            // Fetch the layout algorithm that should be used to compute a layout for its content
            final LayoutAlgorithmData algorithmData = layoutNode.getProperty(CoreOptions.RESOLVED_ALGORITHM);
            if (algorithmData == null) {
                throw new UnsupportedConfigurationException("Resolved algorithm is not set;"
                        + " apply a LayoutAlgorithmResolver before computing layout.");
            }
            final boolean supportsInsideSelfLoops = algorithmData.supportsFeature(GraphFeature.INSIDE_SELF_LOOPS);
           
            // Persist the Hierarchy Handling in the nodes by querying the parent node
            evaluateHierarchyHandlingInheritance(layoutNode);
            
            // If the node contains inside self loops, but no regular children and if the layout
            // algorithm doesn't actually support inside self loops, we cancel
            if (!hasChildren && hasInsideSelfLoops && !supportsInsideSelfLoops) {
                return Collections.emptyList();
            }
            
            // We collect inside self loops of children and post-process them later
            List<ElkEdge> childrenInsideSelfLoops = Lists.newArrayList();
            
            // If the layout provider supports hierarchy, it is expected to layout the node's compound
            // node children as well
            int nodeCount;
            if (layoutNode.getProperty(CoreOptions.HIERARCHY_HANDLING) == HierarchyHandling.INCLUDE_CHILDREN
                    && (algorithmData.supportsFeature(GraphFeature.COMPOUND)
                            || algorithmData.supportsFeature(GraphFeature.CLUSTERS))) {
                
                // The layout algorithm will compute a layout for multiple levels of hierarchy under the current one
                nodeCount = countNodesWithHierarchy(layoutNode);
                
                // Look for nodes that stop the hierarchy handling, evaluating the inheritance on the way
                final Queue<ElkNode> nodeQueue = Lists.newLinkedList();
                nodeQueue.addAll(layoutNode.getChildren());
                
                while (!nodeQueue.isEmpty()) {
                    ElkNode node = nodeQueue.poll();
                    // Persist the Hierarchy Handling in every case. (Won't hurt with nodes that are
                    // evaluated in the next recursion)
                    evaluateHierarchyHandlingInheritance(node);
                    final boolean stopHierarchy = node.getProperty(CoreOptions.HIERARCHY_HANDLING)
                            == HierarchyHandling.SEPARATE_CHILDREN;

                    // Hierarchical layout is stopped by explicitly disabling or switching the algorithm. 
                    // In that case, a separate recursive call is used for child nodes
                    if (stopHierarchy 
                          || (node.hasProperty(CoreOptions.ALGORITHM) 
                                  && !algorithmData.equals(node.getProperty(CoreOptions.RESOLVED_ALGORITHM)))) {
                        List<ElkEdge> childLayoutSelfLoops = layoutRecursively(node, testController, progressMonitor);
                        childrenInsideSelfLoops.addAll(childLayoutSelfLoops);
                        // Explicitly disable hierarchical layout for the child node. Simplifies the
                        // handling of switching algorithms in the layouter.
                        node.setProperty(CoreOptions.HIERARCHY_HANDLING, HierarchyHandling.SEPARATE_CHILDREN);

                        // Apply the LayoutOptions.SCALE_FACTOR if present
                        ElkUtil.applyConfiguredNodeScaling(node);
                    } else {
                        // Child should be included in current layout, possibly adding its own children
                        nodeQueue.addAll(node.getChildren());
                    }
                }

            } else {
                // Layout each compound node contained in this node separately
                nodeCount = layoutNode.getChildren().size();
                for (ElkNode child : layoutNode.getChildren()) {
                    List<ElkEdge> childLayoutSelfLoops = layoutRecursively(child, testController, progressMonitor); 
                    childrenInsideSelfLoops.addAll(childLayoutSelfLoops);
                    
                    // Apply the LayoutOptions.SCALE_FACTOR if present
                    ElkUtil.applyConfiguredNodeScaling(child);
                }
            }

            if (progressMonitor.isCanceled()) {
                return Collections.emptyList();
            }
            
            // Before running layout on our node, we need to exclude any inside self loops of children
            // from being laid out again
            for (final ElkEdge selfLoop : childrenInsideSelfLoops) {
                selfLoop.setProperty(CoreOptions.NO_LAYOUT, true);
            }

            executeAlgorithm(layoutNode, algorithmData, testController, progressMonitor.subTask(nodeCount));
            
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
     * Execute the given layout algorithm on a parent node.
     */
    protected void executeAlgorithm(final ElkNode layoutNode, final LayoutAlgorithmData algorithmData,
            final TestController testController, final IElkProgressMonitor progressMonitor) {
        
        // Get an instance of the layout provider
        AbstractLayoutProvider layoutProvider = algorithmData.getInstancePool().fetch();
        
        // If we have a test controller and the layout algorithm supports test controllers, setup the test
        if (testController != null && testController.targets(algorithmData)) {
            testController.install(layoutProvider);
        }
        
        try {
            // Perform layout on the current hierarchy level
            layoutProvider.layout(layoutNode, progressMonitor);
            algorithmData.getInstancePool().release(layoutProvider);
        } catch (Exception exception) {
            // The layout provider has failed - destroy it slowly and painfully
            layoutProvider.dispose();
            throw exception;
        } finally {
            if (testController != null) {
                testController.uninstall();
            }
        }
    }

    /**
     * Determines the total number of layout nodes in the given layout graph.
     * 
     * @param layoutNode parent layout node to examine
     * @param countAncestors if true, the nodes on the ancestors path are also counted
     * @return total number of child layout nodes
     */
    protected int countNodesRecursively(final ElkNode layoutNode, final boolean countAncestors) {
        // count the content of the given node
        int count = layoutNode.getChildren().size();
        for (ElkNode childNode : layoutNode.getChildren()) {
            if (!childNode.getChildren().isEmpty()) {
                count += countNodesRecursively(childNode, false);
            }
        }
        // count the ancestors path
        if (countAncestors) {
            ElkNode parent = layoutNode.getParent();
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
     * Evaluates one level of inheritance for property {@link CoreOptions#HIERARCHY_HANDLING}. If the root node is
     * evaluated and it is set to inherit (or not set at all) the property is set to
     * {@link HierarchyHandling#SEPARATE_CHILDREN}.
     * 
     * @param layoutNode
     *            The current node which should be evaluated
     */
    private void evaluateHierarchyHandlingInheritance(final ElkNode layoutNode) {
        // Pre-process the hierarchy handling to substitute inherited handling by the parent
        // value. If the root node is set to inherit, it is set to separate the children.
        if (layoutNode.getProperty(CoreOptions.HIERARCHY_HANDLING) == HierarchyHandling.INHERIT) {
            if (layoutNode.getParent() == null) {
                // Set root node to separate children handling
                layoutNode.setProperty(CoreOptions.HIERARCHY_HANDLING, HierarchyHandling.SEPARATE_CHILDREN);
            } else {
                // Set hierarchy handling to the value of the parent. 
                // It is safe to assume that the parent has been handled before and is not set to
                // INHERIT anymore.
                HierarchyHandling parentHandling = layoutNode.getParent().getProperty(CoreOptions.HIERARCHY_HANDLING);
                layoutNode.setProperty(CoreOptions.HIERARCHY_HANDLING, parentHandling);
            }
        }
    }

    /**
     * Determines the number of layout nodes in the given layout graph across multiple levels of
     * hierarchy. Counting is stopped at nodes which disable the hierarchical layout or are
     * configured to use a different layout algorithm.
     * 
     * @param parentNode
     *            parent layout node to examine
     * @return total number of child layout nodes
     */
    private int countNodesWithHierarchy(final ElkNode parentNode) {
        // Count the content of the given node
        int count = parentNode.getChildren().size();
        for (ElkNode childNode : parentNode.getChildren()) {
            if (childNode.getProperty(CoreOptions.HIERARCHY_HANDLING) != HierarchyHandling.SEPARATE_CHILDREN) {
                LayoutAlgorithmData parentData = parentNode.getProperty(CoreOptions.RESOLVED_ALGORITHM);
                LayoutAlgorithmData childData = childNode.getProperty(CoreOptions.RESOLVED_ALGORITHM);
                // Only count nodes that don't abort the hierarchical layout
                if ((parentData == childData || parentData != null && parentData.equals(childData))
                        && !childNode.getChildren().isEmpty()) {
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
    protected List<ElkEdge> gatherInsideSelfLoops(final ElkNode node) {
        if (node.getProperty(CoreOptions.INSIDE_SELF_LOOPS_ACTIVATE)) {
            List<ElkEdge> insideSelfLoops = Lists.newArrayList();
            
            for (ElkEdge edge : ElkGraphUtil.allOutgoingEdges(node)) {
                // MIGRATE Adapt to hyperedges and make error-safe
                if (edge.isSelfloop()) {
                    if (edge.getProperty(CoreOptions.INSIDE_SELF_LOOPS_YO)) {
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
    protected void postProcessInsideSelfLoops(final List<ElkEdge> insideSelfLoops) {
        for (final ElkEdge selfLoop : insideSelfLoops) {
            // MIGRATE Adapt to hyperedges and make error-safe
            final ElkConnectableShape node = ElkGraphUtil.connectableShapeToNode(selfLoop.getSources().get(0));
            
            final double xOffset = node.getX();
            final double yOffset = node.getY();
            
            // Offset the edge coordinates by the node's position
            // MIGRATE Adapt to hyperedges. Also, what about multiple edge sections?
            ElkEdgeSection section = selfLoop.getSections().get(0);
            section.setStartLocation(section.getStartX() + xOffset, section.getStartY() + yOffset);
            section.setEndLocation(section.getEndX() + xOffset, section.getEndY() + yOffset);
            
            for (final ElkBendPoint bend : section.getBendPoints()) {
                bend.set(bend.getX() + xOffset, bend.getY() + yOffset);
            }
            
            // Offset junction points by the node position
            selfLoop.getProperty(CoreOptions.JUNCTION_POINTS).offset(xOffset, yOffset);
        }
    }

}
