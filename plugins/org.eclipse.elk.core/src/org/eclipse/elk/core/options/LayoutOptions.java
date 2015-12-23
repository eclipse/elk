/*******************************************************************************
 * Copyright (c) 2009, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.options;

import java.util.EnumSet;

import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.util.nodespacing.Spacing.Margins;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * Definition of layout options. Layout options are divided into programmatic options, which are
 * defined by static code, and user interface options, which are defined by extension point. The
 * former can be accessed with static variables, while the latter are accessed with methods.
 * 
 * @kieler.design 2011-03-14 reviewed by cmot, cds
 * @kieler.rating yellow 2013-01-09 review KI-32 by ckru, chsch
 * @author msp
 * 
 * @containsLayoutOptions
 */
public final class LayoutOptions {

    // ///// PROGRAMMATIC LAYOUT OPTIONS ///////

    /**
     * Whether the shift from the old layout to the new computed layout shall be animated.
     * [programmatically set in the global settings]
     */
    public static final IProperty<Boolean> ANIMATE = new Property<Boolean>(
            "org.eclipse.elk.animate", true);

    /**
     * The minimal time for animations, in milliseconds. [programmatically set]
     */
    public static final IProperty<Integer> MIN_ANIMATION_TIME = new Property<Integer>(
            "org.eclipse.elk.minAnimTime", 400);

    /**
     * The maximal time for animations, in milliseconds. [programmatically set]
     */
    public static final IProperty<Integer> MAX_ANIMATION_TIME = new Property<Integer>(
            "org.eclipse.elk.maxAnimTime", 4000);

    /**
     * Factor for calculation of animation time. The higher the value, the longer the animation
     * time. If the value is 0, the resulting time is always equal to the minimum defined by
     * {@link #MIN_ANIMATION_TIME}. [programmatically set]
     */
    public static final IProperty<Integer> ANIMATION_TIME_FACTOR = new Property<Integer>(
            "org.eclipse.elk.animTimeFactor", 100);

    /**
     * For each side of a node, this property can reserve additional space before and after the
     * ports on each side. For example, a top spacing of 20 makes sure that the first port on the
     * western and eastern side is 20 units away from the northern border.
     */
    public static final IProperty<Margins> ADDITIONAL_PORT_SPACE = new Property<Margins>(
            "org.eclipse.elk.additionalPortSpace", null);

    /**
     * Whether the associated node is to be interpreted as a comment box. In that case its placement
     * should be similar to how labels are handled. Any edges incident to a comment box specify to
     * which graph elements the comment is related. [programmatically set]
     */
    public static final IProperty<Boolean> COMMENT_BOX = new Property<Boolean>(
            "org.eclipse.elk.commentBox", false);

    /**
     * Where to place an edge label: at the head, center, or tail. [programmatically set]
     */
    public static final IProperty<EdgeLabelPlacement> EDGE_LABEL_PLACEMENT =
            new Property<EdgeLabelPlacement>("org.eclipse.elk.edgeLabelPlacement",
                    EdgeLabelPlacement.UNDEFINED);

    /**
     * The type of edge. This is usually used for UML class diagrams, where associations must be
     * handled differently from generalizations. [programmatically set]
     */
    public static final IProperty<EdgeType> EDGE_TYPE = new Property<EdgeType>(
            "org.eclipse.elk.edgeType", EdgeType.NONE);

    /**
     * The name of the font that is used for a label. [programmatically set]
     */
    public static final IProperty<String> FONT_NAME = new Property<String>(
            "org.eclipse.elk.fontName");

    /**
     * The size of the font that is used for a label. [programmatically set]
     */
    public static final IProperty<Integer> FONT_SIZE = new Property<Integer>(
            "org.eclipse.elk.fontSize", 0);

    /**
     * Whether the associated node is to be interpreted as a hypernode. All incident edges of a
     * hypernode belong to the same hyperedge. [programmatically set]
     */
    public static final IProperty<Boolean> HYPERNODE = new Property<Boolean>(
            "org.eclipse.elk.hypernode", false);

    /**
     * This property is not used as option, but as output of the layout algorithms. It is attached
     * to edges and determines the points where junction symbols should be drawn in order to
     * represent hyperedges with orthogonal routing. Whether such points are computed depends on the
     * chosen layout algorithm and edge routing style. The points are put into the vector chain with
     * no specific order. [programmatically set]
     */
    public static final IProperty<KVectorChain> JUNCTION_POINTS = new Property<KVectorChain>(
            "org.eclipse.elk.junctionPoints", new KVectorChain());

    /**
     * Whether the hierarchy levels on the path from the selected element to the root of the diagram
     * shall be included in the layout process. [programmatically set in the global settings]
     */
    public static final IProperty<Boolean> LAYOUT_ANCESTORS = new Property<Boolean>(
            "org.eclipse.elk.layoutAncestors", false);

    /**
     * Margins define additional space around the actual bounds of a graph element. For instance,
     * ports or labels being placed on the outside of a node's border might introduce such a margin.
     * The margin is used to guarantee non-overlap of other graph elements with those ports or
     * labels. [programmatically set]
     */
    public static final IProperty<Margins> MARGINS = new Property<Margins>(
            "org.eclipse.elk.margins", new Margins());

    /**
     * The minimal height of a node. [programmatically set]
     */
    public static final IProperty<Float> MIN_HEIGHT = new Property<Float>(
            "org.eclipse.elk.minHeight", 0f, 0f);

    /**
     * The minimal width of a node. [programmatically set]
     */
    public static final IProperty<Float> MIN_WIDTH = new Property<Float>(
            "org.eclipse.elk.minWidth", 0f, 0f);

    /**
     * No layout is done for the associated element. This is used to mark parts of a diagram to
     * avoid their inclusion in the layout graph, or to mark parts of the layout graph to prevent
     * layout engines from processing them. [programmatically set]
     * 
     * If you wish to exclude the contents of a compound node from automatic layout, while the node
     * itself is still considered on its own layer, set
     * {@link org.eclipse.elk.core.util.FixedLayoutProvider FixedLayoutProvider#ID} as
     * {@link LayoutOptions#ALGORITHM} for this node.
     */
    public static final IProperty<Boolean> NO_LAYOUT = new Property<Boolean>(
            "org.eclipse.elk.noLayout", false);

    /**
     * Offset of a graph element. [programmatically set] This is mostly used to indicate the
     * distance of a port from its node: with a positive offset the port is moved outside of the
     * node, while with a negative offset the port is moved towards the inside. An offset of 0 means
     * that the port is placed directly on the node border, i.e.
     * <ul>
     * <li>if the port side is north, the port's south border touches the nodes's north border;</li>
     * <li>if the port side is east, the port's west border touches the nodes's east border;</li>
     * <li>if the port side is south, the port's north border touches the node's south border;</li>
     * <li>if the port side is west, the port's east border touches the node's west border.</li>
     * </ul>
     */
    public static final IProperty<Float> OFFSET = new Property<Float>("org.eclipse.elk.offset");

    /**
     * The offset to the port position where connections shall be attached.
     */
    public static final IProperty<KVector> PORT_ANCHOR = new Property<KVector>(
            "org.eclipse.elk.portAnchor");

    /**
     * The index of a port in the fixed order of ports around its node. [programmatically set] The
     * order is assumed as clockwise, starting with the leftmost port on the top side. This option
     * must be set if {@link #PORT_CONSTRAINTS} is set to {@link PortConstraints#FIXED_ORDER} and no
     * specific positions are given for the ports. Additionally, the option {@link #PORT_SIDE} must
     * be defined in this case.
     */
    public static final IProperty<Integer> PORT_INDEX = new Property<Integer>(
            "org.eclipse.elk.portIndex");

    /**
     * On which side of its corresponding node a port is situated. [programmatically set] This
     * option must be set if {@link #PORT_CONSTRAINTS} is set to {@link PortConstraints#FIXED_SIDE}
     * or {@link PortConstraints#FIXED_ORDER} and no specific positions are given for the ports.
     */
    public static final IProperty<PortSide> PORT_SIDE = new Property<PortSide>(
            "org.eclipse.elk.portSide", PortSide.UNDEFINED);

    /**
     * Whether a progress bar shall be displayed during layout computations.
     * [programmatically set in the global settings]
     */
    public static final IProperty<Boolean> PROGRESS_BAR = new Property<Boolean>(
            "org.eclipse.elk.progressBar", false);

    /**
     * Whether the layout configuration of a certain graph element should be reset before a layout
     * run. This might be useful to pass further information to the layout algorithm or during the
     * execution of a 'layout' chain where multiple layout providers are executed after each other.
     * It allows an information flow from earlier to later layout algorithms. However, in most cases
     * a 'clean' graph is desirable. By default {@code true}. [programmatically set]
     */
    public static final IProperty<Boolean> RESET_CONFIG = new Property<Boolean>(
            "org.eclipse.elk.resetConfig", true);

    /**
     * The scaling factor to be applied to the corresponding node in recursive layout.
     * [programmatically set] It causes the corresponding node's size to be adjusted, and its ports
     * & labels to be sized and placed accordingly after the layout of that node has been determined
     * (and before the node itself and its siblings get arranged). The scaling is not reverted
     * afterwards, so the resulting layout graph contains the adjusted size and position data. This
     * option is currently not supported if {@link #LAYOUT_HIERARCHY} is set.
     */
    public static final IProperty<Float> SCALE_FACTOR = new Property<Float>(
            "org.eclipse.elk.scaleFactor", 1f);

    /**
     * The thickness of an edge. [programmatically set] This is a hint on the line width used to
     * draw an edge, possibly requiring more space to be reserved for it.
     */
    public static final IProperty<Float> THICKNESS = new Property<Float>(
            "org.eclipse.elk.thickness", 1f);

    /**
     * Whether the zoom level shall be set to view the whole diagram after layout.
     * [programmatically set in the global settings]
     */
    public static final IProperty<Boolean> ZOOM_TO_FIT = new Property<Boolean>(
            "org.eclipse.elk.zoomToFit", false);

    // ///// USER INTERFACE LAYOUT OPTIONS ///////

    /**
     * Which layout algorithm to use for the content of a parent node.
     */
    public static final IProperty<String> ALGORITHM = new Property<String>(
            "org.eclipse.elk.algorithm");

    /**
     * Alignment of a node. The meaning of this option depends on the specific layout algorithm.
     */
    public static final IProperty<Alignment> ALIGNMENT = new Property<Alignment>(
            "org.eclipse.elk.alignment", Alignment.AUTOMATIC);

    /**
     * The desired aspect ratio of a parent node. The algorithm should try to arrange the graph in
     * such a way that the width / height ratio of the resulting drawing approximates the given
     * value.
     */
    public static final IProperty<Float> ASPECT_RATIO = new Property<Float>(
            "org.eclipse.elk.aspectRatio", 0f);

    /**
     * The bend points of an edge. This is used by the
     * {@link org.eclipse.elk.core.util.FixedLayoutProvider} to specify a pre-defined routing for an
     * edge. The vector chain must include the source point, any bend points, and the target point.
     */
    public static final IProperty<KVectorChain> BEND_POINTS = new Property<KVectorChain>(
            "org.eclipse.elk.bendPoints");

    /**
     * Spacing of the content of a parent node to its inner border. The inner border is the node
     * border, which is given by width and height, with subtracted insets.
     */
    public static final IProperty<Float> BORDER_SPACING = new Property<Float>(
            "org.eclipse.elk.borderSpacing", -1.0f);

    /**
     * Whether the algorithm should run in debug mode for the content of a parent node.
     */
    public static final IProperty<Boolean> DEBUG_MODE = new Property<Boolean>(
            "org.eclipse.elk.debugMode", false);

    /**
     * The overall direction of layout: right, left, down, or up.
     */
    public static final IProperty<Direction> DIRECTION = new Property<Direction>(
            "org.eclipse.elk.direction", Direction.UNDEFINED);

    /**
     * What kind of edge routing style should be applied for the content of a parent node.
     * Algorithms may also set this option to single edges in order to mark them as splines. The
     * bend point list of edges with this option set to {@link EdgeRouting#SPLINES} must be
     * interpreted as control points for a piecewise cubic spline.
     */
    public static final IProperty<EdgeRouting> EDGE_ROUTING = new Property<EdgeRouting>(
            "org.eclipse.elk.edgeRouting", EdgeRouting.UNDEFINED);

    /**
     * Whether the size of contained nodes should be expanded to fill the whole area.
     */
    public static final IProperty<Boolean> EXPAND_NODES = new Property<Boolean>(
            "org.eclipse.elk.expandNodes", false);

    /**
     * Whether the algorithm should be run in interactive mode for the content of a parent node.
     * What this means exactly depends on how the specific algorithm interprets this option. Usually
     * in the interactive mode algorithms try to modify the current layout as little as possible.
     */
    public static final IProperty<Boolean> INTERACTIVE = new Property<Boolean>(
            "org.eclipse.elk.interactive", false);

    /**
     * Determines the amount of space to be left around labels.
     */
    public static final IProperty<Float> LABEL_SPACING = new Property<Float>(
            "org.eclipse.elk.labelSpacing", 3.0f, 0.0f);

    /**
     * Whether the whole hierarchy shall be layouted. If this option is not set, each hierarchy
     * level of the graph is processed independently, possibly by different layout algorithms,
     * beginning with the lowest level. If it is set, the algorithm is responsible to process all
     * hierarchy levels that are contained in the associated parent node.
     * 
     * @see GraphFeature#COMPOUND
     */
    public static final IProperty<Boolean> LAYOUT_HIERARCHY = new Property<Boolean>(
            "org.eclipse.elk.layoutHierarchy", false);

    /**
     * The way node labels are placed. Defaults to node labels not being touched.
     */
    public static final IProperty<EnumSet<NodeLabelPlacement>> NODE_LABEL_PLACEMENT =
            new Property<EnumSet<NodeLabelPlacement>>("org.eclipse.elk.nodeLabelPlacement",
                    NodeLabelPlacement.fixed());

    /**
     * The constraints on port positions for the associated node.
     */
    public static final IProperty<PortConstraints> PORT_CONSTRAINTS =
            new Property<PortConstraints>("org.eclipse.elk.portConstraints",
                    PortConstraints.UNDEFINED);

    /**
     * How port labels are placed.
     */
    public static final IProperty<PortLabelPlacement> PORT_LABEL_PLACEMENT =
            new Property<PortLabelPlacement>("org.eclipse.elk.portLabelPlacement",
                    PortLabelPlacement.OUTSIDE);

    /**
     * How much space to leave between ports if their positions are determined by the layout
     * algorithm.
     */
    public static final IProperty<Float> PORT_SPACING = new Property<Float>(
            "org.eclipse.elk.portSpacing", -1f, 0f);

    /**
     * The default port distribution for all sides.
     */
    public static final IProperty<PortAlignment> PORT_ALIGNMENT = new Property<PortAlignment>(
            "org.eclipse.elk.portAlignment", PortAlignment.JUSTIFIED);

    /**
     * The port distribution for northern side.
     */
    public static final IProperty<PortAlignment> PORT_ALIGNMENT_NORTH =
            new Property<PortAlignment>("org.eclipse.elk.portAlignment.north",
                    PortAlignment.UNDEFINED);

    /**
     * The port distribution for southern side.
     */
    public static final IProperty<PortAlignment> PORT_ALIGNMENT_SOUTH =
            new Property<PortAlignment>("org.eclipse.elk.portAlignment.south",
                    PortAlignment.UNDEFINED);

    /**
     * The port distribution for western side.
     */
    public static final IProperty<PortAlignment> PORT_ALIGNMENT_WEST = new Property<PortAlignment>(
            "org.eclipse.elk.portAlignment.west", PortAlignment.UNDEFINED);

    /**
     * The port distribution for eastern side.
     */
    public static final IProperty<PortAlignment> PORT_ALIGNMENT_EAST = new Property<PortAlignment>(
            "org.eclipse.elk.portAlignment.east", PortAlignment.UNDEFINED);

    /**
     * The position of a node, port, or label. This is used by the
     * {@link org.eclipse.elk.core.util.FixedLayoutProvider} to specify a pre-defined position.
     */
    public static final IProperty<KVector> POSITION = new Property<KVector>(
            "org.eclipse.elk.position");

    /**
     * The priority of a graph element. The meaning of this option depends on the specific layout
     * algorithm and the context where it is used.
     */
    public static final IProperty<Integer> PRIORITY = new Property<Integer>(
            "org.eclipse.elk.priority");

    /**
     * A pre-defined seed for pseudo-random number generators. This can be used to control
     * randomized layout algorithms. If the value is 0, the seed shall be determined pseudo-randomly
     * (e.g. from the system time).
     */
    public static final IProperty<Integer> RANDOM_SEED = new Property<Integer>(
            "org.eclipse.elk.randomSeed");

    /**
     * Whether a self loop should be routed around a node or inside that node. The latter will make
     * the node a compound node if it isn't already, and will require the layout algorithm to support
     * compound nodes with hierarchical ports.
     */
    public static final IProperty<Boolean> SELF_LOOP_INSIDE = new Property<Boolean>(
            "org.eclipse.elk.selfLoopInside", false);
    
    /**
     * Property for choosing whether connected components are processed separately.
     */
    public static final IProperty<Boolean> SEPARATE_CC = new Property<Boolean>(
            "org.eclipse.elk.separateConnComp");

    /**
     * Constraints for determining node sizes. Each member of the set specifies something that
     * should be taken into account when calculating node sizes. The empty set corresponds to node
     * sizes being fixed.
     */
    public static final IProperty<EnumSet<SizeConstraint>> SIZE_CONSTRAINT =
            new Property<EnumSet<SizeConstraint>>("org.eclipse.elk.sizeConstraint",
                    SizeConstraint.fixed());

    /**
     * Options modifying the behavior of the size constraints set on a node. Each member of the set
     * specifies something that should be taken into account when calculating node sizes. The empty
     * set corresponds to no further modifications.
     */
    public static final IProperty<EnumSet<SizeOptions>> SIZE_OPTIONS =
            new Property<EnumSet<SizeOptions>>("org.eclipse.elk.sizeOptions", EnumSet.of(
                    SizeOptions.DEFAULT_MINIMUM_SIZE,
                    SizeOptions.APPLY_ADDITIONAL_INSETS));
    
    /**
     * Overall spacing between elements. This is mostly interpreted as the minimal distance between
     * each two nodes and should also influence the spacing between edges.
     */
    public static final IProperty<Float> SPACING = new Property<Float>("org.eclipse.elk.spacing",
            -1f, 0f);

    /**
     * Hide constructor to avoid instantiation.
     */
    private LayoutOptions() {
    }

}
