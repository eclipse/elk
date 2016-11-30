/*******************************************************************************
 * Copyright (c) 2011, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.eclipse.elk.alg.layered.intermediate.IntermediateProcessorStrategy;


/**
 * A strategy for intermediate layout processors to be used.
 * 
 * <p>Layout phases use instances of this class to specify which intermediate layout
 * processors they depend on. The layout provider uses this class to keep track of
 * which intermediate layout processors must be inserted into the layout algorithm
 * workflow.</p>
 * 
 * <p>Intermediate processing configurations are created through the two static creation
 * methods:</p>
 * <ol>
 *   <li>
 *     {@link #createEmpty()}
 *     creates a new empty processing configuration.
 *   </li>
 *   <li>
 *     {@link #fromExisting(IntermediateProcessingConfiguration)}
 *     creates a new processing configuration that is a copy of an existing configuration.
 *   </li>
 * </ol>
 * 
 * <p>The different add methods can then be used to add additional processors to a
 * configuration. The methods are designed to support method chaining:</p>
 * <pre>
 * IntermediateProcessingConfiguration.createEmpty()
 *      .addBeforePhase3(LayoutProcessorStrategy.STRANGE_PORT_SIDE_PROCESSOR)
 *      .addAfterPhase5(LayoutProcessorStrategy.SOME_OTHER_PROCESSOR)
 *      .addAfterPhase5(LayoutProcessorStrategy.YET_ANOTHER_PROCESSOR);
 * </pre>
 * 
 * @author cds
 * @kieler.design proposed by cds
 * @kieler.rating yellow 2014-11-09 review KI-56 by chsch, als
 */
public final class IntermediateProcessingConfiguration {
    
    /**
     * Enumeration of the different available intermediate processing slots.
     * 
     * @author cds
     */
    public static enum Slot {
        /** Intermediate processing slot before phase 1. */
        BEFORE_PHASE_1,
        /** Intermediate processing slot before phase 2. */
        BEFORE_PHASE_2,
        /** Intermediate processing slot before phase 3. */
        BEFORE_PHASE_3,
        /** Intermediate processing slot before phase 4. */
        BEFORE_PHASE_4,
        /** Intermediate processing slot before phase 5. */
        BEFORE_PHASE_5,
        /** Intermediate processing slot after phase 5. */
        AFTER_PHASE_5;
        
        /** The number of intermediate processing slots defined in here. */
        public static final int SLOT_COUNT = AFTER_PHASE_5.ordinal() + 1;

        /**
         * This is used to prevent hierarchical processors after phase 4.
         * 
         * @return whether all processors in this area will always be non-topological.
         */
        public boolean nonTopological() {
            return this == BEFORE_PHASE_5 || this == AFTER_PHASE_5;
        }
    }
    
    
    /** Array of sets describing which processors this strategy is composed of. */
    private List<Set<IntermediateProcessorStrategy>> strategy = 
        new ArrayList<Set<IntermediateProcessorStrategy>>(Slot.SLOT_COUNT);
    
    
    /**
     * Constructs a new empty strategy.
     */
    private IntermediateProcessingConfiguration() {
        for (int i = 0; i < Slot.SLOT_COUNT; i++) {
            strategy.add(EnumSet.noneOf(IntermediateProcessorStrategy.class));
        }
    }
    
    /**
     * Creates and returns a new empty processing configuration.
     * 
     * @return the created configuration.
     */
    public static IntermediateProcessingConfiguration createEmpty() {
        return new IntermediateProcessingConfiguration();
    }
    
    /**
     * Creates and returns a new processing configuration that is a copy of the given configuration.
     * 
     * @param existing an existing processing configuration.
     * @return a new configuration that equals the existing configuration.
     */
    public static IntermediateProcessingConfiguration fromExisting(
            final IntermediateProcessingConfiguration existing) {
        
        IntermediateProcessingConfiguration configuration = new IntermediateProcessingConfiguration();
        configuration.addAll(existing);
        return configuration;
    }
    
    
    /**
     * Returns the layout processors in the given slot. The returned set is an unmodifiable view of the
     * set of intermediate processors. Note that iterating over the returned {@code Set} will iterate
     * over the elements in the natural order in which they occur in the original enumeration. That
     * natural order is in turn just the order in which they must be executed to satisfy all
     * dependencies.
     * 
     * @param slot the slot whose intermediate processors to return.
     * @return the slot's unmodifiable set of layout processors.
     */
    public Set<IntermediateProcessorStrategy> getProcessors(final Slot slot) {
        return Collections.unmodifiableSet(strategy.get(slot.ordinal()));
    }
    
    /**
     * Adds the given intermediate processor to the slot before phase 1, if not already present.
     * 
     * @param processor the processor to add.
     * @return this strategy.
     */
    public IntermediateProcessingConfiguration addBeforePhase1(
            final IntermediateProcessorStrategy processor) {
        
        strategy.get(Slot.BEFORE_PHASE_1.ordinal()).add(processor);
        return this;
    }
    
    /**
     * Adds the given intermediate processor to the slot before phase 2, if not already present.
     * 
     * @param processor the processor to add.
     * @return this strategy.
     */
    public IntermediateProcessingConfiguration addBeforePhase2(
            final IntermediateProcessorStrategy processor) {
        
        strategy.get(Slot.BEFORE_PHASE_2.ordinal()).add(processor);
        return this;
    }
    
    /**
     * Adds the given intermediate processor to the slot before phase 3, if not already present.
     * 
     * @param processor the processor to add.
     * @return this strategy.
     */
    public IntermediateProcessingConfiguration addBeforePhase3(
            final IntermediateProcessorStrategy processor) {
        
        strategy.get(Slot.BEFORE_PHASE_3.ordinal()).add(processor);
        return this;
    }
    
    /**
     * Adds the given intermediate processor to the slot before phase 4, if not already present.
     * 
     * @param processor the processor to add.
     * @return this strategy.
     */
    public IntermediateProcessingConfiguration addBeforePhase4(
            final IntermediateProcessorStrategy processor) {
        
        strategy.get(Slot.BEFORE_PHASE_4.ordinal()).add(processor);
        return this;
    }
    
    /**
     * Adds the given intermediate processor to the slot before phase 5, if not already present.
     * 
     * @param processor the processor to add.
     * @return this strategy.
     */
    public IntermediateProcessingConfiguration addBeforePhase5(
            final IntermediateProcessorStrategy processor) {
        
        strategy.get(Slot.BEFORE_PHASE_5.ordinal()).add(processor);
        return this;
    }
    
    /**
     * Adds the given intermediate processor to the slot after phase 5, if not already present.
     * 
     * @param processor the processor to add.
     * @return this strategy.
     */
    public IntermediateProcessingConfiguration addAfterPhase5(
            final IntermediateProcessorStrategy processor) {
        
        strategy.get(Slot.AFTER_PHASE_5.ordinal()).add(processor);
        return this;
    }
    
    /**
     * Adds the given layout processors to the given slot, if not already present.
     * 
     * @param slot the slot to add the intermediate processors to.
     * @param processors the layout processors to add. May be {@code null}.
     * @return this strategy.
     */
    public IntermediateProcessingConfiguration addAll(final Slot slot,
            final Collection<IntermediateProcessorStrategy> processors) {
        
        if (processors != null) {
            strategy.get(slot.ordinal()).addAll(processors);
        }
        
        return this;
    }
    
    /**
     * Adds the items from the given strategy to this strategy, if not already present.
     * 
     * @param operand the strategy to unify this strategy with. May be {@code null}.
     * @return this strategy.
     */
    public IntermediateProcessingConfiguration addAll(
            final IntermediateProcessingConfiguration operand) {
        
        if (operand != null) {
            for (int i = 0; i < Slot.SLOT_COUNT; i++) {
                strategy.get(i).addAll(operand.strategy.get(i));
            }
        }
        
        return this;
    }
    
    /**
     * Removes the given layout processor from the given slot.
     * 
     * @param slot the slot to remove the intermediate processor from.
     * @param processor the layout processor to add.
     */
    public void removeLayoutProcessor(final Slot slot, final IntermediateProcessorStrategy processor) {
        strategy.get(slot.ordinal()).remove(processor);
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
