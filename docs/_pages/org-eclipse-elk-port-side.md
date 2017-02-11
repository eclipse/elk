---
layout: page
title: Port Side
type: option
---
## Port Side

----|----
**Type:** | programmatic
**Identifier:** | org.eclipse.elk.port.side
**Meta Data Provider:** | core.options.CoreOptions
**Value Type:** | `org.eclipse.elk.core.options.PortSide` (Enum)
**Possible Values:** | `EAST`<br>`NORTH`<br>`SIDES_EAST`<br>`SIDES_EAST_SOUTH`<br>`SIDES_EAST_SOUTH_WEST`<br>`SIDES_EAST_WEST`<br>`SIDES_NONE`<br>`SIDES_NORTH`<br>`SIDES_NORTH_EAST`<br>`SIDES_NORTH_EAST_SOUTH`<br>`SIDES_NORTH_EAST_SOUTH_WEST`<br>`SIDES_NORTH_EAST_WEST`<br>`SIDES_NORTH_SOUTH`<br>`SIDES_NORTH_SOUTH_WEST`<br>`SIDES_NORTH_WEST`<br>`SIDES_SOUTH`<br>`SIDES_SOUTH_WEST`<br>`SIDES_WEST`<br>`SOUTH`<br>`UNDEFINED`<br>`WEST`
**Default Value:** | `PortSide.UNDEFINED` (as defined in org.eclipse.elk)
**Applies To:** | ports
**Legacy Id:** | de.cau.cs.kieler.portSide
**Containing Group:** | [port](org-eclipse-elk-port)

### Description

The side of a node on which a port is situated. This option must be set if 'Port Constraints' is set to FIXED_SIDE or FIXED_ORDER and no specific positions are given for the ports.
