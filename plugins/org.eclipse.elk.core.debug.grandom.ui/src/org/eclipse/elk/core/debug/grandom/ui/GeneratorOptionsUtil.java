/*******************************************************************************
 * Copyright (c) 2016, 2018 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.core.debug.grandom.ui;

import java.lang.reflect.Field;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.elk.core.debug.grandom.generators.GeneratorOptions;
import org.eclipse.elk.core.debug.grandom.generators.GeneratorOptions.RandVal;
import org.eclipse.elk.core.debug.grandom.ui.internal.GrandomActivator;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.MapPropertyHolder;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * Utility methods extracted from {@link GeneratorOptions} class that have ui dependencies.
 *  
 * @author uru
 */
public final class GeneratorOptionsUtil {
    
    private GeneratorOptionsUtil() { }
    
    //~~~~~~~~~~~~~~~~  Utility methods for preference handling

    /**
     * Save all options that are stored in this property holder in the plugin preferences.
     * 
     * @param propertyHolder
     *            the property holder whose preferences to save.
     */
    public static void savePreferences(final MapPropertyHolder propertyHolder) {
        getPreferenceStore();
        for (Map.Entry<IProperty<?>, Object> entry : propertyHolder.getAllProperties().entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Boolean) {
                preferenceStore.setValue(entry.getKey().getId(), (Boolean) value);
            } else if (value instanceof Integer) {
                preferenceStore.setValue(entry.getKey().getId(), (Integer) value);
            } else if (value instanceof Float) {
                preferenceStore.setValue(entry.getKey().getId(), (Float) value);
            } else if (value instanceof Double) {
                preferenceStore.setValue(entry.getKey().getId(), (Double) value);
            } else {
                preferenceStore.setValue(entry.getKey().getId(), value.toString());
            }
        }
    }

   
    /**
     * Load preferences for all options that are defined as fields of type {@link IProperty} in
     * the {@link GeneratorOptions} class. The property types are derived from their default values.
     * @param propertyHolder
     *            the property holder into which the preferences should be loaded.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void loadPreferences(final MapPropertyHolder propertyHolder) {
        getPreferenceStore();
        try {
            for (Field field : GeneratorOptions.class.getFields()) {
                if (IProperty.class.isAssignableFrom(field.getType())) {
                    IProperty<Object> option = (IProperty<Object>) field.get(propertyHolder);
                    Object value;
                    if (option.getDefault() instanceof Boolean) {
                        value = preferenceStore.getBoolean(option.getId());
                    } else if (option.getDefault() instanceof Integer) {
                        value = preferenceStore.getInt(option.getId());
                    } else if (option.getDefault() instanceof Float) {
                        value = preferenceStore.getFloat(option.getId());
                    } else if (option.getDefault() instanceof Double) {
                        value = preferenceStore.getDouble(option.getId());
                    } else if (option.getDefault() instanceof Enum) {
                        String serializedValue = preferenceStore.getString(option.getId());
                        try {
                            value = Enum.valueOf((Class<? extends Enum>) option.getDefault().getClass(),
                                    serializedValue);
                        } catch (IllegalArgumentException exception) {
                            value = null;
                        }
                    } else if (option.getDefault() instanceof RandVal) {
                        value = option.getDefault();
                    } else {
                        value = preferenceStore.getString(option.getId());
                    }
                    propertyHolder.setProperty(option, value);
                    // check lower and upper bounds of the generator option
                    // FIXME elkMigrate
                    // reactivate once checking is implemented again
                    // checkProperties(option);
                }
            }
        } catch (IllegalAccessException exception) {
            IStatus status = new Status(IStatus.ERROR, "org.eclipse.elk.core.debug.grandom.ui",
                    "Error while loading preferences for graph generation options.");
            StatusManager.getManager().handle(status);
        }
    }

    /**
     * @return the {@link IPreferenceStore} given by the {@link TextActivator}.
     */
    public static IPreferenceStore getPreferenceStore() {
        GrandomActivator instance = GrandomActivator.getInstance();
        if (instance != null) {
            preferenceStore = instance.getPreferenceStore();
        }
        return preferenceStore;
    }
    
    private static IPreferenceStore preferenceStore;
}
