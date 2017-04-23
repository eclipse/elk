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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.math.ElkRectangle;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.util.adapters.GraphAdapters.LabelAdapter;
import org.eclipse.elk.core.util.nodespacing.internal.HorizontalLabelAlignment;
import org.eclipse.elk.core.util.nodespacing.internal.NodeLabelLocation;
import org.eclipse.elk.core.util.nodespacing.internal.VerticalLabelAlignment;

import com.google.common.collect.Lists;

/**
 * A cell which manages the size and placement of labels. It inserts a customizable gap between each of the labels and
 * calculates its minimum size to be the area required to place all of its labels. The horizontal and vertical
 * alignment control what happens if the cell's actual size is larger than required by its labels. Call
 * {@link #applyLabelLayout()} to cause the cell to go ahead and assign positions to all of its labels.
 */
public class LabelCell extends Cell {
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Properties

    /** Horizontal alignment of labels. */
    private HorizontalLabelAlignment horizontalAlignment = HorizontalLabelAlignment.CENTER;
    /** Vertical alignment of labels. */
    private VerticalLabelAlignment verticalAlignment = VerticalLabelAlignment.CENTER;
    /** The gap inserted between two consecutive labels. */
    private final double gap;
    /** The labels in our cell. */
    private List<LabelAdapter<?>> labels = Lists.newArrayListWithCapacity(2);
    /** Our minimum size. This is basically the space we need at minimum to place our labels. */
    private KVector minimumContentAreaSize = new KVector();
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Constructors
    
    /**
     * Constructs a new instance with the given properties.
     * 
     * @param gap
     *            gap between labels.
     */
    public LabelCell(final double gap) {
        this.gap = gap;
    }
    
    /**
     * Constructs a new instance with the given properties.
     * 
     * @param gap
     *            gap between labels.
     * @param nodeLabelLocation
     *            the label location represented by this cell. This determines things like alignment.
     */
    public LabelCell(final double gap, final NodeLabelLocation nodeLabelLocation) {
        this.gap = gap;
        this.horizontalAlignment = nodeLabelLocation.getHorizontalAlignment();
        this.verticalAlignment = nodeLabelLocation.getVerticalAlignment();
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Getters / Setters
    
    /**
     * Returns the horizontal alignment of labels.
     */
    public HorizontalLabelAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    /**
     * Sets the horizontal alignment of labels.
     */
    public void setHorizontalAlignment(final HorizontalLabelAlignment horizontalAlignment) {
        Objects.requireNonNull(horizontalAlignment, "Horizontal alignment cannot be null");
        this.horizontalAlignment = horizontalAlignment;
    }

    /**
     * Returns the vertical alignment of labels.
     */
    public VerticalLabelAlignment getVerticalAlignment() {
        return verticalAlignment;
    }

    /**
     * Sets the vertical alignment of labels.
     */
    public void setVerticalAlignment(final VerticalLabelAlignment verticalAlignment) {
        Objects.requireNonNull(verticalAlignment, "Vertical alignment cannot be null");
        this.verticalAlignment = verticalAlignment;
    }

    /**
     * Returns the list of labels in this cell. The list cannot be modified. To add labels, call
     * {@link #addLabel(LabelAdapter)}.
     * 
     * @return unmodifiable list of labels.
     */
    public List<LabelAdapter<?>> getLabels() {
        return Collections.unmodifiableList(labels);
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Cell
    
    /* (non-Javadoc)
     * @see org.eclipse.elk.core.util.nodespacing.internal.cells.Cell#getMinimumWidth()
     */
    @Override
    public double getMinimumWidth() {
        ElkPadding padding = getPadding();
        return minimumContentAreaSize.x + padding.left + padding.right;
    }

    /* (non-Javadoc)
     * @see org.eclipse.elk.core.util.nodespacing.internal.cells.Cell#getMinimumHeight()
     */
    @Override
    public double getMinimumHeight() {
        ElkPadding padding = getPadding();
        return minimumContentAreaSize.y + padding.top + padding.bottom;
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Adding Labels
    
    /**
     * Adds a label to this cell.
     * 
     * @param label
     *            the label.
     */
    public void addLabel(final LabelAdapter<?> label) {
        labels.add(label);
        
        // Update our minimum size
        KVector labelSize = label.getSize();
        minimumContentAreaSize.x = Math.max(minimumContentAreaSize.x, labelSize.x);
        minimumContentAreaSize.y += labelSize.y;
        
        // If this is not our first label, insert a gap
        if (labels.size() > 1) {
            minimumContentAreaSize.y += gap;
        }
    }
    
    /**
     * Returns whether there are any labels in this cell.
     * 
     * @return {@code true} if this cell contains labels, {@code false} otherwise.
     */
    public boolean hasLabels() {
        return labels.size() > 0;
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Label Layout
    
    /**
     * Actually applies coordinates to the labels based on this cell's content area rectangle.
     */
    public void applyLabelLayout() {
        ElkRectangle cellRect = getCellRectangle();
        ElkPadding cellPadding = getPadding();
        
        // Calculate our starting y coordinate
        double yPos = cellRect.y;
        
        if (verticalAlignment == VerticalLabelAlignment.CENTER) {
            yPos += (cellRect.height - minimumContentAreaSize.y) / 2;
        } else if (verticalAlignment == VerticalLabelAlignment.BOTTOM) {
            yPos += cellRect.height - minimumContentAreaSize.y;
        }
        
        // Place them labels, I say!
        for (LabelAdapter<?> label : labels) {
            KVector labelSize = label.getSize();
            KVector labelPos = new KVector();
            
            // Y coordinate
            labelPos.y = yPos;
            yPos += labelSize.y + gap;
            
            // X coordinate
            switch (horizontalAlignment) {
            case LEFT:
                labelPos.x = cellRect.x + cellPadding.left;
                break;
                
            case CENTER:
                labelPos.x = cellRect.x + cellPadding.left + (cellRect.width - labelSize.x) / 2;
                break;
                
            case RIGHT:
                labelPos.x = cellRect.x + cellRect.width - cellPadding.right - labelSize.x;
                break;
            }
            
            // Apply position
            label.setPosition(labelPos);
        }
    }
    
}
