/*******************************************************************************
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.graph.text.ui.outline

import org.eclipse.elk.graph.ElkEdge
import org.eclipse.xtext.ui.editor.outline.IOutlineNode
import org.eclipse.xtext.ui.editor.outline.impl.DefaultOutlineTreeProvider

/**
 * Customization of the outline tree structure.
 */
class ElkGraphOutlineTreeProvider extends DefaultOutlineTreeProvider {
    
    def dispatch createChildren(IOutlineNode parentNode, ElkEdge edge) {
        for (label : edge.labels) {
            createEObjectNode(parentNode, label)
        }
    }
    
}
