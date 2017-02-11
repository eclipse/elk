---
layout: page
title: BK Edge Straightening
type: option
---
## BK Edge Straightening

----|----
**Type:** | advanced
**Identifier:** | org.eclipse.elk.layered.nodePlacement.bk.edgeStraightening
**Meta Data Provider:** | properties.LayeredMetaDataProvider
**Value Type:** | `org.eclipse.elk.alg.layered.p4nodes.bk.EdgeStraighteningStrategy` (Enum)
**Possible Values:** | `IMPROVE_STRAIGHTNESS`<br>`NONE`
**Default Value:** | `EdgeStraighteningStrategy.IMPROVE_STRAIGHTNESS` (as defined in org.eclipse.elk.layered)
**Applies To:** | parents
**Legacy Id:** | de.cau.cs.kieler.klay.layered.nodeplace.compactionStrategy
**Dependencies:** | [org.eclipse.elk.layered.nodePlacement.strategy](org-eclipse-elk-layered-nodePlacement-strategy)
**Containing Group:** | [nodePlacement](org-eclipse-elk-layered-nodePlacement) -> [bk](org-eclipse-elk-layered-nodePlacement-bk)

### Description

Specifies whether the Brandes Koepf node placer tries to increase the number of straight edges at the expense of diagram size. There is a subtle difference to the 'favorStraightEdges' option, which decides whether a balanced placement of the nodes is desired, or not. In bk terms this means combining the four alignments into a single balanced one, or not. This option on the other hand tries to straighten additional edges during the creation of each of the four alignments.
