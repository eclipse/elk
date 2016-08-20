/**
 * Layout algorithms contributed by GMF / GEF.
 */
package org.eclipse.elk.conn.gmf.layouter;

import java.util.EnumSet;
import org.eclipse.elk.conn.gmf.layouter.Draw2DLayoutProvider;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class Draw2DOptions implements ILayoutMetaDataProvider {
  /**
   * The id of the Draw2D Layout algorithm.
   */
  public final static String ALGORITHM_ID = "org.eclipse.elk.conn.gmf.layouter.Draw2D";
  
  /**
   * Default value for {@link #SPACING_NODE} with algorithm "Draw2D Layout".
   */
  private final static float SPACING_NODE_DEFAULT = 16;
  
  /**
   * Property constant to access Node Spacing from within the layout algorithm code.
   */
  public final static IProperty<Float> SPACING_NODE = new Property<Float>(
                                CoreOptions.SPACING_NODE,
                                SPACING_NODE_DEFAULT);
  
  /**
   * Default value for {@link #SPACING_BORDER} with algorithm "Draw2D Layout".
   */
  private final static float SPACING_BORDER_DEFAULT = 16;
  
  /**
   * Property constant to access Border Spacing from within the layout algorithm code.
   */
  public final static IProperty<Float> SPACING_BORDER = new Property<Float>(
                                CoreOptions.SPACING_BORDER,
                                SPACING_BORDER_DEFAULT);
  
  /**
   * Default value for {@link #DIRECTION} with algorithm "Draw2D Layout".
   */
  private final static Direction DIRECTION_DEFAULT = Direction.RIGHT;
  
  /**
   * Property constant to access Direction from within the layout algorithm code.
   */
  public final static IProperty<Direction> DIRECTION = new Property<Direction>(
                                CoreOptions.DIRECTION,
                                DIRECTION_DEFAULT);
  
  /**
   * Property constant to access Node Size Constraints from within the layout algorithm code.
   */
  public final static IProperty<EnumSet<SizeConstraint>> NODE_SIZE_CONSTRAINTS = CoreOptions.NODE_SIZE_CONSTRAINTS;
  
  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new org.eclipse.elk.core.data.LayoutAlgorithmData(
        "org.eclipse.elk.conn.gmf.layouter.Draw2D",
        "Draw2D Layout",
        "\'Directed Graph Layout\' provided by the Draw2D framework. This is the same algorithm that is used by the standard layout button of GMF diagrams.",
        new org.eclipse.elk.core.util.AlgorithmFactory(Draw2DLayoutProvider.class, ""),
        "org.eclipse.elk.layered",
        "GMF",
        "images/draw2d.png",
        EnumSet.of(org.eclipse.elk.core.options.GraphFeature.MULTI_EDGES)
    ));
    registry.addOptionSupport(
        "org.eclipse.elk.conn.gmf.layouter.Draw2D",
        "org.eclipse.elk.spacing.node",
        SPACING_NODE_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.conn.gmf.layouter.Draw2D",
        "org.eclipse.elk.spacing.border",
        SPACING_BORDER_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.conn.gmf.layouter.Draw2D",
        "org.eclipse.elk.direction",
        DIRECTION_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.conn.gmf.layouter.Draw2D",
        "org.eclipse.elk.nodeSize.constraints",
        NODE_SIZE_CONSTRAINTS.getDefault()
    );
  }
}
