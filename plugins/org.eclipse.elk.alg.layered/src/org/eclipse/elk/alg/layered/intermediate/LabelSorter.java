/*******************************************************************************
 * Copyright (c) 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import java.util.List;

import org.eclipse.elk.alg.common.nodespacing.cellsystem.LabelCell;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * TODO Document.
 * 
 * <dl>
 *   <dt>Precondition:</dt>
 *     <dd>There are no long edge dummies left.</dd>
 *     <dd>There are no north / south port dummies left.</dd>
 *   <dt>Postcondition:</dt>
 *     <dd>End labels at each port are sorted according to order of source / target nodes.</dd>
 *     <dd>Center edge labels are sorted according to order of source / target nodes.</dd>
 *   <dt>Slots:</dt>
 *     <dd>After phase 5.</dd>
 *   <dt>Same-slot dependencies:</dt>
 *     <dd>{@link LongEdgeJoiner}</dd>
 *     <dd>{@link NorthSouthPortPostprocessor}</dd>
 * </dl>
 */
public final class LabelSorter implements ILayoutProcessor<LGraph> {
    
    @Override
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Sort labels", 1);
        
        layeredGraph.getLayers().stream()
            .flatMap(layer -> layer.getNodes().stream())
            .forEach(node -> processNode(node));
        
        monitor.done();
    }
    
    private void processNode(final LNode node) {
        switch (node.getType()) {
        case NORMAL:
            if (node.hasProperty(InternalProperties.END_LABELS)) {
                List<LabelCell> labelCells = node.getProperty(InternalProperties.END_LABELS);
                for (LabelCell cell : labelCells) {
                    System.out.println(cell);
                }
                // TODO There are end labels. Sort them properly.
            }
            break;
            
        case LABEL:
            // TODO Label dummies need to have their labels ordered properly
            break;
        }
    }
    
    /**
     * Called once we find the first instance of labels that have to be sorted. This method initializes everything 
     */
    private void initialize(final LGraph lGraph) {
        // Give ports ascending IDs
        int nextPortID = 0;
        for (Layer layer : lGraph) {
            for (LNode node : layer) {
                node.id = nextPortID++;
            }
        }
    }
    
}
