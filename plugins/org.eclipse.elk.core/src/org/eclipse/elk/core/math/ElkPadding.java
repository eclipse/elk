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
 * Represents padding of a graphical object. padding are space to be left to the object's borders when placing things
 * within the object.
 */
public class ElkPadding implements IDataObject, Cloneable {
    
    /** Serialization version ID. */
    private static final long serialVersionUID = -8033455628103978553L;
    
    /** Top padding. */
    private double top = 0.0;
    /** Bottom padding. */
    private double bottom = 0.0;
    /** Left padding. */
    private double left = 0.0;
    /** Right padding. */
    private double right = 0.0;
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Constructors
    
    /**
     * Create new empty padding.
     */
    public ElkPadding() {
        // This space intentionally left blank
    }
    
    /**
     * Create new padding initialized to the given values.
     * 
     * @param top top padding.
     * @param right right padding.
     * @param bottom bottom padding.
     * @param left left padding.
     */
    public ElkPadding(final double top, final double right, final double bottom, final double left) {
        setPadding(top, right, bottom, left);
    }
    
    /**
     * Create new padding using the same padding for both sides of a common dimension.
     * 
     * @param leftRight value used for the left and right padding.
     * @param topBottom value used for the top and bottom padding.
     */
    public ElkPadding(final double leftRight, final double topBottom) {
        this(topBottom, leftRight, topBottom, leftRight);
    }
    
    /**
     * Create new padding using the same value for every side. 
     * 
     * @param any inset value for every side.
     */
    public ElkPadding(final double any) {
        this(any, any, any, any);
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Accessors
    
    /**
     * Set all padding values at once.
     * 
     * @param newTop top padding.
     * @param newRight right padding.
     * @param newBottom bottom padding.
     * @param newLeft left padding.
     */
    public void setPadding(final double newTop, final double newRight, final double newBottom, final double newLeft) {
        if (newTop < 0 || newRight < 0 || newBottom < 0 || newLeft < 0) {
            throw new IllegalArgumentException("Negative padding value not allowed");
        }
        this.top = newTop;
        this.bottom = newBottom;
        this.left = newLeft;
        this.right = newRight;
    }
    
    /**
     * Returns the top padding.
     */
    public double getTop() {
        return top;
    }

    /**
     * Sets the top padding.
     */
    public void setTop(final double top) {
        this.top = top;
    }

    /**
     * Returns the bottom padding.
     */
    public double getBottom() {
        return bottom;
    }

    /**
     * Sets the bottom padding.
     */
    public void setBottom(final double bottom) {
        this.bottom = bottom;
    }

    /**
     * Returns the left padding.
     */
    public double getLeft() {
        return left;
    }

    /**
     * Sets the left padding.
     */
    public void setLeft(final double left) {
        this.left = left;
    }

    /**
     * Returns the right padding.
     */
    public double getRight() {
        return right;
    }

    /**
     * Sets the right padding.
     */
    public void setRight(final double right) {
        this.right = right;
    }
    
    /**
     * @return the combined horizontal padding, i.e. {@link #getLeft()} + {@link #getRight()}.  
     */
    public double getHorizontal() {
        return this.getLeft() + this.getRight();
    }
    
    /**
     * @return the combined vertical padding, i.e. {@link #getTop()} + {@link #getBottom()}.  
     */
    public double getVertical() {
        return this.getTop() + this.getBottom();
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
    public ElkPadding clone() {
        return new ElkPadding(top, right, bottom, left);
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
