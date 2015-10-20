/*******************************************************************************
 * Copyright (c) 2011, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.elk.core.LayoutConfigService;
import org.eclipse.elk.core.LayoutMetaDataService;
import org.eclipse.elk.core.LayoutOptionData;
import org.eclipse.elk.core.config.CompoundLayoutConfig;
import org.eclipse.elk.core.config.DefaultLayoutConfig;
import org.eclipse.elk.core.config.ILayoutConfig;
import org.eclipse.elk.core.config.IMutableLayoutConfig;
import org.eclipse.elk.core.config.LayoutContext;
import org.eclipse.elk.core.klayoutdata.KLayoutData;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KGraphElement;
import org.eclipse.elk.graph.KLabel;
import org.eclipse.elk.graph.KNode;
import org.eclipse.elk.graph.KPort;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.IWorkbenchPart;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * The main class for configuration of KGraph instances. Configuration means the annotation of
 * graph elements with layout options for selecting layout algorithms and setting parameters
 * for their execution. This is done through <em>layout configurators</em>, which are executed
 * as part of a {@link CompoundLayoutConfig}.
 * The {@link #configure(LayoutMapping, IElkProgressMonitor) configure} method should be
 * called before a graph layout engine is executed for a layout graph.
 * 
 * @author msp
 * @see org.eclipse.elk.core.options.LayoutOptions
 * @see org.eclipse.elk.core.config.ILayoutConfig
 * @kieler.design proposed by msp
 * @kieler.rating yellow 2012-07-05 review KI-18 by cmot, sgu
 */
public class LayoutOptionManager {

    /** internal cache of semantic layout configurations. */
    private final Map<Class<?>, List<ILayoutConfig>> semanticConfigMap = Maps.newHashMap();
    
    /** the default layout configuration. */
    private final DefaultLayoutConfig defaultLayoutConfig = new DefaultLayoutConfig();

    /**
     * Configure the layout graph in the given layout mapping.
     * 
     * @param layoutMapping a layout mapping
     * @param progressMonitor a progress monitor
     */
    public void configure(final LayoutMapping<?> layoutMapping,
            final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Layout configuration", 1);
        
        // create basic layout configuration
        CompoundLayoutConfig clc = new CompoundLayoutConfig();
        clc.add(defaultLayoutConfig);
        clc.addAll(ExtensionLayoutConfigService.getInstance().getActiveConfigs());
        clc.addAll(layoutMapping.getLayoutConfigs());

        // configure the layout graph recursively
        KNode layoutGraph = layoutMapping.getLayoutGraph();
        recursiveConf(layoutGraph, layoutMapping, clc);
        
        progressMonitor.done();
    }

    /**
     * Create a layout configuration that can be used to access all actual values of layout options.
     * 
     * @param domainElement
     *            a domain model element, or {@code null}
     * @param extraConfigs
     *            optional additional layout configurations to include
     * @return a complete layout configuration
     */
    public IMutableLayoutConfig createConfig(final Object domainElement,
            final ILayoutConfig... extraConfigs) {
        CompoundLayoutConfig clc = new CompoundLayoutConfig();
        clc.add(defaultLayoutConfig);
        clc.addAll(ExtensionLayoutConfigService.getInstance().getActiveConfigs());
        clc.addAll(getSemanticConfigs(domainElement));
        for (ILayoutConfig conf : extraConfigs) {
            clc.add(conf);
        }
        return clc;
    }
    
    /**
     * Retrieve a global option value from the given configurator.
     * 
     * @param option a property that defines a layout option
     * @param config the layout configurator for getting the option value
     * @return the global option value stored in the configurator, or the default value
     * @param <T> the type of the given option
     */
    @SuppressWarnings("unchecked")
    public <T> T getGlobalValue(final IProperty<T> option, final ILayoutConfig config) {
        if (config != null) {
            LayoutOptionData optionData = LayoutMetaDataService.getInstance().getOptionData(
                    option.getId());
            if (optionData != null) {
                Object value = config.getOptionValue(optionData, LayoutContext.global());
                if (value != null) {
                    return (T) value;
                }
            }
        }
        return option.getDefault();
    }

    /**
     * Configure all elements contained in the given node.
     * 
     * @param node
     *            a node from the layout graph
     * @param layoutMapping
     *            a layout mapping
     * @param config
     *            the basic layout configuration
     */
    private void recursiveConf(final KNode node, final LayoutMapping<?> layoutMapping,
            final CompoundLayoutConfig config) {
        // configure the node and its label
        configure(node, layoutMapping, config);
        for (KLabel label : node.getLabels()) {
            configure(label, layoutMapping, config);
        }

        // configure ports
        for (KPort port : node.getPorts()) {
            configure(port, layoutMapping, config);
            for (KLabel label : port.getLabels()) {
                configure(label, layoutMapping, config);
            }
        }

        // configure outgoing edges
        for (KEdge edge : node.getOutgoingEdges()) {
            configure(edge, layoutMapping, config);
            for (KLabel label : edge.getLabels()) {
                configure(label, layoutMapping, config);
            }
        }

        // configure child nodes
        for (KNode child : node.getChildren()) {
            recursiveConf(child, layoutMapping, config);
        }
    }
    
    private LayoutOptionData resetConfigOption;

    /**
     * Configure a graph element.
     * 
     * @param graphElement
     *            a graph element
     * @param layoutMapping
     *            a layout mapping
     * @param config
     *            the basic layout configuration
     */
    private void configure(final KGraphElement graphElement, final LayoutMapping<?> layoutMapping,
            final CompoundLayoutConfig config) {
        // create a layout context for the current graph element and initialize basic properties
        LayoutContext context = new LayoutContext();
        context.setProperty(LayoutContext.GRAPH_ELEM, graphElement);
        Object diagramPart = layoutMapping.getGraphMap().get(graphElement);
        context.setProperty(LayoutContext.DIAGRAM_PART, diagramPart);
        IWorkbenchPart workbenchPart = (IWorkbenchPart) Iterables.find(
                layoutMapping.getAllProperties().values(),
                Predicates.instanceOf(IWorkbenchPart.class), null);
        context.setProperty(EclipseLayoutConfig.WORKBENCH_PART, workbenchPart);
        Object modelElement = config.getContextValue(LayoutContext.DOMAIN_MODEL, context);
        context.setProperty(LayoutContext.DOMAIN_MODEL, modelElement);
        
        // add semantic configurations from the extension point
        List<ILayoutConfig> semanticConfigs = getSemanticConfigs(modelElement);
        config.addAll(semanticConfigs);

        // enrich the layout context using the basic configuration
        enrich(context, config, false);

        // clear the previous configuration, unless specified otherwise by 
        // a layout option
        KLayoutData layoutData = graphElement.getData(KLayoutData.class);
        if (resetConfigOption == null) {
            resetConfigOption =
                    LayoutMetaDataService.getInstance().getOptionData(
                            LayoutOptions.RESET_CONFIG.getId());
        }
        if (resetConfigOption == null 
                || (Boolean) config.getOptionValue(resetConfigOption, context)) {
            layoutData.getProperties().clear();
        }
        
        // transfer the options from the layout configuration
        transferValues(layoutData, config, context);

        // remove the semantic layout configurations again
        config.removeAll(semanticConfigs);
    }

    /**
     * Get a list of semantic layout configurations for the given model element, using a cached
     * value if available.
     * 
     * @param modelElement
     *            a domain model element
     * @return the list of semantic layout configurations, or an empty list if the model element
     *          is {@code null}
     */
    private List<ILayoutConfig> getSemanticConfigs(final Object modelElement) {
        if (modelElement == null) {
            return Collections.emptyList();
        }
        Class<?> clazz = modelElement.getClass();
        List<ILayoutConfig> configs = semanticConfigMap.get(clazz);
        if (configs == null) {
            if (modelElement instanceof EObject) {
                configs = LayoutConfigService.getInstance().getSemanticConfigs(
                        ((EObject) modelElement).eClass());
            } else {
                configs = LayoutConfigService.getInstance().getSemanticConfigs(clazz);
            }
            semanticConfigMap.put(clazz, configs);
        }
        return configs;
    }
    
    /**
     * Enrich the given layout context by querying the given configurator for context properties.
     * 
     * @param context a layout context
     * @param config a layout configurator
     * @param makeOptionsList whether to create the {@link DefaultLayoutConfig#OPTIONS} list
     */
    public void enrich(final LayoutContext context, final ILayoutConfig config,
            final boolean makeOptionsList) {
        // check whether the configurator proposes a replacement for the diagram part
        Object diagramPart = config.getContextValue(LayoutContext.DIAGRAM_PART, context);
        if (diagramPart != null) {
            context.setProperty(LayoutContext.DIAGRAM_PART, diagramPart);
        }
        // enrich the domain model element
        enrich(LayoutContext.DOMAIN_MODEL, context, config);
        // enrich the layout option targets
        enrich(LayoutContext.OPT_TARGETS, context, config);
        Set<LayoutOptionData.Target> targets = context.getProperty(LayoutContext.OPT_TARGETS);
        if (targets.contains(LayoutOptionData.Target.NODES)) {
            // enrich the property indicating whether the selected node has ports
            enrich(DefaultLayoutConfig.HAS_PORTS, context, config);
        }
        if (targets.contains(LayoutOptionData.Target.PARENTS)) {
            // enrich the aspect ratio of the diagram viewer
            enrich(EclipseLayoutConfig.ASPECT_RATIO, context, config);
            // enrich the diagram type for the selected element
            enrich(DefaultLayoutConfig.CONTENT_DIAGT, context, config);
        }
        
        if (makeOptionsList) {
            // enrich the container diagram part
            enrich(LayoutContext.CONTAINER_DIAGRAM_PART, context, config);
            // enrich the container domain model element
            enrich(LayoutContext.CONTAINER_DOMAIN_MODEL, context, config);
            // enrich the layout hint for the selected element
            enrich(DefaultLayoutConfig.CONTENT_HINT, context, config);
            // enrich the layout hint for the container element
            enrich(DefaultLayoutConfig.CONTAINER_HINT, context, config);
            // enrich the diagram type for the container element
            enrich(DefaultLayoutConfig.CONTAINER_DIAGT, context, config);
            // enrich the editing domain
            enrich(EclipseLayoutConfig.EDITING_DOMAIN, context, config);
            // enrich the layout algorithm for the selected element
            enrich(DefaultLayoutConfig.CONTENT_ALGO, context, defaultLayoutConfig);
            // enrich the layout algorithm for the container element
            enrich(DefaultLayoutConfig.CONTAINER_ALGO, context, defaultLayoutConfig);
            // enrich the list of supported layout options
            enrich(DefaultLayoutConfig.OPTIONS, context, defaultLayoutConfig);
        }
    }
    
    /**
     * Enrich the given layout context with respect to the given property.
     * 
     * @param property a context property
     * @param context a layout context
     * @param config the configurator queried for the context property
     */
    @SuppressWarnings("unchecked")
    private <T> void enrich(final IProperty<T> property, final LayoutContext context,
            final ILayoutConfig config) {
        if (context.getProperty(property) == null) {
            // warning: evil configurators may return objects of the wrong type here
            context.setProperty(property, (T) config.getContextValue(property, context));
        }
    }
    
    /**
     * Transfer all layout options affected by the given configurator to the layout data instance.
     * 
     * @param layoutData a layout data instance of a graph element
     * @param config a layout configurator
     * @param context the context under which to fetch the options
     */
    @SuppressWarnings("unchecked")
    public void transferValues(final KLayoutData layoutData, final ILayoutConfig config,
            final LayoutContext context) {
        LayoutMetaDataService dataService = LayoutMetaDataService.getInstance();
        Collection<IProperty<?>> options = config.getAffectedOptions(context);
        for (IProperty<?> option : options) {
            Object value = null;
            if (option instanceof LayoutOptionData) {
                value = config.getOptionValue((LayoutOptionData) option, context);
            } else {
                LayoutOptionData optionData = dataService.getOptionData(option.getId());
                if (optionData != null) {
                    value = config.getOptionValue(optionData, context);
                }
            }
            if (value != null) {
                layoutData.setProperty((IProperty<Object>) option, value);
            }
        }
    }

}
