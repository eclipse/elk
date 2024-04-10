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
import org.eclipse.elk.alg.rectpacking.RectPackingLayoutProvider;
import org.eclipse.elk.alg.rectpacking.p1widthapproximation.WidthApproximationStrategy;
import org.eclipse.elk.alg.rectpacking.p2packing.PackingStrategy;
import org.eclipse.elk.alg.rectpacking.p3whitespaceelimination.WhiteSpaceEliminationStrategy;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.ContentAlignment;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.NodeLabelPlacement;
import org.eclipse.elk.core.options.PortLabelPlacement;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * @rectpacking.md
 */
@SuppressWarnings("all")
public class RectPackingOptions implements ILayoutMetaDataProvider {
  /**
   * The id of the ELK Rectangle Packing algorithm.
   */
  public static final String ALGORITHM_ID = "org.eclipse.elk.rectpacking";

  /**
   * Default value for {@link #ASPECT_RATIO} with algorithm "ELK Rectangle Packing".
   */
  private static final double ASPECT_RATIO_DEFAULT = 1.3;

  /**
   * The desired aspect ratio of the drawing, that is the quotient of width by height.
   */
  public static final IProperty<Double> ASPECT_RATIO = new Property<Double>(
                                CoreOptions.ASPECT_RATIO,
                                ASPECT_RATIO_DEFAULT);

  /**
   * Default value for {@link #NODE_SIZE_FIXED_GRAPH_SIZE} with algorithm "ELK Rectangle Packing".
   */
  private static final boolean NODE_SIZE_FIXED_GRAPH_SIZE_DEFAULT = false;

  /**
   * By default, the fixed layout provider will enlarge a graph until it is large enough to contain
   * its children. If this option is set, it won't do so.
   */
  public static final IProperty<Boolean> NODE_SIZE_FIXED_GRAPH_SIZE = new Property<Boolean>(
                                CoreOptions.NODE_SIZE_FIXED_GRAPH_SIZE,
                                NODE_SIZE_FIXED_GRAPH_SIZE_DEFAULT);

  /**
   * Default value for {@link #PADDING} with algorithm "ELK Rectangle Packing".
   */
  private static final ElkPadding PADDING_DEFAULT = new ElkPadding(15);

  /**
   * The padding to be left to a parent element's border when placing child elements. This can
   * also serve as an output option of a layout algorithm if node size calculation is setup
   * appropriately.
   */
  public static final IProperty<ElkPadding> PADDING = new Property<ElkPadding>(
                                CoreOptions.PADDING,
                                PADDING_DEFAULT);

  /**
   * Default value for {@link #SPACING_NODE_NODE} with algorithm "ELK Rectangle Packing".
   */
  private static final double SPACING_NODE_NODE_DEFAULT = 15;

  /**
   * The minimal distance to be preserved between each two nodes.
   */
  public static final IProperty<Double> SPACING_NODE_NODE = new Property<Double>(
                                CoreOptions.SPACING_NODE_NODE,
                                SPACING_NODE_NODE_DEFAULT);

  /**
   * Specifies how the content of a node are aligned. Each node can individually control the alignment of its
   * contents. I.e. if a node should be aligned top left in its parent node, the parent node should specify that
   * option.
   */
  public static final IProperty<EnumSet<ContentAlignment>> CONTENT_ALIGNMENT = CoreOptions.CONTENT_ALIGNMENT;

  /**
   * What should be taken into account when calculating a node's size. Empty size constraints
   * specify that a node's size is already fixed and should not be changed.
   */
  public static final IProperty<EnumSet<SizeConstraint>> NODE_SIZE_CONSTRAINTS = CoreOptions.NODE_SIZE_CONSTRAINTS;

  /**
   * The minimal size to which a node can be reduced.
   */
  public static final IProperty<KVector> NODE_SIZE_MINIMUM = CoreOptions.NODE_SIZE_MINIMUM;

  /**
   * Options modifying the behavior of the size constraints set on a node. Each member of the
   * set specifies something that should be taken into account when calculating node sizes.
   * The empty set corresponds to no further modifications.
   */
  public static final IProperty<EnumSet<SizeOptions>> NODE_SIZE_OPTIONS = CoreOptions.NODE_SIZE_OPTIONS;

  /**
   * Hints for where node labels are to be placed; if empty, the node label's position is not
   * modified.
   */
  public static final IProperty<EnumSet<NodeLabelPlacement>> NODE_LABELS_PLACEMENT = CoreOptions.NODE_LABELS_PLACEMENT;

  /**
   * Node micro layout comprises the computation of node dimensions (if requested), the placement of ports
   * and their labels, and the placement of node labels.
   * The functionality is implemented independent of any specific layout algorithm and shouldn't have any
   * negative impact on the layout algorithm's performance itself. Yet, if any unforeseen behavior occurs,
   * this option allows to deactivate the micro layout.
   */
  public static final IProperty<Boolean> OMIT_NODE_MICRO_LAYOUT = CoreOptions.OMIT_NODE_MICRO_LAYOUT;

  /**
   * Decides on a placement method for port labels; if empty, the node label's position is not
   * modified.
   */
  public static final IProperty<EnumSet<PortLabelPlacement>> PORT_LABELS_PLACEMENT = CoreOptions.PORT_LABELS_PLACEMENT;

  /**
   * Optimization goal for approximation of the bounding box given by the first iteration. Determines whether layout is
   * sorted by the maximum scaling, aspect ratio, or area. Depending on the strategy the aspect ratio might be nearly ignored.
   */
  public static final IProperty<OptimizationGoal> WIDTH_APPROXIMATION_OPTIMIZATION_GOAL = RectPackingMetaDataProvider.WIDTH_APPROXIMATION_OPTIMIZATION_GOAL;

  /**
   * When placing a rectangle behind or below the last placed rectangle in the first iteration, it is sometimes
   * possible to shift the rectangle further to the left or right, resulting in less whitespace. True (default)
   * enables the shift and false disables it. Disabling the shift produces a greater approximated area by the first
   * iteration and a layout, when using ONLY the first iteration (default not the case), where it is sometimes
   * impossible to implement a size transformation of rectangles that will fill the bounding box and eliminate
   * empty spaces.
   */
  public static final IProperty<Boolean> WIDTH_APPROXIMATION_LAST_PLACE_SHIFT = RectPackingMetaDataProvider.WIDTH_APPROXIMATION_LAST_PLACE_SHIFT;

  /**
   * Option to place the rectangles in the given target width instead of approximating the width using the desired
   * aspect ratio.
   * The padding is not included in this. Meaning a drawing will have width of targetwidth +
   * horizontal padding.
   */
  public static final IProperty<Double> WIDTH_APPROXIMATION_TARGET_WIDTH = RectPackingMetaDataProvider.WIDTH_APPROXIMATION_TARGET_WIDTH;

  /**
   * Strategy for finding an initial width of the drawing.
   */
  public static final IProperty<WidthApproximationStrategy> WIDTH_APPROXIMATION_STRATEGY = RectPackingMetaDataProvider.WIDTH_APPROXIMATION_STRATEGY;

  /**
   * Strategy for finding an initial placement on nodes.
   */
  public static final IProperty<PackingStrategy> PACKING_STRATEGY = RectPackingMetaDataProvider.PACKING_STRATEGY;

  /**
   * During the compaction step the height of a row is normally not changed.
   * If this options is set, the blocks of other rows might be added if they exceed the row height.
   * If this is the case the whole row has to be packed again to be optimal regarding the new row height.
   * This option should, therefore, be used with care since it might be computation heavy.
   */
  public static final IProperty<Boolean> PACKING_COMPACTION_ROW_HEIGHT_REEVALUATION = RectPackingMetaDataProvider.PACKING_COMPACTION_ROW_HEIGHT_REEVALUATION;

  /**
   * Defines the number of compaction iterations. E.g. if set to 2 the width is initially approximated,
   * then the drawing is compacted and based on the resulting drawing the target width is decreased or
   * increased and a second compaction step is executed and the result compared to the first one. The best
   * run is used based on the scale measure.
   */
  public static final IProperty<Integer> PACKING_COMPACTION_ITERATIONS = RectPackingMetaDataProvider.PACKING_COMPACTION_ITERATIONS;

  /**
   * Strategy for expanding nodes such that whitespace in the parent is eliminated.
   */
  public static final IProperty<WhiteSpaceEliminationStrategy> WHITE_SPACE_ELIMINATION_STRATEGY = RectPackingMetaDataProvider.WHITE_SPACE_ELIMINATION_STRATEGY;

  /**
   * Whether the algorithm should be run in interactive mode for the content of a parent node.
   * What this means exactly depends on how the specific algorithm interprets this option.
   * Usually in the interactive mode algorithms try to modify the current layout as little as
   * possible.
   */
  public static final IProperty<Boolean> INTERACTIVE = CoreOptions.INTERACTIVE;

  /**
   * Whether the graph should be changeable interactively and by setting constraints
   */
  public static final IProperty<Boolean> INTERACTIVE_LAYOUT = CoreOptions.INTERACTIVE_LAYOUT;

  /**
   * The rectangles are ordered. Normally according to their definition the the model.
   * This option allows to specify a desired position that has preference over the original position.
   */
  public static final IProperty<Integer> DESIRED_POSITION = RectPackingMetaDataProvider.DESIRED_POSITION;

  /**
   * The rectangles are ordered. Normally according to their definition the the model.
   * This option specifies the current position of a node.
   */
  public static final IProperty<Integer> CURRENT_POSITION = RectPackingMetaDataProvider.CURRENT_POSITION;

  /**
   * If set to true this node begins in a new row. Consequently this node cannot be moved in a previous layer during
   * compaction. Width approximation does does not take this into account.
   */
  public static final IProperty<Boolean> IN_NEW_ROW = RectPackingMetaDataProvider.IN_NEW_ROW;

  /**
   * Whether one should check whether the regions are stackable to see whether box layout would do the job.
   * For example, nodes with the same height are not stackable inside a row. Therefore, box layout will perform
   * better and faster.
   */
  public static final IProperty<Boolean> TRYBOX = RectPackingMetaDataProvider.TRYBOX;

  /**
   * Layouter-specific algorithm factory.
   */
  public static class RectpackingFactory implements org.eclipse.elk.core.util.IFactory<AbstractLayoutProvider> {
    public AbstractLayoutProvider create() {
      AbstractLayoutProvider provider = new RectPackingLayoutProvider();
      provider.initialize("");
      return provider;
    }

    public void destroy(final AbstractLayoutProvider obj) {
      obj.dispose();
    }
  }

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutAlgorithmData.Builder()
        .id("org.eclipse.elk.rectpacking")
        .name("ELK Rectangle Packing")
        .description("Algorithm for packing of unconnected boxes, i.e. graphs without edges. The given order of the boxes is always preserved and the main reading direction of the boxes is left to right. The algorithm is divided into two phases. One phase approximates the width in which the rectangles can be placed. The next phase places the rectangles in rows using the previously calculated width as bounding width and bundles rectangles with a similar height in blocks. A compaction step reduces the size of the drawing. Finally, the rectangles are expanded to fill their bounding box and eliminate empty unused spaces.")
        .providerFactory(new RectpackingFactory())
        .melkBundleName(null)
        .definingBundleId("org.eclipse.elk.alg.rectpacking")
        .create()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.rectpacking",
        "org.eclipse.elk.aspectRatio",
        ASPECT_RATIO_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.rectpacking",
        "org.eclipse.elk.nodeSize.fixedGraphSize",
        NODE_SIZE_FIXED_GRAPH_SIZE_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.rectpacking",
        "org.eclipse.elk.padding",
        PADDING_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.rectpacking",
        "org.eclipse.elk.spacing.nodeNode",
        SPACING_NODE_NODE_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.rectpacking",
        "org.eclipse.elk.contentAlignment",
        CONTENT_ALIGNMENT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.rectpacking",
        "org.eclipse.elk.nodeSize.constraints",
        NODE_SIZE_CONSTRAINTS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.rectpacking",
        "org.eclipse.elk.nodeSize.minimum",
        NODE_SIZE_MINIMUM.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.rectpacking",
        "org.eclipse.elk.nodeSize.options",
        NODE_SIZE_OPTIONS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.rectpacking",
        "org.eclipse.elk.nodeLabels.placement",
        NODE_LABELS_PLACEMENT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.rectpacking",
        "org.eclipse.elk.omitNodeMicroLayout",
        OMIT_NODE_MICRO_LAYOUT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.rectpacking",
        "org.eclipse.elk.portLabels.placement",
        PORT_LABELS_PLACEMENT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.rectpacking",
        "org.eclipse.elk.rectpacking.widthApproximation.optimizationGoal",
        WIDTH_APPROXIMATION_OPTIMIZATION_GOAL.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.rectpacking",
        "org.eclipse.elk.rectpacking.widthApproximation.lastPlaceShift",
        WIDTH_APPROXIMATION_LAST_PLACE_SHIFT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.rectpacking",
        "org.eclipse.elk.rectpacking.widthApproximation.targetWidth",
        WIDTH_APPROXIMATION_TARGET_WIDTH.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.rectpacking",
        "org.eclipse.elk.rectpacking.widthApproximation.strategy",
        WIDTH_APPROXIMATION_STRATEGY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.rectpacking",
        "org.eclipse.elk.rectpacking.packing.strategy",
        PACKING_STRATEGY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.rectpacking",
        "org.eclipse.elk.rectpacking.packing.compaction.rowHeightReevaluation",
        PACKING_COMPACTION_ROW_HEIGHT_REEVALUATION.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.rectpacking",
        "org.eclipse.elk.rectpacking.packing.compaction.iterations",
        PACKING_COMPACTION_ITERATIONS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.rectpacking",
        "org.eclipse.elk.rectpacking.whiteSpaceElimination.strategy",
        WHITE_SPACE_ELIMINATION_STRATEGY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.rectpacking",
        "org.eclipse.elk.interactive",
        INTERACTIVE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.rectpacking",
        "org.eclipse.elk.interactiveLayout",
        INTERACTIVE_LAYOUT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.rectpacking",
        "org.eclipse.elk.rectpacking.desiredPosition",
        DESIRED_POSITION.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.rectpacking",
        "org.eclipse.elk.rectpacking.currentPosition",
        CURRENT_POSITION.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.rectpacking",
        "org.eclipse.elk.rectpacking.inNewRow",
        IN_NEW_ROW.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.rectpacking",
        "org.eclipse.elk.rectpacking.trybox",
        TRYBOX.getDefault()
    );
  }
}
