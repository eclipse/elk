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
package org.eclipse.elk.alg.tree.properties;

import java.util.EnumSet;
import org.eclipse.elk.alg.tree.TreeLayoutProvider;
import org.eclipse.elk.alg.tree.properties.OrderWeighting;
import org.eclipse.elk.alg.tree.properties.TreeifyingOrder;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.GraphFeature;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.util.AlgorithmFactory;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * Declarations for the ELK Tree layout algorithm.
 */
@SuppressWarnings("all")
public class Properties implements ILayoutMetaDataProvider {
  /**
   * Default value for {@link #WEIGHTING}.
   */
  private final static OrderWeighting WEIGHTING_DEFAULT = OrderWeighting.DESCENDANTS;
  
  /**
   * Which weighting to use when computing a node order.
   */
  public final static IProperty<OrderWeighting> WEIGHTING = new Property<OrderWeighting>(
            "org.eclipse.elk.alg.tree.weighting",
            WEIGHTING_DEFAULT);
  
  /**
   * Default value for {@link #SEARCH_ORDER}.
   */
  private final static TreeifyingOrder SEARCH_ORDER_DEFAULT = TreeifyingOrder.DFS;
  
  /**
   * Which search order to use when computing a spanning tree.
   */
  public final static IProperty<TreeifyingOrder> SEARCH_ORDER = new Property<TreeifyingOrder>(
            "org.eclipse.elk.alg.tree.searchOrder",
            SEARCH_ORDER_DEFAULT);
  
  /**
   * Default value for {@link #SPACING} with algorithm "ELK Mr. Tree".
   */
  private final static float MR_TREE_SUP_SPACING = 20;
  
  /**
   * Overridden value for Spacing.
   */
  public final static IProperty<Float> SPACING = new Property<Float>(
            LayoutOptions.SPACING,
            MR_TREE_SUP_SPACING);
  
  /**
   * Default value for {@link #BORDER_SPACING} with algorithm "ELK Mr. Tree".
   */
  private final static float MR_TREE_SUP_BORDER_SPACING = 20;
  
  /**
   * Overridden value for Border Spacing.
   */
  public final static IProperty<Float> BORDER_SPACING = new Property<Float>(
            LayoutOptions.BORDER_SPACING,
            MR_TREE_SUP_BORDER_SPACING);
  
  /**
   * Default value for {@link #ASPECT_RATIO} with algorithm "ELK Mr. Tree".
   */
  private final static float MR_TREE_SUP_ASPECT_RATIO = 1.6f;
  
  /**
   * Overridden value for Aspect Ratio.
   */
  public final static IProperty<Float> ASPECT_RATIO = new Property<Float>(
            LayoutOptions.ASPECT_RATIO,
            MR_TREE_SUP_ASPECT_RATIO);
  
  /**
   * Default value for {@link #PRIORITY} with algorithm "ELK Mr. Tree".
   */
  private final static int MR_TREE_SUP_PRIORITY = 1;
  
  /**
   * Overridden value for Priority.
   */
  public final static IProperty<Integer> PRIORITY = new Property<Integer>(
            LayoutOptions.PRIORITY,
            MR_TREE_SUP_PRIORITY);
  
  /**
   * Default value for {@link #SEPARATE_CONN_COMP} with algorithm "ELK Mr. Tree".
   */
  private final static boolean MR_TREE_SUP_SEPARATE_CONN_COMP = true;
  
  /**
   * Default value for {@link #DIRECTION} with algorithm "ELK Mr. Tree".
   */
  private final static Direction MR_TREE_SUP_DIRECTION = Direction.DOWN;
  
  public void apply(final ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.tree.weighting",
        "Weighting of Nodes",
        "Which weighting to use when computing a node order.",
        WEIGHTING_DEFAULT,
        OrderWeighting.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
    ));
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.alg.tree.searchOrder",
        "Search Order",
        "Which search order to use when computing a spanning tree.",
        SEARCH_ORDER_DEFAULT,
        TreeifyingOrder.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS),
        LayoutOptionData.Visibility.VISIBLE
    ));
    registry.register(new LayoutAlgorithmData(
        "org.eclipse.elk.alg.tree.MrTree",
        "ELK Mr. Tree",
        "Tree-based algorithm provided by the Eclipse Layout Kernel. Computes a spanning tree of the input graph and arranges all nodes according to the resulting parent-children hierarchy. I pity the fool who doesn\'t use Mr. Tree Layout.",
        new AlgorithmFactory(TreeLayoutProvider.class, ""),
        "org.eclipse.elk.Tree",
        null,
        "images/tree.png",
        EnumSet.of(GraphFeature.DISCONNECTED)
    ));
    registry.addOptionSupport(
        "org.eclipse.elk.alg.tree.MrTree",
        "org.eclipse.elk.spacing",
        MR_TREE_SUP_SPACING
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.tree.MrTree",
        "org.eclipse.elk.borderSpacing",
        MR_TREE_SUP_BORDER_SPACING
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.tree.MrTree",
        "org.eclipse.elk.aspectRatio",
        MR_TREE_SUP_ASPECT_RATIO
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.tree.MrTree",
        "org.eclipse.elk.priority",
        MR_TREE_SUP_PRIORITY
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.tree.MrTree",
        "org.eclipse.elk.separateConnComp",
        MR_TREE_SUP_SEPARATE_CONN_COMP
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.tree.MrTree",
        "org.eclipse.elk.direction",
        MR_TREE_SUP_DIRECTION
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.tree.MrTree",
        "org.eclipse.elk.debugMode",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.tree.MrTree",
        "org.eclipse.elk.alg.tree.weighting",
        null
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.tree.MrTree",
        "org.eclipse.elk.alg.tree.searchOrder",
        null
    );
  }
}
