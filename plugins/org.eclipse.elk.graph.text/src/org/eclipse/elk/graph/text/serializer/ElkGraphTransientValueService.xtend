/*******************************************************************************
 * Copyright (c) 2017 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.graph.text.serializer

import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.xtext.serializer.sequencer.LegacyTransientValueService

import static org.eclipse.elk.graph.ElkGraphPackage.Literals.*

class ElkGraphTransientValueService extends LegacyTransientValueService {
    
    override isListTransient(EObject semanticObject, EStructuralFeature feature) {
        switch feature {
            case ELK_CONNECTABLE_SHAPE__INCOMING_EDGES,
            case ELK_CONNECTABLE_SHAPE__OUTGOING_EDGES,
            case ELK_EDGE_SECTION__INCOMING_SECTIONS:
                return ListTransient.YES
            default:
                return super.isListTransient(semanticObject, feature)
        }
    }
    
}