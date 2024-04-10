/**
 * Copyright (c) 2015 - 2022 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.elk.alg.mrtree.options;

import java.util.EnumSet;
import org.eclipse.elk.alg.mrtree.TreeLayoutProvider;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.NodeLabelPlacement;
import org.eclipse.elk.core.options.PortLabelPlacement;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.core.options.TopdownNodeTypes;
import org.eclipse.elk.graph.properties.GraphFeature;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class MrTreeOptions implements ILayoutMetaDataProvider {
  /**
   * The id of the ELK Mr. Tree algorithm.
   */
  public static final String ALGORITHM_ID = "org.eclipse.elk.mrtree";

  /**
   * Default value for {@link #PADDING} with algorithm "ELK Mr. Tree".
   */
  private static final ElkPadding PADDING_DEFAULT = new ElkPadding(20);

  /**
   * The padding to be left to a parent element's border when placing child elements. This can
   * also serve as an output option of a layout algorithm if node size calculation is setup
   * appropriately.
   */
  public static final IProperty<ElkPadding> PADDING = new Property<ElkPadding>(
                                CoreOptions.PADDING,
                                PADDING_DEFAULT);

  /**
   * Default value for {@link #SPACING_NODE_NODE} with algorithm "ELK Mr. Tree".
   */
  private static final double SPACING_NODE_NODE_DEFAULT = 20;

  /**
   * The minimal distance to be preserved between each two nodes.
   */
  public static final IProperty<Double> SPACING_NODE_NODE = new Property<Double>(
                                CoreOptions.SPACING_NODE_NODE,
                                SPACING_NODE_NODE_DEFAULT);

  /**
   * Default value for {@link #SPACING_EDGE_NODE} with algorithm "ELK Mr. Tree".
   */
  private static final double SPACING_EDGE_NODE_DEFAULT = 3;

  /**
   * Spacing to be preserved between nodes and edges.
   */
  public static final IProperty<Double> SPACING_EDGE_NODE = new Property<Double>(
                                CoreOptions.SPACING_EDGE_NODE,
                                SPACING_EDGE_NODE_DEFAULT);

  /**
   * Default value for {@link #ASPECT_RATIO} with algorithm "ELK Mr. Tree".
   */
  private static final double ASPECT_RATIO_DEFAULT = 1.6f;

  /**
   * The desired aspect ratio of the drawing, that is the quotient of width by height.
   */
  public static final IProperty<Double> ASPECT_RATIO = new Property<Double>(
                                CoreOptions.ASPECT_RATIO,
                                ASPECT_RATIO_DEFAULT);

  /**
   * Default value for {@link #PRIORITY} with algorithm "ELK Mr. Tree".
   */
  private static final int PRIORITY_DEFAULT = 1;

  /**
   * Defines the priority of an object; its meaning depends on the specific layout algorithm
   * and the context where it is used.
   * <h3>Algorithm Specific Details</h3>
   * Priorities set on nodes determine the order in which connected components are placed:
   * components with a higher sum of node priorities will end up
   * before components with a lower sum.
   */
  public static final IProperty<Integer> PRIORITY = new Property<Integer>(
                                CoreOptions.PRIORITY,
                                PRIORITY_DEFAULT);

  /**
   * Default value for {@link #SEPARATE_CONNECTED_COMPONENTS} with algorithm "ELK Mr. Tree".
   */
  private static final boolean SEPARATE_CONNECTED_COMPONENTS_DEFAULT = true;

  /**
   * Whether each connected component should be processed separately.
   */
  public static final IProperty<Boolean> SEPARATE_CONNECTED_COMPONENTS = new Property<Boolean>(
                                CoreOptions.SEPARATE_CONNECTED_COMPONENTS,
                                SEPARATE_CONNECTED_COMPONENTS_DEFAULT);

  /**
   * Whether additional debug information shall be generated.
   */
  public static final IProperty<Boolean> DEBUG_MODE = CoreOptions.DEBUG_MODE;

  /**
   * Default value for {@link #DIRECTION} with algorithm "ELK Mr. Tree".
   */
  private static final Direction DIRECTION_DEFAULT = Direction.UNDEFINED;

  /**
   * Overall direction of edges: horizontal (right / left) or
   * vertical (down / up).
   */
  public static final IProperty<Direction> DIRECTION = new Property<Direction>(
                                CoreOptions.DIRECTION,
                                DIRECTION_DEFAULT);

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
   * What should be taken into account when calculating a node's size. Empty size constraints
   * specify that a node's size is already fixed and should not be changed.
   */
  public static final IProperty<EnumSet<SizeConstraint>> NODE_SIZE_CONSTRAINTS = CoreOptions.NODE_SIZE_CONSTRAINTS;

  /**
   * By default, the fixed layout provider will enlarge a graph until it is large enough to contain
   * its children. If this option is set, it won't do so.
   */
  public static final IProperty<Boolean> NODE_SIZE_FIXED_GRAPH_SIZE = CoreOptions.NODE_SIZE_FIXED_GRAPH_SIZE;

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
   * Which weighting to use when computing a node order.
   */
  public static final IProperty<OrderWeighting> WEIGHTING = MrTreeMetaDataProvider.WEIGHTING;

  /**
   * Which search order to use when computing a spanning tree.
   */
  public static final IProperty<TreeifyingOrder> SEARCH_ORDER = MrTreeMetaDataProvider.SEARCH_ORDER;

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
   * The scaling factor to be applied to the nodes laid out within the node in recursive topdown
   * layout. The difference to 'Scale Factor' is that the node itself is not scaled. This value has to be set on
   * hierarchical nodes.
   */
  public static final IProperty<Double> TOPDOWN_SCALE_FACTOR = CoreOptions.TOPDOWN_SCALE_FACTOR;

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
   * Default value for {@link #TOPDOWN_NODE_TYPE} with algorithm "ELK Mr. Tree".
   */
  private static final TopdownNodeTypes TOPDOWN_NODE_TYPE_DEFAULT = TopdownNodeTypes.HIERARCHICAL_NODE;

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
   * When set to a positive number this option will force the algorithm to place the node to the
   * specified position within the trees layer if weighting is set to constraint
   */
  public static final IProperty<Integer> POSITION_CONSTRAINT = MrTreeMetaDataProvider.POSITION_CONSTRAINT;

  /**
   * Chooses an Edge Routing algorithm.
   */
  public static final IProperty<EdgeRoutingMode> EDGE_ROUTING_MODE = MrTreeMetaDataProvider.EDGE_ROUTING_MODE;

  /**
   * The index for the tree level the node is in
   */
  public static final IProperty<Integer> TREE_LEVEL = MrTreeMetaDataProvider.TREE_LEVEL;

  /**
   * Turns on Tree compaction which decreases the size of the whole tree by placing nodes of multiple
   * levels in one large level
   */
  public static final IProperty<Boolean> COMPACTION = MrTreeMetaDataProvider.COMPACTION;

  /**
   * Should be set to the length of the texture at the end of an edge.
   * This value can be used to improve the Edge Routing.
   */
  public static final IProperty<Double> EDGE_END_TEXTURE_LENGTH = MrTreeMetaDataProvider.EDGE_END_TEXTURE_LENGTH;

  /**
   * Layouter-specific algorithm factory.
   */
  public static class MrtreeFactory implements org.eclipse.elk.core.util.IFactory<AbstractLayoutProvider> {
    public AbstractLayoutProvider create() {
      AbstractLayoutProvider provider = new TreeLayoutProvider();
      provider.initialize("");
      return provider;
    }

    public void destroy(final AbstractLayoutProvider obj) {
      obj.dispose();
    }
  }

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutAlgorithmData.Builder()
        .id("org.eclipse.elk.mrtree")
        .name("ELK Mr. Tree")
        .description("Tree-based algorithm provided by the Eclipse Layout Kernel. Computes a spanning tree of the input graph and arranges all nodes according to the resulting parent-children hierarchy. I pity the fool who doesn\'t use Mr. Tree Layout.")
        .providerFactory(new MrtreeFactory())
        .category("org.eclipse.elk.tree")
        .melkBundleName(null)
        .definingBundleId("org.eclipse.elk.alg.mrtree")
        .imagePath("images/mrtree_layout.png")
        .supportedFeatures(EnumSet.of(GraphFeature.DISCONNECTED))
        .create()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.padding",
        PADDING_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.spacing.nodeNode",
        SPACING_NODE_NODE_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.spacing.edgeNode",
        SPACING_EDGE_NODE_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.aspectRatio",
        ASPECT_RATIO_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.priority",
        PRIORITY_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.separateConnectedComponents",
        SEPARATE_CONNECTED_COMPONENTS_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.debugMode",
        DEBUG_MODE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.direction",
        DIRECTION_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.interactive",
        INTERACTIVE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.interactiveLayout",
        INTERACTIVE_LAYOUT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.nodeSize.constraints",
        NODE_SIZE_CONSTRAINTS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.nodeSize.fixedGraphSize",
        NODE_SIZE_FIXED_GRAPH_SIZE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.nodeSize.minimum",
        NODE_SIZE_MINIMUM.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.nodeSize.options",
        NODE_SIZE_OPTIONS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.nodeLabels.placement",
        NODE_LABELS_PLACEMENT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.omitNodeMicroLayout",
        OMIT_NODE_MICRO_LAYOUT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.portLabels.placement",
        PORT_LABELS_PLACEMENT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.mrtree.weighting",
        WEIGHTING.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.mrtree.searchOrder",
        SEARCH_ORDER.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.topdownLayout",
        TOPDOWN_LAYOUT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.topdown.scaleFactor",
        TOPDOWN_SCALE_FACTOR.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.topdown.hierarchicalNodeWidth",
        TOPDOWN_HIERARCHICAL_NODE_WIDTH.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.topdown.hierarchicalNodeAspectRatio",
        TOPDOWN_HIERARCHICAL_NODE_ASPECT_RATIO.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.topdown.nodeType",
        TOPDOWN_NODE_TYPE_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.mrtree.positionConstraint",
        POSITION_CONSTRAINT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.mrtree.edgeRoutingMode",
        EDGE_ROUTING_MODE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.mrtree.treeLevel",
        TREE_LEVEL.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.mrtree.compaction",
        COMPACTION.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree",
        "org.eclipse.elk.mrtree.edgeEndTextureLength",
        EDGE_END_TEXTURE_LENGTH.getDefault()
    );
  }
}
