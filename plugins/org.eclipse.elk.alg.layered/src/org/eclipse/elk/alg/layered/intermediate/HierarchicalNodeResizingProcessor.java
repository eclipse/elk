/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 *
 * Copyright 2015 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 *
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package org.eclipse.elk.alg.layered.intermediate;

import java.util.Set;

import org.eclipse.elk.alg.layered.ILayoutProcessor;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LGraphUtil;
import org.eclipse.elk.alg.layered.graph.LInsets;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.properties.ContentAlignment;
import org.eclipse.elk.alg.layered.properties.GraphProperties;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.layered.properties.LayeredOptions;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * This Processor maps an inner graph to its parent node. It must be run as the last non-hierarchical processor in a
 * compound layout.
 *
 * @author alan
 *
 */
public class HierarchicalNodeResizingProcessor implements ILayoutProcessor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(final LGraph graph, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Finish hierarchical layout", 1);
        for (Layer layer : graph) {
            graph.getLayerlessNodes().addAll(layer.getNodes());
            layer.getNodes().clear();
        }
        for (LNode node : graph.getLayerlessNodes()) {
            node.setLayer(null);
        }
        graph.getLayers().clear();
        resizeGraph(graph);
        if (isNested(graph)) {
            graphLayoutToNode(parentNodeOf(graph), graph);
        }
        progressMonitor.done();
    }

    private LNode parentNodeOf(final LGraph graph) {
        return graph.getProperty(InternalProperties.PARENT_LNODE);
    }

    /**
     * Transfer the layout of the given graph to the given associated node.
     *
     * @param node
     *            a compound node
     * @param lgraph
     *            the graph nested in the compound node
     */
    private void graphLayoutToNode(final LNode node, final LGraph lgraph) {
        // Process external ports
        for (LNode childNode : lgraph.getLayerlessNodes()) {
            Object origin = childNode.getProperty(InternalProperties.ORIGIN);
            if (origin instanceof LPort) {
                LPort port = (LPort) origin;
                KVector portPosition =
                        LGraphUtil.getExternalPortPosition(lgraph, childNode, port.getSize().x,
                                port.getSize().y);
                port.getPosition().x = portPosition.x;
                port.getPosition().y = portPosition.y;
                port.setSide(childNode.getProperty(InternalProperties.EXT_PORT_SIDE));
            }
        }

        // Setup the parent node
        KVector actualGraphSize = lgraph.getActualSize();
        if (lgraph.getProperty(InternalProperties.GRAPH_PROPERTIES).contains(
                GraphProperties.EXTERNAL_PORTS)) {
            // Ports have positions assigned
            node.setProperty(LayeredOptions.PORT_CONSTRAINTS, PortConstraints.FIXED_POS);
            node.getGraph().getProperty(InternalProperties.GRAPH_PROPERTIES)
                    .add(GraphProperties.NON_FREE_PORTS);
            LGraphUtil.resizeNode(node, actualGraphSize, false, true);
        } else {
            // Ports have not been positioned yet - leave this for next layouter
            LGraphUtil.resizeNode(node, actualGraphSize, true, true);
        }
    }

    private boolean isNested(final LGraph graph) {
        return parentNodeOf(graph) != null;
    }

    // //////////////////////////////////////////////////////////////////////////////
    // Graph Postprocessing (Size and External Ports)

    /**
     * Sets the size of the given graph such that size constraints are adhered to. Furthermore, the border spacing is
     * added to the graph size and the graph offset. Afterwards, the border spacing property is reset to 0.
     *
     * <p>
     * Major parts of this method are adapted from
     * {@link KimlUtil#resizeNode(de.cau.cs.kieler.core.kgraph.KNode, float, float, boolean)}.
     * </p>
     *
     * <p>
     * Note: This method doesn't care about labels of compound nodes since those labels are not attached to the graph.
     * </p>
     *
     * @param lgraph
     *            the graph to resize.
     */
    private void resizeGraph(final LGraph lgraph) {
        Set<SizeConstraint> sizeConstraint = lgraph.getProperty(LayeredOptions.NODE_SIZE_CONSTRAINTS);
        Set<SizeOptions> sizeOptions = lgraph.getProperty(LayeredOptions.NODE_SIZE_OPTIONS);
        float borderSpacing = lgraph.getProperty(LayeredOptions.SPACING_BORDER);

        // add the border spacing to the graph size and graph offset
        lgraph.getOffset().x += borderSpacing;
        lgraph.getOffset().y += borderSpacing;
        lgraph.getSize().x += 2 * borderSpacing;
        lgraph.getSize().y += 2 * borderSpacing;

        // the graph size now contains the border spacing, so clear it in order to keep
        // graph.getActualSize() working properly
        lgraph.setProperty(LayeredOptions.SPACING_BORDER, 0f);

        KVector calculatedSize = lgraph.getActualSize();
        KVector adjustedSize = new KVector(calculatedSize);

        // calculate the new size
        if (sizeConstraint.contains(SizeConstraint.MINIMUM_SIZE)) {
            float minWidth = lgraph.getProperty(LayeredOptions.NODE_SIZE_MIN_WIDTH);
            float minHeight = lgraph.getProperty(LayeredOptions.NODE_SIZE_MIN_HEIGHT);

            // if minimum width or height are not set, maybe default to default values
            if (sizeOptions.contains(SizeOptions.DEFAULT_MINIMUM_SIZE)) {
                if (minWidth <= 0) {
                    minWidth = ElkUtil.DEFAULT_MIN_WIDTH;
                }

                if (minHeight <= 0) {
                    minHeight = ElkUtil.DEFAULT_MIN_HEIGHT;
                }
            }

            // apply new size including border spacing
            adjustedSize.x = Math.max(calculatedSize.x, minWidth);
            adjustedSize.y = Math.max(calculatedSize.y, minHeight);
        }

        resizeGraphNoReallyIMeanIt(lgraph, calculatedSize, adjustedSize);
    }

    /**
     * Applies a new effective size to a graph that previously had an old size calculated by the layout algorithm. This
     * method takes care of adjusting content alignments as well as external ports that would be misplaced if the new
     * size is larger than the old one.
     *
     * @param lgraph
     *            the graph to apply the size to.
     * @param oldSize
     *            old size as calculated by the layout algorithm.
     * @param newSize
     *            new size that may be larger than the old one.
     */
    private void resizeGraphNoReallyIMeanIt(final LGraph lgraph, final KVector oldSize,
            final KVector newSize) {

        // obey to specified alignment constraints
        Set<ContentAlignment> contentAlignment = lgraph.getProperty(LayeredOptions.CONTENT_ALIGNMENT);

        // horizontal alignment
        if (newSize.x > oldSize.x) {
            if (contentAlignment.contains(ContentAlignment.H_CENTER)) {
                lgraph.getOffset().x += (newSize.x - oldSize.x) / 2f;
            } else if (contentAlignment.contains(ContentAlignment.H_RIGHT)) {
                lgraph.getOffset().x += newSize.x - oldSize.x;
            }
        }

        // vertical alignment
        if (newSize.y > oldSize.y) {
            if (contentAlignment.contains(ContentAlignment.V_CENTER)) {
                lgraph.getOffset().y += (newSize.y - oldSize.y) / 2f;
            } else if (contentAlignment.contains(ContentAlignment.V_BOTTOM)) {
                lgraph.getOffset().y += newSize.y - oldSize.y;
            }
        }

        // correct the position of eastern and southern hierarchical ports, if necessary
        if (lgraph.getProperty(InternalProperties.GRAPH_PROPERTIES).contains(
                GraphProperties.EXTERNAL_PORTS)
                && (newSize.x > oldSize.x || newSize.y > oldSize.y)) {

            // iterate over the graph's nodes, looking for eastern / southern external ports
            // (at this point, the graph's nodes are not divided into layers anymore)
            for (LNode node : lgraph.getLayerlessNodes()) {
                // we're only looking for external port dummies
                if (node.getType() == NodeType.EXTERNAL_PORT) {
                    // check which side the external port is on
                    PortSide extPortSide = node.getProperty(InternalProperties.EXT_PORT_SIDE);
                    if (extPortSide == PortSide.EAST) {
                        node.getPosition().x += newSize.x - oldSize.x;
                    } else if (extPortSide == PortSide.SOUTH) {
                        node.getPosition().y += newSize.y - oldSize.y;
                    }
                }
            }
        }

        // Actually apply the new size
        LInsets insets = lgraph.getInsets();
        lgraph.getSize().x = newSize.x - insets.left - insets.right;
        lgraph.getSize().y = newSize.y - insets.top - insets.bottom;
    }

}
