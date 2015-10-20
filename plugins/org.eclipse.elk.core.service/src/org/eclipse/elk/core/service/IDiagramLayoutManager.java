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

import org.eclipse.elk.core.config.IMutableLayoutConfig;
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
     * <p>Layout options that are directly set on graph elements are cleared later by the
     * {@link org.eclipse.elk.core.service.LayoutOptionManager LayoutOptionManager}.
     * Therefore those options must be pulled into a volatile configuration and
     * added to the {@link LayoutMapping#getLayoutConfigs()} list:
     * <pre>
     * mapping.getLayoutConfigs().add(VolatileLayoutConfig.fromProperties(
     *         mapping.getLayoutGraph(), PRIORITY));
     * </pre></p> 
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
     * @return a layout graph mapping
     */
    LayoutMapping<T> buildLayoutGraph(IWorkbenchPart workbenchPart,
            Object diagramPart);

    /**
     * Apply the computed layout back to the diagram. Graph elements whose modification flag
     * was not set during layout should be ignored.
     * 
     * <p>This method is usually called from the UI thread.</p>
     * 
     * @param mapping a layout mapping that was created by this layout manager
     * @param zoomToFit whether the diagram should zoom to fit
     * @param animationTime the animation time in milliseconds, or 0 for no animation
     * @see org.eclipse.elk.core.klayoutdata.impl.KShapeLayoutImpl#isModified()
     * @see org.eclipse.elk.core.klayoutdata.impl.KEdgeLayoutImpl#isModified()
     */
    void applyLayout(LayoutMapping<T> mapping, boolean zoomToFit,
            int animationTime);
    
    /**
     * Undo the layout in the original diagram (optional operation).
     * 
     * @param mapping a layout mapping that was created by this layout manager
     */
    void undoLayout(LayoutMapping<T> mapping);
    
    /**
     * Return a mutable layout configurator that is able to read and write layout options
     * through annotations of the diagram. This configurator is necessary for the Layout View.
     * If this method returns {@code null}, the Layout View will not be active for the diagrams
     * handled by this manager.
     * 
     * @return a mutable layout configurator for direct access, or {@code null}
     */
    IMutableLayoutConfig getDiagramConfig();

}
