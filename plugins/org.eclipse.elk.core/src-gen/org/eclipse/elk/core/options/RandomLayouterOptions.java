/**
 * Core definitions of the Eclipse Layout Kernel.
 */
package org.eclipse.elk.core.options;

import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.util.RandomLayoutProvider;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class RandomLayouterOptions implements ILayoutMetaDataProvider {
  /**
   * The id of the Randomizer algorithm.
   */
  public final static String ALGORITHM_ID = "org.eclipse.elk.random";
  
  /**
   * Default value for {@link #RANDOM_SEED} with algorithm "Randomizer".
   */
  private final static int RANDOM_SEED_DEFAULT = 0;
  
  /**
   * Property constant to access Randomization Seed from within the layout algorithm code.
   */
  public final static IProperty<Integer> RANDOM_SEED = new Property<Integer>(
                                CoreOptions.RANDOM_SEED,
                                RANDOM_SEED_DEFAULT);
  
  /**
   * Default value for {@link #SPACING_NODE} with algorithm "Randomizer".
   */
  private final static float SPACING_NODE_DEFAULT = 15;
  
  /**
   * Property constant to access Node Spacing from within the layout algorithm code.
   */
  public final static IProperty<Float> SPACING_NODE = new Property<Float>(
                                CoreOptions.SPACING_NODE,
                                SPACING_NODE_DEFAULT);
  
  /**
   * Default value for {@link #SPACING_BORDER} with algorithm "Randomizer".
   */
  private final static float SPACING_BORDER_DEFAULT = 15;
  
  /**
   * Property constant to access Border Spacing from within the layout algorithm code.
   */
  public final static IProperty<Float> SPACING_BORDER = new Property<Float>(
                                CoreOptions.SPACING_BORDER,
                                SPACING_BORDER_DEFAULT);
  
  /**
   * Default value for {@link #ASPECT_RATIO} with algorithm "Randomizer".
   */
  private final static float ASPECT_RATIO_DEFAULT = 1.6f;
  
  /**
   * Property constant to access Aspect Ratio from within the layout algorithm code.
   */
  public final static IProperty<Float> ASPECT_RATIO = new Property<Float>(
                                CoreOptions.ASPECT_RATIO,
                                ASPECT_RATIO_DEFAULT);
  
  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new org.eclipse.elk.core.data.LayoutAlgorithmData(
        "org.eclipse.elk.random",
        "Randomizer",
        "Distributes the nodes randomly on the plane, leading to very obfuscating layouts. Can be useful to demonstrate the power of \"real\" layout algorithms.",
        new org.eclipse.elk.core.util.AlgorithmFactory(RandomLayoutProvider.class, ""),
        null,
        "ELK",
        "images/random.png",
        null
    ));
    registry.addOptionSupport(
        "org.eclipse.elk.random",
        "org.eclipse.elk.randomSeed",
        RANDOM_SEED_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.random",
        "org.eclipse.elk.spacing.node",
        SPACING_NODE_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.random",
        "org.eclipse.elk.spacing.border",
        SPACING_BORDER_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.random",
        "org.eclipse.elk.aspectRatio",
        ASPECT_RATIO_DEFAULT
    );
  }
}
