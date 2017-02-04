---
layout: page
title: No Layout
type: option
---
## No Layout

----|----
**Type:** | programmatic
**Identifier:** | org.eclipse.elk.noLayout
**Meta Data Provider:** | core.options.CoreOptions
**Value Type:** | `boolean`
**Default Value:** | `false` (as defined in org.eclipse.elk)
**Applies To:** | nodes, edges, ports, labels
**Legacy Id:** | de.cau.cs.kieler.noLayout

### Description

No layout is done for the associated element. This is used to mark parts of a diagram to avoid their inclusion in the layout graph, or to mark parts of the layout graph to prevent layout engines from processing them. If you wish to exclude the contents of a compound node from automatic layout, while the node itself is still considered on its own layer, use the 'Fixed Layout' algorithm for that node.
