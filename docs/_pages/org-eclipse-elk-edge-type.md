---
layout: page
title: Edge Type
type: option
---
## Edge Type

----|----
**Type:** | programmatic
**Identifier:** | org.eclipse.elk.edge.type
**Meta Data Provider:** | core.options.CoreOptions
**Value Type:** | `org.eclipse.elk.core.options.EdgeType` (Enum)
**Possible Values:** | `ASSOCIATION`<br>`DEPENDENCY`<br>`DIRECTED`<br>`GENERALIZATION`<br>`NONE`<br>`UNDIRECTED`
**Default Value:** | `EdgeType.NONE` (as defined in org.eclipse.elk)
**Applies To:** | edges
**Legacy Id:** | de.cau.cs.kieler.edgeType
**Containing Groups:** | [edge](org-eclipse-elk-edge)

### Description

The type of an edge. This is usually used for UML class diagrams, where associations must be handled differently from generalizations.
