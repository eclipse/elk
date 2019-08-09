/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.oldloops.labeling;

import java.util.Collection;

import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopType;
import org.eclipse.elk.alg.layered.p5edges.oldloops.util.SelfLoopUtil;

import com.google.common.collect.Multimap;

/**
 * Contains methods for generating candidate positions for self-loop labels to be placed.
 */
public final class SelfLoopLabelPositionGeneration {

    /**
     * Not instantiation.
     */
    private SelfLoopLabelPositionGeneration() {
    }

    /**
     * Generates candidate positions for all self loop components of the given node.
     */
    public static void generatePositions(final SelfLoopNode slNode) {
        Multimap<SelfLoopType, SelfLoopComponent> componentTypes = SelfLoopUtil.getTypeMap(slNode.getNode());

        // positions for side loops
        SideLoopLabelPositionGenerator sidePlacer = new SideLoopLabelPositionGenerator(slNode);
        generatePositions(componentTypes.get(SelfLoopType.SIDE), sidePlacer);

        // positions for corner loops
        CornerLoopLabelPositionGenerator cornerPlacer = new CornerLoopLabelPositionGenerator(slNode);
        generatePositions(componentTypes.get(SelfLoopType.CORNER), cornerPlacer);

        // positions for opposing loops
        OpposingLoopLabelPositionGenerator oppossingPlacer = new OpposingLoopLabelPositionGenerator(slNode);
        generatePositions(componentTypes.get(SelfLoopType.OPPOSING), oppossingPlacer);

        // positions for three side loops
        ThreeCornerLoopLabelPositionGenerator threeSidePlacer = new ThreeCornerLoopLabelPositionGenerator(slNode);
        generatePositions(componentTypes.get(SelfLoopType.THREE_CORNER), threeSidePlacer);

        // positions for four side loops
        FourCornerLoopLabelPositionGenerator fourSidePlacer = new FourCornerLoopLabelPositionGenerator(slNode);
        generatePositions(componentTypes.get(SelfLoopType.FOUR_CORNER), fourSidePlacer);
    }

    private static void generatePositions(final Collection<SelfLoopComponent> components,
            final ISelfLoopLabelPositionGenerator selfLoopPlacer) {
        
        for (SelfLoopComponent component : components) {
            if (component.getSelfLoopLabel() != null) {
                selfLoopPlacer.generatePositions(component);
            }
        }
    }
}
