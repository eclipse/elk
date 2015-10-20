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

import org.eclipse.elk.graph.KGraphPackage;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
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
 * @see org.eclipse.elk.core.klayoutdata.KLayoutDataFactory
 * @model kind="package"
 * @generated
 */
public interface KLayoutDataPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "klayoutdata";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://elk.eclipse.org/KLayoutData";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "klayoutdata";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    KLayoutDataPackage eINSTANCE = org.eclipse.elk.core.klayoutdata.impl.KLayoutDataPackageImpl.init();

    /**
     * The meta object id for the '{@link org.eclipse.elk.core.klayoutdata.KLayoutData <em>KLayout Data</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.core.klayoutdata.KLayoutData
     * @see org.eclipse.elk.core.klayoutdata.impl.KLayoutDataPackageImpl#getKLayoutData()
     * @generated
     */
    int KLAYOUT_DATA = 2;

    /**
     * The feature id for the '<em><b>Properties</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KLAYOUT_DATA__PROPERTIES = KGraphPackage.KGRAPH_DATA__PROPERTIES;

    /**
     * The feature id for the '<em><b>Persistent Entries</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KLAYOUT_DATA__PERSISTENT_ENTRIES = KGraphPackage.KGRAPH_DATA__PERSISTENT_ENTRIES;

    /**
     * The number of structural features of the '<em>KLayout Data</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KLAYOUT_DATA_FEATURE_COUNT = KGraphPackage.KGRAPH_DATA_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link org.eclipse.elk.core.klayoutdata.impl.KShapeLayoutImpl <em>KShape Layout</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.core.klayoutdata.impl.KShapeLayoutImpl
     * @see org.eclipse.elk.core.klayoutdata.impl.KLayoutDataPackageImpl#getKShapeLayout()
     * @generated
     */
    int KSHAPE_LAYOUT = 0;

    /**
     * The feature id for the '<em><b>Properties</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KSHAPE_LAYOUT__PROPERTIES = KLAYOUT_DATA__PROPERTIES;

    /**
     * The feature id for the '<em><b>Persistent Entries</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KSHAPE_LAYOUT__PERSISTENT_ENTRIES = KLAYOUT_DATA__PERSISTENT_ENTRIES;

    /**
     * The feature id for the '<em><b>Xpos</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KSHAPE_LAYOUT__XPOS = KLAYOUT_DATA_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Ypos</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KSHAPE_LAYOUT__YPOS = KLAYOUT_DATA_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Width</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KSHAPE_LAYOUT__WIDTH = KLAYOUT_DATA_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Height</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KSHAPE_LAYOUT__HEIGHT = KLAYOUT_DATA_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Insets</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KSHAPE_LAYOUT__INSETS = KLAYOUT_DATA_FEATURE_COUNT + 4;

    /**
     * The number of structural features of the '<em>KShape Layout</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KSHAPE_LAYOUT_FEATURE_COUNT = KLAYOUT_DATA_FEATURE_COUNT + 5;

    /**
     * The meta object id for the '{@link org.eclipse.elk.core.klayoutdata.impl.KEdgeLayoutImpl <em>KEdge Layout</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.core.klayoutdata.impl.KEdgeLayoutImpl
     * @see org.eclipse.elk.core.klayoutdata.impl.KLayoutDataPackageImpl#getKEdgeLayout()
     * @generated
     */
    int KEDGE_LAYOUT = 1;

    /**
     * The feature id for the '<em><b>Properties</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KEDGE_LAYOUT__PROPERTIES = KLAYOUT_DATA__PROPERTIES;

    /**
     * The feature id for the '<em><b>Persistent Entries</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KEDGE_LAYOUT__PERSISTENT_ENTRIES = KLAYOUT_DATA__PERSISTENT_ENTRIES;

    /**
     * The feature id for the '<em><b>Bend Points</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KEDGE_LAYOUT__BEND_POINTS = KLAYOUT_DATA_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Source Point</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KEDGE_LAYOUT__SOURCE_POINT = KLAYOUT_DATA_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Target Point</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KEDGE_LAYOUT__TARGET_POINT = KLAYOUT_DATA_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the '<em>KEdge Layout</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KEDGE_LAYOUT_FEATURE_COUNT = KLAYOUT_DATA_FEATURE_COUNT + 3;

    /**
     * The meta object id for the '{@link org.eclipse.elk.core.klayoutdata.impl.KPointImpl <em>KPoint</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.core.klayoutdata.impl.KPointImpl
     * @see org.eclipse.elk.core.klayoutdata.impl.KLayoutDataPackageImpl#getKPoint()
     * @generated
     */
    int KPOINT = 3;

    /**
     * The feature id for the '<em><b>X</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KPOINT__X = 0;

    /**
     * The feature id for the '<em><b>Y</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KPOINT__Y = 1;

    /**
     * The number of structural features of the '<em>KPoint</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KPOINT_FEATURE_COUNT = 2;


    /**
     * The meta object id for the '{@link org.eclipse.elk.core.klayoutdata.impl.KInsetsImpl <em>KInsets</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.core.klayoutdata.impl.KInsetsImpl
     * @see org.eclipse.elk.core.klayoutdata.impl.KLayoutDataPackageImpl#getKInsets()
     * @generated
     */
    int KINSETS = 4;

    /**
     * The feature id for the '<em><b>Top</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KINSETS__TOP = 0;

    /**
     * The feature id for the '<em><b>Bottom</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KINSETS__BOTTOM = 1;

    /**
     * The feature id for the '<em><b>Left</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KINSETS__LEFT = 2;

    /**
     * The feature id for the '<em><b>Right</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KINSETS__RIGHT = 3;

    /**
     * The number of structural features of the '<em>KInsets</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KINSETS_FEATURE_COUNT = 4;


    /**
     * The meta object id for the '{@link org.eclipse.elk.core.klayoutdata.impl.KIdentifierImpl <em>KIdentifier</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.core.klayoutdata.impl.KIdentifierImpl
     * @see org.eclipse.elk.core.klayoutdata.impl.KLayoutDataPackageImpl#getKIdentifier()
     * @generated
     */
    int KIDENTIFIER = 5;

    /**
     * The feature id for the '<em><b>Properties</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KIDENTIFIER__PROPERTIES = KGraphPackage.KGRAPH_DATA__PROPERTIES;

    /**
     * The feature id for the '<em><b>Persistent Entries</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KIDENTIFIER__PERSISTENT_ENTRIES = KGraphPackage.KGRAPH_DATA__PERSISTENT_ENTRIES;

    /**
     * The feature id for the '<em><b>Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KIDENTIFIER__ID = KGraphPackage.KGRAPH_DATA_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>KIdentifier</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KIDENTIFIER_FEATURE_COUNT = KGraphPackage.KGRAPH_DATA_FEATURE_COUNT + 1;


    /**
     * The meta object id for the '{@link org.eclipse.elk.core.math.KVector <em>KVector</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.core.math.KVector
     * @see org.eclipse.elk.core.klayoutdata.impl.KLayoutDataPackageImpl#getKVector()
     * @generated
     */
    int KVECTOR = 6;

    /**
     * The feature id for the '<em><b>X</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KVECTOR__X = 0;

    /**
     * The feature id for the '<em><b>Y</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KVECTOR__Y = 1;

    /**
     * The number of structural features of the '<em>KVector</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KVECTOR_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link org.eclipse.elk.core.math.KVectorChain <em>KVector Chain</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.core.math.KVectorChain
     * @see org.eclipse.elk.core.klayoutdata.impl.KLayoutDataPackageImpl#getKVectorChain()
     * @generated
     */
    int KVECTOR_CHAIN = 7;

    /**
     * The number of structural features of the '<em>KVector Chain</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KVECTOR_CHAIN_FEATURE_COUNT = 0;


    /**
     * Returns the meta object for class '{@link org.eclipse.elk.core.klayoutdata.KShapeLayout <em>KShape Layout</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>KShape Layout</em>'.
     * @see org.eclipse.elk.core.klayoutdata.KShapeLayout
     * @generated
     */
    EClass getKShapeLayout();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.elk.core.klayoutdata.KShapeLayout#getXpos <em>Xpos</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Xpos</em>'.
     * @see org.eclipse.elk.core.klayoutdata.KShapeLayout#getXpos()
     * @see #getKShapeLayout()
     * @generated
     */
    EAttribute getKShapeLayout_Xpos();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.elk.core.klayoutdata.KShapeLayout#getYpos <em>Ypos</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Ypos</em>'.
     * @see org.eclipse.elk.core.klayoutdata.KShapeLayout#getYpos()
     * @see #getKShapeLayout()
     * @generated
     */
    EAttribute getKShapeLayout_Ypos();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.elk.core.klayoutdata.KShapeLayout#getWidth <em>Width</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Width</em>'.
     * @see org.eclipse.elk.core.klayoutdata.KShapeLayout#getWidth()
     * @see #getKShapeLayout()
     * @generated
     */
    EAttribute getKShapeLayout_Width();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.elk.core.klayoutdata.KShapeLayout#getHeight <em>Height</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Height</em>'.
     * @see org.eclipse.elk.core.klayoutdata.KShapeLayout#getHeight()
     * @see #getKShapeLayout()
     * @generated
     */
    EAttribute getKShapeLayout_Height();

    /**
     * Returns the meta object for the containment reference '{@link org.eclipse.elk.core.klayoutdata.KShapeLayout#getInsets <em>Insets</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Insets</em>'.
     * @see org.eclipse.elk.core.klayoutdata.KShapeLayout#getInsets()
     * @see #getKShapeLayout()
     * @generated
     */
    EReference getKShapeLayout_Insets();

    /**
     * Returns the meta object for class '{@link org.eclipse.elk.core.klayoutdata.KEdgeLayout <em>KEdge Layout</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>KEdge Layout</em>'.
     * @see org.eclipse.elk.core.klayoutdata.KEdgeLayout
     * @generated
     */
    EClass getKEdgeLayout();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.elk.core.klayoutdata.KEdgeLayout#getBendPoints <em>Bend Points</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Bend Points</em>'.
     * @see org.eclipse.elk.core.klayoutdata.KEdgeLayout#getBendPoints()
     * @see #getKEdgeLayout()
     * @generated
     */
    EReference getKEdgeLayout_BendPoints();

    /**
     * Returns the meta object for the containment reference '{@link org.eclipse.elk.core.klayoutdata.KEdgeLayout#getSourcePoint <em>Source Point</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Source Point</em>'.
     * @see org.eclipse.elk.core.klayoutdata.KEdgeLayout#getSourcePoint()
     * @see #getKEdgeLayout()
     * @generated
     */
    EReference getKEdgeLayout_SourcePoint();

    /**
     * Returns the meta object for the containment reference '{@link org.eclipse.elk.core.klayoutdata.KEdgeLayout#getTargetPoint <em>Target Point</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Target Point</em>'.
     * @see org.eclipse.elk.core.klayoutdata.KEdgeLayout#getTargetPoint()
     * @see #getKEdgeLayout()
     * @generated
     */
    EReference getKEdgeLayout_TargetPoint();

    /**
     * Returns the meta object for class '{@link org.eclipse.elk.core.klayoutdata.KLayoutData <em>KLayout Data</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>KLayout Data</em>'.
     * @see org.eclipse.elk.core.klayoutdata.KLayoutData
     * @generated
     */
    EClass getKLayoutData();

    /**
     * Returns the meta object for class '{@link org.eclipse.elk.core.klayoutdata.KPoint <em>KPoint</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>KPoint</em>'.
     * @see org.eclipse.elk.core.klayoutdata.KPoint
     * @generated
     */
    EClass getKPoint();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.elk.core.klayoutdata.KPoint#getX <em>X</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>X</em>'.
     * @see org.eclipse.elk.core.klayoutdata.KPoint#getX()
     * @see #getKPoint()
     * @generated
     */
    EAttribute getKPoint_X();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.elk.core.klayoutdata.KPoint#getY <em>Y</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Y</em>'.
     * @see org.eclipse.elk.core.klayoutdata.KPoint#getY()
     * @see #getKPoint()
     * @generated
     */
    EAttribute getKPoint_Y();

    /**
     * Returns the meta object for class '{@link org.eclipse.elk.core.klayoutdata.KInsets <em>KInsets</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>KInsets</em>'.
     * @see org.eclipse.elk.core.klayoutdata.KInsets
     * @generated
     */
    EClass getKInsets();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.elk.core.klayoutdata.KInsets#getTop <em>Top</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Top</em>'.
     * @see org.eclipse.elk.core.klayoutdata.KInsets#getTop()
     * @see #getKInsets()
     * @generated
     */
    EAttribute getKInsets_Top();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.elk.core.klayoutdata.KInsets#getBottom <em>Bottom</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Bottom</em>'.
     * @see org.eclipse.elk.core.klayoutdata.KInsets#getBottom()
     * @see #getKInsets()
     * @generated
     */
    EAttribute getKInsets_Bottom();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.elk.core.klayoutdata.KInsets#getLeft <em>Left</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Left</em>'.
     * @see org.eclipse.elk.core.klayoutdata.KInsets#getLeft()
     * @see #getKInsets()
     * @generated
     */
    EAttribute getKInsets_Left();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.elk.core.klayoutdata.KInsets#getRight <em>Right</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Right</em>'.
     * @see org.eclipse.elk.core.klayoutdata.KInsets#getRight()
     * @see #getKInsets()
     * @generated
     */
    EAttribute getKInsets_Right();

    /**
     * Returns the meta object for class '{@link org.eclipse.elk.core.klayoutdata.KIdentifier <em>KIdentifier</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>KIdentifier</em>'.
     * @see org.eclipse.elk.core.klayoutdata.KIdentifier
     * @generated
     */
    EClass getKIdentifier();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.elk.core.klayoutdata.KIdentifier#getId <em>Id</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Id</em>'.
     * @see org.eclipse.elk.core.klayoutdata.KIdentifier#getId()
     * @see #getKIdentifier()
     * @generated
     */
    EAttribute getKIdentifier_Id();

    /**
     * Returns the meta object for class '{@link org.eclipse.elk.core.math.KVector <em>KVector</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>KVector</em>'.
     * @see org.eclipse.elk.core.math.KVector
     * @model instanceClass="org.eclipse.elk.core.math.KVector"
     * @generated
     */
    EClass getKVector();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.elk.core.math.KVector#getX <em>X</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>X</em>'.
     * @see org.eclipse.elk.core.math.KVector#getX()
     * @see #getKVector()
     * @generated
     */
    EAttribute getKVector_X();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.elk.core.math.KVector#getY <em>Y</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Y</em>'.
     * @see org.eclipse.elk.core.math.KVector#getY()
     * @see #getKVector()
     * @generated
     */
    EAttribute getKVector_Y();

    /**
     * Returns the meta object for class '{@link org.eclipse.elk.core.math.KVectorChain <em>KVector Chain</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>KVector Chain</em>'.
     * @see org.eclipse.elk.core.math.KVectorChain
     * @model instanceClass="org.eclipse.elk.core.math.KVectorChain"
     * @generated
     */
    EClass getKVectorChain();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    KLayoutDataFactory getKLayoutDataFactory();

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
         * The meta object literal for the '{@link org.eclipse.elk.core.klayoutdata.impl.KShapeLayoutImpl <em>KShape Layout</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.elk.core.klayoutdata.impl.KShapeLayoutImpl
         * @see org.eclipse.elk.core.klayoutdata.impl.KLayoutDataPackageImpl#getKShapeLayout()
         * @generated
         */
        EClass KSHAPE_LAYOUT = eINSTANCE.getKShapeLayout();

        /**
         * The meta object literal for the '<em><b>Xpos</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute KSHAPE_LAYOUT__XPOS = eINSTANCE.getKShapeLayout_Xpos();

        /**
         * The meta object literal for the '<em><b>Ypos</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute KSHAPE_LAYOUT__YPOS = eINSTANCE.getKShapeLayout_Ypos();

        /**
         * The meta object literal for the '<em><b>Width</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute KSHAPE_LAYOUT__WIDTH = eINSTANCE.getKShapeLayout_Width();

        /**
         * The meta object literal for the '<em><b>Height</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute KSHAPE_LAYOUT__HEIGHT = eINSTANCE.getKShapeLayout_Height();

        /**
         * The meta object literal for the '<em><b>Insets</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference KSHAPE_LAYOUT__INSETS = eINSTANCE.getKShapeLayout_Insets();

        /**
         * The meta object literal for the '{@link org.eclipse.elk.core.klayoutdata.impl.KEdgeLayoutImpl <em>KEdge Layout</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.elk.core.klayoutdata.impl.KEdgeLayoutImpl
         * @see org.eclipse.elk.core.klayoutdata.impl.KLayoutDataPackageImpl#getKEdgeLayout()
         * @generated
         */
        EClass KEDGE_LAYOUT = eINSTANCE.getKEdgeLayout();

        /**
         * The meta object literal for the '<em><b>Bend Points</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference KEDGE_LAYOUT__BEND_POINTS = eINSTANCE.getKEdgeLayout_BendPoints();

        /**
         * The meta object literal for the '<em><b>Source Point</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference KEDGE_LAYOUT__SOURCE_POINT = eINSTANCE.getKEdgeLayout_SourcePoint();

        /**
         * The meta object literal for the '<em><b>Target Point</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference KEDGE_LAYOUT__TARGET_POINT = eINSTANCE.getKEdgeLayout_TargetPoint();

        /**
         * The meta object literal for the '{@link org.eclipse.elk.core.klayoutdata.KLayoutData <em>KLayout Data</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.elk.core.klayoutdata.KLayoutData
         * @see org.eclipse.elk.core.klayoutdata.impl.KLayoutDataPackageImpl#getKLayoutData()
         * @generated
         */
        EClass KLAYOUT_DATA = eINSTANCE.getKLayoutData();

        /**
         * The meta object literal for the '{@link org.eclipse.elk.core.klayoutdata.impl.KPointImpl <em>KPoint</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.elk.core.klayoutdata.impl.KPointImpl
         * @see org.eclipse.elk.core.klayoutdata.impl.KLayoutDataPackageImpl#getKPoint()
         * @generated
         */
        EClass KPOINT = eINSTANCE.getKPoint();

        /**
         * The meta object literal for the '<em><b>X</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute KPOINT__X = eINSTANCE.getKPoint_X();

        /**
         * The meta object literal for the '<em><b>Y</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute KPOINT__Y = eINSTANCE.getKPoint_Y();

        /**
         * The meta object literal for the '{@link org.eclipse.elk.core.klayoutdata.impl.KInsetsImpl <em>KInsets</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.elk.core.klayoutdata.impl.KInsetsImpl
         * @see org.eclipse.elk.core.klayoutdata.impl.KLayoutDataPackageImpl#getKInsets()
         * @generated
         */
        EClass KINSETS = eINSTANCE.getKInsets();

        /**
         * The meta object literal for the '<em><b>Top</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute KINSETS__TOP = eINSTANCE.getKInsets_Top();

        /**
         * The meta object literal for the '<em><b>Bottom</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute KINSETS__BOTTOM = eINSTANCE.getKInsets_Bottom();

        /**
         * The meta object literal for the '<em><b>Left</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute KINSETS__LEFT = eINSTANCE.getKInsets_Left();

        /**
         * The meta object literal for the '<em><b>Right</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute KINSETS__RIGHT = eINSTANCE.getKInsets_Right();

        /**
         * The meta object literal for the '{@link org.eclipse.elk.core.klayoutdata.impl.KIdentifierImpl <em>KIdentifier</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.elk.core.klayoutdata.impl.KIdentifierImpl
         * @see org.eclipse.elk.core.klayoutdata.impl.KLayoutDataPackageImpl#getKIdentifier()
         * @generated
         */
        EClass KIDENTIFIER = eINSTANCE.getKIdentifier();

        /**
         * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute KIDENTIFIER__ID = eINSTANCE.getKIdentifier_Id();

        /**
         * The meta object literal for the '{@link org.eclipse.elk.core.math.KVector <em>KVector</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.elk.core.math.KVector
         * @see org.eclipse.elk.core.klayoutdata.impl.KLayoutDataPackageImpl#getKVector()
         * @generated
         */
        EClass KVECTOR = eINSTANCE.getKVector();

        /**
         * The meta object literal for the '<em><b>X</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute KVECTOR__X = eINSTANCE.getKVector_X();

        /**
         * The meta object literal for the '<em><b>Y</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute KVECTOR__Y = eINSTANCE.getKVector_Y();

        /**
         * The meta object literal for the '{@link org.eclipse.elk.core.math.KVectorChain <em>KVector Chain</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.elk.core.math.KVectorChain
         * @see org.eclipse.elk.core.klayoutdata.impl.KLayoutDataPackageImpl#getKVectorChain()
         * @generated
         */
        EClass KVECTOR_CHAIN = eINSTANCE.getKVectorChain();

    }

} //KLayoutDataPackage
