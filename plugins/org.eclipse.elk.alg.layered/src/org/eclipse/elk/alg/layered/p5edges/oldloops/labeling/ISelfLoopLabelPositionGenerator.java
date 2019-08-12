/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.oldloops.labeling;

import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopComponent;

/**
 * Classes that implement this interface know how to generate candidate label positions for all labels of a
 * {@link OldSelfLoopComponent}.
 */
public interface ISelfLoopLabelPositionGenerator {

    /**
     * Generate a list of possible positions for labels for the given component and saves them in the component.
     */
    void generatePositions(OldSelfLoopComponent component);

}
