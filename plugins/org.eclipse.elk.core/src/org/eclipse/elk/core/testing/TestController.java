/*******************************************************************************
 * Copyright (c) 2018, 2019 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.core.testing;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.data.LayoutAlgorithmData;

/**
 * A test controller is used to provide listeners with a way to track the execution of a layout algorithm structured
 * into {@link ILayoutProcessor}s. The main reason for the existence of this class is that our test framework supports
 * white box tests, that is, tests that examine the internal state of a layout algorithm before or after certain
 * processors are run. Thus, the test framework uses test controllers to be notified whenever a layout processor is
 * about to be executed or has just finished executing.
 */
public class TestController {

    /** Identifier of the layout algorithm that should be tested with this controller. */
    private String layoutAlgorithmId;
    /** Listeners that want to be notified during the execution of a layout algorithm. */
    private Set<ILayoutExecutionListener> listeners = new LinkedHashSet<>();

    /**
     * Creates a new instance for testing the layout algorithm with the given ID. Each controller is created for a
     * specific layout algorithm.
     */
    public TestController(final String layoutAlgorithmId) {
        this.layoutAlgorithmId = layoutAlgorithmId;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Setup

    /**
     * Returns the identifier of the layout algorithm that should be tested with this test controller.
     */
    public String getTargetAlgorithmId() {
        return layoutAlgorithmId;
    }

    /**
     * Checks whether this test controller targets layout algorithms described by the given data object. If so, this
     * test controller can be installed on the algorithm by calling
     */
    public boolean targets(final LayoutAlgorithmData algorithmData) {
        return algorithmData.getId().equals(layoutAlgorithmId);
    }

    /**
     * Installs this test controller on the given layout provider. Before calling this method, clients should have made
     * sure that the two fit by calling {@link #getTargetAlgorithmId()}.
     * 
     * @throws IllegalArgumentException
     *             of the layout provider does not implement {@link IWhiteBoxTestable}.
     */
    public void install(final AbstractLayoutProvider layoutProvider) {
        if (layoutProvider instanceof IWhiteBoxTestable) {
            ((IWhiteBoxTestable) layoutProvider).setTestController(this);
        } else {
            throw new IllegalArgumentException(
                    "Test controllers can only be installed on white-box testable layout algorithms");
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Notification Methods

    /**
     * Notifies all listeners that a processor is about to be executed on a non-root graph.
     * 
     * @param graph
     *            the graph.
     * @param processor
     *            the processor.
     */
    public void notifyProcessorReady(final Object graph, final ILayoutProcessor<?> processor) {
        for (ILayoutExecutionListener listener : listeners) {
            listener.layoutProcessorReady(processor, graph, false);
        }
    }

    /**
     * Notifies all listeners that a processor just finished executing on a non-root graph.
     * 
     * @param graph
     *            the graph.
     * @param processor
     *            the processor.
     */
    public void notifyProcessorFinished(final Object graph, final ILayoutProcessor<?> processor) {
        for (ILayoutExecutionListener listener : listeners) {
            listener.layoutProcessorFinished(processor, graph, false);
        }
    }

    /**
     * Notifies all listeners that a processor is about to be executed on a root graph.
     * 
     * @param graph
     *            the graph.
     * @param processor
     *            the processor.
     */
    public void notifyRootProcessorReady(final Object graph, final ILayoutProcessor<?> processor) {
        for (ILayoutExecutionListener listener : listeners) {
            listener.layoutProcessorReady(processor, graph, true);
        }
    }

    /**
     * Notifies all listeners that a processor just finished executing on a root graph.
     * 
     * @param graph
     *            the graph.
     * @param processor
     *            the processor.
     */
    public void notifyRootProcessorFinished(final Object graph, final ILayoutProcessor<?> processor) {
        for (ILayoutExecutionListener listener : listeners) {
            listener.layoutProcessorFinished(processor, graph, true);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener Management

    /**
     * Adds a listener to the test controller.
     * 
     * @param listener
     *            the listener to be added.
     */
    public void addLayoutExecutionListener(final ILayoutExecutionListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener from the test controller.
     * 
     * @param listener
     *            the listener to be added.
     */
    public void removeLayoutExecutionListener(final ILayoutExecutionListener listener) {
        listeners.add(listener);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener Interfaces

    /**
     * Interface implemented by listeners interested in the execution of a layout algorithm.
     */
    public interface ILayoutExecutionListener {

        /**
         * Fired when a layout processor is about to be executed.
         * 
         * @param processor
         *            the layout processor about to be executed.
         * @param graph
         *            the graph the processor will be run on.
         * @param isRoot
         *            {@code true} if the graph is the root of the graph hierarchy laid out by the layout algorithm.
         *            This does not have to be the root of the entire graph!
         */
        void layoutProcessorReady(ILayoutProcessor<?> processor, Object graph, boolean isRoot);

        /**
         * Fired when a layout processor finished executing.
         * 
         * @param processor
         *            the layout processor about to be executed.
         * @param graph
         *            the graph the processor will be run on.
         * @param isRoot
         *            {@code true} if the graph is the root of the graph hierarchy laid out by the layout algorithm.
         *            This does not have to be the root of the entire graph!
         */
        void layoutProcessorFinished(ILayoutProcessor<?> processor, Object graph, boolean isRoot);

    }

}
