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

import org.eclipse.emf.ecore.EObject;

import org.eclipse.xtext.xbase.XExpression;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Md Property Support</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.elk.core.meta.metaData.MdPropertySupport#getProperty <em>Property</em>}</li>
 *   <li>{@link org.eclipse.elk.core.meta.metaData.MdPropertySupport#getValue <em>Value</em>}</li>
 *   <li>{@link org.eclipse.elk.core.meta.metaData.MdPropertySupport#isDuplicated <em>Duplicated</em>}</li>
 * </ul>
 *
 * @see org.eclipse.elk.core.meta.metaData.MetaDataPackage#getMdPropertySupport()
 * @model
 * @generated
 */
public interface MdPropertySupport extends EObject
{
  /**
   * Returns the value of the '<em><b>Property</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Property</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Property</em>' reference.
   * @see #setProperty(MdProperty)
   * @see org.eclipse.elk.core.meta.metaData.MetaDataPackage#getMdPropertySupport_Property()
   * @model
   * @generated
   */
  MdProperty getProperty();

  /**
   * Sets the value of the '{@link org.eclipse.elk.core.meta.metaData.MdPropertySupport#getProperty <em>Property</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Property</em>' reference.
   * @see #getProperty()
   * @generated
   */
  void setProperty(MdProperty value);

  /**
   * Returns the value of the '<em><b>Value</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Value</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Value</em>' containment reference.
   * @see #setValue(XExpression)
   * @see org.eclipse.elk.core.meta.metaData.MetaDataPackage#getMdPropertySupport_Value()
   * @model containment="true"
   * @generated
   */
  XExpression getValue();

  /**
   * Sets the value of the '{@link org.eclipse.elk.core.meta.metaData.MdPropertySupport#getValue <em>Value</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Value</em>' containment reference.
   * @see #getValue()
   * @generated
   */
  void setValue(XExpression value);

  /**
   * Returns the value of the '<em><b>Duplicated</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Duplicated</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Duplicated</em>' attribute.
   * @see #setDuplicated(boolean)
   * @see org.eclipse.elk.core.meta.metaData.MetaDataPackage#getMdPropertySupport_Duplicated()
   * @model
   * @generated
   */
  boolean isDuplicated();

  /**
   * Sets the value of the '{@link org.eclipse.elk.core.meta.metaData.MdPropertySupport#isDuplicated <em>Duplicated</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Duplicated</em>' attribute.
   * @see #isDuplicated()
   * @generated
   */
  void setDuplicated(boolean value);

} // MdPropertySupport
