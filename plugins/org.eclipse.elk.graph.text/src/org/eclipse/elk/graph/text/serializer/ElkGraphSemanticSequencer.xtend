/*******************************************************************************
 * Copyright (c) 2017 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.graph.text.serializer

import com.google.inject.Inject
import java.util.Map
import org.eclipse.elk.graph.impl.ElkPropertyToValueMapEntryImpl
import org.eclipse.elk.graph.properties.IPropertyValueProxy
import org.eclipse.elk.graph.text.services.ElkGraphGrammarAccess
import org.eclipse.xtext.conversion.ValueConverterException
import org.eclipse.xtext.conversion.impl.IDValueConverter
import org.eclipse.xtext.serializer.ISerializationContext
import org.eclipse.xtext.util.Strings

import static org.eclipse.elk.graph.ElkGraphPackage.Literals.*

class ElkGraphSemanticSequencer extends AbstractElkGraphSemanticSequencer {
    
    @Inject ElkGraphGrammarAccess grammarAccess
    IDValueConverter idValueConverter
    
    override protected sequence_Property(ISerializationContext context, Map.Entry semanticObject) {
        if (semanticObject instanceof ElkPropertyToValueMapEntryImpl) {
            val key = semanticObject.key
            val value = semanticObject.value
            if (value instanceof IPropertyValueProxy && key !== null) {
                val resolvedValue = (value as IPropertyValueProxy).resolveValue(key)
                if (resolvedValue !== null)
                    semanticObject.value = resolvedValue
            }
            if (errorAcceptor !== null && key === null) {
                errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, ELK_PROPERTY_TO_VALUE_MAP_ENTRY__KEY))
            }
            
            val feeder = createSequencerFeeder(context, semanticObject)
            feeder.accept(grammarAccess.propertyAccess.keyPropertyKeyParserRuleCall_0_0, key)
            if (value instanceof Boolean) {
                feeder.accept(grammarAccess.propertyAccess.valueBooleanValueParserRuleCall_2_3_0, value)
            } else if (value instanceof Number) {
                feeder.accept(grammarAccess.propertyAccess.valueNumberValueParserRuleCall_2_2_0, value)
            } else if (value !== null) {
                val string = value.toString
                if (string.isQuotingNecessary)
                    feeder.accept(grammarAccess.propertyAccess.valueStringValueParserRuleCall_2_0_0, value)
                else
                    feeder.accept(grammarAccess.propertyAccess.valueQualifiedIdValueParserRuleCall_2_1_0, value)
            }
            feeder.finish()
            
        } else {
            super.sequence_Property(context, semanticObject)
        }
    }
    
    private def isQuotingNecessary(String s) {
        try {
            Strings.split(s, '.').map[idValueConverter.toString(it)]
            return false
        } catch (ValueConverterException e) {
            return true
        }
    }
    
}
