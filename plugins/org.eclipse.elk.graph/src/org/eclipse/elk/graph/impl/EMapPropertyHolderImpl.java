/**
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Kiel University - initial API and implementation
 */
package org.eclipse.elk.graph.impl;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.elk.graph.EMapPropertyHolder;
import org.eclipse.elk.graph.ElkGraphFactory;
import org.eclipse.elk.graph.ElkGraphPackage;
import org.eclipse.elk.graph.ElkPersistentEntry;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.IPropertyHolder;
import org.eclipse.elk.graph.properties.IPropertyValueProxy;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EMap Property Holder</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.elk.graph.impl.EMapPropertyHolderImpl#getProperties <em>Properties</em>}</li>
 *   <li>{@link org.eclipse.elk.graph.impl.EMapPropertyHolderImpl#getPersistentEntries <em>Persistent Entries</em>}</li>
 * </ul>
 *
 * @generated
 */
public class EMapPropertyHolderImpl extends MinimalEObjectImpl.Container implements EMapPropertyHolder {
    /**
     * The cached value of the '{@link #getProperties() <em>Properties</em>}' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProperties()
     * @generated
     * @ordered
     */
    protected EMap<IProperty<?>, Object> properties;

    /**
     * The cached value of the '{@link #getPersistentEntries() <em>Persistent Entries</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPersistentEntries()
     * @generated
     * @ordered
     */
    protected EList<ElkPersistentEntry> persistentEntries;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EMapPropertyHolderImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ElkGraphPackage.Literals.EMAP_PROPERTY_HOLDER;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMap<IProperty<?>, Object> getProperties() {
        if (properties == null) {
            properties = new EcoreEMap<IProperty<?>,Object>(ElkGraphPackage.Literals.ELK_PROPERTY_TO_VALUE_MAP_ENTRY, ElkPropertyToValueMapEntryImpl.class, this, ElkGraphPackage.EMAP_PROPERTY_HOLDER__PROPERTIES);
        }
        return properties;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<ElkPersistentEntry> getPersistentEntries() {
        if (persistentEntries == null) {
            persistentEntries = new EObjectResolvingEList<ElkPersistentEntry>(ElkPersistentEntry.class, this, ElkGraphPackage.EMAP_PROPERTY_HOLDER__PERSISTENT_ENTRIES);
        }
        return persistentEntries;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public void makePersistent() {
        // chsch: deactivated the delivering of notifications as that feature is not used in ELK
        //  so far and disturbs while working with KLighD
        boolean deliver = this.eDeliver();
        this.eSetDeliver(false);
        
        int i = 0;
        List<ElkPersistentEntry> persisEntries = getPersistentEntries();
        
        for (Entry<IProperty<?>, Object> entry : getProperties()) {
            IProperty<?> key = entry.getKey();
            Object value = entry.getValue();
            if (key != null && value != null) {
                ElkPersistentEntry persisEntry;
                if (i >= persisEntries.size()) {
                    persisEntry = ElkGraphFactory.eINSTANCE.createElkPersistentEntry();
                    persisEntries.add(persisEntry);
                    i++;
                } else {
                    persisEntry = persisEntries.get(i++);
                }
                
                boolean pEdeliver = persisEntry.eDeliver();
                persisEntry.eSetDeliver(false);
                persisEntry.setKey(key.getId());
                persisEntry.setValue(value.toString());
                persisEntry.eSetDeliver(pEdeliver);
            }
        }
        
        // remove any superfluous persistent entries that are left from previous 
        // 'persist actions'
        ListIterator<ElkPersistentEntry> peIt = persisEntries.listIterator(i);
        while (peIt.hasNext()) {
            peIt.next();
            peIt.remove();
        }
        
        this.eSetDeliver(deliver);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public <T> IPropertyHolder setProperty(IProperty<? super T> property, T value) {
        if (value == null) {
            getProperties().removeKey(property);
        } else {
            getProperties().put(property, value);
        }
        
        return this;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    @SuppressWarnings("unchecked")
    public <T> T getProperty(IProperty<T> property) {
        Object value = getProperties().get(property);
        if (value instanceof IPropertyValueProxy) {
            value = ((IPropertyValueProxy) value).resolveValue(property);
            if (value != null) {
                getProperties().put(property, value);
                return (T) value;
            }
        } else if (value != null) {
            return (T) value;
        }
        
        T defaultValue = property.getDefault();
        if (defaultValue instanceof Cloneable) {
            setProperty(property, defaultValue);
        }
        
        return defaultValue;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public IPropertyHolder copyProperties(IPropertyHolder source) {
        if (source == null) {
            return this;
        }

        if (source instanceof EMapPropertyHolder) {
            EMapPropertyHolder other = (EMapPropertyHolder) source;
            EMap<IProperty<?>, Object> ourProps = this.getProperties();
            for (Map.Entry<IProperty<?>, Object> entry : other.getProperties()) {
                Object value = entry.getValue();
                if (value instanceof IPropertyValueProxy) {
                    IPropertyValueProxy proxy = (IPropertyValueProxy) value;
                    Object newValue = proxy.resolveValue(entry.getKey());
                    if (newValue != null) {
                        entry.setValue(newValue);
                        value = newValue;
                    }
                }
                ourProps.put(entry.getKey(), value);
            }
        } else {
            this.getProperties().putAll(source.getAllProperties());
        }

        return this;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public Map<IProperty<?>, Object> getAllProperties() {
        EMap<IProperty<?>, Object> props = getProperties();
        
        // check for unresolved properties
        for (Map.Entry<IProperty<?>, Object> entry : props) {
            if (entry.getValue() instanceof IPropertyValueProxy) {
                IPropertyValueProxy proxy = (IPropertyValueProxy) entry.getValue();
                
                // Try to resolve the proxy's value, maybe the layout option was 
                // registered by now. If not, we preserve the proxy. 
                Object value = proxy.resolveValue(entry.getKey());
                if (value != null) {
                    entry.setValue(value);
                }
            }
        }
        
        return props.map();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case ElkGraphPackage.EMAP_PROPERTY_HOLDER__PROPERTIES:
                return ((InternalEList<?>)getProperties()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case ElkGraphPackage.EMAP_PROPERTY_HOLDER__PROPERTIES:
                if (coreType) return getProperties();
                else return getProperties().map();
            case ElkGraphPackage.EMAP_PROPERTY_HOLDER__PERSISTENT_ENTRIES:
                return getPersistentEntries();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case ElkGraphPackage.EMAP_PROPERTY_HOLDER__PROPERTIES:
                ((EStructuralFeature.Setting)getProperties()).set(newValue);
                return;
            case ElkGraphPackage.EMAP_PROPERTY_HOLDER__PERSISTENT_ENTRIES:
                getPersistentEntries().clear();
                getPersistentEntries().addAll((Collection<? extends ElkPersistentEntry>)newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case ElkGraphPackage.EMAP_PROPERTY_HOLDER__PROPERTIES:
                getProperties().clear();
                return;
            case ElkGraphPackage.EMAP_PROPERTY_HOLDER__PERSISTENT_ENTRIES:
                getPersistentEntries().clear();
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case ElkGraphPackage.EMAP_PROPERTY_HOLDER__PROPERTIES:
                return properties != null && !properties.isEmpty();
            case ElkGraphPackage.EMAP_PROPERTY_HOLDER__PERSISTENT_ENTRIES:
                return persistentEntries != null && !persistentEntries.isEmpty();
        }
        return super.eIsSet(featureID);
    }

} //EMapPropertyHolderImpl
