/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christoph Daniel Schulze - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.graph.util;

import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkGraphElement;
import org.eclipse.elk.graph.ElkLabel;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.ElkPort;

/**
 * Generates identifiers for graph elements where missing. Inside ELK, this class is mainly used to generate
 * proper identifiers before a graph is serialized using the textual graph languages, which uses identifiers for
 * elements referencing each other.
 */
public final class GraphIdentifierGenerator {
    
    /** the highest ID generated for a node so far. */
    private int currNodeId = 1;
    /** the highest ID generated for a port so far. */
    private int currPortId = 1;
    /** the highest ID generated for an edge so far. */
    private int currEdgeId = 1;
    /** the highest ID generated for an edge section so far. */
    private int currEdgeSectionId = 1;
    /** the highest ID generated for a label so far. */
    private int currLabelId = 1;
    
    
    /**
     * Constructor is only called from the inside.
     */
    private GraphIdentifierGenerator() {
        
    }
    
    
    /**
     * Generates identifiers for the graph's elements, where missing.
     * 
     * @param graph the graph to generate identifiers for.
     */
    public static void generate(final ElkNode graph) {
        new GraphIdentifierGenerator().generateIdentifiers(graph);
    }
    
    
    /**
     * Recursively generates identifiers for the given element and its child elements.
     * 
     * @param element the element to generate an identifier for.
     */
    private void generateIdentifiers(final ElkGraphElement element) {
        new ElkGraphSwitch<Object>() {
            
            @Override
            public Object caseElkNode(final ElkNode node) {
                if (node.getParent() == null) {
                    setIdentifierIfMissing(node, "G1");
                } else {
                    setIdentifierIfMissing(node, "N" + currNodeId++);
                }
                
                node.getLabels().stream().forEach(l -> generateIdentifiers(l));
                node.getPorts().stream().forEach(l -> generateIdentifiers(l));
                node.getContainedEdges().stream().forEach(l -> generateIdentifiers(l));
                
                return null;
            }
            
            @Override
            public Object caseElkPort(final ElkPort port) {
                setIdentifierIfMissing(port, "P" + currPortId++);
                port.getLabels().stream().forEach(l -> generateIdentifiers(l));
                return null;
            }
            
            @Override
            public Object caseElkLabel(final ElkLabel label) {
                setIdentifierIfMissing(label, "L" + currLabelId++);
                label.getLabels().stream().forEach(l -> generateIdentifiers(l));
                return null;
            }
            
            @Override
            public Object caseElkEdge(final ElkEdge edge) {
                setIdentifierIfMissing(edge, "E" + currEdgeId++);
                edge.getLabels().stream().forEach(l -> generateIdentifiers(l));
                return null;
            }
            
            @Override
            public Object caseElkEdgeSection(final ElkEdgeSection section) {
                setIdentifierIfMissing(section, "ES" + currEdgeSectionId++);
                return null;
            }
            
        }.doSwitch(element);
    }
    
    private boolean setIdentifierIfMissing(final ElkGraphElement element, final String id) {
        if (element.getIdentifier() == null || element.getIdentifier().trim().isEmpty()) {
            element.setIdentifier(id);
            return true;
        } else {
            return false;
        }
    }
    
    private boolean setIdentifierIfMissing(final ElkEdgeSection section, final String id) {
        if (section.getIdentifier() == null || section.getIdentifier().trim().isEmpty()) {
            section.setIdentifier(id);
            return true;
        } else {
            return false;
        }
    }
    
}
