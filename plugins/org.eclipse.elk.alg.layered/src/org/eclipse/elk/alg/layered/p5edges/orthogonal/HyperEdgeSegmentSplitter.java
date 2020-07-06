/*******************************************************************************
 * Copyright (c) 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.orthogonal;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.elk.alg.layered.p5edges.orthogonal.HyperEdgeSegmentDependency.DependencyType;

import com.google.common.collect.Streams;

/**
 * Responsible for splitting {@link HyperEdgeSegment}s in order to avoid overlaps. This class assumes that critical
 * dependency cycles have been found and that a set of critical dependencies whose removal will break those cycles has
 * been computed. These dependencies will determine the {@link HyperEdgeSegment}s that will be split.
 * 
 * <p>
 * All throughout this code, the terminology will refer to the horizontal layout case.
 * </p>
 */
public final class HyperEdgeSegmentSplitter {
    
    /** The routing generator that created us. */
    private OrthogonalRoutingGenerator routingGenerator;
    
    /**
     * Creates a new instance to be used by the given routing generator.
     */
    public HyperEdgeSegmentSplitter(final OrthogonalRoutingGenerator routingGenerator) {
        this.routingGenerator = routingGenerator;
    }
    
    /**
     * Breaks critical dependency cycles by resolving the given dependencies. For each dependency, we split one of the
     * involved {@link HyperEdgeSegment}s, which will result in modified dependencies and additional segments, which are
     * added to the given list of segments.
     */
    public void splitSegments(final List<HyperEdgeSegmentDependency> dependenciesToResolve,
            final List<HyperEdgeSegment> segments, final double criticalConflictThreshold) {
        
        // Only start preparations if there's going to be things to do
        if (dependenciesToResolve.isEmpty()) {
            return;
        }
        
        // Collect all relevant spaces between horizontal segments that are large enough to house another horizontal
        // segment without causing additional conflicts
        List<FreeArea> freeAreas = findFreeAreas(segments, criticalConflictThreshold);
        
        // For each dependency, choose which segment to split
        Set<HyperEdgeSegment> segmentsToSplit = decideWhichSegmentsToSplit(dependenciesToResolve);
        
        // Split the segments in order from smalles to largest. The smallest ones need to be split first since they
        // have fewer options for where to put their horizontal connecting segments.
        segmentsToSplit.stream()
            .sorted((hes1, hes2) -> Double.compare(hes1.getSize(), hes2.getSize()))
            .forEach(segment -> split(segment, segments, freeAreas));
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Finding Space
    
    /**
     * Assembles the areas between horizontal segments that are large enough to allow another horizontal segment to slip
     * in.
     */
    private List<FreeArea> findFreeAreas(final List<HyperEdgeSegment> segments,
            final double criticalConflictThreshold) {
        
        List<FreeArea> freeAreas = new ArrayList<>();
        
        // Retrieve all positions where hyperedge segments connect to ports, and sort them
        Stream<Double> inCoordinates = segments.stream().flatMap(s -> s.getIncomingConnectionCoordinates().stream());
        Stream<Double> outCoordinates = segments.stream().flatMap(s -> s.getOutgoingConnectionCoordinates().stream());
        
        double[] sortedCoordinates = Streams.concat(inCoordinates, outCoordinates)
            .mapToDouble(coordinate -> coordinate.doubleValue())
            .sorted()
            .toArray();
        
        // Go through each pair of coordinates and create free areas for those that are at least twice the critical
        // threshold
        for (int i = 1; i < sortedCoordinates.length; i++) {
            if (sortedCoordinates[i] - sortedCoordinates[i - 1] >= 2 * criticalConflictThreshold) {
                freeAreas.add(new FreeArea(
                        sortedCoordinates[i - 1] + criticalConflictThreshold,
                        sortedCoordinates[i] - criticalConflictThreshold));
            }
        }
        
        return freeAreas;
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Split Segment Decisions

    /**
     * Given a list of hyperedge dependencies, this method assembles a list of {@link HyperEdgeSegment}s which, if
     * split, will cause the critical cycles caused by the dependencies to be broken.
     */
    private LinkedHashSet<HyperEdgeSegment> decideWhichSegmentsToSplit(
            final List<HyperEdgeSegmentDependency> dependencies) {
        
        // We want to be sure to have a stable iteration order, but we also want fast containment checks
        LinkedHashSet<HyperEdgeSegment> segmentsToSplit = new LinkedHashSet<>();
        
        for (HyperEdgeSegmentDependency dependency : dependencies) {
            HyperEdgeSegment sourceSegment = dependency.getSource();
            HyperEdgeSegment targetSegment = dependency.getTarget();
            
            // If either of the involved segments were already selected for splitting because of another dependency,
            // that's sufficient
            if (segmentsToSplit.contains(sourceSegment) || segmentsToSplit.contains(targetSegment)) {
                continue;
            }
            
            // One segment will be split, and the other one will be remembered to have caused the split. The latter
            // will have to stay between the two segments the former will be split into, or else we'll have overlaps
            // again
            HyperEdgeSegment segmentToSplit = sourceSegment;
            HyperEdgeSegment segmentCausingSplit = targetSegment;
            
            // This is perfectly fine unless the segment to split represents a hyperedge and the other does not. We
            // prefer splitting regular edges since hyperedges have a higher chance of causing additional crossings.
            // Note: the original code took the layout direction into account here. We may want to investigate at some
            // point whether that's necessary.
            if (sourceSegment.representsHyperedge() && !targetSegment.representsHyperedge()) {
                segmentToSplit = targetSegment;
                segmentCausingSplit = sourceSegment;
            }
            
            segmentsToSplit.add(segmentToSplit);
            segmentToSplit.setSplitBy(segmentCausingSplit);
        }
        
        return segmentsToSplit;
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Actual Splitting
    
    /**
     * Splits the given segment at the optimal position.
     */
    private void split(final HyperEdgeSegment segment, final List<HyperEdgeSegment> segments,
            final List<FreeArea> freeAreas) {
        
        // Split the segment at the best position and add the new segment to our list
        double splitPosition = computePositionToSplitAndUpdateFreeAreas(segment, freeAreas);
        segments.add(segment.splitAt(splitPosition));
        
        // Update the dependencies to reflect the new situation
        updateDependencies(segment, segments);
    }

    /**
     * Returns a hopefully good position to split the given segment at, and updates the list of free areas since an area
     * now either disappears or is split into two smaller areas.
     */
    private static double computePositionToSplitAndUpdateFreeAreas(final HyperEdgeSegment segment,
            final List<FreeArea> freeAreas) {
        
        // TODO Return a proper position here
        return (segment.getStartCoordinate() + segment.getEndCoordinate()) / 2;
    }

    /**
     * Adds all necessary dependencies for the given segment and its split partner. The segment was recently split, and
     * neither it nor its split partner have any dependencies beyond the critical ones.
     */
    private void updateDependencies(final HyperEdgeSegment segment, final List<HyperEdgeSegment> segments) {
        HyperEdgeSegment splitCausingSegment = segment.getSplitBy();
        HyperEdgeSegment splitPartner = segment.getSplitPartner();
        
        // The segment currently has no dependencies at all. We first need for the segments to be ordered like this:
        //    segment ---> split-causing segment ---> split partner
        new HyperEdgeSegmentDependency(
                DependencyType.CRITICAL,
                segment,
                splitCausingSegment,
                OrthogonalRoutingGenerator.CRITICAL_DEPENDENCY_WEIGHT);
        new HyperEdgeSegmentDependency(
                DependencyType.CRITICAL,
                splitCausingSegment,
                splitPartner,
                OrthogonalRoutingGenerator.CRITICAL_DEPENDENCY_WEIGHT);
        
        // Now we just need to re-introduce dependencies to other segments
        for (HyperEdgeSegment otherSegment : segments) {
            // We already have our dependencies between our three segments involved in the conflict settled
            if (otherSegment != splitCausingSegment && otherSegment != segment && otherSegment != splitPartner) {
                routingGenerator.createDependencyIfNecessary(otherSegment, segment);
                routingGenerator.createDependencyIfNecessary(otherSegment, splitPartner);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Data Holders
    
    /**
     * Represents a free area between two horizontal edge segments, which can be used to route new horizontal segments
     * that connect the two parts of a split {@link HyperEdgeSegment}.
     */
    private static final class FreeArea {
        
        /** Start of the free area. */
        private final double startPosition;
        /** End of the free area. */
        private final double endPosition;
        /** Convenience variable that stores the size of the area. */
        private final double size;
        
        private FreeArea(final double startPosition, final double endPosition) {
            assert endPosition > startPosition;
            
            this.startPosition = startPosition;
            this.endPosition = endPosition;
            this.size = endPosition - startPosition;
        }
        
    }
    
}
