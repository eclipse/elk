/**
 * Copyright (c) 2015 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.elk.alg.graphviz.layouter;

import java.util.EnumSet;
import org.eclipse.elk.alg.graphviz.dot.transform.OverlapMode;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.graph.properties.GraphFeature;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class TwopiOptions implements ILayoutMetaDataProvider {
  /**
   * The id of the Graphviz Twopi algorithm.
   */
  public static final String ALGORITHM_ID = "org.eclipse.elk.graphviz.twopi";

  /**
   * Default value for {@link #PADDING} with algorithm "Graphviz Twopi".
   */
  private static final ElkPadding PADDING_DEFAULT = new ElkPadding(10);

  /**
   * The padding to be left to a parent element's border when placing child elements. This can
   * also serve as an output option of a layout algorithm if node size calculation is setup
   * appropriately.
   */
  public static final IProperty<ElkPadding> PADDING = new Property<ElkPadding>(
                                CoreOptions.PADDING,
                                PADDING_DEFAULT);

  /**
   * Default value for {@link #SPACING_NODE_NODE} with algorithm "Graphviz Twopi".
   */
  private static final double SPACING_NODE_NODE_DEFAULT = 60;

  /**
   * The minimal distance to be preserved between each two nodes.
   */
  public static final IProperty<Double> SPACING_NODE_NODE = new Property<Double>(
                                CoreOptions.SPACING_NODE_NODE,
                                SPACING_NODE_NODE_DEFAULT);

  /**
   * The minimal distance to be preserved between a label and the edge it is associated with.
   * Note that the placement of a label is influenced by the 'edgelabels.placement' option.
   */
  public static final IProperty<Double> SPACING_EDGE_LABEL = CoreOptions.SPACING_EDGE_LABEL;

  /**
   * What should be taken into account when calculating a node's size. Empty size constraints
   * specify that a node's size is already fixed and should not be changed.
   */
  public static final IProperty<EnumSet<SizeConstraint>> NODE_SIZE_CONSTRAINTS = CoreOptions.NODE_SIZE_CONSTRAINTS;

  /**
   * Options modifying the behavior of the size constraints set on a node. Each member of the
   * set specifies something that should be taken into account when calculating node sizes.
   * The empty set corresponds to no further modifications.
   */
  public static final IProperty<EnumSet<SizeOptions>> NODE_SIZE_OPTIONS = CoreOptions.NODE_SIZE_OPTIONS;

  /**
   * Default value for {@link #EDGE_ROUTING} with algorithm "Graphviz Twopi".
   */
  private static final EdgeRouting EDGE_ROUTING_DEFAULT = EdgeRouting.SPLINES;

  /**
   * What kind of edge routing style should be applied for the content of a parent node.
   * Algorithms may also set this option to single edges in order to mark them as splines.
   * The bend point list of edges with this option set to SPLINES must be interpreted as control
   * points for a piecewise cubic spline.
   */
  public static final IProperty<EdgeRouting> EDGE_ROUTING = new Property<EdgeRouting>(
                                CoreOptions.EDGE_ROUTING,
                                EDGE_ROUTING_DEFAULT);

  /**
   * Whether additional debug information shall be generated.
   */
  public static final IProperty<Boolean> DEBUG_MODE = CoreOptions.DEBUG_MODE;

  /**
   * Merges multiedges into a single edge and causes partially parallel edges to share part of
   * their paths.
   */
  public static final IProperty<Boolean> CONCENTRATE = GraphvizMetaDataProvider.CONCENTRATE;

  /**
   * Distance of head / tail positioned edge labels to the source or target node.
   */
  public static final IProperty<Double> LABEL_DISTANCE = GraphvizMetaDataProvider.LABEL_DISTANCE;

  /**
   * Angle between head / tail positioned edge labels and the corresponding edge.
   */
  public static final IProperty<Double> LABEL_ANGLE = GraphvizMetaDataProvider.LABEL_ANGLE;

  /**
   * Determines if and how node overlaps should be removed.
   */
  public static final IProperty<OverlapMode> OVERLAP_MODE = GraphvizMetaDataProvider.OVERLAP_MODE;

  /**
   * Whether ports should be moved to the point where edges cross the node's bounds.
   */
  public static final IProperty<Boolean> ADAPT_PORT_POSITIONS = GraphvizMetaDataProvider.ADAPT_PORT_POSITIONS;

  /**
   * Layouter-specific algorithm factory.
   */
  public static class TwopiFactory implements org.eclipse.elk.core.util.IFactory<AbstractLayoutProvider> {
    public AbstractLayoutProvider create() {
      AbstractLayoutProvider provider = new GraphvizLayoutProvider();
      provider.initialize("TWOPI");
      return provider;
    }

    public void destroy(final AbstractLayoutProvider obj) {
      obj.dispose();
    }
  }

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutAlgorithmData.Builder()
        .id("org.eclipse.elk.graphviz.twopi")
        .name("Graphviz Twopi")
        .description("Radial layouts, after Wills \'97. The nodes are placed on concentric circles depending on their distance from a given root node. The algorithm is designed to handle not only small graphs, but also very large ones.")
        .providerFactory(new TwopiFactory())
        .category("org.eclipse.elk.radial")
        .melkBundleName("Graphviz")
        .definingBundleId("org.eclipse.elk.alg.graphviz.layouter")
        .imagePath("images/twopi_layout.png")
        .supportedFeatures(EnumSet.of(GraphFeature.SELF_LOOPS, GraphFeature.MULTI_EDGES, GraphFeature.EDGE_LABELS))
        .create()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.twopi",
        "org.eclipse.elk.padding",
        PADDING_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.twopi",
        "org.eclipse.elk.spacing.nodeNode",
        SPACING_NODE_NODE_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.twopi",
        "org.eclipse.elk.spacing.edgeLabel",
        SPACING_EDGE_LABEL.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.twopi",
        "org.eclipse.elk.nodeSize.constraints",
        NODE_SIZE_CONSTRAINTS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.twopi",
        "org.eclipse.elk.nodeSize.options",
        NODE_SIZE_OPTIONS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.twopi",
        "org.eclipse.elk.edgeRouting",
        EDGE_ROUTING_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.twopi",
        "org.eclipse.elk.debugMode",
        DEBUG_MODE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.twopi",
        "org.eclipse.elk.graphviz.concentrate",
        CONCENTRATE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.twopi",
        "org.eclipse.elk.graphviz.labelDistance",
        LABEL_DISTANCE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.twopi",
        "org.eclipse.elk.graphviz.labelAngle",
        LABEL_ANGLE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.twopi",
        "org.eclipse.elk.graphviz.overlapMode",
        OVERLAP_MODE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.twopi",
        "org.eclipse.elk.graphviz.adaptPortPositions",
        ADAPT_PORT_POSITIONS.getDefault()
    );
  }
}
