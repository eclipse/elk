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
import java.util.ListIterator;
import java.util.Set;

import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.EdgeLabelPlacement;
import org.eclipse.elk.core.options.NodeLabelPlacement;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.core.util.selection.DefaultSelectionIterator;
import org.eclipse.elk.core.util.selection.SelectionIterator;
import org.eclipse.elk.graph.EMapPropertyHolder;
import org.eclipse.elk.graph.ElkBendPoint;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkGraphElement;
import org.eclipse.elk.graph.ElkGraphFactory;
import org.eclipse.elk.graph.ElkLabel;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.ElkPort;
import org.eclipse.elk.graph.ElkShape;
import org.eclipse.elk.graph.util.ElkGraphUtil;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
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
     * Create a unique identifier for the given graph element. Note that this identifier
     * is not necessarily universally unique, since it uses the hash code, which
     * usually covers only the range of heap space addresses.
     * 
     * @param element a graph element
     */
    public static void createIdentifier(final ElkGraphElement element) {
        element.setIdentifier(Integer.toString(element.hashCode()));
    }

    /**
     * Determines the port side for the given port from its relative position at
     * its corresponding node.
     * 
     * @param port port to analyze
     * @param direction the overall layout direction
     * @return the port side relative to its containing node
     * @throws IllegalStateException if the port does not have a parent node.
     */
    public static PortSide calcPortSide(final ElkPort port, final Direction direction) {
        if (port.getParent() == null) {
            throw new IllegalStateException("port must have a parent node to calculate the port side");
        }
        
        // if the node has zero size, we cannot decide anything
        ElkNode node = port.getParent();
        double nodeWidth = node.getWidth(), nodeHeight = node.getHeight();
        if (nodeWidth <= 0 && nodeHeight <= 0) {
            return PortSide.UNDEFINED;
        }

        // check direction-dependent criterion
        double xpos = port.getX(), ypos = port.getY();
        switch (direction) {
        case LEFT:
        case RIGHT:
            if (xpos < 0) {
                return PortSide.WEST;
            } else if (xpos + port.getWidth() > nodeWidth) {
                return PortSide.EAST;
            }
            break;
        case UP:
        case DOWN:
            if (ypos < 0) {
                return PortSide.NORTH;
            } else if (ypos + port.getHeight() > nodeHeight) {
                return PortSide.SOUTH;
            }
        }
        
        // check general criterion
        double widthPercent = (xpos + port.getWidth() / 2) / nodeWidth;
        double heightPercent = (ypos + port.getHeight() / 2) / nodeHeight;
        if (widthPercent + heightPercent <= 1 && widthPercent - heightPercent <= 0) {
            // port is on the left
            return PortSide.WEST;
        } else if (widthPercent + heightPercent >= 1 && widthPercent - heightPercent >= 0) {
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
     * @throws IllegalStateException if the port does not have a parent node.
     */
    public static double calcPortOffset(final ElkPort port, final PortSide side) {
        if (port.getParent() == null) {
            throw new IllegalStateException("port must have a parent node to calculate the port side");
        }
        
        ElkNode node = port.getParent();
        switch (side) {
        case NORTH:
            return -(port.getY() + port.getHeight());
        case EAST:
            return port.getX() - node.getWidth();
        case SOUTH:
            return port.getY() - node.getHeight();
        case WEST:
            return -(port.getX() + port.getWidth());
        }
        return 0;
    }

    /**
     * Sets the size of a given node, depending on the minimal size, the number of ports
     * on each side and the label.
     * 
     * @param node the node that shall be resized
     * @return a vector holding the width and height resizing ratio, or {@code null} if the size
     *     constraint is set to {@code FIXED}
     */
    public static KVector resizeNode(final ElkNode node) {
        Set<SizeConstraint> sizeConstraint = node.getProperty(CoreOptions.NODE_SIZE_CONSTRAINTS);
        if (sizeConstraint.isEmpty()) {
            return null;
        }
        
        double newWidth = 0, newHeight = 0;

        if (sizeConstraint.contains(SizeConstraint.PORTS)) {
            PortConstraints portConstraints = node.getProperty(CoreOptions.PORT_CONSTRAINTS);
            double minNorth = 2, minEast = 2, minSouth = 2, minWest = 2;
            Direction direction = node.getParent() == null
                    ? node.getProperty(CoreOptions.DIRECTION)
                    : node.getParent().getProperty(CoreOptions.DIRECTION);
            for (ElkPort port : node.getPorts()) {
                PortSide portSide = port.getProperty(CoreOptions.PORT_SIDE);
                if (portSide == PortSide.UNDEFINED) {
                    portSide = calcPortSide(port, direction);
                    port.setProperty(CoreOptions.PORT_SIDE, portSide);
                }
                if (portConstraints == PortConstraints.FIXED_POS) {
                    switch (portSide) {
                    case NORTH:
                        minNorth = Math.max(minNorth, port.getX() + port.getWidth());
                        break;
                    case EAST:
                        minEast = Math.max(minEast, port.getY() + port.getHeight());
                        break;
                    case SOUTH:
                        minSouth = Math.max(minSouth, port.getX() + port.getWidth());
                        break;
                    case WEST:
                        minWest = Math.max(minWest, port.getY() + port.getHeight());
                        break;
                    }
                } else {
                    switch (portSide) {
                    case NORTH:
                        minNorth += port.getWidth() + 2;
                        break;
                    case EAST:
                        minEast += port.getHeight() + 2;
                        break;
                    case SOUTH:
                        minSouth += port.getWidth() + 2;
                        break;
                    case WEST:
                        minWest += port.getHeight() + 2;
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
    public static KVector resizeNode(final ElkNode node, final double newWidth, final double newHeight,
            final boolean movePorts, final boolean moveLabels) {
        
        Set<SizeConstraint> sizeConstraint = node.getProperty(CoreOptions.NODE_SIZE_CONSTRAINTS);
        
        KVector oldSize = new KVector(node.getWidth(), node.getHeight());
        KVector newSize;
        
        // Calculate the new size
        if (sizeConstraint.contains(SizeConstraint.MINIMUM_SIZE)) {
            Set<SizeOptions> sizeOptions = node.getProperty(CoreOptions.NODE_SIZE_OPTIONS);
            KVector minSize = node.getProperty(CoreOptions.NODE_SIZE_MINIMUM);
            double minWidth, minHeight;
            if (minSize == null) {
                minWidth = node.getProperty(CoreOptions.NODE_SIZE_MIN_WIDTH);
                minHeight = node.getProperty(CoreOptions.NODE_SIZE_MIN_HEIGHT);
            } else {
                minWidth = minSize.x;
                minHeight = minSize.y; 
            }
            
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
        
        double widthRatio = newSize.x / oldSize.x;
        double heightRatio = newSize.y / oldSize.y;
        double widthDiff = newSize.x - oldSize.x;
        double heightDiff = newSize.y - oldSize.y;

        // update port positions
        if (movePorts) {
            Direction direction = node.getParent() == null
                    ? node.getProperty(CoreOptions.DIRECTION)
                    : node.getParent().getProperty(CoreOptions.DIRECTION);
            boolean fixedPorts = node.getProperty(CoreOptions.PORT_CONSTRAINTS) == PortConstraints.FIXED_POS;
            
            for (ElkPort port : node.getPorts()) {
                PortSide portSide = port.getProperty(CoreOptions.PORT_SIDE);
                
                if (portSide == PortSide.UNDEFINED) {
                    portSide = calcPortSide(port, direction);
                    port.setProperty(CoreOptions.PORT_SIDE, portSide);
                }
                
                switch (portSide) {
                case NORTH:
                    if (!fixedPorts) {
                        port.setX(port.getX() * widthRatio);
                    }
                    break;
                case EAST:
                    port.setX(port.getX() + widthDiff);
                    if (!fixedPorts) {
                        port.setY(port.getY() * heightRatio);
                    }
                    break;
                case SOUTH:
                    if (!fixedPorts) {
                        port.setX(port.getX() * widthRatio);
                    }
                    port.setY(port.getY() + heightDiff);
                    break;
                case WEST:
                    if (!fixedPorts) {
                        port.setY(port.getY() * heightRatio);
                    }
                    break;
                }
            }
        }
        
        // resize the node AFTER ports have been placed, since calcPortSide needs the old size
        node.setDimensions(newSize.x, newSize.y);
        
        // update label positions
        if (moveLabels) {
            for (ElkLabel label : node.getLabels()) {
                double midx = label.getX() + label.getWidth() / 2;
                double midy = label.getY() + label.getHeight() / 2;
                double widthPercent = midx / oldSize.x;
                double heightPercent = midy / oldSize.y;
                
                if (widthPercent + heightPercent >= 1) {
                    if (widthPercent - heightPercent > 0 && midy >= 0) {
                        // label is on the right
                        label.setX(label.getX() + widthDiff);
                        label.setY(label.getY() + heightDiff * heightPercent);
                    } else if (widthPercent - heightPercent < 0 && midx >= 0) {
                        // label is on the bottom
                        label.setX(label.getX() + widthDiff * widthPercent);
                        label.setY(label.getY() + heightDiff);
                    }
                }
            }
        }
        
        // set fixed size option for the node: now the size is assumed to stay as determined here
        node.setProperty(CoreOptions.NODE_SIZE_CONSTRAINTS, SizeConstraint.fixed());
        
        return new KVector(widthRatio, heightRatio);
    }

    /**
     * Applies the scaling factor configured in terms of {@link CoreOptions#SCALE_FACTOR} to {@code node}'s
     * size data, and updates the layout data of {@code node}'s ports and labels accordingly.<br>
     * <b>Note:</b> The scaled layout data won't be reverted during the layout process, see
     * {@link CoreOptions#SCALE_FACTOR}.
     * 
     * @param node
     *            the node to be scaled
     */
    public static void applyConfiguredNodeScaling(final ElkNode node) {
        final float scalingFactor = node.getProperty(CoreOptions.SCALE_FACTOR);
        if (scalingFactor == 1f) {
            return;
        }

        node.setDimensions(scalingFactor * node.getWidth(), scalingFactor * node.getHeight());

        for (ElkShape shape : Iterables.concat(node.getPorts(), node.getLabels())) {
            shape.setLocation(scalingFactor * shape.getX(), scalingFactor * shape.getY());
            shape.setDimensions(scalingFactor * shape.getWidth(), scalingFactor * shape.getHeight());
            
            final KVector anchor = shape.getProperty(CoreOptions.PORT_ANCHOR);
            if (anchor != null) {
                anchor.x *= scalingFactor;
                anchor.y *= scalingFactor;
            }
        }
    }
    
    /**
     * Converts the given relative point to an absolute location.
     * 
     * @param point a relative point
     * @param parent the parent node to which the point is relative to
     * @return {@code point} for convenience
     */
    public static KVector toAbsolute(final KVector point, final ElkNode parent) {
        ElkNode node = parent;
        while (node != null) {
            point.add(node.getX(), node.getY());
            node = node.getParent();
        }
        return point;
    }
    
    /**
     * Converts the given absolute point to a relative location.
     * 
     * @param point an absolute point
     * @param parent the parent node to which the point shall be made relative to
     * @return {@code point} for convenience
     */
    public static KVector toRelative(final KVector point, final ElkNode parent) {
        ElkNode node = parent;
        while (node != null) {
            point.add(-node.getX(), -node.getY());
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
    public static void translate(final ElkNode parent, final double xoffset, final double yoffset) {
        for (ElkNode child : parent.getChildren()) {
            // Translate node position
            child.setLocation(child.getX() + xoffset, child.getY() + yoffset);
            
            // Translate edge bend points
            // MIGRATE This used to be outgoing edges; check if it makes sense to translate all contained edges instead
            for (ElkEdge edge : child.getContainedEdges()) {
                for (ElkEdgeSection section : edge.getSections()) {
                    translate(section, xoffset, yoffset);
                }
                
                // Translate edge label positions
                for (ElkLabel edgeLabel : edge.getLabels()) {
                    edgeLabel.setLocation(edgeLabel.getX() + xoffset, edgeLabel.getY() + yoffset);
                }
            }
        }
    }
    
    /**
     * Translates the given edge section by an offset.
     * 
     * @param section edge section that shall be translated
     * @param xoffset x coordinate offset
     * @param yoffset y coordinate offset
     */
    public static void translate(final ElkEdgeSection section, final double xoffset, final double yoffset) {
        // Translate source point
        section.setStartLocation(section.getStartX() + xoffset, section.getStartY() + yoffset);
        
        // Translate bend points
        for (ElkBendPoint bendPoint : section.getBendPoints()) {
            bendPoint.set(bendPoint.getX() + xoffset, bendPoint.getY() + yoffset);
        }
        
        // Translate target point
        section.setEndLocation(section.getEndX() + xoffset, section.getEndY() + yoffset);
    }

    /**
     * Persists all KGraphData elements of a KGraph by serializing the contained properties into
     * {@link org.eclipse.elk.graph.PersistentEntry} tuples.
     *
     * @param graph the root element of the graph to persist elements of.
     */
    public static void persistDataElements(final ElkNode graph) {
        TreeIterator<EObject> iterator = graph.eAllContents();
        while (iterator.hasNext()) {
            EObject eObject = iterator.next();
            if (eObject instanceof EMapPropertyHolder) {
                ((EMapPropertyHolder) eObject).makePersistent();
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
    public static KVectorChain determineJunctionPoints(final ElkEdge edge) {
        // MIGRATE This method will have to take different coordinate systems resulting from different edge containments into account
        
        KVectorChain junctionPoints = new KVectorChain();
        
//        Map<KEdge, KVector[]> pointsMap = Maps.newHashMap();
//        pointsMap.put(edge, getPoints(edge));
//        
//        // for each connected port p
//        List<KPort> connectedPorts = Lists.newArrayListWithCapacity(2);
//        if (edge.getSourcePort() != null) {
//            connectedPorts.add(edge.getSourcePort());
//        }
//        if (edge.getTargetPort() != null) {
//            connectedPorts.add(edge.getTargetPort());
//        }
//        for (KPort p : connectedPorts) {
//            
//            // let allConnectedEdges be the set of edges connected to p without the main edge
//            List<KEdge> allConnectedEdges = Lists.newLinkedList();
//            allConnectedEdges.addAll(p.getEdges());
//            allConnectedEdges.remove(edge);
//            if (!allConnectedEdges.isEmpty()) {
//                KVector[] thisPoints = pointsMap.get(edge);
//                boolean reverse;
//                
//                // let p1 be the start point
//                KVector p1;
//                if (p == edge.getTargetPort()) {
//                    p1 = thisPoints[thisPoints.length - 1];
//                    reverse = true;
//                } else {
//                    p1 = thisPoints[0];
//                    reverse = false;
//                }
//                
//                // for all bend points of this connection
//                for (int i = 1; i < thisPoints.length; i++) {
//                    // let p2 be the next bend point on this connection
//                    KVector p2;
//                    if (reverse) {
//                        p2 = thisPoints[thisPoints.length - 1 - i];
//                    } else {
//                        p2 = thisPoints[i];
//                    }
//                    
//                    // for all other connections that are still on the same track as this one
//                    Iterator<KEdge> allEdgeIter = allConnectedEdges.iterator();
//                    while (allEdgeIter.hasNext()) {
//                        KEdge otherEdge = allEdgeIter.next();
//                        KVector[] otherPoints = pointsMap.get(otherEdge);
//                        if (otherPoints == null) {
//                            otherPoints = getPoints(otherEdge);
//                            pointsMap.put(otherEdge, otherPoints);
//                        }
//                        if (otherPoints.length <= i) {
//                            allEdgeIter.remove();
//                        } else {
//                            
//                            // let p3 be the next bend point of the other connection
//                            KVector p3;
//                            if (reverse) {
//                                p3 = otherPoints[otherPoints.length - 1 - i];
//                            } else {
//                                p3 = otherPoints[i];
//                            }
//                            if (p2.x != p3.x || p2.y != p3.y) {
//                                // the next point of this and the other connection differ
//                                double dx2 = p2.x - p1.x;
//                                double dy2 = p2.y - p1.y;
//                                double dx3 = p3.x - p1.x;
//                                double dy3 = p3.y - p1.y;
//                                if ((dx3 * dy2) == (dy3 * dx2)
//                                        && signum(dx2) == signum(dx3)
//                                        && signum(dy2) == signum(dy3)) {
//                                    
//                                    // the points p1, p2, p3 form a straight line,
//                                    // now check whether p2 is between p1 and p3
//                                    if (Math.abs(dx2) < Math.abs(dx3)
//                                            || Math.abs(dy2) < Math.abs(dy3)) {
//                                        junctionPoints.add(p2);
//                                    }
//                                    
//                                } else if (i > 1) {
//                                    // p2 and p3 have diverged, so the last common point is p1
//                                    junctionPoints.add(p1);
//                                }
//
//                                // do not consider the other connection in the next iterations
//                                allEdgeIter.remove();
//                            }
//                        }
//                    }
//                    // for the next iteration p2 is taken as reference point
//                    p1 = p2;
//                }
//            }
//        }
        
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
     * Get the edge points as an array of vectors. Note that this method requires the edge to have exactly one
     * edge section.
     * 
     * @param edge an edge
     * @return an array with all edge points
     * @throws IllegalArgumentException if the edge has no or more than one edge section.
     */
    private static KVector[] getPoints(final ElkEdge edge) {
        if (edge.getSections().size() != 1) {
            throw new IllegalArgumentException("The edge needs to have exactly one edge section. Found: "
                    + edge.getSections().size());
        }
        
        ElkEdgeSection section = edge.getSections().get(0);
        
        int n = section.getBendPoints().size() + 2;
        KVector[] points = new KVector[n];
        
        // Source point
        points[0] = new KVector(section.getStartX(), section.getStartY());
        
        // Bend points
        ListIterator<ElkBendPoint> pointIter = section.getBendPoints().listIterator();
        while (pointIter.hasNext()) {
            ElkBendPoint bendPoint = pointIter.next();
            points[pointIter.nextIndex()] = new KVector(bendPoint.getX(), bendPoint.getY());
        }
        
        // Target point
        points[n - 1] = new KVector(section.getEndX(), section.getEndY());
        
        return points;
    }

    /**
     * Determines the edges that are (transitively) connected to the given edges across hierarchy boundaries
     * via common ports. See {@link #getConnectedEdges(ElkEdge)} for details.
     *
     * @see #getConnectedEdges(ElkEdge)
     * @param edges
     *            an {@link Iterable} of edges that shall be checked
     * @return an {@link Iterator} visiting the given edges and all (transitively) connected ones.
     * @deprecated Use {@link #getConnectedElements(ElkEdge, SelectionIterator, SelectionIterator)} in
     *             combination with {@link DefaultSelectionIterator}
     */
    public static Iterator<ElkEdge> getConnectedEdges(final Iterable<ElkEdge> edges) {
        return Iterators.concat(Iterators.transform(edges.iterator(), new Function<ElkEdge, Iterator<ElkEdge>>() {
            public Iterator<ElkEdge> apply(final ElkEdge kedge) {
                return getConnectedEdges(kedge);
            }
        }));
    }

    /**
     * Determines the edges that are (transitively) connected to the given edge across
     * hierarchy boundaries via common ports. Rational: Multiple edges that are
     * pairwise connected by means of an {@link ElkPort} (target port of edge a == source port of edge
     * b or vice versa) may form one logical connection. This kind of splitting might be already
     * present in the view model, or is performed by the layout algorithm for decomposing a nested
     * layout input graph into flat sub graphs.
     *
     * @param edge
     *            the edge check for connected edges
     * @return an {@link Iterator} visiting the given edge and all connected edges in a(n
     *         almost) breadth first search fashion
     * @deprecated Use {@link #getConnectedElements(ElkEdge, SelectionIterator, SelectionIterator)} in
     *             combination with {@link DefaultSelectionIterator}
     */
    public static Iterator<ElkEdge> getConnectedEdges(final ElkEdge edge) {
        // Default behavior should be to not select the ports
        return Iterators.filter(getConnectedElements(edge, false), ElkEdge.class);
    }
    
    /**
     * Determines the {@link ElkGraphElement ElkGraphElements} that are (transitively) connected to
     * {@code kedge} across hierarchy boundaries via common ports. Rational: Multiple {@link ElkEdge
     * ElkEdges} that are pairwise connected by means of an {@link ElkPort} (target port of edge a ==
     * source port of edge b or vice versa) may form one logical connection. This kind of splitting
     * might be already present in the view model, or is performed by the layout algorithm for
     * decomposing a nested layout input graph into flat sub graphs.
     * This version allows to also include ports in the selection.
     *
     * @param kedge
     *            the edge to check for connected elements
     * @param addPorts
     *            flag to determine, whether ports should be added to the selection or not
     * @return an {@link Iterator} visiting the given {@code edge} and all connected edges in a(n
     *         almost) breadth first search fashion
     * @deprecated Use {@link #getConnectedElements(ElkEdge, SelectionIterator, SelectionIterator)} in
     *             combination with {@link DefaultSelectionIterator}
     */
    public static Iterator<ElkGraphElement> getConnectedElements(final ElkEdge edge, final boolean addPorts) {
        final SelectionIterator sourceSideIt = new DefaultSelectionIterator(edge, addPorts, false);
        final SelectionIterator targetSideIt = new DefaultSelectionIterator(edge, addPorts, true);

        return getConnectedElements(edge, sourceSideIt, targetSideIt);
    }
    
    /**
     * Determines the {@link ElkEdge ElkEdges} that are (transitively) connected to {@code edge} across
     * hierarchy boundaries via common ports. Rational: Multiple {@link ElkEdge ElkEdges} that are
     * pairwise connected by means of na {@link ElkPort} (target port of edge a == source port of edge
     * b or vice versa) may form one logical connection. This kind of splitting might be already
     * present in the view model, or is performed by the layout algorithm for decomposing a nested
     * layout input graph into flat sub graphs.
     *
     * @param edge
     *            the {@link ElkEdge} check for connected elements
     * @param sourceIterator
     *            the {@link SelectionIterator} to be used for iterating towards the tail of the
     *            selected edge
     * @param targetIterator
     *            the {@link SelectionIterator} to be used for iterating towards the head of the
     *            selected edge
     * @return an {@link Iterator} visiting the given {@code edge} and all connected elements
     *         determined by the {@link SelectionIterator SelectionIterators}
     */
    public static Iterator<ElkGraphElement> getConnectedElements(final ElkEdge edge,
            final SelectionIterator sourceIterator, final SelectionIterator targetIterator) {

        // get a singleton iterator offering 'edge'
        final Iterator<ElkGraphElement> kedgeIt = Iterators.singletonIterator(edge);
        
        // Keep a set of visited elements for the tree iterators
        final Set<ElkPort> visited = Sets.newHashSet();

        // Grab source iterator if edge has a source
        final SelectionIterator sourceSideIt =
                !edge.getSources().isEmpty() ? null : sourceIterator;
        if (sourceSideIt != null) {
            // Configure the iterator
            sourceSideIt.attachVisitedSet(visited);
        }

        // Grab target iterator if edge has a target
        final SelectionIterator targetSideIt =
                !edge.getTargets().isEmpty() ? null : targetIterator;
        if (targetSideIt != null) {
            // Configure the iterator
            targetSideIt.attachVisitedSet(visited);
        }

        // concatenate the source-sidewise and target-sidewise iterators if present ...
        final Iterator<ElkGraphElement> connectedEdges =
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
    public static void configureDefaultsRecursively(final ElkNode graph) {
        Iterator<ElkGraphElement> kgeIt = Iterators.filter(graph.eAllContents(), ElkGraphElement.class);
        while (kgeIt.hasNext()) {
            ElkGraphElement kge = kgeIt.next();
            if (kge instanceof ElkNode) {
                configureWithDefaultValues((ElkNode) kge);
            } else if (kge instanceof ElkPort) {
                configureWithDefaultValues((ElkPort) kge);
            } else if (kge instanceof ElkEdge) {
                configureWithDefaultValues((ElkEdge) kge);
            }
        }
    }
    
    /**
     * Adds some default values to the passed node. This includes a reasonable size, a label based
     * on the node's identifier and a inside center node label placement.
     * 
     * Such default values are useful for fast test case generation.
     * 
     * @param node
     *            a node of a graph
     */
    public static void configureWithDefaultValues(final ElkNode node) {
        // Make sure the node has a size if the size constraints are fixed
        Set<SizeConstraint> sc = node.getProperty(CoreOptions.NODE_SIZE_CONSTRAINTS);
        
        if (sc.equals(SizeConstraint.fixed()) && node.getWidth() == 0f && node.getHeight() == 0f) {
            node.setWidth(DEFAULT_MIN_WIDTH * 2 * 2);
            node.setHeight(DEFAULT_MIN_HEIGHT * 2 * 2);
        }
        
        // label
        ensureLabel(node);
        
        Set<NodeLabelPlacement> nlp = node.getProperty(CoreOptions.NODE_LABELS_PLACEMENT);
        if (nlp.equals(NodeLabelPlacement.fixed())) {
            node.setProperty(CoreOptions.NODE_LABELS_PLACEMENT, NodeLabelPlacement.insideCenter());
        }
        
    }
    
    /**
     * Adds some default values to the passed port. This includes a reasonable size 
     * and a label based on the port's identifier.
     * 
     * Such default values are useful for fast test case generation.
     * 
     * @param port
     *            a port of a node of a graph
     */
    public static void configureWithDefaultValues(final ElkPort port) {
        if (port.getWidth() == 0 && port.getHeight() == 0) {
            port.setWidth(DEFAULT_MIN_WIDTH / 2 / 2);
            port.setHeight(DEFAULT_MIN_HEIGHT / 2 / 2);
        }
        
        ensureLabel(port);
    }
    
    /**
     * Configures the {@link EdgeLabelPlacement} of the passed edge to be center of the edge.
     * 
     * @param edge
     *            an edge of a graph
     */
    public static void configureWithDefaultValues(final ElkEdge edge) {
        EdgeLabelPlacement elp = edge.getProperty(CoreOptions.EDGE_LABELS_PLACEMENT);
        if (elp == EdgeLabelPlacement.UNDEFINED) {
            edge.setProperty(CoreOptions.EDGE_LABELS_PLACEMENT, EdgeLabelPlacement.CENTER);
        }
    }
    
    /**
     * If the element does not already own a label, a label is created based on the element's
     * identifier.
     */
    private static void ensureLabel(final ElkGraphElement klge) {
        if (klge.getLabels().isEmpty()) {
            if (!Strings.isNullOrEmpty(klge.getIdentifier())) {
                ElkLabel label = ElkGraphUtil.createLabel(klge);
                label.setText(klge.getIdentifier());
            }
        }
    }
    
    /**
     * Print information on the given graph element to the given string builder.
     */
    public static void printElementPath(final ElkGraphElement element, final StringBuilder builder) {
        if (element.eContainer() instanceof ElkGraphElement) {
            printElementPath((ElkGraphElement) element.eContainer(), builder);
            builder.append(" > ");
        } else {
            builder.append("Root ");
        }
        
        String className = element.eClass().getName();
        if (className.startsWith("Elk")) {
            // CHECKSTYLEOFF MagicNumber
            builder.append(className.substring(3));
            // CHECKSTYLEON MagicNumber
        } else {
            builder.append(className);
        }
        
        if (element instanceof ElkLabel) {
            String text = ((ElkLabel) element).getText();
            if (!Strings.isNullOrEmpty(text)) {
                builder.append(' ').append(text);
            }
        } else  if (element instanceof ElkGraphElement) {
            ElkGraphElement labeledElement = (ElkGraphElement) element;
            if (!labeledElement.getLabels().isEmpty()) {
                ElkLabel firstLabel = labeledElement.getLabels().get(0);
                String text = firstLabel.getText();
                if (!Strings.isNullOrEmpty(text)) {
                    builder.append(' ').append(text);
                }
            }
        }
    }
    
    /**
     * Applies the vector chain's vectors to the given edge section. The first and the last point of the vector chain
     * are used as the section's new source and start point, respectively. The remaining points become the section's
     * new bend points. The method tries to reuse as many bend points as possible instead of wiping all bend points
     * out and creating new ones.
     * 
     * @param vectorChain the vector chain to apply.
     * @param section the edge section to apply the chain to.
     * @throws IllegalArgumentException if the vector chain contains less than two vectors.
     */
    public static void applyVectorChain(final KVectorChain vectorChain, final ElkEdgeSection section) {
        // We need at least a start and an end point
        if (vectorChain.size() < 2) {
            throw new IllegalArgumentException("The vector chain must contain at least a source and a target point.");
        }
        
        // Start point
        KVector firstPoint = vectorChain.getFirst();
        section.setStartLocation(firstPoint.x, firstPoint.y);
        
        // Reuse as many existing bend points as possible
        ListIterator<ElkBendPoint> oldPointIter = section.getBendPoints().listIterator();
        ListIterator<KVector> newPointIter = vectorChain.listIterator(1);
        
        while (newPointIter.nextIndex() < vectorChain.size() - 1) {
            KVector nextPoint = newPointIter.next();
            ElkBendPoint bendpoint;
            if (oldPointIter.hasNext()) {
                bendpoint = oldPointIter.next();
            } else {
                bendpoint = ElkGraphFactory.eINSTANCE.createElkBendPoint();
                oldPointIter.add(bendpoint);
            }
            
            bendpoint.set(nextPoint.x, nextPoint.y);
        }
        
        // Remove existing bend points that we did not use
        while (oldPointIter.hasNext()) {
            oldPointIter.next();
            oldPointIter.remove();
        }
        
        // End point
        KVector lastPoint = vectorChain.getLast();
        section.setEndLocation(lastPoint.x, lastPoint.y);
    }
    
    /**
     * Creates a vector chain containing the start point, bend points, and end point of the given edge section. Note
     * that modifying the vector chain will be of no consequence to the edge section.
     * 
     * @param edgeSection 
     *            the edge section to initialize the vector chain with.
     * @return the vector chain.
     */
    public static KVectorChain createVectorChain(final ElkEdgeSection edgeSection) {
        KVectorChain chain = new KVectorChain();
        
        chain.add(new KVector(edgeSection.getStartX(), edgeSection.getStartY()));
        
        for (ElkBendPoint bendPoint : edgeSection.getBendPoints()) {
            chain.add(new KVector(bendPoint.getX(), bendPoint.getY()));
        }
        
        chain.add(new KVector(edgeSection.getEndX(), edgeSection.getEndY()));
        
        return chain;
    }
    
}