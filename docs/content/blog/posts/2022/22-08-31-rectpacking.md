---
title: "Rectpacking"
menu:
  main:
    identifier: "22-08-31-rectpacking"
    parent: "2022"
    weight: 10
---
_By Sören Domrös, August 31, 2022_

The [`rectpacking`](https://www.eclipse.org/elk/reference/algorithms/org-eclipse-elk-rectpacking.html) algorithm was introduced to solved common problems with the [`box`](https://www.eclipse.org/elk/reference/algorithms/org-eclipse-elk-box.html) algorithm, which cannot stack boxes in a row.
The idea is to form stacks with subrows inside rows, while the size of a row is always dominated by a highest rectangle to provide a visual anchor point to "read" the rows from left to right.

Since it was a common use case of the `box` algorithm to add a priority to order the rectangles rectpacking uses the model order (which corresponds to the input order of the rectangles) as a criterion.
By enabling [interactive layout](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-interactive.html) and setting [desired positions](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-rectpacking-desiredPosition.html) for nodes, this order can be changed without changing the order in the input graph.

Box algorithm:

{{< image src="scchartsregions-box-noexpand-annotated.svg" alt="Example graph with box algorithm." >}}

Rectpacking algorithm:

{{< image src="scchartsregions-subrows-annotated.svg" alt="Better layout with rectpacking by using subrows inside rows." >}}

## Algorithm

The algorithm is divided into several phases.

### Width approximation

Same as the `box` algorithm `rectpacking` packs rectangles inside a given aspect ratio.
As a first step this problem is transformed in a strip packing problem by approximating the width.

The width can also be specified by setting a [target width](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-rectpacking-targetWidth.html).

Different strategies can be chosen for width approximation based on which [goal](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-rectpacking-optimizationGoal.html) the greedy algorithm should prioritize.

Since the approximated width is mainly responsible for the line breaks between the rows that are formed by rectpacking, one can make sure that a rectangle is placed [in a new row](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-rectpacking-inNewRow.html) (since ELK 0.9.0).

Greedy width approximation may yield the following graph:

{{< image src="scchartsregions-widthapproximation-annotated.svg" alt="Graph after width approximation." >}}



### Placement

Rectangles are placed in rows similar to the `box` algorithm. Per default the row height does not change after this step. By enabling [row height re-evaluation](TODO) (since ELK 0.9.0) the row height might change by investing more computation time.

After placement the graph looks like this:

{{< image src="scchartsregions-placement-annotated.svg" alt="Graph after initial placement." >}}

### Compaction

After placement the rectangles are compacted to from stack with subrows inside the rows. This can also be [disabled](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-rectpacking-rowCompaction.html), however, this is not recommended.

{{< image src="scchartsregions-rectpacking-noexpand-annotated.svg" alt="graph after compaction." >}}

### Whitespace Elimination

Rectangles can [fill potential whitespace](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-expandNodes.html). 

{{< image src="scchartsregions-rectpacking-annotated.svg" alt="graph after whitespace elimination." >}}

The drawing can also be configured to fill whitespace such that the drawing has the [desired aspect ratio](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-rectpacking-expandToAspectRatio.html).

{{< image src="scchartsregions-rectpacking-toaspectratio2.svg" alt="Whitespace eliminated to fit the aspect ratio." >}}

