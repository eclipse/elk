/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.util.nodespacing.internal.cellsystem;

/**
 * Enumeration of three container areas that can be used by containers that use three areas.
 * 
 * @see StripContainerCell
 * @see GridContainerCell
 */
public enum ContainerArea {
    /** The top row or left column of the container. */
    BEGIN,
    /** The center row or column of the container. */
    CENTER,
    /** The bottom row or right column of the container. */
    END;
}