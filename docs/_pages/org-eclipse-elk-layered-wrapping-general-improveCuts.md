---
layout: page
title: Improve Cuts
type: option
---
## Improve Cuts

----|----
**Type:** | advanced
**Identifier:** | org.eclipse.elk.layered.wrapping.general.improveCuts
**Meta Data Provider:** | options.LayeredMetaDataProvider
**Value Type:** | `boolean`
**Default Value:** | `true` (as defined in org.eclipse.elk.layered)
**Applies To:** | parents
**Dependencies:** | [org.eclipse.elk.layered.wrapping.strategy](org-eclipse-elk-layered-wrapping-strategy)
**Containing Group:** | [wrapping](org-eclipse-elk-layered-wrapping) -> [general](org-eclipse-elk-layered-wrapping-general)

### Description

For general graphs it is important that not too many edges wrap backwards. Thus a compromise between evenly-distributed cuts and the total number of cut edges is sought.
