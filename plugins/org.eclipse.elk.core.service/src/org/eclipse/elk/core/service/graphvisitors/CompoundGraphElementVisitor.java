/*******************************************************************************
 * Copyright (c) 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0  *******************************************************************************/
package org.eclipse.elk.core.service.graphvisitors;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.core.util.IGraphElementVisitor;
import org.eclipse.elk.graph.ElkGraphElement;
import org.eclipse.elk.graph.ElkNode;

/**
 * Class to apply many graph element visitors on the same graph.
 */
public class CompoundGraphElementVisitor implements IGraphElementVisitor {
    
    /**
     * List of graph visitors which will be applied in the given order.
     */
    private List<IGraphElementVisitor> graphVisitors = new LinkedList<>();
    
    /**
     * Creates a compound graph visitor using the given visitors.
     * 
     * @param visitors The graph visitors
     */
    public CompoundGraphElementVisitor(final IGraphElementVisitor... visitors) {
        graphVisitors.addAll(Arrays.asList(visitors));
    }

    /* (non-Javadoc)
     * @see org.eclipse.elk.core.util.IGraphElementVisitor#visit(org.eclipse.elk.graph.ElkGraphElement)
     */
    @Override
    public void visit(final ElkGraphElement element) {
        if (element.eContainer() == null && element instanceof ElkNode) {
            ElkUtil.applyVisitors((ElkNode) element,
                    graphVisitors.toArray(new IGraphElementVisitor[graphVisitors.size()]));
        }

    }
    
    /**
     * Add {@link IGraphElementVisitor} to this compound visitor.
     * @param visitors The graph visitors
     */
    public void addGraphVisitor(final IGraphElementVisitor... visitors) {
        this.graphVisitors.addAll(Arrays.asList(visitors));
    }

    /**
     * @return the graphVisitors
     */
    public List<IGraphElementVisitor> getGraphVisitors() {
        return graphVisitors;
    }

}
