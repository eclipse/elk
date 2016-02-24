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
package org.eclipse.elk.alg.force.properties;

import java.util.EnumSet;
import org.eclipse.elk.alg.force.ForceLayoutProvider;
import org.eclipse.elk.alg.force.model.ForceModelStrategy;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.GraphFeature;
import org.eclipse.elk.core.util.AlgorithmFactory;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * Declarations for the ELK Force layout algorithm.
 */
@SuppressWarnings("all")
public class ForceOptions implements ILayoutMetaDataProvider {
  /**
   * Default value for {@link #MODEL}.
   */
  private final static ForceModelStrategy MODEL_DEFAULT = ForceModelStrategy.FRUCHTERMAN_REINGOLD;
  
  /**
   * Determines the model for force calculation.
   */
  public final static IProperty<ForceModelStrategy> MODEL = new Property<ForceModelStrategy>(
            "org.eclipse.elk.alg.force.model",
            MODEL_DEFAULT);
  
  /**
   * Default value for {@link #ITERATIONS}.
   */
  private final static int ITERATIONS_DEFAULT = 300;
  
  /**
   * The number of iterations on the force model.
   */
  public final static IProperty<Integer> ITERATIONS = new Property<Integer>(
            "org.eclipse.elk.alg.force.iterations",
            ITERATIONS_DEFAULT);
  
  /**
   * Default value for {@link #REPULSIVE_POWER}.
   */
  private final static int REPULSIVE_POWER_DEFAULT = 0;
  
  /**
   * Determines how many bend points are added to the edge; such bend points are regarded as
   * repelling particles in the force model
   */
  public final static IProperty<Integer> REPULSIVE_POWER = new Property<Integer>(
            "org.eclipse.elk.alg.force.repulsivePower",
            REPULSIVE_POWER_DEFAULT);
  
  /**
   * Default value for {@link #TEMPERATURE}.
   */
  private final static float TEMPERATURE_DEFAULT = 0.001f;
  
  /**
   * The temperature is used as a scaling factor for particle displacements.
   */
  public final static IProperty<Float> TEMPERATURE = new Property<Float>(
            "org.eclipse.elk.alg.force.temperature",
            TEMPERATURE_DEFAULT);
  
  /**
   * Default value for {@link #REPULSION}.
   */
  private final static float REPULSION_DEFAULT = 5.0f;
  
  /**
   * Factor for repulsive forces in Eades' model.
   */
  public final static IProperty<Float> REPULSION = new Property<Float>(
            "org.eclipse.elk.alg.force.repulsion",
            REPULSION_DEFAULT);
  
  /**
   * Required value for dependency between {@link #TEMPERATURE} and {@link #MODEL}.
   */
  private final static ForceModelStrategy TEMPERATURE_DEP_MODEL = ForceModelStrategy.FRUCHTERMAN_REINGOLD;
  
  /**
   * Required value for dependency between {@link #REPULSION} and {@link #MODEL}.
   */
  private final static ForceModelStrategy REPULSION_DEP_MODEL = ForceModelStrategy.EADES;
  
  /**
   * Default value for {@link #PRIORITY} with algorithm "ELK Force".
   */
  private final static int FORCE_SUP_PRIORITY = 1;
  
  /**
   * Overridden value for Priority.
   */
  public final static IProperty<Integer> PRIORITY = new Property<Integer>(
            CoreOptions.PRIORITY,
            FORCE_SUP_PRIORITY);
  
  /**
   * Default value for {@link #SPACING_NODE} with algorithm "ELK Force".
   */
  private final static float FORCE_SUP_SPACING_NODE = 80;
  
  /**
   * Overridden value for Node Spacing.
   */
  public final static IProperty<Float> SPACING_NODE = new Property<Float>(
            CoreOptions.SPACING_NODE,
            FORCE_SUP_SPACING_NODE);
  
  /**
   * Default value for {@link #SPACING_BORDER} with algorithm "ELK Force".
   */
  private final static float FORCE_SUP_SPACING_BORDER = 50;
  
  /**
   * Overridden value for Border Spacing.
   */
  public final static IProperty<Float> SPACING_BORDER = new Property<Float>(
            CoreOptions.SPACING_BORDER,
            FORCE_SUP_SPACING_BORDER);
  
  /**
   * Default value for {@link #SPACING_LABEL} with algorithm "ELK Force".
   */
  private final static float FORCE_SUP_SPACING_LABEL = 5;
  
  /**
   * Overridden value for Label Spacing.
   */
  public final static IProperty<Float> SPACING_LABEL = new Property<Float>(
            CoreOptions.SPACING_LABEL,
            FORCE_SUP_SPACING_LABEL);
  
  /**
   * Default value for {@link #ASPECT_RATIO} with algorithm "ELK Force".
   */
  private final static float FORCE_SUP_ASPECT_RATIO = 1.6f;
  
  /**
   * Overridden value for Aspect Ratio.
   */
  public final static IProperty<Float> ASPECT_RATIO = new Property<Float>(
            CoreOptions.ASPECT_RATIO,
            FORCE_SUP_ASPECT_RATIO);
  
  /**
   * Default value for {@link #RANDOM_SEED} with algorithm "ELK Force".
   */
  private final static int FORCE_SUP_RANDOM_SEED = 1;
  
  /**
   * Default value for {@link #SEPARATE_CONNECTED_COMPONENTS} with algorithm "ELK Force".
   */
  private final static boolean FORCE_SUP_SEPARATE_CONNECTED_COMPONENTS = true;
  
  public void apply(final ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.force.model",
        "",
        "Force Model",
        "Determines the model for force calculation.",
        MODEL_DEFAULT,
        LayoutOptionData.Type.ENUM,
        ForceModelStrategy.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.force.iterations",
        "",
        "Iterations",
        "The number of iterations on the force model.",
        ITERATIONS_DEFAULT,
        LayoutOptionData.Type.INT,
        int.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.force.repulsivePower",
        "",
        "Repulsive Power",
        "Determines how many bend points are added to the edge; such bend points are regarded as repelling particles in the force model",
        REPULSIVE_POWER_DEFAULT,
        LayoutOptionData.Type.INT,
        int.class,
        EnumSet.of(LayoutOptionData.Target.EDGES),
        LayoutOptionData.Visibility.VISIBLE
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.force.temperature",
        "",
        "FR Temperature",
        "The temperature is used as a scaling factor for particle displacements.",
        TEMPERATURE_DEFAULT,
        LayoutOptionData.Type.FLOAT,
        float.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
    ));
    registry.addDependency(
        "org.eclipse.elk.alg.force.temperature",
        "org.eclipse.elk.alg.force.model",
        TEMPERATURE_DEP_MODEL
    );
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.force.repulsion",
        "",
        "Eades Repulsion",
        "Factor for repulsive forces in Eades\' model.",
        REPULSION_DEFAULT,
        LayoutOptionData.Type.FLOAT,
        float.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
    ));
    registry.addDependency(
        "org.eclipse.elk.alg.force.repulsion",
        "org.eclipse.elk.alg.force.model",
        REPULSION_DEP_MODEL
    );
    registry.register(new LayoutAlgorithmData(
        "org.eclipse.elk.alg.force.force",
        "ELK Force",
        "Force-based algorithm provided by the Eclipse Layout Kernel. Implements methods that follow physical analogies by simulating forces that move the nodes into a balanced distribution. Currently the original Eades model and the Fruchterman - Reingold model are supported.",
        new AlgorithmFactory(ForceLayoutProvider.class, ""),
        "org.eclipse.elk.force",
        null,
        "images/force.png",
        EnumSet.of(GraphFeature.MULTI_EDGES, GraphFeature.EDGE_LABELS)
    ));
    registry.addOptionSupport(
        "org.eclipse.elk.alg.force.force",
        "org.eclipse.elk.priority",
        FORCE_SUP_PRIORITY
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.force.force",
        "org.eclipse.elk.spacing.node",
        FORCE_SUP_SPACING_NODE
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.force.force",
        "org.eclipse.elk.spacing.border",
        FORCE_SUP_SPACING_BORDER
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.force.force",
        "org.eclipse.elk.spacing.label",
        FORCE_SUP_SPACING_LABEL
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.force.force",
        "org.eclipse.elk.aspectRatio",
        FORCE_SUP_ASPECT_RATIO
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.force.force",
        "org.eclipse.elk.randomSeed",
        FORCE_SUP_RANDOM_SEED
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.force.force",
        "org.eclipse.elk.separateConnectedComponents",
        FORCE_SUP_SEPARATE_CONNECTED_COMPONENTS
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.force.force",
        "org.eclipse.elk.interactive",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.force.force",
        "org.eclipse.elk.alg.force.model",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.force.force",
        "org.eclipse.elk.alg.force.temperature",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.force.force",
        "org.eclipse.elk.alg.force.iterations",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.force.force",
        "org.eclipse.elk.alg.force.repulsion",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.force.force",
        "org.eclipse.elk.alg.force.repulsivePower",
        null
    );
  }
}
