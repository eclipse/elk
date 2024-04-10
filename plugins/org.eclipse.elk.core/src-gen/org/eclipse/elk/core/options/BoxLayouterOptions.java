/**
 * Copyright (c) 2015, 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.elk.core.options;

import java.util.EnumSet;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.util.BoxLayoutProvider;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class BoxLayouterOptions implements ILayoutMetaDataProvider {
  /**
   * The id of the ELK Box algorithm.
   */
  public static final String ALGORITHM_ID = "org.eclipse.elk.box";

  /**
   * Default value for {@link #PADDING} with algorithm "ELK Box".
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
   * Default value for {@link #SPACING_NODE_NODE} with algorithm "ELK Box".
   */
  private static final double SPACING_NODE_NODE_DEFAULT = 15;

  /**
   * The minimal distance to be preserved between each two nodes.
   */
  public static final IProperty<Double> SPACING_NODE_NODE = new Property<Double>(
                                CoreOptions.SPACING_NODE_NODE,
                                SPACING_NODE_NODE_DEFAULT);

  /**
   * Default value for {@link #PRIORITY} with algorithm "ELK Box".
   */
  private static final int PRIORITY_DEFAULT = 0;

  /**
   * Defines the priority of an object; its meaning depends on the specific layout algorithm
   * and the context where it is used.
   * <h3>Algorithm Specific Details</h3>
   * Priorities set on nodes determine the order in which they are placed:
   * boxes with a higher priority will end up before boxes with a lower priority.
   * Boxes with equal priorities are sorted from smaller to bigger
   * unless the layout algorithm is set to interactive mode.
   */
  public static final IProperty<Integer> PRIORITY = new Property<Integer>(
                                CoreOptions.PRIORITY,
                                PRIORITY_DEFAULT);

  /**
   * If active, nodes are expanded to fill the area of their parent.
   */
  public static final IProperty<Boolean> EXPAND_NODES = CoreOptions.EXPAND_NODES;

  /**
   * What should be taken into account when calculating a node's size. Empty size constraints
   * specify that a node's size is already fixed and should not be changed.
   */
  public static final IProperty<EnumSet<SizeConstraint>> NODE_SIZE_CONSTRAINTS = CoreOptions.NODE_SIZE_CONSTRAINTS;

  /**
   * Options modifying the behavior of the size constraints set on a node. Each member of the
   * set specifies something that should be taken into account when calculating node sizes.
   * The empty set corresponds to no further modifications.
   */
  public static final IProperty<EnumSet<SizeOptions>> NODE_SIZE_OPTIONS = CoreOptions.NODE_SIZE_OPTIONS;

  /**
   * Default value for {@link #ASPECT_RATIO} with algorithm "ELK Box".
   */
  private static final double ASPECT_RATIO_DEFAULT = 1.3f;

  /**
   * The desired aspect ratio of the drawing, that is the quotient of width by height.
   */
  public static final IProperty<Double> ASPECT_RATIO = new Property<Double>(
                                CoreOptions.ASPECT_RATIO,
                                ASPECT_RATIO_DEFAULT);

  /**
   * Whether the algorithm should be run in interactive mode for the content of a parent node.
   * What this means exactly depends on how the specific algorithm interprets this option.
   * Usually in the interactive mode algorithms try to modify the current layout as little as
   * possible.
   */
  public static final IProperty<Boolean> INTERACTIVE = CoreOptions.INTERACTIVE;

  /**
   * The minimal size to which a node can be reduced.
   */
  public static final IProperty<KVector> NODE_SIZE_MINIMUM = CoreOptions.NODE_SIZE_MINIMUM;

  /**
   * Configures the packing mode used by the {@link BoxLayoutProvider}.
   * If SIMPLE is not required (neither priorities are used nor the interactive mode),
   * GROUP_DEC can improve the packing and decrease the area.
   * GROUP_MIXED and GROUP_INC may, in very specific scenarios, work better.
   */
  public static final IProperty<BoxLayoutProvider.PackingMode> BOX_PACKING_MODE = CoreOptions.BOX_PACKING_MODE;

  /**
   * Specifies how the content of a node are aligned. Each node can individually control the alignment of its
   * contents. I.e. if a node should be aligned top left in its parent node, the parent node should specify that
   * option.
   */
  public static final IProperty<EnumSet<ContentAlignment>> CONTENT_ALIGNMENT = CoreOptions.CONTENT_ALIGNMENT;

  /**
   * Layouter-specific algorithm factory.
   */
  public static class BoxFactory implements org.eclipse.elk.core.util.IFactory<AbstractLayoutProvider> {
    public AbstractLayoutProvider create() {
      AbstractLayoutProvider provider = new BoxLayoutProvider();
      provider.initialize("");
      return provider;
    }

    public void destroy(final AbstractLayoutProvider obj) {
      obj.dispose();
    }
  }

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutAlgorithmData.Builder()
        .id("org.eclipse.elk.box")
        .name("ELK Box")
        .description("Algorithm for packing of unconnected boxes, i.e. graphs without edges.")
        .providerFactory(new BoxFactory())
        .melkBundleName("ELK")
        .definingBundleId("org.eclipse.elk.core")
        .imagePath("images/box_layout.png")
        .create()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.box",
        "org.eclipse.elk.padding",
        PADDING_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.box",
        "org.eclipse.elk.spacing.nodeNode",
        SPACING_NODE_NODE_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.box",
        "org.eclipse.elk.priority",
        PRIORITY_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.box",
        "org.eclipse.elk.expandNodes",
        EXPAND_NODES.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.box",
        "org.eclipse.elk.nodeSize.constraints",
        NODE_SIZE_CONSTRAINTS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.box",
        "org.eclipse.elk.nodeSize.options",
        NODE_SIZE_OPTIONS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.box",
        "org.eclipse.elk.aspectRatio",
        ASPECT_RATIO_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.box",
        "org.eclipse.elk.interactive",
        INTERACTIVE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.box",
        "org.eclipse.elk.nodeSize.minimum",
        NODE_SIZE_MINIMUM.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.box",
        "org.eclipse.elk.box.packingMode",
        BOX_PACKING_MODE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.box",
        "org.eclipse.elk.contentAlignment",
        CONTENT_ALIGNMENT.getDefault()
    );
  }
}
