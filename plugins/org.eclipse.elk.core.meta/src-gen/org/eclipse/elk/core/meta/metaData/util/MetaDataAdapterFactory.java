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
package org.eclipse.elk.core.meta.metaData.util;

import org.eclipse.elk.core.meta.metaData.*;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.elk.core.meta.metaData.MetaDataPackage
 * @generated
 */
public class MetaDataAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static MetaDataPackage modelPackage;

  /**
   * Creates an instance of the adapter factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MetaDataAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = MetaDataPackage.eINSTANCE;
    }
  }

  /**
   * Returns whether this factory is applicable for the type of the object.
   * <!-- begin-user-doc -->
   * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
   * <!-- end-user-doc -->
   * @return whether this factory is applicable for the type of the object.
   * @generated
   */
  @Override
  public boolean isFactoryForType(Object object)
  {
    if (object == modelPackage)
    {
      return true;
    }
    if (object instanceof EObject)
    {
      return ((EObject)object).eClass().getEPackage() == modelPackage;
    }
    return false;
  }

  /**
   * The switch that delegates to the <code>createXXX</code> methods.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected MetaDataSwitch<Adapter> modelSwitch =
    new MetaDataSwitch<Adapter>()
    {
      @Override
      public Adapter caseMdModel(MdModel object)
      {
        return createMdModelAdapter();
      }
      @Override
      public Adapter caseMdBundle(MdBundle object)
      {
        return createMdBundleAdapter();
      }
      @Override
      public Adapter caseMdBundleMember(MdBundleMember object)
      {
        return createMdBundleMemberAdapter();
      }
      @Override
      public Adapter caseMdGroupOrProperty(MdGroupOrProperty object)
      {
        return createMdGroupOrPropertyAdapter();
      }
      @Override
      public Adapter caseMdGroup(MdGroup object)
      {
        return createMdGroupAdapter();
      }
      @Override
      public Adapter caseMdProperty(MdProperty object)
      {
        return createMdPropertyAdapter();
      }
      @Override
      public Adapter caseMdPropertyDependency(MdPropertyDependency object)
      {
        return createMdPropertyDependencyAdapter();
      }
      @Override
      public Adapter caseMdAlgorithm(MdAlgorithm object)
      {
        return createMdAlgorithmAdapter();
      }
      @Override
      public Adapter caseMdCategory(MdCategory object)
      {
        return createMdCategoryAdapter();
      }
      @Override
      public Adapter caseMdPropertySupport(MdPropertySupport object)
      {
        return createMdPropertySupportAdapter();
      }
      @Override
      public Adapter defaultCase(EObject object)
      {
        return createEObjectAdapter();
      }
    };

  /**
   * Creates an adapter for the <code>target</code>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param target the object to adapt.
   * @return the adapter for the <code>target</code>.
   * @generated
   */
  @Override
  public Adapter createAdapter(Notifier target)
  {
    return modelSwitch.doSwitch((EObject)target);
  }


  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.elk.core.meta.metaData.MdModel <em>Md Model</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.elk.core.meta.metaData.MdModel
   * @generated
   */
  public Adapter createMdModelAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.elk.core.meta.metaData.MdBundle <em>Md Bundle</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.elk.core.meta.metaData.MdBundle
   * @generated
   */
  public Adapter createMdBundleAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.elk.core.meta.metaData.MdBundleMember <em>Md Bundle Member</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.elk.core.meta.metaData.MdBundleMember
   * @generated
   */
  public Adapter createMdBundleMemberAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.elk.core.meta.metaData.MdGroupOrProperty <em>Md Group Or Property</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.elk.core.meta.metaData.MdGroupOrProperty
   * @generated
   */
  public Adapter createMdGroupOrPropertyAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.elk.core.meta.metaData.MdGroup <em>Md Group</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.elk.core.meta.metaData.MdGroup
   * @generated
   */
  public Adapter createMdGroupAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.elk.core.meta.metaData.MdProperty <em>Md Property</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.elk.core.meta.metaData.MdProperty
   * @generated
   */
  public Adapter createMdPropertyAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.elk.core.meta.metaData.MdPropertyDependency <em>Md Property Dependency</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.elk.core.meta.metaData.MdPropertyDependency
   * @generated
   */
  public Adapter createMdPropertyDependencyAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.elk.core.meta.metaData.MdAlgorithm <em>Md Algorithm</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.elk.core.meta.metaData.MdAlgorithm
   * @generated
   */
  public Adapter createMdAlgorithmAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.elk.core.meta.metaData.MdCategory <em>Md Category</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.elk.core.meta.metaData.MdCategory
   * @generated
   */
  public Adapter createMdCategoryAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.elk.core.meta.metaData.MdPropertySupport <em>Md Property Support</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.elk.core.meta.metaData.MdPropertySupport
   * @generated
   */
  public Adapter createMdPropertySupportAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for the default case.
   * <!-- begin-user-doc -->
   * This default implementation returns null.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @generated
   */
  public Adapter createEObjectAdapter()
  {
    return null;
  }

} //MetaDataAdapterFactory
