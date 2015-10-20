/*******************************************************************************
 * Copyright (c) 2010, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.layered.properties;

import java.util.EnumSet;

import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;
import org.eclipse.elk.layered.p1cycles.CycleBreakingStrategy;
import org.eclipse.elk.layered.p2layers.LayeringStrategy;
import org.eclipse.elk.layered.p3order.CrossingMinimizationStrategy;
import org.eclipse.elk.layered.p4nodes.NodePlacementStrategy;
import org.eclipse.elk.layered.p4nodes.bk.CompactionStrategy;

/**
 * Container for public property definitions. These are layout options that can be set on graph
 * elements before the algorithm is invoked.
 *
 * @author msp
 * @author cds
 * @author uru
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public final class Properties {

    /**
     * A pre-defined seed for pseudo-random number generators.
     * We redefine the property here to set its default value to 1.
     *
     * @see org.eclipse.elk.core.options.LayoutOptions#RANDOM_SEED
     */
    public static final IProperty<Integer> RANDOM_SEED = new Property<Integer>(
            "org.eclipse.elk.randomSeed", 1);

    /**
     * The factor by which the in-layer spacing between objects differs from the inter-layer
     * {@link InternalProperties#SPACING}.
     */
    public static final IProperty<Float> OBJ_SPACING_IN_LAYER_FACTOR = new Property<Float>(
            "org.eclipse.elk.layered.inLayerSpacingFactor", 1.0f, 0f);

    /**
     * Factor for minimal spacing between edges.
     */
    public static final Property<Float> EDGE_SPACING_FACTOR = new Property<Float>(
            "org.eclipse.elk.layered.edgeSpacingFactor", 0.5f);

    /**
     * Factor for minimal spacing between an edge and a node that is close by.
     */
    public static final Property<Float> EDGE_NODE_SPACING_FACTOR = new Property<Float>(
            "org.eclipse.elk.layered.edgeNodeSpacingFactor", 0.7f);

    /**
     * Whether nodes shall be distributed during layer assignment.
     *
     * @deprecated use the {@link #WIDE_NODES_ON_MULTIPLE_LAYERS} property instead.
     */
    @Deprecated
    public static final IProperty<Boolean> DISTRIBUTE_NODES = new Property<Boolean>(
            "org.eclipse.elk.layered.distributeNodes", false);

    /**
     * Whether wide nodes may be be distributed over several layers.
     */
    public static final IProperty<WideNodesStrategy> WIDE_NODES_ON_MULTIPLE_LAYERS =
            new Property<WideNodesStrategy>(
                    "org.eclipse.elk.layered.wideNodesOnMultipleLayers",
                    WideNodesStrategy.OFF);
    /**
     * Property to choose a cycle breaking strategy.
     */
    public static final IProperty<CycleBreakingStrategy> CYCLE_BREAKING =
            new Property<CycleBreakingStrategy>("org.eclipse.elk.layered.cycleBreaking",
                    CycleBreakingStrategy.GREEDY);

    /**
     * Property to choose a node layering strategy.
     */
    public static final IProperty<LayeringStrategy> NODE_LAYERING = new Property<LayeringStrategy>(
            "org.eclipse.elk.layered.nodeLayering", LayeringStrategy.NETWORK_SIMPLEX);

    /**
     * Property to choose a crossing minimization strategy.
     */
    public static final IProperty<CrossingMinimizationStrategy> CROSS_MIN =
            new Property<CrossingMinimizationStrategy>("org.eclipse.elk.layered.crossMin",
                    CrossingMinimizationStrategy.LAYER_SWEEP);

    /**
     * Property to choose a greedy Crossing Minimization Strategy.
     */
    public static final IProperty<GreedySwitchType> GREEDY_SWITCH_TYPE =
            new Property<GreedySwitchType>("org.eclipse.elk.layered.greedySwitch",
                    GreedySwitchType.TWO_SIDED);

    /**
     * Property to choose a node placement strategy.
     */
    public static final IProperty<NodePlacementStrategy> NODE_PLACER =
            new Property<NodePlacementStrategy>("org.eclipse.elk.layered.nodePlace",
                    NodePlacementStrategy.BRANDES_KOEPF);

    /**
     * Dampening of deflections between linear segments in the linear segments node placer.
     */
    public static final IProperty<Float> LINEAR_SEGMENTS_DEFLECTION_DAMPENING =
            new Property<Float>("org.eclipse.elk.layered.linearSegmentsDeflectionDampening",
                    0.3f, 0.0f, 1.0f);

    /**
     * Tells the BK node placer to use a certain alignment instead of taking the optimal result.
     */
    public static final IProperty<FixedAlignment> FIXED_ALIGNMENT = new Property<FixedAlignment>(
            "org.eclipse.elk.layered.fixedAlignment", FixedAlignment.NONE);

    /**
     * Property to choose an edge label placement strategy.
     */
    public static final IProperty<EdgeLabelSideSelection> EDGE_LABEL_SIDE_SELECTION =
            new Property<EdgeLabelSideSelection>(
                    "org.eclipse.elk.layered.edgeLabelSideSelection",
                    EdgeLabelSideSelection.ALWAYS_DOWN);

    /**
     * Property to switch debug mode on or off.
     */
    public static final IProperty<Boolean> DEBUG_MODE = new Property<Boolean>(
            "org.eclipse.elk.debugMode", false);

    /**
     * Property that determines how much effort should be spent.
     */
    public static final IProperty<Integer> THOROUGHNESS = new Property<Integer>(
            "org.eclipse.elk.layered.thoroughness", 10, 1);

    /**
     * Property to set constraints on the node layering.
     */
    public static final IProperty<LayerConstraint> LAYER_CONSTRAINT =
            new Property<LayerConstraint>("org.eclipse.elk.layered.layerConstraint",
                    LayerConstraint.NONE);

    /**
     * Property to enable or disable port merging. Merging ports is only interesting for edges
     * directly connected to nodes instead of ports. When this option is disabled, one port is
     * created for each edge directly connected to a node. When it is enabled, all such incoming
     * edges share an input port, and all outgoing edges share an output port.
     */
    public static final IProperty<Boolean> MERGE_EDGES = new Property<Boolean>(
            "org.eclipse.elk.layered.mergeEdges", false);

    /**
     * Property to enable or disable hierarchical port merging. Merging hierarchical ports is only
     * interesting for hierarchy-crossing edges. Those are broken by the algorithm, with
     * hierarchical ports inserted as required. Usually, one such port is created for each edge at
     * each hierarchy crossing point. With this option set to {@code true}, we try to create as few
     * hierarchical ports as possible in the process. In particular, all edges that form a hyperedge
     * can share a port.
     */
    public static final IProperty<Boolean> MERGE_HIERARCHICAL_EDGES = new Property<Boolean>(
            "org.eclipse.elk.layered.mergeHierarchyEdges", true);

    /**
     * Property that determines which point in a node determines the result of interactive phases.
     */
    public static final IProperty<InteractiveReferencePoint> INTERACTIVE_REFERENCE_POINT =
            new Property<InteractiveReferencePoint>(
                    "org.eclipse.elk.layered.interactiveReferencePoint",
                    InteractiveReferencePoint.CENTER);

    /**
     * Whether feedback edges should be highlighted by routing around the nodes.
     */
    public static final IProperty<Boolean> FEEDBACK_EDGES = new Property<Boolean>(
            "org.eclipse.elk.layered.feedBackEdges", false);

    /**
     * If true, each long edge dummy will contribute a bend point to its edges and
     * hierarchy-crossing edges will always get a bend point where they cross hierarchy boundaries.
     * By default, bend points are only added where an edge changes direction.
     */
    public static final IProperty<Boolean> ADD_UNNECESSARY_BENDPOINTS = new Property<Boolean>(
            "org.eclipse.elk.layered.unnecessaryBendpoints", false);

    /**
     * Specifies how the content of compound nodes is to be aligned, e.g. top-left or center-center.
     */
    public static final IProperty<EnumSet<ContentAlignment>> CONTENT_ALIGNMENT =
            new Property<EnumSet<ContentAlignment>>(
                    "org.eclipse.elk.layered.contentAlignment", ContentAlignment.topLeft());

    /**
     * Handles large sausages.
     */
    public static final IProperty<Boolean> SAUSAGE_FOLDING = new Property<Boolean>(
            "org.eclipse.elk.layered.sausageFolding", false);

    /**
     * The spline-self-loop distribution method.
     */
    public static final IProperty<SelfLoopPlacement> SPLINE_SELF_LOOP_PLACEMENT =
            new Property<SelfLoopPlacement>("org.eclipse.elk.layered.splines.selfLoopPlacement",
                    SelfLoopPlacement.NORTH_STACKED);

    /**
     * Specifies the compaction strategy when using the
     * {@link org.eclipse.elk.layered.p4nodes.bk.BKNodePlacer BKNodePlacer}.
     */
    public static final IProperty<CompactionStrategy> COMPACTION_STRATEGY =
            new Property<CompactionStrategy>(
                    "org.eclipse.elk.layered.nodeplace.compactionStrategy",
                    CompactionStrategy.CLASSIC);

    // /////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR

    /**
     * Hidden default constructor.
     */
    private Properties() {
    }

}
