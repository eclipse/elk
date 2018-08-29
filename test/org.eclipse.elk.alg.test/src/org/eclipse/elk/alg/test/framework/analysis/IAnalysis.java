/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework.analysis;

import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

/**
 * The interface all graph analysis algorithms have to implement.
 * 
 * @deprecated This interface disappears as soon as we port GrAna to ELK.
 */
public interface IAnalysis {

    /**
     * Performs the actual analysis process and returns the results. If more than one component have been specified for
     * the analysis in the extension the method is expected to return an array.
     * 
     * @param parentNode
     *            the parent node which the analysis is performed on
     * @param progressMonitor
     *            progress monitor used to keep track of progress
     * @return the analysis results
     */
    Object doAnalysis(ElkNode parentNode, IElkProgressMonitor progressMonitor);

}
