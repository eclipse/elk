---
layout: page
title: Hierarchy Handling (Dot)
type: option
---
## Hierarchy Handling (Dot)

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

Determines whether the descendants should be layouted separately or together with their parents. If the root node is set to inherit (or not set at all), the option is assumed as SEPARATE_CHILDREN.

## Additional Documentation

If activated, the whole hierarchical graph is passed to dot as a whole. Note however that dot performs a 'compound' layout where it somewhat flattens the hierarchy and performs a layout on the flattened graph. As a consequence, padding information of hierarchical child nodes is discarded.
