---
title: "Layered: Constraining the Model (September 2022)"
menu:
  main:
    identifier: "22-09-30-constraining-the-model"
    parent: "2022"
    weight: 20
---

Since it is often desired to constrain the layout in a specific way to achieve a desired layout or to increase layout stability, this post should summarize all current (ELK 0.8.1) and future (planned for 0.9.0) ways to do that:

## Interactive Constraints

Interactive layout works by having previously defined positions and by using the following layout options for the layered algorithm:
```
cycleBreaking.strategy: INTERACTIVE
layering.strategy: INTERACTIVE
crossingMinimization.semiInteractive: true
separateConnectedComponents: false
```

There are two different ways to get positions for your nodes.

### Pseudo Positions

Add pseudo positions to your nodes as seen [here](https://rtsys.informatik.uni-kiel.de/elklive/examples.html?e=user-hints%2Finteractive-constraints%2FinteractiveLayeredLayout_circle_pseudo_positions).

```
layout [position: 100, 0]
```

### Constraints

[KLighD](https://github.com/kieler/KLighD) configures a second layout run with the interactive strategies mentioned above if the `interactiveLayout` property is set on the root.
For the second final run the `layerChoiceConstraint`s and `positionsChoiceConstraint`s are evaluated.
In the future relative constraint will also be supported: `crossingMinimization.inLayerPredOf`, `crossingMinimization.inLayerSuccOf`.

These constraint are evaluated and translated in pseudo positions.

This solution requires more than ELK can deliver, ELK only specifies the corresponding constraints. Evaluating and enforcing them is not part of ELK.

## Partition

By activating partition cycle breaking as well as layer assignment can be influenced, as seen in [this example](https://rtsys.informatik.uni-kiel.de/elklive/examples.html?e=user-hints%2Flayered%2Fpartitioning).
`partitioning.activate: true` is set. By setting the `partitioning.partition` layers are established.

Also consider to have a look at the following example that showcase interactive constraints by pseudo positions and partition:

[Horizontal Order](https://rtsys.informatik.uni-kiel.de/elklive/examples.html?e=user-hints%2Flayered%2FhorizontalOrder)
[Vertical Order](https://rtsys.informatik.uni-kiel.de/elklive/examples.html?e=user-hints%2Flayered%2FverticalOrder)

## Port Constraints

The order of ports can be influenced in by setting `portConstraints` as specified [here](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-portConstraints.html).

## Model Order

### Cycle Breaking

ELK 0.8.1 introduced the `GREEDY_MODEL_ORDER` and the `MODEL_ORDER` [cycleBreaking.strategy](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-layered-cycleBreaking-strategy.html).
The greedy model order cycle breaker optimizes for backward edges but refers to the textual ordering of the nodes if no unique alternative exists.
The model order cycle breaker only refers to the textual ordering and reverses edges that go against it.
Both cycle breakers support [`layerConstraint`s](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-layered-layering-layerConstraint.html)

### Layer Assignment (ELK 0.9.0)

ELK 0.9.0 will introduce model order layer assignment by node promotion.
TODO

### Crossing Minimization

TODO