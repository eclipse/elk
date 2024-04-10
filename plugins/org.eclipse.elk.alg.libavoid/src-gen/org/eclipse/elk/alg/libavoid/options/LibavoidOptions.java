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

import org.eclipse.elk.alg.libavoid.LibavoidLayoutProvider;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class LibavoidOptions implements ILayoutMetaDataProvider {
  /**
   * The id of the Libavoid algorithm.
   */
  public static final String ALGORITHM_ID = "org.eclipse.elk.alg.libavoid";

  /**
   * Default value for {@link #DEBUG_MODE} with algorithm "Libavoid".
   */
  private static final boolean DEBUG_MODE_DEFAULT = false;

  /**
   * Whether additional debug information shall be generated.
   */
  public static final IProperty<Boolean> DEBUG_MODE = new Property<Boolean>(
                                CoreOptions.DEBUG_MODE,
                                DEBUG_MODE_DEFAULT);

  /**
   * The side of a node on which a port is situated. This option must be set if 'Port
   * Constraints' is set to FIXED_SIDE or FIXED_ORDER and no specific positions are given
   * for the ports.
   */
  public static final IProperty<PortSide> PORT_SIDE = CoreOptions.PORT_SIDE;

  /**
   * Overall direction of edges: horizontal (right / left) or
   * vertical (down / up).
   */
  public static final IProperty<Direction> DIRECTION = CoreOptions.DIRECTION;

  /**
   * Default value for {@link #EDGE_ROUTING} with algorithm "Libavoid".
   */
  private static final EdgeRouting EDGE_ROUTING_DEFAULT = EdgeRouting.ORTHOGONAL;

  /**
   * What kind of edge routing style should be applied for the content of a parent node.
   * Algorithms may also set this option to single edges in order to mark them as splines.
   * The bend point list of edges with this option set to SPLINES must be interpreted as control
   * points for a piecewise cubic spline.
   */
  public static final IProperty<EdgeRouting> EDGE_ROUTING = new Property<EdgeRouting>(
                                CoreOptions.EDGE_ROUTING,
                                EDGE_ROUTING_DEFAULT);

  /**
   * Default value for {@link #PORT_CONSTRAINTS} with algorithm "Libavoid".
   */
  private static final PortConstraints PORT_CONSTRAINTS_DEFAULT = PortConstraints.FREE;

  /**
   * Defines constraints of the position of the ports of a node.
   */
  public static final IProperty<PortConstraints> PORT_CONSTRAINTS = new Property<PortConstraints>(
                                CoreOptions.PORT_CONSTRAINTS,
                                PORT_CONSTRAINTS_DEFAULT);

  /**
   * Node micro layout comprises the computation of node dimensions (if requested), the placement of ports
   * and their labels, and the placement of node labels.
   * The functionality is implemented independent of any specific layout algorithm and shouldn't have any
   * negative impact on the layout algorithm's performance itself. Yet, if any unforeseen behavior occurs,
   * this option allows to deactivate the micro layout.
   */
  public static final IProperty<Boolean> OMIT_NODE_MICRO_LAYOUT = CoreOptions.OMIT_NODE_MICRO_LAYOUT;

  /**
   * This penalty is applied for each segment in the connector path beyond the first.
   * This should always normally be set when doing orthogonal routing to prevent
   * step-like connector paths.
   */
  public static final IProperty<Double> SEGMENT_PENALTY = LibavoidMetaDataProvider.SEGMENT_PENALTY;

  /**
   * This penalty is applied in its full amount to tight acute bends in the connector path.
   * A smaller portion of the penalty is applied for slight bends, i.e., where the bend is close
   * to 180 degrees. This is useful for polyline routing where there is some evidence that tighter
   * corners are worse for readability, but that slight bends might not be so bad,
   * especially when smoothed by curves.
   */
  public static final IProperty<Double> ANGLE_PENALTY = LibavoidMetaDataProvider.ANGLE_PENALTY;

  /**
   * This penalty is applied whenever a connector path crosses another connector path.
   * It takes shared paths into consideration and the penalty is only applied
   * if there is an actual crossing.
   */
  public static final IProperty<Double> CROSSING_PENALTY = LibavoidMetaDataProvider.CROSSING_PENALTY;

  /**
   * This penalty is applied whenever a connector path crosses a cluster boundary.
   */
  public static final IProperty<Double> CLUSTER_CROSSING_PENALTY = LibavoidMetaDataProvider.CLUSTER_CROSSING_PENALTY;

  /**
   * This penalty is applied whenever a connector path shares some segments with an immovable
   * portion of an existing connector route (such as the first or last segment of a connector).
   */
  public static final IProperty<Double> FIXED_SHARED_PATH_PENALTY = LibavoidMetaDataProvider.FIXED_SHARED_PATH_PENALTY;

  /**
   * This penalty is applied to port selection choice when the other end of the connector
   * being routed does not appear in any of the 90 degree visibility cones centered on the
   * visibility directions for the port.
   */
  public static final IProperty<Double> PORT_DIRECTION_PENALTY = LibavoidMetaDataProvider.PORT_DIRECTION_PENALTY;

  /**
   * This parameter defines the spacing distance that will be added to the sides of each
   * shape when determining obstacle sizes for routing. This controls how closely connectors
   * pass shapes, and can be used to prevent connectors overlapping with shape boundaries.
   */
  public static final IProperty<Double> SHAPE_BUFFER_DISTANCE = LibavoidMetaDataProvider.SHAPE_BUFFER_DISTANCE;

  /**
   * This parameter defines the spacing distance that will be used for nudging apart
   * overlapping corners and line segments of connectors.
   */
  public static final IProperty<Double> IDEAL_NUDGING_DISTANCE = LibavoidMetaDataProvider.IDEAL_NUDGING_DISTANCE;

  /**
   * This penalty is applied whenever a connector path travels in the direction opposite
   * of the destination from the source endpoint. By default this penalty is set to zero.
   * This shouldn't be needed in most cases but can be useful if you use penalties such as
   * crossingPenalty which cause connectors to loop around obstacles.
   */
  public static final IProperty<Double> REVERSE_DIRECTION_PENALTY = LibavoidMetaDataProvider.REVERSE_DIRECTION_PENALTY;

  /**
   * This option causes the final segments of connectors, which are attached to shapes,
   * to be nudged apart. Usually these segments are fixed, since they are considered to be
   * attached to ports.
   */
  public static final IProperty<Boolean> NUDGE_ORTHOGONAL_SEGMENTS_CONNECTED_TO_SHAPES = LibavoidMetaDataProvider.NUDGE_ORTHOGONAL_SEGMENTS_CONNECTED_TO_SHAPES;

  /**
   * This option causes hyperedge routes to be locally improved fixing obviously bad paths.
   * As part of this process libavoid will effectively move junctions, setting new ideal positions
   * for each junction.
   */
  public static final IProperty<Boolean> IMPROVE_HYPEREDGE_ROUTES_MOVING_JUNCTIONS = LibavoidMetaDataProvider.IMPROVE_HYPEREDGE_ROUTES_MOVING_JUNCTIONS;

  /**
   * This option penalises and attempts to reroute orthogonal shared connector paths terminating
   * at a common junction or shape connection pin. When multiple connector paths enter or leave
   * the same side of a junction (or shape pin), the router will attempt to reroute these to
   * different sides of the junction or different shape pins. This option depends on the
   * fixedSharedPathPenalty penalty having been set.
   */
  public static final IProperty<Boolean> PENALISE_ORTHOGONAL_SHARED_PATHS_AT_CONN_ENDS = LibavoidMetaDataProvider.PENALISE_ORTHOGONAL_SHARED_PATHS_AT_CONN_ENDS;

  /**
   * This option can be used to control whether colinear line segments that touch just at
   * their ends will be nudged apart. The overlap will usually be resolved in the other dimension,
   * so this is not usually required.
   */
  public static final IProperty<Boolean> NUDGE_ORTHOGONAL_TOUCHING_COLINEAR_SEGMENTS = LibavoidMetaDataProvider.NUDGE_ORTHOGONAL_TOUCHING_COLINEAR_SEGMENTS;

  /**
   * This option can be used to control whether the router performs a preprocessing step before
   * orthogonal nudging where is tries to unify segments and centre them in free space.
   * This generally results in better quality ordering and nudging.
   */
  public static final IProperty<Boolean> PERFORM_UNIFYING_NUDGING_PREPROCESSING_STEP = LibavoidMetaDataProvider.PERFORM_UNIFYING_NUDGING_PREPROCESSING_STEP;

  /**
   * This option causes hyperedge routes to be locally improved fixing obviously bad paths.
   * It can cause junctions and connectors to be added or removed from hyperedges. As part of
   * this process libavoid will effectively move junctions by setting new ideal positions for
   * each remaining or added junction. If set, this option overrides the
   * improveHyperedgeRoutesMovingJunctions option.
   */
  public static final IProperty<Boolean> IMPROVE_HYPEREDGE_ROUTES_MOVING_ADDING_AND_DELETING_JUNCTIONS = LibavoidMetaDataProvider.IMPROVE_HYPEREDGE_ROUTES_MOVING_ADDING_AND_DELETING_JUNCTIONS;

  /**
   * This option determines whether intermediate segments of connectors that are attached to
   * common endpoints will be nudged apart. Usually these segments get nudged apart, but you
   * may want to turn this off if you would prefer that entire shared paths terminating at a
   * common end point should overlap.
   */
  public static final IProperty<Boolean> NUDGE_SHARED_PATHS_WITH_COMMON_END_POINT = LibavoidMetaDataProvider.NUDGE_SHARED_PATHS_WITH_COMMON_END_POINT;

  /**
   * This option enables a post-processing step that creates hyperedges for all edges with the same source.
   * Be aware that this step will significantly decrease performance.
   */
  public static final IProperty<Boolean> ENABLE_HYPEREDGES_FROM_COMMON_SOURCE = LibavoidMetaDataProvider.ENABLE_HYPEREDGES_FROM_COMMON_SOURCE;

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
  public static final IProperty<Boolean> IS_CLUSTER = LibavoidMetaDataProvider.IS_CLUSTER;

  /**
   * Layouter-specific algorithm factory.
   */
  public static class LibavoidFactory implements org.eclipse.elk.core.util.IFactory<AbstractLayoutProvider> {
    public AbstractLayoutProvider create() {
      AbstractLayoutProvider provider = new LibavoidLayoutProvider();
      provider.initialize("");
      return provider;
    }

    public void destroy(final AbstractLayoutProvider obj) {
      obj.dispose();
    }
  }

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutAlgorithmData.Builder()
        .id("org.eclipse.elk.alg.libavoid")
        .name("Libavoid")
        .description("libavoid is a cross-platform C++ library providing fast, object-avoiding orthogonal and polyline connector routing for use in interactive diagram editors.")
        .providerFactory(new LibavoidFactory())
        .category("org.eclipse.elk.alg.libavoid.edge")
        .melkBundleName("Libavoid Connector Routing")
        .definingBundleId("org.eclipse.elk.alg.libavoid")
        .create()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.libavoid",
        "org.eclipse.elk.debugMode",
        DEBUG_MODE_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.libavoid",
        "org.eclipse.elk.port.side",
        PORT_SIDE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.libavoid",
        "org.eclipse.elk.direction",
        DIRECTION.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.libavoid",
        "org.eclipse.elk.edgeRouting",
        EDGE_ROUTING_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.libavoid",
        "org.eclipse.elk.portConstraints",
        PORT_CONSTRAINTS_DEFAULT
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.libavoid",
        "org.eclipse.elk.omitNodeMicroLayout",
        OMIT_NODE_MICRO_LAYOUT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.libavoid",
        "org.eclipse.elk.alg.libavoid.segmentPenalty",
        SEGMENT_PENALTY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.libavoid",
        "org.eclipse.elk.alg.libavoid.anglePenalty",
        ANGLE_PENALTY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.libavoid",
        "org.eclipse.elk.alg.libavoid.crossingPenalty",
        CROSSING_PENALTY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.libavoid",
        "org.eclipse.elk.alg.libavoid.clusterCrossingPenalty",
        CLUSTER_CROSSING_PENALTY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.libavoid",
        "org.eclipse.elk.alg.libavoid.fixedSharedPathPenalty",
        FIXED_SHARED_PATH_PENALTY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.libavoid",
        "org.eclipse.elk.alg.libavoid.portDirectionPenalty",
        PORT_DIRECTION_PENALTY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.libavoid",
        "org.eclipse.elk.alg.libavoid.shapeBufferDistance",
        SHAPE_BUFFER_DISTANCE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.libavoid",
        "org.eclipse.elk.alg.libavoid.idealNudgingDistance",
        IDEAL_NUDGING_DISTANCE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.libavoid",
        "org.eclipse.elk.alg.libavoid.reverseDirectionPenalty",
        REVERSE_DIRECTION_PENALTY.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.libavoid",
        "org.eclipse.elk.alg.libavoid.nudgeOrthogonalSegmentsConnectedToShapes",
        NUDGE_ORTHOGONAL_SEGMENTS_CONNECTED_TO_SHAPES.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.libavoid",
        "org.eclipse.elk.alg.libavoid.improveHyperedgeRoutesMovingJunctions",
        IMPROVE_HYPEREDGE_ROUTES_MOVING_JUNCTIONS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.libavoid",
        "org.eclipse.elk.alg.libavoid.penaliseOrthogonalSharedPathsAtConnEnds",
        PENALISE_ORTHOGONAL_SHARED_PATHS_AT_CONN_ENDS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.libavoid",
        "org.eclipse.elk.alg.libavoid.nudgeOrthogonalTouchingColinearSegments",
        NUDGE_ORTHOGONAL_TOUCHING_COLINEAR_SEGMENTS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.libavoid",
        "org.eclipse.elk.alg.libavoid.performUnifyingNudgingPreprocessingStep",
        PERFORM_UNIFYING_NUDGING_PREPROCESSING_STEP.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.libavoid",
        "org.eclipse.elk.alg.libavoid.improveHyperedgeRoutesMovingAddingAndDeletingJunctions",
        IMPROVE_HYPEREDGE_ROUTES_MOVING_ADDING_AND_DELETING_JUNCTIONS.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.libavoid",
        "org.eclipse.elk.alg.libavoid.nudgeSharedPathsWithCommonEndPoint",
        NUDGE_SHARED_PATHS_WITH_COMMON_END_POINT.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.libavoid",
        "org.eclipse.elk.alg.libavoid.enableHyperedgesFromCommonSource",
        ENABLE_HYPEREDGES_FROM_COMMON_SOURCE.getDefault()
    );
    registry.addOptionSupport(
        "org.eclipse.elk.alg.libavoid",
        "org.eclipse.elk.alg.libavoid.isCluster",
        IS_CLUSTER.getDefault()
    );
  }
}
