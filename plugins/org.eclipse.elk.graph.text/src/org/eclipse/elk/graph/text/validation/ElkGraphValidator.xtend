/*******************************************************************************
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.graph.text.validation

import com.google.inject.Inject
import java.util.EnumSet
import java.util.Map
import org.eclipse.elk.core.LayoutOptionValidator
import org.eclipse.elk.core.data.LayoutMetaDataService
import org.eclipse.elk.core.data.LayoutOptionData
import org.eclipse.elk.core.options.CoreOptions
import org.eclipse.elk.graph.EMapPropertyHolder
import org.eclipse.elk.graph.ElkEdge
import org.eclipse.elk.graph.ElkEdgeSection
import org.eclipse.elk.graph.ElkGraphElement
import org.eclipse.elk.graph.impl.ElkPropertyToValueMapEntryImpl
import org.eclipse.elk.graph.properties.IProperty
import org.eclipse.elk.graph.util.ElkGraphUtil
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.validation.Check

import static org.eclipse.elk.graph.ElkGraphPackage.Literals.*

import static extension org.eclipse.xtext.EcoreUtil2.*

/**
 * Custom validation rules for the ElkGraph language. 
 */
class ElkGraphValidator extends AbstractElkGraphValidator {
    
    @Inject
    LayoutOptionValidator layoutOptionValidator
	
	@Check
	def void checkPropertyValue(ElkPropertyToValueMapEntryImpl entry) {
	    val property = entry.key
	    val value = entry.value
	    if (property instanceof LayoutOptionData) {
	        var Object translatedValue
	        if (value instanceof String) {
	            translatedValue = property.parseValue(value)
            }
            if (translatedValue === null) {
    	        switch property.type {
    	            case BOOLEAN:
    	                if (value instanceof Boolean)
    	                    translatedValue = value
    	                else
                            expectPropertyType(Boolean)
    	            case INT:
    	                if (value instanceof Integer)
                            translatedValue = value
                        else
                            expectPropertyType(Integer)
    	            case DOUBLE:
                        if (value instanceof Double)
                            translatedValue = value
                        else if (value instanceof Integer)
                            translatedValue = Double.valueOf(value)
                        else
                            expectPropertyType(Double, Integer)
    	            case STRING:
    	                // We know it's not a string, otherwise property.parseValue(value) would have returned it
    	                expectPropertyType(String)
    	            case ENUMSET:
                        expectPropertyType(EnumSet)
    	            default:
    	                if (property.optionClass !== null)
    	                    expectPropertyType(property.optionClass)
    	        }
	        }
	        if (translatedValue !== null) {
	            val issues = layoutOptionValidator.checkProperty(property, translatedValue,
	                   entry.getContainerOfType(ElkGraphElement))
	            for (issue : issues) {
	                switch issue.severity {
	                    case ERROR:
	                        error(issue.message, ELK_PROPERTY_TO_VALUE_MAP_ENTRY__VALUE)
	                    case WARNING:
	                        warning(issue.message, ELK_PROPERTY_TO_VALUE_MAP_ENTRY__VALUE)
	                }
	            }
	            if (CoreOptions.ALGORITHM == property) {
	                if (LayoutMetaDataService.instance.getAlgorithmDataBySuffix(translatedValue as String) === null)
	                   error("No layout algorithm with identifier '" + translatedValue + "' can be found.",
                           ELK_PROPERTY_TO_VALUE_MAP_ENTRY__VALUE)
	            }
	        }
	    }
	}
	
	private def void expectPropertyType(Class<?>... types) {
       error("Expected value of type " + types.map[simpleName].join(' or ') + ".", ELK_PROPERTY_TO_VALUE_MAP_ENTRY__VALUE)
	}
	
	@Check
	def void checkUniqueProperties(EMapPropertyHolder propertyHolder) {
	    val usedProperties = newHashMap
        for (entry : propertyHolder.properties) {
            val property = entry.key
            if (usedProperties.containsKey(property)) {
                propertyAlreadyAssigned(entry)
                val other = usedProperties.get(property)
                if (other !== null) {
                   propertyAlreadyAssigned(other)
                   usedProperties.put(property, null)
               }
            } else {
                usedProperties.put(property, entry)
            }
        }
	}
	
	private def void propertyAlreadyAssigned(Map.Entry<IProperty<?>, Object> entry) {
	    if (entry instanceof ElkPropertyToValueMapEntryImpl)
	       error("Property is already assigned.", entry, ELK_PROPERTY_TO_VALUE_MAP_ENTRY__KEY)
	}
	
	@Check
	def void checkUniqueNames(ElkGraphElement element) {
	    val usedNames = newHashMap
	    for (object : element.eContents) {
	        val name = switch object {
	            ElkGraphElement: object.identifier
	            ElkEdgeSection: object.identifier
	        }
	        if (name !== null) {
	            if (usedNames.containsKey(name)) {
	                nameAlreadyUsed(object)
	                val other = usedNames.get(name)
	                if (other !== null) {
	                   nameAlreadyUsed(other)
	                   usedNames.put(name, null)
                   }
	            } else {
	                usedNames.put(name, object)
	            }
	        }
	    }
	}
	
	private def void nameAlreadyUsed(EObject object) {
	    val feature = switch object {
            ElkGraphElement: ELK_GRAPH_ELEMENT__IDENTIFIER
            ElkEdgeSection: ELK_EDGE_SECTION__IDENTIFIER
        }
	    error("Identifier is already used.", object, feature)
	}
	
	@Check
	def void checkEdgeContainer(ElkEdge edge) {
	    if (!(edge.sources.empty && edge.targets.empty)) {
	        val bestContainer = ElkGraphUtil.findBestEdgeContainment(edge)
	        if (bestContainer !== null && bestContainer != edge.containingNode) {
	            if (bestContainer.parent === null)
	                warning("This edge should be declared in the root node of this graph.", null)
	            else
	                warning("This edge should be declared in node " + bestContainer.identifier + ".", null)
	        }
        }
	}
	
	@Check
	def void checkEdgeSection(ElkEdgeSection edgeSection) {
	    val edge = edgeSection.parent
	    val incomingShape = edgeSection.incomingShape
        if (incomingShape !== null && !incomingShape.eIsProxy) {
            if (!edge.sources.contains(incomingShape))
	            error("The " + incomingShape.eClass.name + " " + incomingShape.identifier + " is not a source of this edge.",
	                ELK_EDGE_SECTION__INCOMING_SHAPE)
            if (!edgeSection.incomingSections.empty)
                error("An edge section cannot be connected to an " + incomingShape.eClass.name + " and other sections at the same time.",
                    ELK_EDGE_SECTION__INCOMING_SHAPE)
        }
        val outgoingShape = edgeSection.outgoingShape
        if (outgoingShape !== null && !outgoingShape.eIsProxy) {
            if (!edge.targets.contains(outgoingShape))
	            error("The " + outgoingShape.eClass.name + " " + outgoingShape.identifier + " is not a target of this edge.",
	                ELK_EDGE_SECTION__OUTGOING_SHAPE)
            if (!edgeSection.outgoingSections.empty)
                error("An edge section cannot be connected to an " + outgoingShape.eClass.name + " and other sections at the same time.",
                    ELK_EDGE_SECTION__OUTGOING_SHAPE)
        }
	}
	
}
