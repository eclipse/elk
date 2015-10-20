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
package org.eclipse.elk.core.config;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.elk.core.LayoutMetaDataService;
import org.eclipse.elk.core.LayoutOptionData;
import org.eclipse.elk.core.klayoutdata.KLayoutData;
import org.eclipse.elk.graph.KGraphElement;
import org.eclipse.elk.graph.KGraphPackage;
import org.eclipse.elk.graph.KNode;
import org.eclipse.elk.graph.properties.IProperty;

import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;

/**
 * A layout configurator that can be used to generate on-the-fly layout options.
 * Use {@link #setValue(IProperty, Object, IProperty, Object)} to set a layout option value for a
 * particular context. Use {@link #setValue(IProperty, Object)} to set a layout option value
 * globally for all graph elements to which the option can be applied. All configured values are
 * held in a hash map that is queried when the configurator is applied to the layout graph.
 * The configuration is non-persistent (hence the name <em>volatile</em>).
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating yellow 2013-07-01 review KI-38 by cds, uru
 */
public class VolatileLayoutConfig extends AbstractMutableLayoutConfig {
    
    /** the default priority for volatile layout configurators. */
    public static final int DEFAULT_PRIORITY = 100;
    
    /**
     * Create a volatile layout configurator from the properties attached to the given graph.
     * 
     * @param graph a graph
     * @param priority the priority
     * @return a layout configurator that reflects the current properties of the graph
     */
    @SuppressWarnings("unchecked")
    public static VolatileLayoutConfig fromProperties(final KNode graph, final int priority) {
        VolatileLayoutConfig config = new VolatileLayoutConfig(priority);
        Iterator<KGraphElement> elementIter = Iterators.filter(graph.eAllContents(),
                KGraphElement.class);
        while (elementIter.hasNext()) {
            KGraphElement element = elementIter.next();
            KLayoutData layoutData = element.getData(KLayoutData.class);
            if (layoutData != null) {
                for (Map.Entry<IProperty<?>, Object> entry : layoutData.getProperties()) {
                    if (entry.getKey() != null && entry.getValue() != null) {
                        config.setValue((IProperty<Object>) entry.getKey(), element,
                                LayoutContext.GRAPH_ELEM, entry.getValue());
                    }
                }
            }
        }
        return config;
    }

    /** map of focus objects and property identifiers to their values. */
    private final Map<Object, Map<IProperty<Object>, Object>> focusOptionMap = Maps.newHashMap();
    /** the layout context keys managed by this configurator. */
    private final Set<IProperty<?>> contextKeys = new HashSet<IProperty<?>>();
    
    /** the map of layout options set for this configurator. */
    private final Map<LayoutOptionData, Object> globalOptionMap = Maps.newHashMap();
    
    /** the priority of this configurator. */
    private int priority;
    
    /**
     * Creates a volatile layout configurator with default priority.
     */
    public VolatileLayoutConfig() {
        priority = DEFAULT_PRIORITY;
    }
    
    /**
     * Creates a volatile layout configurator with given priority.
     * 
     * @param prio the priority to apply for this configurator
     */
    public VolatileLayoutConfig(final int prio) {
        this.priority = prio;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "VolatileLayoutConfig:" + focusOptionMap.toString() + globalOptionMap.toString();
    }

    /**
     * {@inheritDoc}
     */
    public int getPriority() {
        return priority;
    }
        
    /**
     * Copy all values from the given layout configurator into this one.
     * 
     * @param other another volatile layout configurator
     */
    public void copyValues(final VolatileLayoutConfig other) {
        this.contextKeys.addAll(other.contextKeys);
        this.focusOptionMap.putAll(other.focusOptionMap);
        this.globalOptionMap.putAll(other.globalOptionMap);
    }
    
    /**
     * Returns the stored global value for the given layout option, if any.
     * 
     * @param option a layout option
     * @return the global value for the option
     */
    public Object getGlobalValue(final IProperty<?> option) {
        return globalOptionMap.get(option);
    }
    
    /**
     * Returns an unmodifiable view on the globally configured values.
     * 
     * @return a map of global values
     */
    public Map<LayoutOptionData, Object> getGlobalValues() {
        return Collections.unmodifiableMap(globalOptionMap);
    }

    /**
     * {@inheritDoc}
     */
    public Object getOptionValue(final LayoutOptionData optionData, final LayoutContext context) {
        for (IProperty<?> contextKey : contextKeys) {
            // retrieve the object stored under this key from the context
            Object object = context.getProperty(contextKey);
            // retrieve the map of options that have been set for that object
            Map<IProperty<Object>, Object> contextOptions = focusOptionMap.get(object);
            if (contextOptions != null) {
                // get the value set for the given layout option, if any
                Object value = contextOptions.get(optionData);
                if (value != null) {
                    return value;
                }
            }
        }
        
        // retrieve the value stored globally for the given option
        Object value = globalOptionMap.get(optionData);
        if (value != null) {
            KGraphElement graphElem = context.getProperty(LayoutContext.GRAPH_ELEM);
            boolean isGlobal = context.getProperty(LayoutContext.GLOBAL);
            if (isGlobal || matchesTargetType(optionData, graphElem)) {
                return value;
            }
        }
        return null;
    }
    
    /**
     * Set a new value for a layout option in the context of the given object.
     * 
     * @param option a layout option
     * @param contextObj the object to which the option value is attached,
     *          e.g. a domain model element
     * @param contextKey the layout context key related to the context object,
     *          e.g. {@link LayoutContext#DOMAIN_MODEL}
     * @param value the new layout option value
     * @return the instance on which the method was called, for chaining multiple method calls
     * @param <T> the type of the layout option
     * @param <C> the type of the layout context key
     */
    @SuppressWarnings("unchecked")
    public <T, C> VolatileLayoutConfig setValue(final IProperty<? super T> option, final C contextObj,
            final IProperty<? super C> contextKey, final T value) {
        contextKeys.add(contextKey);
        
        Map<IProperty<Object>, Object> contextOptions = focusOptionMap.get(contextObj);
        if (contextOptions == null) {
            contextOptions = new HashMap<IProperty<Object>, Object>();
            focusOptionMap.put(contextObj, contextOptions);
        }
        if (value == null) {
            contextOptions.remove(option);
        } else {
            contextOptions.put((IProperty<Object>) option, value);
        }
        return this;
    }
    
    /**
     * Set the given layout option value globally. This value has lower priority than values
     * set for a specific context via {@link #setValue(IProperty, Object, IProperty, Object)}.
     * 
     * @param option a layout option
     * @param value the new global value for the option
     * @return the instance on which the method was called, for chaining multiple method calls
     * @param <T> the type of the layout option
     */
    public <T> VolatileLayoutConfig setValue(final IProperty<? super T> option, final T value) {
        if (option instanceof LayoutOptionData) {
            globalOptionMap.put((LayoutOptionData) option, value);
        } else {
            LayoutOptionData optionData = LayoutMetaDataService.getInstance().getOptionData(
                    option.getId());
            if (optionData != null) {
                globalOptionMap.put(optionData, value);
            } else {
                throw new IllegalArgumentException(
                        "The given property is not registered as a layout option");
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<IProperty<?>> getAffectedOptions(final LayoutContext context) {
        List<IProperty<?>> options = new LinkedList<IProperty<?>>();
        
        // get globally defined options
        KGraphElement graphElem = context.getProperty(LayoutContext.GRAPH_ELEM);
        boolean isGlobal = context.getProperty(LayoutContext.GLOBAL);
        for (LayoutOptionData option : globalOptionMap.keySet()) {
            if (isGlobal || matchesTargetType(option, graphElem)) {
                options.add(option);
            }
        }
        
        // get options for the focus object
        for (IProperty<?> contextKey : contextKeys) {
            Object object = context.getProperty(contextKey);
            Map<IProperty<Object>, Object> contextOptions = focusOptionMap.get(object);
            if (contextOptions != null) {
                options.addAll(contextOptions.keySet());
            }
        }
        return options;
    }
    
    /**
     * Check whether the given diagram part matches the target type of the layout option.
     * 
     * @param optionData a layout option data instance
     * @param graphElem a graph element
     * @return true if the layout option can be applied to the graph element
     */
    private boolean matchesTargetType(final LayoutOptionData optionData,
            final KGraphElement graphElem) {
        if (graphElem == null) {
            return false;
        }
        Set<LayoutOptionData.Target> optionTargets = optionData.getTargets();
        switch (graphElem.eClass().getClassifierID()) {
        case KGraphPackage.KNODE:
            if (optionTargets.contains(LayoutOptionData.Target.NODES)
                    || !((KNode) graphElem).getChildren().isEmpty()
                    && optionTargets.contains(LayoutOptionData.Target.PARENTS)) {
                return true;
            }
            break;
        case KGraphPackage.KEDGE:
            if (optionTargets.contains(LayoutOptionData.Target.EDGES)) {
                return true;
            }
            break;
        case KGraphPackage.KPORT:
            if (optionTargets.contains(LayoutOptionData.Target.PORTS)) {
                return true;
            }
            break;
        case KGraphPackage.KLABEL:
            if (optionTargets.contains(LayoutOptionData.Target.LABELS)) {
                return true;
            }
            break;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void setOptionValue(final LayoutOptionData optionData, final LayoutContext context,
            final Object value) {
        KGraphElement graphElem = context.getProperty(LayoutContext.GRAPH_ELEM);
        if (context.getProperty(LayoutContext.GLOBAL)) {
            if (matchesTargetType(optionData, graphElem)) {
                globalOptionMap.put(optionData, value);
            }
        } else {
            Object diagramPart = context.getProperty(LayoutContext.DIAGRAM_PART);
            Object domainModel = context.getProperty(LayoutContext.DOMAIN_MODEL);
            if (diagramPart != null) {
                setValue(optionData, diagramPart,
                        LayoutContext.DIAGRAM_PART, value);
            } else if (domainModel != null) {
                setValue(optionData, domainModel,
                        LayoutContext.DOMAIN_MODEL, value);
            } else if (graphElem != null) {
                setValue(optionData, graphElem,
                        LayoutContext.GRAPH_ELEM, value);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearOptionValues(final LayoutContext context) {
        if (context.getProperty(LayoutContext.GLOBAL)) {
            globalOptionMap.clear();
        } else {
            for (IProperty<?> contextKey : contextKeys) {
                Object object = context.getProperty(contextKey);
                focusOptionMap.remove(object);
            }
        }
    }

}
