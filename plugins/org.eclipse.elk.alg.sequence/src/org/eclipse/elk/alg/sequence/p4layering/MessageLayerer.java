/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.sequence.p4layering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.p2layers.NetworkSimplexLayerer;
import org.eclipse.elk.alg.sequence.SequencePhases;
import org.eclipse.elk.alg.sequence.SequenceUtils;
import org.eclipse.elk.alg.sequence.graph.LayoutContext;
import org.eclipse.elk.alg.sequence.graph.SArea;
import org.eclipse.elk.alg.sequence.graph.SLifeline;
import org.eclipse.elk.alg.sequence.graph.SMessage;
import org.eclipse.elk.alg.sequence.options.InternalSequenceProperties;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Uses ELK Layered's {@link NetworkSimplexLayerer} to compute a layering for the messages in the
 * LGraph representation of a sequence diagram. This simply delegates to the network simplex layerer,
 * but needs to be in its own class because the network simplex layerer doesn't implement our layout
 * processor interface.
 */
public final class MessageLayerer implements ILayoutPhase<SequencePhases, LayoutContext> {

    @Override
    public LayoutProcessorConfiguration<SequencePhases, LayoutContext> getLayoutProcessorConfiguration(
            final LayoutContext graph) {
        
        return LayoutProcessorConfiguration.create();
    }
    
    @Override
    public void process(final LayoutContext context, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Message layer assignment", 2);
        
        NetworkSimplexLayerer layerer = new NetworkSimplexLayerer();
        layerer.process(context.lgraph, progressMonitor.subTask(1));
        
        // Assign IDs to everything and fix the layering to remove messages from areas they don't belong in
        assignIds(context);
        fixInvalidAreaOverlaps(context);
        
        progressMonitor.done();
    }
    
    private void assignIds(final LayoutContext context) {
        int lifelineId = 0;
        for (SLifeline lifeline : context.sgraph.getLifelines()) {
            lifeline.id = lifelineId++;
        }
        
        int layerId = 0;
        for (Layer layer : context.lgraph.getLayers()) {
            layer.id = layerId++;
        }
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Fix Layering Algorithm
    
    /* The algorithm works as follows. We iterate over the layers. For each layer, we determine which areas become
     * active by looking through all nodes and checking if they are part of an area that is not active yet. We then
     * use these information to determine which areas are active in each lifeline. A lifeline may have active areas
     * even though it does not have incident messages that belong to the area, simply due to the fact that the area
     * spans that lifeline. It is such messages that we then need to move to the next layer. Finally, we determine
     * which areas cease to be active in the layer. An area ceases to be active once we have seen all of its
     * lowermost nodes. 
     */
    
    /**
     * Fixes the layering by moving nodes that don't belong in an area down until they are not in the area anymore.
     */
    private void fixInvalidAreaOverlaps(final LayoutContext context) {
        // The map of active areas and their data as well as the the set of areas active for each lifeline 
        Map<SArea, AreaData> activeAreas = new HashMap<>();
        List<Set<SArea>> activeAreasPerLifeline = new ArrayList<>(context.sgraph.getLifelines().size());
        
        // Create empty sets for each lifeline
        context.sgraph.getLifelines().stream()
            .forEach(ll -> activeAreasPerLifeline.add(new HashSet<>()));
        
        // During this loop, layers may end up getting added, thus the regular loop instead of a for-each loop
        List<Layer> layers = context.lgraph.getLayers();
        for (int layerIndex = 0; layerIndex < layers.size(); layerIndex++) {
            Layer layer = layers.get(layerIndex);
            
            // Step 1: Add areas that become active in this layer
            addThemActiveAreas(context, layer, activeAreas, activeAreasPerLifeline);
            
            // Step 2: Move nodes downwards that don't belong in this layer due to area constraints
            moveInvalidNodes(layer, activeAreas, activeAreasPerLifeline);
            
            // Step 3: Remove areas that become inactive after this layer
            removeThemActiveAreas(layer, activeAreas, activeAreasPerLifeline);
        }
    }
    

    //////////////////////////////////
    // Active Area Management
    
    /**
     * Creates {@link AreaData} objects for all areas that start spring into existence in this layer. The new areas are
     * added to the map of active areas, and to the set of active areas of each lifeline they span.
     */
    private void addThemActiveAreas(final LayoutContext context, final Layer layer,
            final Map<SArea, AreaData> activeAreas, final List<Set<SArea>> activeAreasPerLifeline) {
        
        outerLoop:
        for (LNode node : layer) {
            // We're looking for the dummy nodes inserted for the area's header
            SArea area = SequenceUtils.originObjectFor(node, SArea.class);
            if (area != null) {
                // Found one!!!
                assert !activeAreas.containsKey(area);
                
                // Check if there are areas currently active that this area cannot overlap with. That is the case if
                // the current area does not contain the other areas as its child areas or vice versa.
                AreaData areaData = new AreaData(area, context);
                
                for (int llIndex = areaData.leftmostLifeline; llIndex <= areaData.rightmostLifeline; llIndex++) {
                    // Every area active at the current lifeline must either include our current lifeline or must be
                    // included by our current lifeline
                    for (SArea activeArea : activeAreasPerLifeline.get(llIndex)) {
                        if (!activeArea.getContainedAreas().contains(area)
                                && !area.getContainedAreas().contains(activeArea)) {
                            
                            continue outerLoop;
                        }
                    }
                }
                
                activeAreas.put(area, areaData);
                
                // Add to set of active areas for each lifeline they span
                for (int i = areaData.leftmostLifeline; i <= areaData.rightmostLifeline; i++) {
                    activeAreasPerLifeline.get(i).add(area);
                }
            }
        }
    }
    
    /**
     * Iterates over the layer's nodes and removes them from each area's set of yet unencountered lowermost nodes. Once
     * we have encountered all of an area's lowermost nodes, that area is removed both from the set of active areas and
     * from each lifeline's set of active areas.
     */
    private void removeThemActiveAreas(final Layer layer, final Map<SArea, AreaData> activeAreas,
            final List<Set<SArea>> activeAreasPerLifeline) {
        
        for (LNode node : layer) {
            // Try removing the node from each active area. We use an iterator here to be able to remove areas that
            // are becoming inactive on the fly
            Iterator<Map.Entry<SArea, AreaData>> areaDataIterator = activeAreas.entrySet().iterator();
            while (areaDataIterator.hasNext()) {
                Map.Entry<SArea, AreaData> areaDataEntry = areaDataIterator.next();
                
                // Try removing the current node from the area's set of lowermost nodes
                AreaData areaData = areaDataEntry.getValue();
                areaData.unencounteredLowermostNodes.remove(node);
                
                if (areaData.unencounteredLowermostNodes.isEmpty()) {
                    SArea area = areaDataEntry.getKey();
                    
                    // We have encountered all of the areas lowermost nodes, so remove it
                    for (int i = areaData.leftmostLifeline; i <= areaData.rightmostLifeline; i++) {
                        activeAreasPerLifeline.get(i).remove(area);
                    }
                    
                    // Remove this entry from the map of active areas
                    areaDataIterator.remove();
                }
            }
        }
    }
    
    
    //////////////////////////////////
    // Node Movement
    
    /**
     * Finds nodes that don't belong in the given layer and moves those to the next. Once this method returns, the
     * layering is valid again.
     */
    private void moveInvalidNodes(final Layer layer, final Map<SArea, AreaData> activeAreas,
            final List<Set<SArea>> activeAreasPerLifeline) {
        
        List<LNode> nodesToBeMoved = new ArrayList<>(layer.getNodes().size());
        
        for (LNode node : layer) {
            if (!canStayInLayer(node, layer, activeAreas.keySet(), activeAreasPerLifeline)) {
                // The node cannot stay in this layer, so schedule if to be moved to the next. Do that later to avoid
                // ConcurrentModificationExceptions
                nodesToBeMoved.add(node);
            }
        }
        
        // If we have moved any nodes to the next layer, we may have made the layering invalid
        if (!nodesToBeMoved.isEmpty()) {
            Layer nextLayer = retrieveNextLayer(layer);
            for (LNode node : nodesToBeMoved) {
                node.setLayer(nextLayer);
            }
            
            // This may have caused the layering to be messed up. Fix that.
            fixLayering(nextLayer);
        }
    }

    /**
     * Checks if the given node can stay in the given layer. For a node to be able to stay, we must check for both of
     * its incident lifelines whether it is part of all areas active there. Nodes that don't represent a message can
     * always stay in their lifeline.
     */
    private boolean canStayInLayer(final LNode node, Layer layer, final Set<SArea> activeAreas,
            final List<Set<SArea>> activeAreasPerLifeline) {
        
        // If we have a message, check whether it is part of all areas in the lifelines between its end points
        SMessage message = SequenceUtils.originObjectFor(node, SMessage.class);
        if (message != null) {
            return isPartOfAllAreas(message, message.getSource(), message.getTarget(), activeAreasPerLifeline);
        }
        
        // If we have the top node of an area, check whether the area should be active in this layer (as determined
        // in step one of each iteration)
        SArea area = SequenceUtils.originObjectFor(node, SArea.class);
        if (area != null) {
            return activeAreas.contains(area);
        }
        
        return true;
    }
    
    /**
     * Checks whether the given message is part of all areas active between the given lifelines.
     */
    private boolean isPartOfAllAreas(final SMessage message, final SLifeline sourceLL, final SLifeline targetLL,
            final List<Set<SArea>> activeAreasPerLifeline) {
        
        if (sourceLL.isDummy()) {
            return isPartOfAllAreas(message, targetLL, activeAreasPerLifeline);
        } else if (targetLL.isDummy()) {
            return isPartOfAllAreas(message, sourceLL, activeAreasPerLifeline);
        } else {
            int leftIndex = sourceLL.id;
            int rightIndex = targetLL.id;
            
            if (leftIndex > rightIndex) {
                int tmp = leftIndex;
                leftIndex = rightIndex;
                rightIndex = tmp;
            }
            
            // Check if the message is part of all lifelines it spans
            List<SLifeline> lifelines = sourceLL.getGraph().getLifelines();
            for (int llIndex = leftIndex; llIndex <= rightIndex; llIndex++) {
                if (!isPartOfAllAreas(message, lifelines.get(llIndex), activeAreasPerLifeline)) {
                    return false;
                }
            }
            
            return true;
        }
    }

    
    /**
     * Checks whether the given message is part of all areas active at the given lifeline. If the lifeline is a dummy
     * lifeline, we return {@code true}.
     */
    private boolean isPartOfAllAreas(final SMessage message, final SLifeline lifeline,
            final List<Set<SArea>> activeAreasPerLifeline) {
        
        // Disregard dummy lifelines
        if (lifeline.isDummy()) {
            return true;
            
        } else {
            // Try finding an area the message is not part of
            for (SArea activeArea : activeAreasPerLifeline.get(lifeline.id)) {
                if (!activeArea.getMessages().contains(message)) {
                    return false;
                }
            }
            
            // The message is part of all areas
            return true;
        }
    }
    
    /**
     * Returns the layer after the given layer. If no next layer exists, a new layer is added to the graph, along with
     * a valid ID.
     */
    private Layer retrieveNextLayer(Layer layer) {
        int layerIndex = layer.id + 1;
        
        if (layer.getGraph().getLayers().size() <= layerIndex) {
            // Create and add a new layer
            Layer nextLayer = new Layer(layer.getGraph());
            layer.getGraph().getLayers().add(nextLayer);
            nextLayer.id = layerIndex;
        }
        
        return layer.getGraph().getLayers().get(layerIndex);
    }
    
    /**
     * Fixes the layering starting at the given layer. For each edge {@code (a, b)}, if {@code b} is in the layer, it
     * gets moved into the next layer. Once at least one node was moved into the next layer, this method continues
     * there.
     */
    private void fixLayering(final Layer layer) {
        boolean continueFixing = true;
        Layer currLayer = layer;
        List<LNode> nodesToBeMoved = new ArrayList<>();
        
        while (continueFixing) {
            // We initially assume for each layer that we don't have to move any node to its successor
            continueFixing = false;
            
            for (LNode node : currLayer) {
                for (LEdge outgoingEdge : node.getOutgoingEdges()) {
                    LNode targetNode = outgoingEdge.getTarget().getNode();
                    
                    if (targetNode != node && targetNode.getLayer().id == currLayer.id) {
                        // Mark the target node as having to be moved (we cannot do that in this loop without causing
                        // a ConcurrentModificationException
                        nodesToBeMoved.add(targetNode);
                    }
                }
            }
            
            // If we have nodes that need to be moved, move them and set things up for the next iteration
            if (!nodesToBeMoved.isEmpty()) {
                Layer nextLayer = retrieveNextLayer(currLayer);
                
                for (LNode node : nodesToBeMoved) {
                    node.setLayer(nextLayer);
                }
                
                continueFixing = true;
                nodesToBeMoved.clear();
                currLayer = nextLayer;
            }
        }
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // AreaData Class

    private static class AreaData {
        /** Those of the area's lowermost nodes that we have not seen yet. Once this is empty, the area is over. */
        private final Set<LNode> unencounteredLowermostNodes;
        /** The leftmost lifeline spanned by this area. */
        private int leftmostLifeline;
        /** The rightmost lifeline spanned by this area. */
        private int rightmostLifeline;
        
        public AreaData(final SArea area, final LayoutContext context) {
            unencounteredLowermostNodes = new HashSet<>(area.getProperty(InternalSequenceProperties.LOWERMOST_NODES));
            
            leftmostLifeline = context.sgraph.getLifelines().size();
            rightmostLifeline = -1;
            
            for (SMessage smessage : area.getMessages()) {
                SLifeline source = smessage.getSource();
                if (!source.isDummy()) {
                    leftmostLifeline = Math.min(leftmostLifeline, source.id);
                    rightmostLifeline = Math.max(rightmostLifeline, source.id);
                }

                SLifeline target = smessage.getTarget();
                if (!target.isDummy()) {
                    leftmostLifeline = Math.min(leftmostLifeline, target.id);
                    rightmostLifeline = Math.max(rightmostLifeline, target.id);
                }
            }
        }
    }

}
