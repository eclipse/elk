/*******************************************************************************
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.graph.text.conversion

import com.google.inject.Inject
import org.eclipse.elk.graph.properties.IProperty
import org.eclipse.xtext.common.services.DefaultTerminalConverters
import org.eclipse.xtext.conversion.IValueConverter
import org.eclipse.xtext.conversion.ValueConverter

class ElkGraphValueConverterService extends DefaultTerminalConverters {

    @Inject
    BooleanValueConverter booleanValueConverter

    @ValueConverter(rule="Boolean")
    def IValueConverter<Boolean> Boolean() {
        booleanValueConverter
    }

    @Inject
    NumberValueConverter numberValueConverter

    @ValueConverter(rule="Number")
    def IValueConverter<Double> Number() {
        numberValueConverter
    }

    @Inject
    PropertyKeyValueConverter propertyKeyValueConverter

    @ValueConverter(rule="PropertyKey")
    def IValueConverter<IProperty<?>> PropertyKey() {
        propertyKeyValueConverter
    }

    @ValueConverter(rule="FLOAT")
    def IValueConverter<Double> FLOAT() {
        numberValueConverter
    }

    @Inject
    IntegerValueConverter integerValueConverter

    @ValueConverter(rule="SIGNED_INT")
    def IValueConverter<Integer> SIGNED_INT() {
        integerValueConverter
    }

}
