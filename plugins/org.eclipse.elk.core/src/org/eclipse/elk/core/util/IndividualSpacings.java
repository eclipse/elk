/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.core.util;

import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.util.adapters.GraphAdapters.NodeAdapter;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.IPropertyHolder;
import org.eclipse.elk.graph.properties.MapPropertyHolder;

/**
 * A property holder which can be used to define individual spacing overrides. The overrides are applied to the
 * element this object is set on, which is done through the {@link CoreOptions#SPACING_INDIVIDUAL} property.
 */
public class IndividualSpacings extends MapPropertyHolder {

    /** Serialization identifier. */
    private static final long serialVersionUID = 737614242607924309L;
    
    
    /**
     * Returns the value of the given property as it applies to the given node. First checks whether an individual
     * override is set on the node that has the given property configured. If so, the configured value is returned.
     * Otherwise, the node's parent node, if any, is queried.
     * 
     * @param node
     *            the node whose property value to return.
     * @param property
     *            the property.
     * @return the individual override for the property or the default value inherited by the parent node.
     */
    public static <T> T getIndividualOrInherited(final ElkNode node, final IProperty<T> property) {
        T result = null;
        
        if (node.hasProperty(CoreOptions.SPACING_INDIVIDUAL)) {
            IPropertyHolder individualSpacings = node.getProperty(CoreOptions.SPACING_INDIVIDUAL);
            if (individualSpacings.hasProperty(property)) {
                result = individualSpacings.getProperty(property);
            }
        }
        
        // use the common value
        if (result == null && node.getParent() != null) {
            result = node.getParent().getProperty(property);
        }
        
        return result;
    }
    
    /**
     * Returns the value of the given property as it applies to the given node. First checks whether an individual
     * override is set on the node that has the given property configured. If so, the configured value is returned.
     * Otherwise, the graph the node is part of, if any, is queried.
     * 
     * @param node
     *            the node whose property value to return.
     * @param property
     *            the property.
     * @return the individual override for the property or the default value inherited by the parent node.
     */
    public static <T> T getIndividualOrInherited(final NodeAdapter<?> node, final IProperty<T> property) {
        T result = null;
        
        if (node.hasProperty(CoreOptions.SPACING_INDIVIDUAL)) {
            IPropertyHolder individualSpacings = node.getProperty(CoreOptions.SPACING_INDIVIDUAL);
            if (individualSpacings.hasProperty(property)) {
                result = individualSpacings.getProperty(property);
            }
        }
        
        // use the common value
        if (result == null && node.getGraph() != null) {
            result = node.getGraph().getProperty(property);
        }
        
        // if the result is still null, we need the property's default value
        if (result == null) {
            result = property.getDefault();
        }
        
        return result;
    }

}
