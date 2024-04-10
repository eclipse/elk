/**
 * Copyright (c) 2017 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.elk.alg.disco.options;

import java.util.EnumSet;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class DisCoMetaDataProvider implements ILayoutMetaDataProvider {
  /**
   * Default value for {@link #COMPONENT_COMPACTION_STRATEGY}.
   */
  private static final CompactionStrategy COMPONENT_COMPACTION_STRATEGY_DEFAULT = CompactionStrategy.POLYOMINO;

  /**
   * Strategy for packing different connected components in order to save space
   * and enhance readability of a graph.
   */
  public static final IProperty<CompactionStrategy> COMPONENT_COMPACTION_STRATEGY = new Property<CompactionStrategy>(
            "org.eclipse.elk.disco.componentCompaction.strategy",
            COMPONENT_COMPACTION_STRATEGY_DEFAULT,
            null,
            null);

  /**
   * A layout algorithm that is to be applied to each connected component
   * before the components themselves are compacted. If unspecified,
   * the positions of the components' nodes are not altered.
   */
  public static final IProperty<String> COMPONENT_COMPACTION_COMPONENT_LAYOUT_ALGORITHM = new Property<String>(
            "org.eclipse.elk.disco.componentCompaction.componentLayoutAlgorithm");

  /**
   * Access to the DCGraph is intended for the debug view,
   */
  public static final IProperty<Object> DEBUG_DISCO_GRAPH = new Property<Object>(
            "org.eclipse.elk.disco.debug.discoGraph");

  /**
   * Access to the polyominoes is intended for the debug view,
   */
  public static final IProperty<Object> DEBUG_DISCO_POLYS = new Property<Object>(
            "org.eclipse.elk.disco.debug.discoPolys");

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.disco.componentCompaction.strategy")
        .group("componentCompaction")
        .name("Connected Components Compaction Strategy")
        .description("Strategy for packing different connected components in order to save space and enhance readability of a graph.")
        .defaultValue(COMPONENT_COMPACTION_STRATEGY_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(CompactionStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.disco.componentCompaction.componentLayoutAlgorithm")
        .group("componentCompaction")
        .name("Connected Components Layout Algorithm")
        .description("A layout algorithm that is to be applied to each connected component before the components themselves are compacted. If unspecified, the positions of the components\' nodes are not altered.")
        .type(LayoutOptionData.Type.STRING)
        .optionClass(String.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.disco.debug.discoGraph")
        .group("debug")
        .name("DCGraph")
        .description("Access to the DCGraph is intended for the debug view,")
        .type(LayoutOptionData.Type.OBJECT)
        .optionClass(Object.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.disco.debug.discoPolys")
        .group("debug")
        .name("List of Polyominoes")
        .description("Access to the polyominoes is intended for the debug view,")
        .type(LayoutOptionData.Type.OBJECT)
        .optionClass(Object.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    new org.eclipse.elk.alg.disco.options.DisCoOptions().apply(registry);
  }
}
