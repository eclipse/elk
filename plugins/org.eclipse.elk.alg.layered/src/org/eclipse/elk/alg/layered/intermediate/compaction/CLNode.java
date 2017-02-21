/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate.compaction;

import org.eclipse.elk.alg.layered.compaction.oned.CNode;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.util.nodespacing.Rectangle;

import com.google.common.collect.Iterables;

/**
 * Representation of a {@link LNode} in the constraint graph.
 * 
 * @see CNode
 */
public final class CLNode extends CNode {
    /** the associated horizontal spacing. */
    private double horizontalSpacing;
    /** the associated horizontal spacing. */
    private double verticalSpacing;
    /** the node. */
    private LNode lNode;

    /**
     * Constructor, infers hitbox from position, size and margins of the {@link LNode}. Also sets
     * the {@link org.eclipse.elk.alg.layered.compaction.oned.Quadruplet CompactionLock} according
     * to the ratio of incoming to outgoing {@link org.eclipse.elk.alg.layered.graph.LEdge LEdge}
     * s. {@link NodeType#EXTERNAL_PORT external port dummy nodes} are never locked to keep the
     * graph size minimal. They are later reset to the border position by the
     * {@link ElkGraphLayoutTransferrer}
     * 
     * @param lNode
     *            the node
     * @param layeredGraph
     *            the containing layered graph
     */
    public CLNode(final LNode lNode, final LGraph layeredGraph) {
        // getting the spacing properties
        horizontalSpacing = layeredGraph.getProperty(LayeredOptions.SPACING_NODE_NODE_BETWEEN_LAYERS).doubleValue();
        verticalSpacing = layeredGraph.getProperty(LayeredOptions.SPACING_NODE_NODE).doubleValue();
        
        // calculating the necessary hitbox dimensions
        this.lNode = lNode;
        hitbox =
                new Rectangle(
                        lNode.getPosition().x - lNode.getMargin().left, 
                        lNode.getPosition().y - lNode.getMargin().top, 
                        lNode.getSize().x + lNode.getMargin().left + lNode.getMargin().right, 
                        lNode.getSize().y + lNode.getMargin().top + lNode.getMargin().bottom);

        cGroupOffset.reset();
        
        // locking the node for directions that fewer edges are connected in
        int difference = Iterables.size(lNode.getIncomingEdges())
                       - Iterables.size(lNode.getOutgoingEdges());
        if (difference < 0) {
            lock.set(true, Direction.LEFT);
        } else if (difference > 0) {
            lock.set(true, Direction.RIGHT);
        }
        
        // excluding external port dummies
        if (lNode.getType() == NodeType.EXTERNAL_PORT) {
            lock.set(false, false, false, false);
        }
    }
    
    /**
     * @return the lNode
     */
    public LNode getlNode() {
        return lNode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyElementPosition() {
        lNode.getPosition().x = hitbox.x + lNode.getMargin().left;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getElementPosition() {
        return lNode.getPosition().x;
    }

    /**
     * {@inheritDoc}
     * <p>
     * If the {@link LNode} is an {@link NodeType#EXTERNAL_PORT external port} the returned spacing
     * is 0. This works because {@link org.eclipse.elk.alg.layered.graph.LGraphUtil
     * LGraphUtil#getExternalPortPosition(LGraph, LNode, double, double)
     * LGraphUtil.getExternalPortPosition} is called in
     * {@link org.eclipse.elk.alg.layered.graph.transform.ElkGraphLayoutTransferrer#applyLayout(LGraph)
     * ElkGraphLayoutTransferrer.applyLayout} and resets the position of the
     * {@link NodeType#EXTERNAL_PORT external port} dummy, which results in the correct border
     * spacing being used.
     * </p>
     */
    @Override
    public double getHorizontalSpacing() {
        if (lNode.getType() == NodeType.EXTERNAL_PORT) {
            return 0;
        }
        return horizontalSpacing;
    }

    /**
     * {@inheritDoc}
     * <p>
     * If the {@link LNode} is an {@link NodeType#EXTERNAL_PORT external port} the returned spacing
     * is 0. This works because {@link org.eclipse.elk.alg.layered.graph.LGraphUtil
     * LGraphUtil#getExternalPortPosition(LGraph, LNode, double, double)
     * LGraphUtil.getExternalPortPosition} is called in
     * {@link org.eclipse.elk.alg.layered.graph.transform.ElkGraphLayoutTransferrer#applyLayout(LGraph)
     * ElkGraphLayoutTransferrer.applyLayout} and resets the position of the
     * {@link NodeType#EXTERNAL_PORT external port} dummy, which results in the correct border
     * spacing being used.
     * </p>
     */
    @Override
    public double getVerticalSpacing() {
        if (lNode.getType() == NodeType.EXTERNAL_PORT) {
            return 0;
        }
        return verticalSpacing;
    }

    @Override
    public String toString() {
        return lNode.getProperty(InternalProperties.ORIGIN).toString();
    }
    
}
