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
import org.eclipse.elk.alg.layered.components.ComponentOrderingStrategy;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.HierarchyHandling;
import org.eclipse.elk.core.util.ExclusiveBounds;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * Declarations for the ELK Layered layout algorithm.
 */
@SuppressWarnings("all")
public class LayeredMetaDataProvider implements ILayoutMetaDataProvider {
  /**
   * Default value for {@link #DIRECTION_CONGRUENCY}.
   */
  private static final DirectionCongruency DIRECTION_CONGRUENCY_DEFAULT = DirectionCongruency.READING_DIRECTION;

  /**
   * Specifies how drawings of the same graph with different layout directions compare to each other:
   * either a natural reading direction is preserved or the drawings are rotated versions of each other.
   */
  public static final IProperty<DirectionCongruency> DIRECTION_CONGRUENCY = new Property<DirectionCongruency>(
            "org.eclipse.elk.layered.directionCongruency",
            DIRECTION_CONGRUENCY_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #FEEDBACK_EDGES}.
   */
  private static final boolean FEEDBACK_EDGES_DEFAULT = false;

  /**
   * Whether feedback edges should be highlighted by routing around the nodes.
   */
  public static final IProperty<Boolean> FEEDBACK_EDGES = new Property<Boolean>(
            "org.eclipse.elk.layered.feedbackEdges",
            FEEDBACK_EDGES_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #INTERACTIVE_REFERENCE_POINT}.
   */
  private static final InteractiveReferencePoint INTERACTIVE_REFERENCE_POINT_DEFAULT = InteractiveReferencePoint.CENTER;

  /**
   * Determines which point of a node is considered by interactive layout phases.
   */
  public static final IProperty<InteractiveReferencePoint> INTERACTIVE_REFERENCE_POINT = new Property<InteractiveReferencePoint>(
            "org.eclipse.elk.layered.interactiveReferencePoint",
            INTERACTIVE_REFERENCE_POINT_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #MERGE_EDGES}.
   */
  private static final boolean MERGE_EDGES_DEFAULT = false;

  /**
   * Edges that have no ports are merged so they touch the connected nodes at the same points.
   * When this option is disabled, one port is created for each edge directly connected to a
   * node. When it is enabled, all such incoming edges share an input port, and all outgoing
   * edges share an output port.
   */
  public static final IProperty<Boolean> MERGE_EDGES = new Property<Boolean>(
            "org.eclipse.elk.layered.mergeEdges",
            MERGE_EDGES_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #MERGE_HIERARCHY_EDGES}.
   */
  private static final boolean MERGE_HIERARCHY_EDGES_DEFAULT = true;

  /**
   * If hierarchical layout is active, hierarchy-crossing edges use as few hierarchical ports
   * as possible. They are broken by the algorithm, with hierarchical ports inserted as
   * required. Usually, one such port is created for each edge at each hierarchy crossing point.
   * With this option set to true, we try to create as few hierarchical ports as possible in
   * the process. In particular, all edges that form a hyperedge can share a port.
   */
  public static final IProperty<Boolean> MERGE_HIERARCHY_EDGES = new Property<Boolean>(
            "org.eclipse.elk.layered.mergeHierarchyEdges",
            MERGE_HIERARCHY_EDGES_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #ALLOW_NON_FLOW_PORTS_TO_SWITCH_SIDES}.
   */
  private static final boolean ALLOW_NON_FLOW_PORTS_TO_SWITCH_SIDES_DEFAULT = false;

  /**
   * Specifies whether non-flow ports may switch sides if their node's port constraints are either FIXED_SIDE
   * or FIXED_ORDER. A non-flow port is a port on a side that is not part of the currently configured layout flow.
   * For instance, given a left-to-right layout direction, north and south ports would be considered non-flow ports.
   * Further note that the underlying criterium whether to switch sides or not solely relies on the
   * minimization of edge crossings. Hence, edge length and other aesthetics criteria are not addressed.
   */
  public static final IProperty<Boolean> ALLOW_NON_FLOW_PORTS_TO_SWITCH_SIDES = new Property<Boolean>(
            "org.eclipse.elk.layered.allowNonFlowPortsToSwitchSides",
            ALLOW_NON_FLOW_PORTS_TO_SWITCH_SIDES_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #PORT_SORTING_STRATEGY}.
   */
  private static final PortSortingStrategy PORT_SORTING_STRATEGY_DEFAULT = PortSortingStrategy.INPUT_ORDER;

  /**
   * Only relevant for nodes with FIXED_SIDE port constraints. Determines the way a node's ports are
   * distributed on the sides of a node if their order is not prescribed. The option is set on parent nodes.
   */
  public static final IProperty<PortSortingStrategy> PORT_SORTING_STRATEGY = new Property<PortSortingStrategy>(
            "org.eclipse.elk.layered.portSortingStrategy",
            PORT_SORTING_STRATEGY_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #THOROUGHNESS}.
   */
  private static final int THOROUGHNESS_DEFAULT = 7;

  /**
   * Lower bound value for {@link #THOROUGHNESS}.
   */
  private static final Comparable<? super Integer> THOROUGHNESS_LOWER_BOUND = Integer.valueOf(1);

  /**
   * How much effort should be spent to produce a nice layout.
   */
  public static final IProperty<Integer> THOROUGHNESS = new Property<Integer>(
            "org.eclipse.elk.layered.thoroughness",
            THOROUGHNESS_DEFAULT,
            THOROUGHNESS_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #UNNECESSARY_BENDPOINTS}.
   */
  private static final boolean UNNECESSARY_BENDPOINTS_DEFAULT = false;

  /**
   * Adds bend points even if an edge does not change direction. If true, each long edge dummy
   * will contribute a bend point to its edges and hierarchy-crossing edges will always get a
   * bend point where they cross hierarchy boundaries. By default, bend points are only added
   * where an edge changes direction.
   */
  public static final IProperty<Boolean> UNNECESSARY_BENDPOINTS = new Property<Boolean>(
            "org.eclipse.elk.layered.unnecessaryBendpoints",
            UNNECESSARY_BENDPOINTS_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #GENERATE_POSITION_AND_LAYER_IDS}.
   */
  private static final boolean GENERATE_POSITION_AND_LAYER_IDS_DEFAULT = false;

  /**
   * If enabled position id and layer id are generated, which are usually only used internally
   * when setting the interactiveLayout option. This option should be specified on the root node.
   */
  public static final IProperty<Boolean> GENERATE_POSITION_AND_LAYER_IDS = new Property<Boolean>(
            "org.eclipse.elk.layered.generatePositionAndLayerIds",
            GENERATE_POSITION_AND_LAYER_IDS_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #EXPANDED}.
   */
  private static final boolean EXPANDED_DEFAULT = false;

  /**
   * If the node is expanded this property is true
   */
  public static final IProperty<Boolean> EXPANDED = new Property<Boolean>(
            "org.eclipse.elk.layered.expanded",
            EXPANDED_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #NAME}.
   */
  private static final String NAME_DEFAULT = "result";

  /**
   * If the node is expanded this property is true
   */
  public static final IProperty<String> NAME = new Property<String>(
            "org.eclipse.elk.layered.name",
            NAME_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #CYCLE_BREAKING_STRATEGY}.
   */
  private static final CycleBreakingStrategy CYCLE_BREAKING_STRATEGY_DEFAULT = CycleBreakingStrategy.GREEDY;

  /**
   * Strategy for cycle breaking. Cycle breaking looks for cycles in the graph and determines
   * which edges to reverse to break the cycles. Reversed edges will end up pointing to the
   * opposite direction of regular edges (that is, reversed edges will point left if edges
   * usually point right).
   */
  public static final IProperty<CycleBreakingStrategy> CYCLE_BREAKING_STRATEGY = new Property<CycleBreakingStrategy>(
            "org.eclipse.elk.layered.cycleBreaking.strategy",
            CYCLE_BREAKING_STRATEGY_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #LAYERING_STRATEGY}.
   */
  private static final LayeringStrategy LAYERING_STRATEGY_DEFAULT = LayeringStrategy.NETWORK_SIMPLEX;

  /**
   * Strategy for node layering.
   */
  public static final IProperty<LayeringStrategy> LAYERING_STRATEGY = new Property<LayeringStrategy>(
            "org.eclipse.elk.layered.layering.strategy",
            LAYERING_STRATEGY_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #LAYERING_LAYER_CONSTRAINT}.
   */
  private static final LayerConstraint LAYERING_LAYER_CONSTRAINT_DEFAULT = LayerConstraint.NONE;

  /**
   * Determines a constraint on the placement of the node regarding the layering.
   */
  public static final IProperty<LayerConstraint> LAYERING_LAYER_CONSTRAINT = new Property<LayerConstraint>(
            "org.eclipse.elk.layered.layering.layerConstraint",
            LAYERING_LAYER_CONSTRAINT_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #LAYERING_LAYER_CHOICE_CONSTRAINT}.
   */
  private static final Integer LAYERING_LAYER_CHOICE_CONSTRAINT_DEFAULT = null;

  /**
   * Lower bound value for {@link #LAYERING_LAYER_CHOICE_CONSTRAINT}.
   */
  private static final Comparable<? super Integer> LAYERING_LAYER_CHOICE_CONSTRAINT_LOWER_BOUND = Integer.valueOf((-1));

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
  public static final IProperty<Integer> LAYERING_LAYER_CHOICE_CONSTRAINT = new Property<Integer>(
            "org.eclipse.elk.layered.layering.layerChoiceConstraint",
            LAYERING_LAYER_CHOICE_CONSTRAINT_DEFAULT,
            LAYERING_LAYER_CHOICE_CONSTRAINT_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #LAYERING_LAYER_ID}.
   */
  private static final int LAYERING_LAYER_ID_DEFAULT = (-1);

  /**
   * Lower bound value for {@link #LAYERING_LAYER_ID}.
   */
  private static final Comparable<? super Integer> LAYERING_LAYER_ID_LOWER_BOUND = Integer.valueOf((-1));

  /**
   * Layer identifier that was calculated by ELK Layered for a node.
   * This is only generated if interactiveLayot or generatePositionAndLayerIds is set.
   */
  public static final IProperty<Integer> LAYERING_LAYER_ID = new Property<Integer>(
            "org.eclipse.elk.layered.layering.layerId",
            LAYERING_LAYER_ID_DEFAULT,
            LAYERING_LAYER_ID_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #LAYERING_MIN_WIDTH_UPPER_BOUND_ON_WIDTH}.
   */
  private static final int LAYERING_MIN_WIDTH_UPPER_BOUND_ON_WIDTH_DEFAULT = 4;

  /**
   * Lower bound value for {@link #LAYERING_MIN_WIDTH_UPPER_BOUND_ON_WIDTH}.
   */
  private static final Comparable<? super Integer> LAYERING_MIN_WIDTH_UPPER_BOUND_ON_WIDTH_LOWER_BOUND = Integer.valueOf((-1));

  /**
   * Defines a loose upper bound on the width of the MinWidth layerer.
   * If set to '-1' multiple values are tested and the best result is selected.
   */
  public static final IProperty<Integer> LAYERING_MIN_WIDTH_UPPER_BOUND_ON_WIDTH = new Property<Integer>(
            "org.eclipse.elk.layered.layering.minWidth.upperBoundOnWidth",
            LAYERING_MIN_WIDTH_UPPER_BOUND_ON_WIDTH_DEFAULT,
            LAYERING_MIN_WIDTH_UPPER_BOUND_ON_WIDTH_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #LAYERING_MIN_WIDTH_UPPER_LAYER_ESTIMATION_SCALING_FACTOR}.
   */
  private static final int LAYERING_MIN_WIDTH_UPPER_LAYER_ESTIMATION_SCALING_FACTOR_DEFAULT = 2;

  /**
   * Lower bound value for {@link #LAYERING_MIN_WIDTH_UPPER_LAYER_ESTIMATION_SCALING_FACTOR}.
   */
  private static final Comparable<? super Integer> LAYERING_MIN_WIDTH_UPPER_LAYER_ESTIMATION_SCALING_FACTOR_LOWER_BOUND = Integer.valueOf((-1));

  /**
   * Multiplied with Upper Bound On Width for defining an upper bound on the width of layers which
   * haven't been determined yet, but whose maximum width had been (roughly) estimated by the MinWidth
   * algorithm. Compensates for too high estimations.
   * If set to '-1' multiple values are tested and the best result is selected.
   */
  public static final IProperty<Integer> LAYERING_MIN_WIDTH_UPPER_LAYER_ESTIMATION_SCALING_FACTOR = new Property<Integer>(
            "org.eclipse.elk.layered.layering.minWidth.upperLayerEstimationScalingFactor",
            LAYERING_MIN_WIDTH_UPPER_LAYER_ESTIMATION_SCALING_FACTOR_DEFAULT,
            LAYERING_MIN_WIDTH_UPPER_LAYER_ESTIMATION_SCALING_FACTOR_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #LAYERING_NODE_PROMOTION_STRATEGY}.
   */
  private static final NodePromotionStrategy LAYERING_NODE_PROMOTION_STRATEGY_DEFAULT = NodePromotionStrategy.NONE;

  /**
   * Reduces number of dummy nodes after layering phase (if possible).
   */
  public static final IProperty<NodePromotionStrategy> LAYERING_NODE_PROMOTION_STRATEGY = new Property<NodePromotionStrategy>(
            "org.eclipse.elk.layered.layering.nodePromotion.strategy",
            LAYERING_NODE_PROMOTION_STRATEGY_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #LAYERING_NODE_PROMOTION_MAX_ITERATIONS}.
   */
  private static final int LAYERING_NODE_PROMOTION_MAX_ITERATIONS_DEFAULT = 0;

  /**
   * Lower bound value for {@link #LAYERING_NODE_PROMOTION_MAX_ITERATIONS}.
   */
  private static final Comparable<? super Integer> LAYERING_NODE_PROMOTION_MAX_ITERATIONS_LOWER_BOUND = Integer.valueOf(0);

  /**
   * Limits the number of iterations for node promotion.
   */
  public static final IProperty<Integer> LAYERING_NODE_PROMOTION_MAX_ITERATIONS = new Property<Integer>(
            "org.eclipse.elk.layered.layering.nodePromotion.maxIterations",
            LAYERING_NODE_PROMOTION_MAX_ITERATIONS_DEFAULT,
            LAYERING_NODE_PROMOTION_MAX_ITERATIONS_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #LAYERING_COFFMAN_GRAHAM_LAYER_BOUND}.
   */
  private static final int LAYERING_COFFMAN_GRAHAM_LAYER_BOUND_DEFAULT = Integer.MAX_VALUE;

  /**
   * The maximum number of nodes allowed per layer.
   */
  public static final IProperty<Integer> LAYERING_COFFMAN_GRAHAM_LAYER_BOUND = new Property<Integer>(
            "org.eclipse.elk.layered.layering.coffmanGraham.layerBound",
            LAYERING_COFFMAN_GRAHAM_LAYER_BOUND_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #CROSSING_MINIMIZATION_STRATEGY}.
   */
  private static final CrossingMinimizationStrategy CROSSING_MINIMIZATION_STRATEGY_DEFAULT = CrossingMinimizationStrategy.LAYER_SWEEP;

  /**
   * Strategy for crossing minimization.
   */
  public static final IProperty<CrossingMinimizationStrategy> CROSSING_MINIMIZATION_STRATEGY = new Property<CrossingMinimizationStrategy>(
            "org.eclipse.elk.layered.crossingMinimization.strategy",
            CROSSING_MINIMIZATION_STRATEGY_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #CROSSING_MINIMIZATION_FORCE_NODE_MODEL_ORDER}.
   */
  private static final boolean CROSSING_MINIMIZATION_FORCE_NODE_MODEL_ORDER_DEFAULT = false;

  /**
   * The node order given by the model does not change to produce a better layout. E.g. if node A
   * is before node B in the model this is not changed during crossing minimization. This assumes that the
   * node model order is already respected before crossing minimization. This can be achieved by setting
   * considerModelOrder.strategy to NODES_AND_EDGES.
   */
  public static final IProperty<Boolean> CROSSING_MINIMIZATION_FORCE_NODE_MODEL_ORDER = new Property<Boolean>(
            "org.eclipse.elk.layered.crossingMinimization.forceNodeModelOrder",
            CROSSING_MINIMIZATION_FORCE_NODE_MODEL_ORDER_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #CROSSING_MINIMIZATION_HIERARCHICAL_SWEEPINESS}.
   */
  private static final double CROSSING_MINIMIZATION_HIERARCHICAL_SWEEPINESS_DEFAULT = 0.1;

  /**
   * How likely it is to use cross-hierarchy (1) vs bottom-up (-1).
   */
  public static final IProperty<Double> CROSSING_MINIMIZATION_HIERARCHICAL_SWEEPINESS = new Property<Double>(
            "org.eclipse.elk.layered.crossingMinimization.hierarchicalSweepiness",
            CROSSING_MINIMIZATION_HIERARCHICAL_SWEEPINESS_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #CROSSING_MINIMIZATION_SEMI_INTERACTIVE}.
   */
  private static final boolean CROSSING_MINIMIZATION_SEMI_INTERACTIVE_DEFAULT = false;

  /**
   * Preserves the order of nodes within a layer but still minimizes crossings between edges connecting
   * long edge dummies. Derives the desired order from positions specified by the 'org.eclipse.elk.position'
   * layout option. Requires a crossing minimization strategy that is able to process 'in-layer' constraints.
   */
  public static final IProperty<Boolean> CROSSING_MINIMIZATION_SEMI_INTERACTIVE = new Property<Boolean>(
            "org.eclipse.elk.layered.crossingMinimization.semiInteractive",
            CROSSING_MINIMIZATION_SEMI_INTERACTIVE_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #CROSSING_MINIMIZATION_IN_LAYER_PRED_OF}.
   */
  private static final String CROSSING_MINIMIZATION_IN_LAYER_PRED_OF_DEFAULT = null;

  /**
   * Allows to set a constraint which specifies of which node
   * the current node is the predecessor.
   * If set to 's' then the node is the predecessor of 's' and is in the same layer
   */
  public static final IProperty<String> CROSSING_MINIMIZATION_IN_LAYER_PRED_OF = new Property<String>(
            "org.eclipse.elk.layered.crossingMinimization.inLayerPredOf",
            CROSSING_MINIMIZATION_IN_LAYER_PRED_OF_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #CROSSING_MINIMIZATION_IN_LAYER_SUCC_OF}.
   */
  private static final String CROSSING_MINIMIZATION_IN_LAYER_SUCC_OF_DEFAULT = null;

  /**
   * Allows to set a constraint which specifies of which node
   * the current node is the successor.
   * If set to 's' then the node is the successor of 's' and is in the same layer
   */
  public static final IProperty<String> CROSSING_MINIMIZATION_IN_LAYER_SUCC_OF = new Property<String>(
            "org.eclipse.elk.layered.crossingMinimization.inLayerSuccOf",
            CROSSING_MINIMIZATION_IN_LAYER_SUCC_OF_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #CROSSING_MINIMIZATION_POSITION_CHOICE_CONSTRAINT}.
   */
  private static final Integer CROSSING_MINIMIZATION_POSITION_CHOICE_CONSTRAINT_DEFAULT = null;

  /**
   * Lower bound value for {@link #CROSSING_MINIMIZATION_POSITION_CHOICE_CONSTRAINT}.
   */
  private static final Comparable<? super Integer> CROSSING_MINIMIZATION_POSITION_CHOICE_CONSTRAINT_LOWER_BOUND = Integer.valueOf((-1));

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
  public static final IProperty<Integer> CROSSING_MINIMIZATION_POSITION_CHOICE_CONSTRAINT = new Property<Integer>(
            "org.eclipse.elk.layered.crossingMinimization.positionChoiceConstraint",
            CROSSING_MINIMIZATION_POSITION_CHOICE_CONSTRAINT_DEFAULT,
            CROSSING_MINIMIZATION_POSITION_CHOICE_CONSTRAINT_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #CROSSING_MINIMIZATION_POSITION_ID}.
   */
  private static final int CROSSING_MINIMIZATION_POSITION_ID_DEFAULT = (-1);

  /**
   * Lower bound value for {@link #CROSSING_MINIMIZATION_POSITION_ID}.
   */
  private static final Comparable<? super Integer> CROSSING_MINIMIZATION_POSITION_ID_LOWER_BOUND = Integer.valueOf((-1));

  /**
   * Position within a layer that was determined by ELK Layered for a node.
   * This is only generated if interactiveLayot or generatePositionAndLayerIds is set.
   */
  public static final IProperty<Integer> CROSSING_MINIMIZATION_POSITION_ID = new Property<Integer>(
            "org.eclipse.elk.layered.crossingMinimization.positionId",
            CROSSING_MINIMIZATION_POSITION_ID_DEFAULT,
            CROSSING_MINIMIZATION_POSITION_ID_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #CROSSING_MINIMIZATION_GREEDY_SWITCH_ACTIVATION_THRESHOLD}.
   */
  private static final int CROSSING_MINIMIZATION_GREEDY_SWITCH_ACTIVATION_THRESHOLD_DEFAULT = 40;

  /**
   * Lower bound value for {@link #CROSSING_MINIMIZATION_GREEDY_SWITCH_ACTIVATION_THRESHOLD}.
   */
  private static final Comparable<? super Integer> CROSSING_MINIMIZATION_GREEDY_SWITCH_ACTIVATION_THRESHOLD_LOWER_BOUND = Integer.valueOf(0);

  /**
   * By default it is decided automatically if the greedy switch is activated or not.
   * The decision is based on whether the size of the input graph (without dummy nodes)
   * is smaller than the value of this option. A '0' enforces the activation.
   */
  public static final IProperty<Integer> CROSSING_MINIMIZATION_GREEDY_SWITCH_ACTIVATION_THRESHOLD = new Property<Integer>(
            "org.eclipse.elk.layered.crossingMinimization.greedySwitch.activationThreshold",
            CROSSING_MINIMIZATION_GREEDY_SWITCH_ACTIVATION_THRESHOLD_DEFAULT,
            CROSSING_MINIMIZATION_GREEDY_SWITCH_ACTIVATION_THRESHOLD_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #CROSSING_MINIMIZATION_GREEDY_SWITCH_TYPE}.
   */
  private static final GreedySwitchType CROSSING_MINIMIZATION_GREEDY_SWITCH_TYPE_DEFAULT = GreedySwitchType.TWO_SIDED;

  /**
   * Greedy Switch strategy for crossing minimization. The greedy switch heuristic is executed
   * after the regular crossing minimization as a post-processor.
   * Note that if 'hierarchyHandling' is set to 'INCLUDE_CHILDREN', the 'greedySwitchHierarchical.type'
   * option must be used.
   */
  public static final IProperty<GreedySwitchType> CROSSING_MINIMIZATION_GREEDY_SWITCH_TYPE = new Property<GreedySwitchType>(
            "org.eclipse.elk.layered.crossingMinimization.greedySwitch.type",
            CROSSING_MINIMIZATION_GREEDY_SWITCH_TYPE_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #CROSSING_MINIMIZATION_GREEDY_SWITCH_HIERARCHICAL_TYPE}.
   */
  private static final GreedySwitchType CROSSING_MINIMIZATION_GREEDY_SWITCH_HIERARCHICAL_TYPE_DEFAULT = GreedySwitchType.OFF;

  /**
   * Activates the greedy switch heuristic in case hierarchical layout is used.
   * The differences to the non-hierarchical case (see 'greedySwitch.type') are:
   * 1) greedy switch is inactive by default,
   * 3) only the option value set on the node at which hierarchical layout starts is relevant, and
   * 2) if it's activated by the user, it properly addresses hierarchy-crossing edges.
   */
  public static final IProperty<GreedySwitchType> CROSSING_MINIMIZATION_GREEDY_SWITCH_HIERARCHICAL_TYPE = new Property<GreedySwitchType>(
            "org.eclipse.elk.layered.crossingMinimization.greedySwitchHierarchical.type",
            CROSSING_MINIMIZATION_GREEDY_SWITCH_HIERARCHICAL_TYPE_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #NODE_PLACEMENT_STRATEGY}.
   */
  private static final NodePlacementStrategy NODE_PLACEMENT_STRATEGY_DEFAULT = NodePlacementStrategy.BRANDES_KOEPF;

  /**
   * Strategy for node placement.
   */
  public static final IProperty<NodePlacementStrategy> NODE_PLACEMENT_STRATEGY = new Property<NodePlacementStrategy>(
            "org.eclipse.elk.layered.nodePlacement.strategy",
            NODE_PLACEMENT_STRATEGY_DEFAULT,
            null,
            null);

  /**
   * Favor straight edges over a balanced node placement.
   * The default behavior is determined automatically based on the used 'edgeRouting'.
   * For an orthogonal style it is set to true, for all other styles to false.
   */
  public static final IProperty<Boolean> NODE_PLACEMENT_FAVOR_STRAIGHT_EDGES = new Property<Boolean>(
            "org.eclipse.elk.layered.nodePlacement.favorStraightEdges");

  /**
   * Default value for {@link #NODE_PLACEMENT_BK_EDGE_STRAIGHTENING}.
   */
  private static final EdgeStraighteningStrategy NODE_PLACEMENT_BK_EDGE_STRAIGHTENING_DEFAULT = EdgeStraighteningStrategy.IMPROVE_STRAIGHTNESS;

  /**
   * Specifies whether the Brandes Koepf node placer tries to increase the number of straight edges
   * at the expense of diagram size.
   * There is a subtle difference to the 'favorStraightEdges' option, which decides whether
   * a balanced placement of the nodes is desired, or not. In bk terms this means combining the four
   * alignments into a single balanced one, or not. This option on the other hand tries to straighten
   * additional edges during the creation of each of the four alignments.
   */
  public static final IProperty<EdgeStraighteningStrategy> NODE_PLACEMENT_BK_EDGE_STRAIGHTENING = new Property<EdgeStraighteningStrategy>(
            "org.eclipse.elk.layered.nodePlacement.bk.edgeStraightening",
            NODE_PLACEMENT_BK_EDGE_STRAIGHTENING_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #NODE_PLACEMENT_BK_FIXED_ALIGNMENT}.
   */
  private static final FixedAlignment NODE_PLACEMENT_BK_FIXED_ALIGNMENT_DEFAULT = FixedAlignment.NONE;

  /**
   * Tells the BK node placer to use a certain alignment (out of its four) instead of the
   * one producing the smallest height, or the combination of all four.
   */
  public static final IProperty<FixedAlignment> NODE_PLACEMENT_BK_FIXED_ALIGNMENT = new Property<FixedAlignment>(
            "org.eclipse.elk.layered.nodePlacement.bk.fixedAlignment",
            NODE_PLACEMENT_BK_FIXED_ALIGNMENT_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #NODE_PLACEMENT_LINEAR_SEGMENTS_DEFLECTION_DAMPENING}.
   */
  private static final double NODE_PLACEMENT_LINEAR_SEGMENTS_DEFLECTION_DAMPENING_DEFAULT = 0.3;

  /**
   * Lower bound value for {@link #NODE_PLACEMENT_LINEAR_SEGMENTS_DEFLECTION_DAMPENING}.
   */
  private static final Comparable<? super Double> NODE_PLACEMENT_LINEAR_SEGMENTS_DEFLECTION_DAMPENING_LOWER_BOUND = ExclusiveBounds.greaterThan(0);

  /**
   * Dampens the movement of nodes to keep the diagram from getting too large.
   */
  public static final IProperty<Double> NODE_PLACEMENT_LINEAR_SEGMENTS_DEFLECTION_DAMPENING = new Property<Double>(
            "org.eclipse.elk.layered.nodePlacement.linearSegments.deflectionDampening",
            NODE_PLACEMENT_LINEAR_SEGMENTS_DEFLECTION_DAMPENING_DEFAULT,
            NODE_PLACEMENT_LINEAR_SEGMENTS_DEFLECTION_DAMPENING_LOWER_BOUND,
            null);

  /**
   * Aims at shorter and straighter edges. Two configurations are possible:
   * (a) allow ports to move freely on the side they are assigned to (the order is always defined beforehand),
   * (b) additionally allow to enlarge a node wherever it helps.
   * If this option is not configured for a node, the 'nodeFlexibility.default' value is used,
   * which is specified for the node's parent.
   */
  public static final IProperty<NodeFlexibility> NODE_PLACEMENT_NETWORK_SIMPLEX_NODE_FLEXIBILITY = new Property<NodeFlexibility>(
            "org.eclipse.elk.layered.nodePlacement.networkSimplex.nodeFlexibility");

  /**
   * Default value for {@link #NODE_PLACEMENT_NETWORK_SIMPLEX_NODE_FLEXIBILITY_DEFAULT}.
   */
  private static final NodeFlexibility NODE_PLACEMENT_NETWORK_SIMPLEX_NODE_FLEXIBILITY_DEFAULT_DEFAULT = NodeFlexibility.NONE;

  /**
   * Default value of the 'nodeFlexibility' option for the children of a hierarchical node.
   */
  public static final IProperty<NodeFlexibility> NODE_PLACEMENT_NETWORK_SIMPLEX_NODE_FLEXIBILITY_DEFAULT = new Property<NodeFlexibility>(
            "org.eclipse.elk.layered.nodePlacement.networkSimplex.nodeFlexibility.default",
            NODE_PLACEMENT_NETWORK_SIMPLEX_NODE_FLEXIBILITY_DEFAULT_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #EDGE_ROUTING_SELF_LOOP_DISTRIBUTION}.
   */
  private static final SelfLoopDistributionStrategy EDGE_ROUTING_SELF_LOOP_DISTRIBUTION_DEFAULT = SelfLoopDistributionStrategy.NORTH;

  /**
   * Alter the distribution of the loops around the node. It only takes effect for PortConstraints.FREE.
   */
  public static final IProperty<SelfLoopDistributionStrategy> EDGE_ROUTING_SELF_LOOP_DISTRIBUTION = new Property<SelfLoopDistributionStrategy>(
            "org.eclipse.elk.layered.edgeRouting.selfLoopDistribution",
            EDGE_ROUTING_SELF_LOOP_DISTRIBUTION_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #EDGE_ROUTING_SELF_LOOP_ORDERING}.
   */
  private static final SelfLoopOrderingStrategy EDGE_ROUTING_SELF_LOOP_ORDERING_DEFAULT = SelfLoopOrderingStrategy.STACKED;

  /**
   * Alter the ordering of the loops they can either be stacked or sequenced.
   * It only takes effect for PortConstraints.FREE.
   */
  public static final IProperty<SelfLoopOrderingStrategy> EDGE_ROUTING_SELF_LOOP_ORDERING = new Property<SelfLoopOrderingStrategy>(
            "org.eclipse.elk.layered.edgeRouting.selfLoopOrdering",
            EDGE_ROUTING_SELF_LOOP_ORDERING_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #EDGE_ROUTING_SPLINES_MODE}.
   */
  private static final SplineRoutingMode EDGE_ROUTING_SPLINES_MODE_DEFAULT = SplineRoutingMode.SLOPPY;

  /**
   * Specifies the way control points are assembled for each individual edge.
   * CONSERVATIVE ensures that edges are properly routed around the nodes
   * but feels rather orthogonal at times.
   * SLOPPY uses fewer control points to obtain curvier edge routes but may result in
   * edges overlapping nodes.
   */
  public static final IProperty<SplineRoutingMode> EDGE_ROUTING_SPLINES_MODE = new Property<SplineRoutingMode>(
            "org.eclipse.elk.layered.edgeRouting.splines.mode",
            EDGE_ROUTING_SPLINES_MODE_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #EDGE_ROUTING_SPLINES_SLOPPY_LAYER_SPACING_FACTOR}.
   */
  private static final double EDGE_ROUTING_SPLINES_SLOPPY_LAYER_SPACING_FACTOR_DEFAULT = 0.2;

  /**
   * Spacing factor for routing area between layers when using sloppy spline routing.
   */
  public static final IProperty<Double> EDGE_ROUTING_SPLINES_SLOPPY_LAYER_SPACING_FACTOR = new Property<Double>(
            "org.eclipse.elk.layered.edgeRouting.splines.sloppy.layerSpacingFactor",
            EDGE_ROUTING_SPLINES_SLOPPY_LAYER_SPACING_FACTOR_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #EDGE_ROUTING_POLYLINE_SLOPED_EDGE_ZONE_WIDTH}.
   */
  private static final double EDGE_ROUTING_POLYLINE_SLOPED_EDGE_ZONE_WIDTH_DEFAULT = 2.0;

  /**
   * Width of the strip to the left and to the right of each layer where the polyline edge router
   * is allowed to refrain from ensuring that edges are routed horizontally. This prevents awkward
   * bend points for nodes that extent almost to the edge of their layer.
   */
  public static final IProperty<Double> EDGE_ROUTING_POLYLINE_SLOPED_EDGE_ZONE_WIDTH = new Property<Double>(
            "org.eclipse.elk.layered.edgeRouting.polyline.slopedEdgeZoneWidth",
            EDGE_ROUTING_POLYLINE_SLOPED_EDGE_ZONE_WIDTH_DEFAULT,
            null,
            null);

  /**
   * Lower bound value for {@link #SPACING_BASE_VALUE}.
   */
  private static final Comparable<? super Double> SPACING_BASE_VALUE_LOWER_BOUND = Double.valueOf(0.0);

  /**
   * An optional base value for all other layout options of the 'spacing' group. It can be used to conveniently
   * alter the overall 'spaciousness' of the drawing. Whenever an explicit value is set for the other layout
   * options, this base value will have no effect. The base value is not inherited, i.e. it must be set for
   * each hierarchical node.
   */
  public static final IProperty<Double> SPACING_BASE_VALUE = new Property<Double>(
            "org.eclipse.elk.layered.spacing.baseValue",
            null,
            SPACING_BASE_VALUE_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #SPACING_EDGE_NODE_BETWEEN_LAYERS}.
   */
  private static final double SPACING_EDGE_NODE_BETWEEN_LAYERS_DEFAULT = 10;

  /**
   * Lower bound value for {@link #SPACING_EDGE_NODE_BETWEEN_LAYERS}.
   */
  private static final Comparable<? super Double> SPACING_EDGE_NODE_BETWEEN_LAYERS_LOWER_BOUND = Double.valueOf(0.0);

  /**
   * The spacing to be preserved between nodes and edges that are routed next to the node's layer.
   * For the spacing between nodes and edges that cross the node's layer 'spacing.edgeNode' is used.
   */
  public static final IProperty<Double> SPACING_EDGE_NODE_BETWEEN_LAYERS = new Property<Double>(
            "org.eclipse.elk.layered.spacing.edgeNodeBetweenLayers",
            SPACING_EDGE_NODE_BETWEEN_LAYERS_DEFAULT,
            SPACING_EDGE_NODE_BETWEEN_LAYERS_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #SPACING_EDGE_EDGE_BETWEEN_LAYERS}.
   */
  private static final double SPACING_EDGE_EDGE_BETWEEN_LAYERS_DEFAULT = 10;

  /**
   * Lower bound value for {@link #SPACING_EDGE_EDGE_BETWEEN_LAYERS}.
   */
  private static final Comparable<? super Double> SPACING_EDGE_EDGE_BETWEEN_LAYERS_LOWER_BOUND = Double.valueOf(0.0);

  /**
   * Spacing to be preserved between pairs of edges that are routed between the same pair of layers.
   * Note that 'spacing.edgeEdge' is used for the spacing between pairs of edges crossing the same layer.
   */
  public static final IProperty<Double> SPACING_EDGE_EDGE_BETWEEN_LAYERS = new Property<Double>(
            "org.eclipse.elk.layered.spacing.edgeEdgeBetweenLayers",
            SPACING_EDGE_EDGE_BETWEEN_LAYERS_DEFAULT,
            SPACING_EDGE_EDGE_BETWEEN_LAYERS_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #SPACING_NODE_NODE_BETWEEN_LAYERS}.
   */
  private static final double SPACING_NODE_NODE_BETWEEN_LAYERS_DEFAULT = 20;

  /**
   * Lower bound value for {@link #SPACING_NODE_NODE_BETWEEN_LAYERS}.
   */
  private static final Comparable<? super Double> SPACING_NODE_NODE_BETWEEN_LAYERS_LOWER_BOUND = Double.valueOf(0.0);

  /**
   * The spacing to be preserved between any pair of nodes of two adjacent layers.
   * Note that 'spacing.nodeNode' is used for the spacing between nodes within the layer itself.
   */
  public static final IProperty<Double> SPACING_NODE_NODE_BETWEEN_LAYERS = new Property<Double>(
            "org.eclipse.elk.layered.spacing.nodeNodeBetweenLayers",
            SPACING_NODE_NODE_BETWEEN_LAYERS_DEFAULT,
            SPACING_NODE_NODE_BETWEEN_LAYERS_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #PRIORITY_DIRECTION}.
   */
  private static final int PRIORITY_DIRECTION_DEFAULT = 0;

  /**
   * Lower bound value for {@link #PRIORITY_DIRECTION}.
   */
  private static final Comparable<? super Integer> PRIORITY_DIRECTION_LOWER_BOUND = Integer.valueOf(0);

  /**
   * Defines how important it is to have a certain edge point into the direction of the overall layout.
   * This option is evaluated during the cycle breaking phase.
   */
  public static final IProperty<Integer> PRIORITY_DIRECTION = new Property<Integer>(
            "org.eclipse.elk.layered.priority.direction",
            PRIORITY_DIRECTION_DEFAULT,
            PRIORITY_DIRECTION_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #PRIORITY_SHORTNESS}.
   */
  private static final int PRIORITY_SHORTNESS_DEFAULT = 0;

  /**
   * Lower bound value for {@link #PRIORITY_SHORTNESS}.
   */
  private static final Comparable<? super Integer> PRIORITY_SHORTNESS_LOWER_BOUND = Integer.valueOf(0);

  /**
   * Defines how important it is to keep an edge as short as possible.
   * This option is evaluated during the layering phase.
   */
  public static final IProperty<Integer> PRIORITY_SHORTNESS = new Property<Integer>(
            "org.eclipse.elk.layered.priority.shortness",
            PRIORITY_SHORTNESS_DEFAULT,
            PRIORITY_SHORTNESS_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #PRIORITY_STRAIGHTNESS}.
   */
  private static final int PRIORITY_STRAIGHTNESS_DEFAULT = 0;

  /**
   * Lower bound value for {@link #PRIORITY_STRAIGHTNESS}.
   */
  private static final Comparable<? super Integer> PRIORITY_STRAIGHTNESS_LOWER_BOUND = Integer.valueOf(0);

  /**
   * Defines how important it is to keep an edge straight, i.e. aligned with one of the two axes.
   * This option is evaluated during node placement.
   */
  public static final IProperty<Integer> PRIORITY_STRAIGHTNESS = new Property<Integer>(
            "org.eclipse.elk.layered.priority.straightness",
            PRIORITY_STRAIGHTNESS_DEFAULT,
            PRIORITY_STRAIGHTNESS_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #COMPACTION_CONNECTED_COMPONENTS}.
   */
  private static final boolean COMPACTION_CONNECTED_COMPONENTS_DEFAULT = false;

  /**
   * Tries to further compact components (disconnected sub-graphs).
   */
  public static final IProperty<Boolean> COMPACTION_CONNECTED_COMPONENTS = new Property<Boolean>(
            "org.eclipse.elk.layered.compaction.connectedComponents",
            COMPACTION_CONNECTED_COMPONENTS_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #COMPACTION_POST_COMPACTION_STRATEGY}.
   */
  private static final GraphCompactionStrategy COMPACTION_POST_COMPACTION_STRATEGY_DEFAULT = GraphCompactionStrategy.NONE;

  /**
   * Specifies whether and how post-process compaction is applied.
   */
  public static final IProperty<GraphCompactionStrategy> COMPACTION_POST_COMPACTION_STRATEGY = new Property<GraphCompactionStrategy>(
            "org.eclipse.elk.layered.compaction.postCompaction.strategy",
            COMPACTION_POST_COMPACTION_STRATEGY_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #COMPACTION_POST_COMPACTION_CONSTRAINTS}.
   */
  private static final ConstraintCalculationStrategy COMPACTION_POST_COMPACTION_CONSTRAINTS_DEFAULT = ConstraintCalculationStrategy.SCANLINE;

  /**
   * Specifies whether and how post-process compaction is applied.
   */
  public static final IProperty<ConstraintCalculationStrategy> COMPACTION_POST_COMPACTION_CONSTRAINTS = new Property<ConstraintCalculationStrategy>(
            "org.eclipse.elk.layered.compaction.postCompaction.constraints",
            COMPACTION_POST_COMPACTION_CONSTRAINTS_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #HIGH_DEGREE_NODES_TREATMENT}.
   */
  private static final boolean HIGH_DEGREE_NODES_TREATMENT_DEFAULT = false;

  /**
   * Makes room around high degree nodes to place leafs and trees.
   */
  public static final IProperty<Boolean> HIGH_DEGREE_NODES_TREATMENT = new Property<Boolean>(
            "org.eclipse.elk.layered.highDegreeNodes.treatment",
            HIGH_DEGREE_NODES_TREATMENT_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #HIGH_DEGREE_NODES_THRESHOLD}.
   */
  private static final int HIGH_DEGREE_NODES_THRESHOLD_DEFAULT = 16;

  /**
   * Lower bound value for {@link #HIGH_DEGREE_NODES_THRESHOLD}.
   */
  private static final Comparable<? super Integer> HIGH_DEGREE_NODES_THRESHOLD_LOWER_BOUND = Integer.valueOf(0);

  /**
   * Whether a node is considered to have a high degree.
   */
  public static final IProperty<Integer> HIGH_DEGREE_NODES_THRESHOLD = new Property<Integer>(
            "org.eclipse.elk.layered.highDegreeNodes.threshold",
            HIGH_DEGREE_NODES_THRESHOLD_DEFAULT,
            HIGH_DEGREE_NODES_THRESHOLD_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #HIGH_DEGREE_NODES_TREE_HEIGHT}.
   */
  private static final int HIGH_DEGREE_NODES_TREE_HEIGHT_DEFAULT = 5;

  /**
   * Lower bound value for {@link #HIGH_DEGREE_NODES_TREE_HEIGHT}.
   */
  private static final Comparable<? super Integer> HIGH_DEGREE_NODES_TREE_HEIGHT_LOWER_BOUND = Integer.valueOf(0);

  /**
   * Maximum height of a subtree connected to a high degree node to be moved to separate layers.
   */
  public static final IProperty<Integer> HIGH_DEGREE_NODES_TREE_HEIGHT = new Property<Integer>(
            "org.eclipse.elk.layered.highDegreeNodes.treeHeight",
            HIGH_DEGREE_NODES_TREE_HEIGHT_DEFAULT,
            HIGH_DEGREE_NODES_TREE_HEIGHT_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #WRAPPING_STRATEGY}.
   */
  private static final WrappingStrategy WRAPPING_STRATEGY_DEFAULT = WrappingStrategy.OFF;

  /**
   * For certain graphs and certain prescribed drawing areas it may be desirable to
   * split the laid out graph into chunks that are placed side by side.
   * The edges that connect different chunks are 'wrapped' around from the end of
   * one chunk to the start of the other chunk.
   * The points between the chunks are referred to as 'cuts'.
   */
  public static final IProperty<WrappingStrategy> WRAPPING_STRATEGY = new Property<WrappingStrategy>(
            "org.eclipse.elk.layered.wrapping.strategy",
            WRAPPING_STRATEGY_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #WRAPPING_ADDITIONAL_EDGE_SPACING}.
   */
  private static final double WRAPPING_ADDITIONAL_EDGE_SPACING_DEFAULT = 10;

  /**
   * To visually separate edges that are wrapped from regularly routed edges an additional spacing value
   * can be specified in form of this layout option. The spacing is added to the regular edgeNode spacing.
   */
  public static final IProperty<Double> WRAPPING_ADDITIONAL_EDGE_SPACING = new Property<Double>(
            "org.eclipse.elk.layered.wrapping.additionalEdgeSpacing",
            WRAPPING_ADDITIONAL_EDGE_SPACING_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #WRAPPING_CORRECTION_FACTOR}.
   */
  private static final double WRAPPING_CORRECTION_FACTOR_DEFAULT = 1.0;

  /**
   * At times and for certain types of graphs the executed wrapping may produce results that
   * are consistently biased in the same fashion: either wrapping to often or to rarely.
   * This factor can be used to correct the bias. Internally, it is simply multiplied with
   * the 'aspect ratio' layout option.
   */
  public static final IProperty<Double> WRAPPING_CORRECTION_FACTOR = new Property<Double>(
            "org.eclipse.elk.layered.wrapping.correctionFactor",
            WRAPPING_CORRECTION_FACTOR_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #WRAPPING_CUTTING_STRATEGY}.
   */
  private static final CuttingStrategy WRAPPING_CUTTING_STRATEGY_DEFAULT = CuttingStrategy.MSD;

  /**
   * The strategy by which the layer indexes are determined at which the layering crumbles into chunks.
   */
  public static final IProperty<CuttingStrategy> WRAPPING_CUTTING_STRATEGY = new Property<CuttingStrategy>(
            "org.eclipse.elk.layered.wrapping.cutting.strategy",
            WRAPPING_CUTTING_STRATEGY_DEFAULT,
            null,
            null);

  /**
   * Allows the user to specify her own cuts for a certain graph.
   */
  public static final IProperty<List<Integer>> WRAPPING_CUTTING_CUTS = new Property<List<Integer>>(
            "org.eclipse.elk.layered.wrapping.cutting.cuts");

  /**
   * Default value for {@link #WRAPPING_CUTTING_MSD_FREEDOM}.
   */
  private static final Integer WRAPPING_CUTTING_MSD_FREEDOM_DEFAULT = Integer.valueOf(1);

  /**
   * Lower bound value for {@link #WRAPPING_CUTTING_MSD_FREEDOM}.
   */
  private static final Comparable<? super Integer> WRAPPING_CUTTING_MSD_FREEDOM_LOWER_BOUND = Integer.valueOf(0);

  /**
   * The MSD cutting strategy starts with an initial guess on the number of chunks the graph
   * should be split into. The freedom specifies how much the strategy may deviate from this guess.
   * E.g. if an initial number of 3 is computed, a freedom of 1 allows 2, 3, and 4 cuts.
   */
  public static final IProperty<Integer> WRAPPING_CUTTING_MSD_FREEDOM = new Property<Integer>(
            "org.eclipse.elk.layered.wrapping.cutting.msd.freedom",
            WRAPPING_CUTTING_MSD_FREEDOM_DEFAULT,
            WRAPPING_CUTTING_MSD_FREEDOM_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #WRAPPING_VALIDIFY_STRATEGY}.
   */
  private static final ValidifyStrategy WRAPPING_VALIDIFY_STRATEGY_DEFAULT = ValidifyStrategy.GREEDY;

  /**
   * When wrapping graphs, one can specify indices that are not allowed as split points.
   * The validification strategy makes sure every computed split point is allowed.
   */
  public static final IProperty<ValidifyStrategy> WRAPPING_VALIDIFY_STRATEGY = new Property<ValidifyStrategy>(
            "org.eclipse.elk.layered.wrapping.validify.strategy",
            WRAPPING_VALIDIFY_STRATEGY_DEFAULT,
            null,
            null);

  public static final IProperty<List<Integer>> WRAPPING_VALIDIFY_FORBIDDEN_INDICES = new Property<List<Integer>>(
            "org.eclipse.elk.layered.wrapping.validify.forbiddenIndices");

  /**
   * Default value for {@link #WRAPPING_MULTI_EDGE_IMPROVE_CUTS}.
   */
  private static final boolean WRAPPING_MULTI_EDGE_IMPROVE_CUTS_DEFAULT = true;

  /**
   * For general graphs it is important that not too many edges wrap backwards.
   * Thus a compromise between evenly-distributed cuts and the total number of cut edges is sought.
   */
  public static final IProperty<Boolean> WRAPPING_MULTI_EDGE_IMPROVE_CUTS = new Property<Boolean>(
            "org.eclipse.elk.layered.wrapping.multiEdge.improveCuts",
            WRAPPING_MULTI_EDGE_IMPROVE_CUTS_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #WRAPPING_MULTI_EDGE_DISTANCE_PENALTY}.
   */
  private static final double WRAPPING_MULTI_EDGE_DISTANCE_PENALTY_DEFAULT = 2.0;

  public static final IProperty<Double> WRAPPING_MULTI_EDGE_DISTANCE_PENALTY = new Property<Double>(
            "org.eclipse.elk.layered.wrapping.multiEdge.distancePenalty",
            WRAPPING_MULTI_EDGE_DISTANCE_PENALTY_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #WRAPPING_MULTI_EDGE_IMPROVE_WRAPPED_EDGES}.
   */
  private static final boolean WRAPPING_MULTI_EDGE_IMPROVE_WRAPPED_EDGES_DEFAULT = true;

  /**
   * The initial wrapping is performed in a very simple way. As a consequence, edges that wrap from
   * one chunk to another may be unnecessarily long. Activating this option tries to shorten such edges.
   */
  public static final IProperty<Boolean> WRAPPING_MULTI_EDGE_IMPROVE_WRAPPED_EDGES = new Property<Boolean>(
            "org.eclipse.elk.layered.wrapping.multiEdge.improveWrappedEdges",
            WRAPPING_MULTI_EDGE_IMPROVE_WRAPPED_EDGES_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #EDGE_LABELS_SIDE_SELECTION}.
   */
  private static final EdgeLabelSideSelection EDGE_LABELS_SIDE_SELECTION_DEFAULT = EdgeLabelSideSelection.SMART_DOWN;

  /**
   * Method to decide on edge label sides.
   */
  public static final IProperty<EdgeLabelSideSelection> EDGE_LABELS_SIDE_SELECTION = new Property<EdgeLabelSideSelection>(
            "org.eclipse.elk.layered.edgeLabels.sideSelection",
            EDGE_LABELS_SIDE_SELECTION_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #EDGE_LABELS_CENTER_LABEL_PLACEMENT_STRATEGY}.
   */
  private static final CenterEdgeLabelPlacementStrategy EDGE_LABELS_CENTER_LABEL_PLACEMENT_STRATEGY_DEFAULT = CenterEdgeLabelPlacementStrategy.MEDIAN_LAYER;

  /**
   * Determines in which layer center labels of long edges should be placed.
   */
  public static final IProperty<CenterEdgeLabelPlacementStrategy> EDGE_LABELS_CENTER_LABEL_PLACEMENT_STRATEGY = new Property<CenterEdgeLabelPlacementStrategy>(
            "org.eclipse.elk.layered.edgeLabels.centerLabelPlacementStrategy",
            EDGE_LABELS_CENTER_LABEL_PLACEMENT_STRATEGY_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #CONSIDER_MODEL_ORDER_STRATEGY}.
   */
  private static final OrderingStrategy CONSIDER_MODEL_ORDER_STRATEGY_DEFAULT = OrderingStrategy.NONE;

  /**
   * Preserves the order of nodes and edges in the model file if this does not lead to additional edge
   * crossings. Depending on the strategy this is not always possible since the node and edge order might be
   * conflicting.
   */
  public static final IProperty<OrderingStrategy> CONSIDER_MODEL_ORDER_STRATEGY = new Property<OrderingStrategy>(
            "org.eclipse.elk.layered.considerModelOrder.strategy",
            CONSIDER_MODEL_ORDER_STRATEGY_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #CONSIDER_MODEL_ORDER_PORT_MODEL_ORDER}.
   */
  private static final boolean CONSIDER_MODEL_ORDER_PORT_MODEL_ORDER_DEFAULT = false;

  /**
   * If disabled the port order of output ports is derived from the edge order and input ports are ordered by
   * their incoming connections. If enabled all ports are ordered by the port model order.
   */
  public static final IProperty<Boolean> CONSIDER_MODEL_ORDER_PORT_MODEL_ORDER = new Property<Boolean>(
            "org.eclipse.elk.layered.considerModelOrder.portModelOrder",
            CONSIDER_MODEL_ORDER_PORT_MODEL_ORDER_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #CONSIDER_MODEL_ORDER_NO_MODEL_ORDER}.
   */
  private static final boolean CONSIDER_MODEL_ORDER_NO_MODEL_ORDER_DEFAULT = false;

  /**
   * Set on a node to not set a model order for this node even though it is a real node.
   */
  public static final IProperty<Boolean> CONSIDER_MODEL_ORDER_NO_MODEL_ORDER = new Property<Boolean>(
            "org.eclipse.elk.layered.considerModelOrder.noModelOrder",
            CONSIDER_MODEL_ORDER_NO_MODEL_ORDER_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #CONSIDER_MODEL_ORDER_COMPONENTS}.
   */
  private static final ComponentOrderingStrategy CONSIDER_MODEL_ORDER_COMPONENTS_DEFAULT = ComponentOrderingStrategy.NONE;

  /**
   * If set to NONE the usual ordering strategy (by cumulative node priority and size of nodes) is used.
   * INSIDE_PORT_SIDES orders the components with external ports only inside the groups with the same port side.
   * FORCE_MODEL_ORDER enforces the mode order on components. This option might produce bad alignments and sub
   * optimal drawings in terms of used area since the ordering should be respected.
   */
  public static final IProperty<ComponentOrderingStrategy> CONSIDER_MODEL_ORDER_COMPONENTS = new Property<ComponentOrderingStrategy>(
            "org.eclipse.elk.layered.considerModelOrder.components",
            CONSIDER_MODEL_ORDER_COMPONENTS_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #CONSIDER_MODEL_ORDER_LONG_EDGE_STRATEGY}.
   */
  private static final LongEdgeOrderingStrategy CONSIDER_MODEL_ORDER_LONG_EDGE_STRATEGY_DEFAULT = LongEdgeOrderingStrategy.DUMMY_NODE_OVER;

  /**
   * Indicates whether long edges are sorted under, over, or equal to nodes that have no connection to a
   * previous layer in a left-to-right or right-to-left layout. Under and over changes to right and left in a
   * vertical layout.
   */
  public static final IProperty<LongEdgeOrderingStrategy> CONSIDER_MODEL_ORDER_LONG_EDGE_STRATEGY = new Property<LongEdgeOrderingStrategy>(
            "org.eclipse.elk.layered.considerModelOrder.longEdgeStrategy",
            CONSIDER_MODEL_ORDER_LONG_EDGE_STRATEGY_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #CONSIDER_MODEL_ORDER_CROSSING_COUNTER_NODE_INFLUENCE}.
   */
  private static final double CONSIDER_MODEL_ORDER_CROSSING_COUNTER_NODE_INFLUENCE_DEFAULT = 0;

  /**
   * Indicates with what percentage (1 for 100%) violations of the node model order are weighted against the
   * crossings e.g. a value of 0.5 means two model order violations are as important as on edge crossing.
   * This allows some edge crossings in favor of preserving the model order. It is advised to set this value to
   * a very small positive value (e.g. 0.001) to have minimal crossing and a optimal node order. Defaults to no
   * influence (0).
   */
  public static final IProperty<Double> CONSIDER_MODEL_ORDER_CROSSING_COUNTER_NODE_INFLUENCE = new Property<Double>(
            "org.eclipse.elk.layered.considerModelOrder.crossingCounterNodeInfluence",
            CONSIDER_MODEL_ORDER_CROSSING_COUNTER_NODE_INFLUENCE_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #CONSIDER_MODEL_ORDER_CROSSING_COUNTER_PORT_INFLUENCE}.
   */
  private static final double CONSIDER_MODEL_ORDER_CROSSING_COUNTER_PORT_INFLUENCE_DEFAULT = 0;

  /**
   * Indicates with what percentage (1 for 100%) violations of the port model order are weighted against the
   * crossings e.g. a value of 0.5 means two model order violations are as important as on edge crossing.
   * This allows some edge crossings in favor of preserving the model order. It is advised to set this value to
   * a very small positive value (e.g. 0.001) to have minimal crossing and a optimal port order. Defaults to no
   * influence (0).
   */
  public static final IProperty<Double> CONSIDER_MODEL_ORDER_CROSSING_COUNTER_PORT_INFLUENCE = new Property<Double>(
            "org.eclipse.elk.layered.considerModelOrder.crossingCounterPortInfluence",
            CONSIDER_MODEL_ORDER_CROSSING_COUNTER_PORT_INFLUENCE_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #CONSIDER_MODEL_ORDER_GROUP_I_D}.
   */
  private static final int CONSIDER_MODEL_ORDER_GROUP_I_D_DEFAULT = (-1);

  /**
   * Used to store information about the type of Node. (Used in languages that support different node types such as
   * Lingua Franca)
   */
  public static final IProperty<Integer> CONSIDER_MODEL_ORDER_GROUP_I_D = new Property<Integer>(
            "org.eclipse.elk.layered.considerModelOrder.groupID",
            CONSIDER_MODEL_ORDER_GROUP_I_D_DEFAULT,
            null,
            null);

  /**
   * Required value for dependency between {@link #INTERACTIVE_REFERENCE_POINT} and {@link #CYCLE_BREAKING_STRATEGY}.
   */
  private static final CycleBreakingStrategy INTERACTIVE_REFERENCE_POINT_DEP_CYCLE_BREAKING_STRATEGY_0 = CycleBreakingStrategy.INTERACTIVE;

  /**
   * Required value for dependency between {@link #INTERACTIVE_REFERENCE_POINT} and {@link #CROSSING_MINIMIZATION_STRATEGY}.
   */
  private static final CrossingMinimizationStrategy INTERACTIVE_REFERENCE_POINT_DEP_CROSSING_MINIMIZATION_STRATEGY_1 = CrossingMinimizationStrategy.INTERACTIVE;

  /**
   * Required value for dependency between {@link #LAYERING_MIN_WIDTH_UPPER_BOUND_ON_WIDTH} and {@link #LAYERING_STRATEGY}.
   */
  private static final LayeringStrategy LAYERING_MIN_WIDTH_UPPER_BOUND_ON_WIDTH_DEP_LAYERING_STRATEGY_0 = LayeringStrategy.MIN_WIDTH;

  /**
   * Required value for dependency between {@link #LAYERING_MIN_WIDTH_UPPER_LAYER_ESTIMATION_SCALING_FACTOR} and {@link #LAYERING_STRATEGY}.
   */
  private static final LayeringStrategy LAYERING_MIN_WIDTH_UPPER_LAYER_ESTIMATION_SCALING_FACTOR_DEP_LAYERING_STRATEGY_0 = LayeringStrategy.MIN_WIDTH;

  /**
   * Required value for dependency between {@link #LAYERING_COFFMAN_GRAHAM_LAYER_BOUND} and {@link #LAYERING_STRATEGY}.
   */
  private static final LayeringStrategy LAYERING_COFFMAN_GRAHAM_LAYER_BOUND_DEP_LAYERING_STRATEGY_0 = LayeringStrategy.COFFMAN_GRAHAM;

  /**
   * Required value for dependency between {@link #CROSSING_MINIMIZATION_HIERARCHICAL_SWEEPINESS} and {@link #HIERARCHY_HANDLING}.
   */
  private static final HierarchyHandling CROSSING_MINIMIZATION_HIERARCHICAL_SWEEPINESS_DEP_HIERARCHY_HANDLING_0 = HierarchyHandling.INCLUDE_CHILDREN;

  /**
   * Required value for dependency between {@link #CROSSING_MINIMIZATION_SEMI_INTERACTIVE} and {@link #CROSSING_MINIMIZATION_STRATEGY}.
   */
  private static final CrossingMinimizationStrategy CROSSING_MINIMIZATION_SEMI_INTERACTIVE_DEP_CROSSING_MINIMIZATION_STRATEGY_0 = CrossingMinimizationStrategy.LAYER_SWEEP;

  /**
   * Required value for dependency between {@link #CROSSING_MINIMIZATION_GREEDY_SWITCH_TYPE} and {@link #CROSSING_MINIMIZATION_STRATEGY}.
   */
  private static final CrossingMinimizationStrategy CROSSING_MINIMIZATION_GREEDY_SWITCH_TYPE_DEP_CROSSING_MINIMIZATION_STRATEGY_0 = CrossingMinimizationStrategy.LAYER_SWEEP;

  /**
   * Required value for dependency between {@link #CROSSING_MINIMIZATION_GREEDY_SWITCH_HIERARCHICAL_TYPE} and {@link #CROSSING_MINIMIZATION_STRATEGY}.
   */
  private static final CrossingMinimizationStrategy CROSSING_MINIMIZATION_GREEDY_SWITCH_HIERARCHICAL_TYPE_DEP_CROSSING_MINIMIZATION_STRATEGY_0 = CrossingMinimizationStrategy.LAYER_SWEEP;

  /**
   * Required value for dependency between {@link #CROSSING_MINIMIZATION_GREEDY_SWITCH_HIERARCHICAL_TYPE} and {@link #HIERARCHY_HANDLING}.
   */
  private static final HierarchyHandling CROSSING_MINIMIZATION_GREEDY_SWITCH_HIERARCHICAL_TYPE_DEP_HIERARCHY_HANDLING_1 = HierarchyHandling.INCLUDE_CHILDREN;

  /**
   * Required value for dependency between {@link #NODE_PLACEMENT_FAVOR_STRAIGHT_EDGES} and {@link #NODE_PLACEMENT_STRATEGY}.
   */
  private static final NodePlacementStrategy NODE_PLACEMENT_FAVOR_STRAIGHT_EDGES_DEP_NODE_PLACEMENT_STRATEGY_0 = NodePlacementStrategy.NETWORK_SIMPLEX;

  /**
   * Required value for dependency between {@link #NODE_PLACEMENT_FAVOR_STRAIGHT_EDGES} and {@link #NODE_PLACEMENT_STRATEGY}.
   */
  private static final NodePlacementStrategy NODE_PLACEMENT_FAVOR_STRAIGHT_EDGES_DEP_NODE_PLACEMENT_STRATEGY_1 = NodePlacementStrategy.BRANDES_KOEPF;

  /**
   * Required value for dependency between {@link #NODE_PLACEMENT_BK_EDGE_STRAIGHTENING} and {@link #NODE_PLACEMENT_STRATEGY}.
   */
  private static final NodePlacementStrategy NODE_PLACEMENT_BK_EDGE_STRAIGHTENING_DEP_NODE_PLACEMENT_STRATEGY_0 = NodePlacementStrategy.BRANDES_KOEPF;

  /**
   * Required value for dependency between {@link #NODE_PLACEMENT_BK_FIXED_ALIGNMENT} and {@link #NODE_PLACEMENT_STRATEGY}.
   */
  private static final NodePlacementStrategy NODE_PLACEMENT_BK_FIXED_ALIGNMENT_DEP_NODE_PLACEMENT_STRATEGY_0 = NodePlacementStrategy.BRANDES_KOEPF;

  /**
   * Required value for dependency between {@link #NODE_PLACEMENT_LINEAR_SEGMENTS_DEFLECTION_DAMPENING} and {@link #NODE_PLACEMENT_STRATEGY}.
   */
  private static final NodePlacementStrategy NODE_PLACEMENT_LINEAR_SEGMENTS_DEFLECTION_DAMPENING_DEP_NODE_PLACEMENT_STRATEGY_0 = NodePlacementStrategy.LINEAR_SEGMENTS;

  /**
   * Required value for dependency between {@link #NODE_PLACEMENT_NETWORK_SIMPLEX_NODE_FLEXIBILITY} and {@link #NODE_PLACEMENT_STRATEGY}.
   */
  private static final NodePlacementStrategy NODE_PLACEMENT_NETWORK_SIMPLEX_NODE_FLEXIBILITY_DEP_NODE_PLACEMENT_STRATEGY_0 = NodePlacementStrategy.NETWORK_SIMPLEX;

  /**
   * Required value for dependency between {@link #NODE_PLACEMENT_NETWORK_SIMPLEX_NODE_FLEXIBILITY_DEFAULT} and {@link #NODE_PLACEMENT_STRATEGY}.
   */
  private static final NodePlacementStrategy NODE_PLACEMENT_NETWORK_SIMPLEX_NODE_FLEXIBILITY_DEFAULT_DEP_NODE_PLACEMENT_STRATEGY_0 = NodePlacementStrategy.NETWORK_SIMPLEX;

  /**
   * Required value for dependency between {@link #EDGE_ROUTING_SPLINES_MODE} and {@link #EDGE_ROUTING}.
   */
  private static final EdgeRouting EDGE_ROUTING_SPLINES_MODE_DEP_EDGE_ROUTING_0 = EdgeRouting.SPLINES;

  /**
   * Required value for dependency between {@link #EDGE_ROUTING_SPLINES_SLOPPY_LAYER_SPACING_FACTOR} and {@link #EDGE_ROUTING}.
   */
  private static final EdgeRouting EDGE_ROUTING_SPLINES_SLOPPY_LAYER_SPACING_FACTOR_DEP_EDGE_ROUTING_0 = EdgeRouting.SPLINES;

  /**
   * Required value for dependency between {@link #EDGE_ROUTING_SPLINES_SLOPPY_LAYER_SPACING_FACTOR} and {@link #EDGE_ROUTING_SPLINES_MODE}.
   */
  private static final SplineRoutingMode EDGE_ROUTING_SPLINES_SLOPPY_LAYER_SPACING_FACTOR_DEP_EDGE_ROUTING_SPLINES_MODE_1 = SplineRoutingMode.SLOPPY;

  /**
   * Required value for dependency between {@link #EDGE_ROUTING_POLYLINE_SLOPED_EDGE_ZONE_WIDTH} and {@link #EDGE_ROUTING}.
   */
  private static final EdgeRouting EDGE_ROUTING_POLYLINE_SLOPED_EDGE_ZONE_WIDTH_DEP_EDGE_ROUTING_0 = EdgeRouting.POLYLINE;

  /**
   * Required value for dependency between {@link #COMPACTION_CONNECTED_COMPONENTS} and {@link #SEPARATE_CONNECTED_COMPONENTS}.
   */
  private static final boolean COMPACTION_CONNECTED_COMPONENTS_DEP_SEPARATE_CONNECTED_COMPONENTS_0 = true;

  /**
   * Required value for dependency between {@link #HIGH_DEGREE_NODES_THRESHOLD} and {@link #HIGH_DEGREE_NODES_TREATMENT}.
   */
  private static final boolean HIGH_DEGREE_NODES_THRESHOLD_DEP_HIGH_DEGREE_NODES_TREATMENT_0 = true;

  /**
   * Required value for dependency between {@link #HIGH_DEGREE_NODES_TREE_HEIGHT} and {@link #HIGH_DEGREE_NODES_TREATMENT}.
   */
  private static final boolean HIGH_DEGREE_NODES_TREE_HEIGHT_DEP_HIGH_DEGREE_NODES_TREATMENT_0 = true;

  /**
   * Required value for dependency between {@link #WRAPPING_ADDITIONAL_EDGE_SPACING} and {@link #WRAPPING_STRATEGY}.
   */
  private static final WrappingStrategy WRAPPING_ADDITIONAL_EDGE_SPACING_DEP_WRAPPING_STRATEGY_0 = WrappingStrategy.SINGLE_EDGE;

  /**
   * Required value for dependency between {@link #WRAPPING_ADDITIONAL_EDGE_SPACING} and {@link #WRAPPING_STRATEGY}.
   */
  private static final WrappingStrategy WRAPPING_ADDITIONAL_EDGE_SPACING_DEP_WRAPPING_STRATEGY_1 = WrappingStrategy.MULTI_EDGE;

  /**
   * Required value for dependency between {@link #WRAPPING_CORRECTION_FACTOR} and {@link #WRAPPING_STRATEGY}.
   */
  private static final WrappingStrategy WRAPPING_CORRECTION_FACTOR_DEP_WRAPPING_STRATEGY_0 = WrappingStrategy.SINGLE_EDGE;

  /**
   * Required value for dependency between {@link #WRAPPING_CORRECTION_FACTOR} and {@link #WRAPPING_STRATEGY}.
   */
  private static final WrappingStrategy WRAPPING_CORRECTION_FACTOR_DEP_WRAPPING_STRATEGY_1 = WrappingStrategy.MULTI_EDGE;

  /**
   * Required value for dependency between {@link #WRAPPING_CUTTING_STRATEGY} and {@link #WRAPPING_STRATEGY}.
   */
  private static final WrappingStrategy WRAPPING_CUTTING_STRATEGY_DEP_WRAPPING_STRATEGY_0 = WrappingStrategy.SINGLE_EDGE;

  /**
   * Required value for dependency between {@link #WRAPPING_CUTTING_STRATEGY} and {@link #WRAPPING_STRATEGY}.
   */
  private static final WrappingStrategy WRAPPING_CUTTING_STRATEGY_DEP_WRAPPING_STRATEGY_1 = WrappingStrategy.MULTI_EDGE;

  /**
   * Required value for dependency between {@link #WRAPPING_CUTTING_CUTS} and {@link #WRAPPING_CUTTING_STRATEGY}.
   */
  private static final CuttingStrategy WRAPPING_CUTTING_CUTS_DEP_WRAPPING_CUTTING_STRATEGY_0 = CuttingStrategy.MANUAL;

  /**
   * Required value for dependency between {@link #WRAPPING_CUTTING_MSD_FREEDOM} and {@link #WRAPPING_CUTTING_STRATEGY}.
   */
  private static final CuttingStrategy WRAPPING_CUTTING_MSD_FREEDOM_DEP_WRAPPING_CUTTING_STRATEGY_0 = CuttingStrategy.MSD;

  /**
   * Required value for dependency between {@link #WRAPPING_VALIDIFY_STRATEGY} and {@link #WRAPPING_STRATEGY}.
   */
  private static final WrappingStrategy WRAPPING_VALIDIFY_STRATEGY_DEP_WRAPPING_STRATEGY_0 = WrappingStrategy.SINGLE_EDGE;

  /**
   * Required value for dependency between {@link #WRAPPING_VALIDIFY_STRATEGY} and {@link #WRAPPING_STRATEGY}.
   */
  private static final WrappingStrategy WRAPPING_VALIDIFY_STRATEGY_DEP_WRAPPING_STRATEGY_1 = WrappingStrategy.MULTI_EDGE;

  /**
   * Required value for dependency between {@link #WRAPPING_VALIDIFY_FORBIDDEN_INDICES} and {@link #WRAPPING_STRATEGY}.
   */
  private static final WrappingStrategy WRAPPING_VALIDIFY_FORBIDDEN_INDICES_DEP_WRAPPING_STRATEGY_0 = WrappingStrategy.SINGLE_EDGE;

  /**
   * Required value for dependency between {@link #WRAPPING_VALIDIFY_FORBIDDEN_INDICES} and {@link #WRAPPING_STRATEGY}.
   */
  private static final WrappingStrategy WRAPPING_VALIDIFY_FORBIDDEN_INDICES_DEP_WRAPPING_STRATEGY_1 = WrappingStrategy.MULTI_EDGE;

  /**
   * Required value for dependency between {@link #WRAPPING_MULTI_EDGE_IMPROVE_CUTS} and {@link #WRAPPING_STRATEGY}.
   */
  private static final WrappingStrategy WRAPPING_MULTI_EDGE_IMPROVE_CUTS_DEP_WRAPPING_STRATEGY_0 = WrappingStrategy.MULTI_EDGE;

  /**
   * Required value for dependency between {@link #WRAPPING_MULTI_EDGE_DISTANCE_PENALTY} and {@link #WRAPPING_STRATEGY}.
   */
  private static final WrappingStrategy WRAPPING_MULTI_EDGE_DISTANCE_PENALTY_DEP_WRAPPING_STRATEGY_0 = WrappingStrategy.MULTI_EDGE;

  /**
   * Required value for dependency between {@link #WRAPPING_MULTI_EDGE_DISTANCE_PENALTY} and {@link #WRAPPING_MULTI_EDGE_IMPROVE_CUTS}.
   */
  private static final boolean WRAPPING_MULTI_EDGE_DISTANCE_PENALTY_DEP_WRAPPING_MULTI_EDGE_IMPROVE_CUTS_1 = true;

  /**
   * Required value for dependency between {@link #WRAPPING_MULTI_EDGE_IMPROVE_WRAPPED_EDGES} and {@link #WRAPPING_STRATEGY}.
   */
  private static final WrappingStrategy WRAPPING_MULTI_EDGE_IMPROVE_WRAPPED_EDGES_DEP_WRAPPING_STRATEGY_0 = WrappingStrategy.MULTI_EDGE;

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.directionCongruency")
        .group("")
        .name("Direction Congruency")
        .description("Specifies how drawings of the same graph with different layout directions compare to each other: either a natural reading direction is preserved or the drawings are rotated versions of each other.")
        .defaultValue(DIRECTION_CONGRUENCY_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(DirectionCongruency.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.feedbackEdges")
        .group("")
        .name("Feedback Edges")
        .description("Whether feedback edges should be highlighted by routing around the nodes.")
        .defaultValue(FEEDBACK_EDGES_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.interactiveReferencePoint")
        .group("")
        .name("Interactive Reference Point")
        .description("Determines which point of a node is considered by interactive layout phases.")
        .defaultValue(INTERACTIVE_REFERENCE_POINT_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(InteractiveReferencePoint.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.interactiveReferencePoint",
        "org.eclipse.elk.layered.cycleBreaking.strategy",
        INTERACTIVE_REFERENCE_POINT_DEP_CYCLE_BREAKING_STRATEGY_0
    );
    registry.addDependency(
        "org.eclipse.elk.layered.interactiveReferencePoint",
        "org.eclipse.elk.layered.crossingMinimization.strategy",
        INTERACTIVE_REFERENCE_POINT_DEP_CROSSING_MINIMIZATION_STRATEGY_1
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.mergeEdges")
        .group("")
        .name("Merge Edges")
        .description("Edges that have no ports are merged so they touch the connected nodes at the same points. When this option is disabled, one port is created for each edge directly connected to a node. When it is enabled, all such incoming edges share an input port, and all outgoing edges share an output port.")
        .defaultValue(MERGE_EDGES_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.mergeHierarchyEdges")
        .group("")
        .name("Merge Hierarchy-Crossing Edges")
        .description("If hierarchical layout is active, hierarchy-crossing edges use as few hierarchical ports as possible. They are broken by the algorithm, with hierarchical ports inserted as required. Usually, one such port is created for each edge at each hierarchy crossing point. With this option set to true, we try to create as few hierarchical ports as possible in the process. In particular, all edges that form a hyperedge can share a port.")
        .defaultValue(MERGE_HIERARCHY_EDGES_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.allowNonFlowPortsToSwitchSides")
        .group("")
        .name("Allow Non-Flow Ports To Switch Sides")
        .description("Specifies whether non-flow ports may switch sides if their node\'s port constraints are either FIXED_SIDE or FIXED_ORDER. A non-flow port is a port on a side that is not part of the currently configured layout flow. For instance, given a left-to-right layout direction, north and south ports would be considered non-flow ports. Further note that the underlying criterium whether to switch sides or not solely relies on the minimization of edge crossings. Hence, edge length and other aesthetics criteria are not addressed.")
        .defaultValue(ALLOW_NON_FLOW_PORTS_TO_SWITCH_SIDES_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PORTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .legacyIds("org.eclipse.elk.layered.northOrSouthPort")
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.portSortingStrategy")
        .group("")
        .name("Port Sorting Strategy")
        .description("Only relevant for nodes with FIXED_SIDE port constraints. Determines the way a node\'s ports are distributed on the sides of a node if their order is not prescribed. The option is set on parent nodes.")
        .defaultValue(PORT_SORTING_STRATEGY_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(PortSortingStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.thoroughness")
        .group("")
        .name("Thoroughness")
        .description("How much effort should be spent to produce a nice layout.")
        .defaultValue(THOROUGHNESS_DEFAULT)
        .lowerBound(THOROUGHNESS_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.unnecessaryBendpoints")
        .group("")
        .name("Add Unnecessary Bendpoints")
        .description("Adds bend points even if an edge does not change direction. If true, each long edge dummy will contribute a bend point to its edges and hierarchy-crossing edges will always get a bend point where they cross hierarchy boundaries. By default, bend points are only added where an edge changes direction.")
        .defaultValue(UNNECESSARY_BENDPOINTS_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.generatePositionAndLayerIds")
        .group("")
        .name("Generate Position and Layer IDs")
        .description("If enabled position id and layer id are generated, which are usually only used internally when setting the interactiveLayout option. This option should be specified on the root node.")
        .defaultValue(GENERATE_POSITION_AND_LAYER_IDS_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.expanded")
        .group("")
        .name("Node expansion state")
        .description("If the node is expanded this property is true")
        .defaultValue(EXPANDED_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.name")
        .group("")
        .name("Name of the File")
        .description("If the node is expanded this property is true")
        .defaultValue(NAME_DEFAULT)
        .type(LayoutOptionData.Type.STRING)
        .optionClass(String.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.cycleBreaking.strategy")
        .group("cycleBreaking")
        .name("Cycle Breaking Strategy")
        .description("Strategy for cycle breaking. Cycle breaking looks for cycles in the graph and determines which edges to reverse to break the cycles. Reversed edges will end up pointing to the opposite direction of regular edges (that is, reversed edges will point left if edges usually point right).")
        .defaultValue(CYCLE_BREAKING_STRATEGY_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(CycleBreakingStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.layering.strategy")
        .group("layering")
        .name("Node Layering Strategy")
        .description("Strategy for node layering.")
        .defaultValue(LAYERING_STRATEGY_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(LayeringStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.layering.layerConstraint")
        .group("layering")
        .name("Layer Constraint")
        .description("Determines a constraint on the placement of the node regarding the layering.")
        .defaultValue(LAYERING_LAYER_CONSTRAINT_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(LayerConstraint.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.layering.layerChoiceConstraint")
        .group("layering")
        .name("Layer Choice Constraint")
        .description("Allows to set a constraint regarding the layer placement of a node. Let i be the value of teh constraint. Assumed the drawing has n layers and i < n. If set to i, it expresses that the node should be placed in i-th layer. Should i>=n be true then the node is placed in the last layer of the drawing. Note that this option is not part of any of ELK Layered\'s default configurations but is only evaluated as part of the `InteractiveLayeredGraphVisitor`, which must be applied manually or used via the `DiagramLayoutEngine.")
        .defaultValue(LAYERING_LAYER_CHOICE_CONSTRAINT_DEFAULT)
        .lowerBound(LAYERING_LAYER_CHOICE_CONSTRAINT_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.layering.layerId")
        .group("layering")
        .name("Layer ID")
        .description("Layer identifier that was calculated by ELK Layered for a node. This is only generated if interactiveLayot or generatePositionAndLayerIds is set.")
        .defaultValue(LAYERING_LAYER_ID_DEFAULT)
        .lowerBound(LAYERING_LAYER_ID_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.layering.minWidth.upperBoundOnWidth")
        .group("layering.minWidth")
        .name("Upper Bound On Width [MinWidth Layerer]")
        .description("Defines a loose upper bound on the width of the MinWidth layerer. If set to \'-1\' multiple values are tested and the best result is selected.")
        .defaultValue(LAYERING_MIN_WIDTH_UPPER_BOUND_ON_WIDTH_DEFAULT)
        .lowerBound(LAYERING_MIN_WIDTH_UPPER_BOUND_ON_WIDTH_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.layering.minWidth.upperBoundOnWidth",
        "org.eclipse.elk.layered.layering.strategy",
        LAYERING_MIN_WIDTH_UPPER_BOUND_ON_WIDTH_DEP_LAYERING_STRATEGY_0
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.layering.minWidth.upperLayerEstimationScalingFactor")
        .group("layering.minWidth")
        .name("Upper Layer Estimation Scaling Factor [MinWidth Layerer]")
        .description("Multiplied with Upper Bound On Width for defining an upper bound on the width of layers which haven\'t been determined yet, but whose maximum width had been (roughly) estimated by the MinWidth algorithm. Compensates for too high estimations. If set to \'-1\' multiple values are tested and the best result is selected.")
        .defaultValue(LAYERING_MIN_WIDTH_UPPER_LAYER_ESTIMATION_SCALING_FACTOR_DEFAULT)
        .lowerBound(LAYERING_MIN_WIDTH_UPPER_LAYER_ESTIMATION_SCALING_FACTOR_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.layering.minWidth.upperLayerEstimationScalingFactor",
        "org.eclipse.elk.layered.layering.strategy",
        LAYERING_MIN_WIDTH_UPPER_LAYER_ESTIMATION_SCALING_FACTOR_DEP_LAYERING_STRATEGY_0
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.layering.nodePromotion.strategy")
        .group("layering.nodePromotion")
        .name("Node Promotion Strategy")
        .description("Reduces number of dummy nodes after layering phase (if possible).")
        .defaultValue(LAYERING_NODE_PROMOTION_STRATEGY_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(NodePromotionStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.layering.nodePromotion.maxIterations")
        .group("layering.nodePromotion")
        .name("Max Node Promotion Iterations")
        .description("Limits the number of iterations for node promotion.")
        .defaultValue(LAYERING_NODE_PROMOTION_MAX_ITERATIONS_DEFAULT)
        .lowerBound(LAYERING_NODE_PROMOTION_MAX_ITERATIONS_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.layering.nodePromotion.maxIterations",
        "org.eclipse.elk.layered.layering.nodePromotion.strategy",
        null
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.layering.coffmanGraham.layerBound")
        .group("layering.coffmanGraham")
        .name("Layer Bound")
        .description("The maximum number of nodes allowed per layer.")
        .defaultValue(LAYERING_COFFMAN_GRAHAM_LAYER_BOUND_DEFAULT)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.layering.coffmanGraham.layerBound",
        "org.eclipse.elk.layered.layering.strategy",
        LAYERING_COFFMAN_GRAHAM_LAYER_BOUND_DEP_LAYERING_STRATEGY_0
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.crossingMinimization.strategy")
        .group("crossingMinimization")
        .name("Crossing Minimization Strategy")
        .description("Strategy for crossing minimization.")
        .defaultValue(CROSSING_MINIMIZATION_STRATEGY_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(CrossingMinimizationStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.crossingMinimization.forceNodeModelOrder")
        .group("crossingMinimization")
        .name("Force Node Model Order")
        .description("The node order given by the model does not change to produce a better layout. E.g. if node A is before node B in the model this is not changed during crossing minimization. This assumes that the node model order is already respected before crossing minimization. This can be achieved by setting considerModelOrder.strategy to NODES_AND_EDGES.")
        .defaultValue(CROSSING_MINIMIZATION_FORCE_NODE_MODEL_ORDER_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.crossingMinimization.hierarchicalSweepiness")
        .group("crossingMinimization")
        .name("Hierarchical Sweepiness")
        .description("How likely it is to use cross-hierarchy (1) vs bottom-up (-1).")
        .defaultValue(CROSSING_MINIMIZATION_HIERARCHICAL_SWEEPINESS_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.crossingMinimization.hierarchicalSweepiness",
        "org.eclipse.elk.hierarchyHandling",
        CROSSING_MINIMIZATION_HIERARCHICAL_SWEEPINESS_DEP_HIERARCHY_HANDLING_0
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.crossingMinimization.semiInteractive")
        .group("crossingMinimization")
        .name("Semi-Interactive Crossing Minimization")
        .description("Preserves the order of nodes within a layer but still minimizes crossings between edges connecting long edge dummies. Derives the desired order from positions specified by the \'org.eclipse.elk.position\' layout option. Requires a crossing minimization strategy that is able to process \'in-layer\' constraints.")
        .defaultValue(CROSSING_MINIMIZATION_SEMI_INTERACTIVE_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.crossingMinimization.semiInteractive",
        "org.eclipse.elk.layered.crossingMinimization.strategy",
        CROSSING_MINIMIZATION_SEMI_INTERACTIVE_DEP_CROSSING_MINIMIZATION_STRATEGY_0
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.crossingMinimization.inLayerPredOf")
        .group("crossingMinimization")
        .name("In Layer Predecessor of")
        .description("Allows to set a constraint which specifies of which node the current node is the predecessor. If set to \'s\' then the node is the predecessor of \'s\' and is in the same layer")
        .defaultValue(CROSSING_MINIMIZATION_IN_LAYER_PRED_OF_DEFAULT)
        .type(LayoutOptionData.Type.STRING)
        .optionClass(String.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.crossingMinimization.inLayerSuccOf")
        .group("crossingMinimization")
        .name("In Layer Successor of")
        .description("Allows to set a constraint which specifies of which node the current node is the successor. If set to \'s\' then the node is the successor of \'s\' and is in the same layer")
        .defaultValue(CROSSING_MINIMIZATION_IN_LAYER_SUCC_OF_DEFAULT)
        .type(LayoutOptionData.Type.STRING)
        .optionClass(String.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.crossingMinimization.positionChoiceConstraint")
        .group("crossingMinimization")
        .name("Position Choice Constraint")
        .description("Allows to set a constraint regarding the position placement of a node in a layer. Assumed the layer in which the node placed includes n other nodes and i < n. If set to i, it expresses that the node should be placed at the i-th position. Should i>=n be true then the node is placed at the last position in the layer. Note that this option is not part of any of ELK Layered\'s default configurations but is only evaluated as part of the `InteractiveLayeredGraphVisitor`, which must be applied manually or used via the `DiagramLayoutEngine.")
        .defaultValue(CROSSING_MINIMIZATION_POSITION_CHOICE_CONSTRAINT_DEFAULT)
        .lowerBound(CROSSING_MINIMIZATION_POSITION_CHOICE_CONSTRAINT_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.crossingMinimization.positionId")
        .group("crossingMinimization")
        .name("Position ID")
        .description("Position within a layer that was determined by ELK Layered for a node. This is only generated if interactiveLayot or generatePositionAndLayerIds is set.")
        .defaultValue(CROSSING_MINIMIZATION_POSITION_ID_DEFAULT)
        .lowerBound(CROSSING_MINIMIZATION_POSITION_ID_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.crossingMinimization.greedySwitch.activationThreshold")
        .group("crossingMinimization.greedySwitch")
        .name("Greedy Switch Activation Threshold")
        .description("By default it is decided automatically if the greedy switch is activated or not. The decision is based on whether the size of the input graph (without dummy nodes) is smaller than the value of this option. A \'0\' enforces the activation.")
        .defaultValue(CROSSING_MINIMIZATION_GREEDY_SWITCH_ACTIVATION_THRESHOLD_DEFAULT)
        .lowerBound(CROSSING_MINIMIZATION_GREEDY_SWITCH_ACTIVATION_THRESHOLD_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.crossingMinimization.greedySwitch.type")
        .group("crossingMinimization.greedySwitch")
        .name("Greedy Switch Crossing Minimization")
        .description("Greedy Switch strategy for crossing minimization. The greedy switch heuristic is executed after the regular crossing minimization as a post-processor. Note that if \'hierarchyHandling\' is set to \'INCLUDE_CHILDREN\', the \'greedySwitchHierarchical.type\' option must be used.")
        .defaultValue(CROSSING_MINIMIZATION_GREEDY_SWITCH_TYPE_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(GreedySwitchType.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.crossingMinimization.greedySwitch.type",
        "org.eclipse.elk.layered.crossingMinimization.strategy",
        CROSSING_MINIMIZATION_GREEDY_SWITCH_TYPE_DEP_CROSSING_MINIMIZATION_STRATEGY_0
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.crossingMinimization.greedySwitchHierarchical.type")
        .group("crossingMinimization.greedySwitchHierarchical")
        .name("Greedy Switch Crossing Minimization (hierarchical)")
        .description("Activates the greedy switch heuristic in case hierarchical layout is used. The differences to the non-hierarchical case (see \'greedySwitch.type\') are: 1) greedy switch is inactive by default, 3) only the option value set on the node at which hierarchical layout starts is relevant, and 2) if it\'s activated by the user, it properly addresses hierarchy-crossing edges.")
        .defaultValue(CROSSING_MINIMIZATION_GREEDY_SWITCH_HIERARCHICAL_TYPE_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(GreedySwitchType.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.crossingMinimization.greedySwitchHierarchical.type",
        "org.eclipse.elk.layered.crossingMinimization.strategy",
        CROSSING_MINIMIZATION_GREEDY_SWITCH_HIERARCHICAL_TYPE_DEP_CROSSING_MINIMIZATION_STRATEGY_0
    );
    registry.addDependency(
        "org.eclipse.elk.layered.crossingMinimization.greedySwitchHierarchical.type",
        "org.eclipse.elk.hierarchyHandling",
        CROSSING_MINIMIZATION_GREEDY_SWITCH_HIERARCHICAL_TYPE_DEP_HIERARCHY_HANDLING_1
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.nodePlacement.strategy")
        .group("nodePlacement")
        .name("Node Placement Strategy")
        .description("Strategy for node placement.")
        .defaultValue(NODE_PLACEMENT_STRATEGY_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(NodePlacementStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.nodePlacement.favorStraightEdges")
        .group("nodePlacement")
        .name("Favor Straight Edges Over Balancing")
        .description("Favor straight edges over a balanced node placement. The default behavior is determined automatically based on the used \'edgeRouting\'. For an orthogonal style it is set to true, for all other styles to false.")
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.nodePlacement.favorStraightEdges",
        "org.eclipse.elk.layered.nodePlacement.strategy",
        NODE_PLACEMENT_FAVOR_STRAIGHT_EDGES_DEP_NODE_PLACEMENT_STRATEGY_0
    );
    registry.addDependency(
        "org.eclipse.elk.layered.nodePlacement.favorStraightEdges",
        "org.eclipse.elk.layered.nodePlacement.strategy",
        NODE_PLACEMENT_FAVOR_STRAIGHT_EDGES_DEP_NODE_PLACEMENT_STRATEGY_1
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.nodePlacement.bk.edgeStraightening")
        .group("nodePlacement.bk")
        .name("BK Edge Straightening")
        .description("Specifies whether the Brandes Koepf node placer tries to increase the number of straight edges at the expense of diagram size. There is a subtle difference to the \'favorStraightEdges\' option, which decides whether a balanced placement of the nodes is desired, or not. In bk terms this means combining the four alignments into a single balanced one, or not. This option on the other hand tries to straighten additional edges during the creation of each of the four alignments.")
        .defaultValue(NODE_PLACEMENT_BK_EDGE_STRAIGHTENING_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(EdgeStraighteningStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.nodePlacement.bk.edgeStraightening",
        "org.eclipse.elk.layered.nodePlacement.strategy",
        NODE_PLACEMENT_BK_EDGE_STRAIGHTENING_DEP_NODE_PLACEMENT_STRATEGY_0
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.nodePlacement.bk.fixedAlignment")
        .group("nodePlacement.bk")
        .name("BK Fixed Alignment")
        .description("Tells the BK node placer to use a certain alignment (out of its four) instead of the one producing the smallest height, or the combination of all four.")
        .defaultValue(NODE_PLACEMENT_BK_FIXED_ALIGNMENT_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(FixedAlignment.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.nodePlacement.bk.fixedAlignment",
        "org.eclipse.elk.layered.nodePlacement.strategy",
        NODE_PLACEMENT_BK_FIXED_ALIGNMENT_DEP_NODE_PLACEMENT_STRATEGY_0
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.nodePlacement.linearSegments.deflectionDampening")
        .group("nodePlacement.linearSegments")
        .name("Linear Segments Deflection Dampening")
        .description("Dampens the movement of nodes to keep the diagram from getting too large.")
        .defaultValue(NODE_PLACEMENT_LINEAR_SEGMENTS_DEFLECTION_DAMPENING_DEFAULT)
        .lowerBound(NODE_PLACEMENT_LINEAR_SEGMENTS_DEFLECTION_DAMPENING_LOWER_BOUND)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.nodePlacement.linearSegments.deflectionDampening",
        "org.eclipse.elk.layered.nodePlacement.strategy",
        NODE_PLACEMENT_LINEAR_SEGMENTS_DEFLECTION_DAMPENING_DEP_NODE_PLACEMENT_STRATEGY_0
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.nodePlacement.networkSimplex.nodeFlexibility")
        .group("nodePlacement.networkSimplex")
        .name("Node Flexibility")
        .description("Aims at shorter and straighter edges. Two configurations are possible: (a) allow ports to move freely on the side they are assigned to (the order is always defined beforehand), (b) additionally allow to enlarge a node wherever it helps. If this option is not configured for a node, the \'nodeFlexibility.default\' value is used, which is specified for the node\'s parent.")
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(NodeFlexibility.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.nodePlacement.networkSimplex.nodeFlexibility",
        "org.eclipse.elk.layered.nodePlacement.strategy",
        NODE_PLACEMENT_NETWORK_SIMPLEX_NODE_FLEXIBILITY_DEP_NODE_PLACEMENT_STRATEGY_0
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.nodePlacement.networkSimplex.nodeFlexibility.default")
        .group("nodePlacement.networkSimplex.nodeFlexibility")
        .name("Node Flexibility Default")
        .description("Default value of the \'nodeFlexibility\' option for the children of a hierarchical node.")
        .defaultValue(NODE_PLACEMENT_NETWORK_SIMPLEX_NODE_FLEXIBILITY_DEFAULT_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(NodeFlexibility.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.nodePlacement.networkSimplex.nodeFlexibility.default",
        "org.eclipse.elk.layered.nodePlacement.strategy",
        NODE_PLACEMENT_NETWORK_SIMPLEX_NODE_FLEXIBILITY_DEFAULT_DEP_NODE_PLACEMENT_STRATEGY_0
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.edgeRouting.selfLoopDistribution")
        .group("edgeRouting")
        .name("Self-Loop Distribution")
        .description("Alter the distribution of the loops around the node. It only takes effect for PortConstraints.FREE.")
        .defaultValue(EDGE_ROUTING_SELF_LOOP_DISTRIBUTION_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(SelfLoopDistributionStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.edgeRouting.selfLoopOrdering")
        .group("edgeRouting")
        .name("Self-Loop Ordering")
        .description("Alter the ordering of the loops they can either be stacked or sequenced. It only takes effect for PortConstraints.FREE.")
        .defaultValue(EDGE_ROUTING_SELF_LOOP_ORDERING_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(SelfLoopOrderingStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.edgeRouting.splines.mode")
        .group("edgeRouting.splines")
        .name("Spline Routing Mode")
        .description("Specifies the way control points are assembled for each individual edge. CONSERVATIVE ensures that edges are properly routed around the nodes but feels rather orthogonal at times. SLOPPY uses fewer control points to obtain curvier edge routes but may result in edges overlapping nodes.")
        .defaultValue(EDGE_ROUTING_SPLINES_MODE_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(SplineRoutingMode.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.edgeRouting.splines.mode",
        "org.eclipse.elk.edgeRouting",
        EDGE_ROUTING_SPLINES_MODE_DEP_EDGE_ROUTING_0
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.edgeRouting.splines.sloppy.layerSpacingFactor")
        .group("edgeRouting.splines.sloppy")
        .name("Sloppy Spline Layer Spacing Factor")
        .description("Spacing factor for routing area between layers when using sloppy spline routing.")
        .defaultValue(EDGE_ROUTING_SPLINES_SLOPPY_LAYER_SPACING_FACTOR_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.edgeRouting.splines.sloppy.layerSpacingFactor",
        "org.eclipse.elk.edgeRouting",
        EDGE_ROUTING_SPLINES_SLOPPY_LAYER_SPACING_FACTOR_DEP_EDGE_ROUTING_0
    );
    registry.addDependency(
        "org.eclipse.elk.layered.edgeRouting.splines.sloppy.layerSpacingFactor",
        "org.eclipse.elk.layered.edgeRouting.splines.mode",
        EDGE_ROUTING_SPLINES_SLOPPY_LAYER_SPACING_FACTOR_DEP_EDGE_ROUTING_SPLINES_MODE_1
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.edgeRouting.polyline.slopedEdgeZoneWidth")
        .group("edgeRouting.polyline")
        .name("Sloped Edge Zone Width")
        .description("Width of the strip to the left and to the right of each layer where the polyline edge router is allowed to refrain from ensuring that edges are routed horizontally. This prevents awkward bend points for nodes that extent almost to the edge of their layer.")
        .defaultValue(EDGE_ROUTING_POLYLINE_SLOPED_EDGE_ZONE_WIDTH_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.edgeRouting.polyline.slopedEdgeZoneWidth",
        "org.eclipse.elk.edgeRouting",
        EDGE_ROUTING_POLYLINE_SLOPED_EDGE_ZONE_WIDTH_DEP_EDGE_ROUTING_0
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.spacing.baseValue")
        .group("spacing")
        .name("Spacing Base Value")
        .description("An optional base value for all other layout options of the \'spacing\' group. It can be used to conveniently alter the overall \'spaciousness\' of the drawing. Whenever an explicit value is set for the other layout options, this base value will have no effect. The base value is not inherited, i.e. it must be set for each hierarchical node.")
        .lowerBound(SPACING_BASE_VALUE_LOWER_BOUND)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.spacing.edgeNodeBetweenLayers")
        .group("spacing")
        .name("Edge Node Between Layers Spacing")
        .description("The spacing to be preserved between nodes and edges that are routed next to the node\'s layer. For the spacing between nodes and edges that cross the node\'s layer \'spacing.edgeNode\' is used.")
        .defaultValue(SPACING_EDGE_NODE_BETWEEN_LAYERS_DEFAULT)
        .lowerBound(SPACING_EDGE_NODE_BETWEEN_LAYERS_LOWER_BOUND)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.spacing.edgeEdgeBetweenLayers")
        .group("spacing")
        .name("Edge Edge Between Layer Spacing")
        .description("Spacing to be preserved between pairs of edges that are routed between the same pair of layers. Note that \'spacing.edgeEdge\' is used for the spacing between pairs of edges crossing the same layer.")
        .defaultValue(SPACING_EDGE_EDGE_BETWEEN_LAYERS_DEFAULT)
        .lowerBound(SPACING_EDGE_EDGE_BETWEEN_LAYERS_LOWER_BOUND)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.spacing.nodeNodeBetweenLayers")
        .group("spacing")
        .name("Node Node Between Layers Spacing")
        .description("The spacing to be preserved between any pair of nodes of two adjacent layers. Note that \'spacing.nodeNode\' is used for the spacing between nodes within the layer itself.")
        .defaultValue(SPACING_NODE_NODE_BETWEEN_LAYERS_DEFAULT)
        .lowerBound(SPACING_NODE_NODE_BETWEEN_LAYERS_LOWER_BOUND)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.priority.direction")
        .group("priority")
        .name("Direction Priority")
        .description("Defines how important it is to have a certain edge point into the direction of the overall layout. This option is evaluated during the cycle breaking phase.")
        .defaultValue(PRIORITY_DIRECTION_DEFAULT)
        .lowerBound(PRIORITY_DIRECTION_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.EDGES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.priority.shortness")
        .group("priority")
        .name("Shortness Priority")
        .description("Defines how important it is to keep an edge as short as possible. This option is evaluated during the layering phase.")
        .defaultValue(PRIORITY_SHORTNESS_DEFAULT)
        .lowerBound(PRIORITY_SHORTNESS_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.EDGES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.priority.straightness")
        .group("priority")
        .name("Straightness Priority")
        .description("Defines how important it is to keep an edge straight, i.e. aligned with one of the two axes. This option is evaluated during node placement.")
        .defaultValue(PRIORITY_STRAIGHTNESS_DEFAULT)
        .lowerBound(PRIORITY_STRAIGHTNESS_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.EDGES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.compaction.connectedComponents")
        .group("compaction")
        .name("Connected Components Compaction")
        .description("Tries to further compact components (disconnected sub-graphs).")
        .defaultValue(COMPACTION_CONNECTED_COMPONENTS_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.compaction.connectedComponents",
        "org.eclipse.elk.separateConnectedComponents",
        COMPACTION_CONNECTED_COMPONENTS_DEP_SEPARATE_CONNECTED_COMPONENTS_0
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.compaction.postCompaction.strategy")
        .group("compaction.postCompaction")
        .name("Post Compaction Strategy")
        .description("Specifies whether and how post-process compaction is applied.")
        .defaultValue(COMPACTION_POST_COMPACTION_STRATEGY_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(GraphCompactionStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.compaction.postCompaction.constraints")
        .group("compaction.postCompaction")
        .name("Post Compaction Constraint Calculation")
        .description("Specifies whether and how post-process compaction is applied.")
        .defaultValue(COMPACTION_POST_COMPACTION_CONSTRAINTS_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(ConstraintCalculationStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.highDegreeNodes.treatment")
        .group("highDegreeNodes")
        .name("High Degree Node Treatment")
        .description("Makes room around high degree nodes to place leafs and trees.")
        .defaultValue(HIGH_DEGREE_NODES_TREATMENT_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.highDegreeNodes.threshold")
        .group("highDegreeNodes")
        .name("High Degree Node Threshold")
        .description("Whether a node is considered to have a high degree.")
        .defaultValue(HIGH_DEGREE_NODES_THRESHOLD_DEFAULT)
        .lowerBound(HIGH_DEGREE_NODES_THRESHOLD_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.highDegreeNodes.threshold",
        "org.eclipse.elk.layered.highDegreeNodes.treatment",
        HIGH_DEGREE_NODES_THRESHOLD_DEP_HIGH_DEGREE_NODES_TREATMENT_0
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.highDegreeNodes.treeHeight")
        .group("highDegreeNodes")
        .name("High Degree Node Maximum Tree Height")
        .description("Maximum height of a subtree connected to a high degree node to be moved to separate layers.")
        .defaultValue(HIGH_DEGREE_NODES_TREE_HEIGHT_DEFAULT)
        .lowerBound(HIGH_DEGREE_NODES_TREE_HEIGHT_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.highDegreeNodes.treeHeight",
        "org.eclipse.elk.layered.highDegreeNodes.treatment",
        HIGH_DEGREE_NODES_TREE_HEIGHT_DEP_HIGH_DEGREE_NODES_TREATMENT_0
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.wrapping.strategy")
        .group("wrapping")
        .name("Graph Wrapping Strategy")
        .description("For certain graphs and certain prescribed drawing areas it may be desirable to split the laid out graph into chunks that are placed side by side. The edges that connect different chunks are \'wrapped\' around from the end of one chunk to the start of the other chunk. The points between the chunks are referred to as \'cuts\'.")
        .defaultValue(WRAPPING_STRATEGY_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(WrappingStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.wrapping.additionalEdgeSpacing")
        .group("wrapping")
        .name("Additional Wrapped Edges Spacing")
        .description("To visually separate edges that are wrapped from regularly routed edges an additional spacing value can be specified in form of this layout option. The spacing is added to the regular edgeNode spacing.")
        .defaultValue(WRAPPING_ADDITIONAL_EDGE_SPACING_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.wrapping.additionalEdgeSpacing",
        "org.eclipse.elk.layered.wrapping.strategy",
        WRAPPING_ADDITIONAL_EDGE_SPACING_DEP_WRAPPING_STRATEGY_0
    );
    registry.addDependency(
        "org.eclipse.elk.layered.wrapping.additionalEdgeSpacing",
        "org.eclipse.elk.layered.wrapping.strategy",
        WRAPPING_ADDITIONAL_EDGE_SPACING_DEP_WRAPPING_STRATEGY_1
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.wrapping.correctionFactor")
        .group("wrapping")
        .name("Correction Factor for Wrapping")
        .description("At times and for certain types of graphs the executed wrapping may produce results that are consistently biased in the same fashion: either wrapping to often or to rarely. This factor can be used to correct the bias. Internally, it is simply multiplied with the \'aspect ratio\' layout option.")
        .defaultValue(WRAPPING_CORRECTION_FACTOR_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.wrapping.correctionFactor",
        "org.eclipse.elk.layered.wrapping.strategy",
        WRAPPING_CORRECTION_FACTOR_DEP_WRAPPING_STRATEGY_0
    );
    registry.addDependency(
        "org.eclipse.elk.layered.wrapping.correctionFactor",
        "org.eclipse.elk.layered.wrapping.strategy",
        WRAPPING_CORRECTION_FACTOR_DEP_WRAPPING_STRATEGY_1
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.wrapping.cutting.strategy")
        .group("wrapping.cutting")
        .name("Cutting Strategy")
        .description("The strategy by which the layer indexes are determined at which the layering crumbles into chunks.")
        .defaultValue(WRAPPING_CUTTING_STRATEGY_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(CuttingStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.wrapping.cutting.strategy",
        "org.eclipse.elk.layered.wrapping.strategy",
        WRAPPING_CUTTING_STRATEGY_DEP_WRAPPING_STRATEGY_0
    );
    registry.addDependency(
        "org.eclipse.elk.layered.wrapping.cutting.strategy",
        "org.eclipse.elk.layered.wrapping.strategy",
        WRAPPING_CUTTING_STRATEGY_DEP_WRAPPING_STRATEGY_1
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.wrapping.cutting.cuts")
        .group("wrapping.cutting")
        .name("Manually Specified Cuts")
        .description("Allows the user to specify her own cuts for a certain graph.")
        .type(LayoutOptionData.Type.OBJECT)
        .optionClass(List.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.wrapping.cutting.cuts",
        "org.eclipse.elk.layered.wrapping.cutting.strategy",
        WRAPPING_CUTTING_CUTS_DEP_WRAPPING_CUTTING_STRATEGY_0
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.wrapping.cutting.msd.freedom")
        .group("wrapping.cutting.msd")
        .name("MSD Freedom")
        .description("The MSD cutting strategy starts with an initial guess on the number of chunks the graph should be split into. The freedom specifies how much the strategy may deviate from this guess. E.g. if an initial number of 3 is computed, a freedom of 1 allows 2, 3, and 4 cuts.")
        .defaultValue(WRAPPING_CUTTING_MSD_FREEDOM_DEFAULT)
        .lowerBound(WRAPPING_CUTTING_MSD_FREEDOM_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.wrapping.cutting.msd.freedom",
        "org.eclipse.elk.layered.wrapping.cutting.strategy",
        WRAPPING_CUTTING_MSD_FREEDOM_DEP_WRAPPING_CUTTING_STRATEGY_0
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.wrapping.validify.strategy")
        .group("wrapping.validify")
        .name("Validification Strategy")
        .description("When wrapping graphs, one can specify indices that are not allowed as split points. The validification strategy makes sure every computed split point is allowed.")
        .defaultValue(WRAPPING_VALIDIFY_STRATEGY_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(ValidifyStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.wrapping.validify.strategy",
        "org.eclipse.elk.layered.wrapping.strategy",
        WRAPPING_VALIDIFY_STRATEGY_DEP_WRAPPING_STRATEGY_0
    );
    registry.addDependency(
        "org.eclipse.elk.layered.wrapping.validify.strategy",
        "org.eclipse.elk.layered.wrapping.strategy",
        WRAPPING_VALIDIFY_STRATEGY_DEP_WRAPPING_STRATEGY_1
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.wrapping.validify.forbiddenIndices")
        .group("wrapping.validify")
        .name("Valid Indices for Wrapping")
        .description(null)
        .type(LayoutOptionData.Type.OBJECT)
        .optionClass(List.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.wrapping.validify.forbiddenIndices",
        "org.eclipse.elk.layered.wrapping.strategy",
        WRAPPING_VALIDIFY_FORBIDDEN_INDICES_DEP_WRAPPING_STRATEGY_0
    );
    registry.addDependency(
        "org.eclipse.elk.layered.wrapping.validify.forbiddenIndices",
        "org.eclipse.elk.layered.wrapping.strategy",
        WRAPPING_VALIDIFY_FORBIDDEN_INDICES_DEP_WRAPPING_STRATEGY_1
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.wrapping.multiEdge.improveCuts")
        .group("wrapping.multiEdge")
        .name("Improve Cuts")
        .description("For general graphs it is important that not too many edges wrap backwards. Thus a compromise between evenly-distributed cuts and the total number of cut edges is sought.")
        .defaultValue(WRAPPING_MULTI_EDGE_IMPROVE_CUTS_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.wrapping.multiEdge.improveCuts",
        "org.eclipse.elk.layered.wrapping.strategy",
        WRAPPING_MULTI_EDGE_IMPROVE_CUTS_DEP_WRAPPING_STRATEGY_0
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.wrapping.multiEdge.distancePenalty")
        .group("wrapping.multiEdge")
        .name("Distance Penalty When Improving Cuts")
        .description(null)
        .defaultValue(WRAPPING_MULTI_EDGE_DISTANCE_PENALTY_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.wrapping.multiEdge.distancePenalty",
        "org.eclipse.elk.layered.wrapping.strategy",
        WRAPPING_MULTI_EDGE_DISTANCE_PENALTY_DEP_WRAPPING_STRATEGY_0
    );
    registry.addDependency(
        "org.eclipse.elk.layered.wrapping.multiEdge.distancePenalty",
        "org.eclipse.elk.layered.wrapping.multiEdge.improveCuts",
        WRAPPING_MULTI_EDGE_DISTANCE_PENALTY_DEP_WRAPPING_MULTI_EDGE_IMPROVE_CUTS_1
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.wrapping.multiEdge.improveWrappedEdges")
        .group("wrapping.multiEdge")
        .name("Improve Wrapped Edges")
        .description("The initial wrapping is performed in a very simple way. As a consequence, edges that wrap from one chunk to another may be unnecessarily long. Activating this option tries to shorten such edges.")
        .defaultValue(WRAPPING_MULTI_EDGE_IMPROVE_WRAPPED_EDGES_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.wrapping.multiEdge.improveWrappedEdges",
        "org.eclipse.elk.layered.wrapping.strategy",
        WRAPPING_MULTI_EDGE_IMPROVE_WRAPPED_EDGES_DEP_WRAPPING_STRATEGY_0
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.edgeLabels.sideSelection")
        .group("edgeLabels")
        .name("Edge Label Side Selection")
        .description("Method to decide on edge label sides.")
        .defaultValue(EDGE_LABELS_SIDE_SELECTION_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(EdgeLabelSideSelection.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.edgeLabels.centerLabelPlacementStrategy")
        .group("edgeLabels")
        .name("Edge Center Label Placement Strategy")
        .description("Determines in which layer center labels of long edges should be placed.")
        .defaultValue(EDGE_LABELS_CENTER_LABEL_PLACEMENT_STRATEGY_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(CenterEdgeLabelPlacementStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS, LayoutOptionData.Target.LABELS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.considerModelOrder.strategy")
        .group("considerModelOrder")
        .name("Consider Model Order")
        .description("Preserves the order of nodes and edges in the model file if this does not lead to additional edge crossings. Depending on the strategy this is not always possible since the node and edge order might be conflicting.")
        .defaultValue(CONSIDER_MODEL_ORDER_STRATEGY_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(OrderingStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.considerModelOrder.portModelOrder")
        .group("considerModelOrder")
        .name("Consider Port Order")
        .description("If disabled the port order of output ports is derived from the edge order and input ports are ordered by their incoming connections. If enabled all ports are ordered by the port model order.")
        .defaultValue(CONSIDER_MODEL_ORDER_PORT_MODEL_ORDER_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.considerModelOrder.noModelOrder")
        .group("considerModelOrder")
        .name("No Model Order")
        .description("Set on a node to not set a model order for this node even though it is a real node.")
        .defaultValue(CONSIDER_MODEL_ORDER_NO_MODEL_ORDER_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.considerModelOrder.components")
        .group("considerModelOrder")
        .name("Consider Model Order for Components")
        .description("If set to NONE the usual ordering strategy (by cumulative node priority and size of nodes) is used. INSIDE_PORT_SIDES orders the components with external ports only inside the groups with the same port side. FORCE_MODEL_ORDER enforces the mode order on components. This option might produce bad alignments and sub optimal drawings in terms of used area since the ordering should be respected.")
        .defaultValue(CONSIDER_MODEL_ORDER_COMPONENTS_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(ComponentOrderingStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.considerModelOrder.components",
        "org.eclipse.elk.separateConnectedComponents",
        null
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.considerModelOrder.longEdgeStrategy")
        .group("considerModelOrder")
        .name("Long Edge Ordering Strategy")
        .description("Indicates whether long edges are sorted under, over, or equal to nodes that have no connection to a previous layer in a left-to-right or right-to-left layout. Under and over changes to right and left in a vertical layout.")
        .defaultValue(CONSIDER_MODEL_ORDER_LONG_EDGE_STRATEGY_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(LongEdgeOrderingStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.considerModelOrder.crossingCounterNodeInfluence")
        .group("considerModelOrder")
        .name("Crossing Counter Node Order Influence")
        .description("Indicates with what percentage (1 for 100%) violations of the node model order are weighted against the crossings e.g. a value of 0.5 means two model order violations are as important as on edge crossing. This allows some edge crossings in favor of preserving the model order. It is advised to set this value to a very small positive value (e.g. 0.001) to have minimal crossing and a optimal node order. Defaults to no influence (0).")
        .defaultValue(CONSIDER_MODEL_ORDER_CROSSING_COUNTER_NODE_INFLUENCE_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.considerModelOrder.crossingCounterNodeInfluence",
        "org.eclipse.elk.layered.considerModelOrder.strategy",
        null
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.considerModelOrder.crossingCounterPortInfluence")
        .group("considerModelOrder")
        .name("Crossing Counter Port Order Influence")
        .description("Indicates with what percentage (1 for 100%) violations of the port model order are weighted against the crossings e.g. a value of 0.5 means two model order violations are as important as on edge crossing. This allows some edge crossings in favor of preserving the model order. It is advised to set this value to a very small positive value (e.g. 0.001) to have minimal crossing and a optimal port order. Defaults to no influence (0).")
        .defaultValue(CONSIDER_MODEL_ORDER_CROSSING_COUNTER_PORT_INFLUENCE_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.layered.considerModelOrder.crossingCounterPortInfluence",
        "org.eclipse.elk.layered.considerModelOrder.strategy",
        null
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.layered.considerModelOrder.groupID")
        .group("considerModelOrder")
        .name("Group ID of the Node Type")
        .description("Used to store information about the type of Node. (Used in languages that support different node types such as Lingua Franca)")
        .defaultValue(CONSIDER_MODEL_ORDER_GROUP_I_D_DEFAULT)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    new org.eclipse.elk.alg.layered.options.LayeredOptions().apply(registry);
  }
}
