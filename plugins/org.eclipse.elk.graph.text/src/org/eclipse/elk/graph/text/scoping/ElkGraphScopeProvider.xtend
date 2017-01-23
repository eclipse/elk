/*******************************************************************************
 * Copyright (c) 2017 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.graph.text.scoping

import org.eclipse.elk.graph.ElkEdgeSection
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EReference
import org.eclipse.xtext.naming.QualifiedName
import org.eclipse.xtext.scoping.IScope

import static org.eclipse.elk.graph.ElkGraphPackage.Literals.*
import static org.eclipse.xtext.scoping.Scopes.*

/**
 * This class contains custom scoping description.
 */
class ElkGraphScopeProvider extends AbstractElkGraphScopeProvider {
    
    override getScope(EObject context, EReference reference) {
        switch reference {
            case ELK_EDGE_SECTION__OUTGOING_SECTIONS:
                getScopeForEdgeSection(context as ElkEdgeSection)
            default:
                super.getScope(context, reference)
        }
    }
    
    private def getScopeForEdgeSection(ElkEdgeSection section) {
        val edge = section.parent
        return scopeFor(edge.sections, [QualifiedName.create(identifier)], IScope.NULLSCOPE)
    }

}
