/**
 * Copyright (c) 2015 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.elk.conn.gmf.layouter;

import java.util.EnumSet;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.graph.properties.GraphFeature;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class Draw2DOptions implements ILayoutMetaDataProvider {
  /**
   * The id of the Draw2D Layout algorithm.
   */
  public static final String ALGORITHM_ID = "org.eclipse.elk.conn.gmf.layouter.Draw2D";

  /**
   * Default value for {@link #SPACING_NODE_NODE} with algorithm "Draw2D Layout".
   */
  private static final double SPACING_NODE_NODE_DEFAULT = 16;

  /**
   * The minimal distance to be preserved between each two nodes.
   */
  public static final IProperty<Double> SPACING_NODE_NODE = new Property<Double>(
                                CoreOptions.SPACING_NODE_NODE,
                                SPACING_NODE_NODE_DEFAULT);

  /**
   * Default value for {@link #PADDING} with algorithm "Draw2D Layout".
   */
  private static final ElkPadding PADDING_DEFAULT = new ElkPadding(16);

  /**
   * The padding to be left to a parent element's border when placing child elements. This can
   * also serve as an output option of a layout algorithm if node size calculation is setup
   * appropriately.
   */
  public static final IProperty<ElkPadding> PADDING = new Property<ElkPadding>(
                                CoreOptions.PADDING,
                                PADDING_DEFAULT);

  /**
   * Default value for {@link #DIRECTION} with algorithm "Draw2D Layout".
   */
  private static final Direction DIRECTION_DEFAULT = Direction.RIGHT;

  /**
   * Overall direction of edges: horizontal (right / left) or
   * vertical (down / up).
   */
  public static final IProperty<Direction> DIRECTION = new Property<Direction>(
                                CoreOptions.DIRECTION,
                                DIRECTION_DEFAULT);

  /**
   * What should be taken into account when calculating a node's size. Empty size constraints
   * specify that a node's size is already fixed and should not be changed.
   */
  public static final IProperty<EnumSet<SizeConstraint>> NODE_SIZE_CONSTRAINTS = CoreOptions.NODE_SIZE_CONSTRAINTS;

  /**
   * Layouter-specific algorithm factory.
   */
  public static class Draw2DFactory implements org.eclipse.elk.core.util.IFactory<AbstractLayoutProvider> {
    public AbstractLayoutProvider create() {
      AbstractLayoutProvider provider = new Draw2DLayoutProvider();
      provider.initialize("");
      return provider;
    }

    public void destroy(final AbstractLayoutProvider obj) {
      obj.dispose();
    }
  }

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutAlgorithmData.Builder()
        .id("org.eclipse.elk.conn.gmf.layouter.Draw2D")
        .name("Draw2D Layout")
        .description("\'Directed Graph Layout\' provided by the Draw2D framework. This is the same algorithm that is used by the standard layout button of GMF diagrams.")
        .providerFactory(new Draw2DFactory())
        .category("org.eclipse.elk.layered")
        .melkBundleName("GMF")
        .definingBundleId("org.eclipse.elk.conn.gmf")
        .imagePath("images/draw2d.png")
        .supportedFeatures(EnumSet.of(GraphFeature.MULTI_EDGES))
        .create()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.conn.gmf.layouter.Draw2D",
        "org.eclipse.elk.spacing.nodeNode",
        SPACING_NODE_NODE_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.conn.gmf.layouter.Draw2D",
        "org.eclipse.elk.padding",
        PADDING_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.conn.gmf.layouter.Draw2D",
        "org.eclipse.elk.direction",
        DIRECTION_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.conn.gmf.layouter.Draw2D",
        "org.eclipse.elk.nodeSize.constraints",
        NODE_SIZE_CONSTRAINTS.getDefault()
    );
  }
}
