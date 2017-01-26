---
layout: page
title: Label Manager
type: option
---
## Label Manager

----|----
**Type:** | programmatic
**Identifier:** | org.eclipse.elk.labels.labelManager
**Meta Data Provider:** | LabelManagementOptions
**Value Type:** | `org.eclipse.elk.core.labels.ILabelManager`
**Applies To:** | parents, labels

### Description

The label manager responsible for a given part of the graph. A label manager can either be attached to a compound node (in which case it is responsible for all labels inside) or to specific labels. The label manager can then be called by layout algorithms to modify labels that are too wide to try and shorten them to a given target width.
