/*******************************************************************************
 * Copyright (c) 2017 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.graph.text.ide.contentassist

import com.google.common.base.Predicate
import com.google.inject.Inject
import com.google.inject.Provider
import java.util.List
import org.eclipse.elk.core.data.ILayoutMetaData
import org.eclipse.elk.core.data.LayoutAlgorithmData
import org.eclipse.elk.core.data.LayoutMetaDataService
import org.eclipse.elk.core.data.LayoutOptionData
import org.eclipse.elk.core.data.LayoutOptionData.Type
import org.eclipse.elk.core.options.CoreOptions
import org.eclipse.elk.graph.ElkEdge
import org.eclipse.elk.graph.ElkEdgeSection
import org.eclipse.elk.graph.ElkGraphElement
import org.eclipse.elk.graph.ElkLabel
import org.eclipse.elk.graph.ElkNode
import org.eclipse.elk.graph.ElkPort
import org.eclipse.elk.graph.impl.ElkPropertyToValueMapEntryImpl
import org.eclipse.elk.graph.text.services.ElkGraphGrammarAccess
import org.eclipse.elk.graph.util.ElkGraphUtil
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor
import org.eclipse.xtext.Assignment
import org.eclipse.xtext.CrossReference
import org.eclipse.xtext.Keyword
import org.eclipse.xtext.conversion.impl.IDValueConverter
import org.eclipse.xtext.ide.editor.contentassist.ContentAssistContext
import org.eclipse.xtext.ide.editor.contentassist.ContentAssistEntry
import org.eclipse.xtext.ide.editor.contentassist.IIdeContentProposalAcceptor
import org.eclipse.xtext.ide.editor.contentassist.IdeContentProposalProvider
import org.eclipse.xtext.resource.IEObjectDescription
import org.eclipse.xtext.util.Strings

import static extension org.eclipse.emf.ecore.util.EcoreUtil.*
import static extension org.eclipse.xtext.EcoreUtil2.*

/**
 * Special content assist proposals for the ELK Graph language.
 */
class ElkGraphProposalProvider extends IdeContentProposalProvider {
    
    static val DISABLED_KEYWORDS = #{'}', ']'}
    
    ElkGraphGrammarAccess grammar
    
    IDValueConverter idValueConverter
    
    @Inject
    def void initialize(Provider<IDValueConverter> idValueConverterProvider, ElkGraphGrammarAccess grammarAccess) {
        this.idValueConverter = idValueConverterProvider.get => [
            rule = grammarAccess.IDRule
        ]
        this.grammar = grammarAccess
    }
    
    override protected filterKeyword(Keyword keyword, ContentAssistContext context) {
        !DISABLED_KEYWORDS.contains(keyword.value) && keyword.value != context.prefix
    }
    
    override protected _createProposals(Keyword keyword, ContentAssistContext context, IIdeContentProposalAcceptor acceptor) {
        if (filterKeyword(keyword, context)) {
            val entry = proposalCreator.createProposal(keyword.value, context)
            if (entry !== null) {
                entry.kind = ContentAssistEntry.KIND_KEYWORD
                entry.source = keyword
                acceptor.accept(entry, proposalPriorities.getKeywordPriority(keyword.value, entry))
            }
        }
    }
    
    override protected _createProposals(Assignment assignment, ContentAssistContext context, IIdeContentProposalAcceptor acceptor) {
        switch assignment {
            case grammar.propertyAccess.keyAssignment_0:
                completePropertyKey(context, acceptor)
            case grammar.propertyAccess.valueAssignment_2_0,
            case grammar.propertyAccess.valueAssignment_2_1,
            case grammar.propertyAccess.valueAssignment_2_2,
            case grammar.propertyAccess.valueAssignment_2_3:
                completePropertyValue(context, acceptor)
            default:
                super._createProposals(assignment, context, acceptor)
        }
    }
    
    protected def void completePropertyKey(ContentAssistContext context, IIdeContentProposalAcceptor acceptor) {
        switch model: context.currentModel {
            ElkNode: {
                if (model.parent === null || !model.children.empty)
                    proposeProperties(model, model.algorithm, LayoutOptionData.Target.PARENTS, context, acceptor)
                if (model.parent !== null)
                    proposeProperties(model, model.parent.algorithm, LayoutOptionData.Target.NODES, context, acceptor)
            }
            ElkEdge: {
                proposeProperties(model, model.algorithm, LayoutOptionData.Target.EDGES, context, acceptor)
            }
            ElkPort: {
                proposeProperties(model, model.algorithm, LayoutOptionData.Target.PORTS, context, acceptor)
            }
            ElkLabel: {
                proposeProperties(model, model.algorithm, LayoutOptionData.Target.LABELS, context, acceptor)
            }
        }
    }
    
    private def getAlgorithm(ElkGraphElement element) {
        var node = element.getContainerOfType(ElkNode)
        if (node !== null) {
            if ((element instanceof ElkLabel || element instanceof ElkPort) && node.parent !== null)
                node = node.parent
            val algorithmId = node.getProperty(CoreOptions.ALGORITHM)
            if (!algorithmId.nullOrEmpty)
                return LayoutMetaDataService.instance.getAlgorithmDataBySuffix(algorithmId)
        }
    }
    
    protected def proposeProperties(ElkGraphElement element, LayoutAlgorithmData algorithmData,
            LayoutOptionData.Target targetType, ContentAssistContext context, IIdeContentProposalAcceptor acceptor) {
        val metaDataService = LayoutMetaDataService.instance
        val filteredOptions = metaDataService.optionData.filter[ o |
            targetType === null || o.targets.contains(targetType)
        ].filter[ o |
            algorithmData === null || algorithmData.knowsOption(o) || CoreOptions.ALGORITHM == o
        ].filter[ o |
            element === null || !element.properties.map.containsKey(o)
        ]
        for (option : filteredOptions) {
            val idSplit = Strings.split(option.id, '.')
            val prefixSplit = Strings.split(context.prefix, '.')
            var foundMatch = false
            var i = idSplit.size - 1
            if (i >= 1 && option.group == idSplit.get(i - 1)) {
                i--
            }
            while (i >= 0 && !foundMatch) {
                val suffix = idSplit.drop(i)
                if (metaDataService.getOptionDataBySuffix(suffix.join('.')) !== null && suffix.startsWith(prefixSplit))
                    foundMatch = true
                else
                    i--
            }
            if (foundMatch) {
                val suffix = idSplit.drop(i)
                val entry = proposalCreator.createProposal(suffix.convert, context, ContentAssistEntry.KIND_PROPERTY) [
                    label = suffix.join('.')
                    description = getDescription(option)
                    documentation = option.description
                    source = option
                ]
                acceptor.accept(entry, proposalPriorities.getDefaultPriority(entry))
            }
        }
    }
    
    protected def void completePropertyValue(ContentAssistContext context, IIdeContentProposalAcceptor acceptor) {
        val model = context.currentModel
        if (model instanceof ElkPropertyToValueMapEntryImpl) {
            val property = model.key
            if (property instanceof LayoutOptionData) {
                if (CoreOptions.ALGORITHM == property)
                    proposeAlgorithms(context, acceptor)
                else 
                    typeAwarePropertyValueProposal(property, context, acceptor)
            }
        }
    }
    
    private def typeAwarePropertyValueProposal(LayoutOptionData property, ContentAssistContext context, 
            IIdeContentProposalAcceptor acceptor) {
         
         switch (property.type) {
             case Type.BOOLEAN,
             case Type.ENUM, 
             case Type.ENUMSET: {
                 val choices = property.choices
                 for (var i = 0; i < choices.length; i++) {
                    val proposal = choices.get(i)
                    val enumVal = property.getEnumValue(i)
                    
                    val displayString = new StringBuilder(proposal)
                    var priority = 3
                    if (ElkGraphUtil.isExperimentalPropertyValue(enumVal)) {
                        displayString.append(" - Experimental")
                        priority = 1
                    } else if (ElkGraphUtil.isAdvancedPropertyValue(enumVal)) {
                        displayString.append(" - Advanced")
                        priority = 2
                    }
                    val entry = proposalCreator.createProposal(proposal, context, ContentAssistEntry.KIND_VALUE) [
                        label = displayString.toString
                        source = property
                    ]
                    acceptor.accept(entry, proposalPriorities.getDefaultPriority(entry))
                 }
             }
             case DOUBLE: {
                 val entry = proposalCreator.createProposal("0.0", context, ContentAssistEntry.KIND_VALUE) [
                     label = property.type.toString
                    source = property
                 ]
                 acceptor.accept(entry, proposalPriorities.getDefaultPriority(entry))
             }
             case INT: {
                 val entry = proposalCreator.createProposal("0", context, ContentAssistEntry.KIND_VALUE) [
                     label = property.type.toString
                     source = property
                 ]
                 acceptor.accept(entry, proposalPriorities.getDefaultPriority(entry))
             }
             case OBJECT: {
                val proposal = try {
                    "\"" + property.getOptionClass().newInstance().toString() + "\"";
                } catch (InstantiationException e) ""
                  catch (IllegalAccessException e) ""
                val entry = proposalCreator.createProposal(proposal, context, ContentAssistEntry.KIND_VALUE) [
                     label = property.type.toString()
                     source = property
                ]
                acceptor.accept(entry, proposalPriorities.getDefaultPriority(entry))
             }
             default: { } // nothing to propose
         }
    }
    
    protected def proposeAlgorithms(ContentAssistContext context, IIdeContentProposalAcceptor acceptor) {
        val metaDataService = LayoutMetaDataService.instance
        for (algorithm : metaDataService.algorithmData) {
            val idSplit = Strings.split(algorithm.id, '.')
            val prefixSplit = Strings.split(context.prefix, '.')
            var foundMatch = false
            var i = idSplit.size - 1
            while (i >= 0 && !foundMatch) {
                val suffix = idSplit.drop(i)
                if (metaDataService.getAlgorithmDataBySuffix(suffix.join('.')) !== null && suffix.startsWith(prefixSplit))
                    foundMatch = true
                else
                    i--
            }
            if (foundMatch) {
                val suffix = idSplit.drop(i)
                val entry = proposalCreator.createProposal(suffix.convert, context, ContentAssistEntry.KIND_VALUE) [
                    label = suffix.join('.')
                    description = getDescription(algorithm)
                    documentation = algorithm.description
                    source = algorithm
                ]
                acceptor.accept(entry, proposalPriorities.getDefaultPriority(entry))
            }
        }
    }
    
    private def startsWith(Iterable<String> strings, List<String> prefix) {
        if (prefix.empty)
            return true
        val stringList = strings.toList
        for (var i = 0; i < stringList.size - prefix.size + 1; i++) {
            var j = 0
            var matches = true
            while (j < prefix.size && matches) {
                matches = stringList.get(i + j).startsWith(prefix.get(j))
                j++
            }
            if (matches)
                return true
        }
        return false
    }
    
    private def convert(Iterable<String> suffix) {
        suffix.map[idValueConverter.toString(it)].join('.')
    }
    
    private def String getDescription(ILayoutMetaData data) {
        '''«data.name» («data.id»)'''
    }
    
    override protected getCrossrefFilter(CrossReference reference, ContentAssistContext context) {
        val model = context.currentModel
        if (model instanceof ElkEdgeSection) {
            switch reference {
                case grammar.elkEdgeSectionAccess.incomingShapeElkConnectableShapeCrossReference_4_0_0_2_0,
                case grammar.elkSingleEdgeSectionAccess.incomingShapeElkConnectableShapeCrossReference_1_0_0_2_0:
                    return new SectionShapeFilter(model, SectionShapeFilter.INCOMING)
                case grammar.elkEdgeSectionAccess.outgoingShapeElkConnectableShapeCrossReference_4_0_1_2_0,
                case grammar.elkSingleEdgeSectionAccess.outgoingShapeElkConnectableShapeCrossReference_1_0_1_2_0:
                    return new SectionShapeFilter(model, SectionShapeFilter.OUTGOING)
            }
        }
        // Show only cross-references from the same resource
        // (cross-resource references don't work anyway due to the opposite references)
        val resourceURI = model.eResource.URI
        return [ candidate |
            candidate.EObjectURI.trimFragment == resourceURI
        ]
    }
    
    @FinalFieldsConstructor
    static class SectionShapeFilter implements Predicate<IEObjectDescription> {
        
        static val INCOMING = 0
        static val OUTGOING = 1
        
        val ElkEdgeSection section
        val int type
        
        override apply(IEObjectDescription input) {
            switch type {
                case INCOMING:
                    input.isInList(section.parent.sources)
                case OUTGOING:
                    input.isInList(section.parent.targets)
                default: true
            }
        }
        
        private def isInList(IEObjectDescription input, List<? extends EObject> list) {
            val object = input.EObjectOrProxy
            if (object.eIsProxy) {
                list.exists[URI == input.EObjectURI]
            } else {
                list.contains(object)
            }
        }
        
    }
    
}