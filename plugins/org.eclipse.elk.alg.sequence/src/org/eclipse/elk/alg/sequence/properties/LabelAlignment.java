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
package org.eclipse.elk.alg.sequence.properties;

/**
 * The different message label alignment strategies.
 *  
 * @author grh
 * @kieler.design proposed grh
 * @kieler.rating proposed yellow grh
 */
public enum LabelAlignment {
    /** Centers the label between the source lifeline and its next neighbour lifeline. */
    SOURCE_CENTER,
    /** Aligns the label directly at the source lifeline. */
    SOURCE,
    /** Aligns the label in the center of its message. */
    CENTER;
}
