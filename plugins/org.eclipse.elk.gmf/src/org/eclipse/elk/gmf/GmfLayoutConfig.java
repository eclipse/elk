/*******************************************************************************
 * Copyright (c) 2010, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.gmf;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.elk.core.LayoutMetaDataService;
import org.eclipse.elk.core.LayoutOptionData;
import org.eclipse.elk.core.config.DefaultLayoutConfig;
import org.eclipse.elk.core.config.IMutableLayoutConfig;
import org.eclipse.elk.core.config.LayoutContext;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.service.EclipseLayoutConfig;
import org.eclipse.elk.core.util.Maybe;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gmf.runtime.diagram.ui.editparts.AbstractBorderItemEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.CompartmentEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramRootEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.LabelEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditor;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.NotationFactory;
import org.eclipse.gmf.runtime.notation.StringValueStyle;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;

/**
 * A layout configuration that stores layout options in the notation model of GMF diagrams.
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating yellow 2013-07-01 review KI-38 by cds, uru
 */
public class GmfLayoutConfig implements IMutableLayoutConfig {
    
    /** the priority for the GMF layout configuration. */
    public static final int PRIORITY = 30;
    
    /** Prefix for all layout options. */
    public static final String PREFIX = "layout:";
    /** Prefix for diagram defaults stored in the top-level edit part. */
    public static final String DEF_PREFIX = "defaultLayout:";
    
    /** the notation view for the graph element in focus. */
    public static final IProperty<View> NOTATION_VIEW = new Property<View>("context.notationView");
    
    /** the notation view for the diagram element. */
    public static final IProperty<View> DIAGRAM_NOTATION_VIEW = new Property<View>(
            "context.diagramNotationView");
    
    /**
     * Determines whether the given edit part should not be layouted.
     * 
     * @param editPart an edit part
     * @return true if no layout should be performed for the edit part
     */
    public static boolean isNoLayout(final EditPart editPart) {
        if (editPart instanceof IGraphicalEditPart) {
            Boolean result = (Boolean) EclipseLayoutConfig.getValue(LayoutOptions.NO_LAYOUT,
                    editPart, ((IGraphicalEditPart) editPart).getNotationView().getElement());
            if (result != null) {
                return result;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public int getPriority() {
        return PRIORITY;
    }
    
    /** The aspect ratio is rounded at two decimal places. */
    private static final float ASPECT_RATIO_ROUND = 100;

    /**
     * {@inheritDoc}
     */
    public Object getContextValue(final IProperty<?> property, final LayoutContext context) {
        IGraphicalEditPart focusEditPart = null;
        Object diagramPart = context.getProperty(LayoutContext.DIAGRAM_PART);
        if (diagramPart instanceof CompartmentEditPart) {
            // if the selected object is a compartment, replace it by its parent element
            focusEditPart = (IGraphicalEditPart) ((CompartmentEditPart) diagramPart).getParent();
        } else if (diagramPart instanceof IGraphicalEditPart) {
            focusEditPart = (IGraphicalEditPart) diagramPart;
        } else if (diagramPart instanceof DiagramRootEditPart) {
            focusEditPart = (IGraphicalEditPart) ((DiagramRootEditPart) diagramPart).getContents();
        } else {
            IWorkbenchPart workbenchPart = context.getProperty(EclipseLayoutConfig.WORKBENCH_PART);
            if (workbenchPart instanceof DiagramEditor) {
                focusEditPart = ((DiagramEditor) workbenchPart).getDiagramEditPart();
            }
        }
            
        if (focusEditPart != null) {
            if (property.equals(LayoutContext.DIAGRAM_PART)) {
                return focusEditPart;
                
            } else if (property.equals(LayoutContext.CONTAINER_DIAGRAM_PART)) {
                return getContainer(focusEditPart);
                
            } else if (property.equals(LayoutContext.DOMAIN_MODEL)) {
                if (context.getProperty(LayoutContext.DOMAIN_MODEL) == null) {
                    EObject object = focusEditPart.getNotationView().getElement();
                    // put the EObject into the context only if the edit part has its own model element,
                    // otherwise we would wrongly receive options that are meant for the parent element
                    if (focusEditPart.getParent() == null
                            || !(focusEditPart.getParent().getModel() instanceof View)
                            || ((View) focusEditPart.getParent().getModel()).getElement() != object) {
                        return object;
                    }
                }
                
            } else if (property.equals(LayoutContext.CONTAINER_DOMAIN_MODEL)) {
                Object containerEditPart = context.getProperty(LayoutContext.CONTAINER_DIAGRAM_PART);
                if (containerEditPart instanceof IGraphicalEditPart) {
                    return ((IGraphicalEditPart) containerEditPart).getNotationView().getElement();
                }
                
            } else if (property.equals(LayoutContext.OPT_TARGETS)) {
                // determine the target type and container / containment edit parts
                return findTarget(focusEditPart);
                
            } else if (property.equals(DefaultLayoutConfig.HAS_PORTS)) {
                if (diagramPart instanceof ShapeNodeEditPart) {
                    Maybe<Boolean> hasPorts = Maybe.create();
                    findContainingEditPart(focusEditPart, hasPorts);
                    return hasPorts.get();
                }
                
            } else if (property.equals(EclipseLayoutConfig.EDITING_DOMAIN)) {
                return focusEditPart.getEditingDomain();
                
            } else if (property.equals(EclipseLayoutConfig.ASPECT_RATIO)) {
                // get aspect ratio for the current diagram
                EditPartViewer viewer = focusEditPart.getViewer();
                if (viewer != null) {
                    final Control control = viewer.getControl();
                    if (control != null) {
                        final Maybe<Float> result = new Maybe<Float>();
                        Runnable runnable = new Runnable() {
                            public void run() {
                                try {
                                    Point size = control.getSize();
                                    if (size.x > 0 && size.y > 0) {
                                        result.set(Math.round(
                                                ASPECT_RATIO_ROUND * (float) size.x / size.y)
                                                / ASPECT_RATIO_ROUND);
                                    }
                                } catch (SWTException exception) {
                                    // ignore exception
                                }
                            }
                        };
                        if (control.getDisplay() == Display.getCurrent()) {
                            runnable.run();
                        } else {
                            control.getDisplay().syncExec(runnable);
                        }
                        return result.get();
                    }
                }
                
            } else if (property.equals(DefaultLayoutConfig.CONTENT_HINT)) {
                // get a layout hint for the content of the focused edit part
                LayoutOptionData algorithmOptionData = LayoutMetaDataService.getInstance()
                        .getOptionData(LayoutOptions.ALGORITHM.getId());
                String contentLayoutHint = (String) getValue(algorithmOptionData, PREFIX,
                        focusEditPart.getNotationView());
                if (algorithmOptionData != null && contentLayoutHint == null) {
                    View diagramView = getDiagramNotationView(context);
                    if (diagramView != null) {
                        contentLayoutHint = (String) getValue(algorithmOptionData, DEF_PREFIX,
                                diagramView);
                    }
                }
                return contentLayoutHint;
                
            } else if (property.equals(DefaultLayoutConfig.CONTAINER_HINT)) {
                LayoutOptionData algorithmOptionData = LayoutMetaDataService.getInstance()
                        .getOptionData(LayoutOptions.ALGORITHM.getId());
                Object containerEditPart = context.getProperty(LayoutContext.CONTAINER_DIAGRAM_PART);
                if (algorithmOptionData != null && containerEditPart instanceof IGraphicalEditPart) {
                    String containerLayoutHint = (String) getValue(algorithmOptionData, PREFIX,
                            ((IGraphicalEditPart) containerEditPart).getNotationView());
                    if (containerLayoutHint == null) {
                        View diagramView = getDiagramNotationView(context);
                        if (diagramView != null) {
                            containerLayoutHint = (String) getValue(algorithmOptionData, DEF_PREFIX,
                                    diagramView);
                        }
                    }
                    return containerLayoutHint;
                }
            }
        }
        return null;
    }
    
    /**
     * Determines the type of edit part target for the layout options.
     * 
     * @param editPart an edit part
     * @return the layout option targets
     */
    protected Set<LayoutOptionData.Target> findTarget(final IGraphicalEditPart editPart) {
        if (editPart instanceof AbstractBorderItemEditPart) {
            // this is a border item, i.e. a port 
            return EnumSet.of(LayoutOptionData.Target.PORTS);
        } else if (editPart instanceof ShapeNodeEditPart) {
            // this is a node
            Set<LayoutOptionData.Target> partTarget = EnumSet.of(LayoutOptionData.Target.NODES);
            // check whether the node is a parent
            if (findContainingEditPart(editPart, new Maybe<Boolean>()) != null) {
                partTarget.add(LayoutOptionData.Target.PARENTS);
            }
            return partTarget;
        } else if (editPart instanceof ConnectionEditPart) {
            // this is a connection, i.e. an edge
            return EnumSet.of(LayoutOptionData.Target.EDGES);
        } else if (editPart instanceof LabelEditPart) {
            // this is a label
            return EnumSet.of(LayoutOptionData.Target.LABELS);
        } else if (editPart instanceof DiagramEditPart) {
            // this is a diagram
            return EnumSet.of(LayoutOptionData.Target.PARENTS);
        }
        return null;
    }
    
    /**
     * Determines the container edit part representing a parent node of the given GMF edit part.
     * 
     * @param editPart an edit part
     * @return the container edit part
     */
    protected IGraphicalEditPart getContainer(final IGraphicalEditPart editPart) {
        IGraphicalEditPart containerEditPart = null;
        if (editPart instanceof AbstractBorderItemEditPart) {
            // the container is the parent of the port's containing node
            containerEditPart = (IGraphicalEditPart) editPart.getParent().getParent();
        } else if (editPart instanceof ShapeNodeEditPart) {
            // this is a node
            containerEditPart = (IGraphicalEditPart) editPart.getParent();
        } else if (editPart instanceof ConnectionEditPart) {
            EditPart sourcePart = ((ConnectionEditPart) editPart).getSource();
            EditPart parentPart;
            if (sourcePart instanceof AbstractBorderItemEditPart) {
                // the source element is a port
                parentPart = sourcePart.getParent().getParent();
            } else {
                // the source element is a node
                parentPart = sourcePart.getParent();
            }
            if (parentPart instanceof IGraphicalEditPart) {
                containerEditPart = (IGraphicalEditPart) parentPart;
            }
        } else if (editPart instanceof LabelEditPart) {
            // this is a label
            containerEditPart = (IGraphicalEditPart) editPart.getParent();
            if (containerEditPart instanceof ConnectionEditPart) {
                // we have an edge label, so apply the same container rule as for edges
                EditPart sourcePart = ((ConnectionEditPart) containerEditPart).getSource();
                if (sourcePart instanceof AbstractBorderItemEditPart) {
                    containerEditPart = (IGraphicalEditPart) sourcePart.getParent().getParent();
                } else {
                    containerEditPart = (IGraphicalEditPart) sourcePart.getParent();
                }
            } else if (containerEditPart instanceof AbstractBorderItemEditPart) {
                // we have a port label, so apply the same container rule as for ports
                containerEditPart = (IGraphicalEditPart) containerEditPart.getParent().getParent();
            } else if (containerEditPart instanceof ShapeNodeEditPart) {
                // we have a node label
                containerEditPart = (IGraphicalEditPart) containerEditPart.getParent();
            }
        }
        
        if (containerEditPart instanceof CompartmentEditPart) {
            containerEditPart = (IGraphicalEditPart) containerEditPart.getParent();
        }
        return containerEditPart;
    }
    
    /**
     * Finds the edit part that contains layoutable children, if there are any. The returned
     * edit part is either the parent edit part itself or one of its compartments. While looking
     * for layoutable children, the existence of contained ports is also checked.
     * 
     * @param editPart a node edit part
     * @param hasPorts if the node contains ports, this reference parameter is set to {@code true}
     * @return the edit part that contains other node edit parts, or {@code null} if there is none
     */
    private static IGraphicalEditPart findContainingEditPart(final IGraphicalEditPart editPart,
            final Maybe<Boolean> hasPorts) {
        hasPorts.set(Boolean.FALSE);
        IGraphicalEditPart result = null;
        for (Object child : editPart.getChildren()) {
            if (!isNoLayout((EditPart) child)) {
                if (child instanceof AbstractBorderItemEditPart) {
                    hasPorts.set(Boolean.TRUE);
                    if (result != null) {
                        break;
                    }
                } else if (child instanceof ShapeNodeEditPart) {
                    result = editPart;
                    if (hasPorts.get()) {
                        break;
                    }
                } else if (child instanceof CompartmentEditPart) {
                    for (Object grandChild : ((CompartmentEditPart) child).getChildren()) {
                        if (grandChild instanceof ShapeNodeEditPart
                                && !isNoLayout((EditPart) grandChild)) {
                            result = (IGraphicalEditPart) child;
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }
    
    /**
     * Retrieve the notation view from the given context.
     * 
     * @param context a layout context
     * @return the notation view, or {@code null} if none can be determined
     */
    private View getNotationView(final LayoutContext context) {
        View notationView = context.getProperty(NOTATION_VIEW);
        if (notationView == null) {
            Object editPart = context.getProperty(LayoutContext.DIAGRAM_PART);
            if (editPart instanceof IGraphicalEditPart) {
                notationView = ((IGraphicalEditPart) editPart).getNotationView();
            } else {
                IWorkbenchPart workbenchPart = context.getProperty(EclipseLayoutConfig.WORKBENCH_PART);
                if (workbenchPart instanceof DiagramEditor) {
                    notationView = ((DiagramEditor) workbenchPart).getDiagram();
                }
            }
            context.setProperty(NOTATION_VIEW, notationView);
        }
        return notationView;
    }
    
    /**
     * Retrieve the diagram notation view from the given context.
     * 
     * @param context a layout context
     * @return the diagram notation view, or {@code null} if none can be determined
     */
    private View getDiagramNotationView(final LayoutContext context) {
        View notationView = context.getProperty(DIAGRAM_NOTATION_VIEW);
        if (notationView == null) {
            Object editPart = context.getProperty(LayoutContext.DIAGRAM_PART);
            if (editPart instanceof EditPart) {
                DiagramEditPart diagramEditPart = GmfDiagramLayoutManager.getDiagramEditPart(
                        (EditPart) editPart);
                if (diagramEditPart != null) {
                    notationView = diagramEditPart.getNotationView();
                    context.setProperty(DIAGRAM_NOTATION_VIEW, notationView);
                }
            }
        }
        return notationView;
    }

    /**
     * {@inheritDoc}
     */
    public Object getOptionValue(final LayoutOptionData optionData, final LayoutContext context) {
        View view = getNotationView(context);
        if (view != null) {
            // check option value from notation model
            Object result = getValue(optionData, PREFIX, view);
            if (result != null) {
                return result;
            }
        }

        // check default option of diagram edit part
        View diagramView = getDiagramNotationView(context);
        if (diagramView != null) {
            return getValue(optionData, DEF_PREFIX, diagramView);
        }
        
        return null;
    }

    /**
     * Get a property from the given notation view.
     * 
     * @param optionData the layout option
     * @param prefix the prefix for the style name
     * @param view the notation view
     * @return the value of the option, or {@code null}
     */
    private Object getValue(final LayoutOptionData optionData, final String prefix,
            final View view) {
        String optionKey = prefix + optionData.getId();
        for (Object obj : view.getStyles()) {
            if (obj instanceof StringValueStyle) {
                StringValueStyle style = (StringValueStyle) obj;
                if (optionKey.equals(style.getName())) {
                    Object result = optionData.parseValue(style.getStringValue());
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<IProperty<?>> getAffectedOptions(final LayoutContext context) {
        List<IProperty<?>> options = new LinkedList<IProperty<?>>();
        // add user defined global layout options
        View diagramView = getDiagramNotationView(context);
        if (diagramView != null) {
            getAffectedOptions(options, DEF_PREFIX, diagramView);
        }
        // add user defined local layout options
        View elementView = getNotationView(context);
        if (elementView != null) {
            getAffectedOptions(options, PREFIX, elementView);
        }
        return options;
    }

    /**
     * Add the options from the list of properties to the given list. Only properties whose key
     * starts with the given prefix are considered.
     * 
     * @param options list to which options are added
     * @param prefix the expected prefix of the property keys
     * @param view a notation view
     */
    private void getAffectedOptions(final List<IProperty<?>> options, final String prefix,
            final View view) {
        LayoutMetaDataService layoutService = LayoutMetaDataService.getInstance();
        for (Object obj : view.getStyles()) {
            if (obj instanceof StringValueStyle) {
                StringValueStyle style = (StringValueStyle) obj;
                String key = style.getName();
                if (key != null && key.startsWith(prefix)) {
                    LayoutOptionData optionData = layoutService.getOptionData(
                            key.substring(prefix.length()));
                    if (optionData != null) {
                        options.add(optionData);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setOptionValue(final LayoutOptionData optionData, final LayoutContext context,
            final Object value) {
        if (context.getProperty(LayoutContext.GLOBAL)) {
            View view = getDiagramNotationView(context);
            if (view != null) {
                if (value != null) {
                    removeValue(optionData, PREFIX, view, true);
                }
                setValue(optionData, value, DEF_PREFIX, view);
            }
        } else  {
            View view = getNotationView(context);
            if (view != null) {
                setValue(optionData, value, PREFIX, view);
            }
        }
    }

    /**
     * Set the option for the given notation view. Adds a new style to the view unless the given
     * key already exists.
     * 
     * @param optionData layout option data
     * @param value the value
     * @param prefix the prefix for the property key
     * @param view the notation view
     */
    @SuppressWarnings("unchecked")
    private void setValue(final LayoutOptionData optionData, final Object value,
            final String prefix, final View view) {
        if (value == null) {
            removeValue(optionData, prefix, view, false);
        } else {
            String optionKey = prefix + optionData.getId();
            for (Object obj : view.getStyles()) {
                if (obj instanceof StringValueStyle) {
                    StringValueStyle style = (StringValueStyle) obj;
                    if (optionKey.equals(style.getName())) {
                        style.setStringValue(value.toString());
                        return;
                    }
                }
            }
    
            StringValueStyle style = NotationFactory.eINSTANCE.createStringValueStyle();
            style.setName(optionKey);
            style.setStringValue(value.toString());
            view.getStyles().add(style);
        }
    }

    /**
     * Remove an option from the given notation view.
     * 
     * @param optionData layout option data
     * @param prefix the prefix for the property key
     * @param view the notation view
     * @param recursive whether options should also be removed from children
     */
    private void removeValue(final LayoutOptionData optionData, final String prefix,
            final View view, final boolean recursive) {
        String optionKey = prefix + optionData.getId();
        Iterator<?> iter = view.getStyles().iterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj instanceof StringValueStyle
                    && optionKey.equals(((StringValueStyle) obj).getName())) {
                iter.remove();
            }
        }
        
        if (recursive) {
            for (Object child : view.getPersistedChildren()) {
                removeValue(optionData, prefix, (View) child, true);
                if (child instanceof Diagram) {
                    for (Object edge : ((Diagram) child).getPersistedEdges()) {
                        removeValue(optionData, prefix, (View) edge, true);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void clearOptionValues(final LayoutContext context) {
        View view = getNotationView(context);
        if (view != null) {
            boolean recursive = context.getProperty(LayoutContext.GLOBAL);
            clearValues(view, recursive);
        }
    }
    
    /**
     * Removes all layout options from the given view and its children.
     * 
     * @param view a view from the notation model
     * @param recursive whether the children should also be processed
     */
    private void clearValues(final View view, final boolean recursive) {
        Iterator<?> iter = view.getStyles().iterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj instanceof StringValueStyle) {
                StringValueStyle style = (StringValueStyle) obj;
                String key = style.getName() == null ? "" : style.getName();
                if (key.startsWith(PREFIX) || key.startsWith(DEF_PREFIX)) {
                    iter.remove();
                }
            }
        }
        
        if (recursive) {
            for (Object child : view.getPersistedChildren()) {
                clearValues((View) child, true);
            }
            if (view instanceof Diagram) {
                for (Object child : ((Diagram) view).getPersistedEdges()) {
                    clearValues((View) child, true);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSet(final LayoutOptionData optionData, final LayoutContext context) {
        View view = getNotationView(context);
        if (view != null) {
            // check option value from notation model
            Object result = getValue(optionData, PREFIX, view);
            return result != null;
        }
        return false;
    }
    
}
