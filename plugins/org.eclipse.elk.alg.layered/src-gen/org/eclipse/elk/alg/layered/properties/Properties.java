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
import org.eclipse.elk.alg.layered.p1cycles.CycleBreakingStrategy;
import org.eclipse.elk.alg.layered.p2layers.LayeringStrategy;
import org.eclipse.elk.alg.layered.p3order.CrossingMinimizationStrategy;
import org.eclipse.elk.alg.layered.p4nodes.NodePlacementStrategy;
import org.eclipse.elk.alg.layered.p4nodes.bk.CompactionStrategy;
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
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.GraphFeature;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.options.PortAlignment;
import org.eclipse.elk.core.util.AlgorithmFactory;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * Declarations for the ELK Layered layout algorithm.
 */
@SuppressWarnings("all")
public class Properties implements ILayoutMetaDataProvider {
  /**
   * Default value for {@link #COMPACTION}.
   */
  private final static CompactionStrategy COMPACTION_DEFAULT = CompactionStrategy.IMPROVE_STRAIGHTNESS;
  
  /**
   * Specifies the compaction strategy when using the Brandes and Koepf node placer.
   */
  public final static IProperty<CompactionStrategy> COMPACTION = new Property<CompactionStrategy>(
            "org.eclipse.elk.alg.layered.compaction",
            COMPACTION_DEFAULT);
  
  /**
   * Default value for {@link #CONTENT_ALIGNMENT}.
   */
  private final static EnumSet<ContentAlignment> CONTENT_ALIGNMENT_DEFAULT = EnumSet.<ContentAlignment>noneOf(ContentAlignment.class);
  
  /**
   * Specifies how the content of compound nodes is to be aligned, e.g. top-left.
   */
  public final static IProperty<EnumSet<ContentAlignment>> CONTENT_ALIGNMENT = new Property<EnumSet<ContentAlignment>>(
            "org.eclipse.elk.alg.layered.contentAlignment",
            CONTENT_ALIGNMENT_DEFAULT);
  
  /**
   * Default value for {@link #CROSS_MIN}.
   */
  private final static CrossingMinimizationStrategy CROSS_MIN_DEFAULT = CrossingMinimizationStrategy.LAYER_SWEEP;
  
  /**
   * Strategy for crossing minimization.
   */
  public final static IProperty<CrossingMinimizationStrategy> CROSS_MIN = new Property<CrossingMinimizationStrategy>(
            "org.eclipse.elk.alg.layered.crossMin",
            CROSS_MIN_DEFAULT);
  
  /**
   * Default value for {@link #CYCLE_BREAKING}.
   */
  private final static CycleBreakingStrategy CYCLE_BREAKING_DEFAULT = CycleBreakingStrategy.GREEDY;
  
  /**
   * Strategy for cycle breaking. Cycle breaking looks for cycles in the graph and determines
   * which edges to reverse to break the cycles. Reversed edges will end up pointing to the
   * opposite direction of regular edges (that is, reversed edges will point left if edges
   * usually point right).
   */
  public final static IProperty<CycleBreakingStrategy> CYCLE_BREAKING = new Property<CycleBreakingStrategy>(
            "org.eclipse.elk.alg.layered.cycleBreaking",
            CYCLE_BREAKING_DEFAULT);
  
  /**
   * Default value for {@link #DISTRIBUTE_NODES}.
   */
  private final static boolean DISTRIBUTE_NODES_DEFAULT = false;
  
  /**
   * Whether wide nodes should be distributed to several layers.
   */
  public final static IProperty<Boolean> DISTRIBUTE_NODES = new Property<Boolean>(
            "org.eclipse.elk.alg.layered.distributeNodes",
            DISTRIBUTE_NODES_DEFAULT);
  
  /**
   * Default value for {@link #EDGE_LABEL_SIDE_SELECTION}.
   */
  private final static EdgeLabelSideSelection EDGE_LABEL_SIDE_SELECTION_DEFAULT = EdgeLabelSideSelection.ALWAYS_DOWN;
  
  /**
   * Method to decide on edge label sides.
   */
  public final static IProperty<EdgeLabelSideSelection> EDGE_LABEL_SIDE_SELECTION = new Property<EdgeLabelSideSelection>(
            "org.eclipse.elk.alg.layered.edgeLabelSideSelection",
            EDGE_LABEL_SIDE_SELECTION_DEFAULT);
  
  /**
   * Default value for {@link #EDGE_NODE_SPACING_FACTOR}.
   */
  private final static float EDGE_NODE_SPACING_FACTOR_DEFAULT = 0.5f;
  
  /**
   * Factor by which the object spacing is multiplied to arrive at the minimal spacing between
   * an edge and a node.
   */
  public final static IProperty<Float> EDGE_NODE_SPACING_FACTOR = new Property<Float>(
            "org.eclipse.elk.alg.layered.edgeNodeSpacingFactor",
            EDGE_NODE_SPACING_FACTOR_DEFAULT);
  
  /**
   * Default value for {@link #EDGE_SPACING_FACTOR}.
   */
  private final static float EDGE_SPACING_FACTOR_DEFAULT = 0.5f;
  
  /**
   * Factor by which the object spacing is multiplied to arrive at the minimal spacing between
   * edges.
   */
  public final static IProperty<Float> EDGE_SPACING_FACTOR = new Property<Float>(
            "org.eclipse.elk.alg.layered.edgeSpacingFactor",
            EDGE_SPACING_FACTOR_DEFAULT);
  
  /**
   * Default value for {@link #FEEDBACK_EDGES}.
   */
  private final static boolean FEEDBACK_EDGES_DEFAULT = false;
  
  /**
   * Whether feedback edges should be highlighted by routing around the nodes.
   */
  public final static IProperty<Boolean> FEEDBACK_EDGES = new Property<Boolean>(
            "org.eclipse.elk.alg.layered.feedbackEdges",
            FEEDBACK_EDGES_DEFAULT);
  
  /**
   * Default value for {@link #FIXED_ALIGNMENT}.
   */
  private final static FixedAlignment FIXED_ALIGNMENT_DEFAULT = FixedAlignment.NONE;
  
  /**
   * Tells the BK node placer to use a certain alignment instead of taking the optimal result.
   */
  public final static IProperty<FixedAlignment> FIXED_ALIGNMENT = new Property<FixedAlignment>(
            "org.eclipse.elk.alg.layered.fixedAlignment",
            FIXED_ALIGNMENT_DEFAULT);
  
  /**
   * Default value for {@link #GREEDY_SWITCH}.
   */
  private final static GreedySwitchType GREEDY_SWITCH_DEFAULT = GreedySwitchType.TWO_SIDED;
  
  /**
   * Greedy Switch strategy for crossing minimization.
   */
  public final static IProperty<GreedySwitchType> GREEDY_SWITCH = new Property<GreedySwitchType>(
            "org.eclipse.elk.alg.layered.greedySwitch",
            GREEDY_SWITCH_DEFAULT);
  
  /**
   * Default value for {@link #IN_LAYER_SPACING_FACTOR}.
   */
  private final static float IN_LAYER_SPACING_FACTOR_DEFAULT = 1;
  
  /**
   * Factor by which the usual spacing is multiplied to determine the in-layer spacing between
   * objects.
   */
  public final static IProperty<Float> IN_LAYER_SPACING_FACTOR = new Property<Float>(
            "org.eclipse.elk.alg.layered.inLayerSpacingFactor",
            IN_LAYER_SPACING_FACTOR_DEFAULT);
  
  /**
   * Default value for {@link #INTERACTIVE_REFERENCE_POINT}.
   */
  private final static InteractiveReferencePoint INTERACTIVE_REFERENCE_POINT_DEFAULT = InteractiveReferencePoint.CENTER;
  
  /**
   * Determines which point of a node is considered by interactive layout phases.
   */
  public final static IProperty<InteractiveReferencePoint> INTERACTIVE_REFERENCE_POINT = new Property<InteractiveReferencePoint>(
            "org.eclipse.elk.alg.layered.interactiveReferencePoint",
            INTERACTIVE_REFERENCE_POINT_DEFAULT);
  
  /**
   * Default value for {@link #LAYER_CONSTRAINT}.
   */
  private final static LayerConstraint LAYER_CONSTRAINT_DEFAULT = LayerConstraint.NONE;
  
  /**
   * Determines a constraint on the placement of the node regarding the layering.
   */
  public final static IProperty<LayerConstraint> LAYER_CONSTRAINT = new Property<LayerConstraint>(
            "org.eclipse.elk.alg.layered.layerConstraint",
            LAYER_CONSTRAINT_DEFAULT);
  
  /**
   * Default value for {@link #LINEAR_SEGMENTS_DEFLECTION_DAMPENING}.
   */
  private final static float LINEAR_SEGMENTS_DEFLECTION_DAMPENING_DEFAULT = 0.3f;
  
  /**
   * Dampens the movement of nodes to keep the diagram from getting too large.
   */
  public final static IProperty<Float> LINEAR_SEGMENTS_DEFLECTION_DAMPENING = new Property<Float>(
            "org.eclipse.elk.alg.layered.linearSegmentsDeflectionDampening",
            LINEAR_SEGMENTS_DEFLECTION_DAMPENING_DEFAULT);
  
  /**
   * Default value for {@link #NODE_LAYERING}.
   */
  private final static LayeringStrategy NODE_LAYERING_DEFAULT = LayeringStrategy.NETWORK_SIMPLEX;
  
  /**
   * Strategy for node layering.
   */
  public final static IProperty<LayeringStrategy> NODE_LAYERING = new Property<LayeringStrategy>(
            "org.eclipse.elk.alg.layered.nodeLayering",
            NODE_LAYERING_DEFAULT);
  
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
            "org.eclipse.elk.alg.layered.mergeEdges",
            MERGE_EDGES_DEFAULT);
  
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
            "org.eclipse.elk.alg.layered.mergeHierarchyEdges",
            MERGE_HIERARCHY_EDGES_DEFAULT);
  
  /**
   * Default value for {@link #NODE_PLACEMENT}.
   */
  private final static NodePlacementStrategy NODE_PLACEMENT_DEFAULT = NodePlacementStrategy.BRANDES_KOEPF;
  
  /**
   * Strategy for node placement.
   */
  public final static IProperty<NodePlacementStrategy> NODE_PLACEMENT = new Property<NodePlacementStrategy>(
            "org.eclipse.elk.alg.layered.nodePlacement",
            NODE_PLACEMENT_DEFAULT);
  
  /**
   * Default value for {@link #SAUSAGE_FOLDING}.
   */
  private final static boolean SAUSAGE_FOLDING_DEFAULT = false;
  
  /**
   * Whether long sausages should be folded up nice and tight.
   */
  public final static IProperty<Boolean> SAUSAGE_FOLDING = new Property<Boolean>(
            "org.eclipse.elk.alg.layered.sausageFolding",
            SAUSAGE_FOLDING_DEFAULT);
  
  /**
   * Default value for {@link #SELF_LOOP_PLACEMENT}.
   */
  private final static SelfLoopPlacement SELF_LOOP_PLACEMENT_DEFAULT = SelfLoopPlacement.NORTH_STACKED;
  
  public final static IProperty<SelfLoopPlacement> SELF_LOOP_PLACEMENT = new Property<SelfLoopPlacement>(
            "org.eclipse.elk.alg.layered.selfLoopPlacement",
            SELF_LOOP_PLACEMENT_DEFAULT);
  
  /**
   * Default value for {@link #THOROUGHNESS}.
   */
  private final static int THOROUGHNESS_DEFAULT = 7;
  
  /**
   * How much effort should be spent to produce a nice layout.
   */
  public final static IProperty<Integer> THOROUGHNESS = new Property<Integer>(
            "org.eclipse.elk.alg.layered.thoroughness",
            THOROUGHNESS_DEFAULT);
  
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
            "org.eclipse.elk.alg.layered.unnecessaryBendpoints",
            UNNECESSARY_BENDPOINTS_DEFAULT);
  
  /**
   * Default value for {@link #WIDE_NODES_ON_MULTIPLE_LAYERS}.
   */
  private final static WideNodesStrategy WIDE_NODES_ON_MULTIPLE_LAYERS_DEFAULT = WideNodesStrategy.OFF;
  
  /**
   * Strategy to distribute wide nodes over multiple layers.
   */
  public final static IProperty<WideNodesStrategy> WIDE_NODES_ON_MULTIPLE_LAYERS = new Property<WideNodesStrategy>(
            "org.eclipse.elk.alg.layered.wideNodesOnMultipleLayers",
            WIDE_NODES_ON_MULTIPLE_LAYERS_DEFAULT);
  
  /**
   * Required value for dependency between {@link #COMPACTION} and {@link #NODE_PLACEMENT}.
   */
  private final static NodePlacementStrategy COMPACTION_DEP_NODE_PLACEMENT = NodePlacementStrategy.BRANDES_KOEPF;
  
  /**
   * Required value for dependency between {@link #EDGE_LABEL_SIDE_SELECTION} and {@link #EDGE_ROUTING}.
   */
  private final static EdgeRouting EDGE_LABEL_SIDE_SELECTION_DEP_EDGE_ROUTING = EdgeRouting.ORTHOGONAL;
  
  /**
   * Required value for dependency between {@link #FIXED_ALIGNMENT} and {@link #NODE_PLACEMENT}.
   */
  private final static NodePlacementStrategy FIXED_ALIGNMENT_DEP_NODE_PLACEMENT = NodePlacementStrategy.BRANDES_KOEPF;
  
  /**
   * Required value for dependency between {@link #INTERACTIVE_REFERENCE_POINT} and {@link #CYCLE_BREAKING}.
   */
  private final static CycleBreakingStrategy INTERACTIVE_REFERENCE_POINT_DEP_CYCLE_BREAKING = CycleBreakingStrategy.INTERACTIVE;
  
  /**
   * Required value for dependency between {@link #INTERACTIVE_REFERENCE_POINT} and {@link #CROSS_MIN}.
   */
  private final static CrossingMinimizationStrategy INTERACTIVE_REFERENCE_POINT_DEP_CROSS_MIN = CrossingMinimizationStrategy.INTERACTIVE;
  
  /**
   * Required value for dependency between {@link #LINEAR_SEGMENTS_DEFLECTION_DAMPENING} and {@link #NODE_PLACEMENT}.
   */
  private final static NodePlacementStrategy LINEAR_SEGMENTS_DEFLECTION_DAMPENING_DEP_NODE_PLACEMENT = NodePlacementStrategy.LINEAR_SEGMENTS;
  
  /**
   * Required value for dependency between {@link #MERGE_HIERARCHY_EDGES} and {@link #LAYOUT_HIERARCHY}.
   */
  private final static boolean MERGE_HIERARCHY_EDGES_DEP_LAYOUT_HIERARCHY = true;
  
  /**
   * Required value for dependency between {@link #SAUSAGE_FOLDING} and {@link #NODE_LAYERING}.
   */
  private final static LayeringStrategy SAUSAGE_FOLDING_DEP_NODE_LAYERING = LayeringStrategy.LONGEST_PATH;
  
  /**
   * Required value for dependency between {@link #SELF_LOOP_PLACEMENT} and {@link #EDGE_ROUTING}.
   */
  private final static EdgeRouting SELF_LOOP_PLACEMENT_DEP_EDGE_ROUTING = EdgeRouting.SPLINES;
  
  /**
   * Default value for {@link #SPACING} with algorithm "ELK Layered".
   */
  private final static float LAYERED_SUP_SPACING = 20;
  
  /**
   * Overridden value for Spacing.
   */
  public final static IProperty<Float> SPACING = new Property<Float>(
            LayoutOptions.SPACING,
            LAYERED_SUP_SPACING);
  
  /**
   * Default value for {@link #BORDER_SPACING} with algorithm "ELK Layered".
   */
  private final static float LAYERED_SUP_BORDER_SPACING = 12;
  
  /**
   * Overridden value for Border Spacing.
   */
  public final static IProperty<Float> BORDER_SPACING = new Property<Float>(
            LayoutOptions.BORDER_SPACING,
            LAYERED_SUP_BORDER_SPACING);
  
  /**
   * Default value for {@link #PORT_SPACING} with algorithm "ELK Layered".
   */
  private final static float LAYERED_SUP_PORT_SPACING = 10;
  
  /**
   * Overridden value for Port Spacing.
   */
  public final static IProperty<Float> PORT_SPACING = new Property<Float>(
            LayoutOptions.PORT_SPACING,
            LAYERED_SUP_PORT_SPACING);
  
  /**
   * Default value for {@link #PRIORITY} with algorithm "ELK Layered".
   */
  private final static int LAYERED_SUP_PRIORITY = 0;
  
  /**
   * Overridden value for Priority.
   */
  public final static IProperty<Integer> PRIORITY = new Property<Integer>(
            LayoutOptions.PRIORITY,
            LAYERED_SUP_PRIORITY);
  
  /**
   * Default value for {@link #EDGE_ROUTING} with algorithm "ELK Layered".
   */
  private final static EdgeRouting LAYERED_SUP_EDGE_ROUTING = EdgeRouting.ORTHOGONAL;
  
  /**
   * Overridden value for Edge Routing.
   */
  public final static IProperty<EdgeRouting> EDGE_ROUTING = new Property<EdgeRouting>(
            LayoutOptions.EDGE_ROUTING,
            LAYERED_SUP_EDGE_ROUTING);
  
  /**
   * Default value for {@link #PORT_OFFSET} with algorithm "ELK Layered".
   */
  private final static float LAYERED_SUP_PORT_OFFSET = 0;
  
  /**
   * Overridden value for Port Offset.
   */
  public final static IProperty<Float> PORT_OFFSET = new Property<Float>(
            LayoutOptions.PORT_OFFSET,
            LAYERED_SUP_PORT_OFFSET);
  
  /**
   * Default value for {@link #RANDOM_SEED} with algorithm "ELK Layered".
   */
  private final static int LAYERED_SUP_RANDOM_SEED = 1;
  
  /**
   * Overridden value for Randomization Seed.
   */
  public final static IProperty<Integer> RANDOM_SEED = new Property<Integer>(
            LayoutOptions.RANDOM_SEED,
            LAYERED_SUP_RANDOM_SEED);
  
  /**
   * Default value for {@link #ASPECT_RATIO} with algorithm "ELK Layered".
   */
  private final static float LAYERED_SUP_ASPECT_RATIO = 1.6f;
  
  /**
   * Overridden value for Aspect Ratio.
   */
  public final static IProperty<Float> ASPECT_RATIO = new Property<Float>(
            LayoutOptions.ASPECT_RATIO,
            LAYERED_SUP_ASPECT_RATIO);
  
  /**
   * Default value for {@link #LABEL_SPACING} with algorithm "ELK Layered".
   */
  private final static float LAYERED_SUP_LABEL_SPACING = 0;
  
  /**
   * Overridden value for Label Spacing.
   */
  public final static IProperty<Float> LABEL_SPACING = new Property<Float>(
            LayoutOptions.LABEL_SPACING,
            LAYERED_SUP_LABEL_SPACING);
  
  /**
   * Default value for {@link #SEPARATE_CONN_COMP} with algorithm "ELK Layered".
   */
  private final static boolean LAYERED_SUP_SEPARATE_CONN_COMP = true;
  
  /**
   * Default value for {@link #DIRECTION} with algorithm "ELK Layered".
   */
  private final static Direction LAYERED_SUP_DIRECTION = Direction.RIGHT;
  
  /**
   * Default value for {@link #PORT_ALIGNMENT} with algorithm "ELK Layered".
   */
  private final static PortAlignment LAYERED_SUP_PORT_ALIGNMENT = PortAlignment.JUSTIFIED;
  
  public void apply(final ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.layered.compaction",
        "Compaction Strategy",
        "Specifies the compaction strategy when using the Brandes and Koepf node placer.",
        COMPACTION_DEFAULT,
        CompactionStrategy.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
    ));
    registry.addDependency(
        "org.eclipse.elk.alg.layered.compaction",
        "org.eclipse.elk.alg.layered.nodePlacement",
        COMPACTION_DEP_NODE_PLACEMENT
    );
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.layered.contentAlignment",
        "Content Alignment",
        "Specifies how the content of compound nodes is to be aligned, e.g. top-left.",
        CONTENT_ALIGNMENT_DEFAULT,
        EnumSet.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.layered.crossMin",
        "Crossing Minimization",
        "Strategy for crossing minimization.",
        CROSS_MIN_DEFAULT,
        CrossingMinimizationStrategy.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.layered.cycleBreaking",
        "Cycle Breaking",
        "Strategy for cycle breaking. Cycle breaking looks for cycles in the graph and determines which edges to reverse to break the cycles. Reversed edges will end up pointing to the opposite direction of regular edges (that is, reversed edges will point left if edges usually point right).",
        CYCLE_BREAKING_DEFAULT,
        CycleBreakingStrategy.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.layered.distributeNodes",
        "Distribute Nodes (Deprecated)",
        "Whether wide nodes should be distributed to several layers.",
        DISTRIBUTE_NODES_DEFAULT,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.layered.edgeLabelSideSelection",
        "Edge Label Side Selection",
        "Method to decide on edge label sides.",
        EDGE_LABEL_SIDE_SELECTION_DEFAULT,
        EdgeLabelSideSelection.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
    ));
    registry.addDependency(
        "org.eclipse.elk.alg.layered.edgeLabelSideSelection",
        "org.eclipse.elk.edgeRouting",
        EDGE_LABEL_SIDE_SELECTION_DEP_EDGE_ROUTING
    );
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.layered.edgeNodeSpacingFactor",
        "Edge Node Spacing Factor",
        "Factor by which the object spacing is multiplied to arrive at the minimal spacing between an edge and a node.",
        EDGE_NODE_SPACING_FACTOR_DEFAULT,
        float.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.layered.edgeSpacingFactor",
        "Edge Spacing Factor",
        "Factor by which the object spacing is multiplied to arrive at the minimal spacing between edges.",
        EDGE_SPACING_FACTOR_DEFAULT,
        float.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.layered.feedbackEdges",
        "Feedback Edges",
        "Whether feedback edges should be highlighted by routing around the nodes.",
        FEEDBACK_EDGES_DEFAULT,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.layered.fixedAlignment",
        "Fixed Alignment",
        "Tells the BK node placer to use a certain alignment instead of taking the optimal result.",
        FIXED_ALIGNMENT_DEFAULT,
        FixedAlignment.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
    ));
    registry.addDependency(
        "org.eclipse.elk.alg.layered.fixedAlignment",
        "org.eclipse.elk.alg.layered.nodePlacement",
        FIXED_ALIGNMENT_DEP_NODE_PLACEMENT
    );
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.layered.greedySwitch",
        "Greedy Switch Crossing Minimization",
        "Greedy Switch strategy for crossing minimization.",
        GREEDY_SWITCH_DEFAULT,
        GreedySwitchType.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.layered.inLayerSpacingFactor",
        "In-layer Spacing Factor",
        "Factor by which the usual spacing is multiplied to determine the in-layer spacing between objects.",
        IN_LAYER_SPACING_FACTOR_DEFAULT,
        float.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.layered.interactiveReferencePoint",
        "Interactive Reference Point",
        "Determines which point of a node is considered by interactive layout phases.",
        INTERACTIVE_REFERENCE_POINT_DEFAULT,
        InteractiveReferencePoint.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
    ));
    registry.addDependency(
        "org.eclipse.elk.alg.layered.interactiveReferencePoint",
        "org.eclipse.elk.alg.layered.cycleBreaking",
        INTERACTIVE_REFERENCE_POINT_DEP_CYCLE_BREAKING
    );
    registry.addDependency(
        "org.eclipse.elk.alg.layered.interactiveReferencePoint",
        "org.eclipse.elk.alg.layered.crossMin",
        INTERACTIVE_REFERENCE_POINT_DEP_CROSS_MIN
    );
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.layered.layerConstraint",
        "Layer Constraint",
        "Determines a constraint on the placement of the node regarding the layering.",
        LAYER_CONSTRAINT_DEFAULT,
        LayerConstraint.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.layered.linearSegmentsDeflectionDampening",
        "Linear Segments Deflection Dampening",
        "Dampens the movement of nodes to keep the diagram from getting too large.",
        LINEAR_SEGMENTS_DEFLECTION_DAMPENING_DEFAULT,
        float.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
    ));
    registry.addDependency(
        "org.eclipse.elk.alg.layered.linearSegmentsDeflectionDampening",
        "org.eclipse.elk.alg.layered.nodePlacement",
        LINEAR_SEGMENTS_DEFLECTION_DAMPENING_DEP_NODE_PLACEMENT
    );
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.layered.nodeLayering",
        "Node Layering",
        "Strategy for node layering.",
        NODE_LAYERING_DEFAULT,
        LayeringStrategy.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.layered.mergeEdges",
        "Merge Edges",
        "Edges that have no ports are merged so they touch the connected nodes at the same points. When this option is disabled, one port is created for each edge directly connected to a node. When it is enabled, all such incoming edges share an input port, and all outgoing edges share an output port.",
        MERGE_EDGES_DEFAULT,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.layered.mergeHierarchyEdges",
        "Merge Hierarchy-Crossing Edges",
        "If hierarchical layout is active, hierarchy-crossing edges use as few hierarchical ports as possible. They are broken by the algorithm, with hierarchical ports inserted as required. Usually, one such port is created for each edge at each hierarchy crossing point. With this option set to true, we try to create as few hierarchical ports as possible in the process. In particular, all edges that form a hyperedge can share a port.",
        MERGE_HIERARCHY_EDGES_DEFAULT,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
    ));
    registry.addDependency(
        "org.eclipse.elk.alg.layered.mergeHierarchyEdges",
        "org.eclipse.elk.layoutHierarchy",
        MERGE_HIERARCHY_EDGES_DEP_LAYOUT_HIERARCHY
    );
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.layered.nodePlacement",
        "Node Placement",
        "Strategy for node placement.",
        NODE_PLACEMENT_DEFAULT,
        NodePlacementStrategy.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.layered.sausageFolding",
        "Sausage Folding",
        "Whether long sausages should be folded up nice and tight.",
        SAUSAGE_FOLDING_DEFAULT,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
    ));
    registry.addDependency(
        "org.eclipse.elk.alg.layered.sausageFolding",
        "org.eclipse.elk.alg.layered.nodeLayering",
        SAUSAGE_FOLDING_DEP_NODE_LAYERING
    );
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.layered.selfLoopPlacement",
        "Spline Self-Loop Placement",
        null,
        SELF_LOOP_PLACEMENT_DEFAULT,
        SelfLoopPlacement.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
    ));
    registry.addDependency(
        "org.eclipse.elk.alg.layered.selfLoopPlacement",
        "org.eclipse.elk.edgeRouting",
        SELF_LOOP_PLACEMENT_DEP_EDGE_ROUTING
    );
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.layered.thoroughness",
        "Thoroughness",
        "How much effort should be spent to produce a nice layout.",
        THOROUGHNESS_DEFAULT,
        int.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.layered.unnecessaryBendpoints",
        "Add Unnecessary Bendpoints",
        "Adds bend points even if an edge does not change direction. If true, each long edge dummy will contribute a bend point to its edges and hierarchy-crossing edges will always get a bend point where they cross hierarchy boundaries. By default, bend points are only added where an edge changes direction.",
        UNNECESSARY_BENDPOINTS_DEFAULT,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.layered.wideNodesOnMultipleLayers",
        "Wide Nodes on Multiple Layers",
        "Strategy to distribute wide nodes over multiple layers.",
        WIDE_NODES_ON_MULTIPLE_LAYERS_DEFAULT,
        WideNodesStrategy.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
    ));
    registry.register(new LayoutAlgorithmData(
        "org.eclipse.elk.alg.layered.Layered",
        "ELK Layered",
        "Layer-based algorithm provided by the Eclipse Layout Kernel. Arranges as many edges as possible into one direction by placing nodes into subsequent layers. This implementation supports different routing styles (straight, orthogonal, splines); if orthogonal routing is selected, arbitrary port constraints are respected, thus enabling the layout of block diagrams such as actor-oriented models or circuit schematics. Furthermore, full layout of compound graphs with cross-hierarchy edges is supported when the respective option is activated on the top level.",
        new AlgorithmFactory(LayeredLayoutProvider.class, ""),
        "org.eclipse.elk.Layered",
        null,
        "images/layered.png",
        EnumSet.of(GraphFeature.SELF_LOOPS, GraphFeature.INSIDE_SELF_LOOPS, GraphFeature.MULTI_EDGES, GraphFeature.EDGE_LABELS, GraphFeature.PORTS, GraphFeature.COMPOUND, GraphFeature.CLUSTERS)
    ));
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.spacing",
        LAYERED_SUP_SPACING
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.borderSpacing",
        LAYERED_SUP_BORDER_SPACING
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.portSpacing",
        LAYERED_SUP_PORT_SPACING
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.priority",
        LAYERED_SUP_PRIORITY
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.edgeRouting",
        LAYERED_SUP_EDGE_ROUTING
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.portOffset",
        LAYERED_SUP_PORT_OFFSET
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.randomSeed",
        LAYERED_SUP_RANDOM_SEED
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.aspectRatio",
        LAYERED_SUP_ASPECT_RATIO
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.labelSpacing",
        LAYERED_SUP_LABEL_SPACING
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.noLayout",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.portConstraints",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.portSide",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.debugMode",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.alignment",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.layoutHierarchy",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.separateConnComp",
        LAYERED_SUP_SEPARATE_CONN_COMP
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.selfLoopInside",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.sizeConstraint",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.sizeOptions",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.direction",
        LAYERED_SUP_DIRECTION
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.nodeLabelPlacement",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.portLabelPlacement",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.portAlignment",
        LAYERED_SUP_PORT_ALIGNMENT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.portAlignmentNorth",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.portAlignmentSouth",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.portAlignmentWest",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.portAlignmentEast",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.alg.layered.edgeSpacingFactor",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.alg.layered.edgeNodeSpacingFactor",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.alg.layered.unnecessaryBendpoints",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.alg.layered.nodeLayering",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.alg.layered.thoroughness",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.alg.layered.layerConstraint",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.alg.layered.cycleBreaking",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.alg.layered.crossMin",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.alg.layered.greedySwitch",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.alg.layered.mergeEdges",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.alg.layered.mergeHierarchyEdges",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.alg.layered.interactiveReferencePoint",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.alg.layered.nodePlacement",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.alg.layered.fixedAlignment",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.alg.layered.edgeLabelSideSelection",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.alg.layered.feedbackEdges",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.alg.layered.inLayerSpacingFactor",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.alg.layered.wideNodesOnMultipleLayers",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.alg.layered.linearSegmentsDeflectionDampening",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.alg.layered.selfLoopPlacement",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.alg.layered.contentAlignment",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.layered.Layered",
        "org.eclipse.elk.alg.layered.compaction",
        null
    );
  }
}
