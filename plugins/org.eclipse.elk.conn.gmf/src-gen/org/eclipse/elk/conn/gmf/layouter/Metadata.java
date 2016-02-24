/**
 * Copyright (c) 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    spoenemann - initial API and implementation
 */
package org.eclipse.elk.conn.gmf.layouter;

import java.util.EnumSet;
import org.eclipse.elk.conn.gmf.layouter.Draw2DLayoutProvider;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.GraphFeature;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.util.AlgorithmFactory;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * Layout algorithms contributed by GMF / GEF.
 */
@SuppressWarnings("all")
public class Metadata implements ILayoutMetaDataProvider {
  /**
   * Default value for {@link #SPACING_NODE} with algorithm "Draw2D Layout".
   */
  private final static float DRAW2_D_SUP_SPACING_NODE = 16;
  
  /**
   * Overridden value for Node Spacing.
   */
  public final static IProperty<Float> SPACING_NODE = new Property<Float>(
            LayoutOptions.SPACING_NODE,
            DRAW2_D_SUP_SPACING_NODE);
  
  /**
   * Default value for {@link #SPACING_BORDER} with algorithm "Draw2D Layout".
   */
  private final static float DRAW2_D_SUP_SPACING_BORDER = 16;
  
  /**
   * Overridden value for Border Spacing.
   */
  public final static IProperty<Float> SPACING_BORDER = new Property<Float>(
            LayoutOptions.SPACING_BORDER,
            DRAW2_D_SUP_SPACING_BORDER);
  
  /**
   * Default value for {@link #DIRECTION} with algorithm "Draw2D Layout".
   */
  private final static Direction DRAW2_D_SUP_DIRECTION = Direction.RIGHT;
  
  public void apply(final ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutAlgorithmData(
        "org.eclipse.elk.conn.gmf.layouter.Draw2D",
        "Draw2D Layout",
        "\'Directed Graph Layout\' provided by the Draw2D framework. This is the same algorithm that is used by the standard layout button of GMF diagrams.",
        new AlgorithmFactory(Draw2DLayoutProvider.class, ""),
        "org.eclipse.elk.layered",
        "GMF",
        "images/draw2d.png",
        EnumSet.of(GraphFeature.MULTI_EDGES)
    ));
    registry.addOptionSupport(
        "org.eclipse.elk.conn.gmf.layouter.Draw2D",
        "org.eclipse.elk.spacing.node",
        DRAW2_D_SUP_SPACING_NODE
    );
    registry.addOptionSupport(
        "org.eclipse.elk.conn.gmf.layouter.Draw2D",
        "org.eclipse.elk.spacing.border",
        DRAW2_D_SUP_SPACING_BORDER
    );
    registry.addOptionSupport(
        "org.eclipse.elk.conn.gmf.layouter.Draw2D",
        "org.eclipse.elk.direction",
        DRAW2_D_SUP_DIRECTION
    );
    registry.addOptionSupport(
        "org.eclipse.elk.conn.gmf.layouter.Draw2D",
        "org.eclipse.elk.nodeSize.constraints",
        null
    );
  }
}
