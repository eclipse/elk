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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.IGraphLayoutEngine;
import org.eclipse.elk.core.LayoutConfigService;
import org.eclipse.elk.core.RecursiveGraphLayoutEngine;
import org.eclipse.elk.core.config.CompoundLayoutConfig;
import org.eclipse.elk.core.config.ILayoutConfig;
import org.eclipse.elk.core.config.IMutableLayoutConfig;
import org.eclipse.elk.core.config.LayoutContext;
import org.eclipse.elk.core.config.VolatileLayoutConfig;
import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.service.util.MonitoredOperation;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.core.util.KimlUtil;
import org.eclipse.elk.core.util.Maybe;
import org.eclipse.elk.core.util.Pair;
import org.eclipse.elk.graph.KGraphElement;
import org.eclipse.elk.graph.KNode;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.statushandlers.StatusManager;

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
     * Property for the diagram layout manager used for automatic layout. This property is
     * attached to layout mappings created by the {@code layout} methods.
     */
    public static final IProperty<IDiagramLayoutManager<?>> DIAGRAM_LM
            = new Property<IDiagramLayoutManager<?>>("layoutEngine.diagramLayoutManager");
    /** preference identifier for debug graph output. */
    public static final String PREF_DEBUG_OUTPUT = "kiml.debug.graph";
    /** preference identifier for execution time measurement. */
    public static final String PREF_EXEC_TIME_MEASUREMENT = "kiml.exectime.measure";
    
    
    /** the graph layout engine for executing layout algorithms on the hierarchy levels of a graph. */
    private final IGraphLayoutEngine graphLayoutEngine = new RecursiveGraphLayoutEngine();
    
    /** the layout options manager for configuration of layout graphs. */
    private final LayoutOptionManager layoutOptionManager = new LayoutOptionManager();
    
    /** the executor service used to perform layout operations. */
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    
    /** map of currently running layout operations. */
    private final Multimap<Pair<IWorkbenchPart, Object>, MonitoredOperation> runningOperations
            = HashMultimap.create();


    /**
     * Interface definition of handles allowing to let the {@link DiagramLayoutEngine} know if a
     * layout run should be canceled because of, e.g., the disposition of elements to be arranged.<br>
     * <br>
     * Such a {@link ILayoutCancelationIndicator} can be employed when invoking automatic layout via
     * {@link DiagramLayoutEngine#layout(IWorkbenchPart, Object, ILayoutCancelationIndicator,
     * ILayoutConfig...)}.
     *
     * @author chsch
     */
    public interface ILayoutCancelationIndicator {

        /**
         * To be implemented by callers of the {@link DiagramLayoutEngine}, is called by the
         * {@link DiagramLayoutEngine} to check whether this layout performance must be stopped,
         * e.g. because of the disposal of required data.
         *
         * @return <code>true</code> if the corresponding layout performance must be stopped,
         *         <code>false</code> otherwise
         */
        boolean isCanceled();
    }

    /**
     * Helper method combining a <code>null</code> check and execution of
     * {@link ILayoutCancelationIndicator#isCanceled() handle.isCanceled()}.
     *
     * @param cancelIndicator
     *            the {@link ILayoutCancelationIndicator} to check, may be <code>null</code>
     * @return <code>false</code> if <code>cancelIndicator</code> is <code>null</code>,
     *         <code>cancelIndicator.isCanceled()</code> otherwise
     */
    private static boolean isCanceled(final ILayoutCancelationIndicator cancelIndicator) {
        return cancelIndicator != null && cancelIndicator.isCanceled();
    }


    /**
     * Perform layout on the given workbench part with the given global options.
     * 
     * @param workbenchPart
     *            the workbench part for which layout is performed
     * @param diagramPart
     *            the parent diagram part for which layout is performed, or {@code null} if the whole
     *            diagram shall be layouted
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
        return layout(workbenchPart, diagramPart,
                new VolatileLayoutConfig()
                    .setValue(LayoutOptions.ANIMATE, animate)
                    .setValue(LayoutOptions.PROGRESS_BAR, progressBar)
                    .setValue(LayoutOptions.LAYOUT_ANCESTORS, layoutAncestors)
                    .setValue(LayoutOptions.ZOOM_TO_FIT, zoomToFit));
    }
    
    /**
     * Perform layout on the given workbench part.
     * Use a {@link VolatileLayoutConfig} to configure layout options:
     * <pre>
     * DiagramLayoutEngine.INSTANCE.layout(workbenchPart, diagramPart,
     *          new VolatileLayoutConfig()
     *              .setValue(LayoutOptions.ALGORITHM, "org.eclipse.elk.algorithm.layered")
     *              .setValue(LayoutOptions.SPACING, 30.0f)
     *              .setValue(LayoutOptions.ANIMATE, true));
     * </pre>
     * If multiple configurators are given, the layout is computed multiple times:
     * once for each configurator. This behavior can be used to apply different layout algorithms
     * one after another, e.g. first a node placer algorithm and then an edge router algorithm.
     * Example:
     * <pre>
     * DiagramLayoutEngine.INSTANCE.layout(workbenchPart, diagramPart,
     *          new VolatileLayoutConfig()
     *              .setValue(LayoutOptions.ALGORITHM, "org.eclipse.elk.algorithm.force"),
     *          new VolatileLayoutConfig()
     *              .setValue(LayoutOptions.ALGORITHM, "de.cau.cs.kieler.kiml.libavoid"));
     * </pre>
     * If you want to use multiple configurators in the same layout computation,
     * use a {@link CompoundLayoutConfig}:
     * <pre>
     * DiagramLayoutEngine.INSTANCE.layout(workbenchPart, diagramPart,
     *          CompoundLayoutConfig.of(config1, config2, ...));
     * </pre> 
     * 
     * @param workbenchPart
     *            the workbench part for which layout is performed
     * @param diagramPart
     *            the parent diagram part for which layout is performed, or {@code null} if the whole
     *            diagram shall be layouted
     * @param configurators
     *            the configurators to use for the layout
     * @return the layout mapping used in this session
     */
    public LayoutMapping<?> layout(final IWorkbenchPart workbenchPart, final Object diagramPart,
            final ILayoutConfig... configurators) {
        return layout(workbenchPart, diagramPart, null, configurators);
    }

    /**
     * Perform layout on the given workbench part.
     * Use a {@link VolatileLayoutConfig} to configure layout options:
     * <pre>
     * DiagramLayoutEngine.INSTANCE.layout(workbenchPart, diagramPart, cancelationIndicator,
     *          new VolatileLayoutConfig()
     *              .setValue(LayoutOptions.ALGORITHM, "org.eclipse.elk.algorithm.layered")
     *              .setValue(LayoutOptions.SPACING, 30.0f)
     *              .setValue(LayoutOptions.ANIMATE, true));
     * </pre>
     * If multiple configurators are given, the layout is computed multiple times:
     * once for each configurator. This behavior can be used to apply different layout algorithms
     * one after another, e.g. first a node placer algorithm and then an edge router algorithm.
     * Example:
     * <pre>
     * DiagramLayoutEngine.INSTANCE.layout(workbenchPart, diagramPart, cancelationIndicator,
     *          new VolatileLayoutConfig()
     *              .setValue(LayoutOptions.ALGORITHM, "org.eclipse.elk.algorithm.force"),
     *          new VolatileLayoutConfig()
     *              .setValue(LayoutOptions.ALGORITHM, "de.cau.cs.kieler.kiml.libavoid"));
     * </pre>
     * If you want to use multiple configurators in the same layout computation,
     * use a {@link CompoundLayoutConfig}:
     * <pre>
     * DiagramLayoutEngine.INSTANCE.layout(workbenchPart, diagramPart, cancelationIndicator,
     *          CompoundLayoutConfig.of(config1, config2, ...));
     * </pre> 
     * 
     * @param workbenchPart
     *            the workbench part for which layout is performed
     * @param diagramPart
     *            the parent diagram part for which layout is performed, or {@code null} if the whole
     *            diagram shall be layouted
     * @param cancelationIndicator
     *            an {@link ILayoutCancelationIndicator} evaluated repeatedly during the layout
     *            performance, or <code>null</code>
     * @param configurators
     *            the configurators to use for the layout
     * @return the layout mapping used in this session
     */
    public LayoutMapping<?> layout(final IWorkbenchPart workbenchPart, final Object diagramPart,
            final ILayoutCancelationIndicator cancelationIndicator,
            final ILayoutConfig... configurators) {

        final IDiagramLayoutManager<?> layoutManager = 
                LayoutManagersService.getInstance().getManager(workbenchPart, diagramPart);

        if (layoutManager != null) {
            final LayoutMapping<?> mapping = layout(
                    layoutManager, workbenchPart, diagramPart, cancelationIndicator, configurators);
            if (mapping != null) {
                mapping.setProperty(DIAGRAM_LM, layoutManager);
            }
            return mapping;
        } else {
            final IStatus status = new Status(IStatus.ERROR, KimlServicePlugin.PLUGIN_ID,
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
     * @param cancelationIndicator
     *            an {@link ILayoutCancelationIndicator} evaluated repeatedly during the layout
     *            performance, or <code>null</code>
     * @param configurators
     *            the configurators to use for the layout
     * @return the layout mapping used in this session
     */
    protected <T> LayoutMapping<T> layout(final IDiagramLayoutManager<T> layoutManager,
            final IWorkbenchPart workbenchPart, final Object diagramPart,
            final ILayoutCancelationIndicator cancelationIndicator,
            final ILayoutConfig... configurators) {
        final Maybe<LayoutMapping<T>> layoutMapping = Maybe.create();
        final Pair<IWorkbenchPart, Object> target = Pair.of(workbenchPart, diagramPart);
        final ILayoutConfig globalConfig = configurators.length == 0 ? null : configurators[0];
        
        final MonitoredOperation monitoredOperation = new MonitoredOperation(executorService) {
            
            private final MonitoredOperation thisOperation = this;
            
            /**
             * Specialized {@link MonitoredOperation.CancelableProgressMonitor} evaluating 
             * <code>cancelationIndicator</code> in addition to the original behavior.
             *
             * @author chsch
             */
            class CancelableProgressMonitorEx extends MonitoredOperation.CancelableProgressMonitor {

                @Override
                public boolean isCanceled() {
                    if (super.isCanceled()) {
                        return true;
                        
                    } else if (DiagramLayoutEngine.isCanceled(cancelationIndicator)) {
                        thisOperation.cancel();
                        return true;
                        
                    } else {
                        return false;
                    }
                }
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            protected IElkProgressMonitor createMonitor() {
                return new CancelableProgressMonitorEx();
            }
            
            // first phase: build the layout graph
            @Override
            protected void preUIexec() {
                if (isCanceled(cancelationIndicator)) {
                    this.cancel();
                    return;
                }

                boolean layoutAncestors = layoutOptionManager.getGlobalValue(
                        LayoutOptions.LAYOUT_ANCESTORS, globalConfig);
                layoutMapping.set(layoutManager.buildLayoutGraph(workbenchPart,
                        layoutAncestors ? null : diagramPart));
            }

            // second phase: execute layout algorithms
            @Override
            protected IStatus execute(final IElkProgressMonitor monitor) {
                if (monitor.isCanceled()) {
                    return Status.CANCEL_STATUS;
                }

                LayoutMapping<T> mapping = layoutMapping.get();
                IStatus status;
                if (mapping != null && mapping.getLayoutGraph() != null) {
                    Object transDiagPart = diagramPart;
                    IMutableLayoutConfig diagramConfig = layoutManager.getDiagramConfig();
                    if (diagramConfig != null) {
                        if (!mapping.getLayoutConfigs().contains(diagramConfig)) {
                            mapping.getLayoutConfigs().add(diagramConfig);
                        }
                        
                        // translate the diagram part into one that is understood by the layout manager
                        if (diagramPart != null) {
                            LayoutContext context = new LayoutContext();
                            context.setProperty(LayoutContext.DIAGRAM_PART, diagramPart);
                            Object object = diagramConfig.getContextValue(LayoutContext.DIAGRAM_PART,
                                    context);
                            if (object != null) {
                                transDiagPart = object;
                            }
                        }
                    }
                    
                    // perform the actual layout
                    status = layout(mapping, transDiagPart, monitor, configurators);
                    
                    // stop earlier layout operations that are still running
                    if (!monitor.isCanceled()) {
                        stopEarlierOperations(target, getTimestamp());
                    }
                } else {
                    status = new Status(Status.WARNING, KimlServicePlugin.PLUGIN_ID,
                            "Unable to build the layout graph from the given workbench part.");
                }
                
                monitor.done();
                return status;
            }

            // third phase: apply layout with animation
            @Override
            protected void postUIexec() {
                if (isCanceled(cancelationIndicator)) {
                    this.cancel();
                    return;
                }

                if (layoutMapping.get() != null) {
                    boolean zoomToFit = layoutOptionManager.getGlobalValue(
                            LayoutOptions.ZOOM_TO_FIT, globalConfig);
                    int animationTime = calcAnimationTime(layoutMapping.get(),
                            configurators.length == 0 ? null : configurators[0],
                            workbenchPart != null && !workbenchPart.getSite().getPage()
                                .isPartVisible(workbenchPart));
                    layoutManager.applyLayout(layoutMapping.get(), zoomToFit, animationTime);
                }
            }
        };
        
        synchronized (runningOperations) {
            runningOperations.put(target, monitoredOperation);
        }

        try {
            boolean progressBar = layoutOptionManager.getGlobalValue(
                    LayoutOptions.PROGRESS_BAR, globalConfig);
            if (progressBar) {
                // perform layout with a progress bar
                monitoredOperation.runMonitored();
            } else {
                // perform layout without a progress bar
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
     * Calculates animation time for the given graph size. If the viewer is not visible,
     * the animation time is 0.
     * 
     * @param mapping a mapping of the layout graph
     * @param config the layout configurator from which to read animation settings, or {@code null}
     * @param viewerNotVisible whether the diagram viewer is currently not visible
     * @return number of milliseconds to animate, or 0 if no animation is desired
     */
    private int calcAnimationTime(final LayoutMapping<?> mapping, final ILayoutConfig config,
            final boolean viewerNotVisible) {
        
        final CompoundLayoutConfig clc = CompoundLayoutConfig.of(config);
        clc.addAll(LayoutConfigService.getInstance().getActiveConfigs());
        
        boolean animate = layoutOptionManager.getGlobalValue(LayoutOptions.ANIMATE, clc);
        if (animate) {
            int minTime = layoutOptionManager.getGlobalValue(LayoutOptions.MIN_ANIMATION_TIME, clc);
            if (minTime < 0) {
                minTime = 0;
            }
            int maxTime = layoutOptionManager.getGlobalValue(LayoutOptions.MAX_ANIMATION_TIME, clc);
            if (maxTime < minTime) {
                maxTime = minTime;
            }
            int factor = layoutOptionManager.getGlobalValue(
                    LayoutOptions.ANIMATION_TIME_FACTOR, clc);
            if (factor > 0) {
                int graphSize = countNodes(mapping.getLayoutGraph());
                int time = minTime + (int) (factor * Math.sqrt(graphSize));
                return time <= maxTime ? time : maxTime;
            } else {
                return minTime;
            }
        }
        return 0;
    }

    /**
     * Counts the total number of children in the given node, including deep hierarchies.
     * 
     * @param node
     *            parent node
     * @return number of children and grandchildren in the given parent
     */
    private static int countNodes(final KNode node) {
        int count = 0;
        for (KNode child : node.getChildren()) {
            count += countNodes(child) + 1;
        }
        return count;
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
        IDiagramLayoutManager<?> layoutManager = LayoutManagersService.getInstance().getManager(
                workbenchPart, diagramPart);
        if (layoutManager != null) {
            return layout(layoutManager, workbenchPart, diagramPart, progressMonitor);
        } else {
            return new Status(IStatus.ERROR, KimlServicePlugin.PLUGIN_ID,
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
     * @return a status indicating success or failure
     */
    protected <T> IStatus layout(final IDiagramLayoutManager<T> layoutManager,
            final IWorkbenchPart workbenchPart, final Object diagramPart,
            final IElkProgressMonitor progressMonitor) {
        IElkProgressMonitor monitor;
        if (progressMonitor == null) {
            monitor = new BasicProgressMonitor(0, KimlServicePlugin.getDefault().getPreferenceStore()
                    .getBoolean(PREF_EXEC_TIME_MEASUREMENT));
        } else {
            monitor = progressMonitor;
        }
        // SUPPRESS CHECKSTYLE NEXT MagicNumber
        monitor.begin("Layout Diagram", 3);
        
        // build the layout graph
        IElkProgressMonitor submon1 = monitor.subTask(1);
        submon1.begin("Build layout graph", 1);
        LayoutMapping<T> mapping = layoutManager.buildLayoutGraph(workbenchPart, diagramPart);
        
        IStatus status;
        if (mapping != null && mapping.getLayoutGraph() != null) {
            Object transDiagPart = diagramPart;
            IMutableLayoutConfig diagramConfig = layoutManager.getDiagramConfig();
            if (diagramConfig != null) {
                if (!mapping.getLayoutConfigs().contains(diagramConfig)) {
                    mapping.getLayoutConfigs().add(diagramConfig);
                }
                
                // translate the diagram part into one that is understood by the layout manager
                if (diagramPart != null) {
                    LayoutContext context = new LayoutContext();
                    context.setProperty(LayoutContext.DIAGRAM_PART, diagramPart);
                    Object object = diagramConfig.getContextValue(LayoutContext.DIAGRAM_PART, context);
                    if (object != null) {
                        transDiagPart = object;
                    }
                }
            }
            submon1.done();
            
            // perform the actual layout
            status = layout(mapping, transDiagPart, monitor.subTask(1));
            
            // apply the layout to the diagram
            IElkProgressMonitor submon3 = monitor.subTask(1);
            submon3.begin("Apply layout to the diagram", 1);
            layoutManager.applyLayout(mapping, false, 0);
            submon3.done();
        } else {
            status = new Status(Status.WARNING, KimlServicePlugin.PLUGIN_ID,
                    "Unable to build the layout graph from the given workbench part.");
        }
        
        monitor.done();
        return status;
    }
    
    /**
     * Returns the currently used layout option manager.
     * 
     * @return the layout option manager
     */
    public LayoutOptionManager getOptionManager() {
        return layoutOptionManager;
    }
    
    /** property for layout context: the progress monitor that was used for layout. */
    public static final IProperty<IElkProgressMonitor> PROGRESS_MONITOR
            = new Property<IElkProgressMonitor>("layout.progressMonitor");
    
    /**
     * Perform layout on the given layout graph mapping. If zero or one layout configurator is
     * passed, the layout engine is executed exactly once. If multiple layout configurators are
     * passed, the layout engine is executed accordingly often, but the resulting layout is applied
     * only once. This is useful for composition of multiple algorithms that process only parts of
     * the graph. Layout listeners are notified after the layout has been computed.
     * 
     * @param mapping
     *            a mapping for the layout graph
     * @param diagramPart
     *            the parent diagram part for which layout is performed, or {@code null} if the whole
     *            diagram shall be layouted
     * @param progressMonitor
     *            a progress monitor to which progress of the layout algorithm is reported
     * @param configurators
     *            the configurators to use for the layout
     * @return a status indicating success or failure
     */
    protected IStatus layout(final LayoutMapping<?> mapping, final Object diagramPart,
            final IElkProgressMonitor progressMonitor, final ILayoutConfig... configurators) {

        ILayoutConfig globalConfig = configurators.length == 0 ? null : configurators[0];
        boolean layoutAncestors =
                layoutOptionManager.getGlobalValue(LayoutOptions.LAYOUT_ANCESTORS, globalConfig);

        if (layoutAncestors) {
            // mark all parallel areas for exclusion from layout
            KGraphElement graphElem = mapping.getGraphMap().inverse().get(diagramPart);
            if (graphElem instanceof KNode && ((KNode) graphElem).getParent() != null) {
                KNode node = (KNode) graphElem;
                VolatileLayoutConfig vlc = new VolatileLayoutConfig();
                do {
                    KNode parent = node.getParent();
                    for (KNode child : parent.getChildren()) {
                        if (child != node) {
                            // do not layout the content of the child node
                            vlc.setValue(LayoutOptions.NO_LAYOUT, child,
                                    LayoutContext.GRAPH_ELEM, true);
                            // do not change the size of the child node
                            vlc.setValue(LayoutOptions.SIZE_CONSTRAINT, child,
                                    LayoutContext.GRAPH_ELEM, SizeConstraint.fixed());
                            // do not move the ports of the child node
                            vlc.setValue(LayoutOptions.PORT_CONSTRAINTS, child,
                                    LayoutContext.GRAPH_ELEM, PortConstraints.FIXED_POS);
                        }
                    }
                    node = parent;
                } while (node.getParent() != null);
                mapping.getLayoutConfigs().add(vlc);
            }
        }
        
        mapping.setProperty(PROGRESS_MONITOR, progressMonitor);
        IStatus status = null;
        if (configurators.length == 0) {
            // perform layout without any extra configuration
            status = layout(mapping, progressMonitor);
        } else if (configurators.length == 1) {
            // perform layout once with an extra configuration
            mapping.getLayoutConfigs().add(configurators[0]);
            status = layout(mapping, progressMonitor);
        } else {
            // perform layout multiple times with different configurations
            progressMonitor.begin("Diagram layout engine", TOTAL_WORK * configurators.length);
            for (ILayoutConfig config : configurators) {
                mapping.getLayoutConfigs().add(config);
                status = layout(mapping, progressMonitor);
                if (!status.isOK()) {
                    break;
                }
                mapping.getLayoutConfigs().remove(config);
            }
            progressMonitor.done();
        }

        // notify listeners of the executed layout
        if (layoutListeners != null) {
            for (final ILayoutTerminatedListener listener : layoutListeners) {
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
     * @return a status indicating success or failure
     */
    public IStatus layout(final LayoutMapping<?> mapping,
            final IElkProgressMonitor progressMonitor) {

        if (progressMonitor.isCanceled()) {
            return Status.CANCEL_STATUS;
        }

        boolean newTask = progressMonitor.begin("Diagram layout engine", TOTAL_WORK);
        if (mapping.getProperty(PROGRESS_MONITOR) == null) {
            mapping.setProperty(PROGRESS_MONITOR, progressMonitor);
        }
        
        try {
            // configure the layout graph using a layout option manager
            try {
                // chsch: wrapped the configuration of the layout graph by the addition try catch
                //  block as layout configs contributed via IDiagramLayoutManager.getDiagramConfig()
                //  are likely rely on the corresponding diagram viewer that may be closed
                //  while executing the method call below (in a non-UI thread)
                // hence, if a Throwable 't' occurs during the subsequent method call and the
                //  cancelation check returns 'true',
                //  don't consider the occurrence of 't' a failure and just stop the layout run

                layoutOptionManager.configure(mapping, progressMonitor.subTask(CONFIGURE_WORK));

            } catch (Throwable t) {

                if (progressMonitor.isCanceled()) {
                    return Status.CANCEL_STATUS;

                } else {
                    throw t;
                }
            }
            
            // export the layout graph for debugging
            if (KimlServicePlugin.getDefault().getPreferenceStore().getBoolean(PREF_DEBUG_OUTPUT)) {
                exportLayoutGraph(mapping.getLayoutGraph());
            }

            // perform layout on the layout graph
            graphLayoutEngine.layout(mapping.getLayoutGraph(), progressMonitor.subTask(LAYOUT_WORK));
            
            // if an additional layout configurator is attached to the graph, consider it in the future
            ILayoutConfig addConfig = mapping.getLayoutGraph().getData(KShapeLayout.class).getProperty(
                    AbstractLayoutProvider.ADD_LAYOUT_CONFIG);
            if (addConfig != null) {
                mapping.getLayoutConfigs().add(addConfig);
            }
            
            if (newTask) {
                progressMonitor.done();
            }
            if (progressMonitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            }
            
            // return a positive status
            return Status.OK_STATUS;
            
        } catch (Throwable exception) {
            return new Status(IStatus.ERROR, KimlServicePlugin.PLUGIN_ID,
                    "Failed to perform diagram layout.", exception);
        }
    }
    
    /**
     * Listener interface for graph layout. Implementations must not modify the graph in any way
     * and should execute as quickly as possible.
     */
    public interface ILayoutTerminatedListener {
        /**
         * Called when layout has been done on a graph.
         * 
         * @param layoutGraph the parent node of the graph
         * @param monitor the progress monitor with information on the executed layout
         */
        void layoutDone(KNode layoutGraph, IElkProgressMonitor monitor);
    }
    
    /** list of registered layout listeners. */
    private List<ILayoutTerminatedListener> layoutListeners = null;
    
    /**
     * Add the given object to the list of layout listeners.
     * 
     * @deprecated use {@link #addLayoutTerminatedListener(ILayoutTerminatedListener)}
     * @param listener
     *            a listener
     */
    public void addListener(final ILayoutTerminatedListener listener) {
        addLayoutTerminatedListener(listener);
    }

    /**
     * Add the given object to the list of layout listeners.
     *
     * @param listener a listener
     */
    public void addLayoutTerminatedListener(final ILayoutTerminatedListener listener) {
        if (layoutListeners == null) {
            layoutListeners = new LinkedList<ILayoutTerminatedListener>();
        }
        layoutListeners.add(listener);
    }

    /**
     * Remove the given object from the list of layout listeners.
     *
     * @param listener a listener
     */
    public void removeLayoutTerminatedListener(final ILayoutTerminatedListener listener) {
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
            KimlUtil.persistDataElements(graph);
            
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
