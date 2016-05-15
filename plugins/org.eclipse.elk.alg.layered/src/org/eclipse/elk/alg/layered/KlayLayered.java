/*******************************************************************************
 * Copyright (c) 2010, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.eclipse.elk.alg.layered.components.ComponentsProcessor;
import org.eclipse.elk.alg.layered.compound.CompoundGraphPostprocessor;
import org.eclipse.elk.alg.layered.compound.CompoundGraphPreprocessor;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LInsets;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.properties.ContentAlignment;
import org.eclipse.elk.alg.layered.properties.GraphProperties;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.layered.properties.LayeredOptions;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.core.util.Pair;

/**
 * The main entry point into KLay Layered. KLay Layered is a layout algorithm after the layered
 * layout method proposed by Sugiyama et al. It is structured into five main phases: cycle breaking,
 * layering, crossing minimization, node placement, and edge routing. Before these phases and after
 * the last phase so called intermediate layout processors can be inserted that do some kind of pre
 * or post processing. Implementations of the different main phases specify the intermediate layout
 * processors they require, which are automatically collected and inserted between the main phases.
 * The layout provider itself also specifies some dependencies.
 * 
 * <pre>
 *           Intermediate Layout Processors
 * ---------------------------------------------------
 * |         |         |         |         |         |
 * |   ---   |   ---   |   ---   |   ---   |   ---   |
 * |   | |   |   | |   |   | |   |   | |   |   | |   |
 * |   | |   |   | |   |   | |   |   | |   |   | |   |
 *     | |       | |       | |       | |       | |
 *     | |       | |       | |       | |       | |
 *     ---       ---       ---       ---       ---
 *   Phase 1   Phase 2   Phase 3   Phase 4   Phase 5
 * </pre>
 * 
 * <p>To use KLay Layered to layout a given graph, there are three possibilities depending on the kind
 * of graph that is to be laid out:</p>
 * <ol>
 *   <li>{@link #doLayout(LGraph, IElkProgressMonitor)} computes a layout for the given graph, without
 *     any subgraphs it might have.</li>
 *   <li>{@link #doCompoundLayout(LGraph, IElkProgressMonitor)} computes a layout for the given graph
 *     and for its subgraphs, if any. (Subgraphs are attached to nodes through the
 *     {@link InternalProperties#NESTED_LGRAPH} property.)</li>
 * <li>If you have a {@code KGraph} instead of an {@code LGraph}, you might want to use
 *     {@link LayeredLayoutProvider#doLayout(org.eclipse.elk.graph.KNode, IElkProgressMonitor)}
 *     instead.</li>
 * </ol>
 * <p>In addition to regular layout runs, this class provides methods for automatic unit testing based
 * around the concept of <em>test runs</em>. A test run is executed as follows:</p>
 * <ol>
 *   <li>Call {@link #prepareLayoutTest(LGraph)} to start a new run. The given graph might be split
 *       into its connected components, which are put into the returned state object.</li>
 *   <li>Call one of the actual test methods. {@link #runLayoutTestStep(TestExecutionState)} runs the
 *       next step of the algorithm. {@link #runLayoutTestUntil(Class, TestExecutionState)} runs the
 *       algorithm until a given layout processor has finished executing (its sibling,
 *       {@link #runLayoutTestUntil(Class, boolean, TestExecutionState)}, can also stop just before a
 *       given layout processor starts executing). All of these methods resume execution from where the
 * algorithm has stopped previously.</li>
 * </ol>
 * 
 * @see ILayoutPhase
 * @see ILayoutProcessor
 * @see GraphConfigurator
 * 
 * @author msp
 * @author cds
 * @kieler.design 2012-08-10 chsch grh
 * @kieler.rating yellow 2014-11-09 review KI-56 by chsch, als
 */
public final class KlayLayered {

    ////////////////////////////////////////////////////////////////////////////////
    // Variables

    /** the algorithm's current configuration. */
    private GraphConfigurator graphConfigurator = new GraphConfigurator();
    /** connected components processor. */
    private ComponentsProcessor componentsProcessor = new ComponentsProcessor();
    /** compound graph preprocessor. */
    private CompoundGraphPreprocessor compoundGraphPreprocessor = new CompoundGraphPreprocessor();
    /** compound graph postprocessor. */
    private CompoundGraphPostprocessor compoundGraphPostprocessor = new CompoundGraphPostprocessor();
    

    ////////////////////////////////////////////////////////////////////////////////
    // Regular Layout

    /**
     * Does a layout on the given graph. If the graph contains compound nodes (see class documentation),
     * the nested graphs are ignored.
     * 
     * @param lgraph the graph to layout
     * @param monitor a progress monitor to show progress information in, or {@code null}
     * @see #doCompoundLayout(LGraph, IElkProgressMonitor)
     */
    public void doLayout(final LGraph lgraph, final IElkProgressMonitor monitor) {
        IElkProgressMonitor theMonitor = monitor;
        if (theMonitor == null) {
            theMonitor = new BasicProgressMonitor(0);
        }
        theMonitor.begin("Layered layout", 1);

        // Update the modules depending on user options
        graphConfigurator.prepareGraphForLayout(lgraph);

        // Split the input graph into components and perform layout on them
        List<LGraph> components = componentsProcessor.split(lgraph);
        if (components.size() == 1) {
            // Execute layout on the sole component using the top-level progress monitor
            layout(components.get(0), theMonitor);
        } else {
            // Execute layout on each component using a progress monitor subtask
            float compWork = 1.0f / components.size();
            for (LGraph comp : components) {
                if (monitor.isCanceled()) {
                    return;
                }
                layout(comp, theMonitor.subTask(compWork));
            }
        }
        componentsProcessor.combine(components, lgraph);
        
        // Resize the resulting graph, according to minimal size constraints and such
        resizeGraph(lgraph);

        theMonitor.done();
    }
    

    ////////////////////////////////////////////////////////////////////////////////
    // Compound Graph Layout

    /**
     * Does a layout on the given compound graph. Connected components processing is currently not
     * supported.
     * 
     * @param lgraph the graph to layout
     * @param monitor a progress monitor to show progress information in, or {@code null}
     */
    public void doCompoundLayout(final LGraph lgraph, final IElkProgressMonitor monitor) {
        IElkProgressMonitor theMonitor = monitor;
        if (theMonitor == null) {
            theMonitor = new BasicProgressMonitor(0);
        }
        theMonitor.begin("Layered layout", 2); // SUPPRESS CHECKSTYLE MagicNumber

        // Preprocess the compound graph by splitting cross-hierarchy edges
        compoundGraphPreprocessor.process(lgraph, theMonitor.subTask(1));

        hierarchicalLayout(lgraph, theMonitor.subTask(1));

        // Postprocess the compound graph by combining split cross-hierarchy edges
        compoundGraphPostprocessor.process(lgraph, theMonitor.subTask(1));

        theMonitor.done();
    }
    
    /**
     * Processors can be marked as operating on the full hierarchy.
     * 
     * All graphs are collected using a breadth first search and this list reversed, so that for each graph, all
     * following graphs are on the same hierarchy level or higher, i.e. closer to the parent graph. Each graph then has
     * a unique algorithm, which is comprised of a sequence of processors. The processors can vary depending on the
     * characteristics of each graph. The list of graphs and their algorithms is then traversed. If a processor is not
     * hierarchical it is simply executed. It it is hierarchical and this graph is not the root graph, this processor is
     * skipped and the algorithm is paused until the processor has been executed on the root graph. Then the algorithm
     * is continued, starting with the level lowest in the hierarchy, i.e. furthest away from the root graph.
     */
    private void hierarchicalLayout(final LGraph lgraph, final IElkProgressMonitor monitor) {
        monitor.begin("Recursive Hierarchical layout", 2); // SUPPRESS CHECKSTYLE MagicNumber

        // Perform a reversed breadth first search: The graphs in the lowest hierarchy come first.
        List<LGraph> graphs = collectAllGraphsBottomUp(lgraph);

        // Get list of processors for each graph, since they can be different.
        // Iterators are used, so that processing of a graph can be paused and continued easily.
        List<Pair<LGraph, Iterator<ILayoutProcessor>>> graphsAndAlgorithms =
                prepareForLayout(graphs);

        // When the root graph has finished layout, the layout is complete.
        Iterator<ILayoutProcessor> rootProcessors = getProcessorsForRootGraph(graphsAndAlgorithms);
        while (rootProcessors.hasNext()) {
            // Layout from bottom up
            for (Pair<LGraph, Iterator<ILayoutProcessor>> graphAndAlgorithm : graphsAndAlgorithms) {
                Iterator<ILayoutProcessor> processors = graphAndAlgorithm.getSecond();
                LGraph graph = graphAndAlgorithm.getFirst();

                while (processors.hasNext()) {
                    ILayoutProcessor processor = processors.next();
                    if (!processor.operatesOnFullHierarchy()) {
                        processor.process(graph, monitor.subTask(1));
                    } else if (isRoot(graph)) {
                        // If processor operates on the full hierarchy, it must be executed on the
                        // root.
                        processor.process(graph, monitor.subTask(1));
                        // Continue operation with the graph at the bottom of the hierarchy
                        break;
                    } else { // operates on full hierarchy and is not root graph
                        // skip this processor and pause execution until root graph has processed.
                        break;
                    }
                }
            }
        }

        monitor.done();
    }
    
    /**
     * Implements a breadth first search in compound graphs with reversed order. This way the
     * innermost graphs are first in the list, followed by one level further up, etc.
     * 
     * @param root
     *            the root graph
     * @return Graphs in breadth first search in compound graphs in reverse order.
     */
    private List<LGraph> collectAllGraphsBottomUp(final LGraph root) {
        LinkedList<LGraph> collectedGraphs = new LinkedList<>();
        LinkedList<LGraph> continueSearchingTheseGraphs = new LinkedList<>();
        collectedGraphs.push(root);
        continueSearchingTheseGraphs.push(root);

        while (!continueSearchingTheseGraphs.isEmpty()) {
            LGraph nextGraph = continueSearchingTheseGraphs.pop();
            for (LNode node : nextGraph.getLayerlessNodes()) {
                if (hasNestedGraph(node)) {
                    LGraph nestedGraph = nestedGraphOf(node);
                    collectedGraphs.push(nestedGraph);
                    continueSearchingTheseGraphs.push(nestedGraph);
                }
            }
        }
        return collectedGraphs;
    }

    private Iterator<ILayoutProcessor> getProcessorsForRootGraph(
            final List<Pair<LGraph, Iterator<ILayoutProcessor>>> graphsAndAlgorithms) {
        return graphsAndAlgorithms.get(graphsAndAlgorithms.size() - 1).getSecond();
    }

    private List<Pair<LGraph, Iterator<ILayoutProcessor>>> prepareForLayout(
            final List<LGraph> graphs) {
        List<Pair<LGraph, Iterator<ILayoutProcessor>>> graphAlgorithms = new LinkedList<>();
        for (LGraph g : graphs) {
            graphConfigurator.prepareGraphForLayout(g);
            List<ILayoutProcessor> processors = g.getProperty(InternalProperties.PROCESSORS);
            Iterator<ILayoutProcessor> algorithm = processors.iterator();
            graphAlgorithms.add(Pair.of(g, algorithm));
        }
        return graphAlgorithms;
    }

    private boolean isRoot(final LGraph graph) {
        return parentNodeOf(graph) == null;
    }

    private LNode parentNodeOf(final LGraph graph) {
        return graph.getProperty(InternalProperties.PARENT_LNODE);
    }

    private LGraph nestedGraphOf(final LNode node) {
        return node.getProperty(InternalProperties.NESTED_LGRAPH);
    }

    private boolean hasNestedGraph(final LNode node) {
        return nestedGraphOf(node) != null;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Layout Testing
    
    /**
     * The state of a test execution is held in an instance of this class.
     */
    public static class TestExecutionState {
        /** list of graphs that are currently being laid out. */
        private List<LGraph> graphs;
        /** index of the processor that is to be executed next during a layout test. */
        private int step;
        
        /**
         * Return the list of graphs that are currently being laid out.
         * 
         * @return the graphs under test
         */
        public List<LGraph> getGraphs() {
            return graphs;
        }
        
        /**
         * Return the index of the processor that is to be executed next during a layout test.
         * 
         * @return the index of the next step
         */
        public int getStep() {
            return step;
        }
    }

    /**
     * Prepares a test run of the layout algorithm. After this method has run, call
     * {@link #layoutTestStep()} as often as there are layout processors.
     * 
     * @param lgraph the input graph to initialize the test run with.
     * @return the test execution state
     */
    public TestExecutionState prepareLayoutTest(final LGraph lgraph) {
        TestExecutionState state = new TestExecutionState();
        
        // update the modules depending on user options
        graphConfigurator.prepareGraphForLayout(lgraph);

        // split the input graph into components
        if (componentsProcessor == null) {
            componentsProcessor = new ComponentsProcessor();
        }
        state.graphs = componentsProcessor.split(lgraph);
        
        return state;
    }

    /**
     * Checks if the current test run still has processors to be executed for the algorithm to finish.
     * 
     * @param state the current test execution state
     * @return {@code true} if the current test run has not finished yet. If there is no current
     *         test run, the result is undefined.
     */
    public boolean isLayoutTestFinished(final TestExecutionState state) {
        LGraph graph = state.graphs.get(0);
        List<ILayoutProcessor> algorithm = graph.getProperty(InternalProperties.PROCESSORS);
        return algorithm != null && state.step >= algorithm.size();
    }

    /**
     * Runs the algorithm on the current test graphs up to the point where the given phase or
     * processor has finished executing. If parts of the algorithm were already executed using this
     * or other layout test methods, execution is resumed from there. If the given phase or
     * processor is not among those processors that have not yet executed, an exception is thrown.
     * Also, if there is no current layout test run, an exception is thrown.
     * 
     * @param phase the phase or processor to stop after
     * @param inclusive {@code true} if the specified phase should be executed as well
     * @param state the current test execution state
     * @throws IllegalArgumentException
     *             if the given layout processor is not part of the processors that are still to be
     *             executed.
     */
    public void runLayoutTestUntil(final Class<? extends ILayoutProcessor> phase,
            final boolean inclusive, final TestExecutionState state) {
        
        List<ILayoutProcessor> algorithm = state.graphs.get(0).getProperty(
                InternalProperties.PROCESSORS);

        // check if the given phase exists in our current algorithm configuration
        boolean phaseExists = false;
        ListIterator<ILayoutProcessor> algorithmIterator = algorithm.listIterator(state.step);
        int phaseIndex = state.step;
        
        while (algorithmIterator.hasNext() && !phaseExists) {
            if (algorithmIterator.next().getClass().equals(phase)) {
                phaseExists = true;
                
                if (inclusive) {
                    phaseIndex++;
                }
            } else {
                phaseIndex++;
            }
        }

        if (!phaseExists) {
            // FIXME actually, we want to know when a processor is not
            //  part of the algorithm's configuration because this might be
            //  wrong behavior.
            // However, in the current test framework there is no way
            //  to differentiate between 'it's ok' and 'it's not'.
            // throw new IllegalArgumentException(
            // "Given processor not part of the remaining algorithm.");
            System.err
                    .println("Given processor " + phase + " not part of the remaining algorithm.");
        }

        // perform the layout up to and including that phase
        algorithmIterator = algorithm.listIterator(state.step);
        for (; state.step < phaseIndex; state.step++) {
            layoutTest(state.graphs, algorithmIterator.next());
        }
    }

    /**
     * Performs the {@link #runLayoutTestUntil(Class, boolean)} methods with {@code inclusive} set
     * to {@code true}.
     * 
     * @param phase the phase or processor to stop after
     * @param state the current test execution state
     * @see KlayLayered#runLayoutTestUntil(Class, boolean)
     */
    public void runLayoutTestUntil(final Class<? extends ILayoutProcessor> phase,
            final TestExecutionState state) {
        
        runLayoutTestUntil(phase, true, state);
    }

    /**
     * Runs the next step of the current layout test run. Throws exceptions if no layout test run is
     * currently active or if the current run has finished.
     * 
     * @param state the current test execution state
     * @throws IllegalStateException if the given state has finished executing
     */
    public void runLayoutTestStep(final TestExecutionState state) {
        if (isLayoutTestFinished(state)) {
            throw new IllegalStateException("Current layout test run has finished.");
        }

        // perform the next layout step
        List<ILayoutProcessor> algorithm = state.graphs.get(0).getProperty(
                InternalProperties.PROCESSORS);
        layoutTest(state.graphs, algorithm.get(state.step));
        state.step++;
    }

    /**
     * Returns the current list of layout processors that make up the algorithm. This list is only
     * valid and meaningful while a layout test is being run.
     * 
     * @param state the current test execution state
     * @return the algorithm's current configuration.
     */
    public List<ILayoutProcessor> getLayoutTestConfiguration(final TestExecutionState state) {
        return state.graphs.get(0).getProperty(InternalProperties.PROCESSORS);
    }
    

    ////////////////////////////////////////////////////////////////////////////////
    // Actual Layout

    /**
     * Perform the five phases of the layered layouter.
     * 
     * @param lgraph the graph that is to be laid out
     * @param monitor a progress monitor
     */
    private void layout(final LGraph lgraph, final IElkProgressMonitor monitor) {
        boolean monitorStarted = monitor.isRunning();
        if (!monitorStarted) {
            monitor.begin("Component Layout", 1);
        }
        List<ILayoutProcessor> algorithm = lgraph.getProperty(InternalProperties.PROCESSORS);
        float monitorProgress = 1.0f / algorithm.size();

        if (lgraph.getProperty(LayeredOptions.DEBUG_MODE)) {
            // Debug Mode!
            // Print the algorithm configuration and output the whole graph to a file
            // before each slot execution

            System.out.println("KLay Layered uses the following " + algorithm.size() + " modules:");
            int i = 0;
            for (ILayoutProcessor processor : algorithm) {
                System.out.println("   Slot " + String.format("%1$02d", i++) + ": "
                        + processor.getClass().getName());
            }

            // Invoke each layout processor
            int slotIndex = 0;
            for (ILayoutProcessor processor : algorithm) {
                if (monitor.isCanceled()) {
                    return;
                }
                // Graph debug output
                DebugUtil.writeDebugGraph(lgraph, slotIndex++, processor.getClass().getSimpleName());

                processor.process(lgraph, monitor.subTask(monitorProgress));
            }

            // Graph debug output
            DebugUtil.writeDebugGraph(lgraph, slotIndex, "finished");
        } else {
            // Invoke each layout processor
            for (ILayoutProcessor processor : algorithm) {
                if (monitor.isCanceled()) {
                    return;
                }
                processor.process(lgraph, monitor.subTask(monitorProgress));
            }
        }
        
        // Move all nodes away from the layers (we need to remove nodes from their current layer in a
        // second loop to avoid ConcurrentModificationExceptions)
        for (Layer layer : lgraph) {
            lgraph.getLayerlessNodes().addAll(layer.getNodes());
            layer.getNodes().clear();
        }
        for (LNode node : lgraph.getLayerlessNodes()) {
            node.setLayer(null);
        }
        lgraph.getLayers().clear();

        if (!monitorStarted) {
            monitor.done();
        }
    }

    /**
     * Executes the given layout processor on the given list of graphs.
     * 
     * @param lgraphs the list of graphs to be laid out.
     * @param monitor a progress monitor.
     * @param processor processor to execute.
     */
    private void layoutTest(final List<LGraph> lgraphs, final ILayoutProcessor processor) {
        // invoke the layout processor on each of the given graphs
        for (LGraph graph : lgraphs) {
            processor.process(graph, new BasicProgressMonitor());
        }
    }
    

    ////////////////////////////////////////////////////////////////////////////////
    // Graph Postprocessing (Size and External Ports)
    
    /**
     * Sets the size of the given graph such that size constraints are adhered to.
     * Furthermore, the border spacing is added to the graph size and the graph offset.
     * Afterwards, the border spacing property is reset to 0.
     * 
     * <p>Major parts of this method are adapted from
     * {@link ElkUtil#resizeNode(org.eclipse.elk.graph.KNode, float, float, boolean)}.</p>
     * 
     * <p>Note: This method doesn't care about labels of compound nodes since those labels are not
     * attached to the graph.</p>
     * 
     * @param lgraph the graph to resize.
     */
    private void resizeGraph(final LGraph lgraph) {
        Set<SizeConstraint> sizeConstraint = lgraph.getProperty(LayeredOptions.NODE_SIZE_CONSTRAINTS);
        Set<SizeOptions> sizeOptions = lgraph.getProperty(LayeredOptions.NODE_SIZE_OPTIONS);
        float borderSpacing = lgraph.getProperty(LayeredOptions.SPACING_BORDER);
        
        // add the border spacing to the graph size and graph offset
        lgraph.getOffset().x += borderSpacing;
        lgraph.getOffset().y += borderSpacing;
        lgraph.getSize().x += 2 * borderSpacing;
        lgraph.getSize().y += 2 * borderSpacing;
        
        // the graph size now contains the border spacing, so clear it in order to keep
        // graph.getActualSize() working properly
        lgraph.setProperty(LayeredOptions.SPACING_BORDER, 0f);
        
        KVector calculatedSize = lgraph.getActualSize();
        KVector adjustedSize = new KVector(calculatedSize);
        
        // calculate the new size
        if (sizeConstraint.contains(SizeConstraint.MINIMUM_SIZE)) {
            KVector minSize = lgraph.getProperty(LayeredOptions.NODE_SIZE_MINIMUM);
            float minWidth, minHeight;
            if (minSize == null) {
                minWidth = lgraph.getProperty(LayeredOptions.NODE_SIZE_MIN_WIDTH);
                minHeight = lgraph.getProperty(LayeredOptions.NODE_SIZE_MIN_HEIGHT);
            } else {
                minWidth = (float) minSize.x;
                minHeight = (float) minSize.y;
            }
            
            // if minimum width or height are not set, maybe default to default values
            if (sizeOptions.contains(SizeOptions.DEFAULT_MINIMUM_SIZE)) {
                if (minWidth <= 0) {
                    minWidth = ElkUtil.DEFAULT_MIN_WIDTH;
                }
                
                if (minHeight <= 0) {
                    minHeight = ElkUtil.DEFAULT_MIN_HEIGHT;
                }
            }
            
            // apply new size including border spacing
            adjustedSize.x = Math.max(calculatedSize.x, minWidth);
            adjustedSize.y = Math.max(calculatedSize.y, minHeight);
        }
        
        resizeGraphNoReallyIMeanIt(lgraph, calculatedSize, adjustedSize);
    }


    /**
     * Applies a new effective size to a graph that previously had an old size calculated by the
     * layout algorithm. This method takes care of adjusting content alignments as well as external
     * ports that would be misplaced if the new size is larger than the old one.
     * 
     * @param lgraph
     *            the graph to apply the size to.
     * @param oldSize
     *            old size as calculated by the layout algorithm.
     * @param newSize
     *            new size that may be larger than the old one.
     */
    private void resizeGraphNoReallyIMeanIt(final LGraph lgraph, final KVector oldSize,
            final KVector newSize) {
        
        // obey to specified alignment constraints
        Set<ContentAlignment> contentAlignment =
                lgraph.getProperty(LayeredOptions.CONTENT_ALIGNMENT);
        
        // horizontal alignment
        if (newSize.x > oldSize.x) {
            if (contentAlignment.contains(ContentAlignment.H_CENTER)) {
                lgraph.getOffset().x += (newSize.x - oldSize.x) / 2f;
            } else if (contentAlignment.contains(ContentAlignment.H_RIGHT)) {
                lgraph.getOffset().x += newSize.x - oldSize.x;
            }
        }
        
        // vertical alignment
        if (newSize.y > oldSize.y) {
            if (contentAlignment.contains(ContentAlignment.V_CENTER)) {
                lgraph.getOffset().y += (newSize.y - oldSize.y) / 2f;
            } else if (contentAlignment.contains(ContentAlignment.V_BOTTOM)) {
                lgraph.getOffset().y += newSize.y - oldSize.y;
            }
        }
        
        // correct the position of eastern and southern hierarchical ports, if necessary
        if (lgraph.getProperty(InternalProperties.GRAPH_PROPERTIES).contains(
                GraphProperties.EXTERNAL_PORTS)
                && (newSize.x > oldSize.x || newSize.y > oldSize.y)) {
            
            // iterate over the graph's nodes, looking for eastern / southern external ports
            // (at this point, the graph's nodes are not divided into layers anymore)
            for (LNode node : lgraph.getLayerlessNodes()) {
                // we're only looking for external port dummies
                if (node.getType() == NodeType.EXTERNAL_PORT) {
                    // check which side the external port is on
                    PortSide extPortSide = node.getProperty(InternalProperties.EXT_PORT_SIDE);
                    if (extPortSide == PortSide.EAST) {
                        node.getPosition().x += newSize.x - oldSize.x;
                    } else if (extPortSide == PortSide.SOUTH) {
                        node.getPosition().y += newSize.y - oldSize.y;
                    }
                }
            }
        }
        
        // Actually apply the new size
        LInsets insets = lgraph.getInsets();
        lgraph.getSize().x = newSize.x - insets.left - insets.right;
        lgraph.getSize().y = newSize.y - insets.top - insets.bottom;
    }
    
}
