/**
 * Declarations for the ELK Force layout algorithm.
 */
package org.eclipse.elk.alg.force.properties;

import java.util.EnumSet;
import org.eclipse.elk.alg.force.ForceLayoutProvider;
import org.eclipse.elk.alg.force.model.ForceModelStrategy;
import org.eclipse.elk.alg.force.properties.ForceMetaDataProvider;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class ForceOptions implements ILayoutMetaDataProvider {
  /**
   * The id of the ELK Force algorithm.
   */
  public final static String ALGORITHM_ID = "org.eclipse.elk.force";
  
  /**
   * Default value for {@link #PRIORITY} with algorithm "ELK Force".
   */
  private final static int PRIORITY_DEFAULT = 1;
  
  /**
   * Property constant to access Priority from within the layout algorithm code.
   */
  public final static IProperty<Integer> PRIORITY = new Property<Integer>(
                                CoreOptions.PRIORITY,
                                PRIORITY_DEFAULT);
  
  /**
   * Default value for {@link #SPACING_NODE} with algorithm "ELK Force".
   */
  private final static float SPACING_NODE_DEFAULT = 80;
  
  /**
   * Property constant to access Node Spacing from within the layout algorithm code.
   */
  public final static IProperty<Float> SPACING_NODE = new Property<Float>(
                                CoreOptions.SPACING_NODE,
                                SPACING_NODE_DEFAULT);
  
  /**
   * Default value for {@link #SPACING_BORDER} with algorithm "ELK Force".
   */
  private final static float SPACING_BORDER_DEFAULT = 50;
  
  /**
   * Property constant to access Border Spacing from within the layout algorithm code.
   */
  public final static IProperty<Float> SPACING_BORDER = new Property<Float>(
                                CoreOptions.SPACING_BORDER,
                                SPACING_BORDER_DEFAULT);
  
  /**
   * Default value for {@link #SPACING_LABEL} with algorithm "ELK Force".
   */
  private final static float SPACING_LABEL_DEFAULT = 5;
  
  /**
   * Property constant to access Label Spacing from within the layout algorithm code.
   */
  public final static IProperty<Float> SPACING_LABEL = new Property<Float>(
                                CoreOptions.SPACING_LABEL,
                                SPACING_LABEL_DEFAULT);
  
  /**
   * Default value for {@link #ASPECT_RATIO} with algorithm "ELK Force".
   */
  private final static float ASPECT_RATIO_DEFAULT = 1.6f;
  
  /**
   * Property constant to access Aspect Ratio from within the layout algorithm code.
   */
  public final static IProperty<Float> ASPECT_RATIO = new Property<Float>(
                                CoreOptions.ASPECT_RATIO,
                                ASPECT_RATIO_DEFAULT);
  
  /**
   * Default value for {@link #RANDOM_SEED} with algorithm "ELK Force".
   */
  private final static int RANDOM_SEED_DEFAULT = 1;
  
  /**
   * Property constant to access Randomization Seed from within the layout algorithm code.
   */
  public final static IProperty<Integer> RANDOM_SEED = new Property<Integer>(
                                CoreOptions.RANDOM_SEED,
                                RANDOM_SEED_DEFAULT);
  
  /**
   * Default value for {@link #SEPARATE_CONNECTED_COMPONENTS} with algorithm "ELK Force".
   */
  private final static boolean SEPARATE_CONNECTED_COMPONENTS_DEFAULT = true;
  
  /**
   * Property constant to access Separate Connected Components from within the layout algorithm code.
   */
  public final static IProperty<Boolean> SEPARATE_CONNECTED_COMPONENTS = new Property<Boolean>(
                                CoreOptions.SEPARATE_CONNECTED_COMPONENTS,
                                SEPARATE_CONNECTED_COMPONENTS_DEFAULT);
  
  /**
   * Property constant to access Interactive from within the layout algorithm code.
   */
  public final static IProperty<Boolean> INTERACTIVE = CoreOptions.INTERACTIVE;
  
  /**
   * Property constant to access Port Constraints from within the layout algorithm code.
   */
  public final static IProperty<PortConstraints> PORT_CONSTRAINTS = CoreOptions.PORT_CONSTRAINTS;
  
  /**
   * Property constant to access Force Model from within the layout algorithm code.
   */
  public final static IProperty<ForceModelStrategy> MODEL = ForceMetaDataProvider.MODEL;
  
  /**
   * Property constant to access FR Temperature from within the layout algorithm code.
   */
  public final static IProperty<Float> TEMPERATURE = ForceMetaDataProvider.TEMPERATURE;
  
  /**
   * Property constant to access Iterations from within the layout algorithm code.
   */
  public final static IProperty<Integer> ITERATIONS = ForceMetaDataProvider.ITERATIONS;
  
  /**
   * Property constant to access Eades Repulsion from within the layout algorithm code.
   */
  public final static IProperty<Float> REPULSION = ForceMetaDataProvider.REPULSION;
  
  /**
   * Property constant to access Repulsive Power from within the layout algorithm code.
   */
  public final static IProperty<Integer> REPULSIVE_POWER = ForceMetaDataProvider.REPULSIVE_POWER;
  
  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new org.eclipse.elk.core.data.LayoutAlgorithmData(
        "org.eclipse.elk.force",
        "ELK Force",
        "Force-based algorithm provided by the Eclipse Layout Kernel. Implements methods that follow physical analogies by simulating forces that move the nodes into a balanced distribution. Currently the original Eades model and the Fruchterman - Reingold model are supported.",
        new org.eclipse.elk.core.util.AlgorithmFactory(ForceLayoutProvider.class, ""),
        "org.eclipse.elk.force",
        null,
        "images/force.png",
        EnumSet.of(org.eclipse.elk.core.options.GraphFeature.MULTI_EDGES, org.eclipse.elk.core.options.GraphFeature.EDGE_LABELS)
    ));
    registry.addOptionSupport(
        "org.eclipse.elk.force",
        "org.eclipse.elk.priority",
        PRIORITY_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.force",
        "org.eclipse.elk.spacing.node",
        SPACING_NODE_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.force",
        "org.eclipse.elk.spacing.border",
        SPACING_BORDER_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.force",
        "org.eclipse.elk.spacing.label",
        SPACING_LABEL_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.force",
        "org.eclipse.elk.aspectRatio",
        ASPECT_RATIO_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.force",
        "org.eclipse.elk.randomSeed",
        RANDOM_SEED_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.force",
        "org.eclipse.elk.separateConnectedComponents",
        SEPARATE_CONNECTED_COMPONENTS_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.force",
        "org.eclipse.elk.interactive",
        INTERACTIVE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.force",
        "org.eclipse.elk.portConstraints",
        PORT_CONSTRAINTS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.force",
        "org.eclipse.elk.force.model",
        MODEL.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.force",
        "org.eclipse.elk.force.temperature",
        TEMPERATURE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.force",
        "org.eclipse.elk.force.iterations",
        ITERATIONS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.force",
        "org.eclipse.elk.force.repulsion",
        REPULSION.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.force",
        "org.eclipse.elk.force.repulsivePower",
        REPULSIVE_POWER.getDefault()
    );
  }
}
