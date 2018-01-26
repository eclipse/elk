/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.common.spore;

/**
 * Interface for an overlap handling function used in the {@link ScanlineOverlapCheck}.
 */
@FunctionalInterface
public interface IOverlapHandler {
    /**
     * This is what is done with overlapping nodes.
     * 
     * @param n1 one node
     * @param n2 the other node
     */
    void handle(Node n1, Node n2);
}
