/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    cds - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.graph.util;

import java.util.List;
import java.util.Objects;

import org.eclipse.elk.graph.ElkConnectableShape;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkGraphElement;
import org.eclipse.elk.graph.ElkGraphFactory;
import org.eclipse.elk.graph.ElkLabel;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.ElkPort;

/**
 * Utility methods that make using the ElkGraph data structure a bit easier. The class offers different types of
 * methods described below.
 * 
 * 
 * <h2>Factory Methods</h2>
 * 
 * <p>While {@link ElkGraphFactory} offers methods that simply create new model objects, the factory methods offered
 * by this class are designed to make creating a graph programmatically as easy as possible. This includes
 * automatically setting containments and may at some point also include applying sensible defaults, where
 * applicable.</p>
 * 
 * TODO: More documentation about what's in here. Also, much of the class is specified, but not implemented yet.
 */
public final class ElkGraphUtil {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Creation
    
    /**
     * Creates a new root node that will represent a graph.
     * 
     * @return the root node.
     */
    public static ElkNode createGraph() {
        return createNode(null);
    }
    
    /**
     * Creates a new node in the graph represented by the given parent node.
     * 
     * @param parent the parent node. May be {@code null}, in which case the new node is not added to anything.
     * @return the new node.
     */
    public static ElkNode createNode(final ElkNode parent) {
        ElkNode node = ElkGraphFactory.eINSTANCE.createElkNode();
        
        if (parent != null) {
            node.setParent(parent);
        }
        
        return node;
    }

    /**
     * Creates a new port for the given parent node.
     * 
     * @param parent the parent node. May be {@code null}, in which case the new port is not added to anything.
     * @return the new port.
     */
    public static ElkPort createPort(final ElkNode parent) {
        ElkPort port = ElkGraphFactory.eINSTANCE.createElkPort();
        
        if (parent != null) {
            port.setParent(parent);
        }
        
        return port;
    }

    /**
     * Creates a new label for the given graph element.
     * 
     * @param parent the parent element. May be {@code null}, in which case the new label is not added to anything.
     * @return the new label.
     */
    public static ElkLabel createLabel(final ElkGraphElement parent) {
        ElkLabel label = ElkGraphFactory.eINSTANCE.createElkLabel();
        
        if (parent != null) {
            label.setParent(parent);
        }
        
        return label;
    }
    
    /**
     * Creates a new edge contained in the given node, but not connecting anything yet. Note that the containing
     * node defines the coordinate system for the edge's routes and is not straightforward to select. One way to get
     * around this issue is to create an edge without a containing node, setup its sources and targets, and call
     * {@link #updateContainment(ElkEdge)} afterwards. 
     * 
     * @param containingNode the edge's containing node. May be {@code null}, in which case the new edge is not added
     *                       to anything.
     * @return the new edge.
     */
    public static ElkEdge createEdge(final ElkNode containingNode) {
        ElkEdge edge = ElkGraphFactory.eINSTANCE.createElkEdge();
        
        if (containingNode != null) {
            edge.setContainingNode(containingNode);
        }
        
        return edge;
    }
    
    /**
     * Creates an edge that connects the given source to the given target and sets the containing node accordingly.
     * This requires the source and target to be in the same graph model.
     * 
     * @param source the edge's source.
     * @param target the edge's target.
     * @return the new edge.
     * @throws NullPointerException if {@code source} or {@code target} is {@code null}.
     */
    public static ElkEdge createSimpleEdge(final ElkConnectableShape source, final ElkConnectableShape target) {
        Objects.requireNonNull(source, "source cannot be null");
        Objects.requireNonNull(target, "target cannot be null");
        
        ElkEdge edge = createEdge(null);
        
        edge.getSources().add(source);
        edge.getTargets().add(target);
        updateContainment(edge);
        
        return edge;
    }
    
    /**
     * Creates a hyperedge that connects the given sources to the given targets and sets the containing node
     * accordingly. This requires the sources and targets to be in the same graph model.
     * 
     * @param sources the edge's sources.
     * @param targets the edge's targets.
     * @return the new edge.
     * @throws NullPointerException if {@code sources} or {@code targets} is {@code null}.
     */
    public static ElkEdge createHyperedge(final Iterable<ElkConnectableShape> sources,
            final Iterable<ElkConnectableShape> targets) {
        
        Objects.requireNonNull(sources, "sources cannot be null");
        Objects.requireNonNull(targets, "targets cannot be null");
        
        ElkEdge edge = createEdge(null);
        
        List<ElkConnectableShape> edgeSources = edge.getSources();
        for (ElkConnectableShape source : sources) {
            edgeSources.add(source);
        }
        
        List<ElkConnectableShape> edgeTargets = edge.getTargets();
        for (ElkConnectableShape target : targets) {
            edgeTargets.add(target);
        }
        
        updateContainment(edge);
        
        return edge;
    }
    
    /**
     * Changes an edge's containment to the one returned by {@link #findBestEdgeContainment(ElkEdge)}.
     * 
     * @param edge the edge to update the containment of.
     * @throws NullPointerException if {@code edge} is {@code null}.
     */
    public static void updateContainment(final ElkEdge edge) {
        Objects.requireNonNull(edge, "edge cannot be null");
        
        edge.setContainingNode(findBestEdgeContainment(edge));
    }
    
    /**
     * Finds the node the given edge should best be contained in given the edge's sources and targets. This is usually
     * the first common ancestor of all sources and targets. Finding this containment requires all sources and targets
     * to be contained in the same graph model. If that is not the case, this method will return {@code null}.
     * 
     * @param edge the edge to find the best containment for.
     * @return the best containing node, or {@code null} if none could be found.
     * @throws NullPointerException if {@code edge} is {@code null}.
     */
    public static ElkNode findBestEdgeContainment(final ElkEdge edge) {
        Objects.requireNonNull(edge, "edge cannot be null");
        
        // TODO Implement
        return null;
    }
    
    // TODO Add methods to create edge sections.
    
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Convenience
    
    // TODO: Should the return type really be an edge, or an iterable?
    public static List<ElkEdge> allIncomingEdges(final ElkNode node) {
        return null;
    }
    
    public static List<ElkEdge> allOutgoingEdges(final ElkNode node) {
        return null;
    }
    
    public static List<ElkEdgeSection> allIncidentSections(final ElkEdgeSection section) {
        return null;
    }

    /**
     * Determines whether the given child node is a descendant of the given ancestor. This method is not reflexive (a
     * node is not its own descendant).
     * 
     * @param child a child node.
     * @param ancestor a prospective ancestory node.
     * @return {@code true} if {@code child} is a direct or indirect child of {@code ancestor}.
     */
    public static boolean isDescendant(final ElkNode child, final ElkNode ancestor) {
        // Go up the hierarchy and see if we find the ancestor
        ElkNode current = child;
        while (current.getParent() != null) {
            current = current.getParent();
            if (current == ancestor) {
                return true;
            }
        }
        
        // Reached the root node without finding the ancestor
        return false;
    }
    
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Privates
    
    /**
     * Private constructor, don't call.
     */
    private ElkGraphUtil() {
        // Nothing to do here...
    }
    
}
