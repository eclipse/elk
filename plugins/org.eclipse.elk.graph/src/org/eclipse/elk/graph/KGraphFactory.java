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

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.elk.graph.KGraphPackage
 * @generated
 */
public interface KGraphFactory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    KGraphFactory eINSTANCE = org.eclipse.elk.graph.impl.KGraphFactoryImpl.init();

    /**
     * Returns a new object of class '<em>Data</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Data</em>'.
     * @generated
     */
    KGraphData createKGraphData();

    /**
     * Returns a new object of class '<em>KNode</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>KNode</em>'.
     * @generated
     */
    KNode createKNode();

    /**
     * Returns a new object of class '<em>KEdge</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>KEdge</em>'.
     * @generated
     */
    KEdge createKEdge();

    /**
     * Returns a new object of class '<em>KPort</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>KPort</em>'.
     * @generated
     */
    KPort createKPort();

    /**
     * Returns a new object of class '<em>KLabel</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>KLabel</em>'.
     * @generated
     */
    KLabel createKLabel();

    /**
     * Returns a new object of class '<em>Persistent Entry</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Persistent Entry</em>'.
     * @generated
     */
    PersistentEntry createPersistentEntry();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    KGraphPackage getKGraphPackage();

} //KGraphFactory
