---
title: "Top-down Layout: Zoom in the Layout Process"
menu:
  main:
    identifier: "23-04-11-topdown-layout"
    parent: "2023"
    weight: 10
---

_By Maximilian Kasperowski, April 11, 2023_

The coming update (ELK 0.9.0) introduces a new approach to layout hierarchical graphs. Instead of increasing the size of parent nodes to fit their content we apply scaling to the content to make it fit the parent. In this post I will go over the new properties provided to achieve this and what kinds of output can be produced using top-down layout.

## Scaling
In addition to the existing data assigned to graph elements during layout such as positions, nodes can now also have the additional property `org.eclipse.elk.topdown.scaleFactor`. The scale factor tells the renderer what the relative scale of the sub-layout of the node should be.

The following graph demonstrates the scale factor. The top-level red node contains a layered layout that consists of two nodes. We have assigned it a fixed width and height and in order to fit the children the computed scale factor is applied around all the children as a group. In the svg this is realized by wrapping the children in a `<g>` tag with a `transform="scale(scaleFactor)` attribute.
{{< image src="topdown-example.svg" alt="top-down layout example." width="200%">}}

## Usage
There are two main approaches to performing a top-down layout. The first simple method is to apply some scale factor for each sublayout. This means that for each node we first set its dimensions (independent of the sublayout) and then, after layouting the children, scale the sublayout so that it fits the parent. To do this we set the following properties:

For all nodes:
 - `org.eclipse.elk.topdownLayout=true`
  
For the root node:
 - `org.eclipse.elk.topdown.nodeType=ROOT_NODE`

For all other nodes:
 - `org.eclipse.elk.topdown.nodeType=HIERARCHICAL_NODE` 
 - `org.eclipse.elk.nodeSize.fixedGraphSize=true`
 - `org.eclipse.elk.algorithm` needs to be set to any algorithm that supports `org.eclipse.elk.nodeSize.fixedGraphSize`