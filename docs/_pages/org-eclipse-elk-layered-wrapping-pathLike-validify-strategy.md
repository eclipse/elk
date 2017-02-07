---
layout: page
title: Path-Like Validify Strategy
type: option
---
## Path-Like Validify Strategy

----|----
**Type:** | advanced
**Identifier:** | org.eclipse.elk.layered.wrapping.pathLike.validify.strategy
**Meta Data Provider:** | properties.LayeredMetaDataProvider
**Value Type:** | `org.eclipse.elk.alg.layered.intermediate.wrapping.ValidifyStrategy` (Enum)
**Possible Values:** | `GREEDY`<br>`LOOK_BACK`<br>`NO`
**Default Value:** | `ValidifyStrategy.GREEDY` (as defined in org.eclipse.elk.layered)
**Applies To:** | parents
**Dependencies:** | [org.eclipse.elk.layered.wrapping.strategy](org-eclipse-elk-layered-wrapping-strategy)
**Containing Groups:** | [wrapping](org-eclipse-elk-layered-wrapping) -> [pathLike](org-eclipse-elk-layered-wrapping-pathLike) -> [validify](org-eclipse-elk-layered-wrapping-pathLike-validify)

### Description

When wrapping path-like graphs, the graph must not be split between any pair of layers. The validify strategy makes sure every computed split point is allowed.
