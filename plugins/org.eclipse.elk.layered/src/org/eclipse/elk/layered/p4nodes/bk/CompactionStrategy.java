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
package org.eclipse.elk.layered.p4nodes.bk;

/**
 * Specifies how the compaction step of the {@link BKNodePlacer} should be executed.
 * 
 * @author uru
 */
public enum CompactionStrategy {
    /** As specified in the original paper. */
    CLASSIC,
    /** An integrated method trying to increase the number of straight edges. */
    IMPROVE_STRAIGHTNESS,
}
