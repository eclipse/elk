---
layout: page
title: Post Compaction Constraint Calculation
type: option
---
## Post Compaction Constraint Calculation

----|----
**Type:** | advanced
**Identifier:** | org.eclipse.elk.layered.compaction.postCompaction.constraints
**Meta Data Provider:** | properties.LayeredMetaDataProvider
**Value Type:** | `org.eclipse.elk.alg.layered.intermediate.compaction.ConstraintCalculationStrategy` (Enum)
**Possible Values:** | `QUADRATIC`<br>`SCANLINE`
**Default Value:** | `ConstraintCalculationStrategy.SCANLINE` (as defined in org.eclipse.elk.layered)
**Applies To:** | parents
**Legacy Id:** | de.cau.cs.kieler.klay.layered.postCompaction.constraints
**Containing Groups:** | [compaction](org-eclipse-elk-layered-compaction) -> [postCompaction](org-eclipse-elk-layered-compaction-postCompaction)

### Description

Specifies whether and how post-process compaction is applied.
