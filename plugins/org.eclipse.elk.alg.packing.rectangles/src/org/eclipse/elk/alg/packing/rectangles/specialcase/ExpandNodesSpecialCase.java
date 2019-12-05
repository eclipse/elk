/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.packing.rectangles.specialcase;

import java.util.List;

import org.eclipse.elk.graph.ElkNode;

/**
 * Offers method to expand nodes in the special case.
 * 
 * @see SpecialCasePlacer
 */
public final class ExpandNodesSpecialCase {

    private ExpandNodesSpecialCase() {
    }

    /**
     * Expands nodes so the bounding box, given by width and height of the bounding box of the packing, is filled. The
     * biggest rectangle does not need to be resized.
     * 
     * @param before
     *            rectangles that come before the biggest rectangle.
     * @param after
     *            rectangles that come after the biggest rectangle.
     * @param height
     *            height of the bounding box or the biggest rectangle.
     * @param howManyColumnsAfter
     *            amount of columns that come before the big rectangle.
     * @param howManyColumnsBefore
     *            amount of columns that come after the big rectangle.
     * @param widthBefore
     *            width of the widest rectangle of the before parameter.
     * @param widthAfter
     *            width of the widest rectangle of the after parameter.
     */
    protected static void expand(final List<ElkNode> before, final List<ElkNode> after, final double height,
            final int howManyColumnsBefore, final int howManyColumnsAfter, final double widthBefore,
            final double widthAfter) {
        // width
        for (ElkNode rect : before) {
            rect.setWidth(widthBefore);
        }
        for (ElkNode rect : after) {
            rect.setWidth(widthAfter);
        }

        // height
        for (ElkNode rect : before.subList(before.size() - howManyColumnsBefore, before.size())) {
            rect.setHeight(height - rect.getY());
        }
        for (ElkNode rect : after.subList(after.size() - howManyColumnsAfter, after.size())) {
            rect.setHeight(height - rect.getY());
        }
    }
}
