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
package org.eclipse.elk.alg.graphiti;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.elk.core.klayoutdata.KEdgeLayout;
import org.eclipse.elk.core.klayoutdata.KInsets;
import org.eclipse.elk.core.klayoutdata.KLayoutDataFactory;
import org.eclipse.elk.core.klayoutdata.KPoint;
import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.EdgeLabelPlacement;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.service.IDiagramLayoutConnector;
import org.eclipse.elk.core.service.LayoutMapping;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.core.util.nodespacing.Spacing.Margins;
import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KGraphElement;
import org.eclipse.elk.graph.KLabel;
import org.eclipse.elk.graph.KLabeledGraphElement;
import org.eclipse.elk.graph.KNode;
import org.eclipse.elk.graph.KPort;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.IPropertyHolder;
import org.eclipse.elk.graph.properties.Property;
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
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
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

        KNode topNode;
        if (rootElement instanceof Diagram) {
            topNode = ElkUtil.createInitializedNode();
            KShapeLayout shapeLayout = topNode.getData(KShapeLayout.class);
            GraphicsAlgorithm ga = rootElement.getGraphicsAlgorithm();
            shapeLayout.setPos(ga.getX(), ga.getY());
            shapeLayout.setSize(ga.getWidth(), ga.getHeight());
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
                KNode node = createNode(mapping, topNode, shape);
                KShapeLayout nodeLayout = node.getData(KShapeLayout.class);
                minx = Math.min(minx, nodeLayout.getXpos());
                miny = Math.min(miny, nodeLayout.getYpos());
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
        boolean zoomToFit = settings.getProperty(LayoutOptions.ZOOM_TO_FIT);
        Object layoutGraphObj = mapping.getParentElement();
        if (zoomToFit && layoutGraphObj instanceof EditPart) {
            // determine pre- or post-layout zoom
            GraphicalViewer viewer = ((IPictogramElementEditPart) layoutGraphObj)
                    .getConfigurationProvider().getDiagramContainer().getGraphicalViewer();
            ZoomManager zoomManager = ((ScalableRootEditPartAnimated) viewer.getRootEditPart())
                    .getZoomManager();
            KNode parentNode = mapping.getLayoutGraph();
            KShapeLayout parentLayout = parentNode.getData(KShapeLayout.class);
            Dimension available = zoomManager.getViewport().getClientArea().getSize();
            float desiredWidth = parentLayout.getWidth();
            double scaleX = Math.min(available.width / desiredWidth, zoomManager.getMaxZoom());
            float desiredHeight = parentLayout.getHeight();
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
        for (Entry<KGraphElement, Object> entry : mapping.getGraphMap().entrySet()) {
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
    protected static void addOffset(final KNode parentNode, final KVector offset) {
        // correct the offset with the minimal computed coordinates
        double minx = Integer.MAX_VALUE;
        double miny = Integer.MAX_VALUE;
        for (KNode child : parentNode.getChildren()) {
            KShapeLayout nodeLayout = child.getData(KShapeLayout.class);
            minx = Math.min(minx, nodeLayout.getXpos());
            miny = Math.min(miny, nodeLayout.getYpos());
        }
        
        // add the corrected offset
        offset.add(-minx, -miny);
        ElkUtil.translate(parentNode, (float) offset.x, (float) offset.y);
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
            final ContainerShape parentElement, final KNode parentNode) {
        for (Shape shape : parentElement.getChildren()) {
            if (graphElemIndicator.isNodeShape(shape)) {
                KNode node = createNode(mapping, parentNode, shape);
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
    protected KNode createNode(final LayoutMapping mapping, final KNode parentNode, final Shape shape) {
        KNode childNode = ElkUtil.createInitializedNode();
        childNode.setParent(parentNode);

        // set the node's layout, considering margins and insets
        KShapeLayout nodeLayout = childNode.getData(KShapeLayout.class);
        computeInsets(nodeLayout.getInsets(), shape);
        Margins nodeMargins = computeMargins(shape);
        nodeLayout.setProperty(LayoutOptions.MARGINS, nodeMargins);
        GraphicsAlgorithm nodeGa = shape.getGraphicsAlgorithm();
        if (parentNode == null) {
            nodeLayout.setPos(nodeGa.getX() + (float) nodeMargins.left,
                    nodeGa.getY() + (float) nodeMargins.top);
        } else {
            KShapeLayout parentLayout = parentNode.getData(KShapeLayout.class);
            Margins parentMargins = parentLayout.getProperty(LayoutOptions.MARGINS);
            KInsets parentInsets = parentLayout.getInsets();
            nodeLayout.setPos(nodeGa.getX() + (float) (nodeMargins.left - parentMargins.left)
                    - parentInsets.getLeft(),
                    nodeGa.getY() + (float) (nodeMargins.top - parentMargins.top)
                    - parentInsets.getTop());
        }
        nodeLayout.setSize(nodeGa.getWidth() - (float) (nodeMargins.left - nodeMargins.right),
                nodeGa.getHeight() - (float) (nodeMargins.top - nodeMargins.bottom));
        // the modification flag must initially be false
        nodeLayout.resetModificationFlag();

        // this very minimal size configuration should be corrected in subclasses
        nodeLayout.setProperty(LayoutOptions.MIN_WIDTH, MIN_SIZE);
        nodeLayout.setProperty(LayoutOptions.MIN_HEIGHT, MIN_SIZE);

        mapping.getGraphMap().put(childNode, shape);

        if (shape instanceof ContainerShape) {
            // find a label for the container shape
            for (Shape child : ((ContainerShape) shape).getChildren()) {
                if (graphElemIndicator.isNodeLabel(child)) {
                    createLabel(childNode, child, (float) -nodeMargins.left, (float) -nodeMargins.top);
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
    protected Margins computeMargins(final PictogramElement pictogramElement) {
        GraphicsAlgorithm graphicsAlgorithm = pictogramElement.getGraphicsAlgorithm();
        GraphicsAlgorithm visibleGa = findVisibleGa(graphicsAlgorithm);
        Margins margins = new Margins();
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
     * retains the default insets (left=0, right=0, top=0, bottom=0). Subclasses may override this.
     * 
     * @param insets the insets where computed values shall be stored
     * @param pictogramElement a pictogram element
     */
    protected void computeInsets(final KInsets insets, final PictogramElement pictogramElement) {
    }

    /**
     * Create a port for the layout graph using a box-relative anchor.
     * 
     * @param mapping the mapping of pictogram elements to graph elements
     * @param parentNode the parent node
     * @param bra the anchor
     * @return the new layout port
     */
    protected KPort createPort(final LayoutMapping mapping,
            final KNode parentNode, final BoxRelativeAnchor bra) {
        KPort port = ElkUtil.createInitializedPort();
        port.setNode(parentNode);
        KShapeLayout portLayout = port.getData(KShapeLayout.class);

        GraphicsAlgorithm referencedGa = bra.getReferencedGraphicsAlgorithm();
        if (referencedGa == null) {
            return null;
        }
        mapping.getGraphMap().put(port, bra);

        double relWidth = bra.getRelativeWidth();
        double relHeight = bra.getRelativeHeight();

        double parentWidth = referencedGa.getWidth();
        double parentHeight = referencedGa.getHeight();
        float xPos = (float) (relWidth * parentWidth);
        float yPos = (float) (relHeight * parentHeight);
        
        GraphicsAlgorithm portGa = bra.getGraphicsAlgorithm(); 
        if (portGa != null) {
            xPos += portGa.getX();
            yPos += portGa.getY();
            portLayout.setSize(portGa.getWidth(), portGa.getHeight());
        }
        portLayout.setPos(xPos, yPos);
        // the modification flag must initially be false
        portLayout.resetModificationFlag();
        
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
    protected KPort createPort(final LayoutMapping mapping, final KNode parentNode,
            final FixPointAnchor fpa) {
        KPort port = ElkUtil.createInitializedPort();
        port.setNode(parentNode);
        KShapeLayout portLayout = port.getData(KShapeLayout.class);
        mapping.getGraphMap().put(port, fpa);
        
        float xPos = fpa.getLocation().getX();
        float yPos = fpa.getLocation().getY();

        GraphicsAlgorithm portGa = fpa.getGraphicsAlgorithm(); 
        if (portGa != null) {
            xPos += portGa.getX();
            yPos += portGa.getY();
            portLayout.setSize(portGa.getWidth(), portGa.getHeight());
        }
        portLayout.setPos(xPos, yPos);
        // the modification flag must initially be false
        portLayout.resetModificationFlag();
        
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
    protected KLabel createLabel(final KLabeledGraphElement element, final Shape shape,
            final float offsetx, final float offsety) {
        KLabel label = ElkUtil.createInitializedLabel(element);
        KShapeLayout labelLayout = label.getData(KShapeLayout.class);
        
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
        
        labelLayout.setPos(xpos + offsetx, ypos + offsety);
        labelLayout.setSize(width, height);
        
        // the modification flag must initially be false
        labelLayout.resetModificationFlag();
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
    protected KEdge createEdge(final LayoutMapping mapping, final Connection connection) {
        BiMap<Object, KGraphElement> inverseGraphMap = mapping.getGraphMap().inverse();

        // set target node and port
        KNode targetNode;
        Anchor targetAnchor = connection.getEnd();
        KPort targetPort = (KPort) inverseGraphMap.get(targetAnchor);
        if (targetPort != null) {
            targetNode = targetPort.getNode();
        } else {
            targetNode = (KNode) inverseGraphMap.get(targetAnchor.getParent());
        }
        if (targetNode == null) {
            return null;
        }

        // set source node and port
        KNode sourceNode;
        Anchor sourceAnchor = connection.getStart();
        KPort sourcePort = (KPort) inverseGraphMap.get(sourceAnchor);
        if (sourcePort != null) {
            sourceNode = sourcePort.getNode();
        } else {
            sourceNode = (KNode) inverseGraphMap.get(sourceAnchor.getParent());
        }
        if (sourceNode == null) {
            return null;
        }
        
        KEdge edge = ElkUtil.createInitializedEdge();
        edge.setTarget(targetNode);
        edge.setTargetPort(targetPort);
        edge.setSource(sourceNode);
        edge.setSourcePort(sourcePort);

        // calculate offset for bend points and labels
        KNode referenceNode = sourceNode;
        if (!ElkUtil.isDescendant(targetNode, sourceNode)) {
            referenceNode = sourceNode.getParent();
        }
        KVector offset = new KVector();
        ElkUtil.toAbsolute(offset, referenceNode);

        // set source and target point
        KEdgeLayout edgeLayout = edge.getData(KEdgeLayout.class);
        KVector sourcePoint = calculateAnchorEnds(sourceNode, sourcePort,
                referenceNode);
        edgeLayout.getSourcePoint().applyVector(sourcePoint);
        KVector targetPoint = calculateAnchorEnds(targetNode, targetPort,
                referenceNode);
        edgeLayout.getTargetPoint().applyVector(targetPoint);
        // set bend points for the new edge
        KVectorChain allPoints = new KVectorChain();
        allPoints.add(sourcePoint);
        if (connection instanceof FreeFormConnection) {
            for (Point point : ((FreeFormConnection) connection).getBendpoints()) {
                KVector v = new KVector(point.getX(), point.getY());
                v.sub(offset);
                allPoints.add(v);
                KPoint kpoint = KLayoutDataFactory.eINSTANCE.createKPoint();
                kpoint.applyVector(v);
                edgeLayout.getBendPoints().add(kpoint);
            }
        }
        allPoints.add(targetPoint);
        // the modification flag must initially be false
        edgeLayout.resetModificationFlag();

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
    protected KLabel createEdgeLabel(final LayoutMapping mapping, final KEdge parentEdge,
            final ConnectionDecorator decorator, final KVectorChain allPoints) {
        KLabel label = ElkUtil.createInitializedLabel(parentEdge);
        mapping.getGraphMap().put(label, decorator);

        // set label placement
        KShapeLayout labelLayout = label.getData(KShapeLayout.class);
        EdgeLabelPlacement placement = EdgeLabelPlacement.CENTER;
        if (decorator.isLocationRelative()) {
            if (decorator.getLocation() >= HEAD_LOCATION) {
                placement = EdgeLabelPlacement.HEAD;
            } else if (decorator.getLocation() <= TAIL_LOCATION) {
                placement = EdgeLabelPlacement.TAIL;
            }
        }
        labelLayout.setProperty(LayoutOptions.EDGE_LABEL_PLACEMENT, placement);

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
        labelLayout.applyVector(labelPos);

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
                    labelLayout.setSize(textSize.getWidth(), textSize.getHeight());
                }
            }
        }

        // the modification flag must initially be false
        labelLayout.resetModificationFlag();
        return label;
    }

    /**
     * Returns an end point for an anchor.
     * 
     * @param node
     *            the node that owns the anchor
     * @param port
     *            the port that represents the anchor, or {@code null}
     * @param referenceNode
     *            the parent node to which edge points are relative, or {@code null}
     * @return the position of the anchor, relative to the reference node
     */
    public KVector calculateAnchorEnds(final KNode node, final KPort port,
            final KNode referenceNode) {
        KVector pos = new KVector();
        if (port != null) {
            // the anchor end is represented by a port (box-relative anchor or fix-point anchor)
            KShapeLayout portLayout = port.getData(KShapeLayout.class);
            pos.x = portLayout.getXpos() + portLayout.getWidth() / 2;
            pos.y = portLayout.getYpos() + portLayout.getHeight() / 2;
            KShapeLayout nodeLayout = node.getData(KShapeLayout.class);
            pos.x += nodeLayout.getXpos();
            pos.y += nodeLayout.getYpos();
        } else {
            // the anchor end is determined by a chopbox anchor
            KShapeLayout nodeLayout = node.getData(KShapeLayout.class);
            pos.x = nodeLayout.getWidth() / 2 + nodeLayout.getXpos();
            pos.y = nodeLayout.getHeight() / 2 + nodeLayout.getYpos();
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
