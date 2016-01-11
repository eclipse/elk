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

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.elk.core.util.DefaultFactory;
import org.eclipse.elk.core.util.IFactory;
import org.eclipse.elk.core.util.Pair;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * A service class for layout managers, which are registered through the extension point.
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow 2012-07-10 msp
 */
public class LayoutManagersService {

    /** preference identifier for oblique edge routing. */
    public static final String PREF_OBLIQUE_ROUTE = "kiml.oblique.route";
    
    /** identifier of the extension point for layout managers. */
    protected static final String EXTP_ID_LAYOUT_MANAGERS =
            "org.eclipse.elk.core.service.layoutManagers";
    /** name of the 'manager' element in the 'layout managers' extension point. */
    protected static final String ELEMENT_MANAGER = "manager";
    /** name of the 'class' attribute in the extension points. */
    protected static final String ATTRIBUTE_CLASS = "class";
    /** name of the 'priority' attribute in the extension points. */
    protected static final String ATTRIBUTE_PRIORITY = "priority";
    
    /** the singleton instance of the managers service. */
    private static LayoutManagersService instance;
    /** the factory for creation of service instances. */
    private static IFactory<? extends LayoutManagersService> instanceFactory
            = new DefaultFactory<LayoutManagersService>(LayoutManagersService.class);
    
    /**
     * Returns the singleton instance of the managers service.
     * 
     * @return the singleton instance
     */
    public static synchronized LayoutManagersService getInstance() {
        if (instance == null) {
            instance = instanceFactory.create();
        }
        return instance;
    }
    
    /**
     * Set the factory for creating instances. If an instance is already created, it is cleared
     * so the next call to {@link #getInstance()} uses the new factory.
     * 
     * @param factory an instance factory
     */
    public static void setInstanceFactory(final IFactory<? extends LayoutManagersService> factory) {
        if (factory == null) {
            throw new NullPointerException("The given instance factory is null");
        }
        instanceFactory = factory;
        instance = null;
    }

    
    /** list of registered diagram layout managers. */
    private final List<Pair<Integer, IDiagramLayoutManager<?>>> managers
            = new LinkedList<Pair<Integer, IDiagramLayoutManager<?>>>();

    /**
     * Load all registered extensions for the layout managers extension point.
     */
    public LayoutManagersService() {
        loadLayoutManagerExtensions();
    }

    /**
     * Report an error that occurred while reading extensions. May be overridden by subclasses
     * in order to report errors in a different way.
     * 
     * @param extensionPoint the identifier of the extension point
     * @param element the configuration element
     * @param attribute the attribute that contains an invalid entry
     * @param exception an optional exception that was caused by the invalid entry
     */
    protected void reportError(final String extensionPoint, final IConfigurationElement element,
            final String attribute, final Throwable exception) {
        String message;
        if (element != null && attribute != null) {
            message = "Extension point " + extensionPoint + ": Invalid entry in attribute '"
                    + attribute + "' of element " + element.getName() + ", contributed by "
                    + element.getContributor().getName();
        } else {
            message = "Extension point " + extensionPoint
                    + ": An error occured while loading extensions.";
        }
        IStatus status = new Status(IStatus.WARNING, ElkServicePlugin.PLUGIN_ID,
                0, message, exception);
        StatusManager.getManager().handle(status);
    }

    /**
     * Report an error that occurred while reading extensions. May be overridden by subclasses
     * in order to report errors in a different way.
     * 
     * @param exception a core exception holding a status with further information
     */
    protected void reportError(final CoreException exception) {
        StatusManager.getManager().handle(exception, ElkServicePlugin.PLUGIN_ID);
    }
    
    /**
     * Returns the most suitable layout manager for the given workbench and diagram part.
     * 
     * @param workbenchPart the workbench part for which the layout manager should be
     *     fetched, or {@code null}
     * @param diagramPart the diagram part for which the layout manager should be
     *     fetched, or {@code null}
     * @return the most suitable diagram layout manager
     */
    public final IDiagramLayoutManager<?> getManager(final IWorkbenchPart workbenchPart,
            final Object diagramPart) {
        for (Pair<Integer, IDiagramLayoutManager<?>> entry : managers) {
            IDiagramLayoutManager<?> manager = entry.getSecond();
            if (workbenchPart == null) {
                if (manager.supports(diagramPart)) {
                    return manager;
                }
            } else if (manager.supports(workbenchPart)
                    && (diagramPart == null || manager.supports(diagramPart))) {
                return manager;
            }
        }
        return null;
    }

    /**
     * Loads all diagram layout manager extensions from the extension point.
     */
    private void loadLayoutManagerExtensions() {
        IConfigurationElement[] extensions = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(EXTP_ID_LAYOUT_MANAGERS);
        
        for (IConfigurationElement element : extensions) {
            try {
                if (ELEMENT_MANAGER.equals(element.getName())) {
                    IDiagramLayoutManager<?> manager = (IDiagramLayoutManager<?>)
                            element.createExecutableExtension(ATTRIBUTE_CLASS);
                    if (manager != null) {
                        int priority = 0;
                        String prioEntry = element.getAttribute(ATTRIBUTE_PRIORITY);
                        if (prioEntry != null) {
                            try {
                                priority = Integer.parseInt(prioEntry);
                            } catch (NumberFormatException exception) {
                                // ignore exception
                            }
                        }
                        insertSorted(manager, priority, managers);
                    }
                    
                }
            } catch (CoreException exception) {
                StatusManager.getManager().handle(exception, ElkServicePlugin.PLUGIN_ID);
            }
        }
    }
    
    /**
     * Insert the given object into a sorted list.
     * 
     * @param object the object to insert
     * @param priority priority at which the object is inserted
     * @param list list of sorted objects
     */
    private static <T> void insertSorted(final T object, final int priority,
            final List<Pair<Integer, T>> list) {
        ListIterator<Pair<Integer, T>> iter = list.listIterator();
        while (iter.hasNext()) {
            Pair<Integer, T> next = iter.next();
            if (next.getFirst() <= priority) {
                iter.previous();
                break;
            }
        }
        iter.add(new Pair<Integer, T>(priority, object));
    }

}
