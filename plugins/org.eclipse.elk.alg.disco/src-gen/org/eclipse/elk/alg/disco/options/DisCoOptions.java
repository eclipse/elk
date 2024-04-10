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

import org.eclipse.elk.alg.common.compaction.options.HighLevelSortingCriterion;
import org.eclipse.elk.alg.common.compaction.options.LowLevelSortingCriterion;
import org.eclipse.elk.alg.common.compaction.options.PolyominoOptions;
import org.eclipse.elk.alg.common.compaction.options.TraversalStrategy;
import org.eclipse.elk.alg.disco.DisCoLayoutProvider;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.graph.properties.IProperty;

@SuppressWarnings("all")
public class DisCoOptions implements ILayoutMetaDataProvider {
  /**
   * The id of the ELK DisCo algorithm.
   */
  public static final String ALGORITHM_ID = "org.eclipse.elk.disco";

  /**
   * Spacing to be preserved between pairs of connected components.
   * This option is only relevant if 'separateConnectedComponents' is activated.
   */
  public static final IProperty<Double> SPACING_COMPONENT_COMPONENT = CoreOptions.SPACING_COMPONENT_COMPONENT;

  /**
   * The thickness of an edge. This is a hint on the line width used to draw an edge, possibly
   * requiring more space to be reserved for it.
   */
  public static final IProperty<Double> EDGE_THICKNESS = CoreOptions.EDGE_THICKNESS;

  /**
   * The desired aspect ratio of the drawing, that is the quotient of width by height.
   */
  public static final IProperty<Double> ASPECT_RATIO = CoreOptions.ASPECT_RATIO;

  /**
   * The padding to be left to a parent element's border when placing child elements. This can
   * also serve as an output option of a layout algorithm if node size calculation is setup
   * appropriately.
   */
  public static final IProperty<ElkPadding> PADDING = CoreOptions.PADDING;

  /**
   * Possible secondary sorting criteria for the processing order of polyominoes.
   * They are used when polyominoes are equal according to the primary
   * sorting criterion HighLevelSortingCriterion.
   */
  public static final IProperty<LowLevelSortingCriterion> POLYOMINO_LOW_LEVEL_SORT = PolyominoOptions.POLYOMINO_LOW_LEVEL_SORT;

  /**
   * Possible primary sorting criteria for the processing order of polyominoes.
   */
  public static final IProperty<HighLevelSortingCriterion> POLYOMINO_HIGH_LEVEL_SORT = PolyominoOptions.POLYOMINO_HIGH_LEVEL_SORT;

  /**
   * Traversal strategy for trying different candidate positions for polyominoes.
   */
  public static final IProperty<TraversalStrategy> POLYOMINO_TRAVERSAL_STRATEGY = PolyominoOptions.POLYOMINO_TRAVERSAL_STRATEGY;

  /**
   * Use the Profile Fill algorithm to fill polyominoes to prevent small polyominoes
   * from being placed inside of big polyominoes with large holes. Might increase packing area.
   */
  public static final IProperty<Boolean> POLYOMINO_FILL = PolyominoOptions.POLYOMINO_FILL;

  /**
   * Strategy for packing different connected components in order to save space
   * and enhance readability of a graph.
   */
  public static final IProperty<CompactionStrategy> COMPONENT_COMPACTION_STRATEGY = DisCoMetaDataProvider.COMPONENT_COMPACTION_STRATEGY;

  /**
   * A layout algorithm that is to be applied to each connected component
   * before the components themselves are compacted. If unspecified,
   * the positions of the components' nodes are not altered.
   */
  public static final IProperty<String> COMPONENT_COMPACTION_COMPONENT_LAYOUT_ALGORITHM = DisCoMetaDataProvider.COMPONENT_COMPACTION_COMPONENT_LAYOUT_ALGORITHM;

  /**
   * Access to the DCGraph is intended for the debug view,
   */
  public static final IProperty<Object> DEBUG_DISCO_GRAPH = DisCoMetaDataProvider.DEBUG_DISCO_GRAPH;

  /**
   * Access to the polyominoes is intended for the debug view,
   */
  public static final IProperty<Object> DEBUG_DISCO_POLYS = DisCoMetaDataProvider.DEBUG_DISCO_POLYS;

  /**
   * Layouter-specific algorithm factory.
   */
  public static class DiscoFactory implements org.eclipse.elk.core.util.IFactory<AbstractLayoutProvider> {
    public AbstractLayoutProvider create() {
      AbstractLayoutProvider provider = new DisCoLayoutProvider();
      provider.initialize("");
      return provider;
    }

    public void destroy(final AbstractLayoutProvider obj) {
      obj.dispose();
    }
  }

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutAlgorithmData.Builder()
        .id("org.eclipse.elk.disco")
        .name("ELK DisCo")
        .description("Layouter for arranging unconnected subgraphs. The subgraphs themselves are, by default, not laid out.")
        .providerFactory(new DiscoFactory())
        .melkBundleName(null)
        .definingBundleId("org.eclipse.elk.alg.disco")
        .imagePath("images/disco_layout.png")
        .create()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.disco",
        "org.eclipse.elk.spacing.componentComponent",
        SPACING_COMPONENT_COMPONENT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.disco",
        "org.eclipse.elk.edge.thickness",
        EDGE_THICKNESS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.disco",
        "org.eclipse.elk.aspectRatio",
        ASPECT_RATIO.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.disco",
        "org.eclipse.elk.padding",
        PADDING.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.disco",
        "org.eclipse.elk.polyomino.lowLevelSort",
        POLYOMINO_LOW_LEVEL_SORT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.disco",
        "org.eclipse.elk.polyomino.highLevelSort",
        POLYOMINO_HIGH_LEVEL_SORT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.disco",
        "org.eclipse.elk.polyomino.traversalStrategy",
        POLYOMINO_TRAVERSAL_STRATEGY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.disco",
        "org.eclipse.elk.polyomino.fill",
        POLYOMINO_FILL.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.disco",
        "org.eclipse.elk.disco.componentCompaction.strategy",
        COMPONENT_COMPACTION_STRATEGY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.disco",
        "org.eclipse.elk.disco.componentCompaction.componentLayoutAlgorithm",
        COMPONENT_COMPACTION_COMPONENT_LAYOUT_ALGORITHM.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.disco",
        "org.eclipse.elk.disco.debug.discoGraph",
        DEBUG_DISCO_GRAPH.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.disco",
        "org.eclipse.elk.disco.debug.discoPolys",
        DEBUG_DISCO_POLYS.getDefault()
    );
  }
}
