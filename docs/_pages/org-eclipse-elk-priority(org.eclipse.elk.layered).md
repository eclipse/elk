---
layout: page
title: Priority (ELK Layered)
type: option
---
## Priority (ELK Layered)

----|----
**Type:** | advanced
**Identifier:** | org.eclipse.elk.priority
**Meta Data Provider:** | core.options.CoreOptions
**Value Type:** | `int`
**Applies To:** | nodes, edges
**Legacy Id:** | de.cau.cs.kieler.priority

### Description

Defines the priority of an object; its meaning depends on the specific layout algorithm and the context where it is used.

## Additional Documentation

Used by the 'simple row graph placer' to decide which connected components to place first. A component's priority is the sum of the node priorities.
