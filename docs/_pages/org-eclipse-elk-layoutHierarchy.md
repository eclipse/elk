---
layout: page
title: Layout Hierarchy
type: option
---
## ~~Layout Hierarchy~~

----|----
**Type:** | deprecated
**Identifier:** | org.eclipse.elk.layoutHierarchy
**Meta Data Provider:** | core.options.CoreOptions
**Value Type:** | `boolean`
**Default Value:** | `false` (as defined in org.eclipse.elk)
**Applies To:** | parents
**Legacy Id:** | de.cau.cs.kieler.layoutHierarchy

### Description

Whether the whole hierarchy shall be layouted. If this option is not set, each hierarchy level of the graph is processed independently, possibly by different layout algorithms, beginning with the lowest level. If it is set, the algorithm is responsible to process all hierarchy levels that are contained in the associated parent node.
