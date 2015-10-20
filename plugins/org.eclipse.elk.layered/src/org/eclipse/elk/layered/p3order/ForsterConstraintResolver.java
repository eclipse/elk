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
package org.eclipse.elk.layered.p3order;

import java.util.List;
import java.util.ListIterator;

import org.eclipse.elk.core.util.Pair;
import org.eclipse.elk.layered.graph.LNode;
import org.eclipse.elk.layered.graph.LNode.NodeType;
import org.eclipse.elk.layered.properties.InternalProperties;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * Detects and resolves violated constraints. Inspired by
 * <ul>
 * <li>Michael Forster. A fast and simple heuristic for constrained two-level crossing reduction. In
 * <i>Graph Drawing</i>, volume 3383 of LNCS, pp. 206-216. Springer, 2005.</li>
 * </ul>
 * This constraint resolver relies on the assumption that all node groups have well-defined barycenter
 * values and are already sorted by these barycenter values.
 * 
 * @author cds
 * @author ima
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public final class ForsterConstraintResolver implements IConstraintResolver {

    /** the layout units for handling dummy nodes for north / south ports. */
    private final Multimap<LNode, LNode> layoutUnits;
    
    /**
     * Constructs a Forster constraint resolver.
     * 
     * @param layoutUnits
     *            a map associating layout units with their respective members
     */
    public ForsterConstraintResolver(final Multimap<LNode, LNode> layoutUnits) {
        this.layoutUnits = layoutUnits;
    }
    
    /**
     * {@inheritDoc}
     */
    public void processConstraints(final List<NodeGroup> nodeGroups) {

        // Build the constraints graph
        buildConstraintsGraph(nodeGroups);

        // Find violated vertices
        Pair<NodeGroup, NodeGroup> violatedConstraint = null;
        while ((violatedConstraint = findViolatedConstraint(nodeGroups)) != null) {
            handleViolatedConstraint(violatedConstraint.getFirst(), violatedConstraint.getSecond(),
                    nodeGroups);
        }
    }

    /**
     * Build the constraint graph for the given vertices. The constraint graph is created from
     * the predefined <em>in-layer successor constraints</em> and the <em>layout units</em>.
     * 
     * @param nodeGroups
     *            the array of single-node vertices sorted by their barycenter values.
     */
    private void buildConstraintsGraph(final List<NodeGroup> nodeGroups) {

        // Reset the constraint fields
        for (NodeGroup nodeGroup : nodeGroups) {
            nodeGroup.resetOutgoingConstraints();
            nodeGroup.incomingConstraintsCount = 0;
        }

        // Iterate through the vertices, adding the necessary constraints
        LNode lastNonDummyNode = null;
        for (NodeGroup nodeGroup : nodeGroups) {
            LNode node = nodeGroup.getNode();

            // Add the constraints given by the vertex's node
            for (LNode successor : node.getProperty(
                    InternalProperties.IN_LAYER_SUCCESSOR_CONSTRAINTS)) {
                NodeGroup successorNodeGroup = successor.getProperty(InternalProperties.NODE_GROUP);
                nodeGroup.getOutgoingConstraints().add(successorNodeGroup);
                successorNodeGroup.incomingConstraintsCount++;
            }

            // Check if we're processing a a normal, none-dummy node
            if (node.getType() == NodeType.NORMAL) {
                // If we already processed another normal, non-dummy node, we need to add
                // constraints from all of that other node's layout unit's vertices to this
                // node's layout unit's vertices
                if (lastNonDummyNode != null) {
                    for (LNode lastUnitNode : layoutUnits.get(lastNonDummyNode)) {
                        NodeGroup lastUnitNodeGroup = lastUnitNode.getProperty(
                                InternalProperties.NODE_GROUP);

                        for (LNode currentUnitNode : layoutUnits.get(node)) {
                            NodeGroup currentUnitNodeGroup = currentUnitNode.getProperty(
                                    InternalProperties.NODE_GROUP);
                            lastUnitNodeGroup.getOutgoingConstraints().add(currentUnitNodeGroup);
                            currentUnitNodeGroup.incomingConstraintsCount++;
                        }
                    }
                }

                lastNonDummyNode = node;
            }
        }
    }

    /**
     * Returns a violated constraint, if any is left. Constraint violation detection is based
     * on the barycenter values of the node groups, hence it is a critical requirement that
     * the node groups are sorted by their barycenter values.
     * 
     * @param nodeGroups
     *            list of vertices.
     * @return the two vertices whose constraint is violated, or {@code null} if none could be
     *         found. The two vertices are returned in the order they should appear in, not in the
     *         order that violates their constraint.
     */
    private Pair<NodeGroup, NodeGroup> findViolatedConstraint(final List<NodeGroup> nodeGroups) {
        List<NodeGroup> activeNodeGroups = null;

        // Iterate through the constrained vertices
        double lastValue = Short.MIN_VALUE;
        for (NodeGroup nodeGroup : nodeGroups) {
            assert nodeGroup.barycenter != null && nodeGroup.barycenter >= lastValue;
            lastValue = nodeGroup.barycenter;
            nodeGroup.resetIncomingConstraints();
            
            // Find sources of the constraint graph to start the constraints check
            if (nodeGroup.hasOutgoingConstraints() && nodeGroup.incomingConstraintsCount == 0) {
                if (activeNodeGroups == null) {
                    activeNodeGroups = Lists.newArrayList();
                }
                activeNodeGroups.add(nodeGroup);
            }
        }

        // Iterate through the active node groups to find one with violated constraints
        if (activeNodeGroups != null) {
            while (!activeNodeGroups.isEmpty()) {
                NodeGroup nodeGroup = activeNodeGroups.remove(0);
    
                // See if we can find a violated constraint
                if (nodeGroup.hasIncomingConstraints()) {
                    for (NodeGroup predecessor : nodeGroup.getIncomingConstraints()) {
                        if (predecessor.barycenter.floatValue() == nodeGroup.barycenter.floatValue()) {
                            if (nodeGroups.indexOf(predecessor) > nodeGroups.indexOf(nodeGroup)) {
                                // The predecessor has equal barycenter, but higher index
                                return Pair.of(predecessor, nodeGroup);
                            }
                        } else if (predecessor.barycenter > nodeGroup.barycenter) {
                            // The predecessor has greater barycenter and thus also higher index
                            return Pair.of(predecessor, nodeGroup);
                        }
                    }
                }
    
                // No violated constraints; add outgoing constraints to the respective incoming list
                for (NodeGroup successor : nodeGroup.getOutgoingConstraints()) {
                    List<NodeGroup> successorIncomingList = successor.getIncomingConstraints();
                    successorIncomingList.add(0, nodeGroup);
    
                    if (successor.incomingConstraintsCount == successorIncomingList.size()) {
                        activeNodeGroups.add(successor);
                    }
                }
            }
        }

        // No violated constraints found
        return null;
    }

    /** Delta that two barycenters can differ by to still be considered equal. */
    private static final float BARYCENTER_EQUALITY_DELTA = 0.0001F;
    
    /**
     * Handles the case of a violated constraint. The node groups must be sorted by their
     * barycenter values. After this method has finished, the list of node groups is smaller
     * by one element, since two node groups have been unified, but the list is still correctly
     * sorted by barycenter values.
     * 
     * @param firstNodeGroup
     *            the node group with violated outgoing constraint
     * @param secondNodeGroup
     *            the node group with violated incoming constraint
     * @param nodeGroups
     *            the list of vertices
     */
    private void handleViolatedConstraint(final NodeGroup firstNodeGroup,
            final NodeGroup secondNodeGroup, final List<NodeGroup> nodeGroups) {

        // Create a new vertex from the two constrain-violating vertices; this also
        // automatically calculates the new vertex's barycenter value
        NodeGroup newNodeGroup = new NodeGroup(firstNodeGroup, secondNodeGroup);
        assert newNodeGroup.barycenter + BARYCENTER_EQUALITY_DELTA >= secondNodeGroup.barycenter;
        assert newNodeGroup.barycenter - BARYCENTER_EQUALITY_DELTA <= firstNodeGroup.barycenter;

        // Iterate through the vertices. Remove the old vertices. Insert the new one
        // according to the barycenter value, thereby keeping the list sorted. Along
        // the way, constraint relationships will be updated
        ListIterator<NodeGroup> nodeGroupIterator = nodeGroups.listIterator();
        boolean alreadyInserted = false;
        while (nodeGroupIterator.hasNext()) {
            NodeGroup nodeGroup = nodeGroupIterator.next();

            if (nodeGroup == firstNodeGroup || nodeGroup == secondNodeGroup) {
                // Remove the two node groups with violated constraint from the list
                nodeGroupIterator.remove();
            } else if (!alreadyInserted && nodeGroup.barycenter > newNodeGroup.barycenter) {
                // If we haven't inserted the new node group into the list already, do that now.
                // Note: we're not calling next() again. This means that during the next iteration,
                // we will again be looking at the current node group. But then, alreadyInserted will
                // be true and we can look at that node group's outgoing constraints.
                nodeGroupIterator.previous();
                nodeGroupIterator.add(newNodeGroup);

                alreadyInserted = true;
            } else if (nodeGroup.hasOutgoingConstraints()) {
                // Check if the vertex has any constraints with the former two vertices
                boolean firstNodeGroupConstraint = nodeGroup.getOutgoingConstraints()
                        .remove(firstNodeGroup);
                boolean secondNodeGroupConstraint = nodeGroup.getOutgoingConstraints()
                        .remove(secondNodeGroup);

                if (firstNodeGroupConstraint || secondNodeGroupConstraint) {
                    nodeGroup.getOutgoingConstraints().add(newNodeGroup);
                    newNodeGroup.incomingConstraintsCount++;
                }
            }
        }

        // If we haven't inserted the new node group already, add it to the end
        if (!alreadyInserted) {
            nodeGroups.add(newNodeGroup);
        }
    }

}
