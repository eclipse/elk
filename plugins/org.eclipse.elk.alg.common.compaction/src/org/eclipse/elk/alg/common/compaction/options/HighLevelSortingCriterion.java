/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.common.compaction.options;

/**
 * <p>
 * Possible primary sorting criteria for the processing order of polyominoes.
 * </p>
 */
public enum HighLevelSortingCriterion {
NUM_OF_EXTERNAL_SIDES_THAN_NUM_OF_EXTENSIONS_LAST,
CORNER_CASES_THAN_SINGLE_SIDE_LAST
}
