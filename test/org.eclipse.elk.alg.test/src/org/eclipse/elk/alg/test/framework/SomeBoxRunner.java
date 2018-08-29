/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

import org.eclipse.elk.core.LayoutConfigurator;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkLabel;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.ElkPort;
import org.eclipse.swt.SWT;
import org.junit.runner.Runner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

import com.google.common.base.Strings;

/**
 * The parent class of the {@link WhiteBoxRunner} and the {@link BlackBoxRunner}. It does the work that is important for
 * both runners. This runner (and the ones extending it) are between the {@link LayoutTestRunner}, which manages the
 * more high-level tasks of a test run, and the {@link ActualTestRunner}, which actually executes the test methods.
 * 
 * @see LayoutTestRunner
 * @see WhiteBoxRunner
 * @see BlackBoxRunner
 * @see ActualTestRunner
 */
public abstract class SomeBoxRunner extends Runner {

    /** The size of the font used to calculate the size of a label. */
    private static final int FONT_SIZE = 10;

    /** The ID of the tested layout algorithm. */
    private String algorithmId;
    /** The test descriptions to be executed. */
    private List<TestMapping> testMappings;
    /** Whether graphs of failed tests should be stored. */
    private boolean storeFailedGraphs = false;

    /**
     * Creates a new instance.
     * 
     * @param algorithmId
     *            the ID of the tested algorithm.
     * @param testClasses
     *            the test classes with all tests that should be executed by this runner.
     * @param storeFailedGraphs
     *            whether graphs of failed tests should be stored.
     * @throws InitializationError
     *             represents an error that occurred during initialization.
     */
    public SomeBoxRunner(final String algorithmId, final List<TestClass> testClasses, final boolean storeFailedGraphs)
            throws InitializationError {
        
        this.algorithmId = algorithmId;
        this.storeFailedGraphs = storeFailedGraphs;
        
        loadTestMappings(testClasses);
        buildRunner();
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Test Runners

    /**
     * Loads the graphs and configures them. For each Testmapping is either a Layoutconfigurator or a method for
     * configuration or non of these two things given, but not both. The algorithm specified in the class given to the
     * Framework is set here together with the other configurations.
     */
    public void loadTestMappings(final List<TestClass> testClasses) {
        testMappings = new TestMappingGenerator().createTestMappings(testClasses);
        
        // Look at the layout configurations specified in each test mapping
        for (TestMapping testMapping : testMappings) {
            if (testMapping.getConfigMethod() != null) {
                // The test has a configuration method, so call that
                applyConfigMethod(testMapping);
                
            } else if (!Strings.isNullOrEmpty(algorithmId)) {
                // The test has no configuration method, and we have an algorithm ID. Set the algorithm on all nodes
                LayoutConfigurator layoutConfig = testMapping.getConfig();
                if (layoutConfig == null) {
                    layoutConfig = new LayoutConfigurator();
                }
                
                layoutConfig.configure(ElkNode.class).setProperty(CoreOptions.ALGORITHM, algorithmId);
                ElkUtil.applyVisitors(testMapping.getGraph(), layoutConfig);
            }
            
            ensureSensibleLabelSize(testMapping.getGraph());
            applyDefaults(testMapping.getGraph(), testMapping);
        }

    }

    /**
     * Creates one {@link ActualTestRunner} for each test class to execute the test methods in that class. The mapping
     * between test classes and their runners are stored in the test mapping itself. 
     */
    protected void buildRunner() {
        for (TestMapping testMapping : testMappings) {
            for (TestClass testClass : testMapping.getTestClasses()) {
                try {
                    testMapping.setRunnerForClass(testClass, new ActualTestRunner(
                            testClass.getJavaClass(), testMapping.getGraphName(), algorithmId, storeFailedGraphs));
                } catch (InitializationError e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Configuration

    /**
     * Calls the mappings configuration method to configure its graph.
     * 
     * @throws IllegalStateException if the configuration method is not static.
     */
    private void applyConfigMethod(final TestMapping testMapping) {
        assert testMapping.getConfigMethod() != null;
        
        // A configuration method needs to be static
        if (!testMapping.getConfigMethod().isStatic()) {
            throw new IllegalStateException("Configuration method " + testMapping.getConfigMethod().getName()
                    + " needs to be static.");
        }
        
        // Invoke the configuration method
        try {
            testMapping.getConfigMethod().invokeExplosively(null, testMapping.getGraph());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Computes a proper size for labels if they have none configured.
     */
    private void ensureSensibleLabelSize(final ElkNode graph) {
        graph.eAllContents().forEachRemaining(obj -> {
            if (obj instanceof ElkLabel) {
                computeLabelSize((ElkLabel) obj);
            }
        });
    }
    
    /**
     * Computes the size of a label if it doesn't already have one. The size is calculated by choosing a font and
     * calculating the space necessary to render the label's text with that font.
     */
    private void computeLabelSize(final ElkLabel label) {
        // Check if we need to do anything
        if (label.getHeight() == 0 && label.getWidth() == 0 && !Strings.isNullOrEmpty(label.getText())) {
            
            // There seems to be a problem with SansSerif on Windows...
            String fontName;
            if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
                fontName = "Arial";
            } else {
                fontName = "SansSerif";
            }
            
            BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            
            Graphics2D graphics = image.createGraphics();
            graphics.setFont(new java.awt.Font(fontName, SWT.NORMAL, FONT_SIZE));
            
            Rectangle2D rect = graphics.getFontMetrics().getStringBounds(label.getText(), graphics);
            label.setHeight(rect.getHeight());
            label.setWidth(rect.getWidth());
        }
    }

    /**
     * Configures the test mapping's graph with default values if necessary.
     */
    private void applyDefaults(final ElkNode parent, final TestMapping testMapping) {
        boolean configNodes = testMapping.isUseDefaultConfigNodes();
        boolean configPorts = testMapping.isUseDefaultConfigPorts();
        boolean configEdges = testMapping.isUseDefaultConfigEdges();
        
        if (configNodes || configPorts || configEdges) {
            for (ElkNode elkNode : parent.getChildren()) {
                if (configNodes) {
                    ElkUtil.configureWithDefaultValues(elkNode);
                }
                
                if (configPorts || configEdges) {
                    for (ElkPort elkPort : elkNode.getPorts()) {
                        if (configPorts) {
                            ElkUtil.configureWithDefaultValues(elkPort);
                        }
                        
                        if (configEdges) {
                            for (ElkEdge elkEdge : elkPort.getOutgoingEdges()) {
                                ElkUtil.configureWithDefaultValues(elkEdge);
                            }
                        }
                    }
                }
                
                if (configEdges) {
                    for (ElkEdge elkEdge : elkNode.getOutgoingEdges()) {
                        ElkUtil.configureWithDefaultValues(elkEdge);
                    }
                }
                
                applyDefaults(elkNode, testMapping);
            }
        }
    }
}
