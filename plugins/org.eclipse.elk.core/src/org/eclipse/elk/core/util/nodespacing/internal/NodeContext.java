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
package org.eclipse.elk.core.util.nodespacing.internal;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.eclipse.elk.core.math.ElkMargin;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.math.ElkRectangle;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.NodeLabelPlacement;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortLabelPlacement;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.core.util.IndividualSpacings;
import org.eclipse.elk.core.util.adapters.GraphAdapters.GraphAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.NodeAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.PortAdapter;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

/**
 * Data holder class to be passed around to avoid having too much state in the size calculation classes. Some of the
 * most relevant settings are copied into variables for convenience.
 */
public final class NodeContext {
   
    // CHECKSTYLEOFF Visibility
    // This is a purely internal data holder class, so we ditch setters for public fields.
    
    /////////////////////////////////////////////////////////////////////////////////
    // Convenience Access to Things
    
    /** The node we calculate stuff for. */
    public final NodeAdapter<?> node;
    /** The node's size constraints. */
    public final Set<SizeConstraint> sizeConstraints;
    /** The node's size options. */
    public final Set<SizeOptions> sizeOptions;
    /** Port constraints set on the node. */
    public final PortConstraints portConstraints;
    /** Whether port labels are placed inside or outside. */
    public final PortLabelPlacement portLabelsPlacement;
    /** Where node labels are placed by default. */
    public final Set<NodeLabelPlacement> nodeLabelPlacement;
    /** Space to leave around the node label area. */
    public final ElkPadding nodeLabelsPadding;
    /** Space between a node and its outside labels. */
    public final double nodeLabelSpacing;
    /** Space between two labels. */
    public final double labelLabelSpacing;
    /** Space between a port and another port. */
    public final double portPortSpacing;
    /** Space between a port and its labels. */
    public final double portLabelSpacing;
    /** Margin to leave around the set of ports on each side. */
    public final ElkMargin surroundingPortMargins;


    /////////////////////////////////////////////////////////////////////////////////
    // More Contexts
    
    /** Context objects that hold more information about each port. Sorted left-to-right / top-to-bottom. */
    public final Multimap<PortSide, PortContext> portContexts = TreeMultimap.create(
            NodeContext::comparePortSides, NodeContext::comparePortContexts);
    /** Map of all label locations to their location contexts. */
    public final Map<LabelLocation, LabelLocationContext> labelLocationContexts = Maps.newEnumMap(LabelLocation.class);
    
    
    /////////////////////////////////////////////////////////////////////////////////
    // Different Areas of the Node
    
    /** The areas where inside port labels are going to be placed. */
    public final Map<PortSide, ElkRectangle> insidePortLabelAreas = Maps.newEnumMap(PortSide.class);
    /** The areas where inside port labels are going to be placed. */
    public final Map<LabelLocation, ElkRectangle> insideNodeLabelAreas = Maps.newEnumMap(LabelLocation.class);
    
    
    /////////////////////////////////////////////////////////////////////////////////
    // Calculated Things
    
    /** The minimum node size required for ports along the horizontal and vertical borders. */
    public final KVector nodeSizeRequiredByPortPlacement = new KVector();
    /** If ports extend into the node's insides, this is by how much. */
    public final ElkPadding insidePortSpace = new ElkPadding();
    /** The rows of inside node labels and the client area. */
    public ThreeRowsOrColumns insideNodeLabelRows = null;
    /** The columns of inside ndoe labels and the client area. */
    public ThreeRowsOrColumns insideNodeLabelColumns = null;
    /** The columns of outside top node labels. */
    public ThreeRowsOrColumns outsideTopNodeLabelColumns = null;
    /** The columns of outside bottom node labels. */
    public ThreeRowsOrColumns outsideBottomNodeLabelColumns = null;
    /** The rows of outside left node labels. */
    public ThreeRowsOrColumns outsideLeftNodeLabelRows = null;
    /** The rows of outside right node labels. */
    public ThreeRowsOrColumns outsideRightNodeLabelRows = null;
    
    
    /////////////////////////////////////////////////////////////////////////////////
    // Creation
    
    /**
     * Creates a new context object for the given node, fully initialized with the node's settings.
     * 
     * @param parentGraph the node's parent graph.
     * @param node the node to create the context for.
     */
    public NodeContext(final GraphAdapter<?> parentGraph, final NodeAdapter<?> node) {
        this.node = node;
        
        // Core size settings
        sizeConstraints = node.getProperty(CoreOptions.NODE_SIZE_CONSTRAINTS);
        sizeOptions = node.getProperty(CoreOptions.NODE_SIZE_OPTIONS);
        portConstraints = node.getProperty(CoreOptions.PORT_CONSTRAINTS);
        portLabelsPlacement = node.getProperty(CoreOptions.PORT_LABELS_PLACEMENT);
        nodeLabelPlacement = node.getProperty(CoreOptions.NODE_LABELS_PLACEMENT);
        
        // Copy spacings for convenience
        nodeLabelsPadding = IndividualSpacings.getIndividualOrInherited(
                parentGraph, node, CoreOptions.NODE_LABELS_PADDING);
        nodeLabelSpacing = IndividualSpacings.getIndividualOrInherited(
                parentGraph, node, CoreOptions.SPACING_LABEL_NODE);
        labelLabelSpacing = IndividualSpacings.getIndividualOrInherited(
                parentGraph, node, CoreOptions.SPACING_LABEL_LABEL);
        portPortSpacing = IndividualSpacings.getIndividualOrInherited(
                parentGraph, node, CoreOptions.SPACING_PORT_PORT);
        portLabelSpacing = IndividualSpacings.getIndividualOrInherited(
                parentGraph, node, CoreOptions.SPACING_LABEL_PORT);
        surroundingPortMargins = IndividualSpacings.getIndividualOrInherited(
                parentGraph, node, CoreOptions.SPACING_PORT_SURROUNDING);
        
        // Create contexts, count the number of ports on each side, and assign volatile IDs to the ports to be able
        // to properly sort them later
        int volatileId = 0;
        for (PortAdapter<?> port : node.getPorts()) {
            if (port.getSide() == PortSide.UNDEFINED) {
                throw new IllegalArgumentException("Label and node size calculator can only be used with ports that "
                        + "have port sides assigned.");
            }
            
            port.setVolatileId(volatileId++);
            portContexts.put(port.getSide(), new PortContext(this, port));
        }
        
        Arrays.stream(LabelLocation.values())
            .forEach(location -> labelLocationContexts.put(location, new LabelLocationContext(this, location)));
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////
    // Comparators
    
    /**
     * Comparator for two port sides.
     */
    public static int comparePortSides(final PortSide portSide1, final PortSide portSide2) {
        return Integer.compare(portSide1.ordinal(), portSide2.ordinal());
    }
    
    /**
     * Comparator for two port contexts.
     */
    public static int comparePortContexts(final PortContext portContext1, final PortContext portContext2) {
        int portSideComparison = comparePortSides(portContext1.port.getSide(), portContext2.port.getSide());
        if (portSideComparison != 0) {
            return portSideComparison;
        }
        
        // Two ports on the same side. Depending on the side, we need an ascending or a descending comparison, because
        // ports are numbered in a clockwise manner, but we want them to be sorted left-to-right / top-to-bottom
        switch (portContext1.port.getSide()) {
        case NORTH:
        case EAST:
            return Integer.compare(portContext1.port.getVolatileId(), portContext2.port.getVolatileId());
        
        case SOUTH:
        case WEST:
            return Integer.compare(portContext2.port.getVolatileId(), portContext1.port.getVolatileId());
            
        default:
            // This should never happen since we have already checked whether a port has an undefined side in the
            // constructor of this class
            assert false;
        }
        
        return 0;
    }
    
}