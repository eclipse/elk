/**
 * Core definitions of the Eclipse Layout Kernel.
 */
package org.eclipse.elk.core.options;

import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.CoreOptions;
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
        "org.eclipse.elk.spacing.border",
        SPACING_BORDER_DEFAULT
    );
  }
}
