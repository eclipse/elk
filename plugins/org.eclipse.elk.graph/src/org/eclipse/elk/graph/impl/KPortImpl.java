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

import java.util.Collection;

import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KGraphPackage;
import org.eclipse.elk.graph.KNode;
import org.eclipse.elk.graph.KPort;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>KPort</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.elk.graph.impl.KPortImpl#getNode <em>Node</em>}</li>
 *   <li>{@link org.eclipse.elk.graph.impl.KPortImpl#getEdges <em>Edges</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class KPortImpl extends KLabeledGraphElementImpl implements KPort {
    /**
     * The cached value of the '{@link #getEdges() <em>Edges</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEdges()
     * @generated
     * @ordered
     */
    protected EList<KEdge> edges;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected KPortImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return KGraphPackage.Literals.KPORT;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public KNode getNode() {
        if (eContainerFeatureID() != KGraphPackage.KPORT__NODE) return null;
        return (KNode)eInternalContainer();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetNode(KNode newNode, NotificationChain msgs) {
        msgs = eBasicSetContainer((InternalEObject)newNode, KGraphPackage.KPORT__NODE, msgs);
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setNode(KNode newNode) {
        if (newNode != eInternalContainer() || (eContainerFeatureID() != KGraphPackage.KPORT__NODE && newNode != null)) {
            if (EcoreUtil.isAncestor(this, newNode))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
            NotificationChain msgs = null;
            if (eInternalContainer() != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newNode != null)
                msgs = ((InternalEObject)newNode).eInverseAdd(this, KGraphPackage.KNODE__PORTS, KNode.class, msgs);
            msgs = basicSetNode(newNode, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, KGraphPackage.KPORT__NODE, newNode, newNode));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<KEdge> getEdges() {
        if (edges == null) {
            edges = new EObjectResolvingEList<KEdge>(KEdge.class, this, KGraphPackage.KPORT__EDGES);
        }
        return edges;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case KGraphPackage.KPORT__NODE:
                if (eInternalContainer() != null)
                    msgs = eBasicRemoveFromContainer(msgs);
                return basicSetNode((KNode)otherEnd, msgs);
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
            case KGraphPackage.KPORT__NODE:
                return basicSetNode(null, msgs);
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
            case KGraphPackage.KPORT__NODE:
                return eInternalContainer().eInverseRemove(this, KGraphPackage.KNODE__PORTS, KNode.class, msgs);
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
            case KGraphPackage.KPORT__NODE:
                return getNode();
            case KGraphPackage.KPORT__EDGES:
                return getEdges();
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
            case KGraphPackage.KPORT__NODE:
                setNode((KNode)newValue);
                return;
            case KGraphPackage.KPORT__EDGES:
                getEdges().clear();
                getEdges().addAll((Collection<? extends KEdge>)newValue);
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
            case KGraphPackage.KPORT__NODE:
                setNode((KNode)null);
                return;
            case KGraphPackage.KPORT__EDGES:
                getEdges().clear();
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
            case KGraphPackage.KPORT__NODE:
                return getNode() != null;
            case KGraphPackage.KPORT__EDGES:
                return edges != null && !edges.isEmpty();
        }
        return super.eIsSet(featureID);
    }
    
    /**
     * @generated NOT
     */
    @Override
    public String toString() {
        if (getLabels().size() > 0) {
            String text = getLabels().get(0).getText();
            if (text != null && text.length() > 0) {
                return "KPort \"" + text + "\"";
            }
        }

        return super.toString();
    }

} //KPortImpl
