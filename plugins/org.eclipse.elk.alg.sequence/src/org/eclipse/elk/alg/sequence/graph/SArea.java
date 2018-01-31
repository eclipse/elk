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
package org.eclipse.elk.alg.sequence.graph;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Data structure for area-like elements (such as fragments) in sequence diagrams.
 */
public final class SArea extends SShape {

    private static final long serialVersionUID = -4129121595931929760L;

    /** The list of messages contained in the area. */
    private final Set<SMessage> messages = Sets.newLinkedHashSet();
    /** The list of affected lifelines. */
    private final Set<SLifeline> lifelines = Sets.newLinkedHashSet();
    /** The list of sections / operators (in case of a combined fragment). */
    private final List<SArea> sections = Lists.newArrayList();
    /** The list of areas that are contained in this area. */
    private final List<SArea> containedAreas = Lists.newArrayList();
    /** The message, that is nearest to the area if the area is empty. */
    private SMessage nextMessage;

    /**
     * Get the list of messages that are covered by the area.
     * 
     * @return the list of messages
     */
    public Set<SMessage> getMessages() {
        return messages;
    }

    /**
     * Get the list of lifelines that are (partly) covered by the area.
     * 
     * @return the list of lifelines
     */
    public Set<SLifeline> getLifelines() {
        return lifelines;
    }

    /**
     * Get the list of sections / operators if the area is a combined fragment.
     * 
     * @return (possibly empty) list of sections.
     */
    public List<SArea> getSections() {
        return sections;
    }

    /**
     * Get the list of areas, that are completely covered by this area.
     * 
     * @return the list of contained areas
     */
    public List<SArea> getContainedAreas() {
        return containedAreas;
    }

    /**
     * Get the message that is next to the area if the area does not contain any messages.
     * 
     * @return the message next to the area
     */
    public SMessage getNextMessage() {
        return nextMessage;
    }

    /**
     * Set the message that is next to the area.
     * 
     * @param nextMessage
     *            the new message that is next to the area
     */
    public void setNextMessage(final SMessage nextMessage) {
        this.nextMessage = nextMessage;
    }

    @Override
    public String toString() {
        String subareas = "\nwith " + sections.size() + " Subareas: " + sections;
        if (sections.size() == 0) {
            subareas = "";
        }
        return "Area with " + messages.size() + " Messages: " + messages + "\nand " + lifelines.size() + " Lifelines: "
                + lifelines + "\nat (" + getPosition().x + "/" + getPosition().y + ") + (" + getSize().x + "/"
                + getSize().y + ")" + subareas;
    }
}
