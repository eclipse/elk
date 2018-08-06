/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.loops.util;

import java.util.List;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopType;

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
    public static Multimap<SelfLoopType, SelfLoopComponent> getTypeMap(final LNode node) {
        Multimap<SelfLoopType, SelfLoopComponent> typeMap = ArrayListMultimap.create();
        
        SelfLoopNode nodeRep = node.getProperty(InternalProperties.SELFLOOP_NODE_REPRESENTATION);
        List<SelfLoopComponent> components = node.getProperty(InternalProperties.SELFLOOP_COMPONENTS);

        for (SelfLoopComponent component : components) {
            typeMap.put(component.getType(nodeRep), component);
        }

        return typeMap;
    }
}
