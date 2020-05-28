/*******************************************************************************
 * Copyright (c) 2020 Kiel University and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.graph.json.text.formatting2

import com.google.inject.Inject
import org.eclipse.elk.graph.ElkNode
import org.eclipse.elk.graph.json.text.services.ElkGraphJsonGrammarAccess
import org.eclipse.xtext.formatting2.AbstractFormatter2
import org.eclipse.xtext.formatting2.IFormattableDocument

class ElkGraphJsonFormatter extends AbstractFormatter2 {
	
	@Inject extension ElkGraphJsonGrammarAccess

	def dispatch void format(ElkNode elkNode, extension IFormattableDocument document) {
		// TODO: format HiddenRegions around keywords, attributes, cross references, etc. 
		for (_elkNode : elkNode.children) {
			_elkNode.format
		}
	}
	
	// TODO: implement for 
}
