/*******************************************************************************
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.graph.text.naming

import com.google.inject.Inject
import com.google.inject.Provider
import org.eclipse.elk.graph.text.services.ElkGraphGrammarAccess
import org.eclipse.xtext.conversion.impl.IDValueConverter
import org.eclipse.xtext.naming.IQualifiedNameConverter
import org.eclipse.xtext.naming.QualifiedName
import org.eclipse.xtext.util.Strings

class ElkGraphQualifiedNameConverter implements IQualifiedNameConverter {
    
    public static val DELIMITER = '.'
    
    IDValueConverter idValueConverter
    
    @Inject
    def void initialize(Provider<IDValueConverter> idValueConverterProvider, ElkGraphGrammarAccess grammarAccess) {
        this.idValueConverter = idValueConverterProvider.get => [
            rule = grammarAccess.IDRule
        ]
    }
    
    override toQualifiedName(String qualifiedNameAsText) {
        if (qualifiedNameAsText === null)
            throw new IllegalArgumentException("Qualified name cannot be null")
        if (qualifiedNameAsText.empty)
            throw new IllegalArgumentException("Qualified name cannot be empty")
        val segs = Strings.split(qualifiedNameAsText, DELIMITER).map[idValueConverter.toValue(it, null)]
        return QualifiedName.create(segs)
    }
    
    override toString(QualifiedName name) {
        if (name === null)
            throw new IllegalArgumentException("Qualified name cannot be null")
        return name.segments.map[idValueConverter.toString(it)].join(DELIMITER)
    }
    
}