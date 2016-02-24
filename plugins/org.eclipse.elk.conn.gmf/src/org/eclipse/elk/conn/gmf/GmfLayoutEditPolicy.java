/*******************************************************************************
 * Copyright (c) 2009, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.conn.gmf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.elk.core.klayoutdata.KEdgeLayout;
import org.eclipse.elk.core.klayoutdata.KInsets;
import org.eclipse.elk.core.klayoutdata.KPoint;
import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.math.ElkMath;
import org.eclipse.elk.core.options.EdgeLabelPlacement;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.core.util.Pair;
import org.eclipse.elk.core.util.WrappedException;
import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KGraphElement;
import org.eclipse.elk.graph.KLabel;
import org.eclipse.elk.graph.KLabeledGraphElement;
import org.eclipse.elk.graph.KNode;
import org.eclipse.elk.graph.KPort;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.INodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.LabelEditPart;
import org.eclipse.gmf.runtime.draw2d.ui.geometry.LineSeg;
import org.eclipse.gmf.runtime.draw2d.ui.geometry.PointListUtilities;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.gef.ui.figures.SlidableAnchor;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.View;

/**
 * Edit policy used to apply layout. This edit policy creates a {@link GmfLayoutCommand} to directly
 * manipulate layout data in the GMF notation model.
 * 
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 * @see org.eclipse.gmf.runtime.diagram.ui.editpolicies.XYLayoutEditPolicy
 * @see org.eclipse.gmf.runtime.diagram.ui.editpolicies.ConnectionBendpointEditPolicy
 * @see org.eclipse.gmf.runtime.diagram.ui.editpolicies.GraphicalNodeEditPolicy
 */
public class GmfLayoutEditPolicy extends AbstractEditPolicy {

    /** map of edge layouts to existing point lists. */
    private Map<KEdgeLayout, PointList> pointListMap = new HashMap<KEdgeLayout, PointList>();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean understandsRequest(final Request req) {
        return (ApplyLayoutRequest.REQ_APPLY_LAYOUT.equals(req.getType()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Command getCommand(final Request request) {
        if (ApplyLayoutRequest.REQ_APPLY_LAYOUT.equals(request.getType())) {
            if (request instanceof ApplyLayoutRequest) {
                ApplyLayoutRequest layoutRequest = (ApplyLayoutRequest) request;
                IGraphicalEditPart hostEditPart = (IGraphicalEditPart) getHost();
                GmfLayoutCommand command = new GmfLayoutCommand(hostEditPart.getEditingDomain(),
                        "Automatic Layout", new EObjectAdapter((View) hostEditPart.getModel()));
                float scale = layoutRequest.getScale();

                // retrieve layout data from the request and compute layout data for the command
                for (Pair<KGraphElement, GraphicalEditPart> layoutPair : layoutRequest
                        .getElements()) {
                    if (layoutPair.getFirst() instanceof KNode) {
                        addShapeLayout(command, layoutPair.getFirst(), layoutPair.getSecond(),
                                scale);
                    } else if (layoutPair.getFirst() instanceof KPort) {
                        addShapeLayout(command, layoutPair.getFirst(), layoutPair.getSecond(),
                                scale);
                    } else if (layoutPair.getFirst() instanceof KEdge) {
                        addEdgeLayout(command, (KEdge) layoutPair.getFirst(),
                                (ConnectionEditPart) layoutPair.getSecond(), scale);
                    } else if (layoutPair.getFirst() instanceof KLabel) {
                        addLabelLayout(command, (KLabel) layoutPair.getFirst(),
                                layoutPair.getSecond(), scale);
                    }
                }

                // TODO Make this configurable?
                command.setObliqueRouting(true);

                pointListMap.clear();
                return new ICommandProxy(command);
            } else {
                return null;
            }
        } else {
            return super.getCommand(request);
        }
    }

    /**
     * Adds a shape layout to the given command.
     * 
     * @param command
     *            command to which a shape layout shall be added
     * @param kgraphElement
     *            graph element with layout data
     * @param editPart
     *            edit part to which layout is applied
     * @param scale
     *            scale factor for coordinates
     */
    private void addShapeLayout(final GmfLayoutCommand command, final KGraphElement kgraphElement,
            final GraphicalEditPart editPart, final float scale) {
        KShapeLayout layoutData = kgraphElement.getData(KShapeLayout.class);
        View view = (View) editPart.getModel();

        // check whether the location has changed
        Point newLocation = new Point((int) (layoutData.getXpos() * scale),
                (int) (layoutData.getYpos() * scale));
        Object oldx = ViewUtil.getStructuralFeatureValue(view,
                NotationPackage.eINSTANCE.getLocation_X());
        Object oldy = ViewUtil.getStructuralFeatureValue(view,
                NotationPackage.eINSTANCE.getLocation_Y());
        if (oldx != null && oldy != null && newLocation.x == (Integer) oldx
                && newLocation.y == (Integer) oldy) {
            newLocation = null;
        }

        // check whether the size has changed
        Dimension newSize = new Dimension((int) (layoutData.getWidth() * scale),
                (int) (layoutData.getHeight() * scale));
        Object oldWidth = ViewUtil.getStructuralFeatureValue(view,
                NotationPackage.eINSTANCE.getSize_Width());
        Object oldHeight = ViewUtil.getStructuralFeatureValue(view,
                NotationPackage.eINSTANCE.getSize_Height());
        if (oldWidth != null && oldHeight != null && newSize.width == (Integer) oldWidth
                && newSize.height == (Integer) oldHeight) {
            newSize = null;
        }

        if (newLocation != null || newSize != null) {
            command.addShapeLayout(view, newLocation, newSize);
        }
    }

    /**
     * Adds an edge layout to the given command.
     * 
     * @param command
     *            command to which an edge layout shall be added
     * @param kedge
     *            edge with layout data
     * @param connectionEditPart
     *            edit part to which layout is applied
     * @param scale
     *            scale factor for coordinates
     */
    private void addEdgeLayout(final GmfLayoutCommand command, final KEdge kedge,
            final ConnectionEditPart connectionEditPart, final double scale) {
        if (connectionEditPart.getSource() != null && connectionEditPart.getTarget() != null) {
            // create source terminal identifier
            INodeEditPart sourceEditPart = (INodeEditPart) connectionEditPart.getSource();
            ConnectionAnchor sourceAnchor;
            if (sourceEditPart instanceof ConnectionEditPart) {
                // if the edge source is a connection, don't consider the source point
                sourceAnchor = new SlidableAnchor(sourceEditPart.getFigure());
            } else {
                KVector sourceRel = getRelativeSourcePoint(kedge);
                sourceAnchor = new SlidableAnchor(sourceEditPart.getFigure(), new PrecisionPoint(
                        sourceRel.x, sourceRel.y));
            }
            String sourceTerminal = sourceEditPart.mapConnectionAnchorToTerminal(sourceAnchor);

            // create target terminal identifier
            INodeEditPart targetEditPart = (INodeEditPart) connectionEditPart.getTarget();
            ConnectionAnchor targetAnchor;
            if (targetEditPart instanceof ConnectionEditPart) {
                // if the edge target is a connection, don't consider the target point
                targetAnchor = new SlidableAnchor(targetEditPart.getFigure());
            } else {
                KVector targetRel = getRelativeTargetPoint(kedge);
                targetAnchor = new SlidableAnchor(targetEditPart.getFigure(), new PrecisionPoint(
                        targetRel.x, targetRel.y));
            }
            String targetTerminal = targetEditPart.mapConnectionAnchorToTerminal(targetAnchor);

            PointList bendPoints = getBendPoints(kedge, connectionEditPart.getFigure(), scale);

            // check whether the connection is a note attachment to an edge, then remove bend points
            if (sourceEditPart instanceof ConnectionEditPart
                    || targetEditPart instanceof ConnectionEditPart) {
                while (bendPoints.size() > 2) {
                    bendPoints.removePoint(1);
                }
            }
            
            // retrieve junction points and transform them to absolute coordinates
            KVectorChain junctionPoints = kedge.getData(KEdgeLayout.class)
                    .getProperty(LayoutOptions.JUNCTION_POINTS);
            String serializedJP = null;
            if (junctionPoints != null) {
                KNode referenceNode = kedge.getSource();
                if (!ElkUtil.isDescendant(kedge.getTarget(), referenceNode)) {
                    referenceNode = referenceNode.getParent();
                }
                for (KVector point : junctionPoints) {
                    ElkUtil.toAbsolute(point, referenceNode);
                }
                serializedJP = junctionPoints.toString();
            }

            command.addEdgeLayout((Edge) connectionEditPart.getModel(), bendPoints, sourceTerminal,
                    targetTerminal, serializedJP);
        }
    }

    /**
     * Create a vector that contains the relative position of the source point to the corresponding
     * source node or port.
     * 
     * @param edge
     *            an edge
     * @return the relative source point
     */
    private KVector getRelativeSourcePoint(final KEdge edge) {
        KEdgeLayout edgeLayout = edge.getData(KEdgeLayout.class);
        KNode sourceNode = edge.getSource();
        KNode targetNode = edge.getTarget();
        KPoint sourcePoint = edgeLayout.getSourcePoint();
        KVector sourceRel = sourcePoint.createVector();
        KShapeLayout sourceLayout = sourceNode.getData(KShapeLayout.class);

        if (ElkUtil.isDescendant(targetNode, sourceNode)) {
            // the target node is contained in the source node
            translateDescendantPoint(sourceRel, sourceLayout);
        } else {
            sourceRel.add(-sourceLayout.getXpos(), -sourceLayout.getYpos());
        }

        if (edge.getSourcePort() != null) {
            // calculate the relative position to the port size
            KShapeLayout portLayout = edge.getSourcePort().getData(KShapeLayout.class);
            if (portLayout.getWidth() <= 0) {
                sourceRel.x = 0;
            } else {
                sourceRel.x = (sourceRel.x - portLayout.getXpos()) / portLayout.getWidth();
            }
            if (portLayout.getHeight() <= 0) {
                sourceRel.y = 0;
            } else {
                sourceRel.y = (sourceRel.y - portLayout.getYpos()) / portLayout.getHeight();
            }
        } else {
            // calculate the relative position to the node size
            if (sourceLayout.getWidth() <= 0) {
                sourceRel.x = 0;
            } else {
                sourceRel.x /= sourceLayout.getWidth();
            }
            if (sourceLayout.getHeight() <= 0) {
                sourceRel.y = 0;
            } else {
                sourceRel.y /= sourceLayout.getHeight();
            }
        }

        // check the bound of the relative position
        return sourceRel.bound(0, 0, 1, 1);
    }

    /**
     * Create a vector that contains the relative position of the target point to the corresponding
     * target node or port.
     * 
     * @param edge
     *            an edge
     * @return the relative target point
     */
    private KVector getRelativeTargetPoint(final KEdge edge) {
        KEdgeLayout edgeLayout = edge.getData(KEdgeLayout.class);
        KNode sourceNode = edge.getSource(), targetNode = edge.getTarget();
        KPoint targetPoint = edgeLayout.getTargetPoint();
        KVector targetRel = targetPoint.createVector();
        KShapeLayout targetLayout = targetNode.getData(KShapeLayout.class);

        if (ElkUtil.isDescendant(targetNode, sourceNode)) {
            // the target node is contained in the source node
            if (sourceNode != targetNode.getParent()) {
                ElkUtil.toAbsolute(targetRel, sourceNode);
                ElkUtil.toRelative(targetRel, targetNode.getParent());
            }
            targetRel.add(-targetLayout.getXpos(), -targetLayout.getYpos());
        } else if (sourceNode.getParent() != targetNode.getParent()) {
            // the reference point of the target is different from the source
            ElkUtil.toAbsolute(targetRel, sourceNode.getParent());
            ElkUtil.toRelative(targetRel, targetNode.getParent());
            targetRel.add(-targetLayout.getXpos(), -targetLayout.getYpos());
        } else {
            // source and target have the same reference point
            targetRel.add(-targetLayout.getXpos(), -targetLayout.getYpos());
        }

        if (edge.getTargetPort() != null) {
            // calculate the relative position to the port size
            KShapeLayout portLayout = edge.getTargetPort().getData(KShapeLayout.class);
            if (portLayout.getWidth() <= 0) {
                targetRel.x = 0;
            } else {
                targetRel.x = (targetRel.x - portLayout.getXpos()) / portLayout.getWidth();
            }
            if (portLayout.getHeight() <= 0) {
                targetRel.y = 0;
            } else {
                targetRel.y = (targetRel.y - portLayout.getYpos()) / portLayout.getHeight();
            }
        } else {
            // calculate the relative position to the node size
            if (targetLayout.getWidth() <= 0) {
                targetRel.x = 0;
            } else {
                targetRel.x /= targetLayout.getWidth();
            }
            if (targetLayout.getHeight() <= 0) {
                targetRel.y = 0;
            } else {
                targetRel.y /= targetLayout.getHeight();
            }
        }

        // check the bound of the relative position
        return targetRel.bound(0, 0, 1, 1);
    }

    /**
     * Adds the necessary insets of the layout to the given point.
     * 
     * @param point
     *            the point to translate.
     * @param layout
     *            layout of the node the point is relative to.
     */
    private void translateDescendantPoint(final KVector point, final KShapeLayout layout) {
        // in this case the edge points are given without the source insets, so add them
        KInsets insets = layout.getInsets();
        point.x += insets.getLeft();
        point.y += insets.getTop();
    }

    /** see LabelViewConstants.TARGET_LOCATION. */
    private static final int SOURCE_LOCATION = 85;
    /** see LabelViewConstants.MIDDLE_LOCATION. */
    private static final int MIDDLE_LOCATION = 50;
    /** see LabelViewConstants.SOURCE_LOCATION. */
    private static final int TARGET_LOCATION = 15;

    /**
     * Adds an edge label layout to the given command.
     * 
     * @param command
     *            command to which the edge label layout shall be added
     * @param klabel
     *            label with layout data
     * @param labelEditPart
     *            edit part to which layout is applied
     * @param scale
     *            scale factor for coordinates
     */
    private void addLabelLayout(final GmfLayoutCommand command, final KLabel klabel,
            final GraphicalEditPart labelEditPart, final double scale) {
        KLabeledGraphElement parent = klabel.getParent();
        KShapeLayout labelLayout = klabel.getData(KShapeLayout.class);
        // node and port labels are processed separately
        if (parent instanceof KNode || parent instanceof KPort) {
            View view = (View) labelEditPart.getModel();
            int xpos = (int) (labelLayout.getXpos() * scale);
            int ypos = (int) (labelLayout.getYpos() * scale);
            Object oldx = ViewUtil.getStructuralFeatureValue(view,
                    NotationPackage.eINSTANCE.getLocation_X());
            Object oldy = ViewUtil.getStructuralFeatureValue(view,
                    NotationPackage.eINSTANCE.getLocation_Y());
            if (oldx == null || oldy == null || xpos != (Integer) oldx || ypos != (Integer) oldy) {
                command.addShapeLayout(view, new Point(xpos, ypos), null);
            }
            return;
        }

        // calculate direct new location of the label
        Rectangle targetBounds = new Rectangle(labelEditPart.getFigure().getBounds());
        targetBounds.x = (int) (labelLayout.getXpos() * scale);
        targetBounds.y = (int) (labelLayout.getYpos() * scale);

        ConnectionEditPart connectionEditPart = (ConnectionEditPart) labelEditPart.getParent();
        PointList bendPoints = getBendPoints((KEdge) parent, connectionEditPart.getFigure(), scale);
        EObject modelElement = connectionEditPart.getNotationView().getElement();
        EdgeLabelPlacement labelPlacement = labelLayout
                .getProperty(LayoutOptions.EDGE_LABELS_PLACEMENT);
        // for labels of the opposite reference of an ecore reference,
        // the list of bend points must be reversed
        if (modelElement instanceof EReference && labelPlacement == EdgeLabelPlacement.TAIL) {
            bendPoints = bendPoints.getCopy();
            bendPoints.reverse();
        }

        // get the referencePoint for the label
        int fromEnd, keyPoint = ConnectionLocator.MIDDLE;
        if (labelEditPart instanceof LabelEditPart) {
            keyPoint = ((LabelEditPart) labelEditPart).getKeyPoint();
        }
        switch (keyPoint) {
        case ConnectionLocator.SOURCE:
            fromEnd = SOURCE_LOCATION;
            break;
        case ConnectionLocator.TARGET:
            fromEnd = TARGET_LOCATION;
            break;
        default:
            fromEnd = MIDDLE_LOCATION;
            break;
        }
        Point refPoint = PointListUtilities.calculatePointRelativeToLine(bendPoints, 0, fromEnd,
                true);

        // get the new relative location
        Point normalPoint = offsetFromRelativeCoordinate(targetBounds, bendPoints, refPoint);
        if (normalPoint != null) {
            command.addShapeLayout((View) labelEditPart.getModel(), normalPoint, null);
        }
    }

    /**
     * Transform the bend points of the given edge layout into a point list, reusing existing ones
     * if possible. The source and target points of the edge layout are included in the point list.
     * 
     * @param edge
     *            the edge for which to fetch bend points
     * @param isSplineEdge
     *            indicates whether the connection supports splines
     * @return point list with the bend points of the edge layout
     * @param scale
     *            scale factor for coordinates
     */
    private PointList getBendPoints(final KEdge edge, final IFigure edgeFigure, final double scale) {
        KEdgeLayout edgeLayout = edge.getData(KEdgeLayout.class);
        PointList pointList = pointListMap.get(edgeLayout);
        if (pointList == null) {
            KVectorChain bendPoints = edgeLayout.createVectorChain();

            // for connections that support splines the control points are passed without change
            boolean approx = handleSplineConnection(edgeFigure,
                    edgeLayout.getProperty(LayoutOptions.EDGE_ROUTING));
            // in other cases an approximation is used
            if (approx && bendPoints.size() >= 1) {
                bendPoints = ElkMath.approximateBezierSpline(bendPoints);
            }

            bendPoints.scale(scale);
            pointList = new PointList(bendPoints.size() + 2);
            for (KVector bendPoint : bendPoints) {
                pointList.addPoint((int) bendPoint.x, (int) bendPoint.y);
            }

            pointListMap.put(edgeLayout, pointList);
        }
        return pointList;
    }

    /**
     * class name of the ELK SplineConnection.
     * 
     * TODO: This class doesn't exist anymore...
     */
    private static final String SPLINE_CONNECTION
            = "org.eclipse.elk.core.model.gmf.figures.SplineConnection";

    /**
     * Handle the ELK SplineConnection class without a direct reference to it. Reflection is used
     * to avoid a dependency to its containing plugin.
     * 
     * @param edgeFigure
     *            the edge figure instance
     * @param edgeRouting
     *            the edge routing returned by the layout algorithm
     * @return {@code true} if an approximation should be used to represent the spline
     */
    private static boolean handleSplineConnection(final IFigure edgeFigure,
            final EdgeRouting edgeRouting) {
        boolean isSC;
        Class<?> clazz = edgeFigure.getClass();
        do {
            String canonicalName = clazz.getCanonicalName();
            // in some cases, eg anonymous classes, the canonical name may be null
            isSC = canonicalName != null && canonicalName.equals(SPLINE_CONNECTION);
            clazz = clazz.getSuperclass();
        } while (!isSC && clazz != null);
        if (isSC) {
            clazz = edgeFigure.getClass();
            try {
                if (edgeRouting == EdgeRouting.SPLINES) {
                    // SplineConnection.SPLINE_CUBIC
                    clazz.getMethod("setSplineMode", int.class).invoke(edgeFigure, 1);
                } else {
                    // SplineConnection.SPLINE_OFF
                    clazz.getMethod("setSplineMode", int.class).invoke(edgeFigure, 0);
                }
                return false;
            } catch (Exception exception) {
                throw new WrappedException(exception);
            }
        }
        // no spline connection class, but spline representation is requested
        return edgeRouting == EdgeRouting.SPLINES;
    }

    /**
     * <!-- CHECKSTYLEOFF LineLength --> Calculates the label offset from the reference point given
     * the label bounds and a points list. This code has been copied and adapted from
     * {@link org.eclipse.gmf.runtime.diagram.ui.internal.figures.LabelHelper#offsetFromRelativeCoordinate(IFigure, Rectangle, PointList, Point)}
     * ,
     * {@link org.eclipse.gmf.runtime.diagram.ui.internal.figures.LabelHelper#normalizeRelativePointToPointOnLine(PointList, Point, Point)}
     * , and
     * {@link org.eclipse.gmf.runtime.diagram.ui.internal.figures.LabelHelper#getOrthogonalDistances(LineSeg, Point, Point)}
     * .
     * 
     * <!-- CHECKSTYLEON LineLength -->
     * 
     * @param bounds
     *            the {@code Rectangle} that is the bounding box of the label
     * @param points
     *            the {@code PointList} that the label offset is relative to
     * @param therefPoint
     *            the {@code Point} that is the reference point that the offset is based on, or
     *            {@code null}
     * @return a {@code Point} which represents a value offset from the {@code refPoint} point
     *         oriented based on the nearest line segment, or {@code null} if no such point can be
     *         determined
     */
    @SuppressWarnings("restriction")
    public static Point offsetFromRelativeCoordinate(final Rectangle bounds,
            final PointList points, final Point therefPoint) {
        Point refPoint = therefPoint;
        if (refPoint == null) {
            refPoint = points.getFirstPoint();
        }
        // compensate for the fact that we are using the figure center
        bounds.translate(bounds.width / 2, bounds.height / 2);
        Point offset = new Point(bounds.x - refPoint.x, bounds.y - refPoint.y);
        // calculate slope of line
        if (points.size() == 1) {
            // this is a node...
            return offset;
        } else if (points.size() >= 2) {
            // this is an edge...
            int segIndex = PointListUtilities.findNearestLineSegIndexOfPoint(points, refPoint);
            @SuppressWarnings("rawtypes")
            List segmentsList = PointListUtilities.getLineSegments(points);
            if (segIndex <= 0) {
                segIndex = 0;
            } else if (segIndex > segmentsList.size()) {
                segIndex = segmentsList.size() - 1;
            } else {
                segIndex--;
            }
            LineSeg segment = (LineSeg) segmentsList.get(segIndex);
            Point normalOffset = null;
            if (segment.isHorizontal()) {
                if (segment.getOrigin().x > segment.getTerminus().x) {
                    normalOffset = offset.getNegated();
                    return normalOffset;
                } else {
                    normalOffset = offset;
                    return normalOffset;
                }
            } else if (segment.isVertical()) {
                if (segment.getOrigin().y < segment.getTerminus().y) {
                    normalOffset = offset.scale(-1, 1).transpose();
                    return normalOffset;
                } else {
                    normalOffset = offset.scale(1, -1).transpose();
                    return normalOffset;
                }
            } else {
                Point offsetRefPoint = refPoint.getTranslated(offset);
                LineSeg parallelSeg = segment.getParallelLineSegThroughPoint(offsetRefPoint);
                Point p1 = parallelSeg.perpIntersect(refPoint.x, refPoint.y);
                double dx = p1.getDistance(offsetRefPoint) * ((p1.x > offsetRefPoint.x) ? -1 : 1);
                double dy = p1.getDistance(refPoint) * ((p1.y < refPoint.y) ? -1 : 1);
                Point orth = new PrecisionPoint(dx, dy);
                // reflection in the y axis
                if (segment.getOrigin().x > segment.getTerminus().x) {
                    orth = orth.scale(-1, -1);
                }
                return orth;
            }
        }
        return null;
    }

}
