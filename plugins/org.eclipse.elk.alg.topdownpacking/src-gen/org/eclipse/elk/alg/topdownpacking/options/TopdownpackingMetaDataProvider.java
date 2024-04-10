/**
 * Copyright (c) 2022 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.elk.alg.topdownpacking.options;

import java.util.EnumSet;
import org.eclipse.elk.alg.topdownpacking.NodeArrangementStrategy;
import org.eclipse.elk.alg.topdownpacking.WhitespaceEliminationStrategy;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class TopdownpackingMetaDataProvider implements ILayoutMetaDataProvider {
  /**
   * Default value for {@link #NODE_ARRANGEMENT_STRATEGY}.
   */
  private static final NodeArrangementStrategy NODE_ARRANGEMENT_STRATEGY_DEFAULT = NodeArrangementStrategy.LEFT_RIGHT_TOP_DOWN_NODE_PLACER;

  /**
   * Strategy for node arrangement. The strategy determines the size of the resulting graph.
   */
  public static final IProperty<NodeArrangementStrategy> NODE_ARRANGEMENT_STRATEGY = new Property<NodeArrangementStrategy>(
            "org.eclipse.elk.topdownpacking.nodeArrangement.strategy",
            NODE_ARRANGEMENT_STRATEGY_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #WHITESPACE_ELIMINATION_STRATEGY}.
   */
  private static final WhitespaceEliminationStrategy WHITESPACE_ELIMINATION_STRATEGY_DEFAULT = WhitespaceEliminationStrategy.BOTTOM_ROW_EQUAL_WHITESPACE_ELIMINATOR;

  /**
   * Strategy for whitespace elimination.
   */
  public static final IProperty<WhitespaceEliminationStrategy> WHITESPACE_ELIMINATION_STRATEGY = new Property<WhitespaceEliminationStrategy>(
            "org.eclipse.elk.topdownpacking.whitespaceElimination.strategy",
            WHITESPACE_ELIMINATION_STRATEGY_DEFAULT,
            null,
            null);

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.topdownpacking.nodeArrangement.strategy")
        .group("nodeArrangement")
        .name("Node arrangement strategy")
        .description("Strategy for node arrangement. The strategy determines the size of the resulting graph.")
        .defaultValue(NODE_ARRANGEMENT_STRATEGY_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(NodeArrangementStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.topdownpacking.whitespaceElimination.strategy")
        .group("whitespaceElimination")
        .name("Whitespace elimination strategy")
        .description("Strategy for whitespace elimination.")
        .defaultValue(WHITESPACE_ELIMINATION_STRATEGY_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(WhitespaceEliminationStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    new org.eclipse.elk.alg.topdownpacking.options.TopdownpackingOptions().apply(registry);
  }
}
