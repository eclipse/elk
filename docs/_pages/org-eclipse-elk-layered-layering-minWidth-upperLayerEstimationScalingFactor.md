---
layout: page
title: Upper Layer Estimation Scaling Factor [MinWidth Layerer]
type: option
---
## Upper Layer Estimation Scaling Factor [MinWidth Layerer]

----|----
**Type:** | advanced
**Identifier:** | org.eclipse.elk.layered.layering.minWidth.upperLayerEstimationScalingFactor
**Meta Data Provider:** | properties.LayeredMetaDataProvider
**Value Type:** | `int`
**Default Value:** | `2` (as defined in org.eclipse.elk.layered)
**Lower Bound:** | `-1`
**Applies To:** | parents
**Legacy Id:** | de.cau.cs.kieler.klay.layered.minWidthUpperLayerEstimationScalingFactor
**Dependencies:** | [org.eclipse.elk.layered.layering.strategy](org-eclipse-elk-layered-layering-strategy)
**Containing Group:** | [layering](org-eclipse-elk-layered-layering) -> [minWidth](org-eclipse-elk-layered-layering-minWidth)

### Description

Multiplied with Upper Bound On Width for defining an upper bound on the width of layers which haven't been determined yet, but whose maximum width had been (roughly) estimated by the MinWidth algorithm. Compensates for too high estimations. If set to '-1' multiple values are tested and the best result is selected.
