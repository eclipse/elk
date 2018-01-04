/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.sequence.graph.transform;

import org.eclipse.elk.alg.sequence.graph.LayoutContext;
import org.eclipse.elk.graph.ElkNode;

/**
 * Imports and exports sequence diagrams from ELK graphs.
 */
public class ElkGraphTransformer implements IGraphTransformer<ElkNode> {

    @Override
    public LayoutContext importGraph(ElkNode graph) {
        return new ElkGraphImporter().importGraph(graph);
    }

    @Override
    public void applyLayout(LayoutContext layoutContext) {
        new ElkGraphExporter().applyLayout(layoutContext);
    }

}
