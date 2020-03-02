/*******************************************************************************
 * Copyright (c) 2018, 2019 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.test;

import org.eclipse.elk.alg.disco.options.DisCoMetaDataProvider;
import org.eclipse.elk.alg.force.options.ForceMetaDataProvider;
import org.eclipse.elk.alg.force.options.StressMetaDataProvider;
import org.eclipse.elk.alg.layered.options.LayeredMetaDataProvider;
import org.eclipse.elk.alg.mrtree.options.MrTreeMetaDataProvider;
import org.eclipse.elk.alg.radial.options.RadialMetaDataProvider;
import org.eclipse.elk.alg.rectpacking.options.RectPackingMetaDataProvider;
import org.eclipse.elk.alg.spore.options.SporeMetaDataProvider;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
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
    
    /** The list of meta data providers we need to register. */
    private static ILayoutMetaDataProvider[] metaDataProviders;
    
    static {
        metaDataProviders = new ILayoutMetaDataProvider[] {
            new DisCoMetaDataProvider(),
            new ForceMetaDataProvider(),
            new LayeredMetaDataProvider(),
            new MrTreeMetaDataProvider(),
            new RadialMetaDataProvider(),
            new RectPackingMetaDataProvider(),
            new SporeMetaDataProvider(),
            new StressMetaDataProvider()
        };
    }
    
    
    /**
     * Prevent instantiation.
     */
    private PlainJavaInitialization() {
    }
    

    /**
     * Setup everything to work outside of Eclipse.
     */
    public static void initializePlainJavaLayout() {
        // Set up GRandom for loading .elkr files and ELK Graph Text for loading .elkg and .elkt files
        GRandomStandaloneSetup.doSetup();
        
        ElkGraphStandaloneSetup.doSetup();
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("elkg", new ElkGraphResourceFactory());
        ElkGraphPackage.eINSTANCE.eClass();

        // Register our meta data providers
        LayoutMetaDataService service = LayoutMetaDataService.getInstance();
        service.registerLayoutMetaDataProviders(metaDataProviders);
    }

}
