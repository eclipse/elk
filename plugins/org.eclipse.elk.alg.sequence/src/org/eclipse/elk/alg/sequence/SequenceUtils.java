/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.sequence;

import org.eclipse.elk.alg.sequence.graph.LayoutContext;
import org.eclipse.elk.alg.sequence.graph.SLifeline;
import org.eclipse.elk.alg.sequence.graph.SMessage;
import org.eclipse.elk.alg.sequence.options.MessageType;
import org.eclipse.elk.alg.sequence.options.SequenceDiagramOptions;

/**
 * Utility methods.
 */
public final class SequenceUtils {
    
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
    
    
    
    private SequenceUtils() {
    }
    
}
