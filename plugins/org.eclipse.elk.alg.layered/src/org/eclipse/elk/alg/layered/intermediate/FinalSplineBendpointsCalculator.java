/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.intermediate.compaction.HorizontalGraphCompactor;
import org.eclipse.elk.alg.layered.options.GraphCompactionStrategy;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.alg.layered.p5edges.splines.NubSpline;
import org.eclipse.elk.alg.layered.p5edges.splines.SplineEdgeRouter;
import org.eclipse.elk.alg.layered.p5edges.splines.SplineSegment;
import org.eclipse.elk.alg.layered.p5edges.splines.SplineSegment.EdgeInformation;
import org.eclipse.elk.alg.layered.p5edges.splines.SplinesMath;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Intermediate processor that runs some time after the edge routing phase and turns tentative spline routes 
 * into concrete bezier control points that become the bendpoints of {@link LEdge}s. 
 * 
 * <dt>Precondition:</dt>
 *    <dd>a layered graph</dd>
 *    <dd>the {@link SplineEdgeRouter} did his job</dd>
 *    <dd>no dummy nodes</dd>
 *  <dt>Postcondition:</dt>
 *    <dd>the bendpoints of edges can be interpreted as Bezier splines</dd>
 *  <dt>Slots:</dt>
 *    <dd>After phase 5.</dd>
 *  <dt>Same-slot dependencies:</dt><dd>{@link LongEdgeJoiner}</dd>
 *                                   <dd>{@link LabelDummyRemover}</dd>
 *                                   <dd>{@link HorizontalGraphCompactor}</dd>
 */
public class FinalSplineBendpointsCalculator implements ILayoutProcessor<LGraph> {

    private double hEdgeSpacing;
    private boolean sloppyRoutingRequested;
    private GraphCompactionStrategy compactionStrategy;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void process(final LGraph graph, final IElkProgressMonitor progressMonitor) {

        this.hEdgeSpacing = graph.getProperty(LayeredOptions.SPACING_EDGE_EDGE_BETWEEN_LAYERS);
        this.sloppyRoutingRequested = graph.getProperty(LayeredOptions.EDGE_ROUTING_SLOPPY_SPLINE_ROUTING);
        this.compactionStrategy = graph.getProperty(LayeredOptions.COMPACTION_POST_COMPACTION_STRATEGY);
        
        List<LEdge> startEdges = graph.getLayers().stream()
            .flatMap(l -> l.getNodes().stream())
            .flatMap(n -> StreamSupport.stream(n.getOutgoingEdges().spliterator(), false))
            .filter(e -> !e.isSelfLoop())
            .filter(e -> e.hasProperty(InternalProperties.SPLINE_ROUTE_START))
            .collect(Collectors.toList());

        // first determine the nubs
        for (LEdge e : startEdges) {
            List<SplineSegment> spline = e.getProperty(InternalProperties.SPLINE_ROUTE_START);
            for (SplineSegment segment : spline) {
                if (segment.isStraight) {
                    calculateNUBSBendPointStraight(segment);
                } else {
                    calculateNUBSBendPoints(segment);
                }
            }
            e.setProperty(InternalProperties.SPLINE_ROUTE_START, null);
        }
        
        // ... then convert them to bezier splines
        for (LEdge e : startEdges) {
            List<LEdge> edgeChain = e.getProperty(InternalProperties.SPLINE_EDGE_CHAIN);
            calculateBezierBendPoints(edgeChain);
            // clear property
            e.setProperty(InternalProperties.SPLINE_EDGE_CHAIN, null);
        }
        
    }
    
    /**
     * Adds a single bendPoint to a straight edge.
     * 
     * @param segment The segment, whose edges shall be processed.
     * @param startPos The start x position of current between layer gap.
     */
    private void calculateNUBSBendPointStraight(final SplineSegment segment) {
        final Set<LEdge> edges = segment.edges;
        if (edges.size() > 1) {
            throw new IllegalArgumentException("In straight spline segments there may be only one edge.");
        }

        double xStartPos = segment.boundingBox.x;
        double xEndPos = segment.boundingBox.x + segment.boundingBox.width;
        edges.iterator().next().getBendPoints()
                .add(new KVector(xStartPos + (xEndPos - xStartPos) / 2, segment.centerControlPointY));
    }
    
    /**
     * Calculates the bend-points of the edges of given {@link SplineSegment}.
     * If the target node of an {@link LEdge} that is part of the segment is NOT 
     * {@link NodeType#NORMAL}, NubSpline control-points are added to the edge, 
     * as it still has successors. 
     * If finally the end node is reached, the mapping preceedingEdge is used to find the whole edge 
     * through the graph. All NubSpline control-points are taken from the edges and the final bezier 
     * bend-points are calculated and added the the last edge part.
     * 
     * @param segment
     *            The segment, whose edges shall be processed.
     */
    private void calculateNUBSBendPoints(final SplineSegment segment) {
        double startXPos = segment.boundingBox.x;
        double endXPos = segment.boundingBox.x + segment.boundingBox.width;
        
        boolean sloppyRouting = sloppyRoutingRequested && segmentAllowsSloppyRouting(segment);
        
        // the center position is the same for all edges but depends on sloppiness of the routing
        final double centerXPos = sloppyRouting ? (startXPos + endXPos) / 2
                : startXPos + (segment.rank + 1) * hEdgeSpacing;
       
        for (final LEdge edge : segment.edges) {
            
            // Remember that the edge is not necessarily valid at this point (may have been remove by long edge joiner)
            EdgeInformation ei = segment.edgeInformation.get(edge);
            
            double ySourceAnchor = ei.startY;
            double yTargetAnchor = ei.endY;
            
            final KVector sourceVerticalCP = 
                    new KVector(centerXPos, ySourceAnchor);
            final KVector targetVerticalCP = 
                    new KVector(centerXPos, yTargetAnchor);

            // Calculate bend points to draw inner layer segments straight
            // to prevent intersections with big nodes
            // TODO why - gap?
            final KVector sourceStraightCP =
                    new KVector(startXPos - SplineEdgeRouter.NODE_TO_VERTICAL_SEGMENT_GAP, ySourceAnchor);
            final KVector targetStraightCP = new KVector(endXPos, yTargetAnchor);

            boolean invertedSource = false;
            boolean invertedTarget = false;

            // Modify straight CPs to handle the in-layer segments 
            // originating from inverted edges.
//            if (targetAnchor.x >= endXPos && sourceAnchor.x >= endXPos) {
            if (ei.invertedLeft) {
                // Inner layer connection on the right layer
                invertedSource = true;
                sourceStraightCP.x = endXPos;
            }
//            if (targetAnchor.x <= startXPos && sourceAnchor.x <= startXPos) {
            if (ei.invertedRight) {
                // Inner layer connection on the right layer
                invertedTarget = true;
                targetStraightCP.x = startXPos - SplineEdgeRouter.NODE_TO_VERTICAL_SEGMENT_GAP;
            }

            // If using sloppy routing, only some bendpoints are needed at the start and end
            // First determine if this part of an edge is connected to a normal node
//            final boolean normalSource = SplineEdgeRouter.isNormalNode(edge.getSource().getNode());
//            final boolean normalTarget = SplineEdgeRouter.isNormalNode(edge.getTarget().getNode());
            final boolean normalSource = ei.normalSourceNode;
            final boolean normalTarget = ei.normalTargetNode;
            
            if (!sloppyRouting || !(normalSource || normalTarget) || invertedTarget || invertedSource) {
                // add the NubSpline control points to the edge, but in revered order!
                if (segment.edges.size() == 1) {
                    // Special handling of single edges. They don't need a center CP.
                    edge.getBendPoints().addAll(sourceStraightCP, sourceVerticalCP, targetVerticalCP, 
                            targetStraightCP);
                } else {
                    final KVector center = new KVector(centerXPos, segment.centerControlPointY);                    
                    edge.getBendPoints().addAll(sourceStraightCP, sourceVerticalCP, center, 
                            targetVerticalCP, targetStraightCP);
                }
            } else {
                if (normalSource && normalTarget) {
                    // If using sloppy routing we want to use a central y point which is just halfway
                    // between the nodes. We add a small offset to create a curved line between the nodes.
                    // The side of the offset is calculated depending on the difference of 
                    // target indegree and source outdegree.
                    int indegree = 0;
                    int outdegree = 0;
                    // Count all the incoming and outgoing edges
                    for (LPort port : edge.getTarget().getNode().getPorts()) {
                        indegree += port.getIncomingEdges().size();
                    }
                    for (LPort port : edge.getSource().getNode().getPorts()) {
                        outdegree += port.getOutgoingEdges().size();
                    }
                    final int degreeDiff = (int) Math.signum(outdegree - indegree);
                    final double centerYPos = ((yTargetAnchor + ySourceAnchor) / 2)
                            + (yTargetAnchor - ySourceAnchor) * (0.4 * degreeDiff);
                    
                    edge.getBendPoints().addAll(new KVector(centerXPos, centerYPos));
                } else if (normalSource) {
                    // If leaving a normal source, add the straight part of the target dummy.
                    // This mostly prevents intersections with big nodes in the same layer as the target 
                    edge.getBendPoints().addAll(targetVerticalCP, targetStraightCP);
                } else if (normalTarget) {
                    // If entering a normal source, keep the straight part of the source dummy.
                    edge.getBendPoints().addAll(sourceStraightCP, sourceVerticalCP);
                }
            }
        }
    }
    
    /**
     * Collects all bend-points defined for any edge in a chain of edges and converts them to bezier
     * bend-points.
     *   
     * @param edge The last edge of the chain of edges we want the bezier CPs to be calculated for. 
     * @param succeedingEdge A mapping pointing from an edge to it's successor.
     */
    private void calculateBezierBendPoints(final List<LEdge> edgeChain) {
        if (edgeChain.isEmpty()) {
            return;
        }
        
        boolean sloppyRouting = sloppyRoutingRequested;
        
        // in this chain we will put all NURBS control points.
        final KVectorChain allCP = new KVectorChain();
        // We will temporarily store north- or south-bendpoints here.
        KVector northSouthBendPoint = null;
        
        LEdge edge = edgeChain.get(0);
        ///////////////////////////////////////
        // Process the source end of the edge-chain. 
        LPort sourcePort = edge.getSource();
        final NodeType sourceNodeType = sourcePort.getNode().getType();
        
        // edge must be the first edge of a chain of edges
        if (!SplineEdgeRouter.isQualifiedAsStartingNode(sourcePort.getNode())) {
            throw new IllegalArgumentException(
                    "The target node of the edge must be a normal node " + "or a northSouthPort.");
        }
        
        // Calculate the NubSpline bend-point for a north or south port and reroute the edge.
        if (sourceNodeType == NodeType.NORTH_SOUTH_PORT) {
            final LPort originPort = (LPort) sourcePort.getProperty(InternalProperties.ORIGIN);
            northSouthBendPoint = new KVector(
                    originPort.getAbsoluteAnchor().x, 
                    sourcePort.getAbsoluteAnchor().y);
            sourcePort = originPort;
        }

        // add the source as the very first CP.
        allCP.addLast(sourcePort.getAbsoluteAnchor());

        double gap;
        KVector offsetOfStraightening;
        if (!sloppyRouting) {
            // Add a control-point for a straight segment at the very start of an edge-chain to prevent
            // the edge from colliding with self-loops or the like inside the margin of the node. This also
            // ensures the correct initial direction of the edge.
            // This is only performed if the splines should be thoroughly routed.
            gap = Math.max(SplineEdgeRouter.NODE_TO_STRAIGHTENING_CP_GAP, 
                    SplinesMath.getMarginOnPortSide(sourcePort.getNode(), sourcePort.getSide()));
            offsetOfStraightening = 
                    new KVector(SplinesMath.portSideToDirection(sourcePort.getSide()));
            offsetOfStraightening.scale(gap);
            allCP.add(offsetOfStraightening.add(sourcePort.getAbsoluteAnchor()));
        }
        // Add the calculated north/south port bend-point, if there is one.
        if (northSouthBendPoint != null) {
            allCP.addLast(northSouthBendPoint);
            northSouthBendPoint = null;
        }
        
        ///////////////////////////////////////
        // Process the inner segments.
        KVector lastCP = null;
        boolean addMidPoint = false;
        
        Iterator<LEdge> edgeIterator = edgeChain.iterator();
        while (edgeIterator.hasNext()) {
            LEdge currentEdge = edgeIterator.next(); 
            // read the stored bend-points for vertical segments, calculated by calculateNUBSBendPoint.
            final KVectorChain currentBendPoints = currentEdge.getBendPoints();

            if (!currentBendPoints.isEmpty()) {
                // add a CP in the middle of the straight segment between two vertical segments to
                // get a more straight horizontal segment
                if (addMidPoint) {
                    allCP.add(lastCP.add(currentBendPoints.getFirst()).scale(SplineEdgeRouter.ONE_HALF));
                    addMidPoint = false;
                } else {
                    addMidPoint = true;
                }
                lastCP = currentBendPoints.getLast().clone();
                allCP.addAll(currentBendPoints);
                currentBendPoints.clear();
            }
        }

        ///////////////////////////////////////
        // Process the end of the chain of edges.
        // LPort targetPort = lastEdge.getTarget();
        LPort targetPort = edge.getTarget();
        
        // Calculate and add a NubSpline bend-point for a north or south port and reroute the edge.
        if (targetPort.getNode().getType() == NodeType.NORTH_SOUTH_PORT) {
            final LPort originPort = (LPort) targetPort.getProperty(InternalProperties.ORIGIN);
            allCP.add(new KVector(
                    originPort.getAbsoluteAnchor().x, 
                    targetPort.getAbsoluteAnchor().y));
            targetPort = originPort;
        }
        
        if (!sloppyRouting) {
            // Add a control-point for a straight segment at the very start of an edge-chain to prevent
            // the edge from colliding with self-loops or the like inside the margin of the node. This also
            // ensures the correct final direction of the edge.
            // This is only performed if the splines should be thoroughly routed.
            gap = Math.max(SplineEdgeRouter.NODE_TO_STRAIGHTENING_CP_GAP,
                    SplinesMath.getMarginOnPortSide(targetPort.getNode(), targetPort.getSide()));
            offsetOfStraightening = new KVector(SplinesMath.portSideToDirection(targetPort.getSide()));
            offsetOfStraightening.scale(gap);
            allCP.add(offsetOfStraightening.add(targetPort.getAbsoluteAnchor()));
        }
        
        // Add the targetPort as a NubSpline bend-point.
        allCP.addLast(targetPort.getAbsoluteAnchor());

        ///////////////////////////////////////
        // convert list of control points to bezier bend points.
        // create the NubSpline for the control-points
        final NubSpline nubSpline = new NubSpline(true, SplineEdgeRouter.DIMENSION, allCP);
        
        // Calculate the bezier CP and set them as the bend-points (without source and target vector). 
        edge.getBendPoints().addAll(nubSpline.getBezierCP());
    }
    
    private boolean segmentAllowsSloppyRouting(final SplineSegment segment) {
        
        // only check this if one dimensional compaction is applied
        if (compactionStrategy == GraphCompactionStrategy.NONE) {
            return true;
        }
        
        double startXPos = segment.boundingBox.x;
        double endXPos = segment.boundingBox.x + segment.boundingBox.width;

        if (segment.initialSegment) {
            LNode n = segment.sourceNode;
            double t = segmentNodeDistanceThreshold(n);
            double nodeSegmentDistance = startXPos - (n.getPosition().x + n.getSize().x); 
            if (nodeSegmentDistance > t) {
                return false;
            }
        }
        if (segment.lastSegment) {
            LNode n = segment.targetNode;
            double t = segmentNodeDistanceThreshold(n);
            double nodeSegmentDistance = n.getPosition().x - endXPos;
            if (nodeSegmentDistance > t) {
                return false;
            }
        }

        return true;
    }
    
    /**
     * @return a magically determined threshold value.  
     */
    private double segmentNodeDistanceThreshold(final LNode n) {
        return n.getLayer().getSize().x - n.getSize().x / 2;
    }
}
