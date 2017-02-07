---
layout: page
title: FDP
type: algorithm
---
## FDP

![](images/org-eclipse-elk-graphviz-fdp_preview_fdp.png)
**Identifier:** org.eclipse.elk.graphviz.fdp
**Meta Data Provider:** layouter.GraphvizMetaDataProvider

### Description

Spring model layouts similar to those of Neato, but does this by reducing forces rather than working with energy. Fdp implements the Fruchterman-Reingold heuristic including a multigrid solver that handles larger graphs and clustered undirected graphs.

## Category: Force

Layout algorithms that follow physical analogies by simulating a system of attractive and repulsive forces. The first successful method of this kind was proposed by Eades in 1984.

## Supported Graph Features

Name | Description
----|----
Self Loops | Edges connecting a node with itself.
Multi Edges | Multiple edges with the same source and target node.
Edge Labels | Labels that are associated with edges.
Clusters | Edges that connect nodes from different clusters, but not the cluster parent nodes.

## Supported Options

Option | Type | Default Value | Identifier
----|----|----|----
[Adapt Port Positions](org-eclipse-elk-graphviz-adaptPortPositions) | `boolean` | `true` | org&#8203;.eclipse&#8203;.elk&#8203;.graphviz&#8203;.adaptPortPositions
[Concentrate Edges](org-eclipse-elk-graphviz-concentrate) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.graphviz&#8203;.concentrate
[Debug Mode](org-eclipse-elk-debugMode) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.debugMode
[Edge Label Spacing](org-eclipse-elk-spacing-edgeLabel) | `double` | `5` | org&#8203;.eclipse&#8203;.elk&#8203;.spacing&#8203;.edgeLabel
[Edge Routing](org-eclipse-elk-edgeRouting) | `EdgeRouting` | `EdgeRouting.SPLINES` | org&#8203;.eclipse&#8203;.elk&#8203;.edgeRouting
[Interactive](org-eclipse-elk-interactive) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.interactive
[Label Angle](org-eclipse-elk-graphviz-labelAngle) | `double` | `-25` | org&#8203;.eclipse&#8203;.elk&#8203;.graphviz&#8203;.labelAngle
[Label Distance](org-eclipse-elk-graphviz-labelDistance) | `double` | `1` | org&#8203;.eclipse&#8203;.elk&#8203;.graphviz&#8203;.labelDistance
[Max. Iterations](org-eclipse-elk-graphviz-maxiter) | `int` | `600` | org&#8203;.eclipse&#8203;.elk&#8203;.graphviz&#8203;.maxiter
[Node Size Constraints](org-eclipse-elk-nodeSize-constraints) | `EnumSet<SizeConstraint>` | `EnumSet.noneOf(SizeConstraint)` | org&#8203;.eclipse&#8203;.elk&#8203;.nodeSize&#8203;.constraints
[Node Size Options](org-eclipse-elk-nodeSize-options) | `EnumSet<SizeOptions>` | `EnumSet.of(SizeOptions.DEFAULT_MINIMUM_SIZE)` | org&#8203;.eclipse&#8203;.elk&#8203;.nodeSize&#8203;.options
[Node Spacing](org-eclipse-elk-spacing-nodeNode) | `double` | `40` | org&#8203;.eclipse&#8203;.elk&#8203;.spacing&#8203;.nodeNode
[Overlap Removal](org-eclipse-elk-graphviz-overlapMode) | `OverlapMode` | `OverlapMode.PRISM` | org&#8203;.eclipse&#8203;.elk&#8203;.graphviz&#8203;.overlapMode
[Padding](org-eclipse-elk-padding) | `ElkPadding` | `new ElkPadding(10)` | org&#8203;.eclipse&#8203;.elk&#8203;.padding
[Separate Connected Components](org-eclipse-elk-separateConnectedComponents) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.separateConnectedComponents

