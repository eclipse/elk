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
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider

/**
 * Provides labels for EObjects.
 * 
 * See https://www.eclipse.org/Xtext/documentation/304_ide_concepts.html#label-provider
 */
class ElkGraphLabelProvider extends DefaultEObjectLabelProvider {

    @Inject
    new(AdapterFactoryLabelProvider delegate) {
        super(delegate);
    }

    // Labels can be computed like this:
    //  def text(Greeting ele) {
    //      'A greeting to ' + ele.name
    //  }
    
    def image(ElkNode node) {
        if (node.parent == null) {
            'elkgraph.gif';
        } else {
            'elknode.gif';
        }
    }
    
    def image(ElkPort port) {
        'elkport.gif';
    }
    
    def image(ElkEdge edge) {
        'elkedge.gif';
    }
    
    def image(ElkLabel label) {
        'elklabel.gif';
    }
}
