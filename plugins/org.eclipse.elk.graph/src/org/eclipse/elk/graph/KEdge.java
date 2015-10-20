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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Edge</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * An edge must be assigned a source and a target node, but the source and target ports
 * are optional. The source and target references are opposite to the lists of outgoing and
 * incoming edges of nodes, respectively. The source and target port references are
 * not opposite to the ports' list of edges, but despite that, setting these references will
 * automatically update the edges reference of the corresponding port.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.elk.graph.KEdge#getSource <em>Source</em>}</li>
 *   <li>{@link org.eclipse.elk.graph.KEdge#getTarget <em>Target</em>}</li>
 *   <li>{@link org.eclipse.elk.graph.KEdge#getSourcePort <em>Source Port</em>}</li>
 *   <li>{@link org.eclipse.elk.graph.KEdge#getTargetPort <em>Target Port</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.elk.graph.KGraphPackage#getKEdge()
 * @model
 * @generated
 */
public interface KEdge extends KLabeledGraphElement {
    /**
     * Returns the value of the '<em><b>Source</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link org.eclipse.elk.graph.KNode#getOutgoingEdges <em>Outgoing Edges</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The source node is expected to be set for each edge. This is especially
     * important because the source node is defined to be the container of the
     * edge, which is relevant for many EMF features such as XML storage or
     * copying. The source reference is opposite to the nodes' list of outgoing
     * edges, hence those references are synchronized automatically.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Source</em>' container reference.
     * @see #setSource(KNode)
     * @see org.eclipse.elk.graph.KGraphPackage#getKEdge_Source()
     * @see org.eclipse.elk.graph.KNode#getOutgoingEdges
     * @model opposite="outgoingEdges" required="true" transient="false"
     * @generated
     */
    KNode getSource();

    /**
     * Sets the value of the '{@link org.eclipse.elk.graph.KEdge#getSource <em>Source</em>}' container reference.
     * <!-- begin-user-doc -->
     * This automatically adds the edge to the the source node's list of outgoing edges.
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Source</em>' container reference.
     * @see #getSource()
     * @generated
     */
    void setSource(KNode value);

    /**
     * Returns the value of the '<em><b>Target</b></em>' reference.
     * It is bidirectional and its opposite is '{@link org.eclipse.elk.graph.KNode#getIncomingEdges <em>Incoming Edges</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The target node is expected to be set for each edge.
     * The target reference is opposite to the nodes' list of incoming
     * edges, hence those references are synchronized automatically.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Target</em>' reference.
     * @see #setTarget(KNode)
     * @see org.eclipse.elk.graph.KGraphPackage#getKEdge_Target()
     * @see org.eclipse.elk.graph.KNode#getIncomingEdges
     * @model opposite="incomingEdges" required="true"
     * @generated
     */
    KNode getTarget();

    /**
     * Sets the value of the '{@link org.eclipse.elk.graph.KEdge#getTarget <em>Target</em>}' reference.
     * <!-- begin-user-doc -->
     * This automatically adds the edge to the target node's list of incoming edges.
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Target</em>' reference.
     * @see #getTarget()
     * @generated
     */
    void setTarget(KNode value);

    /**
     * Returns the value of the '<em><b>Source Port</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * This reference is optional, as a node may have no ports.
     * The reference is not opposite to the list of edges stored by ports,
     * but setting it automatically updates that list.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Source Port</em>' reference.
     * @see #setSourcePort(KPort)
     * @see org.eclipse.elk.graph.KGraphPackage#getKEdge_SourcePort()
     * @model
     * @generated
     */
    KPort getSourcePort();

    /**
     * Sets the value of the '{@link org.eclipse.elk.graph.KEdge#getSourcePort <em>Source Port</em>}' reference.
     * <!-- begin-user-doc -->
     * This reference is not bidirectional, but setting it automatically adds the edge to the
     * {@link KPort#getEdges() <em>Edges</em>} reference of the given port. If the reference was already
     * set before, the edge is automatically removed from the old port's list of edges.
     * <p>
     * The node related to the source port must be equal to the source node of this edge.
     * </p>
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Source Port</em>' reference.
     * @see #getSourcePort()
     * @generated
     */
    void setSourcePort(KPort value);

    /**
     * Returns the value of the '<em><b>Target Port</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * This reference is optional, as a node may have no ports.
     * The reference is not opposite to the list of edges stored by ports,
     * but setting it automatically updates that list.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Target Port</em>' reference.
     * @see #setTargetPort(KPort)
     * @see org.eclipse.elk.graph.KGraphPackage#getKEdge_TargetPort()
     * @model
     * @generated
     */
    KPort getTargetPort();

    /**
     * Sets the value of the '{@link org.eclipse.elk.graph.KEdge#getTargetPort <em>Target Port</em>}' reference.
     * <!-- begin-user-doc -->
     * This reference is not bidirectional, but setting it automatically adds the edge to the
     * {@link KPort#getEdges() <em>Edges</em>} reference of the given port. If the reference was already
     * set before, the edge is automatically removed from the old port's list of edges.
     * <p>
     * The node related to the target port must be equal to the target node of this edge.
     * </p>
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Target Port</em>' reference.
     * @see #getTargetPort()
     * @generated
     */
    void setTargetPort(KPort value);

} // KEdge
