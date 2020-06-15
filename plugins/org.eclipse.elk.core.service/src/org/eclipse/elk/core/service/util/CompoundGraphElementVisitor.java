/*******************************************************************************
 * Copyright (c) 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0  
 *******************************************************************************/
package org.eclipse.elk.core.service.util;

import java.util.ArrayList;
import java.util.Arrays;
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
    private List<IGraphElementVisitor> graphVisitors = new ArrayList<>();
    
    /**
     * True, if the first visitor is applied to the complete graph before the second one is applied.
     * False, if all visitors are first applied to the first node and after that to the second node... .
     */
    private boolean applyToFullGraphFirst = false;
    
    /**
     * Creates a compound graph visitor using the given visitors.
     * 
     * @param visitors The graph visitors
     */
    public CompoundGraphElementVisitor(final IGraphElementVisitor... visitors) {
        graphVisitors.addAll(Arrays.asList(visitors));
    }

    /**
     * Creates a compound graph visitor using the given visitors.
     * 
     * @param applyVisitorsDepthFirst Whether the first visitor is applied to all element first (true) or
     * all visitors first to the first element (false)
     * @param visitors The graph visitors
     */
    public CompoundGraphElementVisitor(final boolean applyToFullGraphFirst, final IGraphElementVisitor... visitors) {
        this(visitors);
        this.applyToFullGraphFirst = applyToFullGraphFirst;
    }

    /* (non-Javadoc)
     * @see org.eclipse.elk.core.util.IGraphElementVisitor#visit(org.eclipse.elk.graph.ElkGraphElement)
     */
    @Override
    public void visit(final ElkGraphElement element) {
        if (element instanceof ElkNode) {
            if (applyToFullGraphFirst) {
                ElkUtil.applyVisitors((ElkNode) element,
                        graphVisitors.toArray(new IGraphElementVisitor[graphVisitors.size()]));
            } else {
                for (IGraphElementVisitor graphVisitor : graphVisitors) {
                    graphVisitor.visit(element);
                }
            }
        }
    }
    
    /**
     * Add {@link IGraphElementVisitor} to this compound visitor.
     * 
     * @param visitors The graph visitors
     */
    public void addGraphVisitor(final IGraphElementVisitor... visitors) {
        this.graphVisitors.addAll(Arrays.asList(visitors));
    }

    /**
     * @return the list of graph visitors
     */
    public List<IGraphElementVisitor> getGraphVisitors() {
        return graphVisitors;
    }

    /**
     * @return true, if the graph visitors are applied depth-first
     */
    public boolean isApplyToFullGraphFirst() {
        return applyToFullGraphFirst;
    }

    /**
     * @param applyToFullGraphFirst Whether the first visitor is applied to all element first (true) or
     * all visitors first to the first element (false)
     */
    public void setApplyToFullGraphFirst(boolean applyToFullGraphFirst) {
        this.applyToFullGraphFirst = applyToFullGraphFirst;
    }

}
