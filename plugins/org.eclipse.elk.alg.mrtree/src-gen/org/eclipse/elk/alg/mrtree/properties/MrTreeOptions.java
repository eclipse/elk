/**
 * Copyright (c) 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    spoenemann - initial API and implementation
 */
package org.eclipse.elk.alg.mrtree.properties;

import java.util.EnumSet;
import org.eclipse.elk.alg.mrtree.TreeLayoutProvider;
import org.eclipse.elk.alg.mrtree.properties.OrderWeighting;
import org.eclipse.elk.alg.mrtree.properties.TreeifyingOrder;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.GraphFeature;
import org.eclipse.elk.core.util.AlgorithmFactory;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * Declarations for the ELK Tree layout algorithm.
 */
@SuppressWarnings("all")
public class MrTreeOptions implements ILayoutMetaDataProvider {
  /**
   * Default value for {@link #WEIGHTING}.
   */
  private final static OrderWeighting WEIGHTING_DEFAULT = OrderWeighting.DESCENDANTS;
  
  /**
   * Which weighting to use when computing a node order.
   */
  public final static IProperty<OrderWeighting> WEIGHTING = new Property<OrderWeighting>(
            "org.eclipse.elk.alg.mrtree.weighting",
            WEIGHTING_DEFAULT);
  
  /**
   * Default value for {@link #SEARCH_ORDER}.
   */
  private final static TreeifyingOrder SEARCH_ORDER_DEFAULT = TreeifyingOrder.DFS;
  
  /**
   * Which search order to use when computing a spanning tree.
   */
  public final static IProperty<TreeifyingOrder> SEARCH_ORDER = new Property<TreeifyingOrder>(
            "org.eclipse.elk.alg.mrtree.searchOrder",
            SEARCH_ORDER_DEFAULT);
  
  /**
   * Default value for {@link #SPACING_NODE} with algorithm "ELK Mr. Tree".
   */
  private final static float MR_TREE_SUP_SPACING_NODE = 20;
  
  /**
   * Overridden value for Node Spacing.
   */
  public final static IProperty<Float> SPACING_NODE = new Property<Float>(
            CoreOptions.SPACING_NODE,
            MR_TREE_SUP_SPACING_NODE);
  
  /**
   * Default value for {@link #SPACING_BORDER} with algorithm "ELK Mr. Tree".
   */
  private final static float MR_TREE_SUP_SPACING_BORDER = 20;
  
  /**
   * Overridden value for Border Spacing.
   */
  public final static IProperty<Float> SPACING_BORDER = new Property<Float>(
            CoreOptions.SPACING_BORDER,
            MR_TREE_SUP_SPACING_BORDER);
  
  /**
   * Default value for {@link #ASPECT_RATIO} with algorithm "ELK Mr. Tree".
   */
  private final static float MR_TREE_SUP_ASPECT_RATIO = 1.6f;
  
  /**
   * Overridden value for Aspect Ratio.
   */
  public final static IProperty<Float> ASPECT_RATIO = new Property<Float>(
            CoreOptions.ASPECT_RATIO,
            MR_TREE_SUP_ASPECT_RATIO);
  
  /**
   * Default value for {@link #PRIORITY} with algorithm "ELK Mr. Tree".
   */
  private final static int MR_TREE_SUP_PRIORITY = 1;
  
  /**
   * Overridden value for Priority.
   */
  public final static IProperty<Integer> PRIORITY = new Property<Integer>(
            CoreOptions.PRIORITY,
            MR_TREE_SUP_PRIORITY);
  
  /**
   * Default value for {@link #SEPARATE_CONNECTED_COMPONENTS} with algorithm "ELK Mr. Tree".
   */
  private final static boolean MR_TREE_SUP_SEPARATE_CONNECTED_COMPONENTS = true;
  
  /**
   * Default value for {@link #DIRECTION} with algorithm "ELK Mr. Tree".
   */
  private final static Direction MR_TREE_SUP_DIRECTION = Direction.DOWN;
  
  public void apply(final ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.mrtree.weighting",
        "",
        "Weighting of Nodes",
        "Which weighting to use when computing a node order.",
        WEIGHTING_DEFAULT,
        LayoutOptionData.Type.ENUM,
        OrderWeighting.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.mrtree.searchOrder",
        "",
        "Search Order",
        "Which search order to use when computing a spanning tree.",
        SEARCH_ORDER_DEFAULT,
        LayoutOptionData.Type.ENUM,
        TreeifyingOrder.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
    ));
    registry.register(new LayoutAlgorithmData(
        "org.eclipse.elk.alg.mrtree.mrTree",
        "ELK Mr. Tree",
        "Tree-based algorithm provided by the Eclipse Layout Kernel. Computes a spanning tree of the input graph and arranges all nodes according to the resulting parent-children hierarchy. I pity the fool who doesn\'t use Mr. Tree Layout.",
        new AlgorithmFactory(TreeLayoutProvider.class, ""),
        "org.eclipse.elk.tree",
        null,
        "images/tree.png",
        EnumSet.of(GraphFeature.DISCONNECTED)
    ));
    registry.addOptionSupport(
        "org.eclipse.elk.alg.mrtree.mrTree",
        "org.eclipse.elk.spacing.node",
        MR_TREE_SUP_SPACING_NODE
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.mrtree.mrTree",
        "org.eclipse.elk.spacing.border",
        MR_TREE_SUP_SPACING_BORDER
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.mrtree.mrTree",
        "org.eclipse.elk.aspectRatio",
        MR_TREE_SUP_ASPECT_RATIO
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.mrtree.mrTree",
        "org.eclipse.elk.priority",
        MR_TREE_SUP_PRIORITY
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.mrtree.mrTree",
        "org.eclipse.elk.separateConnectedComponents",
        MR_TREE_SUP_SEPARATE_CONNECTED_COMPONENTS
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.mrtree.mrTree",
        "org.eclipse.elk.direction",
        MR_TREE_SUP_DIRECTION
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.mrtree.mrTree",
        "org.eclipse.elk.debugMode",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.mrtree.mrTree",
        "org.eclipse.elk.alg.mrtree.weighting",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.mrtree.mrTree",
        "org.eclipse.elk.alg.mrtree.searchOrder",
        null
    );
  }
}
