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
package org.eclipse.elk.core.klayoutdata.impl;

import org.eclipse.elk.core.klayoutdata.KInsets;
import org.eclipse.elk.core.klayoutdata.KLayoutDataPackage;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>KInsets</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.elk.core.klayoutdata.impl.KInsetsImpl#getTop <em>Top</em>}</li>
 *   <li>{@link org.eclipse.elk.core.klayoutdata.impl.KInsetsImpl#getBottom <em>Bottom</em>}</li>
 *   <li>{@link org.eclipse.elk.core.klayoutdata.impl.KInsetsImpl#getLeft <em>Left</em>}</li>
 *   <li>{@link org.eclipse.elk.core.klayoutdata.impl.KInsetsImpl#getRight <em>Right</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class KInsetsImpl extends EObjectImpl implements KInsets {
    /**
     * The default value of the '{@link #getTop() <em>Top</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTop()
     * @generated
     * @ordered
     */
    protected static final float TOP_EDEFAULT = 0.0F;

    /**
     * The cached value of the '{@link #getTop() <em>Top</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTop()
     * @generated
     * @ordered
     */
    protected float top = TOP_EDEFAULT;

    /**
     * The default value of the '{@link #getBottom() <em>Bottom</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getBottom()
     * @generated
     * @ordered
     */
    protected static final float BOTTOM_EDEFAULT = 0.0F;

    /**
     * The cached value of the '{@link #getBottom() <em>Bottom</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getBottom()
     * @generated
     * @ordered
     */
    protected float bottom = BOTTOM_EDEFAULT;

    /**
     * The default value of the '{@link #getLeft() <em>Left</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLeft()
     * @generated
     * @ordered
     */
    protected static final float LEFT_EDEFAULT = 0.0F;

    /**
     * The cached value of the '{@link #getLeft() <em>Left</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLeft()
     * @generated
     * @ordered
     */
    protected float left = LEFT_EDEFAULT;

    /**
     * The default value of the '{@link #getRight() <em>Right</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRight()
     * @generated
     * @ordered
     */
    protected static final float RIGHT_EDEFAULT = 0.0F;

    /**
     * The cached value of the '{@link #getRight() <em>Right</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRight()
     * @generated
     * @ordered
     */
    protected float right = RIGHT_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected KInsetsImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return KLayoutDataPackage.Literals.KINSETS;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public float getTop() {
        return top;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTop(float newTop) {
        float oldTop = top;
        top = newTop;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, KLayoutDataPackage.KINSETS__TOP, oldTop, top));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public float getBottom() {
        return bottom;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setBottom(float newBottom) {
        float oldBottom = bottom;
        bottom = newBottom;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, KLayoutDataPackage.KINSETS__BOTTOM, oldBottom, bottom));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public float getLeft() {
        return left;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setLeft(float newLeft) {
        float oldLeft = left;
        left = newLeft;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, KLayoutDataPackage.KINSETS__LEFT, oldLeft, left));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public float getRight() {
        return right;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setRight(float newRight) {
        float oldRight = right;
        right = newRight;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, KLayoutDataPackage.KINSETS__RIGHT, oldRight, right));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case KLayoutDataPackage.KINSETS__TOP:
                return getTop();
            case KLayoutDataPackage.KINSETS__BOTTOM:
                return getBottom();
            case KLayoutDataPackage.KINSETS__LEFT:
                return getLeft();
            case KLayoutDataPackage.KINSETS__RIGHT:
                return getRight();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case KLayoutDataPackage.KINSETS__TOP:
                setTop((Float)newValue);
                return;
            case KLayoutDataPackage.KINSETS__BOTTOM:
                setBottom((Float)newValue);
                return;
            case KLayoutDataPackage.KINSETS__LEFT:
                setLeft((Float)newValue);
                return;
            case KLayoutDataPackage.KINSETS__RIGHT:
                setRight((Float)newValue);
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
            case KLayoutDataPackage.KINSETS__TOP:
                setTop(TOP_EDEFAULT);
                return;
            case KLayoutDataPackage.KINSETS__BOTTOM:
                setBottom(BOTTOM_EDEFAULT);
                return;
            case KLayoutDataPackage.KINSETS__LEFT:
                setLeft(LEFT_EDEFAULT);
                return;
            case KLayoutDataPackage.KINSETS__RIGHT:
                setRight(RIGHT_EDEFAULT);
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
            case KLayoutDataPackage.KINSETS__TOP:
                return top != TOP_EDEFAULT;
            case KLayoutDataPackage.KINSETS__BOTTOM:
                return bottom != BOTTOM_EDEFAULT;
            case KLayoutDataPackage.KINSETS__LEFT:
                return left != LEFT_EDEFAULT;
            case KLayoutDataPackage.KINSETS__RIGHT:
                return right != RIGHT_EDEFAULT;
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (top: ");
        result.append(top);
        result.append(", bottom: ");
        result.append(bottom);
        result.append(", left: ");
        result.append(left);
        result.append(", right: ");
        result.append(right);
        result.append(')');
        return result.toString();
    }

} //KInsetsImpl
