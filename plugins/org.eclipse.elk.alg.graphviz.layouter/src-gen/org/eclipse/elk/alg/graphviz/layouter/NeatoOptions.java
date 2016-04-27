package org.eclipse.elk.alg.graphviz.layouter;

import java.util.EnumSet;
import org.eclipse.elk.alg.graphviz.dot.transform.NeatoModel;
import org.eclipse.elk.alg.graphviz.dot.transform.OverlapMode;
import org.eclipse.elk.alg.graphviz.layouter.GraphvizLayoutProvider;
import org.eclipse.elk.alg.graphviz.layouter.GraphvizMetaDataProvider;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.GraphFeature;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.core.util.AlgorithmFactory;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class NeatoOptions implements ILayoutMetaDataProvider {
  /**
   * Default value for {@link #SPACING_NODE} with algorithm "Neato".
   */
  private final static float SPACING_NODE_DEFAULT = 40;
  
  /**
   * Property constant to access Node Spacing from within the layout algorithm code.
   */
  public final static IProperty<Float> SPACING_NODE = new Property<Float>(
                                CoreOptions.SPACING_NODE,
                                SPACING_NODE_DEFAULT);
  
  /**
   * Default value for {@link #SPACING_BORDER} with algorithm "Neato".
   */
  private final static float SPACING_BORDER_DEFAULT = 10;
  
  /**
   * Property constant to access Border Spacing from within the layout algorithm code.
   */
  public final static IProperty<Float> SPACING_BORDER = new Property<Float>(
                                CoreOptions.SPACING_BORDER,
                                SPACING_BORDER_DEFAULT);
  
  /**
   * Property constant to access Label Spacing from within the layout algorithm code.
   */
  public final static IProperty<Float> SPACING_LABEL = CoreOptions.SPACING_LABEL;
  
  /**
   * Property constant to access Node Size Constraints from within the layout algorithm code.
   */
  public final static IProperty<EnumSet<SizeConstraint>> NODE_SIZE_CONSTRAINTS = CoreOptions.NODE_SIZE_CONSTRAINTS;
  
  /**
   * Property constant to access Node Size Options from within the layout algorithm code.
   */
  public final static IProperty<EnumSet<SizeOptions>> NODE_SIZE_OPTIONS = CoreOptions.NODE_SIZE_OPTIONS;
  
  /**
   * Default value for {@link #RANDOM_SEED} with algorithm "Neato".
   */
  private final static int RANDOM_SEED_DEFAULT = 1;
  
  /**
   * Property constant to access Randomization Seed from within the layout algorithm code.
   */
  public final static IProperty<Integer> RANDOM_SEED = new Property<Integer>(
                                CoreOptions.RANDOM_SEED,
                                RANDOM_SEED_DEFAULT);
  
  /**
   * Property constant to access Interactive from within the layout algorithm code.
   */
  public final static IProperty<Boolean> INTERACTIVE = CoreOptions.INTERACTIVE;
  
  /**
   * Default value for {@link #EDGE_ROUTING} with algorithm "Neato".
   */
  private final static EdgeRouting EDGE_ROUTING_DEFAULT = EdgeRouting.SPLINES;
  
  /**
   * Property constant to access Edge Routing from within the layout algorithm code.
   */
  public final static IProperty<EdgeRouting> EDGE_ROUTING = new Property<EdgeRouting>(
                                CoreOptions.EDGE_ROUTING,
                                EDGE_ROUTING_DEFAULT);
  
  /**
   * Property constant to access Debug Mode from within the layout algorithm code.
   */
  public final static IProperty<Boolean> DEBUG_MODE = CoreOptions.DEBUG_MODE;
  
  /**
   * Default value for {@link #SEPARATE_CONNECTED_COMPONENTS} with algorithm "Neato".
   */
  private final static boolean SEPARATE_CONNECTED_COMPONENTS_DEFAULT = false;
  
  /**
   * Property constant to access Separate Connected Components from within the layout algorithm code.
   */
  public final static IProperty<Boolean> SEPARATE_CONNECTED_COMPONENTS = new Property<Boolean>(
                                CoreOptions.SEPARATE_CONNECTED_COMPONENTS,
                                SEPARATE_CONNECTED_COMPONENTS_DEFAULT);
  
  /**
   * Property constant to access Concentrate Edges from within the layout algorithm code.
   */
  public final static IProperty<Boolean> CONCENTRATE = GraphvizMetaDataProvider.CONCENTRATE;
  
  /**
   * Default value for {@link #EPSILON} with algorithm "Neato".
   */
  private final static float EPSILON_DEFAULT = 0.0001f;
  
  /**
   * Property constant to access Epsilon from within the layout algorithm code.
   */
  public final static IProperty<Float> EPSILON = new Property<Float>(
                                GraphvizMetaDataProvider.EPSILON,
                                EPSILON_DEFAULT);
  
  /**
   * Property constant to access Label Distance from within the layout algorithm code.
   */
  public final static IProperty<Float> LABEL_DISTANCE = GraphvizMetaDataProvider.LABEL_DISTANCE;
  
  /**
   * Property constant to access Label Angle from within the layout algorithm code.
   */
  public final static IProperty<Float> LABEL_ANGLE = GraphvizMetaDataProvider.LABEL_ANGLE;
  
  /**
   * Default value for {@link #MAXITER} with algorithm "Neato".
   */
  private final static int MAXITER_DEFAULT = 200;
  
  /**
   * Property constant to access Max. Iterations from within the layout algorithm code.
   */
  public final static IProperty<Integer> MAXITER = new Property<Integer>(
                                GraphvizMetaDataProvider.MAXITER,
                                MAXITER_DEFAULT);
  
  /**
   * Property constant to access Distance Model from within the layout algorithm code.
   */
  public final static IProperty<NeatoModel> NEATO_MODEL = GraphvizMetaDataProvider.NEATO_MODEL;
  
  /**
   * Property constant to access Overlap Removal from within the layout algorithm code.
   */
  public final static IProperty<OverlapMode> OVERLAP_MODE = GraphvizMetaDataProvider.OVERLAP_MODE;
  
  /**
   * Property constant to access Adapt Port Positions from within the layout algorithm code.
   */
  public final static IProperty<Boolean> ADAPT_PORT_POSITIONS = GraphvizMetaDataProvider.ADAPT_PORT_POSITIONS;
  
  public void apply(final ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutAlgorithmData(
        "org.eclipse.elk.graphviz.neato",
        "Neato",
        "Spring model layouts. Neato attempts to minimize a global energy function, which is equivalent to statistical multi-dimensional scaling. The solution is achieved using stress majorization, though the older Kamada-Kawai algorithm, using steepest descent, is also available.",
        new AlgorithmFactory(GraphvizLayoutProvider.class, "NEATO"),
        "org.eclipse.elk.force",
        "Graphviz",
        "images/neato.png",
        EnumSet.of(GraphFeature.SELF_LOOPS, GraphFeature.MULTI_EDGES, GraphFeature.EDGE_LABELS)
    ));
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.neato",
        "org.eclipse.elk.spacing.node",
        SPACING_NODE_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.neato",
        "org.eclipse.elk.spacing.border",
        SPACING_BORDER_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.neato",
        "org.eclipse.elk.spacing.label",
        SPACING_LABEL.getDefault()
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
