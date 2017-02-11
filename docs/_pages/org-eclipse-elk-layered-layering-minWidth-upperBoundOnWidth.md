---
layout: page
title: Upper Bound On Width [MinWidth Layerer]
type: option
---
## Upper Bound On Width [MinWidth Layerer]

----|----
**Type:** | advanced
**Identifier:** | org.eclipse.elk.layered.layering.minWidth.upperBoundOnWidth
**Meta Data Provider:** | properties.LayeredMetaDataProvider
**Value Type:** | `int`
**Default Value:** | `4` (as defined in org.eclipse.elk.layered)
**Lower Bound:** | `-1`
**Applies To:** | parents
**Legacy Id:** | de.cau.cs.kieler.klay.layered.minWidthUpperBoundOnWidth
**Dependencies:** | [org.eclipse.elk.layered.layering.strategy](org-eclipse-elk-layered-layering-strategy)
**Containing Group:** | [layering](org-eclipse-elk-layered-layering) -> [minWidth](org-eclipse-elk-layered-layering-minWidth)

### Description

Defines a loose upper bound on the width of the MinWidth layerer. If set to '-1' multiple values are tested and the best result is selected.
