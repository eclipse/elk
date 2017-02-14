---
layout: page
title: Merge Edges
type: option
---
## Merge Edges

----|----
**Type:** | advanced
**Identifier:** | org.eclipse.elk.layered.mergeEdges
**Meta Data Provider:** | options.LayeredMetaDataProvider
**Value Type:** | `boolean`
**Default Value:** | `false` (as defined in org.eclipse.elk.layered)
**Applies To:** | parents
**Legacy Id:** | de.cau.cs.kieler.klay.layered.mergeEdges

### Description

Edges that have no ports are merged so they touch the connected nodes at the same points. When this option is disabled, one port is created for each edge directly connected to a node. When it is enabled, all such incoming edges share an input port, and all outgoing edges share an output port.
