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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

/**
 * A specialized resource for ELK graphs. This is registered once the plug-in is initialized.
 */
public class ElkGraphResource extends XMIResourceImpl {
    
    /**
     * Creates an instance of the resource.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param uri the URI of the new resource.
     * @generated
     */
    public ElkGraphResource(URI uri) {
        super(uri);
    }
    
    
    /* (non-Javadoc)
     * @see org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl#createXMLHelper()
     */
    @Override
    protected XMLHelper createXMLHelper() {
        return new ElkGraphXMIHelper();
    }
    
}
