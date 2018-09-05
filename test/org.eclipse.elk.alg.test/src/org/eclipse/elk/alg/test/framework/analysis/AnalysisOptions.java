/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework.analysis;

import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * Static definitions of options for graph analysis.
 * 
 * @deprecated This interface disappears as soon as we port GrAna to ELK.
 */
public final class AnalysisOptions {
    
    /**
     * Whether the full hierarchy of a graph should be included in the analysis.
     */
    public static final IProperty<Boolean> ANALYZE_HIERARCHY = new Property<Boolean>(
            "de.cau.cs.kieler.grana.analyzeHierarchy", true);

    /**
     * Hidden constructor to avoid instantiation.
     */
    private AnalysisOptions() {
    }

}
