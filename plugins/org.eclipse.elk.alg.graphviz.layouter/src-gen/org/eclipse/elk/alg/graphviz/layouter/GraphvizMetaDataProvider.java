/**
 * Copyright (c) 2015 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.elk.alg.graphviz.layouter;

import java.util.EnumSet;
import org.eclipse.elk.alg.graphviz.dot.transform.NeatoModel;
import org.eclipse.elk.alg.graphviz.dot.transform.OverlapMode;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.core.util.ExclusiveBounds;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class GraphvizMetaDataProvider implements ILayoutMetaDataProvider {
  /**
   * Default value for {@link #ADAPT_PORT_POSITIONS}.
   */
  private static final boolean ADAPT_PORT_POSITIONS_DEFAULT = true;

  /**
   * Whether ports should be moved to the point where edges cross the node's bounds.
   */
  public static final IProperty<Boolean> ADAPT_PORT_POSITIONS = new Property<Boolean>(
            "org.eclipse.elk.graphviz.adaptPortPositions",
            ADAPT_PORT_POSITIONS_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #CONCENTRATE}.
   */
  private static final boolean CONCENTRATE_DEFAULT = false;

  /**
   * Merges multiedges into a single edge and causes partially parallel edges to share part of
   * their paths.
   */
  public static final IProperty<Boolean> CONCENTRATE = new Property<Boolean>(
            "org.eclipse.elk.graphviz.concentrate",
            CONCENTRATE_DEFAULT,
            null,
            null);

  /**
   * Lower bound value for {@link #EPSILON}.
   */
  private static final Comparable<? super Double> EPSILON_LOWER_BOUND = ExclusiveBounds.greaterThan(0);

  /**
   * Terminating condition. If the length squared of all energy gradients are less than
   * epsilon, the algorithm stops.
   */
  public static final IProperty<Double> EPSILON = new Property<Double>(
            "org.eclipse.elk.graphviz.epsilon",
            null,
            EPSILON_LOWER_BOUND,
            null);

  /**
   * Lower bound value for {@link #ITERATIONS_FACTOR}.
   */
  private static final Comparable<? super Double> ITERATIONS_FACTOR_LOWER_BOUND = ExclusiveBounds.greaterThan(0);

  /**
   * Multiplicative scale factor for the maximal number of iterations used during crossing
   * minimization, node ranking, and node positioning.
   */
  public static final IProperty<Double> ITERATIONS_FACTOR = new Property<Double>(
            "org.eclipse.elk.graphviz.iterationsFactor",
            null,
            ITERATIONS_FACTOR_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #LABEL_ANGLE}.
   */
  private static final double LABEL_ANGLE_DEFAULT = (-25);

  /**
   * Angle between head / tail positioned edge labels and the corresponding edge.
   */
  public static final IProperty<Double> LABEL_ANGLE = new Property<Double>(
            "org.eclipse.elk.graphviz.labelAngle",
            LABEL_ANGLE_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #LABEL_DISTANCE}.
   */
  private static final double LABEL_DISTANCE_DEFAULT = 1;

  /**
   * Lower bound value for {@link #LABEL_DISTANCE}.
   */
  private static final Comparable<? super Double> LABEL_DISTANCE_LOWER_BOUND = Double.valueOf(0.0);

  /**
   * Distance of head / tail positioned edge labels to the source or target node.
   */
  public static final IProperty<Double> LABEL_DISTANCE = new Property<Double>(
            "org.eclipse.elk.graphviz.labelDistance",
            LABEL_DISTANCE_DEFAULT,
            LABEL_DISTANCE_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #LAYER_SPACING_FACTOR}.
   */
  private static final double LAYER_SPACING_FACTOR_DEFAULT = 1;

  /**
   * Lower bound value for {@link #LAYER_SPACING_FACTOR}.
   */
  private static final Comparable<? super Double> LAYER_SPACING_FACTOR_LOWER_BOUND = ExclusiveBounds.greaterThan(0);

  /**
   * Factor for the spacing of different layers (ranks).
   */
  public static final IProperty<Double> LAYER_SPACING_FACTOR = new Property<Double>(
            "org.eclipse.elk.graphviz.layerSpacingFactor",
            LAYER_SPACING_FACTOR_DEFAULT,
            LAYER_SPACING_FACTOR_LOWER_BOUND,
            null);

  /**
   * Lower bound value for {@link #MAXITER}.
   */
  private static final Comparable<? super Integer> MAXITER_LOWER_BOUND = Integer.valueOf(1);

  /**
   * The maximum number of iterations.
   */
  public static final IProperty<Integer> MAXITER = new Property<Integer>(
            "org.eclipse.elk.graphviz.maxiter",
            null,
            MAXITER_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #NEATO_MODEL}.
   */
  private static final NeatoModel NEATO_MODEL_DEFAULT = NeatoModel.SHORTPATH;

  /**
   * Specifies how the distance matrix is computed for the input graph.
   */
  public static final IProperty<NeatoModel> NEATO_MODEL = new Property<NeatoModel>(
            "org.eclipse.elk.graphviz.neatoModel",
            NEATO_MODEL_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #OVERLAP_MODE}.
   */
  private static final OverlapMode OVERLAP_MODE_DEFAULT = OverlapMode.PRISM;

  /**
   * Determines if and how node overlaps should be removed.
   */
  public static final IProperty<OverlapMode> OVERLAP_MODE = new Property<OverlapMode>(
            "org.eclipse.elk.graphviz.overlapMode",
            OVERLAP_MODE_DEFAULT,
            null,
            null);

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.graphviz.adaptPortPositions")
        .group("")
        .name("Adapt Port Positions")
        .description("Whether ports should be moved to the point where edges cross the node\'s bounds.")
        .defaultValue(ADAPT_PORT_POSITIONS_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.graphviz.concentrate")
        .group("")
        .name("Concentrate Edges")
        .description("Merges multiedges into a single edge and causes partially parallel edges to share part of their paths.")
        .defaultValue(CONCENTRATE_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.graphviz.epsilon")
        .group("")
        .name("Epsilon")
        .description("Terminating condition. If the length squared of all energy gradients are less than epsilon, the algorithm stops.")
        .lowerBound(EPSILON_LOWER_BOUND)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.graphviz.iterationsFactor")
        .group("")
        .name("Iterations Factor")
        .description("Multiplicative scale factor for the maximal number of iterations used during crossing minimization, node ranking, and node positioning.")
        .lowerBound(ITERATIONS_FACTOR_LOWER_BOUND)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.graphviz.labelAngle")
        .group("")
        .name("Label Angle")
        .description("Angle between head / tail positioned edge labels and the corresponding edge.")
        .defaultValue(LABEL_ANGLE_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.EDGES))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.graphviz.labelDistance")
        .group("")
        .name("Label Distance")
        .description("Distance of head / tail positioned edge labels to the source or target node.")
        .defaultValue(LABEL_DISTANCE_DEFAULT)
        .lowerBound(LABEL_DISTANCE_LOWER_BOUND)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.EDGES))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.graphviz.layerSpacingFactor")
        .group("")
        .name("Layer Spacing Factor")
        .description("Factor for the spacing of different layers (ranks).")
        .defaultValue(LAYER_SPACING_FACTOR_DEFAULT)
        .lowerBound(LAYER_SPACING_FACTOR_LOWER_BOUND)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.graphviz.maxiter")
        .group("")
        .name("Max. Iterations")
        .description("The maximum number of iterations.")
        .lowerBound(MAXITER_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.graphviz.neatoModel")
        .group("")
        .name("Distance Model")
        .description("Specifies how the distance matrix is computed for the input graph.")
        .defaultValue(NEATO_MODEL_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(NeatoModel.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.graphviz.overlapMode")
        .group("")
        .name("Overlap Removal")
        .description("Determines if and how node overlaps should be removed.")
        .defaultValue(OVERLAP_MODE_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(OverlapMode.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    new org.eclipse.elk.alg.graphviz.layouter.DotOptions().apply(registry);
    new org.eclipse.elk.alg.graphviz.layouter.NeatoOptions().apply(registry);
    new org.eclipse.elk.alg.graphviz.layouter.FdpOptions().apply(registry);
    new org.eclipse.elk.alg.graphviz.layouter.TwopiOptions().apply(registry);
    new org.eclipse.elk.alg.graphviz.layouter.CircoOptions().apply(registry);
  }
}
