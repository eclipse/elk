/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.sequence.intermediate;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.eclipse.elk.alg.sequence.graph.LayoutContext;
import org.eclipse.elk.alg.sequence.graph.SExecution;
import org.eclipse.elk.alg.sequence.graph.SLifeline;
import org.eclipse.elk.core.UnsupportedConfigurationException;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Calculates the slots execution specifications are to be placed in. A single simple execution specification is placed
 * at slot 0. All of its direct children are placed at slot 1, their direct children at slot 2, and so forth. For each
 * lifeline, be basically take each bunch of connected executions and run BFS on the root, assigning each execution to
 * its BFS level.
 */
public class ExecutionSlotCalculator implements ILayoutProcessor<LayoutContext> {

    @Override
    public void process(final LayoutContext context, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Assigning executions to slots", 1);
        
        context.sgraph.getLifelines().stream()
            .forEach(ll -> processLifeline(ll));
        
        progressMonitor.done();
    }
    
    /**
     * Computes slots for all executions of the given lifeline.
     */
    private void processLifeline(final SLifeline lifeline) {
        Set<SExecution> visited = new HashSet<>();
        
        for (SExecution sexecution : lifeline.getExcecutions()) {
            if (!visited.contains(sexecution)) {
                bfs(root(sexecution), visited);
            }
        }
    }
    
    /**
     * Returns the root of the execution tree the given execution is part of.
     */
    private SExecution root(final SExecution sexecution) {
        SExecution root = sexecution;
        while (root.getParent() != null) {
            root = root.getParent();
        }
        return root;
    }
    
    /**
     * Assigns slots to the executions in the tree starting at the given root and adds all executions in the tree to
     * the given set.
     */
    private void bfs(final SExecution root, final Set<SExecution> visited) {
        LinkedList<SExecution> queue = new LinkedList<>();
        queue.add(root);
        
        while (!queue.isEmpty()) {
            SExecution currExecution = queue.pollFirst();
            
            if (visited.contains(currExecution)) {
                throw new UnsupportedConfigurationException("Cyclic executions.");
            }
            
            visited.add(currExecution);
            
            if (currExecution.getParent() == null) {
                currExecution.setSlot(0);
            } else {
                currExecution.setSlot(currExecution.getParent().getSlot() + 1);
            }
            
            queue.addAll(currExecution.getChildren());
        }
    }

}
