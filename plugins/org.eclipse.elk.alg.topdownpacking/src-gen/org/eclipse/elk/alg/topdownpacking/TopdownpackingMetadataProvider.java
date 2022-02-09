package org.eclipse.elk.alg.topdownpacking;

import java.util.EnumSet;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class TopdownpackingMetadataProvider implements ILayoutMetaDataProvider {
  /**
   * Default value for {@link #DESIRED_WIDTH}.
   */
  private static final int DESIRED_WIDTH_DEFAULT = 300;
  
  /**
   * Lower bound value for {@link #DESIRED_WIDTH}.
   */
  private static final Comparable<? super Integer> DESIRED_WIDTH_LOWER_BOUND = Integer.valueOf(0);
  
  /**
   * Used to define the dimensions of the parent graph to constrain the layout space.
   */
  public static final IProperty<Integer> DESIRED_WIDTH = new Property<Integer>(
            "org.eclipse.elk.alg.topdownpacking.desiredWidth",
            DESIRED_WIDTH_DEFAULT,
            DESIRED_WIDTH_LOWER_BOUND,
            null);
  
  /**
   * Default value for {@link #DESIRED_ASPECT_RATIO}.
   */
  private static final double DESIRED_ASPECT_RATIO_DEFAULT = 1.5;
  
  /**
   * Desired aspect ratio for the nodes that are being laid out. To determine the size to apply to nodes.
   * TODO: decide whether this should be set on nodes individually or globally.
   */
  public static final IProperty<Double> DESIRED_ASPECT_RATIO = new Property<Double>(
            "org.eclipse.elk.alg.topdownpacking.desiredAspectRatio",
            DESIRED_ASPECT_RATIO_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #GRID_DIMENSION}.
   */
  private static final int GRID_DIMENSION_DEFAULT = 1;
  
  /**
   * This algorithm places nodes within a dynamically created grid. This value provides the width and height of that
   * grid and is necessary to correctly compute the scale factor later.
   * TODO: This is the simple case where all nodes have the same size and the grid has the same number of rows and columns.
   * Eventually we want more flexibility here and will need to adjust the scale calculation accordingly.
   */
  public static final IProperty<Integer> GRID_DIMENSION = new Property<Integer>(
            "org.eclipse.elk.alg.topdownpacking.gridDimension",
            GRID_DIMENSION_DEFAULT,
            null,
            null);
  
  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.topdownpacking.desiredWidth")
        .group("")
        .name("Desired or externally set width of a node")
        .description("Used to define the dimensions of the parent graph to constrain the layout space.")
        .defaultValue(DESIRED_WIDTH_DEFAULT)
        .lowerBound(DESIRED_WIDTH_LOWER_BOUND)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.topdownpacking.desiredAspectRatio")
        .group("")
        .name("Desired aspect ratio")
        .description("Desired aspect ratio for the nodes that are being laid out. To determine the size to apply to nodes. TODO: decide whether this should be set on nodes individually or globally.")
        .defaultValue(DESIRED_ASPECT_RATIO_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.topdownpacking.gridDimension")
        .group("")
        .name("Number of child nodes in one row and column")
        .description("This algorithm places nodes within a dynamically created grid. This value provides the width and height of that grid and is necessary to correctly compute the scale factor later. TODO: This is the simple case where all nodes have the same size and the grid has the same number of rows and columns. Eventually we want more flexibility here and will need to adjust the scale calculation accordingly.")
        .defaultValue(GRID_DIMENSION_DEFAULT)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
    new org.eclipse.elk.alg.topdownpacking.options.TopdownpackingOptions().apply(registry);
  }
}
