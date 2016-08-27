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
package org.eclipse.elk.alg.layered.p3order;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.intermediate.greedyswitch.GreedySwitchHeuristic;
import org.eclipse.elk.alg.layered.p3order.LayerSweepCrossingMinimizer.CrossMinType;
import org.eclipse.elk.alg.layered.p3order.counting.AbstractInitializer;
import org.eclipse.elk.alg.layered.p3order.counting.AbstractInitializer.IInitializable;
import org.eclipse.elk.alg.layered.p3order.counting.AllCrossingsCounter;
import org.eclipse.elk.alg.layered.properties.GraphProperties;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.layered.properties.LayeredOptions;

import com.google.common.collect.Lists;

/**
 * Collects data needed for cross minimization and port distribution.
 *
 * @author alan
 *
 */
public class GraphData implements IInitializable {
    /** Raw graph data. */
    private LGraph lGraph;

    /** Saved node orders. */
    private LNode[][] currentNodeOrder;
    private SweepCopy currentlyBestNodeAndPortOrder;
    private SweepCopy bestNodeNPortOrder;

    /** Processing type information. */
    private boolean processRecursively;
    private CrossMinType crossMinType;

    /** Hierarchy access. */
    private List<LGraph> childGraphs;
    private boolean hasExternalPorts;
    private boolean hasParent;
    private GraphData parentGraphData;
    private LNode parent;
    private LayerSweepTypeDecider layerSweepTypeDecider;

    /** Pre-initialized auxiliary objects. */
    private ICrossingMinimizationHeuristic crossMinimizer;
    private ISweepPortDistributor portDistributor;
    private AllCrossingsCounter crossingsCounter;
    private AbstractInitializer initializer;

    /**
     * Create object collecting information about a graph.
     * 
     * @param graph
     *            The graph
     * @param cMT
     *            The CrossMinimizer
     */
    public GraphData(final LGraph graph, final CrossMinType cMT, final List<GraphData> graphs) {
        lGraph = graph;
        crossMinType = cMT;
        currentNodeOrder = graph.toNodeArray();

        initHierarchyInformation(graph, graphs);

        initAuxiliaryObjects(graph, cMT);

        processRecursively = layerSweepTypeDecider.useBottomUp();
    }

    private void initHierarchyInformation(final LGraph graph, final List<GraphData> graphs) {
        parent = lGraph.getProperty(InternalProperties.PARENT_LNODE);
        hasParent = parent != null;
        parentGraphData = hasParent ? graphs.get(parent.getGraph().id) : null;
        Set<GraphProperties> graphProperties = graph.getProperty(InternalProperties.GRAPH_PROPERTIES);
        hasExternalPorts = graphProperties.contains(GraphProperties.EXTERNAL_PORTS);
        childGraphs = Lists.newArrayList();
    }

    private void initAuxiliaryObjects(final LGraph graph, final CrossMinType cMT) {
        initializer = new Initializer(currentNodeOrder);
        crossingsCounter = new AllCrossingsCounter(currentNodeOrder);
        portDistributor = lGraph.getProperty(InternalProperties.RANDOM).nextBoolean()
                ? new NodeRelativePortDistributor(currentNodeOrder) : new LayerTotalPortDistributor(currentNodeOrder);
        ForsterConstraintResolver constraintResolver = new ForsterConstraintResolver(currentNodeOrder);
        layerSweepTypeDecider = new LayerSweepTypeDecider(this);

        List<IInitializable> initializables =
                Arrays.asList(crossingsCounter, constraintResolver, layerSweepTypeDecider, portDistributor, this);
        Initializer.init(initializables);

        if (cMT == CrossMinType.BARYCENTER) {
            crossMinimizer = new BarycenterHeuristic(constraintResolver, graph.getProperty(InternalProperties.RANDOM),
                    (AbstractBarycenterPortDistributor) portDistributor);
        } else {
            crossMinimizer =
                    new GreedySwitchHeuristic(lGraph.getProperty(LayeredOptions.CROSSING_MINIMIZATION_GREEDY_SWITCH));
        }
    }

    /**
     * @return the processRecursively
     */
    public boolean dontSweepInto() {
        return processRecursively;
    }

    /**
     * @return the lGraph
     */
    public LGraph lGraph() {
        return lGraph;
    }

    /**
     * @return the currentNodeOrder
     */
    public LNode[][] currentNodeOrder() {
        return currentNodeOrder;
    }

    /**
     * @return the currentlyBestNodeAndPortOrder
     */
    public SweepCopy currentlyBestNodeAndPortOrder() {
        return currentlyBestNodeAndPortOrder;
    }

    /**
     * @param currentlyBestNodeAndPortOrder
     *            the currentlyBestNodeAndPortOrder to set
     */
    public void setCurrentlyBestNodeAndPortOrder(final SweepCopy currentlyBestNodeAndPortOrder) {
        this.currentlyBestNodeAndPortOrder = currentlyBestNodeAndPortOrder;
    }

    /**
     * @return the bestNodeNPortOrder
     */
    public SweepCopy bestNodeNPortOrder() {
        return bestNodeNPortOrder;
    }

    /**
     * @param bestNodeNPortOrder
     *            the bestNodeNPortOrder to set
     */
    public void setBestNodeNPortOrder(final SweepCopy bestNodeNPortOrder) {
        this.bestNodeNPortOrder = bestNodeNPortOrder;
    }

    /**
     * @return the crossCounter
     */
    public AllCrossingsCounter crossCounter() {
        return crossingsCounter;
    }

    /**
     * @return the crossMinimizer
     */
    public ICrossingMinimizationHeuristic crossMinimizer() {
        return crossMinimizer;
    }

    /**
     * @return the portDistributor
     */
    public ISweepPortDistributor portDistributor() {
        return portDistributor;
    }

    /**
     * @return the parent
     */
    public LNode parent() {
        return parent;
    }

    /**
     * @return the hasParent
     */
    public boolean hasParent() {
        return hasParent;
    }

    /**
     * @return child graphs
     */
    public Collection<LGraph> childGraphs() {
        return childGraphs;
    }

    /**
     * @return whether this graph's parent node has external ports.
     */
    public boolean hasExternalPorts() {
        return hasExternalPorts;
    }

    @Override
    public String toString() {
        return lGraph.toString();
    }

    /**
     * @return Copy of node order for currently best sweep.
     */
    public SweepCopy getBestSweep() {
        return crossMinimizer.isDeterministic() ? currentlyBestNodeAndPortOrder() : bestNodeNPortOrder();
    }

    /**
     * @return the parentGraphData
     */
    public GraphData parentGraphData() {
        return parentGraphData;
    }

    /**
     * @return
     */
    public boolean crossMinDeterministic() {
        return crossMinimizer.isDeterministic();
    }

    /**
     * TODO-alan change double cross-min type greedy switch.
     * 
     * @return
     */
    public boolean crossMinAlwaysImproves() {
        return crossMinType == CrossMinType.GREEDY_SWITCH
                && !lGraph.getProperty(LayeredOptions.CROSSING_MINIMIZATION_GREEDY_SWITCH).isOneSided();
    }

    @Override
    public AbstractInitializer initializer() {
        return initializer;
    }
    
    /** Defines what needs to be initialized traversing the graph. */
    private final class Initializer extends AbstractInitializer {
        private Initializer(final LNode[][] graph) {
            super(graph);
        }

        @Override
        public void initAtNodeLevel(final int l, final int n) {
            LGraph nestedGraph = getNodeOrder()[l][n].getProperty(InternalProperties.NESTED_LGRAPH);
            if (nestedGraph != null) {
                childGraphs.add(nestedGraph);
            }
        }
    }

}
