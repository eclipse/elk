---
layout: page
title: Linear Segments Deflection Dampening
type: option
---
## Linear Segments Deflection Dampening

----|----
**Type:** | advanced
**Identifier:** | org.eclipse.elk.layered.nodePlacement.linearSegments.deflectionDampening
**Meta Data Provider:** | options.LayeredMetaDataProvider
**Value Type:** | `double`
**Default Value:** | `0.3` (as defined in org.eclipse.elk.layered)
**Lower Bound:** | `ExclusiveBounds.greaterThan(0)`
**Applies To:** | parents
**Legacy Id:** | de.cau.cs.kieler.klay.layered.linearSegmentsDeflectionDampening
**Dependencies:** | [org.eclipse.elk.layered.nodePlacement.strategy](org-eclipse-elk-layered-nodePlacement-strategy)
**Containing Group:** | [nodePlacement](org-eclipse-elk-layered-nodePlacement) -> [linearSegments](org-eclipse-elk-layered-nodePlacement-linearSegments)

### Description

Dampens the movement of nodes to keep the diagram from getting too large.
