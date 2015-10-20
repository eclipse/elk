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
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.elk.graph.EMapPropertyHolder#getProperties <em>Properties</em>}</li>
 *   <li>{@link org.eclipse.elk.graph.EMapPropertyHolder#getPersistentEntries <em>Persistent Entries</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.elk.graph.KGraphPackage#getEMapPropertyHolder()
 * @model abstract="true" superTypes="org.eclipse.elk.graph.properties.IPropertyHolder"
 * @generated
 */
public interface EMapPropertyHolder extends EObject, IPropertyHolder {
    /**
     * Returns the value of the '<em><b>Properties</b></em>' map.
     * The key is of type {@link org.eclipse.elk.graph.properties.IProperty<?>},
     * and the value is of type {@link java.lang.Object},
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Properties</em>' map isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Properties</em>' map.
     * @see org.eclipse.elk.graph.KGraphPackage#getEMapPropertyHolder_Properties()
     * @model mapType="org.eclipse.elk.graph.IPropertyToObjectMap<org.eclipse.elk.graph.properties.IProperty<?>, org.eclipse.emf.ecore.EJavaObject>" transient="true"
     * @generated
     */
    EMap<IProperty<?>, Object> getProperties();

    /**
     * Returns the value of the '<em><b>Persistent Entries</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.elk.graph.PersistentEntry}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Persistent Entries</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Persistent Entries</em>' containment reference list.
     * @see org.eclipse.elk.graph.KGraphPackage#getEMapPropertyHolder_PersistentEntries()
     * @model containment="true"
     * @generated
     */
    EList<PersistentEntry> getPersistentEntries();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Serialize all entries of the properties map using {@link Object#toString()}
     * and write them into the list of persistent entries. The previous content is cleared.
     * <!-- end-model-doc -->
     * @model
     * @generated
     */
    void makePersistent();

} // EMapPropertyHolder
