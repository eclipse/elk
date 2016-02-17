/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.comments;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;

import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.graph.KNode;

/**
 * An {@link IBoundsProvider} that simply returns the bounds defined in the node's shape layout.
 */
public class ShapeLayoutBoundsProvider implements IBoundsProvider {

    /**
     * {@inheritDoc}
     */
    @Override
    public Double boundsFor(final KNode node) {
        KShapeLayout shapeLayout = node.getData(KShapeLayout.class);
        
        if (shapeLayout == null) {
            return null;
        } else {
            return new Rectangle2D.Double(
                    shapeLayout.getXpos(), shapeLayout.getYpos(),
                    shapeLayout.getWidth(), shapeLayout.getHeight());
        }
    }

}
