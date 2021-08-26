/*******************************************************************************
 * Copyright (c) 2021 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p3order;

import java.util.Random;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.options.InternalProperties;

/**
 * Minimizes crossing with the barycenter method. However, the node order given by the order in the model
 * does not change. I.e. only the dummy nodes are sorted in the already sorted real nodes.
 */
public class ModelOrderBarycenterHeuristic extends BarycenterHeuristic {

    /**
     * Constructs a model order barycenter heuristic for crossing minimization.
     * 
     * @param constraintResolver
     *            the constraint resolver
     * @param random
     *            the random number generator
     * @param portDistributor
     *            calculates the port ranks for the barycenter heuristic.
     * @param graph
     *            current node order
     */
    public ModelOrderBarycenterHeuristic(final ForsterConstraintResolver constraintResolver, final Random random,
            final AbstractBarycenterPortDistributor portDistributor, final LNode[][] graph) {
        super(constraintResolver, random, portDistributor, graph);
        barycenterStateComparator = 
                (n1, n2) -> {
                    if (n1.hasProperty(InternalProperties.MODEL_ORDER)
                            && n2.hasProperty(InternalProperties.MODEL_ORDER)) {
                        return Integer.compare(n1.getProperty(InternalProperties.MODEL_ORDER),
                                n2.getProperty(InternalProperties.MODEL_ORDER));
                    }
                    BarycenterState s1 = stateOf(n1);
                    BarycenterState s2 = stateOf(n2);
                        if (s1.barycenter != null && s2.barycenter != null) {
                            return s1.barycenter.compareTo(s2.barycenter);
                        } else if (s1.barycenter != null) {
                            return -1;
                        } else if (s2.barycenter != null) {
                            return 1;
                        }
                        return 0;
                };
    }

}
