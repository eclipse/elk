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
package org.eclipse.elk.core.ui.views;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.eclipse.elk.core.LayoutAlgorithmData;
import org.eclipse.elk.core.LayoutMetaDataService;
import org.eclipse.elk.core.LayoutOptionData;
import org.eclipse.elk.core.LayoutTypeData;
import org.eclipse.elk.core.config.DefaultLayoutConfig;
import org.eclipse.elk.core.config.IMutableLayoutConfig;
import org.eclipse.elk.core.config.LayoutContext;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.service.DiagramLayoutEngine;
import org.eclipse.elk.core.service.EclipseLayoutConfig;
import org.eclipse.elk.core.ui.Messages;
import org.eclipse.elk.core.ui.util.KimlUiUtil;
import org.eclipse.elk.core.util.Pair;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * A property source for layout options for GMF diagrams.
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating yellow 2012-10-26 review KI-29 by cmot, sgu
 */
public class LayoutPropertySource implements IPropertySource {
    
    /** the layout configuration for this property source. */
    private IMutableLayoutConfig layoutConfig;
    /** the layout context describing which element has been selected. */
    private LayoutContext layoutContext;
    /** the editing domain that is used for model changes. */
    private EditingDomain editingDomain;
    /** array of property descriptors for the option data. */
    private IPropertyDescriptor[] propertyDescriptors;
    /** set of layout option identifiers that can affect the visibility of other options. */
    private final Set<String> dependencyOptions = new HashSet<String>();

    /**
     * Creates a layout property source for the given layout configuration.
     * 
     * @param config a mutable layout configuration
     * @param context a layout context describing which element has been selected
     */
    public LayoutPropertySource(final IMutableLayoutConfig config,
            final LayoutContext context) {
        this.layoutConfig = config;
        this.layoutContext = context;
    }
    
    /**
     * Return the layout context used for this property source.
     * 
     * @return the layout context
     */
    public LayoutContext getContext() {
        return layoutContext;
    }

    /**
     * {@inheritDoc}
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        if (propertyDescriptors == null) {
            // enrich the layout context
            DiagramLayoutEngine.INSTANCE.getOptionManager().enrich(layoutContext, layoutConfig, true);
            List<LayoutOptionData> optionData = layoutContext.getProperty(
                    DefaultLayoutConfig.OPTIONS);
            Set<LayoutOptionData.Target> elementTargets = layoutContext.getProperty(
                    LayoutContext.OPT_TARGETS);
            editingDomain = layoutContext.getProperty(EclipseLayoutConfig.EDITING_DOMAIN);
            
            // filter the options hidden by option dependencies
            filterDependencies(optionData);
            
            propertyDescriptors = new IPropertyDescriptor[optionData.size()];
            ListIterator<LayoutOptionData> optionIter = optionData.listIterator();
            while (optionIter.hasNext()) {
                LayoutOptionData data = optionIter.next();
                propertyDescriptors[optionIter.previousIndex()] = new LayoutPropertyDescriptor(data,
                        elementTargets);
            }
        }
        return propertyDescriptors;
    }
    
    /**
     * Remove options that should not be visible from the given list. Options that have dependencies
     * are only visible if the dependencies are met. A dependency is met if it has a target value
     * that equals the actual value, or if it has no target value and the actual value is anything
     * but {@code null}.
     * 
     * @param optionData a list of option meta data
     */
    private void filterDependencies(final List<LayoutOptionData> optionData) {
        // the layout algorithm option always affects other options
        dependencyOptions.add(LayoutOptions.ALGORITHM.getId());
        
        ListIterator<LayoutOptionData> optionIter = optionData.listIterator();
        while (optionIter.hasNext()) {
            LayoutOptionData option = optionIter.next();
            boolean visible = option.getDependencies().isEmpty();
            for (Pair<LayoutOptionData, Object> dependency : option.getDependencies()) {
                // if at least one dependency is met, the option is made visible
                LayoutOptionData targetOption = dependency.getFirst();
                dependencyOptions.add(targetOption.getId());
                Object expectedValue = dependency.getSecond();
                Object value = layoutConfig.getOptionValue(targetOption, layoutContext);
                if (expectedValue == null && value != null
                        || expectedValue != null && expectedValue.equals(value)) {
                    visible = true;
                    break;
                }
            }
            
            if (!visible) {
                optionIter.remove();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object getPropertyValue(final Object id) {
        LayoutMetaDataService layoutServices = LayoutMetaDataService.getInstance();
        LayoutOptionData optionData = layoutServices.getOptionData((String) id);
        if (optionData != null) {
            Object value;
            if (LayoutOptions.ALGORITHM.getId().equals(id)) {
                value = layoutContext.getProperty(DefaultLayoutConfig.CONTENT_ALGO).getId();
            } else {
                value = layoutConfig.getOptionValue(optionData, layoutContext);
            }
            return translateValue(value, optionData);
        }
        return null;
    }
    
    /**
     * Translate a layout option value into an object that can be handled by the cell
     * editors of the layout view.
     * 
     * @param value a layout option value
     * @param optionData the corresponding layout option data
     * @return a cell editor value
     */
    @SuppressWarnings("rawtypes")
    private static Object translateValue(final Object value, final LayoutOptionData optionData) {
        if (value == null) {
            return "";
        }
        switch (optionData.getType()) {
        case INT:
        case FLOAT:           // TextCellEditor
            return value.toString();
        case BOOLEAN:         // ComboBoxCellEditor
            if (value instanceof Boolean) {
                return Integer.valueOf(((Boolean) value) ? 1 : 0);
            } else if (value instanceof String) {
                return Integer.valueOf(Boolean.valueOf((String) value) ? 1 : 0);
            } else {
                return value;
            }
        case ENUM:            // ComboBoxCellEditor
            if (value instanceof Enum<?>) {
                return ((Enum<?>) value).ordinal();
            } else if (value instanceof String) {
                String[] choices = optionData.getChoices();
                for (int i = 0; i < choices.length; i++) {
                    if (choices[i].equals(value)) {
                        return i;
                    }
                }
                return 0;
            }
            return value;
        case ENUMSET:         // MultipleOptionsCellEditor
            Set set = (Set) value;
            String[] result = new String[set.size()];
            
            Iterator iterator = set.iterator();
            for (int i = 0; iterator.hasNext(); i++) {
                Object o = iterator.next();
                
                if (o instanceof Enum) {
                    result[i] = ((Enum) o).name();
                } else {
                    result[i] = ((String) o);
                }
            }
            
            return result;
        case OBJECT:          // TextCellEditor
            return value.toString();
        default:
            return value;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void setPropertyValue(final Object id, final Object thevalue) {
        final LayoutOptionData optionData = LayoutMetaDataService.getInstance()
                .getOptionData((String) id);
        if (optionData != null) {
            Runnable modelChange = new Runnable() {
                public void run() {
                    Object value = thevalue;
                    switch (optionData.getType()) {
                    case STRING:
                        break;
                    case BOOLEAN:
                        value = Boolean.valueOf((Integer) value == 1);
                        break;
                    case ENUM:
                        value = optionData.getEnumValue((Integer) value);
                        break;
                    case ENUMSET:
                        // The returned value is a string array that we will turn into a string
                        // of elements separated by whitespace. We can then use LayoutOptionData
                        // to obtain a proper set
                        StringBuilder elementString = new StringBuilder();
                        for (String s : (String[]) value) {
                            elementString.append(" ").append(s);
                        }
                        value = optionData.parseValue(elementString.toString());
                        break;
                    default:
                        value = optionData.parseValue((String) value);
                    }
                    layoutConfig.setOptionValue(optionData, layoutContext, value);
                }
            };
            KimlUiUtil.runModelChange(modelChange, editingDomain, Messages.getString("kiml.ui.11"));
    
            // if the selected option can affect other options, refresh the whole layout view
            if (dependencyOptions.contains(id)) {
                LayoutViewPart layoutView = LayoutViewPart.findView();
                if (layoutView != null) {
                    layoutView.refresh();
                }
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getEditableValue() {
        // this feature is currently not required (see interface documentation)
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPropertySet(final Object id) {
        LayoutOptionData optionData = LayoutMetaDataService.getInstance().getOptionData((String) id);
        return layoutConfig.isSet(optionData, layoutContext);
    }

    /**
     * {@inheritDoc}
     */
    public void resetPropertyValue(final Object id) {
        final LayoutOptionData optionData = LayoutMetaDataService.getInstance().getOptionData(
                (String) id);
        if (optionData != null) {
            Runnable modelChange = new Runnable() {
                public void run() {
                    layoutConfig.setOptionValue(optionData, layoutContext, null);
                }
            };
            KimlUiUtil.runModelChange(modelChange, editingDomain, Messages.getString("kiml.ui.12"));
            
            // if the selected option can affect other options, refresh the whole layout view
            if (dependencyOptions.contains(id)) {
                LayoutViewPart layoutView = LayoutViewPart.findView();
                if (layoutView != null) {
                    layoutView.refresh();
                }
            }
        }
    }
    
    /**
     * Returns an identifier for a displayed layout hint name. The result is the identifier of
     * an algorithm whose name is a prefix of the displayed name. If there are multiple such
     * algorithms, the one with the longest prefix is taken. If there is no such algorithm,
     * the result is the identifier of a layout type whose name is a prefix of the displayed
     * name. If there are multiple such types, the one with the longest prefix is taken.
     * 
     * @param displayedName a displayed name of a layout provider or a layout type
     * @return the corresponding identifier, or {@code null} if no match is found
     */
    public static String getLayoutHint(final String displayedName) {
        // look for a matching layout provider
        String bestHint = null;
        int bestLength = 0;
        for (LayoutAlgorithmData layouterData : LayoutMetaDataService.getInstance().getAlgorithmData()) {
            String name = layouterData.getName();
            if (displayedName.startsWith(name) && name.length() > bestLength) {
                bestHint = layouterData.getId();
                bestLength = name.length();
            }
        }
        if (bestHint == null) {
            // look for a matching layout type
            for (LayoutTypeData layoutType : LayoutMetaDataService.getInstance().getTypeData()) {
                String typeId = layoutType.getId();
                String typeName = layoutType.getName();
                if (displayedName.startsWith(typeName) && typeName.length() > bestLength) {
                    bestHint = typeId;
                    bestLength = typeName.length();
                }
            }
        }
        return bestHint;
    }

}
