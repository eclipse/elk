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
package org.eclipse.elk.core.klayoutdata.util;

import org.eclipse.elk.core.klayoutdata.*;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.graph.EMapPropertyHolder;
import org.eclipse.elk.graph.KGraphData;
import org.eclipse.elk.graph.properties.IPropertyHolder;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.elk.core.klayoutdata.KLayoutDataPackage
 * @generated
 */
public class KLayoutDataSwitch<T> extends Switch<T> {
    /**
     * The cached model package
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static KLayoutDataPackage modelPackage;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public KLayoutDataSwitch() {
        if (modelPackage == null) {
            modelPackage = KLayoutDataPackage.eINSTANCE;
        }
    }

    /**
     * Checks whether this is a switch for the given package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @parameter ePackage the package in question.
     * @return whether this is a switch for the given package.
     * @generated
     */
    @Override
    protected boolean isSwitchFor(EPackage ePackage) {
        return ePackage == modelPackage;
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    @Override
    protected T doSwitch(int classifierID, EObject theEObject) {
        switch (classifierID) {
            case KLayoutDataPackage.KSHAPE_LAYOUT: {
                KShapeLayout kShapeLayout = (KShapeLayout)theEObject;
                T result = caseKShapeLayout(kShapeLayout);
                if (result == null) result = caseKLayoutData(kShapeLayout);
                if (result == null) result = caseKGraphData(kShapeLayout);
                if (result == null) result = caseEMapPropertyHolder(kShapeLayout);
                if (result == null) result = caseIPropertyHolder(kShapeLayout);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case KLayoutDataPackage.KEDGE_LAYOUT: {
                KEdgeLayout kEdgeLayout = (KEdgeLayout)theEObject;
                T result = caseKEdgeLayout(kEdgeLayout);
                if (result == null) result = caseKLayoutData(kEdgeLayout);
                if (result == null) result = caseKGraphData(kEdgeLayout);
                if (result == null) result = caseEMapPropertyHolder(kEdgeLayout);
                if (result == null) result = caseIPropertyHolder(kEdgeLayout);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case KLayoutDataPackage.KLAYOUT_DATA: {
                KLayoutData kLayoutData = (KLayoutData)theEObject;
                T result = caseKLayoutData(kLayoutData);
                if (result == null) result = caseKGraphData(kLayoutData);
                if (result == null) result = caseEMapPropertyHolder(kLayoutData);
                if (result == null) result = caseIPropertyHolder(kLayoutData);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case KLayoutDataPackage.KPOINT: {
                KPoint kPoint = (KPoint)theEObject;
                T result = caseKPoint(kPoint);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case KLayoutDataPackage.KINSETS: {
                KInsets kInsets = (KInsets)theEObject;
                T result = caseKInsets(kInsets);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case KLayoutDataPackage.KIDENTIFIER: {
                KIdentifier kIdentifier = (KIdentifier)theEObject;
                T result = caseKIdentifier(kIdentifier);
                if (result == null) result = caseKGraphData(kIdentifier);
                if (result == null) result = caseEMapPropertyHolder(kIdentifier);
                if (result == null) result = caseIPropertyHolder(kIdentifier);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            default: return defaultCase(theEObject);
        }
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>KShape Layout</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>KShape Layout</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseKShapeLayout(KShapeLayout object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>KEdge Layout</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>KEdge Layout</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseKEdgeLayout(KEdgeLayout object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>KLayout Data</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>KLayout Data</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseKLayoutData(KLayoutData object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>KPoint</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>KPoint</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseKPoint(KPoint object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>KInsets</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>KInsets</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseKInsets(KInsets object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>KIdentifier</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>KIdentifier</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseKIdentifier(KIdentifier object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>KVector</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>KVector</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseKVector(KVector object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>KVector Chain</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>KVector Chain</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseKVectorChain(KVectorChain object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>IProperty Holder</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>IProperty Holder</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseIPropertyHolder(IPropertyHolder object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>EMap Property Holder</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>EMap Property Holder</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseEMapPropertyHolder(EMapPropertyHolder object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Data</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Data</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseKGraphData(KGraphData object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch, but this is the last case anyway.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject)
     * @generated
     */
    @Override
    public T defaultCase(EObject object) {
        return null;
    }

} //KLayoutDataSwitch
