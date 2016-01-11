/*******************************************************************************
 * Copyright (c) 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    spoenemann - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core;

import java.util.Map;

import org.eclipse.elk.core.klayoutdata.KEdgeLayout;
import org.eclipse.elk.core.klayoutdata.KLayoutData;
import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.util.IGraphElementVisitor;
import org.eclipse.elk.core.util.Pair;
import org.eclipse.elk.graph.KGraphElement;
import org.eclipse.elk.graph.KNode;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.IPropertyHolder;
import org.eclipse.elk.graph.properties.MapPropertyHolder;
import org.eclipse.elk.graph.properties.Property;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

/**
 * A layout configurator is a graph element visitor that applies layout option values. It can be
 * used to modify the layout configuration of a graph after it has been created, e.g. in order to
 * apply multiple layouts with different configurations. Create an instance and then use one of the
 * {@code configure(..)} methods to obtain a property holder that can be filled with values for
 * layout options.
 */
public class LayoutConfigurator implements IGraphElementVisitor {
    
    /**
     * Property attached to the shape layout of the top-level {@link KNode} holding a reference
     * to an additional layout configurator that shall be applied for the remaining iterations.
     * This is applicable only when multiple layout iterations are performed, e.g. for applying
     * different layout algorithms one after another. If only a single iteration is performed or
     * the current iteration is the last one, the value of this property is ignored. Otherwise
     * the referenced configurator will be added to all iterations that follow the current one.
     */
    public static final IProperty<LayoutConfigurator> ADD_LAYOUT_CONFIG =
            new Property<LayoutConfigurator>("org.eclipse.elk.addLayoutConfig");
    
    private final Map<KGraphElement, MapPropertyHolder> elementOptionMap = Maps.newHashMap();
    private final Map<Class<? extends KGraphElement>, MapPropertyHolder> classOptionMap = Maps.newHashMap();
    private boolean clearLayout = false;
    private Predicate<Pair<KGraphElement, IProperty<?>>> optionFilter;
    
    /**
     * Whether to clear the layout of each graph element before the new configuration is applied.
     */
    public boolean isClearLayout() {
        return clearLayout;
    }
    
    /**
     * Set whether to clear the layout of each graph element before the new configuration is applied
     * (the default is {@code false}).
     * 
     * @return {@code this}
     */
    public LayoutConfigurator setClearLayout(boolean doClearLayout) {
        this.clearLayout = doClearLayout;
        return this;
    }
    
    /**
     * Set a filter that is queried for each combination of graph elements and options. An option
     * value is applied only if the filter matches. If no filter is set, all values are applied.
     * 
     * @return {@code this}
     */
    public LayoutConfigurator setFilter(Predicate<Pair<KGraphElement, IProperty<?>>> filter) {
        this.optionFilter = filter;
        return this;
    }
    
    /**
     * Add and return a property holder for the given element. If such a property holder is
     * already present, the previous instance is returned.
     */
    public IPropertyHolder configure(KGraphElement element) {
        MapPropertyHolder result = elementOptionMap.get(element);
        if (result == null) {
            result = new MapPropertyHolder();
            elementOptionMap.put(element, result);
        }
        return result;
    }
    
    /**
     * Return the stored property holder for the given element, or {@code null} if none is present.
     */
    public IPropertyHolder getProperties(KGraphElement element) {
        return elementOptionMap.get(element);
    }
    
    /**
     * Add and return a property holder for the given element class. If such a property holder is
     * already present, the previous instance is returned.
     */
    public IPropertyHolder configure(Class<? extends KGraphElement> elementClass) {
        MapPropertyHolder result = classOptionMap.get(elementClass);
        if (result == null) {
            result = new MapPropertyHolder();
            classOptionMap.put(elementClass, result);
        }
        return result;
    }
    
    /**
     * Return the stored property holder for the given element class, or {@code null} if none is present.
     */
    public IPropertyHolder getProperties(Class<? extends KGraphElement> elementClass) {
        return classOptionMap.get(elementClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void visit(final KGraphElement element) {
        KLayoutData layout = element instanceof KEdgeLayout
                ? element.getData(KEdgeLayout.class) : element.getData(KShapeLayout.class);
        if (clearLayout) {
            layout.getProperties().clear();
        }
        MapPropertyHolder classProperties = classOptionMap.get(element.getClass());
        MapPropertyHolder elementProperties = elementOptionMap.get(element);
        if (optionFilter != null) {
            if (classProperties != null) {
                for (Map.Entry<IProperty<?>, Object> entry : classProperties.getAllProperties().entrySet()) {
                    if (optionFilter.apply(Pair.of(element, entry.getKey()))) {
                        layout.setProperty((IProperty<Object>) entry.getKey(), entry.getValue());
                    }
                }
            }
            if (elementProperties != null) {
                for (Map.Entry<IProperty<?>, Object> entry : elementProperties.getAllProperties().entrySet()) {
                    if (optionFilter.apply(Pair.of(element, entry.getKey()))) {
                        layout.setProperty((IProperty<Object>) entry.getKey(), entry.getValue());
                    }
                }
            }
        } else {
            if (classProperties != null) {
                layout.copyProperties(classProperties);
            }
            if (elementProperties != null) {
                layout.copyProperties(elementProperties);
            }
        }
    }
    
    /**
     * Copy all options from the given configurator to this one, possibly overriding the own options.
     * 
     * @return {@code this}
     */
    public LayoutConfigurator overrideWith(LayoutConfigurator other) {
        for (Map.Entry<KGraphElement, MapPropertyHolder> entry : other.elementOptionMap.entrySet()) {
            MapPropertyHolder thisHolder = this.elementOptionMap.get(entry.getKey());
            if (thisHolder == null) {
                thisHolder = new MapPropertyHolder();
                this.elementOptionMap.put(entry.getKey(), thisHolder);
            }
            thisHolder.copyProperties(entry.getValue());
        }
        for (Map.Entry<Class<? extends KGraphElement>, MapPropertyHolder> entry : other.classOptionMap.entrySet()) {
            MapPropertyHolder thisHolder = this.classOptionMap.get(entry.getKey());
            if (thisHolder == null) {
                thisHolder = new MapPropertyHolder();
                this.classOptionMap.put(entry.getKey(), thisHolder);
            }
            thisHolder.copyProperties(entry.getValue());
        }
        this.clearLayout = other.clearLayout;
        this.optionFilter = other.optionFilter;
        return this;
    }

}
