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
package org.eclipse.elk.alg.layered.intermediate.wrapping;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.elk.alg.layered.ILayoutProcessor;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.intermediate.wrapping.ICutIndexCalculator.ManualCutIndexCalculator;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.layered.properties.LayeredOptions;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.collect.Lists;

/**
 * Intermediate processor that splits path-like graphs into 
 * multiple sub-paths to improve the overall aspect ratio of the drawing.
 * 
 * <p>
 * This is a rather problem-specific intermediate processor 
 * that works only on a very small subset of graphs, namely 
 * paths that have very few branches.
 * Also, it only works in conjunction with the 
 * {@link de.cau.cs.kieler.klay.layered.p2layers.LongestPathLayerer}.
 * </p>
 * 
 * <p>
 * What we do is the following. After layering and crossing minimization
 * we determine into how many "rows" we want to split the path.
 * Let n denote the number of nodes of each row. We start moving 
 * the node in layer n+1 into layer 1, node n+2 into layer 2 and so on.
 * While doing this we have to assure that the order within each 
 * layer is correct, have to insert dummy nodes for the edge that now 
 * spans from layer n+1 to layer 1, and have to treat inverted ports
 * specifically.
 * </p>
 * 
 * <dl>
 *   <dt>Precondition:</dt><dd>A layering has been calculated by the longest
 *   path layerer and the crossing minimization has finished.</dd>
 *   <dt>Postcondition:</dt><dd>Long paths are split such that the 
 *   aspect ratio is closer to the currently available area.</dd>
 *   <dt>Slots:</dt><dd>Before phase 4.</dd>
 * </dl>
 */
public class PathLikeGraphWrapper implements ILayoutProcessor {

    /**
     * {@inheritDoc}
     */
    public void process(final LGraph graph, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Path-Like Graph Wrapping", 1);

        if (graph.getLayers().isEmpty()) {
            progressMonitor.done();
            return;
        }
        
        // initialization 
        GraphStats gs = new GraphStats(graph);
        
        // stop early if there's nothing we can do
        double sumWidth = gs.getMaxWidth() * gs.longestPath;
        double currentAR = sumWidth / gs.getMaxWidth();
        if (gs.dar > currentAR) {
            // nothing to do for us
            progressMonitor.done();
            return;
        }

        // get indexes for cutting
        ICutIndexCalculator icic;
        switch (graph.getProperty(LayeredOptions.WRAPPING_CUTTING_STRATEGY)) {
            case MANUAL: 
                icic = new ManualCutIndexCalculator();
                break;
            case ARD:
                icic = new ARDCutIndexHeuristic();
                break;
            default:
                icic = new MSDCutIndexHeuristic();
        }
        List<Integer> cuts = icic.getCutIndexes(graph, gs);
        
        // if they are not guaranteed to be valid, make them valid
        if (!icic.guaranteeValid()) {
            switch (graph.getProperty(LayeredOptions.WRAPPING_PATH_LIKE_VALIDIFY_STRATEGY)) {
                case LOOK_BACK:
                    cuts = validifyIndexesLookingBack(gs, cuts);
                    break;
                case GREEDY:
                    cuts = validifyIndexesGreedily(gs, cuts);
                    break;
                default:
                    // leave the cuts as they are
            }
        }
        
        // re-build the layering
        performCuts(graph, gs, cuts);

        progressMonitor.done();
    }
    
    private List<Integer> validifyIndexesGreedily(final GraphStats gs, final List<Integer> cuts) {
        
        List<Integer> validCuts = Lists.newArrayList();
        int offset = 0;

        Iterator<Integer> cutIt = cuts.iterator();
        while (cutIt.hasNext()) {
            Integer cut = cutIt.next() + offset;
            while (cut < gs.longestPath && !gs.isCutAllowed(cut)) {
                cut++;
                offset++;
            }
            if (cut >= gs.longestPath) {
                // cut cannot be performed anymore, discard it
                break;
            }
            validCuts.add(cut);
        }
        
        return validCuts;
    }
    
    private List<Integer> validifyIndexesLookingBack(final GraphStats gs, final List<Integer> cuts) {

        if (cuts.isEmpty()) {
            return cuts;
        }

        List<Integer> validCuts = Lists.newArrayList();

        int offset = 0;
        int lastValidIndex = -1;
        
        Iterator<Integer> cutIt = cuts.iterator();
        Integer nextCut = cutIt.next(); // guaranteed to exist
        
        boolean seek = false;
        
        int idx = 1;
        outer: while (idx < gs.longestPath) {
            
            // #1 we are seeking for a valid position for a cut index
            if (seek) {
                
                int foundIndex = -1;
                // #1.1 distance to (existing) lastly valid index is smaller
                int distBack = Math.abs(nextCut - lastValidIndex);
                int distForward = Math.abs(idx - nextCut);
                if (lastValidIndex > 0 && (distBack <= distForward)) {
                    
                    offset -= distBack;
                    foundIndex = lastValidIndex;
                    idx = lastValidIndex; // will be incremented at the end of the while loop
                    
                } else {
                    // #1.2 look forwards
                    if (gs.isCutAllowed(idx)) {
                        offset += distForward;
                        foundIndex = idx;
                    }
                }
                
                // #1.3 accept the identified valid cut index
                if (foundIndex > 0) {
                    validCuts.add(foundIndex);
                    lastValidIndex = -1;
                    seek = false;
                    
                    // find next desired cut index that is larger than the current index
                    //  i.e. we possibly 'skip' desired cut indexes
                    do {
                        if (cutIt.hasNext()) {
                            nextCut = cutIt.next() + offset;
                        } else {
                            break outer;
                        }
                    } while (nextCut <= idx);
                }
                
            } else {
            
                // #2 update the 'lastValidIndex' variable
                if (gs.isCutAllowed(idx)) {
                    lastValidIndex = idx;
                }
                
                // #3 accept next cut index
                if (idx == nextCut) {

                    // #3.1 the desired cut is ok
                    if (gs.isCutAllowed(idx)) {
                        validCuts.add(nextCut);
                        lastValidIndex = -1;
                        // retrieve next desired cut
                        if (cutIt.hasNext()) {
                            nextCut = cutIt.next() + offset;
                        } else {
                            // all done
                            break;
                        }
                    } else {
                        // #3.2 desired cut is forbidden
                        seek = true;
                    }
                }
            }
            idx++;
        }
        
        return validCuts;
    }

    /**
     * Preconditions: <ul>
     *  <li>all cuts are allowed
     *  <li>no cut larger than number of layers
     *  <li>cuts are ordered increasingly.
     * </ul>
     */
    private void performCuts(final LGraph graph, final GraphStats gs, final List<Integer> cuts) {
        
        if (cuts.isEmpty()) {
            return;
        }
        
        // index in the "old" layering
        int index = 0;
        // index in the "new" layering
        int newIndex = 0;
        
        Iterator<Integer> cutIt = cuts.iterator();
        int nextCut = cutIt.next(); // guaranteed to exist
        
        // iterate all original layers
        while (index < gs.longestPath) {

            if (index == nextCut) {
                // start new "row"
                newIndex = 0;
                if (cutIt.hasNext()) {
                    nextCut = cutIt.next();
                } else {
                    // no further cut to apply
                    nextCut = gs.longestPath + 1;
                }
            }
            
            // once we started cutting, we have to move nodes
            if (index != newIndex) {

                Layer oldLayer = graph.getLayers().get(index);
                Layer newLayer = graph.getLayers().get(newIndex);

                List<LNode> nodesToMove = Lists.newArrayList(oldLayer.getNodes()); 
                for (LNode n : nodesToMove) {
                    
                    // first move the original node 
                    // to allow creating dummy nodes for the reversed edge
                    n.setLayer(newLayer.getNodes().size(), newLayer);                    

                    // at the cut points the edges have to be re-routed
                    if (newIndex == 0) {
                        List<LEdge> incEdges = Lists.newArrayList(n.getIncomingEdges());
                        for (LEdge e : incEdges) {
                            e.reverse(graph, true);
                            graph.setProperty(InternalProperties.CYCLIC, true);

                            // insert proper dummy nodes for the newly created long edge
                            CuttingUtils.insertDummies(graph, e, 1);
                        }
                    }
                }
            }

            // move to the next layer
            newIndex++;
            index++;
        }

       // remove old layers
       ListIterator<Layer> it = graph.getLayers().listIterator();
       while (it.hasNext()) {
           Layer l = it.next();
           if (l.getNodes().isEmpty()) {
               it.remove();
           }
       }
    }
    
}
