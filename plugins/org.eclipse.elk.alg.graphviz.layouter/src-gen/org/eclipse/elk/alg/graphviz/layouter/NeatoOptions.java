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
import org.eclipse.elk.alg.graphviz.dot.transform.NeatoModel;
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
public class NeatoOptions implements ILayoutMetaDataProvider {
  /**
   * The id of the Graphviz Neato algorithm.
   */
  public static final String ALGORITHM_ID = "org.eclipse.elk.graphviz.neato";

  /**
   * Default value for {@link #PADDING} with algorithm "Graphviz Neato".
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
   * Default value for {@link #SPACING_NODE_NODE} with algorithm "Graphviz Neato".
   */
  private static final double SPACING_NODE_NODE_DEFAULT = 40;

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
   * Default value for {@link #RANDOM_SEED} with algorithm "Graphviz Neato".
   */
  private static final int RANDOM_SEED_DEFAULT = 1;

  /**
   * Seed used for pseudo-random number generators to control the layout algorithm. If the
   * value is 0, the seed shall be determined pseudo-randomly (e.g. from the system time).
   */
  public static final IProperty<Integer> RANDOM_SEED = new Property<Integer>(
                                CoreOptions.RANDOM_SEED,
                                RANDOM_SEED_DEFAULT);

  /**
   * Whether the algorithm should be run in interactive mode for the content of a parent node.
   * What this means exactly depends on how the specific algorithm interprets this option.
   * Usually in the interactive mode algorithms try to modify the current layout as little as
   * possible.
   */
  public static final IProperty<Boolean> INTERACTIVE = CoreOptions.INTERACTIVE;

  /**
   * Default value for {@link #EDGE_ROUTING} with algorithm "Graphviz Neato".
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
   * Default value for {@link #SEPARATE_CONNECTED_COMPONENTS} with algorithm "Graphviz Neato".
   */
  private static final boolean SEPARATE_CONNECTED_COMPONENTS_DEFAULT = false;

  /**
   * Whether each connected component should be processed separately.
   */
  public static final IProperty<Boolean> SEPARATE_CONNECTED_COMPONENTS = new Property<Boolean>(
                                CoreOptions.SEPARATE_CONNECTED_COMPONENTS,
                                SEPARATE_CONNECTED_COMPONENTS_DEFAULT);

  /**
   * Merges multiedges into a single edge and causes partially parallel edges to share part of
   * their paths.
   */
  public static final IProperty<Boolean> CONCENTRATE = GraphvizMetaDataProvider.CONCENTRATE;

  /**
   * Default value for {@link #EPSILON} with algorithm "Graphviz Neato".
   */
  private static final double EPSILON_DEFAULT = 0.0001f;

  /**
   * Terminating condition. If the length squared of all energy gradients are less than
   * epsilon, the algorithm stops.
   */
  public static final IProperty<Double> EPSILON = new Property<Double>(
                                GraphvizMetaDataProvider.EPSILON,
                                EPSILON_DEFAULT);

  /**
   * Distance of head / tail positioned edge labels to the source or target node.
   */
  public static final IProperty<Double> LABEL_DISTANCE = GraphvizMetaDataProvider.LABEL_DISTANCE;

  /**
   * Angle between head / tail positioned edge labels and the corresponding edge.
   */
  public static final IProperty<Double> LABEL_ANGLE = GraphvizMetaDataProvider.LABEL_ANGLE;

  /**
   * Default value for {@link #MAXITER} with algorithm "Graphviz Neato".
   */
  private static final int MAXITER_DEFAULT = 200;

  /**
   * The maximum number of iterations.
   */
  public static final IProperty<Integer> MAXITER = new Property<Integer>(
                                GraphvizMetaDataProvider.MAXITER,
                                MAXITER_DEFAULT);

  /**
   * Specifies how the distance matrix is computed for the input graph.
   */
  public static final IProperty<NeatoModel> NEATO_MODEL = GraphvizMetaDataProvider.NEATO_MODEL;

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
  public static class NeatoFactory implements org.eclipse.elk.core.util.IFactory<AbstractLayoutProvider> {
    public AbstractLayoutProvider create() {
      AbstractLayoutProvider provider = new GraphvizLayoutProvider();
      provider.initialize("NEATO");
      return provider;
    }

    public void destroy(final AbstractLayoutProvider obj) {
      obj.dispose();
    }
  }

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutAlgorithmData.Builder()
        .id("org.eclipse.elk.graphviz.neato")
        .name("Graphviz Neato")
        .description("Spring model layouts. Neato attempts to minimize a global energy function, which is equivalent to statistical multi-dimensional scaling. The solution is achieved using stress majorization, though the older Kamada-Kawai algorithm, using steepest descent, is also available.")
        .providerFactory(new NeatoFactory())
        .category("org.eclipse.elk.force")
        .melkBundleName("Graphviz")
        .definingBundleId("org.eclipse.elk.alg.graphviz.layouter")
        .imagePath("images/neato_layout.png")
        .supportedFeatures(EnumSet.of(GraphFeature.SELF_LOOPS, GraphFeature.MULTI_EDGES, GraphFeature.EDGE_LABELS))
        .create()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.neato",
        "org.eclipse.elk.padding",
        PADDING_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.neato",
        "org.eclipse.elk.spacing.nodeNode",
        SPACING_NODE_NODE_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.neato",
        "org.eclipse.elk.spacing.edgeLabel",
        SPACING_EDGE_LABEL.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.neato",
        "org.eclipse.elk.nodeSize.constraints",
        NODE_SIZE_CONSTRAINTS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.neato",
        "org.eclipse.elk.nodeSize.options",
        NODE_SIZE_OPTIONS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.neato",
        "org.eclipse.elk.randomSeed",
        RANDOM_SEED_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.neato",
        "org.eclipse.elk.interactive",
        INTERACTIVE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.neato",
        "org.eclipse.elk.edgeRouting",
        EDGE_ROUTING_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.neato",
        "org.eclipse.elk.debugMode",
        DEBUG_MODE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.neato",
        "org.eclipse.elk.separateConnectedComponents",
        SEPARATE_CONNECTED_COMPONENTS_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.neato",
        "org.eclipse.elk.graphviz.concentrate",
        CONCENTRATE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.neato",
        "org.eclipse.elk.graphviz.epsilon",
        EPSILON_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.neato",
        "org.eclipse.elk.graphviz.labelDistance",
        LABEL_DISTANCE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.neato",
        "org.eclipse.elk.graphviz.labelAngle",
        LABEL_ANGLE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.neato",
        "org.eclipse.elk.graphviz.maxiter",
        MAXITER_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.neato",
        "org.eclipse.elk.graphviz.neatoModel",
        NEATO_MODEL.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.neato",
        "org.eclipse.elk.graphviz.overlapMode",
        OVERLAP_MODE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.neato",
        "org.eclipse.elk.graphviz.adaptPortPositions",
        ADAPT_PORT_POSITIONS.getDefault()
    );
  }
}
