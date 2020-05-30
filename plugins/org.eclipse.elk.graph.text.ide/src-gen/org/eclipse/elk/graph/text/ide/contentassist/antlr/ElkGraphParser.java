/*******************************************************************************
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.graph.text.ide.contentassist.antlr;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Map;
import org.eclipse.elk.graph.text.ide.contentassist.antlr.internal.InternalElkGraphParser;
import org.eclipse.elk.graph.text.services.ElkGraphGrammarAccess;
import org.eclipse.xtext.AbstractElement;
import org.eclipse.xtext.ide.editor.contentassist.antlr.AbstractContentAssistParser;

public class ElkGraphParser extends AbstractContentAssistParser {

	@Singleton
	public static final class NameMappings {
		
		private final Map<AbstractElement, String> mappings;
		
		@Inject
		public NameMappings(ElkGraphGrammarAccess grammarAccess) {
			ImmutableMap.Builder<AbstractElement, String> builder = ImmutableMap.builder();
			init(builder, grammarAccess);
			this.mappings = builder.build();
		}
		
		public String getRuleName(AbstractElement element) {
			return mappings.get(element);
		}
		
		private static void init(ImmutableMap.Builder<AbstractElement, String> builder, ElkGraphGrammarAccess grammarAccess) {
			builder.put(grammarAccess.getRootNodeAccess().getAlternatives_4(), "rule__RootNode__Alternatives_4");
			builder.put(grammarAccess.getElkNodeAccess().getAlternatives_2_4(), "rule__ElkNode__Alternatives_2_4");
			builder.put(grammarAccess.getEdgeLayoutAccess().getAlternatives_2(), "rule__EdgeLayout__Alternatives_2");
			builder.put(grammarAccess.getNumberAccess().getAlternatives(), "rule__Number__Alternatives");
			builder.put(grammarAccess.getPropertyAccess().getAlternatives_2(), "rule__Property__Alternatives_2");
			builder.put(grammarAccess.getNumberValueAccess().getAlternatives(), "rule__NumberValue__Alternatives");
			builder.put(grammarAccess.getBooleanValueAccess().getAlternatives(), "rule__BooleanValue__Alternatives");
			builder.put(grammarAccess.getRootNodeAccess().getGroup(), "rule__RootNode__Group__0");
			builder.put(grammarAccess.getRootNodeAccess().getGroup_1(), "rule__RootNode__Group_1__0");
			builder.put(grammarAccess.getElkNodeAccess().getGroup(), "rule__ElkNode__Group__0");
			builder.put(grammarAccess.getElkNodeAccess().getGroup_2(), "rule__ElkNode__Group_2__0");
			builder.put(grammarAccess.getElkNodeAccess().getGroup_2_3(), "rule__ElkNode__Group_2_3__0");
			builder.put(grammarAccess.getElkLabelAccess().getGroup(), "rule__ElkLabel__Group__0");
			builder.put(grammarAccess.getElkLabelAccess().getGroup_1(), "rule__ElkLabel__Group_1__0");
			builder.put(grammarAccess.getElkLabelAccess().getGroup_3(), "rule__ElkLabel__Group_3__0");
			builder.put(grammarAccess.getElkPortAccess().getGroup(), "rule__ElkPort__Group__0");
			builder.put(grammarAccess.getElkPortAccess().getGroup_2(), "rule__ElkPort__Group_2__0");
			builder.put(grammarAccess.getShapeLayoutAccess().getGroup(), "rule__ShapeLayout__Group__0");
			builder.put(grammarAccess.getShapeLayoutAccess().getGroup_2_0(), "rule__ShapeLayout__Group_2_0__0");
			builder.put(grammarAccess.getShapeLayoutAccess().getGroup_2_1(), "rule__ShapeLayout__Group_2_1__0");
			builder.put(grammarAccess.getElkEdgeAccess().getGroup(), "rule__ElkEdge__Group__0");
			builder.put(grammarAccess.getElkEdgeAccess().getGroup_1(), "rule__ElkEdge__Group_1__0");
			builder.put(grammarAccess.getElkEdgeAccess().getGroup_3(), "rule__ElkEdge__Group_3__0");
			builder.put(grammarAccess.getElkEdgeAccess().getGroup_6(), "rule__ElkEdge__Group_6__0");
			builder.put(grammarAccess.getElkEdgeAccess().getGroup_7(), "rule__ElkEdge__Group_7__0");
			builder.put(grammarAccess.getEdgeLayoutAccess().getGroup(), "rule__EdgeLayout__Group__0");
			builder.put(grammarAccess.getElkSingleEdgeSectionAccess().getGroup(), "rule__ElkSingleEdgeSection__Group__0");
			builder.put(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1(), "rule__ElkSingleEdgeSection__Group_1__0");
			builder.put(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_0_0(), "rule__ElkSingleEdgeSection__Group_1_0_0__0");
			builder.put(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_0_1(), "rule__ElkSingleEdgeSection__Group_1_0_1__0");
			builder.put(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_0_2(), "rule__ElkSingleEdgeSection__Group_1_0_2__0");
			builder.put(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_0_3(), "rule__ElkSingleEdgeSection__Group_1_0_3__0");
			builder.put(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_1(), "rule__ElkSingleEdgeSection__Group_1_1__0");
			builder.put(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_1_3(), "rule__ElkSingleEdgeSection__Group_1_1_3__0");
			builder.put(grammarAccess.getElkEdgeSectionAccess().getGroup(), "rule__ElkEdgeSection__Group__0");
			builder.put(grammarAccess.getElkEdgeSectionAccess().getGroup_2(), "rule__ElkEdgeSection__Group_2__0");
			builder.put(grammarAccess.getElkEdgeSectionAccess().getGroup_2_2(), "rule__ElkEdgeSection__Group_2_2__0");
			builder.put(grammarAccess.getElkEdgeSectionAccess().getGroup_4(), "rule__ElkEdgeSection__Group_4__0");
			builder.put(grammarAccess.getElkEdgeSectionAccess().getGroup_4_0_0(), "rule__ElkEdgeSection__Group_4_0_0__0");
			builder.put(grammarAccess.getElkEdgeSectionAccess().getGroup_4_0_1(), "rule__ElkEdgeSection__Group_4_0_1__0");
			builder.put(grammarAccess.getElkEdgeSectionAccess().getGroup_4_0_2(), "rule__ElkEdgeSection__Group_4_0_2__0");
			builder.put(grammarAccess.getElkEdgeSectionAccess().getGroup_4_0_3(), "rule__ElkEdgeSection__Group_4_0_3__0");
			builder.put(grammarAccess.getElkEdgeSectionAccess().getGroup_4_1(), "rule__ElkEdgeSection__Group_4_1__0");
			builder.put(grammarAccess.getElkEdgeSectionAccess().getGroup_4_1_3(), "rule__ElkEdgeSection__Group_4_1_3__0");
			builder.put(grammarAccess.getElkBendPointAccess().getGroup(), "rule__ElkBendPoint__Group__0");
			builder.put(grammarAccess.getQualifiedIdAccess().getGroup(), "rule__QualifiedId__Group__0");
			builder.put(grammarAccess.getQualifiedIdAccess().getGroup_1(), "rule__QualifiedId__Group_1__0");
			builder.put(grammarAccess.getPropertyAccess().getGroup(), "rule__Property__Group__0");
			builder.put(grammarAccess.getPropertyKeyAccess().getGroup(), "rule__PropertyKey__Group__0");
			builder.put(grammarAccess.getPropertyKeyAccess().getGroup_1(), "rule__PropertyKey__Group_1__0");
			builder.put(grammarAccess.getRootNodeAccess().getIdentifierAssignment_1_1(), "rule__RootNode__IdentifierAssignment_1_1");
			builder.put(grammarAccess.getRootNodeAccess().getPropertiesAssignment_3(), "rule__RootNode__PropertiesAssignment_3");
			builder.put(grammarAccess.getRootNodeAccess().getLabelsAssignment_4_0(), "rule__RootNode__LabelsAssignment_4_0");
			builder.put(grammarAccess.getRootNodeAccess().getPortsAssignment_4_1(), "rule__RootNode__PortsAssignment_4_1");
			builder.put(grammarAccess.getRootNodeAccess().getChildrenAssignment_4_2(), "rule__RootNode__ChildrenAssignment_4_2");
			builder.put(grammarAccess.getRootNodeAccess().getContainedEdgesAssignment_4_3(), "rule__RootNode__ContainedEdgesAssignment_4_3");
			builder.put(grammarAccess.getElkNodeAccess().getIdentifierAssignment_1(), "rule__ElkNode__IdentifierAssignment_1");
			builder.put(grammarAccess.getElkNodeAccess().getPropertiesAssignment_2_2(), "rule__ElkNode__PropertiesAssignment_2_2");
			builder.put(grammarAccess.getElkNodeAccess().getPropertiesAssignment_2_3_2(), "rule__ElkNode__PropertiesAssignment_2_3_2");
			builder.put(grammarAccess.getElkNodeAccess().getLabelsAssignment_2_4_0(), "rule__ElkNode__LabelsAssignment_2_4_0");
			builder.put(grammarAccess.getElkNodeAccess().getPortsAssignment_2_4_1(), "rule__ElkNode__PortsAssignment_2_4_1");
			builder.put(grammarAccess.getElkNodeAccess().getChildrenAssignment_2_4_2(), "rule__ElkNode__ChildrenAssignment_2_4_2");
			builder.put(grammarAccess.getElkNodeAccess().getContainedEdgesAssignment_2_4_3(), "rule__ElkNode__ContainedEdgesAssignment_2_4_3");
			builder.put(grammarAccess.getElkLabelAccess().getIdentifierAssignment_1_0(), "rule__ElkLabel__IdentifierAssignment_1_0");
			builder.put(grammarAccess.getElkLabelAccess().getTextAssignment_2(), "rule__ElkLabel__TextAssignment_2");
			builder.put(grammarAccess.getElkLabelAccess().getPropertiesAssignment_3_2(), "rule__ElkLabel__PropertiesAssignment_3_2");
			builder.put(grammarAccess.getElkLabelAccess().getLabelsAssignment_3_3(), "rule__ElkLabel__LabelsAssignment_3_3");
			builder.put(grammarAccess.getElkPortAccess().getIdentifierAssignment_1(), "rule__ElkPort__IdentifierAssignment_1");
			builder.put(grammarAccess.getElkPortAccess().getPropertiesAssignment_2_2(), "rule__ElkPort__PropertiesAssignment_2_2");
			builder.put(grammarAccess.getElkPortAccess().getLabelsAssignment_2_3(), "rule__ElkPort__LabelsAssignment_2_3");
			builder.put(grammarAccess.getShapeLayoutAccess().getXAssignment_2_0_2(), "rule__ShapeLayout__XAssignment_2_0_2");
			builder.put(grammarAccess.getShapeLayoutAccess().getYAssignment_2_0_4(), "rule__ShapeLayout__YAssignment_2_0_4");
			builder.put(grammarAccess.getShapeLayoutAccess().getWidthAssignment_2_1_2(), "rule__ShapeLayout__WidthAssignment_2_1_2");
			builder.put(grammarAccess.getShapeLayoutAccess().getHeightAssignment_2_1_4(), "rule__ShapeLayout__HeightAssignment_2_1_4");
			builder.put(grammarAccess.getElkEdgeAccess().getIdentifierAssignment_1_0(), "rule__ElkEdge__IdentifierAssignment_1_0");
			builder.put(grammarAccess.getElkEdgeAccess().getSourcesAssignment_2(), "rule__ElkEdge__SourcesAssignment_2");
			builder.put(grammarAccess.getElkEdgeAccess().getSourcesAssignment_3_1(), "rule__ElkEdge__SourcesAssignment_3_1");
			builder.put(grammarAccess.getElkEdgeAccess().getTargetsAssignment_5(), "rule__ElkEdge__TargetsAssignment_5");
			builder.put(grammarAccess.getElkEdgeAccess().getTargetsAssignment_6_1(), "rule__ElkEdge__TargetsAssignment_6_1");
			builder.put(grammarAccess.getElkEdgeAccess().getPropertiesAssignment_7_2(), "rule__ElkEdge__PropertiesAssignment_7_2");
			builder.put(grammarAccess.getElkEdgeAccess().getLabelsAssignment_7_3(), "rule__ElkEdge__LabelsAssignment_7_3");
			builder.put(grammarAccess.getEdgeLayoutAccess().getSectionsAssignment_2_0(), "rule__EdgeLayout__SectionsAssignment_2_0");
			builder.put(grammarAccess.getEdgeLayoutAccess().getSectionsAssignment_2_1(), "rule__EdgeLayout__SectionsAssignment_2_1");
			builder.put(grammarAccess.getElkSingleEdgeSectionAccess().getIncomingShapeAssignment_1_0_0_2(), "rule__ElkSingleEdgeSection__IncomingShapeAssignment_1_0_0_2");
			builder.put(grammarAccess.getElkSingleEdgeSectionAccess().getOutgoingShapeAssignment_1_0_1_2(), "rule__ElkSingleEdgeSection__OutgoingShapeAssignment_1_0_1_2");
			builder.put(grammarAccess.getElkSingleEdgeSectionAccess().getStartXAssignment_1_0_2_2(), "rule__ElkSingleEdgeSection__StartXAssignment_1_0_2_2");
			builder.put(grammarAccess.getElkSingleEdgeSectionAccess().getStartYAssignment_1_0_2_4(), "rule__ElkSingleEdgeSection__StartYAssignment_1_0_2_4");
			builder.put(grammarAccess.getElkSingleEdgeSectionAccess().getEndXAssignment_1_0_3_2(), "rule__ElkSingleEdgeSection__EndXAssignment_1_0_3_2");
			builder.put(grammarAccess.getElkSingleEdgeSectionAccess().getEndYAssignment_1_0_3_4(), "rule__ElkSingleEdgeSection__EndYAssignment_1_0_3_4");
			builder.put(grammarAccess.getElkSingleEdgeSectionAccess().getBendPointsAssignment_1_1_2(), "rule__ElkSingleEdgeSection__BendPointsAssignment_1_1_2");
			builder.put(grammarAccess.getElkSingleEdgeSectionAccess().getBendPointsAssignment_1_1_3_1(), "rule__ElkSingleEdgeSection__BendPointsAssignment_1_1_3_1");
			builder.put(grammarAccess.getElkSingleEdgeSectionAccess().getPropertiesAssignment_1_2(), "rule__ElkSingleEdgeSection__PropertiesAssignment_1_2");
			builder.put(grammarAccess.getElkEdgeSectionAccess().getIdentifierAssignment_1(), "rule__ElkEdgeSection__IdentifierAssignment_1");
			builder.put(grammarAccess.getElkEdgeSectionAccess().getOutgoingSectionsAssignment_2_1(), "rule__ElkEdgeSection__OutgoingSectionsAssignment_2_1");
			builder.put(grammarAccess.getElkEdgeSectionAccess().getOutgoingSectionsAssignment_2_2_1(), "rule__ElkEdgeSection__OutgoingSectionsAssignment_2_2_1");
			builder.put(grammarAccess.getElkEdgeSectionAccess().getIncomingShapeAssignment_4_0_0_2(), "rule__ElkEdgeSection__IncomingShapeAssignment_4_0_0_2");
			builder.put(grammarAccess.getElkEdgeSectionAccess().getOutgoingShapeAssignment_4_0_1_2(), "rule__ElkEdgeSection__OutgoingShapeAssignment_4_0_1_2");
			builder.put(grammarAccess.getElkEdgeSectionAccess().getStartXAssignment_4_0_2_2(), "rule__ElkEdgeSection__StartXAssignment_4_0_2_2");
			builder.put(grammarAccess.getElkEdgeSectionAccess().getStartYAssignment_4_0_2_4(), "rule__ElkEdgeSection__StartYAssignment_4_0_2_4");
			builder.put(grammarAccess.getElkEdgeSectionAccess().getEndXAssignment_4_0_3_2(), "rule__ElkEdgeSection__EndXAssignment_4_0_3_2");
			builder.put(grammarAccess.getElkEdgeSectionAccess().getEndYAssignment_4_0_3_4(), "rule__ElkEdgeSection__EndYAssignment_4_0_3_4");
			builder.put(grammarAccess.getElkEdgeSectionAccess().getBendPointsAssignment_4_1_2(), "rule__ElkEdgeSection__BendPointsAssignment_4_1_2");
			builder.put(grammarAccess.getElkEdgeSectionAccess().getBendPointsAssignment_4_1_3_1(), "rule__ElkEdgeSection__BendPointsAssignment_4_1_3_1");
			builder.put(grammarAccess.getElkEdgeSectionAccess().getPropertiesAssignment_4_2(), "rule__ElkEdgeSection__PropertiesAssignment_4_2");
			builder.put(grammarAccess.getElkBendPointAccess().getXAssignment_0(), "rule__ElkBendPoint__XAssignment_0");
			builder.put(grammarAccess.getElkBendPointAccess().getYAssignment_2(), "rule__ElkBendPoint__YAssignment_2");
			builder.put(grammarAccess.getPropertyAccess().getKeyAssignment_0(), "rule__Property__KeyAssignment_0");
			builder.put(grammarAccess.getPropertyAccess().getValueAssignment_2_0(), "rule__Property__ValueAssignment_2_0");
			builder.put(grammarAccess.getPropertyAccess().getValueAssignment_2_1(), "rule__Property__ValueAssignment_2_1");
			builder.put(grammarAccess.getPropertyAccess().getValueAssignment_2_2(), "rule__Property__ValueAssignment_2_2");
			builder.put(grammarAccess.getPropertyAccess().getValueAssignment_2_3(), "rule__Property__ValueAssignment_2_3");
			builder.put(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), "rule__ShapeLayout__UnorderedGroup_2");
			builder.put(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), "rule__ElkSingleEdgeSection__UnorderedGroup_1_0");
			builder.put(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), "rule__ElkEdgeSection__UnorderedGroup_4_0");
		}
	}
	
	@Inject
	private NameMappings nameMappings;

	@Inject
	private ElkGraphGrammarAccess grammarAccess;

	@Override
	protected InternalElkGraphParser createParser() {
		InternalElkGraphParser result = new InternalElkGraphParser(null);
		result.setGrammarAccess(grammarAccess);
		return result;
	}

	@Override
	protected String getRuleName(AbstractElement element) {
		return nameMappings.getRuleName(element);
	}

	@Override
	protected String[] getInitialHiddenTokens() {
		return new String[] { "RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT" };
	}

	public ElkGraphGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}

	public void setGrammarAccess(ElkGraphGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
	
	public NameMappings getNameMappings() {
		return nameMappings;
	}
	
	public void setNameMappings(NameMappings nameMappings) {
		this.nameMappings = nameMappings;
	}
}
