/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    cds - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.math;

import org.eclipse.elk.core.util.IDataObject;

/**
 * Represents insets of a graphical object. Insets are space to be left to the object's borders when placing things
 * within the object.
 */
public class ElkInsets implements IDataObject, Cloneable {
    
    /** Serialization version ID. */
    private static final long serialVersionUID = -8033455628103978553L;
    
    /** Top insets. */
    private double top = 0.0;
    /** Bottom insets. */
    private double bottom = 0.0;
    /** Left insets. */
    private double left = 0.0;
    /** Right insets. */
    private double right = 0.0;
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Constructors
    
    /**
     * Create new empty insets.
     */
    public ElkInsets() {
        // This space intentionally left blank
    }
    
    /**
     * Create new insets initialized to the given values.
     * 
     * @param top top insets.
     * @param right right insets.
     * @param bottom bottom insets.
     * @param left left insets.
     */
    public ElkInsets(final double top, final double right, final double bottom, final double left) {
        setInsets(top, right, bottom, left);
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Accessors
    
    /**
     * Set all inset values at once.
     * 
     * @param newTop top insets.
     * @param newRight right insets.
     * @param newBottom bottom insets.
     * @param newLeft left insets.
     */
    public void setInsets(final double newTop, final double newRight, final double newBottom, final double newLeft) {
        this.top = newTop;
        this.bottom = newBottom;
        this.left = newLeft;
        this.right = newRight;
    }
    
    /**
     * Returns the top insets.
     */
    public double getTop() {
        return top;
    }

    /**
     * Sets the top insets.
     */
    public void setTop(final double top) {
        this.top = top;
    }

    /**
     * Returns the bottom insets.
     */
    public double getBottom() {
        return bottom;
    }

    /**
     * Sets the bottom insets.
     */
    public void setBottom(final double bottom) {
        this.bottom = bottom;
    }

    /**
     * Returns the left insets.
     */
    public double getLeft() {
        return left;
    }

    /**
     * Sets the left insets.
     */
    public void setLeft(final double left) {
        this.left = left;
    }

    /**
     * Returns the right insets.
     */
    public double getRight() {
        return right;
    }

    /**
     * Sets the right insets.
     */
    public void setRight(final double right) {
        this.right = right;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Things
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "(" + top + "," + right + "," + bottom + "," + left + ")";
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Cloneable
    
    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public ElkInsets clone() {
        return new ElkInsets(top, right, bottom, left);
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // IDataObject

    /* (non-Javadoc)
     * @see org.eclipse.elk.core.util.IDataObject#parse(java.lang.String)
     */
    @Override
    public void parse(final String string) {
        int start = 0;
        while (start < string.length() && isdelim(string.charAt(start), "([{\"' \t\r\n")) {
            start++;
        }
        int end = string.length();
        while (end > 0 && isdelim(string.charAt(end - 1), ")]}\"' \t\r\n")) {
            end--;
        }
        if (start >= end) {
            throw new IllegalArgumentException("The given string does not contain any numbers.");
        }
        String[] tokens = string.substring(start, end).split(",|;|\r|\n");
        if (tokens.length != 2) {
            throw new IllegalArgumentException("Exactly two numbers are expected, "
                    + tokens.length + " were found.");
        }
        try {
            // CHECKSTYLEOFF MagicNumber
            setTop(Double.parseDouble(tokens[0].trim()));
            setBottom(Double.parseDouble(tokens[1].trim()));
            setLeft(Double.parseDouble(tokens[2].trim()));
            setRight(Double.parseDouble(tokens[3].trim()));
            // CHECKSTYLEON MagicNumber
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "The given string contains parts that cannot be parsed as numbers." + exception);
        }
    }

    /**
     * Determine whether the given character is a delimiter.
     * 
     * @param c
     *            a character
     * @param delims
     *            a string of possible delimiters
     * @return true if {@code c} is one of the characters in {@code delims}
     */
    private static boolean isdelim(final char c, final String delims) {
        for (int i = 0; i < delims.length(); i++) {
            if (c == delims.charAt(i)) {
                return true;
            }
        }
        return false;
    }

}
