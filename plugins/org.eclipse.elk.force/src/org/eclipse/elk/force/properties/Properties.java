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
package org.eclipse.elk.force.properties;

import java.util.Random;

import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.force.model.ForceModelStrategy;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * Property definitions for the force layouter.
 * 
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public final class Properties {
    
    /** the original object from which a graph element was created. */
    public static final IProperty<Object> ORIGIN = new Property<Object>("origin");
    
    /** random number generator for the algorithm. */
    public static final IProperty<Random> RANDOM = new Property<Random>("random");
    
    /** upper left corner of the graph's bounding box. */
    public static final IProperty<KVector> BB_UPLEFT = new Property<KVector>("boundingBox.upLeft");
    /** lower right corner of the graph's bounding box. */
    public static final IProperty<KVector> BB_LOWRIGHT = new Property<KVector>("boundingBox.lowRight");
    
    ///////////////////////////////////////////////////////////////////////////////
    // USER INTERFACE OPTIONS

    /** force model property. */
    public static final Property<ForceModelStrategy> FORCE_MODEL = new Property<ForceModelStrategy>(
            "org.eclipse.elk.force.model", ForceModelStrategy.FRUCHTERMAN_REINGOLD);
    
    /** minimal spacing between objects. */
    public static final Property<Float> SPACING = new Property<Float>(
            LayoutOptions.SPACING, 80.0f, 1.0f);
    
    /** the aspect ratio for packing connected components. */
    public static final Property<Float> ASPECT_RATIO = new Property<Float>(
            LayoutOptions.ASPECT_RATIO, 1.6f, 0.0f);

    /** priority of nodes or edges. */
    public static final Property<Integer> PRIORITY = new Property<Integer>(LayoutOptions.PRIORITY, 1);
    
    /** label spacing property. */
    public static final Property<Float> LABEL_SPACING = new Property<Float>(
            LayoutOptions.LABEL_SPACING, 5.0f, 0.0f);
    
    /** temperature property. */
    public static final Property<Float> TEMPERATURE = new Property<Float>(
            "org.eclipse.elk.force.temperature", 0.001f, 0.0f);
    
    /** iterations property. */
    public static final Property<Integer> ITERATIONS = new Property<Integer>(
            "org.eclipse.elk.force.iterations", 100, 1);
    
    /** edge repulsive power property. */
    public static final Property<Integer> EDGE_REP = new Property<Integer>(
            "org.eclipse.elk.force.repulsivePower", 0, 0);
    
    /** repulsion factor property. */
    public static final Property<Float> REPULSION = new Property<Float>(
            "org.eclipse.elk.force.repulsion", 5.0f, 0.0f);
    
    ///////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    
    /**
     * Hidden default constructor.
     */
    private Properties() {
    }

}
