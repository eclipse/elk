/*******************************************************************************
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.graph.text.serializer;

import com.google.inject.Inject;
import java.util.List;
import org.eclipse.elk.graph.text.services.ElkGraphGrammarAccess;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.IGrammarAccess;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.serializer.analysis.GrammarAlias.AbstractElementAlias;
import org.eclipse.xtext.serializer.analysis.GrammarAlias.GroupAlias;
import org.eclipse.xtext.serializer.analysis.GrammarAlias.TokenAlias;
import org.eclipse.xtext.serializer.analysis.ISyntacticSequencerPDAProvider.ISynNavigable;
import org.eclipse.xtext.serializer.analysis.ISyntacticSequencerPDAProvider.ISynTransition;
import org.eclipse.xtext.serializer.sequencer.AbstractSyntacticSequencer;

@SuppressWarnings("all")
public abstract class AbstractElkGraphSyntacticSequencer extends AbstractSyntacticSequencer {

	protected ElkGraphGrammarAccess grammarAccess;
	protected AbstractElementAlias match_ElkEdge___LeftCurlyBracketKeyword_7_0_RightCurlyBracketKeyword_7_4__q;
	protected AbstractElementAlias match_ElkNode___IndividualSpacingKeyword_2_3_0_LeftCurlyBracketKeyword_2_3_1_RightCurlyBracketKeyword_2_3_3__q;
	
	@Inject
	protected void init(IGrammarAccess access) {
		grammarAccess = (ElkGraphGrammarAccess) access;
		match_ElkEdge___LeftCurlyBracketKeyword_7_0_RightCurlyBracketKeyword_7_4__q = new GroupAlias(false, true, new TokenAlias(false, false, grammarAccess.getElkEdgeAccess().getLeftCurlyBracketKeyword_7_0()), new TokenAlias(false, false, grammarAccess.getElkEdgeAccess().getRightCurlyBracketKeyword_7_4()));
		match_ElkNode___IndividualSpacingKeyword_2_3_0_LeftCurlyBracketKeyword_2_3_1_RightCurlyBracketKeyword_2_3_3__q = new GroupAlias(false, true, new TokenAlias(false, false, grammarAccess.getElkNodeAccess().getIndividualSpacingKeyword_2_3_0()), new TokenAlias(false, false, grammarAccess.getElkNodeAccess().getLeftCurlyBracketKeyword_2_3_1()), new TokenAlias(false, false, grammarAccess.getElkNodeAccess().getRightCurlyBracketKeyword_2_3_3()));
	}
	
	@Override
	protected String getUnassignedRuleCallToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		return "";
	}
	
	
	@Override
	protected void emitUnassignedTokens(EObject semanticObject, ISynTransition transition, INode fromNode, INode toNode) {
		if (transition.getAmbiguousSyntaxes().isEmpty()) return;
		List<INode> transitionNodes = collectNodes(fromNode, toNode);
		for (AbstractElementAlias syntax : transition.getAmbiguousSyntaxes()) {
			List<INode> syntaxNodes = getNodesFor(transitionNodes, syntax);
			if (match_ElkEdge___LeftCurlyBracketKeyword_7_0_RightCurlyBracketKeyword_7_4__q.equals(syntax))
				emit_ElkEdge___LeftCurlyBracketKeyword_7_0_RightCurlyBracketKeyword_7_4__q(semanticObject, getLastNavigableState(), syntaxNodes);
			else if (match_ElkNode___IndividualSpacingKeyword_2_3_0_LeftCurlyBracketKeyword_2_3_1_RightCurlyBracketKeyword_2_3_3__q.equals(syntax))
				emit_ElkNode___IndividualSpacingKeyword_2_3_0_LeftCurlyBracketKeyword_2_3_1_RightCurlyBracketKeyword_2_3_3__q(semanticObject, getLastNavigableState(), syntaxNodes);
			else acceptNodes(getLastNavigableState(), syntaxNodes);
		}
	}

	/**
	 * Ambiguous syntax:
	 *     ('{' '}')?
	 *
	 * This ambiguous syntax occurs at:
	 *     targets+=[ElkConnectableShape|QualifiedId] (ambiguity) (rule end)
	 */
	protected void emit_ElkEdge___LeftCurlyBracketKeyword_7_0_RightCurlyBracketKeyword_7_4__q(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Ambiguous syntax:
	 *     ('individualSpacing' '{' '}')?
	 *
	 * This ambiguous syntax occurs at:
	 *     height=Number ']' (ambiguity) '}' (rule end)
	 *     height=Number ']' (ambiguity) children+=ElkNode
	 *     height=Number ']' (ambiguity) containedEdges+=ElkEdge
	 *     height=Number ']' (ambiguity) labels+=ElkLabel
	 *     height=Number ']' (ambiguity) ports+=ElkPort
	 *     identifier=ID '{' (ambiguity) children+=ElkNode
	 *     identifier=ID '{' (ambiguity) containedEdges+=ElkEdge
	 *     identifier=ID '{' (ambiguity) labels+=ElkLabel
	 *     identifier=ID '{' (ambiguity) ports+=ElkPort
	 *     properties+=Property (ambiguity) '}' (rule end)
	 *     properties+=Property (ambiguity) children+=ElkNode
	 *     properties+=Property (ambiguity) containedEdges+=ElkEdge
	 *     properties+=Property (ambiguity) labels+=ElkLabel
	 *     properties+=Property (ambiguity) ports+=ElkPort
	 *     y=Number ']' (ambiguity) '}' (rule end)
	 *     y=Number ']' (ambiguity) children+=ElkNode
	 *     y=Number ']' (ambiguity) containedEdges+=ElkEdge
	 *     y=Number ']' (ambiguity) labels+=ElkLabel
	 *     y=Number ']' (ambiguity) ports+=ElkPort
	 */
	protected void emit_ElkNode___IndividualSpacingKeyword_2_3_0_LeftCurlyBracketKeyword_2_3_1_RightCurlyBracketKeyword_2_3_3__q(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
}
