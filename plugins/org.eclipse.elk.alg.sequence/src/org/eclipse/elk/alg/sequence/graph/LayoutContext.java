/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.sequence.graph;

import java.util.List;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.sequence.options.CoordinateSystem;
import org.eclipse.elk.alg.sequence.options.LabelAlignment;
import org.eclipse.elk.alg.sequence.options.LifelineSortingStrategy;
import org.eclipse.elk.alg.sequence.properties.SequenceDiagramOptions;
import org.eclipse.elk.graph.ElkNode;

/**
 * A simple data holder class used to pass data about the layout process around to the different phases
 * of the layout algorithm.
 * 
 * @author cds
 */
public final class LayoutContext {
    // CHECKSTYLEOFF VisibilityModifier
    
    // Layout Graphs
    
    /** The original KGraph the layout algorithm was called with. */
    public ElkNode kgraph;
    /** The {@link SGraph} to be laid out. */
    public SGraph sgraph;
    /** The {@link LGraph} created from the SGraph. */
    public LGraph lgraph;
    /** The order of lifelines as determined later in the algorithm. */
    public List<SLifeline> lifelineOrder;
    
    
    // Layout Settings
    
    /** Border spacing. */
    public double borderSpacing;
    /** Vertical spacing between two neighbored layers of messages. */
    public double messageSpacing;
    /** Horizontal spacing between two neighbored lifelines. */
    public double lifelineSpacing;
    /** The vertical position of lifelines. */
    public double lifelineYPos;
    /** The height of the lifeline's header. */
    public double lifelineHeader;
    /** The height of the header of combined fragments. */
    public double areaHeader;
    /** The width of timing observations. */
    public double timeObservationWidth;
    /** The offset between two nested areas. */
    public double containmentOffset;
    /** The label alignment strategy. */
    public LabelAlignment labelAlignment;
    /** The lifeline sorting strategy. */
    public LifelineSortingStrategy sortingStrategy;
    /** Whether to include areas in the lifeline sorting process. Used by some sorters. */
    public boolean groupAreasWhenSorting;
    /** The coordinate system to use. */
    public CoordinateSystem coordinateSystem;
    
    // CHECKSTYLEON VisibilityModifier
    
    
    /**
     * Use {@link #fromLayoutData(KLayoutData)} to obtain a new instance.
     */
    private LayoutContext() {
        
    }
    
    /**
     * Creates a new instance initialized based on the given layout data. Should be called with the
     * layout data of the sequence diagram graph.
     * 
     * @param parentNode
     *            parent node of the graph that is to be laid out.
     * @return initialized context object.
     */
    public static LayoutContext fromLayoutData(final ElkNode parentNode) {
        LayoutContext context = new LayoutContext();
        
        context.kgraph = parentNode;
        
        // TODO The padding needs to be respected to al four sides
        context.borderSpacing = parentNode.getProperty(SequenceDiagramOptions.PADDING).top;
        context.messageSpacing = parentNode.getProperty(SequenceDiagramOptions.MESSAGE_SPACING);
        context.lifelineSpacing = parentNode.getProperty(SequenceDiagramOptions.LIFELINE_SPACING);
        context.lifelineYPos = parentNode.getProperty(SequenceDiagramOptions.LIFELINE_Y_POS);
        context.lifelineHeader = parentNode.getProperty(
                SequenceDiagramOptions.LIFELINE_HEADER_HEIGHT);
        context.areaHeader = parentNode.getProperty(SequenceDiagramOptions.AREA_HEADER_HEIGHT);
        context.timeObservationWidth = parentNode.getProperty(
                SequenceDiagramOptions.TIME_OBSERVATION_WIDTH);
        context.containmentOffset = parentNode.getProperty(
                SequenceDiagramOptions.CONTAINMENT_OFFSET);
        context.labelAlignment = parentNode.getProperty(SequenceDiagramOptions.LABEL_ALIGNMENT);
        context.sortingStrategy = parentNode.getProperty(
                SequenceDiagramOptions.LIFELINE_SORTING_STRATEGY);
        context.groupAreasWhenSorting = parentNode.getProperty(SequenceDiagramOptions.GROUP_AREAS);
        context.coordinateSystem = parentNode.getProperty(SequenceDiagramOptions.COORDINATE_SYSTEM);
        
        return context;
    }
}