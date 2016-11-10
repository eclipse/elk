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

import org.eclipse.elk.graph.ElkNode;

/**
 * An {@link IBoundsProvider} that simply returns the bounds defined in the node's shape layout.
 */
public class ShapeLayoutBoundsProvider implements IBoundsProvider {

    /**
     * {@inheritDoc}
     */
    @Override
    public Double boundsFor(final ElkNode node) {
        return new Rectangle2D.Double(node.getX(), node.getY(), node.getWidth(), node.getHeight());
    }

}
