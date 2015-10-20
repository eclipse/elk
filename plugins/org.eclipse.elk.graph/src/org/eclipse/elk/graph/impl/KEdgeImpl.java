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
package org.eclipse.elk.graph.impl;

import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KGraphPackage;
import org.eclipse.elk.graph.KNode;
import org.eclipse.elk.graph.KPort;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>KEdge</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.elk.graph.impl.KEdgeImpl#getSource <em>Source</em>}</li>
 *   <li>{@link org.eclipse.elk.graph.impl.KEdgeImpl#getTarget <em>Target</em>}</li>
 *   <li>{@link org.eclipse.elk.graph.impl.KEdgeImpl#getSourcePort <em>Source Port</em>}</li>
 *   <li>{@link org.eclipse.elk.graph.impl.KEdgeImpl#getTargetPort <em>Target Port</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class KEdgeImpl extends KLabeledGraphElementImpl implements KEdge {
    /**
     * The cached value of the '{@link #getTarget() <em>Target</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTarget()
     * @generated
     * @ordered
     */
    protected KNode target;

    /**
     * The cached value of the '{@link #getSourcePort() <em>Source Port</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSourcePort()
     * @generated
     * @ordered
     */
    protected KPort sourcePort;

    /**
     * The cached value of the '{@link #getTargetPort() <em>Target Port</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTargetPort()
     * @generated
     * @ordered
     */
    protected KPort targetPort;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected KEdgeImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return KGraphPackage.Literals.KEDGE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public KNode getSource() {
        if (eContainerFeatureID() != KGraphPackage.KEDGE__SOURCE) return null;
        return (KNode)eInternalContainer();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetSource(KNode newSource, NotificationChain msgs) {
        msgs = eBasicSetContainer((InternalEObject)newSource, KGraphPackage.KEDGE__SOURCE, msgs);
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSource(KNode newSource) {
        if (newSource != eInternalContainer() || (eContainerFeatureID() != KGraphPackage.KEDGE__SOURCE && newSource != null)) {
            if (EcoreUtil.isAncestor(this, newSource))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
            NotificationChain msgs = null;
            if (eInternalContainer() != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newSource != null)
                msgs = ((InternalEObject)newSource).eInverseAdd(this, KGraphPackage.KNODE__OUTGOING_EDGES, KNode.class, msgs);
            msgs = basicSetSource(newSource, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, KGraphPackage.KEDGE__SOURCE, newSource, newSource));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public KNode getTarget() {
        if (target != null && target.eIsProxy()) {
            InternalEObject oldTarget = (InternalEObject)target;
            target = (KNode)eResolveProxy(oldTarget);
            if (target != oldTarget) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, KGraphPackage.KEDGE__TARGET, oldTarget, target));
            }
        }
        return target;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public KNode basicGetTarget() {
        return target;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetTarget(KNode newTarget, NotificationChain msgs) {
        KNode oldTarget = target;
        target = newTarget;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, KGraphPackage.KEDGE__TARGET, oldTarget, newTarget);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTarget(KNode newTarget) {
        if (newTarget != target) {
            NotificationChain msgs = null;
            if (target != null)
                msgs = ((InternalEObject)target).eInverseRemove(this, KGraphPackage.KNODE__INCOMING_EDGES, KNode.class, msgs);
            if (newTarget != null)
                msgs = ((InternalEObject)newTarget).eInverseAdd(this, KGraphPackage.KNODE__INCOMING_EDGES, KNode.class, msgs);
            msgs = basicSetTarget(newTarget, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, KGraphPackage.KEDGE__TARGET, newTarget, newTarget));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public KPort getSourcePort() {
        if (sourcePort != null && sourcePort.eIsProxy()) {
            InternalEObject oldSourcePort = (InternalEObject)sourcePort;
            sourcePort = (KPort)eResolveProxy(oldSourcePort);
            if (sourcePort != oldSourcePort) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, KGraphPackage.KEDGE__SOURCE_PORT, oldSourcePort, sourcePort));
            }
        }
        return sourcePort;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public KPort basicGetSourcePort() {
        return sourcePort;
    }

    /**
     * <!-- begin-user-doc -->
     * The original generated code is extended so the {@link KPort#getEdges()} list is also updated.
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public void setSourcePort(KPort newSourcePort) {
        KPort oldSourcePort = sourcePort;
        sourcePort = newSourcePort;
        if (oldSourcePort != null && oldSourcePort != targetPort) {
            oldSourcePort.getEdges().remove(this);
        }
        if (newSourcePort != null && !newSourcePort.getEdges().contains(this)) {
            newSourcePort.getEdges().add(this);
        }
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, KGraphPackage.KEDGE__SOURCE_PORT, oldSourcePort, sourcePort));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public KPort getTargetPort() {
        if (targetPort != null && targetPort.eIsProxy()) {
            InternalEObject oldTargetPort = (InternalEObject)targetPort;
            targetPort = (KPort)eResolveProxy(oldTargetPort);
            if (targetPort != oldTargetPort) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, KGraphPackage.KEDGE__TARGET_PORT, oldTargetPort, targetPort));
            }
        }
        return targetPort;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public KPort basicGetTargetPort() {
        return targetPort;
    }

    /**
     * <!-- begin-user-doc -->
     * The original generated code is extended so the {@link KPort#getEdges()} list is also updated.
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public void setTargetPort(KPort newTargetPort) {
        KPort oldTargetPort = targetPort;
        targetPort = newTargetPort;
        if (oldTargetPort != null && oldTargetPort != sourcePort) {
            oldTargetPort.getEdges().remove(this);
        }
        if (newTargetPort != null && !newTargetPort.getEdges().contains(this)) {
            newTargetPort.getEdges().add(this);
        }
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, KGraphPackage.KEDGE__TARGET_PORT, oldTargetPort, targetPort));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case KGraphPackage.KEDGE__SOURCE:
                if (eInternalContainer() != null)
                    msgs = eBasicRemoveFromContainer(msgs);
                return basicSetSource((KNode)otherEnd, msgs);
            case KGraphPackage.KEDGE__TARGET:
                if (target != null)
                    msgs = ((InternalEObject)target).eInverseRemove(this, KGraphPackage.KNODE__INCOMING_EDGES, KNode.class, msgs);
                return basicSetTarget((KNode)otherEnd, msgs);
        }
        return super.eInverseAdd(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case KGraphPackage.KEDGE__SOURCE:
                return basicSetSource(null, msgs);
            case KGraphPackage.KEDGE__TARGET:
                return basicSetTarget(null, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
        switch (eContainerFeatureID()) {
            case KGraphPackage.KEDGE__SOURCE:
                return eInternalContainer().eInverseRemove(this, KGraphPackage.KNODE__OUTGOING_EDGES, KNode.class, msgs);
        }
        return super.eBasicRemoveFromContainerFeature(msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case KGraphPackage.KEDGE__SOURCE:
                return getSource();
            case KGraphPackage.KEDGE__TARGET:
                if (resolve) return getTarget();
                return basicGetTarget();
            case KGraphPackage.KEDGE__SOURCE_PORT:
                if (resolve) return getSourcePort();
                return basicGetSourcePort();
            case KGraphPackage.KEDGE__TARGET_PORT:
                if (resolve) return getTargetPort();
                return basicGetTargetPort();
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
            case KGraphPackage.KEDGE__SOURCE:
                setSource((KNode)newValue);
                return;
            case KGraphPackage.KEDGE__TARGET:
                setTarget((KNode)newValue);
                return;
            case KGraphPackage.KEDGE__SOURCE_PORT:
                setSourcePort((KPort)newValue);
                return;
            case KGraphPackage.KEDGE__TARGET_PORT:
                setTargetPort((KPort)newValue);
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
            case KGraphPackage.KEDGE__SOURCE:
                setSource((KNode)null);
                return;
            case KGraphPackage.KEDGE__TARGET:
                setTarget((KNode)null);
                return;
            case KGraphPackage.KEDGE__SOURCE_PORT:
                setSourcePort((KPort)null);
                return;
            case KGraphPackage.KEDGE__TARGET_PORT:
                setTargetPort((KPort)null);
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
            case KGraphPackage.KEDGE__SOURCE:
                return getSource() != null;
            case KGraphPackage.KEDGE__TARGET:
                return target != null;
            case KGraphPackage.KEDGE__SOURCE_PORT:
                return sourcePort != null;
            case KGraphPackage.KEDGE__TARGET_PORT:
                return targetPort != null;
        }
        return super.eIsSet(featureID);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        KNode source = getSource();
        if (source != null && target != null) {
            final String sPort = getSourcePort() == null ? "" : (":" + getSourcePort().toString());
            final String tPort = getTargetPort() == null ? "" : (":" + getTargetPort().toString());
            return "KEdge:" + source.toString() + sPort + "->" + target.toString() + tPort;
        }
        return super.toString();
    }

} //KEdgeImpl
