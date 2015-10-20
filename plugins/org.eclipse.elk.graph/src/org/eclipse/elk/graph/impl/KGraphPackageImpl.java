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

import java.util.Map;

import org.eclipse.elk.graph.EMapPropertyHolder;
import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KGraphData;
import org.eclipse.elk.graph.KGraphElement;
import org.eclipse.elk.graph.KGraphFactory;
import org.eclipse.elk.graph.KGraphPackage;
import org.eclipse.elk.graph.KLabel;
import org.eclipse.elk.graph.KLabeledGraphElement;
import org.eclipse.elk.graph.KNode;
import org.eclipse.elk.graph.KPort;
import org.eclipse.elk.graph.PersistentEntry;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.IPropertyHolder;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class KGraphPackageImpl extends EPackageImpl implements KGraphPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass kGraphElementEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass kLabeledGraphElementEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass kGraphDataEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass kNodeEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass kEdgeEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass kPortEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass kLabelEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass eMapPropertyHolderEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass iPropertyToObjectMapEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass iPropertyHolderEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass persistentEntryEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType iPropertyEDataType = null;

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
     * @see org.eclipse.elk.graph.KGraphPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private KGraphPackageImpl() {
        super(eNS_URI, KGraphFactory.eINSTANCE);
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
     * <p>This method is used to initialize {@link KGraphPackage#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static KGraphPackage init() {
        if (isInited) return (KGraphPackage)EPackage.Registry.INSTANCE.getEPackage(KGraphPackage.eNS_URI);

        // Obtain or create and register package
        KGraphPackageImpl theKGraphPackage = (KGraphPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof KGraphPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new KGraphPackageImpl());

        isInited = true;

        // Create package meta-data objects
        theKGraphPackage.createPackageContents();

        // Initialize created meta-data
        theKGraphPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theKGraphPackage.freeze();

  
        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put(KGraphPackage.eNS_URI, theKGraphPackage);
        return theKGraphPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getKGraphElement() {
        return kGraphElementEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getKGraphElement_Data() {
        return (EReference)kGraphElementEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getKLabeledGraphElement() {
        return kLabeledGraphElementEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getKLabeledGraphElement_Labels() {
        return (EReference)kLabeledGraphElementEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getKGraphData() {
        return kGraphDataEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getKNode() {
        return kNodeEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getKNode_Children() {
        return (EReference)kNodeEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getKNode_Parent() {
        return (EReference)kNodeEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getKNode_Ports() {
        return (EReference)kNodeEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getKNode_OutgoingEdges() {
        return (EReference)kNodeEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getKNode_IncomingEdges() {
        return (EReference)kNodeEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getKEdge() {
        return kEdgeEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getKEdge_Source() {
        return (EReference)kEdgeEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getKEdge_Target() {
        return (EReference)kEdgeEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getKEdge_SourcePort() {
        return (EReference)kEdgeEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getKEdge_TargetPort() {
        return (EReference)kEdgeEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getKPort() {
        return kPortEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getKPort_Node() {
        return (EReference)kPortEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getKPort_Edges() {
        return (EReference)kPortEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getKLabel() {
        return kLabelEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getKLabel_Text() {
        return (EAttribute)kLabelEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getKLabel_Parent() {
        return (EReference)kLabelEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getEMapPropertyHolder() {
        return eMapPropertyHolderEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getEMapPropertyHolder_Properties() {
        return (EReference)eMapPropertyHolderEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getEMapPropertyHolder_PersistentEntries() {
        return (EReference)eMapPropertyHolderEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getIPropertyToObjectMap() {
        return iPropertyToObjectMapEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getIPropertyToObjectMap_Key() {
        return (EAttribute)iPropertyToObjectMapEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getIPropertyToObjectMap_Value() {
        return (EAttribute)iPropertyToObjectMapEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getIProperty() {
        return iPropertyEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getIPropertyHolder() {
        return iPropertyHolderEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getPersistentEntry() {
        return persistentEntryEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getPersistentEntry_Key() {
        return (EAttribute)persistentEntryEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getPersistentEntry_Value() {
        return (EAttribute)persistentEntryEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public KGraphFactory getKGraphFactory() {
        return (KGraphFactory)getEFactoryInstance();
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
        kGraphElementEClass = createEClass(KGRAPH_ELEMENT);
        createEReference(kGraphElementEClass, KGRAPH_ELEMENT__DATA);

        kLabeledGraphElementEClass = createEClass(KLABELED_GRAPH_ELEMENT);
        createEReference(kLabeledGraphElementEClass, KLABELED_GRAPH_ELEMENT__LABELS);

        kGraphDataEClass = createEClass(KGRAPH_DATA);

        kNodeEClass = createEClass(KNODE);
        createEReference(kNodeEClass, KNODE__CHILDREN);
        createEReference(kNodeEClass, KNODE__PARENT);
        createEReference(kNodeEClass, KNODE__PORTS);
        createEReference(kNodeEClass, KNODE__OUTGOING_EDGES);
        createEReference(kNodeEClass, KNODE__INCOMING_EDGES);

        kEdgeEClass = createEClass(KEDGE);
        createEReference(kEdgeEClass, KEDGE__SOURCE);
        createEReference(kEdgeEClass, KEDGE__TARGET);
        createEReference(kEdgeEClass, KEDGE__SOURCE_PORT);
        createEReference(kEdgeEClass, KEDGE__TARGET_PORT);

        kPortEClass = createEClass(KPORT);
        createEReference(kPortEClass, KPORT__NODE);
        createEReference(kPortEClass, KPORT__EDGES);

        kLabelEClass = createEClass(KLABEL);
        createEAttribute(kLabelEClass, KLABEL__TEXT);
        createEReference(kLabelEClass, KLABEL__PARENT);

        eMapPropertyHolderEClass = createEClass(EMAP_PROPERTY_HOLDER);
        createEReference(eMapPropertyHolderEClass, EMAP_PROPERTY_HOLDER__PROPERTIES);
        createEReference(eMapPropertyHolderEClass, EMAP_PROPERTY_HOLDER__PERSISTENT_ENTRIES);

        iPropertyToObjectMapEClass = createEClass(IPROPERTY_TO_OBJECT_MAP);
        createEAttribute(iPropertyToObjectMapEClass, IPROPERTY_TO_OBJECT_MAP__KEY);
        createEAttribute(iPropertyToObjectMapEClass, IPROPERTY_TO_OBJECT_MAP__VALUE);

        iPropertyHolderEClass = createEClass(IPROPERTY_HOLDER);

        persistentEntryEClass = createEClass(PERSISTENT_ENTRY);
        createEAttribute(persistentEntryEClass, PERSISTENT_ENTRY__KEY);
        createEAttribute(persistentEntryEClass, PERSISTENT_ENTRY__VALUE);

        // Create data types
        iPropertyEDataType = createEDataType(IPROPERTY);
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

        // Create type parameters
        addETypeParameter(iPropertyEDataType, "T");

        // Set bounds for type parameters

        // Add supertypes to classes
        kLabeledGraphElementEClass.getESuperTypes().add(this.getKGraphElement());
        kGraphDataEClass.getESuperTypes().add(this.getEMapPropertyHolder());
        kNodeEClass.getESuperTypes().add(this.getKLabeledGraphElement());
        kEdgeEClass.getESuperTypes().add(this.getKLabeledGraphElement());
        kPortEClass.getESuperTypes().add(this.getKLabeledGraphElement());
        kLabelEClass.getESuperTypes().add(this.getKGraphElement());
        eMapPropertyHolderEClass.getESuperTypes().add(this.getIPropertyHolder());

        // Initialize classes and features; add operations and parameters
        initEClass(kGraphElementEClass, KGraphElement.class, "KGraphElement", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getKGraphElement_Data(), this.getKGraphData(), null, "data", null, 0, -1, KGraphElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        EOperation op = addEOperation(kGraphElementEClass, this.getKGraphData(), "getData", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, ecorePackage.getEClass(), "type", 0, 1, IS_UNIQUE, IS_ORDERED);

        op = addEOperation(kGraphElementEClass, null, "getData", 0, 1, IS_UNIQUE, IS_ORDERED);
        ETypeParameter t1 = addETypeParameter(op, "T");
        EGenericType g1 = createEGenericType(this.getKGraphData());
        t1.getEBounds().add(g1);
        g1 = createEGenericType(ecorePackage.getEJavaClass());
        EGenericType g2 = createEGenericType(t1);
        g1.getETypeArguments().add(g2);
        addEParameter(op, g1, "type", 0, 1, IS_UNIQUE, IS_ORDERED);
        g1 = createEGenericType(t1);
        initEOperation(op, g1);

        initEClass(kLabeledGraphElementEClass, KLabeledGraphElement.class, "KLabeledGraphElement", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getKLabeledGraphElement_Labels(), this.getKLabel(), this.getKLabel_Parent(), "labels", null, 0, -1, KLabeledGraphElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(kGraphDataEClass, KGraphData.class, "KGraphData", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(kNodeEClass, KNode.class, "KNode", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getKNode_Children(), this.getKNode(), this.getKNode_Parent(), "children", null, 0, -1, KNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getKNode_Parent(), this.getKNode(), this.getKNode_Children(), "parent", null, 0, 1, KNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getKNode_Ports(), this.getKPort(), this.getKPort_Node(), "ports", null, 0, -1, KNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getKNode_OutgoingEdges(), this.getKEdge(), this.getKEdge_Source(), "outgoingEdges", null, 0, -1, KNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getKNode_IncomingEdges(), this.getKEdge(), this.getKEdge_Target(), "incomingEdges", null, 0, -1, KNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(kEdgeEClass, KEdge.class, "KEdge", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getKEdge_Source(), this.getKNode(), this.getKNode_OutgoingEdges(), "source", null, 1, 1, KEdge.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getKEdge_Target(), this.getKNode(), this.getKNode_IncomingEdges(), "target", null, 1, 1, KEdge.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getKEdge_SourcePort(), this.getKPort(), null, "sourcePort", null, 0, 1, KEdge.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getKEdge_TargetPort(), this.getKPort(), null, "targetPort", null, 0, 1, KEdge.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(kPortEClass, KPort.class, "KPort", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getKPort_Node(), this.getKNode(), this.getKNode_Ports(), "node", null, 1, 1, KPort.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getKPort_Edges(), this.getKEdge(), null, "edges", null, 0, -1, KPort.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

        initEClass(kLabelEClass, KLabel.class, "KLabel", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getKLabel_Text(), ecorePackage.getEString(), "text", null, 0, 1, KLabel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getKLabel_Parent(), this.getKLabeledGraphElement(), this.getKLabeledGraphElement_Labels(), "parent", null, 1, 1, KLabel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(eMapPropertyHolderEClass, EMapPropertyHolder.class, "EMapPropertyHolder", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getEMapPropertyHolder_Properties(), this.getIPropertyToObjectMap(), null, "properties", null, 0, -1, EMapPropertyHolder.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getEMapPropertyHolder_PersistentEntries(), this.getPersistentEntry(), null, "persistentEntries", null, 0, -1, EMapPropertyHolder.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        addEOperation(eMapPropertyHolderEClass, null, "makePersistent", 0, 1, IS_UNIQUE, IS_ORDERED);

        initEClass(iPropertyToObjectMapEClass, Map.Entry.class, "IPropertyToObjectMap", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
        g1 = createEGenericType(this.getIProperty());
        g2 = createEGenericType();
        g1.getETypeArguments().add(g2);
        initEAttribute(getIPropertyToObjectMap_Key(), g1, "key", null, 1, 1, Map.Entry.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getIPropertyToObjectMap_Value(), ecorePackage.getEJavaObject(), "value", null, 0, 1, Map.Entry.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(iPropertyHolderEClass, IPropertyHolder.class, "IPropertyHolder", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);

        op = addEOperation(iPropertyHolderEClass, this.getIPropertyHolder(), "setProperty", 0, 1, IS_UNIQUE, IS_ORDERED);
        t1 = addETypeParameter(op, "T");
        g1 = createEGenericType(this.getIProperty());
        g2 = createEGenericType(t1);
        g1.getETypeArguments().add(g2);
        addEParameter(op, g1, "property", 1, 1, IS_UNIQUE, IS_ORDERED);
        g1 = createEGenericType(t1);
        addEParameter(op, g1, "value", 0, 1, IS_UNIQUE, IS_ORDERED);

        op = addEOperation(iPropertyHolderEClass, null, "getProperty", 0, 1, IS_UNIQUE, IS_ORDERED);
        t1 = addETypeParameter(op, "T");
        g1 = createEGenericType(this.getIProperty());
        g2 = createEGenericType(t1);
        g1.getETypeArguments().add(g2);
        addEParameter(op, g1, "property", 0, 1, IS_UNIQUE, IS_ORDERED);
        g1 = createEGenericType(t1);
        initEOperation(op, g1);

        op = addEOperation(iPropertyHolderEClass, this.getIPropertyHolder(), "copyProperties", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, this.getIPropertyHolder(), "holder", 1, 1, IS_UNIQUE, IS_ORDERED);

        op = addEOperation(iPropertyHolderEClass, null, "getAllProperties", 1, 1, IS_UNIQUE, IS_ORDERED);
        g1 = createEGenericType(ecorePackage.getEMap());
        g2 = createEGenericType(this.getIProperty());
        g1.getETypeArguments().add(g2);
        EGenericType g3 = createEGenericType();
        g2.getETypeArguments().add(g3);
        g2 = createEGenericType(ecorePackage.getEJavaObject());
        g1.getETypeArguments().add(g2);
        initEOperation(op, g1);

        initEClass(persistentEntryEClass, PersistentEntry.class, "PersistentEntry", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getPersistentEntry_Key(), ecorePackage.getEString(), "key", null, 1, 1, PersistentEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getPersistentEntry_Value(), ecorePackage.getEString(), "value", null, 0, 1, PersistentEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        // Initialize data types
        initEDataType(iPropertyEDataType, IProperty.class, "IProperty", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

        // Create resource
        createResource(eNS_URI);
    }

} //KGraphPackageImpl
