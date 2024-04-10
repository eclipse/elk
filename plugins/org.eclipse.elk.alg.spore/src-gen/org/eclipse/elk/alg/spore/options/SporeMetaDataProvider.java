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

import java.util.EnumSet;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class SporeMetaDataProvider implements ILayoutMetaDataProvider {
  /**
   * A layout algorithm that is applied to the graph before it is
   * compacted. If this is null, nothing is applied before compaction.
   */
  public static final IProperty<String> UNDERLYING_LAYOUT_ALGORITHM = new Property<String>(
            "org.eclipse.elk.underlyingLayoutAlgorithm");

  /**
   * Default value for {@link #STRUCTURE_STRUCTURE_EXTRACTION_STRATEGY}.
   */
  private static final StructureExtractionStrategy STRUCTURE_STRUCTURE_EXTRACTION_STRATEGY_DEFAULT = StructureExtractionStrategy.DELAUNAY_TRIANGULATION;

  /**
   * This option defines what kind of triangulation or other partitioning of the plane
   * is applied to the vertices.
   */
  public static final IProperty<StructureExtractionStrategy> STRUCTURE_STRUCTURE_EXTRACTION_STRATEGY = new Property<StructureExtractionStrategy>(
            "org.eclipse.elk.structure.structureExtractionStrategy",
            STRUCTURE_STRUCTURE_EXTRACTION_STRATEGY_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #PROCESSING_ORDER_TREE_CONSTRUCTION}.
   */
  private static final TreeConstructionStrategy PROCESSING_ORDER_TREE_CONSTRUCTION_DEFAULT = TreeConstructionStrategy.MINIMUM_SPANNING_TREE;

  /**
   * Whether a minimum spanning tree or a maximum spanning tree should be constructed.
   */
  public static final IProperty<TreeConstructionStrategy> PROCESSING_ORDER_TREE_CONSTRUCTION = new Property<TreeConstructionStrategy>(
            "org.eclipse.elk.processingOrder.treeConstruction",
            PROCESSING_ORDER_TREE_CONSTRUCTION_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #PROCESSING_ORDER_SPANNING_TREE_COST_FUNCTION}.
   */
  private static final SpanningTreeCostFunction PROCESSING_ORDER_SPANNING_TREE_COST_FUNCTION_DEFAULT = SpanningTreeCostFunction.CIRCLE_UNDERLAP;

  /**
   * The cost function is used in the creation of the spanning tree.
   */
  public static final IProperty<SpanningTreeCostFunction> PROCESSING_ORDER_SPANNING_TREE_COST_FUNCTION = new Property<SpanningTreeCostFunction>(
            "org.eclipse.elk.processingOrder.spanningTreeCostFunction",
            PROCESSING_ORDER_SPANNING_TREE_COST_FUNCTION_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #PROCESSING_ORDER_PREFERRED_ROOT}.
   */
  private static final String PROCESSING_ORDER_PREFERRED_ROOT_DEFAULT = null;

  /**
   * The identifier of the node that is preferred as the root of the spanning tree.
   * If this is null, the first node is chosen.
   */
  public static final IProperty<String> PROCESSING_ORDER_PREFERRED_ROOT = new Property<String>(
            "org.eclipse.elk.processingOrder.preferredRoot",
            PROCESSING_ORDER_PREFERRED_ROOT_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #PROCESSING_ORDER_ROOT_SELECTION}.
   */
  private static final RootSelection PROCESSING_ORDER_ROOT_SELECTION_DEFAULT = RootSelection.CENTER_NODE;

  /**
   * This sets the method used to select a root node for the construction of a spanning tree
   */
  public static final IProperty<RootSelection> PROCESSING_ORDER_ROOT_SELECTION = new Property<RootSelection>(
            "org.eclipse.elk.processingOrder.rootSelection",
            PROCESSING_ORDER_ROOT_SELECTION_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #COMPACTION_COMPACTION_STRATEGY}.
   */
  private static final CompactionStrategy COMPACTION_COMPACTION_STRATEGY_DEFAULT = CompactionStrategy.DEPTH_FIRST;

  /**
   * This option defines how the compaction is applied.
   */
  public static final IProperty<CompactionStrategy> COMPACTION_COMPACTION_STRATEGY = new Property<CompactionStrategy>(
            "org.eclipse.elk.compaction.compactionStrategy",
            COMPACTION_COMPACTION_STRATEGY_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #COMPACTION_ORTHOGONAL}.
   */
  private static final boolean COMPACTION_ORTHOGONAL_DEFAULT = false;

  /**
   * Restricts the translation of nodes to orthogonal directions in the compaction phase.
   */
  public static final IProperty<Boolean> COMPACTION_ORTHOGONAL = new Property<Boolean>(
            "org.eclipse.elk.compaction.orthogonal",
            COMPACTION_ORTHOGONAL_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #OVERLAP_REMOVAL_MAX_ITERATIONS}.
   */
  private static final int OVERLAP_REMOVAL_MAX_ITERATIONS_DEFAULT = 64;

  public static final IProperty<Integer> OVERLAP_REMOVAL_MAX_ITERATIONS = new Property<Integer>(
            "org.eclipse.elk.overlapRemoval.maxIterations",
            OVERLAP_REMOVAL_MAX_ITERATIONS_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #OVERLAP_REMOVAL_RUN_SCANLINE}.
   */
  private static final boolean OVERLAP_REMOVAL_RUN_SCANLINE_DEFAULT = true;

  public static final IProperty<Boolean> OVERLAP_REMOVAL_RUN_SCANLINE = new Property<Boolean>(
            "org.eclipse.elk.overlapRemoval.runScanline",
            OVERLAP_REMOVAL_RUN_SCANLINE_DEFAULT,
            null,
            null);

  /**
   * Required value for dependency between {@link #PROCESSING_ORDER_PREFERRED_ROOT} and {@link #PROCESSING_ORDER_ROOT_SELECTION}.
   */
  private static final RootSelection PROCESSING_ORDER_PREFERRED_ROOT_DEP_PROCESSING_ORDER_ROOT_SELECTION_0 = RootSelection.FIXED;

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.underlyingLayoutAlgorithm")
        .group("")
        .name("Underlying Layout Algorithm")
        .description("A layout algorithm that is applied to the graph before it is compacted. If this is null, nothing is applied before compaction.")
        .type(LayoutOptionData.Type.STRING)
        .optionClass(String.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.structure.structureExtractionStrategy")
        .group("structure")
        .name("Structure Extraction Strategy")
        .description("This option defines what kind of triangulation or other partitioning of the plane is applied to the vertices.")
        .defaultValue(STRUCTURE_STRUCTURE_EXTRACTION_STRATEGY_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(StructureExtractionStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.processingOrder.treeConstruction")
        .group("processingOrder")
        .name("Tree Construction Strategy")
        .description("Whether a minimum spanning tree or a maximum spanning tree should be constructed.")
        .defaultValue(PROCESSING_ORDER_TREE_CONSTRUCTION_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(TreeConstructionStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.processingOrder.spanningTreeCostFunction")
        .group("processingOrder")
        .name("Cost Function for Spanning Tree")
        .description("The cost function is used in the creation of the spanning tree.")
        .defaultValue(PROCESSING_ORDER_SPANNING_TREE_COST_FUNCTION_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(SpanningTreeCostFunction.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.processingOrder.preferredRoot")
        .group("processingOrder")
        .name("Root node for spanning tree construction")
        .description("The identifier of the node that is preferred as the root of the spanning tree. If this is null, the first node is chosen.")
        .defaultValue(PROCESSING_ORDER_PREFERRED_ROOT_DEFAULT)
        .type(LayoutOptionData.Type.STRING)
        .optionClass(String.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.addDependency(
        "org.eclipse.elk.processingOrder.preferredRoot",
        "org.eclipse.elk.processingOrder.rootSelection",
        PROCESSING_ORDER_PREFERRED_ROOT_DEP_PROCESSING_ORDER_ROOT_SELECTION_0
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.processingOrder.rootSelection")
        .group("processingOrder")
        .name("Root selection for spanning tree")
        .description("This sets the method used to select a root node for the construction of a spanning tree")
        .defaultValue(PROCESSING_ORDER_ROOT_SELECTION_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(RootSelection.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.compaction.compactionStrategy")
        .group("compaction")
        .name("Compaction Strategy")
        .description("This option defines how the compaction is applied.")
        .defaultValue(COMPACTION_COMPACTION_STRATEGY_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(CompactionStrategy.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.compaction.orthogonal")
        .group("compaction")
        .name("Orthogonal Compaction")
        .description("Restricts the translation of nodes to orthogonal directions in the compaction phase.")
        .defaultValue(COMPACTION_ORTHOGONAL_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.overlapRemoval.maxIterations")
        .group("overlapRemoval")
        .name("Upper limit for iterations of overlap removal")
        .description(null)
        .defaultValue(OVERLAP_REMOVAL_MAX_ITERATIONS_DEFAULT)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.overlapRemoval.runScanline")
        .group("overlapRemoval")
        .name("Whether to run a supplementary scanline overlap check.")
        .description(null)
        .defaultValue(OVERLAP_REMOVAL_RUN_SCANLINE_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    new org.eclipse.elk.alg.spore.options.SporeOverlapRemovalOptions().apply(registry);
    new org.eclipse.elk.alg.spore.options.SporeCompactionOptions().apply(registry);
  }
}
