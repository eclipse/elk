/*******************************************************************************
 * Copyright (c) 2018, 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.packing.rectangles.seconditeration;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.alg.packing.rectangles.util.Block;
import org.eclipse.elk.alg.packing.rectangles.util.RectRow;
import org.eclipse.elk.graph.ElkNode;

/**
 * Class that offers methods that calculate the initial placement of rectangles in {@link RowFillingAndCompaction}.
 * 
 * @see RowFillingAndCompaction
 */
public final class InitialPlacement {

    //////////////////////////////////////////////////////////////////
    // Private Constructor.
    private InitialPlacement() {
    }

    //////////////////////////////////////////////////////////////////
    // Static methods.
    /**
     * Simply places the rectangles as {@link RectRow}s onto the drawing area, bounded by the calculated bounding box
     * width.
     * 
     * @param rectangles The rectangles to be placed.
     * @param boundingWidthThe width of the bounding box.
     * @param nodeNodeSpacing The spacing between two nodes.
     * @return returns the rows in which the rectangles were placed.
     */
    protected static List<RectRow> place(final List<ElkNode> rectangles, final double boundingWidth, final double nodeNodeSpacing) {
        List<RectRow> rows = new ArrayList<RectRow>();
        RectRow row = new RectRow(0);
        double drawingHeight = 0;
        row.addBlock(new Block(0, 0, row, nodeNodeSpacing));

        for (ElkNode rect : rectangles) {
            double potentialRowWidth = row.getWidth() + rect.getWidth();
            if (potentialRowWidth > boundingWidth) {
                Block block = row.getLastBlock();
                // Check whether the block can have a new row
                if (placeRectInBlock(row, block, rect, boundingWidth, nodeNodeSpacing)) {
                    continue;
                }
                // Block can not have a new row, so a new row has to be added
                drawingHeight += row.getHeight();
                rows.add(row);
                row = new RectRow(drawingHeight);
                row.addBlock(new Block(0, row.getY(), row, nodeNodeSpacing));
            }
            
            // Check whether current rectangle can be added to the last block
            Block block = row.getLastBlock();
            if (block.getChildren().isEmpty() || isSimilarHeight(block, rect, nodeNodeSpacing)) {
                block.addChild(rect);
            } else {
                Block newBlock = new Block(block.getX() + block.getWidth(), row.getY(), row, nodeNodeSpacing);
                row.addBlock(newBlock);
                newBlock.addChild(rect);
            }
        }
        rows.add(row);
        return rows;
    }
    
    /**
     * Check whether a rectangle has a similar height as the other rectangles in the block and add it too it depending on
     * the available space.
     * @param row The row.
     * @param block The block.
     * @param rect The rectangle.
     * @param boundingWidth The bounding width of the row.
     * @param nodeNodeSpacing The spacing between two nodes.
     * @return true, if the rectangle was successfully added.
     */
    public static boolean placeRectInBlock(final RectRow row, final Block block, final ElkNode rect,
            final double boundingWidth, final double nodeNodeSpacing) {
        if (isSimilarHeight(block, rect, nodeNodeSpacing)) {
            if (rect.getWidth() + nodeNodeSpacing <= boundingWidth - block.getLastRowNewX() &&
                    (block.getLastRowY() - row.getY() + rect.getHeight() + nodeNodeSpacing <= row.getHeight() || row.getChildren().size() == 1)) {
                // Case it fits in a row in the same block
                block.addChild(rect);
                return true;
            } else if (rect.getWidth() <= boundingWidth - block.getX() &&
                    (block.getHeight() + rect.getHeight() + nodeNodeSpacing <= row.getHeight() || row.getChildren().size() == 1)) {
                // Case a new row in the block can be opened
                block.addChildInNewRow(rect);
                return true;
            }
        }
        return false;
        
    }
    
    /**
     * Checks whether a rectangle has a similar height as the other rectangles in a block.
     * @param block The block.
     * @param rect The rectangle.
     * @param nodeNodeSpacing The spacing between two nodes.
     * @return true if the height of the rectangle is similar to the rectangles in the block.
     */
    public static boolean isSimilarHeight(final Block block, final ElkNode rect, final double nodeNodeSpacing) {
        if (rect.getHeight() + nodeNodeSpacing >= block.getSmallestRectHeight() && rect.getHeight() + nodeNodeSpacing <= block.getMinHeight()) {
            return true;
        } else {
            return block.getAverageHeight() * 0.5 <= rect.getHeight() + nodeNodeSpacing &&
                    block.getAverageHeight() * 1.5 >= rect.getHeight() + nodeNodeSpacing;
        }
    }
}
