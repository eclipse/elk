---
layout: page
title: Animation Time Factor
type: option
---
## Animation Time Factor

----|----
**Type:** | global
**Identifier:** | org.eclipse.elk.animTimeFactor
**Meta Data Provider:** | core.options.CoreOptions
**Value Type:** | `int`
**Default Value:** | `100` (as defined in org.eclipse.elk)
**Lower Bound:** | `0`
**Applies To:** | parents
**Legacy Id:** | de.cau.cs.kieler.animTimeFactor

### Description

Factor for computation of animation time. The higher the value, the longer the animation time. If the value is 0, the resulting time is always equal to the minimum defined by 'Minimal Animation Time'.
