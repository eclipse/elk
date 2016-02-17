/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.graphviz.layouter;

import java.util.List;

import org.eclipse.elk.alg.graphviz.dot.dot.Attribute;
import org.eclipse.elk.alg.graphviz.dot.dot.AttributeStatement;
import org.eclipse.elk.alg.graphviz.dot.dot.AttributeType;
import org.eclipse.elk.alg.graphviz.dot.dot.DotFactory;
import org.eclipse.elk.alg.graphviz.dot.dot.GraphvizModel;
import org.eclipse.elk.alg.graphviz.dot.dot.Statement;
import org.eclipse.elk.alg.graphviz.dot.transform.Attributes;
import org.eclipse.elk.alg.graphviz.dot.transform.Command;
import org.eclipse.elk.alg.graphviz.dot.transform.DotExporter;
import org.eclipse.elk.alg.graphviz.dot.transform.IDotTransformationData;
import org.eclipse.elk.alg.graphviz.dot.transform.NeatoModel;
import org.eclipse.elk.alg.graphviz.dot.transform.OverlapMode;
import org.eclipse.elk.core.klayoutdata.KEdgeLayout;
import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.options.EdgeLabelPlacement;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.HierarchyHandling;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KGraphData;
import org.eclipse.elk.graph.KLabel;
import org.eclipse.elk.graph.KNode;

import com.google.common.collect.Iterables;

/**
 * Specialized exporter for the Dot format that understands layout options.
 */
public class LayoutDotExporter extends DotExporter {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void transform(final IDotTransformationData<KNode, GraphvizModel> transData) {
        KShapeLayout parentLayout = transData.getSourceGraph().getData(KShapeLayout.class);
        Command command = transData.getProperty(COMMAND);
        transData.setProperty(USE_EDGE_IDS, true);
        transData.setProperty(HIERARCHY,
                parentLayout.getProperty(LayoutOptions.HIERARCHY_HANDLING) == HierarchyHandling.INCLUDE_CHILDREN
                && (command == Command.DOT || command == Command.FDP));
        transData.setProperty(TRANSFORM_NODE_LAYOUT, parentLayout.getProperty(LayoutOptions.INTERACTIVE));
        transData.setProperty(TRANSFORM_EDGE_LAYOUT, false);
        transData.setProperty(TRANSFORM_NODE_LABELS, false);
        transData.setProperty(ADAPT_PORT_POSITIONS, parentLayout.getProperty(Properties.ADAPT_PORT_POSITIONS));
        transData.setProperty(BORDER_SPACING, parentLayout.getProperty(LayoutOptions.BORDER_SPACING));
        super.transform(transData);
    }
    
    /** Base factor for setting {@link Attributes#SIMPLEX_LIMIT} from the iterations limit. */
    private static final float NSLIMIT_BASE = 100.0f;
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void setGraphAttributes(final List<Statement> statements,
            final KGraphData parentLayout,
            final IDotTransformationData<KNode, GraphvizModel> transData) {
        
        Command command = transData.getProperty(COMMAND);
        AttributeStatement graphAttrStatement = DotFactory.eINSTANCE.createAttributeStatement();
        graphAttrStatement.setType(AttributeType.GRAPH);
        List<Attribute> graphAttrs = graphAttrStatement.getAttributes();
        statements.add(graphAttrStatement);

        setGeneralNodeAttributes(statements);
        List<Attribute> edgeAttrs = setGeneralEdgeAttributes(statements);

        // set minimal spacing
        Float spacing = parentLayout.getProperty(LayoutOptions.SPACING);
        if (spacing == null || spacing < 0) {
            switch (command) {
            case CIRCO:
            case FDP:
            case NEATO:
                spacing = DEF_SPACING_LARGE;
                break;
            case TWOPI:
                spacing = DEF_SPACING_XLARGE;
                break;
            default:
                spacing = DEF_SPACING_SMALL;
            }
        }

        switch (command) {
        case DOT:
            graphAttrs.add(createAttribute(Attributes.NODESEP, spacing / DPI));
            float rankSepFactor = parentLayout.getProperty(Properties.LAYER_SPACING_FACTOR);
            graphAttrs.add(createAttribute(Attributes.RANKSEP, rankSepFactor * spacing / DPI));
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
            // set iterations limit
            Float iterationsFactor = parentLayout.getProperty(Properties.ITERATIONS_FACTOR);
            if (iterationsFactor != null && iterationsFactor > 0) {
                graphAttrs.add(createAttribute(Attributes.CROSSMIN_LIMIT, iterationsFactor));
                if (iterationsFactor < 1) {
                    float simplexLimit = iterationsFactor * NSLIMIT_BASE;
                    graphAttrs.add(createAttribute(Attributes.SIMPLEX_LIMIT, simplexLimit));
                }
            }
            // enable compound mode
            if (parentLayout.getProperty(LayoutOptions.HIERARCHY_HANDLING) == HierarchyHandling.INCLUDE_CHILDREN) {
                graphAttrs.add(createAttribute(Attributes.COMPOUND, "true"));
            }
            break;

        case TWOPI:
            graphAttrs.add(createAttribute(Attributes.RANKSEP, spacing / DPI));
            break;

        case CIRCO:
            graphAttrs.add(createAttribute(Attributes.MINDIST, spacing / DPI));
            break;

        case NEATO:
            edgeAttrs.add(createAttribute(Attributes.EDGELEN, spacing / DPI));
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
            Float epsilon = parentLayout.getProperty(Properties.EPSILON);
            if (epsilon != null && epsilon > 0) {
                graphAttrs.add(createAttribute(Attributes.EPSILON, epsilon));
            }
            // set distance model
            NeatoModel model = parentLayout.getProperty(Properties.NEATO_MODEL);
            if (model != NeatoModel.SHORTPATH) {
                graphAttrs.add(createAttribute(Attributes.NEATO_MODEL, model.literal()));
            }
            break;

        case FDP:
            graphAttrs.add(createAttribute(Attributes.SPRING_CONSTANT, spacing / DPI));
            break;
        }

        if (command == Command.NEATO || command == Command.FDP) {
            // set maximum number of iterations
            Integer maxiter = parentLayout.getProperty(Properties.MAXITER);
            if (maxiter != null && maxiter > 0) {
                graphAttrs.add(createAttribute(Attributes.MAXITER, maxiter));
            }
        }

        if (command != Command.DOT) {
            // enable or disable node overlap avoidance
            OverlapMode mode = parentLayout.getProperty(Properties.OVERLAP_MODE);
            if (mode != OverlapMode.NONE) {
                graphAttrs.add(createAttribute(Attributes.OVERLAP, mode.literal()));
                graphAttrs.add(createAttribute(Attributes.SEP, "\"+" + Math.round(spacing / 2)
                        + "\""));
            }
            // enable or disable connected component packing
            Boolean pack = parentLayout.getProperty(LayoutOptions.SEPARATE_CONN_COMP);
            if (command == Command.TWOPI || pack != null && pack.booleanValue()) {
                graphAttrs.add(createAttribute(Attributes.PACK, spacing.intValue()));
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
        if (parentLayout.getProperty(Properties.CONCENTRATE)) {
            graphAttrs.add(createAttribute(Attributes.CONCENTRATE, "true"));
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void setEdgeLabels(final KEdge kedge, final List<Attribute> attributes, final boolean isVertical) {
        super.setEdgeLabels(kedge, attributes, isVertical);
        // set label distance and angle
        if (Iterables.any(kedge.getLabels(), (KLabel label) -> {
            KShapeLayout labelLayout = label.getData(KShapeLayout.class);
            EdgeLabelPlacement elp = labelLayout.getProperty(LayoutOptions.EDGE_LABEL_PLACEMENT);
            return elp == EdgeLabelPlacement.HEAD || elp == EdgeLabelPlacement.TAIL;
        })) {
            KEdgeLayout edgeLayout = kedge.getData(KEdgeLayout.class);
            float distance = edgeLayout.getProperty(Properties.LABEL_DISTANCE);
            if (distance >= 0.0f) {
                attributes.add(createAttribute(Attributes.LABELDISTANCE, distance));
            }
            float angle = edgeLayout.getProperty(Properties.LABEL_ANGLE);
            attributes.add(createAttribute(Attributes.LABELANGLE, angle));
        }
    }

}
