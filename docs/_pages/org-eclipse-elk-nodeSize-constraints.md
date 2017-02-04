---
layout: page
title: Node Size Constraints
type: option
---
## Node Size Constraints

----|----
**Identifier:** | org.eclipse.elk.nodeSize.constraints
**Meta Data Provider:** | core.options.CoreOptions
**Value Type:** | `java.util.EnumSet<org.eclipse.elk.core.options.SizeConstraint>`
**Possible Values:** | `MINIMUM_SIZE`<br>`NODE_LABELS`<br>`PORTS`<br>`PORT_LABELS`
**Default Value:** | `EnumSet.noneOf(SizeConstraint)` (as defined in org.eclipse.elk)
**Applies To:** | nodes
**Containing Groups:** | [nodeSize](org-eclipse-elk-nodeSize)

### Description

Constraints for determining node sizes. Each member of the set specifies something that should be taken into account when calculating node sizes. The empty set corresponds to node sizes being fixed.
