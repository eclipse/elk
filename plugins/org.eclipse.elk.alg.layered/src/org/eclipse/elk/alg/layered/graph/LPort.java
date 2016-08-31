/*******************************************************************************
 * Copyright (c) 2010, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.UnaryOperator;

import org.eclipse.elk.alg.layered.properties.PortType;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.PortSide;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * A port in a layered graph. The position of the port is relative to the upper left corner
 * of the containing node and marks the port's upper left corner. The position where edges are
 * attached is marked by the anchor and is relative to the port's position. The incoming edges
 * and outgoing edges are stored separately, but is is possible to iterate over both types of
 * edges using the concatenation of the corresponding lists.
 * 
 * <p>Port must be used even if the original graph does not reveal them. In this
 * case each edge has dedicated source and target ports, which are used to
 * determine the points where the edge touches the source and target nodes.</p>
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating yellow 2013-03-22 review KI-35 by chsch, grh
 */
public final class LPort extends LShape {

    /** the serial version UID. */
    private static final long serialVersionUID = -3406558719744943360L;
    /** the owning node. */
    private LNode owner;
    /** the port side. */
    private PortSide side = PortSide.UNDEFINED;
    /** the anchor point position. */
    private final KVector anchor = new KVector();
    /** the margin area around this port. */
    private final LInsets margin = new LInsets();
    /** this port's labels. */
    private final List<LLabel> labels = Lists.newArrayListWithCapacity(2);

    /** the edges going into the port. */
    private final List<LEdge> incomingEdges = new NoteChangeList<>(4);
    /** the edges going out of the port. */
    private final List<LEdge> outgoingEdges = new NoteChangeList<>(4);
    /** All connected edges in one list. */
    private List<LEdge> connectedEdges;

    /**
     * Returns the node that owns this port.
     * 
     * @return the owning node
     */
    public LNode getNode() {
        return owner;
    }

    /**
     * Return predicate for filtering by portType.
     * 
     * @param portType
     * @return predicate
     */
    public static Predicate<LPort> getInOutputPredicate(final PortType portType) {
        if (portType == PortType.OUTPUT) {
            return port -> !port.getOutgoingEdges().isEmpty();
        } else if (portType == PortType.INPUT) {
            return port -> !port.getIncomingEdges().isEmpty();
        }
        throw new UnsupportedOperationException("Can't filter on undefined PortType");
    }

    /**
     * Return predicate for filtering by side.
     * 
     * @param side
     * @return predicate
     */
    public static Predicate<LPort> sidePredicate(final PortSide side) {
        return port -> port.getSide() == side;
    }

    /**
     * Sets the owning node and adds itself to the node's list of ports. If the port was previously
     * in another node, it is removed from that node's list of ports. Be careful not to use this
     * method while iterating through the ports list of the old node nor of the new node, since that
     * could lead to {@link java.util.ConcurrentModificationException}s.
     * 
     * @param node the owner to set
     */
    public void setNode(final LNode node) {
        if (this.owner != null) {
            this.owner.getPorts().remove(this);
        }
        
        this.owner = node;
        
        if (this.owner != null) {
            this.owner.getPorts().add(this);
        }
    }

    /**
     * Returns the node side on which the port is drawn.
     * 
     * @return the side
     */
    public PortSide getSide() {
        return side;
    }

    /**
     * Sets the node side on which the port is drawn.
     * 
     * @param theside the side to set
     */
    public void setSide(final PortSide theside) {
        if (theside == null) {
            throw new NullPointerException();
        }
        this.side = theside;
    }
    
    /**
     * Returns the anchor position of the port. This is the point where edges should be attached,
     * relative to the port's position. Should only be modified when the port position is changed.
     * 
     * @return the anchor position
     */
    public KVector getAnchor() {
        return anchor;
    }
    
    /**
     * Returns the absolute anchor position of the port. This is the point where edges should be
     * attached, relative to the containing graph. This method creates a new vector, so modifying
     * the result will not affect this port in any way.
     * 
     * @return a new vector with the absolute anchor position
     */
    public KVector getAbsoluteAnchor() {
        return KVector.sum(owner.getPosition(), this.getPosition(), anchor);
    }
    
    /**
     * Returns the margin around this port. The margin is typically used to reserve space for the
     * port's labels.
     * 
     * @return the port's margin.
     */
    public LInsets getMargin() {
        return margin;
    }
    
    /**
     * Returns this port's labels.
     * 
     * @return this port's labels.
     */
    public List<LLabel> getLabels() {
        return labels;
    }
    
    /**
     * Returns the name of the port. The name is derived from the text of the first label, if any.
     * 
     * @return the name, or {@code null}
     */
    public String getName() {
        if (!labels.isEmpty()) {
            return labels.get(0).getText();
        }
        return null;
    }
    
    /**
     * Returns this port's degree, that is, the number of edges connected to it.
     * 
     * @return the number of edges connected to this port.
     */
    public int getDegree() {
        return incomingEdges.size() + outgoingEdges.size();
    }
    
    /**
     * Returns the number of incoming edges minus the number of outgoing edges. This
     * is the net flow of the port.
     * 
     * @return the port's net flow.
     */
    public int getNetFlow() {
        return incomingEdges.size() - outgoingEdges.size();
    }

    /**
     * Returns the list of edges going into this port.
     * 
     * @return the incoming edges
     */
    public List<LEdge> getIncomingEdges() {
        return incomingEdges;
    }

    /**
     * Returns the list of edges going out of this port.
     * 
     * @return the outgoing edges
     */
    public List<LEdge> getOutgoingEdges() {
        return outgoingEdges;
    }
    
    /**
     * Returns an iterable over all connected edges, both incoming and outgoing.
     * 
     * @return an iterable over all connected edges.
     */
    public List<LEdge> getConnectedEdges() {
        if (edgeListsHaveChanged) {
            connectedEdges = new ArrayList<>(incomingEdges.size() + outgoingEdges.size());
            connectedEdges.addAll(incomingEdges);
            connectedEdges.addAll(outgoingEdges);
            edgeListsHaveChanged = false;
        }
        return connectedEdges;
    }

    /**
     * Returns an iterable over all the port's predecessor ports. Predecessor ports are source
     * ports of incoming edges of this port.
     * 
     * @return an iterable over all predecessor ports.
     */
    public Iterable<LPort> getPredecessorPorts() {
        return new Iterable<LPort>() {
            public Iterator<LPort> iterator() {
            final Iterator<LEdge> edgesIter = incomingEdges.iterator();

            return new Iterator<LPort>() {
                public boolean hasNext() {
                    return edgesIter.hasNext();
                }
                public LPort next() {
                    return edgesIter.next().getSource();
                }
                public void remove() {
                    edgesIter.remove();
                }
            };
            }

        };
    }

    /**
     * Returns an iterable over all the port's successor ports. Successor ports are target
     * ports of outgoing edges of this port.
     * 
     * @return an iterable over all successor ports.
     */
    public Iterable<LPort> getSuccessorPorts() {
        return new Iterable<LPort>() {
            public Iterator<LPort> iterator() {
            final Iterator<LEdge> edgesIter = outgoingEdges.iterator();

            return new Iterator<LPort>() {
                public boolean hasNext() {
                    return edgesIter.hasNext();
                }
                public LPort next() {
                    return edgesIter.next().getTarget();
                }
                public void remove() {
                    edgesIter.remove();
                }
            };
            }

        };
    }

    /**
     * Returns an iterable over all connected ports, both predecessors and successors.
     * 
     * @return an iterable over the connected ports
     */
    public Iterable<LPort> getConnectedPorts() {
        return Iterables.concat(getPredecessorPorts(), getSuccessorPorts());
    }
    
    /**
     * Returns the index of the port in the containing node's list of ports. Note
     * that this method has linear running time in the number of ports, so use
     * it with caution.
     * 
     * @return the index of this port, or -1 if the port has no owner
     */
    public int getIndex() {
        if (owner == null) {
            return -1;
        } else {
            return owner.getPorts().indexOf(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        String text = getName();
        if (text == null) {
            return "p_" + id;
        } else {
            return "p_" + text;
        }
    }

    private boolean edgeListsHaveChanged = true;

    private class NoteChangeList<T> extends ArrayList<T> {

        private static final long serialVersionUID = 33124633927146694L;

        public NoteChangeList(final int initialCapacity) {
            super(initialCapacity);
        }

        @Override
        public boolean add(final T e) {
            edgeListsHaveChanged = true;
            return super.add(e);
        }

        @Override
        public boolean remove(final Object o) {
            edgeListsHaveChanged = true;
            return super.remove(o);
        }

        @Override
        public boolean addAll(final Collection<? extends T> c) {
            edgeListsHaveChanged = true;
            return super.addAll(c);
        }

        @Override
        public boolean addAll(final int index, final Collection<? extends T> c) {
            edgeListsHaveChanged = true;
            return super.addAll(index, c);
        }

        @Override
        public boolean removeAll(final Collection<?> c) {
            edgeListsHaveChanged = true;
            return super.removeAll(c);
        }

        @Override
        public boolean retainAll(final Collection<?> c) {
            edgeListsHaveChanged = true;
            return super.retainAll(c);
        }

        @Override
        public void replaceAll(final UnaryOperator<T> operator) {
            edgeListsHaveChanged = true;
            super.replaceAll(operator);
        }

        @Override
        public boolean removeIf(final java.util.function.Predicate<? super T> filter) {
            edgeListsHaveChanged = true;
            return super.removeIf(filter);
        }

        @Override
        public void sort(final Comparator<? super T> c) {
            edgeListsHaveChanged = true;
            super.sort(c);
        }

        @Override
        public void clear() {
            edgeListsHaveChanged = true;
            super.clear();
        }

        @Override
        public T set(final int index, final T element) {
            edgeListsHaveChanged = true;
            return super.set(index, element);
        }

        @Override
        public void add(final int index, final T element) {
            edgeListsHaveChanged = true;
            super.add(index, element);
        }

        @Override
        public T remove(final int index) {
            edgeListsHaveChanged = true;
            return super.remove(index);
        }
    }

}