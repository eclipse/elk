---
layout: page
title: Cutting Strategy
type: option
---
## Cutting Strategy

----|----
**Type:** | advanced
**Identifier:** | org.eclipse.elk.layered.wrapping.cutting.strategy
**Meta Data Provider:** | options.LayeredMetaDataProvider
**Value Type:** | `org.eclipse.elk.alg.layered.options.CuttingStrategy` (Enum)
**Possible Values:** | `ARD`<br>`MANUAL`<br>`MSD`
**Default Value:** | `CuttingStrategy.MSD` (as defined in org.eclipse.elk.layered)
**Applies To:** | parents
**Containing Group:** | [wrapping](org-eclipse-elk-layered-wrapping) -> [cutting](org-eclipse-elk-layered-wrapping-cutting)

### Description

The strategy by which the layer indexes are determined at which the layering crumbles into chunks.
