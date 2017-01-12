/*******************************************************************************
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.graph.text.ui.labeling

import com.google.inject.Inject
import org.eclipse.elk.graph.ElkEdge
import org.eclipse.elk.graph.ElkLabel
import org.eclipse.elk.graph.ElkNode
import org.eclipse.elk.graph.ElkPort
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider
import org.eclipse.xtext.naming.IQualifiedNameProvider
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider

/**
 * Provides labels for ElkGraph classes.
 */
class ElkGraphLabelProvider extends DefaultEObjectLabelProvider {
    
    @Inject extension IQualifiedNameProvider

    @Inject
    new(AdapterFactoryLabelProvider delegate) {
        super(delegate)
    }

    def image(ElkNode node) {
        if (node.parent == null) {
            'elkgraph.gif'
        } else {
            'elknode.gif'
        }
    }
    
    def image(ElkPort port) {
        'elkport.gif'
    }
    
    def text(ElkEdge edge) {
        val result = new StringBuilder
        if (!edge.identifier.nullOrEmpty)
            result.append(edge.identifier).append(': ')
        edge.sources.forEach[ s, i |
            if (i > 0)
                result.append(', ')
            result.append(s.fullyQualifiedName?.toString ?: '?')
        ]
        result.append(' \u2192 ')
        edge.targets.forEach[ t, i |
            if (i > 0)
                result.append(', ')
            result.append(t.fullyQualifiedName?.toString ?: '?')
        ]
        return result.toString
    }
    
    def image(ElkEdge edge) {
        'elkedge.gif'
    }
    
    def text(ElkLabel label) {
        val result = new StringBuilder
        if (!label.identifier.nullOrEmpty)
            result.append(label.identifier).append(': ')
        result.append('"').append(label.text ?: '').append('"')
        return result.toString
    }
    
    def image(ElkLabel label) {
        'elklabel.gif'
    }
    
}
