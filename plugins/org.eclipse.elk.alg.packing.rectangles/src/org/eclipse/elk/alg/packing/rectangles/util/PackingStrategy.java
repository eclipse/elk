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
package org.eclipse.elk.alg.packing.rectangles.util;

/**
 * Specifies the strategy employed to pack rectangles sequentially during {@link FirstIteration}.
 * 
 * @author dalu
 */
public enum PackingStrategy {
    /** Aspect ratio-driven packing heuristic. */
    ASPECT_RATIO_DRIVEN,
    /** Max scale-driven packing heuristic. */
    MAX_SCALE_DRIVEN,
    /** Area driven packing heuristic. */
    AREA_DRIVEN;
}
