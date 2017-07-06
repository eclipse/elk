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
package org.eclipse.elk.alg.common.compaction.oned;

import org.eclipse.elk.core.options.Direction;

/**
 *
 */
@FunctionalInterface
public interface ILockFunction {
    /**
     * @param node
     * @param direction
     * @return whether the passed node may move in the passed direction.
     */
    boolean isLocked(CNode node, Direction direction);
}
