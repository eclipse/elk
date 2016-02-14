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

import java.util.Collection;

import org.eclipse.elk.core.meta.metaData.MdProperty;
import org.eclipse.elk.core.meta.metaData.MdPropertyDependency;
import org.eclipse.elk.core.meta.metaData.MdPropertyTargetType;
import org.eclipse.elk.core.meta.metaData.MetaDataPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.xtext.common.types.JvmTypeReference;

import org.eclipse.xtext.xbase.XExpression;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Md Property</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.elk.core.meta.metaData.impl.MdPropertyImpl#isAdvanced <em>Advanced</em>}</li>
 *   <li>{@link org.eclipse.elk.core.meta.metaData.impl.MdPropertyImpl#isProgrammatic <em>Programmatic</em>}</li>
 *   <li>{@link org.eclipse.elk.core.meta.metaData.impl.MdPropertyImpl#isOutput <em>Output</em>}</li>
 *   <li>{@link org.eclipse.elk.core.meta.metaData.impl.MdPropertyImpl#isGlobal <em>Global</em>}</li>
 *   <li>{@link org.eclipse.elk.core.meta.metaData.impl.MdPropertyImpl#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.elk.core.meta.metaData.impl.MdPropertyImpl#getDefaultValue <em>Default Value</em>}</li>
 *   <li>{@link org.eclipse.elk.core.meta.metaData.impl.MdPropertyImpl#getTargets <em>Targets</em>}</li>
 *   <li>{@link org.eclipse.elk.core.meta.metaData.impl.MdPropertyImpl#getLegacyIds <em>Legacy Ids</em>}</li>
 *   <li>{@link org.eclipse.elk.core.meta.metaData.impl.MdPropertyImpl#getDependencies <em>Dependencies</em>}</li>
 * </ul>
 *
 * @generated
 */
public class MdPropertyImpl extends MdBundleMemberImpl implements MdProperty
{
  /**
   * The default value of the '{@link #isAdvanced() <em>Advanced</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isAdvanced()
   * @generated
   * @ordered
   */
  protected static final boolean ADVANCED_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isAdvanced() <em>Advanced</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isAdvanced()
   * @generated
   * @ordered
   */
  protected boolean advanced = ADVANCED_EDEFAULT;

  /**
   * The default value of the '{@link #isProgrammatic() <em>Programmatic</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isProgrammatic()
   * @generated
   * @ordered
   */
  protected static final boolean PROGRAMMATIC_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isProgrammatic() <em>Programmatic</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isProgrammatic()
   * @generated
   * @ordered
   */
  protected boolean programmatic = PROGRAMMATIC_EDEFAULT;

  /**
   * The default value of the '{@link #isOutput() <em>Output</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isOutput()
   * @generated
   * @ordered
   */
  protected static final boolean OUTPUT_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isOutput() <em>Output</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isOutput()
   * @generated
   * @ordered
   */
  protected boolean output = OUTPUT_EDEFAULT;

  /**
   * The default value of the '{@link #isGlobal() <em>Global</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isGlobal()
   * @generated
   * @ordered
   */
  protected static final boolean GLOBAL_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isGlobal() <em>Global</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isGlobal()
   * @generated
   * @ordered
   */
  protected boolean global = GLOBAL_EDEFAULT;

  /**
   * The cached value of the '{@link #getType() <em>Type</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getType()
   * @generated
   * @ordered
   */
  protected JvmTypeReference type;

  /**
   * The cached value of the '{@link #getDefaultValue() <em>Default Value</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDefaultValue()
   * @generated
   * @ordered
   */
  protected XExpression defaultValue;

  /**
   * The cached value of the '{@link #getTargets() <em>Targets</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTargets()
   * @generated
   * @ordered
   */
  protected EList<MdPropertyTargetType> targets;

  /**
   * The cached value of the '{@link #getLegacyIds() <em>Legacy Ids</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLegacyIds()
   * @generated
   * @ordered
   */
  protected EList<String> legacyIds;

  /**
   * The cached value of the '{@link #getDependencies() <em>Dependencies</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDependencies()
   * @generated
   * @ordered
   */
  protected EList<MdPropertyDependency> dependencies;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected MdPropertyImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return MetaDataPackage.Literals.MD_PROPERTY;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isAdvanced()
  {
    return advanced;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setAdvanced(boolean newAdvanced)
  {
    boolean oldAdvanced = advanced;
    advanced = newAdvanced;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MetaDataPackage.MD_PROPERTY__ADVANCED, oldAdvanced, advanced));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isProgrammatic()
  {
    return programmatic;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setProgrammatic(boolean newProgrammatic)
  {
    boolean oldProgrammatic = programmatic;
    programmatic = newProgrammatic;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MetaDataPackage.MD_PROPERTY__PROGRAMMATIC, oldProgrammatic, programmatic));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isOutput()
  {
    return output;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setOutput(boolean newOutput)
  {
    boolean oldOutput = output;
    output = newOutput;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MetaDataPackage.MD_PROPERTY__OUTPUT, oldOutput, output));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isGlobal()
  {
    return global;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setGlobal(boolean newGlobal)
  {
    boolean oldGlobal = global;
    global = newGlobal;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MetaDataPackage.MD_PROPERTY__GLOBAL, oldGlobal, global));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public JvmTypeReference getType()
  {
    return type;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetType(JvmTypeReference newType, NotificationChain msgs)
  {
    JvmTypeReference oldType = type;
    type = newType;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MetaDataPackage.MD_PROPERTY__TYPE, oldType, newType);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setType(JvmTypeReference newType)
  {
    if (newType != type)
    {
      NotificationChain msgs = null;
      if (type != null)
        msgs = ((InternalEObject)type).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MetaDataPackage.MD_PROPERTY__TYPE, null, msgs);
      if (newType != null)
        msgs = ((InternalEObject)newType).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MetaDataPackage.MD_PROPERTY__TYPE, null, msgs);
      msgs = basicSetType(newType, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MetaDataPackage.MD_PROPERTY__TYPE, newType, newType));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XExpression getDefaultValue()
  {
    return defaultValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetDefaultValue(XExpression newDefaultValue, NotificationChain msgs)
  {
    XExpression oldDefaultValue = defaultValue;
    defaultValue = newDefaultValue;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MetaDataPackage.MD_PROPERTY__DEFAULT_VALUE, oldDefaultValue, newDefaultValue);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setDefaultValue(XExpression newDefaultValue)
  {
    if (newDefaultValue != defaultValue)
    {
      NotificationChain msgs = null;
      if (defaultValue != null)
        msgs = ((InternalEObject)defaultValue).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MetaDataPackage.MD_PROPERTY__DEFAULT_VALUE, null, msgs);
      if (newDefaultValue != null)
        msgs = ((InternalEObject)newDefaultValue).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MetaDataPackage.MD_PROPERTY__DEFAULT_VALUE, null, msgs);
      msgs = basicSetDefaultValue(newDefaultValue, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MetaDataPackage.MD_PROPERTY__DEFAULT_VALUE, newDefaultValue, newDefaultValue));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<MdPropertyTargetType> getTargets()
  {
    if (targets == null)
    {
      targets = new EDataTypeEList<MdPropertyTargetType>(MdPropertyTargetType.class, this, MetaDataPackage.MD_PROPERTY__TARGETS);
    }
    return targets;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<String> getLegacyIds()
  {
    if (legacyIds == null)
    {
      legacyIds = new EDataTypeEList<String>(String.class, this, MetaDataPackage.MD_PROPERTY__LEGACY_IDS);
    }
    return legacyIds;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<MdPropertyDependency> getDependencies()
  {
    if (dependencies == null)
    {
      dependencies = new EObjectContainmentEList<MdPropertyDependency>(MdPropertyDependency.class, this, MetaDataPackage.MD_PROPERTY__DEPENDENCIES);
    }
    return dependencies;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case MetaDataPackage.MD_PROPERTY__TYPE:
        return basicSetType(null, msgs);
      case MetaDataPackage.MD_PROPERTY__DEFAULT_VALUE:
        return basicSetDefaultValue(null, msgs);
      case MetaDataPackage.MD_PROPERTY__DEPENDENCIES:
        return ((InternalEList<?>)getDependencies()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case MetaDataPackage.MD_PROPERTY__ADVANCED:
        return isAdvanced();
      case MetaDataPackage.MD_PROPERTY__PROGRAMMATIC:
        return isProgrammatic();
      case MetaDataPackage.MD_PROPERTY__OUTPUT:
        return isOutput();
      case MetaDataPackage.MD_PROPERTY__GLOBAL:
        return isGlobal();
      case MetaDataPackage.MD_PROPERTY__TYPE:
        return getType();
      case MetaDataPackage.MD_PROPERTY__DEFAULT_VALUE:
        return getDefaultValue();
      case MetaDataPackage.MD_PROPERTY__TARGETS:
        return getTargets();
      case MetaDataPackage.MD_PROPERTY__LEGACY_IDS:
        return getLegacyIds();
      case MetaDataPackage.MD_PROPERTY__DEPENDENCIES:
        return getDependencies();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case MetaDataPackage.MD_PROPERTY__ADVANCED:
        setAdvanced((Boolean)newValue);
        return;
      case MetaDataPackage.MD_PROPERTY__PROGRAMMATIC:
        setProgrammatic((Boolean)newValue);
        return;
      case MetaDataPackage.MD_PROPERTY__OUTPUT:
        setOutput((Boolean)newValue);
        return;
      case MetaDataPackage.MD_PROPERTY__GLOBAL:
        setGlobal((Boolean)newValue);
        return;
      case MetaDataPackage.MD_PROPERTY__TYPE:
        setType((JvmTypeReference)newValue);
        return;
      case MetaDataPackage.MD_PROPERTY__DEFAULT_VALUE:
        setDefaultValue((XExpression)newValue);
        return;
      case MetaDataPackage.MD_PROPERTY__TARGETS:
        getTargets().clear();
        getTargets().addAll((Collection<? extends MdPropertyTargetType>)newValue);
        return;
      case MetaDataPackage.MD_PROPERTY__LEGACY_IDS:
        getLegacyIds().clear();
        getLegacyIds().addAll((Collection<? extends String>)newValue);
        return;
      case MetaDataPackage.MD_PROPERTY__DEPENDENCIES:
        getDependencies().clear();
        getDependencies().addAll((Collection<? extends MdPropertyDependency>)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case MetaDataPackage.MD_PROPERTY__ADVANCED:
        setAdvanced(ADVANCED_EDEFAULT);
        return;
      case MetaDataPackage.MD_PROPERTY__PROGRAMMATIC:
        setProgrammatic(PROGRAMMATIC_EDEFAULT);
        return;
      case MetaDataPackage.MD_PROPERTY__OUTPUT:
        setOutput(OUTPUT_EDEFAULT);
        return;
      case MetaDataPackage.MD_PROPERTY__GLOBAL:
        setGlobal(GLOBAL_EDEFAULT);
        return;
      case MetaDataPackage.MD_PROPERTY__TYPE:
        setType((JvmTypeReference)null);
        return;
      case MetaDataPackage.MD_PROPERTY__DEFAULT_VALUE:
        setDefaultValue((XExpression)null);
        return;
      case MetaDataPackage.MD_PROPERTY__TARGETS:
        getTargets().clear();
        return;
      case MetaDataPackage.MD_PROPERTY__LEGACY_IDS:
        getLegacyIds().clear();
        return;
      case MetaDataPackage.MD_PROPERTY__DEPENDENCIES:
        getDependencies().clear();
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case MetaDataPackage.MD_PROPERTY__ADVANCED:
        return advanced != ADVANCED_EDEFAULT;
      case MetaDataPackage.MD_PROPERTY__PROGRAMMATIC:
        return programmatic != PROGRAMMATIC_EDEFAULT;
      case MetaDataPackage.MD_PROPERTY__OUTPUT:
        return output != OUTPUT_EDEFAULT;
      case MetaDataPackage.MD_PROPERTY__GLOBAL:
        return global != GLOBAL_EDEFAULT;
      case MetaDataPackage.MD_PROPERTY__TYPE:
        return type != null;
      case MetaDataPackage.MD_PROPERTY__DEFAULT_VALUE:
        return defaultValue != null;
      case MetaDataPackage.MD_PROPERTY__TARGETS:
        return targets != null && !targets.isEmpty();
      case MetaDataPackage.MD_PROPERTY__LEGACY_IDS:
        return legacyIds != null && !legacyIds.isEmpty();
      case MetaDataPackage.MD_PROPERTY__DEPENDENCIES:
        return dependencies != null && !dependencies.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (advanced: ");
    result.append(advanced);
    result.append(", programmatic: ");
    result.append(programmatic);
    result.append(", output: ");
    result.append(output);
    result.append(", global: ");
    result.append(global);
    result.append(", targets: ");
    result.append(targets);
    result.append(", legacyIds: ");
    result.append(legacyIds);
    result.append(')');
    return result.toString();
  }

} //MdPropertyImpl
