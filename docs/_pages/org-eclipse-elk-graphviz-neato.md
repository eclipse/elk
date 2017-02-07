---
layout: page
title: Neato
type: algorithm
---
## Neato

![](images/org-eclipse-elk-graphviz-neato_preview_neato.png)
**Identifier:** org.eclipse.elk.graphviz.neato
**Meta Data Provider:** layouter.GraphvizMetaDataProvider

### Description

Spring model layouts. Neato attempts to minimize a global energy function, which is equivalent to statistical multi-dimensional scaling. The solution is achieved using stress majorization, though the older Kamada-Kawai algorithm, using steepest descent, is also available.

## Category: Force

Layout algorithms that follow physical analogies by simulating a system of attractive and repulsive forces. The first successful method of this kind was proposed by Eades in 1984.

## Supported Graph Features

Name | Description
----|----
Self Loops | Edges connecting a node with itself.
Multi Edges | Multiple edges with the same source and target node.
Edge Labels | Labels that are associated with edges.

## Supported Options

Option | Type | Default Value | Identifier
----|----|----|----
[Adapt Port Positions](org-eclipse-elk-graphviz-adaptPortPositions) | `boolean` | `true` | org&#8203;.eclipse&#8203;.elk&#8203;.graphviz&#8203;.adaptPortPositions
[Concentrate Edges](org-eclipse-elk-graphviz-concentrate) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.graphviz&#8203;.concentrate
[Debug Mode](org-eclipse-elk-debugMode) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.debugMode
[Distance Model](org-eclipse-elk-graphviz-neatoModel) | `NeatoModel` | `NeatoModel.SHORTPATH` | org&#8203;.eclipse&#8203;.elk&#8203;.graphviz&#8203;.neatoModel
[Edge Label Spacing](org-eclipse-elk-spacing-edgeLabel) | `double` | `5` | org&#8203;.eclipse&#8203;.elk&#8203;.spacing&#8203;.edgeLabel
[Edge Routing](org-eclipse-elk-edgeRouting) | `EdgeRouting` | `EdgeRouting.SPLINES` | org&#8203;.eclipse&#8203;.elk&#8203;.edgeRouting
[Epsilon](org-eclipse-elk-graphviz-epsilon) | `double` | `0.0001f` | org&#8203;.eclipse&#8203;.elk&#8203;.graphviz&#8203;.epsilon
[Interactive](org-eclipse-elk-interactive) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.interactive
[Label Angle](org-eclipse-elk-graphviz-labelAngle) | `double` | `-25` | org&#8203;.eclipse&#8203;.elk&#8203;.graphviz&#8203;.labelAngle
[Label Distance](org-eclipse-elk-graphviz-labelDistance) | `double` | `1` | org&#8203;.eclipse&#8203;.elk&#8203;.graphviz&#8203;.labelDistance
[Max. Iterations](org-eclipse-elk-graphviz-maxiter) | `int` | `200` | org&#8203;.eclipse&#8203;.elk&#8203;.graphviz&#8203;.maxiter
[Node Size Constraints](org-eclipse-elk-nodeSize-constraints) | `EnumSet<SizeConstraint>` | `EnumSet.noneOf(SizeConstraint)` | org&#8203;.eclipse&#8203;.elk&#8203;.nodeSize&#8203;.constraints
[Node Size Options](org-eclipse-elk-nodeSize-options) | `EnumSet<SizeOptions>` | `EnumSet.of(SizeOptions.DEFAULT_MINIMUM_SIZE)` | org&#8203;.eclipse&#8203;.elk&#8203;.nodeSize&#8203;.options
[Node Spacing](org-eclipse-elk-spacing-nodeNode) | `double` | `40` | org&#8203;.eclipse&#8203;.elk&#8203;.spacing&#8203;.nodeNode
[Overlap Removal](org-eclipse-elk-graphviz-overlapMode) | `OverlapMode` | `OverlapMode.PRISM` | org&#8203;.eclipse&#8203;.elk&#8203;.graphviz&#8203;.overlapMode
[Padding](org-eclipse-elk-padding) | `ElkPadding` | `new ElkPadding(10)` | org&#8203;.eclipse&#8203;.elk&#8203;.padding
[Randomization Seed](org-eclipse-elk-randomSeed) | `int` | `1` | org&#8203;.eclipse&#8203;.elk&#8203;.randomSeed
[Separate Connected Components](org-eclipse-elk-separateConnectedComponents) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.separateConnectedComponents

