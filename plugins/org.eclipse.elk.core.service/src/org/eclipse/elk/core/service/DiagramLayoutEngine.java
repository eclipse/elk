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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.elk.core.IGraphLayoutEngine;
import org.eclipse.elk.core.ILayoutAlgorithmData;
import org.eclipse.elk.core.LayoutConfigurator;
import org.eclipse.elk.core.RecursiveGraphLayoutEngine;
import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.service.data.LayoutOptionData;
import org.eclipse.elk.core.service.util.MonitoredOperation;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.core.util.IElkCancelIndicator;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.core.util.IGraphElementVisitor;
import org.eclipse.elk.core.util.Maybe;
import org.eclipse.elk.core.util.Pair;
import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KGraphElement;
import org.eclipse.elk.graph.KLabel;
import org.eclipse.elk.graph.KNode;
import org.eclipse.elk.graph.KPort;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.IPropertyHolder;
import org.eclipse.elk.graph.properties.MapPropertyHolder;
import org.eclipse.elk.graph.properties.Property;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.statushandlers.StatusManager;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * The entry class for automatic layout of graphical diagrams.
 * Use this class to perform automatic layout on the content of a workbench part that contains
 * a graph-based diagram. The mapping between the diagram and the layout graph structure is managed
 * by a {@link IDiagramLayoutManager} implementation, which has to be registered using the
 * {@code layoutManagers} extension point.
 * 
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating yellow 2012-07-05 review KI-18 by cmot, sgu
 */
public class DiagramLayoutEngine {
    
    /**
     * The singleton instance that can be used whenever layout needs to be performed.
     */
    public static final DiagramLayoutEngine INSTANCE = new DiagramLayoutEngine();
    
    /**
     * Filter for {@link LayoutConfigurator} that checks for each option whether its configured targets
     * match the input element.
     */
    public static final Predicate<Pair<KGraphElement, IProperty<?>>> OPTION_TARGET_FILTER =
            new Predicate<Pair<KGraphElement, IProperty<?>>>() {
        public boolean apply(Pair<KGraphElement, IProperty<?>> input) {
            LayoutOptionData optionData = LayoutMetaDataService.getInstance().getOptionData(input.getSecond().getId());
            if (optionData != null) {
                KGraphElement e = input.getFirst();
                Set<LayoutOptionData.Target> targets = optionData.getTargets();
                if (e instanceof KNode) {
                    if (((KNode) e).getChildren().isEmpty()) {
                        return targets.contains(LayoutOptionData.Target.NODES);
                    } else {
                        return targets.contains(LayoutOptionData.Target.NODES)
                                || targets.contains(LayoutOptionData.Target.PARENTS);
                    }
                } else if (e instanceof KEdge) {
                    return targets.contains(LayoutOptionData.Target.EDGES);
                } else if (e instanceof KPort) {
                    return targets.contains(LayoutOptionData.Target.PORTS);
                } else if (e instanceof KLabel) {
                    return targets.contains(LayoutOptionData.Target.LABELS);
                }
            }
            return true;
        }
    };
    
    /**
     * Property for the diagram layout manager used for automatic layout. This property is
     * attached to layout mappings created by the {@code layout} methods.
     */
    public static final IProperty<IDiagramLayoutManager<?>> DIAGRAM_LM
            = new Property<IDiagramLayoutManager<?>>("layoutEngine.diagramLayoutManager");
    
    /** preference identifier for debug graph output. */
    public static final String PREF_DEBUG_OUTPUT = "kiml.debug.graph";
    /** preference identifier for execution time measurement. */
    public static final String PREF_EXEC_TIME_MEASUREMENT = "kiml.exectime.measure";
    
    /**
     * Configuration class for invoking the {@link DiagramLayoutEngine}.
     * Use a {@link LayoutConfigurator} to configure layout options:
     * <pre>
     * DiagramLayoutEngine.Setup setup = new DiagramLayoutEngine.Setup();
     * setup.addLayoutRun().configure(KNode.class)
     *         .setProperty(LayoutOptions.ALGORITHM, "org.eclipse.elk.algorithm.layered")
     *         .setProperty(LayoutOptions.SPACING, 30.0f)
     *         .setProperty(LayoutOptions.ANIMATE, true);
     * DiagramLayoutEngine.INSTANCE.layout(workbenchPart, diagramPart, setup);
     * </pre>
     * If multiple configurators are given, the layout is computed multiple times:
     * once for each configurator. This behavior can be used to apply different layout algorithms
     * one after another, e.g. first a node placer algorithm and then an edge router algorithm.
     * Example:
     * <pre>
     * DiagramLayoutEngine.Setup setup = new DiagramLayoutEngine.Setup();
     * setup.addLayoutRun().configure(KNode.class)
     *         .setProperty(LayoutOptions.ALGORITHM, "org.eclipse.elk.algorithm.force");
     * setup.addLayoutRun().setClearLayout(true).configure(KNode.class)
     *         .setProperty(LayoutOptions.ALGORITHM, "de.cau.cs.kieler.kiml.libavoid");
     * DiagramLayoutEngine.INSTANCE.layout(workbenchPart, diagramPart, setup);
     * </pre>
     * <b>Note:</b> By using the {@link LayoutConfigurator} approach as shown above, the Layout
     * view does not have any access to the configured values and hence will not work correctly.
     * In order to support the Layout view, use the {@link ILayoutConfigurationStore} interface instead.
     */
    public static class Setup {
        
        private List<LayoutConfigurator> configurators = new LinkedList<LayoutConfigurator>();
        private MapPropertyHolder globalSettings = new MapPropertyHolder();
        private boolean overrideDiagramConfig = true;
        
        /**
         * Set whether to override the configuration from the {@link ILayoutConfigurationStore}
         * provided by the diagram layout manager with the configuration provided by this setup.
         * The default is {@code true}.
         */
        public Setup setOverrideDiagramConfig(boolean override) {
            this.overrideDiagramConfig = override;
            return this;
        }
        
        /**
         * Returns the global settings, i.e. options that are not processed by layout algorithms,
         * but by layout managers or the layout engine itself.
         */
        public IPropertyHolder getGlobalSettings() {
            return globalSettings;
        }
        
        /**
         * Add a layout run with the given configurator. Each invocation of this method corresponds
         * to a separate layout execution on the whole input graph. This can be used to apply
         * multiple layout algorithms one after another, where each algorithm execution can reuse
         * results from previous executions.
         * 
         * @return the given configurator
         */
        public LayoutConfigurator addLayoutRun(LayoutConfigurator configurator) {
            configurators.add(configurator);
            configurator.setFilter(OPTION_TARGET_FILTER);
            return configurator;
        }
        
        /**
         * Convenience method for {@code addLayout(new LayoutConfigurator())}.
         */
        public LayoutConfigurator addLayoutRun() {
            return addLayoutRun(new LayoutConfigurator());
        }
    }
    
    private final LayoutConfigurationManager configManager = new LayoutConfigurationManager();
    
    /** the graph layout engine for executing layout algorithms on the hierarchy levels of a graph. */
    private final IGraphLayoutEngine graphLayoutEngine = new RecursiveGraphLayoutEngine((String input) -> {
        return configManager.getAlgorithm(input);
    });
    
    /** the executor service used to perform layout operations. */
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    
    /** map of currently running layout operations. */
    private final Multimap<Pair<IWorkbenchPart, Object>, MonitoredOperation> runningOperations
            = HashMultimap.create();

    /**
     * Perform layout on the given workbench part with the given global options.
     * 
     * @param workbenchPart
     *            the workbench part for which layout is performed
     * @param diagramPart
     *            the parent diagram part for which layout is performed, or {@code null} if the whole
     *            diagram shall be arranged
     * @param animate
     *            if true, animation is activated
     * @param progressBar
     *            if true, a progress bar is displayed
     * @param layoutAncestors
     *            if true, layout is not only performed for the selected diagram part, but also for
     *            its ancestors
     * @param zoomToFit
     *            if true, automatic zoom-to-fit is activated
     * @return the layout mapping used in this session
     */
    public LayoutMapping<?> layout(final IWorkbenchPart workbenchPart, final Object diagramPart,
            final boolean animate, final boolean progressBar, final boolean layoutAncestors,
            final boolean zoomToFit) {
        Setup setup = new Setup();
        setup.getGlobalSettings()
            .setProperty(LayoutOptions.ANIMATE, animate)
            .setProperty(LayoutOptions.PROGRESS_BAR, progressBar)
            .setProperty(LayoutOptions.LAYOUT_ANCESTORS, layoutAncestors)
            .setProperty(LayoutOptions.ZOOM_TO_FIT, zoomToFit);
        return layout(workbenchPart, diagramPart, setup);
    }
    
    /**
     * Perform layout on the given workbench part with the given setup.
     * 
     * @param workbenchPart
     *            the workbench part for which layout is performed
     * @param diagramPart
     *            the parent diagram part for which layout is performed, or {@code null} if the whole
     *            diagram shall be layouted
     * @param setup
     *            a layout setup, or {@code null} to use default values
     * @return the layout mapping used in this session
     */
    public LayoutMapping<?> layout(final IWorkbenchPart workbenchPart, final Object diagramPart,
            final Setup setup) {
        return layout(workbenchPart, diagramPart, (IElkCancelIndicator) null, setup);
    }

    /**
     * Perform layout on the given workbench part.
     * 
     * @param workbenchPart
     *            the workbench part for which layout is performed
     * @param diagramPart
     *            the parent diagram part for which layout is performed, or {@code null} if the whole
     *            diagram shall be layouted
     * @param cancelIndicator
     *            an {@link IElkCancelIndicator} evaluated repeatedly during the layout
     *            performance, or {@code null}
     * @param setup
     *            a layout setup, or {@code null} to use default values
     * @return the layout mapping used in this session
     */
    public LayoutMapping<?> layout(final IWorkbenchPart workbenchPart, final Object diagramPart,
            final IElkCancelIndicator cancelIndicator, Setup setup) {
        IDiagramLayoutManager<?> layoutManager = 
                LayoutManagersService.getInstance().getManager(workbenchPart, diagramPart);

        if (layoutManager != null) {
            if (setup == null) {
                setup = new Setup();
            }
            LayoutMapping<?> mapping = layout(
                    layoutManager, workbenchPart, diagramPart, cancelIndicator, setup);
            if (mapping != null) {
                mapping.setProperty(DIAGRAM_LM, layoutManager);
            }
            return mapping;
        } else {
            IStatus status = new Status(IStatus.ERROR, ElkServicePlugin.PLUGIN_ID,
                    workbenchPart == null
                    ? "No layout manager is available for the selected part."
                    : "No layout manager is available for " + workbenchPart.getTitle() + ".");
            StatusManager.getManager().handle(status, StatusManager.SHOW);
            return null;
        }
    }

    /**
     * Perform layout on the given workbench part using the given layout manager. If zero or one
     * layout configurator is passed, the layout engine is executed exactly once. If multiple
     * layout configurators are passed, the layout engine is executed accordingly often,
     * but the resulting layout is applied only once. This is useful for composition of multiple
     * algorithms that process only parts of the graph.
     * 
     * @param <T> the type of diagram part that is handled by the given diagram layout manager
     * @param layoutManager
     *            a diagram layout manager
     * @param workbenchPart
     *            the workbench part for which layout is performed
     * @param diagramPart
     *            the parent diagram part for which layout is performed, or {@code null} if the whole
     *            diagram shall be layouted
     * @param cancelIndicator
     *            an {@link IElkCancelIndicator} evaluated repeatedly during the layout
     *            performance, or <code>null</code>
     * @param setup
     *            the layout setup
     * @return the layout mapping used in this session
     */
    protected <T> LayoutMapping<T> layout(final IDiagramLayoutManager<T> layoutManager,
            final IWorkbenchPart workbenchPart, final Object diagramPart,
            final IElkCancelIndicator cancelIndicator, final Setup setup) {
        final Maybe<LayoutMapping<T>> layoutMapping = Maybe.create();
        final Pair<IWorkbenchPart, Object> target = Pair.of(workbenchPart, diagramPart);
        
        final MonitoredOperation monitoredOperation = new MonitoredOperation(executorService, cancelIndicator) {
            
            // First phase: build the layout graph
            @SuppressWarnings("unchecked")
            @Override
            protected void preUIexec() {
                boolean layoutAncestors = setup.getGlobalSettings().getProperty(LayoutOptions.LAYOUT_ANCESTORS);
                LayoutMapping<T> mapping;
                if (layoutAncestors) {
                    mapping = layoutManager.buildLayoutGraph(workbenchPart, null);
                    mapping.setParentElement((T) diagramPart);
                } else {
                    mapping = layoutManager.buildLayoutGraph(workbenchPart, diagramPart);
                }
                layoutMapping.set(mapping);
            }

            // Second phase: execute layout algorithms
            @Override
            protected IStatus execute(final IElkProgressMonitor monitor) {
                if (monitor.isCanceled()) {
                    return Status.CANCEL_STATUS;
                }

                LayoutMapping<T> mapping = layoutMapping.get();
                IStatus status;
                if (mapping != null && mapping.getLayoutGraph() != null) {
                    // Extract the diagram configuration
                    addDiagramConfig(setup, layoutManager, mapping);
                    
                    // Perform the actual layout
                    status = layout(mapping, monitor, setup);
                    
                    // Stop earlier layout operations that are still running
                    if (!monitor.isCanceled()) {
                        stopEarlierOperations(target, getTimestamp());
                    }
                } else {
                    status = new Status(Status.WARNING, ElkServicePlugin.PLUGIN_ID,
                            "Unable to build the layout graph from the given workbench part.");
                }
                
                monitor.done();
                return status;
            }

            // Third phase: apply layout with animation
            @Override
            protected void postUIexec() {
                if (layoutMapping.get() != null) {
                    layoutManager.applyLayout(layoutMapping.get(), setup.getGlobalSettings());
                }
            }
        };
        
        synchronized (runningOperations) {
            runningOperations.put(target, monitoredOperation);
        }

        try {
            boolean progressBar = setup.getGlobalSettings().getProperty(LayoutOptions.PROGRESS_BAR);
            if (progressBar) {
                // Perform layout with a progress bar
                monitoredOperation.runMonitored();
            } else {
                // Perform layout without a progress bar
                monitoredOperation.runUnmonitored();
            }
        } finally {
            synchronized (runningOperations) {
                runningOperations.remove(target, monitoredOperation);
            }
        }
        
        return layoutMapping.get();
    }
    
    /**
     * Stop all running operations whose timestamp is earlier than the given one.
     * 
     * @param target the layout target
     * @param time operations with a timestamp that is less than this are stopped
     */
    private void stopEarlierOperations(final Pair<IWorkbenchPart, Object> target, final long time) {
        synchronized (runningOperations) {
            for (MonitoredOperation operation : runningOperations.get(target)) {
                if (operation.getTimestamp() < time) {
                    operation.cancel();
                }
            }
        }
    }
    
    /**
     * Perform layout with a given progress monitor, possibly without a workbench part.
     * 
     * @param workbenchPart
     *            the workbench part for which layout is performed
     * @param diagramPart
     *            the parent diagram part for which layout is performed, or {@code null} if the whole
     *            diagram shall be layouted
     * @param progressMonitor
     *            a progress monitor to which progress of the layout algorithm is reported,
     *            or {@code null} if no progress reporting is required
     * @return a status indicating success or failure
     */
    public IStatus layout(final IWorkbenchPart workbenchPart, final Object diagramPart,
            final IElkProgressMonitor progressMonitor) {
        return layout(workbenchPart, diagramPart, progressMonitor, null);
    }
    
    /**
     * Perform layout with a given progress monitor, possibly without a workbench part.
     * 
     * @param workbenchPart
     *            the workbench part for which layout is performed
     * @param diagramPart
     *            the parent diagram part for which layout is performed, or {@code null} if the whole
     *            diagram shall be layouted
     * @param progressMonitor
     *            a progress monitor to which progress of the layout algorithm is reported,
     *            or {@code null} if no progress reporting is required
     * @param setup
     *            a layout setup, or {@code null} to use the default values
     * @return a status indicating success or failure
     */
    public IStatus layout(final IWorkbenchPart workbenchPart, final Object diagramPart,
            final IElkProgressMonitor progressMonitor, Setup setup) {
        IDiagramLayoutManager<?> layoutManager = LayoutManagersService.getInstance().getManager(
                workbenchPart, diagramPart);
        if (layoutManager != null) {
            if (setup == null) {
                setup = new Setup();
            }
            return layout(layoutManager, workbenchPart, diagramPart, progressMonitor, setup);
        } else {
            return new Status(IStatus.ERROR, ElkServicePlugin.PLUGIN_ID,
                    "No layout manager is available for the selected part.");
        }
    }
    
    /**
     * Perform layout with a given progress monitor using the given layout manager, possibly
     * without a workbench part.
     * 
     * @param <T> the type of diagram part that is handled by the given diagram layout manager
     * @param layoutManager
     *            a diagram layout manager
     * @param workbenchPart
     *            the workbench part for which layout is performed
     * @param diagramPart
     *            the parent diagram part for which layout is performed, or {@code null} if the whole
     *            diagram shall be layouted
     * @param progressMonitor
     *            a progress monitor to which progress of the layout algorithm is reported,
     *            or {@code null} if no progress reporting is required
     * @param setup
     *            the layout setup
     * @return a status indicating success or failure
     */
    protected <T> IStatus layout(final IDiagramLayoutManager<T> layoutManager,
            final IWorkbenchPart workbenchPart, final Object diagramPart,
            final IElkProgressMonitor progressMonitor, Setup setup) {
        IElkProgressMonitor monitor;
        if (progressMonitor == null) {
            monitor = new BasicProgressMonitor(0, ElkServicePlugin.getDefault().getPreferenceStore()
                    .getBoolean(PREF_EXEC_TIME_MEASUREMENT));
        } else {
            monitor = progressMonitor;
        }
        // SUPPRESS CHECKSTYLE NEXT MagicNumber
        monitor.begin("Layout Diagram", 3);
        
        // Build the layout graph
        IElkProgressMonitor submon1 = monitor.subTask(1);
        submon1.begin("Build layout graph", 1);
        LayoutMapping<T> mapping = layoutManager.buildLayoutGraph(workbenchPart, diagramPart);
        
        IStatus status;
        if (mapping != null && mapping.getLayoutGraph() != null) {
            // Extract the diagram configuration
            addDiagramConfig(setup, layoutManager, mapping);
            submon1.done();
            
            // Perform the actual layout
            status = layout(mapping, monitor.subTask(1), setup);
            
            // Apply the layout to the diagram
            IElkProgressMonitor submon3 = monitor.subTask(1);
            submon3.begin("Apply layout to the diagram", 1);
            layoutManager.applyLayout(mapping, setup.getGlobalSettings());
            submon3.done();
        } else {
            status = new Status(Status.WARNING, ElkServicePlugin.PLUGIN_ID,
                    "Unable to build the layout graph from the given workbench part.");
        }
        
        monitor.done();
        return status;
    }
    
    /**
     * Create a diagram layout configuration and add it to the setup.
     */
    protected <T> void addDiagramConfig(final Setup setup, final IDiagramLayoutManager<T> layoutManager,
            final LayoutMapping<T> layoutMapping) {
        LayoutConfigurator diagramConfig = configManager.createConfigurator(layoutManager, layoutMapping);
        if (setup.configurators.isEmpty()) {
            setup.addLayoutRun(diagramConfig);
        } else {
            ListIterator<LayoutConfigurator> configIter = setup.configurators.listIterator();
            while (configIter.hasNext()) {
                boolean isFirstConfig = !configIter.hasPrevious();
                LayoutConfigurator setupConfig = configIter.next();
                if (setup.overrideDiagramConfig) {
                    if (isFirstConfig || setupConfig.isClearLayout()) {
                        LayoutConfigurator newConfig;
                        if (configIter.hasNext()) {
                            newConfig = new LayoutConfigurator().overrideWith(diagramConfig);
                        } else {
                            newConfig = diagramConfig;
                        }
                        configIter.set(newConfig.overrideWith(setupConfig));
                    }
                } else {
                    setupConfig.overrideWith(diagramConfig);
                }
            }
        }
    }
    
    /**
     * Perform layout on the given layout graph mapping. If zero or one layout configurator is
     * passed, the layout engine is executed exactly once. If multiple layout configurators are
     * passed, the layout engine is executed accordingly often, but the resulting layout is applied
     * only once. This is useful for composition of multiple algorithms that process only parts of
     * the graph. Layout listeners are notified after the layout has been computed.
     * 
     * @param mapping
     *            a mapping for the layout graph
     * @param progressMonitor
     *            a progress monitor to which progress of the layout algorithm is reported
     * @param setup
     *            a layout setup, or {@code null} to use default values
     * @return a status indicating success or failure
     */
    public IStatus layout(final LayoutMapping<?> mapping, final IElkProgressMonitor progressMonitor,
            final Setup setup) {
        
        boolean layoutAncestors = setup.getGlobalSettings().getProperty(LayoutOptions.LAYOUT_ANCESTORS);
        if (layoutAncestors) {
            // Mark all parallel areas for exclusion from layout
            KGraphElement graphElem = mapping.getGraphMap().inverse().get(mapping.getParentElement());
            if (graphElem instanceof KNode && ((KNode) graphElem).getParent() != null) {
                if (setup.configurators.isEmpty()) {
                    setup.configurators.add(new LayoutConfigurator());
                }
                KNode node = (KNode) graphElem;
                do {
                    KNode parent = node.getParent();
                    for (KNode child : parent.getChildren()) {
                        if (child != node) {
                            for (LayoutConfigurator c : setup.configurators) {
                                IPropertyHolder childConfig = c.configure(child);
                                // Do not layout the content of the child node
                                childConfig.setProperty(LayoutOptions.NO_LAYOUT, true);
                                // Do not change the size of the child node
                                childConfig.setProperty(LayoutOptions.SIZE_CONSTRAINT, SizeConstraint.fixed());
                                // Do not move the ports of the child node
                                childConfig.setProperty(LayoutOptions.PORT_CONSTRAINTS, PortConstraints.FIXED_POS);
                            }
                        }
                    }
                    node = parent;
                } while (node.getParent() != null);
            }
        }
        
        IStatus status = null;
        if (setup.configurators.isEmpty()) {
            // Perform layout without any extra configuration
            status = layout(mapping, progressMonitor);
        } else if (setup.configurators.size() == 1) {
            // Perform layout once with an extra configuration
            status = layout(mapping, progressMonitor, setup.configurators.get(0));
        } else {
            // Perform layout multiple times with different configurations
            progressMonitor.begin("Diagram layout engine", TOTAL_WORK * setup.configurators.size());
            ListIterator<LayoutConfigurator> configIter = setup.configurators.listIterator();
            while (configIter.hasNext()) {
                status = layout(mapping, progressMonitor, configIter.next());
                if (!status.isOK()) {
                    break;
                }
                
                // If an additional layout configurator is attached to the graph, consider it in the future
                LayoutConfigurator addConfig = mapping.getLayoutGraph().getData(KShapeLayout.class).getProperty(
                        LayoutConfigurator.ADD_LAYOUT_CONFIG);
                if (addConfig != null) {
                    ListIterator<LayoutConfigurator> configIter2 = setup.configurators.listIterator(configIter.nextIndex());
                    while (configIter2.hasNext()) {
                        configIter2.next().overrideWith(addConfig);
                    }
                }
            }
            progressMonitor.done();
        }

        // Notify listeners of the executed layout
        if (layoutListeners != null) {
            for (final ILayoutDoneListener listener : layoutListeners) {
                listener.layoutDone(mapping.getLayoutGraph(), progressMonitor);
            }
        }
        return status;
    }
    
    private static final float CONFIGURE_WORK = 1;
    private static final float LAYOUT_WORK = 4;
    private static final float TOTAL_WORK = CONFIGURE_WORK + LAYOUT_WORK;
    
    /**
     * Perform layout on the given layout graph mapping. Layout listeners are <em>not</em> notified
     * in this method.
     * 
     * @param mapping
     *            a mapping for the layout graph
     * @param progressMonitor
     *            a progress monitor to which progress of the layout algorithm is reported; if the
     *            given monitor is not yet started, it is started in this method
     * @param visitors
     *            an optional array of graph element visitors to apply
     * @return a status indicating success or failure
     */
    public IStatus layout(final LayoutMapping<?> mapping,
            final IElkProgressMonitor progressMonitor, final IGraphElementVisitor... visitors) {

        if (progressMonitor.isCanceled()) {
            return Status.CANCEL_STATUS;
        }
        boolean newTask = progressMonitor.begin("Diagram layout engine", TOTAL_WORK);
        
        try {
            // Configure the layout graph by applying the given visitors
            if (visitors.length > 0) {
                ElkUtil.applyVisitors(mapping.getLayoutGraph(), visitors);
            }
            
            // Export the layout graph for debugging
            if (ElkServicePlugin.getDefault().getPreferenceStore().getBoolean(PREF_DEBUG_OUTPUT)) {
                exportLayoutGraph(mapping.getLayoutGraph());
            }

            // Perform layout on the layout graph
            graphLayoutEngine.layout(mapping.getLayoutGraph(), progressMonitor.subTask(LAYOUT_WORK));
            
            if (newTask) {
                progressMonitor.done();
            }
            if (progressMonitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            }
            
            // return a positive status
            return Status.OK_STATUS;
            
        } catch (Throwable exception) {
            return new Status(IStatus.ERROR, ElkServicePlugin.PLUGIN_ID,
                    "Failed to perform diagram layout.", exception);
        }
    }
    
    /**
     * Listener interface for graph layout. Implementations must not modify the graph in any way
     * and should execute as quickly as possible.
     */
    public interface ILayoutDoneListener {
        /**
         * Called when layout has been done on a graph.
         * 
         * @param layoutGraph the parent node of the graph
         * @param monitor the progress monitor with information on the executed layout
         */
        void layoutDone(KNode layoutGraph, IElkProgressMonitor monitor);
    }
    
    /** list of registered layout listeners. */
    private List<ILayoutDoneListener> layoutListeners = null;
    
    /**
     * Add the given object to the list of layout listeners.
     *
     * @param listener a listener
     */
    public void addLayoutDoneListener(final ILayoutDoneListener listener) {
        if (layoutListeners == null) {
            layoutListeners = new LinkedList<ILayoutDoneListener>();
        }
        layoutListeners.add(listener);
    }

    /**
     * Remove the given object from the list of layout listeners.
     *
     * @param listener a listener
     */
    public void removeLayoutDoneListener(final ILayoutDoneListener listener) {
        if (layoutListeners == null) {
            return;
        }
        layoutListeners.remove(listener);
    }

    /**
     * Export the given layout graph in KGraph format.
     * 
     * @param graph the parent node of the layout graph
     */
    private void exportLayoutGraph(final KNode graph) {
        String path = System.getProperty("user.home");
        if (path != null) {
            if (path.endsWith(File.separator)) {
                path += "tmp" + File.separator + "layout" + File.separator
                        + Integer.toHexString(graph.hashCode()) + ".kgraph";
            } else {
                path += File.separator + "tmp" + File.separator + "layout" + File.separator
                        + Integer.toHexString(graph.hashCode()) + ".kgraph";
            }
            
            // serialize all properties of the graph
            ElkUtil.persistDataElements(graph);
            
            // save the KGraph to a file
            ResourceSet resourceSet = new ResourceSetImpl();
            Resource resource = resourceSet.createResource(URI.createFileURI(path));
            resource.getContents().add(graph);
            try {
                resource.save(Collections.emptyMap());
            } catch (IOException e) {
                // ignore the exception and abort the layout graph exporting
            }
        }
    }

}
