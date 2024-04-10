/**
 * Copyright (c) 2017, 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.elk.alg.radial.options;

import java.util.EnumSet;
import org.eclipse.elk.alg.radial.RadialLayoutProvider;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.NodeLabelPlacement;
import org.eclipse.elk.core.options.PortLabelPlacement;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.graph.properties.IProperty;

/**
 * @radial.md
 */
@SuppressWarnings("all")
public class RadialOptions implements ILayoutMetaDataProvider {
  /**
   * The id of the ELK Radial algorithm.
   */
  public static final String ALGORITHM_ID = "org.eclipse.elk.radial";

  /**
   * The position of a node, port, or label. This is used by the 'Fixed Layout' algorithm to
   * specify a pre-defined position.
   */
  public static final IProperty<KVector> POSITION = CoreOptions.POSITION;

  /**
   * The minimal distance to be preserved between each two nodes.
   */
  public static final IProperty<Double> SPACING_NODE_NODE = CoreOptions.SPACING_NODE_NODE;

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
   * Determine the size of steps with which the compaction is done.
   * Step size 1 correlates to a compaction of 1 pixel per Iteration.
   */
  public static final IProperty<Integer> COMPACTION_STEP_SIZE = RadialMetaDataProvider.COMPACTION_STEP_SIZE;

  /**
   * With the compacter option it can be determined how compaction on the graph is done.
   * It can be chosen between none, the radial compaction or the compaction of wedges separately.
   */
  public static final IProperty<CompactionStrategy> COMPACTOR = RadialMetaDataProvider.COMPACTOR;

  /**
   * The rotate option determines whether a rotation of the layout should be performed.
   */
  public static final IProperty<Boolean> ROTATE = RadialMetaDataProvider.ROTATE;

  /**
   * The angle in radians that the layout should be rotated to after layout.
   */
  public static final IProperty<Double> ROTATION_TARGET_ANGLE = RadialMetaDataProvider.ROTATION_TARGET_ANGLE;

  /**
   * If set to true, modifies the target angle by rotating further such that space is left
   * for an edge to pass in between the nodes. This option should only be used in conjunction
   * with top-down layout.
   */
  public static final IProperty<Boolean> ROTATION_COMPUTE_ADDITIONAL_WEDGE_SPACE = RadialMetaDataProvider.ROTATION_COMPUTE_ADDITIONAL_WEDGE_SPACE;

  /**
   * Calculate the required angle of connected nodes to leave space for an incoming edge. This
   * option should only be used in conjunction with top-down layout.
   */
  public static final IProperty<Boolean> ROTATION_OUTGOING_EDGE_ANGLES = RadialMetaDataProvider.ROTATION_OUTGOING_EDGE_ANGLES;

  /**
   * Find the optimal translation of the nodes of the first radii according to this criteria.
   * For example edge crossings can be minimized.
   */
  public static final IProperty<RadialTranslationStrategy> OPTIMIZATION_CRITERIA = RadialMetaDataProvider.OPTIMIZATION_CRITERIA;

  /**
   * The id can be used to define an order for nodes of one radius. This can be used to sort them in the
   * layer accordingly.
   */
  public static final IProperty<Integer> ORDER_ID = RadialMetaDataProvider.ORDER_ID;

  /**
   * The radius option can be used to set the initial radius for the radial layouter.
   */
  public static final IProperty<Double> RADIUS = RadialMetaDataProvider.RADIUS;

  /**
   * Sort the nodes per radius according to the sorting algorithm. The strategies are none, by the given order id,
   * or sorting them by polar coordinates.
   */
  public static final IProperty<SortingStrategy> SORTER = RadialMetaDataProvider.SORTER;

  /**
   * Determine how the wedge for the node placement is calculated.
   * It can be chosen between wedge determination by the number of leaves or by the maximum sum of diagonals.
   */
  public static final IProperty<AnnulusWedgeCriteria> WEDGE_CRITERIA = RadialMetaDataProvider.WEDGE_CRITERIA;

  /**
   * Centers the layout on the root of the tree i.e. so that the central node is also the center node of the
   * final layout. This introduces additional whitespace.
   */
  public static final IProperty<Boolean> CENTER_ON_ROOT = RadialMetaDataProvider.CENTER_ON_ROOT;

  /**
   * Layouter-specific algorithm factory.
   */
  public static class RadialFactory implements org.eclipse.elk.core.util.IFactory<AbstractLayoutProvider> {
    public AbstractLayoutProvider create() {
      AbstractLayoutProvider provider = new RadialLayoutProvider();
      provider.initialize("");
      return provider;
    }

    public void destroy(final AbstractLayoutProvider obj) {
      obj.dispose();
    }
  }

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutAlgorithmData.Builder()
        .id("org.eclipse.elk.radial")
        .name("ELK Radial")
        .description("A radial layout provider which is based on the algorithm of Peter Eades published in \"Drawing free trees.\", published by International Institute for Advanced Study of Social Information Science, Fujitsu Limited in 1991. The radial layouter takes a tree and places the nodes in radial order around the root. The nodes of the same tree level are placed on the same radius.")
        .providerFactory(new RadialFactory())
        .category("org.eclipse.elk.radial")
        .melkBundleName(null)
        .definingBundleId("org.eclipse.elk.alg.radial")
        .imagePath("images/radial_layout.png")
        .create()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.radial",
        "org.eclipse.elk.position",
        POSITION.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.radial",
        "org.eclipse.elk.spacing.nodeNode",
        SPACING_NODE_NODE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.radial",
        "org.eclipse.elk.nodeSize.constraints",
        NODE_SIZE_CONSTRAINTS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.radial",
        "org.eclipse.elk.nodeSize.minimum",
        NODE_SIZE_MINIMUM.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.radial",
        "org.eclipse.elk.nodeSize.options",
        NODE_SIZE_OPTIONS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.radial",
        "org.eclipse.elk.nodeLabels.placement",
        NODE_LABELS_PLACEMENT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.radial",
        "org.eclipse.elk.omitNodeMicroLayout",
        OMIT_NODE_MICRO_LAYOUT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.radial",
        "org.eclipse.elk.portLabels.placement",
        PORT_LABELS_PLACEMENT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.radial",
        "org.eclipse.elk.radial.compactionStepSize",
        COMPACTION_STEP_SIZE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.radial",
        "org.eclipse.elk.radial.compactor",
        COMPACTOR.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.radial",
        "org.eclipse.elk.radial.rotate",
        ROTATE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.radial",
        "org.eclipse.elk.radial.rotation.targetAngle",
        ROTATION_TARGET_ANGLE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.radial",
        "org.eclipse.elk.radial.rotation.computeAdditionalWedgeSpace",
        ROTATION_COMPUTE_ADDITIONAL_WEDGE_SPACE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.radial",
        "org.eclipse.elk.radial.rotation.outgoingEdgeAngles",
        ROTATION_OUTGOING_EDGE_ANGLES.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.radial",
        "org.eclipse.elk.radial.optimizationCriteria",
        OPTIMIZATION_CRITERIA.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.radial",
        "org.eclipse.elk.radial.orderId",
        ORDER_ID.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.radial",
        "org.eclipse.elk.radial.radius",
        RADIUS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.radial",
        "org.eclipse.elk.radial.sorter",
        SORTER.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.radial",
        "org.eclipse.elk.radial.wedgeCriteria",
        WEDGE_CRITERIA.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.radial",
        "org.eclipse.elk.radial.centerOnRoot",
        CENTER_ON_ROOT.getDefault()
    );
  }
}
