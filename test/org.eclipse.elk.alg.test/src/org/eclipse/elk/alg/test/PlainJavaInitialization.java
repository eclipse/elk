/*******************************************************************************
 * Copyright (c) 2018, 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.test;

import org.eclipse.elk.core.data.LayoutMetaDataService;
import org.eclipse.elk.core.debug.grandom.GRandomStandaloneSetup;
import org.eclipse.elk.core.util.persistence.ElkGraphResourceFactory;
import org.eclipse.elk.graph.ElkGraphPackage;
import org.eclipse.elk.graph.text.ElkGraphStandaloneSetup;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * This class is used to register all layout algorithms with ELK's meta data provider in order to be able to use them
 * if the tests are not executed as JUnit plug-in tests.
 */
public final class PlainJavaInitialization {
    
    /**
     * Prevent instantiation.
     */
    private PlainJavaInitialization() {
    }
    

    /**
     * Setup everything to work outside of Eclipse.
     */
    public static void initializePlainJavaLayout() {
        // Ensure that the meta data service loads all meta data providers as soon as possible (not doing this worked
        // for at least one test class, so this might not even be necessary)
        LayoutMetaDataService service = LayoutMetaDataService.getInstance();
        
        // Set up GRandom for loading .elkr files and ELK Graph Text for loading .elkg and .elkt files
        GRandomStandaloneSetup.doSetup();
        
        ElkGraphStandaloneSetup.doSetup();
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("elkg", new ElkGraphResourceFactory());
        ElkGraphPackage.eINSTANCE.eClass();
    }

}
