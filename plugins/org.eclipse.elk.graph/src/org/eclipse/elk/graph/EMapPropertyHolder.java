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
package org.eclipse.elk.graph;

import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.IPropertyHolder;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EMap Property Holder</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * A property holder implementation based on {@link org.eclipse.emf.common.util.EMap} which can be used in Ecore models.
 * 
 * <p>This property holder implementation currently has two ways for saving properties: a map of properties as well as a map of <em>persistent entries</em>. Persistent entries are String-String pairs containing String representations of properties. When a graph is serialized, it is the persistent entries that get serialized, not the properties themselves. This has two implications. First, to save a graph, one has to call {@link #makePersistent()} first. Second, after loading a graph, one of the methods in {@link org.eclipse.elk.core.util.GraphDataUtil} needs to be called to turn persistent entries back into usable properties.</p>
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.elk.graph.EMapPropertyHolder#getProperties <em>Properties</em>}</li>
 *   <li>{@link org.eclipse.elk.graph.EMapPropertyHolder#getPersistentEntries <em>Persistent Entries</em>}</li>
 * </ul>
 *
 * @see org.eclipse.elk.graph.ElkGraphPackage#getEMapPropertyHolder()
 * @model superTypes="org.eclipse.elk.graph.IPropertyHolder"
 * @generated
 */
public interface EMapPropertyHolder extends EObject, IPropertyHolder {
    /**
     * Returns the value of the '<em><b>Properties</b></em>' map.
     * The key is of type {@link org.eclipse.elk.graph.properties.IProperty<?>},
     * and the value is of type {@link java.lang.Object},
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Map of properties configured for this property holder.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Properties</em>' map.
     * @see org.eclipse.elk.graph.ElkGraphPackage#getEMapPropertyHolder_Properties()
     * @model mapType="org.eclipse.elk.graph.ElkPropertyToValueMapEntry<org.eclipse.elk.graph.IProperty<?>, org.eclipse.elk.graph.PropertyValue>"
     * @generated
     */
    EMap<IProperty<?>, Object> getProperties();

    /**
     * Returns the value of the '<em><b>Persistent Entries</b></em>' reference list.
     * The list contents are of type {@link org.eclipse.elk.graph.ElkPersistentEntry}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Map of persistent entries of this property holder. See class documentation for more details.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Persistent Entries</em>' reference list.
     * @see org.eclipse.elk.graph.ElkGraphPackage#getEMapPropertyHolder_PersistentEntries()
     * @model
     * @generated
     */
    EList<ElkPersistentEntry> getPersistentEntries();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Serialize all entries of the properties map using {@link Object#toString()} and write them into the list of persistent entries. The previous content is cleared. Call this method before serializing an ElkGraph.
     * 
     * <p><em>Note:</em>
     * This method may be replaced by an alternative mechanism that does not require calling any methods in order to serialize the graph.
     * <!-- end-model-doc -->
     * @model
     * @generated
     */
    void makePersistent();

} // EMapPropertyHolder
