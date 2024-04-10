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

import org.eclipse.elk.alg.spore.OverlapRemovalLayoutProvider;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class SporeOverlapRemovalOptions implements ILayoutMetaDataProvider {
  /**
   * The id of the ELK SPOrE Overlap Removal algorithm.
   */
  public static final String ALGORITHM_ID = "org.eclipse.elk.sporeOverlap";

  /**
   * A layout algorithm that is applied to the graph before it is
   * compacted. If this is null, nothing is applied before compaction.
   */
  public static final IProperty<String> UNDERLYING_LAYOUT_ALGORITHM = SporeMetaDataProvider.UNDERLYING_LAYOUT_ALGORITHM;

  /**
   * Default value for {@link #PADDING} with algorithm "ELK SPOrE Overlap Removal".
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
   * Default value for {@link #SPACING_NODE_NODE} with algorithm "ELK SPOrE Overlap Removal".
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
   * null
   */
  public static final IProperty<Integer> OVERLAP_REMOVAL_MAX_ITERATIONS = SporeMetaDataProvider.OVERLAP_REMOVAL_MAX_ITERATIONS;

  /**
   * null
   */
  public static final IProperty<Boolean> OVERLAP_REMOVAL_RUN_SCANLINE = SporeMetaDataProvider.OVERLAP_REMOVAL_RUN_SCANLINE;

  /**
   * Default value for {@link #DEBUG_MODE} with algorithm "ELK SPOrE Overlap Removal".
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
  public static class SporeOverlapFactory implements org.eclipse.elk.core.util.IFactory<AbstractLayoutProvider> {
    public AbstractLayoutProvider create() {
      AbstractLayoutProvider provider = new OverlapRemovalLayoutProvider();
      provider.initialize("");
      return provider;
    }

    public void destroy(final AbstractLayoutProvider obj) {
      obj.dispose();
    }
  }

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutAlgorithmData.Builder()
        .id("org.eclipse.elk.sporeOverlap")
        .name("ELK SPOrE Overlap Removal")
        .description("A node overlap removal algorithm proposed by Nachmanson et al. in \"Node overlap removal by growing a tree\".")
        .providerFactory(new SporeOverlapFactory())
        .melkBundleName(null)
        .definingBundleId("org.eclipse.elk.alg.spore")
        .imagePath("images/overlap-removal.png")
        .create()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.sporeOverlap",
        "org.eclipse.elk.underlyingLayoutAlgorithm",
        UNDERLYING_LAYOUT_ALGORITHM.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.sporeOverlap",
        "org.eclipse.elk.padding",
        PADDING_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.sporeOverlap",
        "org.eclipse.elk.spacing.nodeNode",
        SPACING_NODE_NODE_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.sporeOverlap",
        "org.eclipse.elk.structure.structureExtractionStrategy",
        STRUCTURE_STRUCTURE_EXTRACTION_STRATEGY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.sporeOverlap",
        "org.eclipse.elk.overlapRemoval.maxIterations",
        OVERLAP_REMOVAL_MAX_ITERATIONS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.sporeOverlap",
        "org.eclipse.elk.overlapRemoval.runScanline",
        OVERLAP_REMOVAL_RUN_SCANLINE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.sporeOverlap",
        "org.eclipse.elk.debugMode",
        DEBUG_MODE_DEFAULT
    );
  }
}
