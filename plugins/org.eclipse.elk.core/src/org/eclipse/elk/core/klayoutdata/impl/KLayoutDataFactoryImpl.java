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

import org.eclipse.elk.core.klayoutdata.*;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class KLayoutDataFactoryImpl extends EFactoryImpl implements KLayoutDataFactory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static KLayoutDataFactory init() {
        try {
            KLayoutDataFactory theKLayoutDataFactory = (KLayoutDataFactory)EPackage.Registry.INSTANCE.getEFactory("http://elk.eclipse.org/KLayoutData"); 
            if (theKLayoutDataFactory != null) {
                return theKLayoutDataFactory;
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new KLayoutDataFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public KLayoutDataFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
            case KLayoutDataPackage.KSHAPE_LAYOUT: return createKShapeLayout();
            case KLayoutDataPackage.KEDGE_LAYOUT: return createKEdgeLayout();
            case KLayoutDataPackage.KPOINT: return createKPoint();
            case KLayoutDataPackage.KINSETS: return createKInsets();
            case KLayoutDataPackage.KIDENTIFIER: return createKIdentifier();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public KShapeLayout createKShapeLayout() {
        KShapeLayoutImpl kShapeLayout = new KShapeLayoutImpl();
        return kShapeLayout;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public KEdgeLayout createKEdgeLayout() {
        KEdgeLayoutImpl kEdgeLayout = new KEdgeLayoutImpl();
        return kEdgeLayout;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public KPoint createKPoint() {
        KPointImpl kPoint = new KPointImpl();
        return kPoint;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public KInsets createKInsets() {
        KInsetsImpl kInsets = new KInsetsImpl();
        return kInsets;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public KIdentifier createKIdentifier() {
        KIdentifierImpl kIdentifier = new KIdentifierImpl();
        return kIdentifier;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public KLayoutDataPackage getKLayoutDataPackage() {
        return (KLayoutDataPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static KLayoutDataPackage getPackage() {
        return KLayoutDataPackage.eINSTANCE;
    }

} //KLayoutDataFactoryImpl
