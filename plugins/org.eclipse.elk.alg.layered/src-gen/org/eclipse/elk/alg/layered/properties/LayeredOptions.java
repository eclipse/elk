/**
 * Declarations for the ELK Layered layout algorithm.
 */
package org.eclipse.elk.alg.layered.properties;

import java.util.EnumSet;
import org.eclipse.elk.alg.layered.LayeredLayoutProvider;
import org.eclipse.elk.alg.layered.intermediate.NodePromotionStrategy;
import org.eclipse.elk.alg.layered.intermediate.compaction.ConstraintCalculationStrategy;
import org.eclipse.elk.alg.layered.intermediate.compaction.GraphCompactionStrategy;
import org.eclipse.elk.alg.layered.p1cycles.CycleBreakingStrategy;
import org.eclipse.elk.alg.layered.p2layers.LayeringStrategy;
import org.eclipse.elk.alg.layered.p3order.CrossingMinimizationStrategy;
import org.eclipse.elk.alg.layered.p4nodes.NodePlacementStrategy;
import org.eclipse.elk.alg.layered.p4nodes.bk.EdgeStraighteningStrategy;
import org.eclipse.elk.alg.layered.properties.ContentAlignment;
import org.eclipse.elk.alg.layered.properties.EdgeLabelSideSelection;
import org.eclipse.elk.alg.layered.properties.FixedAlignment;
import org.eclipse.elk.alg.layered.properties.GreedySwitchType;
import org.eclipse.elk.alg.layered.properties.InteractiveReferencePoint;
import org.eclipse.elk.alg.layered.properties.LayerConstraint;
import org.eclipse.elk.alg.layered.properties.LayeredMetaDataProvider;
import org.eclipse.elk.alg.layered.properties.SelfLoopPlacement;
import org.eclipse.elk.alg.layered.properties.WideNodesStrategy;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.Alignment;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.EdgeLabelPlacement;
import org.eclipse.elk.core.options.EdgeLabelPlacementStrategy;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.GraphFeature;
import org.eclipse.elk.core.options.HierarchyHandling;
import org.eclipse.elk.core.options.NodeLabelPlacement;
import org.eclipse.elk.core.options.PortAlignment;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortLabelPlacement;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.core.util.AlgorithmFactory;
import org.eclipse.elk.core.util.nodespacing.Spacing;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class LayeredOptions implements ILayoutMetaDataProvider {
  /**
   * Default value for {@link #BOTTOM_UP_CROSSING_MINIMIZATION}.
   */
  private final static boolean BOTTOM_UP_CROSSING_MINIMIZATION_DEFAULT = false;
  
  /**
   * Process all hierarchical graphs recursively.
   */
  public final static IProperty<Boolean> BOTTOM_UP_CROSSING_MINIMIZATION = new Property<Boolean>(
            "org.eclipse.elk.layered.bottomUpCrossingMinimization",
            BOTTOM_UP_CROSSING_MINIMIZATION_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #HIERARCHICAL_SWEEPINESS}.
   */
  private final static float HIERARCHICAL_SWEEPINESS_DEFAULT = 0f;
  
  /**
   * Value between -1 (always bottom-up) and +1 (always hierarchical) sets boundary used by heuristic.
   */
  public final static IProperty<Float> HIERARCHICAL_SWEEPINESS = new Property<Float>(
            "org.eclipse.elk.layered.hierarchicalSweepiness",
            HIERARCHICAL_SWEEPINESS_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #CONTENT_ALIGNMENT}.
   */
  private final static EnumSet<ContentAlignment> CONTENT_ALIGNMENT_DEFAULT = EnumSet.<ContentAlignment>noneOf(ContentAlignment.class);
  
  /**
   * Specifies how the content of compound nodes is to be aligned, e.g. top-left.
   */
  public final static IProperty<EnumSet<ContentAlignment>> CONTENT_ALIGNMENT = new Property<EnumSet<ContentAlignment>>(
            "org.eclipse.elk.layered.contentAlignment",
            CONTENT_ALIGNMENT_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #EDGE_CENTER_LABEL_PLACEMENT_STRATEGY}.
   */
  private final static EdgeLabelPlacementStrategy EDGE_CENTER_LABEL_PLACEMENT_STRATEGY_DEFAULT = EdgeLabelPlacementStrategy.CENTER;
  
  /**
   * Determines in which layer center labels of long edges should be placed.
   */
  public final static IProperty<EdgeLabelPlacementStrategy> EDGE_CENTER_LABEL_PLACEMENT_STRATEGY = new Property<EdgeLabelPlacementStrategy>(
            "org.eclipse.elk.layered.edgeCenterLabelPlacementStrategy",
            EDGE_CENTER_LABEL_PLACEMENT_STRATEGY_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #EDGE_LABEL_SIDE_SELECTION}.
   */
  private final static EdgeLabelSideSelection EDGE_LABEL_SIDE_SELECTION_DEFAULT = EdgeLabelSideSelection.ALWAYS_DOWN;
  
  /**
   * Method to decide on edge label sides.
   */
  public final static IProperty<EdgeLabelSideSelection> EDGE_LABEL_SIDE_SELECTION = new Property<EdgeLabelSideSelection>(
            "org.eclipse.elk.layered.edgeLabelSideSelection",
            EDGE_LABEL_SIDE_SELECTION_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #FEEDBACK_EDGES}.
   */
  private final static boolean FEEDBACK_EDGES_DEFAULT = false;
  
  /**
   * Whether feedback edges should be highlighted by routing around the nodes.
   */
  public final static IProperty<Boolean> FEEDBACK_EDGES = new Property<Boolean>(
            "org.eclipse.elk.layered.feedbackEdges",
            FEEDBACK_EDGES_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #INTERACTIVE_REFERENCE_POINT}.
   */
  private final static InteractiveReferencePoint INTERACTIVE_REFERENCE_POINT_DEFAULT = InteractiveReferencePoint.CENTER;
  
  /**
   * Determines which point of a node is considered by interactive layout phases.
   */
  public final static IProperty<InteractiveReferencePoint> INTERACTIVE_REFERENCE_POINT = new Property<InteractiveReferencePoint>(
            "org.eclipse.elk.layered.interactiveReferencePoint",
            INTERACTIVE_REFERENCE_POINT_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #MERGE_EDGES}.
   */
  private final static boolean MERGE_EDGES_DEFAULT = false;
  
  /**
   * Edges that have no ports are merged so they touch the connected nodes at the same points.
   * When this option is disabled, one port is created for each edge directly connected to a
   * node. When it is enabled, all such incoming edges share an input port, and all outgoing
   * edges share an output port.
   */
  public final static IProperty<Boolean> MERGE_EDGES = new Property<Boolean>(
            "org.eclipse.elk.layered.mergeEdges",
            MERGE_EDGES_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #MERGE_HIERARCHY_EDGES}.
   */
  private final static boolean MERGE_HIERARCHY_EDGES_DEFAULT = true;
  
  /**
   * If hierarchical layout is active, hierarchy-crossing edges use as few hierarchical ports
   * as possible. They are broken by the algorithm, with hierarchical ports inserted as
   * required. Usually, one such port is created for each edge at each hierarchy crossing point.
   * With this option set to true, we try to create as few hierarchical ports as possible in
   * the process. In particular, all edges that form a hyperedge can share a port.
   */
  private final static float SPACING_NODE_DEFAULT = 20;
  
  /**
   * Property constant to access Node Spacing from within the layout algorithm code.
   */
  public final static IProperty<Float> SPACING_NODE = new Property<Float>(
                                CoreOptions.SPACING_NODE,
                                SPACING_NODE_DEFAULT);
  
  /**
   * Default value for {@link #SPACING_BORDER} with algorithm "ELK Layered".
   */
  private final static float SPACING_BORDER_DEFAULT = 12;
  
  /**
   * Property constant to access Border Spacing from within the layout algorithm code.
   */
  public final static IProperty<Float> SPACING_BORDER = new Property<Float>(
                                CoreOptions.SPACING_BORDER,
                                SPACING_BORDER_DEFAULT);
  
  /**
   * Property constant to access Port Spacing from within the layout algorithm code.
   */
  public final static IProperty<Float> SPACING_PORT = CoreOptions.SPACING_PORT;
  
  /**
   * Property constant to access Label Spacing from within the layout algorithm code.
   */
  public final static IProperty<Float> SPACING_LABEL = CoreOptions.SPACING_LABEL;
  
  /**
   * Default value for {@link #PRIORITY} with algorithm "ELK Layered".
   */
  private final static int PRIORITY_DEFAULT = 0;
  
  /**
   * Property constant to access Priority from within the layout algorithm code.
   */
  public final static IProperty<Integer> PRIORITY = new Property<Integer>(
                                CoreOptions.PRIORITY,
                                PRIORITY_DEFAULT);
  
  /**
   * Default value for {@link #EDGE_ROUTING} with algorithm "ELK Layered".
   */
  private final static EdgeRouting EDGE_ROUTING_DEFAULT = EdgeRouting.ORTHOGONAL;
  
  /**
   * Property constant to access Edge Routing from within the layout algorithm code.
   */
  public final static IProperty<EdgeRouting> EDGE_ROUTING = new Property<EdgeRouting>(
                                CoreOptions.EDGE_ROUTING,
                                EDGE_ROUTING_DEFAULT);
  
  /**
   * Default value for {@link #PORT_BORDER_OFFSET} with algorithm "ELK Layered".
   */
  private final static float PORT_BORDER_OFFSET_DEFAULT = 0;
  
  /**
   * Property constant to access Port Border Offset from within the layout algorithm code.
   */
  public final static IProperty<Float> PORT_BORDER_OFFSET = new Property<Float>(
                                CoreOptions.PORT_BORDER_OFFSET,
                                PORT_BORDER_OFFSET_DEFAULT);
  
  /**
   * Default value for {@link #RANDOM_SEED} with algorithm "ELK Layered".
   */
  private final static int RANDOM_SEED_DEFAULT = 1;
  
  /**
   * Property constant to access Randomization Seed from within the layout algorithm code.
   */
  public final static IProperty<Integer> RANDOM_SEED = new Property<Integer>(
                                CoreOptions.RANDOM_SEED,
                                RANDOM_SEED_DEFAULT);
  
  /**
   * Default value for {@link #ASPECT_RATIO} with algorithm "ELK Layered".
   */
  private final static float ASPECT_RATIO_DEFAULT = 1.6f;
  
  /**
   * Property constant to access Aspect Ratio from within the layout algorithm code.
   */
  public final static IProperty<Float> ASPECT_RATIO = new Property<Float>(
                                CoreOptions.ASPECT_RATIO,
                                ASPECT_RATIO_DEFAULT);
  
  /**
   * Property constant to access No Layout from within the layout algorithm code.
   */
  public final static IProperty<Boolean> NO_LAYOUT = CoreOptions.NO_LAYOUT;
  
  /**
   * Property constant to access Port Constraints from within the layout algorithm code.
   */
  public final static IProperty<PortConstraints> PORT_CONSTRAINTS = CoreOptions.PORT_CONSTRAINTS;
  
  /**
   * Property constant to access Port Side from within the layout algorithm code.
   */
  public final static IProperty<PortSide> PORT_SIDE = CoreOptions.PORT_SIDE;
  
  /**
   * Property constant to access Debug Mode from within the layout algorithm code.
   */
  public final static IProperty<Boolean> DEBUG_MODE = CoreOptions.DEBUG_MODE;
  
  /**
   * Property constant to access Alignment from within the layout algorithm code.
   */
  public final static IProperty<Alignment> ALIGNMENT = CoreOptions.ALIGNMENT;
  
  /**
   * Property constant to access Layout Hierarchy from within the layout algorithm code.
   */
  public final static IProperty<Boolean> LAYOUT_HIERARCHY = CoreOptions.LAYOUT_HIERARCHY;
  
  /**
   * Property constant to access Hierarchy Handling from within the layout algorithm code.
   */
  public final static IProperty<HierarchyHandling> HIERARCHY_HANDLING = CoreOptions.HIERARCHY_HANDLING;
  
  /**
   * Default value for {@link #SEPARATE_CONNECTED_COMPONENTS} with algorithm "ELK Layered".
   */
  private final static boolean SEPARATE_CONNECTED_COMPONENTS_DEFAULT = true;
  
  /**
   * Property constant to access Separate Connected Components from within the layout algorithm code.
   */
  public final static IProperty<Boolean> SEPARATE_CONNECTED_COMPONENTS = new Property<Boolean>(
                                CoreOptions.SEPARATE_CONNECTED_COMPONENTS,
                                SEPARATE_CONNECTED_COMPONENTS_DEFAULT);
  
  /**
   * Property constant to access Activate Inside Self Loops from within the layout algorithm code.
   */
  public final static IProperty<Boolean> INSIDE_SELF_LOOPS_ACTIVATE = CoreOptions.INSIDE_SELF_LOOPS_ACTIVATE;
  
  /**
   * Property constant to access Inside Self Loop from within the layout algorithm code.
   */
  public final static IProperty<Boolean> INSIDE_SELF_LOOPS_YO = CoreOptions.INSIDE_SELF_LOOPS_YO;
  
  /**
   * Property constant to access Node Size Constraints from within the layout algorithm code.
   */
  public final static IProperty<EnumSet<SizeConstraint>> NODE_SIZE_CONSTRAINTS = CoreOptions.NODE_SIZE_CONSTRAINTS;
  
  /**
   * Property constant to access Node Size Options from within the layout algorithm code.
   */
  public final static IProperty<EnumSet<SizeOptions>> NODE_SIZE_OPTIONS = CoreOptions.NODE_SIZE_OPTIONS;
  
  /**
   * Default value for {@link #DIRECTION} with algorithm "ELK Layered".
   */
  private final static Direction DIRECTION_DEFAULT = Direction.UNDEFINED;
  
  /**
   * Property constant to access Direction from within the layout algorithm code.
   */
  public final static IProperty<Direction> DIRECTION = new Property<Direction>(
                                CoreOptions.DIRECTION,
                                DIRECTION_DEFAULT);
  
  /**
   * Property constant to access Node Label Placement from within the layout algorithm code.
   */
  public final static IProperty<EnumSet<NodeLabelPlacement>> NODE_LABELS_PLACEMENT = CoreOptions.NODE_LABELS_PLACEMENT;
  
  /**
   * Property constant to access Port Label Placement from within the layout algorithm code.
   */
  public final static IProperty<PortLabelPlacement> PORT_LABELS_PLACEMENT = CoreOptions.PORT_LABELS_PLACEMENT;
  
  /**
   * Default value for {@link #PORT_ALIGNMENT_BASIC} with algorithm "ELK Layered".
   */
  private final static PortAlignment PORT_ALIGNMENT_BASIC_DEFAULT = PortAlignment.JUSTIFIED;
  
  /**
   * Property constant to access Port Alignment from within the layout algorithm code.
   */
  public final static IProperty<PortAlignment> PORT_ALIGNMENT_BASIC = new Property<PortAlignment>(
                                CoreOptions.PORT_ALIGNMENT_BASIC,
                                PORT_ALIGNMENT_BASIC_DEFAULT);
  
  /**
   * Property constant to access Port Alignment (North) from within the layout algorithm code.
   */
  public final static IProperty<PortAlignment> PORT_ALIGNMENT_NORTH = CoreOptions.PORT_ALIGNMENT_NORTH;
  
  /**
   * Property constant to access Port Alignment (South) from within the layout algorithm code.
   */
  public final static IProperty<PortAlignment> PORT_ALIGNMENT_SOUTH = CoreOptions.PORT_ALIGNMENT_SOUTH;
  
  /**
   * Property constant to access Port Alignment (West) from within the layout algorithm code.
   */
  public final static IProperty<PortAlignment> PORT_ALIGNMENT_WEST = CoreOptions.PORT_ALIGNMENT_WEST;
  
  /**
   * Property constant to access Port Alignment (East) from within the layout algorithm code.
   */
  public final static IProperty<PortAlignment> PORT_ALIGNMENT_EAST = CoreOptions.PORT_ALIGNMENT_EAST;
  
  /**
   * Property constant to access Edge Node Spacing Factor from within the layout algorithm code.
   */
  public final static IProperty<Float> SPACING_EDGE_NODE_SPACING_FACTOR = LayeredMetaDataProvider.SPACING_EDGE_NODE_SPACING_FACTOR;
  
  /**
   * Property constant to access Edge Spacing Factor from within the layout algorithm code.
   */
  public final static IProperty<Float> SPACING_EDGE_SPACING_FACTOR = LayeredMetaDataProvider.SPACING_EDGE_SPACING_FACTOR;
  
  /**
   * Property constant to access Add Unnecessary Bendpoints from within the layout algorithm code.
   */
  public final static IProperty<Boolean> UNNECESSARY_BENDPOINTS = LayeredMetaDataProvider.UNNECESSARY_BENDPOINTS;
  
  /**
   * Property constant to access Node Layering Strategy from within the layout algorithm code.
   */
  public final static IProperty<LayeringStrategy> LAYERING_STRATEGY = LayeredMetaDataProvider.LAYERING_STRATEGY;
  
  /**
   * Property constant to access Node Promotion Strategy from within the layout algorithm code.
   */
  public final static IProperty<NodePromotionStrategy> LAYERING_NODE_PROMOTION_STRATEGY = LayeredMetaDataProvider.LAYERING_NODE_PROMOTION_STRATEGY;
  
  /**
   * Property constant to access Thoroughness from within the layout algorithm code.
   */
  public final static IProperty<Integer> THOROUGHNESS = LayeredMetaDataProvider.THOROUGHNESS;
  
  /**
   * Property constant to access Layer Constraint from within the layout algorithm code.
   */
  public final static IProperty<LayerConstraint> LAYERING_LAYER_CONSTRAINT = LayeredMetaDataProvider.LAYERING_LAYER_CONSTRAINT;
  
  /**
   * Property constant to access Cycle Breaking Strategy from within the layout algorithm code.
   */
  public final static IProperty<CycleBreakingStrategy> CYCLE_BREAKING_STRATEGY = LayeredMetaDataProvider.CYCLE_BREAKING_STRATEGY;
  
  /**
   * Property constant to access Crossing Minimization Strategy from within the layout algorithm code.
   */
  public final static IProperty<CrossingMinimizationStrategy> CROSSING_MINIMIZATION_STRATEGY = LayeredMetaDataProvider.CROSSING_MINIMIZATION_STRATEGY;
  
  /**
   * Property constant to access Greedy Switch Crossing Minimization from within the layout algorithm code.
   */
  public final static IProperty<GreedySwitchType> CROSSING_MINIMIZATION_GREEDY_SWITCH = LayeredMetaDataProvider.CROSSING_MINIMIZATION_GREEDY_SWITCH;
  
  /**
   * Property constant to access Semi-Interactive Crossing Minimization from within the layout algorithm code.
   */
  public final static IProperty<Boolean> CROSSING_MINIMIZATION_SEMI_INTERACTIVE = LayeredMetaDataProvider.CROSSING_MINIMIZATION_SEMI_INTERACTIVE;
  
  /**
   * Property constant to access Merge Edges from within the layout algorithm code.
   */
  public final static IProperty<Boolean> MERGE_EDGES = LayeredMetaDataProvider.MERGE_EDGES;
  
  /**
   * Property constant to access Merge Hierarchy-Crossing Edges from within the layout algorithm code.
   */
  public final static IProperty<Boolean> MERGE_HIERARCHY_EDGES = LayeredMetaDataProvider.MERGE_HIERARCHY_EDGES;
  
  /**
   * Property constant to access Interactive Reference Point from within the layout algorithm code.
   */
  public final static IProperty<InteractiveReferencePoint> INTERACTIVE_REFERENCE_POINT = LayeredMetaDataProvider.INTERACTIVE_REFERENCE_POINT;
  
  /**
   * Property constant to access Node Placement Strategy from within the layout algorithm code.
   */
  public final static IProperty<NodePlacementStrategy> NODE_PLACEMENT_STRATEGY = LayeredMetaDataProvider.NODE_PLACEMENT_STRATEGY;
  
  /**
   * Property constant to access Fixed Alignment from within the layout algorithm code.
   */
  public final static IProperty<FixedAlignment> NODE_PLACEMENT_BK_FIXED_ALIGNMENT = LayeredMetaDataProvider.NODE_PLACEMENT_BK_FIXED_ALIGNMENT;
  
  /**
   * Property constant to access Edge Label Side Selection from within the layout algorithm code.
   */
  public final static IProperty<EdgeLabelSideSelection> EDGE_LABEL_SIDE_SELECTION = LayeredMetaDataProvider.EDGE_LABEL_SIDE_SELECTION;
  
  /**
   * Property constant to access Feedback Edges from within the layout algorithm code.
   */
  public final static IProperty<Boolean> FEEDBACK_EDGES = LayeredMetaDataProvider.FEEDBACK_EDGES;
  
  /**
   * Property constant to access In-layer Spacing Factor from within the layout algorithm code.
   */
  public final static IProperty<Float> SPACING_IN_LAYER_SPACING_FACTOR = LayeredMetaDataProvider.SPACING_IN_LAYER_SPACING_FACTOR;
  
  /**
   * Property constant to access Wide Nodes on Multiple Layers from within the layout algorithm code.
   */
  public final static IProperty<WideNodesStrategy> LAYERING_WIDE_NODES_ON_MULTIPLE_LAYERS = LayeredMetaDataProvider.LAYERING_WIDE_NODES_ON_MULTIPLE_LAYERS;
  
  /**
   * Property constant to access Linear Segments Deflection Dampening from within the layout algorithm code.
   */
  public final static IProperty<Float> NODE_PLACEMENT_LINEAR_SEGMENTS_DEFLECTION_DAMPENING = LayeredMetaDataProvider.NODE_PLACEMENT_LINEAR_SEGMENTS_DEFLECTION_DAMPENING;
  
  /**
   * Property constant to access Spline Self-Loop Placement from within the layout algorithm code.
   */
  public final static IProperty<SelfLoopPlacement> EDGE_ROUTING_SELF_LOOP_PLACEMENT = LayeredMetaDataProvider.EDGE_ROUTING_SELF_LOOP_PLACEMENT;
  
  /**
   * Property constant to access Content Alignment from within the layout algorithm code.
   */
  public final static IProperty<EnumSet<ContentAlignment>> CONTENT_ALIGNMENT = LayeredMetaDataProvider.CONTENT_ALIGNMENT;
  
  /**
   * Property constant to access Edge Straightening from within the layout algorithm code.
   */
  public final static IProperty<EdgeStraighteningStrategy> NODE_PLACEMENT_BK_EDGE_STRAIGHTENING = LayeredMetaDataProvider.NODE_PLACEMENT_BK_EDGE_STRAIGHTENING;
  
  /**
   * Property constant to access Post Compaction Strategy from within the layout algorithm code.
   */
  public final static IProperty<GraphCompactionStrategy> COMPACTION_POST_COMPACTION_STRATEGY = LayeredMetaDataProvider.COMPACTION_POST_COMPACTION_STRATEGY;
  
  /**
   * Property constant to access Post Compaction Constraint Calculation from within the layout algorithm code.
   */
  public final static IProperty<ConstraintCalculationStrategy> COMPACTION_POST_COMPACTION_CONSTRAINTS = LayeredMetaDataProvider.COMPACTION_POST_COMPACTION_CONSTRAINTS;
  
  /**
   * Property constant to access Connected Components Compaction from within the layout algorithm code.
   */
  public final static IProperty<Boolean> COMPACTION_CONNECTED_COMPONENTS = LayeredMetaDataProvider.COMPACTION_CONNECTED_COMPONENTS;
  
  /**
   * Property constant to access High Degree Node Treatment from within the layout algorithm code.
   */
  public final static IProperty<Boolean> HIGH_DEGREE_NODES_TREATMENT = LayeredMetaDataProvider.HIGH_DEGREE_NODES_TREATMENT;
  
  /**
   * Property constant to access High Degree Node Threshold from within the layout algorithm code.
   */
  public final static IProperty<Integer> HIGH_DEGREE_NODES_THRESHOLD = LayeredMetaDataProvider.HIGH_DEGREE_NODES_THRESHOLD;
  
  /**
   * Property constant to access High Degree Node Maximum Tree Height from within the layout algorithm code.
   */
  public final static IProperty<Integer> HIGH_DEGREE_NODES_TREE_HEIGHT = LayeredMetaDataProvider.HIGH_DEGREE_NODES_TREE_HEIGHT;
  
  /**
   * Property constant to access Node Size Minimum from within the layout algorithm code.
   */
  public final static IProperty<KVector> NODE_SIZE_MINIMUM = CoreOptions.NODE_SIZE_MINIMUM;
  
  /**
   * Property constant to access Minimum Width from within the layout algorithm code.
   */
  public final static IProperty<Float> NODE_SIZE_MIN_WIDTH = CoreOptions.NODE_SIZE_MIN_WIDTH;
  
  /**
   * Property constant to access Minimum Height from within the layout algorithm code.
   */
  public final static IProperty<Float> NODE_SIZE_MIN_HEIGHT = CoreOptions.NODE_SIZE_MIN_HEIGHT;
  
  /**
   * Property constant to access Junction Points from within the layout algorithm code.
   */
  public final static IProperty<KVectorChain> JUNCTION_POINTS = CoreOptions.JUNCTION_POINTS;
  
  /**
   * Property constant to access Edge Thickness from within the layout algorithm code.
   */
  public final static IProperty<Float> EDGE_THICKNESS = CoreOptions.EDGE_THICKNESS;
  
  /**
   * Property constant to access Edge Label Placement from within the layout algorithm code.
   */
  public final static IProperty<EdgeLabelPlacement> EDGE_LABELS_PLACEMENT = CoreOptions.EDGE_LABELS_PLACEMENT;
  
  /**
   * Property constant to access Port Index from within the layout algorithm code.
   */
  public final static IProperty<Integer> PORT_INDEX = CoreOptions.PORT_INDEX;
  
  /**
   * Property constant to access Comment Box from within the layout algorithm code.
   */
  public final static IProperty<Boolean> COMMENT_BOX = CoreOptions.COMMENT_BOX;
  
  /**
   * Property constant to access Hypernode from within the layout algorithm code.
   */
  public final static IProperty<Boolean> HYPERNODE = CoreOptions.HYPERNODE;
  
  /**
   * Property constant to access Port Anchor Offset from within the layout algorithm code.
   */
  public final static IProperty<KVector> PORT_ANCHOR = CoreOptions.PORT_ANCHOR;
  
  /**
   * Property constant to access Layout Partitioning from within the layout algorithm code.
   */
  public final static IProperty<Boolean> PARTITIONING_ACTIVATE = CoreOptions.PARTITIONING_ACTIVATE;
  
  /**
   * Property constant to access Layout Partition from within the layout algorithm code.
   */
  public final static IProperty<Integer> PARTITIONING_PARTITION = CoreOptions.PARTITIONING_PARTITION;
  
  /**
   * Property constant to access Distribute Nodes (Deprecated) from within the layout algorithm code.
   */
  public final static IProperty<Boolean> LAYERING_DISTRIBUTE_NODES = LayeredMetaDataProvider.LAYERING_DISTRIBUTE_NODES;
  
  /**
   * Property constant to access Upper Bound On Width [MinWidth Layerer] from within the layout algorithm code.
   */
  public final static IProperty<Integer> LAYERING_MIN_WIDTH_UPPER_BOUND_ON_WIDTH = LayeredMetaDataProvider.LAYERING_MIN_WIDTH_UPPER_BOUND_ON_WIDTH;
  
  /**
   * Property constant to access Upper Layer Estimation Scaling Factor [MinWidth Layerer] from within the layout algorithm code.
   */
  public final static IProperty<Integer> LAYERING_MIN_WIDTH_UPPER_LAYER_ESTIMATION_SCALING_FACTOR = LayeredMetaDataProvider.LAYERING_MIN_WIDTH_UPPER_LAYER_ESTIMATION_SCALING_FACTOR;
  
  /**
   * Property constant to access Sausage Folding from within the layout algorithm code.
   */
  public final static IProperty<Boolean> SAUSAGE_FOLDING = LayeredMetaDataProvider.SAUSAGE_FOLDING;
  
  /**
   * Property constant to access Position from within the layout algorithm code.
   */
  public final static IProperty<KVector> POSITION = CoreOptions.POSITION;
  
  /**
   * Property constant to access North or South Port from within the layout algorithm code.
   */
  public final static IProperty<Boolean> NORTH_OR_SOUTH_PORT = LayeredMetaDataProvider.NORTH_OR_SOUTH_PORT;
  
  /**
   * Property constant to access Max Node Promotion Iterations from within the layout algorithm code.
   */
  public final static IProperty<Integer> LAYERING_NODE_PROMOTION_MAX_ITERATIONS = LayeredMetaDataProvider.LAYERING_NODE_PROMOTION_MAX_ITERATIONS;
  
  /**
   * Property constant to access Edge Label Placement Strategy from within the layout algorithm code.
   */
  public final static IProperty<EdgeLabelPlacementStrategy> EDGE_CENTER_LABEL_PLACEMENT_STRATEGY = LayeredMetaDataProvider.EDGE_CENTER_LABEL_PLACEMENT_STRATEGY;
  
  /**
   * Property constant to access Margins from within the layout algorithm code.
   */
  public final static IProperty<Spacing.Margins> MARGINS = CoreOptions.MARGINS;
  
  public void apply(final ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.bottomUpCrossingMinimization",
        "",
        "Force bottom-up sweep",
        "Process all hierarchical graphs recursively.",
        BOTTOM_UP_CROSSING_MINIMIZATION_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.hierarchicalSweepiness",
        "",
        "Hierarchical Sweepiness",
        "Value between -1 (always bottom-up) and +1 (always hierarchical) sets boundary used by heuristic.",
        HIERARCHICAL_SWEEPINESS_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.FLOAT,
        float.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.contentAlignment",
        "",
        "Content Alignment",
        "Specifies how the content of compound nodes is to be aligned, e.g. top-left.",
        CONTENT_ALIGNMENT_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.ENUMSET,
        ContentAlignment.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.contentAlignment"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.edgeCenterLabelPlacementStrategy",
        "",
        "Edge Label Placement Strategy",
        "Determines in which layer center labels of long edges should be placed.",
        EDGE_CENTER_LABEL_PLACEMENT_STRATEGY_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.ENUM,
        EdgeLabelPlacementStrategy.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.edgeLabelPlacementStrategy"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.edgeLabelSideSelection",
        "",
        "Edge Label Side Selection",
        "Method to decide on edge label sides.",
        EDGE_LABEL_SIDE_SELECTION_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.ENUM,
        EdgeLabelSideSelection.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.klay.layered.edgeLabelSideSelection"
    ));
    registry.addDependency(
        "org.eclipse.elk.layered.edgeLabelSideSelection",
        "org.eclipse.elk.edgeRouting",
        EDGE_LABEL_SIDE_SELECTION_DEP_EDGE_ROUTING
    );
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.feedbackEdges",
        "",
        "Feedback Edges",
        "Whether feedback edges should be highlighted by routing around the nodes.",
        FEEDBACK_EDGES_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.feedBackEdges"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.interactiveReferencePoint",
        "",
        "Interactive Reference Point",
        "Determines which point of a node is considered by interactive layout phases.",
        INTERACTIVE_REFERENCE_POINT_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.ENUM,
        InteractiveReferencePoint.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.interactiveReferencePoint"
    ));
    registry.addDependency(
        "org.eclipse.elk.layered.interactiveReferencePoint",
        "org.eclipse.elk.layered.cycleBreaking.strategy",
        INTERACTIVE_REFERENCE_POINT_DEP_CYCLE_BREAKING_STRATEGY
    );
    registry.addDependency(
        "org.eclipse.elk.layered.interactiveReferencePoint",
        "org.eclipse.elk.layered.crossingMinimization.strategy",
        INTERACTIVE_REFERENCE_POINT_DEP_CROSSING_MINIMIZATION_STRATEGY
    );
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.mergeEdges",
        "",
        "Merge Edges",
        "Edges that have no ports are merged so they touch the connected nodes at the same points. When this option is disabled, one port is created for each edge directly connected to a node. When it is enabled, all such incoming edges share an input port, and all outgoing edges share an output port.",
        MERGE_EDGES_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.mergeEdges"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.mergeHierarchyEdges",
        "",
        "Merge Hierarchy-Crossing Edges",
        "If hierarchical layout is active, hierarchy-crossing edges use as few hierarchical ports as possible. They are broken by the algorithm, with hierarchical ports inserted as required. Usually, one such port is created for each edge at each hierarchy crossing point. With this option set to true, we try to create as few hierarchical ports as possible in the process. In particular, all edges that form a hyperedge can share a port.",
        MERGE_HIERARCHY_EDGES_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.mergeHierarchyEdges"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.sausageFolding",
        "",
        "Sausage Folding",
        "Whether long sausages should be folded up nice and tight.",
        SAUSAGE_FOLDING_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.sausageFolding"
    ));
    registry.addDependency(
        "org.eclipse.elk.layered.sausageFolding",
        "org.eclipse.elk.layered.layering.strategy",
        SAUSAGE_FOLDING_DEP_LAYERING_STRATEGY
    );
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.thoroughness",
        "",
        "Thoroughness",
        "How much effort should be spent to produce a nice layout.",
        THOROUGHNESS_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.INT,
        int.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.thoroughness"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.unnecessaryBendpoints",
        "",
        "Add Unnecessary Bendpoints",
        "Adds bend points even if an edge does not change direction. If true, each long edge dummy will contribute a bend point to its edges and hierarchy-crossing edges will always get a bend point where they cross hierarchy boundaries. By default, bend points are only added where an edge changes direction.",
        UNNECESSARY_BENDPOINTS_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.unnecessaryBendpoints"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.northOrSouthPort",
        "",
        "North or South Port",
        "Specifies that this port can either be placed on the north side of a node or on the south side (if port constraints permit)",
        NORTH_OR_SOUTH_PORT_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PORTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.northOrSouthPort"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.cycleBreaking.strategy",
        "cycleBreaking",
        "Cycle Breaking Strategy",
        "Strategy for cycle breaking. Cycle breaking looks for cycles in the graph and determines which edges to reverse to break the cycles. Reversed edges will end up pointing to the opposite direction of regular edges (that is, reversed edges will point left if edges usually point right).",
        CYCLE_BREAKING_STRATEGY_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.ENUM,
        CycleBreakingStrategy.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.klay.layered.cycleBreaking"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.layering.strategy",
        "layering",
        "Node Layering Strategy",
        "Strategy for node layering.",
        LAYERING_STRATEGY_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.ENUM,
        LayeringStrategy.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.klay.layered.nodeLayering"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.layering.layerConstraint",
        "layering",
        "Layer Constraint",
        "Determines a constraint on the placement of the node regarding the layering.",
        LAYERING_LAYER_CONSTRAINT_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.ENUM,
        LayerConstraint.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.layerConstraint"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.layering.distributeNodes",
        "layering",
        "Distribute Nodes (Deprecated)",
        "Whether wide nodes should be distributed to several layers.",
        LAYERING_DISTRIBUTE_NODES_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.distributeNodes"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.layering.wideNodesOnMultipleLayers",
        "layering",
        "Wide Nodes on Multiple Layers",
        "Strategy to distribute wide nodes over multiple layers.",
        LAYERING_WIDE_NODES_ON_MULTIPLE_LAYERS_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.ENUM,
        WideNodesStrategy.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.wideNodesOnMultipleLayers"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.layering.minWidth.upperBoundOnWidth",
        "layering.minWidth",
        "Upper Bound On Width [MinWidth Layerer]",
        "Defines a loose upper bound on the width of the MinWidth layerer.",
        LAYERING_MIN_WIDTH_UPPER_BOUND_ON_WIDTH_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.INT,
        int.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.minWidthUpperBoundOnWidth"
    ));
    registry.addDependency(
        "org.eclipse.elk.layered.layering.minWidth.upperBoundOnWidth",
        "org.eclipse.elk.layered.layering.strategy",
        LAYERING_MIN_WIDTH_UPPER_BOUND_ON_WIDTH_DEP_LAYERING_STRATEGY
    );
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.layering.minWidth.upperLayerEstimationScalingFactor",
        "layering.minWidth",
        "Upper Layer Estimation Scaling Factor [MinWidth Layerer]",
        "Multiplied with Upper Bound On Width for defining an upper bound on the width of layers which haven&apos;t been determined yet, but whose maximum width had been (roughly) estimated by the MinWidth algorithm. Compensates for too high estimations.",
        LAYERING_MIN_WIDTH_UPPER_LAYER_ESTIMATION_SCALING_FACTOR_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.INT,
        int.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.minWidthUpperLayerEstimationScalingFactor"
    ));
    registry.addDependency(
        "org.eclipse.elk.layered.layering.minWidth.upperLayerEstimationScalingFactor",
        "org.eclipse.elk.layered.layering.strategy",
        LAYERING_MIN_WIDTH_UPPER_LAYER_ESTIMATION_SCALING_FACTOR_DEP_LAYERING_STRATEGY
    );
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.layering.nodePromotion.strategy",
        "layering.nodePromotion",
        "Node Promotion Strategy",
        "Reduces number of dummy nodes after layering phase (if possible).",
        LAYERING_NODE_PROMOTION_STRATEGY_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.ENUM,
        NodePromotionStrategy.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.nodePromotion"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.layering.nodePromotion.maxIterations",
        "layering.nodePromotion",
        "Max Node Promotion Iterations",
        "Limits the number of iterations for node promotion.",
        LAYERING_NODE_PROMOTION_MAX_ITERATIONS_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.INT,
        int.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.nodePromotionBoundary"
    ));
    registry.addDependency(
        "org.eclipse.elk.layered.layering.nodePromotion.maxIterations",
        "org.eclipse.elk.layered.layering.nodePromotion.strategy",
        null
    );
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.crossingMinimization.strategy",
        "crossingMinimization",
        "Crossing Minimization Strategy",
        "Strategy for crossing minimization.",
        CROSSING_MINIMIZATION_STRATEGY_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.ENUM,
        CrossingMinimizationStrategy.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.klay.layered.crossMin"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.crossingMinimization.greedySwitch",
        "crossingMinimization",
        "Greedy Switch Crossing Minimization",
        "Greedy Switch strategy for crossing minimization.",
        CROSSING_MINIMIZATION_GREEDY_SWITCH_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.ENUM,
        GreedySwitchType.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.greedySwitch"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.crossingMinimization.semiInteractive",
        "crossingMinimization",
        "Semi-Interactive Crossing Minimization",
        "Preserves the order of nodes within a layer but still minimizes crossings between edges connecting long edge dummies. Requires a crossing minimization strategy that is able to process \'in-layer\' constraints.",
        CROSSING_MINIMIZATION_SEMI_INTERACTIVE_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.nodePlacement.strategy",
        "nodePlacement",
        "Node Placement Strategy",
        "Strategy for node placement.",
        NODE_PLACEMENT_STRATEGY_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.ENUM,
        NodePlacementStrategy.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.klay.layered.nodePlacement"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.nodePlacement.bk.edgeStraightening",
        "nodePlacement.bk",
        "Edge Straightening",
        "Specifies whether the Brandes Koepf node placer tries to increase the number of straight edges at the expense of diagram size.",
        NODE_PLACEMENT_BK_EDGE_STRAIGHTENING_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.ENUM,
        EdgeStraighteningStrategy.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.nodeplace.compactionStrategy"
    ));
    registry.addDependency(
        "org.eclipse.elk.layered.nodePlacement.bk.edgeStraightening",
        "org.eclipse.elk.layered.nodePlacement.strategy",
        NODE_PLACEMENT_BK_EDGE_STRAIGHTENING_DEP_NODE_PLACEMENT_STRATEGY
    );
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.nodePlacement.bk.fixedAlignment",
        "nodePlacement.bk",
        "Fixed Alignment",
        "Tells the BK node placer to use a certain alignment instead of taking the optimal result.",
        NODE_PLACEMENT_BK_FIXED_ALIGNMENT_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.ENUM,
        FixedAlignment.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.fixedAlignment"
    ));
    registry.addDependency(
        "org.eclipse.elk.layered.nodePlacement.bk.fixedAlignment",
        "org.eclipse.elk.layered.nodePlacement.strategy",
        NODE_PLACEMENT_BK_FIXED_ALIGNMENT_DEP_NODE_PLACEMENT_STRATEGY
    );
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.nodePlacement.linearSegments.deflectionDampening",
        "nodePlacement.linearSegments",
        "Linear Segments Deflection Dampening",
        "Dampens the movement of nodes to keep the diagram from getting too large.",
        NODE_PLACEMENT_LINEAR_SEGMENTS_DEFLECTION_DAMPENING_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.FLOAT,
        float.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.linearSegmentsDeflectionDampening"
    ));
    registry.addDependency(
        "org.eclipse.elk.layered.nodePlacement.linearSegments.deflectionDampening",
        "org.eclipse.elk.layered.nodePlacement.strategy",
        NODE_PLACEMENT_LINEAR_SEGMENTS_DEFLECTION_DAMPENING_DEP_NODE_PLACEMENT_STRATEGY
    );
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.edgeRouting.selfLoopPlacement",
        "edgeRouting",
        "Spline Self-Loop Placement",
        null,
        EDGE_ROUTING_SELF_LOOP_PLACEMENT_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.ENUM,
        SelfLoopPlacement.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
        , "de.cau.cs.kieler.klay.layered.splines.selfLoopPlacement"
    ));
    registry.addDependency(
        "org.eclipse.elk.layered.edgeRouting.selfLoopPlacement",
        "org.eclipse.elk.edgeRouting",
        EDGE_ROUTING_SELF_LOOP_PLACEMENT_DEP_EDGE_ROUTING
    );
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.spacing.edgeNodeSpacingFactor",
        "spacing",
        "Edge Node Spacing Factor",
        "Factor by which the object spacing is multiplied to arrive at the minimal spacing between an edge and a node.",
        SPACING_EDGE_NODE_SPACING_FACTOR_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.FLOAT,
        float.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.edgeNodeSpacingFactor"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.spacing.edgeSpacingFactor",
        "spacing",
        "Edge Spacing Factor",
        "Factor by which the object spacing is multiplied to arrive at the minimal spacing between edges.",
        SPACING_EDGE_SPACING_FACTOR_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.FLOAT,
        float.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.edgeSpacingFactor"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.spacing.inLayerSpacingFactor",
        "spacing",
        "In-layer Spacing Factor",
        "Factor by which the usual spacing is multiplied to determine the in-layer spacing between objects.",
        SPACING_IN_LAYER_SPACING_FACTOR_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.FLOAT,
        float.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.inLayerSpacingFactor"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.compaction.connectedComponents",
        "compaction",
        "Connected Components Compaction",
        "Tries to further compact components (disconnected sub-graphs).",
        COMPACTION_CONNECTED_COMPONENTS_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.components.compact"
    ));
    registry.addDependency(
        "org.eclipse.elk.layered.compaction.connectedComponents",
        "org.eclipse.elk.separateConnectedComponents",
        COMPACTION_CONNECTED_COMPONENTS_DEP_SEPARATE_CONNECTED_COMPONENTS
    );
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.compaction.postCompaction.strategy",
        "compaction.postCompaction",
        "Post Compaction Strategy",
        "Specifies whether and how post-process compaction is applied.",
        COMPACTION_POST_COMPACTION_STRATEGY_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.ENUM,
        GraphCompactionStrategy.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.postCompaction"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.compaction.postCompaction.constraints",
        "compaction.postCompaction",
        "Post Compaction Constraint Calculation",
        "Specifies whether and how post-process compaction is applied.",
        COMPACTION_POST_COMPACTION_CONSTRAINTS_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.ENUM,
        ConstraintCalculationStrategy.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.postCompaction.constraints"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.highDegreeNodes.treatment",
        "highDegreeNodes",
        "High Degree Node Treatment",
        "Makes room around high degree nodes to place leafs and trees.",
        HIGH_DEGREE_NODES_TREATMENT_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.highDegreeNode.treatment"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.highDegreeNodes.threshold",
        "highDegreeNodes",
        "High Degree Node Threshold",
        "Whether a node is considered to have a high degree.",
        HIGH_DEGREE_NODES_THRESHOLD_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.INT,
        int.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.highDegreeNode.threshold"
    ));
    registry.addDependency(
        "org.eclipse.elk.layered.highDegreeNodes.threshold",
        "org.eclipse.elk.layered.highDegreeNodes.treatment",
        HIGH_DEGREE_NODES_THRESHOLD_DEP_HIGH_DEGREE_NODES_TREATMENT
    );
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.highDegreeNodes.treeHeight",
        "highDegreeNodes",
        "High Degree Node Maximum Tree Height",
        "Maximum height of a subtree connected to a high degree node to be moved to separate layers.",
        HIGH_DEGREE_NODES_TREE_HEIGHT_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.INT,
        int.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.highDegreeNode.treeHeight"
    ));
    registry.addDependency(
        "org.eclipse.elk.layered.highDegreeNodes.treeHeight",
        "org.eclipse.elk.layered.highDegreeNodes.treatment",
        HIGH_DEGREE_NODES_TREE_HEIGHT_DEP_HIGH_DEGREE_NODES_TREATMENT
    );
    registry.register(new LayoutAlgorithmData(
        "org.eclipse.elk.layered",
        "ELK Layered",
        "Layer-based algorithm provided by the Eclipse Layout Kernel. Arranges as many edges as possible into one direction by placing nodes into subsequent layers. This implementation supports different routing styles (straight, orthogonal, splines); if orthogonal routing is selected, arbitrary port constraints are respected, thus enabling the layout of block diagrams such as actor-oriented models or circuit schematics. Furthermore, full layout of compound graphs with cross-hierarchy edges is supported when the respective option is activated on the top level.",
        new AlgorithmFactory(LayeredLayoutProvider.class, ""),
        "org.eclipse.elk.layered",
        null,
        "images/layered.png",
        EnumSet.of(GraphFeature.SELF_LOOPS, GraphFeature.INSIDE_SELF_LOOPS, GraphFeature.MULTI_EDGES, GraphFeature.EDGE_LABELS, GraphFeature.PORTS, GraphFeature.COMPOUND, GraphFeature.CLUSTERS)
    ));
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.spacing.node",
        SPACING_NODE_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.spacing.border",
        SPACING_BORDER_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.spacing.port",
        SPACING_PORT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.spacing.label",
        SPACING_LABEL.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.priority",
        PRIORITY_DEFAULT
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
        "org.eclipse.elk.layoutHierarchy",
        LAYOUT_HIERARCHY.getDefault()
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
        "org.eclipse.elk.portLabels.placement",
        PORT_LABELS_PLACEMENT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.portAlignment.basic",
        PORT_ALIGNMENT_BASIC_DEFAULT
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
        "org.eclipse.elk.layered.spacing.edgeNodeSpacingFactor",
        SPACING_EDGE_NODE_SPACING_FACTOR.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.spacing.edgeSpacingFactor",
        SPACING_EDGE_SPACING_FACTOR.getDefault()
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
        "org.eclipse.elk.layered.crossingMinimization.greedySwitch",
        CROSSING_MINIMIZATION_GREEDY_SWITCH.getDefault()
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
        "org.eclipse.elk.layered.edgeLabelSideSelection",
        EDGE_LABEL_SIDE_SELECTION.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.feedbackEdges",
        FEEDBACK_EDGES.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.spacing.inLayerSpacingFactor",
        SPACING_IN_LAYER_SPACING_FACTOR.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.layering.wideNodesOnMultipleLayers",
        LAYERING_WIDE_NODES_ON_MULTIPLE_LAYERS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.nodePlacement.linearSegments.deflectionDampening",
        NODE_PLACEMENT_LINEAR_SEGMENTS_DEFLECTION_DAMPENING.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.edgeRouting.selfLoopPlacement",
        EDGE_ROUTING_SELF_LOOP_PLACEMENT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.contentAlignment",
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
        "org.eclipse.elk.nodeSize.minWidth",
        NODE_SIZE_MIN_WIDTH.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.nodeSize.minHeight",
        NODE_SIZE_MIN_HEIGHT.getDefault()
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
        "org.eclipse.elk.layered.layering.distributeNodes",
        LAYERING_DISTRIBUTE_NODES.getDefault()
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
        "org.eclipse.elk.layered.sausageFolding",
        SAUSAGE_FOLDING.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.position",
        POSITION.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.northOrSouthPort",
        NORTH_OR_SOUTH_PORT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.layering.nodePromotion.maxIterations",
        LAYERING_NODE_PROMOTION_MAX_ITERATIONS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.edgeCenterLabelPlacementStrategy",
        EDGE_CENTER_LABEL_PLACEMENT_STRATEGY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.margins",
        MARGINS.getDefault()
    );
  }
}
