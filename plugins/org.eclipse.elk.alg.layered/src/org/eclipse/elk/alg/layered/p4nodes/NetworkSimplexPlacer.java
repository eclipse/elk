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
package org.eclipse.elk.alg.layered.p4nodes;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.elk.alg.layered.ILayoutPhase;
import org.eclipse.elk.alg.layered.IntermediateProcessingConfiguration;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.intermediate.IntermediateProcessorStrategy;
import org.eclipse.elk.alg.layered.networksimplex.NEdge;
import org.eclipse.elk.alg.layered.networksimplex.NGraph;
import org.eclipse.elk.alg.layered.networksimplex.NNode;
import org.eclipse.elk.alg.layered.networksimplex.NetworkSimplex;
import org.eclipse.elk.alg.layered.options.GraphProperties;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.Spacings;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Implements the node placement strategy as described by Gansner et al. in the following paper. It
 * is based on the idea to convert the problem into an auxiliary graph which is layered using the
 * network simplex algorithm.
 * <ul>
 *   <li>Emden R. Gansner, Eleftherios Koutsofios, Stephen C. North, Kiem-Phong Vo, A technique for
 *       drawing directed graphs. <i>Software Engineering</i> 19(3), pp. 214-230, 1993.</li>
 * </ul>
 * 
 * <h2>Restrictions</h2> 
 * In this approach node positions are represented by layers, and layers are integral. 
 * This means in particular that we cannot support rational positions, neither for nodes nor for ports.
 * The same is true for margin and spacing values.
 * In case such values are present in the graph we resolve them as follows:
 * <ul>
 *   <li>Margin: The top margin is set to Math.ceil(margin.top).</li>
 *   <li>Nodes: The position, height, margin, and spacing of a node are summed and ceiled.</li> 
 *   <li>Ports: A port's position is altered to the closest integer position.</li>
 * </ul>
 * 
 * <h2>Favor Straight Edges </h2>
 * The way the auxiliary graph is built allows for symmetric optimal solutions 
 * that may result in edge "stair-cases".
 *  
 * Consider the following example. n1 and n4 cannot be moved closer to each other 
 * because of spacing restrictions. If the edge (n4,n3) is straight, several (vertical) positions 
 * of n2 result in minimal edge length.  
 * <pre>
 *   __
 *  |  |
 *  |n1|--.   __
 *  |__|  ^--|n2|--.   ____
 *                 ^--|    | 
 *   __               | n3 |
 *  |n4|--------------|____|
 * </pre>
 * 
 * With the {@link LayeredOptions#NODE_PLACEMENT_FAVOR_EDGE_STRAIGHTNESS} option set,
 * we try to improve this by additionally reducing the number of bends, like this:
 * <pre>
 *   __
 *  |  |      __
 *  |n1|-----|n2|--.
 *  |__|           |   ____
 *                 ^--|    | 
 *   __               | n3 |
 *  |n4|--------------|____|
 * </pre>
 * 
 * Additionally note that north/south ports are realized using dummy nodes within the same layer as the port's parent 
 * node, and the dummy is <b>not</b> connected to the parent. Currently this is neglected when identifying paths are 
 * to introduce the straightness dummies. In other words, vertical segments of north/south edges may be elongated 
 * for straighter horizontal edges.
 */
public class NetworkSimplexPlacer implements ILayoutPhase {
    
    /** Additional processor dependencies for graphs with hierarchical ports. */
    private static final IntermediateProcessingConfiguration HIERARCHY_PROCESSING_ADDITIONS =
        IntermediateProcessingConfiguration.createEmpty()
            .addBeforePhase5(IntermediateProcessorStrategy.HIERARCHICAL_PORT_POSITION_PROCESSOR);

    /** Internal property to associate dummy {@link NNode}s with {@link LEdge}s. */
    private static final IProperty<NNode> EDGE_NNODE = new Property<>("nodePlace.ns.edgeNNode");
    /** Smaller weight than default (1), since horizontal edges are more important. */
    private static final double SMALL_EDGE_WEIGHT = 0.1f;
    
    /** The lGraph to be handled. */
    private LGraph lGraph;
    /**
     * Number of nodes of the {@link #lGraph}. The field is uninitialized until {@link #buildAuxiliaryGraph()} has been
     * called.
     */
    private int nodeCount;
    /**
     * Internal book keeping array for the case that {@link LayeredOptions#NODE_PLACEMENT_FAVOR_EDGE_STRAIGHTNESS} is
     * used. Indexed by {@link LNode#id}.
     */
    private int[] nodeState;
    
    /**
     * {@inheritDoc}
     */
    public IntermediateProcessingConfiguration getIntermediateProcessingConfiguration(
            final LGraph graph) {
        
        if (graph.getProperty(InternalProperties.GRAPH_PROPERTIES)
                .contains(GraphProperties.EXTERNAL_PORTS)) {
            return HIERARCHY_PROCESSING_ADDITIONS;
        } else {
            return null;
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void process(final LGraph layeredGraph, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Network simplex node placement", 1);
        
        this.lGraph = layeredGraph;
        
        // -------------------------------
        // #1 transform nodes & edges
        // -------------------------------
        NGraph nGraph = buildAuxiliaryGraph();
        
        // add something to straighten edges, if desired
        if (layeredGraph.getProperty(LayeredOptions.NODE_PLACEMENT_FAVOR_STRAIGHT_EDGES)) {
            insertStraighteningDummyNodes(nGraph);
        }

        // --------------------------------
        // #2 execute the network simplex
        // --------------------------------
        // compared to {@link NetworkSimplexLayerer} a significantly larger iteration limit 
        // is selected here because the node placement uses an auxiliary graph with
        // larger node and edge count
        int iterLimit = layeredGraph.getProperty(LayeredOptions.THOROUGHNESS) * nGraph.nodes.size();
        
        NetworkSimplex.forGraph(nGraph)
            .withIterationLimit(iterLimit)
            .withBalancing(false)
            .execute(progressMonitor.subTask(1));

        // --------------------------------
        // #3 apply positions
        // --------------------------------
        for (NNode n : nGraph.nodes) {
            if (n.origin != null) {
                LNode lNode = (LNode) n.origin;
                lNode.getPosition().y = n.layer + lNode.getMargin().top;
            }
        }
        
        progressMonitor.done();
    }
    
    /**
     * Builds the auxiliary graph on which the network simplex algorithm is run.
     */
    private NGraph buildAuxiliaryGraph() { // SUPPRESS CHECKSTYLE MethodLength
        
        final Spacings spacings = lGraph.getProperty(InternalProperties.SPACINGS);
        final Map<LNode, NNode> nodeMap = Maps.newHashMap();
        NGraph graph = new NGraph();
        int nodeCnt = 0;
        
        // transform all nodes and add separating and order preserving edges
        for (Layer l : lGraph) {
            
            LNode prevL = null;
            NNode prev = null;
            for (LNode lNode : l) {
                nodeCnt++;
                
                NNode nNode = NNode.of().create(graph);
                nNode.origin = lNode;
                nodeMap.put(lNode, nNode);
                
                // integral margin
                lNode.getMargin().top = Math.ceil(lNode.getMargin().top);
                
                if (prev != null) {
                    
                    NEdge nEdge = new NEdge();
                    nEdge.weight = isConnectedByInlayerEdge(prevL, lNode) ? SMALL_EDGE_WEIGHT : 0;
                    nEdge.delta =
                            // ceil the value to assert integrality and minimum spacing 
                            (int) Math.floor(
                                    prevL.getMargin().top
                                    + prevL.getSize().y 
                                    + prevL.getMargin().bottom 
                                    + spacings.getVerticalSpacing((LNode) prev.origin, lNode)
                            );
                    
                    nEdge.source = prev;
                    nEdge.target = nNode;
                    prev.getOutgoingEdges().add(nEdge);
                    nNode.getIncomingEdges().add(nEdge);
                }
                
                prevL = lNode;
                prev = nNode;
            }
        }
        
        this.nodeCount = nodeCnt;
        
        // "integerify" port positions
        for (Layer l : lGraph) {
            for (LNode lNode : l) {

                for (LPort p : lNode.getPorts()) {
                    double y = p.getPosition().y + p.getAnchor().y;
                    if (y != Math.floor(y)) {
                        double offset = y - Math.round(y);
                        p.getPosition().y -= offset;
                    }
                }
                
            }
        }
        
        // convert the edges
        for (Layer l : lGraph.getLayers()) {
            for (LNode lNode : l.getNodes()) {
                
                for (LEdge lEdge : lNode.getOutgoingEdges()) {
                    
                    // no self loops and in-layer edges
                    if (!isHandledEdge(lEdge)) {
                        continue;
                    }
              
                    // port offsets (top margin and port position should be integers by now)
                    double sourceY =
                            lEdge.getSource().getNode().getMargin().top
                                    + lEdge.getSource().getPosition().y
                                    + lEdge.getSource().getAnchor().y;
                    double targetY =
                            lEdge.getTarget().getNode().getMargin().top
                                   + lEdge.getTarget().getPosition().y
                                   + lEdge.getTarget().getAnchor().y;

                    double delta = targetY - sourceY;
                    int portOffset = (int) delta;
                    
                    // check that the offset is integer
                    assert delta == Math.floor(delta);

                    // a dummy node
                    NNode dummy = NNode.of().create(graph);
                    lEdge.setProperty(EDGE_NNODE, dummy);
                    
                    // an edge to the source 
                    NEdge leftEdge = new NEdge();
                    leftEdge.origin = lEdge;
                    leftEdge.weight = getEdgeWeight(lEdge);
                    
                    leftEdge.delta = portOffset > 0 ? portOffset : 0;
                    
                    leftEdge.source = dummy;
                    leftEdge.target = nodeMap.get(lEdge.getSource().getNode());
                    leftEdge.source.getOutgoingEdges().add(leftEdge);
                    leftEdge.target.getIncomingEdges().add(leftEdge);
                    
                    // an edge to the target
                    NEdge rightEdge = new NEdge();
                    rightEdge.origin = lEdge;
                    rightEdge.weight = getEdgeWeight(lEdge);
                    
                    rightEdge.delta = portOffset < 0 ? -portOffset : 0;

                    rightEdge.source = dummy;
                    rightEdge.target = nodeMap.get(lEdge.getTarget().getNode());
                    rightEdge.source.getOutgoingEdges().add(rightEdge);
                    rightEdge.target.getIncomingEdges().add(rightEdge);
                }
            }
        }
        
        
        // insert NEdges to keep north and south port edges short
        for (Layer l : lGraph.getLayers()) {
            for (LNode n : l.getNodes()) {
                for (LPort sp : n.getPortSideView(PortSide.SOUTH)) {
                    LNode other = sp.getProperty(InternalProperties.PORT_DUMMY);
                    // if no edge was attached to the port, no dummy was created ...
                    if (other != null) {
                        NEdge nEdge = new NEdge();
                        nEdge.delta = 0; // doesn't matter
                        nEdge.weight = SMALL_EDGE_WEIGHT; 
                        
                        nEdge.source = nodeMap.get(n);
                        nEdge.target = nodeMap.get(other);
                        nEdge.source.getOutgoingEdges().add(nEdge);
                        nEdge.target.getIncomingEdges().add(nEdge);
                    }
                }
                
                for (LPort sp : n.getPortSideView(PortSide.NORTH)) {
                    LNode other = sp.getProperty(InternalProperties.PORT_DUMMY);
                    // if no edge was attached to the port, no dummy was created ...
                    if (other != null) {
                        NEdge nEdge = new NEdge();
                        nEdge.delta = 0; // doesn't matter
                        nEdge.weight = SMALL_EDGE_WEIGHT;
                        
                        nEdge.source = nodeMap.get(other);
                        nEdge.target = nodeMap.get(n);
                        nEdge.source.getOutgoingEdges().add(nEdge);
                        nEdge.target.getIncomingEdges().add(nEdge);
                    }
                }
            }
        }
        
        return graph;
    }
    
    /**
     * @return for the passed edge, the individual edge weight as specified by Gansner et al. The
     *         idea is to use a higher weight for long edge dummies such that long edges are drawn
     *         straight with higher priority. Additionally, we consider the
     *         {@link org.eclipse.elk.alg.layered.options.PRIORITY PRIORITY} layout option.
     */
    private int getEdgeWeight(final LEdge edge) {
        
        int priority = Math.max(1, edge.getProperty(LayeredOptions.PRIORITY_STRAIGHTNESS));
        
        int edgeTypeWeight;
        if (edge.getSource().getNode().getType() == NodeType.NORMAL
                && edge.getTarget().getNode().getType() == NodeType.NORMAL) {
            edgeTypeWeight = 1;
        } else if (edge.getSource().getNode().getType() == NodeType.NORMAL
                || edge.getTarget().getNode().getType() == NodeType.NORMAL) {
            edgeTypeWeight = 2;
        } else {
            edgeTypeWeight = 8; // SUPPRESS CHECKSTYLE MagicNumber
        }
        
        return priority * edgeTypeWeight;
    }
    
    /**
     * @return true if the edge is neither a self loop, nor an in-layer edge.
     */
    private boolean isHandledEdge(final LEdge edge) {
        return !edge.isSelfLoop() 
            && !(edge.getTarget().getNode().getLayer() == edge.getSource().getNode().getLayer());
    }
    
    /**
     * @return true if the two nodes are connected by an in-layer edge (with one of the nodes being a dummy node)
     */
    private boolean isConnectedByInlayerEdge(final LNode n1, final LNode n2) {
        // the dummy check tries to avoid iterating many edges, 
        // since the most common case is to return false
        if (n1.getType() == NodeType.NORMAL && n2.getType() == NodeType.NORMAL) {
            return false;
        }
        for (LEdge e : n1.getConnectedEdges()) {
            if (e.isInLayerEdge() && e.getOther(n1) == n2) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Inserts additional dummy nodes to handle the edge straightness case discussed in the class's javadoc.
     * 
     * Note that paths that end in leaf nodes are not considered here since 
     * the built auxiliary graph should result in those paths being straight anyway.
     */
    private void insertStraighteningDummyNodes(final NGraph nGraph) {
        // assign indexes
        int index = 0;
        nodeState = new int[nodeCount];
        for (Layer l : lGraph.getLayers()) {
            for (LNode n : l.getNodes()) {
                n.id = index++;
                // identify junction nodes
                nodeState[n.id] = getNodeState(n);
            }
        }

        // identify directed paths in the graph
        final List<List<LEdge>> paths = identifyPaths();
        
        for (List<LEdge> path : paths) {
            
            Iterator<LEdge> pathIt = path.iterator();
            if (!pathIt.hasNext()) {
                continue;
            }
            
            LEdge last = pathIt.next();
            
            while (pathIt.hasNext()) {
                LEdge cur = pathIt.next();
                
                NNode one = cur.getProperty(EDGE_NNODE);
                NNode two = last.getProperty(EDGE_NNODE);
                
                // a dummy node
                NNode dummy = NNode.of().create(nGraph);
                
                // an edge to the source 
                NEdge leftEdge = new NEdge();
                leftEdge.weight = 1;
                leftEdge.delta = 0;
                
                leftEdge.source = dummy;
                leftEdge.target = one;
                leftEdge.source.getOutgoingEdges().add(leftEdge);
                leftEdge.target.getIncomingEdges().add(leftEdge);
                
                // an edge to the target
                NEdge rightEdge = new NEdge();
                rightEdge.weight = 1;
                rightEdge.delta = 0;

                rightEdge.source = dummy;
                rightEdge.target = two;
                rightEdge.source.getOutgoingEdges().add(rightEdge);
                rightEdge.target.getIncomingEdges().add(rightEdge);
                
                last = cur;
            }
            
        }
    }
    
    private List<List<LEdge>> identifyPaths() {
        
        List<List<LEdge>> paths = Lists.newArrayList();
        
        for (Layer l: lGraph.getLayers()) {
            
            // now check every path starting at a junction
            for (LNode n : l.getNodes()) {
                if (nodeState[n.id] == JUNCTION) {
                    for (LEdge e : n.getConnectedEdges()) {
                        
                        if (!isHandledEdge(e)) {
                            continue;
                        }
                        
                        LNode other = e.getOther(n);
                        if (nodeState[other.id] != VISITED && nodeState[other.id] != JUNCTION) {
                            // follow the path
                            List<LEdge> seq = Lists.newArrayList(e);
                            boolean valid = follow(other, n, seq);

                            if (valid && seq.size() >= 2) {
                                paths.add(seq);
                            }

                            nodeState[other.id] = VISITED;
                        }
                    }
                }
            }
        }

        return paths;
    }
    
    private boolean follow(final LNode curr, final LNode prev, final List<LEdge> seq) {

        // valid paths end in junctions
        if (nodeState[curr.id] == JUNCTION) {
            return true;
        }
        
        for (LEdge incident : curr.getConnectedEdges()) {
            if (!isHandledEdge(incident)) {
                continue;
            }
            LNode otherOther = incident.getOther(curr);
            if (otherOther != prev) { // don't look back!

                seq.add(incident);

                if (nodeState[otherOther.id] != VISITED) {
                    return follow(otherOther, curr, seq);
                }
                
                nodeState[otherOther.id] = VISITED;
            }
        }
        
        return false;
    }
    
    /** Indicates that the node has not been visited yet. */
    private static final int VISITED = -1;
    /** Indicates that a node is neither a {@link #JUNCTION}, nor a {@link #LEAF}. */
    private static final int OTHER = 0;
    /** A junction has either an incoming degree larger than two, an outgoing degree larger than two, or both. */
    private static final int JUNCTION = 2;
    /** A leaf has exactly one incoming or outgoing edge. */
    private static final int LEAF = 1;
    
    private static int getNodeState(final LNode node) {
        int inco = 0;
        int ouco = 0;
        for (LPort p : node.getPorts()) {
            inco += p.getIncomingEdges().size();
            ouco += p.getOutgoingEdges().size();
            if (inco > 1 || ouco > 1) {
                return JUNCTION;
            }
        }
        if (inco + ouco == 1) {
            return LEAF;
        }
        return OTHER;
    }
}
