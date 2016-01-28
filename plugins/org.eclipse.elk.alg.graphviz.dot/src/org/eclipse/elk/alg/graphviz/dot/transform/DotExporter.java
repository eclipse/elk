/*******************************************************************************
 * Copyright (c) 2010, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.graphviz.dot.transform;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.eclipse.elk.core.klayoutdata.KEdgeLayout;
import org.eclipse.elk.core.klayoutdata.KInsets;
import org.eclipse.elk.core.klayoutdata.KLayoutDataFactory;
import org.eclipse.elk.core.klayoutdata.KPoint;
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
import org.eclipse.elk.graph.KGraphData;
import org.eclipse.elk.graph.KGraphElement;
import org.eclipse.elk.graph.KLabel;
import org.eclipse.elk.graph.KNode;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;
import org.eclipse.elk.alg.graphviz.dot.dot.Attribute;
import org.eclipse.elk.alg.graphviz.dot.dot.AttributeStatement;
import org.eclipse.elk.alg.graphviz.dot.dot.AttributeType;
import org.eclipse.elk.alg.graphviz.dot.dot.DotFactory;
import org.eclipse.elk.alg.graphviz.dot.dot.EdgeStatement;
import org.eclipse.elk.alg.graphviz.dot.dot.EdgeTarget;
import org.eclipse.elk.alg.graphviz.dot.dot.Graph;
import org.eclipse.elk.alg.graphviz.dot.dot.GraphType;
import org.eclipse.elk.alg.graphviz.dot.dot.GraphvizModel;
import org.eclipse.elk.alg.graphviz.dot.dot.Node;
import org.eclipse.elk.alg.graphviz.dot.dot.NodeStatement;
import org.eclipse.elk.alg.graphviz.dot.dot.Statement;
import org.eclipse.elk.alg.graphviz.dot.dot.Subgraph;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * This class implements a transformation from the KGraph metamodel to the Dot metamodel.
 * Furthermore it contains functionality to apply layout information attached to a Dot model to a
 * KGraph model.
 * 
 * @author msp
 * @author mri
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public class DotExporter {

    /** small default value for minimal spacing. */
    public static final float DEF_SPACING_SMALL = 20.0f;
    /** large default value for minimal spacing. */
    public static final float DEF_SPACING_LARGE = 40.0f;
    /** extra-large default value for minimal spacing. */
    public static final float DEF_SPACING_XLARGE = 60.0f;
    /** dots per inch specification, needed by Graphviz for some values. */
    public static final float DPI = 72.0f;

    /** the Graphviz command to use for transformation. */
    public static final IProperty<Command> COMMAND = new Property<Command>(
            "dotExporter.command", Command.DOT);
    /** whether edge identifiers should be generated or not. */
    public static final IProperty<Boolean> USE_EDGE_IDS = new Property<Boolean>(
            "dotExporter.useEdgeIds", false);
    /** whether to always transform the whole graph with all hierarchy levels. */
    public static final IProperty<Boolean> FULL_EXPORT = new Property<Boolean>(
            "dotExporter.fullExport", true);
    /** the additional spacing that is created around edges. */
    public static final IProperty<Float> LABEL_SPACING = new Property<Float>(
            LayoutOptions.LABEL_SPACING, 0.0f, 0.0f);
    
    /** default multiplier for font sizes. */
    private static final double FONT_SIZE_MULT = 1.4;
    /** set of delimiters used to parse attribute values. */
    private static final String ATTRIBUTE_DELIM = "\", \t\n\r";

    /** maps each identifier of a graph element to the instance of the element. */
    private static final IProperty<BiMap<String, KGraphElement>> GRAPH_ELEMS
            = new Property<BiMap<String, KGraphElement>>("dotExporter.graphElemMap");
    /** indicates whether splines are used. */
    private static final IProperty<Boolean> USE_SPLINES = new Property<Boolean>(
            "dotExporter.useSplines", false);
    /** the next node identifier to use. */
    private static final IProperty<Integer> NEXT_NODE_ID = new Property<Integer>(
            "dotExporter.nextNodeId", 1);
    /** the next edge identifier to use. */
    private static final IProperty<Integer> NEXT_EDGE_ID = new Property<Integer>(
            "dotExporter.nextEdgeId", 1);
    /** cluster dummy node identifier attached to a node. */
    private static final IProperty<String> CLUSTER_DUMMY = new Property<String>(
            "dotExporter.clusterDummy");

    /**
     * Transforms the KGraph instance to a Dot instance using the given command.
     * 
     * @param transData the transformation data instance
     */
    public void transform(final IDotTransformationData<KNode, GraphvizModel> transData) {
        BiMap<String, KGraphElement> graphElems = HashBiMap.create();
        transData.setProperty(GRAPH_ELEMS, graphElems);
        KNode kgraph = transData.getSourceGraph();
        GraphvizModel graphvizModel = DotFactory.eINSTANCE.createGraphvizModel();
        Graph graph = DotFactory.eINSTANCE.createGraph();
        graph.setType(GraphType.DIGRAPH);
        graphvizModel.getGraphs().add(graph);
        Command command = transData.getProperty(COMMAND);
        boolean layoutHierarchy = transData.getProperty(FULL_EXPORT)
                || kgraph.getData(KShapeLayout.class).getProperty(LayoutOptions.LAYOUT_HIERARCHY)
                && (command == Command.DOT || command == Command.FDP);
        transformNodes(kgraph, graph.getStatements(), layoutHierarchy, new KVector(), transData);
        transformEdges(kgraph, graph.getStatements(), layoutHierarchy, transData);
        transData.getTargetGraphs().add(graphvizModel);
    }

    /**
     * Applies the layout information attached to the given Dot instance to the KGraph instance
     * using the mapping created by a previous call to {@code transform}. Has to be called after a
     * call to {@code transform}.
     * 
     * @param transData the transformation data instance
     */
    public void transferLayout(final IDotTransformationData<KNode, GraphvizModel> transData) {
        float borderSpacing = transData.getSourceGraph().getData(KShapeLayout.class)
                .getProperty(LayoutOptions.BORDER_SPACING);
        if (borderSpacing < 0) {
            borderSpacing = DEF_SPACING_SMALL / 2;
        }
        Graph graph = transData.getTargetGraphs().get(0).getGraphs().get(0);
        
        // process nodes and subgraphs
        KVector baseOffset = new KVector();
        applyLayout(transData.getSourceGraph(), graph.getStatements(), baseOffset, borderSpacing,
                transData);
        
        // finally process the edges
        LinkedList<Statement> statements = new LinkedList<Statement>(graph.getStatements());
        KVector edgeOffset = baseOffset.add(borderSpacing, borderSpacing);
        while (!statements.isEmpty()) {
            Statement statement = statements.removeFirst();
            if (statement instanceof EdgeStatement) {
                applyEdgeLayout((EdgeStatement) statement, edgeOffset, borderSpacing, transData);
            } else if (statement instanceof Subgraph) {
                statements.addAll(((Subgraph) statement).getStatements());
            }
        }
    }

    /**
     * Transform the child nodes of the given parent node.
     * 
     * @param parent a parent node
     * @param statements the list to which new statements are added
     * @param hierarchy whether hierarchy mode is active
     * @param offset offset of the parent node in the whole graph
     * @param transData transformation data
     */
    private void transformNodes(final KNode parent, final List<Statement> statements,
            final boolean hierarchy, final KVector offset,
            final IDotTransformationData<KNode, GraphvizModel> transData) {
        // set attributes for the whole graph
        boolean fullExport = transData.getProperty(FULL_EXPORT);
        KShapeLayout parentLayout = parent.getData(KShapeLayout.class);
        boolean interactive = parentLayout.getProperty(LayoutOptions.INTERACTIVE);
        if (!fullExport) {
            setGraphAttributes(statements, parentLayout, transData);
        }

        // create nodes and subgraphs
        for (KNode childNode : parent.getChildren()) {
            KShapeLayout nodeLayout = childNode.getData(KShapeLayout.class);
            NodeStatement nodeStatement = DotFactory.eINSTANCE.createNodeStatement();
            List<Attribute> attributes = nodeStatement.getAttributes();
            String nodeID;
            // if hierarchy mode is active, create a subgraph, else a regular node
            if (hierarchy && !childNode.getChildren().isEmpty()) {
                String clusterNodeID = getNodeID(childNode, NodeType.CLUSTER, transData);
                Subgraph subgraph = DotFactory.eINSTANCE.createSubgraph();
                subgraph.setName(clusterNodeID);
                statements.add(subgraph);
                // transform child nodes recursively
                double subgraphx = nodeLayout.getXpos() + nodeLayout.getInsets().getLeft();
                double subgraphy = nodeLayout.getYpos() + nodeLayout.getInsets().getTop();
                transformNodes(childNode, subgraph.getStatements(), true,
                        new KVector(offset).add(subgraphx, subgraphy), transData);
                // create a dummy node for compound edges
                nodeID = getNodeID(childNode, NodeType.DUMMY, transData);
                attributes.add(createAttribute(Attributes.STYLE, "invis"));
                attributes.add(createAttribute(Attributes.WIDTH, 0));
                attributes.add(createAttribute(Attributes.HEIGHT, 0));
                subgraph.getStatements().add(nodeStatement);
            } else {
                nodeID = getNodeID(childNode, NodeType.NODE, transData);
                // set width and height
                ElkUtil.resizeNode(childNode);
                if (nodeLayout.getWidth() > 0) {
                    attributes.add(createAttribute(Attributes.WIDTH, nodeLayout.getWidth() / DPI));
                }
                if (nodeLayout.getHeight() > 0) {
                    attributes.add(createAttribute(Attributes.HEIGHT, nodeLayout.getHeight() / DPI));
                }
                if (!childNode.getLabels().isEmpty()
                        && childNode.getLabels().get(0).getText().length() > 0 && fullExport) {
                    attributes.add(createAttribute(Attributes.LABEL, createString(
                            childNode.getLabels().get(0).getText())));
                }
                // add node position if interactive layout is chosen
                if ((interactive || fullExport && !nodeLayout.getProperty(LayoutOptions.NO_LAYOUT))
                        && (nodeLayout.getXpos() != 0 || nodeLayout.getYpos() != 0)) {
                    double xpos = (nodeLayout.getXpos() + nodeLayout.getWidth() / 2 + offset.x);
                    double ypos = (nodeLayout.getYpos() + nodeLayout.getHeight() / 2 + offset.y);
                    String posString = "\"" + Double.toString(xpos)
                            + "," + Double.toString(ypos) + "\"";
                    attributes.add(createAttribute(Attributes.POS, posString));
                }
                statements.add(nodeStatement);
            }
            Node node = DotFactory.eINSTANCE.createNode();
            node.setName(nodeID);
            nodeStatement.setNode(node);
        }
    }

    /**
     * Transform the edges of the given parent node.
     * 
     * @param parent a parent node
     * @param statements the list to which new statements are added
     * @param hierarchy whether hierarchy mode is active
     * @param transData transformation data
     */
    private void transformEdges(final KNode parent, final List<Statement> statements,
            final boolean hierarchy, final IDotTransformationData<KNode, GraphvizModel> transData) {
        boolean fullExport = transData.getProperty(FULL_EXPORT);
        KShapeLayout parentLayout = parent.getData(KShapeLayout.class);
        Direction direction = parentLayout.getProperty(LayoutOptions.DIRECTION);
        boolean vertical = direction == Direction.DOWN || direction == Direction.UP
                || direction == Direction.UNDEFINED;
        LinkedList<KNode> nodes = new LinkedList<KNode>(parent.getChildren());
        BiMap<KGraphElement, String> nodeIds = transData.getProperty(GRAPH_ELEMS).inverse();
        
        while (!nodes.isEmpty()) {
            KNode source = nodes.removeFirst();
            for (KEdge edge : source.getOutgoingEdges()) {
                KNode target = edge.getTarget();
                // cross-hierarchy edges are considered only if hierarchy mode is active
                if (source.getParent() == target.getParent()
                        || hierarchy && isInsideGraph(target, transData.getSourceGraph())) {
                    EdgeStatement edgeStatement = DotFactory.eINSTANCE.createEdgeStatement();
                    List<Attribute> attributes = edgeStatement.getAttributes();
                    // set source node or cluster
                    Node sourceNode = DotFactory.eINSTANCE.createNode();
                    if (hierarchy && !source.getChildren().isEmpty()) {
                        sourceNode.setName(source.getData(KShapeLayout.class)
                                .getProperty(CLUSTER_DUMMY));
                        attributes.add(createAttribute(Attributes.LTAIL,
                                nodeIds.get(source)));
                    } else {
                        sourceNode.setName(nodeIds.get(source));
                    }
                    edgeStatement.setSourceNode(sourceNode);
                    // set target node or cluster
                    EdgeTarget edgeTarget = DotFactory.eINSTANCE.createEdgeTarget();
                    Node targetNode = DotFactory.eINSTANCE.createNode();
                    if (hierarchy && !target.getChildren().isEmpty()) {
                        targetNode.setName(target.getData(KShapeLayout.class)
                                .getProperty(CLUSTER_DUMMY));
                        attributes.add(createAttribute(Attributes.LHEAD,
                                nodeIds.get(target)));
                    } else {
                        targetNode.setName(nodeIds.get(target));
                    }
                    edgeTarget.setTargetnode(targetNode);
                    edgeStatement.getEdgeTargets().add(edgeTarget);

                    // add edge labels at head, tail, and middle position
                    setEdgeLabels(edge, attributes, vertical);
                    
                    if (transData.getProperty(USE_EDGE_IDS)) {
                        // add comment with edge identifier
                        String edgeID = getEdgeID(edge, transData);
                        attributes.add(createAttribute(Attributes.COMMENT, "\"" + edgeID + "\""));
                    }
                    
                    // include edge routing for full export
                    KEdgeLayout edgeLayout = edge.getData(KEdgeLayout.class);
                    KPoint sourcePoint = edgeLayout.getSourcePoint();
                    KPoint targetPoint = edgeLayout.getTargetPoint();
                    if (fullExport && !edgeLayout.getProperty(LayoutOptions.NO_LAYOUT)
                            && (edgeLayout.getBendPoints().size() > 0
                            || sourcePoint.getX() != 0 || sourcePoint.getY() != 0
                            || targetPoint.getX() != 0 || targetPoint.getY() != 0)) {
                        KNode referenceNode = source;
                        if (!ElkUtil.isDescendant(target, source)) {
                            referenceNode = source.getParent();
                        }
                        StringBuilder pos = new StringBuilder();
                        Iterator<KVector> pointIter = edgeLayout.createVectorChain().iterator();
                        while (pointIter.hasNext()) {
                            KVector point = pointIter.next();
                            ElkUtil.toAbsolute(point, referenceNode);
                            pos.append(point.x);
                            pos.append(",");
                            pos.append(point.y);
                            if (pointIter.hasNext()) {
                                pos.append(" ");
                            }
                        }
                        attributes.add(createAttribute(Attributes.POS, "\"" + pos + "\""));
                    }
                    
                    statements.add(edgeStatement);
                }
            }
            if (hierarchy) {
                nodes.addAll(source.getChildren());
            }
        }
    }
    
    /**
     * Determines whether the given node is contained in the currently processed layout graph.
     * 
     * @param node a node
     * @param root the root node
     * @return true if the node is in the layout graph
     */
    private boolean isInsideGraph(final KNode node, final KNode root) {
        KNode n = node;
        do {
            if (n == root) {
                return true;
            }
            n = n.getParent();
        } while (n != null);
        return false;
    }
    
    /** base factor for setting {@link Attributes#SIMPLEX_LIMIT} from the iterations limit. */
    private static final float NSLIMIT_BASE = 100.0f;

    /**
     * Sets attributes for the whole graph.
     * 
     * @param statements
     *            the statement list for adding attributes
     * @param parentLayout
     *            the layout data for the parent node
     * @param transData
     *            transformation data
     */
    private void setGraphAttributes(final List<Statement> statements,
            final KGraphData parentLayout,
            final IDotTransformationData<KNode, GraphvizModel> transData) {
        
        Command command = transData.getProperty(COMMAND);
        AttributeStatement graphAttrStatement = DotFactory.eINSTANCE.createAttributeStatement();
        graphAttrStatement.setType(AttributeType.GRAPH);
        List<Attribute> graphAttrs = graphAttrStatement.getAttributes();
        statements.add(graphAttrStatement);

        List<Attribute> edgeAttrs = configureGeneralNodeAndEdgeAttributes(statements);

        // set minimal spacing
        float minSpacing = parentLayout.getProperty(LayoutOptions.SPACING);
        if (minSpacing < 0) {
            switch (command) {
            case CIRCO:
            case FDP:
            case NEATO:
                minSpacing = DEF_SPACING_LARGE;
                break;
            case TWOPI:
                minSpacing = DEF_SPACING_XLARGE;
                break;
            default:
                minSpacing = DEF_SPACING_SMALL;
            }
        }

        switch (command) {
        case DOT:
            graphAttrs.add(createAttribute(Attributes.NODESEP, minSpacing / DPI));
            float rankSepFactor = parentLayout.getProperty(Attributes.RANK_SEP_PROP);
            graphAttrs.add(createAttribute(Attributes.RANKSEP, rankSepFactor * minSpacing / DPI));
            // set layout direction
            switch (parentLayout.getProperty(LayoutOptions.DIRECTION)) {
            case UP:
                graphAttrs.add(createAttribute(Attributes.RANKDIR, "BT"));
                break;
            case LEFT:
                graphAttrs.add(createAttribute(Attributes.RANKDIR, "RL"));
                break;
            case RIGHT:
                graphAttrs.add(createAttribute(Attributes.RANKDIR, "LR"));
                break;
            default: // DOWN
                graphAttrs.add(createAttribute(Attributes.RANKDIR, "TB"));
            }
            // set aspect ratio (formerly crashed dot, but doesn't seem to anymore; see KIELER-1799)
            float aspectRatio = parentLayout.getProperty(LayoutOptions.ASPECT_RATIO);
            if (aspectRatio > 0) {
                graphAttrs.add(createAttribute(Attributes.ASPECT, "\"" + aspectRatio + ",1\""));
            }
            // set iterations limit
            float iterationsLimit = parentLayout.getProperty(Attributes.ITER_LIMIT_PROP);
            if (iterationsLimit > 0) {
                graphAttrs.add(createAttribute(Attributes.CROSSMIN_LIMIT, iterationsLimit));
                if (iterationsLimit < 1) {
                    float simplexLimit = iterationsLimit * NSLIMIT_BASE;
                    graphAttrs.add(createAttribute(Attributes.SIMPLEX_LIMIT, simplexLimit));
                }
            }
            // enable compound mode
            if (parentLayout.getProperty(LayoutOptions.LAYOUT_HIERARCHY)) {
                graphAttrs.add(createAttribute(Attributes.COMPOUND, "true"));
            }
            break;

        case TWOPI:
            graphAttrs.add(createAttribute(Attributes.RANKSEP, minSpacing / DPI));
            break;

        case CIRCO:
            graphAttrs.add(createAttribute(Attributes.MINDIST, minSpacing / DPI));
            break;

        case NEATO:
            edgeAttrs.add(createAttribute(Attributes.EDGELEN, minSpacing / DPI));
            // configure initial placement of nodes
            Integer seed = parentLayout.getProperty(LayoutOptions.RANDOM_SEED);
            if (seed == null) {
                seed = 1;
            } else if (seed == 0) {
                seed = -1;
            } else if (seed < 0) {
                seed = -seed;
            }
            graphAttrs.add(createAttribute(Attributes.START, "random" + seed));
            // set epsilon value
            float epsilon = parentLayout.getProperty(Attributes.EPSILON_PROP);
            if (epsilon > 0) {
                graphAttrs.add(createAttribute(Attributes.EPSILON, epsilon));
            }
            // set distance model
            NeatoModel model = parentLayout.getProperty(Attributes.NEATO_MODEL_PROP);
            if (model != NeatoModel.SHORTPATH) {
                graphAttrs.add(createAttribute(Attributes.NEATO_MODEL, model.literal()));
            }
            break;

        case FDP:
            graphAttrs.add(createAttribute(Attributes.SPRING_CONSTANT, minSpacing / DPI));
            break;
        }

        if (command == Command.NEATO || command == Command.FDP) {
            // set maximum number of iterations
            int maxiter = parentLayout.getProperty(Attributes.MAXITER_PROP);
            if (maxiter > 0) {
                graphAttrs.add(createAttribute(Attributes.MAXITER, maxiter));
            }
        }

        if (command != Command.DOT) {
            // enable or disable node overlap avoidance
            OverlapMode mode = parentLayout.getProperty(Attributes.OVERLAP_PROP);
            if (mode != OverlapMode.NONE) {
                graphAttrs.add(createAttribute(Attributes.OVERLAP, mode.literal()));
                graphAttrs.add(createAttribute(Attributes.SEP, "\"+" + Math.round(minSpacing / 2)
                        + "\""));
            }
            // enable or disable connected component packing
            Boolean pack = parentLayout.getProperty(LayoutOptions.SEPARATE_CC);
            if (command == Command.TWOPI || pack != null && pack.booleanValue()) {
                graphAttrs.add(createAttribute(Attributes.PACK, (int) minSpacing));
            }
        }

        // configure edge routing
        EdgeRouting edgeRouting = parentLayout.getProperty(LayoutOptions.EDGE_ROUTING);
        String splineMode;
        switch (edgeRouting) {
        case POLYLINE:
            splineMode = "polyline";
            break;
        case ORTHOGONAL:
            splineMode = "ortho";
            break;
        default:
            splineMode = "spline";
            transData.setProperty(USE_SPLINES, true);
        }
        graphAttrs.add(createAttribute(Attributes.SPLINES, splineMode));

        // enable edge concentration
        if (parentLayout.getProperty(Attributes.CONCENTRATE_PROP)) {
            graphAttrs.add(createAttribute(Attributes.CONCENTRATE, "true"));
        }
    }

    /**
     * @param statements
     * @return
     */
    private List<Attribute> configureGeneralNodeAndEdgeAttributes(final List<Statement> statements) {
        // set general node attributes
        AttributeStatement nodeAttrStatement = DotFactory.eINSTANCE.createAttributeStatement();
        nodeAttrStatement.setType(AttributeType.NODE);
        List<Attribute> nodeAttrs = nodeAttrStatement.getAttributes();
        statements.add(nodeAttrStatement);
        nodeAttrs.add(createAttribute(Attributes.SHAPE, "box"));
        nodeAttrs.add(createAttribute(Attributes.FIXEDSIZE, "true"));

        // set general edge attributes
        AttributeStatement edgeAttrStatement = DotFactory.eINSTANCE.createAttributeStatement();
        edgeAttrStatement.setType(AttributeType.EDGE);
        List<Attribute> edgeAttrs = edgeAttrStatement.getAttributes();
        statements.add(edgeAttrStatement);
        edgeAttrs.add(createAttribute(Attributes.EDGEDIR, "none"));
        return edgeAttrs;
    }

    /**
     * Create an attribute with given name and value for the Dot graph.
     * 
     * @param name name of the attribute
     * @param value value of the attribute
     * @return instance of a Dot attribute
     */
    public static Attribute createAttribute(final String name, final String value) {
        Attribute attribute = DotFactory.eINSTANCE.createAttribute();
        attribute.setName(name);
        attribute.setValue(value);
        return attribute;
    }
    
    /**
     * Create an attribute with given name and integer value for the Dot graph.
     * 
     * @param name name of the attribute
     * @param value value of the attribute
     * @return instance of a Dot attribute
     */
    public static Attribute createAttribute(final String name, final int value) {
        Attribute attribute = DotFactory.eINSTANCE.createAttribute();
        attribute.setName(name);
        attribute.setValue("\"" + value + "\"");
        return attribute;
    }

    /**
     * Create an attribute with given name and float value for the Dot graph.
     * 
     * @param name name of the attribute
     * @param value value of the attribute
     * @return instance of a Dot attribute
     */
    public static Attribute createAttribute(final String name, final float value) {
        Attribute attribute = DotFactory.eINSTANCE.createAttribute();
        attribute.setName(name);
        attribute.setValue("\"" + value + "\"");
        return attribute;
    }

    /**
     * Set edge labels for the given edge.
     * 
     * @param kedge edge whose labels shall be set
     * @param attributes edge attribute list to which the labels are added
     * @param isVertical indicates whether vertical layout direction is active
     */
    private static void setEdgeLabels(final KEdge kedge, final List<Attribute> attributes,
            final boolean isVertical) {
        if (kedge.getLabels().isEmpty()) {
            return;
        }
        KEdgeLayout edgeLayout = kedge.getData(KEdgeLayout.class);
        // as Graphviz only supports positioning of one label per label placement, all labels
        // are stacked to one big label as workaround
        StringBuilder midLabel = new StringBuilder(), headLabel = new StringBuilder(),
                tailLabel = new StringBuilder();
        String fontName = null;
        int fontSize = 0;
        boolean isCenterFontName = false, isCenterFontSize = false;
        for (KLabel label : kedge.getLabels()) {
            StringBuilder buffer = midLabel;
            KShapeLayout labelLayout = label.getData(KShapeLayout.class);
            EdgeLabelPlacement placement = labelLayout.getProperty(LayoutOptions.EDGE_LABEL_PLACEMENT);
            boolean takeFontName = false, takeFontSize = false;
            switch (placement) {
            case HEAD:
                takeFontName = fontName == null;
                takeFontSize = fontSize <= 0;
                buffer = headLabel;
                break;
            case TAIL:
                takeFontName = fontName == null;
                takeFontSize = fontSize <= 0;
                buffer = tailLabel;
                break;
            default: // CENTER
                takeFontName = fontName == null || !isCenterFontName;
                isCenterFontName = true;
                takeFontSize = fontSize <= 0 || !isCenterFontSize;
                isCenterFontSize = true;
                break;
            }
            if (buffer.length() > 0) {
                buffer.append("\n");
            }
            buffer.append(label.getText());
            if (takeFontName) {
                fontName = labelLayout.getProperty(LayoutOptions.FONT_NAME);
            }
            if (takeFontSize) {
                fontSize = labelLayout.getProperty(LayoutOptions.FONT_SIZE);
                // increase the font size to let Graphviz prepare more room for
                // the label
                if (fontSize > 0) {
                    fontSize *= FONT_SIZE_MULT;
                }
            }
        }

        // set mid label: if empty, it is filled with a dummy string to avoid
        // edge overlapping
        if (midLabel.length() > 0) {
            float labelSpacing = edgeLayout.getProperty(LABEL_SPACING);
            int charsToAdd = (labelSpacing < 1 ? 0 : (int) labelSpacing) - 1;
            for (int i = 0; i < charsToAdd; i++) {
                midLabel.append(isVertical ? "O" : "\nO");
            }
            attributes.add(createAttribute(Attributes.LABEL, createString(midLabel.toString())));
        }
        // set head label
        if (headLabel.length() > 0) {
            attributes.add(createAttribute(Attributes.HEADLABEL, createString(headLabel.toString())));
        }
        // set tail label
        if (tailLabel.length() > 0) {
            attributes.add(createAttribute(Attributes.TAILLABEL, createString(tailLabel.toString())));
        }
        // set font name
        if (fontName != null && fontName.length() > 0) {
            attributes.add(createAttribute(Attributes.FONTNAME, "\"" + fontName + "\""));
        }
        // set font size
        if (fontSize > 0) {
            attributes.add(createAttribute(Attributes.FONTSIZE, fontSize));
        }
        // set label distance and angle
        if (headLabel.length() > 0 || tailLabel.length() > 0) {
            float distance = edgeLayout.getProperty(Attributes.LABEL_DISTANCE_PROP);
            if (distance >= 0.0f) {
                attributes.add(createAttribute(Attributes.LABELDISTANCE, distance));
            }
            float angle = edgeLayout.getProperty(Attributes.LABEL_ANGLE_PROP);
            attributes.add(createAttribute(Attributes.LABELANGLE, angle));
        }
    }

    /** first character that is not replaced by underscore. */
    private static final char MIN_OUT_CHAR = 32;
    /** last character that is not replaced by underscore. */
    private static final char MAX_OUT_CHAR = 126;

    /**
     * Creates a properly parseable string by adding the escape character '\\' wherever needed and
     * replacing illegal characters.
     * 
     * @param label a label string from the KGraph structure
     * @return string to be used in the Graphviz model
     */
    private static String createString(final String label) {
        StringBuilder escapeBuffer = new StringBuilder(label.length() + 2);
        // prefix the label with an underscore to prevent it from being equal to
        // a keyword
        escapeBuffer.append("\"");
        for (int i = 0; i < label.length(); i++) {
            char c = label.charAt(i);
            if (c == '\"' || c == '\\' || c > MAX_OUT_CHAR) {
                escapeBuffer.append('_');
            } else if (c == '\n') {
                escapeBuffer.append("\\n");
            } else if (c >= MIN_OUT_CHAR) {
                escapeBuffer.append(c);
            }
        }
        escapeBuffer.append('\"');
        return escapeBuffer.toString();
    }

    /** node types used for identifier generation. */
    private enum NodeType {
        NODE, CLUSTER, DUMMY;
    }
    
    /**
     * Creates a unique identifier for the given node.
     * 
     * @param node node for which an identifier shall be created
     * @param cluster whether a cluster id should be created
     * @param transData transformation data
     * @return a unique string used to identify the node
     */
    private String getNodeID(final KNode node, final NodeType type,
            final IDotTransformationData<KNode, GraphvizModel> transData) {
        int id = transData.getProperty(NEXT_NODE_ID);
        transData.setProperty(NEXT_NODE_ID, id + 1);
        String idstring = null;
        switch (type) {
        case NODE:
            idstring = "node" + id;
            transData.getProperty(GRAPH_ELEMS).put(idstring, node);
            break;
        case CLUSTER:
            idstring = "cluster" + id;
            transData.getProperty(GRAPH_ELEMS).put(idstring, node);
            break;
        case DUMMY:
            idstring = "dummy" + id;
            node.getData(KShapeLayout.class).setProperty(CLUSTER_DUMMY, idstring);
            break;
        }
        return idstring;
    }

    /**
     * Creates a unique identifier for the given edge.
     * 
     * @param edge edge for which an identifier shall be created
     * @param transData transformation data
     * @return a unique string used to identify the edge
     */
    private String getEdgeID(final KEdge edge,
            final IDotTransformationData<KNode, GraphvizModel> transData) {
        int id = transData.getProperty(NEXT_EDGE_ID);
        transData.setProperty(NEXT_EDGE_ID, id + 1);
        String idstring = "edge" + id;
        transData.getProperty(GRAPH_ELEMS).put(idstring, edge);
        return idstring;
    }

    /**
     * Applies layout information from a Graphviz model to the original graph.
     * Note that GraphViz uses cubic B-Splines for edge routing, some generalization of Bezier curves.
     * Edge offsets are given separately, since on some platforms edges seem to have a different
     * reference point than nodes.
     * 
     * @param parentNode parent node of the original graph
     * @param statements list of statements to process
     * @param baseOffset offset to be added to edge and subgraph coordinates
     * @param borderSpacing additional border spacing
     * @param transData transformation data
     */
    private void applyLayout(final KNode parentNode, final List<Statement> statements,
            final KVector baseOffset, final float borderSpacing,
            final IDotTransformationData<KNode, GraphvizModel> transData) {
        // process attributes: determine bounding box of the parent node
        float spacing = borderSpacing;
        KVector nodeOffset = new KVector();
        attr_loop: for (Statement statement : statements) {
            if (statement instanceof AttributeStatement) {
                AttributeStatement attributeStatement = (AttributeStatement) statement;
                if (attributeStatement.getType() == AttributeType.GRAPH) {
                    for (Attribute attribute : attributeStatement.getAttributes()) {
                        if (attribute.getName().equals(Attributes.BOUNDINGBOX)) {
                            try {
                                StringTokenizer tokenizer = new StringTokenizer(attribute.getValue(),
                                        ATTRIBUTE_DELIM);
                                double leftx = Double.parseDouble(tokenizer.nextToken());
                                double bottomy = Double.parseDouble(tokenizer.nextToken());
                                double rightx = Double.parseDouble(tokenizer.nextToken());
                                double topy = Double.parseDouble(tokenizer.nextToken());
                                // set parent node attributes
                                KShapeLayout parentLayout = parentNode.getData(KShapeLayout.class);
                                KInsets insets = parentLayout.getInsets();
                                float width = (float) (rightx - leftx) + insets.getLeft()
                                        + insets.getRight();
                                float height = (float) (bottomy - topy) + insets.getTop()
                                        + insets.getBottom();
                                if (parentNode == transData.getSourceGraph()) {
                                    width += 2 * spacing;
                                    height += 2 * spacing;
                                    baseOffset.add(-leftx, -topy);
                                } else {
                                    parentLayout.setXpos((float) (baseOffset.x + leftx
                                            - insets.getLeft() + spacing));
                                    parentLayout.setYpos((float) (baseOffset.y + topy
                                            - insets.getTop() + spacing));
                                    spacing = 0;
                                    nodeOffset.x = -(baseOffset.x + leftx);
                                    nodeOffset.y = -(baseOffset.y + topy);
                                }
                                ElkUtil.resizeNode(parentNode, width, height, false, true);
                                parentLayout.setProperty(LayoutOptions.SIZE_CONSTRAINT,
                                        SizeConstraint.fixed());
                                break attr_loop;
                            } catch (NumberFormatException exception) {
                                // ignore exception
                            } catch (NoSuchElementException exception) {
                                // ignore exception
                            }
                        }
                    }
                }
            }
        }
        
        // process nodes and subgraphs to collect all offset data
        for (Statement statement : statements) {
            if (statement instanceof NodeStatement) {
                applyNodeLayout((NodeStatement) statement, nodeOffset, spacing, transData);
            } else if (statement instanceof Subgraph) {
                Subgraph subgraph = (Subgraph) statement;
                KNode knode = (KNode) transData.getProperty(GRAPH_ELEMS).get(subgraph.getName());
                applyLayout(knode, subgraph.getStatements(), baseOffset, spacing, transData);
                KShapeLayout subGraphLayout = knode.getData(KShapeLayout.class);
                subGraphLayout.setXpos(subGraphLayout.getXpos() + (float) nodeOffset.x);
                subGraphLayout.setYpos(subGraphLayout.getYpos() + (float) nodeOffset.y);
            }
        }
    }
    
    /**
     * Set the position of a node.
     * 
     * @param nodeStatement a node statement
     * @param offset the offset to be added to node coordinates
     * @param spacing additional border spacing
     * @param transData transformation data
     */
    private void applyNodeLayout(final NodeStatement nodeStatement, final KVector offset,
            final float spacing, final IDotTransformationData<KNode, GraphvizModel> transData) {
        KNode knode = (KNode) transData.getProperty(GRAPH_ELEMS).get(
                nodeStatement.getNode().getName());
        if (knode == null) {
            return;
        }
        KShapeLayout nodeLayout = knode.getData(KShapeLayout.class);
        float xpos = 0.0f, ypos = 0.0f, width = 0.0f, height = 0.0f;
        for (Attribute attribute : nodeStatement.getAttributes()) {
            try {
                if (attribute.getName().equals(Attributes.POS)) {
                    KVector pos = new KVector();
                    pos.parse((attribute.getValue()));
                    xpos = (float) (pos.x + offset.x + spacing);
                    ypos = (float) (pos.y + offset.y + spacing);
                } else if (attribute.getName().equals(Attributes.WIDTH)) {
                    StringTokenizer tokenizer = new StringTokenizer(attribute.getValue(),
                            ATTRIBUTE_DELIM);
                    width = Float.parseFloat(tokenizer.nextToken()) * DPI;
                } else if (attribute.getName().equals(Attributes.HEIGHT)) {
                    StringTokenizer tokenizer = new StringTokenizer(attribute.getValue(),
                            ATTRIBUTE_DELIM);
                    height = Float.parseFloat(tokenizer.nextToken()) * DPI;
                }
            } catch (NumberFormatException exception) {
                // ignore exception
            } catch (IllegalArgumentException exception) {
                // ignore exception
            } catch (NoSuchElementException exception) {
                // ignore exception
            }
        }
        nodeLayout.setXpos(xpos - width / 2);
        nodeLayout.setYpos(ypos - height / 2);
    }
    
    /**
     * Sets the bend points of an edge.
     * 
     * @param edgeStatement an edge statement
     * @param edgeOffset offset to be added to edge coordinates
     * @param spacing additional border spacing
     * @param transData transformation data
     */
    private void applyEdgeLayout(final EdgeStatement edgeStatement, final KVector edgeOffset,
            final float spacing, final IDotTransformationData<KNode, GraphvizModel> transData) {
        Map<String, String> attributeMap = createAttributeMap(edgeStatement.getAttributes());
        KEdge kedge = (KEdge) transData.getProperty(GRAPH_ELEMS).get(
                attributeMap.get(Attributes.COMMENT));
        if (kedge == null) {
            return;
        }
        KEdgeLayout edgeLayout = kedge.getData(KEdgeLayout.class);
        List<KPoint> edgePoints = edgeLayout.getBendPoints();
        edgePoints.clear();
        String posString = attributeMap.get(Attributes.POS);
        if (posString == null) {
            posString = "";
        }
        
        KNode referenceNode = kedge.getSource();
        if (!ElkUtil.isDescendant(kedge.getTarget(), referenceNode)) {
            referenceNode = referenceNode.getParent();
        }
        KVector reference = new KVector();
        while (referenceNode != null && referenceNode != transData.getSourceGraph()) {
            KShapeLayout nodeLayout = referenceNode.getData(KShapeLayout.class);
            reference.x += nodeLayout.getXpos() + nodeLayout.getInsets().getLeft();
            reference.y += nodeLayout.getYpos() + nodeLayout.getInsets().getTop();
            referenceNode = referenceNode.getParent();
        }
        KVector offset = edgeOffset.clone().sub(reference);

        // parse the list of spline control coordinates
        List<KVectorChain> splines = new LinkedList<KVectorChain>();
        Pair<KVector, KVector> endpoints = parseSplinePoints(posString, splines, offset);

        KVector sourcePoint = endpoints.getFirst();
        KVector targetPoint = endpoints.getSecond();
        if (!splines.isEmpty()) {
            KLayoutDataFactory layoutDataFactory = KLayoutDataFactory.eINSTANCE;
            
            // the first point in the list is the start point, if no arrowhead is given
            if (sourcePoint == null) {
                List<KVector> points = splines.get(0);
                if (!points.isEmpty()) {
                    sourcePoint = points.remove(0);
                } else {
                    KShapeLayout sourceLayout = kedge.getSource().getData(KShapeLayout.class);
                    sourcePoint = new KVector();
                    sourcePoint.x = sourceLayout.getXpos() + sourceLayout.getWidth() / 2;
                    sourcePoint.y = sourceLayout.getYpos() + sourceLayout.getHeight() / 2;
                }
            }
            
            // the last point in the list is the end point, if no arrowhead is given
            if (targetPoint == null) {
                List<KVector> points = splines.get(splines.size() - 1);
                if (!points.isEmpty()) {
                    targetPoint = points.remove(points.size() - 1);
                } else {
                    KShapeLayout targetLayout = kedge.getTarget().getData(KShapeLayout.class);
                    targetPoint = new KVector();
                    targetPoint.x = targetLayout.getXpos() + targetLayout.getWidth() / 2;
                    targetPoint.y = targetLayout.getYpos() + targetLayout.getHeight() / 2;
                }
            }
            
            // add all other control points to the edge
            for (KVectorChain points : splines) {
                for (KVector point : points) {
                    KPoint controlPoint = layoutDataFactory.createKPoint();
                    controlPoint.applyVector(point);
                    edgePoints.add(controlPoint);
                }
            }
            edgeLayout.getSourcePoint().applyVector(sourcePoint);
            edgeLayout.getTargetPoint().applyVector(targetPoint);
            
            // set source and target port positions accordingly
            boolean adaptPortPositions = transData.getSourceGraph().getData(KShapeLayout.class)
                    .getProperty(Attributes.ADAPT_PORT_POSITIONS);
            if (adaptPortPositions && (kedge.getSourcePort() != null || kedge.getTargetPort() != null)) {
                referenceNode = kedge.getSource();
                if (!ElkUtil.isDescendant(kedge.getTarget(), referenceNode)) {
                    referenceNode = referenceNode.getParent();
                }
                if (kedge.getSourcePort() != null) {
                    ElkUtil.toAbsolute(sourcePoint, referenceNode);
                    ElkUtil.toRelative(sourcePoint, kedge.getSource().getParent());
                    KShapeLayout portLayout = kedge.getSourcePort().getData(KShapeLayout.class);
                    KShapeLayout sourceLayout = kedge.getSource().getData(KShapeLayout.class);
                    portLayout.setXpos((float) sourcePoint.x - sourceLayout.getXpos()
                            - portLayout.getWidth() / 2);
                    portLayout.setYpos((float) sourcePoint.y - sourceLayout.getYpos()
                            - portLayout.getHeight() / 2);
                }
                if (kedge.getTargetPort() != null) {
                    ElkUtil.toAbsolute(targetPoint, referenceNode);
                    ElkUtil.toRelative(targetPoint, kedge.getTarget().getParent());
                    KShapeLayout portLayout = kedge.getTargetPort().getData(KShapeLayout.class);
                    KShapeLayout targetLayout = kedge.getTarget().getData(KShapeLayout.class);
                    portLayout.setXpos((float) targetPoint.x - targetLayout.getXpos()
                            - portLayout.getWidth() / 2);
                    portLayout.setYpos((float) targetPoint.y - targetLayout.getYpos()
                            - portLayout.getHeight() / 2);
                }
            }
        }
        
        if (transData.getProperty(USE_SPLINES)) {
            edgeLayout.setProperty(LayoutOptions.EDGE_ROUTING, EdgeRouting.SPLINES);
        }

        // process the edge labels
        String labelPos = attributeMap.get(Attributes.LABELPOS);
        if (labelPos != null) {
            applyEdgeLabelPos(kedge, labelPos, EdgeLabelPlacement.CENTER, offset);
        }
        labelPos = attributeMap.get(Attributes.HEADLP);
        if (labelPos != null) {
            applyEdgeLabelPos(kedge, labelPos, EdgeLabelPlacement.HEAD, offset);
        }
        labelPos = attributeMap.get(Attributes.TAILLP);
        if (labelPos != null) {
            applyEdgeLabelPos(kedge, labelPos, EdgeLabelPlacement.TAIL, offset);
        }
    }

    /**
     * Applies the edge label positions for the given edge.
     * 
     * @param kedge edge for which labels are processed
     * @param posString string with label position
     * @param placement label placement to choose
     * @param offsetx x offset added to positions
     * @param offsety y offset added to positions
     */
    private void applyEdgeLabelPos(final KEdge kedge, final String posString,
            final EdgeLabelPlacement placement, final KVector offset) {
        float combinedWidth = 0.0f, combinedHeight = 0.0f;
        for (KLabel label : kedge.getLabels()) {
            KShapeLayout labelLayout = label.getData(KShapeLayout.class);
            EdgeLabelPlacement elp = labelLayout.getProperty(LayoutOptions.EDGE_LABEL_PLACEMENT);
            if (elp == placement || elp == EdgeLabelPlacement.UNDEFINED
                    && placement == EdgeLabelPlacement.CENTER) {
                combinedWidth = Math.max(combinedWidth, labelLayout.getWidth());
                combinedHeight += labelLayout.getHeight();
            }
        }
        try {
            KVector pos = new KVector();
            pos.parse(posString);
            float xpos = (float) (pos.x - combinedWidth / 2 + offset.x);
            float ypos = (float) (pos.y - combinedHeight / 2 + offset.y);
            for (KLabel label : kedge.getLabels()) {
                KShapeLayout labelLayout = label.getData(KShapeLayout.class);
                EdgeLabelPlacement elp = labelLayout.getProperty(LayoutOptions.EDGE_LABEL_PLACEMENT);
                if (elp == placement || elp == EdgeLabelPlacement.UNDEFINED
                        && placement == EdgeLabelPlacement.CENTER) {
                    float xoffset = (combinedWidth - labelLayout.getWidth()) / 2;
                    labelLayout.setXpos(xpos + xoffset);
                    labelLayout.setYpos(ypos);
                    ypos += labelLayout.getHeight();
                }
            }
        } catch (IllegalArgumentException exception) {
            // ignore exception
        }
    }

    /**
     * Converts a list of attributes to a map of attribute names to their values.
     * 
     * @param attributes attribute list
     * @return a hash map that contains all given attributes
     */
    private static Map<String, String> createAttributeMap(final List<Attribute> attributes) {
        Map<String, String> attributeMap = new HashMap<String, String>(attributes.size());
        for (Attribute attribute : attributes) {
            attributeMap.put(attribute.getName(), attribute.getValue());
        }
        return attributeMap;
    }

    /**
     * Puts the points of a position string into a list of splines.
     * 
     * @param posString string with spline points
     * @param splines list of splines
     * @param offset offset to add to coordinates
     * @return the source and the target point, if specified by the position string
     */
    private static Pair<KVector, KVector> parseSplinePoints(final String posString,
            final List<KVectorChain> splines, final KVector offset) {
        KVector sourcePoint = null, targetPoint = null;
        StringTokenizer splinesTokenizer = new StringTokenizer(posString, "\";");
        while (splinesTokenizer.hasMoreTokens()) {
            KVectorChain pointList = new KVectorChain();
            StringTokenizer posTokenizer = new StringTokenizer(splinesTokenizer.nextToken(), " \t");
            while (posTokenizer.hasMoreTokens()) {
                String token = posTokenizer.nextToken();
                try {
                    if (token.startsWith("s")) {
                        if (sourcePoint == null) {
                            sourcePoint = new KVector();
                            int commaIndex = token.indexOf(',');
                            sourcePoint.parse(token.substring(commaIndex + 1));
                            sourcePoint.add(offset);
                        }
                    } else if (token.startsWith("e")) {
                        if (targetPoint == null) {
                            targetPoint = new KVector();
                            int commaIndex = token.indexOf(',');
                            targetPoint.parse(token.substring(commaIndex + 1));
                            targetPoint.add(offset);
                        }
                    } else {
                        KVector point = new KVector();
                        point.parse(token);
                        pointList.add(point.add(offset));
                    }
                } catch (IllegalArgumentException exception) {
                    // ignore exception
                }
            }
            splines.add(pointList);
        }
        return new Pair<KVector, KVector>(sourcePoint, targetPoint);
    }
    
}
