/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.radial.intermediate.optimization;

import org.eclipse.elk.graph.ElkNode;

/**
 * The interface for translation optimizations.
 */
public interface IEvaluation {

    /**
     * Evaluate the current graph status.
     * 
     * @param graph The graph.
     * @return a value representing the score of the evaluation
     */
    double evaluate(ElkNode graph);
}