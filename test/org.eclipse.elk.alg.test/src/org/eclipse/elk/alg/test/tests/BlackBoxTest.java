/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.tests;

import java.util.List;

import org.eclipse.elk.alg.force.options.ForceOptions;
import org.eclipse.elk.alg.test.framework.annotations.Algorithm;
import org.eclipse.elk.alg.test.framework.annotations.ConfigMethod;
import org.eclipse.elk.alg.test.framework.annotations.Configurator;
import org.eclipse.elk.alg.test.framework.annotations.Graph;
import org.eclipse.elk.alg.test.framework.annotations.ImportGraphs;
import org.eclipse.elk.alg.test.framework.annotations.StoreResults;
import org.eclipse.elk.alg.test.framework.annotations.UseDefaultConfiguration;
import org.eclipse.elk.alg.test.framework.io.AbstractResourcePath;
import org.eclipse.elk.alg.test.framework.io.ModelResourcePath;
import org.eclipse.elk.alg.test.framework.util.TestUtil;
import org.eclipse.elk.core.LayoutConfigurator;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * A simple example of a black box test that tests different features of the test framework.
 */
@Algorithm(ForceOptions.ALGORITHM_ID)
@UseDefaultConfiguration()
@StoreResults
public class BlackBoxTest {
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Sources

    /**
     * Specifies a list of paths to test models. These will usually be paths to models in ELK's models repository.
     */
    @ImportGraphs
    public List<AbstractResourcePath> importGraphs() {
        return Lists.newArrayList(
                new ModelResourcePath("realworld/ptolemy/hierarchical/continuous_cartracking_CarTracking.elkt"));
    }
    
    /**
     * Returns a concrete graph to test with.
     */
    @Graph
    public ElkNode basicGraph() {
        return TestUtil.buildBasicGraph();
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Configuration
    
    /** A property to test configurators with. */
    private static final IProperty<String> TEST_PROPERTY_CONFIGURATOR_METHOD =
            new Property<>("org.eclipse.elk.alg.test.configuratorMethod", "");
    /** A property to test configurators with. */
    private static final IProperty<String> TEST_PROPERTY_CONFIGURATOR_FIELD =
            new Property<>("org.eclipse.elk.alg.test.configuratorField", "");
    /** A property to test configuration methods with. */
    private static final IProperty<String> TEST_PROPERTY_CONFIGURATION_METHOD =
            new Property<>("org.eclipse.elk.alg.test.2", "");
    
    /** The value we're setting on the property to test stuff. Must differ from the property's default value. */
    private static final String TEST_PROPERTY_VALUE = "Test";
    
    /** A field that supplies a configurator. */
    @Configurator
    public static final LayoutConfigurator CONFIGURATOR = new LayoutConfigurator();
    
    static {
        CONFIGURATOR.configure(ElkNode.class).setProperty(TEST_PROPERTY_CONFIGURATOR_FIELD, TEST_PROPERTY_VALUE);
    }

    /**
     * Supplies a configurator.
     */
    @Configurator
    public LayoutConfigurator configurator() {
        LayoutConfigurator layoutConfig = new LayoutConfigurator();
        layoutConfig.configure(ElkNode.class).setProperty(TEST_PROPERTY_CONFIGURATOR_METHOD, TEST_PROPERTY_VALUE);
        return layoutConfig;
    }
    
    /**
     * Configures the given graph.
     */
    @ConfigMethod
    public static void configureStuff(final ElkNode graph) {
        graph.setProperty(TEST_PROPERTY_CONFIGURATION_METHOD, TEST_PROPERTY_VALUE);
    }

    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Tests
    
    /**
     * Tests whether the layout configurator field was applied.
     */
    @Test
    public void testConfiguratorField(final ElkNode graph) {
        Assert.assertEquals("Property is not set properly.",
                TEST_PROPERTY_VALUE,
                graph.getProperty(TEST_PROPERTY_CONFIGURATOR_FIELD));
    }

    /**
     * Tests whether the layout configurator method was called and the configurator applied.
     */
    @Test
    public void testConfiguratorMethod(final ElkNode graph) {
        Assert.assertEquals("Property is not set properly.",
                TEST_PROPERTY_VALUE,
                graph.getProperty(TEST_PROPERTY_CONFIGURATOR_METHOD));
    }

    /**
     * Tests whether the layout configuration method was called.
     */
    @Test
    public void testConfigurationMethod(final ElkNode graph) {
        Assert.assertEquals("Property is not set properly.",
                TEST_PROPERTY_VALUE,
                graph.getProperty(TEST_PROPERTY_CONFIGURATION_METHOD));
    }

}
