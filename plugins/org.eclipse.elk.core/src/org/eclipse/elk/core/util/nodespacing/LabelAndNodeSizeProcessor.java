/*******************************************************************************
 * Copyright (c) 2014, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.util.nodespacing;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.CoreOptions;
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
import org.eclipse.elk.core.util.labelspacing.LabelGroup;
import org.eclipse.elk.core.util.labelspacing.LabelLocation;
import org.eclipse.elk.core.util.labelspacing.LabelSpaceCalculation;
import org.eclipse.elk.core.util.labelspacing.TextAlignment;
import org.eclipse.elk.core.util.nodespacing.Spacing.Insets;
import org.eclipse.elk.core.util.nodespacing.Spacing.Margins;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

import com.google.common.collect.ImmutableList;

/**
 * Calculates node sizes, places ports, and places node and port labels.
 *
 * @see LabelSideSelector
 * @author cds
 * @author uru
 * @author csp
 */
@SuppressWarnings("incomplete-switch")
public class LabelAndNodeSizeProcessor {

    /**
     * Copy of the
     * {@link de.cau.cs.kieler.klay.layered.properties.InternalProperties#PORT_RATIO_OR_POSITION}
     * option. For further information see the documentation found there. We added this copy here to
     * allow a generic treatment of spacing calculations for graph elements. See the
     * {@link de.cau.cs.kieler.kiml.util.nodespacing} package. [programmatically set]
     */
    public static final IProperty<Double> PORT_RATIO_OR_POSITION = new Property<Double>(
            "portRatioOrPosition", 0.0);

    /*
     * Entry point
     */
    /**
     * {@inheritDoc}
     */
    public void process(final GraphAdapter<?> layeredGraph) {
        final double labelSpacing = layeredGraph.getProperty(CoreOptions.SPACING_LABEL).doubleValue();

        // Iterate over all the graph's nodes
        for (final NodeAdapter<?> node : layeredGraph.getNodes()) {
            /* Note that, upon Miro's request, each phase of the algorithm was given a code name. */

            /*
             * PREPARATIONS:
             * Create new NodeData containing all relevant context information.
             */
            final NodeData data = new NodeData(node);
            data.labelSpacing = labelSpacing;
            data.portSpacing = node.getProperty(CoreOptions.SPACING_PORT).doubleValue();
            data.nodeLabelInsets = node.getProperty(CoreOptions.NODE_LABELS_INSETS);

            /*
             * PHASE 1 (SAD DUCK):
             * PLACE PORT LABELS Port labels are placed and port margins are calculated.
             */
            final PortLabelPlacement labelPlacement =
                    node.getProperty(CoreOptions.PORT_LABELS_PLACEMENT);
            final boolean compoundNodeMode = node.isCompoundNode();

            // Place port labels and calculate the margins
            for (final PortAdapter<?> port : node.getPorts()) {
                placePortLabels(port, labelPlacement, compoundNodeMode, labelSpacing);
                calculateAndSetPortMargins(port);
            }

            // Count ports on each side and calculate how much space they require
            calculatePortInformation(data, node.getProperty(CoreOptions.NODE_SIZE_CONSTRAINTS)
                    .contains(SizeConstraint.PORT_LABELS));

            /*
             * PHASE 2 (DYNAMIC DONALD):
             * CALCULATE INSETS We know the sides the ports will be placed at and we know where node
             * labels are to be placed. Calculate the node's insets accordingly. Also compute the
             * amount of space the node labels will need if stacked vertically. Note that we don't
             * have to know the final position of ports and of node labels to calculate all this
             * stuff.
             * 
             * IMPORTANT NOTE:
             * From this point on, the labels' ID fields are used to assign the location of the labels.
             */
            calculateRequiredPortLabelSpace(data);
            calculateRequiredNodeLabelSpace(data);

            /*
             * PHASE 3 (DANGEROUS DUCKLING):
             * RESIZE NODE If the node has labels, the node insets might have to be adjusted to
             * reserve space for them, which is what this phase does.
             */
            resizeNode(data);

            /*
             * PHASE 4 (DUCK AND COVER):
             * PLACE PORTS The node is resized, taking all node size constraints into account. The
             * port spacing is not required for port placement since the placement will be based on
             * the node's size (if it is not fixed anyway).
             */
            placePorts(data);

            /*
             * PHASE 5 (HAPPY DUCK):
             * PLACE NODE LABELS With space reserved for the node labels, the labels are placed.
             */
            placeNodeLabels(data);

            /*
             * CLEANUP (THANKSGIVING):
             * SET NODE INSETS Set the node insets to include space required for port and node
             * labels. If the labels were not taken into account when calculating the node's size,
             * this may result in insets that, taken together, are larger than the node's actual
             * size.
             */
            final Insets nodeInsets = new Insets(node.getInsets());
            nodeInsets.left = data.requiredNodeLabelSpace.left + data.requiredPortLabelSpace.left;
            nodeInsets.right =
                    data.requiredNodeLabelSpace.right + data.requiredPortLabelSpace.right;
            nodeInsets.top = data.requiredNodeLabelSpace.top + data.requiredPortLabelSpace.top;
            nodeInsets.bottom =
                    data.requiredNodeLabelSpace.bottom + data.requiredPortLabelSpace.bottom;
            node.setInsets(nodeInsets);
        }
    }

    // /////////////////////////////////////////////////////////////////////////////
    // PORT LABEL PLACEMENT

    /**
     * Places the labels of the given port, if any.
     *
     * @param port
     *            the port whose labels to place.
     * @param placement
     *            the port label placement that applies to the port.
     * @param compoundNodeMode
     *            {@code true} if the node contains further nodes in the original graph. This
     *            influences the inner port label placement.
     * @param labelSpacing
     *            spacing between labels and other objects.
     */
    private void placePortLabels(final PortAdapter<?> port, final PortLabelPlacement placement,
            final boolean compoundNodeMode, final double labelSpacing) {

        if (placement.equals(PortLabelPlacement.INSIDE)) {
            placePortLabelsInside(port, compoundNodeMode, labelSpacing);
        } else if (placement.equals(PortLabelPlacement.OUTSIDE)) {
            placePortLabelsOutside(port, labelSpacing);
        }
    }

    /**
     * Places the labels of the given port on the inside of the port's node.
     *
     * @param port
     *            the port whose labels to place.
     * @param compoundNodeMode
     *            {@code true} if the node contains further nodes in the original graph. In this
     *            case, port labels are not placed next to ports, but a little down as well to avoid
     *            edge-label-crossings if the port has edges connected to the node's insides.
     * @param labelSpacing
     *            spacing between labels and other objects.
     */
    private void placePortLabelsInside(final PortAdapter<?> port, final boolean compoundNodeMode,
            final double labelSpacing) {

        ImmutableList<LabelAdapter<?>> labels = ImmutableList.copyOf(port.getLabels());
        if (labels.isEmpty()) {
            return;
        }

        // The initial y position we'll be starting from depends on the port side
        double y = 0;
        switch (port.getSide()) {
        case WEST:
        case EAST:
            // We need the first label's size here and we know that there is at least one label
            y = compoundNodeMode && port.hasCompoundConnections()
                    ? port.getSize().y
                    : (port .getSize().y - labels.get(0).getSize().y) / 2.0 - labelSpacing;
            break;
        case NORTH:
            y = port.getSize().y;
            break;
        case SOUTH:
            y = 0.0;
            break;
        }

        // In the usual case, we simply start at a given y position and place the labels downwards.
        // For southern ports, however, we actually need to start with the last label and place them
        // upwards. We thus first add all labels to a list that we may need to reverse
        if (port.getSide() == PortSide.SOUTH) {
            labels = labels.reverse();
        }

        // Place da labels!
        for (final LabelAdapter<?> label : port.getLabels()) {
            final KVector position = new KVector(port.getPosition());
            switch (port.getSide()) {
            case WEST:
                position.x = port.getSize().x + labelSpacing;
                position.y = y + labelSpacing;

                y += labelSpacing + label.getSize().y;
                break;
            case EAST:
                position.x = -label.getSize().x - labelSpacing;
                position.y = y + labelSpacing;

                y += labelSpacing + label.getSize().y;
                break;
            case NORTH:
                position.x = (port.getSize().x - label.getSize().x) / 2;
                position.y = y + labelSpacing;

                y += labelSpacing + label.getSize().y;
                break;
            case SOUTH:
                position.x = (port.getSize().x - label.getSize().x) / 2;
                position.y = y - labelSpacing - label.getSize().y;

                y -= labelSpacing + label.getSize().y;
                break;
            }
            label.setPosition(position);
        }
    }

    /**
     * Places the labels of the given port on the outside of the port's node. We suppose that the
     * first label has label side information. Those are then used for all labels. We don't support
     * having some labels above and others below incident edges.
     *
     * @param port
     *            the port whose label to place.
     * @param labelSpacing
     *            spacing between labels and other objects.
     */
    private void placePortLabelsOutside(final PortAdapter<?> port, final double labelSpacing) {
        ImmutableList<LabelAdapter<?>> labels = ImmutableList.copyOf(port.getLabels());
        if (labels.isEmpty()) {
            return;
        }

        // Retrieve the first label's side
        LabelSide labelSide = labels.get(0).getSide();
        // Default is BELOW.
        labelSide = labelSide == LabelSide.UNKNOWN ? LabelSide.BELOW : labelSide;

        // The initial y position we'll be starting from depends on port and label sides
        double y = 0;
        switch (port.getSide()) {
        case WEST:
        case EAST:
            if (labelSide == LabelSide.BELOW) {
                y = port.getSize().y;
            }
            break;

        case SOUTH:
            y = port.getSize().y;
            break;
        }

        // If labels are below incident edges, we simply start at a given y position and place the
        // labels downwards. Of they are placed above or if we have a northern port, however, we
        // actually need to start with the last label and place them upwards. We thus first add all
        // labels to a list that we may need to reverse
        if (port.getSide() == PortSide.NORTH || labelSide == LabelSide.ABOVE) {
            labels = labels.reverse();
        }

        for (final LabelAdapter<?> label : labels) {
            final KVector position = new KVector(label.getPosition());
            if (labelSide == LabelSide.ABOVE) {
                // Place label "above" edges
                switch (port.getSide()) {
                case WEST:
                    position.x = -label.getSize().x - labelSpacing;
                    position.y = y - labelSpacing - label.getSize().y;

                    y -= labelSpacing + label.getSize().y;
                    break;
                case EAST:
                    position.x = port.getSize().x + labelSpacing;
                    position.y = y - labelSpacing - label.getSize().y;

                    y -= labelSpacing + label.getSize().y;
                    break;
                case NORTH:
                    position.x = -label.getSize().x - labelSpacing;
                    position.y = y - labelSpacing - label.getSize().y;

                    y -= labelSpacing + label.getSize().y;
                    break;
                case SOUTH:
                    position.x = -label.getSize().x - labelSpacing;
                    position.y = y + labelSpacing;

                    y += labelSpacing + label.getSize().y;
                    break;
                }
            } else {
                // Place label "below" edges
                switch (port.getSide()) {
                case WEST:
                    position.x = -label.getSize().x - labelSpacing;
                    position.y = y + labelSpacing;

                    y += labelSpacing + label.getSize().y;
                    break;
                case EAST:
                    position.x = port.getSize().x + labelSpacing;
                    position.y = y + labelSpacing;

                    y += labelSpacing + label.getSize().y;
                    break;
                case NORTH:
                    position.x = port.getSize().x + labelSpacing;
                    position.y = y - labelSpacing - label.getSize().y;

                    y -= labelSpacing + label.getSize().y;
                    break;
                case SOUTH:
                    position.x = port.getSize().x + labelSpacing;
                    position.y = y + labelSpacing;

                    y += labelSpacing + label.getSize().y;
                    break;
                }
            }
            label.setPosition(position);
        }
    }

    /**
     * Calculates the port's margins such that its labels are part of them and sets them
     * accordingly.
     *
     * @param port
     *            the port whose margins to calculate.
     */
    private void calculateAndSetPortMargins(final PortAdapter<?> port) {
        // Get the port's labels, if any
        final Iterable<LabelAdapter<?>> labels = port.getLabels();
        if (labels.iterator().hasNext()) {
            final Rectangle portBox = new Rectangle(
                    0.0,
                    0.0,
                    port.getSize().x,
                    port.getSize().y);

            // Add all labels to the port's bounding box
            for (final LabelAdapter<?> label : labels) {
                final Rectangle labelBox = new Rectangle(
                        label.getPosition().x,
                        label.getPosition().y,
                        label.getSize().x,
                        label.getSize().y);

                // Calculate the union of the two bounding boxes and calculate the margins
                portBox.union(labelBox);
            }

            final Margins margin = new Margins(port.getMargin());
            margin.top = -portBox.y;
            margin.bottom = portBox.y + portBox.height - port.getSize().y;
            margin.left = -portBox.x;
            margin.right = portBox.x + portBox.width - port.getSize().x;
            port.setMargin(margin);
        }
    }

    /**
     * Calculates the width of ports on the northern and southern sides, and the height of ports on
     * the western and eastern sides of the given node. The information are stored in the class
     * fields and are used later on when calculating the minimum node size and when placing ports.
     *
     * @param data
     *            the data containing the node to calculate the port information for.
     * @param accountForLabels
     *            if {@code true}, the port labels will be taken into account when calculating the
     *            port information.
     */
    private void calculatePortInformation(final NodeData data, final boolean accountForLabels) {
        // Check if there are any ports.
        if (!data.node.getPorts().iterator().hasNext()) {
            return;
        }

        // Iterate over the ports
        for (final PortAdapter<?> port : data.node.getPorts()) {
            int side = port.getSide().ordinal();
            data.portsCount[side]++;
            switch (port.getSide()) {
            case WEST:
            case EAST:
                data.portUsedSpace[side] += port.getSize().y
                        + (accountForLabels ? port.getMargin().bottom + port.getMargin().top : 0.0);
                break;
            case NORTH:
            case SOUTH:
                data.portUsedSpace[side] += port.getSize().x
                        + (accountForLabels ? port.getMargin().left + port.getMargin().right : 0.0);
                break;
            }
        }


        // Get the port distribution from the node.
        PortAlignment portAlignment = data.node.getProperty(CoreOptions.PORT_ALIGNMENT_BASIC);
        // Use JUSTIFIED as default.
        portAlignment =
                portAlignment == PortAlignment.UNDEFINED ? PortAlignment.JUSTIFIED : portAlignment;

        // For each side get the port distribution. If it's UNDEFINED, replace it with the nodes policy.
        data.portAlignment[PortSide.NORTH.ordinal()] =
                data.node.getProperty(CoreOptions.PORT_ALIGNMENT_NORTH);
        data.portAlignment[PortSide.SOUTH.ordinal()] =
                data.node.getProperty(CoreOptions.PORT_ALIGNMENT_SOUTH);
        data.portAlignment[PortSide.WEST.ordinal()] =
                data.node.getProperty(CoreOptions.PORT_ALIGNMENT_WEST);
        data.portAlignment[PortSide.EAST.ordinal()] =
                data.node.getProperty(CoreOptions.PORT_ALIGNMENT_EAST);
        for (PortSide side : PortSide.values()) {
            data.portAlignment[side.ordinal()] =
                    data.portAlignment[side.ordinal()] == PortAlignment.UNDEFINED
                    ? portAlignment : data.portAlignment[side.ordinal()];
        }

        data.hasAdditionalPortSpace =
                data.node.getProperty(CoreOptions.SPACING_PORT_SURROUNDING) != null;

        // Calculate how many gaps we have between ports:
        // single port                                          --> 2 gaps
        // additionalPortSpace unset and alignment == JUSTIFIED --> portsCount + 1 gaps
        // otherwise                                            --> portsCount - 1 gaps

        for (PortSide side : PortSide.values()) {
            if (data.portsCount[side.ordinal()] == 1) {
                data.portGapsCount[side.ordinal()] = 2;
            } else if (!data.hasAdditionalPortSpace
                    && data.portAlignment[side.ordinal()] == PortAlignment.JUSTIFIED) {
                data.portGapsCount[side.ordinal()] = data.portsCount[side.ordinal()] + 1;
            } else {
                data.portGapsCount[side.ordinal()] = data.portsCount[side.ordinal()] - 1;
            }
        }
    }

    // /////////////////////////////////////////////////////////////////////////////
    // INSETS CALCULATION

    /**
     * Calculates the space required to accommodate all port labels and sets
     * {@link #requiredPortLabelSpace}. Also counts the number of ports on each side of the node.
     *
     * <p>
     * <i>Note:</i> We currently only support one label per port.
     * </p>
     *
     * @param data
     *            the data containing the node whose insets to calculate and to set.
     */
    private void calculateRequiredPortLabelSpace(final NodeData data) {
        // Iterate over the ports and look at their margins
        for (final PortAdapter<?> port : data.node.getPorts()) {
            switch (port.getSide()) {
            case WEST:
                data.requiredPortLabelSpace.left =
                        Math.max(data.requiredPortLabelSpace.left, port.getMargin().right);
                break;
            case EAST:
                data.requiredPortLabelSpace.right =
                        Math.max(data.requiredPortLabelSpace.right, port.getMargin().left);
                break;
            case NORTH:
                data.requiredPortLabelSpace.top =
                        Math.max(data.requiredPortLabelSpace.top, port.getMargin().bottom);
                break;
            case SOUTH:
                data.requiredPortLabelSpace.bottom =
                        Math.max(data.requiredPortLabelSpace.bottom, port.getMargin().top);
                break;
            }
        }
    }

    // /////////////////////////////////////////////////////////////////////////////
    // NODE RESIZING

    /**
     * Resizes the given node subject to the sizing constraints and options.
     *
     * @param data
     *            the data containing the node to resize.
     */
    private void resizeNode(final NodeData data) {

        final KVector nodeSize = data.node.getSize();
        final KVector originalNodeSize = new KVector(nodeSize);
        final EnumSet<SizeConstraint> sizeConstraint =
                data.node.getProperty(CoreOptions.NODE_SIZE_CONSTRAINTS);
        final EnumSet<SizeOptions> sizeOptions = data.node.getProperty(CoreOptions.NODE_SIZE_OPTIONS);
        final PortConstraints portConstraints =
                data.node.getProperty(CoreOptions.PORT_CONSTRAINTS);
        final boolean accountForLabels = sizeConstraint.contains(SizeConstraint.PORT_LABELS);

        // If the size constraint is empty, we can't do anything
        if (sizeConstraint.isEmpty()) {
            return;
        }

        // It's not empty, so we will change the node size; we start by resetting the size to zero
        nodeSize.x = 0.0;
        nodeSize.y = 0.0;

        // Find out how large the node will have to be to accommodate all ports. If port
        // constraints are set to FIXED_RATIO, we can't do anything smart, really; in this
        // case we will just assume the original node size to be the minimum for ports
        KVector minSizeForPorts = null;
        switch (portConstraints) {
        case FREE:
        case FIXED_SIDE:
        case FIXED_ORDER:
            // Calculate the space necessary to accommodate all ports
            minSizeForPorts = calculatePortSpaceRequirements(data, data.portSpacing, accountForLabels);
            break;

        case FIXED_RATIO:
            // Keep original node size
            minSizeForPorts = new KVector(originalNodeSize);
            break;

        case FIXED_POS:
            // Find the maximum position of ports
            minSizeForPorts = calculateMinNodeSizeForFixedPorts(data.node, accountForLabels);
            break;
        }

        // If the node size should take port space requirements into account, adjust it accordingly
        if (sizeConstraint.contains(SizeConstraint.PORTS)) {
            // Check if we have a minimum size required for all ports
            if (minSizeForPorts != null) {
                nodeSize.x = Math.max(nodeSize.x, minSizeForPorts.x);
                nodeSize.y = Math.max(nodeSize.y, minSizeForPorts.y);
            }

            // If we account for labels, we need to have the size account for labels placed on the
            // inside of the node (this only affects port label placement INSIDE; OUTSIDE is already
            // part of minSizeForPorts)
            if (accountForLabels) {
                nodeSize.x =
                        Math.max(nodeSize.x, data.requiredPortLabelSpace.left
                                + data.requiredPortLabelSpace.right + data.portSpacing);
                nodeSize.y =
                        Math.max(nodeSize.y, data.requiredPortLabelSpace.top
                                + data.requiredPortLabelSpace.bottom + data.portSpacing);
            }
        }

        // If the node label is to be accounted for, add its required space to the node size
        if (sizeConstraint.contains(SizeConstraint.NODE_LABELS)
                && data.node.getLabels().iterator().hasNext()) {
            enlargeNodeSizeForLabels(data, data.labelSpacing, nodeSize);
        }

        // Respect minimum size
        if (sizeConstraint.contains(SizeConstraint.MINIMUM_SIZE)) {
            KVector minSize = data.node.getProperty(CoreOptions.NODE_SIZE_MINIMUM);
            double minWidth, minHeight;
            if (minSize == null) {
                minWidth = data.node.getProperty(CoreOptions.NODE_SIZE_MIN_WIDTH).doubleValue();
                minHeight = data.node.getProperty(CoreOptions.NODE_SIZE_MIN_HEIGHT).doubleValue();
            } else {
                minWidth = minSize.x;
                minHeight = minSize.y;
            }

            // If we are to use default minima, check if the values are properly set
            if (sizeOptions.contains(SizeOptions.DEFAULT_MINIMUM_SIZE)) {
                if (minWidth <= 0) {
                    minWidth = ElkUtil.DEFAULT_MIN_WIDTH;
                }

                if (minHeight <= 0) {
                    minHeight = ElkUtil.DEFAULT_MIN_HEIGHT;
                }
            }

            // We might have to take the insets into account
            if (sizeOptions.contains(SizeOptions.MINIMUM_SIZE_ACCOUNTS_FOR_INSETS)) {
                if (minWidth > 0) {
                    nodeSize.x =
                            Math.max(nodeSize.x, minWidth + data.requiredPortLabelSpace.left
                                    + data.requiredPortLabelSpace.right);
                }

                if (minHeight > 0) {
                    nodeSize.y =
                            Math.max(nodeSize.y, minHeight + data.requiredPortLabelSpace.top
                                    + data.requiredPortLabelSpace.bottom);
                }
            } else {
                if (minWidth > 0) {
                    nodeSize.x = Math.max(nodeSize.x, minWidth);
                }

                if (minHeight > 0) {
                    nodeSize.y = Math.max(nodeSize.y, minHeight);
                }
            }
        }
        // apply the calculated node size back to the wrapped node
        data.node.setSize(nodeSize);
    }

    /**
     * Enlarges the node size to the required label space.
     * <p>
     * For outside labels, the minimal {width|height} results from the maximal sum of the 3
     * {top|bottom|left|right} label groups' {width|height}.
     * </p><p>
     * For inside labels, the minimal height results from the sum of
     * <ul>
     *     <li> top inset,</li>
     *     <li> maximum of height of 3 vertical centered label groups,</li>
     *     <li> bottom inset.</li>
     * </ul>
     * </p><p>
     * The minimal inside width results from the maximal sum of the 3 vertical {top|centered|bottom}
     * label groups' width.
     * </p>
     * @param data
     *            the data containing the node to resize.
     * @param labelSpacing
     *            the amount of space to leave between labels and other objects.
     * @param nodeSize
     *            the node's size to adjust.
     */
    private void enlargeNodeSizeForLabels(final NodeData data, final double labelSpacing,
            final KVector nodeSize) {

        double sumHeightOusideLeft   = 0; // sum of heights of the 3 outside left label groups
        double sumHeightOusideRight  = 0; // sum of heights of the 3 outside right label groups
        double sumWidthOutsideTop    = 0; // sum of widths of the 3 outside top label groups
        double sumWidthOutsideBottom = 0; // sum of widths of the 3 outside bottom label groups
        double maxHeightInsideCenter = 0; // max of heights of the 3 inside vertical center label groups
        double sumWidthInsideTop     = 0; // sum of widths of the 3 inside vertical top label groups
        double sumWidthInsideCenter  = 0; // sum of widths of the 3 inside vertical center label groups
        double sumWidthInsideBottom  = 0; // sum of widths of the 3 inside vertical bottom label groups

        for (final Entry<LabelLocation, LabelGroup> entry : data.labelGroupsBoundingBoxes.entrySet()) {
            final Rectangle boundingBox = entry.getValue();
            switch (entry.getKey()) {
            // Inside groups
            case IN_T_L:
            case IN_T_C:
            case IN_T_R:
                sumWidthInsideTop += boundingBox.width + labelSpacing;
                break;
            case IN_C_L:
            case IN_C_C:
            case IN_C_R:
                sumWidthInsideCenter += boundingBox.width + labelSpacing;
                maxHeightInsideCenter = Math.max(
                        maxHeightInsideCenter,
                        boundingBox.height + labelSpacing);
                break;
            case IN_B_L:
            case IN_B_C:
            case IN_B_R:
                sumWidthInsideBottom += boundingBox.width + labelSpacing;
                break;

            // Outside groups

            // Top 3 label groups
            case OUT_T_L:
            case OUT_T_C:
            case OUT_T_R:
                sumWidthOutsideTop += boundingBox.width + labelSpacing;
                break;
            // Bottom 3 label groups
            case OUT_B_L:
            case OUT_B_C:
            case OUT_B_R:
                sumWidthOutsideBottom += boundingBox.width + labelSpacing;
                break;
            // Left 3 label groups
            case OUT_L_T:
            case OUT_L_C:
            case OUT_L_B:
                sumHeightOusideLeft += boundingBox.height + labelSpacing;
                break;
            // Right 3 label groups
            case OUT_R_T:
            case OUT_R_C:
            case OUT_R_B:
                sumHeightOusideRight += boundingBox.height + labelSpacing;
                break;
            }
        }

        // remove additionally added label spacing
        // (possible negative values doesn't affect the outcome of the max function below)
        sumHeightOusideLeft   -= labelSpacing;
        sumHeightOusideRight  -= labelSpacing;
        sumWidthOutsideTop    -= labelSpacing;
        sumWidthOutsideBottom -= labelSpacing;
        
        //add missing label spacing and label insets (only if not zero)
        double additionalSpacing = labelSpacing 
                + data.nodeLabelInsets.left + data.nodeLabelInsets.right;
        sumWidthInsideTop    += sumWidthInsideTop    != 0 ? additionalSpacing : 0;
        sumWidthInsideCenter += sumWidthInsideCenter != 0 ? additionalSpacing : 0;
        sumWidthInsideBottom += sumWidthInsideBottom != 0 ? additionalSpacing : 0;
        // label insets are not added here because they are already part of requiredNodeLabelSpace
        double minHeightInside =
                data.requiredNodeLabelSpace.top
                + maxHeightInsideCenter
                + data.requiredNodeLabelSpace.bottom;
        minHeightInside += minHeightInside != 0 ? labelSpacing : 0;

        nodeSize.x = Math.max(nodeSize.x, sumWidthOutsideTop);
        nodeSize.x = Math.max(nodeSize.x, sumWidthInsideTop);
        nodeSize.x = Math.max(nodeSize.x, sumWidthInsideCenter);
        nodeSize.x = Math.max(nodeSize.x, sumWidthInsideBottom);
        nodeSize.x = Math.max(nodeSize.x, sumWidthOutsideBottom);
        nodeSize.y = Math.max(nodeSize.y, sumHeightOusideLeft);
        nodeSize.y = Math.max(nodeSize.y, minHeightInside);
        nodeSize.y = Math.max(nodeSize.y, sumHeightOusideRight);
    }

    /**
     * Calculate how much space the ports will require at a minimum if they can be freely
     * distributed on their respective node side. The x coordinate of the returned vector will be
     * the minimum width required by the ports on the northern and southern side. The y coordinate,
     * in turn, will be the minimum height required by the ports on the western and eastern side.
     * This may include the space required for port labels placed outside of the node. If port
     * labels are placed inside, their space requirements are not included in the result.
     *
     * @param data
     *            the data containing the node to calculate the minimum size for.
     * @param portSpacing
     *            the minimum amount of space to be left between ports if there positions are not
     *            fixed.
     * @param accountForLabels
     *            if {@code true}, the port labels will be taken into account when calculating the
     *            space requirements.
     * @return minimum size.
     */
    private KVector calculatePortSpaceRequirements(final NodeData data, final double portSpacing,
            final boolean accountForLabels) {

        // Calculate the additional port space to be left around the set of ports on each side. If
        // this is not set, we assume the spacing to be the minimum space left between ports
        double additionalWidth;
        double additionalHeight;

        if (data.hasAdditionalPortSpace) {
            final Margins additionalPortSpace =
                    data.node.getProperty(CoreOptions.SPACING_PORT_SURROUNDING);
            additionalWidth = additionalPortSpace.left + additionalPortSpace.right;
            additionalHeight = additionalPortSpace.top + additionalPortSpace.bottom;
        } else {
            additionalWidth = portSpacing * 2;
            additionalHeight = portSpacing * 2;
        }

        // Calculate the required width and height, taking the necessary spacing between (and
        // around) the ports into consideration as well
        final double requiredWidth =
                Math.max(data.portsCount[PortSide.NORTH.ordinal()] > 0
                        ? additionalWidth
                                + data.portGapsCount[PortSide.NORTH.ordinal()] * portSpacing
                                + data.portUsedSpace[PortSide.NORTH.ordinal()]
                        : 0.0,
                        data.portsCount[PortSide.SOUTH.ordinal()] > 0
                        ? additionalWidth
                                + data.portGapsCount[PortSide.SOUTH.ordinal()] * portSpacing
                                + data.portUsedSpace[PortSide.SOUTH.ordinal()]
                        : 0.0);
        final double requiredHeight =
                Math.max(data.portsCount[PortSide.WEST.ordinal()] > 0
                        ? additionalHeight
                                + data.portGapsCount[PortSide.WEST.ordinal()] * portSpacing
                                + data.portUsedSpace[PortSide.WEST.ordinal()]
                        : 0.0,
                        data.portsCount[PortSide.EAST.ordinal()] > 0
                        ? additionalHeight
                                + data.portGapsCount[PortSide.EAST.ordinal()] * portSpacing
                                + data.portUsedSpace[PortSide.EAST.ordinal()]
                        : 0.0);

        return new KVector(requiredWidth, requiredHeight);
    }

    /**
     * For fixed port positions, returns the minimum size of the node to contain all ports.
     * The minimum size equals the position plus the size of the most bottom-right port
     * (biggest x- and y-coordinate)
     *
     * @param node
     *            the node to calculate the minimum size for.
     * @param accountForLabels
     *            whether to regard port labels.
     * @return the minimum size to contain all port positions.
     */
    private KVector calculateMinNodeSizeForFixedPorts(final NodeAdapter<?> node,
            final boolean accountForLabels) {

        // Port positions must be fixed for this method to be called
        assert node.getProperty(CoreOptions.PORT_CONSTRAINTS) == PortConstraints.FIXED_POS;

        final KVector result = new KVector();

        // Iterate over the ports
        for (final PortAdapter<?> port : node.getPorts()) {
            switch (port.getSide()) {
            case WEST:
            case EAST:
                result.y =
                        Math.max(result.y, port.getPosition().y + port.getSize().y
                                + (accountForLabels ? port.getMargin().bottom : 0.0));
                break;

            case NORTH:
            case SOUTH:
                result.x =
                        Math.max(result.x, port.getPosition().x + port.getSize().x
                                + (accountForLabels ? port.getMargin().right : 0.0));
                break;
            }
        }

        return result;
    }

    // /////////////////////////////////////////////////////////////////////////////
    // PORT PLACEMENT

    /**
     * Places the given node's ports. If the node wasn't resized at all and port constraints are set
     * to either {@link PortConstraints#FIXED_RATIO} or {@link PortConstraints#FIXED_POS}, the port
     * positions are not touched.
     *
     * @param data
     *            the data containing the node whose ports to place.
     */
    private void placePorts(final NodeData data) {
        // Check if there are any ports.
        if (!data.node.getPorts().iterator().hasNext()) {
            return;
        }
        final PortConstraints portConstraints =
                data.node.getProperty(CoreOptions.PORT_CONSTRAINTS);

        if (portConstraints == PortConstraints.FIXED_POS) {
            // Fixed Position
            placeFixedPosNodePorts(data.node);
        } else if (portConstraints == PortConstraints.FIXED_RATIO) {
            // Fixed Ratio
            placeFixedRatioNodePorts(data.node);
        } else {
            // Free, Fixed Side, Fixed Order
            if (data.node.getProperty(CoreOptions.HYPERNODE)
                    || (data.node.getSize().x == 0 && data.node.getSize().y == 0)) {

                placeHypernodePorts(data.node);
            } else {
                placeNodePorts(data);
            }
        }
    }

    /**
     * Places the ports of a node assuming that the port constraints are set to fixed port
     * positions. Ports still need to be placed, though, because the node may have been resized.
     *
     * @param node
     *            the node whose ports to place.
     */
    private void placeFixedPosNodePorts(final NodeAdapter<?> node) {
        final KVector nodeSize = node.getSize();

        for (final PortAdapter<?> port : node.getPorts()) {
            Float portOffset = port.getProperty(CoreOptions.PORT_BORDER_OFFSET);
            if (portOffset == null) {
                portOffset = 0f;
            }

            final KVector position = new KVector(port.getPosition());
            switch (port.getSide()) {
            case WEST:
                position.x = -port.getSize().x - portOffset;
                break;
            case EAST:
                position.x = nodeSize.x + portOffset;
                break;
            case NORTH:
                position.y = -port.getSize().y - portOffset;
                break;
            case SOUTH:
                position.y = nodeSize.y + portOffset;
                break;
            }
            port.setPosition(position);
        }
    }

    /**
     * Places the ports of a node keeping the ratio between their position and the length of their
     * respective side intact.
     *
     * @param node
     *            the node whose ports to place.
     */
    private void placeFixedRatioNodePorts(final NodeAdapter<?> node) {
        final KVector nodeSize = node.getSize();

        // Adjust port positions depending on port side. Eastern ports have to have their x
        // coordinate set to the node's current width; the same goes for the y coordinate of
        // southern ports
        for (final PortAdapter<?> port : node.getPorts()) {
            Float portOffset = port.getProperty(CoreOptions.PORT_BORDER_OFFSET);
            if (portOffset == null) {
                portOffset = 0f;
            }

            switch (port.getSide()) {
            case WEST:
                port.getPosition().y = nodeSize.y * port.getProperty(PORT_RATIO_OR_POSITION);
                port.getPosition().x = -port.getSize().x - portOffset;
                break;
            case EAST:
                port.getPosition().y = nodeSize.y * port.getProperty(PORT_RATIO_OR_POSITION);
                port.getPosition().x = nodeSize.x + portOffset;
                break;
            case NORTH:
                port.getPosition().x = nodeSize.x * port.getProperty(PORT_RATIO_OR_POSITION);
                port.getPosition().y = -port.getSize().y - portOffset;
                break;
            case SOUTH:
                port.getPosition().x = nodeSize.x * port.getProperty(PORT_RATIO_OR_POSITION);
                port.getPosition().y = nodeSize.y + portOffset;
                break;
            }
            switch (port.getSide()) {
            case WEST:
                port.getPosition().y = nodeSize.y * port.getProperty(PORT_RATIO_OR_POSITION);
                port.getPosition().x = -port.getSize().x - portOffset;
                break;
            case EAST:
                port.getPosition().y = nodeSize.y * port.getProperty(PORT_RATIO_OR_POSITION);
                port.getPosition().x = nodeSize.x + portOffset;
                break;
            case NORTH:
                port.getPosition().x = nodeSize.x * port.getProperty(PORT_RATIO_OR_POSITION);
                port.getPosition().y = -port.getSize().y - portOffset;
                break;
            case SOUTH:
                port.getPosition().x = nodeSize.x * port.getProperty(PORT_RATIO_OR_POSITION);
                port.getPosition().y = nodeSize.y + portOffset;
                break;
            }
        }
    }

    /**
     * Places the ports of a node, assuming that the ports are not fixed in their position or ratio.
     *
     * @param data
     *            the data containing the node whose ports to place.
     */
    private void placeNodePorts(final NodeData data) {
        final KVector nodeSize = data.node.getSize();
        final boolean accountForLabels =
                data.node.getProperty(CoreOptions.NODE_SIZE_CONSTRAINTS).contains(
                        SizeConstraint.PORT_LABELS);

        // Let someone compute the port placement data we'll need
        computePortPlacementData(data);

        // Arrange the ports
        for (final PortAdapter<?> port : data.node.getPorts()) {
            Float portOffset = port.getProperty(CoreOptions.PORT_BORDER_OFFSET);
            if (portOffset == null) {
                portOffset = 0f;
            }
            final KVector portSize = port.getSize();
            final Margins portMargins = port.getMargin();

            final KVector position = new KVector(port.getPosition());
            switch (port.getSide()) {
            case WEST:
                position.x = -portSize.x - portOffset;
                position.y =
                        data.westY - portSize.y
                                - (accountForLabels ? portMargins.bottom : 0.0);
                data.westY -=
                        data.getPortGapsSize(PortSide.WEST) + portSize.y
                                + (accountForLabels ? portMargins.top + portMargins.bottom : 0.0);
                break;
            case EAST:
                position.x = nodeSize.x + portOffset;
                position.y = data.eastY + (accountForLabels ? portMargins.top : 0.0);
                data.eastY +=
                        data.getPortGapsSize(PortSide.EAST) + portSize.y
                                + (accountForLabels ? portMargins.top + portMargins.bottom : 0.0);
                break;
            case NORTH:
                position.x = data.northX + (accountForLabels ? portMargins.left : 0.0);
                position.y = -port.getSize().y - portOffset;
                data.northX +=
                        data.getPortGapsSize(PortSide.NORTH) + portSize.x
                                + (accountForLabels ? portMargins.left + portMargins.right : 0.0);
                break;
            case SOUTH:
                position.x =
                        data.southX - portSize.x
                                - (accountForLabels ? portMargins.right : 0.0);
                position.y = nodeSize.y + portOffset;
                data.southX -=
                        data.getPortGapsSize(PortSide.SOUTH) + portSize.x
                                + (accountForLabels ? portMargins.left + portMargins.right : 0.0);
                break;
            }
            port.setPosition(position);
        }
    }

    /**
     * Computes the port placement data for the given node.
     *
     * @param data
     *            the data containing the node to compute the placement data for.
     */
    // CHECKSTYLEOFF MethodLength
    // There's no sensible point to separate, too much parameters would have to be introduced.
    private void computePortPlacementData(final NodeData data) {
        final KVector nodeSize = data.node.getSize();

        // The way we calculate everything depends on whether any additional port space is specified
        Margins additionalPortSpace;
        if (data.hasAdditionalPortSpace) {
            additionalPortSpace = data.node.getProperty(CoreOptions.SPACING_PORT_SURROUNDING);
        } else {
            // No additional port space set, so we set it to port spacing.
            additionalPortSpace = new Margins(
                    data.portSpacing,
                    data.portSpacing,
                    data.portSpacing,
                    data.portSpacing);
        }

        // Calculate how much space on each side may actually be used by ports
        double usableSpaceNorth = nodeSize.x;
        if (data.hasAdditionalPortSpace
                || data.portAlignment[PortSide.NORTH.ordinal()] != PortAlignment.JUSTIFIED) {
            
            usableSpaceNorth -= additionalPortSpace.left + additionalPortSpace.right;
        }
        
        double usableSpaceSouth = nodeSize.x;
        if (data.hasAdditionalPortSpace
                || data.portAlignment[PortSide.SOUTH.ordinal()] != PortAlignment.JUSTIFIED) {
            
            usableSpaceSouth -= additionalPortSpace.left + additionalPortSpace.right;
        }
        
        double usableSpaceWest = nodeSize.y;
        if (data.hasAdditionalPortSpace
                || data.portAlignment[PortSide.WEST.ordinal()] != PortAlignment.JUSTIFIED) {
            
            usableSpaceWest -= additionalPortSpace.top + additionalPortSpace.bottom;
        }
        
        double usableSpaceEast = nodeSize.y;
        if (data.hasAdditionalPortSpace
                || data.portAlignment[PortSide.EAST.ordinal()] != PortAlignment.JUSTIFIED) {
            
            usableSpaceEast -= additionalPortSpace.top + additionalPortSpace.bottom;
        }

        // Compute the space to be left between the ports and the coordinate of the first port on
        // each side.
        // Note:
        // If the size constraints of this node are empty, the height and width of the ports on each
        // side are zero. That is intentional: if this wasn't the case, bad things would happen if
        // the ports would actually need more size than the node at its current (unchanged) size
        // would be able to provide.
        
        // NORTH
        if (data.getPortAlignment(PortSide.NORTH) == PortAlignment.JUSTIFIED) {
            data.portGapsSize[PortSide.NORTH.ordinal()] =
                    (usableSpaceNorth - data.getPortUsedSpace(PortSide.NORTH))
                            / data.getPortGapsCount(PortSide.NORTH);
            data.northX = data.hasAdditionalPortSpace
                    ? (additionalPortSpace.left
                            + (data.getPortsCount(PortSide.NORTH) == 1
                            ? data.portGapsSize[PortSide.NORTH.ordinal()] : 0))
                    : data.portGapsSize[PortSide.NORTH.ordinal()];
        } else {
            data.portGapsSize[PortSide.NORTH.ordinal()] = data.portSpacing;
            // Space occupied by all ports (including the in between gaps).
            double usedPortSpace = data.getPortUsedSpace(PortSide.NORTH)
                    + data.portGapsSize[PortSide.NORTH.ordinal()]
                            * (data.getPortsCount(PortSide.NORTH) - 1);
            switch (data.getPortAlignment(PortSide.NORTH)) {
            case BEGIN:
                // Start at leftmost position, additionalSpace from the edge.
                data.northX = additionalPortSpace.left;
                break;
            case CENTER:
                // centered inside the usableWith
                data.northX = additionalPortSpace.left + (usableSpaceNorth - usedPortSpace) / 2.0;
                break;
            case END:
                // Startposition is as far from the right edge as the ports' used space plus
                // additionalSpace.
                data.northX = nodeSize.x - usedPortSpace - additionalPortSpace.right;
                break;
            }
        }

        // SOUTH
        if (data.getPortAlignment(PortSide.SOUTH) == PortAlignment.JUSTIFIED) {
            data.portGapsSize[PortSide.SOUTH.ordinal()] =
                    (usableSpaceSouth - data.getPortUsedSpace(PortSide.SOUTH))
                            / data.getPortGapsCount(PortSide.SOUTH);
            data.southX = nodeSize.x - (data.hasAdditionalPortSpace
                    ? (additionalPortSpace.right
                            + (data.getPortsCount(PortSide.SOUTH) == 1
                            ? data.portGapsSize[PortSide.SOUTH.ordinal()] : 0))
                    : data.portGapsSize[PortSide.SOUTH.ordinal()]);
        } else {
            data.portGapsSize[PortSide.SOUTH.ordinal()] = data.portSpacing;
            // Space occupied by all ports (including the in between gaps).
            double usedPortSpace = data.getPortUsedSpace(PortSide.SOUTH)
                    + data.portGapsSize[PortSide.SOUTH.ordinal()]
                            * (data.getPortsCount(PortSide.SOUTH) - 1);
            switch (data.getPortAlignment(PortSide.SOUTH)) {
            case BEGIN:
                // Startposition is as far from the right edge as the ports' used space plus
                // additionalSpace.
                data.southX = usedPortSpace + additionalPortSpace.left;
                break;
            case CENTER:
                // centered inside the usableWith (starting at the right)
                data.southX = nodeSize.x
                        - (usableSpaceSouth - usedPortSpace) / 2.0
                        - additionalPortSpace.right;
                break;
            case END:
                // Start at rightmost position, additionalSpace from the edge.
                data.southX = nodeSize.x - additionalPortSpace.right;
                break;
            }
        }

        // WEST
        if (data.getPortAlignment(PortSide.WEST) == PortAlignment.JUSTIFIED) {
            data.portGapsSize[PortSide.WEST.ordinal()] =
                    (usableSpaceWest - data.getPortUsedSpace(PortSide.WEST))
                            / data.getPortGapsCount(PortSide.WEST);
            data.westY =  nodeSize.y - (data.hasAdditionalPortSpace
                    ? (additionalPortSpace.bottom
                            + (data.getPortsCount(PortSide.WEST) == 1
                            ? data.portGapsSize[PortSide.WEST.ordinal()] : 0))
                    : data.portGapsSize[PortSide.WEST.ordinal()]);
        } else {
            data.portGapsSize[PortSide.WEST.ordinal()] = data.portSpacing;
            // Space occupied by all ports (including the in between gaps).
            double usedPortSpace = data.getPortUsedSpace(PortSide.WEST)
                    + data.portGapsSize[PortSide.WEST.ordinal()]
                            * (data.getPortsCount(PortSide.WEST) - 1);
            switch (data.getPortAlignment(PortSide.WEST)) {
            case BEGIN:
                // Startposition is as far from the top edge as the ports' used space plus
                // additionalSpace.
                data.westY = usedPortSpace + additionalPortSpace.top;
                break;
            case CENTER:
                // centered inside the usableWith (starting at the bottom)
                data.westY = nodeSize.y
                        - (usableSpaceWest - usedPortSpace) / 2.0
                        - additionalPortSpace.bottom;
                break;
            case END:
                // Start at bottommost position, additionalSpace from the edge.
                data.westY = nodeSize.y - additionalPortSpace.bottom;
                break;
            }
        }

        // EAST
        if (data.getPortAlignment(PortSide.EAST) == PortAlignment.JUSTIFIED) {
            data.portGapsSize[PortSide.EAST.ordinal()] =
                    (usableSpaceEast - data.getPortUsedSpace(PortSide.EAST))
                            / data.getPortGapsCount(PortSide.EAST);
            data.eastY = data.hasAdditionalPortSpace
                    ? (additionalPortSpace.top
                            + (data.getPortsCount(PortSide.EAST) == 1
                            ? data.portGapsSize[PortSide.EAST.ordinal()] : 0))
                    : data.portGapsSize[PortSide.EAST.ordinal()];
        } else {
            data.portGapsSize[PortSide.EAST.ordinal()] = data.portSpacing;
            // Space occupied by all ports (including the in between gaps).
            double usedPortSpace = data.getPortUsedSpace(PortSide.EAST)
                    + data.portGapsSize[PortSide.EAST.ordinal()]
                            * (data.getPortsCount(PortSide.EAST) - 1);
            switch (data.getPortAlignment(PortSide.EAST)) {
            case BEGIN:
                // Start at topmost position, additionalSpace from the edge.
                data.eastY = additionalPortSpace.top;
                break;
            case CENTER:
                // centered inside the usableWith
                data.eastY = additionalPortSpace.top + (usableSpaceEast - usedPortSpace) / 2.0;
                break;
            case END:
                // Start position is as far from the bottom edge as the ports' used space plus
                // additionalSpace.
                data.eastY = nodeSize.y - usedPortSpace - additionalPortSpace.bottom;
                break;
            }
        }
    }
    // CHECKSTYLEON MethodLength

    /**
     * Places the ports of a hypernode.
     *
     * @param node
     *            the hypernode whose ports to place.
     */
    private void placeHypernodePorts(final NodeAdapter<?> node) {
        for (final PortAdapter<?> port : node.getPorts()) {
            final KVector position = new KVector(port.getPosition());
            switch (port.getSide()) {
            case WEST:
                position.x = 0;
                position.y = node.getSize().y / 2;
                break;
            case EAST:
                position.x = node.getSize().x;
                position.y = node.getSize().y / 2;
                break;
            case NORTH:
                position.x = node.getSize().x / 2;
                position.y = 0;
                break;
            case SOUTH:
                position.x = node.getSize().x / 2;
                position.y = node.getSize().y;
                break;
            }
            port.setPosition(position);
        }
    }

    // /////////////////////////////////////////////////////////////////////////////
    // PLACING NODE LABELS

    /**
     * Calculates the position of the node's label groups and places the labels.
     *
     * @param data
     *            the data containing the node whose labels to place.
     */
    private void placeNodeLabels(final NodeData data) {
        // Check if there are any node labels
        if (!data.node.getLabels().iterator().hasNext()) {
            return;
        }

        computeLabelGroupPositions(data);

        doPlaceNodeLabels(data);
    }

    /**
     * Computes the top left position of each label group.
     * 
     * @param data
     *            the data containing the node whose labels to place.
     */
    private void computeLabelGroupPositions(final NodeData data) {
        // TODO Outside label placement doesn't take ports into account yet.
        
        // For each present location, calculate the position of the top left corner of the label group
        for (final Entry<LabelLocation, LabelGroup> entry : data.labelGroupsBoundingBoxes.entrySet()) {
            final Rectangle boundingBox = entry.getValue();
            // Prepare available horizontal and vertical space for centering objects
            double horizontalSpace =
                    data.node.getSize().x - data.nodeLabelInsets.left - data.nodeLabelInsets.right;
            double verticalSpace =
                    data.node.getSize().y - data.nodeLabelInsets.top - data.nodeLabelInsets.bottom;
            switch (entry.getKey()) {
            case OUT_T_L:
                boundingBox.x = 0;
                boundingBox.y = -(boundingBox.height + data.labelSpacing);
                break;
            case OUT_T_C:
                boundingBox.x = (data.node.getSize().x - boundingBox.width) / 2.0;
                boundingBox.y = -(boundingBox.height + data.labelSpacing);
                break;
            case OUT_T_R:
                boundingBox.x = data.node.getSize().x - boundingBox.width;
                boundingBox.y = -(boundingBox.height + data.labelSpacing);
                break;
            case OUT_B_L:
                boundingBox.x = 0;
                boundingBox.y = data.node.getSize().y + data.labelSpacing;
                break;
            case OUT_B_C:
                boundingBox.x = (data.node.getSize().x - boundingBox.width) / 2.0;
                boundingBox.y = data.node.getSize().y + data.labelSpacing;
                break;
            case OUT_B_R:
                boundingBox.x = data.node.getSize().x - boundingBox.width;
                boundingBox.y = data.node.getSize().y + data.labelSpacing;
                break;
            case OUT_L_T:
                boundingBox.x = -(boundingBox.width + data.labelSpacing);
                boundingBox.y = 0;
                break;
            case OUT_L_C:
                boundingBox.x = -(boundingBox.width + data.labelSpacing);
                boundingBox.y = (data.node.getSize().y - boundingBox.height) / 2.0;
                break;
            case OUT_L_B:
                boundingBox.x = -(boundingBox.width + data.labelSpacing);
                boundingBox.y = data.node.getSize().y - boundingBox.height;
                break;
            case OUT_R_T:
                boundingBox.x = data.node.getSize().x + data.labelSpacing;
                boundingBox.y = 0;
                break;
            case OUT_R_C:
                boundingBox.x = data.node.getSize().x + data.labelSpacing;
                boundingBox.y = (data.node.getSize().y - boundingBox.height) / 2.0;
                break;
            case OUT_R_B:
                boundingBox.x = data.node.getSize().x + data.labelSpacing;
                boundingBox.y = data.node.getSize().y - boundingBox.height;
                break;
            case IN_T_L:
                boundingBox.x =
                        data.requiredPortLabelSpace.left + data.labelSpacing
                                + data.nodeLabelInsets.left;
                boundingBox.y =
                        data.requiredPortLabelSpace.top + data.labelSpacing
                                + data.nodeLabelInsets.top;
                break;
            case IN_T_C:
                boundingBox.x =
                        ((horizontalSpace - boundingBox.width) / 2.0) + data.nodeLabelInsets.left;
                boundingBox.y =
                        data.requiredPortLabelSpace.top + data.labelSpacing
                                + data.nodeLabelInsets.top;
                break;
            case IN_T_R:
                boundingBox.x =
                        data.node.getSize().x - data.requiredPortLabelSpace.right
                                - boundingBox.width - data.labelSpacing
                                - data.nodeLabelInsets.right;
                boundingBox.y =
                        data.requiredPortLabelSpace.top + data.labelSpacing
                                + data.nodeLabelInsets.top;
                break;
            case IN_C_L:
                boundingBox.x =
                        data.requiredPortLabelSpace.left + data.labelSpacing
                                + data.nodeLabelInsets.left;
                boundingBox.y =
                        ((verticalSpace - boundingBox.height) / 2.0) + data.nodeLabelInsets.top;
                break;
            case IN_C_C:
                boundingBox.x =
                        ((horizontalSpace - boundingBox.width) / 2.0) + data.nodeLabelInsets.left;
                boundingBox.y =
                        ((verticalSpace - boundingBox.height) / 2.0) + data.nodeLabelInsets.top;
                break;
            case IN_C_R:
                boundingBox.x =
                        data.node.getSize().x - data.requiredPortLabelSpace.right
                                - boundingBox.width - data.labelSpacing - data.nodeLabelInsets.right;
                boundingBox.y =
                        ((verticalSpace - boundingBox.height) / 2.0) + data.nodeLabelInsets.top;
                break;
            case IN_B_L:
                boundingBox.x =
                        data.requiredPortLabelSpace.left + data.labelSpacing
                                + data.nodeLabelInsets.left;
                boundingBox.y =
                        data.node.getSize().y - data.requiredPortLabelSpace.bottom
                                - boundingBox.height - data.labelSpacing
                                - data.nodeLabelInsets.bottom;
                break;
            case IN_B_C:
                boundingBox.x =
                        ((horizontalSpace - boundingBox.width) / 2.0) + data.nodeLabelInsets.left;
                boundingBox.y =
                        data.node.getSize().y - data.requiredPortLabelSpace.bottom
                                - boundingBox.height - data.labelSpacing
                                - data.nodeLabelInsets.bottom;
                break;
            case IN_B_R:
                boundingBox.x =
                        data.node.getSize().x - data.requiredPortLabelSpace.right
                                - boundingBox.width - data.labelSpacing
                                - data.nodeLabelInsets.right;
                boundingBox.y =
                        data.node.getSize().y - data.requiredPortLabelSpace.bottom
                                - boundingBox.height - data.labelSpacing
                                - data.nodeLabelInsets.bottom;
                break;
            }
        }
    }

    /**
     * Places the given node's labels in a vertical stack, starting at the given position.
     *
     * @param data
     *            the data containing the node whose labels are to be placed.
     */
    private void doPlaceNodeLabels(final NodeData data) {

        // Place all labels
        for (final LabelAdapter<?> label : data.node.getLabels()) {
            final KVector position = new KVector(label.getPosition());
            final LabelLocation location = LabelLocation.values()[label.getVolatileId()];
            final LabelGroup boundingBox = data.labelGroupsBoundingBoxes.get(location);

            // Set y coordinate
            position.y = boundingBox.y + boundingBox.nextLabelYPos;

            // The x coordinate depends on the text alignment
            if (location.getHorizontalAlignment() == TextAlignment.LEFT) {
                position.x = boundingBox.x;
            } else if (location.getHorizontalAlignment() == TextAlignment.CENTER) {
                position.x =
                        boundingBox.x + (boundingBox.width - label.getSize().x) / 2.0;
            } else if (location.getHorizontalAlignment() == TextAlignment.RIGHT) {
                position.x = boundingBox.x + boundingBox.width - label.getSize().x;
            }

            // Apply new position
            label.setPosition(position);

            // Update next y coordinate
            boundingBox.nextLabelYPos += label.getSize().y + data.labelSpacing;
        }
    }

    // /////////////////////////////////////////////////////////////////////////////
    // CONTEXT UTILITIES

    /**
     * Information holder to provide context information for the different phases of the algorithm.
     * The information are usually computed by some phase to be made available to later phases.
     *
     * @author csp
     */
    private static final class NodeData {

        /**
         * The currently processed node.
         */
        private final NodeAdapter<?> node;

        /*
         * Spacing around labels.
         */
        private double labelSpacing;

        /**
         * Insets for node labels placed inside the node.
         */
        private Insets nodeLabelInsets;

        /*
         * Spacing around ports.
         */
        private double portSpacing;

        /**
         * Node insets required by port labels inside the node. This is always set, but not always
         * taken into account to calculate the node size.
         */
        private final Insets requiredPortLabelSpace = new Insets();

        /**
         * Node insets required by node labels placed inside the node. This is always set, but not
         * always taken into account to calculate the node size.
         */
        private final Insets requiredNodeLabelSpace = new Insets();
        
        /**
         * Whether the node has additional port space set or not.
         */
        private boolean hasAdditionalPortSpace;

        /**
         * Number of ports on the respective side. Only used if port constraints are not
         * {@link PortConstraints#FIXED_RATIO} or {@link PortConstraints#FIXED_POS}.
         */
        private final int[] portsCount = new int[PortSide.values().length];

        /**
         * Number of gaps between ports on the respective side. Only used if port constraints are not
         * {@link PortConstraints#FIXED_RATIO} or {@link PortConstraints#FIXED_POS}.
         */
        private final int[] portGapsCount = new int[PortSide.values().length];

        /**
         * Size of gops between ports on the respective side. Only used if port constraints are not
         * {@link PortConstraints#FIXED_RATIO} or {@link PortConstraints#FIXED_POS}.
         */
        private final double[] portGapsSize = new double[PortSide.values().length];
        
        // The position of the next port on each side
        private double westY;
        private double eastY;
        private double northX;
        private double southX;

        /**
         * Height of the ports on the respective side. If port labels are accounted for, the space
         * includes the relevant port margins too. Only used if port constraints are not
         * {@link PortConstraints#FIXED_RATIO} or {@link PortConstraints#FIXED_POS}.
         */
        private final double[] portUsedSpace = new double[PortSide.values().length];

        /**
         * Alignment of ports on the respective side. Only used if port constraints are not
         * {@link PortConstraints#FIXED_RATIO} or {@link PortConstraints#FIXED_POS}.
         */
        private final PortAlignment[] portAlignment = new PortAlignment[PortSide.values().length];

        /**
         * Contains the size and position of the corresponding label group for each element of
         * {@link LabelLocation}.
         */
        private final Map<LabelLocation, LabelGroup> labelGroupsBoundingBoxes =
                new EnumMap<LabelLocation, LabelGroup>(LabelLocation.class);

        /**
         * Create a new information holder with default values and the given, currently processed node.
         *
         * @param node
         *            the node currently processed.
         */
        private NodeData(final NodeAdapter<?> node) {
            this.node = node;
            Arrays.fill(portsCount, 0);
            Arrays.fill(portGapsCount, 0);
            Arrays.fill(portUsedSpace, 0.0);
        }

        /**
         * Returns the number of ports on the given port side.
         * 
         * @param side
         *            the port side in question.
         * @return the number of ports on the given side.
         */
        private int getPortsCount(final PortSide side) {
            return portsCount[side.ordinal()];
        }

        /**
         * Returns the number of gaps between ports on the given port side.
         * 
         * @param side
         *            the port side in question.
         * @return the number of gaps on the given side.
         */
        private int getPortGapsCount(final PortSide side) {
            return portGapsCount[side.ordinal()];
        }

        /**
         * Returns the number of gaps between ports on the given port side.
         * 
         * @param side
         *            the port side in question.
         * @return the number of gaps on the given side.
         */
        private double getPortGapsSize(final PortSide side) {
            return portGapsSize[side.ordinal()];
        }

        /**
         * Returns the amount of used space by ports on the given port side.
         * 
         * @param side
         *            the port side in question.
         * @return the amount of used space on the given side.
         */
        private double getPortUsedSpace(final PortSide side) {
            return portUsedSpace[side.ordinal()];
        }

        /**
         * Returns the alignment of ports on the given port side.
         * 
         * @param side
         *            the port side in question.
         * @return the alignment of ports on the given side.
         */
        private PortAlignment getPortAlignment(final PortSide side) {
            return portAlignment[side.ordinal()];
        }
    }

    // /////////////////////////////////////////////////////////////////////////////
    // LABEL PLACEMENT UTILITIES

    /**
     * @param data
     *            the data containing the node in question.
     */
    private void calculateRequiredNodeLabelSpace(final NodeData data) {
        LabelSpaceCalculation.calculateRequiredNodeLabelSpace(data.node, data.labelSpacing,
                data.nodeLabelInsets, data.labelGroupsBoundingBoxes, data.requiredNodeLabelSpace);
    }
    
}
