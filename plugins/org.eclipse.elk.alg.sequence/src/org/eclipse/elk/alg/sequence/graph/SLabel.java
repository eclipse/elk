/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.sequence.graph;

/**
 * A label displays text for a graphical element.
 */
public class SLabel extends SShape {

    private static final long serialVersionUID = -1404084928180577612L;
    
    /** The text displayed by the label. */
    private String text;
    
    
    /**
     * Creates a label with empty text.
     */
    public SLabel() {
        this("");
    }
    
    /**
     * Creates a label.
     * 
     * @param thetext text of the label
     */
    public SLabel(final String thetext) {
        this.text = thetext;
    }
    
    /**
     * Returns the text of the label.
     * 
     * @return the text
     */
    public String getText() {
        return text;
    }
    
    @Override
    public String toString() {
        if (text == null) {
            return "l_" + id;
        } else {
            return "l_" + text;
        }
    }

}
