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

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Node</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * All nodes except exactly one node must have an assigned parent node. The node
 * without parent is the top node of the graph and represents the graph itself. Each
 * node must be assigned a label.
 * <p>The parent-child relationship of nodes can be used to describe hierarchy in
 * nested graphs.</p>
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.elk.graph.KNode#getChildren <em>Children</em>}</li>
 *   <li>{@link org.eclipse.elk.graph.KNode#getParent <em>Parent</em>}</li>
 *   <li>{@link org.eclipse.elk.graph.KNode#getPorts <em>Ports</em>}</li>
 *   <li>{@link org.eclipse.elk.graph.KNode#getOutgoingEdges <em>Outgoing Edges</em>}</li>
 *   <li>{@link org.eclipse.elk.graph.KNode#getIncomingEdges <em>Incoming Edges</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.elk.graph.KGraphPackage#getKNode()
 * @model
 * @generated
 */
public interface KNode extends KLabeledGraphElement {
    /**
     * Returns the value of the '<em><b>Children</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.elk.graph.KNode}.
     * It is bidirectional and its opposite is '{@link org.eclipse.elk.graph.KNode#getParent <em>Parent</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The children together with their edges form a sub-graph that is contained
     * in this parent node.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Children</em>' containment reference list.
     * @see org.eclipse.elk.graph.KGraphPackage#getKNode_Children()
     * @see org.eclipse.elk.graph.KNode#getParent
     * @model opposite="parent" containment="true"
     * @generated
     */
    EList<KNode> getChildren();

    /**
     * Returns the value of the '<em><b>Parent</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link org.eclipse.elk.graph.KNode#getChildren <em>Children</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The parent node must be {@code null} if and only if this is the top node of
     * the graph structure.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Parent</em>' container reference.
     * @see #setParent(KNode)
     * @see org.eclipse.elk.graph.KGraphPackage#getKNode_Parent()
     * @see org.eclipse.elk.graph.KNode#getChildren
     * @model opposite="children" transient="false"
     * @generated
     */
    KNode getParent();

    /**
     * Sets the value of the '{@link org.eclipse.elk.graph.KNode#getParent <em>Parent</em>}' container reference.
     * <!-- begin-user-doc -->
     * This automatically adds the node to the parent's list of children.
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Parent</em>' container reference.
     * @see #getParent()
     * @generated
     */
    void setParent(KNode value);

    /**
     * Returns the value of the '<em><b>Ports</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.elk.graph.KPort}.
     * It is bidirectional and its opposite is '{@link org.eclipse.elk.graph.KPort#getNode <em>Node</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Each node may have an arbitrary number of ports. Edges may or may not be
     * connected to ports.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Ports</em>' containment reference list.
     * @see org.eclipse.elk.graph.KGraphPackage#getKNode_Ports()
     * @see org.eclipse.elk.graph.KPort#getNode
     * @model opposite="node" containment="true"
     * @generated
     */
    EList<KPort> getPorts();

    /**
     * Returns the value of the '<em><b>Outgoing Edges</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.elk.graph.KEdge}.
     * It is bidirectional and its opposite is '{@link org.eclipse.elk.graph.KEdge#getSource <em>Source</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Outgoing Edges</em>' containment reference list.
     * @see org.eclipse.elk.graph.KGraphPackage#getKNode_OutgoingEdges()
     * @see org.eclipse.elk.graph.KEdge#getSource
     * @model opposite="source" containment="true"
     * @generated
     */
    EList<KEdge> getOutgoingEdges();

    /**
     * Returns the value of the '<em><b>Incoming Edges</b></em>' reference list.
     * The list contents are of type {@link org.eclipse.elk.graph.KEdge}.
     * It is bidirectional and its opposite is '{@link org.eclipse.elk.graph.KEdge#getTarget <em>Target</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Incoming Edges</em>' reference list.
     * @see org.eclipse.elk.graph.KGraphPackage#getKNode_IncomingEdges()
     * @see org.eclipse.elk.graph.KEdge#getTarget
     * @model opposite="target"
     * @generated
     */
    EList<KEdge> getIncomingEdges();

} // KNode
