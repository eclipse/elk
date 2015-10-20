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

import org.eclipse.elk.core.math.KVector;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Point</b></em>'. A point has
 * an x and a y coordinate.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.elk.core.klayoutdata.KPoint#getX <em>X</em>}</li>
 *   <li>{@link org.eclipse.elk.core.klayoutdata.KPoint#getY <em>Y</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.elk.core.klayoutdata.KLayoutDataPackage#getKPoint()
 * @model
 * @generated
 */
public interface KPoint extends EObject {
    /**
     * Returns the value of the '<em><b>X</b></em>' attribute.
     * The default value is <code>"0.0f"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>X</em>' attribute.
     * @see #setX(float)
     * @see org.eclipse.elk.core.klayoutdata.KLayoutDataPackage#getKPoint_X()
     * @model default="0.0f"
     * @generated
     */
    float getX();

    /**
     * Sets the value of the '{@link org.eclipse.elk.core.klayoutdata.KPoint#getX <em>X</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>X</em>' attribute.
     * @see #getX()
     * @generated
     */
    void setX(float value);

    /**
     * Returns the value of the '<em><b>Y</b></em>' attribute.
     * The default value is <code>"0.0f"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Y</em>' attribute.
     * @see #setY(float)
     * @see org.eclipse.elk.core.klayoutdata.KLayoutDataPackage#getKPoint_Y()
     * @model default="0.0f"
     * @generated
     */
    float getY();

    /**
     * Sets the value of the '{@link org.eclipse.elk.core.klayoutdata.KPoint#getY <em>Y</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Y</em>' attribute.
     * @see #getY()
     * @generated
     */
    void setY(float value);

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Set a new position for this point.
     * @param x the new x coordinate value
     * @param y the new y coordinate value
     * <!-- end-model-doc -->
     * @model
     * @generated
     */
    void setPos(float x, float y);

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Set the position of this point using a vector.
     * @param pos the vector for the new position
     * <!-- end-model-doc -->
     * @model posType="org.eclipse.elk.core.math.KVector"
     * @generated
     */
    void applyVector(KVector pos);

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Create a vector from this point.
     * @return a vector with the position of this point
     * <!-- end-model-doc -->
     * @model type="org.eclipse.elk.core.math.KVector"
     * @generated
     */
    KVector createVector();

} // KPoint
