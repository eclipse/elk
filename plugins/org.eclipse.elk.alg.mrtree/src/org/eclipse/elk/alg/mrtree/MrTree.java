/*******************************************************************************
 * Copyright (c) 2013, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.mrtree;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.elk.alg.mrtree.graph.TGraph;
import org.eclipse.elk.alg.mrtree.intermediate.IntermediateProcessorStrategy;
import org.eclipse.elk.alg.mrtree.p1treeify.DFSTreeifyer;
import org.eclipse.elk.alg.mrtree.p2order.OrderBalance;
import org.eclipse.elk.alg.mrtree.p3place.NodePlacer;
import org.eclipse.elk.alg.mrtree.p4route.EdgeRouter;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Implements a layout algorithm for trees. The tree layouter uses the algorithm from
 * "A Node-Positioning Algorithm for General Trees, John Q.Walker II" to layout trees. To do this it
 * uses four phases plus a pre-processing to build a corresponding data structure. The first phase
 * "treeifying" transforms the given graph into a tree if necessary. To do this, edges which destroy
 * the tree property will be removed and stored, so that they can be reinserted during a post
 * processing. In the second phase "orderNodes" the nodes of each level are separated into leaves
 * and inner nodes. And then whitespace in the level is filled with leaves. The third phase
 * "NodePlacer" uses the algorithm first mentioned from John Q.Walker II to compute the actual
 * position of the nodes. The last phase routeEdges sets the positions for the edges corresponding
 * to the positions of the nodes.
 * 
 * Each phase uses intermediate processors for small computations on the graph. The corresponding
 * processors are defined in each phase. Some are defined multiple times, but they are invoked only
 * once.
 * 
 * The processors can determine roots of components and fan outs or level neighbors of nodes.
 * 
 * @author sor
 * @author sgu
 */
public final class MrTree {

    // /////////////////////////////////////////////////////////////////////////////
    // Variables

    /** phase 1: treeing module. */
    private ILayoutPhase dfsTreeifyer;
    /** phase 2: order module. */
    private ILayoutPhase nodeOrderer;
    /** phase 3: arrange module. */
    private ILayoutPhase nodePlacer;
    /** phase 4: route module. */
    private ILayoutPhase edgeRouter;

    /** intermediate layout processor configuration. */
    private IntermediateProcessingConfiguration intermediateProcessingConfiguration
            = new IntermediateProcessingConfiguration();

    /** collection of instantiated intermediate modules. */
    private Map<IntermediateProcessorStrategy, ILayoutProcessor> intermediateLayoutProcessorCache
            = new HashMap<IntermediateProcessorStrategy, ILayoutProcessor>();

    /** list of layout processors that compose the current algorithm. */
    private List<ILayoutProcessor> algorithm = new LinkedList<ILayoutProcessor>();

    // /////////////////////////////////////////////////////////////////////////////
    // Regular Layout

    /**
     * Does a layout on the given graph.
     * 
     * @param tgraph
     *            the graph to layout.
     * @param progressMonitor
     *            a progress monitor to show progress information in.
     * @return tree graph with layout applied.
     */
    public TGraph doLayout(final TGraph tgraph, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Tree layout", 1);

        // set up the phases and processors depending on user options
        updateModules(tgraph);

        // do layout for each component
        layout(tgraph, progressMonitor.subTask(1.0f));

        progressMonitor.done();

        return tgraph;
    }

    // /////////////////////////////////////////////////////////////////////////////
    // Options and Modules Management

    /**
     * Update the modules depending on user options.
     * 
     * @param graph
     *            the graph to be laid out.
     */
    private void updateModules(final TGraph graph) {

        // build a tree
        if (dfsTreeifyer == null) {
            dfsTreeifyer = new DFSTreeifyer();
        }

        // order nodes
        if (nodeOrderer == null) {
            nodeOrderer = new OrderBalance();
        }

        // set node placement strategy to use
        // arrange nodes
        if (nodePlacer == null) {
            nodePlacer = new NodePlacer();
        }

        // set node placement strategy to use
        // arrange nodes
        if (edgeRouter == null) {
            edgeRouter = new EdgeRouter();
        }

        // update intermediate processor configuration
        intermediateProcessingConfiguration.clear();
        intermediateProcessingConfiguration
                .addAll(dfsTreeifyer.getIntermediateProcessingConfiguration(graph))
                .addAll(nodeOrderer.getIntermediateProcessingConfiguration(graph))
                .addAll(nodePlacer.getIntermediateProcessingConfiguration(graph))
                .addAll(edgeRouter.getIntermediateProcessingConfiguration(graph));
        // TODO add intermediate processing configuration for block direction
        // .addAll(this.getIntermediateProcessingConfiguration(graph));

        // construct the list of processors that make up the algorithm
        algorithm.clear();
        algorithm.addAll(getIntermediateProcessorList(
                IntermediateProcessingConfiguration.BEFORE_PHASE_1));
        algorithm.add(dfsTreeifyer);
        algorithm.addAll(getIntermediateProcessorList(
                IntermediateProcessingConfiguration.BEFORE_PHASE_2));
        algorithm.add(nodeOrderer);
        algorithm.addAll(getIntermediateProcessorList(
                IntermediateProcessingConfiguration.BEFORE_PHASE_3));
        algorithm.add(nodePlacer);
        algorithm.addAll(getIntermediateProcessorList(
                IntermediateProcessingConfiguration.BEFORE_PHASE_4));
        algorithm.add(edgeRouter);
        algorithm.addAll(getIntermediateProcessorList(
                IntermediateProcessingConfiguration.AFTER_PHASE_4));
    }

    /**
     * Returns a list of layout processor instances for the given intermediate layout processing
     * slot.
     * 
     * @param slotIndex
     *            the slot index. One of the constants defined in
     *            {@link IntermediateProcessingConfiguration}.
     * @return list of layout processors.
     */
    private List<ILayoutProcessor> getIntermediateProcessorList(final int slotIndex) {
        // fetch the set of layout processors configured for the given slot
        EnumSet<IntermediateProcessorStrategy> processors = intermediateProcessingConfiguration
                .getProcessors(slotIndex);
        List<ILayoutProcessor> result = new ArrayList<ILayoutProcessor>(processors.size());

        // iterate through the layout processors and add them to the result list; the EnumSet
        // guarantees that we iterate over the processors in the order in which they occur in
        // the LayoutProcessorStrategy, thereby satisfying all of their runtime order
        // dependencies without having to sort them in any way
        for (IntermediateProcessorStrategy processor : processors) {
            // check if an instance of the given layout processor is already in the cache
            ILayoutProcessor processorImpl = intermediateLayoutProcessorCache.get(processor);

            if (processorImpl == null) {
                // It's not in the cache, so create it and put it in the cache
                processorImpl = processor.create();
                intermediateLayoutProcessorCache.put(processor, processorImpl);
            }

            // add the layout processor to the list of processors for this slot
            result.add(processorImpl);
        }

        return result;
    }

    // TODO add this for intermediate processing configuration for block direction
    // /**
    // * Returns an intermediate processing configuration with processors not tied to specific
    // phases.
    // *
    // * @param graph
    // * the tree graph to be processed. The configuration may vary depending on certain
    // * properties of the graph.
    // * @return intermediate processing configuration. May be {@code null}.
    // */
    // private IntermediateProcessingConfiguration getIntermediateProcessingConfiguration(
    // final TGraph graph) {
    //
    // // get graph properties
    //
    // // Basic configuration
    // IntermediateProcessingConfiguration configuration = new
    // IntermediateProcessingConfiguration();
    //
    // // graph transformations for unusual layout directions
    // switch (graph.getProperty(LayoutOptions.DIRECTION)) {
    // case LEFT:
    // // ADD LEFT_DIR_POSTPROCESSOR to IntermediateProcessingConfiguration
    // break;
    // case RIGHT:
    // // ADD RIGHT_DIR_POSTPROCESSOR to IntermediateProcessingConfiguration
    // break;
    // case DOWN:
    // // ADD DOWN_DIR_POSTPROCESSOR to IntermediateProcessingConfiguration
    // break;
    // case UP:
    // // ADD UP_DIR_POSTPROCESSOR to IntermediateProcessingConfiguration
    // break;
    // default:
    // // This is either RIGHT or UNDEFINED, which is just mapped to RIGHT. Either way, we
    // // don't
    // // need any processors here
    // break;
    // }
    //
    // // Additional dependencies
    //
    // return configuration;
    // }

    // /////////////////////////////////////////////////////////////////////////////
    // Layout

    /**
     * Perform the phases of the tree layouter.
     * 
     * @param graph
     *            the graph that is to be laid out
     * @param themonitor
     *            a progress monitor, or {@code null}
     */
    private void layout(final TGraph graph, final IElkProgressMonitor themonitor) {
        IElkProgressMonitor monitor = themonitor;
        if (monitor == null) {
            monitor = new BasicProgressMonitor();
        }
        monitor.begin("Layout", algorithm.size());

        if (graph.getProperty(LayoutOptions.DEBUG_MODE)) {
            // Debug Mode!
            // Prints the algorithm configuration and outputs the whole graph to a file
            // before each slot execution

            System.out.println("KLay Tree uses the following " + algorithm.size() + " modules:");
            for (int i = 0; i < algorithm.size(); i++) {
                System.out.println("   Slot " + String.format("%1$02d", i) + ": "
                        + algorithm.get(i).getClass().getName());
            }
        }
        // invoke each layout processor
        for (ILayoutProcessor processor : algorithm) {
            if (monitor.isCanceled()) {
                return;
            }
            processor.process(graph, monitor.subTask(1));
        }
        monitor.done();
    }
}
