/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ulf Rueegg - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.elk.alg.layered.ILayoutProcessor;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.layered.properties.LayeredOptions;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * As opposed to the fully interactive {@link org.eclipse.elk.alg.layered.p3order.InteractiveCrossingMinimizer
 * InteractiveCrossingMinimizer}, this processor inserts ordering constraints between any pair of regular node (
 * {@link NodeType#NORMAL}) of the same layer. Apart from this it relies on a crossing minimizer that supports such kind
 * of ordering constraints, e.g. our {@link org.eclipse.elk.alg.layered.p3order.LayerSweepCrossingMinimizer
 * LayerSweepCrossingMinimizer}. The processor must be run after the layering phase.
 * 
 * <dl>
 *   <dt>Precondition:</dt><dd>a layered graph.</dd>
 *   <dt>Postcondition:</dt><dd>inserted in-layer constraints to preserve regular nodes' order</dd>
 *   <dt>Slots:</dt><dd>Before phase 3.</dd>
 *   <dt>Same-slot dependencies:</dt><dd>Any layer-altering processor, such as {@link NodePromotion},
 *                                       must be executed prior to this processor.</dd>
 * </dl>
 */
public class SemiInteractiveCrossMinProcessor implements ILayoutProcessor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(final LGraph layeredGraph, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Semi-Interactive Crossing Minimization Processor", 1);

        for (Layer l : layeredGraph) {

            // #1 extract relevant nodes
            List<LNode> nodes = Lists.newArrayList(Iterables.filter(l.getNodes(), n -> n.getType() == NodeType.NORMAL));
            if (nodes.size() < 2) {
                continue;
            }

            // #2 sort them with ascending y coordinate
            Collections.sort(nodes, (n1, n2) -> {
                KVector origPos1 = n1.getProperty(LayeredOptions.POSITION);
                KVector origPos2 = n2.getProperty(LayeredOptions.POSITION);
                if (origPos1 != null && origPos2 != null) {
                    return Double.compare(origPos1.y, origPos2.y);
                } else {
                    return 0;
                }
            });

            // #3 introduce pair-wise in-layer constraints
            assert nodes.size() >= 2;
            Iterator<LNode> nodeIt = nodes.iterator();
            LNode prev = nodeIt.next();
            while (nodeIt.hasNext()) {
                LNode cur = nodeIt.next();
                prev.getProperty(InternalProperties.IN_LAYER_SUCCESSOR_CONSTRAINTS).add(cur);
                prev = cur;
            }
        }

        progressMonitor.done();
    }
}
