---
layout: page
title: Box Layout Mode
type: option
---
## Box Layout Mode

----|----
**Identifier:** | org.eclipse.elk.box.packingMode
**Meta Data Provider:** | core.options.CoreOptions
**Value Type:** | `org.eclipse.elk.core.util.BoxLayoutProvider$PackingMode` (Enum)
**Possible Values:** | `GROUP_DEC`<br>`GROUP_INC`<br>`GROUP_MIXED`<br>`SIMPLE`
**Default Value:** | `BoxLayoutProvider.PackingMode.SIMPLE` (as defined in org.eclipse.elk)
**Applies To:** | parents
**Containing Groups:** | [box](org-eclipse-elk-box)

### Description

Configures the packing mode used by the {@link BoxLayoutProvider}. If SIMPLE is not required (neither priorities are used nor the interactive mode), GROUP_DEC can improve the packing and decrease the area. GROUP_MIXED and GROUP_INC may, in very specific scenarios, work better.
