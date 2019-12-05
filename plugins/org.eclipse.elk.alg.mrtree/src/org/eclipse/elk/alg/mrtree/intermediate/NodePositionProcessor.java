/*******************************************************************************
 * Copyright (c) 2013, 2015 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.mrtree.intermediate;

import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.elk.alg.mrtree.graph.TGraph;
import org.eclipse.elk.alg.mrtree.graph.TNode;
import org.eclipse.elk.alg.mrtree.options.InternalProperties;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * A processor which sets the final coordinates for each node in a given graph. The property XCOOR
 * has to be set before this processor is called.
 * 
 * @author sor
 * @author sgu
 */
public class NodePositionProcessor implements ILayoutProcessor<TGraph> {

    /** number of nodes in the graph. */
    private int numberOfNodes;

    /**
     * {@inheritDoc}
     */
    public void process(final TGraph tGraph, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Processor set coordinates", 1);

        /** save number of nodes for progress computation */
        numberOfNodes = tGraph.getNodes().isEmpty() ? 1 : tGraph.getNodes().size();

        /** find the root of the component */
        TNode root = null;
        Iterator<TNode> it = tGraph.getNodes().iterator();
        while (root == null && it.hasNext()) {
            TNode tNode = it.next();
            if (tNode.getProperty(InternalProperties.ROOT)) {
                root = tNode;
                KVector pos = tNode.getPosition();
                pos.x = tNode.getProperty(InternalProperties.XCOOR).doubleValue();
                pos.y = 0;
            }
        }

        /** start with the root and level down by bsf */
        setCoordinates(root.getChildrenCopy(), progressMonitor.subTask(1.0f));

        progressMonitor.done();
    }

    /**
     * Set the coordinate for each node in a given level and all underlying levels.
     * 
     * @param currentLevel
     *            the list of TNode for which the neighbors should be calculated
     * @param level
     *            the level index
     * @param progressMonitor
     *            the current progress monitor
     */
    private void setCoordinates(final LinkedList<TNode> currentLevel,
            final IElkProgressMonitor progressMonitor) {

        /** if the level is empty there is nothing to do */
        if (!currentLevel.isEmpty()) {

            LinkedList<TNode> nextLevel = new LinkedList<TNode>();

            /**
             * set the coordinates for each node in the current level and collect the nodes of the
             * next level
             */
            for (TNode tNode : currentLevel) {
                nextLevel.addAll(tNode.getChildrenCopy());
                KVector pos = tNode.getPosition();
                pos.x = tNode.getProperty(InternalProperties.XCOOR).doubleValue();
                pos.y = tNode.getProperty(InternalProperties.YCOOR).doubleValue();
            }

            /** go to the next level */
            setCoordinates(nextLevel, progressMonitor.subTask(nextLevel.size() / numberOfNodes));
        }
    }
    
}
