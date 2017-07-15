/*******************************************************************************
 * Copyright (c) 2012, 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import java.util.List;
import java.util.stream.StreamSupport;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LGraphAdapters;
import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.math.ElkRectangle;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.EdgeLabelPlacement;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.core.util.nodespacing.LabelSide;
import org.eclipse.elk.core.util.nodespacing.cellsystem.HorizontalLabelAlignment;
import org.eclipse.elk.core.util.nodespacing.cellsystem.LabelCell;
import org.eclipse.elk.core.util.nodespacing.cellsystem.VerticalLabelAlignment;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

/**
 * <p>This intermediate processor does the necessary calculations for an absolute positioning
 * of all end and port labels. It uses the port sides and the label side choice made before to
 * find this positioning.</p>
 * 
 * <dl>
 *   <dt>Precondition:</dt>
 *     <dd>a layered graph</dd>
 *     <dd>no dummy nodes</dd>
 *     <dd>labels are marked with a placement side</dd>
 *     <dd>nodes have port sides</dd>
 *   <dt>Postcondition:</dt>
 *     <dd>edge and port labels have absolute coordinates.</dd>
 *   <dt>Slots:</dt>
 *     <dd>After phase 5.</dd>
 *   <dt>Same-slot dependencies:</dt>
 *     <dd>{@link LongEdgeJoiner}</dd>
 *     <dd>{@link NorthSouthPortPostProcessor}</dd>
 *     <dd>{@link LabelDummyRemover}</dd>
 *     <dd>{@link ReverseEdgeRestorer}</dd>
 * </dl>
 */
public final class EndLabelProcessor implements ILayoutProcessor<LGraph> {
    
    /**
     * {@inheritDoc}
     */
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("End label placement", 1);
        
        double edgeLabelSpacing = layeredGraph.getProperty(LayeredOptions.SPACING_EDGE_LABEL);
        double labelLabelSpacing = layeredGraph.getProperty(LayeredOptions.SPACING_LABEL_LABEL);
        boolean verticalLayout = layeredGraph.getProperty(LayeredOptions.DIRECTION).isVertical();
        
        // We iterate over each node and place the end labels of its incident edges
        layeredGraph.getLayers().stream()
                .flatMap(layer -> layer.getNodes().stream())
                .forEach(node -> processNode(node, edgeLabelSpacing, labelLabelSpacing, verticalLayout));
        
        monitor.done();
    }
    
    private void processNode(final LNode node, final double edgeLabelSpacing, final double labelLabelSpacing,
            final boolean verticalLayout) {
        
        // At this stage, the port list should be sorted (once this line stops compiling with newer Guava versions,
        // use the Comparators class instead of Ordering, which is scheduled to be deleted sometime > Guava 21)
        assert Ordering.from(new PortListSorter.PortComparator()).isOrdered(node.getPorts());
        
        // Iterate over all ports and collect their labels in label cells
        int portCount = node.getPorts().size(); 
        LabelCell[] portLabelCells = new LabelCell[portCount];
        
        for (int portIndex = 0; portIndex < portCount; portIndex++) {
            LPort port = node.getPorts().get(portIndex);
            port.id = portIndex;
            
            portLabelCells[portIndex] = gatherLabels(port, labelLabelSpacing, verticalLayout);
        }
        
        // Iterate over the created label cells and place them properly
        for (LPort port : node.getPorts()) {
            if (portLabelCells[port.id] != null) {
                placeLabels(port, portLabelCells[port.id], edgeLabelSpacing);
            }
        }
    }

    /**
     * Returns a label cell that contains all end labels to be placed at the given port. If there are no such lables,
     * {@code null} is magically returned.
     */
    private LabelCell gatherLabels(final LPort port, final double labelLabelSpacing, final boolean verticalLayout) {
        List<LLabel> labels = Lists.newArrayList();
        
        for (LEdge incidentEdge : port.getConnectedEdges()) {
            if (incidentEdge.getSource() == port) {
                // It's an outgoing edge; all tail labels belong to this port
                incidentEdge.getLabels().stream()
                        .filter(label -> label.getProperty(
                                LayeredOptions.EDGE_LABELS_PLACEMENT) == EdgeLabelPlacement.TAIL)
                        .forEach(label -> labels.add(label));
            } else {
                // It's an incoming edge; all head labels belong to this port
                incidentEdge.getLabels().stream()
                        .filter(label -> label.getProperty(
                                LayeredOptions.EDGE_LABELS_PLACEMENT) == EdgeLabelPlacement.HEAD)
                        .forEach(label -> labels.add(label));
            }
        }
        
        return createConfiguredLabelCell(port, labels, labelLabelSpacing, verticalLayout);
    }
    
    /**
     * Creates label cell for the given port with the given labels, if any. If there are no labels, this method returns
     * {@code null}. The label cell will still have to have its alignment set up.
     */
    private LabelCell createConfiguredLabelCell(final LPort port, final List<LLabel> labels,
            final double labelLabelSpacing, final boolean verticalLayout) {
        
        if (labels.isEmpty()) {
            return null;
        }
        
        // Create the new label cell and setup its alignments depending on the port's side
        LabelCell labelCell = new LabelCell(labelLabelSpacing, !verticalLayout);
        
        for (LLabel label : labels) {
            labelCell.addLabel(LGraphAdapters.adapt(label));
        }
        
        return labelCell;
    }

    /**
     * Places the edge end labels that are to be placed near the given port.
     */
    private void placeLabels(final LPort port, final LabelCell labelCell, final double edgeLabelSpacing) {
        // Setup the cell's cell rectangle
        ElkRectangle labelCellRect = labelCell.getCellRectangle();
        labelCellRect.height = labelCell.getMinimumHeight();
        labelCellRect.width = labelCell.getMinimumWidth();
        
        KVector portAbsolutePos = KVector.sum(port.getPosition(), port.getNode().getPosition());
        KVector portAbsoluteAnchor = port.getAbsoluteAnchor();
        
        // Calculate cell position depending on port side
        switch (port.getSide()) {
        case NORTH:
            labelCell.setVerticalAlignment(VerticalLabelAlignment.BOTTOM);
            labelCellRect.y = portAbsolutePos.y
                    - port.getMargin().top
                    - edgeLabelSpacing
                    - labelCellRect.height;
            
            if (getLabelSide(labelCell) == LabelSide.ABOVE) {
                labelCell.setHorizontalAlignment(HorizontalLabelAlignment.RIGHT);
                labelCellRect.x = portAbsoluteAnchor.x
                        - maxEdgeThickness(port)
                        - edgeLabelSpacing
                        - labelCellRect.width;
            } else {
                labelCell.setHorizontalAlignment(HorizontalLabelAlignment.LEFT);
                labelCellRect.x = portAbsoluteAnchor.x
                        + maxEdgeThickness(port)
                        + edgeLabelSpacing;
            }
            break;
            
        case EAST:
            labelCell.setHorizontalAlignment(HorizontalLabelAlignment.LEFT);
            labelCellRect.x = portAbsolutePos.x
                    + port.getSize().x
                    + port.getMargin().right
                    + edgeLabelSpacing;
            
            if (getLabelSide(labelCell) == LabelSide.ABOVE) {
                labelCell.setVerticalAlignment(VerticalLabelAlignment.BOTTOM);
                labelCellRect.y = portAbsoluteAnchor.y
                        - maxEdgeThickness(port)
                        - edgeLabelSpacing
                        - labelCellRect.height;
            } else {
                labelCell.setVerticalAlignment(VerticalLabelAlignment.TOP);
                labelCellRect.y = portAbsoluteAnchor.y
                        + maxEdgeThickness(port)
                        + edgeLabelSpacing;
            }
            break;
            
        case SOUTH:
            labelCell.setVerticalAlignment(VerticalLabelAlignment.TOP);
            labelCellRect.y = portAbsolutePos.y
                    + port.getSize().y
                    + port.getMargin().bottom
                    + edgeLabelSpacing;
            
            if (getLabelSide(labelCell) == LabelSide.ABOVE) {
                labelCell.setHorizontalAlignment(HorizontalLabelAlignment.RIGHT);
                labelCellRect.x = portAbsoluteAnchor.x
                        - maxEdgeThickness(port)
                        - edgeLabelSpacing
                        - labelCellRect.width;
            } else {
                labelCell.setHorizontalAlignment(HorizontalLabelAlignment.LEFT);
                labelCellRect.x = portAbsoluteAnchor.x
                        + maxEdgeThickness(port)
                        + edgeLabelSpacing;
            }
            break;
            
        case WEST:
            labelCell.setHorizontalAlignment(HorizontalLabelAlignment.RIGHT);
            labelCellRect.x = portAbsolutePos.x
                    - port.getMargin().left
                    - edgeLabelSpacing
                    - labelCellRect.width;
            
            if (getLabelSide(labelCell) == LabelSide.ABOVE) {
                labelCell.setVerticalAlignment(VerticalLabelAlignment.BOTTOM);
                labelCellRect.y = portAbsoluteAnchor.y
                        - maxEdgeThickness(port)
                        - edgeLabelSpacing
                        - labelCellRect.height;
            } else {
                labelCell.setVerticalAlignment(VerticalLabelAlignment.TOP);
                labelCellRect.y = portAbsoluteAnchor.y
                        + maxEdgeThickness(port)
                        + edgeLabelSpacing;
            }
            break;
        }
        
        labelCell.applyLabelLayout();
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Utility Methods
    
    /**
     * Retrieve the side of the edge the labels of the given cell should be placed at. Different labels may actually
     * have different label sides configured for them, but we simply put them all into one place. That that!
     */
    private LabelSide getLabelSide(final LabelCell labelCell) {
        assert labelCell != null;
        assert labelCell.hasLabels();
        
        return labelCell.getLabels().get(0).getProperty(InternalProperties.LABEL_SIDE);
    }
    
    /**
     * Returns the maximum thickness of all edges incident to the port.
     */
    private double maxEdgeThickness(final LPort port) {
        return StreamSupport.stream(port.getConnectedEdges().spliterator(), false)
                .mapToDouble(edge -> edge.getProperty(LayeredOptions.EDGE_THICKNESS))
                .max()
                .getAsDouble();
    }

}
