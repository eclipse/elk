/*******************************************************************************
 * Copyright (c) 2018 le-cds and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.core.testing;

import java.util.Collection;

import org.eclipse.elk.core.RecursiveGraphLayoutEngine;
import org.eclipse.elk.core.alg.ILayoutProcessor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * This class can be used to execute white box tests on an algorithm structured into {@link ILayoutProcessor}s. The
 * white box tests annotated with {@code @@RunBeforeProcessor} or {@code @RunAfterProcessor} specify the processor they
 * supposed to be run after or before, respectively. For each test method a {@link ILayoutPreProcessorListener} or
 * {@link ILayoutPostProcessorListener} is created and added to the list. This is done by an instance of the
 * {@code WhiteBoxRunner} JUnit test runner, which hands a {@link TestController} instance to the layout provider of the
 * algorithm to be tested through the {@link RecursiveGraphLayoutEngine}.
 * <p>
 * A layout algorithm that supports white box tests needs to notify the test controller of interesting intermediate
 * results by calling {@link #notifyProcessorReady(Object, ILayoutProcessor)} and
 * {@link #notifyProcessorFinished(Object, ILayoutProcessor)} as a processor is about to be executed or has just
 * finished executing.
 * </p>
 * <p>
 * Sometimes there are tests that should be executed only on the root graph. That can be specified through the
 * {@code isRoot} property of {@code @RunBeforeProcessor} and {@code @RunAfterProcessor}. The layout algorithm notifies
 * the controller of results on the root graph by calling {@link #notifyRootProcessorReady(Object, ILayoutProcessor)}
 * and {@link #notifyRootProcessorFinished(Object, ILayoutProcessor)}.
 * </p>
 */
public class TestController {
    
    /** Listeners that should be executed before a layout processor. */
    private Multimap<Class<? extends ILayoutProcessor<?>>, ILayoutPreProcessorListener> procPreListeners;
    /** Listeners that should be executed after a layout processor. */
    private Multimap<Class<? extends ILayoutProcessor<?>>, ILayoutPostProcessorListener> procPostListeners;
    /** Listeners that should be executed before a layout processor is executed on the root graph. */
    private Multimap<Class<? extends ILayoutProcessor<?>>, ILayoutPreProcessorListener> procRootPreListeners;
    /** Listeners that should be executed after a layout processor is executed on the root graph. */
    private Multimap<Class<? extends ILayoutProcessor<?>>, ILayoutPostProcessorListener> procRootPostListeners;

    /**
     * Creates a new instance to test a layout algorithm with.
     */
    public TestController() {
        procPreListeners = HashMultimap.create();
        procPostListeners = HashMultimap.create();
        procRootPreListeners = HashMultimap.create();
        procRootPostListeners = HashMultimap.create();
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Notification Methods

    /**
     * Notifies all the ILayoutPreProcessorListeners waiting for the actual processor.
     * 
     * @param lgraph
     *            - the actual graph before the processor started execution
     * @param processor
     *            - the processor that is about to be executed
     */
    @SuppressWarnings("unchecked") // a cast with a generic type leads to a warning
    public void notifyProcessorReady(final Object lgraph, final ILayoutProcessor<?> processor) {
        Collection<ILayoutPreProcessorListener> listeners = procPreListeners.get(
                (Class<? extends ILayoutProcessor<?>>) processor.getClass());
        
        for (ILayoutPreProcessorListener listener : listeners) {
            listener.layoutProcessorReady(lgraph, processor);
        }
    }

    /**
     * Notifies all the ILayoutPreProcessorListeners waiting for the actual processor to finish.
     * 
     * @param lgraph
     *            - the actual graph after the processor finished its execution
     * @param processor
     *            - the processor that has finished its execution
     */
    @SuppressWarnings("unchecked") // a cast with a generic type leads to a warning
    public void notifyProcessorFinished(final Object lgraph, final ILayoutProcessor<?> processor) {
        Collection<ILayoutPostProcessorListener> listeners = procPostListeners.get(
                (Class<? extends ILayoutProcessor<?>>) processor.getClass());
        
        for (ILayoutPostProcessorListener listener : listeners) {
            listener.layoutProcessorFinished(lgraph, processor);
        }
    }

    /**
     * Notifies all the ILayoutPreProcessorListeners waiting for the actual processor to be ready to run on the root
     * graph.
     * 
     * @param lgraph
     *            - the actual graph before the processor started execution
     * @param processor
     *            - the processor that is about to be executed
     */
    @SuppressWarnings("unchecked") // a cast with a generic type leads to a warning
    public void notifyRootProcessorReady(final Object lgraph, final ILayoutProcessor<?> processor) {
        Collection<ILayoutPreProcessorListener> listeners = procRootPreListeners.get(
                (Class<? extends ILayoutProcessor<?>>) processor.getClass());
        
        for (ILayoutPreProcessorListener listener : listeners) {
            listener.layoutProcessorReady(lgraph, processor);
        }
    }

    /**
     * Notifies all the ILayoutPreProcessorListeners waiting for the actual processor to finish its execution on the
     * root graph.
     * 
     * @param lgraph
     *            - the actual graph after the processor finished its execution
     * @param processor
     *            - the processor that has finished its execution
     */
    @SuppressWarnings("unchecked") // a cast with a generic type leads to a warning
    public void notifyRootProcessorFinished(final Object lgraph, final ILayoutProcessor<?> processor) {
        Collection<ILayoutPostProcessorListener> listeners = procRootPostListeners.get(
                (Class<? extends ILayoutProcessor<?>>) processor.getClass());
        
        for (ILayoutPostProcessorListener listener : listeners) {
            listener.layoutProcessorFinished(lgraph, processor);
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener Management

    /**
     * Adds a ILayoutPreProcessorListener that should be notified.
     * 
     * @param listener
     *            - the listener that has to be notified
     * @param processor
     *            - the processor the test should be executed before
     * @param onRoot
     *            - true, if the test should be executed only on the root
     */
    public void addPreProcessorRunListener(final ILayoutPreProcessorListener listener,
            final Class<? extends ILayoutProcessor<?>> processor, final boolean onRoot) {
        if (onRoot) {
            procRootPreListeners.put(processor, listener);
        } else {
            procPreListeners.put(processor, listener);
        }

    }

    /**
     * Adds a ILayoutPreProcessorListener that should be notified.
     * 
     * @param listener
     *            - the listener that has to be notified
     * @param processor
     *            - the processor the test should be executed after
     * @param onRoot
     *            - true, if the test should be executed only on the root
     */
    public void addPostProcessorRunListener(final ILayoutPostProcessorListener listener,
            final Class<? extends ILayoutProcessor<?>> processor, final boolean onRoot) {

        if (onRoot) {
            procRootPostListeners.put(processor, listener);
        } else {
            procPostListeners.put(processor, listener);
        }

    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener Interfaces

    /**
     * A listener that wants to be notified before a layout processor is executed.
     */
    public interface ILayoutPreProcessorListener {
        
        /**
         * Called before the given layout processor is about to be run on the given graph.
         */
        void layoutProcessorReady(Object graph, ILayoutProcessor<?> processor);
        
    }

    /**
     * A listener that wants to be notified after a layout processor is executed.
     */
    public interface ILayoutPostProcessorListener {

        /**
         * Called after the given layout processor was run on the given graph.
         */
        void layoutProcessorFinished(Object graph, ILayoutProcessor<?> processor);
        
    }
    
}
