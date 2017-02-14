---
layout: page
title: MSD Freedom
type: option
---
## MSD Freedom

----|----
**Type:** | advanced
**Identifier:** | org.eclipse.elk.layered.wrapping.cutting.msd.freedom
**Meta Data Provider:** | options.LayeredMetaDataProvider
**Value Type:** | `java.lang.Integer`
**Default Value:** | `1` (as defined in org.eclipse.elk.layered)
**Lower Bound:** | `0`
**Applies To:** | parents
**Dependencies:** | [org.eclipse.elk.layered.wrapping.cutting.strategy](org-eclipse-elk-layered-wrapping-cutting-strategy)
**Containing Group:** | [wrapping](org-eclipse-elk-layered-wrapping) -> [cutting](org-eclipse-elk-layered-wrapping-cutting) -> [msd](org-eclipse-elk-layered-wrapping-cutting-msd)

### Description

The MSD cutting strategy starts with an initial guess on the number of chunks the graph should be split into. The freedom specifies how much the strategy may deviate from this guess. E.g. if an initial number of 3 is computed, a freedom of 1 allows 2, 3, and 4 cuts.
