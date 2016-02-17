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
package org.eclipse.elk.core.util;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.elk.core.klayoutdata.KEdgeLayout;
import org.eclipse.elk.core.klayoutdata.KIdentifier;
import org.eclipse.elk.core.klayoutdata.KInsets;
import org.eclipse.elk.core.klayoutdata.KLayoutData;
import org.eclipse.elk.core.klayoutdata.KLayoutDataFactory;
import org.eclipse.elk.core.klayoutdata.KPoint;
import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.EdgeLabelPlacement;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.options.NodeLabelPlacement;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.core.util.selection.DefaultSelectionIterator;
import org.eclipse.elk.core.util.selection.SelectionIterator;
import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KGraphData;
import org.eclipse.elk.graph.KGraphElement;
import org.eclipse.elk.graph.KGraphFactory;
import org.eclipse.elk.graph.KLabel;
import org.eclipse.elk.graph.KLabeledGraphElement;
import org.eclipse.elk.graph.KNode;
import org.eclipse.elk.graph.KPort;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Utility methods for KGraphs and layout data.
 * 
 * @author msp
 * @author uru
 * @kieler.design proposed by msp
 * @kieler.rating 2009-12-11 proposed yellow msp
 */
public final class ElkUtil {
    
    /**
     * Default minimal width for nodes.
     */
    public static final float DEFAULT_MIN_WIDTH = 20.0f;
    
    /**
     * Default minimal height for nodes.
     */
    public static final float DEFAULT_MIN_HEIGHT = 20.0f;
    

    /**
     * Hidden constructor to avoid instantiation.
     */
    private ElkUtil() {
    }
    

    /**
     * Creates a KNode, initializes some attributes, and returns it.
     * 
     * @return an initialized KNode
     */
    public static KNode createInitializedNode() {
        KNode layoutNode = KGraphFactory.eINSTANCE.createKNode();
        KShapeLayout layout = KLayoutDataFactory.eINSTANCE.createKShapeLayout();
        layout.setInsets(KLayoutDataFactory.eINSTANCE.createKInsets());
        layoutNode.getData().add(layout);
        return layoutNode;
    }

    /**
     * Creates a KEdge, initializes some attributes, and returns it.
     * 
     * @return an initialized KEdge
     */
    public static KEdge createInitializedEdge() {
        KEdge edge = KGraphFactory.eINSTANCE.createKEdge();
        KEdgeLayout edgeLayout = KLayoutDataFactory.eINSTANCE.createKEdgeLayout();
        edgeLayout.setSourcePoint(KLayoutDataFactory.eINSTANCE.createKPoint());
        edgeLayout.setTargetPoint(KLayoutDataFactory.eINSTANCE.createKPoint());
        edge.getData().add(edgeLayout);
        return edge;
    }

    /**
     * Creates a KPort, initializes some attributes, and returns it.
     * 
     * @return an initialized KPort
     */
    public static KPort createInitializedPort() {
        KPort port = KGraphFactory.eINSTANCE.createKPort();
        KShapeLayout portLayout = KLayoutDataFactory.eINSTANCE.createKShapeLayout();
        portLayout.setInsets(KLayoutDataFactory.eINSTANCE.createKInsets());
        port.getData().add(portLayout);
        return port;
    }

    /**
     * Creates a KLabel, initializes some attributes, and returns it.
     * 
     * @param element a labeled graph element
     * @return an initialized KLabel
     */
    public static KLabel createInitializedLabel(final KLabeledGraphElement element) {
        KLabel label = KGraphFactory.eINSTANCE.createKLabel();
        KShapeLayout labelLayout = KLayoutDataFactory.eINSTANCE.createKShapeLayout();
        label.getData().add(labelLayout);
        label.setText("");
        label.setParent(element);
        return label;
    }
    
    /**
     * Ensures that each element contained in the given graph is attributed correctly for
     * usage in KIML. {@link KGraphElement}
     * 
     * @param graph the parent node of a graph 
     */
    public static void validate(final KNode graph) {
        KLayoutDataFactory layoutFactory = KLayoutDataFactory.eINSTANCE;
        
        // construct an iterator that first returns the root node, i.e. 'graph',
        //  and all contained {@link KGraphElement KGraphElements} afterwards
        //  ({@link KGraphData} are omitted for performance reasons)
        Iterator<KGraphElement> contentIter = Iterators.concat(
                Lists.newArrayList(graph).iterator(),
                Iterators.filter(graph.eAllContents(), KGraphElement.class));
        
        // Note that using an iterator and adding elements works here
        //  as the eAllContents() iterator relies on the lists provided by eContents()
        //  of EObjects that, in turn, provides a mirrored list of all contained elements.
        while (contentIter.hasNext()) {
            EObject element = contentIter.next();
            // Make sure nodes are OK
            if (element instanceof KNode) {
                KNode node = (KNode) element;
                KShapeLayout nodeLayout = node.getData(KShapeLayout.class);
                if (nodeLayout == null) {
                    nodeLayout = layoutFactory.createKShapeLayout();                   
                    node.getData().add(nodeLayout);
                } 
                if (nodeLayout.getInsets() == null) {
                    nodeLayout.setInsets(layoutFactory.createKInsets());
                }
            // Make sure ports are OK           
            } else if (element instanceof KPort) {
                KPort port = (KPort) element;
                KShapeLayout portLayout = port.getData(KShapeLayout.class);
                if (portLayout == null) {
                    port.getData().add(layoutFactory.createKShapeLayout());
                }
            // Make sure labels are OK
            } else if (element instanceof KLabel) {
                KLabel label = (KLabel) element;
                KShapeLayout labelLayout = label.getData(KShapeLayout.class);
                if (labelLayout == null) {
                    label.getData().add(layoutFactory.createKShapeLayout());
                }
                if (label.getText() == null) {
                    label.setText("");
                }
            // Make sure edges are OK
            } else if (element instanceof KEdge) {
                KEdge edge = (KEdge) element;
                KEdgeLayout edgeLayout = edge.getData(KEdgeLayout.class);
                if (edgeLayout == null) {
                    edgeLayout = layoutFactory.createKEdgeLayout();
                    edge.getData().add(edgeLayout);
                }
                if (edgeLayout.getSourcePoint() == null) {
                    edgeLayout.setSourcePoint(layoutFactory.createKPoint());
                }
                if (edgeLayout.getTargetPoint() == null) {
                    edgeLayout.setTargetPoint(layoutFactory.createKPoint());
                }
                // ports and edges are not opposite, so check whether they are connected properly
                KPort sourcePort = edge.getSourcePort();
                if (sourcePort != null) {
                    if (!sourcePort.getEdges().contains(edge)) {
                        sourcePort.getEdges().add(edge);
                    }
                }
                KPort targetPort = edge.getTargetPort();
                if (targetPort != null) {
                    if (!targetPort.getEdges().contains(edge)) {
                        targetPort.getEdges().add(edge);
                    }
                }
            }
        }
    }
    
    /**
     * Create a unique identifier for the given graph element. Note that this identifier
     * is not necessarily universally unique, since it uses the hash code, which
     * usually covers only the range of heap space addresses.
     * 
     * @param element a graph element
     */
    public static void createIdentifier(final KGraphElement element) {
        KIdentifier identifier = element.getData(KIdentifier.class);
        if (identifier == null) {
            identifier = KLayoutDataFactory.eINSTANCE.createKIdentifier();
            element.getData().add(identifier);
        }
        identifier.setId(Integer.toString(element.hashCode()));
    }

    /**
     * Determines the port side for the given port from its relative position at
     * its corresponding node.
     * 
     * @param port port to analyze
     * @param direction the overall layout direction
     * @return the port side relative to its containing node
     */
    public static PortSide calcPortSide(final KPort port, final Direction direction) {
        KShapeLayout portLayout = port.getData(KShapeLayout.class);

        // if the node has zero size, we cannot decide anything
        KShapeLayout nodeLayout = port.getNode().getData(KShapeLayout.class);
        float nodeWidth = nodeLayout.getWidth(), nodeHeight = nodeLayout.getHeight();
        if (nodeWidth <= 0 && nodeHeight <= 0) {
            return PortSide.UNDEFINED;
        }

        // check direction-dependent criterion
        float xpos = portLayout.getXpos(), ypos = portLayout.getYpos();
        switch (direction) {
        case LEFT:
        case RIGHT:
            if (xpos < 0) {
                return PortSide.WEST;
            } else if (xpos + portLayout.getWidth() > nodeWidth) {
                return PortSide.EAST;
            }
            break;
        case UP:
        case DOWN:
            if (ypos < 0) {
                return PortSide.NORTH;
            } else if (ypos + portLayout.getHeight() > nodeHeight) {
                return PortSide.SOUTH;
            }
        }
        
        // check general criterion
        float widthPercent = (xpos + portLayout.getWidth() / 2) / nodeWidth;
        float heightPercent = (ypos + portLayout.getHeight() / 2) / nodeHeight;
        if (widthPercent + heightPercent <= 1
                && widthPercent - heightPercent <= 0) {
            // port is on the left
            return PortSide.WEST;
        } else if (widthPercent + heightPercent >= 1
                && widthPercent - heightPercent >= 0) {
            // port is on the right
            return PortSide.EAST;
        } else if (heightPercent < 1.0f / 2) {
            // port is on the top
            return PortSide.NORTH;
        } else {
            // port is on the bottom
            return PortSide.SOUTH;
        }
    }
    
    /**
     * Calculate the offset for a port, that is the amount by which it is moved outside of the node.
     * An offset value of 0 means the port has no intersection with the node and touches the outside
     * border of the node.
     * 
     * @param port a port
     * @param side the side on the node for the given port
     * @return the offset on the side
     */
    public static float calcPortOffset(final KPort port, final PortSide side) {
        KShapeLayout portLayout = port.getData(KShapeLayout.class);
        KShapeLayout nodeLayout = port.getNode().getData(KShapeLayout.class);
        switch (side) {
        case NORTH:
            return -(portLayout.getYpos() + portLayout.getHeight());
        case EAST:
            return portLayout.getXpos() - nodeLayout.getWidth();
        case SOUTH:
            return portLayout.getYpos() - nodeLayout.getHeight();
        case WEST:
            return -(portLayout.getXpos() + portLayout.getWidth());
        }
        return 0;
    }

    /**
     * Sets the size of a given node, depending on the minimal size, the number of ports
     * on each side, the insets, and the label.
     * 
     * @param node the node that shall be resized
     * @return a vector holding the width and height resizing ratio, or {@code null} if the size
     *     constraint is set to {@code FIXED}
     */
    public static KVector resizeNode(final KNode node) {
        KShapeLayout nodeLayout = node.getData(KShapeLayout.class);
        Set<SizeConstraint> sizeConstraint = nodeLayout.getProperty(LayoutOptions.SIZE_CONSTRAINT);
        if (sizeConstraint.isEmpty()) {
            return null;
        }
        
        float newWidth = 0, newHeight = 0;

        if (sizeConstraint.contains(SizeConstraint.PORTS)) {
            PortConstraints portConstraints = nodeLayout.getProperty(LayoutOptions.PORT_CONSTRAINTS);
            float minNorth = 2, minEast = 2, minSouth = 2, minWest = 2;
            Direction direction = node.getParent() == null
                    ? nodeLayout.getProperty(LayoutOptions.DIRECTION)
                    : node.getParent().getData(KShapeLayout.class).getProperty(LayoutOptions.DIRECTION);
            for (KPort port : node.getPorts()) {
                KShapeLayout portLayout = port.getData(KShapeLayout.class);
                PortSide portSide = portLayout.getProperty(LayoutOptions.PORT_SIDE);
                if (portSide == PortSide.UNDEFINED) {
                    portSide = calcPortSide(port, direction);
                    portLayout.setProperty(LayoutOptions.PORT_SIDE, portSide);
                }
                if (portConstraints == PortConstraints.FIXED_POS) {
                    switch (portSide) {
                    case NORTH:
                        minNorth = Math.max(minNorth, portLayout.getXpos()
                                + portLayout.getWidth());
                        break;
                    case EAST:
                        minEast = Math.max(minEast, portLayout.getYpos()
                                + portLayout.getHeight());
                        break;
                    case SOUTH:
                        minSouth = Math.max(minSouth, portLayout.getXpos()
                                + portLayout.getWidth());
                        break;
                    case WEST:
                        minWest = Math.max(minWest, portLayout.getYpos()
                                + portLayout.getHeight());
                        break;
                    }
                } else {
                    switch (portSide) {
                    case NORTH:
                        minNorth += portLayout.getWidth() + 2;
                        break;
                    case EAST:
                        minEast += portLayout.getHeight() + 2;
                        break;
                    case SOUTH:
                        minSouth += portLayout.getWidth() + 2;
                        break;
                    case WEST:
                        minWest += portLayout.getHeight() + 2;
                        break;
                    }
                }
            }
            
            newWidth = Math.max(minNorth, minSouth);
            newHeight = Math.max(minEast, minWest);
        }
        
        return resizeNode(node, newWidth, newHeight, true, true);
    }
    
    /**
     * Resize a node to the given width and height, adjusting port and label positions if needed.
     * 
     * @param node a node
     * @param newWidth the new width to set
     * @param newHeight the new height to set
     * @param movePorts whether port positions should be adjusted
     * @param moveLabels whether label positions should be adjusted
     * @return a vector holding the width and height resizing ratio
     */
    public static KVector resizeNode(final KNode node, final float newWidth, final float newHeight,
            final boolean movePorts, final boolean moveLabels) {
        
        KShapeLayout nodeLayout = node.getData(KShapeLayout.class);
        Set<SizeConstraint> sizeConstraint = nodeLayout.getProperty(LayoutOptions.SIZE_CONSTRAINT);
        
        KVector oldSize = new KVector(nodeLayout.getWidth(), nodeLayout.getHeight());
        KVector newSize;
        
        // Calculate the new size
        if (sizeConstraint.contains(SizeConstraint.MINIMUM_SIZE)) {
            Set<SizeOptions> sizeOptions = nodeLayout.getProperty(LayoutOptions.SIZE_OPTIONS);
            float minWidth = nodeLayout.getProperty(LayoutOptions.MIN_WIDTH);
            float minHeight = nodeLayout.getProperty(LayoutOptions.MIN_HEIGHT);
            
            // If minimum width or height are not set, maybe default to default values
            if (sizeOptions.contains(SizeOptions.DEFAULT_MINIMUM_SIZE)) {
                if (minWidth <= 0) {
                    minWidth = DEFAULT_MIN_WIDTH;
                }
                
                if (minHeight <= 0) {
                    minHeight = DEFAULT_MIN_HEIGHT;
                }
            }
            
            newSize = new KVector(Math.max(newWidth, minWidth), Math.max(newHeight, minHeight));
        } else {
            newSize = new KVector(newWidth, newHeight);
        }
        
        float widthRatio = (float) (newSize.x / oldSize.x);
        float heightRatio = (float) (newSize.y / oldSize.y);
        float widthDiff = (float) (newSize.x - oldSize.x);
        float heightDiff = (float) (newSize.y - oldSize.y);

        // update port positions
        if (movePorts) {
            Direction direction = node.getParent() == null
                    ? nodeLayout.getProperty(LayoutOptions.DIRECTION)
                    : node.getParent().getData(KShapeLayout.class).getProperty(LayoutOptions.DIRECTION);
            boolean fixedPorts =
                    nodeLayout.getProperty(LayoutOptions.PORT_CONSTRAINTS) == PortConstraints.FIXED_POS;
            
            for (KPort port : node.getPorts()) {
                KShapeLayout portLayout = port.getData(KShapeLayout.class);
                PortSide portSide = portLayout.getProperty(LayoutOptions.PORT_SIDE);
                
                if (portSide == PortSide.UNDEFINED) {
                    portSide = calcPortSide(port, direction);
                    portLayout.setProperty(LayoutOptions.PORT_SIDE, portSide);
                }
                
                switch (portSide) {
                case NORTH:
                    if (!fixedPorts) {
                        portLayout.setXpos(portLayout.getXpos() * widthRatio);
                    }
                    break;
                case EAST:
                    portLayout.setXpos(portLayout.getXpos() + widthDiff);
                    if (!fixedPorts) {
                        portLayout.setYpos(portLayout.getYpos() * heightRatio);
                    }
                    break;
                case SOUTH:
                    if (!fixedPorts) {
                        portLayout.setXpos(portLayout.getXpos() * widthRatio);
                    }
                    portLayout.setYpos(portLayout.getYpos() + heightDiff);
                    break;
                case WEST:
                    if (!fixedPorts) {
                        portLayout.setYpos(portLayout.getYpos() * heightRatio);
                    }
                    break;
                }
            }
        }
        
        // resize the node AFTER ports have been placed, since calcPortSide needs the old size
        nodeLayout.setSize((float) newSize.x, (float) newSize.y);
        
        // update label positions
        if (moveLabels) {
            for (KLabel label : node.getLabels()) {
                KShapeLayout labelLayout = label.getData(KShapeLayout.class);
                float midx = labelLayout.getXpos() + labelLayout.getWidth() / 2;
                float midy = labelLayout.getYpos() + labelLayout.getHeight() / 2;
                float widthPercent = midx / (float) oldSize.x;
                float heightPercent = midy / (float) oldSize.y;
                
                if (widthPercent + heightPercent >= 1) {
                    if (widthPercent - heightPercent > 0 && midy >= 0) {
                        // label is on the right
                        labelLayout.setXpos(labelLayout.getXpos() + widthDiff);
                        labelLayout.setYpos(labelLayout.getYpos() + heightDiff * heightPercent);
                    } else if (widthPercent - heightPercent < 0 && midx >= 0) {
                        // label is on the bottom
                        labelLayout.setXpos(labelLayout.getXpos() + widthDiff * widthPercent);
                        labelLayout.setYpos(labelLayout.getYpos() + heightDiff);
                    }
                }
            }
        }
        
        // set fixed size option for the node: now the size is assumed to stay as determined here
        nodeLayout.setProperty(LayoutOptions.SIZE_CONSTRAINT, SizeConstraint.fixed());
        
        return new KVector(widthRatio, heightRatio);
    }

    /**
     * Applies the scaling factor configured in terms of {@link LayoutOptions#SCALE_FACTOR} in its
     * {@link KShapeLayout} to {@code node} 's size data, and updates the layout data of
     * {@code node}'s ports and labels accordingly.<br>
     * <b>Note:</b> The scaled layout data won't be reverted during the layout process, see
     * {@link LayoutOptions#SCALE_FACTOR}.
     * 
     * @author chsch
     * 
     * @param node
     *            the {@link KNode} to be scaled
     */
    public static void applyConfiguredNodeScaling(final KNode node) {
        final KShapeLayout shapeLayout = node.getData(KShapeLayout.class);
        final float scalingFactor = shapeLayout.getProperty(LayoutOptions.SCALE_FACTOR);

        if (scalingFactor == 1f) {
            return;
        }

        shapeLayout.setSize(scalingFactor * shapeLayout.getWidth(),
                scalingFactor * shapeLayout.getHeight());

        for (KGraphElement kge : Iterables.concat(node.getPorts(), node.getLabels())) {
            final KShapeLayout kgeLayout = kge.getData(KShapeLayout.class);

            kgeLayout.setPos(scalingFactor * kgeLayout.getXpos(),
                    scalingFactor * kgeLayout.getYpos());
            kgeLayout.setSize(scalingFactor * kgeLayout.getWidth(),
                    scalingFactor * kgeLayout.getHeight());
            
            final KVector anchor = kgeLayout.getProperty(LayoutOptions.PORT_ANCHOR);
            if (anchor != null) {
                anchor.x *= scalingFactor;
                anchor.y *= scalingFactor;
            }
        }
    }

    /**
     * Determines whether the given child node is a descendant of the parent node. This method does
     * not regard a node as its own descendant.
     * 
     * @param child a child node
     * @param parent a parent node
     * @return {@code true} if {@code child} is a direct or indirect child of {@code parent}.
     */
    public static boolean isDescendant(final KNode child, final KNode parent) {
        KNode current = child;
        while (current.getParent() != null) {
            current = current.getParent();
            if (current == parent) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Converts the given relative point to an absolute location. The insets of the parent node
     * are included in this calculation.
     * 
     * @param point a relative point
     * @param parent the parent node to which the point is relative to
     * @return {@code point} for convenience
     */
    public static KVector toAbsolute(final KVector point, final KNode parent) {
        KNode node = parent;
        while (node != null) {
            KShapeLayout nodeLayout = node.getData(KShapeLayout.class);
            KInsets insets = nodeLayout.getInsets();
            point.add(nodeLayout.getXpos() + insets.getLeft(),
                    nodeLayout.getYpos() + insets.getTop());
            node = node.getParent();
        }
        return point;
    }
    
    /**
     * Converts the given absolute point to a relative location. The insets of the parent node
     * are included in this calculation.
     * 
     * @param point an absolute point
     * @param parent the parent node to which the point shall be made relative to
     * @return {@code point} for convenience
     */
    public static KVector toRelative(final KVector point, final KNode parent) {
        KNode node = parent;
        while (node != null) {
            KShapeLayout nodeLayout = node.getData(KShapeLayout.class);
            KInsets insets = nodeLayout.getInsets();
            point.add(-nodeLayout.getXpos() - insets.getLeft(),
                        -nodeLayout.getYpos() - insets.getTop());
            node = node.getParent();
        }
        return point;
    }
    
    /**
     * Translates the contents of the given node by an offset.
     * 
     * @param parent parent node whose children shall be translated
     * @param xoffset x coordinate offset
     * @param yoffset y coordinate offset
     */
    public static void translate(final KNode parent, final float xoffset, final float yoffset) {
        for (KNode child : parent.getChildren()) {
            // Translate node position
            KShapeLayout nodeLayout = child.getData(KShapeLayout.class);
            nodeLayout.setXpos(nodeLayout.getXpos() + xoffset);
            nodeLayout.setYpos(nodeLayout.getYpos() + yoffset);
            
            // Translate edge bend points
            for (KEdge edge : child.getOutgoingEdges()) {
                // If the target of this edge is a descendent of the current child, its bend points are
                // relative to the child's position and thus don't need to be translated
                if (!isDescendant(edge.getTarget(), child)) {
                    // Translate edge bend points
                    KEdgeLayout edgeLayout = edge.getData(KEdgeLayout.class);
                    translate(edgeLayout.getSourcePoint(), xoffset, yoffset);
                    for (KPoint bendPoint : edgeLayout.getBendPoints()) {
                        translate(bendPoint, xoffset, yoffset);
                    }
                    translate(edgeLayout.getTargetPoint(), xoffset, yoffset);
                    
                    // Translate edge label positions
                    for (KLabel edgeLabel : edge.getLabels()) {
                        KShapeLayout labelLayout = edgeLabel.getData(KShapeLayout.class);
                        labelLayout.setXpos(labelLayout.getXpos() + xoffset);
                        labelLayout.setYpos(labelLayout.getYpos() + yoffset);
                    }
                }
            }
        }
    }
    
    /**
     * Translates the given point by an offset.
     * 
     * @param point point that shall be translated
     * @param xoffset x coordinate offset
     * @param yoffset y coordinate offset
     */
    public static void translate(final KPoint point, final float xoffset, final float yoffset) {
        point.setX(point.getX() + xoffset);
        point.setY(point.getY() + yoffset);
    }

    /**
     * Persists all KGraphData elements of a KGraph by serializing the contained properties into
     * {@link org.eclipse.elk.graph.PersistentEntry} tuples.
     *
     * @param graph the root element of the graph to persist elements of.
     */
    public static void persistDataElements(final KNode graph) {
        TreeIterator<EObject> iterator = graph.eAllContents();
        while (iterator.hasNext()) {
            EObject eObject = iterator.next();
            if (eObject instanceof KGraphData) {
                ((KGraphData) eObject).makePersistent();
            }
        }
    }
    
    /**
     * Determine the junction points of the given edge. This is done by comparing the bend points
     * of the given edge with the bend points of all other edges that are connected to the same
     * source port or the same target port.
     * 
     * @param edge an edge
     * @return a list of junction points
     */
    public static KVectorChain determineJunctionPoints(final KEdge edge) {
        KVectorChain junctionPoints = new KVectorChain();
        Map<KEdge, KVector[]> pointsMap = Maps.newHashMap();
        pointsMap.put(edge, getPoints(edge));
        
        // for each connected port p
        List<KPort> connectedPorts = Lists.newArrayListWithCapacity(2);
        if (edge.getSourcePort() != null) {
            connectedPorts.add(edge.getSourcePort());
        }
        if (edge.getTargetPort() != null) {
            connectedPorts.add(edge.getTargetPort());
        }
        for (KPort p : connectedPorts) {
            
            // let allConnectedEdges be the set of edges connected to p without the main edge
            List<KEdge> allConnectedEdges = Lists.newLinkedList();
            allConnectedEdges.addAll(p.getEdges());
            allConnectedEdges.remove(edge);
            if (!allConnectedEdges.isEmpty()) {
                KVector[] thisPoints = pointsMap.get(edge);
                boolean reverse;
                
                // let p1 be the start point
                KVector p1;
                if (p == edge.getTargetPort()) {
                    p1 = thisPoints[thisPoints.length - 1];
                    reverse = true;
                } else {
                    p1 = thisPoints[0];
                    reverse = false;
                }
                
                // for all bend points of this connection
                for (int i = 1; i < thisPoints.length; i++) {
                    // let p2 be the next bend point on this connection
                    KVector p2;
                    if (reverse) {
                        p2 = thisPoints[thisPoints.length - 1 - i];
                    } else {
                        p2 = thisPoints[i];
                    }
                    
                    // for all other connections that are still on the same track as this one
                    Iterator<KEdge> allEdgeIter = allConnectedEdges.iterator();
                    while (allEdgeIter.hasNext()) {
                        KEdge otherEdge = allEdgeIter.next();
                        KVector[] otherPoints = pointsMap.get(otherEdge);
                        if (otherPoints == null) {
                            otherPoints = getPoints(otherEdge);
                            pointsMap.put(otherEdge, otherPoints);
                        }
                        if (otherPoints.length <= i) {
                            allEdgeIter.remove();
                        } else {
                            
                            // let p3 be the next bend point of the other connection
                            KVector p3;
                            if (reverse) {
                                p3 = otherPoints[otherPoints.length - 1 - i];
                            } else {
                                p3 = otherPoints[i];
                            }
                            if (p2.x != p3.x || p2.y != p3.y) {
                                // the next point of this and the other connection differ
                                double dx2 = p2.x - p1.x;
                                double dy2 = p2.y - p1.y;
                                double dx3 = p3.x - p1.x;
                                double dy3 = p3.y - p1.y;
                                if ((dx3 * dy2) == (dy3 * dx2)
                                        && signum(dx2) == signum(dx3)
                                        && signum(dy2) == signum(dy3)) {
                                    
                                    // the points p1, p2, p3 form a straight line,
                                    // now check whether p2 is between p1 and p3
                                    if (Math.abs(dx2) < Math.abs(dx3)
                                            || Math.abs(dy2) < Math.abs(dy3)) {
                                        junctionPoints.add(p2);
                                    }
                                    
                                } else if (i > 1) {
                                    // p2 and p3 have diverged, so the last common point is p1
                                    junctionPoints.add(p1);
                                }

                                // do not consider the other connection in the next iterations
                                allEdgeIter.remove();
                            }
                        }
                    }
                    // for the next iteration p2 is taken as reference point
                    p1 = p2;
                }
            }
        }
        return junctionPoints;
    }
    
    /**
     * Returns the signum function of the specified <tt>double</tt> value. The return value
     * is -1 if the specified value is negative; 0 if the specified value is zero; and 1 if the
     * specified value is positive.
     *
     * @return the signum function of the specified <tt>double</tt> value.
     */
    private static int signum(final double x) {
        if (x < 0) {
            return -1;
        }
        if (x > 0) {
            return 1;
        }
        return 0;
    }
    
    /**
     * Get the edge points as an array of vectors.
     * 
     * @param edge an edge
     * @return an array with all edge points
     */
    private static KVector[] getPoints(final KEdge edge) {
        KEdgeLayout edgeLayout = edge.getData(KEdgeLayout.class);
        int n = edgeLayout.getBendPoints().size() + 2;
        KVector[] points = new KVector[n];
        points[0] = edgeLayout.getSourcePoint().createVector();
        ListIterator<KPoint> pointIter = edgeLayout.getBendPoints().listIterator();
        while (pointIter.hasNext()) {
            KPoint bendPoint = pointIter.next();
            points[pointIter.nextIndex()] = bendPoint.createVector();
        }
        points[n - 1] = edgeLayout.getTargetPoint().createVector();
        return points;
    }

    /**
     * Determines the {@link KEdge KEdges} that are (transitively) connected to the given
     * {@code kedges} across hierarchy boundaries via common ports. See
     * {@link #getConnectedEdges(KEdge)} for details.
     *
     * @see #getConnectedEdges(KEdge)
     * @param kedges
     *            an {@link Iterable} of {@link KEdge KEdges} that shall be checked
     * @return an {@link Iterator} visiting the given {@code kedges} and all (transitively)
     *         connected ones.
     * @deprecated Use {@link #getConnectedElements(KEdge, SelectionIterator, SelectionIterator)} in
     *             combination with {@link DefaultSelectionIterator}
     */
    public static Iterator<KEdge> getConnectedEdges(final Iterable<KEdge> kedges) {
        return Iterators.concat(
                Iterators.transform(kedges.iterator(), new Function<KEdge, Iterator<KEdge>>() {

            public Iterator<KEdge> apply(final KEdge kedge) {
                return getConnectedEdges(kedge);
            }
        }));
    }

    /**
     * Determines the {@link KEdge KEdges} that are (transitively) connected to {@code kedge} across
     * hierarchy boundaries via common ports. Rational: Multiple {@link KEdge KEdges} that are
     * pairwise connected by means of a {@link KPort} (target port of edge a == source port of edge
     * b or vice versa) may form one logical connection. This kind splitting might be already
     * present in the view model, or is performed by the layout algorithm for decomposing a nested
     * layout input graph into flat sub graphs.
     *
     * @param kedge
     *            the {@link KEdge} check for connected edges
     * @return an {@link Iterator} visiting the given {@code kedge} and all connected edges in a(n
     *         almost) breadth first search fashion
     * @deprecated Use {@link #getConnectedElements(KEdge, SelectionIterator, SelectionIterator)} in
     *             combination with {@link DefaultSelectionIterator}
     */
    public static Iterator<KEdge> getConnectedEdges(final KEdge kedge) {
        // Default behavior should be to not select the ports
        return Iterators.filter(getConnectedElements(kedge, false), KEdge.class);
    }
    
    /**
     * Determines the {@link KGraphElement KGraphElements} that are (transitively) connected to
     * {@code kedge} across hierarchy boundaries via common ports. Rational: Multiple {@link KEdge
     * KEdges} that are pairwise connected by means of a {@link KPort} (target port of edge a ==
     * source port of edge b or vice versa) may form one logical connection. This kind splitting
     * might be already present in the view model, or is performed by the layout algorithm for
     * decomposing a nested layout input graph into flat sub graphs.
     * This version allows to also include ports in the selection.
     *
     * @param kedge
     *            the {@link KEdge} check for connected elements
     * @param addPorts
     *            flag to determine, whether ports should be added to the selection or not
     * @return an {@link Iterator} visiting the given {@code kedge} and all connected edges in a(n
     *         almost) breadth first search fashion
     * @deprecated Use {@link #getConnectedElements(KEdge, SelectionIterator, SelectionIterator)} in
     *             combination with {@link DefaultSelectionIterator}
     */
    public static Iterator<KGraphElement> getConnectedElements(final KEdge kedge,
            final boolean addPorts) {

        final SelectionIterator sourceSideIt = new DefaultSelectionIterator(kedge, addPorts, false);
        final SelectionIterator targetSideIt = new DefaultSelectionIterator(kedge, addPorts, true);

        return getConnectedElements(kedge, sourceSideIt, targetSideIt);
    }
    
    /**
     * Determines the {@link KEdge KEdges} that are (transitively) connected to {@code kedge} across
     * hierarchy boundaries via common ports. Rational: Multiple {@link KEdge KEdges} that are
     * pairwise connected by means of a {@link KPort} (target port of edge a == source port of edge
     * b or vice versa) may form one logical connection. This kind splitting might be already
     * present in the view model, or is performed by the layout algorithm for decomposing a nested
     * layout input graph into flat sub graphs.
     *
     * @param kedge
     *            the {@link KEdge} check for connected elements
     * @param sourceIterator
     *            the {@link SelectionIterator} to be used for iterating towards the tail of the
     *            selected edge
     * @param targetIterator
     *            the {@link SelectionIterator} to be used for iterating towards the head of the
     *            selected edge
     * @return an {@link Iterator} visiting the given {@code kedge} and all connected elements
     *         determined by the {@link SelectionIterator SelectionIterators}
     */
    public static Iterator<KGraphElement> getConnectedElements(final KEdge kedge,
            final SelectionIterator sourceIterator, final SelectionIterator targetIterator) {

        // get a singleton iterator offering 'kedge'
        final Iterator<KGraphElement> kedgeIt = Iterators.singletonIterator((KGraphElement) kedge);
        // Keep a set of visited elements for the tree iterators
        final Set<KPort> visited = Sets.newHashSet();

        // Grab source iterator if edge has a source
        final SelectionIterator sourceSideIt =
                kedge.getSourcePort() == null ? null : sourceIterator;
        if (sourceSideIt != null) {
            // Configure the iterator
            sourceSideIt.attachVisitedSet(visited);
        }

        // Grab target iterator if edge has a target
        final SelectionIterator targetSideIt =
                kedge.getTargetPort() == null ? null : targetIterator;
        if (targetSideIt != null) {
            // Configure the iterator
            targetSideIt.attachVisitedSet(visited);
        }

        // concatenate the source-sidewise and target-sidewise iterators if present ...
        final Iterator<KGraphElement> connectedEdges =
                sourceSideIt == null ? targetSideIt : targetSideIt == null ? sourceSideIt
                        : Iterators.concat(sourceSideIt, targetSideIt);

        // ... and attach them to the input 'kedge' offering iterator, or return just the
        // input 'kedge' iterator in case no ports are configured for 'kedge'
        return connectedEdges == null ? kedgeIt : Iterators.concat(kedgeIt, connectedEdges);
    }
    
    /**
     * Recursively configures default values for all child elements of the passed graph. This
     * includes node, ports, and edges.
     * 
     * Note that it is <b>not</b> checked whether the defaults flag is set on the graph.
     * 
     * @param graph
     *            the graph to configure.
     */
    public static void configureDefaultsRecursively(final KNode graph) {

        Iterator<KGraphElement> kgeIt =
                Iterators.filter(graph.eAllContents(), KGraphElement.class);
        while (kgeIt.hasNext()) {
            KGraphElement kge = kgeIt.next();
            if (kge instanceof KNode) {
                configureWithDefaultValues((KNode) kge);
            } else if (kge instanceof KPort) {
                configureWithDefaultValues((KPort) kge);
            } else if (kge instanceof KEdge) {
                configureWithDefaultValues((KEdge) kge);
            }
        }
    }
    /**
     * Adds some default values to the passed node. This includes a reasonable size, a label based
     * on the node's {@link KIdentifier} and a inside center node label placement.
     * 
     * Such default values are useful for fast test case generation.
     * 
     * @param node
     *            a node of a graph
     */
    public static void configureWithDefaultValues(final KNode node) {
       
        // Make sure the node has a size if the size constraints are fixed
        KShapeLayout sl = node.getData(KShapeLayout.class);
        if (sl != null) {
            Set<SizeConstraint> sc = sl.getProperty(LayoutOptions.SIZE_CONSTRAINT);
            
            if (sc.equals(SizeConstraint.fixed()) && sl.getWidth() == 0f && sl.getHeight() == 0f) {
                sl.setWidth(DEFAULT_MIN_WIDTH * 2 * 2);
                sl.setHeight(DEFAULT_MIN_HEIGHT * 2 * 2);
            }
        } 
        
        // label
        ensureLabel(node);
        if (sl != null) {
            Set<NodeLabelPlacement> nlp = sl.getProperty(LayoutOptions.NODE_LABEL_PLACEMENT);
            if (nlp.equals(NodeLabelPlacement.fixed())) {
                sl.setProperty(LayoutOptions.NODE_LABEL_PLACEMENT, NodeLabelPlacement.insideCenter());
            }
        }
        
    }
    
    /**
     * Adds some default values to the passed port. This includes a reasonable size 
     * and a label based on the port's {@link KIdentifier}.
     * 
     * Such default values are useful for fast test case generation.
     * 
     * @param port
     *            a port of a node of a graph
     */
    public static void configureWithDefaultValues(final KPort port) {

        KShapeLayout sl = port.getData(KShapeLayout.class);
        if (sl != null && sl.getWidth() == 0f && sl.getHeight() == 0f) {
            sl.setWidth(DEFAULT_MIN_WIDTH / 2 / 2);
            sl.setHeight(DEFAULT_MIN_HEIGHT / 2 / 2);
        }
        
        ensureLabel(port);
    }
    
    /**
     * Configures the {@link EdgeLabelPlacement} of the passed edge to be center of the edge.
     * 
     * @param edge
     *            an edge of a graph
     */
    public static void configureWithDefaultValues(final KEdge edge) {

        KLayoutData ld = edge.getData(KLayoutData.class);
        if (ld != null) {
            EdgeLabelPlacement elp = ld.getProperty(LayoutOptions.EDGE_LABEL_PLACEMENT);
            if (elp == EdgeLabelPlacement.UNDEFINED) {
                ld.setProperty(LayoutOptions.EDGE_LABEL_PLACEMENT, EdgeLabelPlacement.CENTER);
            }
        }
    }
    
    /**
     * If the element does not already own a label, a label is created based on the element's
     * {@link KIdentifier} (if it exists, that is).
     */
    private static void ensureLabel(final KLabeledGraphElement klge) {
        if (klge.getLabels().isEmpty()) {

            KIdentifier id = klge.getData(KIdentifier.class);
            if (id != null && !Strings.isNullOrEmpty(id.getId())) {
                KLabel label = createInitializedLabel(klge);
                label.setText(id.getId());
            }
        }
    }
    
    /**
     * Apply the given graph element visitors to the content of the given graph.
     */
    public static void applyVisitors(final KNode graph, final IGraphElementVisitor... visitors) {
        for (int i = 0; i < visitors.length; i++) {
            visitors[i].visit(graph);
        }
        Iterator<KGraphElement> allElements = Iterators.filter(graph.eAllContents(), KGraphElement.class);
        while (allElements.hasNext()) {
            KGraphElement element = allElements.next();
            for (int i = 0; i < visitors.length; i++) {
                visitors[i].visit(element);
            }
        }
    }
    
}