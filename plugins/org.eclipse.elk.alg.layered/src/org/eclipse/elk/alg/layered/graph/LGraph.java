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
package org.eclipse.elk.alg.layered.graph;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.elk.alg.layered.properties.Properties;
import org.eclipse.elk.core.math.KVector;

import com.google.common.collect.Lists;

/**
 * A layered graph has a set of layers that contain the nodes, as well as a
 * list of nodes that are not yet assigned to a layer. Layout algorithms are
 * required to layout the graph from left to right. If another layout direction
 * is desired, it can be obtained by pre-processing and post-processing the graph.
 * In contrast to the KGraph structure, the LGraph is not EMF-based, but plain Java.
 * It is optimized for being processed by a layer-based layout algorithm.
 * 
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating yellow 2013-03-22 review KI-35 by chsch, grh
 */
public final class LGraph extends LGraphElement implements Iterable<Layer> {
    
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -8006835373897072852L;
    
    /**
     * The total size of the drawing. The total size includes neither insets nor border spacing. If the
     * actual size, including insets and border spacing, is required, this can be obtained by calling
     * {@link #getActualSize()}.
     */
    private final KVector size = new KVector();
    
    /**
     * The graph's effective insets. This already includes the insets set on the imported graph by the
     * client, as well as any space required for inside node labels. These are the effective insets
     * returned to the client if {@link org.eclipse.elk.core.options.SizeOptions#COMPUTE_INSETS
     * SizeOptions.COMPUTE_INSETS}
     * is active.
     */
    private final LInsets insets = new LInsets(0, 0, 0, 0);
    
    /**
     * The offset that will be applied to all node positions. Adding the offset to the coordinates of
     * child nodes results in child node coordinates that are relative to the node's client area (which
     * does not include the insets around it).
     */
    private final KVector offset = new KVector();
    
    /**
     * Nodes that are not currently part of a layer.
     */
    private final List<LNode> layerlessNodes = Lists.newArrayList();
    
    /**
     * The layers of the layered graph.
     */
    private final List<Layer> layers = Lists.newArrayList();
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        if (layers.isEmpty()) {
            return "G-unlayered" + layerlessNodes.toString();
        } else if (layerlessNodes.isEmpty()) {
            return "G-layered" + layers.toString();
        }
        return "G[layerless" + layerlessNodes.toString() + ", layers" + layers.toString() + "]";
    }
    
    /**
     * Returns the size of the graph, that is the bounding box that covers the
     * whole drawing. The size does not include insets or anything. Modifying the
     * returned value changes the size of the graph.
     * 
     * @return the size of the layered graph; modify to change the graph size.
     */
    public KVector getSize() {
        return size;
    }
    
    /**
     * Returns the graph's size including any borders. If the graph represents a
     * hierarchical node, the returned size represents the node's size. The returned
     * size can be modified at will without having any influence on the graph's size
     * or the actual size returned on the next method call.
     * 
     * @return the graph's size including borders.
     */
    public KVector getActualSize() {
        float borderSpacing = getProperty(Properties.SPACING_BORDER);
        return new KVector(
                size.x + insets.left + insets.right + (2 * borderSpacing),
                size.y + insets.top + insets.bottom + (2 * borderSpacing));
    }
    
    /**
     * Returns the insets of the graph. The insets determine the amount of space between
     * the content area and the graph's actual border. Modifying the returned value
     * changes the insets.
     * 
     * @return the insets; modify to change the graph's insets.
     */
    public LInsets getInsets() {
        return insets;
    }

    /**
     * Returns the offset for the graph, that is a coordinate vector that has
     * to be added to all position values of nodes and edges. It is usually used
     * to reserve some space in the content area for additional edge routing.
     * 
     * <b>Note:</b> Since many different parts of the algorithm may contribute to
     * the offset, never set the offset to an absolute value! Rather, only add to
     * the offset!
     * 
     * @return the offset of the layered graph
     */
    public KVector getOffset() {
        return offset;
    }
    
    /**
     * Returns the list of nodes that are not currently assigned to a layer.
     * When creating a graph, put the nodes here.
     * 
     * @return the layerless nodes.
     */
    public List<LNode> getLayerlessNodes() {
        return layerlessNodes;
    }

    /**
     * Returns the list of layers of the graph. Layers are created automatically by the layout
     * algorithm, so this list must not be touched when the graph is created.
     * 
     * @return the layers
     */
    public List<Layer> getLayers() {
        return layers;
    }

    /**
     * Returns an iterator over the layers.
     * 
     * @return an iterator for the layers of this layered graph
     */
    public Iterator<Layer> iterator() {
        return layers.iterator();
    }
    
    /**
     * Returns a two-dimensional array representation of all nodes in the graph. The first dimension
     * represents the layers, the second dimension represents the nodes in each layer.
     * 
     * @return two-dimensional array of nodes in the graph.
     */
    public LNode[][] toNodeArray() {
        LNode[][] lgraphArray = new LNode[layers.size()][];
        ListIterator<Layer> layerIter = layers.listIterator();
        while (layerIter.hasNext()) {
            Layer layer = layerIter.next();
            int layerIndex = layerIter.previousIndex();
            lgraphArray[layerIndex] = layer.getNodes().toArray(new LNode[layer.getNodes().size()]);
        }
        
        return lgraphArray;
    }
    
}
