/*******************************************************************************
 * Copyright (c) 2020 Kiel University.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0  *******************************************************************************/
package org.eclipse.elk.alg.packing.rectangles.util;

import java.util.ArrayList;
import java.util.List;

public class BlockStack {
    private List<Block> blocks;
    private double x;
    private double y;
    private double width;
    private double height;
    
    public BlockStack(double x, double y) {
        blocks = new ArrayList<>();
        this.x = x;
        this.y = y;
    }
    
    public void addBlock(Block block) {
        blocks.add(block);
        this.width = Math.max(this.width, block.getWidth());
        this.height += block.getHeight();
    }
    
    public void setLocation(double x, double y) {
        double xDiff = x - this.x;
        double yDiff = y - this.y;
        for (Block block : blocks) {
            block.setLocation(block.getX() + xDiff, block.getY() + yDiff);
        }
        this.x = x;
        this.y = y;
        
    }
    
    public void expand(double additionalWidth, double additionalHeight) {
        int index = 0;
        double additionalHeightPerBlock = additionalHeight / blocks.size();
        for (Block block : blocks) {
            block.setLocation(block.getX(),  block.getY() + index * additionalHeightPerBlock);
            block.expand(this.getWidth() - block.getWidth() + additionalWidth, additionalHeightPerBlock);
            index++;
        }
    }

    /**
     * @return the blocks
     */
    public List<Block> getBlocks() {
        return blocks;
    }

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @return the width
     */
    public double getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public double getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(double height) {
        this.height = height;
    }
}
