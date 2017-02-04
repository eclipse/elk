---
layout: page
title: Edge Routing
type: option
---
## Edge Routing

----|----
**Identifier:** | org.eclipse.elk.edgeRouting
**Meta Data Provider:** | core.options.CoreOptions
**Value Type:** | `org.eclipse.elk.core.options.EdgeRouting` (Enum)
**Possible Values:** | `ORTHOGONAL`<br>`POLYLINE`<br>`SPLINES`<br>`UNDEFINED`
**Default Value:** | `EdgeRouting.UNDEFINED` (as defined in org.eclipse.elk)
**Applies To:** | parents
**Legacy Id:** | de.cau.cs.kieler.edgeRouting

### Description

What kind of edge routing style should be applied for the content of a parent node. Algorithms may also set this option to single edges in order to mark them as splines. The bend point list of edges with this option set to SPLINES must be interpreted as control points for a piecewise cubic spline.
