/*******************************************************************************
 * Copyright (c) 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p3order.counting;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.core.options.PortSide;

import com.google.common.collect.Lists;

/**
 * Utility class for iterating over ports in any direction.
 * 
 * @author alan
 */
public final class PortIterable implements Iterable<LPort> {
    private final PortSide side;
    private final LNode node;
    private final PortOrder order;
    
    /**
     * Iterate over ports in north to south and east to west order.
     * 
     * @param node
     *            whose ports are being considered.
     * @param side
     *            of the node we are interested in
     * @return Iterable for ports on given node and side in given order.
     */
    public static List<LPort> listInNorthSouthEastWestOrder(final LNode node, final PortSide side) {
        return createListView(side, node, PortOrder.NORTHSOUTH_EASTWEST);
    }

    /**
     * Iterate over ports in clockwise order.
     * 
     * @param node
     *            whose ports are being considered.
     * @param side
     *            of the node we are interested in
     * @return Iterable for ports on given node and side in given order.
     */
    public static List<LPort> listInClockwiseOrder(final LNode node, final PortSide side) {
        return createListView(side, node, PortOrder.CLOCKWISE);
    }

    /**
     * Iterate over ports in counter-clockwise order.
     * 
     * @param node
     *            whose ports are being considered.
     * @param side
     *            of the node we are interested in
     * @return Iterable for ports on given node and side in given order.
     */
    public static List<LPort> listInCounterClockwiseOrder(final LNode node, final PortSide side) {
        return createListView(side, node, PortOrder.COUNTER_CLOCKWISE);
    }

    private static List<LPort> createListView(final PortSide side, final LNode node, final PortOrder order) {
        final List<LPort> ports = node.getPorts(side);
        if (counterClockwise(side, order)) {
            return Lists.reverse(ports);
        } else {
            return ports;
        }
    }

    private static boolean counterClockwise(final PortSide side, final PortOrder order) {
        return order == PortOrder.COUNTER_CLOCKWISE || order == PortOrder.NORTHSOUTH_EASTWEST
                && (side == PortSide.SOUTH || side == PortSide.WEST);
    }
    
    /**
     * Choose the order in which the ports are to be returned in using this enum.
     * 
     * @author alan
     *
     */
    private static enum PortOrder {
        CLOCKWISE, COUNTER_CLOCKWISE, NORTHSOUTH_EASTWEST
    }

    private PortIterable(final LNode node, final PortSide side, final PortOrder order) {
        this.node = node;
        this.side = side;
        this.order = order;
    }

    /**
     * Iterate over ports in north to south and east to west order.
     * 
     * @param node
     *            whose ports are being considered.
     * @param side
     *            of the node we are interested in
     * @return Iterable for ports on given node and side in given order.
     */
    public static Iterable<LPort> inNorthSouthEastWestOrder(final LNode node, final PortSide side) {
        // TODO-alan think about this whole class.
        switch (side) {
        case EAST:
        case NORTH:
            return node.getPorts(side);
        case SOUTH:
        case WEST:
            return Lists.reverse(node.getPorts(side));// new PortIterable(node, side, PortOrder.NORTHSOUTH_EASTWEST);
        }
        return Collections.emptyList();
    }

    /**
     * Iterate over ports in clockwise order.
     * 
     * @param node
     *            whose ports are being considered.
     * @param side
     *            of the node we are interested in
     * @return Iterable for ports on given node and side in given order.
     */
    public static Iterable<LPort> inClockwiseOrder(final LNode node, final PortSide side) {
        return new PortIterable(node, side, PortOrder.CLOCKWISE);
    }

    /**
     * Iterate over ports in counter-clockwise order.
     * 
     * @param node
     *            whose ports are being considered.
     * @param side
     *            of the node we are interested in
     * @return Iterable for ports on given node and side in given order.
     */
    public static Iterable<LPort> inCounterClockwiseOrder(final LNode node, final PortSide side) {
        return new PortIterable(node, side, PortOrder.COUNTER_CLOCKWISE);
    }
    
    @Override
    public Iterator<LPort> iterator() {
        final List<LPort> ports = node.getPorts(side);
        switch (order) {
        case CLOCKWISE:
            return ports.iterator();
        case COUNTER_CLOCKWISE:
            return getCCWIterator(ports);
        case NORTHSOUTH_EASTWEST:
            switch (side) {
            case EAST:
            case NORTH:
                return ports.iterator();
            case SOUTH:
            case WEST:
                return getCCWIterator(ports);
            }
        }
        throw new UnsupportedOperationException("PortOrder not implemented.");
    }

    private Iterator<LPort> getCCWIterator(final List<LPort> ports) {
        Iterator<LPort> iterator = new Iterator<LPort>() {
            private final ListIterator<LPort> listIterator = ports.listIterator(ports.size());

            @Override
            public boolean hasNext() {
                return listIterator.hasPrevious();
            }

            @Override
            public LPort next() {
                return listIterator.previous();
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return iterator;
    }
}
