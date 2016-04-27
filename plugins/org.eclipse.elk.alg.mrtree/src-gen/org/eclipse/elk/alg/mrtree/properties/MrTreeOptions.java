/**
 * Declarations for the ELK Tree layout algorithm.
 */
package org.eclipse.elk.alg.mrtree.properties;

import java.util.EnumSet;
import org.eclipse.elk.alg.mrtree.TreeLayoutProvider;
import org.eclipse.elk.alg.mrtree.properties.MrTreeMetaDataProvider;
import org.eclipse.elk.alg.mrtree.properties.OrderWeighting;
import org.eclipse.elk.alg.mrtree.properties.TreeifyingOrder;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.GraphFeature;
import org.eclipse.elk.core.util.AlgorithmFactory;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class MrTreeOptions implements ILayoutMetaDataProvider {
  /**
   * Default value for {@link #SPACING_NODE} with algorithm "ELK Mr. Tree".
   */
  private final static float SPACING_NODE_DEFAULT = 20;
  
  /**
   * Property constant to access Node Spacing from within the layout algorithm code.
   */
  public final static IProperty<Float> SPACING_NODE = new Property<Float>(
                                CoreOptions.SPACING_NODE,
                                SPACING_NODE_DEFAULT);
  
  /**
   * Default value for {@link #SPACING_BORDER} with algorithm "ELK Mr. Tree".
   */
  private final static float SPACING_BORDER_DEFAULT = 20;
  
  /**
   * Property constant to access Border Spacing from within the layout algorithm code.
   */
  public final static IProperty<Float> SPACING_BORDER = new Property<Float>(
                                CoreOptions.SPACING_BORDER,
                                SPACING_BORDER_DEFAULT);
  
  /**
   * Default value for {@link #ASPECT_RATIO} with algorithm "ELK Mr. Tree".
   */
  private final static float ASPECT_RATIO_DEFAULT = 1.6f;
  
  /**
   * Property constant to access Aspect Ratio from within the layout algorithm code.
   */
  public final static IProperty<Float> ASPECT_RATIO = new Property<Float>(
                                CoreOptions.ASPECT_RATIO,
                                ASPECT_RATIO_DEFAULT);
  
  /**
   * Default value for {@link #PRIORITY} with algorithm "ELK Mr. Tree".
   */
  private final static int PRIORITY_DEFAULT = 1;
  
  /**
   * Property constant to access Priority from within the layout algorithm code.
   */
  public final static IProperty<Integer> PRIORITY = new Property<Integer>(
                                CoreOptions.PRIORITY,
                                PRIORITY_DEFAULT);
  
  /**
   * Default value for {@link #SEPARATE_CONNECTED_COMPONENTS} with algorithm "ELK Mr. Tree".
   */
  private final static boolean SEPARATE_CONNECTED_COMPONENTS_DEFAULT = true;
  
  /**
   * Property constant to access Separate Connected Components from within the layout algorithm code.
   */
  public final static IProperty<Boolean> SEPARATE_CONNECTED_COMPONENTS = new Property<Boolean>(
                                CoreOptions.SEPARATE_CONNECTED_COMPONENTS,
                                SEPARATE_CONNECTED_COMPONENTS_DEFAULT);
  
  /**
   * Default value for {@link #DIRECTION} with algorithm "ELK Mr. Tree".
   */
  private final static Direction DIRECTION_DEFAULT = Direction.DOWN;
  
  /**
   * Property constant to access Direction from within the layout algorithm code.
   */
  public final static IProperty<Direction> DIRECTION = new Property<Direction>(
                                CoreOptions.DIRECTION,
                                DIRECTION_DEFAULT);
  
  /**
   * Property constant to access Debug Mode from within the layout algorithm code.
   */
  public final static IProperty<Boolean> DEBUG_MODE = CoreOptions.DEBUG_MODE;
  
  /**
   * Property constant to access Weighting of Nodes from within the layout algorithm code.
   */
  public final static IProperty<OrderWeighting> WEIGHTING = MrTreeMetaDataProvider.WEIGHTING;
  
  /**
   * Property constant to access Search Order from within the layout algorithm code.
   */
  public final static IProperty<TreeifyingOrder> SEARCH_ORDER = MrTreeMetaDataProvider.SEARCH_ORDER;
  
  public void apply(final ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutAlgorithmData(
        "org.eclipse.elk.mrtree.mrTree",
        "ELK Mr. Tree",
        "Tree-based algorithm provided by the Eclipse Layout Kernel. Computes a spanning tree of the input graph and arranges all nodes according to the resulting parent-children hierarchy. I pity the fool who doesn\'t use Mr. Tree Layout.",
        new AlgorithmFactory(TreeLayoutProvider.class, ""),
        "org.eclipse.elk.tree",
        null,
        "images/tree.png",
        EnumSet.of(GraphFeature.DISCONNECTED)
    ));
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree.mrTree",
        "org.eclipse.elk.spacing.node",
        SPACING_NODE_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree.mrTree",
        "org.eclipse.elk.spacing.border",
        SPACING_BORDER_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree.mrTree",
        "org.eclipse.elk.aspectRatio",
        ASPECT_RATIO_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree.mrTree",
        "org.eclipse.elk.priority",
        PRIORITY_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree.mrTree",
        "org.eclipse.elk.separateConnectedComponents",
        SEPARATE_CONNECTED_COMPONENTS_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree.mrTree",
        "org.eclipse.elk.direction",
        DIRECTION_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree.mrTree",
        "org.eclipse.elk.debugMode",
        DEBUG_MODE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree.mrTree",
        "org.eclipse.elk.mrtree.weighting",
        WEIGHTING.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.mrtree.mrTree",
        "org.eclipse.elk.mrtree.searchOrder",
        SEARCH_ORDER.getDefault()
    );
  }
}
