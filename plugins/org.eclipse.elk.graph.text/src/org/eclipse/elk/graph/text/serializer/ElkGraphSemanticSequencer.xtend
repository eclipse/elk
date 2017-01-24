/*******************************************************************************
 * Copyright (c) 2017 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.graph.text.serializer

import java.util.Map
import org.eclipse.elk.graph.impl.ElkPropertyToValueMapEntryImpl
import org.eclipse.xtext.serializer.ISerializationContext
import org.eclipse.elk.graph.properties.IPropertyValueProxy

class ElkGraphSemanticSequencer extends AbstractElkGraphSemanticSequencer {
    
    override protected sequence_Property(ISerializationContext context, Map.Entry semanticObject) {
        if (semanticObject instanceof ElkPropertyToValueMapEntryImpl) {
            val value = semanticObject.value
            if (value instanceof IPropertyValueProxy) {
                val resolvedValue = value.resolveValue(semanticObject.key)
                if (resolvedValue !== null)
                    semanticObject.value = resolvedValue
            }
        }
        super.sequence_Property(context, semanticObject)
    }
    
}
