---
title: "Top-down Layout: Zoom in the Layout Process"
menu:
  main:
    identifier: "23-06-09-topdown-layout"
    parent: "2023"
    weight: 10
---

_By Maximilian Kasperowski, June 9, 2023_

The coming update (ELK 0.9.0) introduces a new approach to layout hierarchical graphs. Instead of increasing the size of parent nodes to fit their content we apply scaling to the content to make it fit the parent. In this post I will go over the new properties provided to achieve this and what kinds of output can be produced using top-down layout.

## Scaling
In addition to the existing data assigned to graph elements during layout such as positions, nodes can now also have the additional property `org.eclipse.elk.topdown.scaleFactor`. The scale factor tells the renderer what the relative scale of the sub-layout of the node should be. The renderer needs to apply the scaling as illustrated in the following figure.
{{<image src="top-down-scaling.png" alt="application of top-down scale factor." width="100%">}}

The following graph demonstrates the scale factor. The top-level red node contains a layered layout that consists of two nodes. We have assigned it a fixed width and height and in order to fit the children the computed scale factor is applied around all the children as a group. In the svg this is realized by wrapping the children in a `<g>` tag with a `transform="scale(scaleFactor)` attribute.
{{< image src="topdown-example.svg" alt="top-down layout example." width="200%">}}

## Usage
In general there are many ways that top-down layout can be configured. I will demonstrate two main approaches that showcase all possible options in some typical configurations. 

### Scale all hierarchy levels
The first simple method is to apply some scale factor for each sublayout. This means that for each node we first set its dimensions (independent of the sublayout) and then, after layouting the children, scale the sublayout so that it fits the parent. To do this we set the following properties:

For all nodes:
 - `org.eclipse.elk.topdownLayout=true`
  
For the root node:
 - `org.eclipse.elk.topdown.nodeType=ROOT_NODE`

For all other nodes:
 - `org.eclipse.elk.topdown.nodeType=HIERARCHICAL_NODE` 
 - `org.eclipse.elk.nodeSize.fixedGraphSize=true`
 - `org.eclipse.elk.algorithm` must be set to an algorithm that supports `org.eclipse.elk.nodeSize.fixedGraphSize`

### Mixing scaling approach
Another approach that can be used for certain graph types utilizes the `PARALLEL_NODE` node type. Using this prevents scaling on certain parts of the diagram hierarchy to retain more control over the look of the graph. Due to algorithmic limitations, this approach only works when we have *region-like* nodes, i.e., nodes that are not connected via edges and should be simply packed in some manner.

The two examples shown below are obtained by configuring the properties in the following way:

For all nodes:
 - `org.eclipse.elk.topdownLayout=true`
  
For the root node:
 - `org.eclipse.elk.topdown.nodeType=ROOT_NODE`

For all white regions:
 - `org.eclipse.elk.topdown.nodeType=HIERARCHICAL_NODE` 
 - `org.eclipse.elk.nodeSize.fixedGraphSize=true`
 - `org.eclipse.elk.algorithm` must be set to an algorithm that supports `org.eclipse.elk.nodeSize.fixedGraphSize`

For all blue states:
 - `org.eclipse.elk.topdown.nodeType=PARALLEL_NODE` 
 - `org.eclipse.elk.algorithm=topdownpacking`
 - `org.eclipse.elk.topdown.hierarchicalNodeWidth=200`
 - `org.eclipse.elk.topdown.hierarchicalNodeAspectRatio=1.4`

{{<image src="wagon-topdown.png" alt="example of top-down layout on wagon scchart." width="100%">}}

{{<image src="controller-topdown.png" alt="example of top-down layout on controller scchart." width="100%">}}