/*******************************************************************************
 * Copyright (c) 2018 TypeFox GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.core.util.IValidatingGraphElementVisitor;
import org.eclipse.elk.graph.ElkConnectableShape;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkGraphElement;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;

/**
 * A graph validator that checks basic structural properties of a graph.
 */
public class GraphValidator implements IValidatingGraphElementVisitor {
    
    /** The issues found by this validator. */
    private final List<GraphIssue> issues = new ArrayList<GraphIssue>();

    /**
     * Check the structural properties of the given graph element.
     */
    @Override
    public void visit(final ElkGraphElement element) {
        if (element instanceof ElkEdge) {
            checkEdge((ElkEdge) element);
        }
    }
    
    /**
     * Check the structural properties of the given edge.
     */
    protected void checkEdge(final ElkEdge edge) {
        if (!edge.isConnected()) {
            issues.add(new GraphIssue(edge, "Edge is not connected.", GraphIssue.Severity.ERROR));
        } else {
            ElkNode bestContainer = ElkGraphUtil.findBestEdgeContainment(edge);
            if (bestContainer != null && bestContainer != edge.getContainingNode()) {
                StringBuilder message = new StringBuilder("Edge should be contained in ");
                ElkUtil.printElementPath(bestContainer, message);
                issues.add(new GraphIssue(edge, message.toString(), GraphIssue.Severity.WARNING));
            }
        }
        
        for (ElkEdgeSection edgeSection : edge.getSections()) {
            ElkConnectableShape incomingShape = edgeSection.getIncomingShape();
            if (incomingShape != null) {
                if (!edge.getSources().contains(incomingShape)) {
                    String message = incomingShape.eClass().getName()
                            + " declared as incoming shape is not a source of this edge.";
                    issues.add(new GraphIssue(edge, message, GraphIssue.Severity.ERROR));
                }
                if (!edgeSection.getIncomingSections().isEmpty()) {
                    String message = "An edge section cannot be connected to an "
                            + incomingShape.eClass().getName() + " and other sections at the same time.";
                    issues.add(new GraphIssue(edge, message, GraphIssue.Severity.ERROR));
                }
            }
            ElkConnectableShape outgoingShape = edgeSection.getOutgoingShape();
            if (outgoingShape != null) {
                if (!edge.getTargets().contains(outgoingShape)) {
                    String message = outgoingShape.eClass().getName()
                            + " declared as outgoing shape is not a target of this edge.";
                    issues.add(new GraphIssue(edge, message, GraphIssue.Severity.ERROR));
                }
                if (!edgeSection.getOutgoingSections().isEmpty()) {
                    String message = "An edge section cannot be connected to an "
                            + outgoingShape.eClass().getName() + " and other sections at the same time.";
                    issues.add(new GraphIssue(edge, message, GraphIssue.Severity.ERROR));
                }
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<GraphIssue> getIssues() {
        return issues;
    }

}
