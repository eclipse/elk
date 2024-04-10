/**
 * Copyright (c) 2017, 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.elk.alg.radial.options;

import java.util.EnumSet;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class RadialMetaDataProvider implements ILayoutMetaDataProvider {
  /**
   * Default value for {@link #CENTER_ON_ROOT}.
   */
  private static final boolean CENTER_ON_ROOT_DEFAULT = false;

  /**
   * Centers the layout on the root of the tree i.e. so that the central node is also the center node of the
   * final layout. This introduces additional whitespace.
   */
  public static final IProperty<Boolean> CENTER_ON_ROOT = new Property<Boolean>(
            "org.eclipse.elk.radial.centerOnRoot",
            CENTER_ON_ROOT_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #ORDER_ID}.
   */
  private static final int ORDER_ID_DEFAULT = 0;

  /**
   * The id can be used to define an order for nodes of one radius. This can be used to sort them in the
   * layer accordingly.
   */
  public static final IProperty<Integer> ORDER_ID = new Property<Integer>(
            "org.eclipse.elk.radial.orderId",
            ORDER_ID_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #RADIUS}.
   */
  private static final double RADIUS_DEFAULT = 0.0;

  /**
   * The radius option can be used to set the initial radius for the radial layouter.
   */
  public static final IProperty<Double> RADIUS = new Property<Double>(
            "org.eclipse.elk.radial.radius",
            RADIUS_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #ROTATE}.
   */
  private static final boolean ROTATE_DEFAULT = false;

  /**
   * The rotate option determines whether a rotation of the layout should be performed.
   */
  public static final IProperty<Boolean> ROTATE = new Property<Boolean>(
            "org.eclipse.elk.radial.rotate",
            ROTATE_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #COMPACTOR}.
   */
  private static final CompactionStrategy COMPACTOR_DEFAULT = CompactionStrategy.NONE;

  /**
   * With the compacter option it can be determined how compaction on the graph is done.
   * It can be chosen between none, the radial compaction or the compaction of wedges separately.
   */
  public static final IProperty<CompactionStrategy> COMPACTOR = new Property<CompactionStrategy>(
            "org.eclipse.elk.radial.compactor",
            COMPACTOR_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #COMPACTION_STEP_SIZE}.
   */
  private static final int COMPACTION_STEP_SIZE_DEFAULT = 1;

  /**
   * Lower bound value for {@link #COMPACTION_STEP_SIZE}.
   */
  private static final Comparable<? super Integer> COMPACTION_STEP_SIZE_LOWER_BOUND = Integer.valueOf(0);

  /**
   * Determine the size of steps with which the compaction is done.
   * Step size 1 correlates to a compaction of 1 pixel per Iteration.
   */
  public static final IProperty<Integer> COMPACTION_STEP_SIZE = new Property<Integer>(
            "org.eclipse.elk.radial.compactionStepSize",
            COMPACTION_STEP_SIZE_DEFAULT,
            COMPACTION_STEP_SIZE_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #SORTER}.
   */
  private static final SortingStrategy SORTER_DEFAULT = SortingStrategy.NONE;

  /**
   * Sort the nodes per radius according to the sorting algorithm. The strategies are none, by the given order id,
   * or sorting them by polar coordinates.
   */
  public static final IProperty<SortingStrategy> SORTER = new Property<SortingStrategy>(
            "org.eclipse.elk.radial.sorter",
            SORTER_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #WEDGE_CRITERIA}.
   */
  private static final AnnulusWedgeCriteria WEDGE_CRITERIA_DEFAULT = AnnulusWedgeCriteria.NODE_SIZE;

  /**
   * Determine how the wedge for the node placement is calculated.
   * It can be chosen between wedge determination by the number of leaves or by the maximum sum of diagonals.
   */
  public static final IProperty<AnnulusWedgeCriteria> WEDGE_CRITERIA = new Property<AnnulusWedgeCriteria>(
            "org.eclipse.elk.radial.wedgeCriteria",
            WEDGE_CRITERIA_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #OPTIMIZATION_CRITERIA}.
   */
  private static final RadialTranslationStrategy OPTIMIZATION_CRITERIA_DEFAULT = RadialTranslationStrategy.NONE;

  /**
   * Find the optimal translation of the nodes of the first radii according to this criteria.
   * For example edge crossings can be minimized.
   */
  public static final IProperty<RadialTranslationStrategy> OPTIMIZATION_CRITERIA = new Property<RadialTranslationStrategy>(
            "org.eclipse.elk.radial.optimizationCriteria",
            OPTIMIZATION_CRITERIA_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #ROTATION_TARGET_ANGLE}.
   */
  private static final double ROTATION_TARGET_ANGLE_DEFAULT = 0;

  /**
   * The angle in radians that the layout should be rotated to after layout.
   */
  public static final IProperty<Double> ROTATION_TARGET_ANGLE = new Property<Double>(
            "org.eclipse.elk.radial.rotation.targetAngle",
            ROTATION_TARGET_ANGLE_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #ROTATION_COMPUTE_ADDITIONAL_WEDGE_SPACE}.
   */
  private static final boolean ROTATION_COMPUTE_ADDITIONAL_WEDGE_SPACE_DEFAULT = false;

  /**
   * If set to true, modifies the target angle by rotating further such that space is left
   * for an edge to pass in between the nodes. This option should only be used in conjunction
   * with top-down layout.
   */
  public static final IProperty<Boolean> ROTATION_COMPUTE_ADDITIONAL_WEDGE_SPACE = new Property<Boolean>(
            "org.eclipse.elk.radial.rotation.computeAdditionalWedgeSpace",
            ROTATION_COMPUTE_ADDITIONAL_WEDGE_SPACE_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #ROTATION_OUTGOING_EDGE_ANGLES}.
   */
  private static final boolean ROTATION_OUTGOING_EDGE_ANGLES_DEFAULT = false;

  /**
   * Calculate the required angle of connected nodes to leave space for an incoming edge. This
   * option should only be used in conjunction with top-down layout.
   */
  public static final IProperty<Boolean> ROTATION_OUTGOING_EDGE_ANGLES = new Property<Boolean>(
            "org.eclipse.elk.radial.rotation.outgoingEdgeAngles",
            ROTATION_OUTGOING_EDGE_ANGLES_DEFAULT,
            null,
            null);

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.radial.centerOnRoot")
        .group("")
        .name("Center On Root")
        .description("Centers the layout on the root of the tree i.e. so that the central node is also the center node of the final layout. This introduces additional whitespace.")
        .defaultValue(CENTER_ON_ROOT_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.radial.orderId")
        .group("")
        .name("Order ID")
        .description("The id can be used to define an order for nodes of one radius. This can be used to sort them in the layer accordingly.")
        .defaultValue(ORDER_ID_DEFAULT)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.radial.radius")
        .group("")
        .name("Radius")
        .description("The radius option can be used to set the initial radius for the radial layouter.")
        .defaultValue(RADIUS_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.radial.rotate")
        .group("")
        .name("Rotate")
        .description("The rotate option determines whether a rotation of the layout should be performed.")
        .defaultValue(ROTATE_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.radial.compactor")
        .group("")
        .name("Compaction")
        .description("With the compacter option it can be determined how compaction on the graph is done. It can be chosen between none, the radial compaction or the compaction of wedges separately.")
        .defaultValue(COMPACTOR_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(CompactionStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.radial.compactionStepSize")
        .group("")
        .name("Compaction Step Size")
        .description("Determine the size of steps with which the compaction is done. Step size 1 correlates to a compaction of 1 pixel per Iteration.")
        .defaultValue(COMPACTION_STEP_SIZE_DEFAULT)
        .lowerBound(COMPACTION_STEP_SIZE_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.radial.compactionStepSize",
        "org.eclipse.elk.radial.compactor",
        null
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.radial.sorter")
        .group("")
        .name("Sorter")
        .description("Sort the nodes per radius according to the sorting algorithm. The strategies are none, by the given order id, or sorting them by polar coordinates.")
        .defaultValue(SORTER_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(SortingStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.radial.wedgeCriteria")
        .group("")
        .name("Annulus Wedge Criteria")
        .description("Determine how the wedge for the node placement is calculated. It can be chosen between wedge determination by the number of leaves or by the maximum sum of diagonals.")
        .defaultValue(WEDGE_CRITERIA_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(AnnulusWedgeCriteria.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.radial.optimizationCriteria")
        .group("")
        .name("Translation Optimization")
        .description("Find the optimal translation of the nodes of the first radii according to this criteria. For example edge crossings can be minimized.")
        .defaultValue(OPTIMIZATION_CRITERIA_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(RadialTranslationStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.radial.rotation.targetAngle")
        .group("rotation")
        .name("Target Angle")
        .description("The angle in radians that the layout should be rotated to after layout.")
        .defaultValue(ROTATION_TARGET_ANGLE_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.radial.rotation.targetAngle",
        "org.eclipse.elk.radial.rotate",
        null
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.radial.rotation.computeAdditionalWedgeSpace")
        .group("rotation")
        .name("Additional Wedge Space")
        .description("If set to true, modifies the target angle by rotating further such that space is left for an edge to pass in between the nodes. This option should only be used in conjunction with top-down layout.")
        .defaultValue(ROTATION_COMPUTE_ADDITIONAL_WEDGE_SPACE_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.radial.rotation.computeAdditionalWedgeSpace",
        "org.eclipse.elk.radial.rotate",
        null
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.radial.rotation.outgoingEdgeAngles")
        .group("rotation")
        .name("Outgoing Edge Angles")
        .description("Calculate the required angle of connected nodes to leave space for an incoming edge. This option should only be used in conjunction with top-down layout.")
        .defaultValue(ROTATION_OUTGOING_EDGE_ANGLES_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    new org.eclipse.elk.alg.radial.options.RadialOptions().apply(registry);
  }
}
