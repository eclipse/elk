/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2015 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package org.eclipse.elk.alg.layered.p3order;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.intermediate.greedyswitch.TestGraphCreator;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.junit.Test;

/**
 * @author alan
 *
 */
public class LayerSweepCrossingMinimizerTest extends TestGraphCreator {
    /**
     * <pre>
     * 
     * *  ___
     *  \/| |
     *  /\|_|
     * *
     * </pre>
     * 
     */
    @Test
    public void simpleBackwardSweepCrossing() {
        LNode[] leftNodes = addNodesToLayer(2, makeLayer(graph));
        LNode rightNode = addNodeToLayer(makeLayer(graph));
        eastWestEdgeFromTo(leftNodes[0], rightNode);
        eastWestEdgeFromTo(leftNodes[1], rightNode);
        setFixedOrderConstraint(rightNode);
        setUpIds();  

        List<LNode> expectedOrder = switchOrderInList(0, 1, getNodesInLayer(0));

        LayerSweepCrossingMinimizer cMin = new LayerSweepCrossingMinimizer();

        cMin.process(graph, new BasicProgressMonitor());

        assertThat("Layer one", getNodesInLayer(0), is(expectedOrder));
    }

    protected List<LNode> getNodesInLayer(final int index) {
        return graph.getLayers().get(index).getNodes();
    }
}
