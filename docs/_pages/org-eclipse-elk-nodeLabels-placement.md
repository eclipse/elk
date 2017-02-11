---
layout: page
title: Node Label Placement
type: option
---
## Node Label Placement

----|----
**Identifier:** | org.eclipse.elk.nodeLabels.placement
**Meta Data Provider:** | core.options.CoreOptions
**Value Type:** | `java.util.EnumSet<org.eclipse.elk.core.options.NodeLabelPlacement>`
**Possible Values:** | `H_CENTER`<br>`H_LEFT`<br>`H_PRIORITY`<br>`H_RIGHT`<br>`INSIDE`<br>`OUTSIDE`<br>`V_BOTTOM`<br>`V_CENTER`<br>`V_TOP`
**Default Value:** | `NodeLabelPlacement.fixed()` (as defined in org.eclipse.elk)
**Applies To:** | nodes, labels
**Legacy Id:** | de.cau.cs.kieler.nodeLabelPlacement
**Containing Group:** | [nodeLabels](org-eclipse-elk-nodeLabels)

### Description

Hints for where node labels are to be placed; if empty, the node label's position is not modified.
