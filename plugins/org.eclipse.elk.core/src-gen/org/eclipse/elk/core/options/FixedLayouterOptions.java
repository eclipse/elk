/**
 * Copyright (c) 2015, 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.elk.core.options;

import java.util.EnumSet;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.util.FixedLayoutProvider;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class FixedLayouterOptions implements ILayoutMetaDataProvider {
  /**
   * The id of the ELK Fixed algorithm.
   */
  public static final String ALGORITHM_ID = "org.eclipse.elk.fixed";

  /**
   * Default value for {@link #PADDING} with algorithm "ELK Fixed".
   */
  private static final ElkPadding PADDING_DEFAULT = new ElkPadding(15);

  /**
   * The padding to be left to a parent element's border when placing child elements. This can
   * also serve as an output option of a layout algorithm if node size calculation is setup
   * appropriately.
   */
  public static final IProperty<ElkPadding> PADDING = new Property<ElkPadding>(
                                CoreOptions.PADDING,
                                PADDING_DEFAULT);

  /**
   * The position of a node, port, or label. This is used by the 'Fixed Layout' algorithm to
   * specify a pre-defined position.
   */
  public static final IProperty<KVector> POSITION = CoreOptions.POSITION;

  /**
   * A fixed list of bend points for the edge. This is used by the 'Fixed Layout' algorithm to
   * specify a pre-defined routing for an edge. The vector chain must include the source point,
   * any bend points, and the target point, so it must have at least two points.
   */
  public static final IProperty<KVectorChain> BEND_POINTS = CoreOptions.BEND_POINTS;

  /**
   * What should be taken into account when calculating a node's size. Empty size constraints
   * specify that a node's size is already fixed and should not be changed.
   */
  public static final IProperty<EnumSet<SizeConstraint>> NODE_SIZE_CONSTRAINTS = CoreOptions.NODE_SIZE_CONSTRAINTS;

  /**
   * The minimal size to which a node can be reduced.
   */
  public static final IProperty<KVector> NODE_SIZE_MINIMUM = CoreOptions.NODE_SIZE_MINIMUM;

  /**
   * By default, the fixed layout provider will enlarge a graph until it is large enough to contain
   * its children. If this option is set, it won't do so.
   */
  public static final IProperty<Boolean> NODE_SIZE_FIXED_GRAPH_SIZE = CoreOptions.NODE_SIZE_FIXED_GRAPH_SIZE;

  /**
   * Layouter-specific algorithm factory.
   */
  public static class FixedFactory implements org.eclipse.elk.core.util.IFactory<AbstractLayoutProvider> {
    public AbstractLayoutProvider create() {
      AbstractLayoutProvider provider = new FixedLayoutProvider();
      provider.initialize("");
      return provider;
    }

    public void destroy(final AbstractLayoutProvider obj) {
      obj.dispose();
    }
  }

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutAlgorithmData.Builder()
        .id("org.eclipse.elk.fixed")
        .name("ELK Fixed")
        .description("Keeps the current layout as it is, without any automatic modification. Optional coordinates can be given for nodes and edge bend points.")
        .providerFactory(new FixedFactory())
        .melkBundleName("ELK")
        .definingBundleId("org.eclipse.elk.core")
        .create()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.fixed",
        "org.eclipse.elk.padding",
        PADDING_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.fixed",
        "org.eclipse.elk.position",
        POSITION.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.fixed",
        "org.eclipse.elk.bendPoints",
        BEND_POINTS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.fixed",
        "org.eclipse.elk.nodeSize.constraints",
        NODE_SIZE_CONSTRAINTS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.fixed",
        "org.eclipse.elk.nodeSize.minimum",
        NODE_SIZE_MINIMUM.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.fixed",
        "org.eclipse.elk.nodeSize.fixedGraphSize",
        NODE_SIZE_FIXED_GRAPH_SIZE.getDefault()
    );
  }
}
