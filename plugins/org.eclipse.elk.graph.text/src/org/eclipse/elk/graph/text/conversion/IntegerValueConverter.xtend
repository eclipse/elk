/*******************************************************************************
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.graph.text.conversion

import org.eclipse.xtext.conversion.ValueConverterException
import org.eclipse.xtext.conversion.impl.AbstractValueConverter
import org.eclipse.xtext.nodemodel.INode

class IntegerValueConverter extends AbstractValueConverter<Integer> {
    
    override toString(Integer value) throws ValueConverterException {
        if (value === null)
            throw new ValueConverterException("Integer value may not be null.", null, null)
        return value.toString
    }
    
    override toValue(String string, INode node) throws ValueConverterException {
        if (string.nullOrEmpty)
            throw new ValueConverterException("Cannot convert empty string to an integer value.", node, null);
        try {
            return Integer.valueOf(string)
        } catch (NumberFormatException e) {
            throw new ValueConverterException("Cannot convert '" + string + "' to an integer value.", node, e)
        }
    }
    
}