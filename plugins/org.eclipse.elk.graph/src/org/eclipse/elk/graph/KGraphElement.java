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

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Graph Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * This is the superclass of all elements of a graph such as nodes, edges, ports,
 * and labels. A graph element may contain an arbitrary number of additional
 * data instances.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.elk.graph.KGraphElement#getData <em>Data</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.elk.graph.KGraphPackage#getKGraphElement()
 * @model abstract="true"
 * @generated
 */
public interface KGraphElement extends EObject {
    /**
     * Returns the value of the '<em><b>Data</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.elk.graph.KGraphData}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Each element of this list may contain additional data for the model element.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Data</em>' containment reference list.
     * @see org.eclipse.elk.graph.KGraphPackage#getKGraphElement_Data()
     * @model containment="true"
     * @generated
     */
    EList<KGraphData> getData();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Returns the first data instance that matches the given class. Classes
     * can be obtained using the static package methods of the corresponding
     * EMF model.
     * @return graph data for the given type, or {@code null} if there is none
     * <!-- end-model-doc -->
     * @model
     * @generated
     */
    KGraphData getData(EClass type);

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Returns the first data instance that matches the given class.
     * @param type the class of graph data to retrieve
     * @return graph data for the given type, or {@code null} if there is none
     * <!-- end-model-doc -->
     * @model
     * @generated
     */
    <T extends KGraphData> T getData(Class<T> type);

} // KGraphElement
