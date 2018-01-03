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
package org.eclipse.elk.alg.sequence.properties;

import java.util.List;
import java.util.Set;

import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.graph.KNode;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Data structure for area-like elements in sequence diagrams. This class is not part of the core data
 * structure of this layout algorithm since it actually has to be attached to the graph in the
 * {@link InternalSequenceProperties#AREAS} property. Thus, while also being used by the algorithm, this
 * class has to be visible to the outside.
 * 
 * <p>
 * This data structure is only used outside of the layout algorithm if the algorithm is in Papyrus mode.
 * </p>
 * 
 * @author grh
 * @kieler.design proposed grh
 * @kieler.rating proposed yellow grh
 */
public final class SequenceArea {
    /** The layout graph node that represents this sequence area. */
    private KNode layoutNode;
    /** The list of messages contained in the area. */
    private Set<Object> messages = Sets.newLinkedHashSet();
    /** The list of affected lifelines. */
    private Set<Object> lifelines = Sets.newLinkedHashSet();
    /** The list of subareas (in case of a combined fragment). */
    private List<SequenceArea> subAreas = Lists.newArrayList();
    /** The list of areas that are contained in this area. */
    private List<SequenceArea> containedAreas = Lists.newArrayList();
    /** The message, that is nearest to the area if the area is empty. */
    private Object nextMessage;
    /** The size of the area. */
    private KVector size = new KVector();
    /** The position of the area. */
    private KVector position = new KVector();
    
    
    /**
     * Creates a new sequence area represented in the layout graph by the given node.
     * 
     * @param node the node in the layout graph.
     */
    public SequenceArea(final KNode node) {
        layoutNode = node;
    }
    

    /**
     * Get the size of the area.
     * 
     * @return the KVector with the size
     */
    public KVector getSize() {
        return size;
    }

    /**
     * Get the position of the area.
     * 
     * @return the KVector with the position
     */
    public KVector getPosition() {
        return position;
    }

    /**
     * Returns the node in the layout graph that represents this area.
     * 
     * @return the layout node.
     */
    public KNode getLayoutNode() {
        return layoutNode;
    }

    /**
     * Sets the node in the layout graph that represents this area.
     * 
     * @param node the layout node.
     */
    public void setLayoutNode(final KNode node) {
        layoutNode = node;
    }
    
    /**
     * Get the list of messages that are covered by the area.
     * 
     * @return the list of messages
     */
    public Set<Object> getMessages() {
        return messages;
    }

    /**
     * Get the list of lifelines that are (partly) covered by the area.
     * 
     * @return the list of lifelines
     */
    public Set<Object> getLifelines() {
        return lifelines;
    }

    /**
     * Get the list of sub-areas (for combined fragments) of the area.
     * 
     * @return the list of sub-areas
     */
    public List<SequenceArea> getSubAreas() {
        return subAreas;
    }

    /**
     * Get the list of areas, that are completely covered by this area.
     * 
     * @return the list of contained areas
     */
    public List<SequenceArea> getContainedAreas() {
        return containedAreas;
    }

    /**
     * Get the message that is next to the area if the area does not contain any messages.
     * 
     * @return the message next to the area
     */
    public Object getNextMessage() {
        return nextMessage;
    }

    /**
     * Set the message that is next to the area.
     * 
     * @param nextMessage
     *            the new message that is next to the area
     */
    public void setNextMessage(final Object nextMessage) {
        this.nextMessage = nextMessage;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        String subareas = "\nwith " + subAreas.size() + " Subareas: " + subAreas;
        if (subAreas.size() == 0) {
            subareas = "";
        }
        return "Area " + layoutNode + "\nwith " + messages.size() + " Messages: " + messages + "\nand "
                + lifelines.size() + " Lifelines: " + lifelines + "\nat (" + getPosition().x + "/"
                + getPosition().y + ") + (" + getSize().x + "/" + getSize().y + ")" + subareas;
    }
}
