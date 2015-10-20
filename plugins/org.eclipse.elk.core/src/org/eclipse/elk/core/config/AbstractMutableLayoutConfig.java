/*******************************************************************************
 * Copyright (c) 2014, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.config;

import java.util.Collection;
import java.util.EnumSet;

import org.eclipse.elk.core.LayoutAlgorithmData;
import org.eclipse.elk.core.LayoutMetaDataService;
import org.eclipse.elk.core.LayoutOptionData;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.graph.properties.IProperty;

/**
 * An abstract superclass for the convenient implementation of mutable layout configurators.
 * 
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public abstract class AbstractMutableLayoutConfig implements IMutableLayoutConfig {
    
    /**
     * Create a context for the container element of the given context.
     * 
     * @param context a layout context
     * @return a new context in which the focus element is the container of the given context
     */
    public static LayoutContext createContainerContext(final LayoutContext context) {
        LayoutContext containerContext = new LayoutContext();
        if (context.getProperty(LayoutContext.GLOBAL)) {
            containerContext.setProperty(LayoutContext.GLOBAL, true);
        }
        Object diagramPart = context.getProperty(LayoutContext.CONTAINER_DIAGRAM_PART);
        if (diagramPart != null) {
            containerContext.setProperty(LayoutContext.DIAGRAM_PART, diagramPart);
        }
        Object domainModel = context.getProperty(LayoutContext.CONTAINER_DOMAIN_MODEL);
        if (domainModel != null) {
            containerContext.setProperty(LayoutContext.DOMAIN_MODEL, domainModel);
        }
        String layoutHint = context.getProperty(DefaultLayoutConfig.CONTAINER_HINT);
        if (layoutHint != null) {
            containerContext.setProperty(DefaultLayoutConfig.CONTENT_HINT, layoutHint);
        }
        String diagramType = context.getProperty(DefaultLayoutConfig.CONTAINER_DIAGT);
        if (diagramPart != null) {
            containerContext.setProperty(DefaultLayoutConfig.CONTENT_DIAGT, diagramType);
        }
        LayoutAlgorithmData algoData = context.getProperty(DefaultLayoutConfig.CONTAINER_ALGO);
        if (algoData != null) {
            containerContext.setProperty(DefaultLayoutConfig.CONTENT_ALGO, algoData);
        }
        containerContext.setProperty(LayoutContext.OPT_TARGETS,
                EnumSet.of(LayoutOptionData.Target.PARENTS));
        return containerContext;
    }

    /**
     * {@inheritDoc}
     */
    public Object getContextValue(final IProperty<?> property, final LayoutContext context) {
        if (property.equals(DefaultLayoutConfig.CONTENT_HINT)) {
            // set layout algorithm hint for the content of the selected element
            LayoutOptionData algorithmData = LayoutMetaDataService.getInstance().getOptionData(
                    LayoutOptions.ALGORITHM.getId());
            if (algorithmData != null) {
                return getOptionValue(algorithmData, context);
            }
            
        } else if (property.equals(DefaultLayoutConfig.CONTENT_DIAGT)) {
            // set diagram type for the content of the selected element
            LayoutOptionData diagTypeData = LayoutMetaDataService.getInstance().getOptionData(
                    LayoutOptions.DIAGRAM_TYPE.getId());
            if (diagTypeData != null) {
                return getOptionValue(diagTypeData, context);
            }
            
        } else if (property.equals(DefaultLayoutConfig.CONTAINER_HINT)) {
            // set layout algorithm hint for the container of the selected element
            LayoutOptionData algorithmData = LayoutMetaDataService.getInstance().getOptionData(
                    LayoutOptions.ALGORITHM.getId());
            if (algorithmData != null) {
                return getOptionValue(algorithmData, createContainerContext(context));
            }
            
        } else if (property.equals(DefaultLayoutConfig.CONTAINER_DIAGT)) {
            // set diagram type for the container of the selected element
            LayoutOptionData diagTypeData = LayoutMetaDataService.getInstance().getOptionData(
                    LayoutOptions.DIAGRAM_TYPE.getId());
            if (diagTypeData != null) {
                return getOptionValue(diagTypeData, createContainerContext(context));
            }
            
        }
        
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void clearOptionValues(final LayoutContext context) {
        LayoutMetaDataService layoutDataService = LayoutMetaDataService.getInstance();
        Collection<IProperty<?>> affectedOptions = getAffectedOptions(context);
        if (affectedOptions != null) {
            for (IProperty<?> property : affectedOptions) {
                LayoutOptionData optionData;
                if (property instanceof LayoutOptionData) {
                    optionData = (LayoutOptionData) property;
                } else {
                    optionData = layoutDataService.getOptionData(property.getId());
                }
                
                if (optionData != null) {
                    setOptionValue(optionData, context, null);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSet(final LayoutOptionData optionData, final LayoutContext context) {
        return getOptionValue(optionData, context) != null;
    }

}
