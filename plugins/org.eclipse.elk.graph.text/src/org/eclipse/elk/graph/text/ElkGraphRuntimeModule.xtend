/*******************************************************************************
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.graph.text

import org.eclipse.elk.graph.text.conversion.ElkGraphValueConverterService
import org.eclipse.elk.graph.text.linking.ElkGraphLinker
import org.eclipse.elk.graph.text.naming.ElkGraphQualifiedNameConverter
import org.eclipse.elk.graph.text.naming.ElkGraphQualifiedNameProvider
import org.eclipse.elk.graph.text.serializer.ElkGraphTransientValueService
import org.eclipse.xtext.naming.IQualifiedNameConverter
import org.eclipse.xtext.serializer.sequencer.ITransientValueService

/**
 * Use this class to register components for the ElkGraph language.
 */
class ElkGraphRuntimeModule extends AbstractElkGraphRuntimeModule {
    
    override bindIValueConverterService() {
        ElkGraphValueConverterService
    }
    
    override bindIQualifiedNameProvider() {
        ElkGraphQualifiedNameProvider
    }
    
    def Class<? extends IQualifiedNameConverter> bindIQualifiedNameConverter() {
        ElkGraphQualifiedNameConverter
    }
    
    def Class<? extends ITransientValueService> bindITransientValueService2() {
        ElkGraphTransientValueService
    }
    
    override bindILinker() {
        ElkGraphLinker
    }
}
