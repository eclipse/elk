/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.oldloops.util;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopType;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * Basic self-loop utility methods.
 */
public final class SelfLoopUtil {

    private SelfLoopUtil() {
        // nothing
    }

    /**
     * Assembles a multimap that contains all self-loop components that match each component type for the given node.
     */
    public static Multimap<OldSelfLoopType, OldSelfLoopComponent> getTypeMap(final LNode node) {
        Multimap<OldSelfLoopType, OldSelfLoopComponent> typeMap = ArrayListMultimap.create();
        
        OldSelfLoopNode slNode = node.getProperty(InternalProperties.SELF_LOOP_NODE_REPRESENTATION);

        for (OldSelfLoopComponent component : slNode.getSelfLoopComponents()) {
            typeMap.put(component.getType(slNode), component);
        }

        return typeMap;
    }
}
