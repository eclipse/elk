/*******************************************************************************
 * Copyright (c) 2010, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.properties;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.eclipse.elk.alg.layered.ILayoutProcessor;
import org.eclipse.elk.alg.layered.IntermediateProcessingConfiguration;
import org.eclipse.elk.alg.layered.compound.CrossHierarchyEdge;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.intermediate.wrapping.BreakingPointInserter;
import org.eclipse.elk.alg.layered.p5edges.splines.ConnectedSelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.splines.LoopSide;
import org.eclipse.elk.core.math.ElkMargin;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.nodespacing.LabelSide;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

import com.google.common.base.Function;
import com.google.common.collect.Multimap;

/**
 * Container for property definitions for internal use of the algorithm. These properties should
 * not be accessed from outside. Some properties here are redefinitions of layout options in
 * {@link LayeredOptions} to change their defaults.
 *
 * @author msp
 * @author cds
 * @author uru
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public final class InternalProperties {

    /**
     * The original object from which a graph element was created.
     */
    public static final IProperty<Object> ORIGIN = new Property<Object>("origin");
    
    /**
     * The {@link LGraph} the coordinates of a cross-hierarchy edge are relative to.
     */
    public static final IProperty<LGraph> COORDINATE_SYSTEM_ORIGIN = new Property<>("coordinateOrigin");

    /**
     * The intermediate processing configuration for an input graph.
     */
    public static final IProperty<IntermediateProcessingConfiguration> CONFIGURATION =
            new Property<IntermediateProcessingConfiguration>("processingConfiguration");

    /**
     * The list of layout processors executed for an input graph.
     */
    public static final IProperty<List<ILayoutProcessor>> PROCESSORS =
            new Property<List<ILayoutProcessor>>("processors");

    /**
     * Whether the original node an LNode was created from was a compound node or not. This might
     * influence certain layout decisions, such as where to place inside port labels so that they
     * don't overlap edges.
     */
    public static final IProperty<Boolean> COMPOUND_NODE = new Property<Boolean>("compoundNode",
            false);

    /**
     * Whether the original port an LPort was created from was a compound port with connections to
     * or from descendants of its node. This might influence certain layout decisions, such as where
     * to place its inside port label.
     */
    public static final IProperty<Boolean> INSIDE_CONNECTIONS = new Property<Boolean>(
            "insideConnections", false);

    /**
     * An LNode that represents a compound node can hold a reference to a nested LGraph which
     * represents the graph that is contained within the compound node.
     */
    public static final IProperty<LGraph> NESTED_LGRAPH = new Property<LGraph>("nestedLGraph");

    /**
     * A nested LGraph has a reference to the LNode that contains it.
     */
    public static final IProperty<LNode> PARENT_LNODE = new Property<LNode>("parentLNode");

    /**
     * The original bend points of an edge.
     */
    public static final IProperty<KVectorChain> ORIGINAL_BENDPOINTS = new Property<KVectorChain>(
            "originalBendpoints");

    /**
     * In interactive layout settings, this property can be set to indicate where a dummy node that
     * represents an edge in a given layer was probably placed in that layer. This information can
     * be calculated during the crossing minimization phase and later be used by an interactive node
     * placement algorithm.
     */
    public static final IProperty<Double> ORIGINAL_DUMMY_NODE_POSITION = new Property<Double>(
            "originalDummyNodePosition");

    /**
     * The edge a label originally belonged to. This property was introduced to remember which
     * cross-hierarchy edge a label originally belonged to.
     */
    public static final IProperty<LEdge> ORIGINAL_LABEL_EDGE = new Property<LEdge>(
            "originalLabelEdge");

    /**
     * Edge labels represented by an edge label dummy node.
     */
    public static final IProperty<List<LLabel>> REPRESENTED_LABELS =
            new Property<List<LLabel>>("representedLabels");

    /**
     * The side (of an edge) a label is placed on.
     */
    public static final IProperty<LabelSide> LABEL_SIDE = new Property<LabelSide>(
            "labelSide", LabelSide.UNKNOWN);

    /**
     * Flag for reversed edges.
     */
    public static final IProperty<Boolean> REVERSED = new Property<Boolean>("reversed", false);

    /**
     * Random number generator for the algorithm.
     */
    public static final IProperty<Random> RANDOM = new Property<Random>("random");

    /**
     * The source port of a long edge before it was broken into multiple segments.
     */
    public static final IProperty<LPort> LONG_EDGE_SOURCE = new Property<>("longEdgeSource", null);
    
    /**
     * The target port of a long edge before it was broken into multiple segments.
     */
    public static final IProperty<LPort> LONG_EDGE_TARGET = new Property<>("longEdgeTarget", null);
    
    /**
     * Set on long edge dummies to indicate whether someplace in the long edge is a label dummy. This can for
     * example influence decisions on how to merge long edge dummies.
     */
    public static final IProperty<Boolean> LONG_EDGE_HAS_LABEL_DUMMIES = new Property<>("longEdgeHasLabelDummies",
            false);
    
    /**
     * If a long edge has a label dummy somewhere, this property indicates for every long edge dummy whether or
     * not it comes before that label dummy ({@code true}) or not ({@code false}). Coming before a label dummy
     * in this case means being in a layer left of the label dummy.
     */
    public static final IProperty<Boolean> LONG_EDGE_BEFORE_LABEL_DUMMY = new Property<>("longEdgeBeforeLabelDummy",
            false);

    /**
     * Edge constraints for nodes.
     */
    public static final IProperty<EdgeConstraint> EDGE_CONSTRAINT = new Property<EdgeConstraint>(
            "edgeConstraint", EdgeConstraint.NONE);

    /**
     * The layout unit a node belongs to. This property only makes sense for nodes. A layout unit is
     * a set of nodes between which no nodes belonging to other layout units may be placed. Nodes
     * not belonging to any layout unit may be placed arbitrarily between nodes of a layout unit.
     * Layer layout units are identified through one of their nodes.
     */
    public static final IProperty<LNode> IN_LAYER_LAYOUT_UNIT = new Property<LNode>(
            "inLayerLayoutUnit");

    /**
     * The in-layer constraint placed on a node. This indicates whether this node should be handled
     * like any other node, or if it must be placed at the top or bottom of a layer. This is
     * important for external port dummy nodes. Crossing minimizers are not required to respect this
     * constraint. If they don't, however, they must include a dependency on
     * {@link org.eclipse.elk.alg.layered.intermediate.InLayerConstraintProcessor}.
     */
    public static final IProperty<InLayerConstraint> IN_LAYER_CONSTRAINT
           = new Property<InLayerConstraint>("inLayerConstraint", InLayerConstraint.NONE);

    /**
     * Indicates that a node {@code x} may only appear inside a layer before the node {@code y} the
     * property is set to. That is, having {@code x} appear after {@code y} would violate this
     * constraint. This property only makes sense for nodes.
     */
    public static final IProperty<List<LNode>> IN_LAYER_SUCCESSOR_CONSTRAINTS =
            new Property<List<LNode>>("inLayerSuccessorConstraint", new ArrayList<LNode>());

    /**
     * A property set on ports indicating a dummy node created for that port. This is not set for
     * all ports that have dummy nodes created for them.
     */
    public static final IProperty<LNode> PORT_DUMMY = new Property<LNode>("portDummy");

    /**
     * Crossing hint used for in-layer cross counting with northern and southern port dummies. This
     * is effectively the number of different ports a northern or southern port dummy represents.
     */
    public static final IProperty<Integer> CROSSING_HINT = new Property<Integer>("crossingHint", 0);

    /**
     * Flags indicating the properties of a graph.
     */
    public static final IProperty<Set<GraphProperties>> GRAPH_PROPERTIES =
            new Property<Set<GraphProperties>>("graphProperties",
                    EnumSet.noneOf(GraphProperties.class));

    /**
     * The side of an external port a dummy node was created for.
     */
    public static final IProperty<PortSide> EXT_PORT_SIDE = new Property<PortSide>(
            "externalPortSide", PortSide.UNDEFINED);

    /**
     * Original size of the external port a dummy node was created for.
     */
    public static final IProperty<KVector> EXT_PORT_SIZE = new Property<KVector>(
            "externalPortSize", new KVector());

    /**
     * External port dummies that represent northern or southern external ports are replaced by new
     * dummy nodes during layout. In these cases, this property is set to the original dummy node.
     */
    public static final IProperty<LNode> EXT_PORT_REPLACED_DUMMY = new Property<LNode>(
            "externalPortReplacedDummy");

    /**
     * The port sides of external ports a connected component connects to. This property is set on
     * the layered graph that represents a connected component and defaults to no connections. If a
     * connected component connects to an external port on the EAST side and to another external
     * port on the NORTH side, this enumeration will list both sides.
     */
    public static final IProperty<Set<PortSide>> EXT_PORT_CONNECTIONS =
            new Property<Set<PortSide>>("externalPortConnections", EnumSet.noneOf(PortSide.class));

    /**
     * The original position or position-to-node-size ratio of a port. This property has two use
     * cases:
     * <ol>
     * <li>For external port dummies. In this use case, the property gives the original position of
     * the external port (if port constraints are set to {@code FIXED_POS}) or the original
     * position-to-node-size ratio of the external port ((if port constraints are set to
     * {@code FIXED_RATIO}).</li>
     * <li>For ports of regular nodes with port constraints set to {@code FIXED_RATIO}. Since
     * regular nodes may be resized, the original ratio must be remembered for the new port position
     * to be determined.</li>
     * </ol>
     * <p>
     * This is a one-dimensional value since the side of the port determines the other dimension.
     * (For eastern and western ports, the x coordinate is determined automatically; for northern
     * and southern ports, the y coordinate is determined automatically)
     * </p>
     */
    public static final IProperty<Double> PORT_RATIO_OR_POSITION = new Property<Double>(
            "portRatioOrPosition", 0.0);

    /**
     * A list of nodes whose barycenters should go into the barycenter calculation of the node this
     * property is set on. Nodes in this list are expected to be in the same layer as the node the
     * property is set on. This is primarily used when edges are rerouted from a node to dummy
     * nodes.
     * <p>
     * This property is currently not declared as one of the layout options offered by KLay Layered
     * and should be considered highly experimental.
     */
    public static final IProperty<List<LNode>> BARYCENTER_ASSOCIATES = new Property<List<LNode>>(
            "barycenterAssociates");

    /**
     * List of comment boxes that are placed on top of a node.
     */
    public static final IProperty<List<LNode>> TOP_COMMENTS = new Property<List<LNode>>(
            "TopSideComments");

    /**
     * List of comment boxes that are placed in the bottom of of a node.
     */
    public static final IProperty<List<LNode>> BOTTOM_COMMENTS = new Property<List<LNode>>(
            "BottomSideComments");

    /**
     * The port of a node that originally connected a comment box with that node.
     */
    public static final IProperty<LPort> COMMENT_CONN_PORT = new Property<LPort>(
            "CommentConnectionPort");

    /**
     * Whether a port is used to collect all incoming edges of a node.
     */
    public static final IProperty<Boolean> INPUT_COLLECT = new Property<Boolean>("inputCollect",
            false);
    /**
     * Whether a port is used to collect all outgoing edges of a node.
     */
    public static final IProperty<Boolean> OUTPUT_COLLECT = new Property<Boolean>("outputCollect",
            false);

    /**
     * Property of a LayeredGraph. Whether the graph has been processed by the cycle breaker and the
     * cycle breaker has detected cycles and reverted edges.
     */
    public static final IProperty<Boolean> CYCLIC = new Property<Boolean>("cyclic", false);

    /**
     * Determines the original size of a big node.
     */
    public static final IProperty<Float> BIG_NODE_ORIGINAL_SIZE = new Property<Float>(
            "bigNodeOriginalSize", 0f);

    /**
     * Specifies if the corresponding node is the first node in a big node chain.
     */
    public static final IProperty<Boolean> BIG_NODE_INITIAL = new Property<Boolean>(
            "bigNodeInitial", false);

    /**
     * Original labels of a big node.
     */
    public static final IProperty<List<LLabel>> BIGNODES_ORIG_LABELS = new Property<List<LLabel>>(
            "org.eclipse.elk.alg.layered.bigNodeLabels", new ArrayList<LLabel>());

    /**
     * A post processing function that is called during big nodes post processing.
     */
    public static final IProperty<Function<Void, Void>> BIGNODES_POST_PROCESS =
            new Property<Function<Void, Void>>("org.eclipse.elk.alg.layered.postProcess", null);

    /**
     * Map of original hierarchy crossing edges to a set of dummy edges by which the original edge
     * has been replaced.
     */
    public static final IProperty<Multimap<LEdge, CrossHierarchyEdge>> CROSS_HIERARCHY_MAP =
            new Property<Multimap<LEdge, CrossHierarchyEdge>>("crossHierarchyMap");

    /**
     * Offset to be added to the target anchor point of an edge when the layout is applied back to
     * the origin.
     */
    public static final IProperty<KVector> TARGET_OFFSET = new Property<KVector>("targetOffset");

    /**
     * Combined size of all edge labels of a spline self loop.
     */
    public static final IProperty<KVector> SPLINE_LABEL_SIZE =
            new Property<KVector>("splineLabelSize", new KVector());

    /**
     * Determines the loop side of an edge.
     */
    public static final IProperty<LoopSide> SPLINE_LOOPSIDE = new Property<LoopSide>("splineLoopSide",
            LoopSide.UNDEFINED);

    /**
     * A port with this property set will be handled from the SplineSelfLoopPre- and Postprocessor.
     */
    public static final IProperty<List<ConnectedSelfLoopComponent>> SPLINE_SELFLOOP_COMPONENTS =
            new Property<List<ConnectedSelfLoopComponent>>("splineSelfLoopComponents",
                    new ArrayList<ConnectedSelfLoopComponent>());

    /**
     * A node's property storing the margins of a node required for it's self loops.
     */
    public static final IProperty<ElkMargin> SPLINE_SELF_LOOP_MARGINS = new Property<ElkMargin>(
            "splineSelfLoopMargins", new ElkMargin());

    /**
     * Internal container for all possible spacing variations that we support.
     */
    public static final IProperty<Spacings> SPACINGS =
            new Property<Spacings>("spacings");

    /**
     * Specifies if the corresponding LGraph element was added by the
     * {@link de.cau.cs.kieler.klay.layered.intermediate.PartitionPreprocessor
     * PartitionPreprocessor}.
     */
    public static final IProperty<Boolean> PARTITION_DUMMY = new Property<Boolean>(
            "partitionConstraint", false);

    /**
     * Holds information about a breaking point, e.g. the start dummy node, the end dummy node 
     * and the original edge that was split.
     * 
     * @see org.eclipse.elk.alg.layered.intermediate.wrapping.BreakingPointInserter BreakingPointInserter
     */
    public static final IProperty<BreakingPointInserter.BPInfo> BREAKING_POINT_INFO =
            new Property<BreakingPointInserter.BPInfo>("breakingPoint.info");

    /**
     * Hidden default constructor.
     */
    private InternalProperties() {
    }
}
