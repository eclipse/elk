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
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.HierarchyHandling;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.graph.properties.GraphFeature;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class DotOptions implements ILayoutMetaDataProvider {
  /**
   * The id of the Graphviz Dot algorithm.
   */
  public static final String ALGORITHM_ID = "org.eclipse.elk.graphviz.dot";

  /**
   * Default value for {@link #PADDING} with algorithm "Graphviz Dot".
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
   * Default value for {@link #DIRECTION} with algorithm "Graphviz Dot".
   */
  private static final Direction DIRECTION_DEFAULT = Direction.DOWN;

  /**
   * Overall direction of edges: horizontal (right / left) or
   * vertical (down / up).
   */
  public static final IProperty<Direction> DIRECTION = new Property<Direction>(
                                CoreOptions.DIRECTION,
                                DIRECTION_DEFAULT);

  /**
   * Default value for {@link #SPACING_NODE_NODE} with algorithm "Graphviz Dot".
   */
  private static final double SPACING_NODE_NODE_DEFAULT = 20;

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
   * Default value for {@link #EDGE_ROUTING} with algorithm "Graphviz Dot".
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
   * Determines whether separate layout runs are triggered for different compound nodes in a
   * hierarchical graph. Setting a node's hierarchy handling to `INCLUDE_CHILDREN` will lay
   * out that node and all of its descendants in a single layout run, until a descendant is
   * encountered which has its hierarchy handling set to `SEPARATE_CHILDREN`. In general,
   * `SEPARATE_CHILDREN` will ensure that a new layout run is triggered for a node with that
   * setting. Including multiple levels of hierarchy in a single layout run may allow
   * cross-hierarchical edges to be laid out properly. If the root node is set to `INHERIT`
   * (or not set at all), the default behavior is `SEPARATE_CHILDREN`.
   * <h3>Algorithm Specific Details</h3>
   * If activated, the whole hierarchical graph is passed to dot as a whole.
   * Note however that dot performs a 'compound' layout where it somewhat flattens
   * the hierarchy and performs a layout on the flattened graph.
   * As a consequence, padding information of hierarchical child nodes is discarded.
   */
  public static final IProperty<HierarchyHandling> HIERARCHY_HANDLING = CoreOptions.HIERARCHY_HANDLING;

  /**
   * Default value for {@link #ITERATIONS_FACTOR} with algorithm "Graphviz Dot".
   */
  private static final double ITERATIONS_FACTOR_DEFAULT = 1;

  /**
   * Multiplicative scale factor for the maximal number of iterations used during crossing
   * minimization, node ranking, and node positioning.
   */
  public static final IProperty<Double> ITERATIONS_FACTOR = new Property<Double>(
                                GraphvizMetaDataProvider.ITERATIONS_FACTOR,
                                ITERATIONS_FACTOR_DEFAULT);

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
   * Factor for the spacing of different layers (ranks).
   */
  public static final IProperty<Double> LAYER_SPACING_FACTOR = GraphvizMetaDataProvider.LAYER_SPACING_FACTOR;

  /**
   * Whether ports should be moved to the point where edges cross the node's bounds.
   */
  public static final IProperty<Boolean> ADAPT_PORT_POSITIONS = GraphvizMetaDataProvider.ADAPT_PORT_POSITIONS;

  /**
   * Layouter-specific algorithm factory.
   */
  public static class DotFactory implements org.eclipse.elk.core.util.IFactory<AbstractLayoutProvider> {
    public AbstractLayoutProvider create() {
      AbstractLayoutProvider provider = new GraphvizLayoutProvider();
      provider.initialize("DOT");
      return provider;
    }

    public void destroy(final AbstractLayoutProvider obj) {
      obj.dispose();
    }
  }

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutAlgorithmData.Builder()
        .id("org.eclipse.elk.graphviz.dot")
        .name("Graphviz Dot")
        .description("Layered drawings of directed graphs. The algorithm aims edges in the same direction (top to bottom, or left to right) and then attempts to avoid edge crossings and reduce edge length. Edges are routed as spline curves and are thus drawn very smoothly. This algorithm is very suitable for state machine and activity diagrams, where the direction of edges has an important role.")
        .providerFactory(new DotFactory())
        .category("org.eclipse.elk.layered")
        .melkBundleName("Graphviz")
        .definingBundleId("org.eclipse.elk.alg.graphviz.layouter")
        .imagePath("images/dot_layout.png")
        .supportedFeatures(EnumSet.of(GraphFeature.SELF_LOOPS, GraphFeature.MULTI_EDGES, GraphFeature.EDGE_LABELS, GraphFeature.COMPOUND, GraphFeature.CLUSTERS))
        .create()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.dot",
        "org.eclipse.elk.padding",
        PADDING_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.dot",
        "org.eclipse.elk.direction",
        DIRECTION_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.dot",
        "org.eclipse.elk.spacing.nodeNode",
        SPACING_NODE_NODE_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.dot",
        "org.eclipse.elk.spacing.edgeLabel",
        SPACING_EDGE_LABEL.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.dot",
        "org.eclipse.elk.nodeSize.constraints",
        NODE_SIZE_CONSTRAINTS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.dot",
        "org.eclipse.elk.nodeSize.options",
        NODE_SIZE_OPTIONS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.dot",
        "org.eclipse.elk.edgeRouting",
        EDGE_ROUTING_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.dot",
        "org.eclipse.elk.debugMode",
        DEBUG_MODE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.dot",
        "org.eclipse.elk.hierarchyHandling",
        HIERARCHY_HANDLING.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.dot",
        "org.eclipse.elk.graphviz.iterationsFactor",
        ITERATIONS_FACTOR_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.dot",
        "org.eclipse.elk.graphviz.concentrate",
        CONCENTRATE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.dot",
        "org.eclipse.elk.graphviz.labelDistance",
        LABEL_DISTANCE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.dot",
        "org.eclipse.elk.graphviz.labelAngle",
        LABEL_ANGLE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.dot",
        "org.eclipse.elk.graphviz.layerSpacingFactor",
        LAYER_SPACING_FACTOR.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.dot",
        "org.eclipse.elk.graphviz.adaptPortPositions",
        ADAPT_PORT_POSITIONS.getDefault()
    );
  }
}
