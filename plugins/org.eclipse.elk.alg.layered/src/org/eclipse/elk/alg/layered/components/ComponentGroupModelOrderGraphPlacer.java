/*******************************************************************************
 * Copyright (c) 2022 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.layered.components;

import java.util.List;
import java.util.Set;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.PortSide;

/**
 * A graph placer that tries to place the components of a graph with taking the model order and the
 * connections to external ports into account. This graph placer should only be used if the constraints applying to the
 * external ports are either {@code FREE} or {@code FIXED_SIDES}.
 */
public class ComponentGroupModelOrderGraphPlacer extends ComponentGroupGraphPlacer {
    
    private LGraph lastComponent = null;
    
    ///////////////////////////////////////////////////////////////////////////////
    // AbstractGraphPlacer

    @Override
    public void combine(final List<LGraph> components, final LGraph target) {
        lastComponent = null;
        componentGroups.clear();
        assert !components.contains(target);
        target.getLayerlessNodes().clear();
        
        // Check if there are any components to be placed
        if (components.isEmpty()) {
            target.getSize().x = 0;
            target.getSize().y = 0;
            return;
        }
        
        // Set the graph properties
        LGraph firstComponent = components.get(0);
        target.copyProperties(firstComponent);
        
        // Construct component groups
        for (LGraph component : components) {
            addComponent(component);
        }
        
        // Place components in each group
        KVector offset = new KVector();
        KVector maxSize = new KVector();
        double componentSpacing = firstComponent.getProperty(LayeredOptions.SPACING_COMPONENT_COMPONENT);

        for (ComponentGroup group : componentGroups) {
            // Place the components
            KVector groupSize = this.placeComponents((ModelOrderComponentGroup) group, componentSpacing);
            offsetGraphs(group.getComponents(), offset.x, offset.y);
            maxSize.x = Math.max(maxSize.x, groupSize.x + offset.x);
            maxSize.y = Math.max(maxSize.y, groupSize.y + offset.y);
            if (target.getProperty(LayeredOptions.CONSIDER_MODEL_ORDER_COMPONENTS) != ComponentOrderingStrategy.NONE) {
                // Compute the new offset. The previous component might not be in vertical or horizontal conflict.
                // Normally this cannot occur but here graphs with the same port sides may be in different groups.
                for (Set<PortSide> side : group.getPortSides()) {
                    if (side.contains(PortSide.SOUTH)) {
                        offset.x += groupSize.x;
                        break;
                    }
                }
                for (Set<PortSide> side : group.getPortSides()) {
                    if (side.contains(PortSide.EAST)) {
                        offset.y += groupSize.y;
                        break;
                    }
                }
            } else {
                offset.x += groupSize.x;
                offset.y += groupSize.y;
            }
        }
        
        // Set the graph's new size (the component group sizes include additional spacing
        // on the right and bottom sides which we need to subtract at this point)
        target.getSize().x = maxSize.x - componentSpacing;
        target.getSize().y = maxSize.y - componentSpacing;
        
        // if compaction is desired, do so!cing;
        if (firstComponent.getProperty(LayeredOptions.COMPACTION_CONNECTED_COMPONENTS)
                // the compaction only supports orthogonally routed edges
                && firstComponent.getProperty(LayeredOptions.EDGE_ROUTING) == EdgeRouting.ORTHOGONAL) {

            // apply graph offsets (which we reset later on)
            // since the compaction works in a common coordinate system
            for (LGraph h : components) {
                offsetGraph(h, h.getOffset().x, h.getOffset().y);
            }

            ComponentsCompactor compactor = new ComponentsCompactor();
            compactor.compact(components, target.getSize(), componentSpacing);

            // the compaction algorithm places components absolutely,
            // therefore we have to use the final drawing's offset
            for (LGraph h : components) {
                h.getOffset().reset().add(compactor.getOffset());
            }

            // set the new (compacted) graph size
            target.getSize().reset().add(compactor.getGraphSize());
        }

        // finally move the components to the combined graph
        for (ComponentGroup group : componentGroups) {
            moveGraphs(target, group.getComponents(), 0, 0);
        }
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////
    // Component Group Building
    
    /**
     * Adds the given component to the first component group that has place for it.
     * 
     * @param component the component to be placed.
     */
    protected void addComponent(final LGraph component) {
        // Check if one of the existing component groups has some place left
        if (this.componentGroups.size() > 0) {
            ModelOrderComponentGroup group =
                    (ModelOrderComponentGroup) this.componentGroups.get(componentGroups.size() - 1);
            if (group.add(component, lastComponent)) {
                this.lastComponent = component;
                return;
            }
        }
        
        // Create a new component group for the component
        componentGroups.add(new ModelOrderComponentGroup(component));
        this.lastComponent = component;
    }   

}
