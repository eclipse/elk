/**
 * Copyright (c) 2017 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.elk.alg.spore.options;

import org.eclipse.elk.alg.spore.ShrinkTreeLayoutProvider;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * @spore.md
 */
@SuppressWarnings("all")
public class SporeCompactionOptions implements ILayoutMetaDataProvider {
  /**
   * The id of the ELK SPOrE Compaction algorithm.
   */
  public static final String ALGORITHM_ID = "org.eclipse.elk.sporeCompaction";

  /**
   * A layout algorithm that is applied to the graph before it is
   * compacted. If this is null, nothing is applied before compaction.
   */
  public static final IProperty<String> UNDERLYING_LAYOUT_ALGORITHM = SporeMetaDataProvider.UNDERLYING_LAYOUT_ALGORITHM;

  /**
   * Whether a minimum spanning tree or a maximum spanning tree should be constructed.
   */
  public static final IProperty<TreeConstructionStrategy> PROCESSING_ORDER_TREE_CONSTRUCTION = SporeMetaDataProvider.PROCESSING_ORDER_TREE_CONSTRUCTION;

  /**
   * The cost function is used in the creation of the spanning tree.
   */
  public static final IProperty<SpanningTreeCostFunction> PROCESSING_ORDER_SPANNING_TREE_COST_FUNCTION = SporeMetaDataProvider.PROCESSING_ORDER_SPANNING_TREE_COST_FUNCTION;

  /**
   * The identifier of the node that is preferred as the root of the spanning tree.
   * If this is null, the first node is chosen.
   */
  public static final IProperty<String> PROCESSING_ORDER_PREFERRED_ROOT = SporeMetaDataProvider.PROCESSING_ORDER_PREFERRED_ROOT;

  /**
   * This sets the method used to select a root node for the construction of a spanning tree
   */
  public static final IProperty<RootSelection> PROCESSING_ORDER_ROOT_SELECTION = SporeMetaDataProvider.PROCESSING_ORDER_ROOT_SELECTION;

  /**
   * Default value for {@link #PADDING} with algorithm "ELK SPOrE Compaction".
   */
  private static final ElkPadding PADDING_DEFAULT = new ElkPadding(8);

  /**
   * The padding to be left to a parent element's border when placing child elements. This can
   * also serve as an output option of a layout algorithm if node size calculation is setup
   * appropriately.
   */
  public static final IProperty<ElkPadding> PADDING = new Property<ElkPadding>(
                                CoreOptions.PADDING,
                                PADDING_DEFAULT);

  /**
   * Default value for {@link #SPACING_NODE_NODE} with algorithm "ELK SPOrE Compaction".
   */
  private static final double SPACING_NODE_NODE_DEFAULT = 8;

  /**
   * The minimal distance to be preserved between each two nodes.
   */
  public static final IProperty<Double> SPACING_NODE_NODE = new Property<Double>(
                                CoreOptions.SPACING_NODE_NODE,
                                SPACING_NODE_NODE_DEFAULT);

  /**
   * This option defines what kind of triangulation or other partitioning of the plane
   * is applied to the vertices.
   */
  public static final IProperty<StructureExtractionStrategy> STRUCTURE_STRUCTURE_EXTRACTION_STRATEGY = SporeMetaDataProvider.STRUCTURE_STRUCTURE_EXTRACTION_STRATEGY;

  /**
   * This option defines how the compaction is applied.
   */
  public static final IProperty<CompactionStrategy> COMPACTION_COMPACTION_STRATEGY = SporeMetaDataProvider.COMPACTION_COMPACTION_STRATEGY;

  /**
   * Restricts the translation of nodes to orthogonal directions in the compaction phase.
   */
  public static final IProperty<Boolean> COMPACTION_ORTHOGONAL = SporeMetaDataProvider.COMPACTION_ORTHOGONAL;

  /**
   * Default value for {@link #DEBUG_MODE} with algorithm "ELK SPOrE Compaction".
   */
  private static final boolean DEBUG_MODE_DEFAULT = false;

  /**
   * Whether additional debug information shall be generated.
   */
  public static final IProperty<Boolean> DEBUG_MODE = new Property<Boolean>(
                                CoreOptions.DEBUG_MODE,
                                DEBUG_MODE_DEFAULT);

  /**
   * Layouter-specific algorithm factory.
   */
  public static class SporeCompactionFactory implements org.eclipse.elk.core.util.IFactory<AbstractLayoutProvider> {
    public AbstractLayoutProvider create() {
      AbstractLayoutProvider provider = new ShrinkTreeLayoutProvider();
      provider.initialize("");
      return provider;
    }

    public void destroy(final AbstractLayoutProvider obj) {
      obj.dispose();
    }
  }

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutAlgorithmData.Builder()
        .id("org.eclipse.elk.sporeCompaction")
        .name("ELK SPOrE Compaction")
        .description("ShrinkTree is a compaction algorithm that maintains the topology of a layout. The relocation of diagram elements is based on contracting a spanning tree.")
        .providerFactory(new SporeCompactionFactory())
        .melkBundleName(null)
        .definingBundleId("org.eclipse.elk.alg.spore")
        .imagePath("images/compaction-example.png")
        .create()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.sporeCompaction",
        "org.eclipse.elk.underlyingLayoutAlgorithm",
        UNDERLYING_LAYOUT_ALGORITHM.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.sporeCompaction",
        "org.eclipse.elk.processingOrder.treeConstruction",
        PROCESSING_ORDER_TREE_CONSTRUCTION.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.sporeCompaction",
        "org.eclipse.elk.processingOrder.spanningTreeCostFunction",
        PROCESSING_ORDER_SPANNING_TREE_COST_FUNCTION.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.sporeCompaction",
        "org.eclipse.elk.processingOrder.preferredRoot",
        PROCESSING_ORDER_PREFERRED_ROOT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.sporeCompaction",
        "org.eclipse.elk.processingOrder.rootSelection",
        PROCESSING_ORDER_ROOT_SELECTION.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.sporeCompaction",
        "org.eclipse.elk.padding",
        PADDING_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.sporeCompaction",
        "org.eclipse.elk.spacing.nodeNode",
        SPACING_NODE_NODE_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.sporeCompaction",
        "org.eclipse.elk.structure.structureExtractionStrategy",
        STRUCTURE_STRUCTURE_EXTRACTION_STRATEGY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.sporeCompaction",
        "org.eclipse.elk.compaction.compactionStrategy",
        COMPACTION_COMPACTION_STRATEGY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.sporeCompaction",
        "org.eclipse.elk.compaction.orthogonal",
        COMPACTION_ORTHOGONAL.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.sporeCompaction",
        "org.eclipse.elk.debugMode",
        DEBUG_MODE_DEFAULT
    );
  }
}
