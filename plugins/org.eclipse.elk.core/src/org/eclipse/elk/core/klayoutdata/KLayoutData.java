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
package org.eclipse.elk.core.klayoutdata;

import org.eclipse.elk.graph.KGraphData;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>KLayout Data</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Common interface for shape layouts and edge layouts. Shape layouts are
 * used by nodes, ports, and labels, while edge layouts are used by edges.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.elk.core.klayoutdata.KLayoutDataPackage#getKLayoutData()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface KLayoutData extends KGraphData {

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Whether the concrete layout data have been modified since the layout data instance
     * was created or the modification flag was reset. For shape layouts this refers to the
     * position or size, and for edge layouts it refers to the source point, target point, or
     * bend points.
     * <!-- end-model-doc -->
     * @model kind="operation" required="true"
     * @generated
     */
    boolean isModified();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Reset the modification flag to {@code false}. Layout algorithms should not do this.
     * <!-- end-model-doc -->
     * @model
     * @generated
     */
    void resetModificationFlag();
} // KLayoutData
