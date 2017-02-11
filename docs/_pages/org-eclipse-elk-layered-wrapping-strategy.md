---
layout: page
title: Graph Wrapping Strategy
type: option
---
## Graph Wrapping Strategy

----|----
**Type:** | advanced
**Identifier:** | org.eclipse.elk.layered.wrapping.strategy
**Meta Data Provider:** | properties.LayeredMetaDataProvider
**Value Type:** | `org.eclipse.elk.alg.layered.intermediate.wrapping.WrappingStrategy` (Enum)
**Possible Values:** | `GENERAL`<br>`OFF`<br>`PATH_LIKE`
**Default Value:** | `WrappingStrategy.OFF` (as defined in org.eclipse.elk.layered)
**Applies To:** | parents
**Containing Group:** | [wrapping](org-eclipse-elk-layered-wrapping)

### Description

For certain graphs and certain prescribed drawing areas it may be desirable to split the laid out graph into chunks that are placed side by side. The edges that connect different chunks are 'wrapped' around from the end of one chunk to the start of the other chunk. The points between the chunks are referred to as 'cuts'.
