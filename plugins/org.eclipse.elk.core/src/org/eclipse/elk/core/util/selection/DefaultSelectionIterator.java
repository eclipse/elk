/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.util.selection;

import java.util.Iterator;

import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KGraphElement;
import org.eclipse.elk.graph.KPort;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

/**
 * The default {@link SelectionIterator} for usage in
 * {@link de.cau.cs.kieler.kiml.util.KimlUtil#getConnectedElements KimlUtil.getConnectedElements()}.
 * The iterator follows all edges transitively connected to the initial edge. Optionally the
 * iterator can also include the corresponding ports in the selection.
 */
public class DefaultSelectionIterator extends SelectionIterator {

    private static final long serialVersionUID = 2945508835051993888L;

    private boolean addPorts;

    private boolean followEdgeDirection;

    /**
     * Creates a new iterator which can optionally include ports in its selection and can be
     * configured to either iterate towards the tail or the head of the selected {@link KEdge}.
     * 
     * @param edge
     *            the {@link KEdge} to start with
     * @param addPorts
     *            flag to determine whether {@link KPorts KPort} should be included in the selection
     * @param followEdgeDirection
     *            flag whether the iterator should iterate towards the head or the tail of the edge
     */
    public DefaultSelectionIterator(final KEdge edge, final boolean addPorts,
            final boolean followEdgeDirection) {
        super(edge);
        this.addPorts = addPorts;
        this.followEdgeDirection = followEdgeDirection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Iterator<? extends KGraphElement> getChildren(final Object object) {
        // Ensure that the visited set is properly initialized
        if (visited == null) {
            visited = Sets.newHashSet();
        }
        if (object instanceof KEdge) {
            KEdge edge = (KEdge) object;
            final KPort port = followEdgeDirection ? edge.getTargetPort() : edge.getSourcePort();

            if (port == null || visited.contains(port)) {
                // return an empty iterator if no target/source port is configured
                // or if the target/source port has been visited already, in order
                // to break infinite loops
                return Iterators.<KGraphElement>emptyIterator();
            }

            visited.add(port);

            // for each object (kedge) visited by this iterator check all the edges connected to
            // 'port' and visit those edges satisfying the criterion stated above
            Iterator<KEdge> resultEdges =
                    Iterators.filter(port.getEdges().iterator(), new Predicate<KEdge>() {

                        public boolean apply(final KEdge input) {
                            return followEdgeDirection ? port == input.getSourcePort()
                                    : port == input.getTargetPort();
                        }
                    });

            // If the port should be added to the selection, add it to the result set
            if (addPorts) {
                Iterator<KGraphElement> portIterator =
                        Iterators.singletonIterator((KGraphElement) port);
                return Iterators.concat(portIterator, resultEdges);
            } else {
                return resultEdges;
            }
        } else {
            return Iterators.<KGraphElement>emptyIterator();
        }
    }
}
