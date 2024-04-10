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

import org.eclipse.elk.alg.topdownpacking.NodeArrangementStrategy;
import org.eclipse.elk.alg.topdownpacking.TopdownpackingLayoutProvider;
import org.eclipse.elk.alg.topdownpacking.WhitespaceEliminationStrategy;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.TopdownNodeTypes;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class TopdownpackingOptions implements ILayoutMetaDataProvider {
  /**
   * The id of the ELK Top-down Packing algorithm.
   */
  public static final String ALGORITHM_ID = "org.eclipse.elk.topdownpacking";

  /**
   * The padding to be left to a parent element's border when placing child elements. This can
   * also serve as an output option of a layout algorithm if node size calculation is setup
   * appropriately.
   */
  public static final IProperty<ElkPadding> PADDING = CoreOptions.PADDING;

  /**
   * The minimal distance to be preserved between each two nodes.
   */
  public static final IProperty<Double> SPACING_NODE_NODE = CoreOptions.SPACING_NODE_NODE;

  /**
   * The fixed size of a hierarchical node when using topdown layout. If this value is set on a parallel
   * node it applies to its children, when set on a hierarchical node it applies to the node itself.
   */
  public static final IProperty<Double> TOPDOWN_HIERARCHICAL_NODE_WIDTH = CoreOptions.TOPDOWN_HIERARCHICAL_NODE_WIDTH;

  /**
   * The fixed aspect ratio of a hierarchical node when using topdown layout. Default is 1/sqrt(2). If this
   * value is set on a parallel node it applies to its children, when set on a hierarchical node it applies to
   * the node itself.
   */
  public static final IProperty<Double> TOPDOWN_HIERARCHICAL_NODE_ASPECT_RATIO = CoreOptions.TOPDOWN_HIERARCHICAL_NODE_ASPECT_RATIO;

  /**
   * Turns topdown layout on and off. If this option is enabled, hierarchical layout will be computed first for
   * the root node and then for its children recursively. Layouts are then scaled down to fit the area provided by
   * their parents. Graphs must follow a certain structure for topdown layout to work properly.
   * {@link TopdownNodeTypes.PARALLEL_NODE} nodes must have children of type
   * {@link TopdownNodeTypes.HIERARCHICAL_NODE} and must define {@link topdown.hierarchicalNodeWidth} and
   * {@link topdown.hierarchicalNodeAspectRatio} for their children. Furthermore they need to be laid out using an
   * algorithm  that is a {@link TopdownLayoutProvider}. Hierarchical nodes can also be parents of other hierarchical
   * nodes and can optionally use a {@link TopdownSizeApproximator} to dynamically set sizes during topdown layout.
   * In this case {@link topdown.hierarchicalNodeWidth} and {@link topdown.hierarchicalNodeAspectRatio} should be set
   * on the node itself rather than the parent. The values are then used by the size approximator as base values.
   * Hierarchical nodes require the layout option {@link nodeSize.fixedGraphSize} to be true to prevent the algorithm
   * used there from resizing the hierarchical node. This option is not supported if 'Hierarchy Handling' is set to
   * 'INCLUDE_CHILDREN'
   */
  public static final IProperty<Boolean> TOPDOWN_LAYOUT = CoreOptions.TOPDOWN_LAYOUT;

  /**
   * Default value for {@link #TOPDOWN_NODE_TYPE} with algorithm "ELK Top-down Packing".
   */
  private static final TopdownNodeTypes TOPDOWN_NODE_TYPE_DEFAULT = TopdownNodeTypes.PARALLEL_NODE;

  /**
   * The different node types used for topdown layout. If the node type is set
   * to {@link TopdownNodeTypes.PARALLEL_NODE} the algorithm must be set to a {@link TopdownLayoutProvider} such
   * as {@link TopdownPacking}. The {@link nodeSize.fixedGraphSize} option is technically only required for
   * hierarchical nodes.
   */
  public static final IProperty<TopdownNodeTypes> TOPDOWN_NODE_TYPE = new Property<TopdownNodeTypes>(
                                CoreOptions.TOPDOWN_NODE_TYPE,
                                TOPDOWN_NODE_TYPE_DEFAULT);

  /**
   * Strategy for node arrangement. The strategy determines the size of the resulting graph.
   */
  public static final IProperty<NodeArrangementStrategy> NODE_ARRANGEMENT_STRATEGY = TopdownpackingMetaDataProvider.NODE_ARRANGEMENT_STRATEGY;

  /**
   * Strategy for whitespace elimination.
   */
  public static final IProperty<WhitespaceEliminationStrategy> WHITESPACE_ELIMINATION_STRATEGY = TopdownpackingMetaDataProvider.WHITESPACE_ELIMINATION_STRATEGY;

  /**
   * Layouter-specific algorithm factory.
   */
  public static class TopdownpackingFactory implements org.eclipse.elk.core.util.IFactory<AbstractLayoutProvider> {
    public AbstractLayoutProvider create() {
      AbstractLayoutProvider provider = new TopdownpackingLayoutProvider();
      provider.initialize("");
      return provider;
    }

    public void destroy(final AbstractLayoutProvider obj) {
      obj.dispose();
    }
  }

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutAlgorithmData.Builder()
        .id("org.eclipse.elk.topdownpacking")
        .name("ELK Top-down Packing")
        .description("An algorithm for placing boxes of fixed sizes. Expands boxes horizontally to fill empty whitespace. This algorithm can be used standalone or specifically for {@link CoreOptions.TOPDOWN_LAYOUT}. In this use case it should be set for nodes whose {@link CoreOptions.TOPDOWN_NODE_TYPE} is set to {@link TopdownNodeTypes.PARALLEL_NODE}. This allows topdown layout to give children larger sizes based on their number of children.")
        .providerFactory(new TopdownpackingFactory())
        .melkBundleName(null)
        .definingBundleId("org.eclipse.elk.alg.topdownpacking")
        .create()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.topdownpacking",
        "org.eclipse.elk.padding",
        PADDING.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.topdownpacking",
        "org.eclipse.elk.spacing.nodeNode",
        SPACING_NODE_NODE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.topdownpacking",
        "org.eclipse.elk.topdown.hierarchicalNodeWidth",
        TOPDOWN_HIERARCHICAL_NODE_WIDTH.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.topdownpacking",
        "org.eclipse.elk.topdown.hierarchicalNodeAspectRatio",
        TOPDOWN_HIERARCHICAL_NODE_ASPECT_RATIO.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.topdownpacking",
        "org.eclipse.elk.topdownLayout",
        TOPDOWN_LAYOUT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.topdownpacking",
        "org.eclipse.elk.topdown.nodeType",
        TOPDOWN_NODE_TYPE_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.topdownpacking",
        "org.eclipse.elk.topdownpacking.nodeArrangement.strategy",
        NODE_ARRANGEMENT_STRATEGY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.topdownpacking",
        "org.eclipse.elk.topdownpacking.whitespaceElimination.strategy",
        WHITESPACE_ELIMINATION_STRATEGY.getDefault()
    );
  }
}
