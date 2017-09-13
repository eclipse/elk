/*******************************************************************************
 * Copyright (c) 2017 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.graph.text.linking

import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EReference
import org.eclipse.xtext.linking.impl.Linker

import static org.eclipse.elk.graph.ElkGraphPackage.Literals.*

/**
 * Cross-reference linker for the ElkGraph language.
 */
class ElkGraphLinker extends Linker {
    
    // Set of opposite references that are _not_ considered by the ElkGraph text syntax
    static val OPPOSITE_REFS = #{
        ELK_CONNECTABLE_SHAPE__INCOMING_EDGES, ELK_CONNECTABLE_SHAPE__OUTGOING_EDGES,
        ELK_EDGE_SECTION__INCOMING_SECTIONS
    }
    
    override protected clearReference(EObject obj, EReference ref) {
        // Fix for #170, where otherwise eOpposites aren't loaded properly in elkt files
        if (!OPPOSITE_REFS.contains(ref))
            super.clearReference(obj, ref)
    }

}
