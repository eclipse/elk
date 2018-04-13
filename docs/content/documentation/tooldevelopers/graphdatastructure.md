---
title: "Graph Data Structure"
menu:
  main:
    identifier: "GraphDataStructure"
    parent: "ToolDevelopers"
    weight: 10
---

To represent graphs that should be laid out, the Eclipse Layout Kernel provides a robust EMF-based data structure. This page is about describing that graph data structure.

## Terminology

### Graphs

This is a simple example graph along with the terminology that goes with it.

{{< image src="graph_graph.png" alt="Terminology relating to graphs." >}}


### Inclusion Trees

Inclusion trees capture the hierarchical structure of a graph. See below for the inclusion tree of the graph we just saw, along with the terminology that goes with it.

{{< image src="graph_inclusionTree.png" alt="Terminology relating to the inclusion tree." >}}


### Definitions

* **Inclusion Tree:** The parent-child relationships of all nodes. This is basically the node containments in the EMF data structure. Note that in an ELK graph, the tree always has a single root node that represents the drawing area. Top-level nodes are children of that root node.

* **Graph:** A set of nodes and edges and whatever else belongs to them (labels, ports, ...).

    * *Simple Graph:* All children of a single node. The node represents that simple graph. This means that if the objects that the graph consists of are to be passed to a layout algorithm, this is done by simply passing the node object that represents that graph.

    * *Hierarchical Graph:* All descendants of a single node. Although the word "all" is a bit too strong here: we may choose to exclude all descendants of one of the node's descendants. Similar to simple graphs, that single node represents the hierarchical graph. The root node represents the whole graph (see below) and is simply a special case of this rule.

* **Node:**

    * *Simple Node:* A node that does not contain child nodes.

    * *Hierarchical Node:* A node that contains child nodes.

* **Edge:**

    * *Simple Edge:* An edge that connects two nodes in the same simple graph. This of course implies that the edge's source and target nodes both have the same parent node.

    * *Hierarchical Edge:* An edge that is not simple.

        * *Short Hierarchical Edge:* A hierarchical edge that only has to leave (or enter) a single hierarchical node to get to its target. Thus, a short hierarchical edge connects nodes in adjacent layers of hierarchy.

        * *Long Hierarchical Edge:* A hierarchical edge that is not a short hierarchical edge.

* **Port:** An explicit connection point on a node for edges to connect to.

    * *Simple Port:* A port on a simple node, or a port that does not have incident hierarchical edges.

    * *Hierarchical Port:* A port on a hierarchical node that has incident hierarchical edges.

* **Root Node:** The root of the inclusion tree.

    * *Root Node of a Graph:* The lowest common ancestor of all nodes in the graph.


## Graph Data Structure

### The Meta Model

TODO Describe

### Working With the Graph Data Structure

Since ELK graphs are based on EMF, you can simply obtain an instance of the `ElkGraphFactory` interface and start creating graph elements. To make things easier, however, the `ElkGraphUtil` contains a number of utility methods in different categories:

* **Graph Creation:** There are a number of methods whose names begin with `create` that can be used to create and initialize graph elements. For instance, the `createSimpleEdge(source, target)` method not only creates an `ElkEdge`, but also sets its source and target and, as a bonus, adds it to the list of contained edges of the correct node, all in a single method call.

    One method of particular value to layout algorithm developers is `firstEdgeSection(edge, reset, removeOthers)`, which returns the first edge section of the given edge, optionally resetting its layout data and removing all other edge sections. If the edge doesn't have any edge sections yet, one is created and added to it. This method is handy for applying layout results.

* **Edge Containment:** Unless one uses one of the `create` methods to create edges, finding the node an edge should be contained in may be annoying. The `updateContainment(ElkEdge)` method automatically computes the best containment and puts the edge there. This requires at least one of the edge's end points to be set, since that is what determines the best containment.

* **Convenience Methods:** There are a number of convenience methods, for example to iterate over all of a node's incoming or outgoing edges, to find the node that represents the simple graph an element is part of, and more.
