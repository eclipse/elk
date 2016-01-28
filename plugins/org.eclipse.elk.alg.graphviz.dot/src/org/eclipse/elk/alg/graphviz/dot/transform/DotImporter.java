/*******************************************************************************
 * Copyright (c) 2011, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.graphviz.dot.transform;

import java.util.EnumSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.eclipse.elk.core.klayoutdata.KEdgeLayout;
import org.eclipse.elk.core.klayoutdata.KInsets;
import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.EdgeLabelPlacement;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.core.util.Pair;
import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KLabel;
import org.eclipse.elk.graph.KNode;
import org.eclipse.elk.graph.KPort;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.IPropertyHolder;
import org.eclipse.elk.graph.properties.MapPropertyHolder;
import org.eclipse.elk.graph.properties.Property;
import org.eclipse.elk.alg.graphviz.dot.dot.Attribute;
import org.eclipse.elk.alg.graphviz.dot.dot.AttributeStatement;
import org.eclipse.elk.alg.graphviz.dot.dot.AttributeType;
import org.eclipse.elk.alg.graphviz.dot.dot.DotFactory;
import org.eclipse.elk.alg.graphviz.dot.dot.EdgeStatement;
import org.eclipse.elk.alg.graphviz.dot.dot.EdgeTarget;
import org.eclipse.elk.alg.graphviz.dot.dot.Graph;
import org.eclipse.elk.alg.graphviz.dot.dot.GraphvizModel;
import org.eclipse.elk.alg.graphviz.dot.dot.Node;
import org.eclipse.elk.alg.graphviz.dot.dot.NodeStatement;
import org.eclipse.elk.alg.graphviz.dot.dot.Statement;
import org.eclipse.elk.alg.graphviz.dot.dot.Subgraph;
import org.eclipse.elk.alg.graphviz.dot.dot.util.DotSwitch;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.common.collect.Maps;

/**
 * A transformer for Graphviz Dot.
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public class DotImporter {
    
    /** map of Graphviz node identifiers to their KNode instances. */
    private static final IProperty<Map<String, KNode>> NODE_ID_MAP
            = new Property<Map<String, KNode>>("nodeIdMap");
    /** map of Graphviz port identifiers to their KPort instances. */
    private static final IProperty<Map<Pair<KNode, String>, KPort>> PORT_ID_MAP
            = new Property<Map<Pair<KNode, String>, KPort>>("portIdMap");
    /** original Graphviz statements attached to graph elements. */
    private static final IProperty<Statement> PROP_STATEMENT
            = new Property<Statement>("dotTransformer.statement");
    /** original Graphviz identifiers attached to graph elements. */
    private static final IProperty<String> PROP_ID = new Property<String>("dotTransformer.name");
    /** original Graphviz graph attached to parent nodes. */
    private static final IProperty<Graph> PROP_GRAPH = new Property<Graph>("dotTransformer.graph");
    /** default node width to apply for all nodes. */
    private static final IProperty<Float> PROP_DEF_WIDTH = new Property<Float>(
            "dotTransformer.defWidth");
    /** default node height to apply for all nodes. */
    private static final IProperty<Float> PROP_DEF_HEIGHT = new Property<Float>(
            "dotTransformer.defHeight");
    
    /**
     * Transform the GraphViz model into a KGraph.
     * 
     * @param transData
     *            the transformation data instance that holds the source graph and is enriched with
     *            the new target graphs
     */
    public void transform(final IDotTransformationData<GraphvizModel, KNode> transData) {
        for (Graph graph : transData.getSourceGraph().getGraphs()) {
            KNode parent = ElkUtil.createInitializedNode();
            Map<String, KNode> nodeIdMap = Maps.newHashMap();
            transData.setProperty(NODE_ID_MAP, nodeIdMap);
            Map<Pair<KNode, String>, KPort> portIdMap = Maps.newHashMap();
            transData.setProperty(PORT_ID_MAP, portIdMap);
            transform(graph.getStatements(), parent, transData, new MapPropertyHolder(),
                    new MapPropertyHolder());
            transData.getTargetGraphs().add(parent);
            parent.getData(KShapeLayout.class).setProperty(PROP_GRAPH, graph);
        }
    }

    /**
     * Apply the layout of the target KGraphs to the original GraphViz model. This may only be used
     * on target graphs that were created by the same transformation class.
     * 
     * @param transData
     *            the transformation data instance
     */
    public void transferLayout(final IDotTransformationData<GraphvizModel, KNode> transData) {
        for (KNode layoutNode : transData.getTargetGraphs()) {
            applyLayout(layoutNode, new KVector(),
                    layoutNode.getData(KShapeLayout.class).getProperty(PROP_GRAPH));
        }
    }
    
    
    /*---------- Transformation Dot to KGraph ----------*/

    /**
     * Transform a Dot graph to a KNode.
     * 
     * @param statements
     *            a list of Dot statements
     * @param parent
     *            a KNode
     * @param transData
     *            transformation data instance
     * @param nodeProps
     *            properties that are applied to all nodes
     * @param edgeProps
     *            properties that are applied to all edges
     */
    private void transform(final List<Statement> statements, final KNode parent,
            final IDotTransformationData<GraphvizModel, KNode> transData,
            final IPropertyHolder nodeProps, final IPropertyHolder edgeProps) {
        
        final KShapeLayout parentLayout = parent.getData(KShapeLayout.class);
        DotSwitch<Object> statementSwitch = new DotSwitch<Object>() {

            public Object caseNodeStatement(final NodeStatement statement) {
                transformNode(statement, parent, transData, nodeProps);
                return null;
            }

            public Object caseEdgeStatement(final EdgeStatement statement) {
                transformEdge(statement, parent, transData, edgeProps);
                return null;
            }

            public Object caseSubgraph(final Subgraph subgraph) {
                KNode subKNode = parent;
                if (subgraph.getName() != null && subgraph.getName().startsWith("cluster")) {
                    subKNode = transformNode(subgraph.getName(), parent, transData);
                    KShapeLayout nodeLayout = subKNode.getData(KShapeLayout.class);
                    if (nodeLayout.getProperty(PROP_STATEMENT) != null) {
                        transData.log("Discarding cluster subgraph \"" + subgraph.getName()
                                + "\" since its id is already used.");
                        return null;
                    } else {
                        // the subgraph inherits all settings of its parent
                        nodeLayout.copyProperties(parentLayout);
                        nodeLayout.setProperty(PROP_STATEMENT, subgraph);
                    }
                }
                MapPropertyHolder subNodeProps = new MapPropertyHolder();
                subNodeProps.copyProperties(nodeProps);
                MapPropertyHolder subEdgeProps = new MapPropertyHolder();
                subEdgeProps.copyProperties(edgeProps);
                transform(subgraph.getStatements(), subKNode, transData, subNodeProps, subEdgeProps);
                return null;
            }

            public Object caseAttributeStatement(final AttributeStatement statement) {
                switch (statement.getType()) {
                case GRAPH:
                    for (Attribute attr : statement.getAttributes()) {
                        caseAttribute(attr);
                    }
                    break;
                case NODE:
                    for (Attribute attr : statement.getAttributes()) {
                        transformAttribute(nodeProps, attr, transData);
                    }
                    break;
                case EDGE:
                    for (Attribute attr : statement.getAttributes()) {
                        transformAttribute(edgeProps, attr, transData);
                    }
                    break;
                }
                return null;
            }

            public Object caseAttribute(final Attribute attribute) {
                if (Attributes.MARGIN.equals(attribute.getName())) {
                    KInsets insets = parentLayout.getInsets();
                    if (attribute.getValue().indexOf(',') >= 0) {
                        KVector value = new KVector();
                        try {
                            value.parse(attribute.getValue());
                            insets.setLeft((float) value.x);
                            insets.setRight((float) value.x);
                            insets.setTop((float) value.y);
                            insets.setBottom((float) value.y);
                        } catch (IllegalArgumentException exception) {
                            transData.log("Discarding attribute \"" + attribute.getName()
                                    + "\" since its value could not be parsed correctly.");
                        }
                    } else {
                        try {
                            float value = Float.parseFloat(trimValue(attribute));
                            insets.setLeft(value);
                            insets.setRight(value);
                            insets.setTop(value);
                            insets.setBottom(value);
                        } catch (NumberFormatException exception) {
                            transData.log("Discarding attribute \"" + attribute.getName()
                                    + "\" since its value could not be parsed correctly.");
                        }
                    }
                } else {
                    transformAttribute(parentLayout, attribute, transData);
                }
                return null;
            }

        };
        for (Statement statement : statements) {
            statementSwitch.doSwitch(statement);
        }
    }
    
    /**
     * Apply the property given in the attribute to a property holder.
     * 
     * @param target the property holder that receives the new property
     * @param attribute a Graphviz attribute that contains the property
     * @param transData transformation data
     */
    private void transformAttribute(final IPropertyHolder target, final Attribute attribute,
            final IDotTransformationData<GraphvizModel, KNode> transData) {
        String name = attribute.getName();
        String value = trimValue(attribute);
        try {
            if (Attributes.LAYOUT.equals(name)) {
                Command command = Command.parse(value);
                if (command != Command.INVALID) {
                    target.setProperty(LayoutOptions.ALGORITHM,
                            "org.eclipse.elk.algorithm.graphviz." + command);
                } else {
                    target.setProperty(LayoutOptions.ALGORITHM, value);
                }
            } else if (Attributes.WIDTH.equals(name)) {
                target.setProperty(PROP_DEF_WIDTH, Float.valueOf(value)
                        * DotExporter.DPI);
            } else if (Attributes.HEIGHT.equals(name)) {
                target.setProperty(PROP_DEF_HEIGHT, Float.valueOf(value)
                        * DotExporter.DPI);
            } else if (Attributes.ASPECT.equals(name)) {
                int commaIndex = value.indexOf(',');
                if (commaIndex >= 0) {
                    value = value.substring(0, commaIndex);
                }
                target.setProperty(LayoutOptions.ASPECT_RATIO, Float.valueOf(value));
            } else if (Attributes.FIXEDSIZE.equals(name)) {
                Boolean fixedSize = Boolean.valueOf(value);
                target.setProperty(LayoutOptions.SIZE_CONSTRAINT,
                        fixedSize ? SizeConstraint.fixed() : EnumSet.of(SizeConstraint.MINIMUM_SIZE));
            } else if (Attributes.CONCENTRATE.equals(name)) {
                target.setProperty(Attributes.CONCENTRATE_PROP, Boolean.valueOf(value));
            } else if (Attributes.DAMPING.equals(name)) {
                target.setProperty(Attributes.DAMPING_PROP, Float.valueOf(value));
            } else if (Attributes.EPSILON.equals(name)) {
                target.setProperty(Attributes.EPSILON_PROP, Float.valueOf(value));
            } else if (Attributes.LABELDISTANCE.equals(name)) {
                target.setProperty(Attributes.LABEL_DISTANCE_PROP, Float.valueOf(value));
            } else if (Attributes.LABELANGLE.equals(name)) {
                target.setProperty(Attributes.LABEL_ANGLE_PROP, Float.valueOf(value));
            } else if (Attributes.MAXITER.equals(name)) {
                target.setProperty(Attributes.MAXITER_PROP, Integer.valueOf(value));
            } else if (Attributes.CROSSMIN_LIMIT.equals(name)) {
                target.setProperty(Attributes.ITER_LIMIT_PROP, Float.valueOf(value));
            } else if (Attributes.NEATO_MODEL.equals(name)) {
                target.setProperty(Attributes.NEATO_MODEL_PROP, NeatoModel.parse(value));
            } else if (Attributes.NODESEP.equals(name)) {
                target.setProperty(LayoutOptions.SPACING, Float.valueOf(value));
            } else if (Attributes.OVERLAP.equals(name)) {
                target.setProperty(Attributes.OVERLAP_PROP, OverlapMode.parse(value));
            } else if (Attributes.PACK.equals(name)) {
                target.setProperty(LayoutOptions.SEPARATE_CC, Boolean.valueOf(value));
            } else if (Attributes.PAD.equals(name)) {
                if (value.indexOf(',') >= 0) {
                    KVector pad = new KVector();
                    pad.parse(value);
                    target.setProperty(LayoutOptions.BORDER_SPACING, (float) (pad.x + pad.y) / 2);
                } else {
                    target.setProperty(LayoutOptions.BORDER_SPACING, Float.valueOf(value));
                }
            } else if (Attributes.RANKDIR.equals(name)) {
                if (value.equals("TB")) {
                    target.setProperty(LayoutOptions.DIRECTION, Direction.DOWN);
                } else if (value.equals("BT")) {
                    target.setProperty(LayoutOptions.DIRECTION, Direction.UP);
                } else if (value.equals("LR")) {
                    target.setProperty(LayoutOptions.DIRECTION, Direction.RIGHT);
                } else if (value.equals("RL")) {
                    target.setProperty(LayoutOptions.DIRECTION, Direction.LEFT);
                }
            } else if (Attributes.RANKSEP.equals(name)) {
                if (target.getProperty(LayoutOptions.SPACING) <= 0) {
                    target.setProperty(LayoutOptions.SPACING, Float.valueOf(value));
                }
            } else if (Attributes.SPLINES.equals(name)) {
                if (value.equals("spline") || value.equals("true")) {
                    target.setProperty(LayoutOptions.EDGE_ROUTING, EdgeRouting.SPLINES);
                } else if (value.equals("polyline") || value.equals("line")
                        || value.equals("false")) {
                    target.setProperty(LayoutOptions.EDGE_ROUTING, EdgeRouting.POLYLINE);
                } else if (value.equals("ortho")) {
                    target.setProperty(LayoutOptions.EDGE_ROUTING, EdgeRouting.ORTHOGONAL);
                }
            } else if (Attributes.START.equals(name)) {
                if (value.startsWith("random")) {
                    int random = 0;
                    try {
                        random = Integer.parseInt(value.substring("random".length()));
                    } catch (NumberFormatException e) {
                        // ignore exception
                    }
                    target.setProperty(LayoutOptions.RANDOM_SEED, random);
                }
            } else if (Attributes.WEIGHT.equals(name)) {
                target.setProperty(LayoutOptions.PRIORITY, (int) Float.parseFloat(value));
            }
        } catch (NumberFormatException exception) {
            transData.log("Discarding attribute " + attribute.getName() + "="
                    + attribute.getValue() + " since its value could not be parsed correctly.");
        } catch (IllegalArgumentException exception) {
            transData.log("Discarding attribute " + attribute.getName() + "="
                    + attribute.getValue() + " since its value could not be parsed correctly.");
        }
    }
    
    /**
     * Transforms a node.
     * 
     * @param statement a node statement
     * @param parent the parent node
     * @param transData the transformation data instance
     * @param defaultProps default values for node options
     */
    private void transformNode(final NodeStatement statement, final KNode parent,
            final IDotTransformationData<GraphvizModel, KNode> transData,
            final IPropertyHolder defaultProps) {
        KNode knode = transformNode(statement.getNode().getName(), parent, transData);
        KShapeLayout nodeLayout = knode.getData(KShapeLayout.class);
        if (nodeLayout.getProperty(PROP_STATEMENT) != null) {
            transData.log("Discarding node \"" + statement.getNode().getName()
                    + "\" since its id is already used.");
        } else {
            nodeLayout.copyProperties(defaultProps);
            nodeLayout.setProperty(PROP_STATEMENT, statement);
            Float defWidth = defaultProps.getProperty(PROP_DEF_WIDTH);
            if (defWidth != null) {
                nodeLayout.setWidth(defWidth);
            }
            Float defHeight = defaultProps.getProperty(PROP_DEF_HEIGHT);
            if (defHeight != null) {
                nodeLayout.setHeight(defHeight);
            }
            
            // evaluate attributes for the new node
            for (Attribute attr : statement.getAttributes()) {
                String value = trimValue(attr);
                try {
                    if (Attributes.LABEL.equals(attr.getName())) {
                        KLabel label = ElkUtil.createInitializedLabel(knode);
                        label.setText(value);
                    } else if (Attributes.POS.equals(attr.getName())) {
                        KVector pos = new KVector();
                        pos.parse(value);
                        pos.scale(DotExporter.DPI);
                        nodeLayout.applyVector(pos);
                    } else if (Attributes.WIDTH.equals(attr.getName())) {
                        nodeLayout.setWidth(Float.parseFloat(value)
                                * DotExporter.DPI);
                    } else if (Attributes.HEIGHT.equals(attr.getName())) {
                        nodeLayout.setHeight(Float.parseFloat(value)
                                * DotExporter.DPI);
                    } else {
                        transformAttribute(nodeLayout, attr, transData);
                    }
                } catch (NumberFormatException exception) {
                    transData.log("Discarding attribute \"" + attr.getName()
                            + "\" for node \"" + statement.getNode().getName()
                            + "\" since its value could not be parsed correctly.");
                } catch (IllegalArgumentException exception) {
                    transData.log("Discarding attribute \"" + attr.getName()
                            + "\" for node \"" + statement.getNode().getName()
                            + "\" since its value could not be parsed correctly.");
                }
            }
        }
    }
    
    /** the default width and height value for nodes. */
    private static final float DEF_WIDTH = 10.0f;
    
    /**
     * Transforms a single node, if not already done before.
     * 
     * @param nodeId a node identifier
     * @param parent the parent where the new KNode is stored
     * @param transData the transformation data instance
     * @return a KNode instance
     */
    private KNode transformNode(final String nodeId, final KNode parent,
            final IDotTransformationData<GraphvizModel, KNode> transData) {
        Map<String, KNode> nodeIdMap = transData.getProperty(NODE_ID_MAP);
        KNode knode = nodeIdMap.get(nodeId);
        if (knode == null) {
            knode = ElkUtil.createInitializedNode();
            KShapeLayout nodeLayout = knode.getData(KShapeLayout.class);
            nodeLayout.setWidth(DEF_WIDTH);
            nodeLayout.setHeight(DEF_WIDTH);
            knode.setParent(parent);
            if (nodeId != null) {
                nodeIdMap.put(nodeId, knode);
                nodeLayout.setProperty(PROP_ID, nodeId);
            }
        }
        return knode;
    }
    
    /**
     * Transforms a single port, if not already done before.
     * 
     * @param portId a port identifier
     * @param node the node to which the new KPort belongs
     * @param transData the transformation data instance
     * @return a KPort instance
     */
    private KPort transformPort(final String portId, final KNode node,
            final IDotTransformationData<GraphvizModel, KNode> transData) {
        Map<Pair<KNode, String>, KPort> portIdMap = transData.getProperty(PORT_ID_MAP);
        Pair<KNode, String> key = new Pair<KNode, String>(node, portId);
        KPort kport = portIdMap.get(key);
        if (kport == null) {
            kport = ElkUtil.createInitializedPort();
            kport.setNode(node);
            if (portId != null) {
                portIdMap.put(key, kport);
            }
        }
        return kport;
    }
    
    /**
     * Transforms an edge.
     * 
     * @param statement an edge statement
     * @param parent the parent node
     * @param transData the transformation data instance
     * @param defaultProps default values for edge options
     */
    private void transformEdge(final EdgeStatement statement, final KNode parent,
            final IDotTransformationData<GraphvizModel, KNode> transData,
            final IPropertyHolder defaultProps) {
        String sourceName = statement.getSourceNode().getName();
        KNode source = transformNode(sourceName, parent, transData);
        KPort sourcePort = null;
        if (statement.getSourceNode().getPort() != null) {
            String portName = statement.getSourceNode().getPort().getCompass_pt();
            if (portName == null) {
                portName = statement.getSourceNode().getPort().getName();
            }
            sourcePort = transformPort(portName, source, transData);
        }
        ListIterator<EdgeTarget> targetIter = statement.getEdgeTargets().listIterator();
        while (targetIter.hasNext()) {
            EdgeTarget edgeTarget = targetIter.next();
            KEdge kedge = ElkUtil.createInitializedEdge();
            kedge.setSource(source);
            if (sourcePort != null) {
                kedge.setSourcePort(sourcePort);
            }
            KNode target;
            KPort targetPort = null;
            Node edgeTargetNode = edgeTarget.getTargetnode();
            String targetName;
            if (edgeTargetNode == null) {
                targetName = edgeTarget.getTargetSubgraph().getName();
                target = transformNode(targetName, parent, transData);
            } else {
                targetName = edgeTargetNode.getName();
                target = transformNode(targetName, parent, transData);
                if (edgeTargetNode.getPort() != null) {
                    String portName = edgeTargetNode.getPort().getCompass_pt();
                    if (portName == null) {
                        portName = edgeTargetNode.getPort().getName();
                    }
                    targetPort = transformPort(portName, target, transData);
                }
            }
            kedge.setTarget(target);
            if (targetPort != null) {
                kedge.setTargetPort(targetPort);
            }
            
            KEdgeLayout edgeLayout = kedge.getData(KEdgeLayout.class);
            edgeLayout.copyProperties(defaultProps);
            if (targetIter.previousIndex() == 0) {
                // this is the first target - just store the edge statement
                edgeLayout.setProperty(PROP_STATEMENT, statement);
            } else {
                // the edge statement has more that one target - create a copy
                EdgeStatement newStatement = DotFactory.eINSTANCE.createEdgeStatement();
                Node sourceNode = DotFactory.eINSTANCE.createNode();
                sourceNode.setName(sourceName);
                newStatement.setSourceNode(sourceNode);
                targetIter.remove();
                newStatement.getEdgeTargets().add(edgeTarget);
                for (Attribute attr : statement.getAttributes()) {
                    newStatement.getAttributes().add(EcoreUtil.copy(attr));
                }
                edgeLayout.setProperty(PROP_STATEMENT, newStatement);
            }
            
            // evaluate attributes for the new edge
            for (Attribute attr : statement.getAttributes()) {
                String value = trimValue(attr);
                if (Attributes.LABEL.equals(attr.getName())) {
                    KLabel label = ElkUtil.createInitializedLabel(kedge);
                    label.setText(value);
                    label.getData(KShapeLayout.class).setProperty(LayoutOptions.EDGE_LABEL_PLACEMENT,
                            EdgeLabelPlacement.CENTER);
                } else if (Attributes.HEADLABEL.equals(attr.getName())) {
                    KLabel label = ElkUtil.createInitializedLabel(kedge);
                    label.setText(value);
                    label.getData(KShapeLayout.class).setProperty(LayoutOptions.EDGE_LABEL_PLACEMENT,
                            EdgeLabelPlacement.HEAD);
                } else if (Attributes.TAILLABEL.equals(attr.getName())) {
                    KLabel label = ElkUtil.createInitializedLabel(kedge);
                    label.setText(value);
                    label.getData(KShapeLayout.class).setProperty(LayoutOptions.EDGE_LABEL_PLACEMENT,
                            EdgeLabelPlacement.TAIL);
                } else {
                    transformAttribute(edgeLayout, attr, transData);
                }
            }
            
            // the edge target is the source for the next edge target
            source = target;
            sourceName = targetName;
            sourcePort = targetPort;
        }
    }
    

    /*---------- Layout Transfer KGraph to Dot ----------*/

    /**
     * Apply layout to a parent node and its children.
     * 
     * @param parent a parent node
     * @param offset the node's offset in the graph
     * @param graph the Graphviz graph
     */
    private void applyLayout(final KNode parent, final KVector offset, final Graph graph) {
        for (KNode knode : parent.getChildren()) {
            KShapeLayout nodeLayout = knode.getData(KShapeLayout.class);
            Statement statement = nodeLayout.getProperty(PROP_STATEMENT);
            if (statement == null) {
                // the node was only declared implicitly - create an explicit declaration
                NodeStatement stm = DotFactory.eINSTANCE.createNodeStatement();
                Node node = DotFactory.eINSTANCE.createNode();
                node.setName(nodeLayout.getProperty(PROP_ID));
                stm.setNode(node);
                graph.getStatements().add(stm);
                statement = stm;
            }
            if (statement instanceof NodeStatement) {
                List<Attribute> attributes = ((NodeStatement) statement).getAttributes();
                // transfer node position
                removeAttributes(attributes, Attributes.POS);
                double xpos = nodeLayout.getXpos() + nodeLayout.getWidth() / 2 + offset.x;
                double ypos = nodeLayout.getYpos() + nodeLayout.getHeight() / 2 + offset.y;
                String posString = "\"" + Double.toString(xpos) + "," + Double.toString(ypos) + "\"";
                attributes.add(DotExporter.createAttribute(Attributes.POS, posString));
                // transfer node size
                removeAttributes(attributes, Attributes.WIDTH);
                attributes.add(DotExporter.createAttribute(Attributes.WIDTH,
                        nodeLayout.getWidth() / DotExporter.DPI));
                removeAttributes(attributes, Attributes.HEIGHT);
                attributes.add(DotExporter.createAttribute(Attributes.HEIGHT,
                        nodeLayout.getHeight() / DotExporter.DPI));
            } else if (statement instanceof Subgraph) {
                applyLayout(knode, new KVector(offset).add(nodeLayout.getXpos(),
                        nodeLayout.getYpos()), graph);
            }
            
            for (KEdge kedge : knode.getOutgoingEdges()) {
                applyLayout(kedge, offset, graph);
            }
        }
        
        // transfer graph size to bounding box
        List<Statement> statements;
        KShapeLayout parentLayout = parent.getData(KShapeLayout.class);
        Statement graphStm = parentLayout.getProperty(PROP_STATEMENT);
        if (graphStm instanceof Subgraph) {
            statements = ((Subgraph) graphStm).getStatements();
        } else {
            statements = graph.getStatements();
        }
        removeGraphAttributes(statements, Attributes.BOUNDINGBOX);
        String bbString = "\"0,0," + Float.toString(parentLayout.getWidth())
                + "," + Float.toString(parentLayout.getHeight()) + "\"";
        statements.add(DotExporter.createAttribute(
                Attributes.BOUNDINGBOX, bbString));
    }
    
    /**
     * Apply layout to an edge and its labels.
     * 
     * @param edge an edge
     * @param offset its offset in the graph
     * @param graph the Graphviz graph
     */
    private void applyLayout(final KEdge edge, final KVector offset, final Graph graph) {
        KEdgeLayout edgeLayout = edge.getData(KEdgeLayout.class);
        EdgeStatement edgeStatement = (EdgeStatement) edgeLayout.getProperty(PROP_STATEMENT);
        if (edgeStatement.eContainer() == null) {
            // this can happen when an edge with multiple target declarations was found
            graph.getStatements().add(edgeStatement);
        }
        
        // transfer edge bend points and source / target points
        List<Attribute> attributes = edgeStatement.getAttributes();
        removeAttributes(attributes, Attributes.POS);
        StringBuilder bendpointString = new StringBuilder("\"");
        KVectorChain vectorChain = edgeLayout.createVectorChain();
        ListIterator<KVector> chainIter = vectorChain.listIterator();
        while (chainIter.hasNext()) {
            KVector point = chainIter.next().add(offset);
            bendpointString.append(point.x);
            bendpointString.append(',');
            bendpointString.append(point.y);
            if (chainIter.hasNext()) {
                bendpointString.append(' ');
            }
        }
        bendpointString.append('\"');
        attributes.add(DotExporter.createAttribute(Attributes.POS,
                bendpointString.toString()));
        
        // transfer label positions
        for (KLabel label : edge.getLabels()) {
            KShapeLayout labelLayout = label.getData(KShapeLayout.class);
            String attrKey = null;
            switch (labelLayout.getProperty(LayoutOptions.EDGE_LABEL_PLACEMENT)) {
            case CENTER:
                attrKey = Attributes.LABELPOS;
                break;
            case HEAD:
                attrKey = Attributes.HEADLP;
                break;
            case TAIL:
                attrKey = Attributes.TAILLP;
                break;
            }
            if (attrKey != null) {
                removeAttributes(attributes, attrKey);
                double xpos = labelLayout.getXpos() + labelLayout.getWidth() / 2 + offset.x;
                double ypos = labelLayout.getYpos() + labelLayout.getHeight() / 2 + offset.y;
                String posString = "\"" + Double.toString(xpos)
                        + "," + Double.toString(ypos) + "\"";
                attributes.add(DotExporter.createAttribute(attrKey, posString));
            }
        }
    }
    
    /**
     * Removes all graph-related occurrences of the given key from a statement list.
     * 
     * @param statements a list of statements
     * @param key an attribute key
     */
    private void removeGraphAttributes(final List<Statement> statements, final String key) {
        ListIterator<Statement> stmIter = statements.listIterator();
        while (stmIter.hasNext()) {
            Statement stm = stmIter.next();
            if (stm instanceof Attribute) {
                if (key.equals(((Attribute) stm).getName())) {
                    stmIter.remove();
                }
            } else if (stm instanceof AttributeStatement) {
                AttributeStatement attrStatement = (AttributeStatement) stm;
                if (attrStatement.getType() == AttributeType.GRAPH) {
                    removeAttributes(attrStatement.getAttributes(), key);
                    if (attrStatement.getAttributes().isEmpty()) {
                        stmIter.remove();
                    }
                }
            }
        }
    }
    
    /**
     * Removes all occurrences of the given key from an attributes list.
     * 
     * @param attributes a list of attributes
     * @param key an attribute key
     */
    private void removeAttributes(final List<Attribute> attributes, final String key) {
        ListIterator<Attribute> attrIter = attributes.listIterator();
        while (attrIter.hasNext()) {
            Attribute attr = attrIter.next();
            if (key.equals(attr.getName())) {
                attrIter.remove();
            }
        }
    }
    
    /**
     * Remove the quote characters leading and trailing the attribute value.
     * 
     * @param attribute an attribute
     * @return a trimmed value of the attribute
     */
    private String trimValue(final Attribute attribute) {
        String value = attribute.getValue();
        if (value == null) {
            value = "true";
        } else if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1).trim();
        }
        return value;
    }
    
}
