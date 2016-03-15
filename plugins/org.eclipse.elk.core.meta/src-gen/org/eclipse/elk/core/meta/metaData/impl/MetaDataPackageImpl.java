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
package org.eclipse.elk.core.meta.metaData.impl;

import org.eclipse.elk.core.meta.metaData.MdAlgorithm;
import org.eclipse.elk.core.meta.metaData.MdBundle;
import org.eclipse.elk.core.meta.metaData.MdBundleMember;
import org.eclipse.elk.core.meta.metaData.MdCategory;
import org.eclipse.elk.core.meta.metaData.MdGraphFeature;
import org.eclipse.elk.core.meta.metaData.MdGroup;
import org.eclipse.elk.core.meta.metaData.MdGroupOrProperty;
import org.eclipse.elk.core.meta.metaData.MdModel;
import org.eclipse.elk.core.meta.metaData.MdProperty;
import org.eclipse.elk.core.meta.metaData.MdPropertyDependency;
import org.eclipse.elk.core.meta.metaData.MdPropertySupport;
import org.eclipse.elk.core.meta.metaData.MdPropertyTargetType;
import org.eclipse.elk.core.meta.metaData.MetaDataFactory;
import org.eclipse.elk.core.meta.metaData.MetaDataPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.xtext.common.types.TypesPackage;

import org.eclipse.xtext.xbase.XbasePackage;

import org.eclipse.xtext.xtype.XtypePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class MetaDataPackageImpl extends EPackageImpl implements MetaDataPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass mdModelEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass mdBundleEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass mdBundleMemberEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass mdGroupOrPropertyEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass mdGroupEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass mdPropertyEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass mdPropertyDependencyEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass mdAlgorithmEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass mdCategoryEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass mdPropertySupportEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum mdPropertyTargetTypeEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum mdGraphFeatureEEnum = null;

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
   * @see org.eclipse.elk.core.meta.metaData.MetaDataPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private MetaDataPackageImpl()
  {
    super(eNS_URI, MetaDataFactory.eINSTANCE);
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
   * <p>This method is used to initialize {@link MetaDataPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static MetaDataPackage init()
  {
    if (isInited) return (MetaDataPackage)EPackage.Registry.INSTANCE.getEPackage(MetaDataPackage.eNS_URI);

    // Obtain or create and register package
    MetaDataPackageImpl theMetaDataPackage = (MetaDataPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof MetaDataPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new MetaDataPackageImpl());

    isInited = true;

    // Initialize simple dependencies
    XbasePackage.eINSTANCE.eClass();
    XtypePackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theMetaDataPackage.createPackageContents();

    // Initialize created meta-data
    theMetaDataPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theMetaDataPackage.freeze();

  
    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(MetaDataPackage.eNS_URI, theMetaDataPackage);
    return theMetaDataPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getMdModel()
  {
    return mdModelEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdModel_Name()
  {
    return (EAttribute)mdModelEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMdModel_ImportSection()
  {
    return (EReference)mdModelEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMdModel_Bundle()
  {
    return (EReference)mdModelEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getMdBundle()
  {
    return mdBundleEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdBundle_Label()
  {
    return (EAttribute)mdBundleEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdBundle_TargetClass()
  {
    return (EAttribute)mdBundleEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdBundle_DocumentationFolder()
  {
    return (EAttribute)mdBundleEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdBundle_IdPrefix()
  {
    return (EAttribute)mdBundleEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMdBundle_Members()
  {
    return (EReference)mdBundleEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getMdBundleMember()
  {
    return mdBundleMemberEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdBundleMember_Name()
  {
    return (EAttribute)mdBundleMemberEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getMdGroupOrProperty()
  {
    return mdGroupOrPropertyEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getMdGroup()
  {
    return mdGroupEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMdGroup_Children()
  {
    return (EReference)mdGroupEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getMdProperty()
  {
    return mdPropertyEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdProperty_Deprecated()
  {
    return (EAttribute)mdPropertyEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdProperty_Advanced()
  {
    return (EAttribute)mdPropertyEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdProperty_Programmatic()
  {
    return (EAttribute)mdPropertyEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdProperty_Output()
  {
    return (EAttribute)mdPropertyEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdProperty_Global()
  {
    return (EAttribute)mdPropertyEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMdProperty_Type()
  {
    return (EReference)mdPropertyEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdProperty_Label()
  {
    return (EAttribute)mdPropertyEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdProperty_Description()
  {
    return (EAttribute)mdPropertyEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdProperty_Documentation()
  {
    return (EAttribute)mdPropertyEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMdProperty_DefaultValue()
  {
    return (EReference)mdPropertyEClass.getEStructuralFeatures().get(9);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMdProperty_LowerBound()
  {
    return (EReference)mdPropertyEClass.getEStructuralFeatures().get(10);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMdProperty_UpperBound()
  {
    return (EReference)mdPropertyEClass.getEStructuralFeatures().get(11);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdProperty_Targets()
  {
    return (EAttribute)mdPropertyEClass.getEStructuralFeatures().get(12);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdProperty_LegacyIds()
  {
    return (EAttribute)mdPropertyEClass.getEStructuralFeatures().get(13);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMdProperty_Dependencies()
  {
    return (EReference)mdPropertyEClass.getEStructuralFeatures().get(14);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getMdPropertyDependency()
  {
    return mdPropertyDependencyEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMdPropertyDependency_Target()
  {
    return (EReference)mdPropertyDependencyEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMdPropertyDependency_Value()
  {
    return (EReference)mdPropertyDependencyEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getMdAlgorithm()
  {
    return mdAlgorithmEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdAlgorithm_Deprecated()
  {
    return (EAttribute)mdAlgorithmEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMdAlgorithm_Provider()
  {
    return (EReference)mdAlgorithmEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdAlgorithm_Parameter()
  {
    return (EAttribute)mdAlgorithmEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdAlgorithm_Label()
  {
    return (EAttribute)mdAlgorithmEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdAlgorithm_Description()
  {
    return (EAttribute)mdAlgorithmEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdAlgorithm_Documentation()
  {
    return (EAttribute)mdAlgorithmEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMdAlgorithm_Category()
  {
    return (EReference)mdAlgorithmEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdAlgorithm_PreviewImage()
  {
    return (EAttribute)mdAlgorithmEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdAlgorithm_SupportedFeatures()
  {
    return (EAttribute)mdAlgorithmEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMdAlgorithm_SupportedOptions()
  {
    return (EReference)mdAlgorithmEClass.getEStructuralFeatures().get(9);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getMdCategory()
  {
    return mdCategoryEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdCategory_Deprecated()
  {
    return (EAttribute)mdCategoryEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdCategory_Label()
  {
    return (EAttribute)mdCategoryEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdCategory_Description()
  {
    return (EAttribute)mdCategoryEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdCategory_Documentation()
  {
    return (EAttribute)mdCategoryEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getMdPropertySupport()
  {
    return mdPropertySupportEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMdPropertySupport_Property()
  {
    return (EReference)mdPropertySupportEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMdPropertySupport_Value()
  {
    return (EReference)mdPropertySupportEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdPropertySupport_Duplicated()
  {
    return (EAttribute)mdPropertySupportEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMdPropertySupport_Documentation()
  {
    return (EAttribute)mdPropertySupportEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getMdPropertyTargetType()
  {
    return mdPropertyTargetTypeEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getMdGraphFeature()
  {
    return mdGraphFeatureEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MetaDataFactory getMetaDataFactory()
  {
    return (MetaDataFactory)getEFactoryInstance();
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
  public void createPackageContents()
  {
    if (isCreated) return;
    isCreated = true;

    // Create classes and their features
    mdModelEClass = createEClass(MD_MODEL);
    createEAttribute(mdModelEClass, MD_MODEL__NAME);
    createEReference(mdModelEClass, MD_MODEL__IMPORT_SECTION);
    createEReference(mdModelEClass, MD_MODEL__BUNDLE);

    mdBundleEClass = createEClass(MD_BUNDLE);
    createEAttribute(mdBundleEClass, MD_BUNDLE__LABEL);
    createEAttribute(mdBundleEClass, MD_BUNDLE__TARGET_CLASS);
    createEAttribute(mdBundleEClass, MD_BUNDLE__DOCUMENTATION_FOLDER);
    createEAttribute(mdBundleEClass, MD_BUNDLE__ID_PREFIX);
    createEReference(mdBundleEClass, MD_BUNDLE__MEMBERS);

    mdBundleMemberEClass = createEClass(MD_BUNDLE_MEMBER);
    createEAttribute(mdBundleMemberEClass, MD_BUNDLE_MEMBER__NAME);

    mdGroupOrPropertyEClass = createEClass(MD_GROUP_OR_PROPERTY);

    mdGroupEClass = createEClass(MD_GROUP);
    createEReference(mdGroupEClass, MD_GROUP__CHILDREN);

    mdPropertyEClass = createEClass(MD_PROPERTY);
    createEAttribute(mdPropertyEClass, MD_PROPERTY__DEPRECATED);
    createEAttribute(mdPropertyEClass, MD_PROPERTY__ADVANCED);
    createEAttribute(mdPropertyEClass, MD_PROPERTY__PROGRAMMATIC);
    createEAttribute(mdPropertyEClass, MD_PROPERTY__OUTPUT);
    createEAttribute(mdPropertyEClass, MD_PROPERTY__GLOBAL);
    createEReference(mdPropertyEClass, MD_PROPERTY__TYPE);
    createEAttribute(mdPropertyEClass, MD_PROPERTY__LABEL);
    createEAttribute(mdPropertyEClass, MD_PROPERTY__DESCRIPTION);
    createEAttribute(mdPropertyEClass, MD_PROPERTY__DOCUMENTATION);
    createEReference(mdPropertyEClass, MD_PROPERTY__DEFAULT_VALUE);
    createEReference(mdPropertyEClass, MD_PROPERTY__LOWER_BOUND);
    createEReference(mdPropertyEClass, MD_PROPERTY__UPPER_BOUND);
    createEAttribute(mdPropertyEClass, MD_PROPERTY__TARGETS);
    createEAttribute(mdPropertyEClass, MD_PROPERTY__LEGACY_IDS);
    createEReference(mdPropertyEClass, MD_PROPERTY__DEPENDENCIES);

    mdPropertyDependencyEClass = createEClass(MD_PROPERTY_DEPENDENCY);
    createEReference(mdPropertyDependencyEClass, MD_PROPERTY_DEPENDENCY__TARGET);
    createEReference(mdPropertyDependencyEClass, MD_PROPERTY_DEPENDENCY__VALUE);

    mdAlgorithmEClass = createEClass(MD_ALGORITHM);
    createEAttribute(mdAlgorithmEClass, MD_ALGORITHM__DEPRECATED);
    createEReference(mdAlgorithmEClass, MD_ALGORITHM__PROVIDER);
    createEAttribute(mdAlgorithmEClass, MD_ALGORITHM__PARAMETER);
    createEAttribute(mdAlgorithmEClass, MD_ALGORITHM__LABEL);
    createEAttribute(mdAlgorithmEClass, MD_ALGORITHM__DESCRIPTION);
    createEAttribute(mdAlgorithmEClass, MD_ALGORITHM__DOCUMENTATION);
    createEReference(mdAlgorithmEClass, MD_ALGORITHM__CATEGORY);
    createEAttribute(mdAlgorithmEClass, MD_ALGORITHM__PREVIEW_IMAGE);
    createEAttribute(mdAlgorithmEClass, MD_ALGORITHM__SUPPORTED_FEATURES);
    createEReference(mdAlgorithmEClass, MD_ALGORITHM__SUPPORTED_OPTIONS);

    mdCategoryEClass = createEClass(MD_CATEGORY);
    createEAttribute(mdCategoryEClass, MD_CATEGORY__DEPRECATED);
    createEAttribute(mdCategoryEClass, MD_CATEGORY__LABEL);
    createEAttribute(mdCategoryEClass, MD_CATEGORY__DESCRIPTION);
    createEAttribute(mdCategoryEClass, MD_CATEGORY__DOCUMENTATION);

    mdPropertySupportEClass = createEClass(MD_PROPERTY_SUPPORT);
    createEReference(mdPropertySupportEClass, MD_PROPERTY_SUPPORT__PROPERTY);
    createEReference(mdPropertySupportEClass, MD_PROPERTY_SUPPORT__VALUE);
    createEAttribute(mdPropertySupportEClass, MD_PROPERTY_SUPPORT__DUPLICATED);
    createEAttribute(mdPropertySupportEClass, MD_PROPERTY_SUPPORT__DOCUMENTATION);

    // Create enums
    mdPropertyTargetTypeEEnum = createEEnum(MD_PROPERTY_TARGET_TYPE);
    mdGraphFeatureEEnum = createEEnum(MD_GRAPH_FEATURE);
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
  public void initializePackageContents()
  {
    if (isInitialized) return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Obtain other dependent packages
    XtypePackage theXtypePackage = (XtypePackage)EPackage.Registry.INSTANCE.getEPackage(XtypePackage.eNS_URI);
    TypesPackage theTypesPackage = (TypesPackage)EPackage.Registry.INSTANCE.getEPackage(TypesPackage.eNS_URI);
    XbasePackage theXbasePackage = (XbasePackage)EPackage.Registry.INSTANCE.getEPackage(XbasePackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    mdGroupOrPropertyEClass.getESuperTypes().add(this.getMdBundleMember());
    mdGroupEClass.getESuperTypes().add(this.getMdGroupOrProperty());
    mdPropertyEClass.getESuperTypes().add(this.getMdGroupOrProperty());
    mdAlgorithmEClass.getESuperTypes().add(this.getMdBundleMember());
    mdCategoryEClass.getESuperTypes().add(this.getMdBundleMember());

    // Initialize classes and features; add operations and parameters
    initEClass(mdModelEClass, MdModel.class, "MdModel", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getMdModel_Name(), ecorePackage.getEString(), "name", null, 0, 1, MdModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMdModel_ImportSection(), theXtypePackage.getXImportSection(), null, "importSection", null, 0, 1, MdModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMdModel_Bundle(), this.getMdBundle(), null, "bundle", null, 0, 1, MdModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(mdBundleEClass, MdBundle.class, "MdBundle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getMdBundle_Label(), ecorePackage.getEString(), "label", null, 0, 1, MdBundle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMdBundle_TargetClass(), ecorePackage.getEString(), "targetClass", null, 0, 1, MdBundle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMdBundle_DocumentationFolder(), ecorePackage.getEString(), "documentationFolder", null, 0, 1, MdBundle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMdBundle_IdPrefix(), ecorePackage.getEString(), "idPrefix", null, 0, 1, MdBundle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMdBundle_Members(), this.getMdBundleMember(), null, "members", null, 0, -1, MdBundle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(mdBundleMemberEClass, MdBundleMember.class, "MdBundleMember", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getMdBundleMember_Name(), ecorePackage.getEString(), "name", null, 0, 1, MdBundleMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(mdGroupOrPropertyEClass, MdGroupOrProperty.class, "MdGroupOrProperty", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(mdGroupEClass, MdGroup.class, "MdGroup", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getMdGroup_Children(), this.getMdGroupOrProperty(), null, "children", null, 0, -1, MdGroup.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(mdPropertyEClass, MdProperty.class, "MdProperty", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getMdProperty_Deprecated(), ecorePackage.getEBoolean(), "deprecated", null, 0, 1, MdProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMdProperty_Advanced(), ecorePackage.getEBoolean(), "advanced", null, 0, 1, MdProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMdProperty_Programmatic(), ecorePackage.getEBoolean(), "programmatic", null, 0, 1, MdProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMdProperty_Output(), ecorePackage.getEBoolean(), "output", null, 0, 1, MdProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMdProperty_Global(), ecorePackage.getEBoolean(), "global", null, 0, 1, MdProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMdProperty_Type(), theTypesPackage.getJvmTypeReference(), null, "type", null, 0, 1, MdProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMdProperty_Label(), ecorePackage.getEString(), "label", null, 0, 1, MdProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMdProperty_Description(), ecorePackage.getEString(), "description", null, 0, 1, MdProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMdProperty_Documentation(), ecorePackage.getEString(), "documentation", null, 0, 1, MdProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMdProperty_DefaultValue(), theXbasePackage.getXExpression(), null, "defaultValue", null, 0, 1, MdProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMdProperty_LowerBound(), theXbasePackage.getXExpression(), null, "lowerBound", null, 0, 1, MdProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMdProperty_UpperBound(), theXbasePackage.getXExpression(), null, "upperBound", null, 0, 1, MdProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMdProperty_Targets(), this.getMdPropertyTargetType(), "targets", null, 0, -1, MdProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMdProperty_LegacyIds(), ecorePackage.getEString(), "legacyIds", null, 0, -1, MdProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMdProperty_Dependencies(), this.getMdPropertyDependency(), null, "dependencies", null, 0, -1, MdProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(mdPropertyDependencyEClass, MdPropertyDependency.class, "MdPropertyDependency", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getMdPropertyDependency_Target(), this.getMdProperty(), null, "target", null, 0, 1, MdPropertyDependency.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMdPropertyDependency_Value(), theXbasePackage.getXExpression(), null, "value", null, 0, 1, MdPropertyDependency.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(mdAlgorithmEClass, MdAlgorithm.class, "MdAlgorithm", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getMdAlgorithm_Deprecated(), ecorePackage.getEBoolean(), "deprecated", null, 0, 1, MdAlgorithm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMdAlgorithm_Provider(), theTypesPackage.getJvmTypeReference(), null, "provider", null, 0, 1, MdAlgorithm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMdAlgorithm_Parameter(), ecorePackage.getEString(), "parameter", null, 0, 1, MdAlgorithm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMdAlgorithm_Label(), ecorePackage.getEString(), "label", null, 0, 1, MdAlgorithm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMdAlgorithm_Description(), ecorePackage.getEString(), "description", null, 0, 1, MdAlgorithm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMdAlgorithm_Documentation(), ecorePackage.getEString(), "documentation", null, 0, 1, MdAlgorithm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMdAlgorithm_Category(), this.getMdCategory(), null, "category", null, 0, 1, MdAlgorithm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMdAlgorithm_PreviewImage(), ecorePackage.getEString(), "previewImage", null, 0, 1, MdAlgorithm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMdAlgorithm_SupportedFeatures(), this.getMdGraphFeature(), "supportedFeatures", null, 0, -1, MdAlgorithm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMdAlgorithm_SupportedOptions(), this.getMdPropertySupport(), null, "supportedOptions", null, 0, -1, MdAlgorithm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(mdCategoryEClass, MdCategory.class, "MdCategory", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getMdCategory_Deprecated(), ecorePackage.getEBoolean(), "deprecated", null, 0, 1, MdCategory.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMdCategory_Label(), ecorePackage.getEString(), "label", null, 0, 1, MdCategory.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMdCategory_Description(), ecorePackage.getEString(), "description", null, 0, 1, MdCategory.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMdCategory_Documentation(), ecorePackage.getEString(), "documentation", null, 0, 1, MdCategory.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(mdPropertySupportEClass, MdPropertySupport.class, "MdPropertySupport", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getMdPropertySupport_Property(), this.getMdProperty(), null, "property", null, 0, 1, MdPropertySupport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMdPropertySupport_Value(), theXbasePackage.getXExpression(), null, "value", null, 0, 1, MdPropertySupport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMdPropertySupport_Duplicated(), ecorePackage.getEBoolean(), "duplicated", null, 0, 1, MdPropertySupport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMdPropertySupport_Documentation(), ecorePackage.getEString(), "documentation", null, 0, 1, MdPropertySupport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize enums and add enum literals
    initEEnum(mdPropertyTargetTypeEEnum, MdPropertyTargetType.class, "MdPropertyTargetType");
    addEEnumLiteral(mdPropertyTargetTypeEEnum, MdPropertyTargetType.PARENTS);
    addEEnumLiteral(mdPropertyTargetTypeEEnum, MdPropertyTargetType.NODES);
    addEEnumLiteral(mdPropertyTargetTypeEEnum, MdPropertyTargetType.EDGES);
    addEEnumLiteral(mdPropertyTargetTypeEEnum, MdPropertyTargetType.PORTS);
    addEEnumLiteral(mdPropertyTargetTypeEEnum, MdPropertyTargetType.LABELS);

    initEEnum(mdGraphFeatureEEnum, MdGraphFeature.class, "MdGraphFeature");
    addEEnumLiteral(mdGraphFeatureEEnum, MdGraphFeature.SELF_LOOPS);
    addEEnumLiteral(mdGraphFeatureEEnum, MdGraphFeature.INSIDE_SELF_LOOPS);
    addEEnumLiteral(mdGraphFeatureEEnum, MdGraphFeature.MULTI_EDGES);
    addEEnumLiteral(mdGraphFeatureEEnum, MdGraphFeature.EDGE_LABELS);
    addEEnumLiteral(mdGraphFeatureEEnum, MdGraphFeature.PORTS);
    addEEnumLiteral(mdGraphFeatureEEnum, MdGraphFeature.COMPOUND);
    addEEnumLiteral(mdGraphFeatureEEnum, MdGraphFeature.CLUSTERS);
    addEEnumLiteral(mdGraphFeatureEEnum, MdGraphFeature.DISCONNECTED);

    // Create resource
    createResource(eNS_URI);
  }

} //MetaDataPackageImpl
