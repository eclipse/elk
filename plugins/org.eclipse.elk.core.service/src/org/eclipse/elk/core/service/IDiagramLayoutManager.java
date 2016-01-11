/*******************************************************************************
 * Copyright (c) 2009, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.service;

import org.eclipse.elk.graph.properties.IPropertyHolder;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Interface for managers of diagram layout. A diagram layout manager is responsible for transforming
 * the diagram contained in a workbench part into a layout graph, which is an instance of the
 * KGraph meta model. Furthermore it must handle the transfer of concrete layout data from the
 * layout graph back to the diagram after a layout has been computed.
 * 
 * @param <T> the type of diagram part that is handled by this diagram layout manager
 * @kieler.design proposed by msp
 * @kieler.rating yellow 2012-07-19 review KI-20 by cds, jjc
 * @author msp
 */
public interface IDiagramLayoutManager<T> {

    /**
     * Determine whether this layout manager is able to perform layout for the given object.
     * 
     * @param object a workbench part or edit part
     * @return true if this layout manager supports the object
     */
    boolean supports(Object object);

    /**
     * Build a KGraph instance for the given diagram. The resulting layout graph should reflect
     * the structure of the original diagram. All graph elements must have
     * {@link org.eclipse.elk.core.klayoutdata.KShapeLayout KShapeLayouts} or
     * {@link org.eclipse.elk.core.klayoutdata.KEdgeLayout KEdgeLayouts} attached,
     * and their modification flags must be set to {@code false}.
     * 
     * <p>At least one of the two parameters must be non-null.</p>
     * 
     * <p>This method is usually called from the UI thread.</p>
     * 
     * @param workbenchPart
     *            the workbench part for which layout is performed, or {@code null} if there
     *            is no workbench part for the diagram
     * @param diagramPart
     *            the parent object for which layout is performed, or {@code null} if the
     *            whole diagram shall be layouted
     * @return a layout graph mapping, or {@code null} if the given workbench part or diagram part
     *            is not supported
     */
    LayoutMapping<T> buildLayoutGraph(IWorkbenchPart workbenchPart, Object diagramPart);

    /**
     * Apply the computed layout back to the diagram. Graph elements whose modification flag
     * was not set during layout should be ignored.
     * 
     * <p>This method is usually called from the UI thread.</p>
     * 
     * @param mapping a layout mapping that was created by this layout manager
     * @param settings general settings for applying layout, e.g. whether to use animation
     */
    void applyLayout(LayoutMapping<T> mapping, IPropertyHolder settings);
    
    /**
     * Return a layout configuration store that is able to read and write layout options
     * through annotations of the diagram. This configurator is necessary for the Layout View.
     * If this method returns {@code null}, the Layout View will not be active for the diagrams
     * handled by this manager.
     * 
     * @param workbenchPart a workbench part, or {@code null}
     * @param context a context for layout configuration, usually a selected diagram element
     * @return a layout configuration store, or {@code null}
     */
    ILayoutConfigurationStore getConfigurationStore(IWorkbenchPart workbenchPart, Object context);

}
