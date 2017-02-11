---
layout: page
title: Activate Inside Self Loops
type: option
---
## Activate Inside Self Loops

----|----
**Type:** | advanced
**Identifier:** | org.eclipse.elk.insideSelfLoops.activate
**Meta Data Provider:** | core.options.CoreOptions
**Value Type:** | `boolean`
**Default Value:** | `false` (as defined in org.eclipse.elk)
**Applies To:** | nodes
**Legacy Id:** | de.cau.cs.kieler.selfLoopInside
**Containing Group:** | [insideSelfLoops](org-eclipse-elk-insideSelfLoops)

### Description

Whether this node allows to route self loops inside of it instead of around it. If set to true, this will make the node a compound node if it isn't already, and will require the layout algorithm to support compound nodes with hierarchical ports.
