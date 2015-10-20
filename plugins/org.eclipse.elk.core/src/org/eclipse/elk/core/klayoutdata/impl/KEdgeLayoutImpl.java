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

import java.util.Collection;
import java.util.ListIterator;

import org.eclipse.elk.core.klayoutdata.KEdgeLayout;
import org.eclipse.elk.core.klayoutdata.KLayoutDataFactory;
import org.eclipse.elk.core.klayoutdata.KLayoutDataPackage;
import org.eclipse.elk.core.klayoutdata.KPoint;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.graph.impl.KGraphDataImpl;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>KEdge Layout</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.elk.core.klayoutdata.impl.KEdgeLayoutImpl#getBendPoints <em>Bend Points</em>}</li>
 *   <li>{@link org.eclipse.elk.core.klayoutdata.impl.KEdgeLayoutImpl#getSourcePoint <em>Source Point</em>}</li>
 *   <li>{@link org.eclipse.elk.core.klayoutdata.impl.KEdgeLayoutImpl#getTargetPoint <em>Target Point</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class KEdgeLayoutImpl extends KGraphDataImpl implements KEdgeLayout {
    /**
     * The cached value of the '{@link #getBendPoints() <em>Bend Points</em>}' containment reference list.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getBendPoints()
     * @generated
     * @ordered
     */
    protected EList<KPoint> bendPoints;

    /**
     * The cached value of the '{@link #getSourcePoint() <em>Source Point</em>}' containment reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getSourcePoint()
     * @generated
     * @ordered
     */
    protected KPoint sourcePoint;

    /**
     * The cached value of the '{@link #getTargetPoint() <em>Target Point</em>}' containment reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getTargetPoint()
     * @generated
     * @ordered
     */
    protected KPoint targetPoint;
    
    /**
     * <!-- begin-user-doc -->
     * Whether the position or size has been modified. This flag is package-visible, since
     * {@link KPointImpl} needs direct access to it.
     * <!-- end-user-doc -->
     * @generated NOT
     */
    boolean modified = false;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected KEdgeLayoutImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return KLayoutDataPackage.Literals.KEDGE_LAYOUT;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EList<KPoint> getBendPoints() {
        if (bendPoints == null) {
            bendPoints = new EObjectContainmentEList<KPoint>(KPoint.class, this, KLayoutDataPackage.KEDGE_LAYOUT__BEND_POINTS);
        }
        return bendPoints;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public KPoint getSourcePoint() {
        return sourcePoint;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetSourcePoint(KPoint newSourcePoint, NotificationChain msgs) {
        KPoint oldSourcePoint = sourcePoint;
        sourcePoint = newSourcePoint;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, KLayoutDataPackage.KEDGE_LAYOUT__SOURCE_POINT, oldSourcePoint, newSourcePoint);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated NOT
     */
    public void setSourcePoint(KPoint newSourcePoint) {
        if (newSourcePoint != sourcePoint) {
            NotificationChain msgs = null;
            if (sourcePoint != null) {
                msgs = ((InternalEObject)sourcePoint).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - KLayoutDataPackage.KEDGE_LAYOUT__SOURCE_POINT, null, msgs);
            }
            if (newSourcePoint != null) {
                msgs = ((InternalEObject)newSourcePoint).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - KLayoutDataPackage.KEDGE_LAYOUT__SOURCE_POINT, null, msgs);
            }
            msgs = basicSetSourcePoint(newSourcePoint, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, KLayoutDataPackage.KEDGE_LAYOUT__SOURCE_POINT, newSourcePoint, newSourcePoint));
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public KPoint getTargetPoint() {
        return targetPoint;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetTargetPoint(KPoint newTargetPoint, NotificationChain msgs) {
        KPoint oldTargetPoint = targetPoint;
        targetPoint = newTargetPoint;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, KLayoutDataPackage.KEDGE_LAYOUT__TARGET_POINT, oldTargetPoint, newTargetPoint);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated NOT
     */
    public void setTargetPoint(KPoint newTargetPoint) {
        if (newTargetPoint != targetPoint) {
            NotificationChain msgs = null;
            if (targetPoint != null) {
                msgs = ((InternalEObject)targetPoint).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - KLayoutDataPackage.KEDGE_LAYOUT__TARGET_POINT, null, msgs);
            }
            if (newTargetPoint != null) {
                msgs = ((InternalEObject)newTargetPoint).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - KLayoutDataPackage.KEDGE_LAYOUT__TARGET_POINT, null, msgs);
            }
            msgs = basicSetTargetPoint(newTargetPoint, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, KLayoutDataPackage.KEDGE_LAYOUT__TARGET_POINT, newTargetPoint, newTargetPoint));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * {@inheritDoc}
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public void applyVectorChain(KVectorChain points) {
        if (sourcePoint == null) {
            setSourcePoint(KLayoutDataFactory.eINSTANCE.createKPoint());
        }
        KVector firstPoint = points.getFirst();
        sourcePoint.setX((float) firstPoint.x);
        sourcePoint.setY((float) firstPoint.y);
        
        // reuse as many existing bend points as possible
        ListIterator<KPoint> oldPointIter = getBendPoints().listIterator();
        ListIterator<KVector> newPointIter = points.listIterator(1);
        while (newPointIter.nextIndex() < points.size() - 1) {
            KVector nextPoint = newPointIter.next();
            KPoint kpoint;
            if (oldPointIter.hasNext()) {
                kpoint = oldPointIter.next();
            } else {
                kpoint = KLayoutDataFactory.eINSTANCE.createKPoint();
                oldPointIter.add(kpoint);
            }
            kpoint.setX((float) nextPoint.x);
            kpoint.setY((float) nextPoint.y);
        }
        while (oldPointIter.hasNext()) {
            oldPointIter.next();
            oldPointIter.remove();
        }
        
        if (targetPoint == null) {
            setTargetPoint(KLayoutDataFactory.eINSTANCE.createKPoint());
        }
        KVector lastPoint = points.getLast();
        targetPoint.setX((float) lastPoint.x);
        targetPoint.setY((float) lastPoint.y);
    }

    /**
     * <!-- begin-user-doc -->
     * {@inheritDoc}
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public KVectorChain createVectorChain() {
        KVectorChain vectorChain = new KVectorChain();
        if (sourcePoint != null) {
            vectorChain.add(sourcePoint.getX(), sourcePoint.getY());
        }
        for (KPoint bendPoint : getBendPoints()) {
            vectorChain.add(bendPoint.getX(), bendPoint.getY());
        }
        if (targetPoint != null) {
            vectorChain.add(targetPoint.getX(), targetPoint.getY());
        }
        return vectorChain;
    }

    /**
     * <!-- begin-user-doc -->
     * {@inheritDoc}
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * <!-- begin-user-doc -->
     * {@inheritDoc}
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public void resetModificationFlag() {
        modified = false;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case KLayoutDataPackage.KEDGE_LAYOUT__BEND_POINTS:
                return ((InternalEList<?>)getBendPoints()).basicRemove(otherEnd, msgs);
            case KLayoutDataPackage.KEDGE_LAYOUT__SOURCE_POINT:
                return basicSetSourcePoint(null, msgs);
            case KLayoutDataPackage.KEDGE_LAYOUT__TARGET_POINT:
                return basicSetTargetPoint(null, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case KLayoutDataPackage.KEDGE_LAYOUT__BEND_POINTS:
                return getBendPoints();
            case KLayoutDataPackage.KEDGE_LAYOUT__SOURCE_POINT:
                return getSourcePoint();
            case KLayoutDataPackage.KEDGE_LAYOUT__TARGET_POINT:
                return getTargetPoint();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case KLayoutDataPackage.KEDGE_LAYOUT__BEND_POINTS:
                getBendPoints().clear();
                getBendPoints().addAll((Collection<? extends KPoint>)newValue);
                return;
            case KLayoutDataPackage.KEDGE_LAYOUT__SOURCE_POINT:
                setSourcePoint((KPoint)newValue);
                return;
            case KLayoutDataPackage.KEDGE_LAYOUT__TARGET_POINT:
                setTargetPoint((KPoint)newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case KLayoutDataPackage.KEDGE_LAYOUT__BEND_POINTS:
                getBendPoints().clear();
                return;
            case KLayoutDataPackage.KEDGE_LAYOUT__SOURCE_POINT:
                setSourcePoint((KPoint)null);
                return;
            case KLayoutDataPackage.KEDGE_LAYOUT__TARGET_POINT:
                setTargetPoint((KPoint)null);
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case KLayoutDataPackage.KEDGE_LAYOUT__BEND_POINTS:
                return bendPoints != null && !bendPoints.isEmpty();
            case KLayoutDataPackage.KEDGE_LAYOUT__SOURCE_POINT:
                return sourcePoint != null;
            case KLayoutDataPackage.KEDGE_LAYOUT__TARGET_POINT:
                return targetPoint != null;
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("( ").append(sourcePoint);
        if (bendPoints != null) {
            for (KPoint p : bendPoints) {
                result.append(" -> ").append(p);
            }
        }
        result.append(" -> ").append(targetPoint).append(" )");
        return result.toString();
    }
} // KEdgeLayoutImpl
