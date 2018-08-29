/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework;

import java.util.List;

import org.eclipse.elk.alg.test.framework.io.AbstractResourcePath;
import org.eclipse.elk.core.LayoutConfigurator;
import org.eclipse.elk.graph.ElkNode;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

import com.google.common.collect.Lists;

/**
 * Holds all the information about the input for the tested layout algorithm and the tests that should be executed
 * after the layout.
 */
public class TestMapping {
    
    /** The input graph. */
    private ElkNode graph = null;
    /** The path of the graph that has to be imported as input graph. */
    private AbstractResourcePath graphPath;
    /**
     * The name of the graph is needed to store and display results. In case of an imported graph it is the name of the
     * file. In case of a graph provided by a method in a test class it is a combination of the name of the class and
     * the method.
     */
    private String graphName = "";
    /** Specifies whether the graph was generated randomly. */
    private boolean isGraphRandom = false;
    /** The layout configurator the graph should be configured with. */
    private LayoutConfigurator configurator = null;
    /** A method that has to be used to configure the input graph. */
    private FrameworkMethod configMethod = null;
    /** The test classes whose tests should be run on the graph. */
    private List<TestClass> testClasses = null;
    /** Specifies whether the graph should use default edge configurations. */
    private boolean useDefaultConfigEdges = false;
    /** Specifies whether the graph should use default node configurations. */
    private boolean useDefaultConfigNodes = false;
    /** Specifies whether the graph should use default port configurations. */
    private boolean useDefaultConfigPorts = false;
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Getters

    /**
     * 
     * @return The graph that should be laid out.
     */
    public ElkNode getGraph() {
        return graph;
    }

    /**
     * 
     * @return The resource path of a graph that should be imported or a directory with graphs.
     */
    public AbstractResourcePath getResourcePath() {
        return graphPath;
    }

    /**
     * 
     * @return The name of the graph.
     */
    public String getGraphName() {
        return graphName;
    }

    /**
     * 
     * @return Specifies, whether the graph is a random generated graph.
     */
    public boolean isRandom() {
        return isGraphRandom;
    }

    /**
     * 
     * @return The configurator used to configure the graph.
     */
    public LayoutConfigurator getConfig() {
        return configurator;
    }

    /**
     * 
     * @return The method that configures the graph.
     */
    public FrameworkMethod getConfigMethod() {
        return configMethod;
    }

    /**
     * 
     * @return The test classes with the tests that should be executed.
     */
    public List<TestClass> getTestClasses() {
        return testClasses;
    }

    /**
     * @return the useDefaultConfigEdges
     */
    public boolean isUseDefaultConfigEdges() {
        return useDefaultConfigEdges;
    }

    /**
     * @return the useDefaultConfigNodes
     */
    public boolean isUseDefaultConfigNodes() {
        return useDefaultConfigNodes;
    }

    /**
     * @return the useDefaultConfigPorts
     */
    public boolean isUseDefaultConfigPorts() {
        return useDefaultConfigPorts;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Setters

    /**
     * Configures the given graph to be used.
     */
    public TestMapping withGraph(final ElkNode theGraph) {
        this.graph = theGraph;
        return this;
    }

    /**
     * Configures the given resource path to load a graph from.
     */
    public TestMapping withGraphPath(final AbstractResourcePath theGraphPath) {
        this.graphPath = theGraphPath;
        return this;
    }

    /**
     * Configures the given graph name to be used.
     */
    public TestMapping withGraphName(final String theGraphName) {
        this.graphName = theGraphName;
        return this;
    }

    /**
     * Configures whether the graph has been generated randomly.
     */
    public TestMapping withRandomGraph(final boolean random) {
        this.isGraphRandom = random;
        return this;
    }

    /**
     * Configures the given layout configurator to be used for configuring the graph.
     */
    public TestMapping withConfigurator(final LayoutConfigurator theConfigurator) {
        this.configurator = theConfigurator;
        return this;
    }

    /**
     * Configures the given method to be used to configure the graph.
     */
    public TestMapping withConfigMethod(final FrameworkMethod theConfigMethod) {
        configMethod = theConfigMethod;
        return this;
    }
    
    /**
     * Configures the methods in the given test class to be executed.
     */
    public TestMapping withTestClass(final TestClass theTestClass) {
        return withTestClasses(Lists.newArrayList(theTestClass));
    }

    /**
     * Configures the methods in the given test classes to be executed.
     */
    public TestMapping withTestClasses(final List<TestClass> theTestClasses) {
        this.testClasses = theTestClasses;
        return this;
    }

    /**
     * Configures whether edges should get default configurations.
     */
    public TestMapping withDefaultEdgeConfiguration(final boolean useDefault) {
        this.useDefaultConfigEdges = useDefault;
        return this;
    }

    /**
     * Configures whether nodes should get default configurations.
     */
    public TestMapping withDefaultNodeConfiguration(final boolean useDefault) {
        this.useDefaultConfigNodes = useDefault;
        return this;
    }

    /**
     * Configures whether ports should get default configurations.
     */
    public TestMapping withDefaultPortConfiguration(final boolean useDefault) {
        this.useDefaultConfigPorts = useDefault;
        return this;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Equality Testing

    /**
     * Tests whether the object is a test mapping with the same import.
     * 
     * @param other
     *            the object that should be compared.
     * @param notEmpty
     *            whether the method should return {@code false} if no import resource path is specified.
     */
    public boolean sameImport(final Object other, final boolean notEmpty) {
        if (other instanceof TestMapping) {
            TestMapping otherMapping = (TestMapping) other;

            if (this.graphPath == null || otherMapping.graphPath == null) {
                if (!notEmpty) {
                    return this.graphPath == null && otherMapping.graphPath == null;
                }
            } else {
                return this.graphPath.equals(otherMapping.graphPath);
            }
        }
        
        return false;
    }

    /**
     * Tests whether the object is a test mapping with the same configurator. It is as well important whether the nodes
     * should be configured with the same default configurators.
     * 
     * @param other
     *            the object that should be compared.
     * @param notEmpty
     *            whether the method should return {@code false} if no configurator is specified.
     * @return whether the object is a test mapping with the same configurator.
     */
    public boolean sameConfig(final Object other, final boolean notEmpty) {
        if (other instanceof TestMapping) {
            TestMapping otherMapping = (TestMapping) other;
            
            // Tests with a configuration method shouldn't be grouped
            if (this.configMethod != null || otherMapping.configMethod != null) {
                return false;
            }

            if (this.configurator == null || otherMapping.configurator == null) {
                if (!notEmpty) {
                    return this.configurator == null && otherMapping.configurator == null
                            && this.useDefaultConfigEdges == otherMapping.useDefaultConfigEdges
                            && this.useDefaultConfigNodes == otherMapping.useDefaultConfigNodes
                            && this.useDefaultConfigPorts == otherMapping.useDefaultConfigPorts;
                }
            } else {
                return this.configurator.equals(otherMapping.configurator)
                        && this.useDefaultConfigEdges == otherMapping.useDefaultConfigEdges
                        && this.useDefaultConfigNodes == otherMapping.useDefaultConfigNodes
                        && this.useDefaultConfigPorts == otherMapping.useDefaultConfigPorts;
            }
        }
        
        return false;
    }
    
}
