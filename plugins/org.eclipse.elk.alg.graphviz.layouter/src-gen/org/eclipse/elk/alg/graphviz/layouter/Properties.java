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
package org.eclipse.elk.alg.graphviz.layouter;

import java.util.EnumSet;
import org.eclipse.elk.alg.graphviz.dot.transform.NeatoModel;
import org.eclipse.elk.alg.graphviz.dot.transform.OverlapMode;
import org.eclipse.elk.alg.graphviz.layouter.GraphvizLayoutProvider;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.GraphFeature;
import org.eclipse.elk.core.util.AlgorithmFactory;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class Properties implements ILayoutMetaDataProvider {
  /**
   * Default value for {@link #ADAPT_PORT_POSITIONS}.
   */
  private final static boolean ADAPT_PORT_POSITIONS_DEFAULT = true;
  
  /**
   * Whether ports should be moved to the point where edges cross the node's bounds.
   */
  public final static IProperty<Boolean> ADAPT_PORT_POSITIONS = new Property<Boolean>(
            "org.eclipse.elk.alg.graphviz.adaptPortPositions",
            ADAPT_PORT_POSITIONS_DEFAULT);
  
  /**
   * Default value for {@link #CONCENTRATE}.
   */
  private final static boolean CONCENTRATE_DEFAULT = false;
  
  /**
   * Merges multiedges into a single edge and causes partially parallel edges to share part of
   * their paths.
   */
  public final static IProperty<Boolean> CONCENTRATE = new Property<Boolean>(
            "org.eclipse.elk.alg.graphviz.concentrate",
            CONCENTRATE_DEFAULT);
  
  /**
   * Terminating condition. If the length squared of all energy gradients are less than
   * epsilon, the algorithm stops.
   */
  public final static IProperty<Float> EPSILON = new Property<Float>(
            "org.eclipse.elk.alg.graphviz.epsilon");
  
  /**
   * Multiplicative scale factor for the maximal number of iterations used during crossing
   * minimization, node ranking, and node positioning.
   */
  public final static IProperty<Float> ITERATIONS_FACTOR = new Property<Float>(
            "org.eclipse.elk.alg.graphviz.iterationsFactor");
  
  /**
   * Default value for {@link #LABEL_ANGLE}.
   */
  private final static float LABEL_ANGLE_DEFAULT = (-25);
  
  /**
   * Angle between head / tail positioned edge labels and the corresponding edge.
   */
  public final static IProperty<Float> LABEL_ANGLE = new Property<Float>(
            "org.eclipse.elk.alg.graphviz.labelAngle",
            LABEL_ANGLE_DEFAULT);
  
  /**
   * Default value for {@link #LABEL_DISTANCE}.
   */
  private final static float LABEL_DISTANCE_DEFAULT = 1;
  
  /**
   * Distance of head / tail positioned edge labels to the source or target node.
   */
  public final static IProperty<Float> LABEL_DISTANCE = new Property<Float>(
            "org.eclipse.elk.alg.graphviz.labelDistance",
            LABEL_DISTANCE_DEFAULT);
  
  /**
   * Default value for {@link #LAYER_SPACING_FACTOR}.
   */
  private final static float LAYER_SPACING_FACTOR_DEFAULT = 1;
  
  /**
   * Factor for the spacing of different layers (ranks).
   */
  public final static IProperty<Float> LAYER_SPACING_FACTOR = new Property<Float>(
            "org.eclipse.elk.alg.graphviz.layerSpacingFactor",
            LAYER_SPACING_FACTOR_DEFAULT);
  
  /**
   * The maximum number of iterations.
   */
  public final static IProperty<Integer> MAXITER = new Property<Integer>(
            "org.eclipse.elk.alg.graphviz.maxiter");
  
  /**
   * Default value for {@link #NEATO_MODEL}.
   */
  private final static NeatoModel NEATO_MODEL_DEFAULT = NeatoModel.SHORTPATH;
  
  /**
   * Specifies how the distance matrix is computed for the input graph.
   */
  public final static IProperty<NeatoModel> NEATO_MODEL = new Property<NeatoModel>(
            "org.eclipse.elk.alg.graphviz.neatoModel",
            NEATO_MODEL_DEFAULT);
  
  /**
   * Default value for {@link #OVERLAP_MODE}.
   */
  private final static OverlapMode OVERLAP_MODE_DEFAULT = OverlapMode.PRISM;
  
  /**
   * Determines if and how node overlaps should be removed.
   */
  public final static IProperty<OverlapMode> OVERLAP_MODE = new Property<OverlapMode>(
            "org.eclipse.elk.alg.graphviz.overlapMode",
            OVERLAP_MODE_DEFAULT);
  
  /**
   * Default value for {@link #DIRECTION} with algorithm "Dot".
   */
  private final static Direction DOT_SUP_DIRECTION = Direction.DOWN;
  
  /**
   * Default value for {@link #SPACING_NODE} with algorithm "Dot".
   */
  private final static float DOT_SUP_SPACING_NODE = 20;
  
  /**
   * Default value for {@link #SPACING_BORDER} with algorithm "Dot".
   */
  private final static float DOT_SUP_SPACING_BORDER = 10;
  
  /**
   * Default value for {@link #EDGE_ROUTING} with algorithm "Dot".
   */
  private final static EdgeRouting DOT_SUP_EDGE_ROUTING = EdgeRouting.SPLINES;
  
  /**
   * Default value for {@link #ITERATIONS_FACTOR} with algorithm "Dot".
   */
  private final static float DOT_SUP_ITERATIONS_FACTOR = 1;
  
  /**
   * Default value for {@link #SPACING_NODE} with algorithm "Neato".
   */
  private final static float NEATO_SUP_SPACING_NODE = 40;
  
  /**
   * Default value for {@link #SPACING_BORDER} with algorithm "Neato".
   */
  private final static float NEATO_SUP_SPACING_BORDER = 10;
  
  /**
   * Default value for {@link #RANDOM_SEED} with algorithm "Neato".
   */
  private final static int NEATO_SUP_RANDOM_SEED = 1;
  
  /**
   * Default value for {@link #EDGE_ROUTING} with algorithm "Neato".
   */
  private final static EdgeRouting NEATO_SUP_EDGE_ROUTING = EdgeRouting.SPLINES;
  
  /**
   * Default value for {@link #SEPARATE_CONNECTED_COMPONENTS} with algorithm "Neato".
   */
  private final static boolean NEATO_SUP_SEPARATE_CONNECTED_COMPONENTS = false;
  
  /**
   * Default value for {@link #EPSILON} with algorithm "Neato".
   */
  private final static float NEATO_SUP_EPSILON = 0.0001f;
  
  /**
   * Default value for {@link #MAXITER} with algorithm "Neato".
   */
  private final static int NEATO_SUP_MAXITER = 200;
  
  /**
   * Default value for {@link #SPACING_NODE} with algorithm "FDP".
   */
  private final static float FDP_SUP_SPACING_NODE = 40;
  
  /**
   * Default value for {@link #SPACING_BORDER} with algorithm "FDP".
   */
  private final static float FDP_SUP_SPACING_BORDER = 10;
  
  /**
   * Default value for {@link #EDGE_ROUTING} with algorithm "FDP".
   */
  private final static EdgeRouting FDP_SUP_EDGE_ROUTING = EdgeRouting.SPLINES;
  
  /**
   * Default value for {@link #SEPARATE_CONNECTED_COMPONENTS} with algorithm "FDP".
   */
  private final static boolean FDP_SUP_SEPARATE_CONNECTED_COMPONENTS = false;
  
  /**
   * Default value for {@link #MAXITER} with algorithm "FDP".
   */
  private final static int FDP_SUP_MAXITER = 600;
  
  /**
   * Default value for {@link #SPACING_NODE} with algorithm "Twopi".
   */
  private final static float TWOPI_SUP_SPACING_NODE = 60;
  
  /**
   * Default value for {@link #SPACING_BORDER} with algorithm "Twopi".
   */
  private final static float TWOPI_SUP_SPACING_BORDER = 10;
  
  /**
   * Default value for {@link #EDGE_ROUTING} with algorithm "Twopi".
   */
  private final static EdgeRouting TWOPI_SUP_EDGE_ROUTING = EdgeRouting.SPLINES;
  
  /**
   * Default value for {@link #SPACING_NODE} with algorithm "Circo".
   */
  private final static float CIRCO_SUP_SPACING_NODE = 40;
  
  /**
   * Default value for {@link #SPACING_BORDER} with algorithm "Circo".
   */
  private final static float CIRCO_SUP_SPACING_BORDER = 10;
  
  /**
   * Default value for {@link #EDGE_ROUTING} with algorithm "Circo".
   */
  private final static EdgeRouting CIRCO_SUP_EDGE_ROUTING = EdgeRouting.SPLINES;
  
  /**
   * Default value for {@link #SEPARATE_CONNECTED_COMPONENTS} with algorithm "Circo".
   */
  private final static boolean CIRCO_SUP_SEPARATE_CONNECTED_COMPONENTS = false;
  
  public void apply(final ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.graphviz.adaptPortPositions",
        "",
        "Adapt Port Positions",
        "Whether ports should be moved to the point where edges cross the node\'s bounds.",
        ADAPT_PORT_POSITIONS_DEFAULT,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.graphviz.concentrate",
        "",
        "Concentrate Edges",
        "Merges multiedges into a single edge and causes partially parallel edges to share part of their paths.",
        CONCENTRATE_DEFAULT,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.graphviz.epsilon",
        "",
        "Epsilon",
        "Terminating condition. If the length squared of all energy gradients are less than epsilon, the algorithm stops.",
        null,
        LayoutOptionData.Type.FLOAT,
        float.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.graphviz.iterationsFactor",
        "",
        "Iterations Factor",
        "Multiplicative scale factor for the maximal number of iterations used during crossing minimization, node ranking, and node positioning.",
        null,
        LayoutOptionData.Type.FLOAT,
        float.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.graphviz.labelAngle",
        "",
        "Label Angle",
        "Angle between head / tail positioned edge labels and the corresponding edge.",
        LABEL_ANGLE_DEFAULT,
        LayoutOptionData.Type.FLOAT,
        float.class,
        EnumSet.of(LayoutOptionData.Target.EDGES),
        LayoutOptionData.Visibility.VISIBLE
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.graphviz.labelDistance",
        "",
        "Label Distance",
        "Distance of head / tail positioned edge labels to the source or target node.",
        LABEL_DISTANCE_DEFAULT,
        LayoutOptionData.Type.FLOAT,
        float.class,
        EnumSet.of(LayoutOptionData.Target.EDGES),
        LayoutOptionData.Visibility.VISIBLE
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.graphviz.layerSpacingFactor",
        "",
        "Layer Spacing Factor",
        "Factor for the spacing of different layers (ranks).",
        LAYER_SPACING_FACTOR_DEFAULT,
        LayoutOptionData.Type.FLOAT,
        float.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.graphviz.maxiter",
        "",
        "Max. Iterations",
        "The maximum number of iterations.",
        null,
        LayoutOptionData.Type.INT,
        int.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.graphviz.neatoModel",
        "",
        "Distance Model",
        "Specifies how the distance matrix is computed for the input graph.",
        NEATO_MODEL_DEFAULT,
        LayoutOptionData.Type.ENUM,
        NeatoModel.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.ADVANCED
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.graphviz.overlapMode",
        "",
        "Overlap Removal",
        "Determines if and how node overlaps should be removed.",
        OVERLAP_MODE_DEFAULT,
        LayoutOptionData.Type.ENUM,
        OverlapMode.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
    ));
    registry.register(new LayoutAlgorithmData(
        "org.eclipse.elk.alg.graphviz.dot",
        "Dot",
        "Layered drawings of directed graphs. The algorithm aims edges in the same direction (top to bottom, or left to right) and then attempts to avoid edge crossings and reduce edge length. Edges are routed as spline curves and are thus drawn very smoothly. This algorithm is very suitable for state machine and activity diagrams, where the direction of edges has an important role.",
        new AlgorithmFactory(GraphvizLayoutProvider.class, "DOT"),
        "org.eclipse.elk.layered",
        "Graphviz",
        "images/dot.png",
        EnumSet.of(GraphFeature.SELF_LOOPS, GraphFeature.MULTI_EDGES, GraphFeature.EDGE_LABELS, GraphFeature.COMPOUND, GraphFeature.CLUSTERS)
    ));
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.dot",
        "org.eclipse.elk.direction",
        DOT_SUP_DIRECTION
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.dot",
        "org.eclipse.elk.spacing.node",
        DOT_SUP_SPACING_NODE
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.dot",
        "org.eclipse.elk.spacing.border",
        DOT_SUP_SPACING_BORDER
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.dot",
        "org.eclipse.elk.spacing.label",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.dot",
        "org.eclipse.elk.nodeSize.constraints",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.dot",
        "org.eclipse.elk.nodeSize.options",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.dot",
        "org.eclipse.elk.edgeRouting",
        DOT_SUP_EDGE_ROUTING
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.dot",
        "org.eclipse.elk.debugMode",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.dot",
        "org.eclipse.elk.hierarchyHandling",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.dot",
        "org.eclipse.elk.alg.graphviz.iterationsFactor",
        DOT_SUP_ITERATIONS_FACTOR
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.dot",
        "org.eclipse.elk.alg.graphviz.concentrate",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.dot",
        "org.eclipse.elk.alg.graphviz.labelDistance",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.dot",
        "org.eclipse.elk.alg.graphviz.labelAngle",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.dot",
        "org.eclipse.elk.alg.graphviz.layerSpacingFactor",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.dot",
        "org.eclipse.elk.alg.graphviz.adaptPortPositions",
        null
    );
    registry.register(new LayoutAlgorithmData(
        "org.eclipse.elk.alg.graphviz.neato",
        "Neato",
        "Spring model layouts. Neato attempts to minimize a global energy function, which is equivalent to statistical multi-dimensional scaling. The solution is achieved using stress majorization, though the older Kamada-Kawai algorithm, using steepest descent, is also available.",
        new AlgorithmFactory(GraphvizLayoutProvider.class, "NEATO"),
        "org.eclipse.elk.force",
        "Graphviz",
        "images/neato.png",
        EnumSet.of(GraphFeature.SELF_LOOPS, GraphFeature.MULTI_EDGES, GraphFeature.EDGE_LABELS)
    ));
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.neato",
        "org.eclipse.elk.spacing.node",
        NEATO_SUP_SPACING_NODE
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.neato",
        "org.eclipse.elk.spacing.border",
        NEATO_SUP_SPACING_BORDER
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.neato",
        "org.eclipse.elk.spacing.label",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.neato",
        "org.eclipse.elk.nodeSize.constraints",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.neato",
        "org.eclipse.elk.nodeSize.options",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.neato",
        "org.eclipse.elk.randomSeed",
        NEATO_SUP_RANDOM_SEED
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.neato",
        "org.eclipse.elk.interactive",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.neato",
        "org.eclipse.elk.edgeRouting",
        NEATO_SUP_EDGE_ROUTING
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.neato",
        "org.eclipse.elk.debugMode",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.neato",
        "org.eclipse.elk.separateConnectedComponents",
        NEATO_SUP_SEPARATE_CONNECTED_COMPONENTS
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.neato",
        "org.eclipse.elk.alg.graphviz.concentrate",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.neato",
        "org.eclipse.elk.alg.graphviz.epsilon",
        NEATO_SUP_EPSILON
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.neato",
        "org.eclipse.elk.alg.graphviz.labelDistance",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.neato",
        "org.eclipse.elk.alg.graphviz.labelAngle",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.neato",
        "org.eclipse.elk.alg.graphviz.maxiter",
        NEATO_SUP_MAXITER
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.neato",
        "org.eclipse.elk.alg.graphviz.neatoModel",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.neato",
        "org.eclipse.elk.alg.graphviz.overlapMode",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.neato",
        "org.eclipse.elk.alg.graphviz.adaptPortPositions",
        null
    );
    registry.register(new LayoutAlgorithmData(
        "org.eclipse.elk.alg.graphviz.fdp",
        "FDP",
        "Spring model layouts similar to those of Neato, but does this by reducing forces rather than working with energy. Fdp implements the Fruchterman-Reingold heuristic including a multigrid solver that handles larger graphs and clustered undirected graphs.",
        new AlgorithmFactory(GraphvizLayoutProvider.class, "FDP"),
        "org.eclipse.elk.force",
        "Graphviz",
        "images/fdp.png",
        EnumSet.of(GraphFeature.SELF_LOOPS, GraphFeature.MULTI_EDGES, GraphFeature.EDGE_LABELS, GraphFeature.CLUSTERS)
    ));
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.fdp",
        "org.eclipse.elk.spacing.node",
        FDP_SUP_SPACING_NODE
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.fdp",
        "org.eclipse.elk.spacing.border",
        FDP_SUP_SPACING_BORDER
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.fdp",
        "org.eclipse.elk.spacing.label",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.fdp",
        "org.eclipse.elk.nodeSize.constraints",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.fdp",
        "org.eclipse.elk.nodeSize.options",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.fdp",
        "org.eclipse.elk.interactive",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.fdp",
        "org.eclipse.elk.edgeRouting",
        FDP_SUP_EDGE_ROUTING
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.fdp",
        "org.eclipse.elk.debugMode",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.fdp",
        "org.eclipse.elk.hierarchyHandling",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.fdp",
        "org.eclipse.elk.separateConnectedComponents",
        FDP_SUP_SEPARATE_CONNECTED_COMPONENTS
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.fdp",
        "org.eclipse.elk.alg.graphviz.concentrate",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.fdp",
        "org.eclipse.elk.alg.graphviz.labelDistance",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.fdp",
        "org.eclipse.elk.alg.graphviz.labelAngle",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.fdp",
        "org.eclipse.elk.alg.graphviz.maxiter",
        FDP_SUP_MAXITER
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.fdp",
        "org.eclipse.elk.alg.graphviz.overlapMode",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.fdp",
        "org.eclipse.elk.alg.graphviz.adaptPortPositions",
        null
    );
    registry.register(new LayoutAlgorithmData(
        "org.eclipse.elk.alg.graphviz.twopi",
        "Twopi",
        "Radial layouts, after Wills \'97. The nodes are placed on concentric circles depending on their distance from a given root node. The algorithm is designed to handle not only small graphs, but also very large ones.",
        new AlgorithmFactory(GraphvizLayoutProvider.class, "TWOPI"),
        "org.eclipse.elk.tree",
        "Graphviz",
        "images/twopi.png",
        EnumSet.of(GraphFeature.SELF_LOOPS, GraphFeature.MULTI_EDGES, GraphFeature.EDGE_LABELS)
    ));
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.twopi",
        "org.eclipse.elk.spacing.node",
        TWOPI_SUP_SPACING_NODE
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.twopi",
        "org.eclipse.elk.spacing.border",
        TWOPI_SUP_SPACING_BORDER
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.twopi",
        "org.eclipse.elk.spacing.label",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.twopi",
        "org.eclipse.elk.nodeSize.constraints",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.twopi",
        "org.eclipse.elk.nodeSize.options",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.twopi",
        "org.eclipse.elk.edgeRouting",
        TWOPI_SUP_EDGE_ROUTING
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.twopi",
        "org.eclipse.elk.debugMode",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.twopi",
        "org.eclipse.elk.alg.graphviz.concentrate",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.twopi",
        "org.eclipse.elk.alg.graphviz.labelDistance",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.twopi",
        "org.eclipse.elk.alg.graphviz.labelAngle",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.twopi",
        "org.eclipse.elk.alg.graphviz.overlapMode",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.twopi",
        "org.eclipse.elk.alg.graphviz.adaptPortPositions",
        null
    );
    registry.register(new LayoutAlgorithmData(
        "org.eclipse.elk.alg.graphviz.circo",
        "Circo",
        "Circular layout, after Six and Tollis \'99, Kaufmann and Wiese \'02. The algorithm finds biconnected components and arranges each component in a circle, trying to minimize the number of crossings inside the circle. This is suitable for certain diagrams of multiple cyclic structures such as certain telecommunications networks.",
        new AlgorithmFactory(GraphvizLayoutProvider.class, "CIRCO"),
        "org.eclipse.elk.circle",
        "Graphviz",
        "images/circo.png",
        EnumSet.of(GraphFeature.SELF_LOOPS, GraphFeature.MULTI_EDGES, GraphFeature.EDGE_LABELS)
    ));
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.circo",
        "org.eclipse.elk.spacing.node",
        CIRCO_SUP_SPACING_NODE
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.circo",
        "org.eclipse.elk.spacing.border",
        CIRCO_SUP_SPACING_BORDER
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.circo",
        "org.eclipse.elk.spacing.label",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.circo",
        "org.eclipse.elk.nodeSize.constraints",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.circo",
        "org.eclipse.elk.nodeSize.options",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.circo",
        "org.eclipse.elk.edgeRouting",
        CIRCO_SUP_EDGE_ROUTING
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.circo",
        "org.eclipse.elk.debugMode",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.circo",
        "org.eclipse.elk.separateConnectedComponents",
        CIRCO_SUP_SEPARATE_CONNECTED_COMPONENTS
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.circo",
        "org.eclipse.elk.alg.graphviz.concentrate",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.circo",
        "org.eclipse.elk.alg.graphviz.labelDistance",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.circo",
        "org.eclipse.elk.alg.graphviz.labelAngle",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.circo",
        "org.eclipse.elk.alg.graphviz.overlapMode",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.graphviz.circo",
        "org.eclipse.elk.alg.graphviz.adaptPortPositions",
        null
    );
  }
}
