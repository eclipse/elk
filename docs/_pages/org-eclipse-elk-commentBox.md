---
layout: page
title: Comment Box
type: option
---
## Comment Box

----|----
**Type:** | programmatic
**Identifier:** | org.eclipse.elk.commentBox
**Meta Data Provider:** | core.options.CoreOptions
**Value Type:** | `boolean`
**Default Value:** | `false` (as defined in org.eclipse.elk)
**Applies To:** | nodes
**Legacy Id:** | de.cau.cs.kieler.commentBox

### Description

Whether the node should be regarded as a comment box instead of a regular node. In that case its placement should be similar to how labels are handled. Any edges incident to a comment box specify to which graph elements the comment is related.
