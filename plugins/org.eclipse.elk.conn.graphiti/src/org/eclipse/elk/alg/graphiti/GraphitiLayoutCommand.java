/*******************************************************************************
 * Copyright (c) 2011, 2015 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.graphiti;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.elk.core.math.ElkMath;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.core.util.Pair;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkGraphElement;
import org.eclipse.elk.graph.ElkLabel;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.ElkPort;
import org.eclipse.elk.graph.util.ElkGraphUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.LayoutContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

/**
 * A command for applying the result of automatic layout to a Graphiti diagram.
 * 
 * @author msp
 */
public class GraphitiLayoutCommand extends RecordingCommand {
    
    /** list of graph elements and pictogram elements to layout. */
    private final List<Pair<ElkGraphElement, PictogramElement>> elements = new LinkedList<>();
    /** the feature provider for layout support. */
    private final IFeatureProvider featureProvider;
    /** map of edges to corresponding vector chains. */
    private final Map<ElkEdge, KVectorChain> bendpointsMap = new HashMap<>();
    /** the layout manager for which this command was created. */
    private final GraphitiDiagramLayoutConnector layoutManager;

    /**
     * Creates a Graphiti layout command.
     * 
     * @param domain the transactional editing domain
     * @param thefeatureProvider the feature provider
     */
    public GraphitiLayoutCommand(final TransactionalEditingDomain domain,
            final IFeatureProvider thefeatureProvider, final GraphitiDiagramLayoutConnector manager) {
        
        super(domain, "Automatic Layout");
        this.featureProvider = thefeatureProvider;
        this.layoutManager = manager;
    }
    
    /**
     * Returns the feature provider.
     * 
     * @return the feature provider
     */
    protected IFeatureProvider getFeatureProvider() {
        return featureProvider;
    }

    /**
     * Adds the given element to this command, if it has been modified by a layout algorithm.
     * 
     * @param graphElement an element of the layout graph
     * @param pictogramElement the corresponding pictogram element
     */
    public void add(final ElkGraphElement graphElement, final PictogramElement pictogramElement) {
        // At this point, we marked the graph elements as having been modified, which is not possible anymore
        elements.add(new Pair<ElkGraphElement, PictogramElement>(graphElement, pictogramElement));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        for (Pair<ElkGraphElement, PictogramElement> entry : elements) {
            ElkGraphElement element = entry.getFirst();
            if (element instanceof ElkPort) {
                applyPortLayout((ElkPort) element, entry.getSecond());
            } else if (element instanceof ElkNode) {
                applyNodeLayout((ElkNode) element, entry.getSecond());
            } else if (element instanceof ElkEdge) {
                applyEdgeLayout((ElkEdge) element, entry.getSecond());
            } else if (element instanceof ElkLabel && ((ElkLabel) element).eContainer() instanceof ElkEdge) {
                applyEdgeLabelLayout((ElkLabel) element, entry.getSecond());
            }
        }
        bendpointsMap.clear();
    }

    /**
     * Apply layout for a port.
     * 
     * @param elkport a port
     * @param pelem the corresponding pictogram element
     */
    protected void applyPortLayout(final ElkPort elkport, final PictogramElement pelem) {
        applyPortLayout(elkport.getX(), elkport.getY(), pelem, elkport.getParent());
    }
    
    /**
     * Apply layout for a port.
     * 
     * @param xpos x position of the port
     * @param ypos y position of the port
     * @param pelem the pictogram element
     * @param elknode the node to which the port is connected
     */
    protected void applyPortLayout(final double xpos, final double ypos,
            final PictogramElement pelem, final ElkNode elknode) {
        
        int offsetx = 0, offsety = 0;
        if (pelem.getGraphicsAlgorithm() != null) {
            offsetx = pelem.getGraphicsAlgorithm().getX();
            offsety = pelem.getGraphicsAlgorithm().getY();
        }
        
        if (pelem instanceof BoxRelativeAnchor) {
            BoxRelativeAnchor anchor = (BoxRelativeAnchor) pelem;
            
            double relWidth = (xpos - offsetx) / elknode.getWidth();
            if (relWidth < 0) {
                relWidth = 0;
            } else if (relWidth > 1) {
                relWidth = 1;
            }
            
            double relHeight = (ypos - offsety) / elknode.getHeight();
            if (relHeight < 0) {
                relHeight = 0;
            } else if (relHeight > 1) {
                relHeight = 1;
            }

            anchor.setRelativeWidth(relWidth);
            anchor.setRelativeHeight(relHeight);

            featureProvider.layoutIfPossible(new LayoutContext(pelem));
        } else if (pelem instanceof FixPointAnchor) {
            FixPointAnchor anchor = (FixPointAnchor) pelem;

            anchor.getLocation().setX((int) (xpos - offsetx));
            anchor.getLocation().setY((int) (ypos - offsety));
        }
    }

    /**
     * Apply layout for a node.
     * 
     * @param elknode a node
     * @param pelem the corresponding pictogram element
     */
    protected void applyNodeLayout(final ElkNode elknode, final PictogramElement pelem) {
        GraphicsAlgorithm ga = pelem.getGraphicsAlgorithm();
        
        ga.setX(Math.round((float) elknode.getX()));
        ga.setY(Math.round((float) elknode.getY()));
        ga.setWidth(Math.round((float) elknode.getWidth()));
        ga.setHeight(Math.round((float) elknode.getHeight()));
        
        featureProvider.layoutIfPossible(new LayoutContext(pelem));
    }

    /**
     * Apply layout for an edge.
     * 
     * @param elkedge an edge
     * @param pelem the corresponding pictogram element
     */
    protected void applyEdgeLayout(final ElkEdge elkedge, final PictogramElement pelem) {
        // create bend points for the edge
        KVectorChain bendPoints = getBendPoints(elkedge);

        if (pelem instanceof FreeFormConnection) {
            FreeFormConnection connection = (FreeFormConnection) pelem;
            List<Point> pointList = connection.getBendpoints();
            // add the bend points to the connection, reusing existing points
            for (int i = 0; i < bendPoints.size(); i++) {
                KVector kpoint = bendPoints.get(i);
                if (i >= pointList.size()) {
                    Point point = Graphiti.getGaService().createPoint((int) Math.round(kpoint.x),
                                    (int) Math.round(kpoint.y));
                    pointList.add(point);
                } else {
                    Point point = pointList.get(i);
                    point.setX((int) Math.round(kpoint.x));
                    point.setY((int) Math.round(kpoint.y));
                }
            }
            while (pointList.size() > bendPoints.size()) {
                pointList.remove(pointList.size() - 1);
            }
            
            ElkEdgeSection edgeSection = elkedge.getSections().get(0);
            
            // set source anchor position, if not already set via a port
            if (!(elkedge.getSources().get(0) instanceof ElkPort)) {
                ElkNode source = ElkGraphUtil.connectableShapeToNode(elkedge.getSources().get(0));
                KVector sourcePoint = new KVector(edgeSection.getStartX(), edgeSection.getStartY());
                
                ElkUtil.toAbsolute(sourcePoint, elkedge.getContainingNode());
                ElkUtil.toRelative(sourcePoint, source);
                applyPortLayout(sourcePoint.x, sourcePoint.y, connection.getEnd(), source);
            }
            
            // set target anchor position, if not already set via a port
            if (!(elkedge.getTargets().get(0) instanceof ElkPort)) {
                ElkNode target = ElkGraphUtil.connectableShapeToNode(elkedge.getTargets().get(0));
                KVector targetPoint = new KVector(edgeSection.getEndX(), edgeSection.getEndY());
                
                ElkUtil.toAbsolute(targetPoint, elkedge.getContainingNode());
                ElkUtil.toRelative(targetPoint, target);
                applyPortLayout(targetPoint.x, targetPoint.y, connection.getEnd(), target);
            }
        }
    }
    
    /**
     * Get a vector chain holding the bend points for the given edge.
     * 
     * @param edge a layout edge
     * @return the bend points for the edge
     */
    public KVectorChain getBendPoints(final ElkEdge edge) {
        KVectorChain bendPoints = bendpointsMap.get(edge);
        if (bendPoints == null) {
            // determine the offset for all bend points
            KVector offset = new KVector();
            ElkUtil.toAbsolute(offset, edge.getContainingNode());
            
            // we expect the edge to have an edge section
            ElkEdgeSection edgeSection = edge.getSections().get(0);
            bendPoints = ElkUtil.createVectorChain(edgeSection);
            bendPoints.offset(offset);
            
            // transform spline control points into approximated bend points
            EdgeRouting edgeRouting = edge.getProperty(CoreOptions.EDGE_ROUTING);
            if (edgeRouting == EdgeRouting.SPLINES && edgeSection.getBendPoints().size() >= 1) {
                bendPoints = ElkMath.approximateBezierSpline(bendPoints);
            }
            
            bendPoints.removeFirst();
            bendPoints.removeLast();
            bendpointsMap.put(edge, bendPoints);
        }
        return bendPoints;
    }

    /**
     * Apply layout for an edge label.
     * 
     * @param elklabel an edge label
     * @param pelem the corresponding pictogram element
     */
    protected void applyEdgeLabelLayout(final ElkLabel elklabel, final PictogramElement pelem) {
        GraphicsAlgorithm ga = pelem.getGraphicsAlgorithm();
        ConnectionDecorator decorator = (ConnectionDecorator) pelem;
        ElkEdge elkedge = (ElkEdge) elklabel.eContainer();

        // get vector chain for the bend points of the edge
        KVectorChain bendPoints = new KVectorChain(getBendPoints(elkedge));
        KVector sourcePoint = layoutManager.calculateAnchorEnds(elkedge.getSources().get(0), null);
        bendPoints.addFirst(sourcePoint);
        KVector targetPoint = layoutManager.calculateAnchorEnds(elkedge.getTargets().get(0), null);
        bendPoints.addLast(targetPoint);

        // calculate reference point for the label
        KVector referencePoint;
        if (decorator.isLocationRelative()) {
            referencePoint = bendPoints.pointOnLine(decorator.getLocation()
                            * bendPoints.totalLength());
        } else {
            referencePoint = bendPoints.pointOnLine(decorator.getLocation());
        }
        
        KVector position = new KVector(elklabel.getX(), elklabel.getY());
        ElkUtil.toAbsolute(position, elkedge.getContainingNode());
        ga.setX((int) Math.round(position.x - referencePoint.x));
        ga.setY((int) Math.round(position.y - referencePoint.y));
    }

}
