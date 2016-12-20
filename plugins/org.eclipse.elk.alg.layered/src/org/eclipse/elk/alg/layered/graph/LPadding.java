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
package org.eclipse.elk.alg.layered.graph;

import org.eclipse.elk.core.math.ElkPadding;

/**
 * A mere copycat of {@link ElkPadding}. We use it for a single reason: to make a clear distinction between ELK's
 * padding and the (intermediate) padding used during the execution of ELK Layered.
 *
 * @see LMargin
 */
public class LPadding extends ElkPadding {
    
    /** The serial version UID. */
    private static final long serialVersionUID = -73916295796649526L;

    /**
     * Creates a new instance with all fields set to {@code 0.0}.
     */
    public LPadding() {
        super();
    }

}
