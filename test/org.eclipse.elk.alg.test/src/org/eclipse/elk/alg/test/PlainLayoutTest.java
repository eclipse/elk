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

import org.eclipse.elk.alg.layered.properties.LayeredOptions;
import org.eclipse.elk.core.RecursiveGraphLayoutEngine;
import org.eclipse.elk.core.data.LayoutMetaDataService;
import org.eclipse.elk.core.klayoutdata.KLayoutData;
import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.KNode;
import org.junit.Test;

import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;

/**
 * Test and demonstration of 'Plain Java Layout'. 
 */
public class PlainLayoutTest {

    /**
     * Test a plain Java layout on a hierarchical graph using the ELK Layered algorithm.
     */
    @Test
    public void testPlainLayout() {

        // create a hierarchical KGraph for layout
        KNode parentNode = GraphTestUtils.createHierarchicalGraph();

        // configure every hierarchical node to use ELK Layered (which would also be the default) 
        getAllKNodes(parentNode).forEachRemaining(node -> {
            if (!node.getChildren().isEmpty()) {
                node.getData(KLayoutData.class).setProperty(CoreOptions.ALGORITHM, LayeredOptions.ALGORITHM_ID);
            }
        });

        // create a progress monitor
        IElkProgressMonitor progressMonitor = new BasicProgressMonitor();

        // initialize the meta data service with ELK Layered's meta data
        LayoutMetaDataService service = LayoutMetaDataService.getInstance();
        service.registerLayoutMetaDataProvider(new LayeredOptions());

        // instantiate a recursive graph layout engine and execute layout
        RecursiveGraphLayoutEngine layoutEngine = new RecursiveGraphLayoutEngine();
        layoutEngine.layout(parentNode, progressMonitor);

        // output layout information
        printLayoutInfo(parentNode, progressMonitor);
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
        // SUPPRESS CHECKSTYLE NEXT MagicNumber
        System.out.println("Execution time: " + progressMonitor.getExecutionTime() * 1000 + " ms");

        // print position of each node
        getAllKNodes(parentNode).forEachRemaining(node -> {
            KShapeLayout childLayout = node.getData(KShapeLayout.class);
            System.out.println(node.getLabels().get(0).getText() + ": x = " + childLayout.getXpos() + ", y = "
                    + childLayout.getYpos());
        });
    }
    
    private UnmodifiableIterator<KNode> getAllKNodes(final KNode parentNode) {
        return Iterators.filter(parentNode.eAllContents(), KNode.class);
    }
}
