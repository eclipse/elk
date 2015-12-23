/*******************************************************************************
 * Copyright (c) 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    spoenemann - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core;

import org.eclipse.elk.core.options.GraphFeature;
import org.eclipse.elk.core.util.InstancePool;

public interface ILayoutAlgorithmData {
    
    InstancePool<AbstractLayoutProvider> getInstancePool();
    
    boolean supportsFeature(GraphFeature graphFeature);

}
