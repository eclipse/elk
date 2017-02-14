---
layout: page
title: Favor Straight Edges Over Balancing
type: option
---
## Favor Straight Edges Over Balancing

----|----
**Identifier:** | org.eclipse.elk.layered.nodePlacement.favorStraightEdges
**Meta Data Provider:** | options.LayeredMetaDataProvider
**Value Type:** | `boolean`
**Applies To:** | parents
**Dependencies:** | [org.eclipse.elk.layered.nodePlacement.strategy](org-eclipse-elk-layered-nodePlacement-strategy)
**Containing Group:** | [nodePlacement](org-eclipse-elk-layered-nodePlacement)

### Description

Favor straight edges over a balanced node placement. The default behavior is determined automatically based on the used 'edgeRouting'. For an orthogonal style it is set to true, for all other styles to false.
