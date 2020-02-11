/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
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
import org.eclipse.elk.alg.packing.rectangles.util.RectStack;
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
    // Main method.
    /**
     * Simply places the rectangles as {@link RectRow}s onto the drawing area, bounded by the calculated bounding box
     * width.
     * 
     * @param rectangles
     *            rectangles to be placed.
     * @param boundingWidth
     *            bounding box width.
     * @param nodeNodeSpacing
     *            The spacing between two nodes.
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
            if (block.getChildren().isEmpty() || isSimilarHeight(block, rect)) {
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
    
    public static boolean placeRectInBlock(final RectRow row, final Block block, final ElkNode rect,
            final double boundingWidth, final double nodeNodeSpacing) {
        if (isSimilarHeight(block, rect)) {
            if (rect.getWidth() + nodeNodeSpacing <= boundingWidth - block.getRowX() &&
                    (block.getRowY() + rect.getHeight() + nodeNodeSpacing <= row.getHeight() || row.getChildren().size() == 1)) {
                // Case it fits in a row in the same block
                block.addChild(rect);
                return true;
            } else if (rect.getWidth() + nodeNodeSpacing <= boundingWidth - block.getX() &&
                    (block.getHeight() + rect.getHeight() + nodeNodeSpacing <= row.getHeight() || row.getChildren().size() == 1)) {
                // Case a new row in the block can be opened
                block.addChildInNewRow(rect);
                return true;
            }
        }
        return false;
        
    }
    
    public static boolean isSimilarHeight(Block block, ElkNode rect) {
        if (block.getAverageHeight() * 0.6 >= block.getSmallestRectHeight() || block.getMinHeight() * 0.6 >= block.getAverageHeight()) {
            return rect.getHeight() >= block.getSmallestRectHeight() && rect.getHeight() <= block.getMinHeight();
        } else {
            return block.getAverageHeight() * 0.6 <= rect.getHeight() && block.getAverageHeight() >= rect.getHeight() * 0.6;
        }
    }
}
