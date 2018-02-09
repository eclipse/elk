/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.sequence;

/**
 * Keeps a bunch of constants used throughout the algorithm. Most of this should probably be phased out
 * and replaced by proper layout options.
 */
public final class SequenceLayoutConstants {
    
    // TODO All these constants should really disappear or at least be more sensibly named
    
    /** Constant that is needed to calculate some offsets. */
    public static final int TEN = 10;
    /** Constant that is needed to calculate some offsets. */
    public static final int TWENTY = 20;
    /** Constant that is needed to calculate some offsets. */
    public static final int FOURTY = 40;
    /** The vertical spacing between message and label. */
    public static final int LABELSPACING = 5;
    /** The horizontal margin for message labels. */
    public static final int LABEL_MARGIN = 10;
    /** The minimum height of an execution. */
    public static final int MIN_EXECUTION_HEIGHT = 20;
    /** The width of executions. This could well be turned into a layout option at some point. */
    public static final int EXECUCTION_WIDTH = 16;
    

    /**
     * No instantiation.
     */
    private SequenceLayoutConstants() {
    }

}
