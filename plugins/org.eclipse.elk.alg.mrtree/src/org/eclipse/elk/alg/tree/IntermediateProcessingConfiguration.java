/*******************************************************************************
 * Copyright (c) 2013, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.eclipse.elk.alg.tree.intermediate.IntermediateProcessorStrategy;

/**
 * A strategy for intermediate layout processors to be used.
 * 
 * Layout algorithms use this processor between phases to do small computations, which are need for
 * phases.
 * 
 * If a processor is defined multiple times between a phase, it is invoked only once.
 * 
 * @author sor
 * @author sgu
 */
public final class IntermediateProcessingConfiguration {

    /** Constant for the processors that should come before phase 1. */
    public static final int BEFORE_PHASE_1 = 0;
    /** Constant for the processors that should come after phase 1. */
    public static final int AFTER_PHASE_1 = 1;
    /** Constant for the processors that should come before phase 2. */
    public static final int BEFORE_PHASE_2 = 1;
    /** Constant for the processors that should come after phase 2. */
    public static final int AFTER_PHASE_2 = 2;
    /** Constant for the processors that should come before phase 3. */
    public static final int BEFORE_PHASE_3 = 2;
    /** Constant for the processors that should come after phase 3. */
    public static final int AFTER_PHASE_3 = 3;
    /** Constant for the processors that should come before phase 4. */
    public static final int BEFORE_PHASE_4 = 3;
    /** Constant for the processors that should come after phase 4. */
    public static final int AFTER_PHASE_4 = 4;
    /** How many slots there are for intermediate processing. */
    public static final int INTERMEDIATE_PHASE_SLOTS = 5;

    /** Array of sets describing which processors this strategy is composed of. */
    private List<Set<IntermediateProcessorStrategy>> strategy =
            new ArrayList<Set<IntermediateProcessorStrategy>>(INTERMEDIATE_PHASE_SLOTS);

    /**
     * Constructs a new empty strategy.
     */
    public IntermediateProcessingConfiguration() {
        for (int i = 0; i < INTERMEDIATE_PHASE_SLOTS; i++) {
            strategy.add(EnumSet.noneOf(IntermediateProcessorStrategy.class));
        }
    }

    /**
     * Constructs a new strategy as a copy of the given strategy.
     * 
     * @param init
     *            the strategy to copy.
     */
    public IntermediateProcessingConfiguration(final IntermediateProcessingConfiguration init) {
        for (int i = 0; i < INTERMEDIATE_PHASE_SLOTS; i++) {
            strategy.add(EnumSet.copyOf(init.strategy.get(i)));
        }
    }

    /**
     * Constructs a new strategy that has the given processor in the given slot, but is otherwise
     * empty.
     * 
     * @param slotIndex
     *            the slot index. Must be {@code >= 0} and {@code < INTERMEDIATE_PHASE_SLOTS}.
     * @param processor
     *            a single layout processor to add.
     */
    public IntermediateProcessingConfiguration(final int slotIndex,
            final IntermediateProcessorStrategy processor) {

        this();

        addAll(slotIndex, EnumSet.of(processor));
    }

    /**
     * Constructs a new strategy that has the given processors in the given slot, but is otherwise
     * empty.
     * 
     * @param slotIndex
     *            the slot index. Must be {@code >= 0} and {@code < INTERMEDIATE_PHASE_SLOTS}.
     * @param processors
     *            a collection of layout processors to add.
     */
    public IntermediateProcessingConfiguration(final int slotIndex,
            final Collection<IntermediateProcessorStrategy> processors) {

        this();

        addAll(slotIndex, processors);
    }

    /**
     * Constructs a new strategy containing the given intermediate layout processors.
     * 
     * @param beforePhase1
     *            collection of layout processors before phase 1. May be {@code null}.
     * @param beforePhase2
     *            collection of layout processors before phase 2. May be {@code null}.
     */
    public IntermediateProcessingConfiguration(
            final Collection<IntermediateProcessorStrategy> beforePhase1,
            final Collection<IntermediateProcessorStrategy> beforePhase2) {

        this();

        addAll(BEFORE_PHASE_1, beforePhase1).addAll(BEFORE_PHASE_2, beforePhase2);
    }

    /**
     * Constructs a new strategy containing the given intermediate layout processors.
     * 
     * @param beforePhase1
     *            collection of layout processors before phase 1. May be {@code null}.
     * @param beforePhase2
     *            collection of layout processors before phase 2. May be {@code null}.
     * @param beforePhase3
     *            collection of layout processors before phase 3. May be {@code null}.
     */
    public IntermediateProcessingConfiguration(
            final Collection<IntermediateProcessorStrategy> beforePhase1,
            final Collection<IntermediateProcessorStrategy> beforePhase2,
            final Collection<IntermediateProcessorStrategy> beforePhase3) {

        this();

        addAll(BEFORE_PHASE_1, beforePhase1).addAll(BEFORE_PHASE_2, beforePhase2).addAll(
                BEFORE_PHASE_3, beforePhase3);
    }

    /**
     * Constructs a new strategy containing the given intermediate layout processors.
     * 
     * @param beforePhase1
     *            collection of layout processors before phase 1. May be {@code null}.
     * @param beforePhase2
     *            collection of layout processors before phase 2. May be {@code null}.
     * @param beforePhase3
     *            collection of layout processors before phase 3. May be {@code null}.
     * @param beforePhase4
     *            collection of layout processors before phase 4. May be {@code null}.
     */
    public IntermediateProcessingConfiguration(
            final Collection<IntermediateProcessorStrategy> beforePhase1,
            final Collection<IntermediateProcessorStrategy> beforePhase2,
            final Collection<IntermediateProcessorStrategy> beforePhase3,
            final Collection<IntermediateProcessorStrategy> beforePhase4) {

        this();

        addAll(BEFORE_PHASE_1, beforePhase1).addAll(BEFORE_PHASE_2, beforePhase2)
                .addAll(BEFORE_PHASE_3, beforePhase3).addAll(BEFORE_PHASE_4, beforePhase4);
    }

    /**
     * Returns the layout processors in the given slot. Modifications of the returned set do not
     * result in modifications of this strategy. Note that iterating over the returned
     * {@code EnumSet} will iterate over the elements in the natural order in which they occur in
     * the original enumeration. That natural order is in turn just the order in which they must be
     * executed to satisfy all dependencies.
     * 
     * @param slotIndex
     *            the slot index. Must be {@code >= 0} and {@code < INTERMEDIATE_PHASE_SLOTS}.
     * @return the slot's set of layout processors.
     */
    public EnumSet<IntermediateProcessorStrategy> getProcessors(final int slotIndex) {
        if (slotIndex < 0 || slotIndex >= INTERMEDIATE_PHASE_SLOTS) {
            throw new IllegalArgumentException("slotIndex must be >= 0 and < "
                    + INTERMEDIATE_PHASE_SLOTS + ".");
        }

        return EnumSet.copyOf(strategy.get(slotIndex));
    }

    /**
     * Adds the given layout processor to the given slot, if it's not already in there.
     * 
     * @param slotIndex
     *            the slot index. Must be {@code >= 0} and {@code < INTERMEDIATE_PHASE_SLOTS}.
     * @param processor
     *            the layout processor to add.
     */
    public void addLayoutProcessor(final int slotIndex, final IntermediateProcessorStrategy processor) {
        if (slotIndex < 0 || slotIndex >= INTERMEDIATE_PHASE_SLOTS) {
            throw new IllegalArgumentException("slotIndex must be >= 0 and < "
                    + INTERMEDIATE_PHASE_SLOTS + ".");
        }

        strategy.get(slotIndex).add(processor);
    }

    /**
     * Adds all layout processors in the given collection to the given slot, without duplicates.
     * 
     * @param slotIndex
     *            the slot index. Must be {@code >= 0} and {@code < INTERMEDIATE_PHASE_SLOTS}.
     * @param processors
     *            the layout processors to add. May be {@code null}.
     * @return this strategy.
     */
    public IntermediateProcessingConfiguration addAll(final int slotIndex,
            final Collection<IntermediateProcessorStrategy> processors) {

        if (slotIndex < 0 || slotIndex >= INTERMEDIATE_PHASE_SLOTS) {
            throw new IllegalArgumentException("slotIndex must be >= 0 and < "
                    + INTERMEDIATE_PHASE_SLOTS + ".");
        }

        if (processors != null) {
            strategy.get(slotIndex).addAll(processors);
        }

        return this;
    }

    /**
     * Adds the items from the given strategy to this strategy. The different intermediate
     * processing slots will have duplicate processors removed.
     * 
     * @param operand
     *            the strategy to unify this strategy with. May be {@code null}.
     * @return this strategy.
     */
    public IntermediateProcessingConfiguration addAll(
            final IntermediateProcessingConfiguration operand) {

        if (operand != null) {
            for (int i = 0; i < INTERMEDIATE_PHASE_SLOTS; i++) {
                strategy.get(i).addAll(operand.strategy.get(i));
            }
        }

        return this;
    }

    /**
     * Removes the given layout processor from the given slot.
     * 
     * @param slotIndex
     *            the slot index. Must be {@code >= 0} and {@code < INTERMEDIATE_PHASE_SLOTS}.
     * @param processor
     *            the layout processor to add.
     */
    public void removeLayoutProcessor(final int slotIndex,
            final IntermediateProcessorStrategy processor) {
        
        if (slotIndex < 0 || slotIndex >= INTERMEDIATE_PHASE_SLOTS) {
            throw new IllegalArgumentException("slotIndex must be >= 0 and < "
                    + INTERMEDIATE_PHASE_SLOTS + ".");
        }

        strategy.get(slotIndex).remove(processor);
    }

    /**
     * Clears this strategy.
     */
    public void clear() {
        for (Set<IntermediateProcessorStrategy> set : strategy) {
            set.clear();
        }
    }
}
