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
package org.eclipse.elk.alg.layered.intermediate;

import java.util.List;
import java.util.ListIterator;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Looks for long edge dummy nodes that can be joined together. The aim is to reduce the
 * amount of edges by having edges originating from the same port or going into the same
 * port joined. This should be done after crossing minimization. Only those dummy nodes
 * are joined that the crossing minimizer placed right next to each other.
 * 
 * <dl>
 *   <dt>Precondition:</dt>
 *     <dd>a layered graph</dd>
 *     <dd>node orders are fixed</dd>
 *     <dd>for long edge dummies {@link InternalProperties#LONG_EDGE_SOURCE} and
 *         {@link InternalProperties#LONG_EDGE_TARGET} properties must be set.</dd>
 *   <dt>Postcondition:</dt>
 *     <dd>long edge dummy nodes belonging to the same hyperedge and being directly next to each other
 *         are merged.</dd>
 *   <dt>Slots:</dt>
 *     <dd>Before phase 4.</dd>
 *   <dt>Same-slot dependencies:</dt>
 *     <dd>{@link InLayerConstraintProcessor}</dd>
 *     <dd>{@link LabelDummySwitcher}</dd>
 * </dl>
 *
 * @author cds
 * @kieler.design 2012-08-10 chsch grh
 * @kieler.rating proposed yellow by msp
 */
public final class HyperedgeDummyMerger implements ILayoutProcessor<LGraph> {

    /**
     * {@inheritDoc}
     */
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Hyperedge merging", 1);
        
        // Iterate through the layers
        ListIterator<Layer> layerIter = layeredGraph.getLayers().listIterator();
        while (layerIter.hasNext()) {
            Layer layer = layerIter.next();
            List<LNode> nodes = layer.getNodes();
            
            // If there are no nodes anyway, just move on to the next layer
            if (nodes.isEmpty()) {
                continue;
            }
            
            LNode currNode = null;
            NodeType currNodeType = null;
            LNode lastNode = null;
            NodeType lastNodeType = null;
            
            // Iterate through the remaining nodes
            for (int nodeIndex = 0; nodeIndex < nodes.size(); nodeIndex++) {
                // Get the next node
                currNode = nodes.get(nodeIndex);
                currNodeType = currNode.getType();
                
                // We're only interested if the current and last nodes are long edge dummies
                if (currNodeType == NodeType.LONG_EDGE && lastNodeType == NodeType.LONG_EDGE) {
                    // Get long edge source and target ports
                    LPort currNodeSource = currNode.getProperty(InternalProperties.LONG_EDGE_SOURCE);
                    LPort lastNodeSource = lastNode.getProperty(InternalProperties.LONG_EDGE_SOURCE);
                    LPort currNodeTarget = currNode.getProperty(InternalProperties.LONG_EDGE_TARGET);
                    LPort lastNodeTarget = lastNode.getProperty(InternalProperties.LONG_EDGE_TARGET);
                    
                    // Find out whether the two long edges come from the same source or target (non-null!)
                    boolean sameSource = currNodeSource != null && currNodeSource == lastNodeSource;
                    boolean sameTarget = currNodeTarget != null && currNodeTarget == lastNodeTarget;
                    
                    // If we can merge on grounds of the two long edges having the same source, we need to be
                    // in front of the label dummy of each of the two edges (if any)
                    boolean eligibleForSourceMerging =
                            (!currNode.getProperty(InternalProperties.LONG_EDGE_HAS_LABEL_DUMMIES)
                                    || currNode.getProperty(InternalProperties.LONG_EDGE_BEFORE_LABEL_DUMMY))
                            &&
                            (!lastNode.getProperty(InternalProperties.LONG_EDGE_HAS_LABEL_DUMMIES)
                                    || lastNode.getProperty(InternalProperties.LONG_EDGE_BEFORE_LABEL_DUMMY));
                    
                    // If we can merge on grounds of the two long edges having the same target, we need to be
                    // behind the label dummy of each of the two edges (if any)
                    boolean eligibleForTargetMerging =
                            (!currNode.getProperty(InternalProperties.LONG_EDGE_HAS_LABEL_DUMMIES)
                                    || !currNode.getProperty(InternalProperties.LONG_EDGE_BEFORE_LABEL_DUMMY))
                            &&
                            (!lastNode.getProperty(InternalProperties.LONG_EDGE_HAS_LABEL_DUMMIES)
                                    || !lastNode.getProperty(InternalProperties.LONG_EDGE_BEFORE_LABEL_DUMMY));
                    
                    // If the source or the target are identical and we are allowed to merge, merge the current node
                    // into the last
                    if ((sameSource && eligibleForSourceMerging) || (sameTarget && eligibleForTargetMerging)) {
                        mergeNodes(currNode, lastNode, sameSource, sameTarget);
                        
                        // Remove the current node and make the last node the current node
                        nodes.remove(nodeIndex);
                        nodeIndex--;
                        currNode = lastNode;
                        currNodeType = lastNodeType;
                    }
                }
                
                // Remember this node for the next iteration
                lastNode = currNode;
                lastNodeType = currNodeType;
            }
        }
        
        monitor.done();
    }
    
    /**
     * Merges the merge source node into the merge target node. All edges that were previously
     * connected to the merge source's ports are rerouted to the merge target. The merge target's
     * long edge source and target ports can be set to {@code null}.
     * 
     * @param mergeSource the merge source node.
     * @param mergeTarget the merge target node.
     * @param keepSourcePort if {@code false}, the long edge source property is set to {@code null}.
     *                       This should be done if the long edge sources of the two nodes to be merged
     *                       are different.
     * @param keepTargetPort if {@code false}, the long edge target property is set to {@code null}.
     *                       This should be done if the long edge targets of the two nodes to be merged
     *                       are different.
     */
    private void mergeNodes(final LNode mergeSource, final LNode mergeTarget,
            final boolean keepSourcePort, final boolean keepTargetPort) {
        
        // We assume that the input port is west, and the output port east
        LPort mergeTargetInputPort = mergeTarget.getPorts(PortSide.WEST).iterator().next();
        LPort mergeTargetOutputPort = mergeTarget.getPorts(PortSide.EAST).iterator().next();
        
        for (LPort port : mergeSource.getPorts()) {
            while (!port.getIncomingEdges().isEmpty()) {
                port.getIncomingEdges().get(0).setTarget(mergeTargetInputPort);
            }
            
            while (!port.getOutgoingEdges().isEmpty()) {
                port.getOutgoingEdges().get(0).setSource(mergeTargetOutputPort);
            }
        }
        
        // Possibly reset source and target ports
        if (!keepSourcePort) {
            mergeTarget.setProperty(InternalProperties.LONG_EDGE_SOURCE, null);
        }
        
        if (!keepTargetPort) {
            mergeTarget.setProperty(InternalProperties.LONG_EDGE_TARGET, null);
        }
    }

}
