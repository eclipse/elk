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

  /**
   * Default value for {@link #NODE_RADIUS}.
   */
  private static final double NODE_RADIUS_DEFAULT = 25;

  /**
   * The distances of bend points around a node. A larger radius creates
   * greater curves and therefore increasing the graph size.
   * To small or to large sizes may cause problems.
   */
  public static final IProperty<Double> NODE_RADIUS = new Property<Double>(
            "org.eclipse.elk.alg.knot.nodeRadius",
            NODE_RADIUS_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #SHIFT_THRESHOLD}.
   */
  private static final double SHIFT_THRESHOLD_DEFAULT = 420;

  /**
   * Condition for the moving operation for nodes. Only nodes where the angles at
   * their four bend points is in sum below that threshold are allowed to move.
   * Keeping nodes from moving to much can improve the graph. Angle in degree.
   */
  public static final IProperty<Double> SHIFT_THRESHOLD = new Property<Double>(
            "org.eclipse.elk.alg.knot.shiftThreshold",
            SHIFT_THRESHOLD_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #DESIRED_NODE_DISTANCE}.
   */
  private static final double DESIRED_NODE_DISTANCE_DEFAULT = 25;

  /**
   * The distances between connected nodes that should be preserved.
   * Should be at minimum the same as the node radius.
   */
  public static final IProperty<Double> DESIRED_NODE_DISTANCE = new Property<Double>(
            "org.eclipse.elk.alg.knot.desiredNodeDistance",
            DESIRED_NODE_DISTANCE_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #SEPARATE_AXIS_ROTATION}.
   */
  private static final boolean SEPARATE_AXIS_ROTATION_DEFAULT = false;

  /**
   * Whether the outgoing edges and the incoming edges can be rotated separately.
   */
  public static final IProperty<Boolean> SEPARATE_AXIS_ROTATION = new Property<Boolean>(
            "org.eclipse.elk.alg.knot.separateAxisRotation",
            SEPARATE_AXIS_ROTATION_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #ROTATION_VALUE}.
   */
  private static final double ROTATION_VALUE_DEFAULT = 1;

  /**
   * Weighted step size for node rotations.
   */
  public static final IProperty<Double> ROTATION_VALUE = new Property<Double>(
            "org.eclipse.elk.alg.knot.rotationValue",
            ROTATION_VALUE_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #SHIFT_VALUE}.
   */
  private static final double SHIFT_VALUE_DEFAULT = 1;

  /**
   * Weighted step size for node shift movements.
   */
  public static final IProperty<Double> SHIFT_VALUE = new Property<Double>(
            "org.eclipse.elk.alg.knot.shiftValue",
            SHIFT_VALUE_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #ENABLE_ADDITIONAL_BEND_POINTS}.
   */
  private static final boolean ENABLE_ADDITIONAL_BEND_POINTS_DEFAULT = false;

  /**
   * Whether the algorithm is allowed to create new bend points in the middle of edges
   * and move them in order to relax angles at the outer bend points.
   * Helps creating larger curves.
   */
  public static final IProperty<Boolean> ENABLE_ADDITIONAL_BEND_POINTS = new Property<Boolean>(
            "org.eclipse.elk.alg.knot.enableAdditionalBendPoints",
            ENABLE_ADDITIONAL_BEND_POINTS_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #ADDITIONAL_BEND_POINTS_THRESHOLD}.
   */
  private static final double ADDITIONAL_BEND_POINTS_THRESHOLD_DEFAULT = 100;

  /**
   * When the bend point angles of an edge are below this threshold the edge
   * is considered overstretched. If additional bend points are enabled the algorithm
   * will add further bend points in the middle of edges when the outer bend points have
   * an angle below this threshold in degree .
   */
  public static final IProperty<Double> ADDITIONAL_BEND_POINTS_THRESHOLD = new Property<Double>(
            "org.eclipse.elk.alg.knot.additionalBendPointsThreshold",
            ADDITIONAL_BEND_POINTS_THRESHOLD_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #CURVE_HEIGHT}.
   */
  private static final double CURVE_HEIGHT_DEFAULT = 30;

  /**
   * Maximum distance that the additional bend points can distance themselves and therefore influences
   * the altitude of the curve they create.
   */
  public static final IProperty<Double> CURVE_HEIGHT = new Property<Double>(
            "org.eclipse.elk.alg.knot.curveHeight",
            CURVE_HEIGHT_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #CURVE_WIDTH_FACTOR}.
   */
  private static final double CURVE_WIDTH_FACTOR_DEFAULT = 0.2;

  /**
   * Scale factor for the distance of helper bend points towards the additional bend points. It influences
   * the width and pointedness of the curve they create.
   */
  public static final IProperty<Double> CURVE_WIDTH_FACTOR = new Property<Double>(
            "org.eclipse.elk.alg.knot.curveWidthFactor",
            CURVE_WIDTH_FACTOR_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #ITERATION_LIMIT}.
   */
  private static final int ITERATION_LIMIT_DEFAULT = 200;

  /**
   * Maximum number of iterations of each stress reducing process.
   */
  public static final IProperty<Integer> ITERATION_LIMIT = new Property<Integer>(
            "org.eclipse.elk.alg.knot.iterationLimit",
            ITERATION_LIMIT_DEFAULT,
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
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.knot.nodeRadius")
        .group("")
        .name("Node radius")
        .description("The distances of bend points around a node. A larger radius creates greater curves and therefore increasing the graph size. To small or to large sizes may cause problems.")
        .defaultValue(NODE_RADIUS_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.knot.shiftThreshold")
        .group("")
        .name("Shift threshold")
        .description("Condition for the moving operation for nodes. Only nodes where the angles at their four bend points is in sum below that threshold are allowed to move. Keeping nodes from moving to much can improve the graph. Angle in degree.")
        .defaultValue(SHIFT_THRESHOLD_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.knot.desiredNodeDistance")
        .group("")
        .name("Desired distance between nodes")
        .description("The distances between connected nodes that should be preserved. Should be at minimum the same as the node radius.")
        .defaultValue(DESIRED_NODE_DISTANCE_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.knot.separateAxisRotation")
        .group("")
        .name("Allow separate axis rotation")
        .description("Whether the outgoing edges and the incoming edges can be rotated separately.")
        .defaultValue(SEPARATE_AXIS_ROTATION_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.knot.rotationValue")
        .group("")
        .name("Rotation step size")
        .description("Weighted step size for node rotations.")
        .defaultValue(ROTATION_VALUE_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.knot.shiftValue")
        .group("")
        .name("Shift step size")
        .description("Weighted step size for node shift movements.")
        .defaultValue(SHIFT_VALUE_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.knot.enableAdditionalBendPoints")
        .group("")
        .name("Enable additional bend points")
        .description("Whether the algorithm is allowed to create new bend points in the middle of edges and move them in order to relax angles at the outer bend points. Helps creating larger curves.")
        .defaultValue(ENABLE_ADDITIONAL_BEND_POINTS_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.knot.additionalBendPointsThreshold")
        .group("")
        .name("Threshold for additional bend points")
        .description("When the bend point angles of an edge are below this threshold the edge is considered overstretched. If additional bend points are enabled the algorithm will add further bend points in the middle of edges when the outer bend points have an angle below this threshold in degree .")
        .defaultValue(ADDITIONAL_BEND_POINTS_THRESHOLD_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.knot.curveHeight")
        .group("")
        .name("Curve Height")
        .description("Maximum distance that the additional bend points can distance themselves and therefore influences the altitude of the curve they create.")
        .defaultValue(CURVE_HEIGHT_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.knot.curveWidthFactor")
        .group("")
        .name("Curve Width")
        .description("Scale factor for the distance of helper bend points towards the additional bend points. It influences the width and pointedness of the curve they create.")
        .defaultValue(CURVE_WIDTH_FACTOR_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.knot.iterationLimit")
        .group("")
        .name("Iteration Limit")
        .description("Maximum number of iterations of each stress reducing process.")
        .defaultValue(ITERATION_LIMIT_DEFAULT)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    new org.eclipse.elk.alg.knot.options.KnotOptions().apply(registry);
  }
}
