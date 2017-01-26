---
layout: page
title: Box Layout
type: algorithm
---
## Box Layout

![](images/org-eclipse-elk-box_preview_box_layout.png)
**Identifier:** org.eclipse.elk.box
**Meta Data Provider:** core.options.CoreOptions

### Description

Algorithm for packing of unconnected boxes, i.e. graphs without edges.

## Supported Options

Option | Type | Default Value | Identifier
----|----|----|----
[Aspect Ratio](org-eclipse-elk-aspectRatio) | `double` | `1.3f` | org&#8203;.eclipse&#8203;.elk&#8203;.aspectRatio
[Box Layout Mode](org-eclipse-elk-boxOptions-packingMode) | `PackingMode` | `BoxLayoutProvider.PackingMode.SIMPLE` | org&#8203;.eclipse&#8203;.elk&#8203;.boxOptions&#8203;.packingMode
[Expand Nodes](org-eclipse-elk-expandNodes) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.expandNodes
[Interactive](org-eclipse-elk-interactive) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.interactive
[Minimum Height](org-eclipse-elk-nodeSize-minHeight) | `double` | `0` | org&#8203;.eclipse&#8203;.elk&#8203;.nodeSize&#8203;.minHeight
[Minimum Width](org-eclipse-elk-nodeSize-minWidth) | `double` | `0` | org&#8203;.eclipse&#8203;.elk&#8203;.nodeSize&#8203;.minWidth
[Node Size Constraints](org-eclipse-elk-nodeSize-constraints) | `EnumSet<SizeConstraint>` | `EnumSet.noneOf(SizeConstraint)` | org&#8203;.eclipse&#8203;.elk&#8203;.nodeSize&#8203;.constraints
[Node Size Minimum](org-eclipse-elk-nodeSize-minimum) | `KVector` | `<not defined>` | org&#8203;.eclipse&#8203;.elk&#8203;.nodeSize&#8203;.minimum
[Node Size Options](org-eclipse-elk-nodeSize-options) | `EnumSet<SizeOptions>` | `EnumSet.of(SizeOptions.DEFAULT_MINIMUM_SIZE, SizeOptions.APPLY_ADDITIONAL_PADDING)` | org&#8203;.eclipse&#8203;.elk&#8203;.nodeSize&#8203;.options
[Node Spacing](org-eclipse-elk-spacing-nodeNode) | `double` | `15` | org&#8203;.eclipse&#8203;.elk&#8203;.spacing&#8203;.nodeNode
[Padding](org-eclipse-elk-padding) | `ElkPadding` | `new ElkPadding(15)` | org&#8203;.eclipse&#8203;.elk&#8203;.padding
[Priority](org-eclipse-elk-priority(org.eclipse.elk.box)) | `int` | `0` | org&#8203;.eclipse&#8203;.elk&#8203;.priority

