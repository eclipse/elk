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
package org.eclipse.elk.alg.layered.properties;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LGraphElement;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.core.options.CoreOptions;

/**
 * Container class for a variety of spacing values that are either specified in the general
 * {@link CoreOptions} class or KLay Layered's dedicated {@link LayeredOptions} class.
 * 
 * This class allows to either select the recorded spacing values directly or to query for spacing
 * values using one of the convenience methods. The methods do not provide results for every
 * combination of graph elements yet. In case it does not know the answer a
 * {@link UnspecifiedSpacingException} is thrown. In such a case the developer should add the
 * required functionality to this class.
 * 
 * @author uru
 */
public final class Spacings {

    // SUPPRESS CHECKSTYLE NEXT 40 VisibilityModifier
    /**
     * The horizontal spacing to be preserved between adjacent nodes. Multiply this value by
     * {@link #inLayerSpacingFactor} to get the vertical spacing value.
     */
    public final float nodeSpacing;
    /**
     * The horizontal spacing to be preserved between adjacent edge segments. Multiply this value by
     * {@link #inLayerSpacingFactor} to get the vertical spacing value.
     */
    public final float edgeEdgeSpacing;
    /**
     * The horizontal spacing to be preserved between a node and an adjacent edge segment. Multiply
     * this value by {@link #inLayerSpacingFactor} to get the vertical spacing value.
     */
    public final float edgeNodeSpacing;
    /**
     * The minimal spacing to be preserved between any pair of ports.
     */
    public final float portSpacing;
    /**
     * The minimal horizontal spacing to be preserved between external port dummies marked by the
     * {@link NodeType#EXTERNAL_PORT} flag. Multiply by {@link #inLayerSpacingFactor} to get the 
     * vertical spacing.
     */
    public final float externalPortSpacing;
    /**
     * The minimal spacing to be preserved between labels.
     */
    public final float labelSpacing;

    /**
     * A factor influencing all vertical spacing values. 
     */
    public final float inLayerSpacingFactor;

    /**
     * Pre calculated spacing values between any pair of {@link NodeType}s.
     */
    private final float[][] nodeTypeSpacings;
    
    /**
     * @param graph
     *            the {@link LGraph} for which to record the spacing values.
     */
    public Spacings(final LGraph graph) {

        nodeSpacing = graph.getProperty(LayeredOptions.SPACING_NODE);
        inLayerSpacingFactor = graph.getProperty(LayeredOptions.SPACING_IN_LAYER_SPACING_FACTOR);
        edgeEdgeSpacing = nodeSpacing * graph.getProperty(LayeredOptions.SPACING_EDGE_SPACING_FACTOR);
        edgeNodeSpacing = nodeSpacing * graph.getProperty(LayeredOptions.SPACING_EDGE_NODE_SPACING_FACTOR);
        portSpacing = graph.getProperty(CoreOptions.SPACING_PORT);
        externalPortSpacing = graph.getProperty(CoreOptions.SPACING_PORT);
        labelSpacing = graph.getProperty(CoreOptions.SPACING_LABEL);

        // pre calculate the spacings between pairs of node types
        int n = NodeType.values().length;
        nodeTypeSpacings = new float[n][n];
        precalculateNodeTypeSpacings();
    }
    
    private void precalculateNodeTypeSpacings() {
        // 6 node types --> (6 ncr 2) = 16 2-subsets + 6 1-subsets = 22
        // sort them based on expected frequency
        
        // normal
        nodeTypeSpacing(NodeType.NORMAL, nodeSpacing);
        nodeTypeSpacing(NodeType.NORMAL, NodeType.LONG_EDGE, edgeNodeSpacing);
        nodeTypeSpacing(NodeType.NORMAL, NodeType.NORTH_SOUTH_PORT, edgeNodeSpacing);
        nodeTypeSpacing(NodeType.NORMAL, NodeType.EXTERNAL_PORT, externalPortSpacing);
        nodeTypeSpacing(NodeType.NORMAL, NodeType.LABEL, edgeNodeSpacing);
        nodeTypeSpacing(NodeType.NORMAL, NodeType.BIG_NODE, edgeNodeSpacing);

        // longedge
        nodeTypeSpacing(NodeType.LONG_EDGE, edgeEdgeSpacing);
        nodeTypeSpacing(NodeType.LONG_EDGE, NodeType.NORTH_SOUTH_PORT, edgeEdgeSpacing);
        nodeTypeSpacing(NodeType.LONG_EDGE, NodeType.EXTERNAL_PORT, externalPortSpacing);
        nodeTypeSpacing(NodeType.LONG_EDGE, NodeType.LABEL, labelSpacing);
        nodeTypeSpacing(NodeType.LONG_EDGE, NodeType.BIG_NODE, edgeNodeSpacing);

        // northsouth
        nodeTypeSpacing(NodeType.NORTH_SOUTH_PORT, edgeEdgeSpacing);
        nodeTypeSpacing(NodeType.NORTH_SOUTH_PORT, NodeType.EXTERNAL_PORT, externalPortSpacing);
        nodeTypeSpacing(NodeType.NORTH_SOUTH_PORT, NodeType.LABEL, labelSpacing);
        nodeTypeSpacing(NodeType.NORTH_SOUTH_PORT, NodeType.BIG_NODE, edgeNodeSpacing);

        // external
        nodeTypeSpacing(NodeType.EXTERNAL_PORT, externalPortSpacing);
        nodeTypeSpacing(NodeType.EXTERNAL_PORT, NodeType.LABEL, externalPortSpacing);
        nodeTypeSpacing(NodeType.EXTERNAL_PORT, NodeType.BIG_NODE, externalPortSpacing);

        // label
        nodeTypeSpacing(NodeType.LABEL, labelSpacing);
        nodeTypeSpacing(NodeType.LABEL, NodeType.BIG_NODE, labelSpacing);

        // bignode
        nodeTypeSpacing(NodeType.BIG_NODE, nodeSpacing);
    }
    
    private void nodeTypeSpacing(final NodeType nt, final float spacing) {
        nodeTypeSpacings[nt.ordinal()][nt.ordinal()] = spacing;
    }
    
    private  void nodeTypeSpacing(final NodeType n1, final NodeType n2, final float spacing) {
        nodeTypeSpacings[n1.ordinal()][n2.ordinal()] = spacing;
        nodeTypeSpacings[n2.ordinal()][n1.ordinal()] = spacing;
    }

    // ----------------------------------------------------------------------------------
    // Regular, a.k.a. horizontal spacings
    // ----------------------------------------------------------------------------------

    /**
     * @param e1
     *            a graph element
     * @param e2
     *            another graph element
     * @return the horizontal spacing to be preserved between {@code e1} and {@code e2}
     */
    public float getHorizontalSpacing(final LGraphElement e1, final LGraphElement e2) {
        if (e1 instanceof LNode && e2 instanceof LNode) {
            return getHorizontalSpacing((LNode) e1, (LNode) e2);
        }

        throw new UnspecifiedSpacingException();
    }

    /**
     * @param n1
     *            a node
     * @param n2
     *            another node
     * @return the spacing to be preserved between {@code n1} and {@code n2}
     */
    public float getHorizontalSpacing(final LNode n1, final LNode n2) {
        return getHorizontalSpacing(n1.getType(), n2.getType());
    }

    /**
     * @param t1
     *            the type of one node
     * @param t2
     *            the type of the other node
     * @return the horizontal spacing value according to the two passed node types.
     */
    public float getHorizontalSpacing(final NodeType t1, final NodeType t2) {
        return nodeTypeSpacings[t1.ordinal()][t2.ordinal()];
    }

    // ----------------------------------------------------------------------------------
    // Vertical spacings
    // ----------------------------------------------------------------------------------

    /**
     * @param n1
     *            a node
     * @param n2
     *            another node
     * @return the vertical spacing to be preserved between {@code n1} and {@code n2}
     */
    public float getVerticalSpacing(final LNode n1, final LNode n2) {
        return getHorizontalSpacing(n1, n2) * inLayerSpacingFactor;
    }

    /**
     * @param t1
     *            a node type
     * @param t2
     *            another node type
     * @return the vertical spacing to be preserved between node type {@code t1} and {@code t2}
     */
    public float getVerticalSpacing(final NodeType t1, final NodeType t2) {
        return getHorizontalSpacing(t1, t2) * inLayerSpacingFactor;
    }

    /**
     * Dedicated exception indicating that no spacing value could be determined for a certain set of
     * graph elements. This is probably due to a programming error.
     */
    public static class UnspecifiedSpacingException extends RuntimeException {

        private static final long serialVersionUID = 1609767701465615319L;

        // SUPPRESS CHECKSTYLE NEXT 4 Javadoc
        public UnspecifiedSpacingException() {
        }

        public UnspecifiedSpacingException(final String msg) {
            super(msg);
        }
    }
}
