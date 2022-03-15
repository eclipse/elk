package org.eclipse.elk.alg.topdownpacking;

import java.util.EnumSet;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class TopdownpackingMetadataProvider implements ILayoutMetaDataProvider {
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
