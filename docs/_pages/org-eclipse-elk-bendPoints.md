---
layout: page
title: Bend Points
type: option
---
## Bend Points

----|----
**Type:** | programmatic
**Identifier:** | org.eclipse.elk.bendPoints
**Meta Data Provider:** | core.options.CoreOptions
**Value Type:** | `org.eclipse.elk.core.math.KVectorChain`
**Applies To:** | edges
**Legacy Id:** | de.cau.cs.kieler.bendPoints

### Description

A fixed list of bend points for the edge. This is used by the 'Fixed Layout' algorithm to specify a pre-defined routing for an edge. The vector chain must include the source point, any bend points, and the target point, so it must have at least two points.
