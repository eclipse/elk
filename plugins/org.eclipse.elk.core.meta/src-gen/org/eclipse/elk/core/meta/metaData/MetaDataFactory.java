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

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.elk.core.meta.metaData.MetaDataPackage
 * @generated
 */
public interface MetaDataFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  MetaDataFactory eINSTANCE = org.eclipse.elk.core.meta.metaData.impl.MetaDataFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Md Model</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Md Model</em>'.
   * @generated
   */
  MdModel createMdModel();

  /**
   * Returns a new object of class '<em>Md Bundle</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Md Bundle</em>'.
   * @generated
   */
  MdBundle createMdBundle();

  /**
   * Returns a new object of class '<em>Md Bundle Member</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Md Bundle Member</em>'.
   * @generated
   */
  MdBundleMember createMdBundleMember();

  /**
   * Returns a new object of class '<em>Md Group Or Property</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Md Group Or Property</em>'.
   * @generated
   */
  MdGroupOrProperty createMdGroupOrProperty();

  /**
   * Returns a new object of class '<em>Md Group</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Md Group</em>'.
   * @generated
   */
  MdGroup createMdGroup();

  /**
   * Returns a new object of class '<em>Md Property</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Md Property</em>'.
   * @generated
   */
  MdProperty createMdProperty();

  /**
   * Returns a new object of class '<em>Md Property Dependency</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Md Property Dependency</em>'.
   * @generated
   */
  MdPropertyDependency createMdPropertyDependency();

  /**
   * Returns a new object of class '<em>Md Algorithm</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Md Algorithm</em>'.
   * @generated
   */
  MdAlgorithm createMdAlgorithm();

  /**
   * Returns a new object of class '<em>Md Category</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Md Category</em>'.
   * @generated
   */
  MdCategory createMdCategory();

  /**
   * Returns a new object of class '<em>Md Property Support</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Md Property Support</em>'.
   * @generated
   */
  MdPropertySupport createMdPropertySupport();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  MetaDataPackage getMetaDataPackage();

} //MetaDataFactory
