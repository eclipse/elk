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

import org.eclipse.elk.alg.sequence.options.SequenceExecutionType;

import com.google.common.collect.Lists;

/**
 * Data structure for execution specification elements in sequence diagrams.
 */
public final class SExecution extends SShape {

    private static final long serialVersionUID = -4034839145972542469L;

    /** The type of the execution. */
    private SequenceExecutionType type = null;
    /** The list of connected messages. */
    private final List<SMessage> messages = Lists.newArrayList();

    /**
     * Get the type of the execution.
     * 
     * @return the type
     */
    public SequenceExecutionType getType() {
        return type;
    }

    /**
     * Set the type of the execution.
     * 
     * @param type
     *            the new type
     */
    public void setType(final SequenceExecutionType type) {
        this.type = type;
    }

    /**
     * Get the list of messages that are connected to the execution.
     * 
     * @return the list of messages
     */
    public List<SMessage> getMessages() {
        return messages;
    }

    /**
     * Add a message to the list of connected messages.
     * 
     * @param message
     *            the new message
     */
    public void addMessage(final SMessage message) {
        this.messages.add(message);
    }

    @Override
    public String toString() {
        return "Messages: " + messages + ", Pos: (" + getPosition().x + "/" + getPosition().y + "), Size: ("
                + getSize().x + "/" + getSize().y + ")";
    }
}
