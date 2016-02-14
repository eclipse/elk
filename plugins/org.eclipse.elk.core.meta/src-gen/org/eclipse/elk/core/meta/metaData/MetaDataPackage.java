/**
 * ******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  *
 * Contributors:
 *     Kiel University - initial API and implementation
 *  ******************************************************************************
 */
package org.eclipse.elk.core.meta.metaData;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
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
 * @see org.eclipse.elk.core.meta.metaData.MetaDataFactory
 * @model kind="package"
 * @generated
 */
public interface MetaDataPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "metaData";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/elk/core/meta/MetaData";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "metaData";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  MetaDataPackage eINSTANCE = org.eclipse.elk.core.meta.metaData.impl.MetaDataPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.elk.core.meta.metaData.impl.MdModelImpl <em>Md Model</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.elk.core.meta.metaData.impl.MdModelImpl
   * @see org.eclipse.elk.core.meta.metaData.impl.MetaDataPackageImpl#getMdModel()
   * @generated
   */
  int MD_MODEL = 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_MODEL__NAME = 0;

  /**
   * The feature id for the '<em><b>Import Section</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_MODEL__IMPORT_SECTION = 1;

  /**
   * The feature id for the '<em><b>Bundle</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_MODEL__BUNDLE = 2;

  /**
   * The number of structural features of the '<em>Md Model</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_MODEL_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.elk.core.meta.metaData.impl.MdBundleImpl <em>Md Bundle</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.elk.core.meta.metaData.impl.MdBundleImpl
   * @see org.eclipse.elk.core.meta.metaData.impl.MetaDataPackageImpl#getMdBundle()
   * @generated
   */
  int MD_BUNDLE = 1;

  /**
   * The feature id for the '<em><b>Label</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_BUNDLE__LABEL = 0;

  /**
   * The feature id for the '<em><b>Target Class</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_BUNDLE__TARGET_CLASS = 1;

  /**
   * The feature id for the '<em><b>Members</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_BUNDLE__MEMBERS = 2;

  /**
   * The number of structural features of the '<em>Md Bundle</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_BUNDLE_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.elk.core.meta.metaData.impl.MdBundleMemberImpl <em>Md Bundle Member</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.elk.core.meta.metaData.impl.MdBundleMemberImpl
   * @see org.eclipse.elk.core.meta.metaData.impl.MetaDataPackageImpl#getMdBundleMember()
   * @generated
   */
  int MD_BUNDLE_MEMBER = 2;

  /**
   * The feature id for the '<em><b>Deprecated</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_BUNDLE_MEMBER__DEPRECATED = 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_BUNDLE_MEMBER__NAME = 1;

  /**
   * The feature id for the '<em><b>Label</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_BUNDLE_MEMBER__LABEL = 2;

  /**
   * The feature id for the '<em><b>Description</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_BUNDLE_MEMBER__DESCRIPTION = 3;

  /**
   * The number of structural features of the '<em>Md Bundle Member</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_BUNDLE_MEMBER_FEATURE_COUNT = 4;

  /**
   * The meta object id for the '{@link org.eclipse.elk.core.meta.metaData.impl.MdPropertyImpl <em>Md Property</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.elk.core.meta.metaData.impl.MdPropertyImpl
   * @see org.eclipse.elk.core.meta.metaData.impl.MetaDataPackageImpl#getMdProperty()
   * @generated
   */
  int MD_PROPERTY = 3;

  /**
   * The feature id for the '<em><b>Deprecated</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_PROPERTY__DEPRECATED = MD_BUNDLE_MEMBER__DEPRECATED;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_PROPERTY__NAME = MD_BUNDLE_MEMBER__NAME;

  /**
   * The feature id for the '<em><b>Label</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_PROPERTY__LABEL = MD_BUNDLE_MEMBER__LABEL;

  /**
   * The feature id for the '<em><b>Description</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_PROPERTY__DESCRIPTION = MD_BUNDLE_MEMBER__DESCRIPTION;

  /**
   * The feature id for the '<em><b>Advanced</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_PROPERTY__ADVANCED = MD_BUNDLE_MEMBER_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Programmatic</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_PROPERTY__PROGRAMMATIC = MD_BUNDLE_MEMBER_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Output</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_PROPERTY__OUTPUT = MD_BUNDLE_MEMBER_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Global</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_PROPERTY__GLOBAL = MD_BUNDLE_MEMBER_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Type</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_PROPERTY__TYPE = MD_BUNDLE_MEMBER_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Default Value</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_PROPERTY__DEFAULT_VALUE = MD_BUNDLE_MEMBER_FEATURE_COUNT + 5;

  /**
   * The feature id for the '<em><b>Targets</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_PROPERTY__TARGETS = MD_BUNDLE_MEMBER_FEATURE_COUNT + 6;

  /**
   * The feature id for the '<em><b>Legacy Ids</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_PROPERTY__LEGACY_IDS = MD_BUNDLE_MEMBER_FEATURE_COUNT + 7;

  /**
   * The feature id for the '<em><b>Dependencies</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_PROPERTY__DEPENDENCIES = MD_BUNDLE_MEMBER_FEATURE_COUNT + 8;

  /**
   * The number of structural features of the '<em>Md Property</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_PROPERTY_FEATURE_COUNT = MD_BUNDLE_MEMBER_FEATURE_COUNT + 9;

  /**
   * The meta object id for the '{@link org.eclipse.elk.core.meta.metaData.impl.MdPropertyDependencyImpl <em>Md Property Dependency</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.elk.core.meta.metaData.impl.MdPropertyDependencyImpl
   * @see org.eclipse.elk.core.meta.metaData.impl.MetaDataPackageImpl#getMdPropertyDependency()
   * @generated
   */
  int MD_PROPERTY_DEPENDENCY = 4;

  /**
   * The feature id for the '<em><b>Target</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_PROPERTY_DEPENDENCY__TARGET = 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_PROPERTY_DEPENDENCY__VALUE = 1;

  /**
   * The number of structural features of the '<em>Md Property Dependency</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_PROPERTY_DEPENDENCY_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.elk.core.meta.metaData.impl.MdAlgorithmImpl <em>Md Algorithm</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.elk.core.meta.metaData.impl.MdAlgorithmImpl
   * @see org.eclipse.elk.core.meta.metaData.impl.MetaDataPackageImpl#getMdAlgorithm()
   * @generated
   */
  int MD_ALGORITHM = 5;

  /**
   * The feature id for the '<em><b>Deprecated</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_ALGORITHM__DEPRECATED = MD_BUNDLE_MEMBER__DEPRECATED;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_ALGORITHM__NAME = MD_BUNDLE_MEMBER__NAME;

  /**
   * The feature id for the '<em><b>Label</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_ALGORITHM__LABEL = MD_BUNDLE_MEMBER__LABEL;

  /**
   * The feature id for the '<em><b>Description</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_ALGORITHM__DESCRIPTION = MD_BUNDLE_MEMBER__DESCRIPTION;

  /**
   * The feature id for the '<em><b>Provider</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_ALGORITHM__PROVIDER = MD_BUNDLE_MEMBER_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Parameter</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_ALGORITHM__PARAMETER = MD_BUNDLE_MEMBER_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Category</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_ALGORITHM__CATEGORY = MD_BUNDLE_MEMBER_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Preview Image</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_ALGORITHM__PREVIEW_IMAGE = MD_BUNDLE_MEMBER_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Supported Features</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_ALGORITHM__SUPPORTED_FEATURES = MD_BUNDLE_MEMBER_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Supported Options</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_ALGORITHM__SUPPORTED_OPTIONS = MD_BUNDLE_MEMBER_FEATURE_COUNT + 5;

  /**
   * The number of structural features of the '<em>Md Algorithm</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_ALGORITHM_FEATURE_COUNT = MD_BUNDLE_MEMBER_FEATURE_COUNT + 6;

  /**
   * The meta object id for the '{@link org.eclipse.elk.core.meta.metaData.impl.MdCategoryImpl <em>Md Category</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.elk.core.meta.metaData.impl.MdCategoryImpl
   * @see org.eclipse.elk.core.meta.metaData.impl.MetaDataPackageImpl#getMdCategory()
   * @generated
   */
  int MD_CATEGORY = 6;

  /**
   * The feature id for the '<em><b>Deprecated</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_CATEGORY__DEPRECATED = MD_BUNDLE_MEMBER__DEPRECATED;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_CATEGORY__NAME = MD_BUNDLE_MEMBER__NAME;

  /**
   * The feature id for the '<em><b>Label</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_CATEGORY__LABEL = MD_BUNDLE_MEMBER__LABEL;

  /**
   * The feature id for the '<em><b>Description</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_CATEGORY__DESCRIPTION = MD_BUNDLE_MEMBER__DESCRIPTION;

  /**
   * The number of structural features of the '<em>Md Category</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_CATEGORY_FEATURE_COUNT = MD_BUNDLE_MEMBER_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.elk.core.meta.metaData.impl.MdPropertySupportImpl <em>Md Property Support</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.elk.core.meta.metaData.impl.MdPropertySupportImpl
   * @see org.eclipse.elk.core.meta.metaData.impl.MetaDataPackageImpl#getMdPropertySupport()
   * @generated
   */
  int MD_PROPERTY_SUPPORT = 7;

  /**
   * The feature id for the '<em><b>Property</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_PROPERTY_SUPPORT__PROPERTY = 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_PROPERTY_SUPPORT__VALUE = 1;

  /**
   * The feature id for the '<em><b>Duplicated</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_PROPERTY_SUPPORT__DUPLICATED = 2;

  /**
   * The number of structural features of the '<em>Md Property Support</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MD_PROPERTY_SUPPORT_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.elk.core.meta.metaData.MdPropertyTargetType <em>Md Property Target Type</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.elk.core.meta.metaData.MdPropertyTargetType
   * @see org.eclipse.elk.core.meta.metaData.impl.MetaDataPackageImpl#getMdPropertyTargetType()
   * @generated
   */
  int MD_PROPERTY_TARGET_TYPE = 8;

  /**
   * The meta object id for the '{@link org.eclipse.elk.core.meta.metaData.MdGraphFeature <em>Md Graph Feature</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.elk.core.meta.metaData.MdGraphFeature
   * @see org.eclipse.elk.core.meta.metaData.impl.MetaDataPackageImpl#getMdGraphFeature()
   * @generated
   */
  int MD_GRAPH_FEATURE = 9;


  /**
   * Returns the meta object for class '{@link org.eclipse.elk.core.meta.metaData.MdModel <em>Md Model</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Md Model</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdModel
   * @generated
   */
  EClass getMdModel();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.elk.core.meta.metaData.MdModel#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdModel#getName()
   * @see #getMdModel()
   * @generated
   */
  EAttribute getMdModel_Name();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.elk.core.meta.metaData.MdModel#getImportSection <em>Import Section</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Import Section</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdModel#getImportSection()
   * @see #getMdModel()
   * @generated
   */
  EReference getMdModel_ImportSection();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.elk.core.meta.metaData.MdModel#getBundle <em>Bundle</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Bundle</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdModel#getBundle()
   * @see #getMdModel()
   * @generated
   */
  EReference getMdModel_Bundle();

  /**
   * Returns the meta object for class '{@link org.eclipse.elk.core.meta.metaData.MdBundle <em>Md Bundle</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Md Bundle</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdBundle
   * @generated
   */
  EClass getMdBundle();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.elk.core.meta.metaData.MdBundle#getLabel <em>Label</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Label</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdBundle#getLabel()
   * @see #getMdBundle()
   * @generated
   */
  EAttribute getMdBundle_Label();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.elk.core.meta.metaData.MdBundle#getTargetClass <em>Target Class</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Target Class</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdBundle#getTargetClass()
   * @see #getMdBundle()
   * @generated
   */
  EAttribute getMdBundle_TargetClass();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.elk.core.meta.metaData.MdBundle#getMembers <em>Members</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Members</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdBundle#getMembers()
   * @see #getMdBundle()
   * @generated
   */
  EReference getMdBundle_Members();

  /**
   * Returns the meta object for class '{@link org.eclipse.elk.core.meta.metaData.MdBundleMember <em>Md Bundle Member</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Md Bundle Member</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdBundleMember
   * @generated
   */
  EClass getMdBundleMember();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.elk.core.meta.metaData.MdBundleMember#isDeprecated <em>Deprecated</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Deprecated</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdBundleMember#isDeprecated()
   * @see #getMdBundleMember()
   * @generated
   */
  EAttribute getMdBundleMember_Deprecated();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.elk.core.meta.metaData.MdBundleMember#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdBundleMember#getName()
   * @see #getMdBundleMember()
   * @generated
   */
  EAttribute getMdBundleMember_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.elk.core.meta.metaData.MdBundleMember#getLabel <em>Label</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Label</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdBundleMember#getLabel()
   * @see #getMdBundleMember()
   * @generated
   */
  EAttribute getMdBundleMember_Label();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.elk.core.meta.metaData.MdBundleMember#getDescription <em>Description</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Description</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdBundleMember#getDescription()
   * @see #getMdBundleMember()
   * @generated
   */
  EAttribute getMdBundleMember_Description();

  /**
   * Returns the meta object for class '{@link org.eclipse.elk.core.meta.metaData.MdProperty <em>Md Property</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Md Property</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdProperty
   * @generated
   */
  EClass getMdProperty();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.elk.core.meta.metaData.MdProperty#isAdvanced <em>Advanced</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Advanced</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdProperty#isAdvanced()
   * @see #getMdProperty()
   * @generated
   */
  EAttribute getMdProperty_Advanced();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.elk.core.meta.metaData.MdProperty#isProgrammatic <em>Programmatic</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Programmatic</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdProperty#isProgrammatic()
   * @see #getMdProperty()
   * @generated
   */
  EAttribute getMdProperty_Programmatic();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.elk.core.meta.metaData.MdProperty#isOutput <em>Output</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Output</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdProperty#isOutput()
   * @see #getMdProperty()
   * @generated
   */
  EAttribute getMdProperty_Output();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.elk.core.meta.metaData.MdProperty#isGlobal <em>Global</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Global</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdProperty#isGlobal()
   * @see #getMdProperty()
   * @generated
   */
  EAttribute getMdProperty_Global();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.elk.core.meta.metaData.MdProperty#getType <em>Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Type</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdProperty#getType()
   * @see #getMdProperty()
   * @generated
   */
  EReference getMdProperty_Type();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.elk.core.meta.metaData.MdProperty#getDefaultValue <em>Default Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Default Value</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdProperty#getDefaultValue()
   * @see #getMdProperty()
   * @generated
   */
  EReference getMdProperty_DefaultValue();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.elk.core.meta.metaData.MdProperty#getTargets <em>Targets</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Targets</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdProperty#getTargets()
   * @see #getMdProperty()
   * @generated
   */
  EAttribute getMdProperty_Targets();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.elk.core.meta.metaData.MdProperty#getLegacyIds <em>Legacy Ids</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Legacy Ids</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdProperty#getLegacyIds()
   * @see #getMdProperty()
   * @generated
   */
  EAttribute getMdProperty_LegacyIds();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.elk.core.meta.metaData.MdProperty#getDependencies <em>Dependencies</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Dependencies</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdProperty#getDependencies()
   * @see #getMdProperty()
   * @generated
   */
  EReference getMdProperty_Dependencies();

  /**
   * Returns the meta object for class '{@link org.eclipse.elk.core.meta.metaData.MdPropertyDependency <em>Md Property Dependency</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Md Property Dependency</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdPropertyDependency
   * @generated
   */
  EClass getMdPropertyDependency();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.elk.core.meta.metaData.MdPropertyDependency#getTarget <em>Target</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Target</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdPropertyDependency#getTarget()
   * @see #getMdPropertyDependency()
   * @generated
   */
  EReference getMdPropertyDependency_Target();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.elk.core.meta.metaData.MdPropertyDependency#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Value</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdPropertyDependency#getValue()
   * @see #getMdPropertyDependency()
   * @generated
   */
  EReference getMdPropertyDependency_Value();

  /**
   * Returns the meta object for class '{@link org.eclipse.elk.core.meta.metaData.MdAlgorithm <em>Md Algorithm</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Md Algorithm</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdAlgorithm
   * @generated
   */
  EClass getMdAlgorithm();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.elk.core.meta.metaData.MdAlgorithm#getProvider <em>Provider</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Provider</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdAlgorithm#getProvider()
   * @see #getMdAlgorithm()
   * @generated
   */
  EReference getMdAlgorithm_Provider();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.elk.core.meta.metaData.MdAlgorithm#getParameter <em>Parameter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Parameter</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdAlgorithm#getParameter()
   * @see #getMdAlgorithm()
   * @generated
   */
  EAttribute getMdAlgorithm_Parameter();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.elk.core.meta.metaData.MdAlgorithm#getCategory <em>Category</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Category</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdAlgorithm#getCategory()
   * @see #getMdAlgorithm()
   * @generated
   */
  EReference getMdAlgorithm_Category();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.elk.core.meta.metaData.MdAlgorithm#getPreviewImage <em>Preview Image</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Preview Image</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdAlgorithm#getPreviewImage()
   * @see #getMdAlgorithm()
   * @generated
   */
  EAttribute getMdAlgorithm_PreviewImage();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.elk.core.meta.metaData.MdAlgorithm#getSupportedFeatures <em>Supported Features</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Supported Features</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdAlgorithm#getSupportedFeatures()
   * @see #getMdAlgorithm()
   * @generated
   */
  EAttribute getMdAlgorithm_SupportedFeatures();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.elk.core.meta.metaData.MdAlgorithm#getSupportedOptions <em>Supported Options</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Supported Options</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdAlgorithm#getSupportedOptions()
   * @see #getMdAlgorithm()
   * @generated
   */
  EReference getMdAlgorithm_SupportedOptions();

  /**
   * Returns the meta object for class '{@link org.eclipse.elk.core.meta.metaData.MdCategory <em>Md Category</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Md Category</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdCategory
   * @generated
   */
  EClass getMdCategory();

  /**
   * Returns the meta object for class '{@link org.eclipse.elk.core.meta.metaData.MdPropertySupport <em>Md Property Support</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Md Property Support</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdPropertySupport
   * @generated
   */
  EClass getMdPropertySupport();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.elk.core.meta.metaData.MdPropertySupport#getProperty <em>Property</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Property</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdPropertySupport#getProperty()
   * @see #getMdPropertySupport()
   * @generated
   */
  EReference getMdPropertySupport_Property();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.elk.core.meta.metaData.MdPropertySupport#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Value</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdPropertySupport#getValue()
   * @see #getMdPropertySupport()
   * @generated
   */
  EReference getMdPropertySupport_Value();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.elk.core.meta.metaData.MdPropertySupport#isDuplicated <em>Duplicated</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Duplicated</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdPropertySupport#isDuplicated()
   * @see #getMdPropertySupport()
   * @generated
   */
  EAttribute getMdPropertySupport_Duplicated();

  /**
   * Returns the meta object for enum '{@link org.eclipse.elk.core.meta.metaData.MdPropertyTargetType <em>Md Property Target Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Md Property Target Type</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdPropertyTargetType
   * @generated
   */
  EEnum getMdPropertyTargetType();

  /**
   * Returns the meta object for enum '{@link org.eclipse.elk.core.meta.metaData.MdGraphFeature <em>Md Graph Feature</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Md Graph Feature</em>'.
   * @see org.eclipse.elk.core.meta.metaData.MdGraphFeature
   * @generated
   */
  EEnum getMdGraphFeature();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  MetaDataFactory getMetaDataFactory();

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
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.elk.core.meta.metaData.impl.MdModelImpl <em>Md Model</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.core.meta.metaData.impl.MdModelImpl
     * @see org.eclipse.elk.core.meta.metaData.impl.MetaDataPackageImpl#getMdModel()
     * @generated
     */
    EClass MD_MODEL = eINSTANCE.getMdModel();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MD_MODEL__NAME = eINSTANCE.getMdModel_Name();

    /**
     * The meta object literal for the '<em><b>Import Section</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MD_MODEL__IMPORT_SECTION = eINSTANCE.getMdModel_ImportSection();

    /**
     * The meta object literal for the '<em><b>Bundle</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MD_MODEL__BUNDLE = eINSTANCE.getMdModel_Bundle();

    /**
     * The meta object literal for the '{@link org.eclipse.elk.core.meta.metaData.impl.MdBundleImpl <em>Md Bundle</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.core.meta.metaData.impl.MdBundleImpl
     * @see org.eclipse.elk.core.meta.metaData.impl.MetaDataPackageImpl#getMdBundle()
     * @generated
     */
    EClass MD_BUNDLE = eINSTANCE.getMdBundle();

    /**
     * The meta object literal for the '<em><b>Label</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MD_BUNDLE__LABEL = eINSTANCE.getMdBundle_Label();

    /**
     * The meta object literal for the '<em><b>Target Class</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MD_BUNDLE__TARGET_CLASS = eINSTANCE.getMdBundle_TargetClass();

    /**
     * The meta object literal for the '<em><b>Members</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MD_BUNDLE__MEMBERS = eINSTANCE.getMdBundle_Members();

    /**
     * The meta object literal for the '{@link org.eclipse.elk.core.meta.metaData.impl.MdBundleMemberImpl <em>Md Bundle Member</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.core.meta.metaData.impl.MdBundleMemberImpl
     * @see org.eclipse.elk.core.meta.metaData.impl.MetaDataPackageImpl#getMdBundleMember()
     * @generated
     */
    EClass MD_BUNDLE_MEMBER = eINSTANCE.getMdBundleMember();

    /**
     * The meta object literal for the '<em><b>Deprecated</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MD_BUNDLE_MEMBER__DEPRECATED = eINSTANCE.getMdBundleMember_Deprecated();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MD_BUNDLE_MEMBER__NAME = eINSTANCE.getMdBundleMember_Name();

    /**
     * The meta object literal for the '<em><b>Label</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MD_BUNDLE_MEMBER__LABEL = eINSTANCE.getMdBundleMember_Label();

    /**
     * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MD_BUNDLE_MEMBER__DESCRIPTION = eINSTANCE.getMdBundleMember_Description();

    /**
     * The meta object literal for the '{@link org.eclipse.elk.core.meta.metaData.impl.MdPropertyImpl <em>Md Property</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.core.meta.metaData.impl.MdPropertyImpl
     * @see org.eclipse.elk.core.meta.metaData.impl.MetaDataPackageImpl#getMdProperty()
     * @generated
     */
    EClass MD_PROPERTY = eINSTANCE.getMdProperty();

    /**
     * The meta object literal for the '<em><b>Advanced</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MD_PROPERTY__ADVANCED = eINSTANCE.getMdProperty_Advanced();

    /**
     * The meta object literal for the '<em><b>Programmatic</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MD_PROPERTY__PROGRAMMATIC = eINSTANCE.getMdProperty_Programmatic();

    /**
     * The meta object literal for the '<em><b>Output</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MD_PROPERTY__OUTPUT = eINSTANCE.getMdProperty_Output();

    /**
     * The meta object literal for the '<em><b>Global</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MD_PROPERTY__GLOBAL = eINSTANCE.getMdProperty_Global();

    /**
     * The meta object literal for the '<em><b>Type</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MD_PROPERTY__TYPE = eINSTANCE.getMdProperty_Type();

    /**
     * The meta object literal for the '<em><b>Default Value</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MD_PROPERTY__DEFAULT_VALUE = eINSTANCE.getMdProperty_DefaultValue();

    /**
     * The meta object literal for the '<em><b>Targets</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MD_PROPERTY__TARGETS = eINSTANCE.getMdProperty_Targets();

    /**
     * The meta object literal for the '<em><b>Legacy Ids</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MD_PROPERTY__LEGACY_IDS = eINSTANCE.getMdProperty_LegacyIds();

    /**
     * The meta object literal for the '<em><b>Dependencies</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MD_PROPERTY__DEPENDENCIES = eINSTANCE.getMdProperty_Dependencies();

    /**
     * The meta object literal for the '{@link org.eclipse.elk.core.meta.metaData.impl.MdPropertyDependencyImpl <em>Md Property Dependency</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.core.meta.metaData.impl.MdPropertyDependencyImpl
     * @see org.eclipse.elk.core.meta.metaData.impl.MetaDataPackageImpl#getMdPropertyDependency()
     * @generated
     */
    EClass MD_PROPERTY_DEPENDENCY = eINSTANCE.getMdPropertyDependency();

    /**
     * The meta object literal for the '<em><b>Target</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MD_PROPERTY_DEPENDENCY__TARGET = eINSTANCE.getMdPropertyDependency_Target();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MD_PROPERTY_DEPENDENCY__VALUE = eINSTANCE.getMdPropertyDependency_Value();

    /**
     * The meta object literal for the '{@link org.eclipse.elk.core.meta.metaData.impl.MdAlgorithmImpl <em>Md Algorithm</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.core.meta.metaData.impl.MdAlgorithmImpl
     * @see org.eclipse.elk.core.meta.metaData.impl.MetaDataPackageImpl#getMdAlgorithm()
     * @generated
     */
    EClass MD_ALGORITHM = eINSTANCE.getMdAlgorithm();

    /**
     * The meta object literal for the '<em><b>Provider</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MD_ALGORITHM__PROVIDER = eINSTANCE.getMdAlgorithm_Provider();

    /**
     * The meta object literal for the '<em><b>Parameter</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MD_ALGORITHM__PARAMETER = eINSTANCE.getMdAlgorithm_Parameter();

    /**
     * The meta object literal for the '<em><b>Category</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MD_ALGORITHM__CATEGORY = eINSTANCE.getMdAlgorithm_Category();

    /**
     * The meta object literal for the '<em><b>Preview Image</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MD_ALGORITHM__PREVIEW_IMAGE = eINSTANCE.getMdAlgorithm_PreviewImage();

    /**
     * The meta object literal for the '<em><b>Supported Features</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MD_ALGORITHM__SUPPORTED_FEATURES = eINSTANCE.getMdAlgorithm_SupportedFeatures();

    /**
     * The meta object literal for the '<em><b>Supported Options</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MD_ALGORITHM__SUPPORTED_OPTIONS = eINSTANCE.getMdAlgorithm_SupportedOptions();

    /**
     * The meta object literal for the '{@link org.eclipse.elk.core.meta.metaData.impl.MdCategoryImpl <em>Md Category</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.core.meta.metaData.impl.MdCategoryImpl
     * @see org.eclipse.elk.core.meta.metaData.impl.MetaDataPackageImpl#getMdCategory()
     * @generated
     */
    EClass MD_CATEGORY = eINSTANCE.getMdCategory();

    /**
     * The meta object literal for the '{@link org.eclipse.elk.core.meta.metaData.impl.MdPropertySupportImpl <em>Md Property Support</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.core.meta.metaData.impl.MdPropertySupportImpl
     * @see org.eclipse.elk.core.meta.metaData.impl.MetaDataPackageImpl#getMdPropertySupport()
     * @generated
     */
    EClass MD_PROPERTY_SUPPORT = eINSTANCE.getMdPropertySupport();

    /**
     * The meta object literal for the '<em><b>Property</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MD_PROPERTY_SUPPORT__PROPERTY = eINSTANCE.getMdPropertySupport_Property();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MD_PROPERTY_SUPPORT__VALUE = eINSTANCE.getMdPropertySupport_Value();

    /**
     * The meta object literal for the '<em><b>Duplicated</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MD_PROPERTY_SUPPORT__DUPLICATED = eINSTANCE.getMdPropertySupport_Duplicated();

    /**
     * The meta object literal for the '{@link org.eclipse.elk.core.meta.metaData.MdPropertyTargetType <em>Md Property Target Type</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.core.meta.metaData.MdPropertyTargetType
     * @see org.eclipse.elk.core.meta.metaData.impl.MetaDataPackageImpl#getMdPropertyTargetType()
     * @generated
     */
    EEnum MD_PROPERTY_TARGET_TYPE = eINSTANCE.getMdPropertyTargetType();

    /**
     * The meta object literal for the '{@link org.eclipse.elk.core.meta.metaData.MdGraphFeature <em>Md Graph Feature</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.elk.core.meta.metaData.MdGraphFeature
     * @see org.eclipse.elk.core.meta.metaData.impl.MetaDataPackageImpl#getMdGraphFeature()
     * @generated
     */
    EEnum MD_GRAPH_FEATURE = eINSTANCE.getMdGraphFeature();

  }

} //MetaDataPackage
