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

import java.awt.geom.Rectangle2D;

import org.eclipse.elk.graph.ElkNode;

/**
 * Determines if a comment is eligible for attachment based on its size. Use the methods named
 * {@code withXXX} to configure the filter.
 */
public class SizeFilter implements IFilter {
    
    /** The bounds provider to use. */
    private IBoundsProvider boundsProvider = new ElkGraphBoundsProvider();
    /** The maximum area for a comment to still be eligible. */
    private double maxArea = -1;
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    // Configuration
    
    /**
     * Configures the filter to consider comments up to the given are to be attachable.
     * 
     * <p>
     * If this method is not called, no comment is considered eligible for attachment.
     * </p>
     * 
     * @param area
     *            the maximum possible area.
     * @return this object for method chaining.
     */
    public SizeFilter withMaximumArea(final double area) {
        if (area < 0) {
            throw new IllegalArgumentException("Maximum area must be >= 0.");
        }
        
        maxArea = area;
        
        return this;
    }
    
    /**
     * Configures the filter to use the given bounds provider to determine the bounds of
     * comments.
     * 
     * <p>
     * If this method is not called, the {@link ElkGraphBoundsProvider} is used by default.
     * </p>
     * 
     * @param provider
     *            the bounds provider to use.
     * @return this object for method chaining.
     */
    public SizeFilter withBoundsProvider(final IBoundsProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("Bounds provider must not be null.");
        }
        
        this.boundsProvider = provider;
        return this;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    // IEligibilityFilter

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean eligibleForAttachment(final ElkNode comment) {
        Rectangle2D.Double bounds = boundsProvider.boundsFor(comment);
        return bounds.height * bounds.width <= maxArea;
    }

}
