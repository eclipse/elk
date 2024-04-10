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
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * Declarations for the ELK Tree layout algorithm.
 */
@SuppressWarnings("all")
public class MrTreeMetaDataProvider implements ILayoutMetaDataProvider {
  /**
   * Default value for {@link #COMPACTION}.
   */
  private static final boolean COMPACTION_DEFAULT = false;

  /**
   * Turns on Tree compaction which decreases the size of the whole tree by placing nodes of multiple
   * levels in one large level
   */
  public static final IProperty<Boolean> COMPACTION = new Property<Boolean>(
            "org.eclipse.elk.mrtree.compaction",
            COMPACTION_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #EDGE_END_TEXTURE_LENGTH}.
   */
  private static final double EDGE_END_TEXTURE_LENGTH_DEFAULT = 7;

  /**
   * Should be set to the length of the texture at the end of an edge.
   * This value can be used to improve the Edge Routing.
   */
  public static final IProperty<Double> EDGE_END_TEXTURE_LENGTH = new Property<Double>(
            "org.eclipse.elk.mrtree.edgeEndTextureLength",
            EDGE_END_TEXTURE_LENGTH_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #TREE_LEVEL}.
   */
  private static final int TREE_LEVEL_DEFAULT = 0;

  /**
   * Lower bound value for {@link #TREE_LEVEL}.
   */
  private static final Comparable<? super Integer> TREE_LEVEL_LOWER_BOUND = Integer.valueOf(0);

  /**
   * The index for the tree level the node is in
   */
  public static final IProperty<Integer> TREE_LEVEL = new Property<Integer>(
            "org.eclipse.elk.mrtree.treeLevel",
            TREE_LEVEL_DEFAULT,
            TREE_LEVEL_LOWER_BOUND,
            null);

  /**
   * Default value for {@link #POSITION_CONSTRAINT}.
   */
  private static final int POSITION_CONSTRAINT_DEFAULT = (-1);

  /**
   * When set to a positive number this option will force the algorithm to place the node to the
   * specified position within the trees layer if weighting is set to constraint
   */
  public static final IProperty<Integer> POSITION_CONSTRAINT = new Property<Integer>(
            "org.eclipse.elk.mrtree.positionConstraint",
            POSITION_CONSTRAINT_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #WEIGHTING}.
   */
  private static final OrderWeighting WEIGHTING_DEFAULT = OrderWeighting.MODEL_ORDER;

  /**
   * Which weighting to use when computing a node order.
   */
  public static final IProperty<OrderWeighting> WEIGHTING = new Property<OrderWeighting>(
            "org.eclipse.elk.mrtree.weighting",
            WEIGHTING_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #EDGE_ROUTING_MODE}.
   */
  private static final EdgeRoutingMode EDGE_ROUTING_MODE_DEFAULT = EdgeRoutingMode.AVOID_OVERLAP;

  /**
   * Chooses an Edge Routing algorithm.
   */
  public static final IProperty<EdgeRoutingMode> EDGE_ROUTING_MODE = new Property<EdgeRoutingMode>(
            "org.eclipse.elk.mrtree.edgeRoutingMode",
            EDGE_ROUTING_MODE_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #SEARCH_ORDER}.
   */
  private static final TreeifyingOrder SEARCH_ORDER_DEFAULT = TreeifyingOrder.DFS;

  /**
   * Which search order to use when computing a spanning tree.
   */
  public static final IProperty<TreeifyingOrder> SEARCH_ORDER = new Property<TreeifyingOrder>(
            "org.eclipse.elk.mrtree.searchOrder",
            SEARCH_ORDER_DEFAULT,
            null,
            null);

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.mrtree.compaction")
        .group("")
        .name("Position Constraint")
        .description("Turns on Tree compaction which decreases the size of the whole tree by placing nodes of multiple levels in one large level")
        .defaultValue(COMPACTION_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.mrtree.edgeEndTextureLength")
        .group("")
        .name("Edge End Texture Length")
        .description("Should be set to the length of the texture at the end of an edge. This value can be used to improve the Edge Routing.")
        .defaultValue(EDGE_END_TEXTURE_LENGTH_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.mrtree.treeLevel")
        .group("")
        .name("Tree Level")
        .description("The index for the tree level the node is in")
        .defaultValue(TREE_LEVEL_DEFAULT)
        .lowerBound(TREE_LEVEL_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.mrtree.positionConstraint")
        .group("")
        .name("Position Constraint")
        .description("When set to a positive number this option will force the algorithm to place the node to the specified position within the trees layer if weighting is set to constraint")
        .defaultValue(POSITION_CONSTRAINT_DEFAULT)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.mrtree.weighting")
        .group("")
        .name("Weighting of Nodes")
        .description("Which weighting to use when computing a node order.")
        .defaultValue(WEIGHTING_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(OrderWeighting.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.mrtree.edgeRoutingMode")
        .group("")
        .name("Edge Routing Mode")
        .description("Chooses an Edge Routing algorithm.")
        .defaultValue(EDGE_ROUTING_MODE_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(EdgeRoutingMode.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.mrtree.searchOrder")
        .group("")
        .name("Search Order")
        .description("Which search order to use when computing a spanning tree.")
        .defaultValue(SEARCH_ORDER_DEFAULT)
        .type(LayoutOptionData.Type.ENUM)
        .optionClass(TreeifyingOrder.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    new org.eclipse.elk.alg.mrtree.options.MrTreeOptions().apply(registry);
  }
}
