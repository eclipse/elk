---
layout: page
title: Shortness Priority (ELK Layered)
type: option
---
## Shortness Priority (ELK Layered)

----|----
**Type:** | advanced
**Identifier:** | org.eclipse.elk.layered.priority.shortness
**Meta Data Provider:** | properties.LayeredMetaDataProvider
**Value Type:** | `int`
**Default Value:** | `0` (as defined in org.eclipse.elk.layered)
**Lower Bound:** | `0`
**Applies To:** | edges
**Containing Groups:** | [priority](org-eclipse-elk-layered-priority)

### Description

Defines how important it is to keep an edge as short as possible. This option is evaluated during the layering phase.

## Additional Documentation

Currently only supported by the network simplex layerer.
