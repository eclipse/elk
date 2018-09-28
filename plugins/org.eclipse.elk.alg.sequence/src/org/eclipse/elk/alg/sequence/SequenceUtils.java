/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.sequence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LGraphElement;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.sequence.SequenceUtils.AreaNestingTreeNode;
import org.eclipse.elk.alg.sequence.graph.LayoutContext;
import org.eclipse.elk.alg.sequence.graph.SArea;
import org.eclipse.elk.alg.sequence.graph.SLifeline;
import org.eclipse.elk.alg.sequence.graph.SMessage;
import org.eclipse.elk.alg.sequence.options.InternalSequenceProperties;
import org.eclipse.elk.alg.sequence.options.MessageType;
import org.eclipse.elk.alg.sequence.options.SequenceDiagramOptions;
import org.eclipse.elk.core.math.ElkPadding;

/**
 * Utility methods.
 */
public final class SequenceUtils {

    /**
     * No instantiation.
     */
    private SequenceUtils() {
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Message Utilities
    
    /**
     * Calculates the effective length of the given lost / found message, taking a possible message label into account.
     * A message always has a certain minimum length, but may need more space.
     * 
     * @param smessage
     *            the lost / found message.
     * @param slifeline
     *            the non-dummy lifeline the message is connected to.
     * @param context
     *            the layout context.
     * @return the message's length.
     */
    public static double calculateLostFoundMessageLength(final SMessage smessage, final SLifeline slifeline,
            final LayoutContext context) {
        
        assert smessage.getProperty(SequenceDiagramOptions.TYPE_MESSAGE) == MessageType.LOST
                || smessage.getProperty(SequenceDiagramOptions.TYPE_MESSAGE) == MessageType.FOUND;
        
        return Math.max(
                (slifeline.getSize().x + context.lifelineSpacing) / 2,
                calculateMessageLength(smessage, context));
    }
    
    /**
     * Calculates the effective length of the given message, taking a possible message label into account. A message
     * must always be this long, but may need more space.
     * 
     * @param smessage
     *            the message.
     * @param context
     *            the layout context.
     * @return the space required by this message.
     */
    public static double calculateMessageLength(final SMessage smessage, final LayoutContext context) {
        double messageLength = 0;
        
        if (smessage.getLabel() != null) {
            messageLength = Math.max(messageLength, smessage.getLabel().getSize().x + 2 * context.labelSpacing);
        }
        
        return messageLength;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // LGraph Utilities
    
    /**
     * Creates a new layered node with a single port and adds it to the given graph.
     */
    public static LNode createLNode(final LGraph graph) {
        LNode node = new LNode(graph);
        graph.getLayerlessNodes().add(node);
        
        LPort port = new LPort();
        port.setNode(node);
        
        return node;
    }

    /**
     * Returns the origin object set on the given element if it is of the specified type.
     * 
     * @param element
     *            the element whose origin property to query.
     * @param type
     *            the type we expect the origin to be of.
     * @return the origin cast to the specified type, or {@code null} if it is {@code null} or not an instance of that
     *         type.
     */
    public static <T> T originObjectFor(final LGraphElement element, final Class<T> type) {
        Object origin = element.getProperty(InternalSequenceProperties.ORIGIN);
        
        if (type.isInstance(origin)) {
            return type.cast(origin);
        } else {
            return null;
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Area Utilities
    
    /**
     * Returns the padding to be applied to the given area. This can be a specific padding, or the default padding from
     * the layout context.
     */
    public static ElkPadding getAreaPadding(final SArea sArea, final LayoutContext context) {
        if (sArea.hasProperty(SequenceDiagramOptions.AREAS_PADDING)) {
            return sArea.getProperty(SequenceDiagramOptions.AREAS_PADDING);
        } else {
            return context.areaPadding;
        }
    }
    
    /**
     * For the given areas, this method computes their nesting trees. Areas that have no nesting relationship will end
     * up in different trees. The returned list contains the root node of each tree. 
     */
    public static List<AreaNestingTreeNode> computeAreaNestings(final Collection<SArea> areas) {
        // Create tree nodes for all areas
        Map<SArea, AreaNestingTreeNode> treeNodeMap = new HashMap<>();
        areas.stream().forEach(sArea -> treeNodeMap.put(sArea, new AreaNestingTreeNode(sArea)));
        
        // Add area relationships
        for (SArea sArea : areas) {
            SArea sParentArea = sArea.getParentArea();
            if (sParentArea != null && treeNodeMap.containsKey(sParentArea)) {
                AreaNestingTreeNode areaNode = treeNodeMap.get(sArea);
                AreaNestingTreeNode parentAreaNode = treeNodeMap.get(sParentArea);
                
                areaNode.parent = parentAreaNode;
                parentAreaNode.children.add(areaNode);
            }
        }
        
        // Return a list of all root nodes
        return treeNodeMap.values().stream()
                .filter(node -> node.parent == null)
                .collect(Collectors.toList());
    }
    
    /**
     * Represents a node in an area nesting tree computed to find the nesting of a given set of areas.
     */
    public static final class AreaNestingTreeNode {
        /** Area represented by this node. */
        public SArea sArea;
        /** Parent in the nesting tree. */
        public AreaNestingTreeNode parent;
        /** Children in the nesting tree. */
        public List<AreaNestingTreeNode> children = new ArrayList<>();
        
        /**
         * Creates a new instance for the given area.
         */
        public AreaNestingTreeNode(final SArea sArea) {
            this.sArea = sArea;
        }
    }
    
}
