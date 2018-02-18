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

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.sequence.graph.SGraphAdapters.SGraphAdapter;
import org.eclipse.elk.alg.sequence.options.LabelAlignmentStrategy;
import org.eclipse.elk.alg.sequence.options.LabelSideSelection;
import org.eclipse.elk.alg.sequence.options.LifelineSortingStrategy;
import org.eclipse.elk.alg.sequence.options.SequenceDiagramOptions;
import org.eclipse.elk.core.labels.ILabelManager;
import org.eclipse.elk.core.labels.LabelManagementOptions;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.graph.ElkGraphElement;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * A simple data holder class used to pass data about the layout process around to the different phases
 * of the layout algorithm.
 */
public final class LayoutContext {
    // CHECKSTYLEOFF VisibilityModifier
    
    // Layout Graphs
    
    /** The original ELK graph the layout algorithm was called with. */
    public final ElkNode elkgraph;
    /** The {@link SGraph} to be laid out. */
    public SGraph sgraph;
    /** The {@link LGraph} created from the SGraph. */
    public LGraph lgraph;
    
    /** A wrapper around the {@link SGraph} used for comment size calculations. */ 
    public SGraphAdapter sgraphAdapter;
    
    
    // Layout Settings
    
    /** The label side selection strategy. */
    public final LabelSideSelection labelSideSelection;
    /** The label alignment strategy. */
    public final LabelAlignmentStrategy labelAlignment;
    /** The label managemer, if any. */
    public final ILabelManager labelManager;
    
    /** The lifeline sorting strategy. */
    public final LifelineSortingStrategy sortingStrategy;
    /** Whether to include areas in the lifeline sorting process. Used by some sorters. */
    public final boolean groupAreasWhenSorting;
    
    /** Horizontal spacing between two consecutive lifelines. */
    public final double lifelineSpacing;
    /** Vertical spacing between two consecutive layers of messages. */
    public final double messageSpacing;
    /** Space to be left between labels and labeled elements. */
    public final double labelSpacing;
    /** Default padding between an area's border and its innards. Can be overriden by area nodes. */
    public final ElkPadding areaPadding;
    /** The height of lifeline headers. */
    // TODO: This should actually be specific to each lifeline.
    public final double lifelineHeaderHeight;
    /** The minimum height of executions. */
    public final double minExecutionHeight;
    /** The width of executions. */
    public final double executionWidth;
    /** The width of timing observations. */
    public final double timeObservationWidth;
    
    // CHECKSTYLEON VisibilityModifier
    
    
    /**
     * Creates a new instance intialized based on the properties configured for the given interaction node.
     * 
     * @param interactionNode
     *            parent node of the graph that is to be laid out.
     */
    public LayoutContext(final ElkNode parentNode) {
        elkgraph = parentNode;
        
        labelSideSelection = parentNode.getProperty(
                SequenceDiagramOptions.LABEL_SIDE);
        labelAlignment = parentNode.getProperty(
                SequenceDiagramOptions.LABEL_ALIGNMENT);
        
        // Label management can be installed either on the interaction node or the parent graph
        if (elkgraph.hasProperty(LabelManagementOptions.LABEL_MANAGER)) {
            labelManager = elkgraph.getProperty(LabelManagementOptions.LABEL_MANAGER);
        } else {
            ElkGraphElement root = (ElkGraphElement) EcoreUtil.getRootContainer(elkgraph);
            labelManager = root.getProperty(LabelManagementOptions.LABEL_MANAGER);
        }
        
        sortingStrategy = parentNode.getProperty(
                SequenceDiagramOptions.LIFELINE_SORTING_STRATEGY);
        groupAreasWhenSorting = parentNode.getProperty(
                SequenceDiagramOptions.AREAS_GROUP);
        
        lifelineSpacing = parentNode.getProperty(
                SequenceDiagramOptions.SPACING_LIFELINE);
        messageSpacing = parentNode.getProperty(
                SequenceDiagramOptions.SPACING_MESSAGE);
        labelSpacing = parentNode.getProperty(
                SequenceDiagramOptions.SPACING_EDGE_LABEL);
        areaPadding = parentNode.getProperty(
                SequenceDiagramOptions.AREAS_PADDING);
        lifelineHeaderHeight = parentNode.getProperty(
                SequenceDiagramOptions.SIZE_LIFELINE_HEADER_HEIGHT);
        minExecutionHeight = parentNode.getProperty(
                SequenceDiagramOptions.SIZE_MIN_EXECUTION_HEIGHT);
        executionWidth = parentNode.getProperty(
                SequenceDiagramOptions.SIZE_EXECUTION_WIDTH);
        timeObservationWidth = parentNode.getProperty(
                SequenceDiagramOptions.SIZE_TIME_OBSERVATION_WIDTH);
    }
}