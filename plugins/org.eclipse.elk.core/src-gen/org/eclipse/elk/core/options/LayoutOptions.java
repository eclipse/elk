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
package org.eclipse.elk.core.options;

import java.util.EnumSet;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.data.LayoutCategoryData;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.core.labels.ILabelManager;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.Alignment;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.EdgeLabelPlacement;
import org.eclipse.elk.core.options.EdgeLabelPlacementStrategy;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.EdgeType;
import org.eclipse.elk.core.options.HierarchyHandling;
import org.eclipse.elk.core.options.NodeLabelPlacement;
import org.eclipse.elk.core.options.PortAlignment;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortLabelPlacement;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.core.util.AlgorithmFactory;
import org.eclipse.elk.core.util.BoxLayoutProvider;
import org.eclipse.elk.core.util.FixedLayoutProvider;
import org.eclipse.elk.core.util.RandomLayoutProvider;
import org.eclipse.elk.core.util.nodespacing.Spacing;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * Core definitions of the Eclipse Layout Kernel.
 */
@SuppressWarnings("all")
public class LayoutOptions implements ILayoutMetaDataProvider {
  /**
   * Select a specific layout algorithm.
   */
  public final static IProperty<String> ALGORITHM = new Property<String>(
            "org.eclipse.elk.algorithm");
  
  /**
   * Default value for {@link #ALIGNMENT}.
   */
  private final static Alignment ALIGNMENT_DEFAULT = Alignment.AUTOMATIC;
  
  /**
   * Alignment of the selected node relative to other nodes in the same row or column.
   */
  public final static IProperty<Alignment> ALIGNMENT = new Property<Alignment>(
            "org.eclipse.elk.alignment",
            ALIGNMENT_DEFAULT);
  
  /**
   * The desired aspect ratio of the drawing, that is the quotient of width by height.
   */
  public final static IProperty<Float> ASPECT_RATIO = new Property<Float>(
            "org.eclipse.elk.aspectRatio");
  
  /**
   * A fixed list of bend points for the edge. This is used by the 'Fixed Layout' algorithm to
   * specify a pre-defined routing for an edge. The vector chain must include the source point,
   * any bend points, and the target point, so it must have at least two points.
   */
  public final static IProperty<KVectorChain> BEND_POINTS = new Property<KVectorChain>(
            "org.eclipse.elk.bendPoints");
  
  /**
   * Spacing of the content of a parent node to its inner border. The inner border is the node
   * border, which is given by width and height, with subtracted insets.
   */
  public final static IProperty<Float> BORDER_SPACING = new Property<Float>(
            "org.eclipse.elk.borderSpacing");
  
  /**
   * Default value for {@link #DEBUG_MODE}.
   */
  private final static boolean DEBUG_MODE_DEFAULT = false;
  
  /**
   * Whether additional debug information shall be generated.
   */
  public final static IProperty<Boolean> DEBUG_MODE = new Property<Boolean>(
            "org.eclipse.elk.debugMode",
            DEBUG_MODE_DEFAULT);
  
  /**
   * Default value for {@link #DIRECTION}.
   */
  private final static Direction DIRECTION_DEFAULT = Direction.UNDEFINED;
  
  /**
   * Overall direction of edges: horizontal (right / left) or
   * vertical (down / up).
   */
  public final static IProperty<Direction> DIRECTION = new Property<Direction>(
            "org.eclipse.elk.direction",
            DIRECTION_DEFAULT);
  
  /**
   * Default value for {@link #EDGE_LABEL_PLACEMENT_STRATEGY}.
   */
  private final static EdgeLabelPlacementStrategy EDGE_LABEL_PLACEMENT_STRATEGY_DEFAULT = EdgeLabelPlacementStrategy.CENTER;
  
  /**
   * Determines in which layer center labels of long edges should be placed.
   */
  public final static IProperty<EdgeLabelPlacementStrategy> EDGE_LABEL_PLACEMENT_STRATEGY = new Property<EdgeLabelPlacementStrategy>(
            "org.eclipse.elk.edgeLabelPlacementStrategy",
            EDGE_LABEL_PLACEMENT_STRATEGY_DEFAULT);
  
  /**
   * Default value for {@link #EDGE_ROUTING}.
   */
  private final static EdgeRouting EDGE_ROUTING_DEFAULT = EdgeRouting.UNDEFINED;
  
  /**
   * What kind of edge routing style should be applied for the content of a parent node.
   * Algorithms may also set this option to single edges in order to mark them as splines.
   * The bend point list of edges with this option set to SPLINES must be interpreted as control
   * points for a piecewise cubic spline.
   */
  public final static IProperty<EdgeRouting> EDGE_ROUTING = new Property<EdgeRouting>(
            "org.eclipse.elk.edgeRouting",
            EDGE_ROUTING_DEFAULT);
  
  /**
   * Default value for {@link #EXPAND_NODES}.
   */
  private final static boolean EXPAND_NODES_DEFAULT = false;
  
  /**
   * If active, nodes are expanded to fill the area of their parent.
   */
  public final static IProperty<Boolean> EXPAND_NODES = new Property<Boolean>(
            "org.eclipse.elk.expandNodes",
            EXPAND_NODES_DEFAULT);
  
  /**
   * Default value for {@link #HIERARCHY_HANDLING}.
   */
  private final static HierarchyHandling HIERARCHY_HANDLING_DEFAULT = HierarchyHandling.INHERIT;
  
  /**
   * Determines whether the descendants should be layouted separately or together with their
   * parents. If the root node is set to inherit (or not set at all), the property is assumed
   * as SEPARATE_CHILDREN.
   */
  public final static IProperty<HierarchyHandling> HIERARCHY_HANDLING = new Property<HierarchyHandling>(
            "org.eclipse.elk.hierarchyHandling",
            HIERARCHY_HANDLING_DEFAULT);
  
  /**
   * Default value for {@link #INTERACTIVE}.
   */
  private final static boolean INTERACTIVE_DEFAULT = false;
  
  /**
   * Whether the algorithm should be run in interactive mode for the content of a parent node.
   * What this means exactly depends on how the specific algorithm interprets this option.
   * Usually in the interactive mode algorithms try to modify the current layout as little as
   * possible.
   */
  public final static IProperty<Boolean> INTERACTIVE = new Property<Boolean>(
            "org.eclipse.elk.interactive",
            INTERACTIVE_DEFAULT);
  
  /**
   * Default value for {@link #LABEL_SPACING}.
   */
  private final static float LABEL_SPACING_DEFAULT = 0;
  
  /**
   * Determines the amount of space to be left around labels.
   */
  public final static IProperty<Float> LABEL_SPACING = new Property<Float>(
            "org.eclipse.elk.labelSpacing",
            LABEL_SPACING_DEFAULT);
  
  /**
   * Default value for {@link #LAYOUT_HIERARCHY}.
   */
  private final static boolean LAYOUT_HIERARCHY_DEFAULT = false;
  
  /**
   * Whether the whole hierarchy shall be layouted. If this option is not set, each hierarchy
   * level of the graph is processed independently, possibly by different layout algorithms,
   * beginning with the lowest level. If it is set, the algorithm is responsible to process
   * all hierarchy levels that are contained in the associated parent node.
   */
  public final static IProperty<Boolean> LAYOUT_HIERARCHY = new Property<Boolean>(
            "org.eclipse.elk.layoutHierarchy",
            LAYOUT_HIERARCHY_DEFAULT);
  
  /**
   * Partition to which the node belongs to. If 'layoutPartitions' is true,
   * all nodes are expected to have a partition.
   */
  public final static IProperty<Integer> LAYOUT_PARTITION = new Property<Integer>(
            "org.eclipse.elk.layoutPartition");
  
  /**
   * Default value for {@link #LAYOUT_PARTITIONS}.
   */
  private final static Boolean LAYOUT_PARTITIONS_DEFAULT = Boolean.valueOf(false);
  
  /**
   * Whether to activate partitioned layout.
   */
  public final static IProperty<Boolean> LAYOUT_PARTITIONS = new Property<Boolean>(
            "org.eclipse.elk.layoutPartitions",
            LAYOUT_PARTITIONS_DEFAULT);
  
  /**
   * Default value for {@link #NODE_LABEL_INSETS}.
   */
  private final static Spacing.Insets NODE_LABEL_INSETS_DEFAULT = new Spacing.Insets(0, 0, 0, 0);
  
  /**
   * Define insets for node labels that are placed inside of a node.
   */
  public final static IProperty<Spacing.Insets> NODE_LABEL_INSETS = new Property<Spacing.Insets>(
            "org.eclipse.elk.nodeLabelInsets",
            NODE_LABEL_INSETS_DEFAULT);
  
  /**
   * Default value for {@link #NODE_LABEL_PLACEMENT}.
   */
  private final static EnumSet<NodeLabelPlacement> NODE_LABEL_PLACEMENT_DEFAULT = NodeLabelPlacement.fixed();
  
  /**
   * Hints for where node labels are to be placed; if empty, the node label's position is not
   * modified.
   */
  public final static IProperty<EnumSet<NodeLabelPlacement>> NODE_LABEL_PLACEMENT = new Property<EnumSet<NodeLabelPlacement>>(
            "org.eclipse.elk.nodeLabelPlacement",
            NODE_LABEL_PLACEMENT_DEFAULT);
  
  /**
   * Default value for {@link #PORT_ALIGNMENT}.
   */
  private final static PortAlignment PORT_ALIGNMENT_DEFAULT = PortAlignment.JUSTIFIED;
  
  /**
   * Defines the default port distribution for a node.
   */
  public final static IProperty<PortAlignment> PORT_ALIGNMENT = new Property<PortAlignment>(
            "org.eclipse.elk.portAlignment",
            PORT_ALIGNMENT_DEFAULT);
  
  /**
   * Default value for {@link #PORT_ALIGNMENT_NORTH}.
   */
  private final static PortAlignment PORT_ALIGNMENT_NORTH_DEFAULT = PortAlignment.UNDEFINED;
  
  /**
   * Defines how ports on the northern side are placed, overriding the node's general port alignment.
   */
  public final static IProperty<PortAlignment> PORT_ALIGNMENT_NORTH = new Property<PortAlignment>(
            "org.eclipse.elk.portAlignmentNorth",
            PORT_ALIGNMENT_NORTH_DEFAULT);
  
  /**
   * Default value for {@link #PORT_ALIGNMENT_SOUTH}.
   */
  private final static PortAlignment PORT_ALIGNMENT_SOUTH_DEFAULT = PortAlignment.UNDEFINED;
  
  /**
   * Defines how ports on the southern side are placed, overriding the node's general port alignment.
   */
  public final static IProperty<PortAlignment> PORT_ALIGNMENT_SOUTH = new Property<PortAlignment>(
            "org.eclipse.elk.portAlignmentSouth",
            PORT_ALIGNMENT_SOUTH_DEFAULT);
  
  /**
   * Default value for {@link #PORT_ALIGNMENT_WEST}.
   */
  private final static PortAlignment PORT_ALIGNMENT_WEST_DEFAULT = PortAlignment.UNDEFINED;
  
  /**
   * Defines how ports on the western side are placed, overriding the node's general port alignment.
   */
  public final static IProperty<PortAlignment> PORT_ALIGNMENT_WEST = new Property<PortAlignment>(
            "org.eclipse.elk.portAlignmentWest",
            PORT_ALIGNMENT_WEST_DEFAULT);
  
  /**
   * Default value for {@link #PORT_ALIGNMENT_EAST}.
   */
  private final static PortAlignment PORT_ALIGNMENT_EAST_DEFAULT = PortAlignment.UNDEFINED;
  
  /**
   * Defines how ports on the eastern side are placed, overriding the node's general port alignment.
   */
  public final static IProperty<PortAlignment> PORT_ALIGNMENT_EAST = new Property<PortAlignment>(
            "org.eclipse.elk.portAlignmentEast",
            PORT_ALIGNMENT_EAST_DEFAULT);
  
  /**
   * Default value for {@link #PORT_CONSTRAINTS}.
   */
  private final static PortConstraints PORT_CONSTRAINTS_DEFAULT = PortConstraints.UNDEFINED;
  
  /**
   * Defines constraints of the position of the ports of a node.
   */
  public final static IProperty<PortConstraints> PORT_CONSTRAINTS = new Property<PortConstraints>(
            "org.eclipse.elk.portConstraints",
            PORT_CONSTRAINTS_DEFAULT);
  
  /**
   * Default value for {@link #PORT_SPACING}.
   */
  private final static float PORT_SPACING_DEFAULT = 10;
  
  /**
   * Spacing between ports of a given node.
   */
  public final static IProperty<Float> PORT_SPACING = new Property<Float>(
            "org.eclipse.elk.portSpacing",
            PORT_SPACING_DEFAULT);
  
  /**
   * The position of a node, port, or label. This is used by the 'Fixed Layout' algorithm to
   * specify a pre-defined position.
   */
  public final static IProperty<KVector> POSITION = new Property<KVector>(
            "org.eclipse.elk.position");
  
  /**
   * Defines the priority of an object; its meaning depends on the specific layout algorithm
   * and the context where it is used.
   */
  public final static IProperty<Integer> PRIORITY = new Property<Integer>(
            "org.eclipse.elk.priority");
  
  /**
   * Seed used for pseudo-random number generators to control the layout algorithm. If the
   * value is 0, the seed shall be determined pseudo-randomly (e.g. from the system time).
   */
  public final static IProperty<Integer> RANDOM_SEED = new Property<Integer>(
            "org.eclipse.elk.randomSeed");
  
  /**
   * Whether each connected component should be processed separately.
   */
  public final static IProperty<Boolean> SEPARATE_CONN_COMP = new Property<Boolean>(
            "org.eclipse.elk.separateConnComp");
  
  /**
   * Default value for {@link #SIZE_CONSTRAINT}.
   */
  private final static EnumSet<SizeConstraint> SIZE_CONSTRAINT_DEFAULT = EnumSet.<SizeConstraint>noneOf(SizeConstraint.class);
  
  /**
   * Constraints for determining node sizes. Each member of the set specifies something that
   * should be taken into account when calculating node sizes. The empty set corresponds to
   * node sizes being fixed.
   */
  public final static IProperty<EnumSet<SizeConstraint>> SIZE_CONSTRAINT = new Property<EnumSet<SizeConstraint>>(
            "org.eclipse.elk.sizeConstraint",
            SIZE_CONSTRAINT_DEFAULT);
  
  /**
   * Default value for {@link #SIZE_OPTIONS}.
   */
  private final static EnumSet<SizeOptions> SIZE_OPTIONS_DEFAULT = EnumSet.<SizeOptions>of(SizeOptions.DEFAULT_MINIMUM_SIZE, 
    SizeOptions.APPLY_ADDITIONAL_INSETS);
  
  /**
   * Options modifying the behavior of the size constraints set on a node. Each member of the
   * set specifies something that should be taken into account when calculating node sizes.
   * The empty set corresponds to no further modifications.
   */
  public final static IProperty<EnumSet<SizeOptions>> SIZE_OPTIONS = new Property<EnumSet<SizeOptions>>(
            "org.eclipse.elk.sizeOptions",
            SIZE_OPTIONS_DEFAULT);
  
  /**
   * Overall spacing between elements. This is mostly interpreted as the minimal distance
   * between each two nodes and should also influence the spacing between edges.
   */
  public final static IProperty<Float> SPACING = new Property<Float>(
            "org.eclipse.elk.spacing");
  
  /**
   * This property is not used as option, but as output of the layout algorithms. It is
   * attached to edges and determines the points where junction symbols should be drawn in
   * order to represent hyperedges with orthogonal routing. Whether such points are computed
   * depends on the chosen layout algorithm and edge routing style. The points are put into
   * the vector chain with no specific order.
   */
  public final static IProperty<KVectorChain> JUNCTION_POINTS = new Property<KVectorChain>(
            "org.eclipse.elk.junctionPoints");
  
  /**
   * Additional space around the sets of ports on each node side. For each side of a node,
   * this property can reserve additional space before and after the ports on each side. For
   * example, a top spacing of 20 makes sure that the first port on the western and eastern
   * side is 20 units away from the northern border.
   */
  public final static IProperty<Spacing.Margins> ADDITIONAL_PORT_SPACE = new Property<Spacing.Margins>(
            "org.eclipse.elk.additionalPortSpace");
  
  /**
   * Default value for {@link #COMMENT_BOX}.
   */
  private final static boolean COMMENT_BOX_DEFAULT = false;
  
  /**
   * Whether the node should be regarded as a comment box instead of a regular node. In that
   * case its placement should be similar to how labels are handled. Any edges incident to a
   * comment box specify to which graph elements the comment is related.
   */
  public final static IProperty<Boolean> COMMENT_BOX = new Property<Boolean>(
            "org.eclipse.elk.commentBox",
            COMMENT_BOX_DEFAULT);
  
  /**
   * Default value for {@link #EDGE_LABEL_PLACEMENT}.
   */
  private final static EdgeLabelPlacement EDGE_LABEL_PLACEMENT_DEFAULT = EdgeLabelPlacement.UNDEFINED;
  
  /**
   * Gives a hint on where to put edge labels.
   */
  public final static IProperty<EdgeLabelPlacement> EDGE_LABEL_PLACEMENT = new Property<EdgeLabelPlacement>(
            "org.eclipse.elk.edgeLabelPlacement",
            EDGE_LABEL_PLACEMENT_DEFAULT);
  
  /**
   * Default value for {@link #EDGE_TYPE}.
   */
  private final static EdgeType EDGE_TYPE_DEFAULT = EdgeType.NONE;
  
  /**
   * The type of an edge. This is usually used for UML class diagrams, where associations must
   * be handled differently from generalizations.
   */
  public final static IProperty<EdgeType> EDGE_TYPE = new Property<EdgeType>(
            "org.eclipse.elk.edgeType",
            EDGE_TYPE_DEFAULT);
  
  /**
   * Font name used for a label.
   */
  public final static IProperty<String> FONT_NAME = new Property<String>(
            "org.eclipse.elk.fontName");
  
  /**
   * Font size used for a label.
   */
  public final static IProperty<Integer> FONT_SIZE = new Property<Integer>(
            "org.eclipse.elk.fontSize");
  
  /**
   * Default value for {@link #HYPERNODE}.
   */
  private final static boolean HYPERNODE_DEFAULT = false;
  
  /**
   * Whether the node should be handled as a hypernode.
   */
  public final static IProperty<Boolean> HYPERNODE = new Property<Boolean>(
            "org.eclipse.elk.hypernode",
            HYPERNODE_DEFAULT);
  
  /**
   * Label managers can shorten labels upon a layout algorithm's request.
   */
  public final static IProperty<ILabelManager> LABEL_MANAGER = new Property<ILabelManager>(
            "org.eclipse.elk.labelManager");
  
  /**
   * Default value for {@link #MARGINS}.
   */
  private final static Spacing.Margins MARGINS_DEFAULT = new Spacing.Margins();
  
  /**
   * Margins define additional space around the actual bounds of a graph element. For instance,
   * ports or labels being placed on the outside of a node's border might introduce such a
   * margin. The margin is used to guarantee non-overlap of other graph elements with those
   * ports or labels.
   */
  public final static IProperty<Spacing.Margins> MARGINS = new Property<Spacing.Margins>(
            "org.eclipse.elk.margins",
            MARGINS_DEFAULT);
  
  /**
   * Default value for {@link #MIN_HEIGHT}.
   */
  private final static float MIN_HEIGHT_DEFAULT = 0;
  
  /**
   * The minimal height to which a node can be reduced.
   */
  public final static IProperty<Float> MIN_HEIGHT = new Property<Float>(
            "org.eclipse.elk.minHeight",
            MIN_HEIGHT_DEFAULT);
  
  /**
   * Default value for {@link #MIN_WIDTH}.
   */
  private final static float MIN_WIDTH_DEFAULT = 0;
  
  /**
   * The minimal width to which a node can be reduced.
   */
  public final static IProperty<Float> MIN_WIDTH = new Property<Float>(
            "org.eclipse.elk.minWidth",
            MIN_WIDTH_DEFAULT);
  
  /**
   * Default value for {@link #NO_LAYOUT}.
   */
  private final static boolean NO_LAYOUT_DEFAULT = false;
  
  /**
   * No layout is done for the associated element. This is used to mark parts of a diagram to
   * avoid their inclusion in the layout graph, or to mark parts of the layout graph to
   * prevent layout engines from processing them. If you wish to exclude the contents of a
   * compound node from automatic layout, while the node itself is still considered on its own
   * layer, use the 'Fixed Layout' algorithm for that node.
   */
  public final static IProperty<Boolean> NO_LAYOUT = new Property<Boolean>(
            "org.eclipse.elk.noLayout",
            NO_LAYOUT_DEFAULT);
  
  /**
   * The offset to the port position where connections shall be attached.
   */
  public final static IProperty<KVector> PORT_ANCHOR = new Property<KVector>(
            "org.eclipse.elk.portAnchor");
  
  /**
   * The index of a port in the fixed order around a node. The order is assumed as clockwise,
   * starting with the leftmost port on the top side. This option must be set if 'Port
   * Constraints' is set to FIXED_ORDER and no specific positions are given for the ports.
   * Additionally, the option 'Port Side' must be defined in this case.
   */
  public final static IProperty<Integer> PORT_INDEX = new Property<Integer>(
            "org.eclipse.elk.portIndex");
  
  /**
   * Default value for {@link #PORT_LABEL_PLACEMENT}.
   */
  private final static PortLabelPlacement PORT_LABEL_PLACEMENT_DEFAULT = PortLabelPlacement.OUTSIDE;
  
  /**
   * Decides on a placement method for port labels.
   */
  public final static IProperty<PortLabelPlacement> PORT_LABEL_PLACEMENT = new Property<PortLabelPlacement>(
            "org.eclipse.elk.portLabelPlacement",
            PORT_LABEL_PLACEMENT_DEFAULT);
  
  /**
   * Default value for {@link #PORT_SIDE}.
   */
  private final static PortSide PORT_SIDE_DEFAULT = PortSide.UNDEFINED;
  
  /**
   * The side of a node on which a port is situated. This option must be set if 'Port
   * Constraints' is set to FIXED_SIDE or FIXED_ORDER and no specific positions are given
   * for the ports.
   */
  public final static IProperty<PortSide> PORT_SIDE = new Property<PortSide>(
            "org.eclipse.elk.portSide",
            PORT_SIDE_DEFAULT);
  
  /**
   * The offset of ports on the node border. With a positive offset the port is moved outside
   * of the node, while with a negative offset the port is moved towards the inside. An offset
   * of 0 means that the port is placed directly on the node border, i.e.
   * if the port side is north, the port's south border touches the nodes's north border;
   * if the port side is east, the port's west border touches the nodes's east border;
   * if the port side is south, the port's north border touches the node's south border;
   * if the port side is west, the port's east border touches the node's west border.
   */
  public final static IProperty<Float> PORT_OFFSET = new Property<Float>(
            "org.eclipse.elk.portOffset");
  
  /**
   * Default value for {@link #SCALE_FACTOR}.
   */
  private final static float SCALE_FACTOR_DEFAULT = 1;
  
  /**
   * The scaling factor to be applied to the corresponding node in recursive layout. It causes
   * the corresponding node's size to be adjusted, and its ports and labels to be sized and
   * placed accordingly after the layout of that node has been determined (and before the node
   * itself and its siblings are arranged). The scaling is not reverted afterwards, so the
   * resulting layout graph contains the adjusted size and position data. This option is
   * currently not supported if 'Layout Hierarchy' is set.
   */
  public final static IProperty<Float> SCALE_FACTOR = new Property<Float>(
            "org.eclipse.elk.scaleFactor",
            SCALE_FACTOR_DEFAULT);
  
  /**
   * Default value for {@link #SELF_LOOP_INSIDE}.
   */
  private final static boolean SELF_LOOP_INSIDE_DEFAULT = false;
  
  /**
   * Whether a self loop should be routed around a node or inside that node. The latter will
   * make the node a compound node if it isn't already, and will require the layout algorithm
   * to support compound nodes with hierarchical ports.
   */
  public final static IProperty<Boolean> SELF_LOOP_INSIDE = new Property<Boolean>(
            "org.eclipse.elk.selfLoopInside",
            SELF_LOOP_INSIDE_DEFAULT);
  
  /**
   * Default value for {@link #THICKNESS}.
   */
  private final static float THICKNESS_DEFAULT = 1;
  
  /**
   * The thickness of an edge. This is a hint on the line width used to draw an edge, possibly
   * requiring more space to be reserved for it.
   */
  public final static IProperty<Float> THICKNESS = new Property<Float>(
            "org.eclipse.elk.thickness",
            THICKNESS_DEFAULT);
  
  /**
   * Default value for {@link #ANIMATE}.
   */
  private final static boolean ANIMATE_DEFAULT = true;
  
  /**
   * Whether the shift from the old layout to the new computed layout shall be animated.
   */
  public final static IProperty<Boolean> ANIMATE = new Property<Boolean>(
            "org.eclipse.elk.animate",
            ANIMATE_DEFAULT);
  
  /**
   * Default value for {@link #ANIM_TIME_FACTOR}.
   */
  private final static int ANIM_TIME_FACTOR_DEFAULT = 100;
  
  /**
   * Factor for computation of animation time. The higher the value, the longer the animation
   * time. If the value is 0, the resulting time is always equal to the minimum defined by
   * 'Minimal Animation Time'.
   */
  public final static IProperty<Integer> ANIM_TIME_FACTOR = new Property<Integer>(
            "org.eclipse.elk.animTimeFactor",
            ANIM_TIME_FACTOR_DEFAULT);
  
  /**
   * Default value for {@link #LAYOUT_ANCESTORS}.
   */
  private final static boolean LAYOUT_ANCESTORS_DEFAULT = false;
  
  /**
   * Whether the hierarchy levels on the path from the selected element to the root of the
   * diagram shall be included in the layout process.
   */
  public final static IProperty<Boolean> LAYOUT_ANCESTORS = new Property<Boolean>(
            "org.eclipse.elk.layoutAncestors",
            LAYOUT_ANCESTORS_DEFAULT);
  
  /**
   * Default value for {@link #MAX_ANIM_TIME}.
   */
  private final static int MAX_ANIM_TIME_DEFAULT = 4000;
  
  /**
   * The maximal time for animations, in milliseconds.
   */
  public final static IProperty<Integer> MAX_ANIM_TIME = new Property<Integer>(
            "org.eclipse.elk.maxAnimTime",
            MAX_ANIM_TIME_DEFAULT);
  
  /**
   * Default value for {@link #MIN_ANIM_TIME}.
   */
  private final static int MIN_ANIM_TIME_DEFAULT = 400;
  
  /**
   * The minimal time for animations, in milliseconds.
   */
  public final static IProperty<Integer> MIN_ANIM_TIME = new Property<Integer>(
            "org.eclipse.elk.minAnimTime",
            MIN_ANIM_TIME_DEFAULT);
  
  /**
   * Default value for {@link #PROGRESS_BAR}.
   */
  private final static boolean PROGRESS_BAR_DEFAULT = false;
  
  /**
   * Whether a progress bar shall be displayed during layout computations.
   */
  public final static IProperty<Boolean> PROGRESS_BAR = new Property<Boolean>(
            "org.eclipse.elk.progressBar",
            PROGRESS_BAR_DEFAULT);
  
  /**
   * Default value for {@link #ZOOM_TO_FIT}.
   */
  private final static boolean ZOOM_TO_FIT_DEFAULT = false;
  
  /**
   * Whether the zoom level shall be set to view the whole diagram after layout.
   */
  public final static IProperty<Boolean> ZOOM_TO_FIT = new Property<Boolean>(
            "org.eclipse.elk.zoomToFit",
            ZOOM_TO_FIT_DEFAULT);
  
  /**
   * Required value for dependency between {@link #SCALE_FACTOR} and {@link #LAYOUT_HIERARCHY}.
   */
  private final static boolean SCALE_FACTOR_DEP_LAYOUT_HIERARCHY = false;
  
  /**
   * Default value for {@link #BORDER_SPACING} with algorithm "Fixed Layout".
   */
  private final static float FIXED_SUP_BORDER_SPACING = 15;
  
  /**
   * Default value for {@link #SPACING} with algorithm "Box Layout".
   */
  private final static float BOX_SUP_SPACING = 15;
  
  /**
   * Default value for {@link #BORDER_SPACING} with algorithm "Box Layout".
   */
  private final static float BOX_SUP_BORDER_SPACING = 15;
  
  /**
   * Default value for {@link #PRIORITY} with algorithm "Box Layout".
   */
  private final static int BOX_SUP_PRIORITY = 0;
  
  /**
   * Default value for {@link #ASPECT_RATIO} with algorithm "Box Layout".
   */
  private final static float BOX_SUP_ASPECT_RATIO = 1.3f;
  
  /**
   * Default value for {@link #RANDOM_SEED} with algorithm "Randomizer".
   */
  private final static int RANDOM_SUP_RANDOM_SEED = 0;
  
  /**
   * Default value for {@link #SPACING} with algorithm "Randomizer".
   */
  private final static float RANDOM_SUP_SPACING = 15;
  
  /**
   * Default value for {@link #BORDER_SPACING} with algorithm "Randomizer".
   */
  private final static float RANDOM_SUP_BORDER_SPACING = 15;
  
  /**
   * Default value for {@link #ASPECT_RATIO} with algorithm "Randomizer".
   */
  private final static float RANDOM_SUP_ASPECT_RATIO = 1.6f;
  
  public void apply(final ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.algorithm",
        "Layout Algorithm",
        "Select a specific layout algorithm.",
        null,
        LayoutOptionData.Type.STRING,
        String.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.algorithm"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alignment",
        "Alignment",
        "Alignment of the selected node relative to other nodes in the same row or column.",
        ALIGNMENT_DEFAULT,
        LayoutOptionData.Type.ENUM,
        Alignment.class,
        EnumSet.of(LayoutOptionData.Target.NODES),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.animate"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.aspectRatio",
        "Aspect Ratio",
        "The desired aspect ratio of the drawing, that is the quotient of width by height.",
        null,
        LayoutOptionData.Type.FLOAT,
        float.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.aspectRatio"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.bendPoints",
        "Bend Points",
        "A fixed list of bend points for the edge. This is used by the \'Fixed Layout\' algorithm to specify a pre-defined routing for an edge. The vector chain must include the source point, any bend points, and the target point, so it must have at least two points.",
        null,
        LayoutOptionData.Type.OBJECT,
        KVectorChain.class,
        EnumSet.of(LayoutOptionData.Target.EDGES),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.bendPoints"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.borderSpacing",
        "Border Spacing",
        "Spacing of the content of a parent node to its inner border. The inner border is the node border, which is given by width and height, with subtracted insets.",
        null,
        LayoutOptionData.Type.FLOAT,
        float.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.borderSpacing"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.debugMode",
        "Debug Mode",
        "Whether additional debug information shall be generated.",
        DEBUG_MODE_DEFAULT,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.debugMode"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.direction",
        "Direction",
        "Overall direction of edges: horizontal (right / left) or vertical (down / up).",
        DIRECTION_DEFAULT,
        LayoutOptionData.Type.ENUM,
        Direction.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.direction"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.edgeLabelPlacementStrategy",
        "Edge Label Placement Strategy",
        "Determines in which layer center labels of long edges should be placed.",
        EDGE_LABEL_PLACEMENT_STRATEGY_DEFAULT,
        LayoutOptionData.Type.ENUM,
        EdgeLabelPlacementStrategy.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.edgeLabelPlacementStrategy"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.edgeRouting",
        "Edge Routing",
        "What kind of edge routing style should be applied for the content of a parent node. Algorithms may also set this option to single edges in order to mark them as splines. The bend point list of edges with this option set to SPLINES must be interpreted as control points for a piecewise cubic spline.",
        EDGE_ROUTING_DEFAULT,
        LayoutOptionData.Type.ENUM,
        EdgeRouting.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.edgeRouting"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.expandNodes",
        "Expand Nodes",
        "If active, nodes are expanded to fill the area of their parent.",
        EXPAND_NODES_DEFAULT,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.expandNodes"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.hierarchyHandling",
        "Hierarchy Handling",
        "Determines whether the descendants should be layouted separately or together with their parents. If the root node is set to inherit (or not set at all), the property is assumed as SEPARATE_CHILDREN.",
        HIERARCHY_HANDLING_DEFAULT,
        LayoutOptionData.Type.ENUM,
        HierarchyHandling.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS, LayoutOptionData.Target.NODES),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.hierarchyHandling"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.interactive",
        "Interactive",
        "Whether the algorithm should be run in interactive mode for the content of a parent node. What this means exactly depends on how the specific algorithm interprets this option. Usually in the interactive mode algorithms try to modify the current layout as little as possible.",
        INTERACTIVE_DEFAULT,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.interactive"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.labelSpacing",
        "Label Spacing",
        "Determines the amount of space to be left around labels.",
        LABEL_SPACING_DEFAULT,
        LayoutOptionData.Type.FLOAT,
        float.class,
        EnumSet.of(LayoutOptionData.Target.EDGES, LayoutOptionData.Target.NODES),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.labelSpacing"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layoutHierarchy",
        "Layout Hierarchy",
        "Whether the whole hierarchy shall be layouted. If this option is not set, each hierarchy level of the graph is processed independently, possibly by different layout algorithms, beginning with the lowest level. If it is set, the algorithm is responsible to process all hierarchy levels that are contained in the associated parent node.",
        LAYOUT_HIERARCHY_DEFAULT,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.layoutHierarchy"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layoutPartition",
        "Layout Partition",
        "Partition to which the node belongs to. If \'layoutPartitions\' is true, all nodes are expected to have a partition.",
        null,
        LayoutOptionData.Type.INT,
        Integer.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS, LayoutOptionData.Target.NODES),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.partition"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layoutPartitions",
        "Layout Partitions",
        "Whether to activate partitioned layout.",
        LAYOUT_PARTITIONS_DEFAULT,
        LayoutOptionData.Type.BOOLEAN,
        Boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.layoutPartitions"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.nodeLabelInsets",
        "Node Label Insets",
        "Define insets for node labels that are placed inside of a node.",
        NODE_LABEL_INSETS_DEFAULT,
        LayoutOptionData.Type.OBJECT,
        Spacing.Insets.class,
        EnumSet.of(LayoutOptionData.Target.NODES),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.nodeLabelInset"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.nodeLabelPlacement",
        "Node Label Placement",
        "Hints for where node labels are to be placed; if empty, the node label\'s position is not modified.",
        NODE_LABEL_PLACEMENT_DEFAULT,
        LayoutOptionData.Type.ENUMSET,
        NodeLabelPlacement.class,
        EnumSet.of(LayoutOptionData.Target.NODES, LayoutOptionData.Target.LABELS),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.nodeLabelPlacement"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.portAlignment",
        "Port Alignment",
        "Defines the default port distribution for a node.",
        PORT_ALIGNMENT_DEFAULT,
        LayoutOptionData.Type.ENUM,
        PortAlignment.class,
        EnumSet.of(LayoutOptionData.Target.NODES),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.portAlignment"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.portAlignmentNorth",
        "Port Alignment (North)",
        "Defines how ports on the northern side are placed, overriding the node\'s general port alignment.",
        PORT_ALIGNMENT_NORTH_DEFAULT,
        LayoutOptionData.Type.ENUM,
        PortAlignment.class,
        EnumSet.of(LayoutOptionData.Target.NODES),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.portAlignment.north"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.portAlignmentSouth",
        "Port Alignment (South)",
        "Defines how ports on the southern side are placed, overriding the node\'s general port alignment.",
        PORT_ALIGNMENT_SOUTH_DEFAULT,
        LayoutOptionData.Type.ENUM,
        PortAlignment.class,
        EnumSet.of(LayoutOptionData.Target.NODES),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.portAlignment.south"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.portAlignmentWest",
        "Port Alignment (West)",
        "Defines how ports on the western side are placed, overriding the node\'s general port alignment.",
        PORT_ALIGNMENT_WEST_DEFAULT,
        LayoutOptionData.Type.ENUM,
        PortAlignment.class,
        EnumSet.of(LayoutOptionData.Target.NODES),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.portAlignment.west"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.portAlignmentEast",
        "Port Alignment (East)",
        "Defines how ports on the eastern side are placed, overriding the node\'s general port alignment.",
        PORT_ALIGNMENT_EAST_DEFAULT,
        LayoutOptionData.Type.ENUM,
        PortAlignment.class,
        EnumSet.of(LayoutOptionData.Target.NODES),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.portAlignment.east"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.portConstraints",
        "Port Constraints",
        "Defines constraints of the position of the ports of a node.",
        PORT_CONSTRAINTS_DEFAULT,
        LayoutOptionData.Type.ENUM,
        PortConstraints.class,
        EnumSet.of(LayoutOptionData.Target.NODES),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.portConstraints"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.portSpacing",
        "Port Spacing",
        "Spacing between ports of a given node.",
        PORT_SPACING_DEFAULT,
        LayoutOptionData.Type.FLOAT,
        float.class,
        EnumSet.of(LayoutOptionData.Target.NODES),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.portSpacing"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.position",
        "Position",
        "The position of a node, port, or label. This is used by the \'Fixed Layout\' algorithm to specify a pre-defined position.",
        null,
        LayoutOptionData.Type.OBJECT,
        KVector.class,
        EnumSet.of(LayoutOptionData.Target.NODES, LayoutOptionData.Target.PORTS, LayoutOptionData.Target.LABELS),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.position"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.priority",
        "Priority",
        "Defines the priority of an object; its meaning depends on the specific layout algorithm and the context where it is used.",
        null,
        LayoutOptionData.Type.INT,
        int.class,
        EnumSet.of(LayoutOptionData.Target.NODES, LayoutOptionData.Target.EDGES),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.priority"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.randomSeed",
        "Randomization Seed",
        "Seed used for pseudo-random number generators to control the layout algorithm. If the value is 0, the seed shall be determined pseudo-randomly (e.g. from the system time).",
        null,
        LayoutOptionData.Type.INT,
        int.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.randomSeed"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.separateConnComp",
        "Separate Connected Components",
        "Whether each connected component should be processed separately.",
        null,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.separateConnComp"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.sizeConstraint",
        "Size Constraint",
        "Constraints for determining node sizes. Each member of the set specifies something that should be taken into account when calculating node sizes. The empty set corresponds to node sizes being fixed.",
        SIZE_CONSTRAINT_DEFAULT,
        LayoutOptionData.Type.ENUMSET,
        SizeConstraint.class,
        EnumSet.of(LayoutOptionData.Target.NODES),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.sizeConstraint"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.sizeOptions",
        "Size Options",
        "Options modifying the behavior of the size constraints set on a node. Each member of the set specifies something that should be taken into account when calculating node sizes. The empty set corresponds to no further modifications.",
        SIZE_OPTIONS_DEFAULT,
        LayoutOptionData.Type.ENUMSET,
        SizeOptions.class,
        EnumSet.of(LayoutOptionData.Target.NODES),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.sizeOptions"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.spacing",
        "Spacing",
        "Overall spacing between elements. This is mostly interpreted as the minimal distance between each two nodes and should also influence the spacing between edges.",
        null,
        LayoutOptionData.Type.FLOAT,
        float.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.spacing"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.junctionPoints",
        "Junction Points",
        "This property is not used as option, but as output of the layout algorithms. It is attached to edges and determines the points where junction symbols should be drawn in order to represent hyperedges with orthogonal routing. Whether such points are computed depends on the chosen layout algorithm and edge routing style. The points are put into the vector chain with no specific order.",
        null,
        LayoutOptionData.Type.OBJECT,
        KVectorChain.class,
        EnumSet.of(LayoutOptionData.Target.EDGES),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.junctionPoints"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.additionalPortSpace",
        "Additional Port Space",
        "Additional space around the sets of ports on each node side. For each side of a node, this property can reserve additional space before and after the ports on each side. For example, a top spacing of 20 makes sure that the first port on the western and eastern side is 20 units away from the northern border.",
        null,
        LayoutOptionData.Type.OBJECT,
        Spacing.Margins.class,
        EnumSet.of(LayoutOptionData.Target.NODES),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.additionalPortSpace"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.commentBox",
        "Comment Box",
        "Whether the node should be regarded as a comment box instead of a regular node. In that case its placement should be similar to how labels are handled. Any edges incident to a comment box specify to which graph elements the comment is related.",
        COMMENT_BOX_DEFAULT,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.NODES),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.commentBox"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.edgeLabelPlacement",
        "Edge Label Placement",
        "Gives a hint on where to put edge labels.",
        EDGE_LABEL_PLACEMENT_DEFAULT,
        LayoutOptionData.Type.ENUM,
        EdgeLabelPlacement.class,
        EnumSet.of(LayoutOptionData.Target.LABELS),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.edgeLabelPlacement"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.edgeType",
        "Edge Type",
        "The type of an edge. This is usually used for UML class diagrams, where associations must be handled differently from generalizations.",
        EDGE_TYPE_DEFAULT,
        LayoutOptionData.Type.ENUM,
        EdgeType.class,
        EnumSet.of(LayoutOptionData.Target.EDGES),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.edgeType"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.fontName",
        "Font Name",
        "Font name used for a label.",
        null,
        LayoutOptionData.Type.STRING,
        String.class,
        EnumSet.of(LayoutOptionData.Target.LABELS),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.fontName"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.fontSize",
        "Font Size",
        "Font size used for a label.",
        null,
        LayoutOptionData.Type.INT,
        int.class,
        EnumSet.of(LayoutOptionData.Target.LABELS),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.fontSize"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.hypernode",
        "Hypernode",
        "Whether the node should be handled as a hypernode.",
        HYPERNODE_DEFAULT,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.NODES),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.hypernode"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.labelManager",
        "Label Manager",
        "Label managers can shorten labels upon a layout algorithm\'s request.",
        null,
        LayoutOptionData.Type.UNDEFINED,
        ILabelManager.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS, LayoutOptionData.Target.LABELS),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.labelManager"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.margins",
        "Margins",
        "Margins define additional space around the actual bounds of a graph element. For instance, ports or labels being placed on the outside of a node\'s border might introduce such a margin. The margin is used to guarantee non-overlap of other graph elements with those ports or labels.",
        MARGINS_DEFAULT,
        LayoutOptionData.Type.OBJECT,
        Spacing.Margins.class,
        EnumSet.of(LayoutOptionData.Target.NODES),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.margins"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.minHeight",
        "Minimal Height",
        "The minimal height to which a node can be reduced.",
        MIN_HEIGHT_DEFAULT,
        LayoutOptionData.Type.FLOAT,
        float.class,
        EnumSet.of(LayoutOptionData.Target.NODES),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.minHeight"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.minWidth",
        "Minimal Width",
        "The minimal width to which a node can be reduced.",
        MIN_WIDTH_DEFAULT,
        LayoutOptionData.Type.FLOAT,
        float.class,
        EnumSet.of(LayoutOptionData.Target.NODES),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.minWidth"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.noLayout",
        "No Layout",
        "No layout is done for the associated element. This is used to mark parts of a diagram to avoid their inclusion in the layout graph, or to mark parts of the layout graph to prevent layout engines from processing them. If you wish to exclude the contents of a compound node from automatic layout, while the node itself is still considered on its own layer, use the \'Fixed Layout\' algorithm for that node.",
        NO_LAYOUT_DEFAULT,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.NODES, LayoutOptionData.Target.EDGES, LayoutOptionData.Target.PORTS, LayoutOptionData.Target.LABELS),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.noLayout"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.portAnchor",
        "Port Anchor Offset",
        "The offset to the port position where connections shall be attached.",
        null,
        LayoutOptionData.Type.OBJECT,
        KVector.class,
        EnumSet.of(LayoutOptionData.Target.PORTS),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.portAnchor"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.portIndex",
        "Port Index",
        "The index of a port in the fixed order around a node. The order is assumed as clockwise, starting with the leftmost port on the top side. This option must be set if \'Port Constraints\' is set to FIXED_ORDER and no specific positions are given for the ports. Additionally, the option \'Port Side\' must be defined in this case.",
        null,
        LayoutOptionData.Type.INT,
        int.class,
        EnumSet.of(LayoutOptionData.Target.PORTS),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.portIndex"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.portLabelPlacement",
        "Port Label Placement",
        "Decides on a placement method for port labels.",
        PORT_LABEL_PLACEMENT_DEFAULT,
        LayoutOptionData.Type.ENUM,
        PortLabelPlacement.class,
        EnumSet.of(LayoutOptionData.Target.NODES),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.portLabelPlacement"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.portSide",
        "Port Side",
        "The side of a node on which a port is situated. This option must be set if \'Port Constraints\' is set to FIXED_SIDE or FIXED_ORDER and no specific positions are given for the ports.",
        PORT_SIDE_DEFAULT,
        LayoutOptionData.Type.ENUM,
        PortSide.class,
        EnumSet.of(LayoutOptionData.Target.PORTS),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.portSide"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.portOffset",
        "Port Offset",
        "The offset of ports on the node border. With a positive offset the port is moved outside of the node, while with a negative offset the port is moved towards the inside. An offset of 0 means that the port is placed directly on the node border, i.e. if the port side is north, the port\'s south border touches the nodes\'s north border; if the port side is east, the port\'s west border touches the nodes\'s east border; if the port side is south, the port\'s north border touches the node\'s south border; if the port side is west, the port\'s east border touches the node\'s west border.",
        null,
        LayoutOptionData.Type.FLOAT,
        float.class,
        EnumSet.of(LayoutOptionData.Target.PORTS),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.portOffset"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.scaleFactor",
        "Scale Factor",
        "The scaling factor to be applied to the corresponding node in recursive layout. It causes the corresponding node\'s size to be adjusted, and its ports and labels to be sized and placed accordingly after the layout of that node has been determined (and before the node itself and its siblings are arranged). The scaling is not reverted afterwards, so the resulting layout graph contains the adjusted size and position data. This option is currently not supported if \'Layout Hierarchy\' is set.",
        SCALE_FACTOR_DEFAULT,
        LayoutOptionData.Type.FLOAT,
        float.class,
        EnumSet.of(LayoutOptionData.Target.NODES),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.scaleFactor"
    ));
    registry.addDependency(
        "org.eclipse.elk.scaleFactor",
        "org.eclipse.elk.layoutHierarchy",
        SCALE_FACTOR_DEP_LAYOUT_HIERARCHY
    );
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.selfLoopInside",
        "Route Self Loop Inside",
        "Whether a self loop should be routed around a node or inside that node. The latter will make the node a compound node if it isn\'t already, and will require the layout algorithm to support compound nodes with hierarchical ports.",
        SELF_LOOP_INSIDE_DEFAULT,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.NODES, LayoutOptionData.Target.EDGES),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.selfLoopInside"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.thickness",
        "Thickness",
        "The thickness of an edge. This is a hint on the line width used to draw an edge, possibly requiring more space to be reserved for it.",
        THICKNESS_DEFAULT,
        LayoutOptionData.Type.FLOAT,
        float.class,
        EnumSet.of(LayoutOptionData.Target.EDGES),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.thickness"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.animate",
        "Animate",
        "Whether the shift from the old layout to the new computed layout shall be animated.",
        ANIMATE_DEFAULT,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.animate"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.animTimeFactor",
        "Animation Time Factor",
        "Factor for computation of animation time. The higher the value, the longer the animation time. If the value is 0, the resulting time is always equal to the minimum defined by \'Minimal Animation Time\'.",
        ANIM_TIME_FACTOR_DEFAULT,
        LayoutOptionData.Type.INT,
        int.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.animTimeFactor"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layoutAncestors",
        "Layout Ancestors",
        "Whether the hierarchy levels on the path from the selected element to the root of the diagram shall be included in the layout process.",
        LAYOUT_ANCESTORS_DEFAULT,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.layoutAncestors"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.maxAnimTime",
        "Maximal Animation Time",
        "The maximal time for animations, in milliseconds.",
        MAX_ANIM_TIME_DEFAULT,
        LayoutOptionData.Type.INT,
        int.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.maxAnimTime"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.minAnimTime",
        "Minimal Animation Time",
        "The minimal time for animations, in milliseconds.",
        MIN_ANIM_TIME_DEFAULT,
        LayoutOptionData.Type.INT,
        int.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.minAnimTime"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.progressBar",
        "Progress Bar",
        "Whether a progress bar shall be displayed during layout computations.",
        PROGRESS_BAR_DEFAULT,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.progressBar"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.zoomToFit",
        "Zoom to Fit",
        "Whether the zoom level shall be set to view the whole diagram after layout.",
        ZOOM_TO_FIT_DEFAULT,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.HIDDEN
        , "de.cau.cs.kieler.zoomToFit"
    ));
    registry.register(new LayoutCategoryData(
        "org.eclipse.elk.layered",
        "Layered",
        "The layer-based method was introduced by Sugiyama, Tagawa and Toda in 1981. It emphasizes the direction of edges by pointing as many edges as possible into the same direction. The nodes are arranged in layers, which are sometimes called \"hierarchies\", and then reordered such that the number of edge crossings is minimized. Afterwards, concrete coordinates are computed for the nodes and edge bend points."
    ));
    registry.register(new LayoutCategoryData(
        "org.eclipse.elk.orthogonal",
        "Orthogonal",
        "Orthogonal methods that follow the \"topology-shape-metrics\" approach by Batini, Nardelli and Tamassia \'86. The first phase determines the topology of the drawing by applying a planarization technique, which results in a planar representation of the graph. The orthogonal shape is computed in the second phase, which aims at minimizing the number of edge bends, and is called orthogonalization. The third phase leads to concrete coordinates for nodes and edge bend points by applying a compaction method, thus defining the metrics."
    ));
    registry.register(new LayoutCategoryData(
        "org.eclipse.elk.force",
        "Force",
        "Layout algorithms that follow physical analogies by simulating a system of attractive and repulsive forces. The first successful method of this kind was proposed by Eades in 1984."
    ));
    registry.register(new LayoutCategoryData(
        "org.eclipse.elk.circle",
        "Circle",
        "Circular layout algorithms emphasize cycles or biconnected components of a graph by arranging them in circles. This is useful if a drawing is desired where such components are clearly grouped, or where cycles are shown as prominent properties of the graph."
    ));
    registry.register(new LayoutCategoryData(
        "org.eclipse.elk.tree",
        "Tree",
        "Specialized layout methods for trees, i.e. acyclic graphs. The regular structure of graphs that have no undirected cycles can be emphasized using an algorithm of this type."
    ));
    registry.register(new LayoutAlgorithmData(
        "org.eclipse.elk.fixed",
        "Fixed Layout",
        "Keeps the current layout as it is, without any automatic modification. Optional coordinates can be given for nodes and edge bend points.",
        new AlgorithmFactory(FixedLayoutProvider.class, ""),
        null,
        "ELK",
        null,
        null
    ));
    registry.addOptionSupport(
        "org.eclipse.elk.fixed",
        "org.eclipse.elk.position",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.fixed",
        "org.eclipse.elk.bendPoints",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.fixed",
        "org.eclipse.elk.borderSpacing",
        FIXED_SUP_BORDER_SPACING
    );
    registry.register(new LayoutAlgorithmData(
        "org.eclipse.elk.box",
        "Box Layout",
        "Algorithm for packing of unconnected boxes, i.e. graphs without edges.",
        new AlgorithmFactory(BoxLayoutProvider.class, ""),
        null,
        "ELK",
        "images/box_layout.png",
        null
    ));
    registry.addOptionSupport(
        "org.eclipse.elk.box",
        "org.eclipse.elk.spacing",
        BOX_SUP_SPACING
    );
    registry.addOptionSupport(
        "org.eclipse.elk.box",
        "org.eclipse.elk.borderSpacing",
        BOX_SUP_BORDER_SPACING
    );
    registry.addOptionSupport(
        "org.eclipse.elk.box",
        "org.eclipse.elk.priority",
        BOX_SUP_PRIORITY
    );
    registry.addOptionSupport(
        "org.eclipse.elk.box",
        "org.eclipse.elk.expandNodes",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.box",
        "org.eclipse.elk.sizeConstraint",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.box",
        "org.eclipse.elk.sizeOptions",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.box",
        "org.eclipse.elk.aspectRatio",
        BOX_SUP_ASPECT_RATIO
    );
    registry.addOptionSupport(
        "org.eclipse.elk.box",
        "org.eclipse.elk.interactive",
        null
    );
    registry.register(new LayoutAlgorithmData(
        "org.eclipse.elk.random",
        "Randomizer",
        "Distributes the nodes randomly on the plane, leading to very obfuscating layouts. Can be useful to demonstrate the power of \"real\" layout algorithms.",
        new AlgorithmFactory(RandomLayoutProvider.class, ""),
        null,
        "ELK",
        "images/random.png",
        null
    ));
    registry.addOptionSupport(
        "org.eclipse.elk.random",
        "org.eclipse.elk.randomSeed",
        RANDOM_SUP_RANDOM_SEED
    );
    registry.addOptionSupport(
        "org.eclipse.elk.random",
        "org.eclipse.elk.spacing",
        RANDOM_SUP_SPACING
    );
    registry.addOptionSupport(
        "org.eclipse.elk.random",
        "org.eclipse.elk.borderSpacing",
        RANDOM_SUP_BORDER_SPACING
    );
    registry.addOptionSupport(
        "org.eclipse.elk.random",
        "org.eclipse.elk.aspectRatio",
        RANDOM_SUP_ASPECT_RATIO
    );
  }
}
