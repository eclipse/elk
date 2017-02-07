---
layout: page
title: Twopi
type: algorithm
---
## Twopi

![](images/org-eclipse-elk-graphviz-twopi_preview_twopi.png)
**Identifier:** org.eclipse.elk.graphviz.twopi
**Meta Data Provider:** layouter.GraphvizMetaDataProvider

### Description

Radial layouts, after Wills '97. The nodes are placed on concentric circles depending on their distance from a given root node. The algorithm is designed to handle not only small graphs, but also very large ones.

## Category: Tree

Specialized layout methods for trees, i.e. acyclic graphs. The regular structure of graphs that have no undirected cycles can be emphasized using an algorithm of this type.

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
[Edge Label Spacing](org-eclipse-elk-spacing-edgeLabel) | `double` | `5` | org&#8203;.eclipse&#8203;.elk&#8203;.spacing&#8203;.edgeLabel
[Edge Routing](org-eclipse-elk-edgeRouting) | `EdgeRouting` | `EdgeRouting.SPLINES` | org&#8203;.eclipse&#8203;.elk&#8203;.edgeRouting
[Label Angle](org-eclipse-elk-graphviz-labelAngle) | `double` | `-25` | org&#8203;.eclipse&#8203;.elk&#8203;.graphviz&#8203;.labelAngle
[Label Distance](org-eclipse-elk-graphviz-labelDistance) | `double` | `1` | org&#8203;.eclipse&#8203;.elk&#8203;.graphviz&#8203;.labelDistance
[Node Size Constraints](org-eclipse-elk-nodeSize-constraints) | `EnumSet<SizeConstraint>` | `EnumSet.noneOf(SizeConstraint)` | org&#8203;.eclipse&#8203;.elk&#8203;.nodeSize&#8203;.constraints
[Node Size Options](org-eclipse-elk-nodeSize-options) | `EnumSet<SizeOptions>` | `EnumSet.of(SizeOptions.DEFAULT_MINIMUM_SIZE)` | org&#8203;.eclipse&#8203;.elk&#8203;.nodeSize&#8203;.options
[Node Spacing](org-eclipse-elk-spacing-nodeNode) | `double` | `60` | org&#8203;.eclipse&#8203;.elk&#8203;.spacing&#8203;.nodeNode
[Overlap Removal](org-eclipse-elk-graphviz-overlapMode) | `OverlapMode` | `OverlapMode.PRISM` | org&#8203;.eclipse&#8203;.elk&#8203;.graphviz&#8203;.overlapMode
[Padding](org-eclipse-elk-padding) | `ElkPadding` | `new ElkPadding(10)` | org&#8203;.eclipse&#8203;.elk&#8203;.padding

