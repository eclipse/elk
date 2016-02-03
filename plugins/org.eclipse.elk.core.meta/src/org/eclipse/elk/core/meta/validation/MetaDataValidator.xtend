/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.meta.validation

import java.util.Map
import org.eclipse.elk.core.meta.metaData.MdBundle
import org.eclipse.elk.core.meta.metaData.MdBundleMember
import org.eclipse.elk.core.meta.metaData.MdPropertySupport
import org.eclipse.xtext.validation.Check

import static org.eclipse.elk.core.meta.metaData.MetaDataPackage.Literals.*
import org.eclipse.elk.core.meta.metaData.MdAlgorithm

/**
 * This class contains custom validation rules. 
 *
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 */
class MetaDataValidator extends AbstractMetaDataValidator {
	
    @Check
    def void checkDuplicateMemberId(MdBundle bundle) {
        val Map<String, MdBundleMember> ids = newHashMap
        for (member : bundle.members) {
            if (ids.containsKey(member.name)) {
                val otherMember = ids.get(member.name)
                if (otherMember !== null) {
                    duplicateName(otherMember)
                    // The first occurrence should be marked only once
                    ids.put(member.name, null)
                }
                duplicateName(member)
            } else {
                ids.put(member.name, member)
            }
        }
    }
    
    def void duplicateName(MdBundleMember member) {
        error("The id '" + member.name + "' is already used.", member, MD_BUNDLE_MEMBER__NAME)
    }
    
    @Check
    def void checkDuplicateOption(MdPropertySupport support) {
        if (support.property !== null && support.duplicated) {
            val algorithm = support.eContainer as MdAlgorithm
            if (algorithm.eContainer == support.property.eContainer)
                error("A property defined in the same bundle cannot be duplicated.", MD_PROPERTY_SUPPORT__DUPLICATED)
        }
    }
	
}
