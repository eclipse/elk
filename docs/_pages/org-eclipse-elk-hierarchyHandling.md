---
layout: page
title: Hierarchy Handling
type: option
---
## Hierarchy Handling

----|----
**Type:** | advanced
**Identifier:** | org.eclipse.elk.hierarchyHandling
**Meta Data Provider:** | core.options.CoreOptions
**Value Type:** | `org.eclipse.elk.core.options.HierarchyHandling` (Enum)
**Possible Values:** | `INCLUDE_CHILDREN`<br>`INHERIT`<br>`SEPARATE_CHILDREN`
**Default Value:** | `HierarchyHandling.INHERIT` (as defined in org.eclipse.elk)
**Applies To:** | parents, nodes
**Legacy Id:** | de.cau.cs.kieler.hierarchyHandling

### Description

If this option is set to SEPARATE_CHILDREN, each hierarchy level of the graph is processed independently, possibly by different layout algorithms, beginning with the lowest level. If it is set to INCLUDE_CHILDREN, the algorithm is responsible to process all hierarchy levels that are contained in the associated parent node. If the root node is set to inherit (or not set at all), the default behavior is SEPARATE_CHILDREN.
