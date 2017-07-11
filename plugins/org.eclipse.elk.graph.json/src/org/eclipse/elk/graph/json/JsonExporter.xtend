/**
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Kiel University - initial API and implementation
 */
package org.eclipse.elk.graph.json

import com.google.common.base.Splitter
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.common.collect.Maps
import java.util.Collections
import java.util.Iterator
import java.util.Map
import org.eclipse.elk.core.data.LayoutMetaDataService
import org.eclipse.elk.graph.EMapPropertyHolder
import org.eclipse.elk.graph.ElkEdge
import org.eclipse.elk.graph.ElkEdgeSection
import org.eclipse.elk.graph.ElkLabel
import org.eclipse.elk.graph.ElkNode
import org.eclipse.elk.graph.ElkPort
import org.eclipse.elk.graph.ElkShape

/**
 * Exporter from elk graph to json.
 */
public final class JsonExporter {
    
    extension JsonAdapter = new JsonAdapter

    private val BiMap<ElkNode, String> nodeIdMap = HashBiMap.create()
    private val BiMap<ElkPort, String> portIdMap = HashBiMap.create()
    private val BiMap<ElkEdge, String> edgeIdMap = HashBiMap.create()
    private val BiMap<ElkEdgeSection, String> edgeSectionIdMap = HashBiMap.create()

    private val Map<ElkNode, Object> nodeJsonMap = Maps.newHashMap
    private val Map<ElkPort, Object> portJsonMap = Maps.newHashMap
    private val Map<ElkEdge, Object> edgeJsonMap = Maps.newHashMap
    private val Map<ElkEdgeSection, Object> edgeSectionJsonMap = Maps.newHashMap

    private var nodeIdCounter = 0
    private var portIdCounter = 0
    private var edgeIdCounter = 0
    private var edgeSectionIdCounter = 0
    
    // configuration
    private var omitZeroPos = true
    private var omitZeroDim = true
    private var omitLayout = false
    private var shortLayoutOptionKeys = true
    
    new () { }

    public def setOptions(boolean omitZeroPos, boolean omitZeroDim, boolean omitLayout, boolean shortLayoutOptionKeys) {
        this.omitZeroPos = omitZeroPos;
        this.omitZeroDim = omitZeroDim;
        this.omitLayout = omitLayout;
        this.shortLayoutOptionKeys = shortLayoutOptionKeys;
    }

    public def export(ElkNode root) {
        init

        // create a tmp array
        val jsonArray = newJsonArray
        // transform the root node
        root.transformNode(jsonArray)

        // edges (important that all nodes are already transformed!)
        root.transformEdges

        // retrieve the result from the tmp array
        val jsonGraph = jsonArray.optJSONObject(0)
        return jsonGraph
    }

    private def init() {
        nodeIdMap.clear
        portIdMap.clear
        edgeIdMap.clear
        edgeSectionIdMap.clear
        nodeJsonMap.clear
        portJsonMap.clear
        edgeJsonMap.clear
        edgeSectionJsonMap.clear
        nodeIdCounter = 0
        portIdCounter = 0
        edgeIdCounter = 0
        edgeSectionIdCounter = 0
    }

    private def void transformNode(ElkNode node, Object arrayA) {
        // create the node and add it to the parent
        val jsonObj = node.createAndRegister
        val array = arrayA.toJsonArray
        array.addJsonArr(jsonObj)

        // labels
        if (!node.labels.nullOrEmpty) {
            val labels = newJsonArray
            jsonObj.addJsonObj("labels", labels)
            node.labels.forEach[ it.transformLabel(labels) ]
        }

        // ports
        if (!node.ports.nullOrEmpty) {
            val ports = newJsonArray
            jsonObj.addJsonObj("ports", ports)
            node.ports.forEach[ it.transformPort(ports) ]
        }

        // children
        if (!node.children.nullOrEmpty) {
            val children = newJsonArray
            jsonObj.addJsonObj("children", children)
            node.children.forEach[ it.transformNode(children) ]
        }
        
        // properties
        node.transformProperties(jsonObj)
        node.transferShapeLayout(jsonObj)
    }

    private def void transformPort(ElkPort port, Object arrayA) {
        val jsonObj = port.createAndRegister
        val array = arrayA.toJsonArray
        array.addJsonArr(jsonObj)

        // labels
        if (!port.labels.nullOrEmpty) {
            val labels = newJsonArray
            jsonObj.addJsonObj("labels", labels)
            port.labels.forEach[ it.transformLabel(labels) ]
        }

        // properties and things
        port.transformProperties(jsonObj)
        port.transferShapeLayout(jsonObj)
    }

    private def void transformEdges(ElkNode node) {
        
        if (!node.containedEdges.nullOrEmpty) {
            val jsonObj = nodeJsonMap.get(node).toJsonObject
            val edges = newJsonArray
            jsonObj.addJsonObj("edges", edges)
            node.containedEdges.forEach[ it.transformEdge(edges) ]
        }

        // edges of children
        node.children.forEach[ it.transformEdges ]
    }

    private def void transformEdge(ElkEdge edge, Object arrayA) {
        val jsonObj = edge.createAndRegister
        val array = arrayA.toJsonArray
        array.addJsonArr(jsonObj)

        // connection points
        val sources = newJsonArray
        edge.sources.forEach[ s | 
            var source = portIdMap.get(s)
            if (source === null) {
                source = nodeIdMap.get(s)
            }
            if (source === null) {
                 formatError("Unknown edge source: " + s)
            }
            sources.addJsonArr(source.toJson)
        ]
        jsonObj.addJsonObj("sources", sources)
        
        val targets = newJsonArray
        edge.targets.forEach[ t | 
            var target = portIdMap.get(t)
            if (target === null) {
                nodeIdMap.get(t)
            }
            if (target === null) {
                formatError("Unknown edge target: " + target)
            }
            targets.addJsonArr(target.toJson)
        ]
        jsonObj.addJsonObj("targets", targets)

        // labels
        if (!edge.labels.nullOrEmpty) {
            val labels = newJsonArray
            jsonObj.addJsonObj("labels", labels)
            edge.labels.forEach[ it.transformLabel(labels) ]
        }
        
        // sections
        if (!omitLayout && !edge.sections.nullOrEmpty) {
            val sections = newJsonArray
            jsonObj.addJsonObj("sections", sections)
            edge.sections.forEach[ it.transformSection(sections) ]
        }

        // properties        
        edge.transformProperties(jsonObj)
    }
    
    private def void transformSection(ElkEdgeSection section, Object sectionsA) {
        val jsonObj = section.createAndRegister
        val sections = sectionsA.toJsonArray
        sections.addJsonArr(jsonObj)
        
        // Start Point
        val startPoint = newJsonObject
        startPoint.addProperty("x", section.startX)
        startPoint.addProperty("y", section.startY)
        jsonObj.addJsonObj("startPoint", startPoint)
        
        // End Point
        val endPoint = newJsonObject
        endPoint.addProperty("x", section.endX)
        endPoint.addProperty("y", section.endY)
        jsonObj.addJsonObj("endPoint", endPoint)
        
        // Bend Points
        if (!section.bendPoints.nullOrEmpty) {
        val bendPoints = newJsonArray
            section.bendPoints.forEach [ pnt |
                val jsonPnt = newJsonObject
                jsonPnt.addProperty("x", pnt.x)
                jsonPnt.addProperty("y", pnt.y)
                bendPoints.addJsonArr(jsonPnt)
            ]
        }
        
        // Incoming shape
        if (section.incomingShape !== null) {
            jsonObj.addProperty("incomingShape", idByElement(section.incomingShape))
        }
        
        // Outgoing shape
        if (section.outgoingShape !== null) {
            jsonObj.addProperty("outgoingShape", idByElement(section.outgoingShape))
        }
        
        // Incoming sections
        if (!section.incomingSections.nullOrEmpty) {
            val incomingSections = newJsonArray
            section.incomingSections.forEach [ sec | incomingSections.addJsonArr(idByElement(sec).toJson) ]
            jsonObj.addJsonObj("incomingSections", incomingSections)
        }
        
        // Outgoing sections
        if (!section.outgoingSections.nullOrEmpty) {
            val outgoingSections = newJsonArray
            section.outgoingSections.forEach [ sec | outgoingSections.addJsonArr(idByElement(sec).toJson) ]
            jsonObj.addJsonObj("outgoingSections", outgoingSections)
        }
        
        section.transformProperties(jsonObj);
    }

    private def void transformLabel(ElkLabel label, Object array) {
        val jsonLabel = newJsonObject
        jsonLabel.addProperty("text", label.text)
        if (!label.identifier.nullOrEmpty) {
            jsonLabel.addProperty("id", label.identifier)
        }
        array.toJsonArray.addJsonArr(jsonLabel)

        // properties and things
        label.transformProperties(jsonLabel)
        label.transferShapeLayout(jsonLabel)
    }

    private def void transformProperties(EMapPropertyHolder holder, Object parentA) {
        // skip if empty
        if (holder.properties.nullOrEmpty) {
            return
        }
        
        val jsonProps = newJsonObject
        val parent = parentA.toJsonObject
        parent.addJsonObj("properties", jsonProps)
        holder.properties.entrySet.forEach [ p |
            var key = if (shortLayoutOptionKeys) p.key.id.shortOptionKey else p.key.id
            jsonProps.addProperty(key, p.value.toString)
        ]
    }

    private def transferShapeLayout(ElkShape shape, Object jsonObjA) {

        val jsonObj = jsonObjA.toJsonObject
        // position
        if (!omitLayout) {
            if (!omitZeroPos) {
                jsonObj.addProperty("x", shape.x)
                jsonObj.addProperty("y", shape.y)  
            } else {
                // non-equality with double is fine here
                if (shape.x != 0.0) jsonObj.addProperty("x", shape.x)
                if (shape.y != 0.0) jsonObj.addProperty("y", shape.y)
            }
        }
        // dimension
        if (!omitZeroDim) {
            jsonObj.addProperty("width", shape.width)
            jsonObj.addProperty("height", shape.height)    
        } else {
            if (shape.width != 0.0) jsonObj.addProperty("width", shape.width)
            if (shape.height != 0.0) jsonObj.addProperty("height", shape.height)
        }
    }

    /* ---------------------------------------------------------------------------
     *   Convenience methods
     */
    private def createAndRegister(ElkNode node) {
        val obj = newJsonObject
        var id = node.identifier ?: ("n" + nodeIdCounter)
        while (nodeIdMap.inverse.containsKey(id)) {
            id += "_"
        }
        obj.addProperty("id", id)
        nodeIdCounter = nodeIdCounter + 1

        nodeIdMap.put(node, id)
        nodeJsonMap.put(node, obj)

        return obj
    }

    private def createAndRegister(ElkPort port) {
        val obj = newJsonObject
        var id = port.identifier ?: ("p" + portIdCounter)
        while (portIdMap.inverse.containsKey(id)) {
            id += "_"
        }
        obj.addProperty("id", id)
        portIdCounter = portIdCounter + 1

        portIdMap.put(port, id)
        portJsonMap.put(port, obj)

        return obj
    }

    private def createAndRegister(ElkEdge edge) {
        val obj = newJsonObject
        var id = edge.identifier ?: ("e" + edgeIdCounter)
        while (edgeIdMap.inverse.containsKey(id)) {
            id += "_"
        }
        obj.addProperty("id", id)
        edgeIdCounter = edgeIdCounter + 1

        edgeIdMap.put(edge, id)
        edgeJsonMap.put(edge, obj)

        return obj
    }

    private def createAndRegister(ElkEdgeSection section) {
        val obj = newJsonObject
        var id = section.identifier ?: ("s" + edgeSectionIdCounter)
        while (edgeSectionIdMap.inverse.containsKey(id)) {
            id += "_"
        }
        obj.addProperty("id", id)
        edgeSectionIdCounter = edgeIdCounter + 1

        edgeSectionIdMap.put(section, id)
        edgeSectionJsonMap.put(section, obj)

        return obj
    }
    
    private def dispatch idByElement(ElkNode node) {
        return nodeIdMap.get(node);
    }
    
    private def dispatch idByElement(ElkPort port) {
        return portIdMap.get(port);
    }
    
    private def dispatch idByElement(ElkEdgeSection section) {
        return edgeSectionIdMap.get(section);
    }
    
    private def getShortOptionKey(String fullId) {
        val option = LayoutMetaDataService.instance.getOptionData(fullId)
        val idSplit = Splitter.on('.').split(option.id)
        var foundMatch = false
        var i = idSplit.size - 1
        if (i >= 1 && option.group == idSplit.get(i - 1)) {
            i--
        }
        while (i >= 0 && !foundMatch) {
            val suffix = idSplit.drop(i)
            if (LayoutMetaDataService.instance.getOptionDataBySuffix(suffix.join('.')) !== null) {
                foundMatch = true
            } else {
                i--                
            }
        }
        if (foundMatch) {
            return idSplit.drop(i).join('.')
        } else {
            return option.id
        }
    }
        
    def <T> Iterator<T> emptyIfNull(Iterator<T> iterator) {
        if (iterator === null) {
            return Collections.emptyIterator
        } else {
            iterator
        }
    } 
}
