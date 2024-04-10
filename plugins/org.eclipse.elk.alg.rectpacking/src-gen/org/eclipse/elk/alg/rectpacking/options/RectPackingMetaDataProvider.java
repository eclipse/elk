/**
 * Copyright (c) 2018, 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.elk.alg.rectpacking.options;

import java.util.EnumSet;
import org.eclipse.elk.alg.rectpacking.p1widthapproximation.WidthApproximationStrategy;
import org.eclipse.elk.alg.rectpacking.p2packing.PackingStrategy;
import org.eclipse.elk.alg.rectpacking.p3whitespaceelimination.WhiteSpaceEliminationStrategy;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class RectPackingMetaDataProvider implements ILayoutMetaDataProvider {
  /**
   * Default value for {@link #TRYBOX}.
   */
  private static final boolean TRYBOX_DEFAULT = false;

  /**
   * Whether one should check whether the regions are stackable to see whether box layout would do the job.
   * For example, nodes with the same height are not stackable inside a row. Therefore, box layout will perform
   * better and faster.
   */
  public static final IProperty<Boolean> TRYBOX = new Property<Boolean>(
            "org.eclipse.elk.rectpacking.trybox",
            TRYBOX_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #CURRENT_POSITION}.
   */
  private static final int CURRENT_POSITION_DEFAULT = (-1);

  /**
   * Lower bound value for {@link #CURRENT_POSITION}.
   */
  private static final Comparable<? super Integer> CURRENT_POSITION_LOWER_BOUND = Integer.valueOf((-1));

  /**
   * The rectangles are ordered. Normally according to their definition the the model.
   * This option specifies the current position of a node.
   */
  public static final IProperty<Integer> CURRENT_POSITION = new Property<Integer>(
            "org.eclipse.elk.rectpacking.currentPosition",
            CURRENT_POSITION_DEFAULT,
            CURRENT_POSITION_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #DESIRED_POSITION}.
   */
  private static final int DESIRED_POSITION_DEFAULT = (-1);

  /**
   * Lower bound value for {@link #DESIRED_POSITION}.
   */
  private static final Comparable<? super Integer> DESIRED_POSITION_LOWER_BOUND = Integer.valueOf((-1));

  /**
   * The rectangles are ordered. Normally according to their definition the the model.
   * This option allows to specify a desired position that has preference over the original position.
   */
  public static final IProperty<Integer> DESIRED_POSITION = new Property<Integer>(
            "org.eclipse.elk.rectpacking.desiredPosition",
            DESIRED_POSITION_DEFAULT,
            DESIRED_POSITION_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #IN_NEW_ROW}.
   */
  private static final boolean IN_NEW_ROW_DEFAULT = false;

  /**
   * If set to true this node begins in a new row. Consequently this node cannot be moved in a previous layer during
   * compaction. Width approximation does does not take this into account.
   */
  public static final IProperty<Boolean> IN_NEW_ROW = new Property<Boolean>(
            "org.eclipse.elk.rectpacking.inNewRow",
            IN_NEW_ROW_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #WIDTH_APPROXIMATION_STRATEGY}.
   */
  private static final WidthApproximationStrategy WIDTH_APPROXIMATION_STRATEGY_DEFAULT = WidthApproximationStrategy.GREEDY;

  /**
   * Strategy for finding an initial width of the drawing.
   */
  public static final IProperty<WidthApproximationStrategy> WIDTH_APPROXIMATION_STRATEGY = new Property<WidthApproximationStrategy>(
            "org.eclipse.elk.rectpacking.widthApproximation.strategy",
            WIDTH_APPROXIMATION_STRATEGY_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #WIDTH_APPROXIMATION_TARGET_WIDTH}.
   */
  private static final double WIDTH_APPROXIMATION_TARGET_WIDTH_DEFAULT = (-1);

  /**
   * Option to place the rectangles in the given target width instead of approximating the width using the desired
   * aspect ratio.
   * The padding is not included in this. Meaning a drawing will have width of targetwidth +
   * horizontal padding.
   */
  public static final IProperty<Double> WIDTH_APPROXIMATION_TARGET_WIDTH = new Property<Double>(
            "org.eclipse.elk.rectpacking.widthApproximation.targetWidth",
            WIDTH_APPROXIMATION_TARGET_WIDTH_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #WIDTH_APPROXIMATION_OPTIMIZATION_GOAL}.
   */
  private static final OptimizationGoal WIDTH_APPROXIMATION_OPTIMIZATION_GOAL_DEFAULT = OptimizationGoal.MAX_SCALE_DRIVEN;

  /**
   * Optimization goal for approximation of the bounding box given by the first iteration. Determines whether layout is
   * sorted by the maximum scaling, aspect ratio, or area. Depending on the strategy the aspect ratio might be nearly ignored.
   */
  public static final IProperty<OptimizationGoal> WIDTH_APPROXIMATION_OPTIMIZATION_GOAL = new Property<OptimizationGoal>(
            "org.eclipse.elk.rectpacking.widthApproximation.optimizationGoal",
            WIDTH_APPROXIMATION_OPTIMIZATION_GOAL_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #WIDTH_APPROXIMATION_LAST_PLACE_SHIFT}.
   */
  private static final boolean WIDTH_APPROXIMATION_LAST_PLACE_SHIFT_DEFAULT = true;

  /**
   * When placing a rectangle behind or below the last placed rectangle in the first iteration, it is sometimes
   * possible to shift the rectangle further to the left or right, resulting in less whitespace. True (default)
   * enables the shift and false disables it. Disabling the shift produces a greater approximated area by the first
   * iteration and a layout, when using ONLY the first iteration (default not the case), where it is sometimes
   * impossible to implement a size transformation of rectangles that will fill the bounding box and eliminate
   * empty spaces.
   */
  public static final IProperty<Boolean> WIDTH_APPROXIMATION_LAST_PLACE_SHIFT = new Property<Boolean>(
            "org.eclipse.elk.rectpacking.widthApproximation.lastPlaceShift",
            WIDTH_APPROXIMATION_LAST_PLACE_SHIFT_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #PACKING_STRATEGY}.
   */
  private static final PackingStrategy PACKING_STRATEGY_DEFAULT = PackingStrategy.COMPACTION;

  /**
   * Strategy for finding an initial placement on nodes.
   */
  public static final IProperty<PackingStrategy> PACKING_STRATEGY = new Property<PackingStrategy>(
            "org.eclipse.elk.rectpacking.packing.strategy",
            PACKING_STRATEGY_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #PACKING_COMPACTION_ROW_HEIGHT_REEVALUATION}.
   */
  private static final boolean PACKING_COMPACTION_ROW_HEIGHT_REEVALUATION_DEFAULT = false;

  /**
   * During the compaction step the height of a row is normally not changed.
   * If this options is set, the blocks of other rows might be added if they exceed the row height.
   * If this is the case the whole row has to be packed again to be optimal regarding the new row height.
   * This option should, therefore, be used with care since it might be computation heavy.
   */
  public static final IProperty<Boolean> PACKING_COMPACTION_ROW_HEIGHT_REEVALUATION = new Property<Boolean>(
            "org.eclipse.elk.rectpacking.packing.compaction.rowHeightReevaluation",
            PACKING_COMPACTION_ROW_HEIGHT_REEVALUATION_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #PACKING_COMPACTION_ITERATIONS}.
   */
  private static final int PACKING_COMPACTION_ITERATIONS_DEFAULT = 1;

  /**
   * Lower bound value for {@link #PACKING_COMPACTION_ITERATIONS}.
   */
  private static final Comparable<? super Integer> PACKING_COMPACTION_ITERATIONS_LOWER_BOUND = Integer.valueOf(1);

  /**
   * Defines the number of compaction iterations. E.g. if set to 2 the width is initially approximated,
   * then the drawing is compacted and based on the resulting drawing the target width is decreased or
   * increased and a second compaction step is executed and the result compared to the first one. The best
   * run is used based on the scale measure.
   */
  public static final IProperty<Integer> PACKING_COMPACTION_ITERATIONS = new Property<Integer>(
            "org.eclipse.elk.rectpacking.packing.compaction.iterations",
            PACKING_COMPACTION_ITERATIONS_DEFAULT,
            PACKING_COMPACTION_ITERATIONS_LOWER_BOUND,
            null);

  /**
   * Strategy for expanding nodes such that whitespace in the parent is eliminated.
   */
  public static final IProperty<WhiteSpaceEliminationStrategy> WHITE_SPACE_ELIMINATION_STRATEGY = new Property<WhiteSpaceEliminationStrategy>(
            "org.eclipse.elk.rectpacking.whiteSpaceElimination.strategy");

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.rectpacking.trybox")
        .group("")
        .name("Try box layout first")
        .description("Whether one should check whether the regions are stackable to see whether box layout would do the job. For example, nodes with the same height are not stackable inside a row. Therefore, box layout will perform better and faster.")
        .defaultValue(TRYBOX_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.rectpacking.currentPosition")
        .group("")
        .name("Current position of a node in the order of nodes")
        .description("The rectangles are ordered. Normally according to their definition the the model. This option specifies the current position of a node.")
        .defaultValue(CURRENT_POSITION_DEFAULT)
        .lowerBound(CURRENT_POSITION_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.rectpacking.desiredPosition")
        .group("")
        .name("Desired index of node")
        .description("The rectangles are ordered. Normally according to their definition the the model. This option allows to specify a desired position that has preference over the original position.")
        .defaultValue(DESIRED_POSITION_DEFAULT)
        .lowerBound(DESIRED_POSITION_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.rectpacking.inNewRow")
        .group("")
        .name("In new Row")
        .description("If set to true this node begins in a new row. Consequently this node cannot be moved in a previous layer during compaction. Width approximation does does not take this into account.")
        .defaultValue(IN_NEW_ROW_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.rectpacking.widthApproximation.strategy")
        .group("widthApproximation")
        .name("Width Approximation Strategy")
        .description("Strategy for finding an initial width of the drawing.")
        .defaultValue(WIDTH_APPROXIMATION_STRATEGY_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(WidthApproximationStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.rectpacking.widthApproximation.targetWidth")
        .group("widthApproximation")
        .name("Target Width")
        .description("Option to place the rectangles in the given target width instead of approximating the width using the desired aspect ratio. The padding is not included in this. Meaning a drawing will have width of targetwidth + horizontal padding.")
        .defaultValue(WIDTH_APPROXIMATION_TARGET_WIDTH_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.rectpacking.widthApproximation.optimizationGoal")
        .group("widthApproximation")
        .name("Optimization Goal")
        .description("Optimization goal for approximation of the bounding box given by the first iteration. Determines whether layout is sorted by the maximum scaling, aspect ratio, or area. Depending on the strategy the aspect ratio might be nearly ignored.")
        .defaultValue(WIDTH_APPROXIMATION_OPTIMIZATION_GOAL_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(OptimizationGoal.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.rectpacking.widthApproximation.lastPlaceShift")
        .group("widthApproximation")
        .name("Shift Last Placed.")
        .description("When placing a rectangle behind or below the last placed rectangle in the first iteration, it is sometimes possible to shift the rectangle further to the left or right, resulting in less whitespace. True (default) enables the shift and false disables it. Disabling the shift produces a greater approximated area by the first iteration and a layout, when using ONLY the first iteration (default not the case), where it is sometimes impossible to implement a size transformation of rectangles that will fill the bounding box and eliminate empty spaces.")
        .defaultValue(WIDTH_APPROXIMATION_LAST_PLACE_SHIFT_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.rectpacking.packing.strategy")
        .group("packing")
        .name("Compaction Strategy")
        .description("Strategy for finding an initial placement on nodes.")
        .defaultValue(PACKING_STRATEGY_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(PackingStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.rectpacking.packing.compaction.rowHeightReevaluation")
        .group("packing.compaction")
        .name("Row Height Reevaluation")
        .description("During the compaction step the height of a row is normally not changed. If this options is set, the blocks of other rows might be added if they exceed the row height. If this is the case the whole row has to be packed again to be optimal regarding the new row height. This option should, therefore, be used with care since it might be computation heavy.")
        .defaultValue(PACKING_COMPACTION_ROW_HEIGHT_REEVALUATION_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.rectpacking.packing.compaction.iterations")
        .group("packing.compaction")
        .name("Compaction iterations")
        .description("Defines the number of compaction iterations. E.g. if set to 2 the width is initially approximated, then the drawing is compacted and based on the resulting drawing the target width is decreased or increased and a second compaction step is executed and the result compared to the first one. The best run is used based on the scale measure.")
        .defaultValue(PACKING_COMPACTION_ITERATIONS_DEFAULT)
        .lowerBound(PACKING_COMPACTION_ITERATIONS_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.rectpacking.whiteSpaceElimination.strategy")
        .group("whiteSpaceElimination")
        .name("White Space Approximation Strategy")
        .description("Strategy for expanding nodes such that whitespace in the parent is eliminated.")
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(WhiteSpaceEliminationStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    new org.eclipse.elk.alg.rectpacking.options.RectPackingOptions().apply(registry);
  }
}
