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
import org.eclipse.elk.alg.force.model.ForceModelStrategy;
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
  private final static ForceModelStrategy MODEL_DEFAULT = ForceModelStrategy.FRUCHTERMAN_REINGOLD;
  
  /**
   * Determines the model for force calculation.
   */
  public final static IProperty<ForceModelStrategy> MODEL = new Property<ForceModelStrategy>(
            "org.eclipse.elk.force.model",
            MODEL_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #ITERATIONS}.
   */
  private final static int ITERATIONS_DEFAULT = 300;
  
  /**
   * Lower bound value for {@link #ITERATIONS}.
   */
  private final static Comparable<? super Integer> ITERATIONS_LOWER_BOUND = Integer.valueOf(1);
  
  /**
   * The number of iterations on the force model.
   */
  public final static IProperty<Integer> ITERATIONS = new Property<Integer>(
            "org.eclipse.elk.force.iterations",
            ITERATIONS_DEFAULT,
            ITERATIONS_LOWER_BOUND,
            null);
  
  /**
   * Default value for {@link #REPULSIVE_POWER}.
   */
  private final static int REPULSIVE_POWER_DEFAULT = 0;
  
  /**
   * Lower bound value for {@link #REPULSIVE_POWER}.
   */
  private final static Comparable<? super Integer> REPULSIVE_POWER_LOWER_BOUND = Integer.valueOf(0);
  
  /**
   * Determines how many bend points are added to the edge; such bend points are regarded as
   * repelling particles in the force model
   */
  public final static IProperty<Integer> REPULSIVE_POWER = new Property<Integer>(
            "org.eclipse.elk.force.repulsivePower",
            REPULSIVE_POWER_DEFAULT,
            REPULSIVE_POWER_LOWER_BOUND,
            null);
  
  /**
   * Default value for {@link #TEMPERATURE}.
   */
  private final static float TEMPERATURE_DEFAULT = 0.001f;
  
  /**
   * Lower bound value for {@link #TEMPERATURE}.
   */
  private final static Comparable<? super Float> TEMPERATURE_LOWER_BOUND = ExclusiveBounds.greaterThan(0);
  
  /**
   * The temperature is used as a scaling factor for particle displacements.
   */
  public final static IProperty<Float> TEMPERATURE = new Property<Float>(
            "org.eclipse.elk.force.temperature",
            TEMPERATURE_DEFAULT,
            TEMPERATURE_LOWER_BOUND,
            null);
  
  /**
   * Default value for {@link #REPULSION}.
   */
  private final static float REPULSION_DEFAULT = 5.0f;
  
  /**
   * Lower bound value for {@link #REPULSION}.
   */
  private final static Comparable<? super Float> REPULSION_LOWER_BOUND = ExclusiveBounds.greaterThan(0);
  
  /**
   * Factor for repulsive forces in Eades' model.
   */
  public final static IProperty<Float> REPULSION = new Property<Float>(
            "org.eclipse.elk.force.repulsion",
            REPULSION_DEFAULT,
            REPULSION_LOWER_BOUND,
            null);
  
  /**
   * Required value for dependency between {@link #TEMPERATURE} and {@link #MODEL}.
   */
  private final static ForceModelStrategy TEMPERATURE_DEP_MODEL = ForceModelStrategy.FRUCHTERMAN_REINGOLD;
  
  /**
   * Required value for dependency between {@link #REPULSION} and {@link #MODEL}.
   */
  private final static ForceModelStrategy REPULSION_DEP_MODEL = ForceModelStrategy.EADES;
  
  public void apply(final ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.force.model",
        "",
        "Force Model",
        "Determines the model for force calculation.",
        MODEL_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.ENUM,
        ForceModelStrategy.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.force.iterations",
        "",
        "Iterations",
        "The number of iterations on the force model.",
        ITERATIONS_DEFAULT,
        ITERATIONS_LOWER_BOUND,
        null,
        LayoutOptionData.Type.INT,
        Integer.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.force.repulsivePower",
        "",
        "Repulsive Power",
        "Determines how many bend points are added to the edge; such bend points are regarded as repelling particles in the force model",
        REPULSIVE_POWER_DEFAULT,
        REPULSIVE_POWER_LOWER_BOUND,
        null,
        LayoutOptionData.Type.INT,
        Integer.class,
        EnumSet.of(LayoutOptionData.Target.EDGES),
        LayoutOptionData.Visibility.VISIBLE
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.force.temperature",
        "",
        "FR Temperature",
        "The temperature is used as a scaling factor for particle displacements.",
        TEMPERATURE_DEFAULT,
        TEMPERATURE_LOWER_BOUND,
        null,
        LayoutOptionData.Type.FLOAT,
        Float.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
    ));
    registry.addDependency(
        "org.eclipse.elk.force.temperature",
        "org.eclipse.elk.force.model",
        TEMPERATURE_DEP_MODEL
    );
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.force.repulsion",
        "",
        "Eades Repulsion",
        "Factor for repulsive forces in Eades\' model.",
        REPULSION_DEFAULT,
        REPULSION_LOWER_BOUND,
        null,
        LayoutOptionData.Type.FLOAT,
        Float.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
    ));
    registry.addDependency(
        "org.eclipse.elk.force.repulsion",
        "org.eclipse.elk.force.model",
        REPULSION_DEP_MODEL
    );
    new org.eclipse.elk.alg.force.properties.ForceOptions().apply(registry);
  }
}
