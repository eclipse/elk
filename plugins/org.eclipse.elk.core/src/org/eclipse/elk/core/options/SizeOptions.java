/*******************************************************************************
 * Copyright (c) 2012, 2015 Kiel University and others.
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
 * Size constraint options that modify how size constraints of a node are applied. Interpreting a
 * size option set on a node only makes sense when its size constraint is taken into consideration
 * as well. <b>This enumeration is not set directly on {@link CoreOptions#SIZE_OPTIONS}; instead,
 * an {@code EnumSet} over this enumeration is used there.</b>
 * 
 * <p><i>Note:</i> Layout algorithms may only support a subset of these options.</p>
 *
 * @author cds
 */
public enum SizeOptions {
    
    /**
     * If no minimum size is set on an element, the minimum size options are assumed to be some
     * default value determined by the particular layout algorithm. This option only makes sense
     * if the {@link SizeConstraint#MINIMUM_SIZE} constraint is set.
     */
    DEFAULT_MINIMUM_SIZE,
    
    /**
     * If this option is set and padding are computed by the algorithm, the minimum size plus the
     * computed padding are a lower bound on the node size. If this option is not set, the minimum size
     * will be applied to the node's whole size regardless of any computed padding. Note that,
     * depending on the algorithm, this option may only apply to non-hierarchical nodes. This option
     * only makes sense if the {@link SizeConstraint#MINIMUM_SIZE} constraint is set.
     */
    MINIMUM_SIZE_ACCOUNTS_FOR_PADDING,
    
    /**
     * With this option set, the padding of nodes will be computed and returned as part of the
     * algorithm's result. If port labels or node labels are placed, they may influence the size of
     * the padding. Note that, depending on the algorithm, this option may only apply to
     * non-hierarchical nodes. This option is independent of the size constraint set on a node.
     * This options is also independent from {@link #APPLY_ADDITIONAL_PADDING}. If both are set, one has to deal
     * with the effectively doubled padding.
     */
    COMPUTE_PADDING;
    
    /**
     * Returns the enumeration value related to the given ordinal.
     * 
     * @param i ordinal value
     * @return the related enumeration value
     */
    public static SizeOptions valueOf(final int i) {
        return values()[i];
    }
}
