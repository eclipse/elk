package org.eclipse.elk.alg.graphviz.layouter;

import java.util.EnumSet;
import org.eclipse.elk.alg.graphviz.dot.transform.OverlapMode;
import org.eclipse.elk.alg.graphviz.layouter.GraphvizLayoutProvider;
import org.eclipse.elk.alg.graphviz.layouter.GraphvizMetaDataProvider;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class TwopiOptions implements ILayoutMetaDataProvider {
  /**
   * The id of the Twopi algorithm.
   */
  public final static String ALGORITHM_ID = "org.eclipse.elk.graphviz.twopi";
  
  /**
   * Default value for {@link #SPACING_NODE} with algorithm "Twopi".
   */
  private final static float SPACING_NODE_DEFAULT = 60;
  
  /**
   * Property constant to access Node Spacing from within the layout algorithm code.
   */
  public final static IProperty<Float> SPACING_NODE = new Property<Float>(
                                CoreOptions.SPACING_NODE,
                                SPACING_NODE_DEFAULT);
  
  /**
   * Default value for {@link #SPACING_BORDER} with algorithm "Twopi".
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
   * Default value for {@link #EDGE_ROUTING} with algorithm "Twopi".
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
   * Property constant to access Concentrate Edges from within the layout algorithm code.
   */
  public final static IProperty<Boolean> CONCENTRATE = GraphvizMetaDataProvider.CONCENTRATE;
  
  /**
   * Property constant to access Label Distance from within the layout algorithm code.
   */
  public final static IProperty<Float> LABEL_DISTANCE = GraphvizMetaDataProvider.LABEL_DISTANCE;
  
  /**
   * Property constant to access Label Angle from within the layout algorithm code.
   */
  public final static IProperty<Float> LABEL_ANGLE = GraphvizMetaDataProvider.LABEL_ANGLE;
  
  /**
   * Property constant to access Overlap Removal from within the layout algorithm code.
   */
  public final static IProperty<OverlapMode> OVERLAP_MODE = GraphvizMetaDataProvider.OVERLAP_MODE;
  
  /**
   * Property constant to access Adapt Port Positions from within the layout algorithm code.
   */
  public final static IProperty<Boolean> ADAPT_PORT_POSITIONS = GraphvizMetaDataProvider.ADAPT_PORT_POSITIONS;
  
  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new org.eclipse.elk.core.data.LayoutAlgorithmData(
        "org.eclipse.elk.graphviz.twopi",
        "Twopi",
        "Radial layouts, after Wills \'97. The nodes are placed on concentric circles depending on their distance from a given root node. The algorithm is designed to handle not only small graphs, but also very large ones.",
        new org.eclipse.elk.core.util.AlgorithmFactory(GraphvizLayoutProvider.class, "TWOPI"),
        "org.eclipse.elk.tree",
        "Graphviz",
        "images/twopi.png",
        EnumSet.of(org.eclipse.elk.core.options.GraphFeature.SELF_LOOPS, org.eclipse.elk.core.options.GraphFeature.MULTI_EDGES, org.eclipse.elk.core.options.GraphFeature.EDGE_LABELS)
    ));
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.twopi",
        "org.eclipse.elk.spacing.node",
        SPACING_NODE_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.twopi",
        "org.eclipse.elk.spacing.border",
        SPACING_BORDER_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.twopi",
        "org.eclipse.elk.spacing.label",
        SPACING_LABEL.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.twopi",
        "org.eclipse.elk.nodeSize.constraints",
        NODE_SIZE_CONSTRAINTS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.twopi",
        "org.eclipse.elk.nodeSize.options",
        NODE_SIZE_OPTIONS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.twopi",
        "org.eclipse.elk.edgeRouting",
        EDGE_ROUTING_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.twopi",
        "org.eclipse.elk.debugMode",
        DEBUG_MODE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.twopi",
        "org.eclipse.elk.graphviz.concentrate",
        CONCENTRATE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.twopi",
        "org.eclipse.elk.graphviz.labelDistance",
        LABEL_DISTANCE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.twopi",
        "org.eclipse.elk.graphviz.labelAngle",
        LABEL_ANGLE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.twopi",
        "org.eclipse.elk.graphviz.overlapMode",
        OVERLAP_MODE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.graphviz.twopi",
        "org.eclipse.elk.graphviz.adaptPortPositions",
        ADAPT_PORT_POSITIONS.getDefault()
    );
  }
}
