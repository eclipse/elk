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

import org.eclipse.elk.core.klayoutdata.KEdgeLayout;
import org.eclipse.elk.core.klayoutdata.KIdentifier;
import org.eclipse.elk.core.klayoutdata.KInsets;
import org.eclipse.elk.core.klayoutdata.KLayoutData;
import org.eclipse.elk.core.klayoutdata.KLayoutDataFactory;
import org.eclipse.elk.core.klayoutdata.KLayoutDataPackage;
import org.eclipse.elk.core.klayoutdata.KPoint;
import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.graph.KGraphPackage;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class KLayoutDataPackageImpl extends EPackageImpl implements KLayoutDataPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass kShapeLayoutEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass kEdgeLayoutEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass kLayoutDataEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass kPointEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass kInsetsEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass kIdentifierEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass kVectorEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass kVectorChainEClass = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
     * package URI value.
     * <p>Note: the correct way to create the package is via the static
     * factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package,
     * if one already exists.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see org.eclipse.elk.core.klayoutdata.KLayoutDataPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private KLayoutDataPackageImpl() {
        super(eNS_URI, KLayoutDataFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
     * 
     * <p>This method is used to initialize {@link KLayoutDataPackage#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static KLayoutDataPackage init() {
        if (isInited) return (KLayoutDataPackage)EPackage.Registry.INSTANCE.getEPackage(KLayoutDataPackage.eNS_URI);

        // Obtain or create and register package
        KLayoutDataPackageImpl theKLayoutDataPackage = (KLayoutDataPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof KLayoutDataPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new KLayoutDataPackageImpl());

        isInited = true;

        // Initialize simple dependencies
        KGraphPackage.eINSTANCE.eClass();

        // Create package meta-data objects
        theKLayoutDataPackage.createPackageContents();

        // Initialize created meta-data
        theKLayoutDataPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theKLayoutDataPackage.freeze();

  
        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put(KLayoutDataPackage.eNS_URI, theKLayoutDataPackage);
        return theKLayoutDataPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getKShapeLayout() {
        return kShapeLayoutEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getKShapeLayout_Xpos() {
        return (EAttribute)kShapeLayoutEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getKShapeLayout_Ypos() {
        return (EAttribute)kShapeLayoutEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getKShapeLayout_Width() {
        return (EAttribute)kShapeLayoutEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getKShapeLayout_Height() {
        return (EAttribute)kShapeLayoutEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getKShapeLayout_Insets() {
        return (EReference)kShapeLayoutEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getKEdgeLayout() {
        return kEdgeLayoutEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getKEdgeLayout_BendPoints() {
        return (EReference)kEdgeLayoutEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getKEdgeLayout_SourcePoint() {
        return (EReference)kEdgeLayoutEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getKEdgeLayout_TargetPoint() {
        return (EReference)kEdgeLayoutEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getKLayoutData() {
        return kLayoutDataEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getKPoint() {
        return kPointEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getKPoint_X() {
        return (EAttribute)kPointEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getKPoint_Y() {
        return (EAttribute)kPointEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getKInsets() {
        return kInsetsEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getKInsets_Top() {
        return (EAttribute)kInsetsEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getKInsets_Bottom() {
        return (EAttribute)kInsetsEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getKInsets_Left() {
        return (EAttribute)kInsetsEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getKInsets_Right() {
        return (EAttribute)kInsetsEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getKIdentifier() {
        return kIdentifierEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getKIdentifier_Id() {
        return (EAttribute)kIdentifierEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getKVector() {
        return kVectorEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getKVector_X() {
        return (EAttribute)kVectorEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getKVector_Y() {
        return (EAttribute)kVectorEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getKVectorChain() {
        return kVectorChainEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public KLayoutDataFactory getKLayoutDataFactory() {
        return (KLayoutDataFactory)getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void createPackageContents() {
        if (isCreated) return;
        isCreated = true;

        // Create classes and their features
        kShapeLayoutEClass = createEClass(KSHAPE_LAYOUT);
        createEAttribute(kShapeLayoutEClass, KSHAPE_LAYOUT__XPOS);
        createEAttribute(kShapeLayoutEClass, KSHAPE_LAYOUT__YPOS);
        createEAttribute(kShapeLayoutEClass, KSHAPE_LAYOUT__WIDTH);
        createEAttribute(kShapeLayoutEClass, KSHAPE_LAYOUT__HEIGHT);
        createEReference(kShapeLayoutEClass, KSHAPE_LAYOUT__INSETS);

        kEdgeLayoutEClass = createEClass(KEDGE_LAYOUT);
        createEReference(kEdgeLayoutEClass, KEDGE_LAYOUT__BEND_POINTS);
        createEReference(kEdgeLayoutEClass, KEDGE_LAYOUT__SOURCE_POINT);
        createEReference(kEdgeLayoutEClass, KEDGE_LAYOUT__TARGET_POINT);

        kLayoutDataEClass = createEClass(KLAYOUT_DATA);

        kPointEClass = createEClass(KPOINT);
        createEAttribute(kPointEClass, KPOINT__X);
        createEAttribute(kPointEClass, KPOINT__Y);

        kInsetsEClass = createEClass(KINSETS);
        createEAttribute(kInsetsEClass, KINSETS__TOP);
        createEAttribute(kInsetsEClass, KINSETS__BOTTOM);
        createEAttribute(kInsetsEClass, KINSETS__LEFT);
        createEAttribute(kInsetsEClass, KINSETS__RIGHT);

        kIdentifierEClass = createEClass(KIDENTIFIER);
        createEAttribute(kIdentifierEClass, KIDENTIFIER__ID);

        kVectorEClass = createEClass(KVECTOR);
        createEAttribute(kVectorEClass, KVECTOR__X);
        createEAttribute(kVectorEClass, KVECTOR__Y);

        kVectorChainEClass = createEClass(KVECTOR_CHAIN);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void initializePackageContents() {
        if (isInitialized) return;
        isInitialized = true;

        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);

        // Obtain other dependent packages
        KGraphPackage theKGraphPackage = (KGraphPackage)EPackage.Registry.INSTANCE.getEPackage(KGraphPackage.eNS_URI);

        // Create type parameters

        // Set bounds for type parameters

        // Add supertypes to classes
        kShapeLayoutEClass.getESuperTypes().add(this.getKLayoutData());
        kEdgeLayoutEClass.getESuperTypes().add(this.getKLayoutData());
        kLayoutDataEClass.getESuperTypes().add(theKGraphPackage.getKGraphData());
        kIdentifierEClass.getESuperTypes().add(theKGraphPackage.getKGraphData());

        // Initialize classes and features; add operations and parameters
        initEClass(kShapeLayoutEClass, KShapeLayout.class, "KShapeLayout", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getKShapeLayout_Xpos(), ecorePackage.getEFloat(), "xpos", "0.0f", 0, 1, KShapeLayout.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getKShapeLayout_Ypos(), ecorePackage.getEFloat(), "ypos", "0.0f", 0, 1, KShapeLayout.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getKShapeLayout_Width(), ecorePackage.getEFloat(), "width", "0.0f", 0, 1, KShapeLayout.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getKShapeLayout_Height(), ecorePackage.getEFloat(), "height", "0.0f", 0, 1, KShapeLayout.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getKShapeLayout_Insets(), this.getKInsets(), null, "insets", null, 0, 1, KShapeLayout.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        EOperation op = addEOperation(kShapeLayoutEClass, null, "setPos", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, ecorePackage.getEFloat(), "x", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, ecorePackage.getEFloat(), "y", 0, 1, IS_UNIQUE, IS_ORDERED);

        op = addEOperation(kShapeLayoutEClass, null, "applyVector", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, this.getKVector(), "pos", 0, 1, IS_UNIQUE, IS_ORDERED);

        addEOperation(kShapeLayoutEClass, this.getKVector(), "createVector", 0, 1, IS_UNIQUE, IS_ORDERED);

        op = addEOperation(kShapeLayoutEClass, null, "setSize", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, ecorePackage.getEFloat(), "width", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, ecorePackage.getEFloat(), "height", 0, 1, IS_UNIQUE, IS_ORDERED);

        initEClass(kEdgeLayoutEClass, KEdgeLayout.class, "KEdgeLayout", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getKEdgeLayout_BendPoints(), this.getKPoint(), null, "bendPoints", null, 0, -1, KEdgeLayout.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getKEdgeLayout_SourcePoint(), this.getKPoint(), null, "sourcePoint", null, 1, 1, KEdgeLayout.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getKEdgeLayout_TargetPoint(), this.getKPoint(), null, "targetPoint", null, 1, 1, KEdgeLayout.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        op = addEOperation(kEdgeLayoutEClass, null, "applyVectorChain", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, this.getKVectorChain(), "points", 0, 1, IS_UNIQUE, IS_ORDERED);

        addEOperation(kEdgeLayoutEClass, this.getKVectorChain(), "createVectorChain", 0, 1, IS_UNIQUE, IS_ORDERED);

        initEClass(kLayoutDataEClass, KLayoutData.class, "KLayoutData", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        addEOperation(kLayoutDataEClass, ecorePackage.getEBoolean(), "isModified", 1, 1, IS_UNIQUE, IS_ORDERED);

        addEOperation(kLayoutDataEClass, null, "resetModificationFlag", 0, 1, IS_UNIQUE, IS_ORDERED);

        initEClass(kPointEClass, KPoint.class, "KPoint", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getKPoint_X(), ecorePackage.getEFloat(), "x", "0.0f", 0, 1, KPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getKPoint_Y(), ecorePackage.getEFloat(), "y", "0.0f", 0, 1, KPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        op = addEOperation(kPointEClass, null, "setPos", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, ecorePackage.getEFloat(), "x", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, ecorePackage.getEFloat(), "y", 0, 1, IS_UNIQUE, IS_ORDERED);

        op = addEOperation(kPointEClass, null, "applyVector", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, this.getKVector(), "pos", 0, 1, IS_UNIQUE, IS_ORDERED);

        addEOperation(kPointEClass, this.getKVector(), "createVector", 0, 1, IS_UNIQUE, IS_ORDERED);

        initEClass(kInsetsEClass, KInsets.class, "KInsets", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getKInsets_Top(), ecorePackage.getEFloat(), "top", null, 0, 1, KInsets.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getKInsets_Bottom(), ecorePackage.getEFloat(), "bottom", null, 0, 1, KInsets.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getKInsets_Left(), ecorePackage.getEFloat(), "left", null, 0, 1, KInsets.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getKInsets_Right(), ecorePackage.getEFloat(), "right", null, 0, 1, KInsets.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(kIdentifierEClass, KIdentifier.class, "KIdentifier", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getKIdentifier_Id(), ecorePackage.getEString(), "id", null, 1, 1, KIdentifier.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(kVectorEClass, KVector.class, "KVector", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getKVector_X(), ecorePackage.getEDouble(), "x", null, 0, 1, KVector.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getKVector_Y(), ecorePackage.getEDouble(), "y", null, 0, 1, KVector.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(kVectorChainEClass, KVectorChain.class, "KVectorChain", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);

        // Create resource
        createResource(eNS_URI);
    }

} //KLayoutDataPackageImpl
