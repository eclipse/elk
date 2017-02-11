---
layout: page
title: Node Layering Strategy
type: option
---
## Node Layering Strategy

----|----
**Identifier:** | org.eclipse.elk.layered.layering.strategy
**Meta Data Provider:** | properties.LayeredMetaDataProvider
**Value Type:** | `org.eclipse.elk.alg.layered.p2layers.LayeringStrategy` (Enum)
**Possible Values:** | `COFFMAN_GRAHAM` (*`@AdvancedPropertyValue`*)<br>`INTERACTIVE` (*`@AdvancedPropertyValue`*)<br>`LONGEST_PATH`<br>`MIN_WIDTH` (*`@ExperimentalPropertyValue`*)<br>`NETWORK_SIMPLEX`<br>`STRETCH_WIDTH` (*`@ExperimentalPropertyValue`*)
**Default Value:** | `LayeringStrategy.NETWORK_SIMPLEX` (as defined in org.eclipse.elk.layered)
**Applies To:** | parents
**Legacy Id:** | de.cau.cs.kieler.klay.layered.nodeLayering
**Containing Group:** | [layering](org-eclipse-elk-layered-layering)

### Description

Strategy for node layering.
