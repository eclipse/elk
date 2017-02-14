---
layout: page
title: Greedy Switch Activation Threshold
type: option
---
## Greedy Switch Activation Threshold

----|----
**Type:** | advanced
**Identifier:** | org.eclipse.elk.layered.crossingMinimization.greedySwitch.activationThreshold
**Meta Data Provider:** | options.LayeredMetaDataProvider
**Value Type:** | `int`
**Default Value:** | `40` (as defined in org.eclipse.elk.layered)
**Lower Bound:** | `0`
**Applies To:** | parents
**Containing Group:** | [crossingMinimization](org-eclipse-elk-layered-crossingMinimization) -> [greedySwitch](org-eclipse-elk-layered-crossingMinimization-greedySwitch)

### Description

By default it is decided automatically if the greedy switch is activated or not. The decision is based on whether the size of the input graph (without dummy nodes) is smaller than the value of this option. A '0' enforces the activation.
