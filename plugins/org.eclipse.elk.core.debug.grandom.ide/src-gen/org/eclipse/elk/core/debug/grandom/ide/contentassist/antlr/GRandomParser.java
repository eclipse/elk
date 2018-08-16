/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.core.debug.grandom.ide.contentassist.antlr;

import com.google.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.elk.core.debug.grandom.ide.contentassist.antlr.internal.InternalGRandomParser;
import org.eclipse.elk.core.debug.grandom.services.GRandomGrammarAccess;
import org.eclipse.xtext.AbstractElement;
import org.eclipse.xtext.ide.editor.contentassist.antlr.AbstractContentAssistParser;

public class GRandomParser extends AbstractContentAssistParser {

	@Inject
	private GRandomGrammarAccess grammarAccess;

	private Map<AbstractElement, String> nameMappings;

	@Override
	protected InternalGRandomParser createParser() {
		InternalGRandomParser result = new InternalGRandomParser(null);
		result.setGrammarAccess(grammarAccess);
		return result;
	}

	@Override
	protected String getRuleName(AbstractElement element) {
		if (nameMappings == null) {
			nameMappings = new HashMap<AbstractElement, String>() {
				private static final long serialVersionUID = 1L;
				{
					put(grammarAccess.getEdgesAccess().getAlternatives_0_1(), "rule__Edges__Alternatives_0_1");
					put(grammarAccess.getDoubleQuantityAccess().getAlternatives(), "rule__DoubleQuantity__Alternatives");
					put(grammarAccess.getFormatsAccess().getAlternatives(), "rule__Formats__Alternatives");
					put(grammarAccess.getFormAccess().getAlternatives(), "rule__Form__Alternatives");
					put(grammarAccess.getSideAccess().getAlternatives(), "rule__Side__Alternatives");
					put(grammarAccess.getFlowTypeAccess().getAlternatives(), "rule__FlowType__Alternatives");
					put(grammarAccess.getConstraintTypeAccess().getAlternatives(), "rule__ConstraintType__Alternatives");
					put(grammarAccess.getConfigurationAccess().getGroup(), "rule__Configuration__Group__0");
					put(grammarAccess.getConfigurationAccess().getGroup_3(), "rule__Configuration__Group_3__0");
					put(grammarAccess.getConfigurationAccess().getGroup_3_1_2(), "rule__Configuration__Group_3_1_2__0");
					put(grammarAccess.getConfigurationAccess().getGroup_3_1_3(), "rule__Configuration__Group_3_1_3__0");
					put(grammarAccess.getConfigurationAccess().getGroup_3_1_4(), "rule__Configuration__Group_3_1_4__0");
					put(grammarAccess.getConfigurationAccess().getGroup_3_1_6(), "rule__Configuration__Group_3_1_6__0");
					put(grammarAccess.getConfigurationAccess().getGroup_3_1_7(), "rule__Configuration__Group_3_1_7__0");
					put(grammarAccess.getConfigurationAccess().getGroup_3_1_8(), "rule__Configuration__Group_3_1_8__0");
					put(grammarAccess.getHierarchyAccess().getGroup(), "rule__Hierarchy__Group__0");
					put(grammarAccess.getHierarchyAccess().getGroup_2(), "rule__Hierarchy__Group_2__0");
					put(grammarAccess.getHierarchyAccess().getGroup_2_1_0(), "rule__Hierarchy__Group_2_1_0__0");
					put(grammarAccess.getHierarchyAccess().getGroup_2_1_1(), "rule__Hierarchy__Group_2_1_1__0");
					put(grammarAccess.getHierarchyAccess().getGroup_2_1_2(), "rule__Hierarchy__Group_2_1_2__0");
					put(grammarAccess.getHierarchyAccess().getGroup_2_1_3(), "rule__Hierarchy__Group_2_1_3__0");
					put(grammarAccess.getEdgesAccess().getGroup(), "rule__Edges__Group__0");
					put(grammarAccess.getEdgesAccess().getGroup_0(), "rule__Edges__Group_0__0");
					put(grammarAccess.getEdgesAccess().getGroup_1(), "rule__Edges__Group_1__0");
					put(grammarAccess.getNodesAccess().getGroup(), "rule__Nodes__Group__0");
					put(grammarAccess.getNodesAccess().getGroup_4(), "rule__Nodes__Group_4__0");
					put(grammarAccess.getSizeAccess().getGroup(), "rule__Size__Group__0");
					put(grammarAccess.getSizeAccess().getGroup_1(), "rule__Size__Group_1__0");
					put(grammarAccess.getSizeAccess().getGroup_1_1(), "rule__Size__Group_1_1__0");
					put(grammarAccess.getSizeAccess().getGroup_1_1_1_0(), "rule__Size__Group_1_1_1_0__0");
					put(grammarAccess.getSizeAccess().getGroup_1_1_1_1(), "rule__Size__Group_1_1_1_1__0");
					put(grammarAccess.getPortsAccess().getGroup(), "rule__Ports__Group__0");
					put(grammarAccess.getPortsAccess().getGroup_2(), "rule__Ports__Group_2__0");
					put(grammarAccess.getPortsAccess().getGroup_2_1_1(), "rule__Ports__Group_2_1_1__0");
					put(grammarAccess.getPortsAccess().getGroup_2_1_3(), "rule__Ports__Group_2_1_3__0");
					put(grammarAccess.getFlowAccess().getGroup(), "rule__Flow__Group__0");
					put(grammarAccess.getDoubleQuantityAccess().getGroup_1(), "rule__DoubleQuantity__Group_1__0");
					put(grammarAccess.getDoubleQuantityAccess().getGroup_2(), "rule__DoubleQuantity__Group_2__0");
					put(grammarAccess.getDoubleAccess().getGroup(), "rule__Double__Group__0");
					put(grammarAccess.getDoubleAccess().getGroup_1(), "rule__Double__Group_1__0");
					put(grammarAccess.getFloatAccess().getGroup(), "rule__Float__Group__0");
					put(grammarAccess.getFloatAccess().getGroup_1(), "rule__Float__Group_1__0");
					put(grammarAccess.getIntegerAccess().getGroup(), "rule__Integer__Group__0");
					put(grammarAccess.getIntegerAccess().getGroup_1(), "rule__Integer__Group_1__0");
					put(grammarAccess.getRandGraphAccess().getConfigsAssignment(), "rule__RandGraph__ConfigsAssignment");
					put(grammarAccess.getConfigurationAccess().getSamplesAssignment_1(), "rule__Configuration__SamplesAssignment_1");
					put(grammarAccess.getConfigurationAccess().getFormAssignment_2(), "rule__Configuration__FormAssignment_2");
					put(grammarAccess.getConfigurationAccess().getNodesAssignment_3_1_0(), "rule__Configuration__NodesAssignment_3_1_0");
					put(grammarAccess.getConfigurationAccess().getEdgesAssignment_3_1_1(), "rule__Configuration__EdgesAssignment_3_1_1");
					put(grammarAccess.getConfigurationAccess().getMWAssignment_3_1_2_0(), "rule__Configuration__MWAssignment_3_1_2_0");
					put(grammarAccess.getConfigurationAccess().getMaxWidthAssignment_3_1_2_2(), "rule__Configuration__MaxWidthAssignment_3_1_2_2");
					put(grammarAccess.getConfigurationAccess().getMDAssignment_3_1_3_0(), "rule__Configuration__MDAssignment_3_1_3_0");
					put(grammarAccess.getConfigurationAccess().getMaxDegreeAssignment_3_1_3_2(), "rule__Configuration__MaxDegreeAssignment_3_1_3_2");
					put(grammarAccess.getConfigurationAccess().getPFAssignment_3_1_4_0(), "rule__Configuration__PFAssignment_3_1_4_0");
					put(grammarAccess.getConfigurationAccess().getFractionAssignment_3_1_4_2(), "rule__Configuration__FractionAssignment_3_1_4_2");
					put(grammarAccess.getConfigurationAccess().getHierarchyAssignment_3_1_5(), "rule__Configuration__HierarchyAssignment_3_1_5");
					put(grammarAccess.getConfigurationAccess().getSeedAssignment_3_1_6_2(), "rule__Configuration__SeedAssignment_3_1_6_2");
					put(grammarAccess.getConfigurationAccess().getFormatAssignment_3_1_7_2(), "rule__Configuration__FormatAssignment_3_1_7_2");
					put(grammarAccess.getConfigurationAccess().getFilenameAssignment_3_1_8_2(), "rule__Configuration__FilenameAssignment_3_1_8_2");
					put(grammarAccess.getHierarchyAccess().getLevelsAssignment_2_1_0_2(), "rule__Hierarchy__LevelsAssignment_2_1_0_2");
					put(grammarAccess.getHierarchyAccess().getEdgesAssignment_2_1_1_2(), "rule__Hierarchy__EdgesAssignment_2_1_1_2");
					put(grammarAccess.getHierarchyAccess().getNumHierarchNodesAssignment_2_1_2_2(), "rule__Hierarchy__NumHierarchNodesAssignment_2_1_2_2");
					put(grammarAccess.getHierarchyAccess().getCrossHierarchRelAssignment_2_1_3_2(), "rule__Hierarchy__CrossHierarchRelAssignment_2_1_3_2");
					put(grammarAccess.getEdgesAccess().getDensityAssignment_0_1_0(), "rule__Edges__DensityAssignment_0_1_0");
					put(grammarAccess.getEdgesAccess().getTotalAssignment_0_1_1(), "rule__Edges__TotalAssignment_0_1_1");
					put(grammarAccess.getEdgesAccess().getRelativeAssignment_0_1_2(), "rule__Edges__RelativeAssignment_0_1_2");
					put(grammarAccess.getEdgesAccess().getOutboundAssignment_0_1_3(), "rule__Edges__OutboundAssignment_0_1_3");
					put(grammarAccess.getEdgesAccess().getNEdgesAssignment_0_3(), "rule__Edges__NEdgesAssignment_0_3");
					put(grammarAccess.getEdgesAccess().getLabelsAssignment_1_1_0(), "rule__Edges__LabelsAssignment_1_1_0");
					put(grammarAccess.getEdgesAccess().getSelfLoopsAssignment_1_1_1(), "rule__Edges__SelfLoopsAssignment_1_1_1");
					put(grammarAccess.getNodesAccess().getNNodesAssignment_3(), "rule__Nodes__NNodesAssignment_3");
					put(grammarAccess.getNodesAccess().getPortsAssignment_4_1_0(), "rule__Nodes__PortsAssignment_4_1_0");
					put(grammarAccess.getNodesAccess().getLabelsAssignment_4_1_1(), "rule__Nodes__LabelsAssignment_4_1_1");
					put(grammarAccess.getNodesAccess().getSizeAssignment_4_1_2(), "rule__Nodes__SizeAssignment_4_1_2");
					put(grammarAccess.getNodesAccess().getRemoveIsolatedAssignment_4_1_3(), "rule__Nodes__RemoveIsolatedAssignment_4_1_3");
					put(grammarAccess.getSizeAccess().getHeightAssignment_1_1_1_0_2(), "rule__Size__HeightAssignment_1_1_1_0_2");
					put(grammarAccess.getSizeAccess().getWidthAssignment_1_1_1_1_2(), "rule__Size__WidthAssignment_1_1_1_1_2");
					put(grammarAccess.getPortsAccess().getLabelsAssignment_2_1_0(), "rule__Ports__LabelsAssignment_2_1_0");
					put(grammarAccess.getPortsAccess().getReUseAssignment_2_1_1_2(), "rule__Ports__ReUseAssignment_2_1_1_2");
					put(grammarAccess.getPortsAccess().getSizeAssignment_2_1_2(), "rule__Ports__SizeAssignment_2_1_2");
					put(grammarAccess.getPortsAccess().getConstraintAssignment_2_1_3_2(), "rule__Ports__ConstraintAssignment_2_1_3_2");
					put(grammarAccess.getPortsAccess().getFlowAssignment_2_1_4(), "rule__Ports__FlowAssignment_2_1_4");
					put(grammarAccess.getFlowAccess().getFlowTypeAssignment_0(), "rule__Flow__FlowTypeAssignment_0");
					put(grammarAccess.getFlowAccess().getSideAssignment_1(), "rule__Flow__SideAssignment_1");
					put(grammarAccess.getFlowAccess().getAmountAssignment_3(), "rule__Flow__AmountAssignment_3");
					put(grammarAccess.getDoubleQuantityAccess().getQuantAssignment_0(), "rule__DoubleQuantity__QuantAssignment_0");
					put(grammarAccess.getDoubleQuantityAccess().getMinAssignment_1_0(), "rule__DoubleQuantity__MinAssignment_1_0");
					put(grammarAccess.getDoubleQuantityAccess().getMinMaxAssignment_1_1(), "rule__DoubleQuantity__MinMaxAssignment_1_1");
					put(grammarAccess.getDoubleQuantityAccess().getMaxAssignment_1_2(), "rule__DoubleQuantity__MaxAssignment_1_2");
					put(grammarAccess.getDoubleQuantityAccess().getMeanAssignment_2_0(), "rule__DoubleQuantity__MeanAssignment_2_0");
					put(grammarAccess.getDoubleQuantityAccess().getGaussianAssignment_2_1(), "rule__DoubleQuantity__GaussianAssignment_2_1");
					put(grammarAccess.getDoubleQuantityAccess().getStddvAssignment_2_2(), "rule__DoubleQuantity__StddvAssignment_2_2");
					put(grammarAccess.getConfigurationAccess().getUnorderedGroup_3_1(), "rule__Configuration__UnorderedGroup_3_1");
					put(grammarAccess.getHierarchyAccess().getUnorderedGroup_2_1(), "rule__Hierarchy__UnorderedGroup_2_1");
					put(grammarAccess.getEdgesAccess().getUnorderedGroup_1_1(), "rule__Edges__UnorderedGroup_1_1");
					put(grammarAccess.getNodesAccess().getUnorderedGroup_4_1(), "rule__Nodes__UnorderedGroup_4_1");
					put(grammarAccess.getSizeAccess().getUnorderedGroup_1_1_1(), "rule__Size__UnorderedGroup_1_1_1");
					put(grammarAccess.getPortsAccess().getUnorderedGroup_2_1(), "rule__Ports__UnorderedGroup_2_1");
				}
			};
		}
		return nameMappings.get(element);
	}
			
	@Override
	protected String[] getInitialHiddenTokens() {
		return new String[] { "RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT" };
	}

	public GRandomGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}

	public void setGrammarAccess(GRandomGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
}
