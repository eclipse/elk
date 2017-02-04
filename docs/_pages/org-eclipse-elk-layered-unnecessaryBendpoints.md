---
layout: page
title: Add Unnecessary Bendpoints
type: option
---
## Add Unnecessary Bendpoints

----|----
**Type:** | advanced
**Identifier:** | org.eclipse.elk.layered.unnecessaryBendpoints
**Meta Data Provider:** | properties.LayeredMetaDataProvider
**Value Type:** | `boolean`
**Default Value:** | `false` (as defined in org.eclipse.elk.layered)
**Applies To:** | parents
**Legacy Id:** | de.cau.cs.kieler.klay.layered.unnecessaryBendpoints

### Description

Adds bend points even if an edge does not change direction. If true, each long edge dummy will contribute a bend point to its edges and hierarchy-crossing edges will always get a bend point where they cross hierarchy boundaries. By default, bend points are only added where an edge changes direction.
