/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.elk.alg.test.framework.TestMapping;
import org.eclipse.elk.core.debug.grandom.gRandom.RandGraph;
import org.eclipse.elk.core.debug.grandom.ui.GRandomGraphMaker;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * Contains methods to load graphs from resources.
 */
public final class GraphProvider {

    /**
     * No instantiation.
     */
    private GraphProvider() {
    }
    
    /**
     * 
     */
    public List<TestMapping> realizeTestMapping(final TestMapping testMapping) {
        List<ElkNode> graphs = new ArrayList<>();
        boolean random = false;
        
        // Actually load the graphs
        AbstractResourcePath resourcePath = testMapping.getResourcePath();
        if (isRandomGraphFile(resourcePath)) {
            graphs.addAll(loadRandomGraph(resourcePath));
            random = true;
        } else if (isElkGraphFile(resourcePath)) {
            ElkNode graph = loadElkGraph(resourcePath);
            if (graph != null) {
                graphs.add(loadElkGraph(resourcePath));
            }
        }
        
        // Generate test mappings
        List<TestMapping> result = new ArrayList<>();
        int counter = 1;
        
        for (ElkNode graph : graphs) {
            String graphName = resourcePath.getFile().getName();
            if (random) {
                graphName = "RANDOM" + graphName + counter++;
            }
            
            result.add(new TestMapping()
                    .withTestClasses(testMapping.getTestClasses())
                    .withGraph(graph)
                    .withGraphName(graphName)
                    .withGraphPath(resourcePath)
                    .withRandomGraph(testMapping.isRandom())
                    .withConfigurator(testMapping.getConfig())
                    .withConfigMethod(testMapping.getConfigMethod())
                    .withDefaultEdgeConfiguration(testMapping.isUseDefaultConfigEdges())
                    .withDefaultNodeConfiguration(testMapping.isUseDefaultConfigNodes())
                    .withDefaultPortConfiguration(testMapping.isUseDefaultConfigPorts()));
        }
        
        return result;
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ELK Graphs
    
    /**
     * Checks whether the resource describes a file that can be loaded as an ELK graph file.
     */
    public static boolean isElkGraphFile(final AbstractResourcePath resourcePath) {
        File file = resourcePath.getFile();
        String fileName = file.getName();
        
        return file.exists() && file.isFile() && (fileName.endsWith(".elkg") || fileName.endsWith(".elkt"));
    }

    /**
     * Tries to load an ELK graph from the given resource.
     */
    public static ElkNode loadElkGraph(final AbstractResourcePath resourcePath) {
        // For this to work, we assume that PlainJavaInitialization was already triggered
        ResourceSet resourceSet = new ResourceSetImpl();
        Resource resource = resourceSet.getResource(URI.createFileURI(resourcePath.getFile().getPath()), true);

        if (resource != null) {
            EObject eo = resource.getContents().get(0);
            if (eo instanceof ElkNode) {
                return (ElkNode) eo;
            }
        }
        
        return null;
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Random Graphs
    
    /**
     * Checks whether the resource refers to a file that contains random graph definitions.
     */
    public static boolean isRandomGraphFile(final AbstractResourcePath resourcePath) {
        File file = resourcePath.getFile();
        return file.exists() && file.isFile() && file.getName().endsWith(".elkr");
    }

    /**
     * Triggers the random graph generator to generate the graphs specified in the given file.
     */
    public static List<ElkNode> loadRandomGraph(final AbstractResourcePath resourcePath) {
        List<ElkNode> graphs = new ArrayList<>();
        URI fileURI = URI.createFileURI(resourcePath.getFile().getAbsolutePath());
        
        try {
            ResourceSet resourceSet = new ResourceSetImpl();
            Resource resource = resourceSet.getResource(fileURI, true);
            resource.load(Collections.emptyMap());
            
            if (!resource.getContents().isEmpty() && resource.getContents().get(0) instanceof RandGraph) {
                // load RandGraph out of file
                RandGraph rand = (RandGraph) resource.getContents().get(0);
                
                GRandomGraphMaker graphMaker = new GRandomGraphMaker(rand);
                graphs = graphMaker.loadGraph();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return graphs;
    }
}
