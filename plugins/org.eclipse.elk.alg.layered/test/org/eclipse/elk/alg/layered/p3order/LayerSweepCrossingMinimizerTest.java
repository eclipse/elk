/*******************************************************************************
 * Copyright (c) 2011, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p3order;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.intermediate.greedyswitch.TestGraphCreator;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.junit.Test;

// CHECKSTYLEOFF javadoc
// CHECKSTYLEOFF MagicNumber
// CHECKSTYLEOFF MethodName
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
        LNode[] leftNodes = addNodesToLayer(2, makeLayer(getGraph()));
        LNode rightNode = addNodeToLayer(makeLayer(getGraph()));
        eastWestEdgeFromTo(leftNodes[0], rightNode);
        eastWestEdgeFromTo(leftNodes[1], rightNode);
        setFixedOrderConstraint(rightNode);
        setUpIds();

        List<LNode> expectedOrder = switchOrderInList(0, 1, getNodesInLayer(0));

        LayerSweepCrossingMinimizer cMin = new LayerSweepCrossingMinimizer();

        cMin.process(getGraph(), new BasicProgressMonitor());

        assertThat("Layer one", getNodesInLayer(0), is(expectedOrder));
    }

    protected List<LNode> getNodesInLayer(final int index) {
        return getGraph().getLayers().get(index).getNodes();
    }
}
