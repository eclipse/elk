/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.service.internal;

import org.eclipse.elk.core.IGraphLayoutEngine;
import org.eclipse.elk.core.RecursiveGraphLayoutEngine;
import org.eclipse.elk.core.service.LayoutConnectorsService;

import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * Default bindings that are included in all injectors created by the {@link LayoutConnectorsService}.
 */
public class DefaultModule implements Module {

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(final Binder binder) {
        binder.bind(IGraphLayoutEngine.class).to(RecursiveGraphLayoutEngine.class);
    }

}
