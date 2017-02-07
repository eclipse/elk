---
layout: page
title: Fixed Layout
type: algorithm
---
## Fixed Layout

**Identifier:** org.eclipse.elk.fixed
**Meta Data Provider:** core.options.CoreOptions

### Description

Keeps the current layout as it is, without any automatic modification. Optional coordinates can be given for nodes and edge bend points.

## Supported Options

Option | Type | Default Value | Identifier
----|----|----|----
[Bend Points](org-eclipse-elk-bendPoints) | `KVectorChain` | `<not defined>` | org&#8203;.eclipse&#8203;.elk&#8203;.bendPoints
[Node Size Constraints](org-eclipse-elk-nodeSize-constraints) | `EnumSet<SizeConstraint>` | `EnumSet.noneOf(SizeConstraint)` | org&#8203;.eclipse&#8203;.elk&#8203;.nodeSize&#8203;.constraints
[Node Size Minimum](org-eclipse-elk-nodeSize-minimum) | `KVector` | `new KVector(0, 0)` | org&#8203;.eclipse&#8203;.elk&#8203;.nodeSize&#8203;.minimum
[Padding](org-eclipse-elk-padding) | `ElkPadding` | `new ElkPadding(15)` | org&#8203;.eclipse&#8203;.elk&#8203;.padding
[Position](org-eclipse-elk-position) | `KVector` | `<not defined>` | org&#8203;.eclipse&#8203;.elk&#8203;.position

