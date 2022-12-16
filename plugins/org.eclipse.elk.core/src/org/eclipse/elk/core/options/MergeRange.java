/*******************************************************************************
 * Copyright (c) 2022 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.core.options;

import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.util.IDataObject;

/**
 * Stores the range for label merging. The range determines which labels will be tried to merge.
 * @author jep
 *
 */
public class MergeRange implements IDataObject, Cloneable {
    
    /** The serial version UID. */
    private static final long serialVersionUID = 2491028146866390909L;
    
    public int start = 0;
    public int end = 0;
    
    /**
     * Creates a new instance with all fields set to 0.
     */
    public MergeRange() {}
    
    /**
     * Creates a new instance with the given values.
     * @param start The start of the range.
     * @param end The end of the range.
     */
    public MergeRange(final int start, final int end) {
        this.start = start;
        this.end = end;
    }
    
    /**
     * @return the start
     */
    public int getStart() {
        return this.start;
    }
    
    /**
     * @return the end
     */
    public int getEnd() {
        return this.end;
    }

    @Override
    public void parse(String string) {
        int start = 0;
        while (start < string.length() && isdelim(string.charAt(start), "([{\"' \t\r\n")) {
            start++;
        }
        int end = string.length();
        while (end > 0 && isdelim(string.charAt(end - 1), ")]}\"' \t\r\n")) {
            end--;
        }
        if (start < end) {
            String[] tokens = string.substring(start, end).split(",|;");
            try {
                for (String token : tokens) {
                    String[] keyandvalue = token.split("=");
                    if (keyandvalue.length != 2) {
                        throw new IllegalArgumentException("Expecting a list of key-value pairs.");
                    }
                    String key = keyandvalue[0].trim();
                    int value = Integer.parseInt(keyandvalue[1].trim());
                    if (key.equals("start")) {
                        this.start = value;
                    } else if (key.equals("end")) {
                        this.end = value;
                    }
                }
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException(
                        "The given string contains parts that cannot be parsed as numbers." + exception);
            }
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

    /**
     * Creates a new instance with all fields set to the value of {@code other}.
     * 
     * @param other
     *            merge range object from which to copy the values.
     */
    public MergeRange(final MergeRange other) {
        this(other.start, other.end);
    }


    @Override
    public MergeRange clone() {
        return new MergeRange(this);
    }
    
}
