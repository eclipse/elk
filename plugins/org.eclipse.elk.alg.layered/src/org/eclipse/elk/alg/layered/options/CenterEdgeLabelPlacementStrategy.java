/*******************************************************************************
 * Copyright (c) 2016, 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.options;

/**
 * Strategies regarding how to place center edge labels. Implementation-wise, the placement strategy influences which
 * layer a center edge label dummy is moved into.
 */
public enum CenterEdgeLabelPlacementStrategy {
    
    /** Consider all layers a labeled edge crosses and place its label dummy in the one at the median spot. */
    MEDIAN_LAYER,
    /** Place a label dummy in the widest of all layers its edge crosses. */
    WIDEST_LAYER,
    /** Places the label dummy in a layer that we think will be closest to the edge's physical center later. */
    EDGE_CENTER;
    
    
    /**
     * Checks whether this strategy is based on information about the width of layers.
     * 
     * @return {@code true} iff that is the case.
     */
    public boolean usesLabelSizeInformation() {
        return this == WIDEST_LAYER || this == EDGE_CENTER;
    }
    
}