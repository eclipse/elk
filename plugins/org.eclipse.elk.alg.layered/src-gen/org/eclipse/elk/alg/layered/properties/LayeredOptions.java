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
import org.eclipse.elk.alg.layered.properties.SelfLoopPlacement;
import org.eclipse.elk.alg.layered.properties.WideNodesStrategy;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.EdgeLabelPlacementStrategy;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.GraphFeature;
import org.eclipse.elk.core.options.PortAlignment;
import org.eclipse.elk.core.util.AlgorithmFactory;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * Declarations for the ELK Layered layout algorithm.
 */
@SuppressWarnings("all")
public class LayeredOptions implements ILayoutMetaDataProvider {
  /**
   * Default value for {@link #CROSS_MIN_RECURSIVE}.
   */
  private final static boolean CROSS_MIN_RECURSIVE_DEFAULT = false;
  
  /**
   * Process all hierarchical graphs recursively.
   */
  public final static IProperty<Boolean> CROSS_MIN_RECURSIVE = new Property<Boolean>(
            "org.eclipse.elk.layered.crossMinRecursive",
            CROSS_MIN_RECURSIVE_DEFAULT);
  
  /**
   * Default value for {@link #RECURSIVE_BOUNDARY}.
   */
  private final static float RECURSIVE_BOUNDARY_DEFAULT = 0.2f;
  
  /**
   * Process all hierarchical graphs recursively.
   */
  public final static IProperty<Float> RECURSIVE_BOUNDARY = new Property<Float>(
            "org.eclipse.elk.layered.recursiveBoundary",
            RECURSIVE_BOUNDARY_DEFAULT);
  
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
  public final static IProperty<Boolean> MERGE_HIERARCHY_EDGES = new Property<Boolean>(
            "org.eclipse.elk.layered.mergeHierarchyEdges",
            MERGE_HIERARCHY_EDGES_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #SAUSAGE_FOLDING}.
   */
  private final static boolean SAUSAGE_FOLDING_DEFAULT = false;
  
  /**
   * Whether long sausages should be folded up nice and tight.
   */
  public final static IProperty<Boolean> SAUSAGE_FOLDING = new Property<Boolean>(
            "org.eclipse.elk.layered.sausageFolding",
            SAUSAGE_FOLDING_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #THOROUGHNESS}.
   */
  private final static int THOROUGHNESS_DEFAULT = 7;
  
  /**
   * How much effort should be spent to produce a nice layout.
   */
  public final static IProperty<Integer> THOROUGHNESS = new Property<Integer>(
            "org.eclipse.elk.layered.thoroughness",
            THOROUGHNESS_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #UNNECESSARY_BENDPOINTS}.
   */
  private final static boolean UNNECESSARY_BENDPOINTS_DEFAULT = false;
  
  /**
   * Adds bend points even if an edge does not change direction. If true, each long edge dummy
   * will contribute a bend point to its edges and hierarchy-crossing edges will always get a
   * bend point where they cross hierarchy boundaries. By default, bend points are only added
   * where an edge changes direction.
   */
  public final static IProperty<Boolean> UNNECESSARY_BENDPOINTS = new Property<Boolean>(
            "org.eclipse.elk.layered.unnecessaryBendpoints",
            UNNECESSARY_BENDPOINTS_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #NORTH_OR_SOUTH_PORT}.
   */
  private final static boolean NORTH_OR_SOUTH_PORT_DEFAULT = false;
  
  /**
   * Specifies that this port can either be placed on the north side of a node or on the south
   * side (if port constraints permit)
   */
  public final static IProperty<Boolean> NORTH_OR_SOUTH_PORT = new Property<Boolean>(
            "org.eclipse.elk.layered.northOrSouthPort",
            NORTH_OR_SOUTH_PORT_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #CYCLE_BREAKING_STRATEGY}.
   */
  private final static CycleBreakingStrategy CYCLE_BREAKING_STRATEGY_DEFAULT = CycleBreakingStrategy.GREEDY;
  
  /**
   * Strategy for cycle breaking. Cycle breaking looks for cycles in the graph and determines
   * which edges to reverse to break the cycles. Reversed edges will end up pointing to the
   * opposite direction of regular edges (that is, reversed edges will point left if edges
   * usually point right).
   */
  public final static IProperty<CycleBreakingStrategy> CYCLE_BREAKING_STRATEGY = new Property<CycleBreakingStrategy>(
            "org.eclipse.elk.layered.cycleBreaking.strategy",
            CYCLE_BREAKING_STRATEGY_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #LAYERING_STRATEGY}.
   */
  private final static LayeringStrategy LAYERING_STRATEGY_DEFAULT = LayeringStrategy.NETWORK_SIMPLEX;
  
  /**
   * Strategy for node layering.
   */
  public final static IProperty<LayeringStrategy> LAYERING_STRATEGY = new Property<LayeringStrategy>(
            "org.eclipse.elk.layered.layering.strategy",
            LAYERING_STRATEGY_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #LAYERING_LAYER_CONSTRAINT}.
   */
  private final static LayerConstraint LAYERING_LAYER_CONSTRAINT_DEFAULT = LayerConstraint.NONE;
  
  /**
   * Determines a constraint on the placement of the node regarding the layering.
   */
  public final static IProperty<LayerConstraint> LAYERING_LAYER_CONSTRAINT = new Property<LayerConstraint>(
            "org.eclipse.elk.layered.layering.layerConstraint",
            LAYERING_LAYER_CONSTRAINT_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #LAYERING_DISTRIBUTE_NODES}.
   */
  private final static boolean LAYERING_DISTRIBUTE_NODES_DEFAULT = false;
  
  /**
   * Whether wide nodes should be distributed to several layers.
   */
  public final static IProperty<Boolean> LAYERING_DISTRIBUTE_NODES = new Property<Boolean>(
            "org.eclipse.elk.layered.layering.distributeNodes",
            LAYERING_DISTRIBUTE_NODES_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #LAYERING_WIDE_NODES_ON_MULTIPLE_LAYERS}.
   */
  private final static WideNodesStrategy LAYERING_WIDE_NODES_ON_MULTIPLE_LAYERS_DEFAULT = WideNodesStrategy.OFF;
  
  /**
   * Strategy to distribute wide nodes over multiple layers.
   */
  public final static IProperty<WideNodesStrategy> LAYERING_WIDE_NODES_ON_MULTIPLE_LAYERS = new Property<WideNodesStrategy>(
            "org.eclipse.elk.layered.layering.wideNodesOnMultipleLayers",
            LAYERING_WIDE_NODES_ON_MULTIPLE_LAYERS_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #LAYERING_MIN_WIDTH_UPPER_BOUND_ON_WIDTH}.
   */
  private final static int LAYERING_MIN_WIDTH_UPPER_BOUND_ON_WIDTH_DEFAULT = 4;
  
  /**
   * Defines a loose upper bound on the width of the MinWidth layerer.
   */
  public final static IProperty<Integer> LAYERING_MIN_WIDTH_UPPER_BOUND_ON_WIDTH = new Property<Integer>(
            "org.eclipse.elk.layered.layering.minWidth.upperBoundOnWidth",
            LAYERING_MIN_WIDTH_UPPER_BOUND_ON_WIDTH_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #LAYERING_MIN_WIDTH_UPPER_LAYER_ESTIMATION_SCALING_FACTOR}.
   */
  private final static int LAYERING_MIN_WIDTH_UPPER_LAYER_ESTIMATION_SCALING_FACTOR_DEFAULT = 2;
  
  /**
   * Multiplied with Upper Bound On Width for defining an upper bound on the width of layers which haven&apos;t been determined yet, but whose maximum width had been (roughly) estimated by the MinWidth algorithm. Compensates for too high estimations.
   */
  public final static IProperty<Integer> LAYERING_MIN_WIDTH_UPPER_LAYER_ESTIMATION_SCALING_FACTOR = new Property<Integer>(
            "org.eclipse.elk.layered.layering.minWidth.upperLayerEstimationScalingFactor",
            LAYERING_MIN_WIDTH_UPPER_LAYER_ESTIMATION_SCALING_FACTOR_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #LAYERING_NODE_PROMOTION_STRATEGY}.
   */
  private final static NodePromotionStrategy LAYERING_NODE_PROMOTION_STRATEGY_DEFAULT = NodePromotionStrategy.NONE;
  
  /**
   * Reduces number of dummy nodes after layering phase (if possible).
   */
  public final static IProperty<NodePromotionStrategy> LAYERING_NODE_PROMOTION_STRATEGY = new Property<NodePromotionStrategy>(
            "org.eclipse.elk.layered.layering.nodePromotion.strategy",
            LAYERING_NODE_PROMOTION_STRATEGY_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #LAYERING_NODE_PROMOTION_MAX_ITERATIONS}.
   */
  private final static int LAYERING_NODE_PROMOTION_MAX_ITERATIONS_DEFAULT = 0;
  
  /**
   * Limits the number of iterations for node promotion.
   */
  public final static IProperty<Integer> LAYERING_NODE_PROMOTION_MAX_ITERATIONS = new Property<Integer>(
            "org.eclipse.elk.layered.layering.nodePromotion.maxIterations",
            LAYERING_NODE_PROMOTION_MAX_ITERATIONS_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #CROSSING_MINIMIZATION_STRATEGY}.
   */
  private final static CrossingMinimizationStrategy CROSSING_MINIMIZATION_STRATEGY_DEFAULT = CrossingMinimizationStrategy.LAYER_SWEEP;
  
  /**
   * Strategy for crossing minimization.
   */
  public final static IProperty<CrossingMinimizationStrategy> CROSSING_MINIMIZATION_STRATEGY = new Property<CrossingMinimizationStrategy>(
            "org.eclipse.elk.layered.crossingMinimization.strategy",
            CROSSING_MINIMIZATION_STRATEGY_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #CROSSING_MINIMIZATION_GREEDY_SWITCH}.
   */
  private final static GreedySwitchType CROSSING_MINIMIZATION_GREEDY_SWITCH_DEFAULT = GreedySwitchType.TWO_SIDED;
  
  /**
   * Greedy Switch strategy for crossing minimization.
   */
  public final static IProperty<GreedySwitchType> CROSSING_MINIMIZATION_GREEDY_SWITCH = new Property<GreedySwitchType>(
            "org.eclipse.elk.layered.crossingMinimization.greedySwitch",
            CROSSING_MINIMIZATION_GREEDY_SWITCH_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #NODE_PLACEMENT_STRATEGY}.
   */
  private final static NodePlacementStrategy NODE_PLACEMENT_STRATEGY_DEFAULT = NodePlacementStrategy.BRANDES_KOEPF;
  
  /**
   * Strategy for node placement.
   */
  public final static IProperty<NodePlacementStrategy> NODE_PLACEMENT_STRATEGY = new Property<NodePlacementStrategy>(
            "org.eclipse.elk.layered.nodePlacement.strategy",
            NODE_PLACEMENT_STRATEGY_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #NODE_PLACEMENT_BK_EDGE_STRAIGHTENING}.
   */
  private final static EdgeStraighteningStrategy NODE_PLACEMENT_BK_EDGE_STRAIGHTENING_DEFAULT = EdgeStraighteningStrategy.IMPROVE_STRAIGHTNESS;
  
  /**
   * Specifies whether the Brandes Koepf node placer tries to increase the number of straight edges
   * at the expense of diagram size.
   */
  public final static IProperty<EdgeStraighteningStrategy> NODE_PLACEMENT_BK_EDGE_STRAIGHTENING = new Property<EdgeStraighteningStrategy>(
            "org.eclipse.elk.layered.nodePlacement.bk.edgeStraightening",
            NODE_PLACEMENT_BK_EDGE_STRAIGHTENING_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #NODE_PLACEMENT_BK_FIXED_ALIGNMENT}.
   */
  private final static FixedAlignment NODE_PLACEMENT_BK_FIXED_ALIGNMENT_DEFAULT = FixedAlignment.NONE;
  
  /**
   * Tells the BK node placer to use a certain alignment instead of taking the optimal result.
   */
  public final static IProperty<FixedAlignment> NODE_PLACEMENT_BK_FIXED_ALIGNMENT = new Property<FixedAlignment>(
            "org.eclipse.elk.layered.nodePlacement.bk.fixedAlignment",
            NODE_PLACEMENT_BK_FIXED_ALIGNMENT_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #NODE_PLACEMENT_LINEAR_SEGMENTS_DEFLECTION_DAMPENING}.
   */
  private final static float NODE_PLACEMENT_LINEAR_SEGMENTS_DEFLECTION_DAMPENING_DEFAULT = 0.3f;
  
  /**
   * Dampens the movement of nodes to keep the diagram from getting too large.
   */
  public final static IProperty<Float> NODE_PLACEMENT_LINEAR_SEGMENTS_DEFLECTION_DAMPENING = new Property<Float>(
            "org.eclipse.elk.layered.nodePlacement.linearSegments.deflectionDampening",
            NODE_PLACEMENT_LINEAR_SEGMENTS_DEFLECTION_DAMPENING_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #EDGE_ROUTING_SELF_LOOP_PLACEMENT}.
   */
  private final static SelfLoopPlacement EDGE_ROUTING_SELF_LOOP_PLACEMENT_DEFAULT = SelfLoopPlacement.NORTH_STACKED;
  
  public final static IProperty<SelfLoopPlacement> EDGE_ROUTING_SELF_LOOP_PLACEMENT = new Property<SelfLoopPlacement>(
            "org.eclipse.elk.layered.edgeRouting.selfLoopPlacement",
            EDGE_ROUTING_SELF_LOOP_PLACEMENT_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #SPACING_EDGE_NODE_SPACING_FACTOR}.
   */
  private final static float SPACING_EDGE_NODE_SPACING_FACTOR_DEFAULT = 0.5f;
  
  /**
   * Factor by which the object spacing is multiplied to arrive at the minimal spacing between
   * an edge and a node.
   */
  public final static IProperty<Float> SPACING_EDGE_NODE_SPACING_FACTOR = new Property<Float>(
            "org.eclipse.elk.layered.spacing.edgeNodeSpacingFactor",
            SPACING_EDGE_NODE_SPACING_FACTOR_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #SPACING_EDGE_SPACING_FACTOR}.
   */
  private final static float SPACING_EDGE_SPACING_FACTOR_DEFAULT = 0.5f;
  
  /**
   * Factor by which the object spacing is multiplied to arrive at the minimal spacing between
   * edges.
   */
  public final static IProperty<Float> SPACING_EDGE_SPACING_FACTOR = new Property<Float>(
            "org.eclipse.elk.layered.spacing.edgeSpacingFactor",
            SPACING_EDGE_SPACING_FACTOR_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #SPACING_IN_LAYER_SPACING_FACTOR}.
   */
  private final static float SPACING_IN_LAYER_SPACING_FACTOR_DEFAULT = 1;
  
  /**
   * Factor by which the usual spacing is multiplied to determine the in-layer spacing between
   * objects.
   */
  public final static IProperty<Float> SPACING_IN_LAYER_SPACING_FACTOR = new Property<Float>(
            "org.eclipse.elk.layered.spacing.inLayerSpacingFactor",
            SPACING_IN_LAYER_SPACING_FACTOR_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #COMPACTION_CONNECTED_COMPONENTS}.
   */
  private final static boolean COMPACTION_CONNECTED_COMPONENTS_DEFAULT = false;
  
  /**
   * Tries to further compact components (disconnected sub-graphs).
   */
  public final static IProperty<Boolean> COMPACTION_CONNECTED_COMPONENTS = new Property<Boolean>(
            "org.eclipse.elk.layered.compaction.connectedComponents",
            COMPACTION_CONNECTED_COMPONENTS_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #COMPACTION_POST_COMPACTION_STRATEGY}.
   */
  private final static GraphCompactionStrategy COMPACTION_POST_COMPACTION_STRATEGY_DEFAULT = GraphCompactionStrategy.NONE;
  
  /**
   * Specifies whether and how post-process compaction is applied.
   */
  public final static IProperty<GraphCompactionStrategy> COMPACTION_POST_COMPACTION_STRATEGY = new Property<GraphCompactionStrategy>(
            "org.eclipse.elk.layered.compaction.postCompaction.strategy",
            COMPACTION_POST_COMPACTION_STRATEGY_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #COMPACTION_POST_COMPACTION_CONSTRAINTS}.
   */
  private final static ConstraintCalculationStrategy COMPACTION_POST_COMPACTION_CONSTRAINTS_DEFAULT = ConstraintCalculationStrategy.SCANLINE;
  
  /**
   * Specifies whether and how post-process compaction is applied.
   */
  public final static IProperty<ConstraintCalculationStrategy> COMPACTION_POST_COMPACTION_CONSTRAINTS = new Property<ConstraintCalculationStrategy>(
            "org.eclipse.elk.layered.compaction.postCompaction.constraints",
            COMPACTION_POST_COMPACTION_CONSTRAINTS_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #HIGH_DEGREE_NODES_TREATMENT}.
   */
  private final static boolean HIGH_DEGREE_NODES_TREATMENT_DEFAULT = false;
  
  /**
   * Makes room around high degree nodes to place leafs and trees.
   */
  public final static IProperty<Boolean> HIGH_DEGREE_NODES_TREATMENT = new Property<Boolean>(
            "org.eclipse.elk.layered.highDegreeNodes.treatment",
            HIGH_DEGREE_NODES_TREATMENT_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #HIGH_DEGREE_NODES_THRESHOLD}.
   */
  private final static int HIGH_DEGREE_NODES_THRESHOLD_DEFAULT = 16;
  
  /**
   * Whether a node is considered to have a high degree.
   */
  public final static IProperty<Integer> HIGH_DEGREE_NODES_THRESHOLD = new Property<Integer>(
            "org.eclipse.elk.layered.highDegreeNodes.threshold",
            HIGH_DEGREE_NODES_THRESHOLD_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #HIGH_DEGREE_NODES_TREE_HEIGHT}.
   */
  private final static int HIGH_DEGREE_NODES_TREE_HEIGHT_DEFAULT = 5;
  
  /**
   * Maximum height of a subtree connected to a high degree node to be moved to separate layers.
   */
  public final static IProperty<Integer> HIGH_DEGREE_NODES_TREE_HEIGHT = new Property<Integer>(
            "org.eclipse.elk.layered.highDegreeNodes.treeHeight",
            HIGH_DEGREE_NODES_TREE_HEIGHT_DEFAULT,
            null,
            null);
  
  /**
   * Required value for dependency between {@link #EDGE_LABEL_SIDE_SELECTION} and {@link #EDGE_ROUTING}.
   */
  private final static EdgeRouting EDGE_LABEL_SIDE_SELECTION_DEP_EDGE_ROUTING = EdgeRouting.ORTHOGONAL;
  
  /**
   * Required value for dependency between {@link #INTERACTIVE_REFERENCE_POINT} and {@link #CYCLE_BREAKING_STRATEGY}.
   */
  private final static CycleBreakingStrategy INTERACTIVE_REFERENCE_POINT_DEP_CYCLE_BREAKING_STRATEGY = CycleBreakingStrategy.INTERACTIVE;
  
  /**
   * Required value for dependency between {@link #INTERACTIVE_REFERENCE_POINT} and {@link #CROSSING_MINIMIZATION_STRATEGY}.
   */
  private final static CrossingMinimizationStrategy INTERACTIVE_REFERENCE_POINT_DEP_CROSSING_MINIMIZATION_STRATEGY = CrossingMinimizationStrategy.INTERACTIVE;
  
  /**
   * Required value for dependency between {@link #SAUSAGE_FOLDING} and {@link #LAYERING_STRATEGY}.
   */
  private final static LayeringStrategy SAUSAGE_FOLDING_DEP_LAYERING_STRATEGY = LayeringStrategy.LONGEST_PATH;
  
  /**
   * Required value for dependency between {@link #LAYERING_MIN_WIDTH_UPPER_BOUND_ON_WIDTH} and {@link #LAYERING_STRATEGY}.
   */
  private final static LayeringStrategy LAYERING_MIN_WIDTH_UPPER_BOUND_ON_WIDTH_DEP_LAYERING_STRATEGY = LayeringStrategy.EXP_MIN_WIDTH;
  
  /**
   * Required value for dependency between {@link #LAYERING_MIN_WIDTH_UPPER_LAYER_ESTIMATION_SCALING_FACTOR} and {@link #LAYERING_STRATEGY}.
   */
  private final static LayeringStrategy LAYERING_MIN_WIDTH_UPPER_LAYER_ESTIMATION_SCALING_FACTOR_DEP_LAYERING_STRATEGY = LayeringStrategy.EXP_MIN_WIDTH;
  
  /**
   * Required value for dependency between {@link #NODE_PLACEMENT_BK_EDGE_STRAIGHTENING} and {@link #NODE_PLACEMENT_STRATEGY}.
   */
  private final static NodePlacementStrategy NODE_PLACEMENT_BK_EDGE_STRAIGHTENING_DEP_NODE_PLACEMENT_STRATEGY = NodePlacementStrategy.BRANDES_KOEPF;
  
  /**
   * Required value for dependency between {@link #NODE_PLACEMENT_BK_FIXED_ALIGNMENT} and {@link #NODE_PLACEMENT_STRATEGY}.
   */
  private final static NodePlacementStrategy NODE_PLACEMENT_BK_FIXED_ALIGNMENT_DEP_NODE_PLACEMENT_STRATEGY = NodePlacementStrategy.BRANDES_KOEPF;
  
  /**
   * Required value for dependency between {@link #NODE_PLACEMENT_LINEAR_SEGMENTS_DEFLECTION_DAMPENING} and {@link #NODE_PLACEMENT_STRATEGY}.
   */
  private final static NodePlacementStrategy NODE_PLACEMENT_LINEAR_SEGMENTS_DEFLECTION_DAMPENING_DEP_NODE_PLACEMENT_STRATEGY = NodePlacementStrategy.LINEAR_SEGMENTS;
  
  /**
   * Required value for dependency between {@link #EDGE_ROUTING_SELF_LOOP_PLACEMENT} and {@link #EDGE_ROUTING}.
   */
  private final static EdgeRouting EDGE_ROUTING_SELF_LOOP_PLACEMENT_DEP_EDGE_ROUTING = EdgeRouting.SPLINES;
  
  /**
   * Required value for dependency between {@link #COMPACTION_CONNECTED_COMPONENTS} and {@link #SEPARATE_CONNECTED_COMPONENTS}.
   */
  private final static boolean COMPACTION_CONNECTED_COMPONENTS_DEP_SEPARATE_CONNECTED_COMPONENTS = true;
  
  /**
   * Required value for dependency between {@link #HIGH_DEGREE_NODES_THRESHOLD} and {@link #HIGH_DEGREE_NODES_TREATMENT}.
   */
  private final static boolean HIGH_DEGREE_NODES_THRESHOLD_DEP_HIGH_DEGREE_NODES_TREATMENT = true;
  
  /**
   * Required value for dependency between {@link #HIGH_DEGREE_NODES_TREE_HEIGHT} and {@link #HIGH_DEGREE_NODES_TREATMENT}.
   */
  private final static boolean HIGH_DEGREE_NODES_TREE_HEIGHT_DEP_HIGH_DEGREE_NODES_TREATMENT = true;
  
  /**
   * Default value for {@link #SPACING_NODE} with algorithm "ELK Layered".
   */
  private final static float LAYERED_SUP_SPACING_NODE = 20;
  
  /**
   * Overridden value for Node Spacing.
   */
  public final static IProperty<Float> SPACING_NODE = new Property<Float>(
            CoreOptions.SPACING_NODE,
            LAYERED_SUP_SPACING_NODE);
  
  /**
   * Default value for {@link #SPACING_BORDER} with algorithm "ELK Layered".
   */
  private final static float LAYERED_SUP_SPACING_BORDER = 12;
  
  /**
   * Overridden value for Border Spacing.
   */
  public final static IProperty<Float> SPACING_BORDER = new Property<Float>(
            CoreOptions.SPACING_BORDER,
            LAYERED_SUP_SPACING_BORDER);
  
  /**
   * Default value for {@link #PRIORITY} with algorithm "ELK Layered".
   */
  private final static int LAYERED_SUP_PRIORITY = 0;
  
  /**
   * Overridden value for Priority.
   */
  public final static IProperty<Integer> PRIORITY = new Property<Integer>(
            CoreOptions.PRIORITY,
            LAYERED_SUP_PRIORITY);
  
  /**
   * Default value for {@link #EDGE_ROUTING} with algorithm "ELK Layered".
   */
  private final static EdgeRouting LAYERED_SUP_EDGE_ROUTING = EdgeRouting.ORTHOGONAL;
  
  /**
   * Overridden value for Edge Routing.
   */
  public final static IProperty<EdgeRouting> EDGE_ROUTING = new Property<EdgeRouting>(
            CoreOptions.EDGE_ROUTING,
            LAYERED_SUP_EDGE_ROUTING);
  
  /**
   * Default value for {@link #PORT_BORDER_OFFSET} with algorithm "ELK Layered".
   */
  private final static float LAYERED_SUP_PORT_BORDER_OFFSET = 0;
  
  /**
   * Overridden value for Port Border Offset.
   */
  public final static IProperty<Float> PORT_BORDER_OFFSET = new Property<Float>(
            CoreOptions.PORT_BORDER_OFFSET,
            LAYERED_SUP_PORT_BORDER_OFFSET);
  
  /**
   * Default value for {@link #RANDOM_SEED} with algorithm "ELK Layered".
   */
  private final static int LAYERED_SUP_RANDOM_SEED = 1;
  
  /**
   * Overridden value for Randomization Seed.
   */
  public final static IProperty<Integer> RANDOM_SEED = new Property<Integer>(
            CoreOptions.RANDOM_SEED,
            LAYERED_SUP_RANDOM_SEED);
  
  /**
   * Default value for {@link #ASPECT_RATIO} with algorithm "ELK Layered".
   */
  private final static float LAYERED_SUP_ASPECT_RATIO = 1.6f;
  
  /**
   * Overridden value for Aspect Ratio.
   */
  public final static IProperty<Float> ASPECT_RATIO = new Property<Float>(
            CoreOptions.ASPECT_RATIO,
            LAYERED_SUP_ASPECT_RATIO);
  
  /**
   * Default value for {@link #SEPARATE_CONNECTED_COMPONENTS} with algorithm "ELK Layered".
   */
  private final static boolean LAYERED_SUP_SEPARATE_CONNECTED_COMPONENTS = true;
  
  /**
   * Default value for {@link #DIRECTION} with algorithm "ELK Layered".
   */
  private final static Direction LAYERED_SUP_DIRECTION = Direction.RIGHT;
  
  /**
   * Default value for {@link #PORT_ALIGNMENT_BASIC} with algorithm "ELK Layered".
   */
  private final static PortAlignment LAYERED_SUP_PORT_ALIGNMENT_BASIC = PortAlignment.JUSTIFIED;
  
  public void apply(final ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.crossMinRecursive",
        "",
        "Recursive sweep",
        "Process all hierarchical graphs recursively.",
        CROSS_MIN_RECURSIVE_DEFAULT,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.crossMinRec"
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.layered.recursiveBoundary",
        "",
        "Recursive sweep",
        "Process all hierarchical graphs recursively.",
        RECURSIVE_BOUNDARY_DEFAULT,
        LayoutOptionData.Type.FLOAT,
        float.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
        , "de.cau.cs.kieler.klay.layered.recursiveBoundary"
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
        LAYERED_SUP_SPACING_NODE
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.spacing.border",
        LAYERED_SUP_SPACING_BORDER
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.spacing.port",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.spacing.label",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.priority",
        LAYERED_SUP_PRIORITY
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.edgeRouting",
        LAYERED_SUP_EDGE_ROUTING
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.port.borderOffset",
        LAYERED_SUP_PORT_BORDER_OFFSET
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.randomSeed",
        LAYERED_SUP_RANDOM_SEED
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.aspectRatio",
        LAYERED_SUP_ASPECT_RATIO
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.noLayout",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.portConstraints",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.port.side",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.debugMode",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.alignment",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layoutHierarchy",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.hierarchyHandling",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.separateConnectedComponents",
        LAYERED_SUP_SEPARATE_CONNECTED_COMPONENTS
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.insideSelfLoops.activate",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.insideSelfLoops.yo",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.nodeSize.constraints",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.nodeSize.options",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.direction",
        LAYERED_SUP_DIRECTION
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.nodeLabels.placement",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.portLabels.placement",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.portAlignment.basic",
        LAYERED_SUP_PORT_ALIGNMENT_BASIC
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.portAlignment.north",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.portAlignment.south",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.portAlignment.west",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.portAlignment.east",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.spacing.edgeNodeSpacingFactor",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.spacing.edgeNodeSpacingFactor",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.unnecessaryBendpoints",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.layering.strategy",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.thoroughness",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.layering.layerConstraint",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.cycleBreaking.strategy",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.crossingMinimization.strategy",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.crossingMinimization.greedySwitch",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.mergeEdges",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.mergeHierarchyEdges",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.interactiveReferencePoint",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.nodePlacement.strategy",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.nodePlacement.bk.fixedAlignment",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.edgeLabelSideSelection",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.feedbackEdges",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.spacing.inLayerSpacingFactor",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.layering.wideNodesOnMultipleLayers",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.nodePlacement.linearSegments.deflectionDampening",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.edgeRouting.selfLoopPlacement",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.contentAlignment",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.nodePlacement.bk.edgeStraightening",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.compaction.postCompaction.strategy",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.compaction.postCompaction.constraints",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.compaction.connectedComponents",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.highDegreeNodes.treatment",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.highDegreeNodes.threshold",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.layered",
        "org.eclipse.elk.layered.highDegreeNodes.treeHeight",
        null
    );
  }
}
