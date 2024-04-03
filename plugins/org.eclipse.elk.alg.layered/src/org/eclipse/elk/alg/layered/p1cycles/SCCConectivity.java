/*******************************************************************************
 * Copyright (c) 2024 Sasuk and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p1cycles;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.options.LayeredOptions;

import com.google.common.collect.Iterables;

/**
 * Based on the SCCModelOrderCycleBreaker. This finds the nodes with minimum and maximum model order and reverses the 
 * incoming nodes of the minimum, if its the in-degree is greater than the out-degree of the maximum node. Else it 
 * reverses the out-going edges of the maximum node.
 * @author Mwr
 *
 */
public class SCCConectivity extends SCCModelOrderCycleBreaker {

    @Override
    public void findNodes(int offset) {
      for (int i = 0; i < stronglyConnectedComponents.size(); i++) {
          if (stronglyConnectedComponents.get(i).size() <= 1) {
              continue;
          }
          LNode min = null;
          LNode max = null;
          int modelOrderMin = Integer.MAX_VALUE;
          int modelOrderMax = Integer.MIN_VALUE;
          for (LNode n : stronglyConnectedComponents.get(i)) {
              List<Integer> layermask = new LinkedList<Integer>();
              layermask.add(1);
              layermask.add(4);
              int groupID = n.getProperty(LayeredOptions.CONSIDER_MODEL_ORDER_GROUP_I_D);
              if (!layermask.contains(groupID)) {
                  continue;
              }
              if (min == null || max == null) {
                  min = n;
                  modelOrderMin = computeConstraintModelOrder(n,offset);
                  max = n;
                  modelOrderMax = modelOrderMin;
                  continue;
              }
              int modelOrderCurrent = computeConstraintModelOrder(n, offset);
              if (modelOrderMin > modelOrderCurrent) {
                  min = n;
                  modelOrderMin = modelOrderCurrent;
              }
              else if(modelOrderMax < modelOrderCurrent) {
                  max = n;
                  modelOrderMax = modelOrderCurrent;
              }
          }
          if (min != null && max != null) {
              if (Iterables.size(min.getIncomingEdges()) > 
                      Iterables.size(max.getOutgoingEdges())) {
                  for (LEdge edge : min.getIncomingEdges()) {
                      if (stronglyConnectedComponents.get(i).contains(edge.getSource().getNode())) {
                          revEdges.add(edge);
                      }
                 }
              } else {
                  for (LEdge edge : max.getOutgoingEdges()) {
                      if (stronglyConnectedComponents.get(i).contains(edge.getTarget().getNode())) {
                          revEdges.add(edge);
                      }
                  }
              }
          }
      }
    }
}