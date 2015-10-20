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
package org.eclipse.elk.graph;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.elk.graph.KGraphFactory
 * @model kind="package"
 * @generated
 */
public interface KGraphPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "kgraph";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://elk.eclipse.org/KGraph";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "kgraph";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    KGraphPackage eINSTANCE = org.eclipse.elk.graph.impl.KGraphPackageImpl.init();

    /**
     * The meta object id for the '{@link org.eclipse.elk.graph.impl.KGraphElementImpl <em>Element</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.graph.impl.KGraphElementImpl
     * @see org.eclipse.elk.graph.impl.KGraphPackageImpl#getKGraphElement()
     * @generated
     */
    int KGRAPH_ELEMENT = 0;

    /**
     * The feature id for the '<em><b>Data</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KGRAPH_ELEMENT__DATA = 0;

    /**
     * The number of structural features of the '<em>Element</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KGRAPH_ELEMENT_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link org.eclipse.elk.graph.impl.KLabeledGraphElementImpl <em>KLabeled Graph Element</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.graph.impl.KLabeledGraphElementImpl
     * @see org.eclipse.elk.graph.impl.KGraphPackageImpl#getKLabeledGraphElement()
     * @generated
     */
    int KLABELED_GRAPH_ELEMENT = 1;

    /**
     * The feature id for the '<em><b>Data</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KLABELED_GRAPH_ELEMENT__DATA = KGRAPH_ELEMENT__DATA;

    /**
     * The feature id for the '<em><b>Labels</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KLABELED_GRAPH_ELEMENT__LABELS = KGRAPH_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>KLabeled Graph Element</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KLABELED_GRAPH_ELEMENT_FEATURE_COUNT = KGRAPH_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.eclipse.elk.graph.properties.IPropertyHolder <em>IProperty Holder</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.graph.properties.IPropertyHolder
     * @see org.eclipse.elk.graph.impl.KGraphPackageImpl#getIPropertyHolder()
     * @generated
     */
    int IPROPERTY_HOLDER = 9;

    /**
     * The number of structural features of the '<em>IProperty Holder</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int IPROPERTY_HOLDER_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.elk.graph.impl.EMapPropertyHolderImpl <em>EMap Property Holder</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.graph.impl.EMapPropertyHolderImpl
     * @see org.eclipse.elk.graph.impl.KGraphPackageImpl#getEMapPropertyHolder()
     * @generated
     */
    int EMAP_PROPERTY_HOLDER = 7;

    /**
     * The feature id for the '<em><b>Properties</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMAP_PROPERTY_HOLDER__PROPERTIES = IPROPERTY_HOLDER_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Persistent Entries</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMAP_PROPERTY_HOLDER__PERSISTENT_ENTRIES = IPROPERTY_HOLDER_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>EMap Property Holder</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMAP_PROPERTY_HOLDER_FEATURE_COUNT = IPROPERTY_HOLDER_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link org.eclipse.elk.graph.impl.KGraphDataImpl <em>Data</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.graph.impl.KGraphDataImpl
     * @see org.eclipse.elk.graph.impl.KGraphPackageImpl#getKGraphData()
     * @generated
     */
    int KGRAPH_DATA = 2;

    /**
     * The feature id for the '<em><b>Properties</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KGRAPH_DATA__PROPERTIES = EMAP_PROPERTY_HOLDER__PROPERTIES;

    /**
     * The feature id for the '<em><b>Persistent Entries</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KGRAPH_DATA__PERSISTENT_ENTRIES = EMAP_PROPERTY_HOLDER__PERSISTENT_ENTRIES;

    /**
     * The number of structural features of the '<em>Data</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KGRAPH_DATA_FEATURE_COUNT = EMAP_PROPERTY_HOLDER_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link org.eclipse.elk.graph.impl.KNodeImpl <em>KNode</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.graph.impl.KNodeImpl
     * @see org.eclipse.elk.graph.impl.KGraphPackageImpl#getKNode()
     * @generated
     */
    int KNODE = 3;

    /**
     * The feature id for the '<em><b>Data</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KNODE__DATA = KLABELED_GRAPH_ELEMENT__DATA;

    /**
     * The feature id for the '<em><b>Labels</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KNODE__LABELS = KLABELED_GRAPH_ELEMENT__LABELS;

    /**
     * The feature id for the '<em><b>Children</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KNODE__CHILDREN = KLABELED_GRAPH_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Parent</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KNODE__PARENT = KLABELED_GRAPH_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Ports</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KNODE__PORTS = KLABELED_GRAPH_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Outgoing Edges</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KNODE__OUTGOING_EDGES = KLABELED_GRAPH_ELEMENT_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Incoming Edges</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KNODE__INCOMING_EDGES = KLABELED_GRAPH_ELEMENT_FEATURE_COUNT + 4;

    /**
     * The number of structural features of the '<em>KNode</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KNODE_FEATURE_COUNT = KLABELED_GRAPH_ELEMENT_FEATURE_COUNT + 5;

    /**
     * The meta object id for the '{@link org.eclipse.elk.graph.impl.KEdgeImpl <em>KEdge</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.graph.impl.KEdgeImpl
     * @see org.eclipse.elk.graph.impl.KGraphPackageImpl#getKEdge()
     * @generated
     */
    int KEDGE = 4;

    /**
     * The feature id for the '<em><b>Data</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KEDGE__DATA = KLABELED_GRAPH_ELEMENT__DATA;

    /**
     * The feature id for the '<em><b>Labels</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KEDGE__LABELS = KLABELED_GRAPH_ELEMENT__LABELS;

    /**
     * The feature id for the '<em><b>Source</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KEDGE__SOURCE = KLABELED_GRAPH_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Target</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KEDGE__TARGET = KLABELED_GRAPH_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Source Port</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KEDGE__SOURCE_PORT = KLABELED_GRAPH_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Target Port</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KEDGE__TARGET_PORT = KLABELED_GRAPH_ELEMENT_FEATURE_COUNT + 3;

    /**
     * The number of structural features of the '<em>KEdge</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KEDGE_FEATURE_COUNT = KLABELED_GRAPH_ELEMENT_FEATURE_COUNT + 4;

    /**
     * The meta object id for the '{@link org.eclipse.elk.graph.impl.KPortImpl <em>KPort</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.graph.impl.KPortImpl
     * @see org.eclipse.elk.graph.impl.KGraphPackageImpl#getKPort()
     * @generated
     */
    int KPORT = 5;

    /**
     * The feature id for the '<em><b>Data</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KPORT__DATA = KLABELED_GRAPH_ELEMENT__DATA;

    /**
     * The feature id for the '<em><b>Labels</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KPORT__LABELS = KLABELED_GRAPH_ELEMENT__LABELS;

    /**
     * The feature id for the '<em><b>Node</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KPORT__NODE = KLABELED_GRAPH_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Edges</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KPORT__EDGES = KLABELED_GRAPH_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>KPort</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KPORT_FEATURE_COUNT = KLABELED_GRAPH_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link org.eclipse.elk.graph.impl.KLabelImpl <em>KLabel</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.graph.impl.KLabelImpl
     * @see org.eclipse.elk.graph.impl.KGraphPackageImpl#getKLabel()
     * @generated
     */
    int KLABEL = 6;

    /**
     * The feature id for the '<em><b>Data</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KLABEL__DATA = KGRAPH_ELEMENT__DATA;

    /**
     * The feature id for the '<em><b>Text</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KLABEL__TEXT = KGRAPH_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Parent</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KLABEL__PARENT = KGRAPH_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>KLabel</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KLABEL_FEATURE_COUNT = KGRAPH_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link org.eclipse.elk.graph.impl.IPropertyToObjectMapImpl <em>IProperty To Object Map</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.graph.impl.IPropertyToObjectMapImpl
     * @see org.eclipse.elk.graph.impl.KGraphPackageImpl#getIPropertyToObjectMap()
     * @generated
     */
    int IPROPERTY_TO_OBJECT_MAP = 8;

    /**
     * The feature id for the '<em><b>Key</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int IPROPERTY_TO_OBJECT_MAP__KEY = 0;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int IPROPERTY_TO_OBJECT_MAP__VALUE = 1;

    /**
     * The number of structural features of the '<em>IProperty To Object Map</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int IPROPERTY_TO_OBJECT_MAP_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '<em>IProperty</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.graph.properties.IProperty
     * @see org.eclipse.elk.graph.impl.KGraphPackageImpl#getIProperty()
     * @generated
     */
    int IPROPERTY = 11;

    /**
     * The meta object id for the '{@link org.eclipse.elk.graph.impl.PersistentEntryImpl <em>Persistent Entry</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.graph.impl.PersistentEntryImpl
     * @see org.eclipse.elk.graph.impl.KGraphPackageImpl#getPersistentEntry()
     * @generated
     */
    int PERSISTENT_ENTRY = 10;

    /**
     * The feature id for the '<em><b>Key</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PERSISTENT_ENTRY__KEY = 0;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PERSISTENT_ENTRY__VALUE = 1;

    /**
     * The number of structural features of the '<em>Persistent Entry</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PERSISTENT_ENTRY_FEATURE_COUNT = 2;

    /**
     * Returns the meta object for class '{@link org.eclipse.elk.graph.KGraphElement <em>Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Element</em>'.
     * @see org.eclipse.elk.graph.KGraphElement
     * @generated
     */
    EClass getKGraphElement();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.elk.graph.KGraphElement#getData <em>Data</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Data</em>'.
     * @see org.eclipse.elk.graph.KGraphElement#getData()
     * @see #getKGraphElement()
     * @generated
     */
    EReference getKGraphElement_Data();

    /**
     * Returns the meta object for class '{@link org.eclipse.elk.graph.KLabeledGraphElement <em>KLabeled Graph Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>KLabeled Graph Element</em>'.
     * @see org.eclipse.elk.graph.KLabeledGraphElement
     * @generated
     */
    EClass getKLabeledGraphElement();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.elk.graph.KLabeledGraphElement#getLabels <em>Labels</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Labels</em>'.
     * @see org.eclipse.elk.graph.KLabeledGraphElement#getLabels()
     * @see #getKLabeledGraphElement()
     * @generated
     */
    EReference getKLabeledGraphElement_Labels();

    /**
     * Returns the meta object for class '{@link org.eclipse.elk.graph.KGraphData <em>Data</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Data</em>'.
     * @see org.eclipse.elk.graph.KGraphData
     * @generated
     */
    EClass getKGraphData();

    /**
     * Returns the meta object for class '{@link org.eclipse.elk.graph.KNode <em>KNode</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>KNode</em>'.
     * @see org.eclipse.elk.graph.KNode
     * @generated
     */
    EClass getKNode();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.elk.graph.KNode#getChildren <em>Children</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Children</em>'.
     * @see org.eclipse.elk.graph.KNode#getChildren()
     * @see #getKNode()
     * @generated
     */
    EReference getKNode_Children();

    /**
     * Returns the meta object for the container reference '{@link org.eclipse.elk.graph.KNode#getParent <em>Parent</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Parent</em>'.
     * @see org.eclipse.elk.graph.KNode#getParent()
     * @see #getKNode()
     * @generated
     */
    EReference getKNode_Parent();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.elk.graph.KNode#getPorts <em>Ports</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Ports</em>'.
     * @see org.eclipse.elk.graph.KNode#getPorts()
     * @see #getKNode()
     * @generated
     */
    EReference getKNode_Ports();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.elk.graph.KNode#getOutgoingEdges <em>Outgoing Edges</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Outgoing Edges</em>'.
     * @see org.eclipse.elk.graph.KNode#getOutgoingEdges()
     * @see #getKNode()
     * @generated
     */
    EReference getKNode_OutgoingEdges();

    /**
     * Returns the meta object for the reference list '{@link org.eclipse.elk.graph.KNode#getIncomingEdges <em>Incoming Edges</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Incoming Edges</em>'.
     * @see org.eclipse.elk.graph.KNode#getIncomingEdges()
     * @see #getKNode()
     * @generated
     */
    EReference getKNode_IncomingEdges();

    /**
     * Returns the meta object for class '{@link org.eclipse.elk.graph.KEdge <em>KEdge</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>KEdge</em>'.
     * @see org.eclipse.elk.graph.KEdge
     * @generated
     */
    EClass getKEdge();

    /**
     * Returns the meta object for the container reference '{@link org.eclipse.elk.graph.KEdge#getSource <em>Source</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Source</em>'.
     * @see org.eclipse.elk.graph.KEdge#getSource()
     * @see #getKEdge()
     * @generated
     */
    EReference getKEdge_Source();

    /**
     * Returns the meta object for the reference '{@link org.eclipse.elk.graph.KEdge#getTarget <em>Target</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Target</em>'.
     * @see org.eclipse.elk.graph.KEdge#getTarget()
     * @see #getKEdge()
     * @generated
     */
    EReference getKEdge_Target();

    /**
     * Returns the meta object for the reference '{@link org.eclipse.elk.graph.KEdge#getSourcePort <em>Source Port</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Source Port</em>'.
     * @see org.eclipse.elk.graph.KEdge#getSourcePort()
     * @see #getKEdge()
     * @generated
     */
    EReference getKEdge_SourcePort();

    /**
     * Returns the meta object for the reference '{@link org.eclipse.elk.graph.KEdge#getTargetPort <em>Target Port</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Target Port</em>'.
     * @see org.eclipse.elk.graph.KEdge#getTargetPort()
     * @see #getKEdge()
     * @generated
     */
    EReference getKEdge_TargetPort();

    /**
     * Returns the meta object for class '{@link org.eclipse.elk.graph.KPort <em>KPort</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>KPort</em>'.
     * @see org.eclipse.elk.graph.KPort
     * @generated
     */
    EClass getKPort();

    /**
     * Returns the meta object for the container reference '{@link org.eclipse.elk.graph.KPort#getNode <em>Node</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Node</em>'.
     * @see org.eclipse.elk.graph.KPort#getNode()
     * @see #getKPort()
     * @generated
     */
    EReference getKPort_Node();

    /**
     * Returns the meta object for the reference list '{@link org.eclipse.elk.graph.KPort#getEdges <em>Edges</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Edges</em>'.
     * @see org.eclipse.elk.graph.KPort#getEdges()
     * @see #getKPort()
     * @generated
     */
    EReference getKPort_Edges();

    /**
     * Returns the meta object for class '{@link org.eclipse.elk.graph.KLabel <em>KLabel</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>KLabel</em>'.
     * @see org.eclipse.elk.graph.KLabel
     * @generated
     */
    EClass getKLabel();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.elk.graph.KLabel#getText <em>Text</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Text</em>'.
     * @see org.eclipse.elk.graph.KLabel#getText()
     * @see #getKLabel()
     * @generated
     */
    EAttribute getKLabel_Text();

    /**
     * Returns the meta object for the container reference '{@link org.eclipse.elk.graph.KLabel#getParent <em>Parent</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Parent</em>'.
     * @see org.eclipse.elk.graph.KLabel#getParent()
     * @see #getKLabel()
     * @generated
     */
    EReference getKLabel_Parent();

    /**
     * Returns the meta object for class '{@link org.eclipse.elk.graph.EMapPropertyHolder <em>EMap Property Holder</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>EMap Property Holder</em>'.
     * @see org.eclipse.elk.graph.EMapPropertyHolder
     * @generated
     */
    EClass getEMapPropertyHolder();

    /**
     * Returns the meta object for the map '{@link org.eclipse.elk.graph.EMapPropertyHolder#getProperties <em>Properties</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the map '<em>Properties</em>'.
     * @see org.eclipse.elk.graph.EMapPropertyHolder#getProperties()
     * @see #getEMapPropertyHolder()
     * @generated
     */
    EReference getEMapPropertyHolder_Properties();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.elk.graph.EMapPropertyHolder#getPersistentEntries <em>Persistent Entries</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Persistent Entries</em>'.
     * @see org.eclipse.elk.graph.EMapPropertyHolder#getPersistentEntries()
     * @see #getEMapPropertyHolder()
     * @generated
     */
    EReference getEMapPropertyHolder_PersistentEntries();

    /**
     * Returns the meta object for class '{@link java.util.Map.Entry <em>IProperty To Object Map</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>IProperty To Object Map</em>'.
     * @see java.util.Map.Entry
     * @model keyDataType="org.eclipse.elk.graph.properties.IProperty<?>" keyRequired="true" keyTransient="true"
     *        valueDataType="org.eclipse.emf.ecore.EJavaObject" valueTransient="true"
     * @generated
     */
    EClass getIPropertyToObjectMap();

    /**
     * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Key</em>'.
     * @see java.util.Map.Entry
     * @see #getIPropertyToObjectMap()
     * @generated
     */
    EAttribute getIPropertyToObjectMap_Key();

    /**
     * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Value</em>'.
     * @see java.util.Map.Entry
     * @see #getIPropertyToObjectMap()
     * @generated
     */
    EAttribute getIPropertyToObjectMap_Value();

    /**
     * Returns the meta object for data type '{@link org.eclipse.elk.graph.properties.IProperty <em>IProperty</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>IProperty</em>'.
     * @see org.eclipse.elk.graph.properties.IProperty
     * @model instanceClass="org.eclipse.elk.graph.properties.IProperty" typeParameters="T"
     * @generated
     */
    EDataType getIProperty();

    /**
     * Returns the meta object for class '{@link org.eclipse.elk.graph.properties.IPropertyHolder <em>IProperty Holder</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>IProperty Holder</em>'.
     * @see org.eclipse.elk.graph.properties.IPropertyHolder
     * @model instanceClass="org.eclipse.elk.graph.properties.IPropertyHolder"
     * @generated
     */
    EClass getIPropertyHolder();

    /**
     * Returns the meta object for class '{@link org.eclipse.elk.graph.PersistentEntry <em>Persistent Entry</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Persistent Entry</em>'.
     * @see org.eclipse.elk.graph.PersistentEntry
     * @generated
     */
    EClass getPersistentEntry();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.elk.graph.PersistentEntry#getKey <em>Key</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Key</em>'.
     * @see org.eclipse.elk.graph.PersistentEntry#getKey()
     * @see #getPersistentEntry()
     * @generated
     */
    EAttribute getPersistentEntry_Key();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.elk.graph.PersistentEntry#getValue <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Value</em>'.
     * @see org.eclipse.elk.graph.PersistentEntry#getValue()
     * @see #getPersistentEntry()
     * @generated
     */
    EAttribute getPersistentEntry_Value();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    KGraphFactory getKGraphFactory();

    /**
     * <!-- begin-user-doc -->
     * Defines literals for the meta objects that represent
     * <ul>
     *   <li>each class,</li>
     *   <li>each feature of each class,</li>
     *   <li>each enum,</li>
     *   <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     * @generated
     */
    interface Literals {
        /**
         * The meta object literal for the '{@link org.eclipse.elk.graph.impl.KGraphElementImpl <em>Element</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.elk.graph.impl.KGraphElementImpl
         * @see org.eclipse.elk.graph.impl.KGraphPackageImpl#getKGraphElement()
         * @generated
         */
        EClass KGRAPH_ELEMENT = eINSTANCE.getKGraphElement();

        /**
         * The meta object literal for the '<em><b>Data</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference KGRAPH_ELEMENT__DATA = eINSTANCE.getKGraphElement_Data();

        /**
         * The meta object literal for the '{@link org.eclipse.elk.graph.impl.KLabeledGraphElementImpl <em>KLabeled Graph Element</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.elk.graph.impl.KLabeledGraphElementImpl
         * @see org.eclipse.elk.graph.impl.KGraphPackageImpl#getKLabeledGraphElement()
         * @generated
         */
        EClass KLABELED_GRAPH_ELEMENT = eINSTANCE.getKLabeledGraphElement();

        /**
         * The meta object literal for the '<em><b>Labels</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference KLABELED_GRAPH_ELEMENT__LABELS = eINSTANCE.getKLabeledGraphElement_Labels();

        /**
         * The meta object literal for the '{@link org.eclipse.elk.graph.impl.KGraphDataImpl <em>Data</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.elk.graph.impl.KGraphDataImpl
         * @see org.eclipse.elk.graph.impl.KGraphPackageImpl#getKGraphData()
         * @generated
         */
        EClass KGRAPH_DATA = eINSTANCE.getKGraphData();

        /**
         * The meta object literal for the '{@link org.eclipse.elk.graph.impl.KNodeImpl <em>KNode</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.elk.graph.impl.KNodeImpl
         * @see org.eclipse.elk.graph.impl.KGraphPackageImpl#getKNode()
         * @generated
         */
        EClass KNODE = eINSTANCE.getKNode();

        /**
         * The meta object literal for the '<em><b>Children</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference KNODE__CHILDREN = eINSTANCE.getKNode_Children();

        /**
         * The meta object literal for the '<em><b>Parent</b></em>' container reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference KNODE__PARENT = eINSTANCE.getKNode_Parent();

        /**
         * The meta object literal for the '<em><b>Ports</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference KNODE__PORTS = eINSTANCE.getKNode_Ports();

        /**
         * The meta object literal for the '<em><b>Outgoing Edges</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference KNODE__OUTGOING_EDGES = eINSTANCE.getKNode_OutgoingEdges();

        /**
         * The meta object literal for the '<em><b>Incoming Edges</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference KNODE__INCOMING_EDGES = eINSTANCE.getKNode_IncomingEdges();

        /**
         * The meta object literal for the '{@link org.eclipse.elk.graph.impl.KEdgeImpl <em>KEdge</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.elk.graph.impl.KEdgeImpl
         * @see org.eclipse.elk.graph.impl.KGraphPackageImpl#getKEdge()
         * @generated
         */
        EClass KEDGE = eINSTANCE.getKEdge();

        /**
         * The meta object literal for the '<em><b>Source</b></em>' container reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference KEDGE__SOURCE = eINSTANCE.getKEdge_Source();

        /**
         * The meta object literal for the '<em><b>Target</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference KEDGE__TARGET = eINSTANCE.getKEdge_Target();

        /**
         * The meta object literal for the '<em><b>Source Port</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference KEDGE__SOURCE_PORT = eINSTANCE.getKEdge_SourcePort();

        /**
         * The meta object literal for the '<em><b>Target Port</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference KEDGE__TARGET_PORT = eINSTANCE.getKEdge_TargetPort();

        /**
         * The meta object literal for the '{@link org.eclipse.elk.graph.impl.KPortImpl <em>KPort</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.elk.graph.impl.KPortImpl
         * @see org.eclipse.elk.graph.impl.KGraphPackageImpl#getKPort()
         * @generated
         */
        EClass KPORT = eINSTANCE.getKPort();

        /**
         * The meta object literal for the '<em><b>Node</b></em>' container reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference KPORT__NODE = eINSTANCE.getKPort_Node();

        /**
         * The meta object literal for the '<em><b>Edges</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference KPORT__EDGES = eINSTANCE.getKPort_Edges();

        /**
         * The meta object literal for the '{@link org.eclipse.elk.graph.impl.KLabelImpl <em>KLabel</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.elk.graph.impl.KLabelImpl
         * @see org.eclipse.elk.graph.impl.KGraphPackageImpl#getKLabel()
         * @generated
         */
        EClass KLABEL = eINSTANCE.getKLabel();

        /**
         * The meta object literal for the '<em><b>Text</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute KLABEL__TEXT = eINSTANCE.getKLabel_Text();

        /**
         * The meta object literal for the '<em><b>Parent</b></em>' container reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference KLABEL__PARENT = eINSTANCE.getKLabel_Parent();

        /**
         * The meta object literal for the '{@link org.eclipse.elk.graph.impl.EMapPropertyHolderImpl <em>EMap Property Holder</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.elk.graph.impl.EMapPropertyHolderImpl
         * @see org.eclipse.elk.graph.impl.KGraphPackageImpl#getEMapPropertyHolder()
         * @generated
         */
        EClass EMAP_PROPERTY_HOLDER = eINSTANCE.getEMapPropertyHolder();

        /**
         * The meta object literal for the '<em><b>Properties</b></em>' map feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference EMAP_PROPERTY_HOLDER__PROPERTIES = eINSTANCE.getEMapPropertyHolder_Properties();

        /**
         * The meta object literal for the '<em><b>Persistent Entries</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference EMAP_PROPERTY_HOLDER__PERSISTENT_ENTRIES = eINSTANCE.getEMapPropertyHolder_PersistentEntries();

        /**
         * The meta object literal for the '{@link org.eclipse.elk.graph.impl.IPropertyToObjectMapImpl <em>IProperty To Object Map</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.elk.graph.impl.IPropertyToObjectMapImpl
         * @see org.eclipse.elk.graph.impl.KGraphPackageImpl#getIPropertyToObjectMap()
         * @generated
         */
        EClass IPROPERTY_TO_OBJECT_MAP = eINSTANCE.getIPropertyToObjectMap();

        /**
         * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute IPROPERTY_TO_OBJECT_MAP__KEY = eINSTANCE.getIPropertyToObjectMap_Key();

        /**
         * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute IPROPERTY_TO_OBJECT_MAP__VALUE = eINSTANCE.getIPropertyToObjectMap_Value();

        /**
         * The meta object literal for the '<em>IProperty</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.elk.graph.properties.IProperty
         * @see org.eclipse.elk.graph.impl.KGraphPackageImpl#getIProperty()
         * @generated
         */
        EDataType IPROPERTY = eINSTANCE.getIProperty();

        /**
         * The meta object literal for the '{@link org.eclipse.elk.graph.properties.IPropertyHolder <em>IProperty Holder</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.elk.graph.properties.IPropertyHolder
         * @see org.eclipse.elk.graph.impl.KGraphPackageImpl#getIPropertyHolder()
         * @generated
         */
        EClass IPROPERTY_HOLDER = eINSTANCE.getIPropertyHolder();

        /**
         * The meta object literal for the '{@link org.eclipse.elk.graph.impl.PersistentEntryImpl <em>Persistent Entry</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.elk.graph.impl.PersistentEntryImpl
         * @see org.eclipse.elk.graph.impl.KGraphPackageImpl#getPersistentEntry()
         * @generated
         */
        EClass PERSISTENT_ENTRY = eINSTANCE.getPersistentEntry();

        /**
         * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute PERSISTENT_ENTRY__KEY = eINSTANCE.getPersistentEntry_Key();

        /**
         * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute PERSISTENT_ENTRY__VALUE = eINSTANCE.getPersistentEntry_Value();

    }

} //KGraphPackage
