/**
 * Copyright (c) 2016, 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.elk.alg.force.options;

import java.util.EnumSet;
import org.eclipse.elk.alg.force.stress.StressMajorization;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * Declarations for the ELK Stress layout algorithm.
 */
@SuppressWarnings("all")
public class StressMetaDataProvider implements ILayoutMetaDataProvider {
  /**
   * Default value for {@link #FIXED}.
   */
  private static final boolean FIXED_DEFAULT = false;

  /**
   * Prevent that the node is moved by the layout algorithm.
   */
  public static final IProperty<Boolean> FIXED = new Property<Boolean>(
            "org.eclipse.elk.stress.fixed",
            FIXED_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #DESIRED_EDGE_LENGTH}.
   */
  private static final double DESIRED_EDGE_LENGTH_DEFAULT = 100.0;

  /**
   * Either specified for parent nodes or for individual edges,
   * where the latter takes higher precedence.
   */
  public static final IProperty<Double> DESIRED_EDGE_LENGTH = new Property<Double>(
            "org.eclipse.elk.stress.desiredEdgeLength",
            DESIRED_EDGE_LENGTH_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #DIMENSION}.
   */
  private static final StressMajorization.Dimension DIMENSION_DEFAULT = StressMajorization.Dimension.XY;

  /**
   * Dimensions that are permitted to be altered during layout.
   */
  public static final IProperty<StressMajorization.Dimension> DIMENSION = new Property<StressMajorization.Dimension>(
            "org.eclipse.elk.stress.dimension",
            DIMENSION_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #EPSILON}.
   */
  private static final double EPSILON_DEFAULT = 10e-4;

  /**
   * Termination criterion for the iterative process.
   */
  public static final IProperty<Double> EPSILON = new Property<Double>(
            "org.eclipse.elk.stress.epsilon",
            EPSILON_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #ITERATION_LIMIT}.
   */
  private static final int ITERATION_LIMIT_DEFAULT = Integer.MAX_VALUE;

  /**
   * Maximum number of performed iterations. Takes higher
   * precedence than 'epsilon'.
   */
  public static final IProperty<Integer> ITERATION_LIMIT = new Property<Integer>(
            "org.eclipse.elk.stress.iterationLimit",
            ITERATION_LIMIT_DEFAULT,
            null,
            null);

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.stress.fixed")
        .group("")
        .name("Fixed Position")
        .description("Prevent that the node is moved by the layout algorithm.")
        .defaultValue(FIXED_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.stress.desiredEdgeLength")
        .group("")
        .name("Desired Edge Length")
        .description("Either specified for parent nodes or for individual edges, where the latter takes higher precedence.")
        .defaultValue(DESIRED_EDGE_LENGTH_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS, LayoutOptionData.Target.EDGES))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.stress.dimension")
        .group("")
        .name("Layout Dimension")
        .description("Dimensions that are permitted to be altered during layout.")
        .defaultValue(DIMENSION_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(StressMajorization.Dimension.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.stress.epsilon")
        .group("")
        .name("Stress Epsilon")
        .description("Termination criterion for the iterative process.")
        .defaultValue(EPSILON_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.stress.iterationLimit")
        .group("")
        .name("Iteration Limit")
        .description("Maximum number of performed iterations. Takes higher precedence than \'epsilon\'.")
        .defaultValue(ITERATION_LIMIT_DEFAULT)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    new org.eclipse.elk.alg.force.options.StressOptions().apply(registry);
  }
}
