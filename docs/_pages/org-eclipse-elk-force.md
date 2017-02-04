---
layout: page
title: ELK Force
type: algorithm
---
## ELK Force

![](images/org-eclipse-elk-force_preview_force.png)
**Identifier:** org.eclipse.elk.force
**Meta Data Provider:** properties.ForceMetaDataProvider

### Description

Force-based algorithm provided by the Eclipse Layout Kernel. Implements methods that follow physical analogies by simulating forces that move the nodes into a balanced distribution. Currently the original Eades model and the Fruchterman - Reingold model are supported.

## Category: Force

Layout algorithms that follow physical analogies by simulating a system of attractive and repulsive forces. The first successful method of this kind was proposed by Eades in 1984.

## Supported Graph Features

Name | Description
----|----
Multi Edges | Multiple edges with the same source and target node.
Edge Labels | Labels that are associated with edges.

## Supported Options

Option | Type | Default Value | Identifier
----|----|----|----
[Aspect Ratio](org-eclipse-elk-aspectRatio) | `double` | `1.6f` | org&#8203;.eclipse&#8203;.elk&#8203;.aspectRatio
[Eades Repulsion](org-eclipse-elk-force-repulsion) | `double` | `5.0` | org&#8203;.eclipse&#8203;.elk&#8203;.force&#8203;.repulsion
[Edge Label Spacing](org-eclipse-elk-spacing-edgeLabel) | `double` | `5` | org&#8203;.eclipse&#8203;.elk&#8203;.spacing&#8203;.edgeLabel
[Force Model](org-eclipse-elk-force-model) | `ForceModelStrategy` | `ForceModelStrategy.FRUCHTERMAN_REINGOLD` | org&#8203;.eclipse&#8203;.elk&#8203;.force&#8203;.model
[FR Temperature](org-eclipse-elk-force-temperature) | `double` | `0.001` | org&#8203;.eclipse&#8203;.elk&#8203;.force&#8203;.temperature
[Interactive](org-eclipse-elk-interactive) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.interactive
[Iterations](org-eclipse-elk-force-iterations) | `int` | `300` | org&#8203;.eclipse&#8203;.elk&#8203;.force&#8203;.iterations
[Node Spacing](org-eclipse-elk-spacing-nodeNode) | `double` | `80` | org&#8203;.eclipse&#8203;.elk&#8203;.spacing&#8203;.nodeNode
[Padding](org-eclipse-elk-padding) | `ElkPadding` | `new ElkPadding(50)` | org&#8203;.eclipse&#8203;.elk&#8203;.padding
[Port Constraints](org-eclipse-elk-portConstraints) | `PortConstraints` | `PortConstraints.UNDEFINED` | org&#8203;.eclipse&#8203;.elk&#8203;.portConstraints
[Priority](org-eclipse-elk-priority) | `int` | `1` | org&#8203;.eclipse&#8203;.elk&#8203;.priority
[Randomization Seed](org-eclipse-elk-randomSeed) | `int` | `1` | org&#8203;.eclipse&#8203;.elk&#8203;.randomSeed
[Repulsive Power](org-eclipse-elk-force-repulsivePower) | `int` | `0` | org&#8203;.eclipse&#8203;.elk&#8203;.force&#8203;.repulsivePower
[Separate Connected Components](org-eclipse-elk-separateConnectedComponents) | `boolean` | `true` | org&#8203;.eclipse&#8203;.elk&#8203;.separateConnectedComponents

