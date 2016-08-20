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
import org.eclipse.elk.alg.mrtree.properties.OrderWeighting;
import org.eclipse.elk.alg.mrtree.properties.TreeifyingOrder;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * Declarations for the ELK Tree layout algorithm.
 */
@SuppressWarnings("all")
public class MrTreeMetaDataProvider implements ILayoutMetaDataProvider {
  /**
   * Default value for {@link #WEIGHTING}.
   */
  private final static OrderWeighting WEIGHTING_DEFAULT = OrderWeighting.DESCENDANTS;
  
  /**
   * Which weighting to use when computing a node order.
   */
  public final static IProperty<OrderWeighting> WEIGHTING = new Property<OrderWeighting>(
            "org.eclipse.elk.mrtree.weighting",
            WEIGHTING_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #SEARCH_ORDER}.
   */
  private final static TreeifyingOrder SEARCH_ORDER_DEFAULT = TreeifyingOrder.DFS;
  
  /**
   * Which search order to use when computing a spanning tree.
   */
  public final static IProperty<TreeifyingOrder> SEARCH_ORDER = new Property<TreeifyingOrder>(
            "org.eclipse.elk.mrtree.searchOrder",
            SEARCH_ORDER_DEFAULT,
            null,
            null);
  
  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new org.eclipse.elk.core.data.LayoutOptionData(
        "org.eclipse.elk.mrtree.weighting",
        "",
        "Weighting of Nodes",
        "Which weighting to use when computing a node order.",
        WEIGHTING_DEFAULT,
        null,
        null,
        org.eclipse.elk.core.data.LayoutOptionData.Type.ENUM,
        OrderWeighting.class,
        EnumSet.of(org.eclipse.elk.core.data.LayoutOptionData.Target.PARENTS),
        org.eclipse.elk.core.data.LayoutOptionData.Visibility.VISIBLE
    ));
    registry.register(new org.eclipse.elk.core.data.LayoutOptionData(
        "org.eclipse.elk.mrtree.searchOrder",
        "",
        "Search Order",
        "Which search order to use when computing a spanning tree.",
        SEARCH_ORDER_DEFAULT,
        null,
        null,
        org.eclipse.elk.core.data.LayoutOptionData.Type.ENUM,
        TreeifyingOrder.class,
        EnumSet.of(org.eclipse.elk.core.data.LayoutOptionData.Target.PARENTS),
        org.eclipse.elk.core.data.LayoutOptionData.Visibility.VISIBLE
    ));
    new org.eclipse.elk.alg.mrtree.properties.MrTreeOptions().apply(registry);
  }
}
