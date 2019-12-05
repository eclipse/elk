/*******************************************************************************
 * Copyright (c) 2009, 2015 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.graphiti;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.core.data.LayoutOptionData.Target;
import org.eclipse.elk.core.service.ILayoutConfigurationStore;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.graphiti.mm.MmFactory;
import org.eclipse.graphiti.mm.Property;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.internal.parts.IPictogramElementEditPart;
import org.eclipse.ui.IWorkbenchPart;

import com.google.inject.Inject;

/**
 * Layout option configuration for Graphiti.
 * 
 * @author soh
 * @author msp
 */
public class GraphitiLayoutConfigurationStore implements ILayoutConfigurationStore {
    
    /**
     * Provider for Graphiti layout configuration stores.
     */
    public static final class Provider implements ILayoutConfigurationStore.Provider {
        
        @Inject
        private GraphElementIndicator graphElemIndicator;

        @Override
        public ILayoutConfigurationStore get(final IWorkbenchPart workbenchPart, final Object context) {
            EditingDomain editingDomain = null;
            if (workbenchPart instanceof DiagramEditor) {
                editingDomain = ((DiagramEditor) workbenchPart).getEditingDomain();
            }
            if (context instanceof PictogramElement) {
                return new GraphitiLayoutConfigurationStore((PictogramElement) context, editingDomain,
                        graphElemIndicator);
            } else if (context instanceof IPictogramElementEditPart) {
                IPictogramElementEditPart editPart = (IPictogramElementEditPart) context;
                return new GraphitiLayoutConfigurationStore(editPart.getPictogramElement(), editingDomain,
                        graphElemIndicator);
            }
            return null;
        }
        
    }
    
    /** Prefix for all layout options. */
    public static final String PREFIX = "layout:";
    
    /** The pictogram element used as context for this configuration store. */
    private final PictogramElement pictogramElement;
    /** The editing domain for performing changes. */
    private final EditingDomain editingDomain;
    /** The graph element indicator for recognizing the graph structure. */
    private GraphElementIndicator graphElemIndicator;

    /**
     * Create a Graphiti layout configuration store.
     */
    public GraphitiLayoutConfigurationStore(final PictogramElement thePictogramElement,
            final EditingDomain theEditingDomain, final GraphElementIndicator indicator) {
        this.pictogramElement = thePictogramElement;
        this.editingDomain = theEditingDomain;
        this.graphElemIndicator = indicator;
    }

    /**
     * {@inheritDoc}
     */
    public EditingDomain getEditingDomain() {
        return editingDomain;
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getOptionValue(final String optionId) {
        String optionKey = PREFIX + optionId;
        for (Property p : pictogramElement.getProperties()) {
            if (optionKey.equals(p.getKey())) {
                Object result = p.getValue();
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
    public void setOptionValue(final String optionId, final String value) {
        if (value == null) {
            removeValue(optionId);
        } else {
            String optionKey = PREFIX + optionId;
            for (Property p : pictogramElement.getProperties()) {
                if (optionKey.equals(p.getKey())) {
                    p.setValue(value.toString());
                    return;
                }
            }

            Property p = MmFactory.eINSTANCE.createProperty();
            p.setKey(optionKey);
            p.setValue(value.toString());
            pictogramElement.getProperties().add(p);
        }
    }

    /**
     * Remove the option with the given id.
     */
    private void removeValue(final String optionId) {
        Iterator<Property> iter = pictogramElement.getProperties().iterator();
        String optionKey = PREFIX + optionId;
        while (iter.hasNext()) {
            Property p = iter.next();
            if (optionKey.equals(p.getKey())) {
                iter.remove();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getAffectedOptions() {
        List<String> options = new LinkedList<String>();
        Iterator<Property> iter = pictogramElement.getProperties().iterator();
        while (iter.hasNext()) {
            Property p = iter.next();
            String key = p.getKey();
            if (key != null && key.startsWith(PREFIX)) {
                options.add(key.substring(PREFIX.length()));
            }
        }
        return options;
    }

    @Override
    public Set<Target> getOptionTargets() {
        if (pictogramElement instanceof Diagram) {
            return EnumSet.of(LayoutOptionData.Target.PARENTS);
        } else if (pictogramElement instanceof Shape) {
            Shape shape = (Shape) pictogramElement;
            Set<LayoutOptionData.Target> targets = EnumSet.noneOf(LayoutOptionData.Target.class);
            if (graphElemIndicator.isNodeShape(shape)) {
                targets.add(LayoutOptionData.Target.NODES);
                if (shape instanceof ContainerShape) {
                    for (Shape child : ((ContainerShape) shape).getChildren()) {
                        if (graphElemIndicator.isNodeShape(child)) {
                            targets.add(LayoutOptionData.Target.PARENTS);
                            break;
                        }
                    }
                }
            }
            return targets;
        } else if (pictogramElement instanceof Connection) {
            Connection connection = (Connection) pictogramElement;
            AnchorContainer ac = connection.getStart().getParent();
            if (ac instanceof Shape) {
                return EnumSet.of(LayoutOptionData.Target.EDGES);
            }
        } else if (pictogramElement instanceof Anchor) {
            Anchor anchor = (Anchor) pictogramElement;
            AnchorContainer ac = anchor.getParent();
            if (ac instanceof Shape && graphElemIndicator.isPortAnchor(anchor)) {
                return EnumSet.of(LayoutOptionData.Target.PORTS);
            }
        } else if (pictogramElement instanceof ConnectionDecorator) {
            ConnectionDecorator decorator = (ConnectionDecorator) pictogramElement;
            if (graphElemIndicator.isEdgeLabel(decorator)) {
                return EnumSet.of(LayoutOptionData.Target.LABELS);
            }
        }
        return EnumSet.noneOf(LayoutOptionData.Target.class);
    }

    @Override
    public ILayoutConfigurationStore getParent() {
        PictogramElement container = getContainer();
        if (container != null) {
            return new GraphitiLayoutConfigurationStore(container, editingDomain, graphElemIndicator);
        }
        return null;
    }
    
    /**
     * Determine the container element of the context pictogram element.
     */
    private PictogramElement getContainer() {
        if (pictogramElement instanceof Shape) {
            return ((Shape) pictogramElement).getContainer();
        } else if (pictogramElement instanceof Connection) {
            AnchorContainer ac = ((Connection) pictogramElement).getStart().getParent();
            if (ac instanceof Shape) {
                return ((Shape) ac).getContainer();
            }
        } else if (pictogramElement instanceof Anchor) {
            AnchorContainer ac = ((Anchor) pictogramElement).getParent();
            if (ac instanceof Shape) {
                return ((Shape) ac).getContainer();
            }
        }
        return null;
    }

}
