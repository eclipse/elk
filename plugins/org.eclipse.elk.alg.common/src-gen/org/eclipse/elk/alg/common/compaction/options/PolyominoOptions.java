/**
 * Copyright (c) 2017 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.elk.alg.common.compaction.options;

import java.util.EnumSet;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class PolyominoOptions implements ILayoutMetaDataProvider {
  /**
   * Default value for {@link #POLYOMINO_TRAVERSAL_STRATEGY}.
   */
  private static final TraversalStrategy POLYOMINO_TRAVERSAL_STRATEGY_DEFAULT = TraversalStrategy.QUADRANTS_LINE_BY_LINE;

  /**
   * Traversal strategy for trying different candidate positions for polyominoes.
   */
  public static final IProperty<TraversalStrategy> POLYOMINO_TRAVERSAL_STRATEGY = new Property<TraversalStrategy>(
            "org.eclipse.elk.polyomino.traversalStrategy",
            POLYOMINO_TRAVERSAL_STRATEGY_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #POLYOMINO_LOW_LEVEL_SORT}.
   */
  private static final LowLevelSortingCriterion POLYOMINO_LOW_LEVEL_SORT_DEFAULT = LowLevelSortingCriterion.BY_SIZE_AND_SHAPE;

  /**
   * Possible secondary sorting criteria for the processing order of polyominoes.
   * They are used when polyominoes are equal according to the primary
   * sorting criterion HighLevelSortingCriterion.
   */
  public static final IProperty<LowLevelSortingCriterion> POLYOMINO_LOW_LEVEL_SORT = new Property<LowLevelSortingCriterion>(
            "org.eclipse.elk.polyomino.lowLevelSort",
            POLYOMINO_LOW_LEVEL_SORT_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #POLYOMINO_HIGH_LEVEL_SORT}.
   */
  private static final HighLevelSortingCriterion POLYOMINO_HIGH_LEVEL_SORT_DEFAULT = HighLevelSortingCriterion.NUM_OF_EXTERNAL_SIDES_THAN_NUM_OF_EXTENSIONS_LAST;

  /**
   * Possible primary sorting criteria for the processing order of polyominoes.
   */
  public static final IProperty<HighLevelSortingCriterion> POLYOMINO_HIGH_LEVEL_SORT = new Property<HighLevelSortingCriterion>(
            "org.eclipse.elk.polyomino.highLevelSort",
            POLYOMINO_HIGH_LEVEL_SORT_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #POLYOMINO_FILL}.
   */
  private static final boolean POLYOMINO_FILL_DEFAULT = true;

  /**
   * Use the Profile Fill algorithm to fill polyominoes to prevent small polyominoes
   * from being placed inside of big polyominoes with large holes. Might increase packing area.
   */
  public static final IProperty<Boolean> POLYOMINO_FILL = new Property<Boolean>(
            "org.eclipse.elk.polyomino.fill",
            POLYOMINO_FILL_DEFAULT,
            null,
            null);

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.polyomino.traversalStrategy")
        .group("polyomino")
        .name("Polyomino Traversal Strategy")
        .description("Traversal strategy for trying different candidate positions for polyominoes.")
        .defaultValue(POLYOMINO_TRAVERSAL_STRATEGY_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(TraversalStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.polyomino.lowLevelSort")
        .group("polyomino")
        .name("Polyomino Secondary Sorting Criterion")
        .description("Possible secondary sorting criteria for the processing order of polyominoes. They are used when polyominoes are equal according to the primary sorting criterion HighLevelSortingCriterion.")
        .defaultValue(POLYOMINO_LOW_LEVEL_SORT_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(LowLevelSortingCriterion.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.polyomino.highLevelSort")
        .group("polyomino")
        .name("Polyomino Primary Sorting Criterion")
        .description("Possible primary sorting criteria for the processing order of polyominoes.")
        .defaultValue(POLYOMINO_HIGH_LEVEL_SORT_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(HighLevelSortingCriterion.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.polyomino.fill")
        .group("polyomino")
        .name("Fill Polyominoes")
        .description("Use the Profile Fill algorithm to fill polyominoes to prevent small polyominoes from being placed inside of big polyominoes with large holes. Might increase packing area.")
        .defaultValue(POLYOMINO_FILL_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
  }
}
