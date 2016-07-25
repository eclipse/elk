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
package org.eclipse.elk.core.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.PriorityQueue;
import java.util.Queue;

import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.klayoutdata.KInsets;
import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.BoxLayouterOptions;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.graph.KNode;

import com.google.common.collect.Lists;

/**
 * A layout algorithm that does not take edges into account, but treats all nodes as isolated boxes.
 * This is useful for parts of a diagram that consist of objects without connections, such as
 * parallel regions in Statecharts.
 * 
 * The box layouter allows to configure the underlying packing strategy, see the {@link PackingMode} enumeration.
 * 
 * TODO there's duplicate code in this class, handling either the simple mode or any of the grouped modes. 
 * It should be possible to re-use the grouped code for the simple packing mode though.
 * 
 * @kieler.rating yellow 2012-08-10 review KI-23 by cds, sgu
 * @kieler.design proposed by msp
 * @author msp
 */
public class BoxLayoutProvider extends AbstractLayoutProvider {
    
    /**
     * Configures the packing mode used by the {@link BoxLayoutProvider}.
     * If {@value #SIMPLE} is not required, {@link #GROUP_DEC} could improve the packing and decrease the area. 
     * {@value #GROUP_MIXED} and {@link #GROUP_INC} may, in very specific scenarios, work better.
     */
    public enum PackingMode {
        
        /** In order to use either of {@link BoxLayouterOptions#PRIORITY} 
         * {@link BoxLayouterOptions#INTERACTIVE}, this mode must be used.*/
        SIMPLE,
        
        /** Tries to group boxes iterating the boxes in decreasing area. */
        GROUP_DEC,

        /**
         * Iterating in increasing area, tries to be smart by grouping a subset of small nodes such that their combined
         * area is close to half of the area of a larger node.
         */
        GROUP_MIXED,
        
        /** Tries to group boxes iterating the boxes in increasing area. */
        GROUP_INC,       
    }
    
    /** default value for spacing between boxes. */
    private static final float DEF_SPACING = 15.0f;
    /** default value for aspect ratio. */
    public static final float DEF_ASPECT_RATIO = 1.3f;

    /**
     * {@inheritDoc}
     */
    @Override
    public void layout(final KNode layoutNode, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Box layout", 2);
        KShapeLayout parentLayout = layoutNode.getData(KShapeLayout.class);
        // set option for minimal spacing
        Float objSpacing = parentLayout.getProperty(BoxLayouterOptions.SPACING_NODE);
        if (objSpacing == null || objSpacing < 0) {
            objSpacing = DEF_SPACING;
        }
        // set option for border spacing
        Float borderSpacing = parentLayout.getProperty(BoxLayouterOptions.SPACING_BORDER);
        if (borderSpacing == null || borderSpacing < 0) {
            borderSpacing = DEF_SPACING;
        }
        // set expand nodes option
        boolean expandNodes = parentLayout.getProperty(BoxLayouterOptions.EXPAND_NODES);
        // set interactive option
        boolean interactive = parentLayout.getProperty(BoxLayouterOptions.INTERACTIVE);

        PackingMode mode = parentLayout.getProperty(BoxLayouterOptions.BOX_PACKING_MODE);
        switch (mode) {
        case SIMPLE:
            // sort boxes according to priority and position or size
            List<KNode> sortedBoxes = sort(layoutNode, interactive);
            // place boxes on the plane
            placeBoxes(sortedBoxes, layoutNode, objSpacing, borderSpacing, expandNodes);
            break;

        default: // any of the groups
            // the method takes care of sorting the boxes itself
            placeBoxesGrouping(layoutNode, objSpacing, borderSpacing, expandNodes);
        }

        progressMonitor.done();
    }

    /**
     * Sorts nodes according to priority and size or position. Nodes with higher priority are
     * put to the start of the list.
     * 
     * @param parentNode parent node
     * @param interactive whether position should be considered instead of size
     * @return sorted list of children
     */
    private List<KNode> sort(final KNode parentNode, final boolean interactive) {
        List<KNode> sortedBoxes = new LinkedList<KNode>(
                parentNode.getChildren());
        
        Collections.sort(sortedBoxes, new Comparator<KNode>() {
            public int compare(final KNode child1, final KNode child2) {
                KShapeLayout layout1 = child1.getData(KShapeLayout.class);
                Integer prio1 = layout1.getProperty(BoxLayouterOptions.PRIORITY);
                if (prio1 == null) {
                    prio1 = 0;
                }
                KShapeLayout layout2 = child2.getData(KShapeLayout.class);
                Integer prio2 = layout2.getProperty(BoxLayouterOptions.PRIORITY);
                if (prio2 == null) {
                    prio2 = 0;
                }
                if (prio1 > prio2) {
                    return -1;
                } else if (prio1 < prio2) {
                    return 1;
                } else {
                    // boxes have same priority - compare their position or size
                    if (interactive) {
                        int c = Float.compare(layout1.getYpos(), layout2.getYpos());
                        if (c != 0) {
                            return c;
                        }
                        c = Float.compare(layout1.getXpos(), layout2.getXpos());
                        if (c != 0) {
                            return c;
                        }
                    }
                    float size1 = layout1.getWidth() * layout1.getHeight();
                    float size2 = layout2.getWidth() * layout2.getHeight();
                    return Float.compare(size1, size2);
                }
            }
        });
        
        return sortedBoxes;
    }

    /**
     * Place the boxes of the given sorted list according to their order in the list.
     * Furthermore, adjust the parent size to fit the bounding box.
     * 
     * @param sortedBoxes sorted list of boxes
     * @param parentNode parent node
     * @param objSpacing minimal spacing between elements
     * @param borderSpacing spacing to the border
     * @param expandNodes if true, the nodes are expanded to fill their parent
     */
    private void placeBoxes(final List<KNode> sortedBoxes, final KNode parentNode,
            final float objSpacing, final float borderSpacing, final boolean expandNodes) {
        KShapeLayout parentLayout = parentNode.getData(KShapeLayout.class);
        
        KInsets insets = parentLayout.getInsets();
        KVector minSize = parentLayout.getProperty(BoxLayouterOptions.NODE_SIZE_MINIMUM);
        float minWidth, minHeight;
        if (minSize == null) {
            minWidth = parentLayout.getProperty(BoxLayouterOptions.NODE_SIZE_MIN_WIDTH);
            minHeight = parentLayout.getProperty(BoxLayouterOptions.NODE_SIZE_MIN_HEIGHT);
        } else {
            minWidth = (float) minSize.x;
            minHeight = (float) minSize.y; 
        }
        minWidth = Math.max(minWidth - insets.getLeft() - insets.getRight(), 0);
        minHeight = Math.max(minHeight - insets.getTop() - insets.getBottom(), 0);
        
        Float aspectRatio = parentLayout.getProperty(BoxLayouterOptions.ASPECT_RATIO);
        if (aspectRatio == null || aspectRatio <= 0) {
            aspectRatio = DEF_ASPECT_RATIO;
        }

        // do place the boxes
        KVector parentSize = placeBoxes(sortedBoxes, objSpacing, borderSpacing, minWidth, minHeight,
                expandNodes, aspectRatio);

        // adjust parent size
        float width = insets.getLeft() + (float) parentSize.x + insets.getRight();
        float height = insets.getTop() + (float) parentSize.y + insets.getBottom();
        ElkUtil.resizeNode(parentNode, width, height, false, true);
    }

    /**
     * Place the boxes of the given sorted list according to their order in the list.
     * 
     * @param sortedBoxes sorted list of boxes
     * @param minSpacing minimal spacing between elements
     * @param borderSpacing spacing to the border
     * @param minTotalWidth minimal width of the parent node
     * @param minTotalHeight minimal height of the parent node
     * @param expandNodes if true, the nodes are expanded to fill their parent
     * @param aspectRatio the desired aspect ratio
     * @return the bounding box of the resulting layout
     */
    private KVector placeBoxes(final List<KNode> sortedBoxes, final float minSpacing,
            final float borderSpacing, final float minTotalWidth, final float minTotalHeight,
            final boolean expandNodes, final float aspectRatio) {
        // determine the maximal row width by the maximal box width and the total area
        float maxRowWidth = 0.0f;
        float totalArea = 0.0f;
        for (KNode box : sortedBoxes) {
            KShapeLayout boxLayout = box.getData(KShapeLayout.class);
            ElkUtil.resizeNode(box);
            maxRowWidth = Math.max(maxRowWidth, boxLayout.getWidth());
            totalArea += boxLayout.getWidth() * boxLayout.getHeight();
        }
        
        // calculate std deviation
        //  rationale: the greater the diversity of box sizes, the more space will be 'wasted',
        //  contributing to the total area. We address this by adding x*n*stddev to the area.
        //  TODO x should be assessed empirically, for the moment set it to 1
        float mean = totalArea / sortedBoxes.size();
        float stddev = areaStdDev(sortedBoxes, mean);
        
        totalArea += (sortedBoxes.size() * 1 * stddev);

        // calculate the required row width w to achieve the desired aspect ratio,
        //  i.e.:  w*h=area s.t. w/h=dar  ->  w=sqrt(area * dar) 
        maxRowWidth = (float) Math.max(maxRowWidth, Math.sqrt(totalArea * aspectRatio)) + borderSpacing;

        // place nodes iteratively into rows
        float xpos = borderSpacing;
        float ypos = borderSpacing;
        float highestBox = 0.0f;
        float broadestRow = 2 * borderSpacing;
        LinkedList<Integer> rowIndices = new LinkedList<Integer>();
        rowIndices.add(Integer.valueOf(0));
        LinkedList<Float> rowHeights = new LinkedList<Float>();
        ListIterator<KNode> boxIter = sortedBoxes.listIterator();
        while (boxIter.hasNext()) {
            KNode box = boxIter.next();
            KShapeLayout boxLayout = box.getData(KShapeLayout.class);
            float width = boxLayout.getWidth();
            float height = boxLayout.getHeight();
            if (xpos + width > maxRowWidth) {
                // place box into the next row
                if (expandNodes) {
                    rowHeights.addLast(Float.valueOf(highestBox));
                    rowIndices.addLast(Integer.valueOf(boxIter.previousIndex()));
                }
                xpos = borderSpacing;
                ypos += highestBox + minSpacing;
                highestBox = 0.0f;
                broadestRow = Math.max(broadestRow, 2 * borderSpacing + width);
            }
            boxLayout.setPos(xpos, ypos);
            broadestRow = Math.max(broadestRow, xpos + width + borderSpacing);
            highestBox = Math.max(highestBox, height);
            xpos += width + minSpacing;
        }
        broadestRow = Math.max(broadestRow, minTotalWidth);
        float totalHeight = ypos + highestBox + borderSpacing;
        if (totalHeight < minTotalHeight) {
            highestBox += minTotalHeight - totalHeight;
            totalHeight = minTotalHeight;
        }

        // expand nodes if required
        if (expandNodes) {
            xpos = borderSpacing;
            boxIter = sortedBoxes.listIterator();
            rowIndices.addLast(Integer.valueOf(sortedBoxes.size()));
            ListIterator<Integer> rowIndexIter = rowIndices.listIterator();
            int nextRowIndex = rowIndexIter.next();
            rowHeights.addLast(Float.valueOf(highestBox));
            ListIterator<Float> rowHeightIter = rowHeights.listIterator();
            float rowHeight = 0.0f;
            while (boxIter.hasNext()) {
                if (boxIter.nextIndex() == nextRowIndex) {
                    xpos = borderSpacing;
                    rowHeight = rowHeightIter.next();
                    nextRowIndex = rowIndexIter.next();
                }
                KNode box = boxIter.next();
                KShapeLayout boxLayout = box.getData(KShapeLayout.class);
                boxLayout.setHeight(rowHeight);
                if (boxIter.nextIndex() == nextRowIndex) {
                    float newWidth = broadestRow - xpos - borderSpacing;
                    float oldWidth = boxLayout.getWidth();
                    boxLayout.setWidth(newWidth);
                    ElkUtil.translate(box, (newWidth - oldWidth) / 2, 0.0f);
                }
                xpos += boxLayout.getWidth() + minSpacing;
            }
        }

        // return parent size
        return new KVector(broadestRow, totalHeight);
    }

    private float areaStdDev(final List<KNode> boxes, final float mean) {
        float variance = 0;
        for (KNode box : boxes) {
            KShapeLayout boxLayout = box.getData(KShapeLayout.class);
            variance += Math.pow(boxLayout.getWidth() * boxLayout.getHeight() - mean, 2);
        }
        float stddev = (float) Math.sqrt(variance / (boxes.size() - 1));
        return stddev;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////
    //  Now the grouping methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Place the boxes of the given sorted list according to their order in the list.
     * Furthermore, adjust the parent size to fit the bounding box.
     * 
     * @param parentNode parent node
     * @param objSpacing minimal spacing between elements
     * @param borderSpacing spacing to the border
     * @param expandNodes if true, the nodes are expanded to fill their parent
     */
    private void placeBoxesGrouping(final KNode parentNode,
            final float objSpacing, final float borderSpacing, final boolean expandNodes) {
        
        KShapeLayout parentLayout = parentNode.getData(KShapeLayout.class);
        KInsets insets = parentLayout.getInsets();
        KVector minSize = parentLayout.getProperty(BoxLayouterOptions.NODE_SIZE_MINIMUM);
        float minWidth, minHeight;
        if (minSize == null) {
            minWidth = parentLayout.getProperty(BoxLayouterOptions.NODE_SIZE_MIN_WIDTH);
            minHeight = parentLayout.getProperty(BoxLayouterOptions.NODE_SIZE_MIN_HEIGHT);
        } else {
            minWidth = (float) minSize.x;
            minHeight = (float) minSize.y; 
        }
        minWidth = Math.max(minWidth - insets.getLeft() - insets.getRight(), 0);
        minHeight = Math.max(minHeight - insets.getTop() - insets.getBottom(), 0);
        
        Float aspectRatio = parentLayout.getProperty(BoxLayouterOptions.ASPECT_RATIO);
        if (aspectRatio == null || aspectRatio <= 0) {
            aspectRatio = DEF_ASPECT_RATIO;
        }

        // wrap boxes in groups
        List<Group> groups = Lists.newLinkedList();
        for (KNode node : parentNode.getChildren()) {
            Group g = new Group(node);
            groups.add(g);
        }

        // pack according to the mode (sorting is done within every method)
        PackingMode mode = parentLayout.getProperty(BoxLayouterOptions.BOX_PACKING_MODE);
        List<Group> toBePlaced;
        // no border spacing for the grouped nodes!
        switch (mode) {
        case GROUP_INC:
            toBePlaced = mergeAndPlaceInc(groups, objSpacing, minWidth, minHeight, expandNodes, aspectRatio);
            break;
        case GROUP_DEC:
            toBePlaced = mergeAndPlaceDec(groups, objSpacing, minWidth, minHeight, expandNodes, aspectRatio);
            break;
        default: // GROUP_MIXED
            toBePlaced = mergeAndPlaceMixed(groups, objSpacing, minWidth, minHeight, expandNodes, aspectRatio);
            break;
        }
        
        Group finalGroup = new Group(toBePlaced);
        // do final packing of the 'out-most' boxes
        KVector parentSize = placeInnerBoxes(finalGroup, objSpacing, borderSpacing, minWidth, minHeight,
                expandNodes, aspectRatio);

        // account for border spacing
        finalGroup.translate(borderSpacing, borderSpacing);
        parentSize.add(2 * borderSpacing, 2 * borderSpacing);
        
        // adjust parent size
        float width = insets.getLeft() + (float) parentSize.x + insets.getRight();
        float height = insets.getTop() + (float) parentSize.y + insets.getBottom();
        ElkUtil.resizeNode(parentNode, width, height, false, true);
    }
    
    /**
     * Place the boxes of the given sorted list according to their order in the list.
     * 
     * @param sortedBoxes sorted list of boxes
     * @param minSpacing minimal spacing between elements
     * @param borderSpacing spacing to the border
     * @param minTotalWidth minimal width of the parent node
     * @param minTotalHeight minimal height of the parent node
     * @param expandNodes if true, the nodes are expanded to fill their parent
     * @param aspectRatio the desired aspect ratio
     * @return the bounding box of the resulting layout
     */
    private KVector placeInnerBoxes(final Group group, final float minSpacing,
            final float borderSpacing, final float minTotalWidth, final float minTotalHeight,
            final boolean expandNodes, final float aspectRatio) {
        // determine the maximal row width by the maximal box width and the total area
        float maxRowWidth = 0.0f;
        float totalArea = 0.0f;
        for (Group box : group.groups) {
            if (box.node != null) {
                ElkUtil.resizeNode(box.node);
            }
            maxRowWidth = Math.max(maxRowWidth, box.getWidth());
            totalArea += box.getWidth() * box.getHeight();
        }
        
        // calculate std deviation
        //  rationale: the greater the diversity of box sizes, the more space will be 'wasted',
        //  contributing to the total area. We address this by adding x*n*stddev to the area.
        //  TODO x should be assessed empirically, for the moment set it to 1 (GROUP_DEC worked better with 2 though)
        float mean = totalArea / group.groups.size();
        float stddev = areaStdDev2(group.groups, mean);
        
        float sdInfluence = 1;
        totalArea += (group.groups.size() * sdInfluence * stddev);

        // calculate the required row width w to achieve the desired aspect ratio,
        //  i.e.:  w*h=area s.t. w/h=dar  ->  w=sqrt(area * dar) 
        maxRowWidth = (float) Math.max(maxRowWidth, Math.sqrt(totalArea * aspectRatio)) + borderSpacing;
        
        // place nodes iteratively into rows
        float xpos = borderSpacing;
        float ypos = borderSpacing;
        float highestBox = 0.0f;
        float broadestRow = 2 * borderSpacing;
        LinkedList<Integer> rowIndices = new LinkedList<Integer>();
        rowIndices.add(Integer.valueOf(0));
        LinkedList<Float> rowHeights = new LinkedList<Float>();
        ListIterator<Group> boxIter = group.groups.listIterator();
        Group last = null;
        List<Group> bottoms = Lists.newArrayList();
        while (boxIter.hasNext()) {
            Group box = boxIter.next();
            float width = box.getWidth();
            float height = box.getHeight();
            if (xpos + width > maxRowWidth) {
                // place box into the next row
                if (expandNodes) {
                    rowHeights.addLast(Float.valueOf(highestBox));
                    rowIndices.addLast(Integer.valueOf(boxIter.previousIndex()));
                    group.right.add(last);
                    bottoms.clear();
                }
                xpos = borderSpacing;
                ypos += highestBox + minSpacing;
                highestBox = 0.0f;
                broadestRow = Math.max(broadestRow, 2 * borderSpacing + width);
            }
            bottoms.add(box);
            box.translate(xpos, ypos);
            broadestRow = Math.max(broadestRow, xpos + width + borderSpacing);
            highestBox = Math.max(highestBox, height);
            xpos += width + minSpacing;
            last = box;
        }
        group.bottom.addAll(bottoms);
        group.right.add(bottoms.get(bottoms.size() - 1));
        broadestRow = Math.max(broadestRow, minTotalWidth);
        float totalHeight = ypos + highestBox + borderSpacing;
        if (totalHeight < minTotalHeight) {
            highestBox += minTotalHeight - totalHeight;
            totalHeight = minTotalHeight;
        }

        // expand nodes if required
        if (expandNodes) {
            xpos = borderSpacing;
            boxIter = group.groups.listIterator();
            rowIndices.addLast(Integer.valueOf(group.groups.size()));
            ListIterator<Integer> rowIndexIter = rowIndices.listIterator();
            int nextRowIndex = rowIndexIter.next();
            rowHeights.addLast(Float.valueOf(highestBox));
            ListIterator<Float> rowHeightIter = rowHeights.listIterator();
            float rowHeight = 0.0f;
            while (boxIter.hasNext()) {
                if (boxIter.nextIndex() == nextRowIndex) {
                    xpos = borderSpacing;
                    rowHeight = rowHeightIter.next();
                    nextRowIndex = rowIndexIter.next();
                }
                Group box = boxIter.next();
                box.setHeight(rowHeight);
                if (boxIter.nextIndex() == nextRowIndex) {
                    float newWidth = broadestRow - xpos - borderSpacing;
                    float oldWidth = box.getWidth();
                    box.setWidth(newWidth);
                    box.translateInnerNodes((newWidth - oldWidth) / 2, 0.0f);
                }
                xpos += box.getWidth() + minSpacing;
            }
        }

        // return parent size
        return new KVector(broadestRow, totalHeight);
    }
    
    private float areaStdDev2(final List<Group> boxes, final float mean) {
        float variance = 0;
        for (Group box : boxes) {
            variance += Math.pow(box.area() - mean, 2);
        }
        float stddev = (float) Math.sqrt(variance / (boxes.size() - 1));
        return stddev;
    }

    // Now the packing modes ///////////////////////////////////////////////////////////////////////////

    /**
     * Start with large nodes and collect smaller nodes afterwards until the large node's 
     * half size is reached, then groups the small nodes.
     */
    private List<Group> mergeAndPlaceDec(final List<Group> groups, 
            final float objSpacing,
            final float minWidth, final float minHeight, 
            final boolean expandNodes,
            final float aspectRatio) {
        
        // sort in decreasing area
        // the code below makes relies on this
        Collections.sort(groups, (g1, g2) -> -Double.compare(g1.area(), g2.area()));
        
        Queue<Group> boxQueue = Lists.newLinkedList(groups);
        List<Group> toBePlaced = Lists.newArrayList();
        List<Group> maybeGroup = Lists.newArrayList();
        
        // current 'large' node and the combined area of 'smaller' nodes
        Group boxToBeat = null;         
        double collectedArea = 0;
        
        while (!boxQueue.isEmpty()) {
            Group box = boxQueue.poll();
            if ((boxToBeat == null)
                    // prevent 'box' from getting its own 'innerGroup'
                    || (boxToBeat.area() / 2 < box.area())) {
                boxToBeat = box;
                toBePlaced.add(box);
            } else {
                collectedArea += box.area();
                maybeGroup.add(box);
                
                if (maybeGroup.size() > 1 
                        && ((collectedArea > boxToBeat.area() / 2)
                        // make sure the remaining nodes in the queue get grouped
                        || boxQueue.isEmpty())) {
                    
                    // combine
                    Group innerGroup = new Group(maybeGroup);
                    // TODO there may be a better choice for the inner aspect ratio
                    float innerAspectRatio = boxToBeat.getWidth() / boxToBeat.getHeight();
                    KVector groupSize = placeInnerBoxes(innerGroup, objSpacing, 0, minWidth, minHeight, expandNodes,
                            innerAspectRatio);
                    innerGroup.size.reset().add(groupSize);
                    
                    boxToBeat = innerGroup;
                    toBePlaced.add(innerGroup);

                    collectedArea = 0;
                    maybeGroup.clear();
                } 
            }
        }
        
        // add remaining boxes
        toBePlaced.addAll(maybeGroup);
        return toBePlaced;
    }
    
    /**
     * Use a priority queue, once a large node is encountered, 
     * check if there's an (arbitrary) number of small nodes
     * that can be combined to come close to the larger node's size. 
     */
    private List<Group> mergeAndPlaceMixed(final List<Group> groups, 
            final float objSpacing,
            final float minWidth, final float minHeight, 
            final boolean expandNodes,
            final float aspectRatio) {

        double[] cumAreaArray = new double[groups.size()];
        
        PriorityQueue<Group> pq =
                new PriorityQueue<BoxLayoutProvider.Group>((g1, g2) -> Double.compare(g1.area(), g2.area()));
        pq.addAll(groups);
        
        int index = 0;
        List<Group> toBePlaced = Lists.newArrayList();
        
        while (!pq.isEmpty()) {
            // only take a look, we might not want to remove it
            Group box = pq.peek();
            
            // we need at least two boxes in the 'toBePlaced' list that can be merged 
            // thus 'index > 1'
            if (index > 1 && (box.area() / 2f > cumAreaArray[0])) {
                
                // TODO this should be a binary search 
                int anIndex = 0;
                // find the largest index
                // we can use at most 'toBePlaced.size()' groups 
                while (anIndex < toBePlaced.size() - 1 && box.area() / 2f > cumAreaArray[anIndex]) {
                    anIndex++;
                }
                
                // place the selected groups
                List<Group> select = toBePlaced.subList(0, anIndex + 1);
                Group innerGroup = new Group(select);
                // TODO there's probably a better strategy to select this aspect ratio
                float innerAspectRatio = box.getWidth() / box.getHeight();

                KVector groupSize =
                        placeInnerBoxes(innerGroup, objSpacing, 0, minWidth, minHeight, expandNodes, innerAspectRatio);
                innerGroup.size.reset().add(groupSize);
                
                // now start over
                pq.add(innerGroup);
                List<Group> remain = toBePlaced.subList(anIndex + 1, toBePlaced.size());
                pq.addAll(remain);
                toBePlaced.clear();
                index = 0;
                Arrays.fill(cumAreaArray, 0);
                
            } else {
                // ok, now remove the group from the queue
                pq.poll();
                if (index > 0) {
                    cumAreaArray[index] = cumAreaArray[index - 1];
                }
                cumAreaArray[index] += box.area();
                index++;
                toBePlaced.add(box);
            }
        }
        
        return toBePlaced;
    }
    
    /**
     * In increasing node area order: pick nodes until the next node's half size is larger than
     * the current cumulated area. Problematic for certain graphs, where 
     * the cumulation becomes quite large early. 
     */
    private List<Group> mergeAndPlaceInc(final List<Group> groups, 
            final float objSpacing, 
            final float minWidth, final float minHeight, 
            final boolean expandNodes,
            final float aspectRatio) {

        // sort increasingly 
        Collections.sort(groups, (g1, g2) -> Double.compare(g1.area(), g2.area()));
        
        ListIterator<Group> groupIterator = groups.listIterator();
        List<Group> toBePlaced = Lists.newArrayList();
        double commonArea = 0;

        while (groupIterator.hasNext()) {
            Group g = groupIterator.next();
            if (!toBePlaced.isEmpty() && g.area() > (commonArea * 2)) {
                // merge the current set of groups into a common group
                Group merged = new Group(toBePlaced);
                float innerAspectRatio = g.getWidth() / g.getHeight();
                
                // place the nodes in the group
                KVector groupSize = placeInnerBoxes(merged, objSpacing, 0, minWidth, minHeight, expandNodes,
                        innerAspectRatio);
                merged.size.reset().add(groupSize);

                // reset
                toBePlaced.clear();
                commonArea = 0;

                // add the merged group and the current group to
                // the active list 
                toBePlaced.add(merged);
                toBePlaced.add(g);
                commonArea = merged.area() + g.area();
            } else {
                // add the group to the active set
                toBePlaced.add(g);
                commonArea += g.area();
            }
        }
        
        return toBePlaced;
    }
    
    /**
     * Internal representation of a group. A group either wraps a single node, or holds a collection of groups.
     * In essence it delegates position/dimension changes to the inner-most groups (wrapping nodes).
     */
    private class Group {
        
        // SUPPRESS CHECKSTYLE NEXT 13 VisibilityModifier
        /** The node represented by this group, may be {@code null}. */
        KNode node;
        /** If {@code node != null}, this holds the node's {@link KShapeLayout}. */
        KShapeLayout nodeLayout;
        /** The set of groups wrapped in this group, may only be used if {@code node} is {@code null}. */
        List<Group> groups; 
        /** If this group represents other groups, {@code size} represents the bounding rectangle. */
        KVector size;

        /** Stores the bottom-most nodes of this group, used if {@link CoreOptions#EXPAND_NODES} is active .*/
        List<Group> bottom;
        /** Stores the right-most nodes of this group, used if {@link CoreOptions#EXPAND_NODES} is active .*/
        List<Group> right;
        
        Group(final KNode node) {
            this.node = node;
            this.nodeLayout = node.getData(KShapeLayout.class);
            nodeLayout.setPos(0, 0);
        }
        
        Group(final Iterable<Group> groups) {
            this.groups = Lists.newArrayList(groups);
            this.bottom = Lists.newArrayList();
            this.right = Lists.newArrayList();
            this.size = new KVector();
        }

        public float area() {
            return getWidth() * getHeight();
        }
        
        public float getWidth() {
            if (node != null) {
                return nodeLayout.getWidth();
            }
            return (float) size.x;
        }
        
        public float getHeight() {
            if (node != null) {
                return nodeLayout.getHeight();
            }
            return (float) size.y;
        }

        /**
         * Sets (increases) the width of this group. For node-wrapping groups this directly alters the
         * {@link KShapeLayout}'s with. For groups of groups this only makes sense in conjunction with
         * {@link CoreOptions#EXPAND_NODES}, effectively increasing all inner-most nodes (by recursion) of the
         * {@link #right} collection as to fit their parent group's width.
         */
        public void setWidth(final float w) {
            assert w > getWidth();
            if (node != null) {
                nodeLayout.setWidth(w);
            } else {
                // increase width of the rightmost nodes
                float delta = w - getWidth();
                for (Group g : right) {
                    g.setWidth(g.getWidth() + delta);
                }
            }
        }
        
        /**
         * Sets (increases) the height of this group. See {@link #setWidth(float)} for further details.
         */
        public void setHeight(final float h) {
            assert h > getHeight();
            if (node != null) {
                nodeLayout.setHeight(h);
            } else {
                // increase bottom most groups
                float delta = h - getHeight();
                for (Group g : bottom) {
                    g.setHeight(g.getHeight() + delta);
                }
            }
        }
     
        /**
         * For groups wrapping nodes, the passed {@code x} and {@code y} are added to the node's {@link KShapeLayout}'s
         * position. For group-wrapping groups, the method is called recursively. 
         * 
         * As opposed to {@link #translateInnerNodes(float, float)} this method alters the position of a wrapped node's
         * {@link KShapeLayout}'s position.
         */
        public void translate(final float x, final float y) {
            if (node != null) {
                nodeLayout.setXpos(nodeLayout.getXpos() + x);
                nodeLayout.setYpos(nodeLayout.getYpos() + y);
            } else {
                for (Group g : groups) {
                    g.translate(x, y);
                }
            }
        }

        /**
         * Translates the nested nodes of wrapped hierarchical nodes.
         * 
         * As opposed to {@link #translate(float, float)} this method translates the nested nodes of a hierarchical
         * node. See also {@link ElkUtil#translate(KNode, float, float)}.
         */
        public void translateInnerNodes(final float x, final float y) {
            if (node != null) {
                ElkUtil.translate(node, x, y);
            } else {
                for (Group g : groups) {
                    g.translateInnerNodes(x, y);
                }
            }
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            if (node != null) {
                return node.toString();
            } else {
                return groups.toString();
            }
        }
    }
    
}
