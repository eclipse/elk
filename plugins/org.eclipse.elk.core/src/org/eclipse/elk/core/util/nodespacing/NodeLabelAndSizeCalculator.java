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

import org.eclipse.elk.core.util.adapters.GraphAdapters.GraphAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.NodeAdapter;
import org.eclipse.elk.core.util.nodespacing.internal.algorithm.CellSystemConfigurator;
import org.eclipse.elk.core.util.nodespacing.internal.algorithm.InsidePortLabelCellCreator;
import org.eclipse.elk.core.util.nodespacing.internal.algorithm.LabelPlacer;
import org.eclipse.elk.core.util.nodespacing.internal.algorithm.NodeLabelAndSizeUtilities;
import org.eclipse.elk.core.util.nodespacing.internal.algorithm.NodeLabelCellCreator;
import org.eclipse.elk.core.util.nodespacing.internal.algorithm.NodeSizeCalculator;
import org.eclipse.elk.core.util.nodespacing.internal.algorithm.PortContextCreator;
import org.eclipse.elk.core.util.nodespacing.internal.algorithm.PortLabelPlacementCalculator;
import org.eclipse.elk.core.util.nodespacing.internal.algorithm.PortPlacementCalculator;
import org.eclipse.elk.core.util.nodespacing.internal.algorithm.PortPlacementSizeCalculator;
import org.eclipse.elk.core.util.nodespacing.internal.contexts.NodeContext;


// TODO Add support for FIXED_RATIO port constraints
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

        /* PREPARATIONS
         * Create new context object containing all relevant context information, including pointers to all the
         * components of the cell system. The different method calls will often just update information in the context
         * object (or nested objects) that subsequent method calls will make use of. Creating this context object will
         * already cause it to initialize some information contained in it for more convenient access.
         */
        NodeContext nodeContext = new NodeContext(graph, node);
        PortContextCreator.createPortContexts(nodeContext);
        
        
        /* SETUP ALL CELLS
         * Create all container cells that can hold node label containers, both for the outside and for the inside.
         * Also, create all the relevant label cells. For inside port label cells, the height or width (and relevant
         * offset) is calculated under the assumption that ports can be placed freely. The information may later be
         * overwritten if inside port label placement actually needs more space.
         */
        NodeLabelCellCreator.createNodeLabelCells(nodeContext);
        InsidePortLabelCellCreator.createInsidePortLabelCells(nodeContext);
        
        
        /* SETUP CLIENT AREA SPACE AND NODE CELL PADDING
         * Apply the minimum client area size to the central grid container cell. Also, reserve space for ports that
         * extend inside the node due to negative port border offsets.
         */
        NodeLabelAndSizeUtilities.setupMinimumClientAreaSize(nodeContext);
        NodeLabelAndSizeUtilities.setupNodePaddingForPortsWithOffset(nodeContext);
        
        
        /* MINIMUM SPACE REQUIRED TO PLACE PORTS
         * It is now time to find out how large the node needs to be if all ports are to be placed in a way that
         * satisfies all spacing constraints. This may or may not include the labels of ports.
         */
        PortPlacementSizeCalculator.calculateHorizontalPortPlacementSize(nodeContext);
        PortPlacementSizeCalculator.calculateVerticalPortPlacementSize(nodeContext);
        
        
        /* SETUP CELL SYSTEM SIZE CONTRIBUTION FLAGS
         * Depending on the size constraints, the different cells may contribute to the height or to the width of the
         * node. In this phase, we setup the size contribution flags according to the size constraints.
         */
        CellSystemConfigurator.configureCellSystemSizeContributions(nodeContext);
        
        
        /* SET NODE WIDTH AND PLACE HORIZONTAL PORTS
         * We can now set the node's width and place the ports (and port labels) along the horizontal sides. Since we
         * have no idea how large the node is going to be yet, we place southern ports with the assumption that the
         * node has a height of 0. We will later have to offset those ports by the node's height. Setting the node
         * width has the side effect of computing a horizontal layout for the cell system.
         */
        NodeSizeCalculator.setNodeWidth(nodeContext);
        
        PortPlacementCalculator.placeHorizontalPorts(nodeContext);
        PortLabelPlacementCalculator.placeHorizontalPortLabels(nodeContext);
        
        
        /* SET NODE HEIGHT AND PLACE VERTICAL PORTS
         * We can now calculate the node's height and place the ports (and port labels) along the vertical sides. Also,
         * since we now know the node's height, we can finally correct the southern port positions. Before we can do
         * all that, however, we might need to update the settings of the eastern and western inside port label cells
         * to be sure that free ports are positioned properly.
         */
        CellSystemConfigurator.updateVerticalInsidePortLabelCellPadding(nodeContext);
        NodeSizeCalculator.setNodeHeight(nodeContext);
        NodeLabelAndSizeUtilities.offsetSouthernPortsByNodeSize(nodeContext);
        
        PortPlacementCalculator.placeVerticalPorts(nodeContext);
        PortLabelPlacementCalculator.placeVerticalPortLabels(nodeContext);
        
        
        /* PLACE LABELS
         * Since we now have the node's final size, we can now calculate the positions of the containers for outer node
         * labels and place all inner and outer node labels. Also, the port label cells have positions assigned to them
         * and can be told to apply positions to their labels.
         */
        LabelPlacer.placeLabels(nodeContext);
        // TODO Apply client area size
    }
    
}
