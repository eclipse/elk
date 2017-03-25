/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.util.nodespacing;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.elk.core.math.ElkRectangle;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.NodeLabelPlacement;
import org.eclipse.elk.core.options.PortAlignment;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortLabelPlacement;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.core.util.adapters.GraphAdapters.GraphAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.LabelAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.NodeAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.PortAdapter;
import org.eclipse.elk.core.util.nodespacing.internal.LabelLocation;
import org.eclipse.elk.core.util.nodespacing.internal.LabelLocationContext;
import org.eclipse.elk.core.util.nodespacing.internal.NodeContext;
import org.eclipse.elk.core.util.nodespacing.internal.PortContext;
import org.eclipse.elk.core.util.nodespacing.internal.ThreeRowsOrColumns;
import org.eclipse.elk.core.util.nodespacing.internal.ThreeRowsOrColumns.OuterSymmetry;
import org.eclipse.elk.core.util.nodespacing.internal.ThreeRowsOrColumns.RowOrColumn;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;


// TODO Add support for FIXED_RATIO port constraints
public class NodeLabelAndSizeCalculator {
    
    /**
     * Copy of the {@link de.cau.cs.kieler.klay.layered.properties.InternalProperties#PORT_RATIO_OR_POSITION}
     * option. For further information see the documentation found there. We added this copy here to
     * allow a generic treatment of spacing calculations for graph elements. See the
     * {@link org.eclipse.elk.core.util.nodespacing} package. [programmatically set]
     * 
     * TODO This should be replaced by a proper property declared in a MELK file.
     */
    public static final IProperty<Double> PORT_RATIO_OR_POSITION = new Property<Double>(
            "portRatioOrPosition", 0.0);
    
    
    /**
     * Processes all direct children of the given graph.
     * 
     * @param graph the graph.
     */
    public void process(final GraphAdapter<?> graph) {
        // Process all of the graph's direct children
        graph.getNodes().forEach(node -> process(graph, node));
    }
    
    /**
     * Processes the given node which is assumed to be a child of the given graph. Note that this method does not
     * check whether or not this is the case. The worst that can happen, however, is that wrong spacing values are
     * applied during processing.
     * 
     * @param graph the node's parent graph.
     * @param node the node to process.
     */
    public void process(final GraphAdapter<?> graph, final NodeAdapter<?> node) {
        // Note that, upon Miro's request, each phase of the algorithm was given a code name in the first version of
        // this code. We happily carry on fulfilling this request in the second version.

        /* PREPARATIONS:
         * Create new context object containing all relevant context information. The different method calls will
         * often just update information in the context object that subsequent method calls will make use of. Creating
         * this context object will already cause it to initialize some information contained in it for more
         * convenient access.
         */
        NodeContext nodeContext = new NodeContext(graph, node);
        
        
        /* SETUP LABEL GROUP RECTANGLES
         * Each port as well as the node itself may have multiple labels. To allow most of the code to not worry about
         * them, those we group them and then remember the size they will take up in rectangles. This applies to all
         * possible node label locations (9 inside, 12 outside) as well as for all ports. Rectangles can be null if
         * there are no labels at a particular location.
         */
        setupPortLabelRectangles(nodeContext);
        setupNodeLabelRectangles(nodeContext);
        
        
        /* PORTS EXTENDING INTO THE NODE
         * Ports can have a negative port border offset, which will cause them to extend into the node itself. We need
         * to remember how far to move inside port labels inwards and to extend the node size to adjust for the ports.
         */
        calculateInsidePortSpace(nodeContext);
        
        
        /* CLIENT AREA AND INSIDE NODE LABELS
         * The client area may have a minimum size, which then needs to be taken into account. Also, inside and
         * outside node labels may influence the node size, which, if they to, need to be taken into account as well.
         * The following methods handle these cases by setting up the inside node label columns and rows of the node
         * context. Also, outside node labels can influence the node size. The corresponding ThreeRowsOrColumns
         * objects are created and set up in these methods, but only if the corresponding minimum size constraints
         * are set. If not, the object references remain null.
         */
        calculateMinimumClientAreaSize(nodeContext);
        calculateNodeLabelSize(nodeContext);
        
        
        /* EASTERN AND WESTERN INSIDE PORT LABELS
         * If port labels are to be placed on the inside, the space required for eastern and western labels can now
         * be calculated.
         */
        calculateInsidePortLabelsAreasEastWest(nodeContext);
        
        
        /* MINIMUM SPACE REQUIRED TO PLACE PORTS
         * It is now time to find out how large the node needs to be if all ports are to be placed in a way that
         * satisfies all spacing constraints. This may or may not include the labels of ports.
         */
        calculateHorizontalNodeSizeRequiredByPorts(nodeContext);
        calculateVerticalNodeSizeRequiredByPorts(nodeContext);
        
        
        /* SET NODE WIDTH AND PLACE HORIZONTAL PORTS
         * We can now set the node's width and place the ports (and port labels) along the horizontal sides. Since we
         * have no idea how large the node is going to be yet, we place southern ports with the assumption that the
         * node has a height of 0. We will later have to offset those ports by the node's height.
         */
        setNodeWidth(nodeContext);
        placeHorizontalPortsAndPortLabels(nodeContext);
        
        
        // TODO Ensure somewhere that the node's minimum size is respected if that applies to the whole node
        //      instead of only the center area
        
        // TODO Offset southern ports by the node's height
        
        // TODO This code currently requires ports to have their port side explicitly set. Document somewhere?
        
        // TODO In case of FIXED_RATIO, this code currently requires ports to have their ratio set. Document somewhere?
        
        nodeContext.node.setSize(new KVector(nodeContext.node.getSize().x, 50));
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // LABEL GROUPING

    /**
     * Initializes {@link PortContext#labelSpace} for each of the node's ports.
     */
    private void setupPortLabelRectangles(final NodeContext nodeContext) {
        // If port labels are fixed instead of being inside or outside, we simply ignore all labels by not inviting
        // them to our little party over here
        if (nodeContext.portLabelsPlacement == PortLabelPlacement.FIXED) {
            return;
        }
        
        nodeContext.portContexts.values().forEach(portContext -> setupPortLabelRectangles(portContext));
    }
    
    /**
     * Initializes {@link PortContext#labelSpace} for the port.
     */
    private void setupPortLabelRectangles(final PortContext portContext) {
        ElkRectangle labelRectangle = new ElkRectangle();
        int labelCount = 0;
        
        // Iterate over the port's labels and add the required space to the label rectangle (space between labels
        // will be added afterwards)
        for (LabelAdapter<?> label : portContext.port.getLabels()) {
            labelCount++;
            
            KVector labelSize = label.getSize();
            labelRectangle.height += labelSize.y;
            labelRectangle.width = Math.max(labelRectangle.width, labelSize.x);
        }
        
        // Include label-label space if there are labels between which there could be space...
        if (labelCount > 1) {
            labelRectangle.height += portContext.parentNodeContext.labelLabelSpacing * (labelCount - 1);
        }
        
        portContext.labelSpace = labelRectangle;
    }
    
    /**
     * Adds each label to the correct label location context and calculate the size of the context's label space
     * rectangle.
     */
    private void setupNodeLabelRectangles(final NodeContext nodeContext) {
        // Go over all of the node's labels (even if the node's default node label placement is to not place the
        // buggers, individual labels may have a proper node label location assigned, so we always need to iterate
        // over them all)
        for (LabelAdapter<?> nodeLabel : nodeContext.node.getLabels()) {
            // Find the effective label location
            Set<NodeLabelPlacement> labelPlacement = nodeLabel.hasProperty(CoreOptions.NODE_LABELS_PLACEMENT)
                    ? nodeLabel.getProperty(CoreOptions.NODE_LABELS_PLACEMENT)
                    : nodeContext.nodeLabelPlacement;
            LabelLocation labelLocation = LabelLocation.fromNodeLabelPlacement(labelPlacement);
            
            // If the label has its location fixed, we will ignore it
            if (labelLocation == LabelLocation.UNDEFINED) {
                continue;
            }
            
            // Retrieve the associated label location context and add the label to it
            LabelLocationContext labelLocationContext = nodeContext.labelLocationContexts.get(labelLocation);
            labelLocationContext.labels.add(nodeLabel);
            
            // Ensure there is a label space rectangle
            if (labelLocationContext.labelSpace == null) {
                labelLocationContext.labelSpace = new ElkRectangle();
            }
            
            // Enlarge the rectangle
            KVector nodeLabelSize = nodeLabel.getSize();
            labelLocationContext.labelSpace.height += nodeLabelSize.y;
            labelLocationContext.labelSpace.width = Math.max(labelLocationContext.labelSpace.width, nodeLabelSize.x);
            
            // If this is not the first label, we need some label-to-label space
            if (labelLocationContext.labels.size() > 1) {
                labelLocationContext.labelSpace.y += nodeContext.labelLabelSpacing;
            }
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PORTS EXTENDING INTO THE NODE
    
    /**
     * Calculates {@link NodeContext#insidePortSpace} for ports that extend into the node.
     */
    private void calculateInsidePortSpace(final NodeContext nodeContext) {
        for (PortContext portContext : nodeContext.portContexts.values()) {
            // If the port extends into the node, ensure the inside port space is enough
            if (portContext.port.hasProperty(CoreOptions.PORT_BORDER_OFFSET)) {
                double portBorderOffset = portContext.port.getProperty(CoreOptions.PORT_BORDER_OFFSET);
                
                if (portBorderOffset < 0) {
                    // The port does extend into the node, by -portBorderOffset
                    switch (portContext.port.getSide()) {
                    case NORTH:
                        nodeContext.insidePortSpace.top = Math.max(
                                nodeContext.insidePortSpace.top,
                                -portBorderOffset);
                        break;
                        
                    case SOUTH:
                        nodeContext.insidePortSpace.bottom = Math.max(
                                nodeContext.insidePortSpace.bottom,
                                -portBorderOffset);
                        break;
                        
                    case EAST:
                        nodeContext.insidePortSpace.right = Math.max(
                                nodeContext.insidePortSpace.right,
                                -portBorderOffset);
                        break;
                        
                    case WEST:
                        nodeContext.insidePortSpace.left = Math.max(
                                nodeContext.insidePortSpace.left,
                                -portBorderOffset);
                        break;
                    }
                }
            }
        }
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // CLIENT AREA AND INSIDE NODE LABELS
    
    /**
     * If the node is setup to have a minimum size for its client area (which, incidentally, it shares with the center
     * inside node labels), this method handles that by configuring {@link NodeContext#insideNodeLabelRows}
     * and {@link NodeContext#insideNodeLabelColumns} appropriately.
     */
    private void calculateMinimumClientAreaSize(final NodeContext nodeContext) {
        // If the node has a minimum size set, and the minimum size only applies to the client area, this is where
        // we apply that size
        if (!nodeContext.sizeConstraints.contains(SizeConstraint.MINIMUM_SIZE)
                || !nodeContext.sizeOptions.contains(SizeOptions.MINIMUM_SIZE_ACCOUNTS_FOR_PADDING)) {
            
            return;
        }
            
        // Retrieve the minimum size
        KVector minSize = new KVector(nodeContext.node.getProperty(CoreOptions.NODE_SIZE_MINIMUM));
        
        // If we are instructed to revert to a default minimum size, we check whether we need to revert to that
        if (nodeContext.sizeOptions.contains(SizeOptions.DEFAULT_MINIMUM_SIZE)) {
            if (minSize.x <= 0) {
                minSize.x = ElkUtil.DEFAULT_MIN_WIDTH;
            }

            if (minSize.y <= 0) {
                minSize.y = ElkUtil.DEFAULT_MIN_HEIGHT;
            }
        }
        
        // Only apply the minimum size if there actually is one
        if (minSize.x > 0 && minSize.y > 0) {
            // Check if the inside row and column objects exist and update the size of the center row or column
            ensureInsideNodeLabelRowsAndColumnsExist(nodeContext);
            nodeContext.insideNodeLabelRows.enlargeIfNecessary(RowOrColumn.CENTER, minSize.y);
            nodeContext.insideNodeLabelColumns.enlargeIfNecessary(RowOrColumn.CENTER, minSize.x);
        }
    }

    /**
     * If node labels influence node size, handles that by delegating to the appropriate inside and outside node
     * label handling methods.
     */
    private void calculateNodeLabelSize(final NodeContext nodeContext) {
        // If we are not supposed to take node labels into account in the first place, simply don't
        if (!nodeContext.sizeConstraints.contains(SizeConstraint.NODE_LABELS)) {
            return;
        }
        
        calculateInsideNodeLabelSize(nodeContext);
        calculateOutsideNodeLabelSize(nodeContext);
    }
    
    /**
     * Handles space required for inside node labels by configuring {@link NodeContext#insideNodeLabelRows}
     * and {@link NodeContext#insideNodeLabelColumns} appropriately.
     */
    private void calculateInsideNodeLabelSize(final NodeContext nodeContext) {
        // Make sure we actually have rows and columns
        ensureInsideNodeLabelRowsAndColumnsExist(nodeContext);
        
        // Go through all nine possible inside label locations (note that if the client area has already been set,
        // this code will not overwrite that, but instead enlarge it if necessary for the inside center labels)
        calculateInsideNodeLabelLocation(LabelLocation.IN_T_L, nodeContext);
        calculateInsideNodeLabelLocation(LabelLocation.IN_T_C, nodeContext);
        calculateInsideNodeLabelLocation(LabelLocation.IN_T_R, nodeContext);
        
        calculateInsideNodeLabelLocation(LabelLocation.IN_C_L, nodeContext);
        calculateInsideNodeLabelLocation(LabelLocation.IN_C_C, nodeContext);
        calculateInsideNodeLabelLocation(LabelLocation.IN_C_R, nodeContext);
        
        calculateInsideNodeLabelLocation(LabelLocation.IN_B_L, nodeContext);
        calculateInsideNodeLabelLocation(LabelLocation.IN_B_C, nodeContext);
        calculateInsideNodeLabelLocation(LabelLocation.IN_B_R, nodeContext);
    }
    
    /**
     * Does the work of {@link #calculateInsideNodeLabelSize(NodeContext)} for a particular label location.
     */
    private void calculateInsideNodeLabelLocation(final LabelLocation labelLocation, final NodeContext nodeContext) {
        // Check if there are labels in the given position
        LabelLocationContext labelLocationContext = nodeContext.labelLocationContexts.get(labelLocation);
        if (labelLocationContext.labels.isEmpty()) {
            return;
        }
        
        nodeContext.insideNodeLabelRows.enlargeIfNecessary(
                labelLocation.getThreeRowsRow(), labelLocationContext.labelSpace.height);
        nodeContext.insideNodeLabelColumns.enlargeIfNecessary(
                labelLocation.getThreeColumnsColumn(), labelLocationContext.labelSpace.width);
    }
    
    /**
     * Creates {@link NodeContext#insideNodeLabelRows} and {@link NodeContext#insideNodeLabelColumns}
     * if they don't exist.
     */
    private void ensureInsideNodeLabelRowsAndColumnsExist(final NodeContext nodeContext) {
        if (nodeContext.insideNodeLabelRows == null) {
            nodeContext.insideNodeLabelRows =
                    new ThreeRowsOrColumns(nodeContext.labelLabelSpacing * 2, OuterSymmetry.SYMMETRICAL);
        }

        if (nodeContext.insideNodeLabelRows == null) {
            nodeContext.insideNodeLabelRows =
                    new ThreeRowsOrColumns(nodeContext.labelLabelSpacing * 2, OuterSymmetry.SYMMETRICAL);
        }
    }
    
    /**
     * Handles space required for outside node labels.
     */
    private void calculateOutsideNodeLabelSize(final NodeContext nodeContext) {
        // Make sure we actually have rows and columns
        ensureOutsideNodeLabelRowsAndColumnsExist(nodeContext);
        
        // Go through all twelve possible outside label locations
        calculateOutsideTopOrBottomNodeLabelLocation(
                LabelLocation.OUT_T_L, nodeContext.outsideTopNodeLabelColumns, nodeContext);
        calculateOutsideTopOrBottomNodeLabelLocation(
                LabelLocation.OUT_T_C, nodeContext.outsideTopNodeLabelColumns, nodeContext);
        calculateOutsideTopOrBottomNodeLabelLocation(
                LabelLocation.OUT_T_R, nodeContext.outsideTopNodeLabelColumns, nodeContext);
        
        calculateOutsideTopOrBottomNodeLabelLocation(
                LabelLocation.OUT_B_L, nodeContext.outsideBottomNodeLabelColumns, nodeContext);
        calculateOutsideTopOrBottomNodeLabelLocation(
                LabelLocation.OUT_B_C, nodeContext.outsideBottomNodeLabelColumns, nodeContext);
        calculateOutsideTopOrBottomNodeLabelLocation(
                LabelLocation.OUT_B_R, nodeContext.outsideBottomNodeLabelColumns, nodeContext);

        calculateOutsideLeftOrRightNodeLabelLocation(
                LabelLocation.OUT_L_T, nodeContext.outsideLeftNodeLabelRows, nodeContext);
        calculateOutsideLeftOrRightNodeLabelLocation(
                LabelLocation.OUT_L_C, nodeContext.outsideLeftNodeLabelRows, nodeContext);
        calculateOutsideLeftOrRightNodeLabelLocation(
                LabelLocation.OUT_L_B, nodeContext.outsideLeftNodeLabelRows, nodeContext);

        calculateOutsideLeftOrRightNodeLabelLocation(
                LabelLocation.OUT_R_T, nodeContext.outsideRightNodeLabelRows, nodeContext);
        calculateOutsideLeftOrRightNodeLabelLocation(
                LabelLocation.OUT_R_C, nodeContext.outsideRightNodeLabelRows, nodeContext);
        calculateOutsideLeftOrRightNodeLabelLocation(
                LabelLocation.OUT_R_B, nodeContext.outsideRightNodeLabelRows, nodeContext);
    }
    
    /**
     * Does the work of {@link #calculateOutsideNodeLabelSize(NodeContext)} for a particular top or bottom label
     * location.
     */
    private void calculateOutsideTopOrBottomNodeLabelLocation(final LabelLocation labelLocation,
            final ThreeRowsOrColumns topOrBottomColumns, final NodeContext nodeContext) {
        
        // Check if there are labels in the given position
        LabelLocationContext labelLocationContext = nodeContext.labelLocationContexts.get(labelLocation);
        if (labelLocationContext.labels.isEmpty()) {
            return;
        }
        
        topOrBottomColumns.enlargeIfNecessary(
                labelLocation.getThreeColumnsColumn(), labelLocationContext.labelSpace.width);
    }
    
    /**
     * Does the work of {@link #calculateOutsideNodeLabelSize(NodeContext)} for a particular left or right label
     * location.
     */
    private void calculateOutsideLeftOrRightNodeLabelLocation(final LabelLocation labelLocation,
            final ThreeRowsOrColumns leftOrRightRows, final NodeContext nodeContext) {
        
        // Check if there are labels in the given position
        LabelLocationContext labelLocationContext = nodeContext.labelLocationContexts.get(labelLocation);
        if (labelLocationContext.labels.isEmpty()) {
            return;
        }
        
        leftOrRightRows.enlargeIfNecessary(
                labelLocation.getThreeRowsRow(), labelLocationContext.labelSpace.height);
    }
    
    /**
     * Creates the rows and columns for outside node labels in {@link NodeContext} if they don't exist.
     */
    private void ensureOutsideNodeLabelRowsAndColumnsExist(final NodeContext nodeContext) {
        if (nodeContext.outsideTopNodeLabelColumns == null) {
            nodeContext.outsideTopNodeLabelColumns =
                    new ThreeRowsOrColumns(nodeContext.labelLabelSpacing * 2, OuterSymmetry.SYMMETRICAL_UNLESS_SINGLE);
        }

        if (nodeContext.outsideBottomNodeLabelColumns == null) {
            nodeContext.outsideBottomNodeLabelColumns =
                    new ThreeRowsOrColumns(nodeContext.labelLabelSpacing * 2, OuterSymmetry.SYMMETRICAL_UNLESS_SINGLE);
        }

        if (nodeContext.outsideLeftNodeLabelRows == null) {
            nodeContext.outsideLeftNodeLabelRows =
                    new ThreeRowsOrColumns(nodeContext.labelLabelSpacing * 2, OuterSymmetry.SYMMETRICAL_UNLESS_SINGLE);
        }

        if (nodeContext.outsideRightNodeLabelRows == null) {
            nodeContext.outsideRightNodeLabelRows =
                    new ThreeRowsOrColumns(nodeContext.labelLabelSpacing * 2, OuterSymmetry.SYMMETRICAL_UNLESS_SINGLE);
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // EASTERN AND WESTERN INSIDE PORT LABELS
    
    /**
     * Calculates the eastern and western inside port label areas.
     */
    private void calculateInsidePortLabelsAreasEastWest(final NodeContext nodeContext) {
        // If we don't take port labels into account when calculating the size, or if port labels are not to be placed
        // inside the node, we're done here
        if (!nodeContext.sizeConstraints.contains(SizeConstraint.PORT_LABELS)
                || nodeContext.portLabelsPlacement != PortLabelPlacement.INSIDE) {
            
            return;
        }
        
        calculateRequiredEasternOrWesternPortLabelSpace(nodeContext, PortSide.EAST);
        calculateRequiredEasternOrWesternPortLabelSpace(nodeContext, PortSide.WEST);
    }
    
    /**
     * Calculates the inside port label space required by nodes on the given side. This method can only be sensibly
     * called if {@code side} is either {@link PortSide#EAST} or {@link PortSide#WEST}.
     */
    private void calculateRequiredEasternOrWesternPortLabelSpace(final NodeContext nodeContext, final PortSide side) {
        double maximumSpace = 0.0;
        
        for (PortContext portContext : nodeContext.portContexts.values()) {
            // Iterate over all ports on the given side
            if (portContext.port.getSide() != side) {
                continue;
            }
            
            // If the port has labels, remember the required space
            if (portContext.labelSpace != null && portContext.labelSpace.width > 0) {
                double spaceRequiredByCurrentPort = nodeContext.portLabelSpacing + portContext.labelSpace.width;
                maximumSpace = Math.max(maximumSpace, spaceRequiredByCurrentPort);
            }
        }
        
        // Ensure the data structure's existence
        ElkRectangle insidePortLabelArea = nodeContext.insidePortLabelAreas.get(PortSide.EAST);
        if (insidePortLabelArea == null) {
            insidePortLabelArea = new ElkRectangle();
            nodeContext.insidePortLabelAreas.put(PortSide.EAST, insidePortLabelArea);
        }
        
        // Just in case the algorithm is changed later thus that the rectangle already existed, we'd better use the
        // maximum here instead of simply setting the value
        insidePortLabelArea.width = Math.max(insidePortLabelArea.width, maximumSpace);
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // MINIMUM SPACE REQUIRED TO PLACE HORIZONTAL PORTS
    
    /**
     * Fills the horizontal component of {@link NodeContext#nodeSizeRequiredByPortPlacement} according to how much
     * space we need to place all ports, possibly including their labels, and respect all spacing constraints in the
     * process.
     */
    private void calculateHorizontalNodeSizeRequiredByPorts(final NodeContext nodeContext) {
        // We need to calculate the space required by the ports even if ports are not part of the size constraints,
        // since we will use that later to place them
        
        // Space northern and southern ports require (we will later take the maximum)
        double northPortSpace = 0;
        double southPortSpace = 0;
        
        // Check how much freedom we have in placing our ports
        switch (nodeContext.portConstraints) {
        case FIXED_POS:
            // We don't have any freedom at all, so simply calculate where the rightmost port is on each side
            northPortSpace = calculateHorizontalNodeSizeRequiredByFixedPosPorts(nodeContext, PortSide.NORTH);
            southPortSpace = calculateHorizontalNodeSizeRequiredByFixedPosPorts(nodeContext, PortSide.SOUTH);
            break;
            
        case FIXED_RATIO:
            // We can require the node to be large enough to avoid spacing violations with fixed ratio ports
            northPortSpace = calculateHorizontalNodeSizeRequiredByFixedRatioPorts(nodeContext, PortSide.NORTH);
            southPortSpace = calculateHorizontalNodeSizeRequiredByFixedRatioPorts(nodeContext, PortSide.SOUTH);
            break;
            
        default:
            // If we are free to place things, make the node large enough to place everything properly
            northPortSpace = calculateHorizontalNodeSizeRequiredByFreePorts(nodeContext, PortSide.NORTH);
            southPortSpace = calculateHorizontalNodeSizeRequiredByFreePorts(nodeContext, PortSide.SOUTH);
            break;
        }
        
        // Take the maximum amount of space required by northern and southern ports
        nodeContext.nodeSizeRequiredByPortPlacement.x = Math.max(northPortSpace, southPortSpace);
    }
    
    /**
     * {@link #calculateHorizontalNodeSizeRequiredByPorts(NodeContext)} for {@link PortConstraints#FIXED_POS}.
     */
    private double calculateHorizontalNodeSizeRequiredByFixedPosPorts(final NodeContext nodeContext,
            final PortSide portSide) {
        
        double rightmostPortBorder = 0.0;
        
        // Check all ports on the correct side
        for (PortAdapter<?> port : nodeContext.node.getPorts()) {
            if (port.getSide() == portSide) {
                rightmostPortBorder = Math.max(rightmostPortBorder, port.getPosition().x + port.getSize().x);
            }
        }
        
        // Add surrounding port margins, if existent
        if (nodeContext.surroundingPortMargins != null) {
            rightmostPortBorder += nodeContext.surroundingPortMargins.right;
        }
        
        return rightmostPortBorder;
    }
    
    /**
     * {@link #calculateHorizontalNodeSizeRequiredByPorts(NodeContext)} for {@link PortConstraints#FIXED_RATIO}.
     */
    private double calculateHorizontalNodeSizeRequiredByFixedRatioPorts(final NodeContext nodeContext,
            final PortSide portSide) {
        
        // TODO Implement
        return 0;
    }
    
    /**
     * {@link #calculateHorizontalNodeSizeRequiredByPorts(NodeContext)} for {@link PortConstraints#FIXED_ORDER},
     * {@link PortConstraints#FIXED_SIDE}, and {@link PortConstraints#FREE}.
     */
    private double calculateHorizontalNodeSizeRequiredByFreePorts(final NodeContext nodeContext,
            final PortSide portSide) {
        
        // Handle the common case first: if there are no ports, we're done here
        if (nodeContext.portContexts.get(portSide).size() == 0) {
            return 0.0;
        }
        
        // A few convenience variables
        boolean includePortLabels = nodeContext.sizeConstraints.contains(SizeConstraint.PORT_LABELS);
        boolean twoPorts = nodeContext.portContexts.get(portSide).size() == 2;
        boolean portLabelsOutside = nodeContext.portLabelsPlacement == PortLabelPlacement.OUTSIDE;
        boolean uniformPortSpacing = nodeContext.sizeOptions.contains(SizeOptions.UNIFORM_PORT_SPACING);
        double width = 0.0;
        
        // How we need to calculate things really depends on which situation we find ourselves in...
        if (!includePortLabels || (twoPorts && portLabelsOutside)) {
            // We ignore port labels or we have only two ports whose labels won't be placed between them. The space
            // between the ports is a function of their combined width, number, and the port-port spacing
            width = portWidthPlusPortPortSpacing(nodeContext, portSide, false, false);
            
        } else if (portLabelsOutside) {
            // Each port contributes its own amount of space, along with its labels (except for the rightmost port).
            // If uniform port spacing is requested, we need to apply the longest label width to every port
            if (uniformPortSpacing) {
                double maxLabelWidth = maximumPortLabelWidth(nodeContext, portSide);
                
                // If there is a maximum label width, setup the port margins accordingly
                if (maxLabelWidth > 0) {
                    setHorizontalPortMargins(nodeContext, portSide, false, maxLabelWidth);
                }
                
                // Sum up the width of all ports including their margins, except for the last part (its label is not
                // part of the port area anymore ans is allowed to overhang)
                width = portWidthPlusPortPortSpacing(nodeContext, portSide, true, false);
                
            } else {
                // Setup the port margins to include port labels
                setHorizontalPortMargins(nodeContext, portSide, false, 0);
                
                // Sum up the space required
                width = portWidthPlusPortPortSpacing(nodeContext, portSide, true, false);
            }
            
        } else if (!portLabelsOutside) {
            // Each port is centered above / below its label, so it contributes either itself and port-port spacing
            // or its label's width plus port-port spacing to the whole requested width
            if (uniformPortSpacing) {
                // Since ports could in theory be bigger than labels
                int ports = nodeContext.portContexts.get(portSide).size() - 1;
                double maxPortOrLabelWidth = maximumPortOrLabelWidth(nodeContext, portSide);
                width = maxPortOrLabelWidth * ports + nodeContext.portPortSpacing * (ports - 1);
                
                // If there is a maximum label width, setup the port margins accordingly
                if (maxPortOrLabelWidth > 0) {
                    setHorizontalPortMargins(nodeContext, portSide, true, maxPortOrLabelWidth);
                }
                
            } else {
                // Setup the port margins to include port labels
                setHorizontalPortMargins(nodeContext, portSide, true, 0);
                
                // Sum up the space required
                width = portWidthPlusPortPortSpacing(nodeContext, portSide, true, true);
            }
            
        } else {
            // I don't think this case should ever be reachable.
            assert false;
        }
        
        // If there are port margins, we need to add them
        if (nodeContext.surroundingPortMargins != null) {
            width += nodeContext.surroundingPortMargins.left + nodeContext.surroundingPortMargins.right;
        }
        
        return width;
    }

    /**
     * Finds the maximum width of any port label on the given port side or 0 if there weren't any port labels or ports.
     */
    private double maximumPortLabelWidth(final NodeContext nodeContext, final PortSide portSide) {
        return nodeContext.portContexts.get(portSide).stream()
            .mapToDouble(portContext -> portContext.labelSpace == null ? 0 : portContext.labelSpace.width)
            .max()
            .orElse(0);
    }
    
    /**
     * Finds the maximum width of any port or port label on the given port side or 0 if there weren't any port labels
     * or ports.
     */
    private double maximumPortOrLabelWidth(final NodeContext nodeContext, final PortSide portSide) {
        double maxResult = 0.0;
        
        for (PortContext portContext : nodeContext.portContexts.get(portSide)) {
            if (portContext.labelSpace != null && portContext.labelSpace.x > maxResult) {
                maxResult = portContext.labelSpace.x;
            }
            
            if (portContext.port.getSize().x > maxResult) {
                maxResult = portContext.port.getSize().x;
            }
        }
        
        return maxResult;
    }

    /**
     * Takes all ports on the given side, sums up their width, and inserts port-port spacings between them. The margins
     * of ports can be included as well to include space used for labels.
     */
    private double portWidthPlusPortPortSpacing(final NodeContext nodeContext, final PortSide portSide,
            final boolean includeMargins, final boolean includeMarginsOfLastPort) {
        
        double result = 0;
        
        Iterator<PortContext> portContextIterator = nodeContext.portContexts.get(portSide).iterator();
        while (portContextIterator.hasNext()) {
            PortContext portContext = portContextIterator.next();
            
            // Add the current port's width to our result
            result += portContext.port.getSize().x;
            
            // If we are to include the margins of the current port, do so
            if (includeMargins && (portContextIterator.hasNext() || includeMarginsOfLastPort)) {
                result += portContext.portMargin.left + portContext.portMargin.right;
            }
            
            // If there is another port after this one, include the required spacing
            if (portContextIterator.hasNext()) {
                result += nodeContext.portPortSpacing;
            }
        }
        
        return result;
    }
    
    /**
     * Sets the port margins such that they include the space required for labels. If {@code uniformLabelWidth > 0},
     * not the width of the port's labels is used, but the uniform width.
     */
    private void setHorizontalPortMargins(final NodeContext nodeContext, final PortSide portSide,
            final boolean centered, final double uniformLabelWidth) {
        
        for (PortContext portContext : nodeContext.portContexts.get(portSide)) {
            double labelWidth = uniformLabelWidth > 0 ? uniformLabelWidth : portContext.labelSpace.width;
            
            if (labelWidth > 0) {
                if (centered) {
                    double portWidth = portContext.port.getSize().x;
                    if (labelWidth > portWidth) {
                        double overhang = (labelWidth - portWidth) / 2;
                        portContext.portMargin.left = overhang;
                        portContext.portMargin.right = overhang;
                    }
                } else {
                    portContext.portMargin.right = nodeContext.portLabelSpacing + labelWidth;
                }
            }
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // MINIMUM SPACE REQUIRED TO PLACE VERTICAL PORTS
    
    /**
     * Fills the vertical component of {@link NodeContext#nodeSizeRequiredByPortPlacement} according to how much
     * space we need to place all ports, possibly including their labels, and respect all spacing constraints in the
     * process.
     */
    private void calculateVerticalNodeSizeRequiredByPorts(final NodeContext nodeContext) {
        // We need to calculate the space required by the ports even if ports are not part of the size constraints,
        // since we will use that later to place them
        
        // Space eastern and western ports require (we will later take the maximum)
        double eastPortSpace = 0;
        double westPortSpace = 0;
        
        // Check how much freedom we have in placing our ports
        switch (nodeContext.portConstraints) {
        case FIXED_POS:
            // We don't have any freedom at all, so simply calculate where the bottommost port is on each side
            eastPortSpace = calculateVerticalNodeSizeRequiredByFixedPosPorts(nodeContext, PortSide.EAST);
            westPortSpace = calculateVerticalNodeSizeRequiredByFixedPosPorts(nodeContext, PortSide.WEST);
            break;
            
        case FIXED_RATIO:
            // We can require the node to be large enough to avoid spacing violations with fixed ratio ports
            eastPortSpace = calculateVerticalNodeSizeRequiredByFixedRatioPorts(nodeContext, PortSide.EAST);
            westPortSpace = calculateVerticalNodeSizeRequiredByFixedRatioPorts(nodeContext, PortSide.WEST);
            break;
            
        default:
            // If we are free to place things, make the node large enough to place everything properly
            eastPortSpace = calculateVerticalNodeSizeRequiredByFreePorts(nodeContext, PortSide.EAST);
            westPortSpace = calculateVerticalNodeSizeRequiredByFreePorts(nodeContext, PortSide.WEST);
            break;
        }
        
        // Take the maximum amount of space required by eastern and western ports
        nodeContext.nodeSizeRequiredByPortPlacement.x = Math.max(eastPortSpace, westPortSpace);
    }
    
    /**
     * {@link #calculateVerticalNodeSizeRequiredByPorts(NodeContext)} for {@link PortConstraints#FIXED_POS}.
     */
    private double calculateVerticalNodeSizeRequiredByFixedPosPorts(final NodeContext nodeContext,
            final PortSide portSide) {
        
        double bottommostPortBorder = 0.0;
        
        // Check all ports on the correct side
        for (PortAdapter<?> port : nodeContext.node.getPorts()) {
            if (port.getSide() == portSide) {
                bottommostPortBorder = Math.max(bottommostPortBorder, port.getPosition().y + port.getSize().y);
            }
        }
        
        // Add surrounding port margins, if existent
        if (nodeContext.surroundingPortMargins != null) {
            bottommostPortBorder += nodeContext.surroundingPortMargins.bottom;
        }
        
        return bottommostPortBorder;
    }
    
    /**
     * {@link #calculateVerticalNodeSizeRequiredByPorts(NodeContext)} for {@link PortConstraints#FIXED_RATIO}.
     */
    private double calculateVerticalNodeSizeRequiredByFixedRatioPorts(final NodeContext nodeContext,
            final PortSide portSide) {
        
        // TODO Implement
        return 0;
    }
    
    /**
     * {@link #calculateVerticalNodeSizeRequiredByPorts(NodeContext)} for {@link PortConstraints#FIXED_ORDER},
     * {@link PortConstraints#FIXED_SIDE}, and {@link PortConstraints#FREE}.
     */
    private double calculateVerticalNodeSizeRequiredByFreePorts(final NodeContext nodeContext,
            final PortSide portSide) {
        
        // Handle the common case first: if there are no ports, we're done here
        if (nodeContext.portContexts.get(portSide).size() == 0) {
            return 0.0;
        }
        
        // A few convenience variables
        boolean includePortLabels = nodeContext.sizeConstraints.contains(SizeConstraint.PORT_LABELS);
        boolean twoPorts = nodeContext.portContexts.get(portSide).size() == 2;
        boolean portLabelsOutside = nodeContext.portLabelsPlacement == PortLabelPlacement.OUTSIDE;
        boolean uniformPortSpacing = nodeContext.sizeOptions.contains(SizeOptions.UNIFORM_PORT_SPACING);
        double height = 0.0;
        
        // How we need to calculate things really depends on which situation we find ourselves in...
        if (!includePortLabels || (twoPorts && portLabelsOutside)) {
            // We ignore port labels or we have only two ports whose labels won't be placed between them. The space
            // between the ports is a function of their combined height, number, and the port-port spacing
            height = portWidthPlusPortPortSpacing(nodeContext, portSide, false, false);
            
        } else if (portLabelsOutside) {
            // Each port contributes its own amount of space, along with its labels (except for the bottommost port).
            // If uniform port spacing is requested, we need to apply the highest label height to every port
            if (uniformPortSpacing) {
                double maxLabelHeight = maximumPortLabelHeight(nodeContext, portSide);
                
                // If there is a maximum label width, setup the port margins accordingly
                if (maxLabelHeight > 0) {
                    setVerticalPortMargins(nodeContext, portSide, false, maxLabelHeight);
                }
                
                // Sum up the height of all ports including their margins, except for the last part (its label is not
                // part of the port area anymore ans is allowed to overhang)
                height = portHeightPlusPortPortSpacing(nodeContext, portSide, true, false);
                
            } else {
                // Setup the port margins to include port labels
                setVerticalPortMargins(nodeContext, portSide, false, 0);
                
                // Sum up the space required
                height = portHeightPlusPortPortSpacing(nodeContext, portSide, true, false);
            }
            
        } else if (!portLabelsOutside) {
            // Each port is centered left / right ofits label, so it contributes either itself and port-port spacing
            // or its label's height plus port-port spacing to the whole requested height
            if (uniformPortSpacing) {
                // Since ports could in theory be bigger than labels
                int ports = nodeContext.portContexts.get(portSide).size() - 1;
                double maxPortOrLabelHeight = maximumPortOrLabelHeight(nodeContext, portSide);
                height = maxPortOrLabelHeight * ports + nodeContext.portPortSpacing * (ports - 1);
                
                // If there is a maximum label height, setup the port margins accordingly
                if (maxPortOrLabelHeight > 0) {
                    setVerticalPortMargins(nodeContext, portSide, true, maxPortOrLabelHeight);
                }
                
            } else {
                // Setup the port margins to include port labels
                setVerticalPortMargins(nodeContext, portSide, true, 0);
                
                // Sum up the space required
                height = portWidthPlusPortPortSpacing(nodeContext, portSide, true, true);
            }
            
        } else {
            // I don't think this case should ever be reachable.
            assert false;
        }
        
        // If there are port margins, we need to add them
        if (nodeContext.surroundingPortMargins != null) {
            height += nodeContext.surroundingPortMargins.top + nodeContext.surroundingPortMargins.bottom;
        }
        
        return height;
    }
    
    /**
     * Finds the maximum height of any port label on the given port side or 0 if there weren't any port labels or ports.
     */
    private double maximumPortLabelHeight(final NodeContext nodeContext, final PortSide portSide) {
        return nodeContext.portContexts.get(portSide).stream()
            .mapToDouble(portContext -> portContext.labelSpace == null ? 0 : portContext.labelSpace.height)
            .max()
            .orElse(0);
    }
    
    /**
     * Finds the maximum height of any port or port label on the given port side or 0 if there weren't any port labels
     * or ports.
     */
    private double maximumPortOrLabelHeight(final NodeContext nodeContext, final PortSide portSide) {
        double maxResult = 0.0;
        
        for (PortContext portContext : nodeContext.portContexts.get(portSide)) {
            if (portContext.labelSpace != null && portContext.labelSpace.y > maxResult) {
                maxResult = portContext.labelSpace.y;
            }
            
            if (portContext.port.getSize().y > maxResult) {
                maxResult = portContext.port.getSize().y;
            }
        }
        
        return maxResult;
    }

    /**
     * Takes all ports on the given side, sums up their height, and inserts port-port spacings between them. The margins
     * of ports can be included as well to include space used for labels.
     */
    private double portHeightPlusPortPortSpacing(final NodeContext nodeContext, final PortSide portSide,
            final boolean includeMargins, final boolean includeMarginsOfLastPort) {
        
        double result = 0;
        
        Iterator<PortContext> portContextIterator = nodeContext.portContexts.get(portSide).iterator();
        while (portContextIterator.hasNext()) {
            PortContext portContext = portContextIterator.next();
            
            // Add the current port's height to our result
            result += portContext.port.getSize().y;
            
            // If we are to include the margins of the current port, do so
            if (includeMargins && (portContextIterator.hasNext() || includeMarginsOfLastPort)) {
                result += portContext.portMargin.top + portContext.portMargin.bottom;
            }
            
            // If there is another port after this one, include the required spacing
            if (portContextIterator.hasNext()) {
                result += nodeContext.portPortSpacing;
            }
        }
        
        return result;
    }
    
    /**
     * Takes all ports on the given side, sums up their height, and inserts port-port spacings between them.
     */
    private double portOrLabelHeightPlusPortPortSpacing(final NodeContext nodeContext, final PortSide portSide) {
        double result = 0;
        
        for (PortContext portContext : nodeContext.portContexts.get(portSide)) {
            if (portContext.labelSpace != null && portContext.labelSpace.y > portContext.port.getSize().y) {
                result += portContext.labelSpace.y;
            } else {
                result += portContext.port.getSize().y;
            }
        }
        
        result += nodeContext.portPortSpacing * (nodeContext.portContexts.get(portSide).size() - 1);
        return result;
    }
    
    /**
     * Sets the port margins such that they include the space required for labels. If {@code uniformLabelHeight > 0},
     * not the height of the port's labels is used, but the uniform height.
     */
    private void setVerticalPortMargins(final NodeContext nodeContext, final PortSide portSide,
            final boolean centered, final double uniformLabelHeight) {
        
        for (PortContext portContext : nodeContext.portContexts.get(portSide)) {
            double labelHeight = uniformLabelHeight > 0 ? uniformLabelHeight : portContext.labelSpace.height;
            
            if (labelHeight > 0) {
                if (centered) {
                    double portHeight = portContext.port.getSize().y;
                    if (labelHeight > portHeight) {
                        double overhang = (labelHeight - portHeight) / 2;
                        portContext.portMargin.top = overhang;
                        portContext.portMargin.bottom = overhang;
                    }
                } else {
                    portContext.portMargin.bottom = nodeContext.portLabelSpacing + labelHeight;
                }
            }
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // NODE WIDTH
    
    /**
     * Sets the node's width based on whatever the size constraints are.
     */
    private void setNodeWidth(final NodeContext nodeContext) {
        if (nodeContext.sizeConstraints.isEmpty()) {
            // "When Little Jimmy discovered that there was no actual work to be done, he felt glad to be alive and
            // went to torture helpless rabbits." -- Anonymous
            return;
        }
        
        double width = 0.0;
        
        // The center area which contains inside node labels and the client area may be > 0, in which case it needs
        // to be included in the size (we don't check whether we care for node labels here because they won't be
        // included anyway if we don't, and the client area is included here if a minimum size is imposed on it)
        if (nodeContext.insideNodeLabelColumns != null) {
            width = nodeContext.insideNodeLabelColumns.getSize();
            if (width > 0) {
                width += nodeContext.nodeLabelsPadding.left + nodeContext.nodeLabelsPadding.right;
            }
        }
        
        // If we have inside eastern / western inside port labels, we need to reserve space for those
        if (nodeContext.sizeConstraints.contains(SizeConstraint.PORT_LABELS)) {
            ElkRectangle eastInsidePortLabelArea = nodeContext.insidePortLabelAreas.get(PortSide.EAST);
            if (eastInsidePortLabelArea != null && eastInsidePortLabelArea.width > 0) {
                width += eastInsidePortLabelArea.width + nodeContext.portLabelSpacing;
            }
            
            ElkRectangle westInsidePortLabelArea = nodeContext.insidePortLabelAreas.get(PortSide.WEST);
            if (westInsidePortLabelArea != null && westInsidePortLabelArea.width > 0) {
                width += westInsidePortLabelArea.width + nodeContext.portLabelSpacing;
            }
        }
        
        // If we should reserve space for ports, ...
        if (nodeContext.sizeConstraints.contains(SizeConstraint.PORTS)) {
            // ...include space reserved for ports set into the node
            width += nodeContext.insidePortSpace.left + nodeContext.insidePortSpace.right;
            
            // ...make sure horizontal ports have enough space to be placed
            width = Math.max(width, nodeContext.nodeSizeRequiredByPortPlacement.x);
        }
        
        // If we have a minimum node width, apply that
        if (nodeContext.sizeConstraints.contains(SizeConstraint.MINIMUM_SIZE)
                && !nodeContext.sizeOptions.contains(SizeOptions.MINIMUM_SIZE_ACCOUNTS_FOR_PADDING)) {
            
            // Retrieve the minimum size
            KVector minSize = new KVector(nodeContext.node.getProperty(CoreOptions.NODE_SIZE_MINIMUM));
            
            // If we are instructed to revert to a default minimum size, we check whether we need to revert to that
            if (nodeContext.sizeOptions.contains(SizeOptions.DEFAULT_MINIMUM_SIZE)) {
                if (minSize.x <= 0) {
                    minSize.x = ElkUtil.DEFAULT_MIN_WIDTH;
                }
            }
            
            // Only apply the minimum size if there actually is one
            if (minSize.x > 0) {
                width = Math.max(width, minSize.x);
            }
        }
        
        // Apply new width
        nodeContext.node.setSize(new KVector(width, nodeContext.node.getSize().y));
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // HORIZONTAL PORT AND PORT LABEL PLACEMENT
    
    /**
     * Places ports and port labels on the node's northern and southern border. Since we don't necessarily know how
     * large the node is going to be, we place southern ports with the assumption that the node's southern border is
     * at {@code y == 0}. They will have to be offset by the node's height later.
     */
    private void placeHorizontalPortsAndPortLabels(final NodeContext nodeContext) {
        // How we are going to place the ports depends on their constraints
        switch (nodeContext.portConstraints) {
        case FIXED_POS:
            placeHorizontalFixedPosPorts(nodeContext, PortSide.NORTH);
            placeHorizontalFixedPosPorts(nodeContext, PortSide.SOUTH);
            break;
            
        case FIXED_RATIO:
            placeHorizontalFixedRatioPorts(nodeContext, PortSide.NORTH);
            placeHorizontalFixedRatioPorts(nodeContext, PortSide.SOUTH);
            break;
            
        default:
            placeHorizontalFreePorts(nodeContext, PortSide.NORTH);
            placeHorizontalFreePorts(nodeContext, PortSide.SOUTH);
            break;
        }
        
        // TODO Place labels
    }

    /**
     * Places ports on the northern and southern side for the {@link PortConstraints#FIXED_POS} case.
     */
    private void placeHorizontalFixedPosPorts(final NodeContext nodeContext, final PortSide portSide) {
        for (PortContext portContext : nodeContext.portContexts.get(portSide)) {
            PortAdapter<?> port = portContext.port;
            KVector portPosition = port.getPosition();
            
            // The port's x coordinate is already fixed anyway, so simply adjust its y coordinate according to its
            // offset, if any
            portPosition.y = calculateHorizontalPortYCoordinate(port);
            
            portContext.port.setPosition(portPosition);
        }
    }

    /**
     * Places ports on the northern and southern side for the {@link PortConstraints#FIXED_RATIO} case.
     */
    private void placeHorizontalFixedRatioPorts(final NodeContext nodeContext, final PortSide portSide) {
        final double nodeWidth = nodeContext.node.getSize().x;
        
        for (PortContext portContext : nodeContext.portContexts.get(portSide)) {
            PortAdapter<?> port = portContext.port;
            KVector portPosition = new KVector();
            
            // The x coordinate is a function of the node's width and the port's position ratio
            portPosition.x = nodeWidth * port.getProperty(PORT_RATIO_OR_POSITION);
            portPosition.y = calculateHorizontalPortYCoordinate(port);
            
            portContext.port.setPosition(portPosition);
        }
    }

    /**
     * Places ports on the northern and southern side for the unconstrained cases.
     */
    private void placeHorizontalFreePorts(final NodeContext nodeContext, final PortSide portSide) {
        // If there are no ports on the given side, abort
        if (nodeContext.portContexts.get(portSide).isEmpty()) {
            return;
        }
        
        // Note that we don't have to distinguish any cases here because the port margins already include space
        // required for labels, if such space is to be reserved. Yay!
        PortAlignment portAlignment = nodeContext.getPortAlignment(portSide);
        double currentXPos = nodeContext.surroundingPortMargins.left;
        double spaceBetweenPorts = nodeContext.portPortSpacing;
        
        // Calculate where we need to start placing ports (note that the node size required by the port placement
        // includes left and right surrounding port margins, which changes the formulas a bit from what you'd
        // otherwise expect)
        switch (portAlignment) {
        case BEGIN:
            // There's nothing to do here
            break;
            
        case CENTER:
            currentXPos = nodeContext.surroundingPortMargins.left
                    + (nodeContext.node.getSize().x - nodeContext.nodeSizeRequiredByPortPlacement.x) / 2;
            break;
            
        case END:
            currentXPos = nodeContext.node.getSize().x
                    - nodeContext.nodeSizeRequiredByPortPlacement.x
                    + nodeContext.surroundingPortMargins.left;
            break;
            
        case JUSTIFIED:
            currentXPos = nodeContext.surroundingPortMargins.left;
            spaceBetweenPorts = (nodeContext.node.getSize().x - nodeContext.nodeSizeRequiredByPortPlacement.x)
                    / (nodeContext.portContexts.get(portSide).size());
            break;
        }
        
        // Iterate over all ports and place them
        for (PortContext portContext : nodeContext.portContexts.get(portSide)) {
            PortAdapter<?> port = portContext.port;
            KVector portPosition = new KVector();
            
            // The x coordinate is a function of the node's width and the port's position ratio
            portPosition.x = currentXPos + portContext.portMargin.left;
            portPosition.y = calculateHorizontalPortYCoordinate(port);
            
            portContext.port.setPosition(portPosition);
            
            // Update the x coordinate for the next port
            currentXPos += portContext.portMargin.left
                    + port.getSize().x
                    + portContext.portMargin.right
                    + spaceBetweenPorts;
        }
    }

    /**
     * Returns the y coordinate for the given port.
     */
    private double calculateHorizontalPortYCoordinate(final PortAdapter<?> port) {
        // The y coordinate is set according to the port's offset, if any
        if (port.hasProperty(CoreOptions.PORT_BORDER_OFFSET)) {
            return port.getSide() == PortSide.NORTH
                    ? -port.getSize().y - port.getProperty(CoreOptions.PORT_BORDER_OFFSET)
                    : port.getProperty(CoreOptions.PORT_BORDER_OFFSET);
        } else {
            return port.getSide() == PortSide.NORTH
                    ? -port.getSize().y
                    : 0;
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
