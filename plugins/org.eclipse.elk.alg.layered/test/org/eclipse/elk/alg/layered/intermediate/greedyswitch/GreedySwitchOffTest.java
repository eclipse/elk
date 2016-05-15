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
package org.eclipse.elk.alg.layered.intermediate.greedyswitch;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.p3order.LayerSweepCrossingMinimizer;
import org.eclipse.elk.alg.layered.p3order.LayerSweepCrossingMinimizer.CrossMinType;
import org.eclipse.elk.alg.layered.properties.GreedySwitchType;
import org.eclipse.elk.alg.layered.properties.LayeredOptions;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.junit.Test;

/**
 * Tests OFF setting.
 *
 * @author alan
 *
 */
public class GreedySwitchOffTest {

    // CHECKSTYLEOFF javadoc
    // CHECKSTYLEOFF MagicNumber
    @Test
    public void greedySwitchIsOff() {
        TestGraphCreator creator = new TestGraphCreator();
        LGraph graph = creator.getCrossFormedGraph();
        graph.setProperty(LayeredOptions.CROSSING_MINIMIZATION_GREEDY_SWITCH, GreedySwitchType.OFF);

        List<LNode> expectedOrderLayerOne = getNodesInLayer(0, graph);
        List<LNode> expectedOrderLayerTwo = getNodesInLayer(1, graph);

        LayerSweepCrossingMinimizer greedySwitcher =
                new LayerSweepCrossingMinimizer(CrossMinType.GREEDY_SWITCH);
        greedySwitcher.process(graph, new BasicProgressMonitor());

        assertThat(getNodesInLayer(0, graph), is(expectedOrderLayerOne));
        assertThat(getNodesInLayer(1, graph), is(expectedOrderLayerTwo));
    }

    private List<LNode> getNodesInLayer(final int layerIndex, final LGraph graph) {
        return new ArrayList<LNode>(graph.getLayers().get(layerIndex).getNodes());
    }
}
