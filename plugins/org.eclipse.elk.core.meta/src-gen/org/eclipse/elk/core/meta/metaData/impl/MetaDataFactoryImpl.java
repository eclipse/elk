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

import org.eclipse.elk.core.meta.metaData.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class MetaDataFactoryImpl extends EFactoryImpl implements MetaDataFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static MetaDataFactory init()
  {
    try
    {
      MetaDataFactory theMetaDataFactory = (MetaDataFactory)EPackage.Registry.INSTANCE.getEFactory(MetaDataPackage.eNS_URI);
      if (theMetaDataFactory != null)
      {
        return theMetaDataFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new MetaDataFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MetaDataFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
      case MetaDataPackage.MD_MODEL: return createMdModel();
      case MetaDataPackage.MD_BUNDLE: return createMdBundle();
      case MetaDataPackage.MD_BUNDLE_MEMBER: return createMdBundleMember();
      case MetaDataPackage.MD_PROPERTY: return createMdProperty();
      case MetaDataPackage.MD_PROPERTY_DEPENDENCY: return createMdPropertyDependency();
      case MetaDataPackage.MD_ALGORITHM: return createMdAlgorithm();
      case MetaDataPackage.MD_CATEGORY: return createMdCategory();
      case MetaDataPackage.MD_PROPERTY_SUPPORT: return createMdPropertySupport();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
      case MetaDataPackage.MD_PROPERTY_TARGET_TYPE:
        return createMdPropertyTargetTypeFromString(eDataType, initialValue);
      case MetaDataPackage.MD_GRAPH_FEATURE:
        return createMdGraphFeatureFromString(eDataType, initialValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
      case MetaDataPackage.MD_PROPERTY_TARGET_TYPE:
        return convertMdPropertyTargetTypeToString(eDataType, instanceValue);
      case MetaDataPackage.MD_GRAPH_FEATURE:
        return convertMdGraphFeatureToString(eDataType, instanceValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MdModel createMdModel()
  {
    MdModelImpl mdModel = new MdModelImpl();
    return mdModel;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MdBundle createMdBundle()
  {
    MdBundleImpl mdBundle = new MdBundleImpl();
    return mdBundle;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MdBundleMember createMdBundleMember()
  {
    MdBundleMemberImpl mdBundleMember = new MdBundleMemberImpl();
    return mdBundleMember;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MdProperty createMdProperty()
  {
    MdPropertyImpl mdProperty = new MdPropertyImpl();
    return mdProperty;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MdPropertyDependency createMdPropertyDependency()
  {
    MdPropertyDependencyImpl mdPropertyDependency = new MdPropertyDependencyImpl();
    return mdPropertyDependency;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MdAlgorithm createMdAlgorithm()
  {
    MdAlgorithmImpl mdAlgorithm = new MdAlgorithmImpl();
    return mdAlgorithm;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MdCategory createMdCategory()
  {
    MdCategoryImpl mdCategory = new MdCategoryImpl();
    return mdCategory;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MdPropertySupport createMdPropertySupport()
  {
    MdPropertySupportImpl mdPropertySupport = new MdPropertySupportImpl();
    return mdPropertySupport;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MdPropertyTargetType createMdPropertyTargetTypeFromString(EDataType eDataType, String initialValue)
  {
    MdPropertyTargetType result = MdPropertyTargetType.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertMdPropertyTargetTypeToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MdGraphFeature createMdGraphFeatureFromString(EDataType eDataType, String initialValue)
  {
    MdGraphFeature result = MdGraphFeature.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertMdGraphFeatureToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MetaDataPackage getMetaDataPackage()
  {
    return (MetaDataPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static MetaDataPackage getPackage()
  {
    return MetaDataPackage.eINSTANCE;
  }

} //MetaDataFactoryImpl
