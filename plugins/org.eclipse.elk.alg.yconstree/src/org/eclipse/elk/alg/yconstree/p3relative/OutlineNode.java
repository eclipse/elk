/*******************************************************************************
 * Copyright (c) 2023 claas and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.yconstree.p3relative;

/**
 * @author claas
 *
 */
public class OutlineNode {
    
    private double relativeX;
    private double relativeY;
    private OutlineNode next;
    
    OutlineNode(double relativeX, double relativeY, OutlineNode next){
        this.setRelativeX(relativeX);
        this.setRelativeY(relativeY);
        this.setNext(next);
    }

    /**
     * @return the relativeY
     */
    public double getRelativeY() {
        return relativeY;
    }

    /**
     * @param relativeY the relativeY to set
     */
    public void setRelativeY(double relativeY) {
        this.relativeY = relativeY;
    }

    /**
     * @return the relativeX
     */
    public double getRelativeX() {
        return relativeX;
    }

    /**
     * @param relativeX the relativeX to set
     */
    public void setRelativeX(double relativeX) {
        this.relativeX = relativeX;
    }

    /**
     * @return the next
     */
    public OutlineNode getNext() {
        return next;
    }

    /**
     * @param next the next to set
     */
    public void setNext(OutlineNode next) {
        this.next = next;
    }
    
    /**
     * @return a string representation of this objects relative coordinates
     */
    public String toString() {
        return "X:" + relativeX + ",  Y:" + relativeY;
    }
    
    /**
     * 
     * @return if this OutlineNode has no "next"
     */
    public boolean isLast() {
        return next == null;
    }
    
    public void printFullOutline() {
        System.out.println(this.toString());
        if(!this.isLast()) {
            this.getNext().printFullOutline();
        }
    }
}
