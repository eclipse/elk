/**
 * Copyright (c) 2016, 2023 Kiel University, Primetals Technologies Austria GmbH and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.elk.alg.libavoid.options;

import java.util.EnumSet;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutCategoryData;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class LibavoidMetaDataProvider implements ILayoutMetaDataProvider {
  /**
   * Default value for {@link #SEGMENT_PENALTY}.
   */
  private static final double SEGMENT_PENALTY_DEFAULT = 10;

  /**
   * This penalty is applied for each segment in the connector path beyond the first.
   * This should always normally be set when doing orthogonal routing to prevent
   * step-like connector paths.
   */
  public static final IProperty<Double> SEGMENT_PENALTY = new Property<Double>(
            "org.eclipse.elk.alg.libavoid.segmentPenalty",
            SEGMENT_PENALTY_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #ANGLE_PENALTY}.
   */
  private static final double ANGLE_PENALTY_DEFAULT = 0;

  /**
   * This penalty is applied in its full amount to tight acute bends in the connector path.
   * A smaller portion of the penalty is applied for slight bends, i.e., where the bend is close
   * to 180 degrees. This is useful for polyline routing where there is some evidence that tighter
   * corners are worse for readability, but that slight bends might not be so bad,
   * especially when smoothed by curves.
   */
  public static final IProperty<Double> ANGLE_PENALTY = new Property<Double>(
            "org.eclipse.elk.alg.libavoid.anglePenalty",
            ANGLE_PENALTY_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #CROSSING_PENALTY}.
   */
  private static final double CROSSING_PENALTY_DEFAULT = 0;

  /**
   * This penalty is applied whenever a connector path crosses another connector path.
   * It takes shared paths into consideration and the penalty is only applied
   * if there is an actual crossing.
   */
  public static final IProperty<Double> CROSSING_PENALTY = new Property<Double>(
            "org.eclipse.elk.alg.libavoid.crossingPenalty",
            CROSSING_PENALTY_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #CLUSTER_CROSSING_PENALTY}.
   */
  private static final double CLUSTER_CROSSING_PENALTY_DEFAULT = 0;

  /**
   * This penalty is applied whenever a connector path crosses a cluster boundary.
   */
  public static final IProperty<Double> CLUSTER_CROSSING_PENALTY = new Property<Double>(
            "org.eclipse.elk.alg.libavoid.clusterCrossingPenalty",
            CLUSTER_CROSSING_PENALTY_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #FIXED_SHARED_PATH_PENALTY}.
   */
  private static final double FIXED_SHARED_PATH_PENALTY_DEFAULT = 0;

  /**
   * This penalty is applied whenever a connector path shares some segments with an immovable
   * portion of an existing connector route (such as the first or last segment of a connector).
   */
  public static final IProperty<Double> FIXED_SHARED_PATH_PENALTY = new Property<Double>(
            "org.eclipse.elk.alg.libavoid.fixedSharedPathPenalty",
            FIXED_SHARED_PATH_PENALTY_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #PORT_DIRECTION_PENALTY}.
   */
  private static final double PORT_DIRECTION_PENALTY_DEFAULT = 0;

  /**
   * This penalty is applied to port selection choice when the other end of the connector
   * being routed does not appear in any of the 90 degree visibility cones centered on the
   * visibility directions for the port.
   */
  public static final IProperty<Double> PORT_DIRECTION_PENALTY = new Property<Double>(
            "org.eclipse.elk.alg.libavoid.portDirectionPenalty",
            PORT_DIRECTION_PENALTY_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #SHAPE_BUFFER_DISTANCE}.
   */
  private static final double SHAPE_BUFFER_DISTANCE_DEFAULT = 4;

  /**
   * This parameter defines the spacing distance that will be added to the sides of each
   * shape when determining obstacle sizes for routing. This controls how closely connectors
   * pass shapes, and can be used to prevent connectors overlapping with shape boundaries.
   */
  public static final IProperty<Double> SHAPE_BUFFER_DISTANCE = new Property<Double>(
            "org.eclipse.elk.alg.libavoid.shapeBufferDistance",
            SHAPE_BUFFER_DISTANCE_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #IDEAL_NUDGING_DISTANCE}.
   */
  private static final double IDEAL_NUDGING_DISTANCE_DEFAULT = 4;

  /**
   * This parameter defines the spacing distance that will be used for nudging apart
   * overlapping corners and line segments of connectors.
   */
  public static final IProperty<Double> IDEAL_NUDGING_DISTANCE = new Property<Double>(
            "org.eclipse.elk.alg.libavoid.idealNudgingDistance",
            IDEAL_NUDGING_DISTANCE_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #REVERSE_DIRECTION_PENALTY}.
   */
  private static final double REVERSE_DIRECTION_PENALTY_DEFAULT = 0;

  /**
   * This penalty is applied whenever a connector path travels in the direction opposite
   * of the destination from the source endpoint. By default this penalty is set to zero.
   * This shouldn't be needed in most cases but can be useful if you use penalties such as
   * crossingPenalty which cause connectors to loop around obstacles.
   */
  public static final IProperty<Double> REVERSE_DIRECTION_PENALTY = new Property<Double>(
            "org.eclipse.elk.alg.libavoid.reverseDirectionPenalty",
            REVERSE_DIRECTION_PENALTY_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #NUDGE_ORTHOGONAL_SEGMENTS_CONNECTED_TO_SHAPES}.
   */
  private static final boolean NUDGE_ORTHOGONAL_SEGMENTS_CONNECTED_TO_SHAPES_DEFAULT = false;

  /**
   * This option causes the final segments of connectors, which are attached to shapes,
   * to be nudged apart. Usually these segments are fixed, since they are considered to be
   * attached to ports.
   */
  public static final IProperty<Boolean> NUDGE_ORTHOGONAL_SEGMENTS_CONNECTED_TO_SHAPES = new Property<Boolean>(
            "org.eclipse.elk.alg.libavoid.nudgeOrthogonalSegmentsConnectedToShapes",
            NUDGE_ORTHOGONAL_SEGMENTS_CONNECTED_TO_SHAPES_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #IMPROVE_HYPEREDGE_ROUTES_MOVING_JUNCTIONS}.
   */
  private static final boolean IMPROVE_HYPEREDGE_ROUTES_MOVING_JUNCTIONS_DEFAULT = true;

  /**
   * This option causes hyperedge routes to be locally improved fixing obviously bad paths.
   * As part of this process libavoid will effectively move junctions, setting new ideal positions
   * for each junction.
   */
  public static final IProperty<Boolean> IMPROVE_HYPEREDGE_ROUTES_MOVING_JUNCTIONS = new Property<Boolean>(
            "org.eclipse.elk.alg.libavoid.improveHyperedgeRoutesMovingJunctions",
            IMPROVE_HYPEREDGE_ROUTES_MOVING_JUNCTIONS_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #PENALISE_ORTHOGONAL_SHARED_PATHS_AT_CONN_ENDS}.
   */
  private static final boolean PENALISE_ORTHOGONAL_SHARED_PATHS_AT_CONN_ENDS_DEFAULT = false;

  /**
   * This option penalises and attempts to reroute orthogonal shared connector paths terminating
   * at a common junction or shape connection pin. When multiple connector paths enter or leave
   * the same side of a junction (or shape pin), the router will attempt to reroute these to
   * different sides of the junction or different shape pins. This option depends on the
   * fixedSharedPathPenalty penalty having been set.
   */
  public static final IProperty<Boolean> PENALISE_ORTHOGONAL_SHARED_PATHS_AT_CONN_ENDS = new Property<Boolean>(
            "org.eclipse.elk.alg.libavoid.penaliseOrthogonalSharedPathsAtConnEnds",
            PENALISE_ORTHOGONAL_SHARED_PATHS_AT_CONN_ENDS_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #NUDGE_ORTHOGONAL_TOUCHING_COLINEAR_SEGMENTS}.
   */
  private static final boolean NUDGE_ORTHOGONAL_TOUCHING_COLINEAR_SEGMENTS_DEFAULT = false;

  /**
   * This option can be used to control whether colinear line segments that touch just at
   * their ends will be nudged apart. The overlap will usually be resolved in the other dimension,
   * so this is not usually required.
   */
  public static final IProperty<Boolean> NUDGE_ORTHOGONAL_TOUCHING_COLINEAR_SEGMENTS = new Property<Boolean>(
            "org.eclipse.elk.alg.libavoid.nudgeOrthogonalTouchingColinearSegments",
            NUDGE_ORTHOGONAL_TOUCHING_COLINEAR_SEGMENTS_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #PERFORM_UNIFYING_NUDGING_PREPROCESSING_STEP}.
   */
  private static final boolean PERFORM_UNIFYING_NUDGING_PREPROCESSING_STEP_DEFAULT = true;

  /**
   * This option can be used to control whether the router performs a preprocessing step before
   * orthogonal nudging where is tries to unify segments and centre them in free space.
   * This generally results in better quality ordering and nudging.
   */
  public static final IProperty<Boolean> PERFORM_UNIFYING_NUDGING_PREPROCESSING_STEP = new Property<Boolean>(
            "org.eclipse.elk.alg.libavoid.performUnifyingNudgingPreprocessingStep",
            PERFORM_UNIFYING_NUDGING_PREPROCESSING_STEP_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #IMPROVE_HYPEREDGE_ROUTES_MOVING_ADDING_AND_DELETING_JUNCTIONS}.
   */
  private static final boolean IMPROVE_HYPEREDGE_ROUTES_MOVING_ADDING_AND_DELETING_JUNCTIONS_DEFAULT = false;

  /**
   * This option causes hyperedge routes to be locally improved fixing obviously bad paths.
   * It can cause junctions and connectors to be added or removed from hyperedges. As part of
   * this process libavoid will effectively move junctions by setting new ideal positions for
   * each remaining or added junction. If set, this option overrides the
   * improveHyperedgeRoutesMovingJunctions option.
   */
  public static final IProperty<Boolean> IMPROVE_HYPEREDGE_ROUTES_MOVING_ADDING_AND_DELETING_JUNCTIONS = new Property<Boolean>(
            "org.eclipse.elk.alg.libavoid.improveHyperedgeRoutesMovingAddingAndDeletingJunctions",
            IMPROVE_HYPEREDGE_ROUTES_MOVING_ADDING_AND_DELETING_JUNCTIONS_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #NUDGE_SHARED_PATHS_WITH_COMMON_END_POINT}.
   */
  private static final boolean NUDGE_SHARED_PATHS_WITH_COMMON_END_POINT_DEFAULT = true;

  /**
   * This option determines whether intermediate segments of connectors that are attached to
   * common endpoints will be nudged apart. Usually these segments get nudged apart, but you
   * may want to turn this off if you would prefer that entire shared paths terminating at a
   * common end point should overlap.
   */
  public static final IProperty<Boolean> NUDGE_SHARED_PATHS_WITH_COMMON_END_POINT = new Property<Boolean>(
            "org.eclipse.elk.alg.libavoid.nudgeSharedPathsWithCommonEndPoint",
            NUDGE_SHARED_PATHS_WITH_COMMON_END_POINT_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #ENABLE_HYPEREDGES_FROM_COMMON_SOURCE}.
   */
  private static final boolean ENABLE_HYPEREDGES_FROM_COMMON_SOURCE_DEFAULT = false;

  /**
   * This option enables a post-processing step that creates hyperedges for all edges with the same source.
   * Be aware that this step will significantly decrease performance.
   */
  public static final IProperty<Boolean> ENABLE_HYPEREDGES_FROM_COMMON_SOURCE = new Property<Boolean>(
            "org.eclipse.elk.alg.libavoid.enableHyperedgesFromCommonSource",
            ENABLE_HYPEREDGES_FROM_COMMON_SOURCE_DEFAULT,
            null,
            null);

  /**
   * Default value for {@link #IS_CLUSTER}.
   */
  private static final boolean IS_CLUSTER_DEFAULT = false;

  /**
   * This option marks a node as a cluster, resulting in its children being handled as
   * relative to the graph itself while the marked node is only added as a cluster.
   * Note that clusters are experimental and can therefore have a negative impact on performance.
   * The cluster node cannot have:
   * - clusters as children
   * - outgoing or incoming connections (directly to the node)
   * - ports
   * Edges into or out of the cluster must be added across the cluster's borders, without the use of hierarchical ports.
   */
  public static final IProperty<Boolean> IS_CLUSTER = new Property<Boolean>(
            "org.eclipse.elk.alg.libavoid.isCluster",
            IS_CLUSTER_DEFAULT,
            null,
            null);

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.libavoid.segmentPenalty")
        .group("")
        .name("Segment Penalty")
        .description("This penalty is applied for each segment in the connector path beyond the first. This should always normally be set when doing orthogonal routing to prevent step-like connector paths.")
        .defaultValue(SEGMENT_PENALTY_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.libavoid.anglePenalty")
        .group("")
        .name("Angle Penalty")
        .description("This penalty is applied in its full amount to tight acute bends in the connector path. A smaller portion of the penalty is applied for slight bends, i.e., where the bend is close to 180 degrees. This is useful for polyline routing where there is some evidence that tighter corners are worse for readability, but that slight bends might not be so bad, especially when smoothed by curves.")
        .defaultValue(ANGLE_PENALTY_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.libavoid.crossingPenalty")
        .group("")
        .name("Crossing Penalty")
        .description("This penalty is applied whenever a connector path crosses another connector path. It takes shared paths into consideration and the penalty is only applied if there is an actual crossing.")
        .defaultValue(CROSSING_PENALTY_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.libavoid.clusterCrossingPenalty")
        .group("")
        .name("Cluster Crossing Penalty")
        .description("This penalty is applied whenever a connector path crosses a cluster boundary.")
        .defaultValue(CLUSTER_CROSSING_PENALTY_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.libavoid.fixedSharedPathPenalty")
        .group("")
        .name("Fixed Shared Path Penalty")
        .description("This penalty is applied whenever a connector path shares some segments with an immovable portion of an existing connector route (such as the first or last segment of a connector).")
        .defaultValue(FIXED_SHARED_PATH_PENALTY_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.libavoid.portDirectionPenalty")
        .group("")
        .name("Port Direction Penalty")
        .description("This penalty is applied to port selection choice when the other end of the connector being routed does not appear in any of the 90 degree visibility cones centered on the visibility directions for the port.")
        .defaultValue(PORT_DIRECTION_PENALTY_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.libavoid.shapeBufferDistance")
        .group("")
        .name("Shape Buffer Distance")
        .description("This parameter defines the spacing distance that will be added to the sides of each shape when determining obstacle sizes for routing. This controls how closely connectors pass shapes, and can be used to prevent connectors overlapping with shape boundaries.")
        .defaultValue(SHAPE_BUFFER_DISTANCE_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.libavoid.idealNudgingDistance")
        .group("")
        .name("Ideal Nudging Distance")
        .description("This parameter defines the spacing distance that will be used for nudging apart overlapping corners and line segments of connectors.")
        .defaultValue(IDEAL_NUDGING_DISTANCE_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.libavoid.reverseDirectionPenalty")
        .group("")
        .name("Reverse Direction Penalty")
        .description("This penalty is applied whenever a connector path travels in the direction opposite of the destination from the source endpoint. By default this penalty is set to zero. This shouldn\'t be needed in most cases but can be useful if you use penalties such as crossingPenalty which cause connectors to loop around obstacles.")
        .defaultValue(REVERSE_DIRECTION_PENALTY_DEFAULT)
        .type(LayoutOptionData.Type.DOUBLE)
        .optionClass(Double.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.libavoid.nudgeOrthogonalSegmentsConnectedToShapes")
        .group("")
        .name("Nudge Orthogonal Segments")
        .description("This option causes the final segments of connectors, which are attached to shapes, to be nudged apart. Usually these segments are fixed, since they are considered to be attached to ports.")
        .defaultValue(NUDGE_ORTHOGONAL_SEGMENTS_CONNECTED_TO_SHAPES_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.libavoid.improveHyperedgeRoutesMovingJunctions")
        .group("")
        .name("Improve Hyperedge Routes")
        .description("This option causes hyperedge routes to be locally improved fixing obviously bad paths. As part of this process libavoid will effectively move junctions, setting new ideal positions for each junction.")
        .defaultValue(IMPROVE_HYPEREDGE_ROUTES_MOVING_JUNCTIONS_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.libavoid.penaliseOrthogonalSharedPathsAtConnEnds")
        .group("")
        .name("Penalise Orthogonal Shared Paths")
        .description("This option penalises and attempts to reroute orthogonal shared connector paths terminating at a common junction or shape connection pin. When multiple connector paths enter or leave the same side of a junction (or shape pin), the router will attempt to reroute these to different sides of the junction or different shape pins. This option depends on the fixedSharedPathPenalty penalty having been set.")
        .defaultValue(PENALISE_ORTHOGONAL_SHARED_PATHS_AT_CONN_ENDS_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.libavoid.nudgeOrthogonalTouchingColinearSegments")
        .group("")
        .name("Nudge Orthogonal Touching Colinear Segments")
        .description("This option can be used to control whether colinear line segments that touch just at their ends will be nudged apart. The overlap will usually be resolved in the other dimension, so this is not usually required.")
        .defaultValue(NUDGE_ORTHOGONAL_TOUCHING_COLINEAR_SEGMENTS_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.libavoid.performUnifyingNudgingPreprocessingStep")
        .group("")
        .name("Perform Unifying Nudging Preprocessing")
        .description("This option can be used to control whether the router performs a preprocessing step before orthogonal nudging where is tries to unify segments and centre them in free space. This generally results in better quality ordering and nudging.")
        .defaultValue(PERFORM_UNIFYING_NUDGING_PREPROCESSING_STEP_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.libavoid.improveHyperedgeRoutesMovingAddingAndDeletingJunctions")
        .group("")
        .name("Improve Hyperedge Routes Add/Delete")
        .description("This option causes hyperedge routes to be locally improved fixing obviously bad paths. It can cause junctions and connectors to be added or removed from hyperedges. As part of this process libavoid will effectively move junctions by setting new ideal positions for each remaining or added junction. If set, this option overrides the improveHyperedgeRoutesMovingJunctions option.")
        .defaultValue(IMPROVE_HYPEREDGE_ROUTES_MOVING_ADDING_AND_DELETING_JUNCTIONS_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.libavoid.nudgeSharedPathsWithCommonEndPoint")
        .group("")
        .name("Nudge Shared Paths With Common Endpoint")
        .description("This option determines whether intermediate segments of connectors that are attached to common endpoints will be nudged apart. Usually these segments get nudged apart, but you may want to turn this off if you would prefer that entire shared paths terminating at a common end point should overlap.")
        .defaultValue(NUDGE_SHARED_PATHS_WITH_COMMON_END_POINT_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.libavoid.enableHyperedgesFromCommonSource")
        .group("")
        .name("Enable Hyperedges From Common Source")
        .description("This option enables a post-processing step that creates hyperedges for all edges with the same source. Be aware that this step will significantly decrease performance.")
        .defaultValue(ENABLE_HYPEREDGES_FROM_COMMON_SOURCE_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.alg.libavoid.isCluster")
        .group("")
        .name("Marks a node as a cluster")
        .description("This option marks a node as a cluster, resulting in its children being handled as relative to the graph itself while the marked node is only added as a cluster. Note that clusters are experimental and can therefore have a negative impact on performance. The cluster node cannot have: - clusters as children - outgoing or incoming connections (directly to the node) - ports Edges into or out of the cluster must be added across the cluster\'s borders, without the use of hierarchical ports.")
        .defaultValue(IS_CLUSTER_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.NODES))
        .visibility(LayoutOptionData.Visibility.ADVANCED)
        .create()
    );
    registry.register(new LayoutCategoryData.Builder()
        .id("org.eclipse.elk.alg.libavoid.edge")
        .name("Edge Routing")
        .description("Only route the edges without touching the node&apos;s positions.")
        .create()
    );
    new org.eclipse.elk.alg.libavoid.options.LibavoidOptions().apply(registry);
  }
}
