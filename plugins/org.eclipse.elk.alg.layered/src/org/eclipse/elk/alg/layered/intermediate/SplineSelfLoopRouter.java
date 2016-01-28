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
package org.eclipse.elk.alg.layered.intermediate;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.elk.alg.layered.ILayoutProcessor;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.LShape;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.p5edges.splines.ConnectedSelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.splines.LoopSide;
import org.eclipse.elk.alg.layered.p5edges.splines.NubsSelfLoop;
import org.eclipse.elk.alg.layered.p5edges.splines.Rectangle;
import org.eclipse.elk.alg.layered.p5edges.splines.SplinesMath;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Routes self-loops using splines. The connected components of self-loops that are holding more than 
 * one loop are sorted, so that the loop with the smallest label is drawn first. 
 * 
 * <dl>
 *   <dt>Precondition:</dt><dd>A layered graph. Connected components of all self-loops must have been 
 *             stored in the SPLINE_SELFLOOP_COMPONENTS property of the nodes.</dd>
 *             <dd>The ports of the compontents must be part of the port list
 *             of the node the component is laying on.</dd>
 *   <dt>Postcondition:</dt><dd>Bendpoint for all self-loops are calcuated.</dd>
 *             <dd>The SPLINE_SELF_LOOP_MARGINS is increased to include all self-loops
 *             and their labels.</dd>
 *   <dt>Slots:</dt><dd>After phase 3.</dd>
 *   <dt>Same-slot dependencies:</dt><dd>SplineSelfLoopPositioner, LabelAndNodeSizeProcessor.</dd>
 * </dl>
 * 
 * @author tit
 */
public final class SplineSelfLoopRouter implements ILayoutProcessor {
    // Constants to manipulate the layout.
    /** Minimum height of a self-loop laying on a straight edge of the node. */
    private static final double MIN_SIDE_HEIGHT = 15.0;
    /** Minimum height of a self-loop laying on a corner of the node. This is the source side height. */
    private static final double MIN_CORNER_SOURCE_LENGTH = 15.0;
    /** Minimum height of a self-loop laying on a corner of the node. This is the target side height. */
    private static final double MIN_CORNER_TARGET_LENGTH = 15.0;
    /** Minimum height of a self-loop going around the node. This is the source side height. */
    private static final double MIN_ACROSS_SOURCE_LENGTH = 15.0;
    /** Minimum height of a self-loop going around the node. This is the middle side height. */
    private static final double MIN_ACROSS_MID_HEIGHT = 15.0;
    /** Minimum height of a self-loop going around the node. This is the target side height. */
    private static final double MIN_ACROSS_TARGET_LENGTH = 15.0;
    /** Height offset between two self-loops. */
    private static final double SPACE_FOR_EDGE = 15.0;
    /** Edge labels are pushed away from their edge by this value. */
    private static final double LABEL_Y_DISTANCE = 0.5;
    
    /**
     * {@inheritDoc}
     */
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Spline SelfLoop routing", 1);
        final Comparator<SelfLoopEdge> stepSizeComparator = new SelfLoopEdge.StepSizeComparator();
        
        for (final Layer layer : layeredGraph) {
            for (final LNode node : layer) {
                // list of all ports of the node
                final List<LPort> ports = node.getPorts();
                
                // set of all loop-edges of the node
                final Set<LEdge> loopEdges = Sets.newLinkedHashSet();
                for (final ConnectedSelfLoopComponent component : node
                        .getProperty(InternalProperties.SPLINE_SELFLOOP_COMPONENTS)) {
                    loopEdges.addAll(component.getEdges());
                }
                
                // Construct a SelfLoopEdge for every loop-edge 
                final List<SelfLoopEdge> selfLoops = Lists.newArrayList();
                for (final LEdge edge : loopEdges) {
                    final LPort sourcePort = edge.getSource();
                    final LPort targetPort = edge.getTarget();
                    final Iterator<LPort> iter = edge.getSource().getNode().getPorts().iterator();

                    // Lookup the position (index) of source and target port of current edge.
                    int sourceIndex = 0;
                    int targetIndex = 0;
                    int found = 0;
                    int index = 0;
                    while (found < 2) {
                        final LPort currentPort = iter.next();
                        if (sourcePort.equals(currentPort)) {
                            sourceIndex = index;
                            found++;
                        }
                        if (targetPort.equals(currentPort)) {
                            targetIndex = index;
                            found++;
                        }
                        index++;
                    }
                    
                    // calculate the stepSize
                    final LoopSide side = edge.getProperty(InternalProperties.SPLINE_LOOPSIDE);
                    final int stepSize = side == LoopSide.NW || side == LoopSide.ENW 
                            ? ports.size() - Math.abs(targetIndex - sourceIndex) + 1 // via zero-port
                            : Math.abs(targetIndex - sourceIndex);  // default case

                    selfLoops.add(new SelfLoopEdge(sourceIndex, targetIndex, stepSize, side, edge));
                }

                // sort the selfLoops, so the loop edge with the smallest step size is the first.
                Collections.sort(selfLoops, stepSizeComparator);
                
                // Process the self-loops, loops with smallest step size first.
                // While processing 
                // - the loopPaddings collection is updated (add new paddings resulting from new loop)
                // - all margins of loops are collected to calculate the node margin
                final Set<LoopPadding> loopPaddings = Sets.newHashSet();
                final Iterator<SelfLoopEdge> loopIter = selfLoops.iterator();
                if (loopIter.hasNext()) {
                    final Rectangle loopRectangle = processSelfLoop(loopIter.next(), loopPaddings);
                    while (loopIter.hasNext()) {
                        loopRectangle.union(processSelfLoop(loopIter.next(), loopPaddings));
                    }
                    // Calculate total margins around the node.
                    node.setProperty(InternalProperties.SPLINE_SELF_LOOP_MARGINS, 
                            loopRectangle.toNodeMargins(node));
                }
            }
        }
        monitor.done();
    }
    
    /**
     * Processes a self-loop. Calculates the relevant paddings and calls the correct self-loop calculator
     * 
     * @param selfLoop The self-loop to process.
     * @param loopPaddings The loopPaddings in this collection are respected. 
     *                  ATTENTION: As a side effect the padding resulting from the created loop is added.
     * @return The {@link Rectangle} around this loop.
     */
    private Rectangle processSelfLoop(
            final SelfLoopEdge selfLoop, final Set<LoopPadding> loopPaddings) {
        Rectangle loopRectangle = null;
        
        // First filter for relevant paddings ...
        final Predicate<LoopPadding> pred = new LoopPadding.EnclosingPredicate(selfLoop);
        final List<LoopPadding> relevantPaddings = 
                Lists.newArrayList(Iterables.filter(loopPaddings, pred));
        // ... then sort them (biggest padding will be the first element).
        Collections.sort(relevantPaddings, new LoopPadding.MarginComparator());

        // Depending of the type of LoopSide (across, side or corner), we have to take up to three
        // paddings into account
        final LoopSide loopSide = selfLoop.loopSide;
        Predicate<LoopPadding> sidePredicate;
        double sourceHeight, middleHeight, targetHeight;
        Iterator<LoopPadding> itr;
        
        switch (loopSide.getType()) {
        case ACROSS:
            // we have to take three portSides/margins into account: the one of the sourcePort, the
            // side that the loop is bending around and the one of the targetPort
            sidePredicate = new LoopPadding.PortSidePredicate(loopSide.getSourceSide());
            itr = Iterables.filter(relevantPaddings, sidePredicate).iterator();
            if (itr.hasNext()) {
                sourceHeight =  itr.next().padding;
            } else {
                sourceHeight = MIN_ACROSS_SOURCE_LENGTH;
            }
            
            sidePredicate = new LoopPadding.PortSidePredicate(loopSide.getMiddleSide());
            itr = Iterables.filter(relevantPaddings, sidePredicate).iterator();
            if (itr.hasNext()) {
                middleHeight =  itr.next().padding;
            } else {
                middleHeight = MIN_ACROSS_MID_HEIGHT;
            }
            
            sidePredicate = new LoopPadding.PortSidePredicate(loopSide.getTargetSide());
            itr = Iterables.filter(relevantPaddings, sidePredicate).iterator();
            if (itr.hasNext()) {
                targetHeight =  itr.next().padding;
            } else {
                targetHeight = MIN_ACROSS_TARGET_LENGTH;
            }
            
            loopRectangle = calculateAcrossSelfLoop(selfLoop, sourceHeight, middleHeight, targetHeight);
            loopPaddings.add(new LoopPadding(loopRectangle, selfLoop.sourceIndex, selfLoop.targetIndex,
                    selfLoop.lEdge.getSource().getNode(), loopSide.getSourceSide()));
            loopPaddings.add(new LoopPadding(loopRectangle, selfLoop.sourceIndex, selfLoop.targetIndex,
                    selfLoop.lEdge.getSource().getNode(), loopSide.getMiddleSide()));
            loopPaddings.add(new LoopPadding(loopRectangle, selfLoop.sourceIndex, selfLoop.targetIndex,
                    selfLoop.lEdge.getSource().getNode(), loopSide.getTargetSide()));
            break;
        case CORNER:
            // we have to take two portSides into account: the one of the sourcePort and the one of the
            // targetPort
            sidePredicate = new LoopPadding.PortSidePredicate(loopSide.getSourceSide());
            itr = Iterables.filter(relevantPaddings, sidePredicate).iterator();
            if (itr.hasNext()) {
                sourceHeight =  itr.next().padding;
            } else {
                sourceHeight = MIN_CORNER_SOURCE_LENGTH;
            }

            sidePredicate = new LoopPadding.PortSidePredicate(loopSide.getTargetSide());
            itr = Iterables.filter(relevantPaddings, sidePredicate).iterator();
            if (itr.hasNext()) {
                targetHeight =  itr.next().padding;
            } else {
                targetHeight = MIN_CORNER_TARGET_LENGTH;
            }
            
            loopRectangle = calculateCornerSelfLoop(selfLoop, sourceHeight, targetHeight);
            loopPaddings.add(new LoopPadding(loopRectangle, selfLoop.sourceIndex, selfLoop.targetIndex, 
                    selfLoop.lEdge.getSource().getNode(), loopSide.getSourceSide()));
            loopPaddings.add(new LoopPadding(loopRectangle, selfLoop.sourceIndex, selfLoop.targetIndex, 
                    selfLoop.lEdge.getSource().getNode(), loopSide.getTargetSide()));
            break;
        case SIDE:
            // the filtered margins are all on the same side
            sidePredicate = new LoopPadding.PortSidePredicate(loopSide.getSourceSide());
            itr = Iterables.filter(relevantPaddings, sidePredicate).iterator();
            if (itr.hasNext()) {
                sourceHeight =  itr.next().padding;
            } else {
                sourceHeight = MIN_SIDE_HEIGHT;
            }

            loopRectangle = calculateSideSelfLoop(selfLoop, sourceHeight);
            loopPaddings.add(new LoopPadding(loopRectangle, selfLoop.sourceIndex, selfLoop.targetIndex, 
                    selfLoop.lEdge.getSource().getNode(), loopSide.getSourceSide()));
            break;
        default:
            throw new IllegalArgumentException("The loopside must be defined.");
        }

        return loopRectangle;
    }
    
    /**
     * Calculates the bend-points and label positions of a across self-loop.
     * 
     * @param selfLoops The selfLoop to calculate.
     * @param sourcePadding The necessary padding on the side of the source port.
     * @param targetPadding The necessary padding on the side of the target port.
     * @return The margins of the self-loop.
     */
    private Rectangle calculateCornerSelfLoop(final SelfLoopEdge edge, 
            final double sourcePadding, final double targetPadding) {
        final LoopSide side = edge.loopSide;
        
        // calculate maximum label length
        double labelLength = 0.0;
        for (final LLabel label : edge.lEdge.getLabels()) {
            labelLength = Math.max(labelLength, label.getSize().x);
        }

        // calculate the bendPoints
        final NubsSelfLoop nubs = NubsSelfLoop.createCornerSelfLoop(
                edge.lEdge.getSource(), edge.lEdge.getTarget(), 
                sourcePadding, targetPadding, labelLength);
        edge.lEdge.getBendPoints().addAll(nubs.getBezierCP());

        // place labels
        final Rectangle labelMargins = 
                placeLabels(edge.lEdge.getLabels(), nubs.getFirstLabelPosition(), side);
        
        final Rectangle edgeMargins = new Rectangle(nubs.getOuterBox());
        edgeMargins.enlarge(SPACE_FOR_EDGE);
        
        // calculate total margins
        if (labelMargins == null) {
            return edgeMargins;
        } else {
            return Rectangle.union(edgeMargins, labelMargins);
        }
    }
    
    /**
     * Calculates the bend-points and label positions of a side self-loop.
     * 
     * @param selfLoops The selfLoop to calculate.
     * @param padding The necessary padding.
     * @return The margins of the self-loop.
     */
    private Rectangle calculateSideSelfLoop(final SelfLoopEdge edge, final double padding) {
        final LoopSide side = edge.loopSide;
       
        // calculate the bendPoints
        final NubsSelfLoop nubs = 
              NubsSelfLoop.createSideSelfLoop(edge.lEdge.getSource(), edge.lEdge.getTarget(), padding);
        edge.lEdge.getBendPoints().addAll(nubs.getBezierCP());
        // place labels
        final Rectangle labelMargins = 
                placeLabels(edge.lEdge.getLabels(), nubs.getFirstLabelPosition(), side);

        final Rectangle edgeMargins = new Rectangle(nubs.getOuterBox());
        edgeMargins.enlarge(SPACE_FOR_EDGE);

        // calculate total margins
        if (labelMargins == null) {
            return edgeMargins;
        } else {
            return Rectangle.union(edgeMargins, labelMargins);
        }
    }
    
    /**
     * Calculates the bend-points and label positions of a across self-loop.
     * 
     * @param selfLoops The selfLoop to calculate.
     * @param sourcePadding The necessary padding on the side of the source port.
     * @param middlePadding The necessary padding on the middle side.
     * @param targetPadding The necessary padding on the side of the target port.
     * @return The margins of the self-loop.
     */
    private Rectangle calculateAcrossSelfLoop(final SelfLoopEdge edge, final double sourcePadding, 
            final double middlePadding, final double targetPadding) {

        double textLength = 0.0;
        for (final LLabel label : edge.lEdge.getLabels()) {
            textLength = Math.max(textLength, label.getSize().x);
        }

        // calculate the bendPoints
        final NubsSelfLoop nubs = NubsSelfLoop.createAcrossSelfLoop(
                edge.lEdge.getSource(), sourcePadding,
                edge.lEdge.getTarget(), targetPadding,
                edge.loopSide.getMiddleSide(), middlePadding, textLength);
        edge.lEdge.getBendPoints().addAll(nubs.getBezierCP());   
        
        // place labels
        final Rectangle labelMargins = 
                placeLabels(edge.lEdge.getLabels(), nubs.getFirstLabelPosition(), edge.loopSide);
        
        final Rectangle edgeMargins = new Rectangle(nubs.getOuterBox());
        edgeMargins.enlarge(SPACE_FOR_EDGE);

        // calculate total margins
        if (labelMargins == null) {
            return edgeMargins;
        } else {
            return Rectangle.union(edgeMargins, labelMargins);
        }
    }
    
    /**
     * Places the labels on a self-loop edge. Returns a {@link Rectangle} around all labels given.
     * The raw position of a label is the center of it's lower edge.
     * 
     * @param labels The labels to place.
     * @param rawPosition The raw position of the first label. 
     * @param side The side the self loop is laying on.
     * @return A margin around all placed labels or {@code null} if there is no label.
     */
    private Rectangle placeLabels(final List<LLabel> labels, final KVector rawPosition,
            final LoopSide side) {
        
        final Iterator<LLabel> iter = labels.iterator();
        // process all labels
        if (iter.hasNext()) {
            LLabel label = iter.next();
            final Rectangle allLabels = placeLabel(label, rawPosition, side);

            while (iter.hasNext()) {
                label = iter.next();
                allLabels.union(placeLabel(label, rawPosition, side));
            }
            // return the margin of all labels
            return new Rectangle(allLabels);
        } else {
            return null;
        }
    }

    /**
     * Places a label on a self-loop edge. Also calculates the raw position of next label and modifies
     * the rawPosition parameter to hold the new value. Raw position is the position where the center
     * of the lower edge of the label shall be positioned.
     * 
     * @param label The label to place.
     * @param rawPosition The raw position of the label to place. Holds the position for the next label
     *          after calculation is done. 
     * @param side The {@link LoopSide} of the edge, that the label is placed on.
     * @return A {@link Rectangle} around the placed label.
     */
    private Rectangle placeLabel(
            final LLabel label, final KVector rawPosition, final LoopSide side) {
        
        final KVector thisLabelPosition = new KVector(rawPosition);
        final KVector labelSize = new KVector(label.getSize());
        
        // calculate the exact position for current label and raw position for next label.
        switch (side) {
        case NW:
            thisLabelPosition.add(-labelSize.x / 2.0, -labelSize.y);
            rawPosition.add(0.0, -(LABEL_Y_DISTANCE + labelSize.y));
            break;
        case WS:
            thisLabelPosition.add(-labelSize.x / 2.0, 0.0);
            rawPosition.add(0.0, LABEL_Y_DISTANCE + labelSize.y);
            break;
        case EN:
            thisLabelPosition.add(-labelSize.x / 2.0, -labelSize.y);
            rawPosition.add(0.0, -(LABEL_Y_DISTANCE + labelSize.y));
            break;
        case SE:
            thisLabelPosition.add(-labelSize.x / 2.0, 0.0);
            rawPosition.add(0.0, LABEL_Y_DISTANCE + labelSize.y);
            break;
        case N:
            thisLabelPosition.add(-labelSize.x / 2.0, -labelSize.y);
            rawPosition.add(0.0, -(LABEL_Y_DISTANCE + -labelSize.y));
            break;
        case E:
            thisLabelPosition.add(0.0, -labelSize.y / 2.0);
            rawPosition.add(0.0, -(LABEL_Y_DISTANCE + labelSize.y));
            break;
        case S:
            thisLabelPosition.add(-labelSize.x / 2.0, 0.0);
            rawPosition.add(0.0, LABEL_Y_DISTANCE + labelSize.y);
            break;
        case W:
            thisLabelPosition.add(-labelSize.x, labelSize.y / 2);
            rawPosition.add(0.0, -(LABEL_Y_DISTANCE + labelSize.y));
            break;
        case ENW:
            thisLabelPosition.add(-labelSize.x / 2.0, -labelSize.y);
            rawPosition.add(0.0, -(LABEL_Y_DISTANCE + labelSize.y));
            break;
        case ESW:
            thisLabelPosition.add(-labelSize.x / 2.0, 0.0);
            rawPosition.add(0.0, -(LABEL_Y_DISTANCE + labelSize.y));
            break;
        case SEN:
            thisLabelPosition.add(0.0, -labelSize.y / 2.0);
            rawPosition.add(0.0, -(LABEL_Y_DISTANCE + labelSize.y));
            break;
        case SWN:
            thisLabelPosition.add(-labelSize.x, -labelSize.y / 2.0);
            rawPosition.add(0.0, -(LABEL_Y_DISTANCE + labelSize.y));
            break;
        default:
            break;
        }

        // Set the position of current label.
        label.getPosition().reset().add(thisLabelPosition);

        // return the margin of the label
        return new Rectangle(label);
    }

    /**
     * Represents a LEdge that forms a self-loop. It contains the original edge, and in addition
     * holds the index of it's source and target port, stepSize and loopSide.
     * 
     * @author tit
     */
    private static final class SelfLoopEdge {
        /** Index of source port in the list of ports of parent node. */ 
        private final int sourceIndex;
        /** Index of target port in the list of ports of parent node. */ 
        private final int targetIndex;
        /** How many steps to get from source to target port. Imagine to start running in front of the
         *  first port, walking around the node in the same direction as the edge and counting all ports
         *  passed till the target port has been crossed. So the stepSize of two neighbor ports is two. 
         */
        private final int stepSize;
        /** The loopSide of this self-loop. */
        private final LoopSide loopSide;
        /** The LEdge of this self-loop. */
        private final LEdge lEdge;
        
        SelfLoopEdge(final int sourceIndex, final int targetIndex, final int stepSize, 
                final LoopSide loopSide, final LEdge edge) {
            this.sourceIndex = sourceIndex;
            this.targetIndex = targetIndex;
            this.stepSize = stepSize;
            this.loopSide = loopSide;
            this.lEdge = edge;
        }
        
        /**
         * Comparator that uses the step size as base.
         */
        private static class StepSizeComparator implements Comparator<SelfLoopEdge> {
            /**
             * {@inheritDoc}
             */
            public int compare(final SelfLoopEdge edge0, final SelfLoopEdge edge1) {
                return edge0.stepSize - edge1.stepSize;
            }
        }
    }
    
    
    /**
     * Defines the space occupied by a self loop.
     * 
     * @author tit
     */
    private static final class LoopPadding {
        /** The PortSide of this padding. */
        private final PortSide side;
        /** The index of the source port of this padding. */
        private final int startIndex;
        /** The index of the target port of this padding. */
        private final int endIndex;
        /** The padding (height) of this padding. */
        private final double padding;

        /**
         * Constructs a new {@link LoopPadding} for a loop represented by it's bounding-box.
         * 
         * @param boundingBox A rectangle around the self-loop.
         * @param sourceIndex The index of the sourcePort.
         * @param targetIndex The index of the targetPort.
         * @param shape The {@link LShape} (normally a {@link LNode}) the self-loops is laying at.
         * @param side The side, for which the padding shall be calculated. 
         */
        LoopPadding(
                final Rectangle boundingBox,
                final int sourceIndex,
                final int targetIndex,
                final LShape shape, 
                final PortSide side) {
            this.side = side;
            this.startIndex = sourceIndex;
            this.endIndex = targetIndex;
            
            switch (side) {
            case WEST:
                this.padding = Math.abs(boundingBox.getLeft());
                break;
            case NORTH:
                this.padding = Math.abs(boundingBox.getTop());
                break;
            case EAST:
                this.padding = Math.abs(boundingBox.getRight() - shape.getSize().x);
                break;
            case SOUTH:
                this.padding = Math.abs(boundingBox.getBottom() - shape.getSize().y);
                break;
            default:
                this.padding = 0.0;
                break;
            }
        }
        
        /**
         * {@inheritDoc}
         */
        public String toString() {
            return padding + ": " + startIndex + " -> " + endIndex + " " + side.toString();
        }

        /**
         * Comparator that uses the margin as base and is "inverted", so biggest margins are at the left.
         */
        public static class MarginComparator implements Comparator<LoopPadding> {

            /**
             * {@inheritDoc}
             */
            public int compare(final LoopPadding margin0, final LoopPadding margin1) {
                return Double.compare(margin1.padding, margin0.padding);
            }
        }

        /**
         * A {@link Predicate} to filter {@link LoopPadding}s. Predicate is {@code true} for
         * any {@link LoopPadding} those connected ports are inside (including the border) the range
         * defined by the predicate constructor.
         * 
         * @author tit
         */
        private static class EnclosingPredicate implements Predicate<LoopPadding> {
            /** The LoopSide of this predicate. */
            private final LoopSide loopSide;
            /** The first border of this predicate. */
            private final int startIndex;
            /** The second border of this predicate. */
            private final int endIndex;

            /**
             * Creates a new {@link EnclosingPredicate} for the given self-loop.
             * 
             * @param edge The constructed predicate will be {@code true} for any {@link LoopPadding}
             *          those ports are inside the range of this edge.
             */
            public EnclosingPredicate(final SelfLoopEdge edge) {
                this.startIndex = edge.sourceIndex;
                this.endIndex = edge.targetIndex;
                this.loopSide = edge.loopSide;
            }

            /**
             * {@inheritDoc}
             */
            public boolean apply(final LoopPadding margin) {
                if (!loopSide.getPortSides().contains(margin.side)) {
                    return false;
                }

                if (loopSide.viaNW()) {
                    return !(SplinesMath.isBetween(margin.startIndex, startIndex, endIndex)
                        && SplinesMath.isBetween(margin.endIndex, startIndex, endIndex));
                } else {
                    return SplinesMath.isBetween(margin.startIndex, startIndex, endIndex)
                        && SplinesMath.isBetween(margin.endIndex, startIndex, endIndex);
                }
            }
        }
        
        /**
         * A {@link Predicate} to filter {@link LoopPadding}s. Predicate is {@code true} for
         * any {@link LoopPadding} laying on the same side as defined in the predicate's construction.
         * 
         * @author tit
         */
        private static class PortSidePredicate implements Predicate<LoopPadding> {
            /** The PortSide of this Predicate. */
            private final PortSide side;
            
            /**
             * Creates a new {@link Predicate}.
             * 
             * @param side The {@link Predicate} will be {@code true} for this {@link PortSide}.
             */
            public PortSidePredicate(final PortSide side) {
                this.side = side;
            }
            
            /**
             * {@inheritDoc}
             */
            public boolean apply(final LoopPadding margin) {
                return margin.side == side;
            }
        }
    }
}
