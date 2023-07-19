/*******************************************************************************
 * Copyright (c) 2023 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.radial.options;

import org.eclipse.elk.alg.radial.intermediate.rotation.AngleRotation;
import org.eclipse.elk.alg.radial.intermediate.rotation.IRadialRotator;

/**
 * The list of selectable rotation algorithms.
 *
 */
public enum RotationStrategy {
    
    /** No rotation. **/
    NONE,
    /** Rotate to a given angle. */
    ROTATE_TO_ANGLE;
    
    /**
     * Instantiate the chosen rotation strategy.
     * 
     * @return A rotator.
     */
    public IRadialRotator create() {
        switch (this) {
        case ROTATE_TO_ANGLE:
            return new AngleRotation();
        default:
            throw new IllegalArgumentException(
                    "No implementation is available for the layout option " + this.toString());
        }
    }
}
