---
layout: page
title: Node Size Options
type: option
---
## Node Size Options

----|----
**Identifier:** | org.eclipse.elk.nodeSize.options
**Meta Data Provider:** | core.options.CoreOptions
**Value Type:** | `java.util.EnumSet<org.eclipse.elk.core.options.SizeOptions>`
**Possible Values:** | `COMPUTE_PADDING`<br>`DEFAULT_MINIMUM_SIZE`<br>`MINIMUM_SIZE_ACCOUNTS_FOR_PADDING`
**Default Value:** | `EnumSet.of(SizeOptions.DEFAULT_MINIMUM_SIZE)` (as defined in org.eclipse.elk)
**Applies To:** | nodes
**Containing Groups:** | [nodeSize](org-eclipse-elk-nodeSize)

### Description

Options modifying the behavior of the size constraints set on a node. Each member of the set specifies something that should be taken into account when calculating node sizes. The empty set corresponds to no further modifications.
