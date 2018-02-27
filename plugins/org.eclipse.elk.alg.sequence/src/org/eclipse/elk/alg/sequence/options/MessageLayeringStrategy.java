/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.sequence.options;

import org.eclipse.elk.alg.sequence.SequencePhases;
import org.eclipse.elk.alg.sequence.graph.LayoutContext;
import org.eclipse.elk.alg.sequence.p4layering.MessageLayerer;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.ILayoutPhaseFactory;

/**
 * Definition of available message layering strategies for the sequence diagram layouter.
 */
public enum MessageLayeringStrategy implements ILayoutPhaseFactory<SequencePhases, LayoutContext> {

    /** The only message layering implementation. */
    DEFAULT;

    
    @Override
    public ILayoutPhase<SequencePhases, LayoutContext> create() {
        switch (this) {
        case DEFAULT:
            return new MessageLayerer();
            
        default:
            throw new IllegalArgumentException(
                    "No implementation is available for the message layerer " + this.toString());
        }
    }
    
}
