/*******************************************************************************
 * Copyright (c) 2016 TypeFox GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    spoenemann - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.core.klayoutdata.KEdgeLayout;
import org.eclipse.elk.core.klayoutdata.KLayoutData;
import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.util.IValidatingGraphElementVisitor;
import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KGraphElement;
import org.eclipse.elk.graph.properties.IProperty;

/**
 * A validator for lower and upper bounds of layout options.
 */
public class LayoutOptionValidator implements IValidatingGraphElementVisitor {
    
    /** The issues found by this validator. */
    private final List<GraphIssue> issues = new ArrayList<GraphIssue>();

    /**
     * Check the lower and upper bounds of all properties attached to graph elements.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void visit(final KGraphElement element) {
        KLayoutData layoutData = element instanceof KEdge
                ? element.getData(KEdgeLayout.class)
                : element.getData(KShapeLayout.class);
        for (Map.Entry<IProperty<?>, Object> entry : layoutData.getProperties()) {
            Object value = entry.getValue();
            if (value != null) {
                checkProperty(element, (IProperty<Object>) entry.getKey(), value);
            }
        }
    }
    
    /**
     * Check the lower and upper bounds of the given property.
     */
    protected void checkProperty(final KGraphElement element, final IProperty<Object> property,
            final Object value) {
        if (property.getLowerBound().compareTo(value) > 0) {
            String optionName = property instanceof LayoutOptionData
                    ? ((LayoutOptionData) property).getName()
                    : property.getId();
            String message = "The assigned value " + value.toString()
                    + " of the option '" + optionName
                    + "' is less than the lower bound " + property.getLowerBound().toString() + ".";
            issues.add(new GraphIssue(element, message, GraphIssue.Severity.ERROR));
        } else if (property.getUpperBound().compareTo(value) < 0) {
            String optionName = property instanceof LayoutOptionData
                    ? ((LayoutOptionData) property).getName()
                    : property.getId();
            String message = "The assigned value " + value.toString()
                    + " of the option '" + optionName
                    + "' is greater than the upper bound " + property.getUpperBound().toString() + ".";
            issues.add(new GraphIssue(element, message, GraphIssue.Severity.ERROR));
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
