/*******************************************************************************
 * Copyright (c) 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0  
 *******************************************************************************/
package org.eclipse.elk.alg.layered;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.elk.alg.layered.options.CycleBreakingStrategy;
import org.eclipse.elk.alg.layered.options.LayerConstraint;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.alg.layered.options.LayeringStrategy;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.util.IGraphElementVisitor;
import org.eclipse.elk.graph.ElkConnectableShape;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkGraphElement;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.ElkPort;

/**
 * Graph visitor which visits only the root node and recursively steps through the graph
 * to set interactive options and pseudo coordinates for the layered algorithm.
 * Interactive layout works via two consecutive layout runs.
 * The first one is a "normal" run. The second one will is configured using this graph visitor.
 * The graph visitor recursively sets the interactive strategies for all phases and assigns pseudo positions to all
 * node based on their last position and the desired layer and position in the layer expressed by the
 * {@code LayerChoiceConstraint} and {@code PositionChoiceConstraint}.
 * The pseudo position have to represent layers and the ordering in the layer.
 * <br>
 * This class is used by adding it as an additional graph visitor to configure a layout run
 * in the {@link DiagramLayoutEngine}.
 * <pre>
 * DiagramLayoutEngine.Parameters params = new DiagramLayoutEngine.Parameters();
 * params.addLayoutRun(InteractiveLayeredGraphVisitor);
 * DiagramLayoutEngine.invokeLayout(workbenchPart, diagramPart, params);
 * </pre>
 * 
 * This graph visitor is added after the normal layered layout configurator since it needs previous layers, positions,
 * sizes, and coordinates to work with.
 */
public class InteractiveLayeredGraphVisitor implements IGraphElementVisitor {

    /**
     * Since nodes should not overlap and their width and height are only the width and height of the previous
     * layout run and therefore not reliable, we have to add additional spacing between them.
     * Therefore, nodes are placed with the following spacing, which is hopefully enough to make up for size
     * changes caused by interactive layout.
     * Of course one could construct a model for which this spacing is not enough, but we choose to disregard this.
     * In all non constructed normal cases this spacing should be sufficient to ensure that nodes do not overlap.
     */
    public static final int PSEUDO_POSITION_SPACING = Integer.MAX_VALUE;
    
    /**
     * Constants used as the index of the last layer.
     * This is used since a {@code layerConstraint} property might be set that assigns a node to the {@code LAST} layer.
     * Since a {@code layerConstraint} should still be respected it is translated into a {@code layerChoiceConstraint}
     * using this constant as the value for the last layer.
     */
    public static final int LAST_LAYER_INDEX = Integer.MAX_VALUE;

    /**
     * Visits all nodes and sets interactive options for the {@code layered} algorithm.
     * This assumes that the {@code layered} algorithm is the default algorithm.
     */
    @Override
    public void visit(final ElkGraphElement element) {
        // Only apply to root of the graph
        if (element instanceof ElkNode) {
            ElkNode root = (ElkNode) element;
            setInteractiveOptionsAndPseudoPositions(root);
        }
    }

    /**
     * Sets pseudo positions and interactive strategies for the given graph.
     * 
     * @param root
     *            Root of the graph
     */
    private void setInteractiveOptionsAndPseudoPositions(final ElkNode root) {
        if (!root.getChildren().isEmpty()) {
            String algorithm = root.getProperty(CoreOptions.ALGORITHM);
            if (algorithm == null || LayeredOptions.ALGORITHM_ID.endsWith(algorithm)) {
                // Make sure that potential usages of the LayerConstraint are translated into
                // LayerChoiceConstraints
                for (ElkNode node : root.getChildren()) {
                    if (node.hasProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT)) {
                        LayerConstraint constraint = node.getProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT);
                        node.setProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT, LayerConstraint.NONE);
                        switch (constraint) {
                        case FIRST:
                            if (node.getProperty(LayeredOptions.LAYERING_LAYER_CHOICE_CONSTRAINT) == -1) {
                                node.setProperty(LayeredOptions.LAYERING_LAYER_CHOICE_CONSTRAINT, 0);
                            }
                            break;
                        case LAST:
                            if (node.getProperty(LayeredOptions.LAYERING_LAYER_CHOICE_CONSTRAINT) == -1) {
                                node.setProperty(LayeredOptions.LAYERING_LAYER_CHOICE_CONSTRAINT, LAST_LAYER_INDEX);
                            }
                            break;
                        }
                    }
                    setCoordinates(root);
                    setInteractiveStrategies(root);
                }
            }
        }
    }

    /**
     * Sets the coordinates of the nodes in the graph {@code root} according to the set constraints.
     * 
     * @param root
     *            The root of the graph that should be layouted.
     */
    private void setCoordinates(final ElkNode root) {
        List<List<ElkNode>> layers = calcLayerNodes(root.getChildren());
        Direction direction = root.getProperty(LayeredOptions.DIRECTION);
        setCoordinateInLayoutDirection(layers, direction);
        int layerId = 0;
        for (List<ElkNode> layer : layers) {
            if (layer.size() > 0) {
                setCoordinatesOrthogonalToLayoutDirection(layer, layerId, direction);
                layerId++;
            }
        }
        return;
    }

    /**
     * Calculates the layers the {@code nodes} belong to.
     * 
     * @param nodes
     *            The nodes of the graph for which the layers should be calculated.
     */
    private List<List<ElkNode>> calcLayerNodes(final List<ElkNode> nodes) {
        ArrayList<ElkNode> allNodes = new ArrayList<ElkNode>();
        ArrayList<ElkNode> nodesWithLayerConstraint = new ArrayList<ElkNode>();
        // Save the nodes which layer constraint are set in a separate list
        for (ElkNode node : nodes) {
            allNodes.add(node);
            if (node.getProperty(LayeredOptions.LAYERING_LAYER_CHOICE_CONSTRAINT) != -1) {
                nodesWithLayerConstraint.add(node);
            }
        }

        // Calculate layers for nodes without constraints based on their layerID,
        // which is set by the previous layout run.
        List<List<ElkNode>> layerNodes = initialLayers(allNodes);

        // Assign layers to nodes with constraints.
        assignLayersToNodesWithProperty(nodesWithLayerConstraint, layerNodes);

        return layerNodes;
    }

    /**
     * Adds the nodes with a layer constraint to the already assigned layered nodes based on their layer constraint.
     * 
     * @param nodesWithLayerConstraint
     *            Nodes with set layer constraint that should be added to the layers.
     * @param layering
     *            List that contains the layers with their corresponding nodes.
     */
    private void assignLayersToNodesWithProperty(final List<ElkNode> nodesWithLayerConstraint,
            final List<List<ElkNode>> layering) {
        // Sort nodes with constraint based on their layer.
        nodesWithLayerConstraint.sort((ElkNode a, ElkNode b) -> {
            return a.getProperty(LayeredOptions.LAYERING_LAYER_CHOICE_CONSTRAINT)
                    - b.getProperty(LayeredOptions.LAYERING_LAYER_CHOICE_CONSTRAINT);
        });

        // Add the nodes with constraints to the their desired layer.

        // diff keeps track of the difference between the layer the node should
        // be in and the layer the node is really in.
        // This way nodes with the same layer constraint go in the same layer
        // although it may not be the layer that is specified by the constraint.
        int diff = 0;
        for (ElkNode node : nodesWithLayerConstraint) {
            int currentLayer = node.getProperty(LayeredOptions.LAYERING_LAYER_CHOICE_CONSTRAINT) - diff;
            if (currentLayer < layering.size()) {
                List<ElkNode> nodesOfLayer = layering.get(currentLayer);
                // Shift nodes to remove in-layer edges.
                shiftOtherNodes(node, currentLayer, layering, true);
                shiftOtherNodes(node, currentLayer, layering, false);
                nodesOfLayer.add(node);
            } else {
                diff = diff + currentLayer - layering.size();
                layering.add(new ArrayList<>(Arrays.asList(node)));
            }
        }
    }

    /**
     * Shifts nodes to the right such that edges in the same layer do not exist.
     * 
     * @param movedNode
     *            The node which connected nodes must be shifted .
     * @param layer
     *            The layer {@code moveNode} is in.
     * @param layerNodes
     *            All existing layers with the containing nodes.
     * @param incoming
     *            Determines if incoming or outgoing edges should be considered. True: incoming edges.
     */
    private void shiftOtherNodes(final ElkNode movedNode, final int layer, final List<List<ElkNode>> layerNodes,
            final boolean incoming) {
        List<ElkNode> nodesOfLayer = layerNodes.get(layer);
        // get edges
        List<ElkEdge> edges = new ArrayList<>();
        if (incoming) {
            ElkNode root = movedNode.getParent();
            for (ElkEdge edge : root.getContainedEdges()) {
                for (ElkConnectableShape target : edge.getTargets()) {
                    if (target.equals(movedNode)
                            || (target instanceof ElkPort && target.eContainer().equals(movedNode))) {
                        edges.add(edge);
                    }
                }
            }
        } else {
            ElkNode root = movedNode.getParent();
            for (ElkEdge edge : root.getContainedEdges()) {
                for (ElkConnectableShape target : edge.getSources()) {
                    if (target.equals(movedNode)
                            || (target instanceof ElkPort && target.eContainer().equals(movedNode))) {
                        edges.add(edge);
                    }
                }
            }
        }

        for (ElkEdge edge : edges) {
            // get connected node
            ElkNode node = null;
            if (incoming) {
                if (edge.getSources().get(0) instanceof ElkPort) {
                    node = (ElkNode) edge.getSources().get(0).eContainer();
                } else if (edge.getSources().get(0) instanceof ElkNode) {
                    node = (ElkNode) edge.getSources().get(0);
                }
            } else {
                if (edge.getTargets().get(0) instanceof ElkPort) {
                    node = (ElkNode) edge.getTargets().get(0).eContainer();
                } else if (edge.getTargets().get(0) instanceof ElkNode) {
                    node = (ElkNode) edge.getTargets().get(0);
                }
            }
            // shift node to the next layer
            if (nodesOfLayer.contains(node)) {
                nodesOfLayer.remove(node);
                List<ElkNode> newLayer;
                if (layer + 1 < layerNodes.size()) {
                    newLayer = layerNodes.get(layer + 1);
                    newLayer.add(node);
                    // the connected nodes in the layer the node is shifted to must be shifted too
                    shiftOtherNodes(node, layer + 1, layerNodes, false);
                    shiftOtherNodes(node, layer + 1, layerNodes, true);
                } else {
                    layerNodes.add(new ArrayList<>(Arrays.asList(node)));
                }
            }
        }
    }

    /**
     * Sorts the {@code nodes} in layers based on their layerID that was set via the {@code ConstraintsPostProcessor}.
     * 
     * @param nodes
     *            The nodes of the graph which layers should be calculated.
     */
    private List<List<ElkNode>> initialLayers(final ArrayList<ElkNode> nodes) {
        // Sort by layerID.
        nodes.sort((ElkNode a, ElkNode b) -> {
            return a.getProperty(LayeredOptions.LAYERING_LAYER_I_D) - b.getProperty(LayeredOptions.LAYERING_LAYER_I_D);
        });

        List<List<ElkNode>> layerNodes = new ArrayList<List<ElkNode>>();
        List<ElkNode> nodesOfLayer = new ArrayList<ElkNode>();
        int currentLayer = -1;
        // Assign nodes to layers.
        for (ElkNode node : nodes) {
            int layer = node.getProperty(LayeredOptions.LAYERING_LAYER_I_D);
            if (layer > currentLayer) {
                // Check if a node is added to a new layer.
                if (!nodesOfLayer.isEmpty()) {
                    layerNodes.add(nodesOfLayer);
                }
                nodesOfLayer = new ArrayList<ElkNode>();
                currentLayer = layer;
            }

            // Nodes with layer constraint should be ignored, since they are added later.
            if (node.getProperty(LayeredOptions.LAYERING_LAYER_CHOICE_CONSTRAINT) == -1) {
                nodesOfLayer.add(node);
            }
        }
        if (!nodesOfLayer.isEmpty()) {
            // Add the last layer nodes.
            layerNodes.add(nodesOfLayer);
        }
        return layerNodes;
    }

    /**
     * Sets the x coordinates of the nodes in {@code layers} according to their layer. The problem is that the height
     * and width of a node are determined by the height and width before the layout with constraints. They are therefore
     * potentially wrong. The placement has to be adjusted for this by a very high spacing between nodes.
     * 
     * @param layers
     *            The layers containing the associated nodes, already sorted regarding layers
     * @param direction
     *            The layout direction
     */
    private void setCoordinateInLayoutDirection(final List<List<ElkNode>> layers, final Direction direction) {
        double position = 0;
        double nextPosition = 0;
        // Assign x (RIGHT, LEFT)/y (UP, DOWN) coordinate such that all nodes get a pseudo position
        // that assigns them to the correct layer if interactive mode is used.
        for (List<ElkNode> nodesOfLayer : layers) {
            for (ElkNode node : nodesOfLayer) {
                switch (direction) {
                case UNDEFINED:
                case RIGHT:
                    node.setX(position);
                    if (position + node.getWidth() / 2 >= nextPosition) {
                        nextPosition = node.getX() + node.getWidth() + PSEUDO_POSITION_SPACING;
                    }
                    break;
                case LEFT:
                    node.setX(position);
                    if (node.getX() <= nextPosition) {
                        nextPosition = node.getX() - PSEUDO_POSITION_SPACING;
                    }
                    break;
                case DOWN:
                    node.setY(position);
                    if (position + node.getHeight() >= nextPosition) {
                        nextPosition = node.getY() + node.getHeight() + PSEUDO_POSITION_SPACING;
                    }
                    break;
                case UP:
                    node.setY(position);
                    if (node.getY() <= nextPosition) {
                        nextPosition = node.getY() - PSEUDO_POSITION_SPACING;
                    }
                    break;
                }
            }
            position = nextPosition;
        }
        return;
    }

    /**
     * Sets the positions of the nodes in their layer.
     * 
     * @param nodesOfLayer
     *            The list containing nodes that are in the same layer.
     * @param direction
     *            The layout direction.
     */
    private void setCoordinatesOrthogonalToLayoutDirection(final List<ElkNode> nodesOfLayer, final int layerId,
            final Direction direction) {
        // Separate nodes with and without position constraints.
        List<ElkNode> nodesWithPositionConstraint = new ArrayList<ElkNode>();
        List<ElkNode> nodes = new ArrayList<ElkNode>();
        for (ElkNode node : nodesOfLayer) {
            if (node.getProperty(LayeredOptions.CROSSING_MINIMIZATION_POSITION_CHOICE_CONSTRAINT) != -1) {
                nodesWithPositionConstraint.add(node);
            } else {
                nodes.add(node);
            }
        }

        // Determine the order of the nodes.
        sortNodesInLayer(nodesWithPositionConstraint, nodes, direction);
        // Add the nodes with position constraint at the desired position in their layer.
        for (ElkNode node : nodesWithPositionConstraint) {
            int pos = node.getProperty(LayeredOptions.CROSSING_MINIMIZATION_POSITION_CHOICE_CONSTRAINT);
            if (pos < nodes.size()) {
                nodes.add(pos, node);
            } else {
                nodes.add(node);
            }
        }

        // Set the y/x positions according to the order of the nodes.
        switch (direction) {
        case UNDEFINED:
        case RIGHT:
        case LEFT:
            double yPos = nodes.get(0).getY();
            for (ElkNode node : nodes) {
                node.setProperty(LayeredOptions.POSITION, new KVector(node.getX(), yPos));
                yPos += node.getHeight() + PSEUDO_POSITION_SPACING;
            }
            break;
        case DOWN:
        case UP:
            double xPos = nodes.get(0).getX() + 2 * layerId * PSEUDO_POSITION_SPACING;
            for (ElkNode node : nodes) {
                node.setProperty(LayeredOptions.POSITION, new KVector(xPos, node.getY()));
                xPos += node.getWidth() + PSEUDO_POSITION_SPACING;
            }
            break;
        }
    }

    /**
     * Sorts the {@code nodesWithPositionConstraint} according their position constraint and {@code nodes} according to
     * relevant coordinate (y for left, right, x for up, down).
     * 
     * @param nodesWithPositionConstraint
     *            The nodes with position constraint.
     * @param nodes
     *            The nodes without position constraints.
     * @param direction
     *            The layout direction.
     */
    private void sortNodesInLayer(final List<ElkNode> nodesWithPositionConstraint, final List<ElkNode> nodes,
            final Direction direction) {
        // Sort nodes with constraint by their position constraint.
        nodesWithPositionConstraint.sort((ElkNode a, ElkNode b) -> {
            return a.getProperty(LayeredOptions.CROSSING_MINIMIZATION_POSITION_CHOICE_CONSTRAINT)
                    - b.getProperty(LayeredOptions.CROSSING_MINIMIZATION_POSITION_CHOICE_CONSTRAINT);
        });
        // Sort other nodes based on their y(RIGHT/LEFT)/x(DOWN/UP) coordinate.
        nodes.sort((ElkNode a, ElkNode b) -> {
            switch (direction) {
            case UNDEFINED:
            case RIGHT:
            case LEFT:
                return (int) (a.getY() - b.getY());
            case DOWN:
            case UP:
                return (int) (a.getX() - b.getX());
            }
            return 0;
        });
    }

    /**
     * Sets the (semi) interactive strategies in the phases crossing minimization, layer assignment, cycle breaking for
     * the given parent node.
     * 
     * @param parent
     *            The graph which strategies should be set.
     */
    private void setInteractiveStrategies(final ElkNode parent) {
        parent.setProperty(LayeredOptions.CROSSING_MINIMIZATION_SEMI_INTERACTIVE, true);
        parent.setProperty(LayeredOptions.LAYERING_STRATEGY, LayeringStrategy.INTERACTIVE);
        parent.setProperty(LayeredOptions.CYCLE_BREAKING_STRATEGY, CycleBreakingStrategy.INTERACTIVE);
    }

}
