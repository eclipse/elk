/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.graphiti;

import java.util.Collection;

import org.eclipse.elk.core.service.IDiagramLayoutConnector;
import org.eclipse.elk.core.service.ILayoutConfigurationStore;
import org.eclipse.elk.core.service.ILayoutSetup;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.internal.parts.IPictogramElementEditPart;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * Layout setup for the generic Graphiti connector.
 */
public class GraphitiLayoutSetup implements ILayoutSetup {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(final Object object) {
        if (object instanceof Collection) {
            Collection<?> collection = (Collection<?>) object;
            for (Object o : collection) {
                if (o instanceof IPictogramElementEditPart || o instanceof PictogramElement) {
                    return true;
                }
            }
            return false;
        }
        return object instanceof DiagramEditor || object instanceof IPictogramElementEditPart
                || object instanceof PictogramElement;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Injector createInjector(final Module defaultModule) {
        return Guice.createInjector(Modules.override(defaultModule).with(new GraphitiLayoutModule()));
    }
    
    /**
     * Guice module for the generic Graphiti connector.
     */
    public static class GraphitiLayoutModule implements Module {

        @Override
        public void configure(final Binder binder) {
            binder.bind(IDiagramLayoutConnector.class).to(GraphitiDiagramLayoutConnector.class);
            binder.bind(ILayoutConfigurationStore.Provider.class).to(GraphitiLayoutConfigurationStore.Provider.class);
        }
        
    }
    
}
