package org.eclipse.elk.alg.knot;

import java.util.EnumSet;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class KnotMetadataProvider implements ILayoutMetaDataProvider {
  /**
   * Default value for {@link #REVERSE_INPUT}.
   */
  private static final boolean REVERSE_INPUT_DEFAULT = false;

  /**
   * True if nodes should be placed in reverse order of their
   * appearance in the graph.
   */
  public static final IProperty<Boolean> REVERSE_INPUT = new Property<Boolean>(
            "org.eclipse.elk.alg.knot.reverseInput",
            REVERSE_INPUT_DEFAULT,
            null,
            null);

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.knot.reverseInput")
        .group("")
        .name("Reverse Input")
        .description("True if nodes should be placed in reverse order of their appearance in the graph.")
        .defaultValue(REVERSE_INPUT_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    new org.eclipse.elk.alg.knot.options.KnotOptions().apply(registry);
  }
}
