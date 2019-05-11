/*******************************************************************************
 * Copyright (c) 2019 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.core.util;

import org.eclipse.elk.graph.ElkNode;

/**
 * A logged graph, including relevant information about the graph. The actual data type of a logged graph can be either
 * an {@link org.eclipse.elk.graph.ElkNode} (for graphs of type {@link Type#ELK}, or a string (for the other graph
 * types).
 */
public final class LoggedGraph {

    /**
     * Possible types for a logged graph.
     */
    public static enum Type {
        /** Type for elk graphs. */
        ELK(ElkNode.class),
        /** Type for json graphs. */
        JSON(String.class),
        /** Type for dot graphs. */
        DOT(String.class);
        
        private final Class<?> expectedType;
        
        Type(final Class<?> expectedType) {
            this.expectedType = expectedType;
        }
        
        /**
         * Checks if {@code o}'s type is the same or a subtype of the type we would expect.
         */
        boolean isTypeCompatible(final Object o) {
            return expectedType.isAssignableFrom(o.getClass());
        }
    }
    

    /** A graph. */
    private final Object graph;
    /** Type of the graph. */
    private final Type graphType;
    /** Tag for identifying the graph. */
    private final String tag;
    

    /**
     * Creates a new logged graph.
     * 
     * @throws ClassCastException
     *             if the type of {@code graph} does not conform to the type expected for {@code graphType}.
     */
    public LoggedGraph(final Object graph, final String tag, final Type graphType) {
        this.graph = graph;
        this.tag = tag;
        this.graphType = graphType;
    }

    /**
     * Returns the graph.
     */
    public Object getGraph() {
        return graph;
    }

    /**
     * Returns the graph type.
     */
    public Type getGraphType() {
        return graphType;
    }

    /**
     * Returns a string that will describe the graph.
     */
    public String getTag() {
        return tag;
    }

}
