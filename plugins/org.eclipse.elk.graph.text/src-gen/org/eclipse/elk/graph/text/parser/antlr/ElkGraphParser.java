/*******************************************************************************
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.graph.text.parser.antlr;

import com.google.inject.Inject;
import org.eclipse.elk.graph.text.parser.antlr.internal.InternalElkGraphParser;
import org.eclipse.elk.graph.text.services.ElkGraphGrammarAccess;
import org.eclipse.xtext.parser.antlr.AbstractAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;

public class ElkGraphParser extends AbstractAntlrParser {

	@Inject
	private ElkGraphGrammarAccess grammarAccess;

	@Override
	protected void setInitialHiddenTokens(XtextTokenStream tokenStream) {
		tokenStream.setInitialHiddenTokens("RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT");
	}
	

	@Override
	protected InternalElkGraphParser createParser(XtextTokenStream stream) {
		return new InternalElkGraphParser(stream, getGrammarAccess());
	}

	@Override 
	protected String getDefaultRuleName() {
		return "RootNode";
	}

	public ElkGraphGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}

	public void setGrammarAccess(ElkGraphGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
}
