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
package org.eclipse.elk.graphiti;

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
import org.eclipse.gef.EditPart;
import org.eclipse.graphiti.mm.MmFactory;
import org.eclipse.graphiti.mm.Property;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.mm.pictograms.util.PictogramsSwitch;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.internal.parts.IPictogramElementEditPart;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Layout option configuration for Graphiti.
 * 
 * @author soh
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
@SuppressWarnings("restriction")
public class GraphitiLayoutConfig implements IMutableLayoutConfig {
    
    /** the priority for the Graphiti layout configuration. */
    public static final int PRIORITY = 30;

    /** Prefix for all layout options. */
    public static final String PREFIX = "layout:";
    /** Prefix for diagram defaults stored in the top-level edit part. */
    public static final String DEF_PREFIX = "defaultLayout:";
    
    /** the pictogram element for the graph element in the context. */
    public static final IProperty<PictogramElement> PICTO_ELEM
            = new org.eclipse.elk.graph.properties.Property<PictogramElement>(
                    "context.pictogramElement");
    /** the diagram element of the pictogram model in the context. */
    public static final IProperty<Diagram> DIAGRAM
            = new org.eclipse.elk.graph.properties.Property<Diagram>(
                    "context.diagramPictogram");
    
    /** the layout manager for which this configurator was created. */
    private GraphitiDiagramLayoutManager layoutManager;

    /**
     * Create a Graphiti layout configurator for the given layout manager.
     * 
     * @param layoutManager a Graphiti layout manager
     */
    public GraphitiLayoutConfig(final GraphitiDiagramLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }
    
    /**
     * {@inheritDoc}
     */
    public int getPriority() {
        return PRIORITY;
    }
    
    private static final float ASPECT_RATIO_ROUND = 100;

    /**
     * {@inheritDoc}
     */
    public Object getContextValue(final IProperty<?> property, final LayoutContext context) {
        if (property.equals(EclipseLayoutConfig.ASPECT_RATIO)) {
            // get aspect ratio for the current diagram
            IWorkbenchPart workbenchPart = context.getProperty(EclipseLayoutConfig.WORKBENCH_PART);
            if (workbenchPart instanceof DiagramEditor) {
                final Control control = ((DiagramEditor) workbenchPart).getGraphicalViewer()
                        .getControl();
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
            return null;
            
        } else if (property.equals(EclipseLayoutConfig.EDITING_DOMAIN)) {
            IWorkbenchPart workbenchPart = context.getProperty(EclipseLayoutConfig.WORKBENCH_PART);
            if (workbenchPart instanceof DiagramEditor) {
                return ((DiagramEditor) workbenchPart).getEditingDomain();
            }
            return null;
        }
        
        PictogramElement pictogramElem = getPictogramElement(context);
        if (pictogramElem != null) {
            if (property.equals(LayoutContext.DIAGRAM_PART)) {
                return pictogramElem;
                
            } else if (property.equals(LayoutContext.CONTAINER_DIAGRAM_PART)) {
                return getContainer(pictogramElem);
                
            } else if (property.equals(LayoutContext.DOMAIN_MODEL)) {
                // add domain model element to the context
                PictogramLink link = pictogramElem.getLink();
                if (link != null && link.getBusinessObjects().size() > 0) {
                    return link.getBusinessObjects().get(0);
                }
                
            } else if (property.equals(LayoutContext.CONTAINER_DOMAIN_MODEL)) {
                // add domain model element of the container element to the context
                PictogramElement containerPe = getContainer(pictogramElem);
                if (containerPe != null) {
                    PictogramLink link = containerPe.getLink();
                    if (link != null && link.getBusinessObjects().size() > 0) {
                        return link.getBusinessObjects().get(0);
                    }
                }
                
            } else if (property.equals(LayoutContext.OPT_TARGETS)) {
                return findTarget(pictogramElem);
                
            } else if (property.equals(DefaultLayoutConfig.HAS_PORTS)) {
                // set whether the selected element is a node that contains ports
                if (pictogramElem instanceof Shape) {
                    // the same check for ports as in the layout manager must be made here
                    for (Anchor anchor : ((Shape) pictogramElem).getAnchors()) {
                        if (layoutManager.isPortAnchor(anchor)) {
                            return true;
                        }
                    }
                }
                return false;
                
            } else if (property.equals(DefaultLayoutConfig.CONTENT_HINT)) {
                // get a layout hint for the content of the focused pictogram element
                LayoutOptionData algorithmOptionData = LayoutMetaDataService.getInstance()
                        .getOptionData(LayoutOptions.ALGORITHM.getId());
                if (algorithmOptionData != null) {
                    String contentLayoutHint = (String) getValue(algorithmOptionData, PREFIX,
                            pictogramElem);
                    if (contentLayoutHint == null) {
                        Diagram diagram = getDiagram(context);
                        if (diagram != null) {
                            contentLayoutHint = (String) getValue(algorithmOptionData, DEF_PREFIX,
                                    diagram);
                        }
                    }
                    return contentLayoutHint;
                }
                
            } else if (property.equals(DefaultLayoutConfig.CONTAINER_HINT)) {
                // get a layout hint for the container edit part
                LayoutOptionData algorithmOptionData = LayoutMetaDataService.getInstance()
                        .getOptionData(LayoutOptions.ALGORITHM.getId());
                PictogramElement containerPe = getContainer(pictogramElem);
                if (algorithmOptionData != null && containerPe != null) {
                    String containerLayoutHint = (String) getValue(algorithmOptionData, PREFIX,
                            containerPe);
                    if (containerLayoutHint == null) {
                        Diagram diagram = getDiagram(context);
                        if (diagram != null) {
                            containerLayoutHint = (String) getValue(algorithmOptionData, DEF_PREFIX,
                                    diagram);
                        }
                    }
                    return containerLayoutHint;
                }
                
            }
        }
        return null;
    }
    
    /**
     * Determine the type of edit part target for layout options.
     * 
     * @param pe a pictogram element
     * @return the layout option targets
     */
    private Set<LayoutOptionData.Target> findTarget(final PictogramElement pe) {
        PictogramsSwitch<Set<LayoutOptionData.Target>> pictogramsSwitch
                = new PictogramsSwitch<Set<LayoutOptionData.Target>>() {
            public Set<LayoutOptionData.Target> caseDiagram(final Diagram diagram) {
                return EnumSet.of(LayoutOptionData.Target.PARENTS);
            }
            public Set<LayoutOptionData.Target> caseShape(final Shape shape) {
                Set<LayoutOptionData.Target> targets = EnumSet.noneOf(LayoutOptionData.Target.class);
                if (layoutManager.isNodeShape(shape)) {
                    targets.add(LayoutOptionData.Target.NODES);
                    if (pe instanceof ContainerShape) {
                        for (Shape child : ((ContainerShape) pe).getChildren()) {
                            if (layoutManager.isNodeShape(child)) {
                                targets.add(LayoutOptionData.Target.PARENTS);
                                break;
                            }
                        }
                    }
                }
                return targets;
            }
            public Set<LayoutOptionData.Target> caseConnection(final Connection connection) {
                AnchorContainer ac = connection.getStart().getParent();
                if (ac instanceof Shape) {
                    return EnumSet.of(LayoutOptionData.Target.EDGES);
                }
                return null;
            }
            public Set<LayoutOptionData.Target> caseAnchor(final Anchor anchor) {
                AnchorContainer ac = anchor.getParent();
                if (ac instanceof Shape && layoutManager.isPortAnchor(anchor)) {
                    return EnumSet.of(LayoutOptionData.Target.PORTS);
                }
                return null;
            }
            public Set<LayoutOptionData.Target> caseConnectionDecorator(
                    final ConnectionDecorator decorator) {
                if (layoutManager.isEdgeLabel(decorator)) {
                    return EnumSet.of(LayoutOptionData.Target.LABELS);
                }
                return EnumSet.noneOf(LayoutOptionData.Target.class);
            }
        };
        return pictogramsSwitch.doSwitch(pe);
    }
    
    /**
     * Determine the container element of the given pictogram element.
     * 
     * @param pe a pictogram element
     * @return the container element
     */
    private PictogramElement getContainer(final PictogramElement pe) {
        PictogramsSwitch<PictogramElement> pictogramsSwitch
                = new PictogramsSwitch<PictogramElement>() {
            public PictogramElement caseDiagram(final Diagram diagram) {
                return diagram;
            }
            public PictogramElement caseShape(final Shape shape) {
                return shape.getContainer();
            }
            public PictogramElement caseConnection(final Connection connection) {
                AnchorContainer ac = connection.getStart().getParent();
                if (ac instanceof Shape) {
                    return ((Shape) ac).getContainer();
                }
                return null;
            }
            public PictogramElement caseAnchor(final Anchor anchor) {
                AnchorContainer ac = anchor.getParent();
                if (ac instanceof Shape) {
                    return ((Shape) ac).getContainer();
                }
                return null;
            }
        };
        return pictogramsSwitch.doSwitch(pe);
    }
    
    /**
     * Determine the pictogram element from the given context.
     * 
     * @param context a layout context
     * @return the corresponding pictogram element, or {@code null} if none can be determined
     */
    private PictogramElement getPictogramElement(final LayoutContext context) {
        PictogramElement pictogramElem = context.getProperty(PICTO_ELEM);
        if (pictogramElem == null) {
            Object diagramPart = context.getProperty(LayoutContext.DIAGRAM_PART);
            if (diagramPart instanceof PictogramElement) {
                pictogramElem = (PictogramElement) diagramPart;
            } else if (diagramPart instanceof IPictogramElementEditPart) {
                IPictogramElementEditPart focusEditPart = (IPictogramElementEditPart) diagramPart;
                pictogramElem = focusEditPart.getPictogramElement();
            } else {
                IWorkbenchPart workbenchPart = context.getProperty(EclipseLayoutConfig.WORKBENCH_PART);
                if (workbenchPart instanceof DiagramEditor) {
                    EditPart contents = ((DiagramEditor) workbenchPart).getGraphicalViewer()
                            .getContents();
                    if (contents instanceof IPictogramElementEditPart) {
                        IPictogramElementEditPart focusEditPart = (IPictogramElementEditPart) contents;
                        pictogramElem = focusEditPart.getPictogramElement();
                    }
                }
            }
            context.setProperty(PICTO_ELEM, pictogramElem);
        }
        return pictogramElem;
    }
    
    /**
     * Determine the diagram element from the given context.
     * 
     * @param context a layout context
     * @return the corresponding diagram element, or {@code null} if none can be determined
     */
    private Diagram getDiagram(final LayoutContext context) {
        Diagram diagram = context.getProperty(DIAGRAM);
        if (diagram == null) {
            Object diagramPart = context.getProperty(LayoutContext.DIAGRAM_PART);
            if (diagramPart instanceof IPictogramElementEditPart) {
                IPictogramElementEditPart focusEditPart = (IPictogramElementEditPart) diagramPart;
                diagram = focusEditPart.getConfigurationProvider().getDiagram();
            } else if (diagramPart instanceof PictogramElement) {
                PictogramElement pictogramElement = (PictogramElement) diagramPart;
                while (pictogramElement != null && !(pictogramElement instanceof Diagram)) {
                    pictogramElement = (PictogramElement) pictogramElement.eContainer();
                }
                if (pictogramElement != null) {
                    diagram = (Diagram) pictogramElement;
                }
            }
            context.setProperty(DIAGRAM, diagram);
        }
        return diagram;
    }

    /**
     * {@inheritDoc}
     */
    public Object getOptionValue(final LayoutOptionData optionData, final LayoutContext context) {
        PictogramElement pictogramElement = getPictogramElement(context);
        if (pictogramElement != null) {
            Object result = getValue(optionData, PREFIX, pictogramElement);
            if (result != null) {
                return result;
            }
        }
        
        // check default option of diagram
        Diagram diagram = getDiagram(context);
        if (diagram != null) {
            return getValue(optionData, DEF_PREFIX, diagram);
        }
        return null;
    }

    /**
     * Get a property from the given pictogram element.
     * 
     * @param <T>
     *            the type of the value of the option
     * @param optionData
     *            the layout option
     * @param prefix
     *            the prefix for the property key
     * @param pictogramElement
     *            a pictogram element
     * @return the value of the option, or {@code null}
     */
    private Object getValue(final LayoutOptionData optionData, final String prefix,
            final PictogramElement pictogramElement) {
        String optionKey = prefix + optionData.getId();
        for (Property p : pictogramElement.getProperties()) {
            if (optionKey.equals(p.getKey())) {
                Object result = optionData.parseValue(p.getValue());
                if (result != null) {
                    return result;
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
        Diagram diagram = getDiagram(context);
        if (diagram != null) {
            addAffectedOptions(options, DEF_PREFIX, diagram);
        }
        // add user defined local layout options
        PictogramElement pictogramElement = getPictogramElement(context);
        if (pictogramElement != null) {
            addAffectedOptions(options, PREFIX, pictogramElement);
        }
        return options;
    }

    /**
     * Add the options from the list of properties to the given list.
     * 
     * @param options list to which options are added
     * @param prefix the prefix for the property key
     * @param pe a pictogram element
     */
    private void addAffectedOptions(final List<IProperty<?>> options, final String prefix,
            final PictogramElement pe) {
        LayoutMetaDataService layoutServices = LayoutMetaDataService.getInstance();
        for (Property prop : pe.getProperties()) {
            String key = prop.getKey();
            if (key != null && key.startsWith(prefix)) {
                LayoutOptionData optionData = layoutServices.getOptionData(
                        key.substring(prefix.length()));
                if (optionData != null) {
                    options.add(optionData);
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
            Diagram diagram = getDiagram(context);
            if (diagram != null) {
                if (value != null) {
                    removeValue(optionData, PREFIX, diagram, true);
                }
                setValue(optionData, value, DEF_PREFIX, diagram);
            }
        } else {
            PictogramElement pictogramElement = getPictogramElement(context);
            if (pictogramElement != null) {
                setValue(optionData, value, PREFIX, pictogramElement);
            }
        }
    }

    /**
     * Set the option for the given pictogram element. Adds a new property to the property list
     * unless the given key already exists.
     * 
     * @param optionData layout option data
     * @param value the value
     * @param prefix the prefix for the property key
     * @param pictogramElement the pictogram element
     */
    private void setValue(final LayoutOptionData optionData, final Object value,
            final String prefix, final PictogramElement pictogramElement) {
        if (value == null) {
            removeValue(optionData, prefix, pictogramElement, false);
        } else {
            String optionKey = prefix + optionData.getId();
            for (Property p : pictogramElement.getProperties()) {
                if (optionKey.equals(p.getKey())) {
                    p.setValue(value.toString());
                    return;
                }
            }

            Property p = MmFactory.eINSTANCE.createProperty();
            p.setKey(prefix + optionData.getId());
            p.setValue(value.toString());
            pictogramElement.getProperties().add(p);
        }
    }

    /**
     * Remove an option from the given pictogram element.
     * 
     * @param optionData layout option data
     * @param prefix the prefix for the property key
     * @param pictogramElement the pictogram element
     * @param recursive whether options should also be removed from children
     */
    private void removeValue(final LayoutOptionData optionData, final String prefix,
            final PictogramElement pictogramElement, final boolean recursive) {
        Iterator<Property> iter = pictogramElement.getProperties().iterator();
        String optionKey = prefix + optionData.getId();
        while (iter.hasNext()) {
            Property p = iter.next();
            if (optionKey.equals(p.getKey())) {
                iter.remove();
            }
        }
        
        if (recursive) {
            if (pictogramElement instanceof ContainerShape) {
                ContainerShape cs = (ContainerShape) pictogramElement;
                for (Shape shape : cs.getChildren()) {
                    removeValue(optionData, prefix, shape, true);
                }
                for (Anchor anchor : cs.getAnchors()) {
                    removeValue(optionData, prefix, anchor, true);
                }
                if (cs instanceof Diagram) {
                    for (Connection connection : ((Diagram) cs).getConnections()) {
                        removeValue(optionData, prefix, connection, true);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void clearOptionValues(final LayoutContext context) {
        PictogramElement pe = getPictogramElement(context);
        if (pe != null) {
            boolean recursive = context.getProperty(LayoutContext.GLOBAL);
            clearValues(pe, recursive);
        }
    }

    /**
     * Clear all layout options from the given pictogram element.
     * 
     * @param pictogramElement a pictogram element
     * @param recursive whether the children should also be processed
     */
    private static void clearValues(final PictogramElement pictogramElement,
            final boolean recursive) {
        Iterator<Property> iter = pictogramElement.getProperties().iterator();
        while (iter.hasNext()) {
            Property p = iter.next();
            String key = p.getKey() == null ? "" : p.getKey();
            if (key.startsWith(PREFIX) || key.startsWith(DEF_PREFIX)) {
                iter.remove();
            }
        }

        if (recursive) {
            if (pictogramElement instanceof ContainerShape) {
                ContainerShape cs = (ContainerShape) pictogramElement;
                for (Shape shape : cs.getChildren()) {
                    clearValues(shape, true);
                }
                for (Anchor anchor : cs.getAnchors()) {
                    clearValues(anchor, true);
                }
                if (cs instanceof Diagram) {
                    for (Connection connection : ((Diagram) cs).getConnections()) {
                        clearValues(connection, true);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSet(final LayoutOptionData optionData, final LayoutContext context) {
        PictogramElement pe = getPictogramElement(context);
        if (pe != null) {
            Object result = getValue(optionData, PREFIX, pe);
            return result != null;
        }
        return false;
    }

}
