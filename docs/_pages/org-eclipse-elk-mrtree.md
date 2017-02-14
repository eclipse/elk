---
layout: page
title: ELK Mr. Tree
type: algorithm
---
## ELK Mr. Tree

![](images/org-eclipse-elk-mrtree_preview_tree.png)
**Identifier:** org.eclipse.elk.mrtree
**Meta Data Provider:** options.MrTreeMetaDataProvider

### Description

Tree-based algorithm provided by the Eclipse Layout Kernel. Computes a spanning tree of the input graph and arranges all nodes according to the resulting parent-children hierarchy. I pity the fool who doesn't use Mr. Tree Layout.

## Category: Tree

Specialized layout methods for trees, i.e. acyclic graphs. The regular structure of graphs that have no undirected cycles can be emphasized using an algorithm of this type.

## Supported Graph Features

Name | Description
----|----
Disconnected | Multiple connected components.

## Supported Options

Option | Type | Default Value | Identifier
----|----|----|----
[Aspect Ratio](org-eclipse-elk-aspectRatio) | `double` | `1.6f` | org&#8203;.eclipse&#8203;.elk&#8203;.aspectRatio
[Debug Mode](org-eclipse-elk-debugMode) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.debugMode
[Direction](org-eclipse-elk-direction) | `Direction` | `Direction.DOWN` | org&#8203;.eclipse&#8203;.elk&#8203;.direction
[Node Spacing](org-eclipse-elk-spacing-nodeNode) | `double` | `20` | org&#8203;.eclipse&#8203;.elk&#8203;.spacing&#8203;.nodeNode
[Padding](org-eclipse-elk-padding) | `ElkPadding` | `new ElkPadding(20)` | org&#8203;.eclipse&#8203;.elk&#8203;.padding
[Priority](org-eclipse-elk-priority) | `int` | `1` | org&#8203;.eclipse&#8203;.elk&#8203;.priority
[Search Order](org-eclipse-elk-mrtree-searchOrder) | `TreeifyingOrder` | `TreeifyingOrder.DFS` | org&#8203;.eclipse&#8203;.elk&#8203;.mrtree&#8203;.searchOrder
[Separate Connected Components](org-eclipse-elk-separateConnectedComponents) | `boolean` | `true` | org&#8203;.eclipse&#8203;.elk&#8203;.separateConnectedComponents
[Weighting of Nodes](org-eclipse-elk-mrtree-weighting) | `OrderWeighting` | `OrderWeighting.DESCENDANTS` | org&#8203;.eclipse&#8203;.elk&#8203;.mrtree&#8203;.weighting

