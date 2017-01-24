/*******************************************************************************
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.graph.text.conversion

import com.google.inject.Inject
import com.google.inject.Provider
import org.eclipse.elk.core.data.LayoutMetaDataService
import org.eclipse.elk.graph.properties.IProperty
import org.eclipse.elk.graph.text.services.ElkGraphGrammarAccess
import org.eclipse.xtext.conversion.ValueConverterException
import org.eclipse.xtext.conversion.impl.AbstractValueConverter
import org.eclipse.xtext.conversion.impl.IDValueConverter
import org.eclipse.xtext.nodemodel.INode
import org.eclipse.xtext.util.Strings

class PropertyKeyValueConverter extends AbstractValueConverter<IProperty<?>> {
    
    IDValueConverter idValueConverter
    
    @Inject
    def void initialize(Provider<IDValueConverter> idValueConverterProvider, ElkGraphGrammarAccess grammarAccess) {
        this.idValueConverter = idValueConverterProvider.get => [
            rule = grammarAccess.IDRule
        ]
    }
    
    override toString(IProperty<?> value) throws ValueConverterException {
        if (value === null)
            throw new ValueConverterException("IProperty value may not be null.", null, null)
        val metaDataService = LayoutMetaDataService.instance
        val split = Strings.split(value.id, '.')
        var String suffix
        var i = split.size - 1
        while (i >= 0) {
            if (suffix === null)
                suffix = idValueConverter.toString(split.get(i--))
            else
                suffix = idValueConverter.toString(split.get(i--)) + '.' + suffix
            if (metaDataService.getOptionDataBySuffix(suffix) !== null)
                return suffix
        }
        return split.map[idValueConverter.toString(it)].join('.')
    }
    
    override toValue(String string, INode node) throws ValueConverterException {
        if (string.nullOrEmpty)
            throw new ValueConverterException("Cannot convert empty string to a property idenfifier.", node, null)
        val idSuffix = Strings.split(string, '.').map[idValueConverter.toValue(it, node)].join('.')
        val optionData = LayoutMetaDataService.instance.getOptionDataBySuffix(idSuffix)
        if (optionData === null)
            throw new ValueConverterException("No layout option with identifier '" + idSuffix + "' can be found.", node, null)
        return optionData
    }
    
}