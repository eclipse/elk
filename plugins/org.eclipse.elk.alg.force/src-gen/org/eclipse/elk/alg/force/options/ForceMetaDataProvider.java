/**
 * Copyright (c) 2015, 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.elk.alg.force.options;

import java.util.EnumSet;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.core.util.ExclusiveBounds;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * Declarations for the ELK Force layout algorithm.
 */
@SuppressWarnings("all")
public class ForceMetaDataProvider implements ILayoutMetaDataProvider {
  /**
   * Default value for {@link #MODEL}.
   */
  private static final ForceModelStrategy MODEL_DEFAULT = ForceModelStrategy.FRUCHTERMAN_REINGOLD;

  /**
   * Determines the model for force calculation.
   */
  public static final IProperty<ForceModelStrategy> MODEL = new Property<ForceModelStrategy>(
            "org.eclipse.elk.force.model",
            MODEL_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #ITERATIONS}.
   */
  private static final int ITERATIONS_DEFAULT = 300;

  /**
   * Lower bound value for {@link #ITERATIONS}.
   */
  private static final Comparable<? super Integer> ITERATIONS_LOWER_BOUND = Integer.valueOf(1);

  /**
   * The number of iterations on the force model.
   */
  public static final IProperty<Integer> ITERATIONS = new Property<Integer>(
            "org.eclipse.elk.force.iterations",
            ITERATIONS_DEFAULT,
            ITERATIONS_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #REPULSIVE_POWER}.
   */
  private static final int REPULSIVE_POWER_DEFAULT = 0;

  /**
   * Lower bound value for {@link #REPULSIVE_POWER}.
   */
  private static final Comparable<? super Integer> REPULSIVE_POWER_LOWER_BOUND = Integer.valueOf(0);

  /**
   * Determines how many bend points are added to the edge; such bend points are regarded as
   * repelling particles in the force model
   */
  public static final IProperty<Integer> REPULSIVE_POWER = new Property<Integer>(
            "org.eclipse.elk.force.repulsivePower",
            REPULSIVE_POWER_DEFAULT,
            REPULSIVE_POWER_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #TEMPERATURE}.
   */
  private static final double TEMPERATURE_DEFAULT = 0.001;

  /**
   * Lower bound value for {@link #TEMPERATURE}.
   */
  private static final Comparable<? super Double> TEMPERATURE_LOWER_BOUND = ExclusiveBounds.greaterThan(0);

  /**
   * The temperature is used as a scaling factor for particle displacements.
   */
  public static final IProperty<Double> TEMPERATURE = new Property<Double>(
            "org.eclipse.elk.force.temperature",
            TEMPERATURE_DEFAULT,
            TEMPERATURE_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #REPULSION}.
   */
  private static final double REPULSION_DEFAULT = 5.0;

  /**
   * Lower bound value for {@link #REPULSION}.
   */
  private static final Comparable<? super Double> REPULSION_LOWER_BOUND = ExclusiveBounds.greaterThan(0);

  /**
   * Factor for repulsive forces in Eades' model.
   */
  public static final IProperty<Double> REPULSION = new Property<Double>(
            "org.eclipse.elk.force.repulsion",
            REPULSION_DEFAULT,
            REPULSION_LOWER_BOUND,
            null);

  /**
   * Required value for dependency between {@link #TEMPERATURE} and {@link #MODEL}.
   */
  private static final ForceModelStrategy TEMPERATURE_DEP_MODEL_0 = ForceModelStrategy.FRUCHTERMAN_REINGOLD;

  /**
   * Required value for dependency between {@link #REPULSION} and {@link #MODEL}.
   */
  private static final ForceModelStrategy REPULSION_DEP_MODEL_0 = ForceModelStrategy.EADES;

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.force.model")
        .group("")
        .name("Force Model")
        .description("Determines the model for force calculation.")
        .defaultValue(MODEL_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(ForceModelStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.force.iterations")
        .group("")
        .name("Iterations")
        .description("The number of iterations on the force model.")
        .defaultValue(ITERATIONS_DEFAULT)
        .lowerBound(ITERATIONS_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.force.repulsivePower")
        .group("")
        .name("Repulsive Power")
        .description("Determines how many bend points are added to the edge; such bend points are regarded as repelling particles in the force model")
        .defaultValue(REPULSIVE_POWER_DEFAULT)
        .lowerBound(REPULSIVE_POWER_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.EDGES))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.force.temperature")
        .group("")
        .name("FR Temperature")
        .description("The temperature is used as a scaling factor for particle displacements.")
        .defaultValue(TEMPERATURE_DEFAULT)
        .lowerBound(TEMPERATURE_LOWER_BOUND)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.force.temperature",
        "org.eclipse.elk.force.model",
        TEMPERATURE_DEP_MODEL_0
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.force.repulsion")
        .group("")
        .name("Eades Repulsion")
        .description("Factor for repulsive forces in Eades\' model.")
        .defaultValue(REPULSION_DEFAULT)
        .lowerBound(REPULSION_LOWER_BOUND)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.force.repulsion",
        "org.eclipse.elk.force.model",
        REPULSION_DEP_MODEL_0
    );
    new org.eclipse.elk.alg.force.options.ForceOptions().apply(registry);
  }
}
