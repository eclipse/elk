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

import org.eclipse.elk.core.service.ILayoutConfigurationStore;
import org.eclipse.elk.core.service.data.LayoutOptionData;
import org.eclipse.elk.core.service.data.LayoutOptionData.Target;
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

/**
 * Layout option configuration for Graphiti.
 * 
 * @author soh
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public class GraphitiLayoutConfigurationStore implements ILayoutConfigurationStore {
    
    /** Prefix for all layout options. */
    public static final String PREFIX = "layout:";
    
    /** The pictogram element used as context for this configuration store. */
    private final PictogramElement pictogramElement;
    /** */
    private final EditingDomain editingDomain;
    /** the layout manager for which this configurator was created. */
    private final GraphitiDiagramLayoutManager layoutManager;

    public GraphitiLayoutConfigurationStore(final PictogramElement thePictogramElement,
            final EditingDomain theEditingDomain, final GraphitiDiagramLayoutManager theLayoutManager) {
        this.pictogramElement = thePictogramElement;
        this.editingDomain = theEditingDomain;
        this.layoutManager = theLayoutManager;
    }

    /**
     * {@inheritDoc}
     */
    public Object getContext() {
        return pictogramElement;
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
    public Object getOptionValue(String optionId) {
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
    public void setOptionValue(String optionId, String value) {
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
    private void removeValue(String optionId) {
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
            if (layoutManager.isNodeShape(shape)) {
                targets.add(LayoutOptionData.Target.NODES);
                if (shape instanceof ContainerShape) {
                    for (Shape child : ((ContainerShape) shape).getChildren()) {
                        if (layoutManager.isNodeShape(child)) {
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
            if (ac instanceof Shape && layoutManager.isPortAnchor(anchor)) {
                return EnumSet.of(LayoutOptionData.Target.PORTS);
            }
        } else if (pictogramElement instanceof ConnectionDecorator) {
            ConnectionDecorator decorator = (ConnectionDecorator) pictogramElement;
            if (layoutManager.isEdgeLabel(decorator)) {
                return EnumSet.of(LayoutOptionData.Target.LABELS);
            }
        }
        return EnumSet.noneOf(LayoutOptionData.Target.class);
    }

    @Override
    public ILayoutConfigurationStore getParent() {
        PictogramElement container = getContainer();
        if (container != null) {
            return new GraphitiLayoutConfigurationStore(container, editingDomain, layoutManager);
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
