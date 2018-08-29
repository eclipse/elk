/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.eclipse.elk.alg.test.framework.annotations.ConfigMethod;
import org.eclipse.elk.alg.test.framework.annotations.Configurator;
import org.eclipse.elk.alg.test.framework.annotations.Graph;
import org.eclipse.elk.alg.test.framework.annotations.ImportGraphs;
import org.eclipse.elk.alg.test.framework.annotations.RandomGeneratorOptions;
import org.eclipse.elk.alg.test.framework.annotations.RandomGraphFile;
import org.eclipse.elk.alg.test.framework.annotations.UseDefaultConfiguration;
import org.eclipse.elk.alg.test.framework.io.AbstractResourcePath;
import org.eclipse.elk.alg.test.framework.io.GraphProvider;
import org.eclipse.elk.alg.test.framework.util.TestUtil;
import org.eclipse.elk.core.LayoutConfigurator;
import org.eclipse.elk.core.debug.grandom.generators.GeneratorOptions;
import org.eclipse.elk.core.debug.grandom.generators.RandomGraphGenerator;
import org.eclipse.elk.core.util.Pair;
import org.eclipse.elk.graph.ElkNode;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

/**
 * Generates test mappings for a list of test classes. To do so, this class loads all graphs and configurators and
 * groups them in order to improve performance. Used by test runners.
 */
public class TestMappingGenerator {

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Loading Test Mappings

    /**
     * Creates test mappings for the given lst of test classes. Each test mapping has a single graph set on it that
     * layout and test can be run on.
     */
    public List<TestMapping> createTestMappings(final List<TestClass> testClasses) {
        List<TestMapping> result = new ArrayList<>();
        
        for (TestClass testClass : testClasses) {
            // Load everything we need to create the test mappings. Note that this keeps us from grouping tests at the
            // moment. To reintroduce that feature, we would have to find a way to properly group graphs loaded from
            // the same files and configured the same way
            List<Pair<ElkNode, String>> graphs = loadGraphs(testClass);
            List<Pair<ElkNode, String>> randomGraphs = loadRandomGraphs(testClass);
            List<LayoutConfigurator> layoutConfigurators = loadLayoutConfigurators(testClass);
            List<FrameworkMethod> layoutConfigMethods = testClass.getAnnotatedMethods(ConfigMethod.class);
            DefaultConfigs defaultConfigs = loadDefaultConfigs(testClass);
            
            // Now that we have everything we need, we need to combine every graph we have, both random and non-random,
            // with every kind of configuration facility we have. If that doesn't yield any combinations, that means
            // that no configuration facilities were provided, so we need to create a non-configured test mapping for
            // each graph
            List<TestMapping> testMappings = new ArrayList<>();
            testMappings.addAll(combineGraphsWithConfigurators(graphs, layoutConfigurators, false));
            testMappings.addAll(combineGraphsWithConfigurators(randomGraphs, layoutConfigurators, true));
            testMappings.addAll(combineGraphsWithConfigMethods(graphs, layoutConfigMethods, false));
            testMappings.addAll(combineGraphsWithConfigMethods(randomGraphs, layoutConfigMethods, true));
            
            if (testMappings.isEmpty()) {
                // If no configuration was provided, we wouldn't have any test mappings. Add non-configuring test
                // mappings for our graphs
                testMappings.addAll(createTestMappingsWithoutConfig(graphs, false));
                testMappings.addAll(createTestMappingsWithoutConfig(randomGraphs, false));
            }
            
            // Be sure to apply default configuration and test classes to all test mappings
            for (TestMapping mapping : testMappings) {
                mapping
                    .withTestClass(testClass)
                    .withDefaultEdgeConfiguration(defaultConfigs.edges)
                    .withDefaultNodeConfiguration(defaultConfigs.nodes)
                    .withDefaultPortConfiguration(defaultConfigs.ports);
            }
            
            result.addAll(testMappings);
        }
        
        return result;
    }

    /**
     * Returns a list of test mappings created by combining every configurator with every graph.
     */
    private List<TestMapping> combineGraphsWithConfigurators(final List<Pair<ElkNode, String>> graphs,
            final List<LayoutConfigurator> configurators, final boolean randomGraphs) {
        
        List<TestMapping> result = new ArrayList<>();
        
        for (LayoutConfigurator configurator : configurators) {
            for (Pair<ElkNode, String> graphPair : graphs) {
                result.add(new TestMapping()
                        .withConfigurator(configurator)
                        .withGraph(graphPair.getFirst())
                        .withGraphName(graphPair.getSecond())
                        .withRandomGraph(randomGraphs));
            }
        }
        
        return result;
    }

    /**
     * Returns a list of test mappings created by combining every configuration method with every graph.
     */
    private List<TestMapping> combineGraphsWithConfigMethods(final List<Pair<ElkNode, String>> graphs,
            final List<FrameworkMethod> configMethods, final boolean randomGraphs) {
        
        List<TestMapping> result = new ArrayList<>();
        
        for (FrameworkMethod configMethod : configMethods) {
            for (Pair<ElkNode, String> graphPair : graphs) {
                result.add(new TestMapping()
                        .withConfigMethod(configMethod)
                        .withGraph(graphPair.getFirst())
                        .withGraphName(graphPair.getSecond())
                        .withRandomGraph(randomGraphs));
            }
        }
        
        return result;
    }

    /**
     * Returns a list of test mappings created by creating one mapping for every graph.
     */
    private Collection<? extends TestMapping> createTestMappingsWithoutConfig(final List<Pair<ElkNode, String>> graphs,
            final boolean randomGraphs) {
        
        List<TestMapping> result = new ArrayList<>();
        
        for (Pair<ElkNode, String> graphPair : graphs) {
            result.add(new TestMapping()
                    .withGraph(graphPair.getFirst())
                    .withGraphName(graphPair.getSecond())
                    .withRandomGraph(randomGraphs));
        }
        
        return result;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Annotations

    /**
     * Load all non-random graphs from the given test class. This method takes care of both, directly provided graphs
     * as well as graphs that have to be loaded from a resource.
     */
    private List<Pair<ElkNode, String>> loadGraphs(final TestClass testClass) {
        List<Pair<ElkNode, String>> graphs = new ArrayList<>();
        
        // Load graphs specified through fields and methods
        for (Pair<Object, String> pair : TestUtil.loadAnnotatedFieldsAndMethodsWithNames(testClass, Graph.class)) {
            if (pair.getFirst() instanceof ElkNode) {
                graphs.add(Pair.of((ElkNode) pair.getFirst(), pair.getSecond()));
            }
        }
        
        // Load graphs referenced through resources
        for (Object o : TestUtil.loadAnnotatedFieldsAndMethods(testClass, ImportGraphs.class)) {
            if (o instanceof List<?>) {
                // Resource paths may denote directories, so we need to find all files they describe first
                List<AbstractResourcePath> paths = ((List<?>) o).stream()
                    .filter(obj -> obj instanceof AbstractResourcePath)
                    .flatMap(obj -> ((AbstractResourcePath) obj).listResources().stream())
                    .collect(Collectors.toList());
                
                for (AbstractResourcePath path : paths) {
                    if (GraphProvider.isElkGraphFile(path)) {
                        graphs.add(Pair.of(GraphProvider.loadElkGraph(path), path.getFile().getName()));
                    }
                }
            }
        }
        
        return graphs;
    }
    
    /**
     * Load all random graphs from the given test class. This method takes care of random generator options specified
     * directly in the test class as well as in referenced elkr files.
     */
    private List<Pair<ElkNode, String>> loadRandomGraphs(final TestClass testClass) {
        List<Pair<ElkNode, String>> graphs = new ArrayList<>();

        // Load options specified through fields and methods
        List<Pair<GeneratorOptions, String>> options = new ArrayList<>();
        
        List<Pair<Object, String>> objList = TestUtil.loadAnnotatedFieldsAndMethodsWithNames(
                testClass, RandomGeneratorOptions.class);
        for (Pair<Object, String> pair : objList) {
            if (pair.getFirst() instanceof GeneratorOptions) {
                options.add(Pair.of((GeneratorOptions) pair.getFirst(), pair.getSecond()));
            }
        }
        
        // Generate graphs from those options
        RandomGraphGenerator generator = new RandomGraphGenerator(new Random());
        for (Pair<GeneratorOptions, String> pair : options) {
            ElkNode graph = generator.generate(pair.getFirst());
            graphs.add(Pair.of(graph, pair.getSecond()));
        }
        
        // Load graphs referenced through resources
        for (Object value : TestUtil.loadAnnotatedFieldsAndMethods(testClass, RandomGraphFile.class)) {
            if (value instanceof AbstractResourcePath) {
                for (AbstractResourcePath path : ((AbstractResourcePath) value).listResources()) {
                    if (GraphProvider.isRandomGraphFile(path)) {
                        int counter = 0;
                        for (ElkNode graph : GraphProvider.loadRandomGraph(path)) {
                            graphs.add(Pair.of(graph, path.getFile().getName() + counter++));
                        }
                    }
                }
            }
        }
        
        return graphs;
    }

    /**
     * Loads all layout configurators specified by the test class, either through fields or through methods.
     */
    private List<LayoutConfigurator> loadLayoutConfigurators(final TestClass testClass) {
        List<LayoutConfigurator> configs = new ArrayList<>();

        for (Object o : TestUtil.loadAnnotatedFieldsAndMethods(testClass, Configurator.class)) {
            if (o instanceof LayoutConfigurator) {
                configs.add((LayoutConfigurator) o);
            }
        }
        
        return configs;
    }
    
    /**
     * Loads default configuration parameters from the test class.
     */
    private DefaultConfigs loadDefaultConfigs(final TestClass testClass) {
        DefaultConfigs result = new DefaultConfigs();
        
        UseDefaultConfiguration defaultConfig = testClass.getAnnotation(UseDefaultConfiguration.class);
        if (defaultConfig != null) {
            result.edges = defaultConfig.edges();
            result.nodes = defaultConfig.nodes();
            result.ports = defaultConfig.ports();
        }
        
        return result;
    }
    
    /**
     * A small data holder class that specifies whether edges, nodes, and ports should be configured with default
     * values.
     */
    private static class DefaultConfigs {
        private boolean edges;
        private boolean nodes;
        private boolean ports;
    }
    
}
