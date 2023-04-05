---
title: "Layered: Constraining the Model"
menu:
  main:
    identifier: "23-01-09-constraining-the-model"
    parent: "2023"
    weight: 10
---

_By Sören Domrös, January 9, 2023_

Since it is often desired to constrain the layout in a specific way to achieve a desired layout or to increase layout stability, this post should summarize all current (ELK 0.8.1) and future (planned for 0.9.0) ways to do that. The following will focus primarily on constraining the node order. If you wish to constrain the ports have a look at the [`portConstraints`](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-portConstraints.html) property and [this](https://rtsys.informatik.uni-kiel.de/elklive/examples.html?e=general%2Fspacing%2Fports) example.

## Layer Constraints

The [`layerConstraint`](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-layered-layering-layerConstraint.html) property allows constraining a node to the first or last layer. This option is quite basic and should be compatible with the Partition and model order cycle breaking. Other approaches might produce unexpected results when used together.

## Interactive Constraints

Interactive layout works by having previously defined positions and by using the following layout options for the layered algorithm:
```
cycleBreaking.strategy: INTERACTIVE
layering.strategy: INTERACTIVE
crossingMinimization.semiInteractive: true
separateConnectedComponents: false
```

There are two different ways to get positions for your nodes.

#### Using Pseudo Positions

Add pseudo positions to your nodes as seen [here](https://rtsys.informatik.uni-kiel.de/elklive/examples.html?e=user-hints%2Finteractive-constraints%2FinteractiveLayeredLayout_circle_pseudo_positions).

```
layout [position: 100, 0]
```

#### Using Constraints

[KLighD](https://github.com/kieler/KLighD) configures a second layout run with the interactive strategies mentioned above if the `interactiveLayout` property is set on the root.
For the second final run the `layerChoiceConstraint`s and `positionsChoiceConstraint`s are evaluated.
In the future relative constraint will also be supported: `crossingMinimization.inLayerPredOf`, `crossingMinimization.inLayerSuccOf`.

These constraint are evaluated and translated in pseudo positions.

Examples with constraints and pseudo positions can be found [here](https://rtsys.informatik.uni-kiel.de/elklive/examples.html?e=user-hints%2Finteractive-constraints%2FinteractiveLayeredLayout_circle_pseudo_positions) and in the example in the same category. Note that the constraints are not correctly applied since no second layout run is configured.

This solution requires more than ELK can deliver, ELK only specifies the corresponding constraints. Evaluating and enforcing them is not part of ELK.

## Partition

By activating partition cycle breaking as well as layer assignment can be influenced, as seen in [this example](https://rtsys.informatik.uni-kiel.de/elklive/examples.html?e=user-hints%2Flayered%2Fpartitioning).
`partitioning.activate: true` is set. By setting the `partitioning.partition` layers are established.

Also consider to have a look at the following example that showcase interactive constraints by pseudo positions and partition:

[Horizontal Order](https://rtsys.informatik.uni-kiel.de/elklive/examples.html?e=user-hints%2Flayered%2FhorizontalOrder)
[Vertical Order](https://rtsys.informatik.uni-kiel.de/elklive/examples.html?e=user-hints%2Flayered%2FverticalOrder)

## Model Order

In the following we assume that all states and edges are ordered in their (textual) model as it is indicated by their labels.

#### Cycle Breaking

ELK 0.8.1 introduced the `GREEDY_MODEL_ORDER` and the `MODEL_ORDER` [cycleBreaking.strategy](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-layered-cycleBreaking-strategy.html).
The greedy model order cycle breaker optimizes for backward edges but refers to the textual ordering of the nodes if no unique alternative exists.
The model order cycle breaker only refers to the textual ordering and reverses edges that go against it.
Both cycle breakers support [`layerConstraint`s](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-layered-layering-layerConstraint.html) such as `FIRST` and `LAST`.

The difference between the `GREEDY` cycle breaker, the two model order cycle breakers mentioned above can be seen in the following.

{{< image src="cb-greedy.svg" alt="Graph drawn with the greedy cycle breaker." >}}

The greedy cycle breaker. optimizes the number of backward edges and randomly makes a decision if no clear better alternative exists (such as for the edges between `n1` and `n2`).

{{< image src="cb-greedyMO.svg" alt="Graph drawn with the greedy model order cycle breaker." >}}

The greedy model order cycle breaker optimizes the number of backward edges and uses the model order as a tie-breaker. It reverse the edges from `n2` to `n1` since `n2` is "after" `n1` in the model.

{{< image src="cb-MO.svg" alt="Graph drawn with the model order cycle breaker." >}}

The model order cycle breaker only uses the model order and reverses all edges that go against it such as the edge from `n3` to `n2` and the edges from `n2` to `n1`.

Examples can be found [here](https://rtsys.informatik.uni-kiel.de/elklive/examples.html?e=user-hints%2Fmodel-order%2FmodelOrderCycleBreaking).

#### Layer Assignment (ELK 0.9.0)

ELK 0.9.0 will introduce model order layer assignment by node promotion.
Setting `nodePromotion` to `MODEL_ORDER_LEFT_TO_RIGHT` together with `LONGEST_PATH_SOURCE` (and model order cycle breaking) allows to specify the layer of each node by ordering all nodes in a breath-first order.

{{< image src="la.svg" alt="Intial layer assignment." >}}

The initial layer assignment places `n4` in the layer before `n3`, which violates their ordering.

In the following `n4` is moved in the correct layer using node promotion by model order.

{{< image src="la-correct.svg" alt="Intial layer assignment." >}}

#### Crossing Minimization (partly ELK 0.9.0)

ELK 0.8.1 already introduced model order as a tie-breaker with the [`considerModelOrder.strategy`](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-layered-considerModelOrder-strategy.html) property, which allows to sort the nodes and ports before crossing minimization by the edge order (`PREFER_EDGES`), the node and the edge order (`NODES_AND_EDGES`), and the nodes (will be added in 0.9.0, `PREFER_NODES`).

The difference of the three strategies can be seen in the following drawings that were created by disabling crossing minimization:

{{< image src="cm-preferEdges.svg" alt="Graph drawn with the preferEdges pre-crossing minimization sorting strategy." >}}

The `PREFER_EDGES` pre-crossing minimization sorting strategy makes sure that the edge order is preserved before crossing minimization starts (which might scramble it again). The node order is only used for nodes without incoming edges.

{{< image src="cm-nodesAndEdges.svg" alt="Graph drawn with the nodesAndEdges pre-crossing minimization sorting strategy." >}}

The `NODES_AND_EDGES` pre-crossing minimization sorting strategy makes sure that the edge and node order are preserved before crossing minimization starts (which might scramble it again). Here this results in additional crossings since they are conflicting.

{{< image src="cm-preferNodes.svg" alt="Graph drawn with the preferNodes pre-crossing minimization sorting strategy." >}}

The `PREFER_NODES` pre-crossing minimization sorting strategy makes sure that the node order are preserved before crossing minimization starts (which might scramble it again). The order of the edges is also determined by the node order of their targets.

This allows the algorithm to consider the ordered graph during crossing minimization. Should it be crossing minimal, the ordering will always be used as the final solution.

If the initial ordering is not crossing minimal, the following properties allow to weight the ordering violations against the edge crossing to use model order as a tie-breaker: [`crossingCounterNodeInfluence`](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-layered-considerModelOrder-crossingCounterNodeInfluence.html), [`crossingCounterPortInfluence`](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-layered-considerModelOrder-crossingCounterNodeInfluence.html). We advice setting these to a value below zero to ensure that edge crossing are still more important than the ordering. E.g. a value of 0.1 might be optimal for some use case, which means that 10 order violations are as important as one edge crossing.

This allows to select the best ordered crossing minimal solution. Note that this is still limited by the number of random tries (see [`thoroughness`](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-layered-thoroughness.html)), therefore, the optimal solution will not always be found if the thoroughness is not increased.

If one wants to only constrain the node order, this can be achieved by setting [`forceNodeModelOrder`](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-layered-crossingMinimization-forceNodeModelOrder.html).

If the crossing minimization strategy is set to `NONE` and the `greedySwitch.type` is set to `OFF` the ordering created by the model order sorting strategy will not change and the model order is enforced.

An overview of model order as the tie-breaker, enforced node order, and no crossing minimization strategies can be seen in the following:

{{< image src="cm-tiebreaker.svg" alt="Model order crossing minimization used as a tie-breaker." >}}

{{< image src="cm-enforceNodes.svg" alt="Model order crossing minimization used with enforced node order." >}}

{{< image src="cm-enforceMO.svg" alt="Model order crossing minimization used with enforced model order by disabling crossing minimization.." >}}

Examples for different model order crossing minimization strategies can be found [here](https://rtsys.informatik.uni-kiel.de/elklive/examples.html?e=user-hints%2Fmodel-order%2FmodelOrderCrossingMinimization).

## Component Ordering (partly ELK 0.9.0)

If the graph consist of several separate connected component and this `separateConnectedComponent` property is set to `true`. There are two different ways of ordering them.

- The separate components are ordered by size (default)
- The separate components are ordered by the sum of their [`priority`](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-priority.html) property
- The separate components are ordered by model order using their minimal element (see [`considerModelOrder.components`](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-layered-considerModelOrder-components.html)). The preferred strategy (`MODEL_ORDER`) will be introduced with the 0.9.0 release delivers a compact ordering of the separate components that considers to the model order.



I hope this helps to answer several reoccurring questions regarding ordering. 



