/*******************************************************************************
 * Copyright (c) 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0  
 *******************************************************************************/
package org.eclipse.elk.graph.text

import com.google.inject.Inject
import org.eclipse.elk.core.options.CoreOptions
import org.eclipse.elk.core.util.IndividualSpacings
import org.eclipse.elk.graph.ElkNode
import org.eclipse.elk.graph.impl.ElkPropertyToValueMapEntryImpl
import org.eclipse.elk.graph.properties.IProperty
import org.eclipse.elk.graph.text.services.ElkGraphGrammarAccess
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.conversion.ValueConverterException
import org.eclipse.xtext.nodemodel.INode
import org.eclipse.xtext.parser.DefaultEcoreElementFactory

/**
 * Custom AstFactory that moves specified individual spacing layout options 
 * to a dedicated container.
 */
class ElkGraphAstFactory extends DefaultEcoreElementFactory {

    @Inject ElkGraphGrammarAccess grammarAccess

    override add(EObject object, String feature, Object value, String ruleName,
        INode node) throws ValueConverterException {

        val individualSpacingRuleName = grammarAccess.grammar.name + "." +
            grammarAccess.individualSpacingPropertyRule.name
        if (ruleName == individualSpacingRuleName) {
            val elkNode = object as ElkNode
            if (!elkNode.hasProperty(CoreOptions.SPACING_INDIVIDUAL_OVERRIDE)) {
                elkNode.setProperty(CoreOptions.SPACING_INDIVIDUAL_OVERRIDE, new IndividualSpacings())
            }
            val individualSpacing = elkNode.getProperty(CoreOptions.SPACING_INDIVIDUAL_OVERRIDE)

            val mapEntry = value as ElkPropertyToValueMapEntryImpl
            individualSpacing.setProperty(mapEntry.key as IProperty, mapEntry.value)

        } else {
            super.add(object, feature, value, ruleName, node)
        }
    }

}
