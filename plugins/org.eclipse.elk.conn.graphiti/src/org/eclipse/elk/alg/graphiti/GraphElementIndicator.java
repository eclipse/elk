/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.graphiti;

import org.eclipse.elk.core.service.ILayoutSetup;
import org.eclipse.graphiti.mm.algorithms.AbstractText;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.Shape;

/**
 * This class is responsible for deciding which pictogram elements are accepted as graph elements
 * in the layout graph. You may customize the default behavior by creating a subclass and binding it
 * in the Guice injector of your {@link ILayoutSetup}.
 */
public class GraphElementIndicator {
    
    /**
     * Determine whether the given shape shall be treated as a node in the layout graph.
     * 
     * <p>This implementation checks whether the shape has any anchors. Subclasses may override
     * this in order to implement other checks for excluding shapes that are not to be included
     * in the layout graph.</p>
     * 
     * @param shape a shape
     * @return whether the shape shall be treated a a node
     */
    public boolean isNodeShape(final Shape shape) {
        return !shape.getAnchors().isEmpty();
    }
    
    /**
     * Determine whether the given anchor shall be treated as a port in the layout graph.
     * 
     * <p>This implementation returns true if the anchor has a graphics algorithm and it is
     * either a {@link BoxRelativeAnchor} or a {@link FixPointAnchor}. Subclasses may override
     * this.</p>
     * 
     * @param anchor an anchor
     * @return whether the anchor shall be treated a a port
     */
    public boolean isPortAnchor(final Anchor anchor) {
        return anchor.getGraphicsAlgorithm() != null
                && (anchor instanceof BoxRelativeAnchor || anchor instanceof FixPointAnchor);
    }

    /**
     * Determine whether the given shape shall be treated as a node label in the layout graph.
     * 
     * <p>This implementation checks whether the shape has a text as graphics algorithm.
     * Subclasses may override this in order to implement specialized checks.</p>
     * 
     * @param shape a shape
     * @return whether the shape shall be treated as a node label
     */
    public boolean isNodeLabel(final Shape shape) {
        return shape.getGraphicsAlgorithm() instanceof AbstractText
                && ((AbstractText) shape.getGraphicsAlgorithm()).getValue() != null;
    }

    /**
     * Determine whether the given decorator shall be treated as an edge label in the layout graph.
     * 
     * <p>This implementation checks whether the decorator has a text as graphics algorithm.
     * Subclasses may override this in order to implement specialized checks.</p>
     * 
     * @param decorator a decorator
     * @return whether the decorator shall be treated as an edge label
     */
    public boolean isEdgeLabel(final ConnectionDecorator decorator) {
        return decorator.getGraphicsAlgorithm() instanceof AbstractText
                && ((AbstractText) decorator.getGraphicsAlgorithm()).getValue() != null;
    }

}
