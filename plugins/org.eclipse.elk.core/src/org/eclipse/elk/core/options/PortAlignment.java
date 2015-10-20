/*******************************************************************************
 * Copyright (c) 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.options;

/**
 * Defines the distribution of ports.
 *
 * @author csp
 */
public enum PortAlignment {

    /** The alignment is not set. */
    UNDEFINED,

    /** Ports are evenly distributed. */
    JUSTIFIED,

    /** Ports are placed at the most top respectively left position with minimal spacing. */
    BEGIN,

    /** Ports are centered with minimal spacing. */
    CENTER,

    /** Ports are placed at the most top respectively left position with minimal spacing. */
    END;

}
