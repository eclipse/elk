/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.shared.test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import org.eclipse.elk.alg.force.options.ForceMetaDataProvider;
import org.eclipse.elk.alg.layered.options.LayeredMetaDataProvider;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.alg.mrtree.options.MrTreeMetaDataProvider;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutMetaDataService;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.core.data.LayoutOptionData.Type;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.util.IDataObject;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;
import org.eclipse.elk.graph.util.ElkReflect;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Checks that all required property types (at least the ones we know about) are registered with the {@link ElkReflect}
 * class.
 */
public class ElkReflectTest {

    /** Dedicated meta data providers. */
    private List<Class<? extends ILayoutMetaDataProvider>> providers =
            Lists.newArrayList(
                    CoreOptions.class, 
                    LayeredMetaDataProvider.class,
                    ForceMetaDataProvider.class,
                    org.eclipse.elk.alg.radial.options.RadialMetaDataProvider.class,
                    MrTreeMetaDataProvider.class);
    
    /** Properties used internally somewhere. */
    private List<Class<?>> otherPropertyFiles = 
            Lists.newArrayList(
                    org.eclipse.elk.alg.layered.options.InternalProperties.class, 
                    org.eclipse.elk.alg.force.options.InternalProperties.class,
                    org.eclipse.elk.alg.radial.InternalProperties.class,
                    org.eclipse.elk.alg.mrtree.options.InternalProperties.class);

    /** 
     * Register the known providers with the {@link LayoutMetaDataService}. 
     */
    @Before
    public void init() throws IllegalAccessException, InstantiationException {
        LayoutMetaDataService service = LayoutMetaDataService.getInstance();
        for (Class<? extends ILayoutMetaDataProvider> p : providers) {
            service.registerLayoutMetaDataProviders(p.newInstance());
        }
    }
    
    /**
     * Tests the simple {@link Property} class, in particular, that the {@link Property#getDefault()} method behaves
     * correctly.
     */
    @Test
    public void testSimpleProperty() throws IllegalAccessException {
        
        final Iterable<Class<?>> allKnownProperties = Iterables.concat(providers, otherPropertyFiles);
        
        for (Class<?> p : allKnownProperties) {
            Field[] resolverFields = p.getDeclaredFields();
            for (Field f : resolverFields) {
                if (f.getType().isAssignableFrom(IProperty.class)) {
                    f.setAccessible(true); // required for private static fields

                    // get the property
                    IProperty<?> property = (IProperty<?>) f.get(null);

                    checkPropertyDefault(property);
                    
                }
            }
        }
    }
    
    /** 
     * A set of registered layout options that do not fill the contract to implement {@link IDataObject}.  
     */
    private Set<IProperty<?>> iDataObjectExceptions = Sets.newHashSet(
            // cds grrrrrrr
            CoreOptions.LABEL_MANAGER,
            CoreOptions.SPACING_INDIVIDUAL_OVERRIDE, 
            LayeredOptions.WRAPPING_CUTTING_CUTS);

    /**
     * Tests the {@link LayoutOptionData} which, as opposed to the simple {@link Property}, knows how to serialize and
     * parse {@link IDataObject}s.
     */
    @Test 
    public void testLayoutOptionData() {
        Iterable<LayoutOptionData> lods = LayoutMetaDataService.getInstance().getOptionData();
        for (LayoutOptionData ld : lods) {
            
            checkPropertyDefault(ld);

            if (ld.getType() == Type.OBJECT) {
                
                // FIXME exceptions 
                if (iDataObjectExceptions.contains(ld)) {
                    continue;
                }
                
                // objects must be parsable
                Assert.assertTrue(ld + " is parsable.", ld.canParseValue());
                // thus the class must be an IDataObject
                Object o = ld.getDefault();
                // if there's no default, try to create an element
                try {
                    o = ld.getClass().newInstance();
                } catch (IllegalAccessException | InstantiationException e) {
                    // give up testing
                }
                
                if (o != null) {
                    // check that it can be serialized and parsed
                    Assert.assertTrue(o instanceof IDataObject);

                    IDataObject ido = (IDataObject) o;
                    String serialized = ido.toString();
                    IDataObject parsed = (IDataObject) ld.parseValue(serialized);

                    String serializedAgain = parsed.toString();
                    Assert.assertEquals(serialized, serializedAgain);
                }
            }
            
        }
    }

    private void checkPropertyDefault(IProperty<?> property) {
        // check that the default value can be accessed
        Object o = property.getDefault();
        if (o != null && o instanceof Cloneable) {
            // a second object must be a new instance
            Object o2 = property.getDefault();
            Assert.assertNotSame(o, o2);
        }
    }
}
