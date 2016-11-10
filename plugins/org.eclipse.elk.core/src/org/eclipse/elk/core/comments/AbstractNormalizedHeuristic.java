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

import org.eclipse.elk.graph.ElkGraphElement;
import org.eclipse.elk.graph.ElkNode;

/**
 * Provides a basic and configurable implementation of a normalized heuristic. Clients have to
 * implement {@link #raw(ElkNode, ElkGraphElement)}, the rest is done by this implementation. The actual
 * normalization is controlled through {@link NormalizationFunction}.
 * 
 * <p>
 * The way normalization is performed can be controlled through a bunch of configuration methods
 * that are protected to be accessed by subclasses only. Subclasses will probably want to offer
 * specialized configuration methods that call the configuration methods in this class as
 * appropriate, but may also increase the visibility of the provided methods.
 * </p>
 */
public abstract class AbstractNormalizedHeuristic implements IHeuristic {
    
    /**
     * Enumeration of available normalization functions.
     * 
     * @author cds
     */
    public enum NormalizationFunction {
        
        /** Interpolates linearly between the worst and best value. */
        LINEAR,
        /** Normalizes everything up to the worst value to 1, everything beyond to 0. */
        BINARY;
        
    }
    
    /** The raw value that marks the point at which the normalized heuristic will become 0. */
    private double worstRawValue = 0;
    /** The raw value that marks the point at which the normalized heuristic will become 1. */
    private double bestRawValue = 1;
    /** The function used to normalize raw values. */
    private NormalizationFunction normalizationFunction = NormalizationFunction.LINEAR;
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    // Configuration
    
    /**
     * Sets the raw values at which the normalized value will become 0 and 1. There is no
     * restriction on which of the two is the larger value. If the two are equal, the normalized value
     * will be 1 if the raw value matches the two, and 0 otherwise.
     * 
     * @param worstRawVal
     *            the raw value at which the normalized value will become 0.
     * @param bestRawVal
     *            the raw value at which the normalized value will become 1.
     * @return this object for method chaining.
     */
    protected AbstractNormalizedHeuristic withBounds(final double worstRawVal, final double bestRawVal) {
        this.worstRawValue = worstRawVal;
        this.bestRawValue = bestRawVal;
        
        return this;
    }
    
    /**
     * Sets how exactly raw values between the bounds are normalized.
     * 
     * @param function
     *            the function to use for normalization.
     * @return this object for method chaining.
     * @throws IllegalArgumentException if the normalization function is {@code null}.
     */
    protected AbstractNormalizedHeuristic withNormalizationFunction(
            final NormalizationFunction function) {
        
        if (function == null) {
            throw new IllegalArgumentException("Normalization function cannot be null.");
        }
        
        this.normalizationFunction = function;
        
        return this;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    // Getters
    
    /**
     * Returns the raw value at and beyond which raw values are normalized to 0.
     * 
     * @return best raw value.
     */
    public double getWorstRawValue() {
        return worstRawValue;
    }
    
    /**
     * Returns the raw value at and beyond which raw values are normalized to 1.
     * 
     * @return best raw value.
     */
    public double getBestRawValue() {
        return bestRawValue;
    }

    
    /////////////////////////////////////////////////////////////////////////////////////////////
    // IHeuristic Implementation
    
    /**
     * {@inheritDoc}
     */
    @Override
    public double normalized(final ElkNode comment, final ElkGraphElement element) {
        double rawValue = raw(comment, element);
        
        switch (normalizationFunction) {
        case LINEAR:
            return normalizeLinear(rawValue);
        case BINARY:
            return normalizeBinary(rawValue);
        default:
            assert false;
            return 0;
        }
    }

    
    /////////////////////////////////////////////////////////////////////////////////////////////
    // Normalization Functions
    
    /**
     * Normalizes the given raw value by interpolating linearly between the worst and best values.
     * 
     * @param raw
     *            the raw value.
     * @return the normalized value.
     */
    protected final double normalizeLinear(final double raw) {
        if (worstRawValue < bestRawValue) {
            if (raw <= worstRawValue) {
                return 0;
            } else if (raw >= bestRawValue) {
                return 1;
            } else {
                return (raw - worstRawValue) / (bestRawValue - worstRawValue);
            }
        } else if (bestRawValue < worstRawValue) {
            if (raw <= bestRawValue) {
                return 1;
            } else if (raw >= worstRawValue) {
                return 0;
            } else {
                return 1 - (raw - bestRawValue) / (worstRawValue - bestRawValue);
            }
        } else {
            return raw == bestRawValue ? 1 : 0;
        }
    }
    
    /**
     * Normalizes the given raw value by mapping everything up to the worst possible value to 1, and
     * everything beyond the worst value to 0.
     * 
     * @param raw
     *            the raw value.
     * @return the normalized value.
     */
    protected final double normalizeBinary(final double raw) {
        if (worstRawValue < bestRawValue) {
            return raw <= worstRawValue ? 0 : 1;
        } else if (bestRawValue < worstRawValue) {
            return raw >= worstRawValue ? 0 : 1;
        } else {
            return raw == bestRawValue ? 1 : 0;
        }
    }

}
