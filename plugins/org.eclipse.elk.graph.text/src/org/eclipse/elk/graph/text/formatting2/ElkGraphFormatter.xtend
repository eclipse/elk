/*******************************************************************************
 * Copyright (c) 2017 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.graph.text.formatting2

import com.google.inject.Inject
import org.eclipse.elk.graph.EMapPropertyHolder
import org.eclipse.elk.graph.ElkBendPoint
import org.eclipse.elk.graph.ElkEdge
import org.eclipse.elk.graph.ElkEdgeSection
import org.eclipse.elk.graph.ElkLabel
import org.eclipse.elk.graph.ElkNode
import org.eclipse.elk.graph.ElkPort
import org.eclipse.elk.graph.ElkShape
import org.eclipse.elk.graph.impl.ElkPropertyToValueMapEntryImpl
import org.eclipse.elk.graph.text.services.ElkGraphGrammarAccess
import org.eclipse.xtext.formatting2.AbstractFormatter2
import org.eclipse.xtext.formatting2.IFormattableDocument
import org.eclipse.xtext.formatting2.IHiddenRegionFormatter
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1

class ElkGraphFormatter extends AbstractFormatter2 {
    
    static val Procedure1<? super IHiddenRegionFormatter> no_space = [noSpace]
    static val Procedure1<? super IHiddenRegionFormatter> one_space = [oneSpace]
    static val Procedure1<? super IHiddenRegionFormatter> new_line = [newLine]
    static val Procedure1<? super IHiddenRegionFormatter> new_lines = [setNewLines(1, 1, 2)]
    
    @Inject extension ElkGraphGrammarAccess

    def dispatch void format(ElkNode node, extension IFormattableDocument document) {
        if (node.parent === null) {
            node.regionFor.keyword(rootNodeAccess.graphKeyword_1_0).append(one_space)
            node.regionFor.assignment(rootNodeAccess.identifierAssignment_1_1).append(new_lines)
        } else {
            node.regionFor.keyword(elkNodeAccess.nodeKeyword_0).append(one_space)
            node.regionFor.keywords(':', ',').forEach[prepend(no_space).append(one_space)]
            interior(
                node.regionFor.keyword(elkNodeAccess.leftCurlyBracketKeyword_2_0).prepend(one_space).append(new_line),
                node.regionFor.keyword(elkNodeAccess.rightCurlyBracketKeyword_2_4),
                [indent]
            )
            node.formatShapeLayout(document)
        }
        for (property : node.properties) {
            property.format
        }
        for (child : node.children) {
            child.append(new_lines).format
        }
        for (edge : node.containedEdges) {
            edge.append(new_lines).format
        }
        for (port : node.ports) {
            port.append(new_lines).format
        }
        for (label : node.labels) {
            label.append(new_lines).format
        }
    }
    
    def dispatch void format(ElkEdge edge, extension IFormattableDocument document) {
        edge.regionFor.keyword(elkEdgeAccess.edgeKeyword_0).append(one_space)
        edge.regionFor.keyword(elkEdgeAccess.hyphenMinusGreaterThanSignKeyword_4).prepend(one_space).append(one_space)
        edge.regionFor.keywords(':', ',').forEach[prepend(no_space).append(one_space)]
        interior(
            edge.regionFor.keyword(elkEdgeAccess.leftCurlyBracketKeyword_7_0).prepend(one_space).append(new_line),
            edge.regionFor.keyword(elkEdgeAccess.rightCurlyBracketKeyword_7_4),
            [indent]
        )
        interior(
            edge.regionFor.keyword(edgeLayoutAccess.leftSquareBracketKeyword_1).prepend(one_space).append(new_line),
            edge.regionFor.keyword(edgeLayoutAccess.rightSquareBracketKeyword_3).append(new_lines),
            [indent]
        )
        for (section : edge.sections) {
            section.format
            if (section.identifier !== null)
                section.append(new_lines)
        }
        for (property : edge.properties) {
            property.format
        }
        for (label : edge.labels) {
            label.append(new_lines).format
        }
    }
    
    def dispatch void format(ElkPort port, extension IFormattableDocument document) {
        port.regionFor.keyword(elkPortAccess.portKeyword_0).append(one_space)
        port.regionFor.keywords(':', ',').forEach[prepend(no_space).append(one_space)]
        interior(
            port.regionFor.keyword(elkPortAccess.leftCurlyBracketKeyword_2_0).prepend(one_space).append(new_line),
            port.regionFor.keyword(elkPortAccess.rightCurlyBracketKeyword_2_4),
            [indent]
        )
        port.formatShapeLayout(document)
        for (property : port.properties) {
            property.format
        }
        for (label : port.labels) {
            label.append(new_lines).format
        }
    }

    def dispatch void format(ElkLabel label, extension IFormattableDocument document) {
        label.regionFor.keyword(elkLabelAccess.labelKeyword_0).append(one_space)
        label.regionFor.keywords(':', ',').forEach[prepend(no_space).append(one_space)]
        interior(
            label.regionFor.keyword(elkLabelAccess.leftCurlyBracketKeyword_3_0).prepend(one_space).append(new_line),
            label.regionFor.keyword(elkLabelAccess.rightCurlyBracketKeyword_3_4),
            [indent]
        )
        label.formatShapeLayout(document)
        for (property : label.properties) {
            property.format
        }
        for (nestedLabel : label.labels) {
            nestedLabel.append(new_lines).format
        }
    }
    
    def dispatch void format(ElkEdgeSection section, extension IFormattableDocument document) {
        section.regionFor.keywords(':', ',').forEach[prepend(no_space).append(one_space)]
        if (section.identifier === null) {
            section.regionFor.assignment(elkSingleEdgeSectionAccess.incomingShapeAssignment_1_0_0_2).append(new_line)
            section.regionFor.assignment(elkSingleEdgeSectionAccess.outgoingShapeAssignment_1_0_1_2).append(new_line)
            section.regionFor.assignment(elkSingleEdgeSectionAccess.startYAssignment_1_0_2_4).append(new_line)
            section.regionFor.assignment(elkSingleEdgeSectionAccess.endYAssignment_1_0_3_4).append(new_line)
            section.regionFor.keywords(elkSingleEdgeSectionAccess.verticalLineKeyword_1_1_3_0).forEach[prepend(one_space).append(one_space)]
        } else {
            section.regionFor.keyword(elkEdgeSectionAccess.sectionKeyword_0).append(one_space)
            section.regionFor.keyword(elkEdgeSectionAccess.hyphenMinusGreaterThanSignKeyword_2_0).prepend(one_space).append(one_space)
            interior(
                section.regionFor.keyword(elkEdgeSectionAccess.leftSquareBracketKeyword_3).prepend(one_space).append(new_line),
                section.regionFor.keyword(elkEdgeSectionAccess.rightSquareBracketKeyword_5),
                [indent]
            )
            section.regionFor.assignment(elkEdgeSectionAccess.incomingShapeAssignment_4_0_0_2).append(new_line)
            section.regionFor.assignment(elkEdgeSectionAccess.outgoingShapeAssignment_4_0_1_2).append(new_line)
            section.regionFor.assignment(elkEdgeSectionAccess.startYAssignment_4_0_2_4).append(new_line)
            section.regionFor.assignment(elkEdgeSectionAccess.endYAssignment_4_0_3_4).append(new_line)
            section.regionFor.keywords(elkEdgeSectionAccess.verticalLineKeyword_4_1_3_0).forEach[prepend(one_space).append(one_space)]
        }
        section.bendPoints.last.append(new_line)
        for (point : section.bendPoints) {
            point.format
        }
        for (property : section.properties) {
            property.format
        }
    }
    
    def dispatch void format(ElkBendPoint bendPoint, extension IFormattableDocument document) {
        bendPoint.regionFor.keyword(elkBendPointAccess.commaKeyword_1).prepend(no_space).append(one_space)
    }
    
    def dispatch void format(ElkPropertyToValueMapEntryImpl entry, extension IFormattableDocument document) {
        entry.regionFor.keyword(propertyAccess.colonKeyword_1).prepend(no_space).append(one_space)
        val container = entry.eContainer as EMapPropertyHolder
        if (container.properties.indexOf(entry) == container.properties.size - 1)
            entry.append(new_lines)
        else
            entry.append(new_line)
    }
    
    private def void formatShapeLayout(ElkShape shape, extension IFormattableDocument document) {
        interior(
            shape.regionFor.keyword(shapeLayoutAccess.leftSquareBracketKeyword_1).prepend(one_space).append(new_line),
            shape.regionFor.keyword(shapeLayoutAccess.rightSquareBracketKeyword_3).append(new_lines),
            [indent]
        )
        shape.regionFor.assignment(shapeLayoutAccess.YAssignment_2_0_4).append(new_line)
        shape.regionFor.assignment(shapeLayoutAccess.heightAssignment_2_1_4).append(new_line)
    }
    
}
