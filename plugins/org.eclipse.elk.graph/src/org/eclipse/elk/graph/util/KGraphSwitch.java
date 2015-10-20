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
package org.eclipse.elk.graph.util;

import java.util.Map;

import org.eclipse.elk.graph.*;
import org.eclipse.elk.graph.properties.IProperty;
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
 * @see org.eclipse.elk.graph.KGraphPackage
 * @generated
 */
public class KGraphSwitch<T> extends Switch<T> {
    /**
     * The cached model package
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static KGraphPackage modelPackage;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public KGraphSwitch() {
        if (modelPackage == null) {
            modelPackage = KGraphPackage.eINSTANCE;
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
            case KGraphPackage.KGRAPH_ELEMENT: {
                KGraphElement kGraphElement = (KGraphElement)theEObject;
                T result = caseKGraphElement(kGraphElement);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case KGraphPackage.KLABELED_GRAPH_ELEMENT: {
                KLabeledGraphElement kLabeledGraphElement = (KLabeledGraphElement)theEObject;
                T result = caseKLabeledGraphElement(kLabeledGraphElement);
                if (result == null) result = caseKGraphElement(kLabeledGraphElement);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case KGraphPackage.KGRAPH_DATA: {
                KGraphData kGraphData = (KGraphData)theEObject;
                T result = caseKGraphData(kGraphData);
                if (result == null) result = caseEMapPropertyHolder(kGraphData);
                if (result == null) result = caseIPropertyHolder(kGraphData);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case KGraphPackage.KNODE: {
                KNode kNode = (KNode)theEObject;
                T result = caseKNode(kNode);
                if (result == null) result = caseKLabeledGraphElement(kNode);
                if (result == null) result = caseKGraphElement(kNode);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case KGraphPackage.KEDGE: {
                KEdge kEdge = (KEdge)theEObject;
                T result = caseKEdge(kEdge);
                if (result == null) result = caseKLabeledGraphElement(kEdge);
                if (result == null) result = caseKGraphElement(kEdge);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case KGraphPackage.KPORT: {
                KPort kPort = (KPort)theEObject;
                T result = caseKPort(kPort);
                if (result == null) result = caseKLabeledGraphElement(kPort);
                if (result == null) result = caseKGraphElement(kPort);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case KGraphPackage.KLABEL: {
                KLabel kLabel = (KLabel)theEObject;
                T result = caseKLabel(kLabel);
                if (result == null) result = caseKGraphElement(kLabel);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case KGraphPackage.EMAP_PROPERTY_HOLDER: {
                EMapPropertyHolder eMapPropertyHolder = (EMapPropertyHolder)theEObject;
                T result = caseEMapPropertyHolder(eMapPropertyHolder);
                if (result == null) result = caseIPropertyHolder(eMapPropertyHolder);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case KGraphPackage.IPROPERTY_TO_OBJECT_MAP: {
                @SuppressWarnings("unchecked") Map.Entry<IProperty<?>, Object> iPropertyToObjectMap = (Map.Entry<IProperty<?>, Object>)theEObject;
                T result = caseIPropertyToObjectMap(iPropertyToObjectMap);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case KGraphPackage.IPROPERTY_HOLDER: {
                IPropertyHolder iPropertyHolder = (IPropertyHolder)theEObject;
                T result = caseIPropertyHolder(iPropertyHolder);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case KGraphPackage.PERSISTENT_ENTRY: {
                PersistentEntry persistentEntry = (PersistentEntry)theEObject;
                T result = casePersistentEntry(persistentEntry);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            default: return defaultCase(theEObject);
        }
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Element</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Element</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseKGraphElement(KGraphElement object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>KLabeled Graph Element</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>KLabeled Graph Element</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseKLabeledGraphElement(KLabeledGraphElement object) {
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
     * Returns the result of interpreting the object as an instance of '<em>KNode</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>KNode</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseKNode(KNode object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>KEdge</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>KEdge</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseKEdge(KEdge object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>KPort</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>KPort</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseKPort(KPort object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>KLabel</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>KLabel</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseKLabel(KLabel object) {
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
     * Returns the result of interpreting the object as an instance of '<em>IProperty To Object Map</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>IProperty To Object Map</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseIPropertyToObjectMap(Map.Entry<IProperty<?>, Object> object) {
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
     * Returns the result of interpreting the object as an instance of '<em>Persistent Entry</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Persistent Entry</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T casePersistentEntry(PersistentEntry object) {
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

} //KGraphSwitch
