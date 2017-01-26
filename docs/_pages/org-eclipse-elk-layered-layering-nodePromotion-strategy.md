---
layout: page
title: Node Promotion Strategy
type: option
---
## Node Promotion Strategy

----|----
**Type:** | advanced
**Identifier:** | org.eclipse.elk.layered.layering.nodePromotion.strategy
**Meta Data Provider:** | properties.LayeredMetaDataProvider
**Value Type:** | `org.eclipse.elk.alg.layered.intermediate.NodePromotionStrategy` (Enum)
**Possible Values:** | `DUMMYNODE_PERCENTAGE`<br>`NIKOLOV`<br>`NIKOLOV_IMPROVED`<br>`NIKOLOV_IMPROVED_PIXEL`<br>`NIKOLOV_PIXEL`<br>`NODECOUNT_PERCENTAGE`<br>`NONE`<br>`NO_BOUNDARY`
**Default Value:** | `NodePromotionStrategy.NONE` (as defined in org.eclipse.elk.layered)
**Applies To:** | parents
**Legacy Id:** | de.cau.cs.kieler.klay.layered.nodePromotion
**Containing Groups:** | [layering](org-eclipse-elk-layered-layering) -> [nodePromotion](org-eclipse-elk-layered-layering-nodePromotion)

### Description

Reduces number of dummy nodes after layering phase (if possible).
