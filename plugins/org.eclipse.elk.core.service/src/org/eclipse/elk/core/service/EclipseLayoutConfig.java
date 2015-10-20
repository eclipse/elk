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
package org.eclipse.elk.core.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.elk.core.LayoutConfigService;
import org.eclipse.elk.core.LayoutMetaDataService;
import org.eclipse.elk.core.LayoutOptionData;
import org.eclipse.elk.core.config.DefaultLayoutConfig;
import org.eclipse.elk.core.config.ILayoutConfig;
import org.eclipse.elk.core.config.LayoutContext;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.ui.IWorkbenchPart;

/**
 * A layout configuration for extension point configurations and user preferences.
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public class EclipseLayoutConfig implements ILayoutConfig {
    
    /** the property for activation of the Eclipse layout config. */
    public static final Property<Boolean> ACTIVATION = new Property<Boolean>(
            "org.eclipse.elk.eclipse", true);
    
    /** the priority for the Eclipse layout configuration. */
    public static final int PRIORITY = 10;
    
    /** context property for the currently tracked diagram editor. */
    public static final IProperty<IWorkbenchPart> WORKBENCH_PART = new Property<IWorkbenchPart>(
            "context.workbenchPart");
    
    /** context property for the editing domain of a diagram editor. */
    public static final IProperty<EditingDomain> EDITING_DOMAIN = new Property<EditingDomain>(
            "context.editingDomain");
    
    /** context property for the aspect ratio of the currently processed diagram viewer. */
    public static final IProperty<Float> ASPECT_RATIO = new Property<Float>(
            "context.aspectRatio");
    
    /**
     * Retrieves a layout option value for the given edit part and model element by querying the option
     * for the edit part's class name and its domain model name.
     * 
     * If both diagramPart and modelElement are null it will return the 
     * value set by the global static layout configuration.
     * 
     * @param property the layout option data
     * @param diagramPart a diagram part such as an edit part
     * @param modelElement the corresponding domain model element
     * @return the current value for the given option, or {@code null}
     */
    public static Object getValue(final IProperty<?> property, final Object diagramPart,
            final Object modelElement) {
        LayoutConfigService configService = LayoutConfigService.getInstance();
        String id = property.getId();
        if (diagramPart != null) {
            // get option for the edit part class
            String clazzName = diagramPart.getClass().getName();
            Object value = configService.getOptionValue(clazzName, id);
            if (value != null) {
                return value;
            }
        }
        if (modelElement instanceof EObject) {
            // get option for the domain model element as EMF class
            EClass eclazz = ((EObject) modelElement).eClass();
            Object value = configService.getOptionValue(eclazz, id);
            if (value != null) {
                return value;
            }
        } else if (modelElement != null) {
            // get option for the domain model element as other class
            String clazzName = modelElement.getClass().getName();
            Object value = configService.getOptionValue(clazzName, id);
            if (value != null) {
                return value;
            }
        } else {
            Object value = configService.getOptionValue(
                    ExtensionLayoutConfigService.GLOBAL_OPTION_VALUE_ID, id);
            return value;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public int getPriority() {
        return PRIORITY;
    }

    /**
     * {@inheritDoc}
     */
    public Object getContextValue(final IProperty<?> property, final LayoutContext context) {
        if (property.equals(DefaultLayoutConfig.CONTENT_DIAGT)) {
            // set diagram type for the content of the main edit part
            Object diagPart = context.getProperty(LayoutContext.DIAGRAM_PART);
            Object domainElem = context.getProperty(LayoutContext.DOMAIN_MODEL);
            return getValue(LayoutOptions.DIAGRAM_TYPE, diagPart, domainElem);
            
        } else if (property.equals(DefaultLayoutConfig.CONTAINER_DIAGT)) {
            Object containerDiagPart = context.getProperty(LayoutContext.CONTAINER_DIAGRAM_PART);
            Object containerDomainElem = context.getProperty(LayoutContext.CONTAINER_DOMAIN_MODEL);
            return getValue(LayoutOptions.DIAGRAM_TYPE, containerDiagPart, containerDomainElem);
            
        } else if (property.equals(DefaultLayoutConfig.CONTENT_HINT)) {
            // set layout algorithm or type identifier for the content
            Object diagPart = context.getProperty(LayoutContext.DIAGRAM_PART);
            Object domainElem = context.getProperty(LayoutContext.DOMAIN_MODEL);
            String layoutHint = (String) getValue(LayoutOptions.ALGORITHM, diagPart, domainElem);
            if (layoutHint == null) {
                String diagramType = context.getProperty(DefaultLayoutConfig.CONTENT_DIAGT);
                layoutHint = (String) ExtensionLayoutConfigService.getInstance().getOptionValue(
                        diagramType, LayoutOptions.ALGORITHM.getId());
            }
            return layoutHint;
            
        } else if (property.equals(DefaultLayoutConfig.CONTAINER_HINT)) {
            Object containerDiagPart = context.getProperty(LayoutContext.CONTAINER_DIAGRAM_PART);
            Object containerDomainElem = context.getProperty(LayoutContext.CONTAINER_DOMAIN_MODEL);
            String containerLayoutHint = (String) getValue(LayoutOptions.ALGORITHM,
                    containerDiagPart, containerDomainElem);
            if (containerLayoutHint == null) {
                String containerDiagramType = context.getProperty(DefaultLayoutConfig.CONTAINER_DIAGT);
                containerLayoutHint = (String) LayoutConfigService.getInstance().getOptionValue(
                        containerDiagramType, LayoutOptions.ALGORITHM.getId());
            }
            return containerLayoutHint;
            
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getOptionValue(final LayoutOptionData optionData, final LayoutContext context) {
        LayoutConfigService configService = LayoutConfigService.getInstance();
        Object result = null;
        
        // check default value set for the actual edit part or its model element
        result = getValue(optionData, context.getProperty(LayoutContext.DIAGRAM_PART),
                context.getProperty(LayoutContext.DOMAIN_MODEL));
        if (result != null) {
            return result;
        }
        
        if (optionData.getTargets().contains(LayoutOptionData.Target.PARENTS)) {
            // check default value for the diagram type of the selection's content
            result = configService.getOptionValue(context.getProperty(
                    DefaultLayoutConfig.CONTENT_DIAGT),
                    optionData.getId());
            if (result != null) {
                return result;
            }
        } else {
            // check default value for the diagram type of the selection's container
            result = configService.getOptionValue(context.getProperty(
                    DefaultLayoutConfig.CONTAINER_DIAGT),
                    optionData.getId());
            if (result != null) {
                return result;
            }
        }
        
        // fall back to dynamic default value of specific options
        if (LayoutOptions.SIZE_CONSTRAINT.equals(optionData)) {
            return getSizeConstraintValue(context);
        } else if (LayoutOptions.PORT_CONSTRAINTS.equals(optionData)) {
            return getPortConstraintsValue(context);
        } else if (LayoutOptions.ASPECT_RATIO.equals(optionData)) {
            return getAspectRatioValue(context);
        }
        
        return null;
    }
    
    /**
     * Return the dynamic value for the size constraint option.
     * 
     * @param context a context for layout configuration
     * @return {@code null} if the selected node has no children, and {@code MINIMUM_SIZE}
     *          / {@code PORTS} otherwise
     */
    private EnumSet<SizeConstraint> getSizeConstraintValue(final LayoutContext context) {
        Set<LayoutOptionData.Target> targets = context.getProperty(LayoutContext.OPT_TARGETS);
        if (targets != null && targets.contains(LayoutOptionData.Target.NODES)) {
            if (!targets.contains(LayoutOptionData.Target.PARENTS)) {
                return SizeConstraint.fixed();
            }
            Boolean hasPorts = context.getProperty(DefaultLayoutConfig.HAS_PORTS);
            if (hasPorts != null && hasPorts) {
                return SizeConstraint.minimumSizeWithPorts();
            }
            return EnumSet.of(SizeConstraint.MINIMUM_SIZE);
        }
        return null; // default value: SizeConstraint.fixed()
    }
    
    /**
     * Return the dynamic value for the port constraints option.
     * 
     * @param context a context for layout configuration
     * @return {@code FIXED_POS} if the selected node has ports and no children,
     *          and {@code FREE} otherwise
     */
    private PortConstraints getPortConstraintsValue(final LayoutContext context) {
        Set<LayoutOptionData.Target> targets = context.getProperty(LayoutContext.OPT_TARGETS);
        Boolean hasPorts = context.getProperty(DefaultLayoutConfig.HAS_PORTS);
        if (targets != null && targets.contains(LayoutOptionData.Target.NODES) && hasPorts != null) {
            if (!targets.contains(LayoutOptionData.Target.PARENTS) && hasPorts) {
                return PortConstraints.FIXED_POS;
            } else {
                return PortConstraints.FREE;
            }
        }
        return null;
    }
    
    /**
     * Return the dynamic value for the aspect ratio option.
     * 
     * @param context a context for layout configuration
     * @return the aspect ratio, if it has been configured in the context
     */
    private Float getAspectRatioValue(final LayoutContext context) {
        Float aspectRatio = context.getProperty(ASPECT_RATIO);
        if (aspectRatio != null && aspectRatio > 0) {
             return aspectRatio;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<IProperty<?>> getAffectedOptions(final LayoutContext context) {
        List<IProperty<?>> options = new LinkedList<IProperty<?>>();
        
        // specific options with dynamic values
        Set<LayoutOptionData.Target> targets = context.getProperty(LayoutContext.OPT_TARGETS);
        if (targets != null && targets.contains(LayoutOptionData.Target.NODES)) {
            options.add(LayoutOptions.SIZE_CONSTRAINT);
            Boolean hasPorts = context.getProperty(DefaultLayoutConfig.HAS_PORTS);
            if (hasPorts != null) {
                options.add(LayoutOptions.PORT_CONSTRAINTS);
            }
        }
        Float aspectRatio = context.getProperty(ASPECT_RATIO);
        if (aspectRatio != null && aspectRatio > 0) {
            options.add(LayoutOptions.ASPECT_RATIO);
        }

        LayoutConfigService configService = LayoutConfigService.getInstance();
        LayoutMetaDataService dataService = LayoutMetaDataService.getInstance();
        Object diagPart = context.getProperty(LayoutContext.DIAGRAM_PART);
        Object modelElement = context.getProperty(LayoutContext.DOMAIN_MODEL);

        // get default layout options for the diagram type
        String diagramType = context.getProperty(DefaultLayoutConfig.CONTENT_DIAGT);
        if (diagramType != null) {
            for (Entry<String, Object> entry : configService.getOptionValues(diagramType).entrySet()) {
                if (entry.getValue() != null) {
                    LayoutOptionData optionData = dataService.getOptionData(entry.getKey());
                    if (optionData != null) {
                        options.add(optionData);
                    }
                }
            }
        }
        
        // get default layout options for the domain model element
        if (modelElement != null) {
            Map<String, Object> domainOptionsMap;
            if (modelElement instanceof EObject) {
                domainOptionsMap = configService.getOptionValues(((EObject) modelElement).eClass());
            } else {
                domainOptionsMap = configService.getOptionValues(modelElement.getClass().getName());
            }
            for (Entry<String, Object> entry : domainOptionsMap.entrySet()) {
                if (entry.getValue() != null) {
                    LayoutOptionData optionData = dataService.getOptionData(entry.getKey());
                    if (optionData != null) {
                        options.add(optionData);
                    }
                }
            }
        }
        
        // get default layout options for the edit part
        if (diagPart != null) {
            String clazzName = diagPart.getClass().getName();
            for (Entry<String, Object> entry : configService.getOptionValues(clazzName).entrySet()) {
                if (entry.getValue() != null) {
                    LayoutOptionData optionData = dataService.getOptionData(entry.getKey());
                    if (optionData != null) {
                        options.add(optionData);
                    }
                }
            }
        }
        return options;
    }
    
}
