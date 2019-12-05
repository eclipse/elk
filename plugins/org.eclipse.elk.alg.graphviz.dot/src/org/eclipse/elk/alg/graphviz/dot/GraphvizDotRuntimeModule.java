/*******************************************************************************
 * Copyright (c) 2011, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.graphviz.dot;


import org.eclipse.elk.alg.graphviz.dot.serializer.GraphvizDotSerializer;
import org.eclipse.xtext.serializer.ISerializer;

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 * 
 * @author msp
 */
public class GraphvizDotRuntimeModule extends AbstractGraphvizDotRuntimeModule {
    
    @Override
    public Class<? extends ISerializer> bindISerializer() {
        return GraphvizDotSerializer.class;
    }
    
}
