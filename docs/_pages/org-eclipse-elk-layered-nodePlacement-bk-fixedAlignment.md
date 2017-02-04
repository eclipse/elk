---
layout: page
title: BK Fixed Alignment
type: option
---
## BK Fixed Alignment

----|----
**Type:** | advanced
**Identifier:** | org.eclipse.elk.layered.nodePlacement.bk.fixedAlignment
**Meta Data Provider:** | properties.LayeredMetaDataProvider
**Value Type:** | `org.eclipse.elk.alg.layered.properties.FixedAlignment` (Enum)
**Possible Values:** | `BALANCED`<br>`LEFTDOWN`<br>`LEFTUP`<br>`NONE`<br>`RIGHTDOWN`<br>`RIGHTUP`
**Default Value:** | `FixedAlignment.NONE` (as defined in org.eclipse.elk.layered)
**Applies To:** | parents
**Legacy Id:** | de.cau.cs.kieler.klay.layered.fixedAlignment
**Dependencies:** | [org.eclipse.elk.layered.nodePlacement.strategy](org-eclipse-elk-layered-nodePlacement-strategy)
**Containing Groups:** | [nodePlacement](org-eclipse-elk-layered-nodePlacement) -> [bk](org-eclipse-elk-layered-nodePlacement-bk)

### Description

Tells the BK node placer to use a certain alignment (out of its four) instead of the one producing the smallest height, or the combination of all four.
