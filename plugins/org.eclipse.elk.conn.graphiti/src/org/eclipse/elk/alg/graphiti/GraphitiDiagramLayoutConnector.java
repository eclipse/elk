/*******************************************************************************
 * Copyright (c) 2010, 2015 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.graphiti;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.elk.core.math.ElkMargin;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.EdgeLabelPlacement;
import org.eclipse.elk.core.service.IDiagramLayoutConnector;
import org.eclipse.elk.core.service.LayoutMapping;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.graph.ElkConnectableShape;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkGraphElement;
import org.eclipse.elk.graph.ElkLabel;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.ElkPort;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.IPropertyHolder;
import org.eclipse.elk.graph.properties.Property;
import org.eclipse.elk.graph.util.ElkGraphUtil;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.mm.algorithms.AbstractText;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.internal.parts.IPictogramElementEditPart;
import org.eclipse.graphiti.ui.internal.util.gef.ScalableRootEditPartAnimated;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.swt.SWTException;
import org.eclipse.ui.IWorkbenchPart;

import com.google.common.collect.BiMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Generic layout connector implementation for Graphiti diagrams.
 * 
 * @author atr
 * @author soh
 * @author msp
 */
@SuppressWarnings("restriction")
@Singleton
public class GraphitiDiagramLayoutConnector implements IDiagramLayoutConnector {

    /** property for the the command that is executed for applying automatic layout. */
    public static final IProperty<Command> LAYOUT_COMMAND = new Property<Command>(
            "graphiti.applyLayoutCommand");

    /** property for the list of all connections in the diagram. */
    public static final IProperty<List<Connection>> CONNECTIONS = new Property<List<Connection>>(
            "graphiti.connections");
    
    /** property for the the offset to add for all coordinates. */
    public static final IProperty<KVector> COORDINATE_OFFSET = new Property<KVector>(
            "graphiti.coordinateOffset");
    
    @Inject
    private GraphElementIndicator graphElemIndicator;

    /**
     * {@inheritDoc}
     */
    public LayoutMapping buildLayoutGraph(final IWorkbenchPart workbenchPart, final Object diagramPart) {
        LayoutMapping mapping = new LayoutMapping(workbenchPart);
        mapping.setProperty(CONNECTIONS, new LinkedList<Connection>());

        Shape rootElement = null;
        List<Shape> selectedElements = null;
        if (diagramPart instanceof Shape) {
            rootElement = (Shape) diagramPart;
        } else if (diagramPart instanceof IPictogramElementEditPart) {
            PictogramElement pe = ((IPictogramElementEditPart) diagramPart).getPictogramElement();
            if (pe instanceof Shape) {
                rootElement = (Shape) pe;
            }
        } else if (diagramPart instanceof Collection) {
            Collection<?> selection = (Collection<?>) diagramPart;
            List<Shape> shapes = new LinkedList<Shape>();
            // determine the layout root part from the selection
            for (Object object : selection) {
                Shape shape = null;
                if (object instanceof Shape) {
                    shape = (Shape) object;
                } else if (object instanceof IPictogramElementEditPart) {
                    PictogramElement pe = ((IPictogramElementEditPart) object).getPictogramElement();
                    if (pe instanceof Shape) {
                        shape = (Shape) pe;
                    }
                }
                if (shape != null) {
                    if (rootElement != null) {
                        Shape parent = commonParent(rootElement, shape);
                        if (parent != null) {
                            rootElement = parent;
                        }
                    } else {
                        rootElement = shape;
                    }
                    shapes.add(shape);
                }
            }
            // build a list of shapes that shall be layouted completely
            if (rootElement != null) {
                selectedElements = new ArrayList<Shape>(shapes.size());
                for (Shape shape : shapes) {
                    while (shape != null && shape.getContainer() != rootElement) {
                        shape = shape.getContainer();
                    }
                    if (!selectedElements.contains(shape)) {
                        selectedElements.add(shape);
                    }
                }
            }
        }
        if (rootElement == null && workbenchPart instanceof DiagramEditor) {
            EditPart editorContent = ((DiagramEditor) workbenchPart).getGraphicalViewer().getContents();
            PictogramElement pe = ((IPictogramElementEditPart) editorContent).getPictogramElement();
            if (pe instanceof Shape) {
                rootElement = (Shape) pe;
            }
        }
        if (rootElement == null) {
            throw new UnsupportedOperationException(
                    "Not supported by this layout connector: Workbench part "
                    + workbenchPart + ", Element " + diagramPart);
        }
        mapping.setParentElement(rootElement);

        ElkNode topNode;
        if (rootElement instanceof Diagram) {
            topNode = ElkGraphUtil.createGraph();
            GraphicsAlgorithm ga = rootElement.getGraphicsAlgorithm();
            topNode.setLocation(ga.getX(), ga.getY());
            topNode.setDimensions(ga.getWidth(), ga.getHeight());
            mapping.getGraphMap().put(topNode, rootElement);
        } else {
            topNode = createNode(mapping, null, rootElement);
        }
        mapping.setLayoutGraph(topNode);

        if (selectedElements != null && !selectedElements.isEmpty()) {
            // layout only the selected elements
            double minx = Integer.MAX_VALUE;
            double miny = Integer.MAX_VALUE;
            for (Shape shape : selectedElements) {
                ElkNode node = createNode(mapping, topNode, shape);
                minx = Math.min(minx, node.getX());
                miny = Math.min(miny, node.getY());
                if (shape instanceof ContainerShape) {
                    buildLayoutGraphRecursively(mapping, (ContainerShape) shape, node);
                }
            }
            mapping.setProperty(COORDINATE_OFFSET, new KVector(minx, miny));
        } else if (rootElement instanceof ContainerShape) {
            // traverse all children of the layout root part
            buildLayoutGraphRecursively(mapping, (ContainerShape) rootElement, topNode);
        }
        
        // transform all connections in the selected area
        for (Connection entry : mapping.getProperty(CONNECTIONS)) {
            createEdge(mapping, entry);
        }
        
        return mapping;
    }
    
    /**
     * Determine the lowest common parent of the two shapes.
     * 
     * @param shape1 the first shape
     * @param shape2 the second shape
     * @return the common parent, or {@code null} if there is none
     */
    protected static Shape commonParent(final Shape shape1, final Shape shape2) {
        Shape s1 = shape1;
        Shape s2 = shape2;
        do {
            if (isParent(s1, s2)) {
                return s1;
            }
            if (isParent(s2, s1)) {
                return s2;
            }
            s1 = s1.getContainer();
            s2 = s2.getContainer();
        } while (s1 != null && s2 != null);
        return null;
    }
    
    /**
     * Determine whether the first shape is a parent of or equals the second one.
     * 
     * @param parent the tentative parent
     * @param child the tentative child
     * @return true if the parent is actually a parent of the child
     */
    protected static boolean isParent(final Shape parent, final Shape child) {
        Shape shape = child;
        do {
            if (shape == parent) {
                return true;
            }
            shape = shape.getContainer();
        } while (shape != null);
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public void applyLayout(final LayoutMapping mapping, final IPropertyHolder settings) {
        boolean zoomToFit = settings.getProperty(CoreOptions.ZOOM_TO_FIT);
        Object layoutGraphObj = mapping.getParentElement();
        if (zoomToFit && layoutGraphObj instanceof EditPart) {
            // determine pre- or post-layout zoom
            GraphicalViewer viewer = ((IPictogramElementEditPart) layoutGraphObj)
                    .getConfigurationProvider().getDiagramContainer().getGraphicalViewer();
            ZoomManager zoomManager = ((ScalableRootEditPartAnimated) viewer.getRootEditPart())
                    .getZoomManager();
            ElkNode parentNode = mapping.getLayoutGraph();
            Dimension available = zoomManager.getViewport().getClientArea().getSize();
            double desiredWidth = parentNode.getWidth();
            double scaleX = Math.min(available.width / desiredWidth, zoomManager.getMaxZoom());
            double desiredHeight = parentNode.getHeight();
            double scaleY = Math.min(available.height / desiredHeight, zoomManager.getMaxZoom());
            final double scale = Math.min(scaleX, scaleY);
            final double oldScale = zoomManager.getZoom();

            if (scale < oldScale) {
                zoomManager.setViewLocation(new org.eclipse.draw2d.geometry.Point(0, 0));
                zoomManager.setZoom(scale);
                zoomManager.setViewLocation(new org.eclipse.draw2d.geometry.Point(0, 0));
            }
            transferLayout(mapping);
            applyLayout(mapping);
            if (scale > oldScale) {
                zoomManager.setViewLocation(new org.eclipse.draw2d.geometry.Point(0, 0));
                zoomManager.setZoom(scale);
                zoomManager.setViewLocation(new org.eclipse.draw2d.geometry.Point(0, 0));
            }
        } else {
            transferLayout(mapping);
            applyLayout(mapping);
        }
    }
    
    /**
     * Transfer all layout data from the last created KGraph instance to the
     * original diagram. The diagram is not modified yet, but all required
     * preparations are performed.
     * 
     * @param mapping a layout mapping that was created by this layout connector
     */
    protected void transferLayout(final LayoutMapping mapping) {
        DiagramEditor diagramEditor = (DiagramEditor) mapping.getWorkbenchPart();
        GraphitiLayoutCommand command = new GraphitiLayoutCommand(diagramEditor.getEditingDomain(),
                diagramEditor.getDiagramTypeProvider().getFeatureProvider(), this);
        for (Entry<ElkGraphElement, Object> entry : mapping.getGraphMap().entrySet()) {
            command.add(entry.getKey(), (PictogramElement) entry.getValue());
        }
        mapping.setProperty(LAYOUT_COMMAND, command);
        
        // correct the layout by adding the offset determined from the selection
        KVector offset = mapping.getProperty(COORDINATE_OFFSET);
        if (offset != null) {
            addOffset(mapping.getLayoutGraph(), offset);
        }
    }
    
    /**
     * Add the given offset to all direct children of the given graph.
     * 
     * @param parentNode the parent node
     * @param offset the offset to add
     */
    protected static void addOffset(final ElkNode parentNode, final KVector offset) {
        // correct the offset with the minimal computed coordinates
        double minx = Integer.MAX_VALUE;
        double miny = Integer.MAX_VALUE;
        for (ElkNode child : parentNode.getChildren()) {
            minx = Math.min(minx, child.getX());
            miny = Math.min(miny, child.getY());
        }
        
        // add the corrected offset
        offset.add(-minx, -miny);
        ElkUtil.translate(parentNode, offset.x, offset.y);
    }

    /**
     * Apply the transferred layout to the original diagram. This final step
     * is where the actual change to the diagram is done.
     * 
     * @param mapping a layout mapping that was created by this layout connector
     */
    protected void applyLayout(final LayoutMapping mapping) {
        TransactionalEditingDomain editingDomain = ((DiagramEditor) mapping.getWorkbenchPart())
                .getEditingDomain();
        editingDomain.getCommandStack().execute(mapping.getProperty(LAYOUT_COMMAND));
    }

    /** the fixed minimal size of shapes. */
    private static final float MIN_SIZE = 3.0f;
    
    /**
     * Recursively builds a layout graph by analyzing the children of the given current pictogram
     * Element.
     *
     * @param mapping the mapping of pictogram elements to graph elements
     * @param parentElement the currently analyzed element
     * @param parentNode the corresponding KNode
     */
    protected void buildLayoutGraphRecursively(final LayoutMapping mapping,
            final ContainerShape parentElement, final ElkNode parentNode) {
        
        for (Shape shape : parentElement.getChildren()) {
            if (graphElemIndicator.isNodeShape(shape)) {
                ElkNode node = createNode(mapping, parentNode, shape);
                if (shape instanceof ContainerShape) {
                    // process the children of the container shape
                    buildLayoutGraphRecursively(mapping, (ContainerShape) shape, node);
                }
            }
        }
    }
    
    /**
     * Create a node for the layout graph.
     * 
     * @param mapping the mapping of pictogram elements to graph elements
     * @param parentNode the parent node
     * @param shape the shape for a new node
     * @return the new layout node
     */
    protected ElkNode createNode(final LayoutMapping mapping, final ElkNode parentNode, final Shape shape) {
        ElkNode childNode = ElkGraphUtil.createNode(parentNode);

        // set the node's layout, considering margins and insets
        ElkPadding padding = computePadding(shape);
        if (padding != null) {
            childNode.setProperty(CoreOptions.PADDING, padding);
        }
        
        ElkMargin nodeMargins = computeMargins(shape);
        childNode.setProperty(CoreOptions.MARGINS, nodeMargins);
        
        GraphicsAlgorithm nodeGa = shape.getGraphicsAlgorithm();
        if (parentNode == null) {
            childNode.setLocation(nodeGa.getX() + nodeMargins.left, nodeGa.getY() + nodeMargins.top);
        } else {
            ElkMargin parentMargins = parentNode.getProperty(CoreOptions.MARGINS);
            childNode.setLocation(nodeGa.getX() + nodeMargins.left - parentMargins.left,
                    nodeGa.getY() + nodeMargins.top - parentMargins.top);
        }
        childNode.setDimensions(nodeGa.getWidth() - nodeMargins.left - nodeMargins.right,
                nodeGa.getHeight() - nodeMargins.top - nodeMargins.bottom);
        
        // the modification flag would have been reset here, but that doesn't exist anymore

        // this very minimal size configuration should be corrected in subclasses
        childNode.setProperty(CoreOptions.NODE_SIZE_MINIMUM, new KVector(MIN_SIZE, MIN_SIZE));

        mapping.getGraphMap().put(childNode, shape);

        if (shape instanceof ContainerShape) {
            // find a label for the container shape
            for (Shape child : ((ContainerShape) shape).getChildren()) {
                if (graphElemIndicator.isNodeLabel(child)) {
                    createLabel(childNode, child, -nodeMargins.left, -nodeMargins.top);
                }
            }
        }

        for (Anchor anchor : shape.getAnchors()) {
            // box-relative anchors and fixed-position anchors are interpreted as ports
            if (graphElemIndicator.isPortAnchor(anchor)) {
                if (anchor instanceof BoxRelativeAnchor) {
                    createPort(mapping, childNode, (BoxRelativeAnchor) anchor);
                } else if (anchor instanceof FixPointAnchor) {
                    createPort(mapping, childNode, (FixPointAnchor) anchor);
                }
            }
            // gather all connections in the diagram
            mapping.getProperty(CONNECTIONS).addAll(anchor.getOutgoingConnections());
        }
        
        return childNode;
    }
    
    /**
     * Determine the margins around a node. Margins are areas on the border of a pictogram element
     * that are not visible in the diagram, but are required for placing elements such as labels
     * or ports. The default implementation computes margins from an invisible rectangle to the
     * first visible shape. Subclasses may override this behavior.
     * 
     * @param pictogramElement a pictogram element
     * @return the margins for the given element
     */
    protected ElkMargin computeMargins(final PictogramElement pictogramElement) {
        GraphicsAlgorithm graphicsAlgorithm = pictogramElement.getGraphicsAlgorithm();
        GraphicsAlgorithm visibleGa = findVisibleGa(graphicsAlgorithm);
        ElkMargin margins = new ElkMargin();
        while (visibleGa != null && visibleGa != graphicsAlgorithm) {
            margins.left += visibleGa.getX();
            margins.top += visibleGa.getY();
            GraphicsAlgorithm parentGa = visibleGa.getParentGraphicsAlgorithm();
            margins.right += parentGa.getWidth() - visibleGa.getX() - visibleGa.getWidth();
            margins.bottom += parentGa.getHeight() - visibleGa.getY() - visibleGa.getHeight();
            visibleGa = parentGa;
        }
        return margins;
    }

    /**
     * Determine the insets of a node. Insets are areas inside the visible part of a pictogram
     * element that must not be covered by contained child nodes. The default implementation
     * returns {@code null}. Subclasses may override this.
     * 
     * @param pictogramElement a pictogram element
     * @return insets or {@code null}.
     */
    protected ElkPadding computePadding(final PictogramElement pictogramElement) {
        return null;
    }

    /**
     * Create a port for the layout graph using a box-relative anchor.
     * 
     * @param mapping the mapping of pictogram elements to graph elements
     * @param parentNode the parent node
     * @param bra the anchor
     * @return the new layout port
     */
    protected ElkPort createPort(final LayoutMapping mapping, final ElkNode parentNode, final BoxRelativeAnchor bra) {
        ElkPort port = ElkGraphUtil.createPort(parentNode);

        GraphicsAlgorithm referencedGa = bra.getReferencedGraphicsAlgorithm();
        if (referencedGa == null) {
            return null;
        }
        mapping.getGraphMap().put(port, bra);

        double relWidth = bra.getRelativeWidth();
        double relHeight = bra.getRelativeHeight();

        double parentWidth = referencedGa.getWidth();
        double parentHeight = referencedGa.getHeight();
        double xPos = relWidth * parentWidth;
        double yPos = relHeight * parentHeight;
        
        GraphicsAlgorithm portGa = bra.getGraphicsAlgorithm(); 
        if (portGa != null) {
            xPos += portGa.getX();
            yPos += portGa.getY();
            port.setDimensions(portGa.getWidth(), portGa.getHeight());
        }
        port.setLocation(xPos, yPos);
        
        // the modification flag would have been reset here, but that doesn't exist anymore
        
        return port;
    }

    /**
     * Create a port for the layout graph using a fixed-position anchor.
     * 
     * @param mapping the mapping of pictogram elements to graph elements
     * @param parentNode the parent node
     * @param fpa the anchor
     * @return the new layout port
     */
    protected ElkPort createPort(final LayoutMapping mapping, final ElkNode parentNode, final FixPointAnchor fpa) {
        ElkPort port = ElkGraphUtil.createPort(parentNode);
        mapping.getGraphMap().put(port, fpa);
        
        double xPos = fpa.getLocation().getX();
        double yPos = fpa.getLocation().getY();

        GraphicsAlgorithm portGa = fpa.getGraphicsAlgorithm(); 
        if (portGa != null) {
            xPos += portGa.getX();
            yPos += portGa.getY();
            port.setDimensions(portGa.getWidth(), portGa.getHeight());
        }
        port.setLocation(xPos, yPos);
        
        // the modification flag would have been reset here, but that doesn't exist anymore
        
        return port;
    }

    /**
     * Create a label for a node or a port.
     * 
     * @param element the graph element to which the label is added
     * @param shape the pictogram shape of the label
     * @param offsetx the x coordinate offset
     * @param offsety the y coordinate offset
     * @return a new label
     */
    protected ElkLabel createLabel(final ElkGraphElement element, final Shape shape,
            final double offsetx, final double offsety) {
        
        ElkLabel label = ElkGraphUtil.createLabel(element);
        
        GraphicsAlgorithm ga = shape.getGraphicsAlgorithm();
        int xpos = ga.getX(), ypos = ga.getY();
        int width = ga.getWidth(), height = ga.getHeight();

        if (ga instanceof AbstractText) {
            AbstractText abstractText = (AbstractText) ga;
            String labelText = abstractText.getValue();
            label.setText(labelText);
            
            IGaService gaService = Graphiti.getGaService();
            Font font = gaService.getFont(abstractText, true);
    
            IDimension textSize = null;
            try {
                textSize = GraphitiUi.getUiLayoutService().calculateTextSize(labelText, font);
            } catch (SWTException exception) {
                // ignore exception
            }
            
            if (textSize != null) {
                if (textSize.getWidth() < width) {
                    int diff = width - textSize.getWidth();
                    switch (gaService.getHorizontalAlignment(abstractText, true)) {
                    case ALIGNMENT_CENTER:
                        xpos += diff / 2;
                        break;
                    case ALIGNMENT_RIGHT:
                        xpos += diff;
                        break;
                    default:
                        break;
                    }
                    width -= diff;
                }
                
                if (textSize.getHeight() < height) {
                    int diff = height - textSize.getHeight();
                    switch (gaService.getVerticalAlignment(abstractText, true)) {
                    case ALIGNMENT_MIDDLE:
                        ypos += diff / 2;
                        break;
                    case ALIGNMENT_BOTTOM:
                        ypos += diff;
                        break;
                    default:
                        break;
                    }
                    height -= diff;
                }
            }
        }
        
        label.setLocation(xpos + offsetx, ypos + offsety);
        label.setDimensions(width, height);
        
        // the modification flag would have been reset here, but that doesn't exist anymore
        
        return label;
    }

    /** minimal value for the relative location of head labels. */
    private static final double HEAD_LOCATION = 0.7;
    /** maximal value for the relative location of tail labels. */
    private static final double TAIL_LOCATION = 0.3;

    /**
     * Create an edge for the layout graph. 
     * 
     * @param mapping
     *            the mapping of pictogram elements to graph elements
     * @param connection
     *            a pictogram connection
     * @return the new layout edge
     */
    protected ElkEdge createEdge(final LayoutMapping mapping, final Connection connection) {
        BiMap<Object, ElkGraphElement> inverseGraphMap = mapping.getGraphMap().inverse();

        // set target
        Anchor targetAnchor = connection.getEnd();
        ElkConnectableShape targetShape = (ElkConnectableShape) inverseGraphMap.get(targetAnchor);
        if (targetShape == null) {
            return null;
        }

        // set source
        Anchor sourceAnchor = connection.getStart();
        ElkConnectableShape sourceShape = (ElkConnectableShape) inverseGraphMap.get(sourceAnchor);
        if (sourceShape == null) {
            return null;
        }
        
        ElkEdge edge = ElkGraphUtil.createSimpleEdge(sourceShape, targetShape);

        // calculate offset for bend points and labels
        ElkNode referenceNode = edge.getContainingNode();
        KVector offset = new KVector();
        ElkUtil.toAbsolute(offset, referenceNode);

        // set source and target point
        ElkEdgeSection edgeSection = ElkGraphUtil.createEdgeSection(edge);
        
        KVector sourcePoint = calculateAnchorEnds(sourceShape, referenceNode);
        edgeSection.setStartLocation(sourcePoint.x, sourcePoint.y);
        
        KVector targetPoint = calculateAnchorEnds(targetShape, referenceNode);
        edgeSection.setEndLocation(targetPoint.x, targetPoint.y);
        
        // set bend points for the new edge
        KVectorChain allPoints = new KVectorChain();
        allPoints.add(sourcePoint);
        if (connection instanceof FreeFormConnection) {
            for (Point point : ((FreeFormConnection) connection).getBendpoints()) {
                KVector v = new KVector(point.getX(), point.getY());
                v.sub(offset);
                allPoints.add(v);
                ElkGraphUtil.createBendPoint(edgeSection, v.x, v.y);
            }
        }
        allPoints.add(targetPoint);
        
        // the modification flag would have been reset here, but that doesn't exist anymore

        mapping.getGraphMap().put(edge, connection);

        // find labels for the connection
        for (ConnectionDecorator decorator : connection.getConnectionDecorators()) {
            if (graphElemIndicator.isEdgeLabel(decorator)) {
                createEdgeLabel(mapping, edge, decorator, allPoints);
            }
        }
        
        return edge;
    }
    
    /**
     * Create an edge label for the layout graph.
     * 
     * @param mapping the mapping of pictogram elements to graph elements
     * @param parentEdge the parent edge containing the label
     * @param decorator the connection decorator
     * @param allPoints the connection points, including end points and bend points
     * @return the new label
     */
    protected ElkLabel createEdgeLabel(final LayoutMapping mapping, final ElkEdge parentEdge,
            final ConnectionDecorator decorator, final KVectorChain allPoints) {
        
        ElkLabel label = ElkGraphUtil.createLabel(parentEdge);
        mapping.getGraphMap().put(label, decorator);

        // set label placement
        EdgeLabelPlacement placement = EdgeLabelPlacement.CENTER;
        if (decorator.isLocationRelative()) {
            if (decorator.getLocation() >= HEAD_LOCATION) {
                placement = EdgeLabelPlacement.HEAD;
            } else if (decorator.getLocation() <= TAIL_LOCATION) {
                placement = EdgeLabelPlacement.TAIL;
            }
        }
        label.setProperty(CoreOptions.EDGE_LABELS_PLACEMENT, placement);

        // set label position
        KVector labelPos;
        if (decorator.isLocationRelative()) {
            labelPos = allPoints.pointOnLine(decorator.getLocation()
                    * allPoints.totalLength());
        } else {
            labelPos = allPoints.pointOnLine(decorator.getLocation());
        }
        GraphicsAlgorithm ga = decorator.getGraphicsAlgorithm();
        labelPos.x += ga.getX();
        labelPos.y += ga.getY();
        label.setLocation(labelPos.x, labelPos.y);

        if (ga instanceof AbstractText) {
            AbstractText text = (AbstractText) ga;
            String labelText = text.getValue();
            label.setText(labelText);

            IGaService gaService = Graphiti.getGaService();
            Font font = gaService.getFont(text, true);
    
            if (labelText != null) {
                IDimension textSize = null;
                try {
                    textSize = GraphitiUi.getUiLayoutService().calculateTextSize(labelText, font);
                } catch (SWTException exception) {
                    // ignore exception
                }
                if (textSize != null) {
                    label.setDimensions(textSize.getWidth(), textSize.getHeight());
                }
            }
        }
        
        // the modification flag would have been reset here, but that doesn't exist anymore
        
        return label;
    }

    /**
     * Returns an end point for an anchor.
     * 
     * @param shape
     *            the connectable shape that owns the anchor
     * @param referenceNode
     *            the parent node to which edge points are relative, or {@code null}
     * @return the position of the anchor, relative to the reference node
     */
    public KVector calculateAnchorEnds(final ElkConnectableShape shape, final ElkNode referenceNode) {
        ElkNode node = ElkGraphUtil.connectableShapeToNode(shape);
        
        KVector pos = new KVector();
        if (shape instanceof ElkPort) {
            // the anchor end is represented by a port (box-relative anchor or fix-point anchor)
            pos.x = shape.getX() + shape.getWidth() / 2;
            pos.y = shape.getY() + shape.getHeight() / 2;
            
            pos.x += node.getX();
            pos.y += node.getY();
        } else {
            // the anchor end is determined by a chopbox anchor
            pos.x = node.getWidth() / 2 + node.getX();
            pos.y = node.getHeight() / 2 + node.getY();
        }
        ElkUtil.toAbsolute(pos, node.getParent());
        if (referenceNode != null) {
            ElkUtil.toRelative(pos, referenceNode);
        }
        return pos;
    }

    /**
     * Given a graphics algorithm, find the first child that is not invisible. If the GA itself is
     * visible, it is returned.
     * 
     * @param graphicsAlgorithm
     *            the parent graphics algorithm
     * @return a visible graphics algorithm
     */
    protected GraphicsAlgorithm findVisibleGa(final GraphicsAlgorithm graphicsAlgorithm) {
        if (graphicsAlgorithm != null) {
            if (graphicsAlgorithm.getLineVisible() || graphicsAlgorithm.getFilled()) {
                return graphicsAlgorithm;
            }
            for (GraphicsAlgorithm ga : graphicsAlgorithm.getGraphicsAlgorithmChildren()) {
                GraphicsAlgorithm result = findVisibleGa(ga);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

}
