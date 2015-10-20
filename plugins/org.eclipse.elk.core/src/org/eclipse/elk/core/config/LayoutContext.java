/*******************************************************************************
 * Copyright (c) 2011, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.config;

import java.util.Set;

import org.eclipse.elk.core.LayoutOptionData;
import org.eclipse.elk.graph.KGraphElement;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.MapPropertyHolder;
import org.eclipse.elk.graph.properties.Property;

/**
 * Context information for configuration of layout options. A layout context contains references
 * to a diagram (view model) element, its corresponding domain model element, its corresponding
 * graph element, and other information that is relevant for the layout of that element.
 * Contexts are used by {@link org.eclipse.elk.core.config.ILayoutConfig ILayoutConfigs}
 * in order to identify elements of a diagram for correctly assign values of layout options.
 * The most important properties that should be contained in a layout context are defined here.
 * These should be considered when a layout configurator is queried about context properties.
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating yellow 2013-07-01 review KI-38 by cds, uru
 */
public class LayoutContext extends MapPropertyHolder {
    
    /**
     * Creates and returns a global context.
     * 
     * @return a global context
     */
    public static LayoutContext global() {
        LayoutContext globalContext = new LayoutContext();
        globalContext.setProperty(GLOBAL, true);
        return globalContext;
    }
    
    /** the serial version UID. */
    private static final long serialVersionUID = -7544617305602906672L;
    
    /** whether the context is global, that means it affects options for the whole layout process. */
    public static final IProperty<Boolean> GLOBAL = new Property<Boolean>("context.global", false);

    /** the graph element in the current context. */
    public static final IProperty<KGraphElement> GRAPH_ELEM = new Property<KGraphElement>(
            "context.graphElement");
    
    /** the main domain model element in the current context. */
    public static final IProperty<Object> DOMAIN_MODEL = new Property<Object>(
            "context.domainModelElement");
    
    /** the domain model element of the container of the current graph element. */
    public static final IProperty<Object> CONTAINER_DOMAIN_MODEL = new Property<Object>(
            "context.containerDomainModelElement");
    
    /** the main diagram part in the current context. */
    public static final IProperty<Object> DIAGRAM_PART = new Property<Object>(
            "context.diagramPart");
    
    /** the diagram part for the container of the current graph element. */
    public static final IProperty<Object> CONTAINER_DIAGRAM_PART = new Property<Object>(
            "context.containerDiagramPart");
    
    /** the types of targets for layout options: this determines whether the graph element in
     *  the current context is a node, edge, port, or label. */
    public static final IProperty<Set<LayoutOptionData.Target>> OPT_TARGETS
            = new Property<Set<LayoutOptionData.Target>>("context.optionTargets");

    /**
     * Create an empty layout context.
     */
    public LayoutContext() {
    }
    
    /**
     * Create a new layout context initialized with the content of an existing context.
     * 
     * @param other another layout context
     */
    public LayoutContext(final LayoutContext other) {
        this.copyProperties(other);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.getAllProperties().toString();
    }
    
}
