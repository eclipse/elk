---
title: "Rectpacking"
menu:
  main:
    identifier: "22-07-25-rectpacking"
    parent: "2022"
    weight: 10
---

The [`rectpacking`](https://www.eclipse.org/elk/reference/algorithms/org-eclipse-elk-rectpacking.html) algorithm was introduced to solved common problems with the [`box`](https://www.eclipse.org/elk/reference/algorithms/org-eclipse-elk-box.html) algorithm, which cannot stack boxes in a row.
The idea is to form stacks with subrows inside rows, while the size of a row is always dominated by a highest rectangle to provide a visual anchor point to "read" the rows from left to right.

Since it was a common use case of the `box` algorithm to add a priority to order the rectangles rectpacking uses the model order (which corresponds to the input order of the rectangles) as a criterion.
By enabling [interactive layout](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-interactive.html) and setting [desired positions](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-rectpacking-desiredPosition.html) for nodes, this order can be changed without changing the order in the input graph.

## Algorithm

The algorithm is divided into several phases.

### Width approximation

Same as the `box` algorithm `rectpacking` packs rectangles inside a given aspect ratio.
As a first step this problem is transformed in a strip packing problem by approximating the width.

The width can also be specified by setting a [target width](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-rectpacking-targetWidth.html).

Different strategies can be chosen for width approximation based on which [goal](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-rectpacking-optimizationGoal.html) the greedy algorithm should prioritize.

Since the approximated width is mainly responsible for the line breaks between the rows that are formed by rectpacking, one can make sure that a rectangle is placed [in a new row](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-rectpacking-inNewRow.html) (since ELK 0.9.0).

### Placement

Rectangles are placed in rows similar to the `box` algorithm. Per default the row height does not change after this step. By enabling [row height re-evaluation](TODO) (since ELK 0.9.0) the row height might change by investing more computation time.

### Compaction

After placement the rectangles are compacted to from stack with subrows inside the rows. This can also be [disabled](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-rectpacking-rowCompaction.html), however, this is not recommended.

### Whitespace Elimination

Rectangles can [fill potential whitespace](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-expandNodes.html). The drawing can also be configured to fill whitespace such that the drawing has the [desired aspect ratio](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-rectpacking-expandToAspectRatio.html).

TODO code example? TODO pictures
