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

import java.util.Set;

import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.math.ElkRectangle;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.NodeLabelPlacement;
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

public class NodeLabelAndSizeCalculator {
    
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
        handleMinimumClientAreaSize(nodeContext);
        handleNodeLabelSize(nodeContext);
        
        
        /* EASTERN AND WESTERN INSIDE PORT LABELS
         * If port labels are to be placed on the inside, the space required for eastern and western labels can now
         * be calculated.
         */
        handleInsidePortLabelsAreasEastWest(nodeContext);
        
        
        /* PORTS AND PORT LABELS
         * Depending on the size constraints, ports and port labels will influence the node's size. If they do, the
         * following call will calculate how much space they need on each side, and possibly inside the node as well
         * if we have inside port labels.
         */
        calculatePortAndPortLabelSpace(nodeContext);
        
        
        // TODO Ensure somewhere that the node's minimum size is respected if that applies to the whole node
        //      instead of only the center area
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
     * Calculates {@link NodeContext#requiredInsidePortSpace} for ports that extend into the node.
     */
    private void calculateInsidePortSpace(final NodeContext nodeContext) {
        nodeContext.requiredInsidePortSpace = new ElkPadding();
        
        for (PortContext portContext : nodeContext.portContexts.values()) {
            // If the port extends into the node, ensure the inside port space is enough
            if (portContext.port.hasProperty(CoreOptions.PORT_BORDER_OFFSET)) {
                double portBorderOffset = portContext.port.getProperty(CoreOptions.PORT_BORDER_OFFSET);
                
                if (portBorderOffset < 0) {
                    // The port does extend into the node, by -portBorderOffset
                    switch (portContext.port.getSide()) {
                    case NORTH:
                        nodeContext.requiredInsidePortSpace.top = Math.max(
                                nodeContext.requiredInsidePortSpace.top,
                                -portBorderOffset);
                        break;
                        
                    case SOUTH:
                        nodeContext.requiredInsidePortSpace.bottom = Math.max(
                                nodeContext.requiredInsidePortSpace.bottom,
                                -portBorderOffset);
                        break;
                        
                    case EAST:
                        nodeContext.requiredInsidePortSpace.right = Math.max(
                                nodeContext.requiredInsidePortSpace.right,
                                -portBorderOffset);
                        break;
                        
                    case WEST:
                        nodeContext.requiredInsidePortSpace.left = Math.max(
                                nodeContext.requiredInsidePortSpace.left,
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
     * inside node labels), this method handles that by configuring {@link NodeContext#requiredInsideNodeLabelRows}
     * and {@link NodeContext#requiredInsideNodeLabelColumns} appropriately.
     */
    private void handleMinimumClientAreaSize(final NodeContext nodeContext) {
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
            nodeContext.requiredInsideNodeLabelRows.enlargeIfNecessary(RowOrColumn.CENTER, minSize.y);
            nodeContext.requiredInsideNodeLabelColumns.enlargeIfNecessary(RowOrColumn.CENTER, minSize.x);
        }
    }

    /**
     * If node labels influence node size, handles that by delegating to the appropriate inside and outside node
     * label handling methods.
     */
    private void handleNodeLabelSize(final NodeContext nodeContext) {
        // If we are not supposed to take node labels into account in the first place, simply don't
        if (!nodeContext.sizeConstraints.contains(SizeConstraint.NODE_LABELS)) {
            return;
        }
        
        handleInsideNodeLabelSize(nodeContext);
        handleOutsideNodeLabelSize(nodeContext);
    }
    
    /**
     * Handles space required for inside node labels by configuring {@link NodeContext#requiredInsideNodeLabelRows}
     * and {@link NodeContext#requiredInsideNodeLabelColumns} appropriately.
     */
    private void handleInsideNodeLabelSize(final NodeContext nodeContext) {
        // Make sure we actually have rows and columns
        ensureInsideNodeLabelRowsAndColumnsExist(nodeContext);
        
        // Go through all nine possible inside label locations (note that if the client area has already been set,
        // this code will not overwrite that, but instead enlarge it if necessary for the inside center labels)
        handleInsideNodeLabelLocation(LabelLocation.IN_T_L, nodeContext);
        handleInsideNodeLabelLocation(LabelLocation.IN_T_C, nodeContext);
        handleInsideNodeLabelLocation(LabelLocation.IN_T_R, nodeContext);
        
        handleInsideNodeLabelLocation(LabelLocation.IN_C_L, nodeContext);
        handleInsideNodeLabelLocation(LabelLocation.IN_C_C, nodeContext);
        handleInsideNodeLabelLocation(LabelLocation.IN_C_R, nodeContext);
        
        handleInsideNodeLabelLocation(LabelLocation.IN_B_L, nodeContext);
        handleInsideNodeLabelLocation(LabelLocation.IN_B_C, nodeContext);
        handleInsideNodeLabelLocation(LabelLocation.IN_B_R, nodeContext);
    }
    
    /**
     * Does the work of {@link #handleInsideNodeLabelSize(NodeContext)} for a particular label location.
     */
    private void handleInsideNodeLabelLocation(final LabelLocation labelLocation, final NodeContext nodeContext) {
        // Check if there are labels in the given position
        LabelLocationContext labelLocationContext = nodeContext.labelLocationContexts.get(labelLocation);
        if (labelLocationContext.labels.isEmpty()) {
            return;
        }
        
        nodeContext.requiredInsideNodeLabelRows.enlargeIfNecessary(
                labelLocation.getThreeRowsRow(), labelLocationContext.labelSpace.height);
        nodeContext.requiredInsideNodeLabelColumns.enlargeIfNecessary(
                labelLocation.getThreeColumnsColumn(), labelLocationContext.labelSpace.width);
    }
    
    /**
     * Creates {@link NodeContext#requiredInsideNodeLabelRows} and {@link NodeContext#requiredInsideNodeLabelColumns}
     * if they don't exist.
     */
    private void ensureInsideNodeLabelRowsAndColumnsExist(final NodeContext nodeContext) {
        if (nodeContext.requiredInsideNodeLabelRows == null) {
            nodeContext.requiredInsideNodeLabelRows =
                    new ThreeRowsOrColumns(nodeContext.labelLabelSpacing * 2, OuterSymmetry.SYMMETRICAL);
        }

        if (nodeContext.requiredInsideNodeLabelRows == null) {
            nodeContext.requiredInsideNodeLabelRows =
                    new ThreeRowsOrColumns(nodeContext.labelLabelSpacing * 2, OuterSymmetry.SYMMETRICAL);
        }
    }
    
    /**
     * Handles space required for outside node labels.
     */
    private void handleOutsideNodeLabelSize(final NodeContext nodeContext) {
        // Make sure we actually have rows and columns
        ensureOutsideNodeLabelRowsAndColumnsExist(nodeContext);
        
        // Go through all twelve possible outside label locations
        handleOutsideTopOrBottomNodeLabelLocation(
                LabelLocation.OUT_T_L, nodeContext.requiredOutsideTopNodeLabelColumns, nodeContext);
        handleOutsideTopOrBottomNodeLabelLocation(
                LabelLocation.OUT_T_C, nodeContext.requiredOutsideTopNodeLabelColumns, nodeContext);
        handleOutsideTopOrBottomNodeLabelLocation(
                LabelLocation.OUT_T_R, nodeContext.requiredOutsideTopNodeLabelColumns, nodeContext);
        
        handleOutsideTopOrBottomNodeLabelLocation(
                LabelLocation.OUT_B_L, nodeContext.requiredOutsideBottomNodeLabelColumns, nodeContext);
        handleOutsideTopOrBottomNodeLabelLocation(
                LabelLocation.OUT_B_C, nodeContext.requiredOutsideBottomNodeLabelColumns, nodeContext);
        handleOutsideTopOrBottomNodeLabelLocation(
                LabelLocation.OUT_B_R, nodeContext.requiredOutsideBottomNodeLabelColumns, nodeContext);

        handleOutsideLeftOrRightNodeLabelLocation(
                LabelLocation.OUT_L_T, nodeContext.requiredOutsideLeftNodeLabelRows, nodeContext);
        handleOutsideLeftOrRightNodeLabelLocation(
                LabelLocation.OUT_L_C, nodeContext.requiredOutsideLeftNodeLabelRows, nodeContext);
        handleOutsideLeftOrRightNodeLabelLocation(
                LabelLocation.OUT_L_B, nodeContext.requiredOutsideLeftNodeLabelRows, nodeContext);

        handleOutsideLeftOrRightNodeLabelLocation(
                LabelLocation.OUT_R_T, nodeContext.requiredOutsideRightNodeLabelRows, nodeContext);
        handleOutsideLeftOrRightNodeLabelLocation(
                LabelLocation.OUT_R_C, nodeContext.requiredOutsideRightNodeLabelRows, nodeContext);
        handleOutsideLeftOrRightNodeLabelLocation(
                LabelLocation.OUT_R_B, nodeContext.requiredOutsideRightNodeLabelRows, nodeContext);
    }
    
    /**
     * Does the work of {@link #handleOutsideNodeLabelSize(NodeContext)} for a particular top or bottom label location.
     */
    private void handleOutsideTopOrBottomNodeLabelLocation(final LabelLocation labelLocation,
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
     * Does the work of {@link #handleOutsideNodeLabelSize(NodeContext)} for a particular left or right label location.
     */
    private void handleOutsideLeftOrRightNodeLabelLocation(final LabelLocation labelLocation,
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
        if (nodeContext.requiredOutsideTopNodeLabelColumns == null) {
            nodeContext.requiredOutsideTopNodeLabelColumns =
                    new ThreeRowsOrColumns(nodeContext.labelLabelSpacing * 2, OuterSymmetry.SYMMETRICAL_UNLESS_SINGLE);
        }

        if (nodeContext.requiredOutsideBottomNodeLabelColumns == null) {
            nodeContext.requiredOutsideBottomNodeLabelColumns =
                    new ThreeRowsOrColumns(nodeContext.labelLabelSpacing * 2, OuterSymmetry.SYMMETRICAL_UNLESS_SINGLE);
        }

        if (nodeContext.requiredOutsideLeftNodeLabelRows == null) {
            nodeContext.requiredOutsideLeftNodeLabelRows =
                    new ThreeRowsOrColumns(nodeContext.labelLabelSpacing * 2, OuterSymmetry.SYMMETRICAL_UNLESS_SINGLE);
        }

        if (nodeContext.requiredOutsideRightNodeLabelRows == null) {
            nodeContext.requiredOutsideRightNodeLabelRows =
                    new ThreeRowsOrColumns(nodeContext.labelLabelSpacing * 2, OuterSymmetry.SYMMETRICAL_UNLESS_SINGLE);
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // EASTERN AND WESTERN INSIDE PORT LABELS
    
    /**
     * Calculates the eastern and western inside port label areas.
     */
    private void handleInsidePortLabelsAreasEastWest(final NodeContext nodeContext) {
        // If we don't take port labels into account when calculating the size, or if port labels are not to be placed
        // inside the node, we're done here
        if (!nodeContext.sizeConstraints.contains(SizeConstraint.PORT_LABELS)
                || nodeContext.portLabelsPlacement != PortLabelPlacement.INSIDE) {
            
            return;
        }
        
        // Ensure the data structure's existence
        if (nodeContext.requiredInsidePortLabelSpace == null) {
            nodeContext.requiredInsidePortLabelSpace = new ElkPadding();
        }
        
        nodeContext.requiredInsidePortLabelSpace.right =
                calculateRequiredEasternOrWesternPortLabelSpace(nodeContext, PortSide.EAST);
        nodeContext.requiredInsidePortLabelSpace.left =
                calculateRequiredEasternOrWesternPortLabelSpace(nodeContext, PortSide.WEST);
    }
    
    /**
     * Calculates the inside port label space required by nodes on the given side. This method can only be sensibly
     * called if {@code side} is either {@link PortSide#EAST} or {@link PortSide#WEST}.
     */
    private double calculateRequiredEasternOrWesternPortLabelSpace(final NodeContext nodeContext, final PortSide side) {
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
        
        return maximumSpace;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PORT PLACEMENT
    
    private void calculatePortAndPortLabelSpace(final NodeContext nodeContext) {
        // If ports don't influence node size, don't even bother
        if (!nodeContext.sizeConstraints.contains(SizeConstraint.PORTS)) {
            return;
        }
        
        boolean includeLabels = nodeContext.sizeConstraints.contains(SizeConstraint.PORT_LABELS);
        boolean freePortPlacement = !nodeContext.portConstraints.isRatioFixed()
                && !nodeContext.portConstraints.isPosFixed();
        
        // TODO Implement
    }

    
}
