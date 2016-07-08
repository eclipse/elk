/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.test;

import org.eclipse.elk.alg.layered.LayeredLayoutProvider;
import org.eclipse.elk.alg.layered.properties.LayeredOptions;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.KNode;
import org.eclipse.elk.graph.KPort;
import org.junit.Test;

/**
 * Test and demonstration of 'Using Layout Algorithms Directly'.
 */
public class DirectAlgorithmLayoutTest {

    /**
     * Test a plain Java layout using the ELK Layered algorithm.
     */
    @Test
    public void testELKLayered() {
        // create a KGraph for layout
        KNode parentNode = GraphTestUtils.createSimpleGraph();

        // add layout options to the elements of the graph
        addLayeredOptions(parentNode);

        // create a progress monitor
        IElkProgressMonitor progressMonitor = new BasicProgressMonitor();

        // create the layout provider
        AbstractLayoutProvider layoutProvider = new LayeredLayoutProvider();
        layoutProvider.initialize(null);

        // perform layout on the created graph
        layoutProvider.layout(parentNode, progressMonitor);

        // execute a trivial junit test
        GraphTestUtils.checkNodeCoordinates(parentNode);
        
        // output layout information
        printLayoutInfo(parentNode, progressMonitor);
    }

    /**
     * Adds layout options to the elements of the given parent node.
     * 
     * @param parentNode
     *            parent node representing a graph
     */
    private static void addLayeredOptions(final KNode parentNode) {
        // add options for the parent node
        KShapeLayout parentLayout = parentNode.getData(KShapeLayout.class);
        // set layout direction to horizontal
        parentLayout.setProperty(LayeredOptions.DIRECTION, Direction.RIGHT);

        // add options for the child nodes
        for (KNode childNode : parentNode.getChildren()) {
            KShapeLayout childLayout = childNode.getData(KShapeLayout.class);
            // CHECKSTYLEOFF MagicNumber
            // set some width and height for the child
            childLayout.setWidth(30.0f);
            childLayout.setHeight(30.0f);
            // set fixed size for the child
            childLayout.setProperty(LayeredOptions.NODE_SIZE_CONSTRAINTS, SizeConstraint.fixed());
            // set port constraints to fixed port positions
            childLayout.setProperty(LayeredOptions.PORT_CONSTRAINTS, PortConstraints.FIXED_POS);

            // add options for the ports
            int i = 0;
            for (KPort port : childNode.getPorts()) {
                i++;
                KShapeLayout portLayout = port.getData(KShapeLayout.class);
                // set position and side
                portLayout.setYpos(i * 30.0f / (childNode.getPorts().size() + 1));
                if (childNode.getLabels().get(0).getText().equals("node1")) {
                    portLayout.setXpos(30.0f);
                    portLayout.setProperty(LayeredOptions.PORT_SIDE, PortSide.EAST);
                } else {
                    portLayout.setXpos(0.0f);
                    portLayout.setProperty(LayeredOptions.PORT_SIDE, PortSide.WEST);
                }
            }
        }
    }

    /**
     * Outputs layout information on the console.
     * 
     * @param parentNode
     *            parent node representing a graph
     * @param progressMonitor
     *            progress monitor for the layout run
     */
    private void printLayoutInfo(final KNode parentNode, final IElkProgressMonitor progressMonitor) {
        // print execution time of the algorithm run
        System.out.println("Execution time: " + progressMonitor.getExecutionTime() * 1000 + " ms");

        // print position of each node
        for (KNode childNode : parentNode.getChildren()) {
            KShapeLayout childLayout = childNode.getData(KShapeLayout.class);
            System.out.println(childNode.getLabels().get(0).getText() + ": x = " + childLayout.getXpos() 
                               + ", y = " + childLayout.getYpos());
        }
    }
}
