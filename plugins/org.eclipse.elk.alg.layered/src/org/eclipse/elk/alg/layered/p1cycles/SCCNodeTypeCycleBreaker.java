/*******************************************************************************
 * Copyright (c) 2023 Sasuk and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p1cycles;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.options.LayeredOptions;

/**
 * @author Sasuk
 *
 */
public class SCCNodeTypeCycleBreaker extends SCCModelOrderCycleBreaker {

    @Override
    public void findNodes(int offset) {
     // lowest model order only outgoing
      for (int i = 0; i < stronglyConnectedComponents.size(); i++) {
          if (stronglyConnectedComponents.get(i).size() <= 1) {
              continue;
          }
          LNode min = null;
          int modelOrderMin = Integer.MAX_VALUE;
          
          LNode max = null;
          int modelOrderMax = Integer.MIN_VALUE;
          for (LNode n : stronglyConnectedComponents.get(i)) {
              List<Integer> layermask = new LinkedList<Integer>();
              layermask.add(1);
              layermask.add(4);
              int groupID = n.getProperty(LayeredOptions.CONSIDER_MODEL_ORDER_GROUP_I_D);
              if (!layermask.contains(groupID)) {
                  continue;
              }
              if (min == null && max == null) {
                  min = n;
                  modelOrderMin = computeConstraintModelOrder(n,offset);
                  
                  max = n;
                  modelOrderMax = computeConstraintModelOrder(n,offset);
                  continue;
              }
              int modelOrderCurrent = computeConstraintModelOrder(n, offset);
              if (modelOrderMin > modelOrderCurrent) {
                  min = n;
                  modelOrderMin = modelOrderCurrent;
              }
              else if (modelOrderMax < modelOrderCurrent) {
                  max = n;
                  modelOrderMax = modelOrderCurrent;
              }
          }
          if (min != null) {
              if (min.getProperty(LayeredOptions.CONSIDER_MODEL_ORDER_GROUP_I_D) == 1) {
                  for (LEdge edge : min.getIncomingEdges()) {
                      if (stronglyConnectedComponents.get(i).contains(edge.getSource().getNode())) {
                          revEdges.add(edge);
                      }
                  }
              }
              else {
                  for (LEdge edge : max.getOutgoingEdges()) {
                      if (stronglyConnectedComponents.get(i).contains(edge.getSource().getNode())) {
                          revEdges.add(edge);
                      }
                  }
              }
          }
      }
    }
}
