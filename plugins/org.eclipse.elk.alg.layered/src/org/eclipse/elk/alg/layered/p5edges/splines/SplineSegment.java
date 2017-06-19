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
package org.eclipse.elk.alg.layered.p5edges.splines;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.intermediate.FinalSplineBendpointsCalculator;
import org.eclipse.elk.alg.layered.intermediate.LongEdgeSplitter;
import org.eclipse.elk.alg.layered.p5edges.splines.SplineEdgeRouter.Dependency;
import org.eclipse.elk.alg.layered.p5edges.splines.SplineEdgeRouter.SideToProcess;
import org.eclipse.elk.core.math.ElkRectangle;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Represents a segment of a spline that is to be created. A segment represents one or more {@link LEdge}s between
 * adjacent layers. In the case that more than one edge is represented, the segments represents a part of a hyperedge.
 * 
 * Since a segment represents {@link LEdge}(s) between adjacent layers, it may represent {@link LEdge}(s) that were
 * introduced during the splitting of long edges (see {@link LongEdgeSplitter}). The control points for the final spline
 * are created by the {@link FinalSplineBendpointsCalculator}, which runs after long edges have been joined again.
 * Consequently, not all {@link LEdge}s exist anymore. Any information that is required from these edges to create final
 * control points is stored in an accompanying {@link EdgeInformation} class.
 * 
 * <h3>Note on Hyperedges</h3> Splines only support 1:n directed hyperedges. Also all ports on the n-side must lay on
 * the same SideToProcess, looking from the single port. So in a left to right routing, it is not possible to create a
 * hyperedge from an edge going upwards and an edge going downwards.
 * 
 * Furthermore only hyperedges are considered for vertical alignment of edges. All other edges are assumed to go
 * straight to the target port.
 * 
 */
public final class SplineSegment implements Comparable<SplineSegment> {
    
    // For convenient usage, members are public
    // SUPPRESS CHECKSTYLE NEXT 48 VisibilityModifier
    /** A sets of ports, determining the ports left of the hyper-edge. */  
    public final Set<LPort> leftPorts = Sets.newHashSet();
    /** A sets of ports, determining the ports right of the hyper-edge. */  
    public final Set<LPort> rightPorts = Sets.newHashSet();
    /** Outgoing dependencies are pointing to hyper-edges that must lay right of this hyper edge. */
    public final List<Dependency> outgoing = Lists.newArrayList();
    /** Incoming dependencies are pointing to hyper-edges that must lay left of this hyper edge. */
    public final List<Dependency> incoming = Lists.newArrayList();

    /** Used to mark nodes in the cycle breaker. */
    public int mark;
    /** Determines how many elements are depending on this. */
    public int inweight;
    /** Determines how many elements this element is depending on. */
    public int outweight;
    /** the rank determines the horizontal distance to the preceding layer. */
    public int rank;
    
    /** A set of all LEdges that are combined in this hyper-edge. */
    public final Set<LEdge> edges = Sets.newHashSet();
    
    /** If true, the hyper-edge has no vertical segment, connecting two ports by a vertical edge. */
    public final boolean isStraight;
    
    /** An imaginary bounding box in which this spline segment should reside. */
    public ElkRectangle boundingBox = new ElkRectangle();
    
    /** If this segment represents a hyperedge, this is the top y coordinate of the segment that is used during
     *  dependency graph creation for minimize edge crossings of vertical segments. */
    public double hyperEdgeTopYPos;
    /** If this segment represents a hyperedge, this is the bottom y coordinate of the segment that is used during
     *  dependency graph creation for minimize edge crossings of vertical segments. */
    public double hyperEdgeBottomYPos;
    /** If this segment is non-straight, this is the center control point of the spline route to be created. */ 
    public double centerControlPointY;
    
    /** Flag indicating if this segment is the initial segment of a spline. */
    public boolean initialSegment = false;
    /** Flag indicating if this segment is the last segment of a spline. */
    public boolean lastSegment = false;
    /** If this segment is the {@link SplineSegment#initialSegment initial segment} of a spline, this field holds
     * the source node of the spline. Otherwise it is {@code null}. */
    public LNode sourceNode;
    /** If this segment is the {@link SplineSegment#lastSegment last segment} of a spline, this field holds
     * the target node of the spline. Otherwise it is {@code null}. */
    public LNode targetNode;
    
    /**
     * A segment represents an {@link LEdge} between adjacent layers. Thus, it may represent an {@link LEdge}
     * that was introduced during the splitting of long edges (see {@link LongEdgeSplitter}). The final spline 
     * 
     */
    public class EdgeInformation {
        // SUPPRESS CHECKSTYLE NEXT 40 VisibilityModifier|Javadoc
        public double startY;
        public double endY;
        
        public boolean normalSourceNode;
        public boolean normalTargetNode;
        
        public boolean invertedLeft;
        public boolean invertedRight;
    }
    
    /** Map holding for each {@link LEdge} information that was valid during the edge routing phase. */
    public Map<LEdge, EdgeInformation> edgeInformation = Maps.newHashMap();
    
    /**
     * Constructor for a 1:n hyper-edge.
     * 
     * @param singlePort
     *            The one and only source port.
     * @param edges
     *            All edges that shall be part of this hyper-edge paired with the side (LEFT or RIGHT) 
     *            they are laying on.
     * @param sourceSide
     *            The side of the source.
     */
    public SplineSegment(final LPort singlePort, final Set<Pair<SideToProcess, LEdge>> edges,
            final SideToProcess sourceSide) {
        
        if (sourceSide == SideToProcess.LEFT) {
            leftPorts.add(singlePort);
        } else {
            rightPorts.add(singlePort);
        }

        double yMinPosOfTarget = Double.POSITIVE_INFINITY;
        double yMaxPosOfTarget = Double.NEGATIVE_INFINITY;
        
        for (final Pair<SideToProcess, LEdge> pair : edges) {
            final SideToProcess side = pair.getFirst();
            final LEdge edge = pair.getSecond();
            
            LPort targetPort = edge.getSource();
            if (targetPort.equals(singlePort)) {
                targetPort = edge.getTarget();
            }
            
            if (side == SideToProcess.LEFT) {
                leftPorts.add(targetPort);
            } else {
                rightPorts.add(targetPort);
            }
            
            final double yPosOfTarget = targetPort.getAbsoluteAnchor().y;
            yMinPosOfTarget = Math.min(yMinPosOfTarget, yPosOfTarget);
            yMaxPosOfTarget = Math.max(yMaxPosOfTarget, yPosOfTarget);
        }
            
        final double yPosOfSingleSide = singlePort.getAbsoluteAnchor().y;

        // set the relevant positions 
        setRelevantPositions(yPosOfSingleSide, yMinPosOfTarget, yMaxPosOfTarget);
        
        for (final Pair<SideToProcess, LEdge> pair : edges) {
            addEdge(pair.getSecond());
        }
        isStraight = false;
    }

    /**
     * Constructor for a hyper-edges consisting of a single edge. This MAY be a straight edge.
     * 
     * @param edge
     *            The straight edge that shall be the one and only element of this hyper-edge.
     * @param sourceSide
     *            On witch Layer (LEFT or RIGHT) lays the source port.
     * @param targetSide
     *            On witch Layer (LEFT or RIGHT) lays the target port.
     */
    public SplineSegment(final LEdge edge, final SideToProcess sourceSide, final SideToProcess targetSide) {
        // adding left and right ports
        if (sourceSide == SideToProcess.LEFT) {
            leftPorts.add(edge.getSource());
        } else {
            rightPorts.add(edge.getSource());
        } 
        if (targetSide == SideToProcess.LEFT) {
            leftPorts.add(edge.getTarget());
        } else {
            rightPorts.add(edge.getTarget());
        }
        
        // adding the edges
        addEdge(edge);
        
        // setting relevant positions
        final double sourceY = edge.getSource().getAbsoluteAnchor().y;
        final double targetY = edge.getTarget().getAbsoluteAnchor().y;
        setRelevantPositions(sourceY, targetY, targetY);

        isStraight = SplineEdgeRouter.isStraight(sourceY, targetY);
    }
    
    private void addEdge(final LEdge edge) {
        edges.add(edge);
        final KVector sourceAnchor = edge.getSource().getAbsoluteAnchor();
        final KVector targetAnchor = edge.getTarget().getAbsoluteAnchor();
        
        EdgeInformation ei = new EdgeInformation();
        edgeInformation.put(edge, ei);
        ei.startY = sourceAnchor.y;
        ei.endY = targetAnchor.y;
        ei.normalSourceNode = SplineEdgeRouter.isNormalNode(edge.getSource().getNode());
        ei.normalTargetNode = SplineEdgeRouter.isNormalNode(edge.getTarget().getNode());
        ei.invertedLeft = edge.getSource().getSide() == PortSide.WEST;
        ei.invertedRight = edge.getTarget().getSide() == PortSide.EAST;
    }
    
    /**
     * @return {@code true} if this spline edge represents a hyperedge, i.e. it represents multiple {@link LEdge}s.
     */
    public boolean isHyperEdge() {
        return edges.size() > 1;
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(final SplineSegment other) {
        return this.mark - other.mark;
    }
    
    /**
     * Sets the {@link #centerControlPointY} and the top and bottom hyperedge y coordinates to appropriate values.
     * 
     * @param sourceY The y-value of the source point.
     * @param targetYMin The y-value of lowest target point.
     * @param targetYMax The y-value of the highest target point.
     */
    private void setRelevantPositions(final double sourceY, final double targetYMin, final double targetYMax) {
        boundingBox.y = Math.min(sourceY, targetYMin);
        boundingBox.height = Math.max(sourceY, targetYMax) - boundingBox.y;
        
        if (sourceY < targetYMin) {
            // source lays below all target ports
            centerControlPointY = SplineEdgeRouter.ONE_HALF * (sourceY + targetYMin);
            hyperEdgeTopYPos = SplineEdgeRouter.RELEVANT_POS_MID_RATE * centerControlPointY 
                               + SplineEdgeRouter.RELEVANT_POS_OUTER_RATE * sourceY;
            hyperEdgeBottomYPos = SplineEdgeRouter.RELEVANT_POS_MID_RATE * centerControlPointY 
                                  + SplineEdgeRouter.RELEVANT_POS_OUTER_RATE * targetYMin;
        } else {
            // source lays above all target ports
            centerControlPointY = SplineEdgeRouter.ONE_HALF * (sourceY + targetYMax);
            hyperEdgeTopYPos = SplineEdgeRouter.RELEVANT_POS_MID_RATE * centerControlPointY
                               + SplineEdgeRouter.RELEVANT_POS_OUTER_RATE * targetYMax;
            hyperEdgeBottomYPos = SplineEdgeRouter.RELEVANT_POS_MID_RATE * centerControlPointY 
                                  + SplineEdgeRouter.RELEVANT_POS_OUTER_RATE * sourceY;
        }
    }
}