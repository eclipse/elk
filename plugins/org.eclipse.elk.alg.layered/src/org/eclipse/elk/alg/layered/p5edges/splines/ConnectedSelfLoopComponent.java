/*******************************************************************************
 * Copyright (c) 2014, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.splines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.PortSide;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * This class is holding a set of edges that are representing a connected component of self loops.
 * 
 * What is a connected component of self loops? For a given node N of the given layeredGraph, construct
 * a graph G with a node N_i for every port P_i of N. If there is a self loop on N, pointing from P_i
 * to P_j, there is also an (undirected) edge in G between N_i to N_j. The connected components of G
 * are equal to the connected components of self loops on node N.
 * 
 * @author tit
 */
public final class ConnectedSelfLoopComponent {
    /** The parent node. */
    private final LNode owner; 
    
    /** All loop-edges that are part of this connectedSelfLoop. */
    private final Set<LEdge> edges = Sets.newLinkedHashSet();
    /**
     * All loop-edges that must be hidden in the graph, as their ports cannot be hidden due to
     * constraints (i.e. fixed side, connection to non-loop-edges). This is a subset of {@code edges}.
     */
    private final Set<LEdge> hiddenEdges = Sets.newLinkedHashSet();
    /** Unconstrained ports only containing outgoing edges of the connected set. */
    private final Set<LPort> exclusiveLoopSourcePorts = Sets.newLinkedHashSet();
    /** Unconstrained ports only containing incoming edges of the connected set. */
    private final Set<LPort> exclusiveLoopTargetPorts = Sets.newLinkedHashSet();
    /** Ports constrained by an non-loop edge to an other node. */
    private final Set<LPort> portsWithNonLoopEdge = Sets.newLinkedHashSet();
    /** Ports constrained by an already defined portSide. */
    private final Set<LPort> portsWithPortSide = Sets.newLinkedHashSet();
    
    /** The maximum text width of this connectedSelfLoop. */
    private double textWidth;
    /** The sum text height of this connectedSelfLoop. */
    private double textHeight;
    /** The loop side of this connected set. */
    private LoopSide loopSide;
    
    /**
     * Constructs a new connectedSelfLoop.
     * 
     * @param node The node all ports must be laying on.
     */
    public ConnectedSelfLoopComponent(final LNode node) {
        owner = node;
    }
    
    /**
     * Adds the given port to the correct set.<ul>
     * <li> if there is an non-loop edge: {@code portsWithNonLoopEdge}</li>      
     * <li> else if port side is already defined: {@code portsWithPortSide}</li>      
     * <li> else if there is no outgoing edge: {@code exclusiveLoopTargetPorts}</li>      
     * <li> else : {@code exclusiveLoopSourcePorts}</li>      
     * @param port The port to add.
     * @return True, if the edge must be hidden, as the port cannot be hidden.
     */
    private boolean addPort(final LPort port) {
        if (hasNonLoopEdge(port)) {
            portsWithNonLoopEdge.add(port);
            return true;
        }
        if (port.getSide() != PortSide.UNDEFINED) {
            portsWithPortSide.add(port);
        }
        if (port.getOutgoingEdges().isEmpty()) {
            exclusiveLoopTargetPorts.add(port);
        } else {
            exclusiveLoopSourcePorts.add(port);
        }
        return false;
    }
    
    /**
     * Adds a new edge and it's ports to the connected set.
     * Hides the edge, if the ports cannot be hidden (i.e. one of the ports has a non-loop edge
     * or the port-order of the node is at least {@code fixedOrder}. 
     * Edges already part of this ConnectedSelfLoop won't be added a second time.
     * 
     * @param edge The edge to add.
     * @param isFixedOrder True, if the order of the node's ports is fixed.
     * @return True, if the edge was added to the connected set.
     */
    public boolean tryAddEdge(final LEdge edge, final boolean isFixedOrder) {
        final LPort source = edge.getSource();
        final LPort target = edge.getTarget();
        boolean edgeMustBeHidden = isFixedOrder;
        
        // only continue, if the edge is not already in the set of edges
        if (edges.add(edge)) {

            if (addPort(source)) {
                edgeMustBeHidden = true;
            }
            if (addPort(target)) {
                edgeMustBeHidden = true;
            }

            // if we cannot hide the ports, we have to hide the edge.
            if (edgeMustBeHidden) {
                edge.getSource().getOutgoingEdges().remove(edge);
                edge.getTarget().getIncomingEdges().remove(edge);
                hiddenEdges.add(edge);
            }
            
            // calculate the new text size of this connectedSelfLoop.
            calculateNewTextSize(edge);
            return true;
        }
        return false;
    }
    
    /**
     * Calculates the new text size of given edge and the resulting changes on text size of whole
     * connectedSelfLoop. Width is maximum of current value and width of all labels of
     * given edge. Height is sum of current value and height of all labels of given edge.
     *  
     * @param edge The edge whose text size shall be calculated.
     */
    private void calculateNewTextSize(final LEdge edge) {
        double edgeMaxTextWidth = 0.0;
        double edgeTextHeight = 0.0;
        
        // calculate width and height
        for (final LLabel label : edge.getLabels()) {
            edgeMaxTextWidth = Math.max(edgeMaxTextWidth, label.getSize().x);
            edgeTextHeight += label.getSize().y; 
        }
        
        // set width and height for label and connectedSelfLoop
        edge.setProperty(InternalProperties.SPLINE_LABEL_SIZE, 
                new KVector(edgeMaxTextWidth, edgeTextHeight));
        if (textWidth < edgeMaxTextWidth) {
            textWidth = edgeMaxTextWidth;
        }
        textHeight += edgeTextHeight;
    }

    /**
     * Checks if the port has an edge to another node than it's owner.
     * 
     * @param port The port to be checked.
     * @return True, if there is a connection to an other node.
     */
    private boolean hasNonLoopEdge(final LPort port) {
        // port has an edge to an other node (outgoing)
        for (final LEdge edge : port.getOutgoingEdges()) {
            if (edge.getTarget().getNode() != owner) {
                return true;
            }
        }
        // port has an edge to an other node (incoming)
        for (final LEdge edge : port.getIncomingEdges()) {
            if (edge.getSource().getNode() != owner) {
                return true;
            }
        }
        // all other cases
        return false;
    }

    /**
     * Return the loop-edges of this connectedSelfLoop. May be modified.
     * 
     * @return The loop-edges.
     */
    public Set<LEdge> getEdges() {
        return edges;
    }
    
    /**
     * Return the loops-edges of this connectedSelfLoop, that are hidden.
     * A loop gets hidden, if it is connected to a constrained port.
     * May be modified.
     * @return All hidden edges.
     */
    public Set<LEdge> getHiddenEdges() {
        return hiddenEdges;
    }
    
    /**
     * Returns all ports that are sorted to the source side. May be modified.
     * 
     * @return All source ports. 
     */
    public Set<LPort> getSourceLoopPorts() {
        return exclusiveLoopSourcePorts; 
    }

    /**
     *  Returns all ports that are sorted to the source side in reversed order. 
     *  This is a copy of the original set.
     *  
     * @return A copy of all source ports in reversed order.
     */
    public Set<LPort> getSourceLoopPortsReversed() {
        final Set<LPort> retVal = Sets.newLinkedHashSet();
        final List<LPort> list = Lists.newArrayList(exclusiveLoopSourcePorts);
        Collections.reverse(list);
        for (final LPort port : list) {
            retVal.add(port);
        }
        return retVal; 
    }
    // TODO: notwendig?
    
    /**
     * Returns all ports that are sorted to the target side. May be modified.
     * 
     * @return All target ports. 
     */
    public Set<LPort> getTargetLoopPorts() {
        return exclusiveLoopTargetPorts;
    }

    /**
     * Returns all ports that are sorted to the target side in reversed order. 
     * This is a copy of the original set.
     * 
     * @return A copy of all target ports in reversed order.
     */
    public Set<LPort> getTargetLoopPortsReversed() {
        final Set<LPort> retVal = Sets.newLinkedHashSet();
        final List<LPort> list = new ArrayList<LPort>(exclusiveLoopTargetPorts);
        Collections.reverse(list);
        for (final LPort port : list) {
            retVal.add(port);
        }
        return retVal; 
    }
    // TODO: notwendig?

    
    /**
     * Returns a new list of all self-loop ports. 
     * First those sorted to the first side, followed by those sorted to the second side.
     * 
     * @return A new list of all self-loop ports.
     */
    public List<LPort> getHidablePorts() {
        final List<LPort> allPorts = Lists.newLinkedList(exclusiveLoopSourcePorts);
        allPorts.addAll(exclusiveLoopTargetPorts);
        allPorts.addAll(portsWithPortSide);
        return allPorts;
    }

    /**
     * Returns the ports of this connectedSelfLoop that are not pure loop ports.
     * 
     * @return The non-loop ports of this ConnectedSelfLoop. 
     */
    public Set<LPort> getNonLoopPorts() {
        return portsWithNonLoopEdge;
    }
    
    /**
     *  Returns a new list of all ports of this component.
     *  
     * @return A new list of all ports of this component.
     */
    public List<LPort> getPorts() {
        final List<LPort> allPorts = getHidablePorts();
        allPorts.addAll(portsWithNonLoopEdge);
        return allPorts;
    }
    
    /**
     * Returns a new set of all ports with a constraint on their portSide.
     * This constraint can be a connection to a non-loop edge or a already defined portSide.
     * 
     * @return A new set of all constraint ports.
     */
    public Set<LPort> getConstrainedPorts() {
        final Set<LPort> constrainedPorts = Sets.newHashSet(getNonLoopPorts());
        constrainedPorts.addAll(portsWithPortSide);
        return constrainedPorts;
    }
    
    /**
     * Returns a set of ports having a defined port side but NO non-loop-edge. 
     * The ports WITH a non-loop-edge are returned by {@link getNonLoopPorts}.
     * May be modified.
     * 
     * @return All ports with a defined port side bot no non-loop-edge.
     */
    public Set<LPort> getPortsWithPortSide() {
        return portsWithPortSide;
    }

    /**
     * Returns the node this connectedSelfLoop is laying on.
     * 
     * @return The node of this component.
     */
    public LNode getNode() {
        return owner;
    }
    
    /**
     * Returns the text width of this connectedSelfLoop.
     * 
     * @return The text width of this connectedSelfLoops.
     */
    public double getTextWidth() {
        return textWidth;
    }

    /**
     * Sets the text width of this connectedSelfLoops.
     * 
     * @param width The new text width.
     */
    public void setTextWidth(final double width) {
        textWidth = width;
    }
    

    /**
     * Returns the text height of this connectedSelfLoop. May be modified.
     * 
     * @return The text height of this connectedSelfLoop.
     */
    public double getTextHeight() {
        return textHeight;
    }
    
    /**
     * Sets the text height of this connectedSelfLoop.
     * 
     * @param height The new text height.
     */
    public void setTextHeight(final double height) {
        textHeight = height;
    }
    
    /**
     * Returns the parent node of this connectedSelfLoop. May be modified.
     * 
     * @return The owner of this connectedSelfLoop.
     */
    public LNode getParent() {
        return owner;
    }
    
    /**
     * Returns the {@link LoopSide} of this connectedSelfLoop. May be modified.
     * 
     * @return The {@link LoopSide} of this connectedSelfLoop.
     */
    public LoopSide getLoopSide() {
        return loopSide;
    }
    
    /**
     * Sets the LoopSide of this connectedSelfLoop and also for all contained loop-edges,
     * if second parameter is true.
     * 
     * @param side The LoopSide to set.
     * @param alsoForEdges Determines of the loopSide shall be set for the loop-edges, too.
     */
    public void setLoopSide(final LoopSide side, final boolean alsoForEdges) {
        loopSide = side;
        if (alsoForEdges) {
            for (final LEdge edge : edges) {
                edge.setProperty(InternalProperties.SPLINE_LOOPSIDE, loopSide);
                edge.getSource().setSide(side.getSourceSide());
                edge.getTarget().setSide(side.getTargetSide());
            }
        }
    }

    /**
     * Unhides all hidden edges by re-adding them to their original ports.
     */
    public void unhideEdges() {
        for (final LEdge edge : hiddenEdges) {
            edge.getSource().getOutgoingEdges().add(edge);
            edge.getTarget().getIncomingEdges().add(edge);
        }
    }
    
    /**
     * Returns a string representation of this ConnectedSelfLoop.
     * 
     * @return A string representation.
     */
    public String toString() {
        final StringBuilder retVal = new StringBuilder();
        retVal.append(this.getLoopSide().toString()).append(": ");
        for (final LPort port : this.getHidablePorts()) {
            retVal.append(port.toString()).append(' ').append(port.getSide().toString()).append(" / ");
        }
        return retVal.substring(0, retVal.length() - 2 - 1);
    }


}
