/*******************************************************************************
 * Copyright (c) 2019 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate.loops;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LGraphUtil;
import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.Direction;

/**
 * Represents the labels associated with a {@link SelfHyperLoop}. The labels are gathered as edges are added to the
 * hyper loop.
 */
public class SelfHyperLoopLabels {
    
    /** The labels represented by this instance. */
    private final List<LLabel> lLabels = new ArrayList<>();
    /** The size required to place all labels. */
    private final KVector size = new KVector();
    /** The graph's layout direction. Influences the way the labels will be placed. */
    private final Direction layoutDirection;
    /** Space to leave between adjacent labels. */
    private final double labelLabelSpacing;
    
    /**
     * Creates a new instance for the given {@link SelfHyperLoop}.
     */
    public SelfHyperLoopLabels(final SelfHyperLoop slLoop) {
        // Initialize our properties from the graph
        LNode lNode = slLoop.getSLHolder().getLNode();
        
        layoutDirection = lNode.getGraph().getProperty(LayeredOptions.DIRECTION);
        labelLabelSpacing = LGraphUtil.getIndividualOrInherited(lNode, LayeredOptions.SPACING_LABEL_LABEL);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Accessors
    
    /**
     * Adds the given collection of labels to this instance.
     */
    public void addLLabels(final Collection<LLabel> newLLabels) {
        for (LLabel newLLabel : newLLabels) {
            lLabels.add(newLLabel);
            updateSize(newLLabel);
        }
    }

    /**
     * Returns the labels represented by this instance.
     */
    public List<LLabel> getLLabels() {
        return lLabels;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Label Placement
    
    /**
     * Updates our size under the assumption that the given label was just added to this instance.
     */
    private void updateSize(final LLabel newLLabel) {
        assert lLabels.get(lLabels.size() - 1) == newLLabel;
        
        KVector newLLabelSize = newLLabel.getSize();
        
        if (layoutDirection.isHorizontal()) {
            // The labels will be stacked vertically
            size.x = Math.max(size.x, newLLabelSize.x);
            size.y += newLLabelSize.y;
            
            // Add a label-label spacing if we already had labels
            if (lLabels.size() > 1) {
                size.y += labelLabelSpacing;
            }
            
        } else {
            // The labels will be stacked horizontally
            size.x += newLLabelSize.x;
            size.y = Math.max(size.y, newLLabelSize.y);
            
            // Add a label-label spacing if we already had labels
            if (lLabels.size() > 1) {
                size.x += labelLabelSpacing;
            }
            
        }
    }
    
}
