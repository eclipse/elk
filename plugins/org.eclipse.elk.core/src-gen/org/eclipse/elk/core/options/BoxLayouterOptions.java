/**
 * Core definitions of the Eclipse Layout Kernel.
 */
package org.eclipse.elk.core.options;

import java.util.EnumSet;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.core.util.AlgorithmFactory;
import org.eclipse.elk.core.util.BoxLayoutProvider;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class BoxLayouterOptions implements ILayoutMetaDataProvider {
  /**
   * Default value for {@link #SPACING_NODE} with algorithm "Box Layout".
   */
  private final static float SPACING_NODE_DEFAULT = 15;
  
  /**
   * Property constant to access Node Spacing from within the layout algorithm code.
   */
  public final static IProperty<Float> SPACING_NODE = new Property<Float>(
                                CoreOptions.SPACING_NODE,
                                SPACING_NODE_DEFAULT);
  
  /**
   * Default value for {@link #SPACING_BORDER} with algorithm "Box Layout".
   */
  private final static float SPACING_BORDER_DEFAULT = 15;
  
  /**
   * Property constant to access Border Spacing from within the layout algorithm code.
   */
  public final static IProperty<Float> SPACING_BORDER = new Property<Float>(
                                CoreOptions.SPACING_BORDER,
                                SPACING_BORDER_DEFAULT);
  
  /**
   * Default value for {@link #PRIORITY} with algorithm "Box Layout".
   */
  private final static int PRIORITY_DEFAULT = 0;
  
  /**
   * Property constant to access Priority from within the layout algorithm code.
   */
  public final static IProperty<Integer> PRIORITY = new Property<Integer>(
                                CoreOptions.PRIORITY,
                                PRIORITY_DEFAULT);
  
  /**
   * Property constant to access Expand Nodes from within the layout algorithm code.
   */
  public final static IProperty<Boolean> EXPAND_NODES = CoreOptions.EXPAND_NODES;
  
  /**
   * Property constant to access Node Size Constraints from within the layout algorithm code.
   */
  public final static IProperty<EnumSet<SizeConstraint>> NODE_SIZE_CONSTRAINTS = CoreOptions.NODE_SIZE_CONSTRAINTS;
  
  /**
   * Property constant to access Node Size Options from within the layout algorithm code.
   */
  public final static IProperty<EnumSet<SizeOptions>> NODE_SIZE_OPTIONS = CoreOptions.NODE_SIZE_OPTIONS;
  
  /**
   * Default value for {@link #ASPECT_RATIO} with algorithm "Box Layout".
   */
  private final static float ASPECT_RATIO_DEFAULT = 1.3f;
  
  /**
   * Property constant to access Aspect Ratio from within the layout algorithm code.
   */
  public final static IProperty<Float> ASPECT_RATIO = new Property<Float>(
                                CoreOptions.ASPECT_RATIO,
                                ASPECT_RATIO_DEFAULT);
  
  /**
   * Property constant to access Interactive from within the layout algorithm code.
   */
  public final static IProperty<Boolean> INTERACTIVE = CoreOptions.INTERACTIVE;
  
  public void apply(final ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutAlgorithmData(
        "org.eclipse.elk.box",
        "Box Layout",
        "Algorithm for packing of unconnected boxes, i.e. graphs without edges.",
        new AlgorithmFactory(BoxLayoutProvider.class, ""),
        null,
        "ELK",
        "images/box_layout.png",
        null
    ));
    registry.addOptionSupport(
        "org.eclipse.elk.box",
        "org.eclipse.elk.spacing.node",
        SPACING_NODE_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.box",
        "org.eclipse.elk.spacing.border",
        SPACING_BORDER_DEFAULT
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
  }
}
