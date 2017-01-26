---
layout: page
title: Margins
type: option
---
## Margins

----|----
**Type:** | programmatic
**Identifier:** | org.eclipse.elk.margins
**Meta Data Provider:** | core.options.CoreOptions
**Value Type:** | `org.eclipse.elk.core.math.ElkMargin`
**Default Value:** | `new ElkMargin()` (as defined in org.eclipse.elk)
**Applies To:** | nodes
**Legacy Id:** | de.cau.cs.kieler.margins

### Description

Margins define additional space around the actual bounds of a graph element. For instance, ports or labels being placed on the outside of a node's border might introduce such a margin. The margin is used to guarantee non-overlap of other graph elements with those ports or labels.
