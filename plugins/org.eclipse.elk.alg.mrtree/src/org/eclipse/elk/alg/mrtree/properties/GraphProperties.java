/*******************************************************************************
 * Copyright (c) 2013, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.mrtree.properties;

/**
 * An enumeration of properties a graph may have. These can be used as part of an {@code EnumSet} to
 * base decisions on graph properties. For example, self-loop processing may be skipped if the graph
 * doesn't contain self-loops in the first place.
 * 
 * <p>
 * An {@code EnumSet} for this enumeration can be attached to a graph via the
 * {@link MrTreeOptions#GRAPH_PROPERTIES} property.
 * </p>
 * 
 * TODO this is not used yet 
 * 
 * @author sor
 * @author sgu
 */
public enum GraphProperties {

    /** The graph contains self-loops. */
    SELF_LOOPS;

}
