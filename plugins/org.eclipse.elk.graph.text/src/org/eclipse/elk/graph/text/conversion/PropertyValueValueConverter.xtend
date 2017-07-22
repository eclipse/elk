/*******************************************************************************
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.graph.text.conversion

import com.google.inject.Inject
import org.eclipse.elk.core.data.LayoutMetaDataService
import org.eclipse.elk.core.data.LayoutOptionData
import org.eclipse.elk.core.util.internal.LayoutOptionProxy
import org.eclipse.elk.graph.impl.ElkPropertyToValueMapEntryImpl
import org.eclipse.elk.graph.properties.IProperty
import org.eclipse.elk.graph.text.naming.ElkGraphQualifiedNameConverter
import org.eclipse.xtext.conversion.ValueConverterException
import org.eclipse.xtext.conversion.impl.AbstractValueConverter
import org.eclipse.xtext.naming.QualifiedName
import org.eclipse.xtext.nodemodel.INode
import org.eclipse.xtext.util.Strings

class PropertyValueValueConverter extends AbstractValueConverter<Object> {
    
    @Inject ElkGraphQualifiedNameConverter qualifiedNameConverter
    
    override toString(Object value) throws ValueConverterException {
        if (value instanceof Double && Math.floor(value as Double) == value as Double)
            return Integer.toString((value as Double).intValue)
        else if (value instanceof Boolean || value instanceof Number || value instanceof Enum<?>)
            return value.toString
        else
            return String.valueOf(value).quoteIfNecessary
    }
    
    private def quoteIfNecessary(String s) {
        try {
            val qname = QualifiedName.create(Strings.split(s, ElkGraphQualifiedNameConverter.DELIMITER))
            return qualifiedNameConverter.toString(qname)
        } catch (ValueConverterException e) {
            return '"' + s + '"'
        }
    }
    
    override toValue(String string, INode node) throws ValueConverterException {
        if (!string.nullOrEmpty) {
            val unquotedString = string.unquoteIfNecessary
            val semanticElem = node?.semanticElement
            if (semanticElem instanceof ElkPropertyToValueMapEntryImpl) {
                val option = semanticElem.key.toLayoutOption
                val value = option?.parseValue(unquotedString)
                if (value !== null)
                    return value
            }
            return new LayoutOptionProxy(unquotedString)
        }
    }
    
    private def unquoteIfNecessary(String s) {
        if (s.length >= 2 && s.startsWith('"') && s.endsWith('"'))
            return s.substring(1, s.length - 1)
        else if (s.length >= 1 && (Character.isJavaIdentifierStart(s.charAt(0)) || s.startsWith('^'))) {
            val qname = qualifiedNameConverter.toQualifiedName(s)
            return qname.toString(ElkGraphQualifiedNameConverter.DELIMITER)
        }
        else
            return s
    }
    
    private def toLayoutOption(IProperty<?> property) {
        if (property instanceof LayoutOptionData)
            return property
        else if (property !== null)
            return LayoutMetaDataService.instance.getOptionData(property.id)
    }
    
}