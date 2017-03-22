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
import org.eclipse.elk.core.math.Spacing;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.NodeLabelPlacement;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortLabelPlacement;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.core.util.IndividualSpacings;
import org.eclipse.elk.core.util.adapters.GraphAdapters.GraphAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.NodeAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.PortAdapter;

import com.google.common.collect.Maps;

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
    /** Space between a port and its labels. */
    public final double portLabelSpacing;
    /** Margin to leave around the set of ports on each side. */
    public final ElkMargin surroundingPortMargins;


    /////////////////////////////////////////////////////////////////////////////////
    // More Contexts
    
    /** Context objects that hold more information about each of the node's ports. */
    public final Map<PortAdapter<?>, PortContext> portContexts = Maps.newHashMap();
    /** Map of all label locations to their location contexts. */
    public final Map<LabelLocation, LabelLocationContext> labelLocationContexts = Maps.newEnumMap(LabelLocation.class);
    
    
    /////////////////////////////////////////////////////////////////////////////////
    // Calculated Things
    
    /** How much space the ports (and possibly their labels) require on each side, if they influence node size. */
    public Spacing requiredPortSpace = null;
    /** If ports extend into the node's insides, this is by how much. */
    public ElkPadding requiredInsidePortSpace = null;
    /** How much space inside port labels require on each side, if they influence node size. */
    public ElkPadding requiredInsidePortLabelSpace = null;
    /** The rows of inside node labels and the client area. */
    public ThreeRowsOrColumns requiredInsideNodeLabelRows = null;
    /** The columns of inside ndoe labels and the client area. */
    public ThreeRowsOrColumns requiredInsideNodeLabelColumns = null;
    /** The columns of outside top node labels. */
    public ThreeRowsOrColumns requiredOutsideTopNodeLabelColumns = null;
    /** The columns of outside bottom node labels. */
    public ThreeRowsOrColumns requiredOutsideBottomNodeLabelColumns = null;
    /** The rows of outside left node labels. */
    public ThreeRowsOrColumns requiredOutsideLeftNodeLabelRows = null;
    /** The rows of outside right node labels. */
    public ThreeRowsOrColumns requiredOutsideRightNodeLabelRows = null;
    
    
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
        portLabelSpacing = IndividualSpacings.getIndividualOrInherited(
                parentGraph, node, CoreOptions.SPACING_LABEL_PORT);
        surroundingPortMargins = IndividualSpacings.getIndividualOrInherited(
                parentGraph, node, CoreOptions.SPACING_PORT_SURROUNDING);
        
        // Create contexts
        node.getPorts()
            .forEach(port -> portContexts.put(port, new PortContext(this, port)));
        
        Arrays.stream(LabelLocation.values())
            .forEach(location -> labelLocationContexts.put(location, new LabelLocationContext(this, location)));
    }
    
}