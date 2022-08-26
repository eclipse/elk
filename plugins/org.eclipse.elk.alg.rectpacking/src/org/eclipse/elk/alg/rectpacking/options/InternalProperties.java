/*******************************************************************************
 * Copyright (c) 2022 Kiel Univerity and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.rectpacking.options;

import java.util.List;

import org.eclipse.elk.alg.rectpacking.util.RectRow;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * @author sdo
 *
 */
public final class InternalProperties {

    /** Additional height of the drawing that is to be added during expansion. */
    public static final IProperty<Double> ADDITIONAL_HEIGHT = new Property<Double>("additionalHeight");
    /** The height of the parent. */
    public static final IProperty<Double> DRAWING_HEIGHT = new Property<Double>("drawingHeight");
    /** The width of the parent. */
    public static final IProperty<Double> DRAWING_WIDTH = new Property<Double>("drawingWidth");

    /** The minimum height of the parent. */
    public static final IProperty<Double> MIN_HEIGHT = new Property<Double>("minHeight");
    /** The minimum width of the parent. */
    public static final IProperty<Double> MIN_WIDTH = new Property<Double>("minWidth");
    
    /** The rows formed by the simple placement. */
    public static final IProperty<List<RectRow>> ROWS = new Property<List<RectRow>>("rows");

    /** The target width of the graph. */
    public static final IProperty<Double> TARGET_WIDTH = new Property<Double>("targetWidth");

}
