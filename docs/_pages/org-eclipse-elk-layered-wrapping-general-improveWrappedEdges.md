---
layout: page
title: Improve Wrapped Edges
type: option
---
## Improve Wrapped Edges

----|----
**Type:** | advanced
**Identifier:** | org.eclipse.elk.layered.wrapping.general.improveWrappedEdges
**Meta Data Provider:** | properties.LayeredMetaDataProvider
**Value Type:** | `boolean`
**Default Value:** | `true` (as defined in org.eclipse.elk.layered)
**Applies To:** | parents
**Dependencies:** | [org.eclipse.elk.layered.wrapping.strategy](org-eclipse-elk-layered-wrapping-strategy)
**Containing Groups:** | [wrapping](org-eclipse-elk-layered-wrapping) -> [general](org-eclipse-elk-layered-wrapping-general)

### Description

The initial wrapping is performed in a very simple way. As a consequence, edges that wrap from one chunk to another may be unnecessarily long. Activating this option tries to shorten such edges.
