/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christoph Daniel Schulze - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.util.persistence;

import org.eclipse.elk.core.data.LayoutMetaDataService;
import org.eclipse.elk.core.util.IDataObject;
import org.eclipse.elk.core.util.internal.LayoutOptionProxy;
import org.eclipse.elk.graph.ElkGraphPackage;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.xmi.impl.XMIHelperImpl;

/**
 * An XMI helper which has special knowledge on how to load {@link IProperty} and {@link IDataObject} instances
 * while loading ELK graphs. A fresh instance must be used for every loading operation. See {@link ElkGraphResource}
 * for details.
 * 
 * @see ElkGraphResource
 */
final class ElkGraphXMIHelper extends XMIHelperImpl {
    
    @Override
    protected Object createFromString(final EFactory eFactory, final EDataType eDataType, final String value) {
        // We know how to obtain IProperty and parse option values
        if (eDataType.equals(ElkGraphPackage.Literals.IPROPERTY)) {
            return createIPropertyFromString(value);
        } else if (eDataType.equals(ElkGraphPackage.Literals.PROPERTY_VALUE)) {
            return createPropertyValueFromString(value);
        } else {
            return super.createFromString(eFactory, eDataType, value);
        }
    }
    
    /**
     * Asks the layout meta data service to load the property which has the given value as its ID. Also saves it
     * as the most recently loaded property.
     */
    private IProperty<?> createIPropertyFromString(final String value) {
        return LayoutMetaDataService.getInstance().getOptionData(value);
    }
    
    /**
     * Uses the most recently loaded property to parse the given value as an instance of the property's proper value
     * type. If no property has been loaded yet, creates a {@link LayoutOptionProxy}.
     */
    private Object createPropertyValueFromString(final String value) {
        return new LayoutOptionProxy(value);
    }
    
}
