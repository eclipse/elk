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

import org.eclipse.emf.common.util.EList;

import org.eclipse.xtext.common.types.JvmTypeReference;

import org.eclipse.xtext.xbase.XExpression;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Md Property</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.elk.core.meta.metaData.MdProperty#isAdvanced <em>Advanced</em>}</li>
 *   <li>{@link org.eclipse.elk.core.meta.metaData.MdProperty#isProgrammatic <em>Programmatic</em>}</li>
 *   <li>{@link org.eclipse.elk.core.meta.metaData.MdProperty#isOutput <em>Output</em>}</li>
 *   <li>{@link org.eclipse.elk.core.meta.metaData.MdProperty#isGlobal <em>Global</em>}</li>
 *   <li>{@link org.eclipse.elk.core.meta.metaData.MdProperty#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.elk.core.meta.metaData.MdProperty#getDefaultValue <em>Default Value</em>}</li>
 *   <li>{@link org.eclipse.elk.core.meta.metaData.MdProperty#getTargets <em>Targets</em>}</li>
 *   <li>{@link org.eclipse.elk.core.meta.metaData.MdProperty#getLegacyIds <em>Legacy Ids</em>}</li>
 *   <li>{@link org.eclipse.elk.core.meta.metaData.MdProperty#getDependencies <em>Dependencies</em>}</li>
 * </ul>
 *
 * @see org.eclipse.elk.core.meta.metaData.MetaDataPackage#getMdProperty()
 * @model
 * @generated
 */
public interface MdProperty extends MdBundleMember
{
  /**
   * Returns the value of the '<em><b>Advanced</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Advanced</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Advanced</em>' attribute.
   * @see #setAdvanced(boolean)
   * @see org.eclipse.elk.core.meta.metaData.MetaDataPackage#getMdProperty_Advanced()
   * @model
   * @generated
   */
  boolean isAdvanced();

  /**
   * Sets the value of the '{@link org.eclipse.elk.core.meta.metaData.MdProperty#isAdvanced <em>Advanced</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Advanced</em>' attribute.
   * @see #isAdvanced()
   * @generated
   */
  void setAdvanced(boolean value);

  /**
   * Returns the value of the '<em><b>Programmatic</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Programmatic</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Programmatic</em>' attribute.
   * @see #setProgrammatic(boolean)
   * @see org.eclipse.elk.core.meta.metaData.MetaDataPackage#getMdProperty_Programmatic()
   * @model
   * @generated
   */
  boolean isProgrammatic();

  /**
   * Sets the value of the '{@link org.eclipse.elk.core.meta.metaData.MdProperty#isProgrammatic <em>Programmatic</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Programmatic</em>' attribute.
   * @see #isProgrammatic()
   * @generated
   */
  void setProgrammatic(boolean value);

  /**
   * Returns the value of the '<em><b>Output</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Output</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Output</em>' attribute.
   * @see #setOutput(boolean)
   * @see org.eclipse.elk.core.meta.metaData.MetaDataPackage#getMdProperty_Output()
   * @model
   * @generated
   */
  boolean isOutput();

  /**
   * Sets the value of the '{@link org.eclipse.elk.core.meta.metaData.MdProperty#isOutput <em>Output</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Output</em>' attribute.
   * @see #isOutput()
   * @generated
   */
  void setOutput(boolean value);

  /**
   * Returns the value of the '<em><b>Global</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Global</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Global</em>' attribute.
   * @see #setGlobal(boolean)
   * @see org.eclipse.elk.core.meta.metaData.MetaDataPackage#getMdProperty_Global()
   * @model
   * @generated
   */
  boolean isGlobal();

  /**
   * Sets the value of the '{@link org.eclipse.elk.core.meta.metaData.MdProperty#isGlobal <em>Global</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Global</em>' attribute.
   * @see #isGlobal()
   * @generated
   */
  void setGlobal(boolean value);

  /**
   * Returns the value of the '<em><b>Type</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Type</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Type</em>' containment reference.
   * @see #setType(JvmTypeReference)
   * @see org.eclipse.elk.core.meta.metaData.MetaDataPackage#getMdProperty_Type()
   * @model containment="true"
   * @generated
   */
  JvmTypeReference getType();

  /**
   * Sets the value of the '{@link org.eclipse.elk.core.meta.metaData.MdProperty#getType <em>Type</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Type</em>' containment reference.
   * @see #getType()
   * @generated
   */
  void setType(JvmTypeReference value);

  /**
   * Returns the value of the '<em><b>Default Value</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Default Value</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Default Value</em>' containment reference.
   * @see #setDefaultValue(XExpression)
   * @see org.eclipse.elk.core.meta.metaData.MetaDataPackage#getMdProperty_DefaultValue()
   * @model containment="true"
   * @generated
   */
  XExpression getDefaultValue();

  /**
   * Sets the value of the '{@link org.eclipse.elk.core.meta.metaData.MdProperty#getDefaultValue <em>Default Value</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Default Value</em>' containment reference.
   * @see #getDefaultValue()
   * @generated
   */
  void setDefaultValue(XExpression value);

  /**
   * Returns the value of the '<em><b>Targets</b></em>' attribute list.
   * The list contents are of type {@link org.eclipse.elk.core.meta.metaData.MdPropertyTargetType}.
   * The literals are from the enumeration {@link org.eclipse.elk.core.meta.metaData.MdPropertyTargetType}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Targets</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Targets</em>' attribute list.
   * @see org.eclipse.elk.core.meta.metaData.MdPropertyTargetType
   * @see org.eclipse.elk.core.meta.metaData.MetaDataPackage#getMdProperty_Targets()
   * @model unique="false"
   * @generated
   */
  EList<MdPropertyTargetType> getTargets();

  /**
   * Returns the value of the '<em><b>Legacy Ids</b></em>' attribute list.
   * The list contents are of type {@link java.lang.String}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Legacy Ids</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Legacy Ids</em>' attribute list.
   * @see org.eclipse.elk.core.meta.metaData.MetaDataPackage#getMdProperty_LegacyIds()
   * @model unique="false"
   * @generated
   */
  EList<String> getLegacyIds();

  /**
   * Returns the value of the '<em><b>Dependencies</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.elk.core.meta.metaData.MdPropertyDependency}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Dependencies</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Dependencies</em>' containment reference list.
   * @see org.eclipse.elk.core.meta.metaData.MetaDataPackage#getMdProperty_Dependencies()
   * @model containment="true"
   * @generated
   */
  EList<MdPropertyDependency> getDependencies();

} // MdProperty
