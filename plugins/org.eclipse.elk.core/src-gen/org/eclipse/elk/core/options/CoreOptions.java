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
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.data.LayoutCategoryData;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.core.labels.ILabelManager;
import org.eclipse.elk.core.math.ElkMargin;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.util.BoxLayoutProvider;
import org.eclipse.elk.core.util.ExclusiveBounds;
import org.eclipse.elk.core.util.IndividualSpacings;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * Core definitions of the Eclipse Layout Kernel.
 */
@SuppressWarnings("all")
public class CoreOptions implements ILayoutMetaDataProvider {
  /**
   * Select a specific layout algorithm.
   */
  public static final IProperty<String> ALGORITHM = new Property<String>(
            "org.eclipse.elk.algorithm");

  /**
   * Meta data associated with the selected algorithm.
   */
  public static final IProperty<LayoutAlgorithmData> RESOLVED_ALGORITHM = new Property<LayoutAlgorithmData>(
            "org.eclipse.elk.resolvedAlgorithm");

  /**
   * Default value for {@link #ALIGNMENT}.
   */
  private static final Alignment ALIGNMENT_DEFAULT = Alignment.AUTOMATIC;

  /**
   * Alignment of the selected node relative to other nodes;
   * the exact meaning depends on the used algorithm.
   */
  public static final IProperty<Alignment> ALIGNMENT = new Property<Alignment>(
            "org.eclipse.elk.alignment",
            ALIGNMENT_DEFAULT,
            null,
            null);

  /**
   * Lower bound value for {@link #ASPECT_RATIO}.
   */
  private static final Comparable<? super Double> ASPECT_RATIO_LOWER_BOUND = ExclusiveBounds.greaterThan(0);

  /**
   * The desired aspect ratio of the drawing, that is the quotient of width by height.
   */
  public static final IProperty<Double> ASPECT_RATIO = new Property<Double>(
            "org.eclipse.elk.aspectRatio",
            null,
            ASPECT_RATIO_LOWER_BOUND,
            null);

  /**
   * A fixed list of bend points for the edge. This is used by the 'Fixed Layout' algorithm to
   * specify a pre-defined routing for an edge. The vector chain must include the source point,
   * any bend points, and the target point, so it must have at least two points.
   */
  public static final IProperty<KVectorChain> BEND_POINTS = new Property<KVectorChain>(
            "org.eclipse.elk.bendPoints");

  /**
   * Default value for {@link #CONTENT_ALIGNMENT}.
   */
  private static final EnumSet<ContentAlignment> CONTENT_ALIGNMENT_DEFAULT = ContentAlignment.topLeft();

  /**
   * Specifies how the content of a node are aligned. Each node can individually control the alignment of its
   * contents. I.e. if a node should be aligned top left in its parent node, the parent node should specify that
   * option.
   */
  public static final IProperty<EnumSet<ContentAlignment>> CONTENT_ALIGNMENT = new Property<EnumSet<ContentAlignment>>(
            "org.eclipse.elk.contentAlignment",
            CONTENT_ALIGNMENT_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #DEBUG_MODE}.
   */
  private static final boolean DEBUG_MODE_DEFAULT = false;

  /**
   * Whether additional debug information shall be generated.
   */
  public static final IProperty<Boolean> DEBUG_MODE = new Property<Boolean>(
            "org.eclipse.elk.debugMode",
            DEBUG_MODE_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #DIRECTION}.
   */
  private static final Direction DIRECTION_DEFAULT = Direction.UNDEFINED;

  /**
   * Overall direction of edges: horizontal (right / left) or
   * vertical (down / up).
   */
  public static final IProperty<Direction> DIRECTION = new Property<Direction>(
            "org.eclipse.elk.direction",
            DIRECTION_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #EDGE_ROUTING}.
   */
  private static final EdgeRouting EDGE_ROUTING_DEFAULT = EdgeRouting.UNDEFINED;

  /**
   * What kind of edge routing style should be applied for the content of a parent node.
   * Algorithms may also set this option to single edges in order to mark them as splines.
   * The bend point list of edges with this option set to SPLINES must be interpreted as control
   * points for a piecewise cubic spline.
   */
  public static final IProperty<EdgeRouting> EDGE_ROUTING = new Property<EdgeRouting>(
            "org.eclipse.elk.edgeRouting",
            EDGE_ROUTING_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #EXPAND_NODES}.
   */
  private static final boolean EXPAND_NODES_DEFAULT = false;

  /**
   * If active, nodes are expanded to fill the area of their parent.
   */
  public static final IProperty<Boolean> EXPAND_NODES = new Property<Boolean>(
            "org.eclipse.elk.expandNodes",
            EXPAND_NODES_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #HIERARCHY_HANDLING}.
   */
  private static final HierarchyHandling HIERARCHY_HANDLING_DEFAULT = HierarchyHandling.INHERIT;

  /**
   * Determines whether separate layout runs are triggered for different compound nodes in a
   * hierarchical graph. Setting a node's hierarchy handling to `INCLUDE_CHILDREN` will lay
   * out that node and all of its descendants in a single layout run, until a descendant is
   * encountered which has its hierarchy handling set to `SEPARATE_CHILDREN`. In general,
   * `SEPARATE_CHILDREN` will ensure that a new layout run is triggered for a node with that
   * setting. Including multiple levels of hierarchy in a single layout run may allow
   * cross-hierarchical edges to be laid out properly. If the root node is set to `INHERIT`
   * (or not set at all), the default behavior is `SEPARATE_CHILDREN`.
   */
  public static final IProperty<HierarchyHandling> HIERARCHY_HANDLING = new Property<HierarchyHandling>(
            "org.eclipse.elk.hierarchyHandling",
            HIERARCHY_HANDLING_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #PADDING}.
   */
  private static final ElkPadding PADDING_DEFAULT = new ElkPadding(12);

  /**
   * The padding to be left to a parent element's border when placing child elements. This can
   * also serve as an output option of a layout algorithm if node size calculation is setup
   * appropriately.
   */
  public static final IProperty<ElkPadding> PADDING = new Property<ElkPadding>(
            "org.eclipse.elk.padding",
            PADDING_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #INTERACTIVE}.
   */
  private static final boolean INTERACTIVE_DEFAULT = false;

  /**
   * Whether the algorithm should be run in interactive mode for the content of a parent node.
   * What this means exactly depends on how the specific algorithm interprets this option.
   * Usually in the interactive mode algorithms try to modify the current layout as little as
   * possible.
   */
  public static final IProperty<Boolean> INTERACTIVE = new Property<Boolean>(
            "org.eclipse.elk.interactive",
            INTERACTIVE_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #INTERACTIVE_LAYOUT}.
   */
  private static final boolean INTERACTIVE_LAYOUT_DEFAULT = false;

  /**
   * Whether the graph should be changeable interactively and by setting constraints
   */
  public static final IProperty<Boolean> INTERACTIVE_LAYOUT = new Property<Boolean>(
            "org.eclipse.elk.interactiveLayout",
            INTERACTIVE_LAYOUT_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #OMIT_NODE_MICRO_LAYOUT}.
   */
  private static final boolean OMIT_NODE_MICRO_LAYOUT_DEFAULT = false;

  /**
   * Node micro layout comprises the computation of node dimensions (if requested), the placement of ports
   * and their labels, and the placement of node labels.
   * The functionality is implemented independent of any specific layout algorithm and shouldn't have any
   * negative impact on the layout algorithm's performance itself. Yet, if any unforeseen behavior occurs,
   * this option allows to deactivate the micro layout.
   */
  public static final IProperty<Boolean> OMIT_NODE_MICRO_LAYOUT = new Property<Boolean>(
            "org.eclipse.elk.omitNodeMicroLayout",
            OMIT_NODE_MICRO_LAYOUT_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #PORT_CONSTRAINTS}.
   */
  private static final PortConstraints PORT_CONSTRAINTS_DEFAULT = PortConstraints.UNDEFINED;

  /**
   * Defines constraints of the position of the ports of a node.
   */
  public static final IProperty<PortConstraints> PORT_CONSTRAINTS = new Property<PortConstraints>(
            "org.eclipse.elk.portConstraints",
            PORT_CONSTRAINTS_DEFAULT,
            null,
            null);

  /**
   * The position of a node, port, or label. This is used by the 'Fixed Layout' algorithm to
   * specify a pre-defined position.
   */
  public static final IProperty<KVector> POSITION = new Property<KVector>(
            "org.eclipse.elk.position");

  /**
   * Defines the priority of an object; its meaning depends on the specific layout algorithm
   * and the context where it is used.
   */
  public static final IProperty<Integer> PRIORITY = new Property<Integer>(
            "org.eclipse.elk.priority");

  /**
   * Seed used for pseudo-random number generators to control the layout algorithm. If the
   * value is 0, the seed shall be determined pseudo-randomly (e.g. from the system time).
   */
  public static final IProperty<Integer> RANDOM_SEED = new Property<Integer>(
            "org.eclipse.elk.randomSeed");

  /**
   * Whether each connected component should be processed separately.
   */
  public static final IProperty<Boolean> SEPARATE_CONNECTED_COMPONENTS = new Property<Boolean>(
            "org.eclipse.elk.separateConnectedComponents");

  /**
   * Default value for {@link #JUNCTION_POINTS}.
   */
  private static final KVectorChain JUNCTION_POINTS_DEFAULT = new KVectorChain();

  /**
   * This option is not used as option, but as output of the layout algorithms. It is
   * attached to edges and determines the points where junction symbols should be drawn in
   * order to represent hyperedges with orthogonal routing. Whether such points are computed
   * depends on the chosen layout algorithm and edge routing style. The points are put into
   * the vector chain with no specific order.
   */
  public static final IProperty<KVectorChain> JUNCTION_POINTS = new Property<KVectorChain>(
            "org.eclipse.elk.junctionPoints",
            JUNCTION_POINTS_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #COMMENT_BOX}.
   */
  private static final boolean COMMENT_BOX_DEFAULT = false;

  /**
   * Whether the node should be regarded as a comment box instead of a regular node. In that
   * case its placement should be similar to how labels are handled. Any edges incident to a
   * comment box specify to which graph elements the comment is related.
   */
  public static final IProperty<Boolean> COMMENT_BOX = new Property<Boolean>(
            "org.eclipse.elk.commentBox",
            COMMENT_BOX_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #HYPERNODE}.
   */
  private static final boolean HYPERNODE_DEFAULT = false;

  /**
   * Whether the node should be handled as a hypernode.
   */
  public static final IProperty<Boolean> HYPERNODE = new Property<Boolean>(
            "org.eclipse.elk.hypernode",
            HYPERNODE_DEFAULT,
            null,
            null);

  /**
   * Label managers can shorten labels upon a layout algorithm's request.
   */
  public static final IProperty<ILabelManager> LABEL_MANAGER = new Property<ILabelManager>(
            "org.eclipse.elk.labelManager");

  /**
   * Default value for {@link #MARGINS}.
   */
  private static final ElkMargin MARGINS_DEFAULT = new ElkMargin();

  /**
   * Margins define additional space around the actual bounds of a graph element. For instance,
   * ports or labels being placed on the outside of a node's border might introduce such a
   * margin. The margin is used to guarantee non-overlap of other graph elements with those
   * ports or labels.
   */
  public static final IProperty<ElkMargin> MARGINS = new Property<ElkMargin>(
            "org.eclipse.elk.margins",
            MARGINS_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #NO_LAYOUT}.
   */
  private static final boolean NO_LAYOUT_DEFAULT = false;

  /**
   * No layout is done for the associated element. This is used to mark parts of a diagram to
   * avoid their inclusion in the layout graph, or to mark parts of the layout graph to
   * prevent layout engines from processing them. If you wish to exclude the contents of a
   * compound node from automatic layout, while the node itself is still considered on its own
   * layer, use the 'Fixed Layout' algorithm for that node.
   */
  public static final IProperty<Boolean> NO_LAYOUT = new Property<Boolean>(
            "org.eclipse.elk.noLayout",
            NO_LAYOUT_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #SCALE_FACTOR}.
   */
  private static final double SCALE_FACTOR_DEFAULT = 1;

  /**
   * Lower bound value for {@link #SCALE_FACTOR}.
   */
  private static final Comparable<? super Double> SCALE_FACTOR_LOWER_BOUND = ExclusiveBounds.greaterThan(0);

  /**
   * The scaling factor to be applied to the corresponding node in recursive layout. It causes
   * the corresponding node's size to be adjusted, and its ports and labels to be sized and
   * placed accordingly after the layout of that node has been determined (and before the node
   * itself and its siblings are arranged). The scaling is not reverted afterwards, so the
   * resulting layout graph contains the adjusted size and position data. This option is
   * currently not supported if 'Layout Hierarchy' is set.
   */
  public static final IProperty<Double> SCALE_FACTOR = new Property<Double>(
            "org.eclipse.elk.scaleFactor",
            SCALE_FACTOR_DEFAULT,
            SCALE_FACTOR_LOWER_BOUND,
            null);

  /**
   * The width of the area occupied by the laid out children of a node.
   */
  public static final IProperty<Double> CHILD_AREA_WIDTH = new Property<Double>(
            "org.eclipse.elk.childAreaWidth");

  /**
   * The height of the area occupied by the laid out children of a node.
   */
  public static final IProperty<Double> CHILD_AREA_HEIGHT = new Property<Double>(
            "org.eclipse.elk.childAreaHeight");

  /**
   * Default value for {@link #TOPDOWN_LAYOUT}.
   */
  private static final boolean TOPDOWN_LAYOUT_DEFAULT = false;

  /**
   * Turns topdown layout on and off. If this option is enabled, hierarchical layout will be computed first for
   * the root node and then for its children recursively. Layouts are then scaled down to fit the area provided by
   * their parents. Graphs must follow a certain structure for topdown layout to work properly.
   * {@link TopdownNodeTypes.PARALLEL_NODE} nodes must have children of type
   * {@link TopdownNodeTypes.HIERARCHICAL_NODE} and must define {@link topdown.hierarchicalNodeWidth} and
   * {@link topdown.hierarchicalNodeAspectRatio} for their children. Furthermore they need to be laid out using an
   * algorithm  that is a {@link TopdownLayoutProvider}. Hierarchical nodes can also be parents of other hierarchical
   * nodes and can optionally use a {@link TopdownSizeApproximator} to dynamically set sizes during topdown layout.
   * In this case {@link topdown.hierarchicalNodeWidth} and {@link topdown.hierarchicalNodeAspectRatio} should be set
   * on the node itself rather than the parent. The values are then used by the size approximator as base values.
   * Hierarchical nodes require the layout option {@link nodeSize.fixedGraphSize} to be true to prevent the algorithm
   * used there from resizing the hierarchical node. This option is not supported if 'Hierarchy Handling' is set to
   * 'INCLUDE_CHILDREN'
   */
  public static final IProperty<Boolean> TOPDOWN_LAYOUT = new Property<Boolean>(
            "org.eclipse.elk.topdownLayout",
            TOPDOWN_LAYOUT_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #ANIMATE}.
   */
  private static final boolean ANIMATE_DEFAULT = true;

  /**
   * Whether the shift from the old layout to the new computed layout shall be animated.
   */
  public static final IProperty<Boolean> ANIMATE = new Property<Boolean>(
            "org.eclipse.elk.animate",
            ANIMATE_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #ANIM_TIME_FACTOR}.
   */
  private static final int ANIM_TIME_FACTOR_DEFAULT = 100;

  /**
   * Lower bound value for {@link #ANIM_TIME_FACTOR}.
   */
  private static final Comparable<? super Integer> ANIM_TIME_FACTOR_LOWER_BOUND = Integer.valueOf(0);

  /**
   * Factor for computation of animation time. The higher the value, the longer the animation
   * time. If the value is 0, the resulting time is always equal to the minimum defined by
   * 'Minimal Animation Time'.
   */
  public static final IProperty<Integer> ANIM_TIME_FACTOR = new Property<Integer>(
            "org.eclipse.elk.animTimeFactor",
            ANIM_TIME_FACTOR_DEFAULT,
            ANIM_TIME_FACTOR_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #LAYOUT_ANCESTORS}.
   */
  private static final boolean LAYOUT_ANCESTORS_DEFAULT = false;

  /**
   * Whether the hierarchy levels on the path from the selected element to the root of the
   * diagram shall be included in the layout process.
   */
  public static final IProperty<Boolean> LAYOUT_ANCESTORS = new Property<Boolean>(
            "org.eclipse.elk.layoutAncestors",
            LAYOUT_ANCESTORS_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #MAX_ANIM_TIME}.
   */
  private static final int MAX_ANIM_TIME_DEFAULT = 4000;

  /**
   * Lower bound value for {@link #MAX_ANIM_TIME}.
   */
  private static final Comparable<? super Integer> MAX_ANIM_TIME_LOWER_BOUND = Integer.valueOf(0);

  /**
   * The maximal time for animations, in milliseconds.
   */
  public static final IProperty<Integer> MAX_ANIM_TIME = new Property<Integer>(
            "org.eclipse.elk.maxAnimTime",
            MAX_ANIM_TIME_DEFAULT,
            MAX_ANIM_TIME_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #MIN_ANIM_TIME}.
   */
  private static final int MIN_ANIM_TIME_DEFAULT = 400;

  /**
   * Lower bound value for {@link #MIN_ANIM_TIME}.
   */
  private static final Comparable<? super Integer> MIN_ANIM_TIME_LOWER_BOUND = Integer.valueOf(0);

  /**
   * The minimal time for animations, in milliseconds.
   */
  public static final IProperty<Integer> MIN_ANIM_TIME = new Property<Integer>(
            "org.eclipse.elk.minAnimTime",
            MIN_ANIM_TIME_DEFAULT,
            MIN_ANIM_TIME_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #PROGRESS_BAR}.
   */
  private static final boolean PROGRESS_BAR_DEFAULT = false;

  /**
   * Whether a progress bar shall be displayed during layout computations.
   */
  public static final IProperty<Boolean> PROGRESS_BAR = new Property<Boolean>(
            "org.eclipse.elk.progressBar",
            PROGRESS_BAR_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #VALIDATE_GRAPH}.
   */
  private static final boolean VALIDATE_GRAPH_DEFAULT = false;

  /**
   * Whether the graph shall be validated before any layout algorithm is applied. If this
   * option is enabled and at least one error is found, the layout process is aborted and a message
   * is shown to the user.
   */
  public static final IProperty<Boolean> VALIDATE_GRAPH = new Property<Boolean>(
            "org.eclipse.elk.validateGraph",
            VALIDATE_GRAPH_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #VALIDATE_OPTIONS}.
   */
  private static final boolean VALIDATE_OPTIONS_DEFAULT = true;

  /**
   * Whether layout options shall be validated before any layout algorithm is applied. If this
   * option is enabled and at least one error is found, the layout process is aborted and a message
   * is shown to the user.
   */
  public static final IProperty<Boolean> VALIDATE_OPTIONS = new Property<Boolean>(
            "org.eclipse.elk.validateOptions",
            VALIDATE_OPTIONS_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #ZOOM_TO_FIT}.
   */
  private static final boolean ZOOM_TO_FIT_DEFAULT = false;

  /**
   * Whether the zoom level shall be set to view the whole diagram after layout.
   */
  public static final IProperty<Boolean> ZOOM_TO_FIT = new Property<Boolean>(
            "org.eclipse.elk.zoomToFit",
            ZOOM_TO_FIT_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #BOX_PACKING_MODE}.
   */
  private static final BoxLayoutProvider.PackingMode BOX_PACKING_MODE_DEFAULT = BoxLayoutProvider.PackingMode.SIMPLE;

  /**
   * Configures the packing mode used by the {@link BoxLayoutProvider}.
   * If SIMPLE is not required (neither priorities are used nor the interactive mode),
   * GROUP_DEC can improve the packing and decrease the area.
   * GROUP_MIXED and GROUP_INC may, in very specific scenarios, work better.
   */
  public static final IProperty<BoxLayoutProvider.PackingMode> BOX_PACKING_MODE = new Property<BoxLayoutProvider.PackingMode>(
            "org.eclipse.elk.box.packingMode",
            BOX_PACKING_MODE_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #SPACING_COMMENT_COMMENT}.
   */
  private static final double SPACING_COMMENT_COMMENT_DEFAULT = 10;

  /**
   * Lower bound value for {@link #SPACING_COMMENT_COMMENT}.
   */
  private static final Comparable<? super Double> SPACING_COMMENT_COMMENT_LOWER_BOUND = Double.valueOf(0.0);

  /**
   * Spacing to be preserved between a comment box and other comment boxes connected to the same node.
   * The space left between comment boxes of different nodes is controlled by the node-node spacing.
   */
  public static final IProperty<Double> SPACING_COMMENT_COMMENT = new Property<Double>(
            "org.eclipse.elk.spacing.commentComment",
            SPACING_COMMENT_COMMENT_DEFAULT,
            SPACING_COMMENT_COMMENT_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #SPACING_COMMENT_NODE}.
   */
  private static final double SPACING_COMMENT_NODE_DEFAULT = 10;

  /**
   * Lower bound value for {@link #SPACING_COMMENT_NODE}.
   */
  private static final Comparable<? super Double> SPACING_COMMENT_NODE_LOWER_BOUND = Double.valueOf(0.0);

  /**
   * Spacing to be preserved between a node and its connected comment boxes. The space left between a node
   * and the comments of another node is controlled by the node-node spacing.
   */
  public static final IProperty<Double> SPACING_COMMENT_NODE = new Property<Double>(
            "org.eclipse.elk.spacing.commentNode",
            SPACING_COMMENT_NODE_DEFAULT,
            SPACING_COMMENT_NODE_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #SPACING_COMPONENT_COMPONENT}.
   */
  private static final double SPACING_COMPONENT_COMPONENT_DEFAULT = 20f;

  /**
   * Lower bound value for {@link #SPACING_COMPONENT_COMPONENT}.
   */
  private static final Comparable<? super Double> SPACING_COMPONENT_COMPONENT_LOWER_BOUND = Double.valueOf(0.0);

  /**
   * Spacing to be preserved between pairs of connected components.
   * This option is only relevant if 'separateConnectedComponents' is activated.
   */
  public static final IProperty<Double> SPACING_COMPONENT_COMPONENT = new Property<Double>(
            "org.eclipse.elk.spacing.componentComponent",
            SPACING_COMPONENT_COMPONENT_DEFAULT,
            SPACING_COMPONENT_COMPONENT_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #SPACING_EDGE_EDGE}.
   */
  private static final double SPACING_EDGE_EDGE_DEFAULT = 10;

  /**
   * Lower bound value for {@link #SPACING_EDGE_EDGE}.
   */
  private static final Comparable<? super Double> SPACING_EDGE_EDGE_LOWER_BOUND = Double.valueOf(0.0);

  /**
   * Spacing to be preserved between any two edges. Note that while this can somewhat easily be satisfied
   * for the segments of orthogonally drawn edges, it is harder for general polylines or splines.
   */
  public static final IProperty<Double> SPACING_EDGE_EDGE = new Property<Double>(
            "org.eclipse.elk.spacing.edgeEdge",
            SPACING_EDGE_EDGE_DEFAULT,
            SPACING_EDGE_EDGE_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #SPACING_EDGE_LABEL}.
   */
  private static final double SPACING_EDGE_LABEL_DEFAULT = 2;

  /**
   * Lower bound value for {@link #SPACING_EDGE_LABEL}.
   */
  private static final Comparable<? super Double> SPACING_EDGE_LABEL_LOWER_BOUND = Double.valueOf(0.0);

  /**
   * The minimal distance to be preserved between a label and the edge it is associated with.
   * Note that the placement of a label is influenced by the 'edgelabels.placement' option.
   */
  public static final IProperty<Double> SPACING_EDGE_LABEL = new Property<Double>(
            "org.eclipse.elk.spacing.edgeLabel",
            SPACING_EDGE_LABEL_DEFAULT,
            SPACING_EDGE_LABEL_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #SPACING_EDGE_NODE}.
   */
  private static final double SPACING_EDGE_NODE_DEFAULT = 10;

  /**
   * Lower bound value for {@link #SPACING_EDGE_NODE}.
   */
  private static final Comparable<? super Double> SPACING_EDGE_NODE_LOWER_BOUND = Double.valueOf(0.0);

  /**
   * Spacing to be preserved between nodes and edges.
   */
  public static final IProperty<Double> SPACING_EDGE_NODE = new Property<Double>(
            "org.eclipse.elk.spacing.edgeNode",
            SPACING_EDGE_NODE_DEFAULT,
            SPACING_EDGE_NODE_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #SPACING_LABEL_LABEL}.
   */
  private static final double SPACING_LABEL_LABEL_DEFAULT = 0;

  /**
   * Lower bound value for {@link #SPACING_LABEL_LABEL}.
   */
  private static final Comparable<? super Double> SPACING_LABEL_LABEL_LOWER_BOUND = Double.valueOf(0.0);

  /**
   * Determines the amount of space to be left between two labels
   * of the same graph element.
   */
  public static final IProperty<Double> SPACING_LABEL_LABEL = new Property<Double>(
            "org.eclipse.elk.spacing.labelLabel",
            SPACING_LABEL_LABEL_DEFAULT,
            SPACING_LABEL_LABEL_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #SPACING_LABEL_NODE}.
   */
  private static final double SPACING_LABEL_NODE_DEFAULT = 5;

  /**
   * Lower bound value for {@link #SPACING_LABEL_NODE}.
   */
  private static final Comparable<? super Double> SPACING_LABEL_NODE_LOWER_BOUND = Double.valueOf(0.0);

  /**
   * Spacing to be preserved between labels and the border of node they are associated with.
   * Note that the placement of a label is influenced by the 'nodelabels.placement' option.
   */
  public static final IProperty<Double> SPACING_LABEL_NODE = new Property<Double>(
            "org.eclipse.elk.spacing.labelNode",
            SPACING_LABEL_NODE_DEFAULT,
            SPACING_LABEL_NODE_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #SPACING_LABEL_PORT_HORIZONTAL}.
   */
  private static final double SPACING_LABEL_PORT_HORIZONTAL_DEFAULT = 1;

  /**
   * Horizontal spacing to be preserved between labels and the ports they are associated with.
   * Note that the placement of a label is influenced by the 'portlabels.placement' option.
   */
  public static final IProperty<Double> SPACING_LABEL_PORT_HORIZONTAL = new Property<Double>(
            "org.eclipse.elk.spacing.labelPortHorizontal",
            SPACING_LABEL_PORT_HORIZONTAL_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #SPACING_LABEL_PORT_VERTICAL}.
   */
  private static final double SPACING_LABEL_PORT_VERTICAL_DEFAULT = 1;

  /**
   * Vertical spacing to be preserved between labels and the ports they are associated with.
   * Note that the placement of a label is influenced by the 'portlabels.placement' option.
   */
  public static final IProperty<Double> SPACING_LABEL_PORT_VERTICAL = new Property<Double>(
            "org.eclipse.elk.spacing.labelPortVertical",
            SPACING_LABEL_PORT_VERTICAL_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #SPACING_NODE_NODE}.
   */
  private static final double SPACING_NODE_NODE_DEFAULT = 20;

  /**
   * Lower bound value for {@link #SPACING_NODE_NODE}.
   */
  private static final Comparable<? super Double> SPACING_NODE_NODE_LOWER_BOUND = Double.valueOf(0.0);

  /**
   * The minimal distance to be preserved between each two nodes.
   */
  public static final IProperty<Double> SPACING_NODE_NODE = new Property<Double>(
            "org.eclipse.elk.spacing.nodeNode",
            SPACING_NODE_NODE_DEFAULT,
            SPACING_NODE_NODE_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #SPACING_NODE_SELF_LOOP}.
   */
  private static final double SPACING_NODE_SELF_LOOP_DEFAULT = 10;

  /**
   * Lower bound value for {@link #SPACING_NODE_SELF_LOOP}.
   */
  private static final Comparable<? super Double> SPACING_NODE_SELF_LOOP_LOWER_BOUND = Double.valueOf(0.0);

  /**
   * Spacing to be preserved between a node and its self loops.
   */
  public static final IProperty<Double> SPACING_NODE_SELF_LOOP = new Property<Double>(
            "org.eclipse.elk.spacing.nodeSelfLoop",
            SPACING_NODE_SELF_LOOP_DEFAULT,
            SPACING_NODE_SELF_LOOP_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #SPACING_PORT_PORT}.
   */
  private static final double SPACING_PORT_PORT_DEFAULT = 10;

  /**
   * Lower bound value for {@link #SPACING_PORT_PORT}.
   */
  private static final Comparable<? super Double> SPACING_PORT_PORT_LOWER_BOUND = Double.valueOf(0.0);

  /**
   * Spacing between pairs of ports of the same node.
   */
  public static final IProperty<Double> SPACING_PORT_PORT = new Property<Double>(
            "org.eclipse.elk.spacing.portPort",
            SPACING_PORT_PORT_DEFAULT,
            SPACING_PORT_PORT_LOWER_BOUND,
            null);

  /**
   * Allows to specify individual spacing values for graph elements that shall be different from
   * the value specified for the element's parent.
   */
  public static final IProperty<IndividualSpacings> SPACING_INDIVIDUAL = new Property<IndividualSpacings>(
            "org.eclipse.elk.spacing.individual");

  /**
   * Default value for {@link #SPACING_PORTS_SURROUNDING}.
   */
  private static final ElkMargin SPACING_PORTS_SURROUNDING_DEFAULT = new ElkMargin(0);

  /**
   * Additional space around the sets of ports on each node side. For each side of a node,
   * this option can reserve additional space before and after the ports on each side. For
   * example, a top spacing of 20 makes sure that the first port on the western and eastern
   * side is 20 units away from the northern border.
   */
  public static final IProperty<ElkMargin> SPACING_PORTS_SURROUNDING = new Property<ElkMargin>(
            "org.eclipse.elk.spacing.portsSurrounding",
            SPACING_PORTS_SURROUNDING_DEFAULT,
            null,
            null);

  /**
   * Partition to which the node belongs. This requires Layout Partitioning to be active. Nodes with lower
   * partition IDs will appear to the left of nodes with higher partition IDs (assuming a left-to-right layout
   * direction).
   */
  public static final IProperty<Integer> PARTITIONING_PARTITION = new Property<Integer>(
            "org.eclipse.elk.partitioning.partition");

  /**
   * Default value for {@link #PARTITIONING_ACTIVATE}.
   */
  private static final Boolean PARTITIONING_ACTIVATE_DEFAULT = Boolean.valueOf(false);

  /**
   * Whether to activate partitioned layout. This will allow to group nodes through the Layout Partition option.
   * a pair of nodes with different partition indices is then placed such that the node with lower index is
   * placed to the left of the other node (with left-to-right layout direction). Depending on the layout
   * algorithm, this may only be guaranteed to work if all nodes have a layout partition configured, or at least
   * if edges that cross partitions are not part of a partition-crossing cycle.
   */
  public static final IProperty<Boolean> PARTITIONING_ACTIVATE = new Property<Boolean>(
            "org.eclipse.elk.partitioning.activate",
            PARTITIONING_ACTIVATE_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #NODE_LABELS_PADDING}.
   */
  private static final ElkPadding NODE_LABELS_PADDING_DEFAULT = new ElkPadding(5);

  /**
   * Define padding for node labels that are placed inside of a node.
   */
  public static final IProperty<ElkPadding> NODE_LABELS_PADDING = new Property<ElkPadding>(
            "org.eclipse.elk.nodeLabels.padding",
            NODE_LABELS_PADDING_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #NODE_LABELS_PLACEMENT}.
   */
  private static final EnumSet<NodeLabelPlacement> NODE_LABELS_PLACEMENT_DEFAULT = NodeLabelPlacement.fixed();

  /**
   * Hints for where node labels are to be placed; if empty, the node label's position is not
   * modified.
   */
  public static final IProperty<EnumSet<NodeLabelPlacement>> NODE_LABELS_PLACEMENT = new Property<EnumSet<NodeLabelPlacement>>(
            "org.eclipse.elk.nodeLabels.placement",
            NODE_LABELS_PLACEMENT_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #PORT_ALIGNMENT_DEFAULT}.
   */
  private static final PortAlignment PORT_ALIGNMENT_DEFAULT_DEFAULT = PortAlignment.DISTRIBUTED;

  /**
   * Defines the default port distribution for a node. May be overridden for each side individually.
   */
  public static final IProperty<PortAlignment> PORT_ALIGNMENT_DEFAULT = new Property<PortAlignment>(
            "org.eclipse.elk.portAlignment.default",
            PORT_ALIGNMENT_DEFAULT_DEFAULT,
            null,
            null);

  /**
   * Defines how ports on the northern side are placed, overriding the node's general port alignment.
   */
  public static final IProperty<PortAlignment> PORT_ALIGNMENT_NORTH = new Property<PortAlignment>(
            "org.eclipse.elk.portAlignment.north");

  /**
   * Defines how ports on the southern side are placed, overriding the node's general port alignment.
   */
  public static final IProperty<PortAlignment> PORT_ALIGNMENT_SOUTH = new Property<PortAlignment>(
            "org.eclipse.elk.portAlignment.south");

  /**
   * Defines how ports on the western side are placed, overriding the node's general port alignment.
   */
  public static final IProperty<PortAlignment> PORT_ALIGNMENT_WEST = new Property<PortAlignment>(
            "org.eclipse.elk.portAlignment.west");

  /**
   * Defines how ports on the eastern side are placed, overriding the node's general port alignment.
   */
  public static final IProperty<PortAlignment> PORT_ALIGNMENT_EAST = new Property<PortAlignment>(
            "org.eclipse.elk.portAlignment.east");

  /**
   * Default value for {@link #NODE_SIZE_CONSTRAINTS}.
   */
  private static final EnumSet<SizeConstraint> NODE_SIZE_CONSTRAINTS_DEFAULT = EnumSet.<SizeConstraint>noneOf(SizeConstraint.class);

  /**
   * What should be taken into account when calculating a node's size. Empty size constraints
   * specify that a node's size is already fixed and should not be changed.
   */
  public static final IProperty<EnumSet<SizeConstraint>> NODE_SIZE_CONSTRAINTS = new Property<EnumSet<SizeConstraint>>(
            "org.eclipse.elk.nodeSize.constraints",
            NODE_SIZE_CONSTRAINTS_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #NODE_SIZE_OPTIONS}.
   */
  private static final EnumSet<SizeOptions> NODE_SIZE_OPTIONS_DEFAULT = EnumSet.<SizeOptions>of(SizeOptions.DEFAULT_MINIMUM_SIZE);

  /**
   * Options modifying the behavior of the size constraints set on a node. Each member of the
   * set specifies something that should be taken into account when calculating node sizes.
   * The empty set corresponds to no further modifications.
   */
  public static final IProperty<EnumSet<SizeOptions>> NODE_SIZE_OPTIONS = new Property<EnumSet<SizeOptions>>(
            "org.eclipse.elk.nodeSize.options",
            NODE_SIZE_OPTIONS_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #NODE_SIZE_MINIMUM}.
   */
  private static final KVector NODE_SIZE_MINIMUM_DEFAULT = new KVector(0, 0);

  /**
   * The minimal size to which a node can be reduced.
   */
  public static final IProperty<KVector> NODE_SIZE_MINIMUM = new Property<KVector>(
            "org.eclipse.elk.nodeSize.minimum",
            NODE_SIZE_MINIMUM_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #NODE_SIZE_FIXED_GRAPH_SIZE}.
   */
  private static final boolean NODE_SIZE_FIXED_GRAPH_SIZE_DEFAULT = false;

  /**
   * By default, the fixed layout provider will enlarge a graph until it is large enough to contain
   * its children. If this option is set, it won't do so.
   */
  public static final IProperty<Boolean> NODE_SIZE_FIXED_GRAPH_SIZE = new Property<Boolean>(
            "org.eclipse.elk.nodeSize.fixedGraphSize",
            NODE_SIZE_FIXED_GRAPH_SIZE_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #EDGE_LABELS_PLACEMENT}.
   */
  private static final EdgeLabelPlacement EDGE_LABELS_PLACEMENT_DEFAULT = EdgeLabelPlacement.CENTER;

  /**
   * Gives a hint on where to put edge labels.
   */
  public static final IProperty<EdgeLabelPlacement> EDGE_LABELS_PLACEMENT = new Property<EdgeLabelPlacement>(
            "org.eclipse.elk.edgeLabels.placement",
            EDGE_LABELS_PLACEMENT_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #EDGE_LABELS_INLINE}.
   */
  private static final boolean EDGE_LABELS_INLINE_DEFAULT = false;

  /**
   * If true, an edge label is placed directly on its edge. May only apply to center edge labels.
   * This kind of label placement is only advisable if the label's rendering is such that it is not
   * crossed by its edge and thus stays legible.
   */
  public static final IProperty<Boolean> EDGE_LABELS_INLINE = new Property<Boolean>(
            "org.eclipse.elk.edgeLabels.inline",
            EDGE_LABELS_INLINE_DEFAULT,
            null,
            null);

  /**
   * Font name used for a label.
   */
  public static final IProperty<String> FONT_NAME = new Property<String>(
            "org.eclipse.elk.font.name");

  /**
   * Lower bound value for {@link #FONT_SIZE}.
   */
  private static final Comparable<? super Integer> FONT_SIZE_LOWER_BOUND = Integer.valueOf(1);

  /**
   * Font size used for a label.
   */
  public static final IProperty<Integer> FONT_SIZE = new Property<Integer>(
            "org.eclipse.elk.font.size",
            null,
            FONT_SIZE_LOWER_BOUND,
            null);

  /**
   * The offset to the port position where connections shall be attached.
   */
  public static final IProperty<KVector> PORT_ANCHOR = new Property<KVector>(
            "org.eclipse.elk.port.anchor");

  /**
   * The index of a port in the fixed order around a node. The order is assumed as clockwise,
   * starting with the leftmost port on the top side. This option must be set if 'Port
   * Constraints' is set to FIXED_ORDER and no specific positions are given for the ports.
   * Additionally, the option 'Port Side' must be defined in this case.
   */
  public static final IProperty<Integer> PORT_INDEX = new Property<Integer>(
            "org.eclipse.elk.port.index");

  /**
   * Default value for {@link #PORT_SIDE}.
   */
  private static final PortSide PORT_SIDE_DEFAULT = PortSide.UNDEFINED;

  /**
   * The side of a node on which a port is situated. This option must be set if 'Port
   * Constraints' is set to FIXED_SIDE or FIXED_ORDER and no specific positions are given
   * for the ports.
   */
  public static final IProperty<PortSide> PORT_SIDE = new Property<PortSide>(
            "org.eclipse.elk.port.side",
            PORT_SIDE_DEFAULT,
            null,
            null);

  /**
   * The offset of ports on the node border. With a positive offset the port is moved outside
   * of the node, while with a negative offset the port is moved towards the inside. An offset
   * of 0 means that the port is placed directly on the node border, i.e.
   * if the port side is north, the port's south border touches the nodes's north border;
   * if the port side is east, the port's west border touches the nodes's east border;
   * if the port side is south, the port's north border touches the node's south border;
   * if the port side is west, the port's east border touches the node's west border.
   */
  public static final IProperty<Double> PORT_BORDER_OFFSET = new Property<Double>(
            "org.eclipse.elk.port.borderOffset");

  /**
   * Default value for {@link #PORT_LABELS_PLACEMENT}.
   */
  private static final EnumSet<PortLabelPlacement> PORT_LABELS_PLACEMENT_DEFAULT = PortLabelPlacement.outside();

  /**
   * Decides on a placement method for port labels; if empty, the node label's position is not
   * modified.
   */
  public static final IProperty<EnumSet<PortLabelPlacement>> PORT_LABELS_PLACEMENT = new Property<EnumSet<PortLabelPlacement>>(
            "org.eclipse.elk.portLabels.placement",
            PORT_LABELS_PLACEMENT_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #PORT_LABELS_NEXT_TO_PORT_IF_POSSIBLE}.
   */
  private static final boolean PORT_LABELS_NEXT_TO_PORT_IF_POSSIBLE_DEFAULT = false;

  /**
   * Use 'portLabels.placement': NEXT_TO_PORT_OF_POSSIBLE.
   */
  @Deprecated
  public static final IProperty<Boolean> PORT_LABELS_NEXT_TO_PORT_IF_POSSIBLE = new Property<Boolean>(
            "org.eclipse.elk.portLabels.nextToPortIfPossible",
            PORT_LABELS_NEXT_TO_PORT_IF_POSSIBLE_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #PORT_LABELS_TREAT_AS_GROUP}.
   */
  private static final boolean PORT_LABELS_TREAT_AS_GROUP_DEFAULT = true;

  /**
   * If this option is true (default), the labels of a port will be treated as a group when
   * it comes to centering them next to their port. If this option is false, only the first label will
   * be centered next to the port, with the others being placed below. This only applies to labels of
   * eastern and western ports and will have no effect if labels are not placed next to their port.
   */
  public static final IProperty<Boolean> PORT_LABELS_TREAT_AS_GROUP = new Property<Boolean>(
            "org.eclipse.elk.portLabels.treatAsGroup",
            PORT_LABELS_TREAT_AS_GROUP_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #TOPDOWN_SCALE_FACTOR}.
   */
  private static final double TOPDOWN_SCALE_FACTOR_DEFAULT = 1;

  /**
   * Lower bound value for {@link #TOPDOWN_SCALE_FACTOR}.
   */
  private static final Comparable<? super Double> TOPDOWN_SCALE_FACTOR_LOWER_BOUND = ExclusiveBounds.greaterThan(0);

  /**
   * The scaling factor to be applied to the nodes laid out within the node in recursive topdown
   * layout. The difference to 'Scale Factor' is that the node itself is not scaled. This value has to be set on
   * hierarchical nodes.
   */
  public static final IProperty<Double> TOPDOWN_SCALE_FACTOR = new Property<Double>(
            "org.eclipse.elk.topdown.scaleFactor",
            TOPDOWN_SCALE_FACTOR_DEFAULT,
            TOPDOWN_SCALE_FACTOR_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #TOPDOWN_SIZE_APPROXIMATOR}.
   */
  private static final TopdownSizeApproximator TOPDOWN_SIZE_APPROXIMATOR_DEFAULT = null;

  /**
   * The size approximator to be used to set sizes of hierarchical nodes during topdown layout. The default
   * value is null, which results in nodes keeping whatever size is defined for them e.g. through parent
   * parallel node or by manually setting the size.
   */
  public static final IProperty<TopdownSizeApproximator> TOPDOWN_SIZE_APPROXIMATOR = new Property<TopdownSizeApproximator>(
            "org.eclipse.elk.topdown.sizeApproximator",
            TOPDOWN_SIZE_APPROXIMATOR_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #TOPDOWN_HIERARCHICAL_NODE_WIDTH}.
   */
  private static final double TOPDOWN_HIERARCHICAL_NODE_WIDTH_DEFAULT = 150;

  /**
   * The fixed size of a hierarchical node when using topdown layout. If this value is set on a parallel
   * node it applies to its children, when set on a hierarchical node it applies to the node itself.
   */
  public static final IProperty<Double> TOPDOWN_HIERARCHICAL_NODE_WIDTH = new Property<Double>(
            "org.eclipse.elk.topdown.hierarchicalNodeWidth",
            TOPDOWN_HIERARCHICAL_NODE_WIDTH_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #TOPDOWN_HIERARCHICAL_NODE_ASPECT_RATIO}.
   */
  private static final double TOPDOWN_HIERARCHICAL_NODE_ASPECT_RATIO_DEFAULT = 1.414;

  /**
   * The fixed aspect ratio of a hierarchical node when using topdown layout. Default is 1/sqrt(2). If this
   * value is set on a parallel node it applies to its children, when set on a hierarchical node it applies to
   * the node itself.
   */
  public static final IProperty<Double> TOPDOWN_HIERARCHICAL_NODE_ASPECT_RATIO = new Property<Double>(
            "org.eclipse.elk.topdown.hierarchicalNodeAspectRatio",
            TOPDOWN_HIERARCHICAL_NODE_ASPECT_RATIO_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #TOPDOWN_NODE_TYPE}.
   */
  private static final TopdownNodeTypes TOPDOWN_NODE_TYPE_DEFAULT = null;

  /**
   * The different node types used for topdown layout. If the node type is set
   * to {@link TopdownNodeTypes.PARALLEL_NODE} the algorithm must be set to a {@link TopdownLayoutProvider} such
   * as {@link TopdownPacking}. The {@link nodeSize.fixedGraphSize} option is technically only required for
   * hierarchical nodes.
   */
  public static final IProperty<TopdownNodeTypes> TOPDOWN_NODE_TYPE = new Property<TopdownNodeTypes>(
            "org.eclipse.elk.topdown.nodeType",
            TOPDOWN_NODE_TYPE_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #TOPDOWN_SCALE_CAP}.
   */
  private static final double TOPDOWN_SCALE_CAP_DEFAULT = 1;

  /**
   * Determines the upper limit for the topdown scale factor. The default value is 1.0 which ensures
   * that nested children never end up appearing larger than their parents in terms of unit sizes such
   * as the font size. If the limit is larger, nodes will fully utilize the available space, but it is
   * counteriniuitive for inner nodes to have a larger scale than outer nodes.
   */
  public static final IProperty<Double> TOPDOWN_SCALE_CAP = new Property<Double>(
            "org.eclipse.elk.topdown.scaleCap",
            TOPDOWN_SCALE_CAP_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #INSIDE_SELF_LOOPS_ACTIVATE}.
   */
  private static final boolean INSIDE_SELF_LOOPS_ACTIVATE_DEFAULT = false;

  /**
   * Whether this node allows to route self loops inside of it instead of around it. If set to true,
   * this will make the node a compound node if it isn't already, and will require the layout algorithm
   * to support compound nodes with hierarchical ports.
   */
  public static final IProperty<Boolean> INSIDE_SELF_LOOPS_ACTIVATE = new Property<Boolean>(
            "org.eclipse.elk.insideSelfLoops.activate",
            INSIDE_SELF_LOOPS_ACTIVATE_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #INSIDE_SELF_LOOPS_YO}.
   */
  private static final boolean INSIDE_SELF_LOOPS_YO_DEFAULT = false;

  /**
   * Whether a self loop should be routed inside a node instead of around that node.
   */
  public static final IProperty<Boolean> INSIDE_SELF_LOOPS_YO = new Property<Boolean>(
            "org.eclipse.elk.insideSelfLoops.yo",
            INSIDE_SELF_LOOPS_YO_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #EDGE_THICKNESS}.
   */
  private static final double EDGE_THICKNESS_DEFAULT = 1;

  /**
   * The thickness of an edge. This is a hint on the line width used to draw an edge, possibly
   * requiring more space to be reserved for it.
   */
  public static final IProperty<Double> EDGE_THICKNESS = new Property<Double>(
            "org.eclipse.elk.edge.thickness",
            EDGE_THICKNESS_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #EDGE_TYPE}.
   */
  private static final EdgeType EDGE_TYPE_DEFAULT = EdgeType.NONE;

  /**
   * The type of an edge. This is usually used for UML class diagrams, where associations must
   * be handled differently from generalizations.
   */
  public static final IProperty<EdgeType> EDGE_TYPE = new Property<EdgeType>(
            "org.eclipse.elk.edge.type",
            EDGE_TYPE_DEFAULT,
            null,
            null);

  /**
   * Required value for dependency between {@link #PARTITIONING_PARTITION} and {@link #PARTITIONING_ACTIVATE}.
   */
  private static final Boolean PARTITIONING_PARTITION_DEP_PARTITIONING_ACTIVATE_0 = Boolean.valueOf(true);

  /**
   * Required value for dependency between {@link #TOPDOWN_SCALE_FACTOR} and {@link #TOPDOWN_NODE_TYPE}.
   */
  private static final TopdownNodeTypes TOPDOWN_SCALE_FACTOR_DEP_TOPDOWN_NODE_TYPE_0 = TopdownNodeTypes.HIERARCHICAL_NODE;

  /**
   * Required value for dependency between {@link #TOPDOWN_SIZE_APPROXIMATOR} and {@link #TOPDOWN_NODE_TYPE}.
   */
  private static final TopdownNodeTypes TOPDOWN_SIZE_APPROXIMATOR_DEP_TOPDOWN_NODE_TYPE_0 = TopdownNodeTypes.HIERARCHICAL_NODE;

  /**
   * Required value for dependency between {@link #TOPDOWN_SCALE_CAP} and {@link #TOPDOWN_NODE_TYPE}.
   */
  private static final TopdownNodeTypes TOPDOWN_SCALE_CAP_DEP_TOPDOWN_NODE_TYPE_0 = TopdownNodeTypes.HIERARCHICAL_NODE;

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.algorithm")
        .group("")
        .name("Layout Algorithm")
        .description("Select a specific layout algorithm.")
        .type(LayoutOptionData.Type.STRING)
        .optionClass(String.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.resolvedAlgorithm")
        .group("")
        .name("Resolved Layout Algorithm")
        .description("Meta data associated with the selected algorithm.")
        .type(LayoutOptionData.Type.OBJECT)
        .optionClass(LayoutAlgorithmData.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alignment")
        .group("")
        .name("Alignment")
        .description("Alignment of the selected node relative to other nodes; the exact meaning depends on the used algorithm.")
        .defaultValue(ALIGNMENT_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(Alignment.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.aspectRatio")
        .group("")
        .name("Aspect Ratio")
        .description("The desired aspect ratio of the drawing, that is the quotient of width by height.")
        .lowerBound(ASPECT_RATIO_LOWER_BOUND)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.bendPoints")
        .group("")
        .name("Bend Points")
        .description("A fixed list of bend points for the edge. This is used by the \'Fixed Layout\' algorithm to specify a pre-defined routing for an edge. The vector chain must include the source point, any bend points, and the target point, so it must have at least two points.")
        .type(LayoutOptionData.Type.OBJECT)
        .optionClass(KVectorChain.class)
        .targets(EnumSet.of(LayoutOptionData.Target.EDGES))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.contentAlignment")
        .group("")
        .name("Content Alignment")
        .description("Specifies how the content of a node are aligned. Each node can individually control the alignment of its contents. I.e. if a node should be aligned top left in its parent node, the parent node should specify that option.")
        .defaultValue(CONTENT_ALIGNMENT_DEFAULT)
        .type(LayoutOptionData.Type.ENUMSET)
        .optionClass(ContentAlignment.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.debugMode")
        .group("")
        .name("Debug Mode")
        .description("Whether additional debug information shall be generated.")
        .defaultValue(DEBUG_MODE_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.direction")
        .group("")
        .name("Direction")
        .description("Overall direction of edges: horizontal (right / left) or vertical (down / up).")
        .defaultValue(DIRECTION_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(Direction.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.edgeRouting")
        .group("")
        .name("Edge Routing")
        .description("What kind of edge routing style should be applied for the content of a parent node. Algorithms may also set this option to single edges in order to mark them as splines. The bend point list of edges with this option set to SPLINES must be interpreted as control points for a piecewise cubic spline.")
        .defaultValue(EDGE_ROUTING_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(EdgeRouting.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.expandNodes")
        .group("")
        .name("Expand Nodes")
        .description("If active, nodes are expanded to fill the area of their parent.")
        .defaultValue(EXPAND_NODES_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.hierarchyHandling")
        .group("")
        .name("Hierarchy Handling")
        .description("Determines whether separate layout runs are triggered for different compound nodes in a hierarchical graph. Setting a node\'s hierarchy handling to `INCLUDE_CHILDREN` will lay out that node and all of its descendants in a single layout run, until a descendant is encountered which has its hierarchy handling set to `SEPARATE_CHILDREN`. In general, `SEPARATE_CHILDREN` will ensure that a new layout run is triggered for a node with that setting. Including multiple levels of hierarchy in a single layout run may allow cross-hierarchical edges to be laid out properly. If the root node is set to `INHERIT` (or not set at all), the default behavior is `SEPARATE_CHILDREN`.")
        .defaultValue(HIERARCHY_HANDLING_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(HierarchyHandling.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS, LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.padding")
        .group("")
        .name("Padding")
        .description("The padding to be left to a parent element\'s border when placing child elements. This can also serve as an output option of a layout algorithm if node size calculation is setup appropriately.")
        .defaultValue(PADDING_DEFAULT)
        .type(LayoutOptionData.Type.OBJECT)
        .optionClass(ElkPadding.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS, LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.interactive")
        .group("")
        .name("Interactive")
        .description("Whether the algorithm should be run in interactive mode for the content of a parent node. What this means exactly depends on how the specific algorithm interprets this option. Usually in the interactive mode algorithms try to modify the current layout as little as possible.")
        .defaultValue(INTERACTIVE_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.interactiveLayout")
        .group("")
        .name("interactive Layout")
        .description("Whether the graph should be changeable interactively and by setting constraints")
        .defaultValue(INTERACTIVE_LAYOUT_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.omitNodeMicroLayout")
        .group("")
        .name("Omit Node Micro Layout")
        .description("Node micro layout comprises the computation of node dimensions (if requested), the placement of ports and their labels, and the placement of node labels. The functionality is implemented independent of any specific layout algorithm and shouldn\'t have any negative impact on the layout algorithm\'s performance itself. Yet, if any unforeseen behavior occurs, this option allows to deactivate the micro layout.")
        .defaultValue(OMIT_NODE_MICRO_LAYOUT_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.portConstraints")
        .group("")
        .name("Port Constraints")
        .description("Defines constraints of the position of the ports of a node.")
        .defaultValue(PORT_CONSTRAINTS_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(PortConstraints.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.position")
        .group("")
        .name("Position")
        .description("The position of a node, port, or label. This is used by the \'Fixed Layout\' algorithm to specify a pre-defined position.")
        .type(LayoutOptionData.Type.OBJECT)
        .optionClass(KVector.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES, LayoutOptionData.Target.PORTS, LayoutOptionData.Target.LABELS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.priority")
        .group("")
        .name("Priority")
        .description("Defines the priority of an object; its meaning depends on the specific layout algorithm and the context where it is used.")
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES, LayoutOptionData.Target.EDGES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.randomSeed")
        .group("")
        .name("Randomization Seed")
        .description("Seed used for pseudo-random number generators to control the layout algorithm. If the value is 0, the seed shall be determined pseudo-randomly (e.g. from the system time).")
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.separateConnectedComponents")
        .group("")
        .name("Separate Connected Components")
        .description("Whether each connected component should be processed separately.")
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.junctionPoints")
        .group("")
        .name("Junction Points")
        .description("This option is not used as option, but as output of the layout algorithms. It is attached to edges and determines the points where junction symbols should be drawn in order to represent hyperedges with orthogonal routing. Whether such points are computed depends on the chosen layout algorithm and edge routing style. The points are put into the vector chain with no specific order.")
        .defaultValue(JUNCTION_POINTS_DEFAULT)
        .type(LayoutOptionData.Type.OBJECT)
        .optionClass(KVectorChain.class)
        .targets(EnumSet.of(LayoutOptionData.Target.EDGES))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.commentBox")
        .group("")
        .name("Comment Box")
        .description("Whether the node should be regarded as a comment box instead of a regular node. In that case its placement should be similar to how labels are handled. Any edges incident to a comment box specify to which graph elements the comment is related.")
        .defaultValue(COMMENT_BOX_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.hypernode")
        .group("")
        .name("Hypernode")
        .description("Whether the node should be handled as a hypernode.")
        .defaultValue(HYPERNODE_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.labelManager")
        .group("")
        .name("Label Manager")
        .description("Label managers can shorten labels upon a layout algorithm\'s request.")
        .type(LayoutOptionData.Type.OBJECT)
        .optionClass(ILabelManager.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS, LayoutOptionData.Target.LABELS))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.margins")
        .group("")
        .name("Margins")
        .description("Margins define additional space around the actual bounds of a graph element. For instance, ports or labels being placed on the outside of a node\'s border might introduce such a margin. The margin is used to guarantee non-overlap of other graph elements with those ports or labels.")
        .defaultValue(MARGINS_DEFAULT)
        .type(LayoutOptionData.Type.OBJECT)
        .optionClass(ElkMargin.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.noLayout")
        .group("")
        .name("No Layout")
        .description("No layout is done for the associated element. This is used to mark parts of a diagram to avoid their inclusion in the layout graph, or to mark parts of the layout graph to prevent layout engines from processing them. If you wish to exclude the contents of a compound node from automatic layout, while the node itself is still considered on its own layer, use the \'Fixed Layout\' algorithm for that node.")
        .defaultValue(NO_LAYOUT_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES, LayoutOptionData.Target.EDGES, LayoutOptionData.Target.PORTS, LayoutOptionData.Target.LABELS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.scaleFactor")
        .group("")
        .name("Scale Factor")
        .description("The scaling factor to be applied to the corresponding node in recursive layout. It causes the corresponding node\'s size to be adjusted, and its ports and labels to be sized and placed accordingly after the layout of that node has been determined (and before the node itself and its siblings are arranged). The scaling is not reverted afterwards, so the resulting layout graph contains the adjusted size and position data. This option is currently not supported if \'Layout Hierarchy\' is set.")
        .defaultValue(SCALE_FACTOR_DEFAULT)
        .lowerBound(SCALE_FACTOR_LOWER_BOUND)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.childAreaWidth")
        .group("")
        .name("Child Area Width")
        .description("The width of the area occupied by the laid out children of a node.")
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.childAreaHeight")
        .group("")
        .name("Child Area Height")
        .description("The height of the area occupied by the laid out children of a node.")
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.topdownLayout")
        .group("")
        .name("Topdown Layout")
        .description("Turns topdown layout on and off. If this option is enabled, hierarchical layout will be computed first for the root node and then for its children recursively. Layouts are then scaled down to fit the area provided by their parents. Graphs must follow a certain structure for topdown layout to work properly. {@link TopdownNodeTypes.PARALLEL_NODE} nodes must have children of type {@link TopdownNodeTypes.HIERARCHICAL_NODE} and must define {@link topdown.hierarchicalNodeWidth} and {@link topdown.hierarchicalNodeAspectRatio} for their children. Furthermore they need to be laid out using an algorithm that is a {@link TopdownLayoutProvider}. Hierarchical nodes can also be parents of other hierarchical nodes and can optionally use a {@link TopdownSizeApproximator} to dynamically set sizes during topdown layout. In this case {@link topdown.hierarchicalNodeWidth} and {@link topdown.hierarchicalNodeAspectRatio} should be set on the node itself rather than the parent. The values are then used by the size approximator as base values. Hierarchical nodes require the layout option {@link nodeSize.fixedGraphSize} to be true to prevent the algorithm used there from resizing the hierarchical node. This option is not supported if \'Hierarchy Handling\' is set to \'INCLUDE_CHILDREN\'")
        .defaultValue(TOPDOWN_LAYOUT_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.topdownLayout",
        "org.eclipse.elk.topdown.nodeType",
        null
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.animate")
        .group("")
        .name("Animate")
        .description("Whether the shift from the old layout to the new computed layout shall be animated.")
        .defaultValue(ANIMATE_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.animTimeFactor")
        .group("")
        .name("Animation Time Factor")
        .description("Factor for computation of animation time. The higher the value, the longer the animation time. If the value is 0, the resulting time is always equal to the minimum defined by \'Minimal Animation Time\'.")
        .defaultValue(ANIM_TIME_FACTOR_DEFAULT)
        .lowerBound(ANIM_TIME_FACTOR_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layoutAncestors")
        .group("")
        .name("Layout Ancestors")
        .description("Whether the hierarchy levels on the path from the selected element to the root of the diagram shall be included in the layout process.")
        .defaultValue(LAYOUT_ANCESTORS_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.maxAnimTime")
        .group("")
        .name("Maximal Animation Time")
        .description("The maximal time for animations, in milliseconds.")
        .defaultValue(MAX_ANIM_TIME_DEFAULT)
        .lowerBound(MAX_ANIM_TIME_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.minAnimTime")
        .group("")
        .name("Minimal Animation Time")
        .description("The minimal time for animations, in milliseconds.")
        .defaultValue(MIN_ANIM_TIME_DEFAULT)
        .lowerBound(MIN_ANIM_TIME_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.progressBar")
        .group("")
        .name("Progress Bar")
        .description("Whether a progress bar shall be displayed during layout computations.")
        .defaultValue(PROGRESS_BAR_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.validateGraph")
        .group("")
        .name("Validate Graph")
        .description("Whether the graph shall be validated before any layout algorithm is applied. If this option is enabled and at least one error is found, the layout process is aborted and a message is shown to the user.")
        .defaultValue(VALIDATE_GRAPH_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.validateOptions")
        .group("")
        .name("Validate Options")
        .description("Whether layout options shall be validated before any layout algorithm is applied. If this option is enabled and at least one error is found, the layout process is aborted and a message is shown to the user.")
        .defaultValue(VALIDATE_OPTIONS_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.zoomToFit")
        .group("")
        .name("Zoom to Fit")
        .description("Whether the zoom level shall be set to view the whole diagram after layout.")
        .defaultValue(ZOOM_TO_FIT_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.box.packingMode")
        .group("box")
        .name("Box Layout Mode")
        .description("Configures the packing mode used by the {@link BoxLayoutProvider}. If SIMPLE is not required (neither priorities are used nor the interactive mode), GROUP_DEC can improve the packing and decrease the area. GROUP_MIXED and GROUP_INC may, in very specific scenarios, work better.")
        .defaultValue(BOX_PACKING_MODE_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(BoxLayoutProvider.PackingMode.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.spacing.commentComment")
        .group("spacing")
        .name("Comment Comment Spacing")
        .description("Spacing to be preserved between a comment box and other comment boxes connected to the same node. The space left between comment boxes of different nodes is controlled by the node-node spacing.")
        .defaultValue(SPACING_COMMENT_COMMENT_DEFAULT)
        .lowerBound(SPACING_COMMENT_COMMENT_LOWER_BOUND)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.spacing.commentNode")
        .group("spacing")
        .name("Comment Node Spacing")
        .description("Spacing to be preserved between a node and its connected comment boxes. The space left between a node and the comments of another node is controlled by the node-node spacing.")
        .defaultValue(SPACING_COMMENT_NODE_DEFAULT)
        .lowerBound(SPACING_COMMENT_NODE_LOWER_BOUND)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.spacing.componentComponent")
        .group("spacing")
        .name("Components Spacing")
        .description("Spacing to be preserved between pairs of connected components. This option is only relevant if \'separateConnectedComponents\' is activated.")
        .defaultValue(SPACING_COMPONENT_COMPONENT_DEFAULT)
        .lowerBound(SPACING_COMPONENT_COMPONENT_LOWER_BOUND)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.spacing.edgeEdge")
        .group("spacing")
        .name("Edge Spacing")
        .description("Spacing to be preserved between any two edges. Note that while this can somewhat easily be satisfied for the segments of orthogonally drawn edges, it is harder for general polylines or splines.")
        .defaultValue(SPACING_EDGE_EDGE_DEFAULT)
        .lowerBound(SPACING_EDGE_EDGE_LOWER_BOUND)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.spacing.edgeLabel")
        .group("spacing")
        .name("Edge Label Spacing")
        .description("The minimal distance to be preserved between a label and the edge it is associated with. Note that the placement of a label is influenced by the \'edgelabels.placement\' option.")
        .defaultValue(SPACING_EDGE_LABEL_DEFAULT)
        .lowerBound(SPACING_EDGE_LABEL_LOWER_BOUND)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.spacing.edgeNode")
        .group("spacing")
        .name("Edge Node Spacing")
        .description("Spacing to be preserved between nodes and edges.")
        .defaultValue(SPACING_EDGE_NODE_DEFAULT)
        .lowerBound(SPACING_EDGE_NODE_LOWER_BOUND)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.spacing.labelLabel")
        .group("spacing")
        .name("Label Spacing")
        .description("Determines the amount of space to be left between two labels of the same graph element.")
        .defaultValue(SPACING_LABEL_LABEL_DEFAULT)
        .lowerBound(SPACING_LABEL_LABEL_LOWER_BOUND)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.spacing.labelNode")
        .group("spacing")
        .name("Label Node Spacing")
        .description("Spacing to be preserved between labels and the border of node they are associated with. Note that the placement of a label is influenced by the \'nodelabels.placement\' option.")
        .defaultValue(SPACING_LABEL_NODE_DEFAULT)
        .lowerBound(SPACING_LABEL_NODE_LOWER_BOUND)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.spacing.labelPortHorizontal")
        .group("spacing")
        .name("Horizontal spacing between Label and Port")
        .description("Horizontal spacing to be preserved between labels and the ports they are associated with. Note that the placement of a label is influenced by the \'portlabels.placement\' option.")
        .defaultValue(SPACING_LABEL_PORT_HORIZONTAL_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.spacing.labelPortVertical")
        .group("spacing")
        .name("Vertical spacing between Label and Port")
        .description("Vertical spacing to be preserved between labels and the ports they are associated with. Note that the placement of a label is influenced by the \'portlabels.placement\' option.")
        .defaultValue(SPACING_LABEL_PORT_VERTICAL_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.spacing.nodeNode")
        .group("spacing")
        .name("Node Spacing")
        .description("The minimal distance to be preserved between each two nodes.")
        .defaultValue(SPACING_NODE_NODE_DEFAULT)
        .lowerBound(SPACING_NODE_NODE_LOWER_BOUND)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.spacing.nodeSelfLoop")
        .group("spacing")
        .name("Node Self Loop Spacing")
        .description("Spacing to be preserved between a node and its self loops.")
        .defaultValue(SPACING_NODE_SELF_LOOP_DEFAULT)
        .lowerBound(SPACING_NODE_SELF_LOOP_LOWER_BOUND)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.spacing.portPort")
        .group("spacing")
        .name("Port Spacing")
        .description("Spacing between pairs of ports of the same node.")
        .defaultValue(SPACING_PORT_PORT_DEFAULT)
        .lowerBound(SPACING_PORT_PORT_LOWER_BOUND)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS, LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.spacing.individual")
        .group("spacing")
        .name("Individual Spacing")
        .description("Allows to specify individual spacing values for graph elements that shall be different from the value specified for the element\'s parent.")
        .type(LayoutOptionData.Type.OBJECT)
        .optionClass(IndividualSpacings.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES, LayoutOptionData.Target.EDGES, LayoutOptionData.Target.PORTS, LayoutOptionData.Target.LABELS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.spacing.portsSurrounding")
        .group("spacing")
        .name("Additional Port Space")
        .description("Additional space around the sets of ports on each node side. For each side of a node, this option can reserve additional space before and after the ports on each side. For example, a top spacing of 20 makes sure that the first port on the western and eastern side is 20 units away from the northern border.")
        .defaultValue(SPACING_PORTS_SURROUNDING_DEFAULT)
        .type(LayoutOptionData.Type.OBJECT)
        .optionClass(ElkMargin.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.partitioning.partition")
        .group("partitioning")
        .name("Layout Partition")
        .description("Partition to which the node belongs. This requires Layout Partitioning to be active. Nodes with lower partition IDs will appear to the left of nodes with higher partition IDs (assuming a left-to-right layout direction).")
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS, LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.partitioning.partition",
        "org.eclipse.elk.partitioning.activate",
        PARTITIONING_PARTITION_DEP_PARTITIONING_ACTIVATE_0
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.partitioning.activate")
        .group("partitioning")
        .name("Layout Partitioning")
        .description("Whether to activate partitioned layout. This will allow to group nodes through the Layout Partition option. a pair of nodes with different partition indices is then placed such that the node with lower index is placed to the left of the other node (with left-to-right layout direction). Depending on the layout algorithm, this may only be guaranteed to work if all nodes have a layout partition configured, or at least if edges that cross partitions are not part of a partition-crossing cycle.")
        .defaultValue(PARTITIONING_ACTIVATE_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.nodeLabels.padding")
        .group("nodeLabels")
        .name("Node Label Padding")
        .description("Define padding for node labels that are placed inside of a node.")
        .defaultValue(NODE_LABELS_PADDING_DEFAULT)
        .type(LayoutOptionData.Type.OBJECT)
        .optionClass(ElkPadding.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.nodeLabels.placement")
        .group("nodeLabels")
        .name("Node Label Placement")
        .description("Hints for where node labels are to be placed; if empty, the node label\'s position is not modified.")
        .defaultValue(NODE_LABELS_PLACEMENT_DEFAULT)
        .type(LayoutOptionData.Type.ENUMSET)
        .optionClass(NodeLabelPlacement.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES, LayoutOptionData.Target.LABELS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.portAlignment.default")
        .group("portAlignment")
        .name("Port Alignment")
        .description("Defines the default port distribution for a node. May be overridden for each side individually.")
        .defaultValue(PORT_ALIGNMENT_DEFAULT_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(PortAlignment.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.portAlignment.north")
        .group("portAlignment")
        .name("Port Alignment (North)")
        .description("Defines how ports on the northern side are placed, overriding the node\'s general port alignment.")
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(PortAlignment.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.portAlignment.south")
        .group("portAlignment")
        .name("Port Alignment (South)")
        .description("Defines how ports on the southern side are placed, overriding the node\'s general port alignment.")
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(PortAlignment.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.portAlignment.west")
        .group("portAlignment")
        .name("Port Alignment (West)")
        .description("Defines how ports on the western side are placed, overriding the node\'s general port alignment.")
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(PortAlignment.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.portAlignment.east")
        .group("portAlignment")
        .name("Port Alignment (East)")
        .description("Defines how ports on the eastern side are placed, overriding the node\'s general port alignment.")
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(PortAlignment.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.nodeSize.constraints")
        .group("nodeSize")
        .name("Node Size Constraints")
        .description("What should be taken into account when calculating a node\'s size. Empty size constraints specify that a node\'s size is already fixed and should not be changed.")
        .defaultValue(NODE_SIZE_CONSTRAINTS_DEFAULT)
        .type(LayoutOptionData.Type.ENUMSET)
        .optionClass(SizeConstraint.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.nodeSize.options")
        .group("nodeSize")
        .name("Node Size Options")
        .description("Options modifying the behavior of the size constraints set on a node. Each member of the set specifies something that should be taken into account when calculating node sizes. The empty set corresponds to no further modifications.")
        .defaultValue(NODE_SIZE_OPTIONS_DEFAULT)
        .type(LayoutOptionData.Type.ENUMSET)
        .optionClass(SizeOptions.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.nodeSize.minimum")
        .group("nodeSize")
        .name("Node Size Minimum")
        .description("The minimal size to which a node can be reduced.")
        .defaultValue(NODE_SIZE_MINIMUM_DEFAULT)
        .type(LayoutOptionData.Type.OBJECT)
        .optionClass(KVector.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.nodeSize.fixedGraphSize")
        .group("nodeSize")
        .name("Fixed Graph Size")
        .description("By default, the fixed layout provider will enlarge a graph until it is large enough to contain its children. If this option is set, it won\'t do so.")
        .defaultValue(NODE_SIZE_FIXED_GRAPH_SIZE_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.edgeLabels.placement")
        .group("edgeLabels")
        .name("Edge Label Placement")
        .description("Gives a hint on where to put edge labels.")
        .defaultValue(EDGE_LABELS_PLACEMENT_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(EdgeLabelPlacement.class)
        .targets(EnumSet.of(LayoutOptionData.Target.LABELS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.edgeLabels.inline")
        .group("edgeLabels")
        .name("Inline Edge Labels")
        .description("If true, an edge label is placed directly on its edge. May only apply to center edge labels. This kind of label placement is only advisable if the label\'s rendering is such that it is not crossed by its edge and thus stays legible.")
        .defaultValue(EDGE_LABELS_INLINE_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.LABELS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.font.name")
        .group("font")
        .name("Font Name")
        .description("Font name used for a label.")
        .type(LayoutOptionData.Type.STRING)
        .optionClass(String.class)
        .targets(EnumSet.of(LayoutOptionData.Target.LABELS))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.font.size")
        .group("font")
        .name("Font Size")
        .description("Font size used for a label.")
        .lowerBound(FONT_SIZE_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.LABELS))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.port.anchor")
        .group("port")
        .name("Port Anchor Offset")
        .description("The offset to the port position where connections shall be attached.")
        .type(LayoutOptionData.Type.OBJECT)
        .optionClass(KVector.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PORTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.port.index")
        .group("port")
        .name("Port Index")
        .description("The index of a port in the fixed order around a node. The order is assumed as clockwise, starting with the leftmost port on the top side. This option must be set if \'Port Constraints\' is set to FIXED_ORDER and no specific positions are given for the ports. Additionally, the option \'Port Side\' must be defined in this case.")
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PORTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.port.side")
        .group("port")
        .name("Port Side")
        .description("The side of a node on which a port is situated. This option must be set if \'Port Constraints\' is set to FIXED_SIDE or FIXED_ORDER and no specific positions are given for the ports.")
        .defaultValue(PORT_SIDE_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(PortSide.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PORTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.port.borderOffset")
        .group("port")
        .name("Port Border Offset")
        .description("The offset of ports on the node border. With a positive offset the port is moved outside of the node, while with a negative offset the port is moved towards the inside. An offset of 0 means that the port is placed directly on the node border, i.e. if the port side is north, the port\'s south border touches the nodes\'s north border; if the port side is east, the port\'s west border touches the nodes\'s east border; if the port side is south, the port\'s north border touches the node\'s south border; if the port side is west, the port\'s east border touches the node\'s west border.")
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PORTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.portLabels.placement")
        .group("portLabels")
        .name("Port Label Placement")
        .description("Decides on a placement method for port labels; if empty, the node label\'s position is not modified.")
        .defaultValue(PORT_LABELS_PLACEMENT_DEFAULT)
        .type(LayoutOptionData.Type.ENUMSET)
        .optionClass(PortLabelPlacement.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.portLabels.nextToPortIfPossible")
        .group("portLabels")
        .name("Port Labels Next to Port")
        .description("Use \'portLabels.placement\': NEXT_TO_PORT_OF_POSSIBLE.")
        .defaultValue(PORT_LABELS_NEXT_TO_PORT_IF_POSSIBLE_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.portLabels.treatAsGroup")
        .group("portLabels")
        .name("Treat Port Labels as Group")
        .description("If this option is true (default), the labels of a port will be treated as a group when it comes to centering them next to their port. If this option is false, only the first label will be centered next to the port, with the others being placed below. This only applies to labels of eastern and western ports and will have no effect if labels are not placed next to their port.")
        .defaultValue(PORT_LABELS_TREAT_AS_GROUP_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.topdown.scaleFactor")
        .group("topdown")
        .name("Topdown Scale Factor")
        .description("The scaling factor to be applied to the nodes laid out within the node in recursive topdown layout. The difference to \'Scale Factor\' is that the node itself is not scaled. This value has to be set on hierarchical nodes.")
        .defaultValue(TOPDOWN_SCALE_FACTOR_DEFAULT)
        .lowerBound(TOPDOWN_SCALE_FACTOR_LOWER_BOUND)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.topdown.scaleFactor",
        "org.eclipse.elk.topdown.nodeType",
        TOPDOWN_SCALE_FACTOR_DEP_TOPDOWN_NODE_TYPE_0
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.topdown.sizeApproximator")
        .group("topdown")
        .name("Topdown Size Approximator")
        .description("The size approximator to be used to set sizes of hierarchical nodes during topdown layout. The default value is null, which results in nodes keeping whatever size is defined for them e.g. through parent parallel node or by manually setting the size.")
        .defaultValue(TOPDOWN_SIZE_APPROXIMATOR_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(TopdownSizeApproximator.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.topdown.sizeApproximator",
        "org.eclipse.elk.topdown.nodeType",
        TOPDOWN_SIZE_APPROXIMATOR_DEP_TOPDOWN_NODE_TYPE_0
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.topdown.hierarchicalNodeWidth")
        .group("topdown")
        .name("Topdown Hierarchical Node Width")
        .description("The fixed size of a hierarchical node when using topdown layout. If this value is set on a parallel node it applies to its children, when set on a hierarchical node it applies to the node itself.")
        .defaultValue(TOPDOWN_HIERARCHICAL_NODE_WIDTH_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS, LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.topdown.hierarchicalNodeWidth",
        "org.eclipse.elk.topdown.nodeType",
        null
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.topdown.hierarchicalNodeAspectRatio")
        .group("topdown")
        .name("Topdown Hierarchical Node Aspect Ratio")
        .description("The fixed aspect ratio of a hierarchical node when using topdown layout. Default is 1/sqrt(2). If this value is set on a parallel node it applies to its children, when set on a hierarchical node it applies to the node itself.")
        .defaultValue(TOPDOWN_HIERARCHICAL_NODE_ASPECT_RATIO_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS, LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.topdown.hierarchicalNodeAspectRatio",
        "org.eclipse.elk.topdown.nodeType",
        null
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.topdown.nodeType")
        .group("topdown")
        .name("Topdown Node Type")
        .description("The different node types used for topdown layout. If the node type is set to {@link TopdownNodeTypes.PARALLEL_NODE} the algorithm must be set to a {@link TopdownLayoutProvider} such as {@link TopdownPacking}. The {@link nodeSize.fixedGraphSize} option is technically only required for hierarchical nodes.")
        .defaultValue(TOPDOWN_NODE_TYPE_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(TopdownNodeTypes.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.topdown.nodeType",
        "org.eclipse.elk.nodeSize.fixedGraphSize",
        null
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.topdown.scaleCap")
        .group("topdown")
        .name("Topdown Scale Cap")
        .description("Determines the upper limit for the topdown scale factor. The default value is 1.0 which ensures that nested children never end up appearing larger than their parents in terms of unit sizes such as the font size. If the limit is larger, nodes will fully utilize the available space, but it is counteriniuitive for inner nodes to have a larger scale than outer nodes.")
        .defaultValue(TOPDOWN_SCALE_CAP_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.topdown.scaleCap",
        "org.eclipse.elk.topdown.nodeType",
        TOPDOWN_SCALE_CAP_DEP_TOPDOWN_NODE_TYPE_0
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.insideSelfLoops.activate")
        .group("insideSelfLoops")
        .name("Activate Inside Self Loops")
        .description("Whether this node allows to route self loops inside of it instead of around it. If set to true, this will make the node a compound node if it isn\'t already, and will require the layout algorithm to support compound nodes with hierarchical ports.")
        .defaultValue(INSIDE_SELF_LOOPS_ACTIVATE_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.insideSelfLoops.yo")
        .group("insideSelfLoops")
        .name("Inside Self Loop")
        .description("Whether a self loop should be routed inside a node instead of around that node.")
        .defaultValue(INSIDE_SELF_LOOPS_YO_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.EDGES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.edge.thickness")
        .group("edge")
        .name("Edge Thickness")
        .description("The thickness of an edge. This is a hint on the line width used to draw an edge, possibly requiring more space to be reserved for it.")
        .defaultValue(EDGE_THICKNESS_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.EDGES))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.edge.type")
        .group("edge")
        .name("Edge Type")
        .description("The type of an edge. This is usually used for UML class diagrams, where associations must be handled differently from generalizations.")
        .defaultValue(EDGE_TYPE_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(EdgeType.class)
        .targets(EnumSet.of(LayoutOptionData.Target.EDGES))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    registry.register(new LayoutCategoryData.Builder()
        .id("org.eclipse.elk.layered")
        .name("Layered")
        .description("The layer-based method was introduced by Sugiyama, Tagawa and Toda in 1981. It emphasizes the direction of edges by pointing as many edges as possible into the same direction. The nodes are arranged in layers, which are sometimes called \"hierarchies\", and then reordered such that the number of edge crossings is minimized. Afterwards, concrete coordinates are computed for the nodes and edge bend points.")
        .create()
    );
    registry.register(new LayoutCategoryData.Builder()
        .id("org.eclipse.elk.orthogonal")
        .name("Orthogonal")
        .description("Orthogonal methods that follow the \"topology-shape-metrics\" approach by Batini, Nardelli and Tamassia \'86. The first phase determines the topology of the drawing by applying a planarization technique, which results in a planar representation of the graph. The orthogonal shape is computed in the second phase, which aims at minimizing the number of edge bends, and is called orthogonalization. The third phase leads to concrete coordinates for nodes and edge bend points by applying a compaction method, thus defining the metrics.")
        .create()
    );
    registry.register(new LayoutCategoryData.Builder()
        .id("org.eclipse.elk.force")
        .name("Force")
        .description("Layout algorithms that follow physical analogies by simulating a system of attractive and repulsive forces. The first successful method of this kind was proposed by Eades in 1984.")
        .create()
    );
    registry.register(new LayoutCategoryData.Builder()
        .id("org.eclipse.elk.circle")
        .name("Circle")
        .description("Circular layout algorithms emphasize cycles or biconnected components of a graph by arranging them in circles. This is useful if a drawing is desired where such components are clearly grouped, or where cycles are shown as prominent OPTIONS of the graph.")
        .create()
    );
    registry.register(new LayoutCategoryData.Builder()
        .id("org.eclipse.elk.tree")
        .name("Tree")
        .description("Specialized layout methods for trees, i.e. acyclic graphs. The regular structure of graphs that have no undirected cycles can be emphasized using an algorithm of this type.")
        .create()
    );
    registry.register(new LayoutCategoryData.Builder()
        .id("org.eclipse.elk.planar")
        .name("Planar")
        .description("Algorithms that require a planar or upward planar graph. Most of these algorithms are theoretically interesting, but not practically usable.")
        .create()
    );
    registry.register(new LayoutCategoryData.Builder()
        .id("org.eclipse.elk.radial")
        .name("Radial")
        .description("Radial layout algorithms usually position the nodes of the graph on concentric circles.")
        .create()
    );
    new org.eclipse.elk.core.options.FixedLayouterOptions().apply(registry);
    new org.eclipse.elk.core.options.BoxLayouterOptions().apply(registry);
    new org.eclipse.elk.core.options.RandomLayouterOptions().apply(registry);
  }
}
