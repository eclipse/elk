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
package org.eclipse.elk.core.comments;

import java.util.Map;

import org.eclipse.elk.graph.KGraphElement;

/**
 * An attachment decider has the final say on which graph element to attach a comment to, if any. The
 * decision is based on the normalized heuristic values produced by the heuristics for each possible
 * graph element the comment can be attached to.
 */
@FunctionalInterface
public interface IAttachmentDecider {
    
    /**
     * Decides which graph element to attach a comment to, if any.
     * 
     * @param normalizedHeuristics
     *            maps possible attachment targets to a map from heuristic implementations to the
     *            normalized heuristic values they produced.
     * @return the selected attachment target, or {@code null} if the comment should be left
     *         unattached.
     */
    KGraphElement makeAttachmentDecision(
            Map<KGraphElement, Map<Class<? extends IHeuristic>, Double>> normalizedHeuristics);
    
}
