/*******************************************************************************
 * Copyright (c) 2019 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.alg.test.framework.annotations.ConfiguratorProvider;
import org.eclipse.elk.alg.test.framework.util.TestUtil;
import org.eclipse.elk.core.LayoutConfigurator;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.graph.ElkNode;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

/**
 * A configuration strategy which applies a layout configurator to a graph.
 */
public final class ConfiguratorTestConfiguration extends TestConfiguration {

    /** The method that supplies the layout configurator. */
    private final FrameworkMethod configuratorMethod;
    /** The layout configurator obtained from the method. Cached after first call. */
    private LayoutConfigurator configurator = null;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Creation

    private ConfiguratorTestConfiguration(final TestClass testClass, final FrameworkMethod configuratorMethod,
            final LayoutConfigurator configurator) {

        super(testClass);
        this.configuratorMethod = configuratorMethod;
        this.configurator = configurator;
    }

    /**
     * Loads the configurator supplier methods defined in the test class.
     * 
     * @param testClass
     *            the test class.
     * @param test
     *            instance of the test class to call methods.
     * @param errors
     *            list of error conditions encountered while evaluating the layout algorithms.
     * @return a list of configurations loaded from the test class.
     */
    public static List<ConfiguratorTestConfiguration> fromTestClass(final TestClass testClass, final Object test,
            final List<Throwable> errors) {

        List<ConfiguratorTestConfiguration> configurations = new ArrayList<>();

        for (FrameworkMethod method : testClass.getAnnotatedMethods(ConfiguratorProvider.class)) {
            TestUtil.ensurePublic(method, errors);
            TestUtil.ensureReturnsType(method, errors, LayoutConfigurator.class);
            TestUtil.ensureParameters(method, errors);

            // Invoke the method to obtain the layout configurator
            try {
                configurations.add(new ConfiguratorTestConfiguration(testClass, method,
                        (LayoutConfigurator) method.invokeExplosively(test)));
            } catch (Throwable e) {
                errors.add(e);
            }
        }

        return configurations;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // TestConfiguration

    @Override
    protected void applyBaseConfiguration(final Object testClass, final ElkNode graph) throws Throwable {
        ElkUtil.applyVisitors(graph, configurator);
    }

    @Override
    public String toString() {
        return super.toString() + " layoutConfigurator(" + configuratorMethod.getName() + ")";
    }

}
