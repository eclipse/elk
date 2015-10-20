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
 * A representation of the model object '<em><b>KPort</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Each port must be assigned a containing node. A port may contain incoming
 * edges as well as outgoing edges, but usually either one or the other kind is
 * referenced. The list of edges is not opposite to the edges' source or target
 * port reference. However, the list content is automatically updated when those
 * references are set.
 * <p>
 * Since the information contained in this list is redundant, it is marked as transient,
 * i.e. it is not serialized.
 * </p>
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.elk.graph.KPort#getNode <em>Node</em>}</li>
 *   <li>{@link org.eclipse.elk.graph.KPort#getEdges <em>Edges</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.elk.graph.KGraphPackage#getKPort()
 * @model
 * @generated
 */
public interface KPort extends KLabeledGraphElement {
    /**
     * Returns the value of the '<em><b>Node</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link org.eclipse.elk.graph.KNode#getPorts <em>Ports</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Each port must be assigned a containing node. This is especially
     * important because the node is defined to be the container of the
     * port, which is relevant for many EMF features such as XML storage or
     * copying.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Node</em>' container reference.
     * @see #setNode(KNode)
     * @see org.eclipse.elk.graph.KGraphPackage#getKPort_Node()
     * @see org.eclipse.elk.graph.KNode#getPorts
     * @model opposite="ports" required="true" transient="false"
     * @generated
     */
    KNode getNode();

    /**
     * Sets the value of the '{@link org.eclipse.elk.graph.KPort#getNode <em>Node</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Node</em>' container reference.
     * @see #getNode()
     * @generated
     */
    void setNode(KNode value);

    /**
     * Returns the value of the '<em><b>Edges</b></em>' reference list.
     * The list contents are of type {@link org.eclipse.elk.graph.KEdge}.
     * <!-- begin-user-doc -->
     * This reference is not bidirectional, so adding edges to it does not
     * affect the source or target port references of the edges.<br/>
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Edges in this list may be incoming as well as outgoing with respect
     * to the containing node. The list of edges is not opposite to the edges'
     * source or target port reference. Just adding an edge to this list does
     * not imply that the source or target port reference is set, since it is
     * unclear which reference to pick. However, the list content is automatically
     * updated when one of those references is set or unset.
     * Therefore it is advisable not to modify this list directly, but to use
     * {@link KEdge#setSourcePort(KPort)} or {@link KEdge#setTargetPort(KPort)}
     * instead.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Edges</em>' reference list.
     * @see org.eclipse.elk.graph.KGraphPackage#getKPort_Edges()
     * @model transient="true" derived="true"
     * @generated
     */
    EList<KEdge> getEdges();

} // KPort
