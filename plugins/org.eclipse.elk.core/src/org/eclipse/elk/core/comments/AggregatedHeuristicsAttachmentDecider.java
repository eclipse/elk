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
package org.eclipse.elk.core.comments;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import org.eclipse.elk.graph.ElkGraphElement;

/**
 * Selects the attachment target with the best aggregated heuristic result. The attachment heuristic
 * results are first aggregated using a configurable aggregation function. Then, the attachment
 * target with the highest result is selected, provided that it is at least as high as or higher
 * than a configurable lower bound. The class provides a number of pre-defined aggregation
 * functions.
 */
public final class AggregatedHeuristicsAttachmentDecider implements IAttachmentDecider {
    
    /** Aggregator used to aggregate heuristics results. */
    private ToDoubleFunction<Collection<Double>> aggregator =
            AggregatedHeuristicsAttachmentDecider::max;
    /** The minimum aggregate result for a comment to be attached to anything. */
    private double lowerBoundary = 0.0;
    /** Whether an attachment is accepted if the aggregate value is exactly the lower boundary. */
    private boolean includeLowerBoundary = false;
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    // Configuration
    
    /**
     * Configures the attachment decider to use the given function to aggregate heuristic results.
     * 
     * <p>
     * If this method is not called, the {@link #max(Collection) maximum aggregator} will be used.
     * </p>
     * 
     * @param f
     *            the aggregator to use. There are no constraints on the values it produces, except
     *            that they must not be negative.
     * @return this object for method chaining.
     * @throws IllegalArgumentException
     *             if the function is {@code null}.
     */
    public AggregatedHeuristicsAttachmentDecider withAggregator(
            final ToDoubleFunction<Collection<Double>> f) {
        
        if (f == null) {
            throw new IllegalArgumentException("Aggregator cannot be null.");
        }
        
        aggregator = f;
        
        return this;
    }
    
    /**
     * Configures the attachment decider to impose the given lower boundary on attachability
     * decisions. This means that if the attachment target with the highest aggregated result
     * doesn't score at least as high as the lower boundary, it is not returned.
     * 
     * <p>
     * If this method is not called, all attachment targets will be considered eligible.
     * </p>
     * 
     * @param lower
     *            the lower boundary, {@code >= 0}.
     * @return this object for method chaining.
     * @throws IllegalArgumentException
     *             if the lower boundary is {@code < 0}.
     */
    public AggregatedHeuristicsAttachmentDecider withLowerAttachmentBoundary(final double lower) {
        if (lower < 0) {
            throw new IllegalArgumentException("Lower boundary must be >= 0.");
        }
        
        lowerBoundary = lower;
        
        return this;
    }
    
    /**
     * Configures the attachment decider to allow attachments already if the highest aggregated result
     * is exactly the lower boundary.
     * 
     * <p>
     * If this method is not called, the decider requires the highest aggregated result to be strictly
     * greater than the lower boundary.
     * </p>
     * 
     * @param include
     *            the lower boundary, {@code >= 0}.
     * @return this object for method chaining.
     */
    public AggregatedHeuristicsAttachmentDecider withLowerBoundaryIncluded(final boolean include) {
        includeLowerBoundary = include;
        
        return this;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    // IAttachmentDecider
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ElkGraphElement makeAttachmentDecision(
            final Map<ElkGraphElement, Map<Class<? extends IHeuristic>, Double>> normalizedHeuristics) {
        
        double max = Double.NEGATIVE_INFINITY;
        ElkGraphElement maxElement = null;
        
        for (Map.Entry<ElkGraphElement, Map<Class<? extends IHeuristic>, Double>> entry
                : normalizedHeuristics.entrySet()) {
            
            double aggregate = aggregator.applyAsDouble(entry.getValue().values());
            if (aggregate < 0) {
                throw new IllegalStateException("The aggregator provided a value < 0.");
            }
            
            if (aggregate > max) {
                max = aggregate;
                maxElement = entry.getKey();
            }
        }
        
        if (includeLowerBoundary) {
            return max >= lowerBoundary ? maxElement : null;
        } else {
            return max > lowerBoundary ? maxElement : null;
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    // Aggregation Functions
    
    /**
     * Aggregator that returns the maximum heuristic result.
     * 
     * @param values heuristic results.
     * @return the maximum result.
     */
    public static double max(final Collection<Double> values) {
        return Collections.max(values);
    }
    
    /**
     * Aggregator that returns the minimum heuristic result.
     * 
     * @param values heuristic results.
     * @return the minimum result.
     */
    public static double min(final Collection<Double> values) {
        return Collections.min(values);
    }
    
    /**
     * Aggregator that returns the average over all heuristic results.
     * 
     * @param values heuristic results.
     * @return the average.
     */
    public static double avg(final Collection<Double> values) {
        return values.stream().collect(Collectors.averagingDouble((d) -> d));
    }
    
    /**
     * Aggregator that returns the sum of all heuristic results.
     * 
     * @param values heuristic results.
     * @return the sum.
     */
    public static double sum(final Collection<Double> values) {
        return values.stream().collect(Collectors.summingDouble((d) -> d));
    }

}
