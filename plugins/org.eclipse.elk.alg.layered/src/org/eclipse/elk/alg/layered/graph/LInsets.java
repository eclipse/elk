/*******************************************************************************
 * Copyright (c) 2011, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.graph;

/**
 * Stores the insets of an object in {@code double} precision.
 * 
 * @author cds
 * @kieler.design proposed by msp
 * @kieler.rating yellow 2013-03-22 review KI-35 by chsch, grh
 */
public class LInsets {
    // Allow public fields in these utility classes.
    // CHECKSTYLEOFF VisibilityModifier
    
    /**
     * The inset from the top.
     */
    public double top = 0.0;
    
    /**
     * The inset from the bottom.
     */
    public double bottom = 0.0;
    
    /**
     * The inset from the left.
     */
    public double left = 0.0;
    
    /**
     * The inset from the right.
     */
    public double right = 0.0;
    
    
    /**
     * Creates a new instance with all fields set to {@code 0.0}.
     */
    public LInsets() {
        
    }
    
    /**
     * Creates a new instance initialized with the given values.
     * 
     * @param top the inset from the top.
     * @param left the inset from the left.
     * @param bottom the inset from the bottom.
     * @param right the inset from the right.
     */
    public LInsets(final double top, final double left, final double bottom, final double right) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }
    
    
    /**
     * Sets all four insets at once.
     * 
     * @param newTop the inset from the top.
     * @param newLeft the inset from the left.
     * @param newBottom the inset from the bottom.
     * @param newRight the inset from the right.
     */
    public void set(final double newTop, final double newLeft, final double newBottom,
            final double newRight) {
        
        this.top = newTop;
        this.left = newLeft;
        this.bottom = newBottom;
        this.right = newRight;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof LInsets) {
            LInsets other = (LInsets) obj;
            
            return this.top == other.top
                && this.bottom == other.bottom
                && this.left == other.left
                && this.right == other.right;
        } else {
            return false;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        // I don't care very much to define explicit constants for this calculation.
        // CHECKSTYLEOFF MagicNumber
        
        int code1 = java.lang.Double.valueOf(left).hashCode() << 16;
        code1 |= java.lang.Double.valueOf(bottom).hashCode() & 0xffff;
        
        int code2 = java.lang.Double.valueOf(right).hashCode() << 16;
        code2 |= java.lang.Double.valueOf(top).hashCode() & 0xffff;
        
        return code1 ^ code2;
        
        // CHECKSTYLEON MagicNumber
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Insets[top="  + top
            + ",left=" + left
            + ",bottom=" + bottom
            + ",right=" + right + "]";
    }
    
    /**
     * Copy insets values from another double valued insets.
     * 
     * @param other another insets
     */
    public void copy(final LInsets other) {
        this.left = other.left;
        this.right = other.right;
        this.top = other.top;
        this.bottom = other.bottom;
    }
}
