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
 * A representation of the model object '<em><b>KLabeled Graph Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Labeled graph elements contain an arbitrary number of labels.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.elk.graph.KLabeledGraphElement#getLabels <em>Labels</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.elk.graph.KGraphPackage#getKLabeledGraphElement()
 * @model abstract="true"
 * @generated
 */
public interface KLabeledGraphElement extends KGraphElement {
    /**
     * Returns the value of the '<em><b>Labels</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.elk.graph.KLabel}.
     * It is bidirectional and its opposite is '{@link org.eclipse.elk.graph.KLabel#getParent <em>Parent</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * An edge may have multiple labels.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Labels</em>' containment reference list.
     * @see org.eclipse.elk.graph.KGraphPackage#getKLabeledGraphElement_Labels()
     * @see org.eclipse.elk.graph.KLabel#getParent
     * @model opposite="parent" containment="true"
     * @generated
     */
    EList<KLabel> getLabels();

} // KLabeledGraphElement
