---
layout: page
title: FR Temperature
type: option
---
## FR Temperature

----|----
**Identifier:** | org.eclipse.elk.force.temperature
**Meta Data Provider:** | options.ForceMetaDataProvider
**Value Type:** | `double`
**Default Value:** | `0.001` (as defined in org.eclipse.elk.force)
**Lower Bound:** | `ExclusiveBounds.greaterThan(0)`
**Applies To:** | parents
**Dependencies:** | [org.eclipse.elk.force.model](org-eclipse-elk-force-model)

### Description

The temperature is used as a scaling factor for particle displacements.
