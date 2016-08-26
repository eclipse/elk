/**
 * Core definitions of the Eclipse Layout Kernel.
 */
package org.eclipse.elk.core.options;

import java.util.EnumSet;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.util.FixedLayoutProvider;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class FixedLayouterOptions implements ILayoutMetaDataProvider {
  /**
   * The id of the Fixed Layout algorithm.
   */
  public final static String ALGORITHM_ID = "org.eclipse.elk.fixed";
  
  /**
   * Property constant to access Position from within the layout algorithm code.
   */
  public final static IProperty<KVector> POSITION = CoreOptions.POSITION;
  
  /**
   * Property constant to access Bend Points from within the layout algorithm code.
   */
  public final static IProperty<KVectorChain> BEND_POINTS = CoreOptions.BEND_POINTS;
  
  /**
   * Property constant to access Node Size Constraints from within the layout algorithm code.
   */
  public final static IProperty<EnumSet<SizeConstraint>> NODE_SIZE_CONSTRAINTS = CoreOptions.NODE_SIZE_CONSTRAINTS;
  
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
   * Default value for {@link #SPACING_BORDER} with algorithm "Fixed Layout".
   */
  private final static float SPACING_BORDER_DEFAULT = 15;
  
  /**
   * Property constant to access Border Spacing from within the layout algorithm code.
   */
  public final static IProperty<Float> SPACING_BORDER = new Property<Float>(
                                CoreOptions.SPACING_BORDER,
                                SPACING_BORDER_DEFAULT);
  
  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new org.eclipse.elk.core.data.LayoutAlgorithmData(
        "org.eclipse.elk.fixed",
        "Fixed Layout",
        "Keeps the current layout as it is, without any automatic modification. Optional coordinates can be given for nodes and edge bend points.",
        new org.eclipse.elk.core.util.AlgorithmFactory(FixedLayoutProvider.class, ""),
        null,
        "ELK",
        null,
        null
    ));
    registry.addOptionSupport(
        "org.eclipse.elk.fixed",
        "org.eclipse.elk.position",
        POSITION.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.fixed",
        "org.eclipse.elk.bendPoints",
        BEND_POINTS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.fixed",
        "org.eclipse.elk.nodeSize.constraints",
        NODE_SIZE_CONSTRAINTS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.fixed",
        "org.eclipse.elk.nodeSize.minimum",
        NODE_SIZE_MINIMUM.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.fixed",
        "org.eclipse.elk.nodeSize.minWidth",
        NODE_SIZE_MIN_WIDTH.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.fixed",
        "org.eclipse.elk.nodeSize.minHeight",
        NODE_SIZE_MIN_HEIGHT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.fixed",
        "org.eclipse.elk.spacing.border",
        SPACING_BORDER_DEFAULT
    );
  }
}
