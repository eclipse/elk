/*******************************************************************************
 * Copyright (c) 2016, 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.core.debug.grandom.ui

import java.io.IOException
import java.util.ArrayList
import java.util.Collections
import java.util.List
import java.util.Random
import org.eclipse.core.resources.IFile
import org.eclipse.core.resources.IProject
import org.eclipse.core.runtime.CoreException
import org.eclipse.elk.core.debug.grandom.gRandom.Configuration
import org.eclipse.elk.core.debug.grandom.gRandom.ConstraintType
import org.eclipse.elk.core.debug.grandom.gRandom.DoubleQuantity
import org.eclipse.elk.core.debug.grandom.gRandom.FlowType
import org.eclipse.elk.core.debug.grandom.gRandom.Formats
import org.eclipse.elk.core.debug.grandom.gRandom.Nodes
import org.eclipse.elk.core.debug.grandom.gRandom.RandGraph
import org.eclipse.elk.core.debug.grandom.gRandom.Side
import org.eclipse.elk.core.debug.grandom.generators.GeneratorOptions
import org.eclipse.elk.core.debug.grandom.generators.GeneratorOptions.EdgeDetermination
import org.eclipse.elk.core.debug.grandom.generators.GeneratorOptions.GraphType
import org.eclipse.elk.core.debug.grandom.generators.GeneratorOptions.RandVal
import org.eclipse.elk.core.debug.grandom.generators.RandomGraphGenerator
import org.eclipse.elk.core.options.PortConstraints
import org.eclipse.elk.graph.ElkNode
import org.eclipse.elk.graph.properties.IProperty
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl

class GRandomGraphMaker {

    val Iterable<Configuration> configs
    val DEFAULT_NUM_GRAPHS = 1
    val DEFAULT_NAME = "random"
    val DEFAULT_FORMAT = Formats.ELKT.toString

    new(RandGraph rdg) {
        configs = rdg.configs
    }

    // here the generation of the graphs is triggered and the graphs are stored
    def gen(IProject project) {
        for (config : configs) {
            val filename = if(config.filename.exists) config.filename else DEFAULT_NAME
            val format = if(config.format.exists) config.format else DEFAULT_FORMAT
            // this should be returned to the Test-frameworks graph provider
            val graphs = genElkNode(config)
            var fileNum = 0
            for (var i = 0; i < graphs.size; i++) {
                while (project.getFile(filename + fileNum + '.' + format).exists) {
                    fileNum++
                }
                val f = project.getFile(filename + fileNum + '.' + format)
                serialize(graphs.get(i), f)
            }
        }
    }

    //here the graphs are generated and returned instead of stored in a file
    def List<ElkNode> loadGraph() {
        val graphs = new ArrayList
        for (config : configs) {
            // this should be returned to the Test-frameworks graph provider
            graphs.addAll(genElkNode(config))
        }
        return graphs
    }

    def List<ElkNode> genElkNode(Configuration config) {
        val generated = new ArrayList
        val samples = config.samples
        val nGraphs = if(samples.exists) samples else DEFAULT_NUM_GRAPHS
        val r = random(config)
        for (i : 0 ..< nGraphs) {
            val options = parseGenOptions(config, r)
            val n = new RandomGraphGenerator(r).generate(options)
            generated.add(n)
        }
        return generated
    }

    // here the options are loaded out of the file
    def parseGenOptions(Configuration config, Random r) {
        val GeneratorOptions genOpt = new GeneratorOptions()
        if (GeneratorOptionsUtil.getPreferenceStore().exists)
            GeneratorOptionsUtil.loadPreferences(genOpt)
        setGraphType(config, genOpt)
        nodes(config.nodes, r, genOpt)
        edges(config, genOpt, r)
        hierarchy(config, genOpt, r)

        setQuantities(genOpt, config.fraction, GeneratorOptions.PARTITION_FRAC)
        genOpt.setIfExists(config.maxWidth, GeneratorOptions.MAX_WIDTH)
        genOpt.setIfExists(config.maxDegree, GeneratorOptions.MAX_DEGREE)

        genOpt
    }

    def hierarchy(Configuration configuration, GeneratorOptions options, Random random) {
        val hierarchy = configuration.hierarchy
        if (hierarchy.exists) {
            val hierarch = hierarchy.numHierarchNodes
            setQuantities(options, hierarchy.crossHierarchRel, GeneratorOptions.EXACT_RELATIVE_HIER)
            options.setProperty(GeneratorOptions.SMALL_HIERARCHY, hierarch.exists)
            setQuantities(options, hierarchy.levels, GeneratorOptions.MAX_HIERARCHY_LEVEL)
            setQuantities(options, hierarch, GeneratorOptions.NUMBER_HIERARCHICAL_NODES)
            setQuantities(options, hierarchy.edges, GeneratorOptions.CROSS_HIER)
        }
    }

    private def random(Configuration g) {
        val seed = g.seed
        if (seed === null)
            return new Random()
        else
            return new Random(seed)
    }

    private def edges(Configuration config, GeneratorOptions genOpt, Random r) {
        val edges = config.edges
        if (edges.exists) {
            if (edges.total) {
                genOpt.setProperty(GeneratorOptions.EDGE_DETERMINATION, EdgeDetermination.ABSOLUTE)
                setQuantities(genOpt, edges.NEdges, GeneratorOptions.EDGES_ABSOLUTE)
            } else if (edges.density) {
                setQuantities(genOpt, edges.NEdges, GeneratorOptions.DENSITY)
                genOpt.setProperty(GeneratorOptions.EDGE_DETERMINATION, EdgeDetermination.DENSITY)
            } else if (edges.relative) {
                genOpt.setProperty(GeneratorOptions.EDGE_DETERMINATION, EdgeDetermination.RELATIVE)
                setQuantities(genOpt, edges.NEdges, GeneratorOptions.RELATIVE_EDGES)
            } else {
                genOpt.setProperty(GeneratorOptions.EDGE_DETERMINATION, EdgeDetermination.OUTGOING)
                genOpt.setQuantities(edges.NEdges, GeneratorOptions.OUTGOING_EDGES)
            }
            genOpt.setProperty(GeneratorOptions.EDGE_LABELS, edges.labels)
            genOpt.setProperty(GeneratorOptions.SELF_LOOPS, edges.selfLoops)
        }
    }

    private def nodes(Nodes nodes, Random r, GeneratorOptions genOpt) {
        if (nodes.exists) {
            setQuantities(genOpt, nodes.NNodes, GeneratorOptions.NUMBER_OF_NODES)
            genOpt.setProperty(GeneratorOptions.CREATE_NODE_LABELS, nodes.labels)
            ports(nodes, genOpt, r)
            size(nodes, r, genOpt)
            genOpt.setProperty(GeneratorOptions.ISOLATED_NODES, !nodes.removeIsolated)
        }
    }

    private def size(Nodes nodes, Random r, GeneratorOptions genOpt) {
        val size = nodes.size
        if (size.exists) {
            setQuantities(genOpt, size.width, GeneratorOptions.NODE_WIDTH)
            setQuantities(genOpt, size.height, GeneratorOptions.NODE_HEIGHT)
        }
    }

    private def ports(Nodes nodes, GeneratorOptions genOpt, Random r) {
        val ports = nodes.ports
        if (ports.exists) {
            genOpt.setProperty(GeneratorOptions.ENABLE_PORTS, true)
            genOpt.setProperty(GeneratorOptions.CREATE_PORT_LABELS, ports.labels)
            genOpt.setProperty(GeneratorOptions.PORT_CONSTRAINTS, getConstraint(ports.constraint))
            setQuantities(genOpt, ports.reUse, GeneratorOptions.USE_EXISTING_PORTS_CHANCE)
            val flows = ports.flow
            if (flows.exists) {
                for (f : flows)
                    genOpt.setFlowSide(f.flowType, f.side, f.amount, r)
            }
            val size = ports.size
            genOpt.setProperty(GeneratorOptions.SET_PORT_SIZE, true)
            if (size.exists) {
                setQuantities(genOpt, size.height, GeneratorOptions.PORT_HEIGHT)
                setQuantities(genOpt, size.width, GeneratorOptions.PORT_WIDTH)
            }
        }
    }

    private def setQuantities(GeneratorOptions genOpt, DoubleQuantity quant, IProperty<RandVal> randomValue) {
        if (quant.exists)
            genOpt.setProperty(randomValue, quant.toRandVal)
    }

    def RandVal toRandVal(DoubleQuantity quant) {
        if (quant.minMax) {
            RandVal.minMax(quant.min, quant.max)
        } else if (quant.gaussian) {
            RandVal.gaussian(quant.mean, quant.stddv)
        } else {
            RandVal.exact(quant.quant)
        }
    }

    private def setFlowSide(GeneratorOptions options, FlowType type, Side side, DoubleQuantity quant, Random r) {
        val amount = quant.toRandVal.intVal(r)
        switch (type) {
            case INCOMING: {
                switch (side) {
                    case EAST: {
                        options.setProperty(GeneratorOptions.INCOMING_EAST_SIDE, amount)
                    }
                    case NORTH: {
                        options.setProperty(GeneratorOptions.INCOMING_NORTH_SIDE, amount)
                    }
                    case SOUTH: {
                        options.setProperty(GeneratorOptions.INCOMING_SOUTH_SIDE, amount)
                    }
                    case WEST: {
                        options.setProperty(GeneratorOptions.INCOMING_WEST_SIDE, amount)
                    }
                }
            }
            case OUTGOING: {
                switch (side) {
                    case EAST: {
                        options.setProperty(GeneratorOptions.OUTGOING_EAST_SIDE, amount)
                    }
                    case NORTH: {
                        options.setProperty(GeneratorOptions.OUTGOING_NORTH_SIDE, amount)
                    }
                    case SOUTH: {
                        options.setProperty(GeneratorOptions.OUTGOING_SOUTH_SIDE, amount)
                    }
                    case WEST: {
                        options.setProperty(GeneratorOptions.OUTGOING_WEST_SIDE, amount)
                    }
                }
            }
        }
    }

    private def getConstraint(ConstraintType constraint) {
        switch (constraint) {
            case FREE: {
                return PortConstraints.FREE
            }
            case ORDER: {
                return PortConstraints.FIXED_ORDER
            }
            case POSITION: {
                return PortConstraints.FIXED_POS
            }
            case SIDE: {
                return PortConstraints.FIXED_SIDE
            }
            case RATIO: {
                return PortConstraints.FIXED_RATIO
            }
        }
        return PortConstraints.UNDEFINED
    }

    private def void setGraphType(Configuration configuration, GeneratorOptions options) {
        switch (configuration.form) {
            case ACYCLIC: {
                options.setProperty(GeneratorOptions.GRAPH_TYPE, GraphType.ACYCLIC_NO_TRANSITIVE_EDGES)
            }
            case BICONNECTED: {
                options.setProperty(GeneratorOptions.GRAPH_TYPE, GraphType.BICONNECTED)
            }
            case BIPARTITE: {
                options.setProperty(GeneratorOptions.GRAPH_TYPE, GraphType.BIPARTITE)
            }
            case CUSTOM: {
                options.setProperty(GeneratorOptions.GRAPH_TYPE, GraphType.CUSTOM)
            }
            case TREES: {
                options.setProperty(GeneratorOptions.GRAPH_TYPE, GraphType.TREE)
            }
            case TRICONNECTED: {
                options.setProperty(GeneratorOptions.GRAPH_TYPE, GraphType.TRICONNECTED)
            }
        }
    }

    private def serialize(ElkNode graph, IFile file) throws IOException, CoreException {
        val resourceSet = new ResourceSetImpl();
        val resource = resourceSet.createResource(URI.createURI(file.getLocationURI().toString()));
        resource.getContents().add(graph);
        resource.save(Collections.EMPTY_MAP);
        file.refreshLocal(1, null);
    }

    private def exists(Object o) {
        return o !== null
    }

    private def <T> setIfExists(GeneratorOptions genOpt, T value, IProperty<T> property) {
        if (value.exists) {
            genOpt.setProperty(property, value)
            return true
        }
    }
}
