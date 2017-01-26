---
layout: page
title: Greedy Switch Crossing Minimization
type: option
---
## Greedy Switch Crossing Minimization

----|----
**Type:** | advanced
**Identifier:** | org.eclipse.elk.layered.crossingMinimization.greedySwitch.type
**Meta Data Provider:** | properties.LayeredMetaDataProvider
**Value Type:** | `org.eclipse.elk.alg.layered.properties.GreedySwitchType` (Enum)
**Possible Values:** | `OFF`<br>`ONE_SIDED`<br>`TWO_SIDED`
**Default Value:** | `GreedySwitchType.TWO_SIDED` (as defined in org.eclipse.elk.layered)
**Applies To:** | parents
**Legacy Id:** | de.cau.cs.kieler.klay.layered.greedySwitch
**Containing Groups:** | [crossingMinimization](org-eclipse-elk-layered-crossingMinimization) -> [greedySwitch](org-eclipse-elk-layered-crossingMinimization-greedySwitch)

### Description

Greedy Switch strategy for crossing minimization. The greedy switch heuristic is executed after the regular layer sweep as a post-processor.
