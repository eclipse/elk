/**
 * Copyright (c) 2016, 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.elk.alg.force.options;

import java.util.EnumSet;
import org.eclipse.elk.alg.force.stress.StressLayoutProvider;
import org.eclipse.elk.alg.force.stress.StressMajorization;
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
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class StressOptions implements ILayoutMetaDataProvider {
  /**
   * The id of the ELK Stress algorithm.
   */
  public static final String ALGORITHM_ID = "org.eclipse.elk.stress";

  /**
   * Whether the algorithm should be run in interactive mode for the content of a parent node.
   * What this means exactly depends on how the specific algorithm interprets this option.
   * Usually in the interactive mode algorithms try to modify the current layout as little as
   * possible.
   */
  public static final IProperty<Boolean> INTERACTIVE = CoreOptions.INTERACTIVE;

  /**
   * Default value for {@link #EDGE_LABELS_INLINE} with algorithm "ELK Stress".
   */
  private static final boolean EDGE_LABELS_INLINE_DEFAULT = true;

  /**
   * If true, an edge label is placed directly on its edge. May only apply to center edge labels.
   * This kind of label placement is only advisable if the label's rendering is such that it is not
   * crossed by its edge and thus stays legible.
   */
  public static final IProperty<Boolean> EDGE_LABELS_INLINE = new Property<Boolean>(
                                CoreOptions.EDGE_LABELS_INLINE,
                                EDGE_LABELS_INLINE_DEFAULT);

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
   * Prevent that the node is moved by the layout algorithm.
   */
  public static final IProperty<Boolean> FIXED = StressMetaDataProvider.FIXED;

  /**
   * Dimensions that are permitted to be altered during layout.
   */
  public static final IProperty<StressMajorization.Dimension> DIMENSION = StressMetaDataProvider.DIMENSION;

  /**
   * Termination criterion for the iterative process.
   */
  public static final IProperty<Double> EPSILON = StressMetaDataProvider.EPSILON;

  /**
   * Maximum number of performed iterations. Takes higher
   * precedence than 'epsilon'.
   */
  public static final IProperty<Integer> ITERATION_LIMIT = StressMetaDataProvider.ITERATION_LIMIT;

  /**
   * Either specified for parent nodes or for individual edges,
   * where the latter takes higher precedence.
   */
  public static final IProperty<Double> DESIRED_EDGE_LENGTH = StressMetaDataProvider.DESIRED_EDGE_LENGTH;

  /**
   * Layouter-specific algorithm factory.
   */
  public static class StressFactory implements org.eclipse.elk.core.util.IFactory<AbstractLayoutProvider> {
    public AbstractLayoutProvider create() {
      AbstractLayoutProvider provider = new StressLayoutProvider();
      provider.initialize("");
      return provider;
    }

    public void destroy(final AbstractLayoutProvider obj) {
      obj.dispose();
    }
  }

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutAlgorithmData.Builder()
        .id("org.eclipse.elk.stress")
        .name("ELK Stress")
        .description("Minimizes the stress within a layout using stress majorization. Stress exists if the euclidean distance between a pair of nodes doesn\'t match their graph theoretic distance, that is, the shortest path between the two nodes. The method allows to specify individual edge lengths.")
        .providerFactory(new StressFactory())
        .category("org.eclipse.elk.force")
        .melkBundleName(null)
        .definingBundleId("org.eclipse.elk.alg.force")
        .imagePath("images/stress_layout.png")
        .create()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.stress",
        "org.eclipse.elk.interactive",
        INTERACTIVE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.stress",
        "org.eclipse.elk.edgeLabels.inline",
        EDGE_LABELS_INLINE_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.stress",
        "org.eclipse.elk.nodeSize.constraints",
        NODE_SIZE_CONSTRAINTS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.stress",
        "org.eclipse.elk.nodeSize.minimum",
        NODE_SIZE_MINIMUM.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.stress",
        "org.eclipse.elk.nodeSize.options",
        NODE_SIZE_OPTIONS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.stress",
        "org.eclipse.elk.nodeLabels.placement",
        NODE_LABELS_PLACEMENT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.stress",
        "org.eclipse.elk.omitNodeMicroLayout",
        OMIT_NODE_MICRO_LAYOUT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.stress",
        "org.eclipse.elk.portLabels.placement",
        PORT_LABELS_PLACEMENT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.stress",
        "org.eclipse.elk.stress.fixed",
        FIXED.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.stress",
        "org.eclipse.elk.stress.dimension",
        DIMENSION.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.stress",
        "org.eclipse.elk.stress.epsilon",
        EPSILON.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.stress",
        "org.eclipse.elk.stress.iterationLimit",
        ITERATION_LIMIT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.stress",
        "org.eclipse.elk.stress.desiredEdgeLength",
        DESIRED_EDGE_LENGTH.getDefault()
    );
  }
}
