/*******************************************************************************
 * Copyright (c) 2016 alan and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    alan - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p3order.counting;

import java.util.List;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;

/**
 * Implement IIinitializable and return an implementation of this class to declare data that needs to be initialized
 * before running the LayerSweepCrossingMinimizer. We iterate through layers, nodes, ports and edges in the input order.
 * 
 * @author alan
 *
 */
public abstract class AbstractInitializer {

    private LNode[][] nodeOrder;

    /** Constructor to ensure we have the node order. */
    public AbstractInitializer(final LNode[][] graph) {
        this.nodeOrder = graph;
    }
    
    // @formatter:off
    /** Initialize anything needed at layer level. */
    public void initAtLayerLevel(final int l) { }

    /** Initialize anything needed at node level. */
    public void initAtNodeLevel(final int l, final int n) { }

    /** Initialize anything needed at port level. */
    public void initAtPortLevel(final int l, final int n, final int p) { }

    /** Initialize anything needed at edge level. */
    public void initAtEdgeLevel(final int l, final int n, final int p, final int e) { }

    /** Finish initialization. */
    public void initAfterTraversal() { }
    // @formatter:on
    
    /** Shortcut to get port. */
    protected LPort port(final int l, final int n, final int p) {
        return nodeOrder[l][n].getPorts().get(p);
    }

    /** Shortcut to get edge. */
    protected LEdge edge(final int l, final int n, final int p, final int e) {
        return nodeOrder[l][n].getPorts().get(p).getConnectedEdges().get(e);
    }

    /** Implement this in using class. */
    public interface IInitializable {
        /** Get initializer object. */
        AbstractInitializer initializer();
    }
    
    /** Traverse graph and on each level call the respective methods for a list of initializable objects. */ 
    public static void init(final List<IInitializable> initializables) {
        LNode[][] order = initializables.get(0).initializer().nodeOrder;
        for (int l = 0; l < order.length; l++) {
            for (IInitializable i : initializables) {
                i.initializer().initAtLayerLevel(l);
            }
            for (int n = 0; n < order[l].length; n++) {
                for (IInitializable i : initializables) {
                    i.initializer().initAtNodeLevel(l, n);
                }
                List<LPort> ports = order[l][n].getPorts();
                for (int p = 0; p < ports.size(); p++) {
                    for (IInitializable i : initializables) {
                        i.initializer().initAtPortLevel(l, n, p);
                    }
                    LPort port = ports.get(p);
                    for (int e = 0; e < port.getConnectedEdges().size(); e++) {
                        for (IInitializable i : initializables) {
                            i.initializer().initAtEdgeLevel(l, n, p, e);
                        }
                    }
                }
            }
        }
        for (IInitializable i : initializables) {
            i.initializer().initAfterTraversal();
        }
    }

    /** Returns node order. */
    public LNode[][] nodeOrder() {
        return nodeOrder;
    }
}

