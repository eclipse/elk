/**
 * Copyright (c) 2015, 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.elk.alg.layered.options;

import java.util.EnumSet;
import java.util.List;
import org.eclipse.elk.alg.layered.LayeredLayoutProvider;
import org.eclipse.elk.alg.layered.components.ComponentOrderingStrategy;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.math.ElkMargin;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.Alignment;
import org.eclipse.elk.core.options.ContentAlignment;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.EdgeLabelPlacement;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.HierarchyHandling;
import org.eclipse.elk.core.options.NodeLabelPlacement;
import org.eclipse.elk.core.options.PortAlignment;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortLabelPlacement;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.core.options.TopdownNodeTypes;
import org.eclipse.elk.core.util.IndividualSpacings;
import org.eclipse.elk.graph.properties.GraphFeature;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class LayeredOptions implements ILayoutMetaDataProvider {
  /**
   * The id of the ELK Layered algorithm.
   */
  public static final String ALGORITHM_ID = "org.eclipse.elk.layered";

  /**
   * Spacing to be preserved between a comment box and other comment boxes connected to the same node.
   * The space left between comment boxes of different nodes is controlled by the node-node spacing.
   */
  public static final IProperty<Double> SPACING_COMMENT_COMMENT = CoreOptions.SPACING_COMMENT_COMMENT;

  /**
   * Spacing to be preserved between a node and its connected comment boxes. The space left between a node
   * and the comments of another node is controlled by the node-node spacing.
   */
  public static final IProperty<Double> SPACING_COMMENT_NODE = CoreOptions.SPACING_COMMENT_NODE;

  /**
   * Spacing to be preserved between pairs of connected components.
   * This option is only relevant if 'separateConnectedComponents' is activated.
   */
  public static final IProperty<Double> SPACING_COMPONENT_COMPONENT = CoreOptions.SPACING_COMPONENT_COMPONENT;

  /**
   * Spacing to be preserved between any two edges. Note that while this can somewhat easily be satisfied
   * for the segments of orthogonally drawn edges, it is harder for general polylines or splines.
   */
  public static final IProperty<Double> SPACING_EDGE_EDGE = CoreOptions.SPACING_EDGE_EDGE;

  /**
   * The minimal distance to be preserved between a label and the edge it is associated with.
   * Note that the placement of a label is influenced by the 'edgelabels.placement' option.
   */
  public static final IProperty<Double> SPACING_EDGE_LABEL = CoreOptions.SPACING_EDGE_LABEL;

  /**
   * Spacing to be preserved between nodes and edges.
   */
  public static final IProperty<Double> SPACING_EDGE_NODE = CoreOptions.SPACING_EDGE_NODE;

  /**
   * Determines the amount of space to be left between two labels
   * of the same graph element.
   */
  public static final IProperty<Double> SPACING_LABEL_LABEL = CoreOptions.SPACING_LABEL_LABEL;

  /**
   * Horizontal spacing to be preserved between labels and the ports they are associated with.
   * Note that the placement of a label is influenced by the 'portlabels.placement' option.
   */
  public static final IProperty<Double> SPACING_LABEL_PORT_HORIZONTAL = CoreOptions.SPACING_LABEL_PORT_HORIZONTAL;

  /**
   * Vertical spacing to be preserved between labels and the ports they are associated with.
   * Note that the placement of a label is influenced by the 'portlabels.placement' option.
   */
  public static final IProperty<Double> SPACING_LABEL_PORT_VERTICAL = CoreOptions.SPACING_LABEL_PORT_VERTICAL;

  /**
   * Spacing to be preserved between labels and the border of node they are associated with.
   * Note that the placement of a label is influenced by the 'nodelabels.placement' option.
   */
  public static final IProperty<Double> SPACING_LABEL_NODE = CoreOptions.SPACING_LABEL_NODE;

  /**
   * The minimal distance to be preserved between each two nodes.
   */
  public static final IProperty<Double> SPACING_NODE_NODE = CoreOptions.SPACING_NODE_NODE;

  /**
   * Spacing to be preserved between a node and its self loops.
   */
  public static final IProperty<Double> SPACING_NODE_SELF_LOOP = CoreOptions.SPACING_NODE_SELF_LOOP;

  /**
   * Spacing between pairs of ports of the same node.
   */
  public static final IProperty<Double> SPACING_PORT_PORT = CoreOptions.SPACING_PORT_PORT;

  /**
   * Allows to specify individual spacing values for graph elements that shall be different from
   * the value specified for the element's parent.
   * <h3>Algorithm Specific Details</h3>
   * Most parts of the algorithm do not support this yet.
   */
  public static final IProperty<IndividualSpacings> SPACING_INDIVIDUAL = CoreOptions.SPACING_INDIVIDUAL;

  /**
   * An optional base value for all other layout options of the 'spacing' group. It can be used to conveniently
   * alter the overall 'spaciousness' of the drawing. Whenever an explicit value is set for the other layout
   * options, this base value will have no effect. The base value is not inherited, i.e. it must be set for
   * each hierarchical node.
   */
  public static final IProperty<Double> SPACING_BASE_VALUE = LayeredMetaDataProvider.SPACING_BASE_VALUE;

  /**
   * Spacing to be preserved between pairs of edges that are routed between the same pair of layers.
   * Note that 'spacing.edgeEdge' is used for the spacing between pairs of edges crossing the same layer.
   */
  public static final IProperty<Double> SPACING_EDGE_EDGE_BETWEEN_LAYERS = LayeredMetaDataProvider.SPACING_EDGE_EDGE_BETWEEN_LAYERS;

  /**
   * The spacing to be preserved between nodes and edges that are routed next to the node's layer.
   * For the spacing between nodes and edges that cross the node's layer 'spacing.edgeNode' is used.
   */
  public static final IProperty<Double> SPACING_EDGE_NODE_BETWEEN_LAYERS = LayeredMetaDataProvider.SPACING_EDGE_NODE_BETWEEN_LAYERS;

  /**
   * The spacing to be preserved between any pair of nodes of two adjacent layers.
   * Note that 'spacing.nodeNode' is used for the spacing between nodes within the layer itself.
   */
  public static final IProperty<Double> SPACING_NODE_NODE_BETWEEN_LAYERS = LayeredMetaDataProvider.SPACING_NODE_NODE_BETWEEN_LAYERS;

  /**
   * Default value for {@link #PRIORITY} with algorithm "ELK Layered".
   */
  private static final int PRIORITY_DEFAULT = 0;

  /**
   * Defines the priority of an object; its meaning depends on the specific layout algorithm
   * and the context where it is used.
   * <h3>Algorithm Specific Details</h3>
   * Used by the 'simple row graph placer' to decide which connected components to place first.
   * A component's priority is the sum of the node priorities,
   * and components with higher priorities will be placed
   * before components with lower priorities.
   */
  public static final IProperty<Integer> PRIORITY = new Property<Integer>(
                                CoreOptions.PRIORITY,
                                PRIORITY_DEFAULT);

  /**
   * Defines how important it is to have a certain edge point into the direction of the overall layout.
   * This option is evaluated during the cycle breaking phase.
   */
  public static final IProperty<Integer> PRIORITY_DIRECTION = LayeredMetaDataProvider.PRIORITY_DIRECTION;

  /**
   * Defines how important it is to keep an edge as short as possible.
   * This option is evaluated during the layering phase.
   * <h3>Algorithm Specific Details</h3>
   * Currently only supported by the network simplex layerer.
   */
  public static final IProperty<Integer> PRIORITY_SHORTNESS = LayeredMetaDataProvider.PRIORITY_SHORTNESS;

  /**
   * Defines how important it is to keep an edge straight, i.e. aligned with one of the two axes.
   * This option is evaluated during node placement.
   */
  public static final IProperty<Integer> PRIORITY_STRAIGHTNESS = LayeredMetaDataProvider.PRIORITY_STRAIGHTNESS;

  /**
   * For certain graphs and certain prescribed drawing areas it may be desirable to
   * split the laid out graph into chunks that are placed side by side.
   * The edges that connect different chunks are 'wrapped' around from the end of
   * one chunk to the start of the other chunk.
   * The points between the chunks are referred to as 'cuts'.
   */
  public static final IProperty<WrappingStrategy> WRAPPING_STRATEGY = LayeredMetaDataProvider.WRAPPING_STRATEGY;

  /**
   * To visually separate edges that are wrapped from regularly routed edges an additional spacing value
   * can be specified in form of this layout option. The spacing is added to the regular edgeNode spacing.
   */
  public static final IProperty<Double> WRAPPING_ADDITIONAL_EDGE_SPACING = LayeredMetaDataProvider.WRAPPING_ADDITIONAL_EDGE_SPACING;

  /**
   * At times and for certain types of graphs the executed wrapping may produce results that
   * are consistently biased in the same fashion: either wrapping to often or to rarely.
   * This factor can be used to correct the bias. Internally, it is simply multiplied with
   * the 'aspect ratio' layout option.
   */
  public static final IProperty<Double> WRAPPING_CORRECTION_FACTOR = LayeredMetaDataProvider.WRAPPING_CORRECTION_FACTOR;

  /**
   * The strategy by which the layer indexes are determined at which the layering crumbles into chunks.
   */
  public static final IProperty<CuttingStrategy> WRAPPING_CUTTING_STRATEGY = LayeredMetaDataProvider.WRAPPING_CUTTING_STRATEGY;

  /**
   * Allows the user to specify her own cuts for a certain graph.
   */
  public static final IProperty<List<Integer>> WRAPPING_CUTTING_CUTS = LayeredMetaDataProvider.WRAPPING_CUTTING_CUTS;

  /**
   * The MSD cutting strategy starts with an initial guess on the number of chunks the graph
   * should be split into. The freedom specifies how much the strategy may deviate from this guess.
   * E.g. if an initial number of 3 is computed, a freedom of 1 allows 2, 3, and 4 cuts.
   */
  public static final IProperty<Integer> WRAPPING_CUTTING_MSD_FREEDOM = LayeredMetaDataProvider.WRAPPING_CUTTING_MSD_FREEDOM;

  /**
   * When wrapping graphs, one can specify indices that are not allowed as split points.
   * The validification strategy makes sure every computed split point is allowed.
   */
  public static final IProperty<ValidifyStrategy> WRAPPING_VALIDIFY_STRATEGY = LayeredMetaDataProvider.WRAPPING_VALIDIFY_STRATEGY;

  /**
   * null
   */
  public static final IProperty<List<Integer>> WRAPPING_VALIDIFY_FORBIDDEN_INDICES = LayeredMetaDataProvider.WRAPPING_VALIDIFY_FORBIDDEN_INDICES;

  /**
   * For general graphs it is important that not too many edges wrap backwards.
   * Thus a compromise between evenly-distributed cuts and the total number of cut edges is sought.
   */
  public static final IProperty<Boolean> WRAPPING_MULTI_EDGE_IMPROVE_CUTS = LayeredMetaDataProvider.WRAPPING_MULTI_EDGE_IMPROVE_CUTS;

  /**
   * null
   */
  public static final IProperty<Double> WRAPPING_MULTI_EDGE_DISTANCE_PENALTY = LayeredMetaDataProvider.WRAPPING_MULTI_EDGE_DISTANCE_PENALTY;

  /**
   * The initial wrapping is performed in a very simple way. As a consequence, edges that wrap from
   * one chunk to another may be unnecessarily long. Activating this option tries to shorten such edges.
   */
  public static final IProperty<Boolean> WRAPPING_MULTI_EDGE_IMPROVE_WRAPPED_EDGES = LayeredMetaDataProvider.WRAPPING_MULTI_EDGE_IMPROVE_WRAPPED_EDGES;

  /**
   * Aims at shorter and straighter edges. Two configurations are possible:
   * (a) allow ports to move freely on the side they are assigned to (the order is always defined beforehand),
   * (b) additionally allow to enlarge a node wherever it helps.
   * If this option is not configured for a node, the 'nodeFlexibility.default' value is used,
   * which is specified for the node's parent.
   */
  public static final IProperty<NodeFlexibility> NODE_PLACEMENT_NETWORK_SIMPLEX_NODE_FLEXIBILITY = LayeredMetaDataProvider.NODE_PLACEMENT_NETWORK_SIMPLEX_NODE_FLEXIBILITY;

  /**
   * Default value of the 'nodeFlexibility' option for the children of a hierarchical node.
   */
  public static final IProperty<NodeFlexibility> NODE_PLACEMENT_NETWORK_SIMPLEX_NODE_FLEXIBILITY_DEFAULT = LayeredMetaDataProvider.NODE_PLACEMENT_NETWORK_SIMPLEX_NODE_FLEXIBILITY_DEFAULT;

  /**
   * Specifies the way control points are assembled for each individual edge.
   * CONSERVATIVE ensures that edges are properly routed around the nodes
   * but feels rather orthogonal at times.
   * SLOPPY uses fewer control points to obtain curvier edge routes but may result in
   * edges overlapping nodes.
   */
  public static final IProperty<SplineRoutingMode> EDGE_ROUTING_SPLINES_MODE = LayeredMetaDataProvider.EDGE_ROUTING_SPLINES_MODE;

  /**
   * Spacing factor for routing area between layers when using sloppy spline routing.
   */
  public static final IProperty<Double> EDGE_ROUTING_SPLINES_SLOPPY_LAYER_SPACING_FACTOR = LayeredMetaDataProvider.EDGE_ROUTING_SPLINES_SLOPPY_LAYER_SPACING_FACTOR;

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
  public static final IProperty<Boolean> TOPDOWN_LAYOUT = CoreOptions.TOPDOWN_LAYOUT;

  /**
   * The scaling factor to be applied to the nodes laid out within the node in recursive topdown
   * layout. The difference to 'Scale Factor' is that the node itself is not scaled. This value has to be set on
   * hierarchical nodes.
   */
  public static final IProperty<Double> TOPDOWN_SCALE_FACTOR = CoreOptions.TOPDOWN_SCALE_FACTOR;

  /**
   * The fixed size of a hierarchical node when using topdown layout. If this value is set on a parallel
   * node it applies to its children, when set on a hierarchical node it applies to the node itself.
   */
  public static final IProperty<Double> TOPDOWN_HIERARCHICAL_NODE_WIDTH = CoreOptions.TOPDOWN_HIERARCHICAL_NODE_WIDTH;

  /**
   * The fixed aspect ratio of a hierarchical node when using topdown layout. Default is 1/sqrt(2). If this
   * value is set on a parallel node it applies to its children, when set on a hierarchical node it applies to
   * the node itself.
   */
  public static final IProperty<Double> TOPDOWN_HIERARCHICAL_NODE_ASPECT_RATIO = CoreOptions.TOPDOWN_HIERARCHICAL_NODE_ASPECT_RATIO;

  /**
   * Default value for {@link #TOPDOWN_NODE_TYPE} with algorithm "ELK Layered".
   */
  private static final TopdownNodeTypes TOPDOWN_NODE_TYPE_DEFAULT = TopdownNodeTypes.HIERARCHICAL_NODE;

  /**
   * The different node types used for topdown layout. If the node type is set
   * to {@link TopdownNodeTypes.PARALLEL_NODE} the algorithm must be set to a {@link TopdownLayoutProvider} such
   * as {@link TopdownPacking}. The {@link nodeSize.fixedGraphSize} option is technically only required for
   * hierarchical nodes.
   */
  public static final IProperty<TopdownNodeTypes> TOPDOWN_NODE_TYPE = new Property<TopdownNodeTypes>(
                                CoreOptions.TOPDOWN_NODE_TYPE,
                                TOPDOWN_NODE_TYPE_DEFAULT);

  /**
   * Default value for {@link #PADDING} with algorithm "ELK Layered".
   */
  private static final ElkPadding PADDING_DEFAULT = new ElkPadding(12);

  /**
   * The padding to be left to a parent element's border when placing child elements. This can
   * also serve as an output option of a layout algorithm if node size calculation is setup
   * appropriately.
   */
  public static final IProperty<ElkPadding> PADDING = new Property<ElkPadding>(
                                CoreOptions.PADDING,
                                PADDING_DEFAULT);

  /**
   * Default value for {@link #EDGE_ROUTING} with algorithm "ELK Layered".
   */
  private static final EdgeRouting EDGE_ROUTING_DEFAULT = EdgeRouting.ORTHOGONAL;

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
   * Default value for {@link #PORT_BORDER_OFFSET} with algorithm "ELK Layered".
   */
  private static final double PORT_BORDER_OFFSET_DEFAULT = 0;

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
                                CoreOptions.PORT_BORDER_OFFSET,
                                PORT_BORDER_OFFSET_DEFAULT);

  /**
   * Default value for {@link #RANDOM_SEED} with algorithm "ELK Layered".
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
   * Default value for {@link #ASPECT_RATIO} with algorithm "ELK Layered".
   */
  private static final double ASPECT_RATIO_DEFAULT = 1.6f;

  /**
   * The desired aspect ratio of the drawing, that is the quotient of width by height.
   */
  public static final IProperty<Double> ASPECT_RATIO = new Property<Double>(
                                CoreOptions.ASPECT_RATIO,
                                ASPECT_RATIO_DEFAULT);

  /**
   * No layout is done for the associated element. This is used to mark parts of a diagram to
   * avoid their inclusion in the layout graph, or to mark parts of the layout graph to
   * prevent layout engines from processing them. If you wish to exclude the contents of a
   * compound node from automatic layout, while the node itself is still considered on its own
   * layer, use the 'Fixed Layout' algorithm for that node.
   */
  public static final IProperty<Boolean> NO_LAYOUT = CoreOptions.NO_LAYOUT;

  /**
   * Defines constraints of the position of the ports of a node.
   */
  public static final IProperty<PortConstraints> PORT_CONSTRAINTS = CoreOptions.PORT_CONSTRAINTS;

  /**
   * The side of a node on which a port is situated. This option must be set if 'Port
   * Constraints' is set to FIXED_SIDE or FIXED_ORDER and no specific positions are given
   * for the ports.
   */
  public static final IProperty<PortSide> PORT_SIDE = CoreOptions.PORT_SIDE;

  /**
   * Whether additional debug information shall be generated.
   */
  public static final IProperty<Boolean> DEBUG_MODE = CoreOptions.DEBUG_MODE;

  /**
   * Alignment of the selected node relative to other nodes;
   * the exact meaning depends on the used algorithm.
   */
  public static final IProperty<Alignment> ALIGNMENT = CoreOptions.ALIGNMENT;

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
  public static final IProperty<HierarchyHandling> HIERARCHY_HANDLING = CoreOptions.HIERARCHY_HANDLING;

  /**
   * Default value for {@link #SEPARATE_CONNECTED_COMPONENTS} with algorithm "ELK Layered".
   */
  private static final boolean SEPARATE_CONNECTED_COMPONENTS_DEFAULT = true;

  /**
   * Whether each connected component should be processed separately.
   */
  public static final IProperty<Boolean> SEPARATE_CONNECTED_COMPONENTS = new Property<Boolean>(
                                CoreOptions.SEPARATE_CONNECTED_COMPONENTS,
                                SEPARATE_CONNECTED_COMPONENTS_DEFAULT);

  /**
   * Whether this node allows to route self loops inside of it instead of around it. If set to true,
   * this will make the node a compound node if it isn't already, and will require the layout algorithm
   * to support compound nodes with hierarchical ports.
   */
  public static final IProperty<Boolean> INSIDE_SELF_LOOPS_ACTIVATE = CoreOptions.INSIDE_SELF_LOOPS_ACTIVATE;

  /**
   * Whether a self loop should be routed inside a node instead of around that node.
   */
  public static final IProperty<Boolean> INSIDE_SELF_LOOPS_YO = CoreOptions.INSIDE_SELF_LOOPS_YO;

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
   * By default, the fixed layout provider will enlarge a graph until it is large enough to contain
   * its children. If this option is set, it won't do so.
   */
  public static final IProperty<Boolean> NODE_SIZE_FIXED_GRAPH_SIZE = CoreOptions.NODE_SIZE_FIXED_GRAPH_SIZE;

  /**
   * Default value for {@link #DIRECTION} with algorithm "ELK Layered".
   */
  private static final Direction DIRECTION_DEFAULT = Direction.UNDEFINED;

  /**
   * Overall direction of edges: horizontal (right / left) or
   * vertical (down / up).
   */
  public static final IProperty<Direction> DIRECTION = new Property<Direction>(
                                CoreOptions.DIRECTION,
                                DIRECTION_DEFAULT);

  /**
   * Hints for where node labels are to be placed; if empty, the node label's position is not
   * modified.
   */
  public static final IProperty<EnumSet<NodeLabelPlacement>> NODE_LABELS_PLACEMENT = CoreOptions.NODE_LABELS_PLACEMENT;

  /**
   * Define padding for node labels that are placed inside of a node.
   */
  public static final IProperty<ElkPadding> NODE_LABELS_PADDING = CoreOptions.NODE_LABELS_PADDING;

  /**
   * Decides on a placement method for port labels; if empty, the node label's position is not
   * modified.
   */
  public static final IProperty<EnumSet<PortLabelPlacement>> PORT_LABELS_PLACEMENT = CoreOptions.PORT_LABELS_PLACEMENT;

  /**
   * Use 'portLabels.placement': NEXT_TO_PORT_OF_POSSIBLE.
   */
  @Deprecated
  public static final IProperty<Boolean> PORT_LABELS_NEXT_TO_PORT_IF_POSSIBLE = CoreOptions.PORT_LABELS_NEXT_TO_PORT_IF_POSSIBLE;

  /**
   * If this option is true (default), the labels of a port will be treated as a group when
   * it comes to centering them next to their port. If this option is false, only the first label will
   * be centered next to the port, with the others being placed below. This only applies to labels of
   * eastern and western ports and will have no effect if labels are not placed next to their port.
   */
  public static final IProperty<Boolean> PORT_LABELS_TREAT_AS_GROUP = CoreOptions.PORT_LABELS_TREAT_AS_GROUP;

  /**
   * Default value for {@link #PORT_ALIGNMENT_DEFAULT} with algorithm "ELK Layered".
   */
  private static final PortAlignment PORT_ALIGNMENT_DEFAULT_DEFAULT = PortAlignment.JUSTIFIED;

  /**
   * Defines the default port distribution for a node. May be overridden for each side individually.
   */
  public static final IProperty<PortAlignment> PORT_ALIGNMENT_DEFAULT = new Property<PortAlignment>(
                                CoreOptions.PORT_ALIGNMENT_DEFAULT,
                                PORT_ALIGNMENT_DEFAULT_DEFAULT);

  /**
   * Defines how ports on the northern side are placed, overriding the node's general port alignment.
   */
  public static final IProperty<PortAlignment> PORT_ALIGNMENT_NORTH = CoreOptions.PORT_ALIGNMENT_NORTH;

  /**
   * Defines how ports on the southern side are placed, overriding the node's general port alignment.
   */
  public static final IProperty<PortAlignment> PORT_ALIGNMENT_SOUTH = CoreOptions.PORT_ALIGNMENT_SOUTH;

  /**
   * Defines how ports on the western side are placed, overriding the node's general port alignment.
   */
  public static final IProperty<PortAlignment> PORT_ALIGNMENT_WEST = CoreOptions.PORT_ALIGNMENT_WEST;

  /**
   * Defines how ports on the eastern side are placed, overriding the node's general port alignment.
   */
  public static final IProperty<PortAlignment> PORT_ALIGNMENT_EAST = CoreOptions.PORT_ALIGNMENT_EAST;

  /**
   * Adds bend points even if an edge does not change direction. If true, each long edge dummy
   * will contribute a bend point to its edges and hierarchy-crossing edges will always get a
   * bend point where they cross hierarchy boundaries. By default, bend points are only added
   * where an edge changes direction.
   */
  public static final IProperty<Boolean> UNNECESSARY_BENDPOINTS = LayeredMetaDataProvider.UNNECESSARY_BENDPOINTS;

  /**
   * Strategy for node layering.
   */
  public static final IProperty<LayeringStrategy> LAYERING_STRATEGY = LayeredMetaDataProvider.LAYERING_STRATEGY;

  /**
   * Reduces number of dummy nodes after layering phase (if possible).
   */
  public static final IProperty<NodePromotionStrategy> LAYERING_NODE_PROMOTION_STRATEGY = LayeredMetaDataProvider.LAYERING_NODE_PROMOTION_STRATEGY;

  /**
   * How much effort should be spent to produce a nice layout.
   */
  public static final IProperty<Integer> THOROUGHNESS = LayeredMetaDataProvider.THOROUGHNESS;

  /**
   * Determines a constraint on the placement of the node regarding the layering.
   */
  public static final IProperty<LayerConstraint> LAYERING_LAYER_CONSTRAINT = LayeredMetaDataProvider.LAYERING_LAYER_CONSTRAINT;

  /**
   * Strategy for cycle breaking. Cycle breaking looks for cycles in the graph and determines
   * which edges to reverse to break the cycles. Reversed edges will end up pointing to the
   * opposite direction of regular edges (that is, reversed edges will point left if edges
   * usually point right).
   */
  public static final IProperty<CycleBreakingStrategy> CYCLE_BREAKING_STRATEGY = LayeredMetaDataProvider.CYCLE_BREAKING_STRATEGY;

  /**
   * Strategy for crossing minimization.
   */
  public static final IProperty<CrossingMinimizationStrategy> CROSSING_MINIMIZATION_STRATEGY = LayeredMetaDataProvider.CROSSING_MINIMIZATION_STRATEGY;

  /**
   * The node order given by the model does not change to produce a better layout. E.g. if node A
   * is before node B in the model this is not changed during crossing minimization. This assumes that the
   * node model order is already respected before crossing minimization. This can be achieved by setting
   * considerModelOrder.strategy to NODES_AND_EDGES.
   */
  public static final IProperty<Boolean> CROSSING_MINIMIZATION_FORCE_NODE_MODEL_ORDER = LayeredMetaDataProvider.CROSSING_MINIMIZATION_FORCE_NODE_MODEL_ORDER;

  /**
   * By default it is decided automatically if the greedy switch is activated or not.
   * The decision is based on whether the size of the input graph (without dummy nodes)
   * is smaller than the value of this option. A '0' enforces the activation.
   */
  public static final IProperty<Integer> CROSSING_MINIMIZATION_GREEDY_SWITCH_ACTIVATION_THRESHOLD = LayeredMetaDataProvider.CROSSING_MINIMIZATION_GREEDY_SWITCH_ACTIVATION_THRESHOLD;

  /**
   * Greedy Switch strategy for crossing minimization. The greedy switch heuristic is executed
   * after the regular crossing minimization as a post-processor.
   * Note that if 'hierarchyHandling' is set to 'INCLUDE_CHILDREN', the 'greedySwitchHierarchical.type'
   * option must be used.
   */
  public static final IProperty<GreedySwitchType> CROSSING_MINIMIZATION_GREEDY_SWITCH_TYPE = LayeredMetaDataProvider.CROSSING_MINIMIZATION_GREEDY_SWITCH_TYPE;

  /**
   * Activates the greedy switch heuristic in case hierarchical layout is used.
   * The differences to the non-hierarchical case (see 'greedySwitch.type') are:
   * 1) greedy switch is inactive by default,
   * 3) only the option value set on the node at which hierarchical layout starts is relevant, and
   * 2) if it's activated by the user, it properly addresses hierarchy-crossing edges.
   */
  public static final IProperty<GreedySwitchType> CROSSING_MINIMIZATION_GREEDY_SWITCH_HIERARCHICAL_TYPE = LayeredMetaDataProvider.CROSSING_MINIMIZATION_GREEDY_SWITCH_HIERARCHICAL_TYPE;

  /**
   * Preserves the order of nodes within a layer but still minimizes crossings between edges connecting
   * long edge dummies. Derives the desired order from positions specified by the 'org.eclipse.elk.position'
   * layout option. Requires a crossing minimization strategy that is able to process 'in-layer' constraints.
   */
  public static final IProperty<Boolean> CROSSING_MINIMIZATION_SEMI_INTERACTIVE = LayeredMetaDataProvider.CROSSING_MINIMIZATION_SEMI_INTERACTIVE;

  /**
   * Edges that have no ports are merged so they touch the connected nodes at the same points.
   * When this option is disabled, one port is created for each edge directly connected to a
   * node. When it is enabled, all such incoming edges share an input port, and all outgoing
   * edges share an output port.
   */
  public static final IProperty<Boolean> MERGE_EDGES = LayeredMetaDataProvider.MERGE_EDGES;

  /**
   * If hierarchical layout is active, hierarchy-crossing edges use as few hierarchical ports
   * as possible. They are broken by the algorithm, with hierarchical ports inserted as
   * required. Usually, one such port is created for each edge at each hierarchy crossing point.
   * With this option set to true, we try to create as few hierarchical ports as possible in
   * the process. In particular, all edges that form a hyperedge can share a port.
   */
  public static final IProperty<Boolean> MERGE_HIERARCHY_EDGES = LayeredMetaDataProvider.MERGE_HIERARCHY_EDGES;

  /**
   * Determines which point of a node is considered by interactive layout phases.
   */
  public static final IProperty<InteractiveReferencePoint> INTERACTIVE_REFERENCE_POINT = LayeredMetaDataProvider.INTERACTIVE_REFERENCE_POINT;

  /**
   * Strategy for node placement.
   */
  public static final IProperty<NodePlacementStrategy> NODE_PLACEMENT_STRATEGY = LayeredMetaDataProvider.NODE_PLACEMENT_STRATEGY;

  /**
   * Tells the BK node placer to use a certain alignment (out of its four) instead of the
   * one producing the smallest height, or the combination of all four.
   */
  public static final IProperty<FixedAlignment> NODE_PLACEMENT_BK_FIXED_ALIGNMENT = LayeredMetaDataProvider.NODE_PLACEMENT_BK_FIXED_ALIGNMENT;

  /**
   * Whether feedback edges should be highlighted by routing around the nodes.
   */
  public static final IProperty<Boolean> FEEDBACK_EDGES = LayeredMetaDataProvider.FEEDBACK_EDGES;

  /**
   * Dampens the movement of nodes to keep the diagram from getting too large.
   */
  public static final IProperty<Double> NODE_PLACEMENT_LINEAR_SEGMENTS_DEFLECTION_DAMPENING = LayeredMetaDataProvider.NODE_PLACEMENT_LINEAR_SEGMENTS_DEFLECTION_DAMPENING;

  /**
   * Alter the distribution of the loops around the node. It only takes effect for PortConstraints.FREE.
   */
  public static final IProperty<SelfLoopDistributionStrategy> EDGE_ROUTING_SELF_LOOP_DISTRIBUTION = LayeredMetaDataProvider.EDGE_ROUTING_SELF_LOOP_DISTRIBUTION;

  /**
   * Alter the ordering of the loops they can either be stacked or sequenced.
   * It only takes effect for PortConstraints.FREE.
   */
  public static final IProperty<SelfLoopOrderingStrategy> EDGE_ROUTING_SELF_LOOP_ORDERING = LayeredMetaDataProvider.EDGE_ROUTING_SELF_LOOP_ORDERING;

  /**
   * Specifies how the content of a node are aligned. Each node can individually control the alignment of its
   * contents. I.e. if a node should be aligned top left in its parent node, the parent node should specify that
   * option.
   */
  public static final IProperty<EnumSet<ContentAlignment>> CONTENT_ALIGNMENT = CoreOptions.CONTENT_ALIGNMENT;

  /**
   * Specifies whether the Brandes Koepf node placer tries to increase the number of straight edges
   * at the expense of diagram size.
   * There is a subtle difference to the 'favorStraightEdges' option, which decides whether
   * a balanced placement of the nodes is desired, or not. In bk terms this means combining the four
   * alignments into a single balanced one, or not. This option on the other hand tries to straighten
   * additional edges during the creation of each of the four alignments.
   */
  public static final IProperty<EdgeStraighteningStrategy> NODE_PLACEMENT_BK_EDGE_STRAIGHTENING = LayeredMetaDataProvider.NODE_PLACEMENT_BK_EDGE_STRAIGHTENING;

  /**
   * Specifies whether and how post-process compaction is applied.
   */
  public static final IProperty<GraphCompactionStrategy> COMPACTION_POST_COMPACTION_STRATEGY = LayeredMetaDataProvider.COMPACTION_POST_COMPACTION_STRATEGY;

  /**
   * Specifies whether and how post-process compaction is applied.
   */
  public static final IProperty<ConstraintCalculationStrategy> COMPACTION_POST_COMPACTION_CONSTRAINTS = LayeredMetaDataProvider.COMPACTION_POST_COMPACTION_CONSTRAINTS;

  /**
   * Tries to further compact components (disconnected sub-graphs).
   */
  public static final IProperty<Boolean> COMPACTION_CONNECTED_COMPONENTS = LayeredMetaDataProvider.COMPACTION_CONNECTED_COMPONENTS;

  /**
   * Makes room around high degree nodes to place leafs and trees.
   */
  public static final IProperty<Boolean> HIGH_DEGREE_NODES_TREATMENT = LayeredMetaDataProvider.HIGH_DEGREE_NODES_TREATMENT;

  /**
   * Whether a node is considered to have a high degree.
   */
  public static final IProperty<Integer> HIGH_DEGREE_NODES_THRESHOLD = LayeredMetaDataProvider.HIGH_DEGREE_NODES_THRESHOLD;

  /**
   * Maximum height of a subtree connected to a high degree node to be moved to separate layers.
   */
  public static final IProperty<Integer> HIGH_DEGREE_NODES_TREE_HEIGHT = LayeredMetaDataProvider.HIGH_DEGREE_NODES_TREE_HEIGHT;

  /**
   * The minimal size to which a node can be reduced.
   */
  public static final IProperty<KVector> NODE_SIZE_MINIMUM = CoreOptions.NODE_SIZE_MINIMUM;

  /**
   * This option is not used as option, but as output of the layout algorithms. It is
   * attached to edges and determines the points where junction symbols should be drawn in
   * order to represent hyperedges with orthogonal routing. Whether such points are computed
   * depends on the chosen layout algorithm and edge routing style. The points are put into
   * the vector chain with no specific order.
   */
  public static final IProperty<KVectorChain> JUNCTION_POINTS = CoreOptions.JUNCTION_POINTS;

  /**
   * The thickness of an edge. This is a hint on the line width used to draw an edge, possibly
   * requiring more space to be reserved for it.
   */
  public static final IProperty<Double> EDGE_THICKNESS = CoreOptions.EDGE_THICKNESS;

  /**
   * Gives a hint on where to put edge labels.
   */
  public static final IProperty<EdgeLabelPlacement> EDGE_LABELS_PLACEMENT = CoreOptions.EDGE_LABELS_PLACEMENT;

  /**
   * If true, an edge label is placed directly on its edge. May only apply to center edge labels.
   * This kind of label placement is only advisable if the label's rendering is such that it is not
   * crossed by its edge and thus stays legible.
   */
  public static final IProperty<Boolean> EDGE_LABELS_INLINE = CoreOptions.EDGE_LABELS_INLINE;

  /**
   * How likely it is to use cross-hierarchy (1) vs bottom-up (-1).
   */
  public static final IProperty<Double> CROSSING_MINIMIZATION_HIERARCHICAL_SWEEPINESS = LayeredMetaDataProvider.CROSSING_MINIMIZATION_HIERARCHICAL_SWEEPINESS;

  /**
   * The index of a port in the fixed order around a node. The order is assumed as clockwise,
   * starting with the leftmost port on the top side. This option must be set if 'Port
   * Constraints' is set to FIXED_ORDER and no specific positions are given for the ports.
   * Additionally, the option 'Port Side' must be defined in this case.
   */
  public static final IProperty<Integer> PORT_INDEX = CoreOptions.PORT_INDEX;

  /**
   * Whether the node should be regarded as a comment box instead of a regular node. In that
   * case its placement should be similar to how labels are handled. Any edges incident to a
   * comment box specify to which graph elements the comment is related.
   */
  public static final IProperty<Boolean> COMMENT_BOX = CoreOptions.COMMENT_BOX;

  /**
   * Whether the node should be handled as a hypernode.
   */
  public static final IProperty<Boolean> HYPERNODE = CoreOptions.HYPERNODE;

  /**
   * The offset to the port position where connections shall be attached.
   */
  public static final IProperty<KVector> PORT_ANCHOR = CoreOptions.PORT_ANCHOR;

  /**
   * Whether to activate partitioned layout. This will allow to group nodes through the Layout Partition option.
   * a pair of nodes with different partition indices is then placed such that the node with lower index is
   * placed to the left of the other node (with left-to-right layout direction). Depending on the layout
   * algorithm, this may only be guaranteed to work if all nodes have a layout partition configured, or at least
   * if edges that cross partitions are not part of a partition-crossing cycle.
   */
  public static final IProperty<Boolean> PARTITIONING_ACTIVATE = CoreOptions.PARTITIONING_ACTIVATE;

  /**
   * Partition to which the node belongs. This requires Layout Partitioning to be active. Nodes with lower
   * partition IDs will appear to the left of nodes with higher partition IDs (assuming a left-to-right layout
   * direction).
   */
  public static final IProperty<Integer> PARTITIONING_PARTITION = CoreOptions.PARTITIONING_PARTITION;

  /**
   * Defines a loose upper bound on the width of the MinWidth layerer.
   * If set to '-1' multiple values are tested and the best result is selected.
   */
  public static final IProperty<Integer> LAYERING_MIN_WIDTH_UPPER_BOUND_ON_WIDTH = LayeredMetaDataProvider.LAYERING_MIN_WIDTH_UPPER_BOUND_ON_WIDTH;

  /**
   * Multiplied with Upper Bound On Width for defining an upper bound on the width of layers which
   * haven't been determined yet, but whose maximum width had been (roughly) estimated by the MinWidth
   * algorithm. Compensates for too high estimations.
   * If set to '-1' multiple values are tested and the best result is selected.
   */
  public static final IProperty<Integer> LAYERING_MIN_WIDTH_UPPER_LAYER_ESTIMATION_SCALING_FACTOR = LayeredMetaDataProvider.LAYERING_MIN_WIDTH_UPPER_LAYER_ESTIMATION_SCALING_FACTOR;

  /**
   * The position of a node, port, or label. This is used by the 'Fixed Layout' algorithm to
   * specify a pre-defined position.
   */
  public static final IProperty<KVector> POSITION = CoreOptions.POSITION;

  /**
   * Specifies whether non-flow ports may switch sides if their node's port constraints are either FIXED_SIDE
   * or FIXED_ORDER. A non-flow port is a port on a side that is not part of the currently configured layout flow.
   * For instance, given a left-to-right layout direction, north and south ports would be considered non-flow ports.
   * Further note that the underlying criterium whether to switch sides or not solely relies on the
   * minimization of edge crossings. Hence, edge length and other aesthetics criteria are not addressed.
   */
  public static final IProperty<Boolean> ALLOW_NON_FLOW_PORTS_TO_SWITCH_SIDES = LayeredMetaDataProvider.ALLOW_NON_FLOW_PORTS_TO_SWITCH_SIDES;

  /**
   * Limits the number of iterations for node promotion.
   */
  public static final IProperty<Integer> LAYERING_NODE_PROMOTION_MAX_ITERATIONS = LayeredMetaDataProvider.LAYERING_NODE_PROMOTION_MAX_ITERATIONS;

  /**
   * Method to decide on edge label sides.
   */
  public static final IProperty<EdgeLabelSideSelection> EDGE_LABELS_SIDE_SELECTION = LayeredMetaDataProvider.EDGE_LABELS_SIDE_SELECTION;

  /**
   * Determines in which layer center labels of long edges should be placed.
   */
  public static final IProperty<CenterEdgeLabelPlacementStrategy> EDGE_LABELS_CENTER_LABEL_PLACEMENT_STRATEGY = LayeredMetaDataProvider.EDGE_LABELS_CENTER_LABEL_PLACEMENT_STRATEGY;

  /**
   * Margins define additional space around the actual bounds of a graph element. For instance,
   * ports or labels being placed on the outside of a node's border might introduce such a
   * margin. The margin is used to guarantee non-overlap of other graph elements with those
   * ports or labels.
   */
  public static final IProperty<ElkMargin> MARGINS = CoreOptions.MARGINS;

  /**
   * The maximum number of nodes allowed per layer.
   */
  public static final IProperty<Integer> LAYERING_COFFMAN_GRAHAM_LAYER_BOUND = LayeredMetaDataProvider.LAYERING_COFFMAN_GRAHAM_LAYER_BOUND;

  /**
   * Favor straight edges over a balanced node placement.
   * The default behavior is determined automatically based on the used 'edgeRouting'.
   * For an orthogonal style it is set to true, for all other styles to false.
   */
  public static final IProperty<Boolean> NODE_PLACEMENT_FAVOR_STRAIGHT_EDGES = LayeredMetaDataProvider.NODE_PLACEMENT_FAVOR_STRAIGHT_EDGES;

  /**
   * Additional space around the sets of ports on each node side. For each side of a node,
   * this option can reserve additional space before and after the ports on each side. For
   * example, a top spacing of 20 makes sure that the first port on the western and eastern
   * side is 20 units away from the northern border.
   */
  public static final IProperty<ElkMargin> SPACING_PORTS_SURROUNDING = CoreOptions.SPACING_PORTS_SURROUNDING;

  /**
   * Specifies how drawings of the same graph with different layout directions compare to each other:
   * either a natural reading direction is preserved or the drawings are rotated versions of each other.
   */
  public static final IProperty<DirectionCongruency> DIRECTION_CONGRUENCY = LayeredMetaDataProvider.DIRECTION_CONGRUENCY;

  /**
   * Only relevant for nodes with FIXED_SIDE port constraints. Determines the way a node's ports are
   * distributed on the sides of a node if their order is not prescribed. The option is set on parent nodes.
   */
  public static final IProperty<PortSortingStrategy> PORT_SORTING_STRATEGY = LayeredMetaDataProvider.PORT_SORTING_STRATEGY;

  /**
   * Width of the strip to the left and to the right of each layer where the polyline edge router
   * is allowed to refrain from ensuring that edges are routed horizontally. This prevents awkward
   * bend points for nodes that extent almost to the edge of their layer.
   */
  public static final IProperty<Double> EDGE_ROUTING_POLYLINE_SLOPED_EDGE_ZONE_WIDTH = LayeredMetaDataProvider.EDGE_ROUTING_POLYLINE_SLOPED_EDGE_ZONE_WIDTH;

  /**
   * Allows to set a constraint which specifies of which node
   * the current node is the predecessor.
   * If set to 's' then the node is the predecessor of 's' and is in the same layer
   */
  public static final IProperty<String> CROSSING_MINIMIZATION_IN_LAYER_PRED_OF = LayeredMetaDataProvider.CROSSING_MINIMIZATION_IN_LAYER_PRED_OF;

  /**
   * Allows to set a constraint which specifies of which node
   * the current node is the successor.
   * If set to 's' then the node is the successor of 's' and is in the same layer
   */
  public static final IProperty<String> CROSSING_MINIMIZATION_IN_LAYER_SUCC_OF = LayeredMetaDataProvider.CROSSING_MINIMIZATION_IN_LAYER_SUCC_OF;

  /**
   * Allows to set a constraint regarding the layer placement of a node.
   * Let i be the value of teh constraint. Assumed the drawing has n layers and i < n.
   * If set to i, it expresses that the node should be placed in i-th layer.
   * Should i>=n be true then the node is placed in the last layer of the drawing.
   * 
   * Note that this option is not part of any of ELK Layered's default configurations
   * but is only evaluated as part of the `InteractiveLayeredGraphVisitor`,
   * which must be applied manually or used via the `DiagramLayoutEngine.
   */
  public static final IProperty<Integer> LAYERING_LAYER_CHOICE_CONSTRAINT = LayeredMetaDataProvider.LAYERING_LAYER_CHOICE_CONSTRAINT;

  /**
   * Allows to set a constraint regarding the position placement of a node in a layer.
   * Assumed the layer in which the node placed includes n other nodes and i < n.
   * If set to i, it expresses that the node should be placed at the i-th position.
   * Should i>=n be true then the node is placed at the last position in the layer.
   * 
   * Note that this option is not part of any of ELK Layered's default configurations
   * but is only evaluated as part of the `InteractiveLayeredGraphVisitor`,
   * which must be applied manually or used via the `DiagramLayoutEngine.
   */
  public static final IProperty<Integer> CROSSING_MINIMIZATION_POSITION_CHOICE_CONSTRAINT = LayeredMetaDataProvider.CROSSING_MINIMIZATION_POSITION_CHOICE_CONSTRAINT;

  /**
   * Whether the graph should be changeable interactively and by setting constraints
   */
  public static final IProperty<Boolean> INTERACTIVE_LAYOUT = CoreOptions.INTERACTIVE_LAYOUT;

  /**
   * Layer identifier that was calculated by ELK Layered for a node.
   * This is only generated if interactiveLayot or generatePositionAndLayerIds is set.
   */
  public static final IProperty<Integer> LAYERING_LAYER_ID = LayeredMetaDataProvider.LAYERING_LAYER_ID;

  /**
   * Position within a layer that was determined by ELK Layered for a node.
   * This is only generated if interactiveLayot or generatePositionAndLayerIds is set.
   */
  public static final IProperty<Integer> CROSSING_MINIMIZATION_POSITION_ID = LayeredMetaDataProvider.CROSSING_MINIMIZATION_POSITION_ID;

  /**
   * Preserves the order of nodes and edges in the model file if this does not lead to additional edge
   * crossings. Depending on the strategy this is not always possible since the node and edge order might be
   * conflicting.
   */
  public static final IProperty<OrderingStrategy> CONSIDER_MODEL_ORDER_STRATEGY = LayeredMetaDataProvider.CONSIDER_MODEL_ORDER_STRATEGY;

  /**
   * Indicates whether long edges are sorted under, over, or equal to nodes that have no connection to a
   * previous layer in a left-to-right or right-to-left layout. Under and over changes to right and left in a
   * vertical layout.
   */
  public static final IProperty<LongEdgeOrderingStrategy> CONSIDER_MODEL_ORDER_LONG_EDGE_STRATEGY = LayeredMetaDataProvider.CONSIDER_MODEL_ORDER_LONG_EDGE_STRATEGY;

  /**
   * Indicates with what percentage (1 for 100%) violations of the node model order are weighted against the
   * crossings e.g. a value of 0.5 means two model order violations are as important as on edge crossing.
   * This allows some edge crossings in favor of preserving the model order. It is advised to set this value to
   * a very small positive value (e.g. 0.001) to have minimal crossing and a optimal node order. Defaults to no
   * influence (0).
   */
  public static final IProperty<Double> CONSIDER_MODEL_ORDER_CROSSING_COUNTER_NODE_INFLUENCE = LayeredMetaDataProvider.CONSIDER_MODEL_ORDER_CROSSING_COUNTER_NODE_INFLUENCE;

  /**
   * Indicates with what percentage (1 for 100%) violations of the port model order are weighted against the
   * crossings e.g. a value of 0.5 means two model order violations are as important as on edge crossing.
   * This allows some edge crossings in favor of preserving the model order. It is advised to set this value to
   * a very small positive value (e.g. 0.001) to have minimal crossing and a optimal port order. Defaults to no
   * influence (0).
   */
  public static final IProperty<Double> CONSIDER_MODEL_ORDER_CROSSING_COUNTER_PORT_INFLUENCE = LayeredMetaDataProvider.CONSIDER_MODEL_ORDER_CROSSING_COUNTER_PORT_INFLUENCE;

  /**
   * Set on a node to not set a model order for this node even though it is a real node.
   */
  public static final IProperty<Boolean> CONSIDER_MODEL_ORDER_NO_MODEL_ORDER = LayeredMetaDataProvider.CONSIDER_MODEL_ORDER_NO_MODEL_ORDER;

  /**
   * If set to NONE the usual ordering strategy (by cumulative node priority and size of nodes) is used.
   * INSIDE_PORT_SIDES orders the components with external ports only inside the groups with the same port side.
   * FORCE_MODEL_ORDER enforces the mode order on components. This option might produce bad alignments and sub
   * optimal drawings in terms of used area since the ordering should be respected.
   */
  public static final IProperty<ComponentOrderingStrategy> CONSIDER_MODEL_ORDER_COMPONENTS = LayeredMetaDataProvider.CONSIDER_MODEL_ORDER_COMPONENTS;

  /**
   * If disabled the port order of output ports is derived from the edge order and input ports are ordered by
   * their incoming connections. If enabled all ports are ordered by the port model order.
   */
  public static final IProperty<Boolean> CONSIDER_MODEL_ORDER_PORT_MODEL_ORDER = LayeredMetaDataProvider.CONSIDER_MODEL_ORDER_PORT_MODEL_ORDER;

  /**
   * If enabled position id and layer id are generated, which are usually only used internally
   * when setting the interactiveLayout option. This option should be specified on the root node.
   */
  public static final IProperty<Boolean> GENERATE_POSITION_AND_LAYER_IDS = LayeredMetaDataProvider.GENERATE_POSITION_AND_LAYER_IDS;

  /**
   * Layouter-specific algorithm factory.
   */
  public static class LayeredFactory implements org.eclipse.elk.core.util.IFactory<AbstractLayoutProvider> {
    public AbstractLayoutProvider create() {
      AbstractLayoutProvider provider = new LayeredLayoutProvider();
      provider.initialize("");
      return provider;
    }

    public void destroy(final AbstractLayoutProvider obj) {
      obj.dispose();
    }
  }

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutAlgorithmData.Builder()
        .id("org.eclipse.elk.layered")
        .name("ELK Layered")
        .description("Layer-based algorithm provided by the Eclipse Layout Kernel. Arranges as many edges as possible into one direction by placing nodes into subsequent layers. This implementation supports different routing styles (straight, orthogonal, splines); if orthogonal routing is selected, arbitrary port constraints are respected, thus enabling the layout of block diagrams such as actor-oriented models or circuit schematics. Furthermore, full layout of compound graphs with cross-hierarchy edges is supported when the respective option is activated on the top level.")
        .providerFactory(new LayeredFactory())
        .category("org.eclipse.elk.layered")
        .melkBundleName(null)
        .definingBundleId("org.eclipse.elk.alg.layered")
        .imagePath("images/layered_layout.png")
        .supportedFeatures(EnumSet.of(GraphFeature.SELF_LOOPS, GraphFeature.INSIDE_SELF_LOOPS, GraphFeature.MULTI_EDGES, GraphFeature.EDGE_LABELS, GraphFeature.PORTS, GraphFeature.COMPOUND, GraphFeature.CLUSTERS))
        .create()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.spacing.commentComment",
        SPACING_COMMENT_COMMENT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.spacing.commentNode",
        SPACING_COMMENT_NODE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.spacing.componentComponent",
        SPACING_COMPONENT_COMPONENT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.spacing.edgeEdge",
        SPACING_EDGE_EDGE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.spacing.edgeLabel",
        SPACING_EDGE_LABEL.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.spacing.edgeNode",
        SPACING_EDGE_NODE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.spacing.labelLabel",
        SPACING_LABEL_LABEL.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.spacing.labelPortHorizontal",
        SPACING_LABEL_PORT_HORIZONTAL.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.spacing.labelPortVertical",
        SPACING_LABEL_PORT_VERTICAL.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.spacing.labelNode",
        SPACING_LABEL_NODE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.spacing.nodeNode",
        SPACING_NODE_NODE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.spacing.nodeSelfLoop",
        SPACING_NODE_SELF_LOOP.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.spacing.portPort",
        SPACING_PORT_PORT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.spacing.individual",
        SPACING_INDIVIDUAL.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.spacing.baseValue",
        SPACING_BASE_VALUE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.spacing.edgeEdgeBetweenLayers",
        SPACING_EDGE_EDGE_BETWEEN_LAYERS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.spacing.edgeNodeBetweenLayers",
        SPACING_EDGE_NODE_BETWEEN_LAYERS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.spacing.nodeNodeBetweenLayers",
        SPACING_NODE_NODE_BETWEEN_LAYERS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.priority",
        PRIORITY_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.priority.direction",
        PRIORITY_DIRECTION.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.priority.shortness",
        PRIORITY_SHORTNESS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.priority.straightness",
        PRIORITY_STRAIGHTNESS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.wrapping.strategy",
        WRAPPING_STRATEGY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.wrapping.additionalEdgeSpacing",
        WRAPPING_ADDITIONAL_EDGE_SPACING.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.wrapping.correctionFactor",
        WRAPPING_CORRECTION_FACTOR.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.wrapping.cutting.strategy",
        WRAPPING_CUTTING_STRATEGY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.wrapping.cutting.cuts",
        WRAPPING_CUTTING_CUTS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.wrapping.cutting.msd.freedom",
        WRAPPING_CUTTING_MSD_FREEDOM.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.wrapping.validify.strategy",
        WRAPPING_VALIDIFY_STRATEGY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.wrapping.validify.forbiddenIndices",
        WRAPPING_VALIDIFY_FORBIDDEN_INDICES.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.wrapping.multiEdge.improveCuts",
        WRAPPING_MULTI_EDGE_IMPROVE_CUTS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.wrapping.multiEdge.distancePenalty",
        WRAPPING_MULTI_EDGE_DISTANCE_PENALTY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.wrapping.multiEdge.improveWrappedEdges",
        WRAPPING_MULTI_EDGE_IMPROVE_WRAPPED_EDGES.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.nodePlacement.networkSimplex.nodeFlexibility",
        NODE_PLACEMENT_NETWORK_SIMPLEX_NODE_FLEXIBILITY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.nodePlacement.networkSimplex.nodeFlexibility.default",
        NODE_PLACEMENT_NETWORK_SIMPLEX_NODE_FLEXIBILITY_DEFAULT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.edgeRouting.splines.mode",
        EDGE_ROUTING_SPLINES_MODE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.edgeRouting.splines.sloppy.layerSpacingFactor",
        EDGE_ROUTING_SPLINES_SLOPPY_LAYER_SPACING_FACTOR.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.topdownLayout",
        TOPDOWN_LAYOUT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.topdown.scaleFactor",
        TOPDOWN_SCALE_FACTOR.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.topdown.hierarchicalNodeWidth",
        TOPDOWN_HIERARCHICAL_NODE_WIDTH.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.topdown.hierarchicalNodeAspectRatio",
        TOPDOWN_HIERARCHICAL_NODE_ASPECT_RATIO.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.topdown.nodeType",
        TOPDOWN_NODE_TYPE_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.padding",
        PADDING_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.edgeRouting",
        EDGE_ROUTING_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.port.borderOffset",
        PORT_BORDER_OFFSET_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.randomSeed",
        RANDOM_SEED_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.aspectRatio",
        ASPECT_RATIO_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.noLayout",
        NO_LAYOUT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.portConstraints",
        PORT_CONSTRAINTS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.port.side",
        PORT_SIDE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.debugMode",
        DEBUG_MODE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.alignment",
        ALIGNMENT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.hierarchyHandling",
        HIERARCHY_HANDLING.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.separateConnectedComponents",
        SEPARATE_CONNECTED_COMPONENTS_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.insideSelfLoops.activate",
        INSIDE_SELF_LOOPS_ACTIVATE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.insideSelfLoops.yo",
        INSIDE_SELF_LOOPS_YO.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.nodeSize.constraints",
        NODE_SIZE_CONSTRAINTS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.nodeSize.options",
        NODE_SIZE_OPTIONS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.nodeSize.fixedGraphSize",
        NODE_SIZE_FIXED_GRAPH_SIZE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.direction",
        DIRECTION_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.nodeLabels.placement",
        NODE_LABELS_PLACEMENT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.nodeLabels.padding",
        NODE_LABELS_PADDING.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.portLabels.placement",
        PORT_LABELS_PLACEMENT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.portLabels.nextToPortIfPossible",
        PORT_LABELS_NEXT_TO_PORT_IF_POSSIBLE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.portLabels.treatAsGroup",
        PORT_LABELS_TREAT_AS_GROUP.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.portAlignment.default",
        PORT_ALIGNMENT_DEFAULT_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.portAlignment.north",
        PORT_ALIGNMENT_NORTH.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.portAlignment.south",
        PORT_ALIGNMENT_SOUTH.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.portAlignment.west",
        PORT_ALIGNMENT_WEST.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.portAlignment.east",
        PORT_ALIGNMENT_EAST.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.unnecessaryBendpoints",
        UNNECESSARY_BENDPOINTS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.layering.strategy",
        LAYERING_STRATEGY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.layering.nodePromotion.strategy",
        LAYERING_NODE_PROMOTION_STRATEGY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.thoroughness",
        THOROUGHNESS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.layering.layerConstraint",
        LAYERING_LAYER_CONSTRAINT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.cycleBreaking.strategy",
        CYCLE_BREAKING_STRATEGY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.crossingMinimization.strategy",
        CROSSING_MINIMIZATION_STRATEGY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.crossingMinimization.forceNodeModelOrder",
        CROSSING_MINIMIZATION_FORCE_NODE_MODEL_ORDER.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.crossingMinimization.greedySwitch.activationThreshold",
        CROSSING_MINIMIZATION_GREEDY_SWITCH_ACTIVATION_THRESHOLD.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.crossingMinimization.greedySwitch.type",
        CROSSING_MINIMIZATION_GREEDY_SWITCH_TYPE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.crossingMinimization.greedySwitchHierarchical.type",
        CROSSING_MINIMIZATION_GREEDY_SWITCH_HIERARCHICAL_TYPE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.crossingMinimization.semiInteractive",
        CROSSING_MINIMIZATION_SEMI_INTERACTIVE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.mergeEdges",
        MERGE_EDGES.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.mergeHierarchyEdges",
        MERGE_HIERARCHY_EDGES.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.interactiveReferencePoint",
        INTERACTIVE_REFERENCE_POINT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.nodePlacement.strategy",
        NODE_PLACEMENT_STRATEGY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.nodePlacement.bk.fixedAlignment",
        NODE_PLACEMENT_BK_FIXED_ALIGNMENT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.feedbackEdges",
        FEEDBACK_EDGES.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.nodePlacement.linearSegments.deflectionDampening",
        NODE_PLACEMENT_LINEAR_SEGMENTS_DEFLECTION_DAMPENING.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.edgeRouting.selfLoopDistribution",
        EDGE_ROUTING_SELF_LOOP_DISTRIBUTION.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.edgeRouting.selfLoopOrdering",
        EDGE_ROUTING_SELF_LOOP_ORDERING.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.contentAlignment",
        CONTENT_ALIGNMENT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.nodePlacement.bk.edgeStraightening",
        NODE_PLACEMENT_BK_EDGE_STRAIGHTENING.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.compaction.postCompaction.strategy",
        COMPACTION_POST_COMPACTION_STRATEGY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.compaction.postCompaction.constraints",
        COMPACTION_POST_COMPACTION_CONSTRAINTS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.compaction.connectedComponents",
        COMPACTION_CONNECTED_COMPONENTS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.highDegreeNodes.treatment",
        HIGH_DEGREE_NODES_TREATMENT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.highDegreeNodes.threshold",
        HIGH_DEGREE_NODES_THRESHOLD.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.highDegreeNodes.treeHeight",
        HIGH_DEGREE_NODES_TREE_HEIGHT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.nodeSize.minimum",
        NODE_SIZE_MINIMUM.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.junctionPoints",
        JUNCTION_POINTS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.edge.thickness",
        EDGE_THICKNESS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.edgeLabels.placement",
        EDGE_LABELS_PLACEMENT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.edgeLabels.inline",
        EDGE_LABELS_INLINE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.crossingMinimization.hierarchicalSweepiness",
        CROSSING_MINIMIZATION_HIERARCHICAL_SWEEPINESS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.port.index",
        PORT_INDEX.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.commentBox",
        COMMENT_BOX.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.hypernode",
        HYPERNODE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.port.anchor",
        PORT_ANCHOR.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.partitioning.activate",
        PARTITIONING_ACTIVATE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.partitioning.partition",
        PARTITIONING_PARTITION.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.layering.minWidth.upperBoundOnWidth",
        LAYERING_MIN_WIDTH_UPPER_BOUND_ON_WIDTH.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.layering.minWidth.upperLayerEstimationScalingFactor",
        LAYERING_MIN_WIDTH_UPPER_LAYER_ESTIMATION_SCALING_FACTOR.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.position",
        POSITION.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.allowNonFlowPortsToSwitchSides",
        ALLOW_NON_FLOW_PORTS_TO_SWITCH_SIDES.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.layering.nodePromotion.maxIterations",
        LAYERING_NODE_PROMOTION_MAX_ITERATIONS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.edgeLabels.sideSelection",
        EDGE_LABELS_SIDE_SELECTION.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.edgeLabels.centerLabelPlacementStrategy",
        EDGE_LABELS_CENTER_LABEL_PLACEMENT_STRATEGY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.margins",
        MARGINS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.layering.coffmanGraham.layerBound",
        LAYERING_COFFMAN_GRAHAM_LAYER_BOUND.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.nodePlacement.favorStraightEdges",
        NODE_PLACEMENT_FAVOR_STRAIGHT_EDGES.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.spacing.portsSurrounding",
        SPACING_PORTS_SURROUNDING.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.directionCongruency",
        DIRECTION_CONGRUENCY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.portSortingStrategy",
        PORT_SORTING_STRATEGY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.edgeRouting.polyline.slopedEdgeZoneWidth",
        EDGE_ROUTING_POLYLINE_SLOPED_EDGE_ZONE_WIDTH.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.crossingMinimization.inLayerPredOf",
        CROSSING_MINIMIZATION_IN_LAYER_PRED_OF.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.crossingMinimization.inLayerSuccOf",
        CROSSING_MINIMIZATION_IN_LAYER_SUCC_OF.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.layering.layerChoiceConstraint",
        LAYERING_LAYER_CHOICE_CONSTRAINT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.crossingMinimization.positionChoiceConstraint",
        CROSSING_MINIMIZATION_POSITION_CHOICE_CONSTRAINT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.interactiveLayout",
        INTERACTIVE_LAYOUT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.layering.layerId",
        LAYERING_LAYER_ID.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.crossingMinimization.positionId",
        CROSSING_MINIMIZATION_POSITION_ID.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.considerModelOrder.strategy",
        CONSIDER_MODEL_ORDER_STRATEGY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.considerModelOrder.longEdgeStrategy",
        CONSIDER_MODEL_ORDER_LONG_EDGE_STRATEGY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.considerModelOrder.crossingCounterNodeInfluence",
        CONSIDER_MODEL_ORDER_CROSSING_COUNTER_NODE_INFLUENCE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.considerModelOrder.crossingCounterPortInfluence",
        CONSIDER_MODEL_ORDER_CROSSING_COUNTER_PORT_INFLUENCE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.considerModelOrder.noModelOrder",
        CONSIDER_MODEL_ORDER_NO_MODEL_ORDER.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.considerModelOrder.components",
        CONSIDER_MODEL_ORDER_COMPONENTS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.considerModelOrder.portModelOrder",
        CONSIDER_MODEL_ORDER_PORT_MODEL_ORDER.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.generatePositionAndLayerIds",
        GENERATE_POSITION_AND_LAYER_IDS.getDefault()
    );
  }
}
