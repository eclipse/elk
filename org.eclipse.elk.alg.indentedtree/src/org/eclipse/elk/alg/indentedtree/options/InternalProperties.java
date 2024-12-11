/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://rtsys.informatik.uni-kiel.de/kieler
 * 
 * Copyright ${year} by
 * + Kiel University
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 */
package org.eclipse.elk.alg.indentedtree.options;

import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * 
 */
public final class InternalProperties {

    
    /**
     * the total indentation of a node, as calculated through a depth-first search
     */
    public static final IProperty<Double> DEPTH = new Property<Double>("depth", 0.0);
    
    /**
     * the indentation of the edge to the left of a node, inherited from its "parent"
     */
    public static final IProperty<Double> EDGE_INDENTATION = new Property<Double>("edge_indentation", 0.0);
}
