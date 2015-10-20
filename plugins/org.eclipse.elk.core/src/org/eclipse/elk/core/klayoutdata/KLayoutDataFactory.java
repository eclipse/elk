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
package org.eclipse.elk.core.klayoutdata;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.elk.core.klayoutdata.KLayoutDataPackage
 * @generated
 */
public interface KLayoutDataFactory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    KLayoutDataFactory eINSTANCE = org.eclipse.elk.core.klayoutdata.impl.KLayoutDataFactoryImpl.init();

    /**
     * Returns a new object of class '<em>KShape Layout</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>KShape Layout</em>'.
     * @generated
     */
    KShapeLayout createKShapeLayout();

    /**
     * Returns a new object of class '<em>KEdge Layout</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>KEdge Layout</em>'.
     * @generated
     */
    KEdgeLayout createKEdgeLayout();

    /**
     * Returns a new object of class '<em>KPoint</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>KPoint</em>'.
     * @generated
     */
    KPoint createKPoint();

    /**
     * Returns a new object of class '<em>KInsets</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>KInsets</em>'.
     * @generated
     */
    KInsets createKInsets();

    /**
     * Returns a new object of class '<em>KIdentifier</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>KIdentifier</em>'.
     * @generated
     */
    KIdentifier createKIdentifier();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    KLayoutDataPackage getKLayoutDataPackage();

} //KLayoutDataFactory
