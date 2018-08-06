/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.loops.labeling;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopType;
import org.eclipse.elk.alg.layered.p5edges.loops.util.SelfLoopUtil;

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
    public static Map<SelfLoopComponent, List<SelfLoopLabelPosition>> generatePositions(final SelfLoopNode slNode) {
        Map<SelfLoopComponent, List<SelfLoopLabelPosition>> allComponentLoopLabelMap = new HashMap<>();

        if (slNode != null) {
            Multimap<SelfLoopType, SelfLoopComponent> componentTypes = SelfLoopUtil.getTypeMap(slNode.getNode());

            // positions for side loops
            SideLoopLabelPositionGenerator sidePlacer = new SideLoopLabelPositionGenerator();
            Map<SelfLoopComponent, List<SelfLoopLabelPosition>> sideLoopLabelMap =
                    placeLabels(componentTypes.get(SelfLoopType.SIDE), sidePlacer);
            allComponentLoopLabelMap.putAll(sideLoopLabelMap);

            // positions for corner loops
            CornerLoopLabelPositionGenerator cornerPlacer = new CornerLoopLabelPositionGenerator();
            Map<SelfLoopComponent, List<SelfLoopLabelPosition>> cornerLoopLabelMap =
                    placeLabels(componentTypes.get(SelfLoopType.CORNER), cornerPlacer);
            allComponentLoopLabelMap.putAll(cornerLoopLabelMap);

            // positions for opposing loops
            OppossingLoopLabelPositionGenerator oppossingPlacer = new OppossingLoopLabelPositionGenerator(slNode);
            Map<SelfLoopComponent, List<SelfLoopLabelPosition>> opposingLoopLabelMap =
                    placeLabels(componentTypes.get(SelfLoopType.OPPOSING), oppossingPlacer);
            allComponentLoopLabelMap.putAll(opposingLoopLabelMap);

            // positions for three side loops
            ThreeCornerLoopLabelPositionGenerator threeSidePlacer = new ThreeCornerLoopLabelPositionGenerator(slNode);
            Map<SelfLoopComponent, List<SelfLoopLabelPosition>> threeSideLoopLabelMap =
                    placeLabels(componentTypes.get(SelfLoopType.THREE_CORNER), threeSidePlacer);
            allComponentLoopLabelMap.putAll(threeSideLoopLabelMap);

            // positions for four side loops
            FourCornerLoopLabelPositionGenerator fourSidePlacer = new FourCornerLoopLabelPositionGenerator(slNode);
            Map<SelfLoopComponent, List<SelfLoopLabelPosition>> fourSideLoopLabelMap =
                    placeLabels(componentTypes.get(SelfLoopType.FOUR_CORNER), fourSidePlacer);
            allComponentLoopLabelMap.putAll(fourSideLoopLabelMap);
        }
        
        return allComponentLoopLabelMap;
    }

    private static Map<SelfLoopComponent, List<SelfLoopLabelPosition>> placeLabels(
            final Collection<SelfLoopComponent> components, final ISelfLoopLabelPositionGenerator selfLoopPlacer) {
        
        Map<SelfLoopComponent, List<SelfLoopLabelPosition>> positionMap = new HashMap<>();
        
        for (SelfLoopComponent component : components) {
            if (component.getLabel() != null && !component.getLabel().getLabels().isEmpty()) {
                List<SelfLoopLabelPosition> positions = selfLoopPlacer.generatePositions(component);
                positionMap.put(component, positions);
            }
        }
        
        return positionMap;
    }
}
