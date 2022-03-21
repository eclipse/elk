---
title: "Spacing Options"
menu:
  main:
    identifier: "SpacingOptions"
    parent: "GraphDataStructure"
    weight: 30

---

## Space-able elements

As far as the ELK core is concerned, the following elements can have spacing:

1. Nodes
2. Ports
3. Labels
4. Edges
5. Comments
6. Connected components

The first three can be thought of as boxes with some space to be left around them. Edges are not boxes, and connected components may be more complex than simple boxes.

ELK Layered adds the concept of _layers_, which leads to a few additional spacing values as described later.


## Spacing Combinations

Spacings are always defined between pairs of things. All spacings in this section are influenced by the [_base value for spacings_](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-layered-spacing-baseValue.html). We define spacings between the following pairs of things (ordered lexicographically):

1. [Comment-Comment](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-spacing-commentComment.html)
1. [Comment-Node](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-spacing-commentNode.html)
1. [Component-Component](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-spacing-componentComponent.html)
1. [Edge-Edge](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-spacing-edgeEdge.html)
1. [Edge-Label](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-spacing-edgeLabel.html)
1. [Edge-Node](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-spacing-edgeNode.html)
1. [Label-Label](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-spacing-labelLabel.html)
1. [Label-Node](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-spacing-labelNode.html)
1. Label-Port Vertical (a positive value means further away from the node)
1. Label-Port Horizontal (a positive value means further away from the node)
1. [Node-Node](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-spacing-nodeNode.html)
1. [Port-Port](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-spacing-portPort.html)

Since ELK Layered adds the concept of layers, it interprets many of the spacings above as being spacings active inside each layer (as vertical spacings, if the layout direction is right). To control spacing between layers (horizontal spacing), ELK Layered adds the following pairs:

1. [Edge-Edge-BetweenLayers](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-layered-spacing-edgeEdgeBetweenLayers.html)
1. [Edge-Node-BetweenLayers](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-layered-spacing-edgeNodeBetweenLayers.html)
1. [Node-Node-BetweenLayers](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-layered-spacing-nodeNodeBetweenLayers.html)

Additionally the following spacing values were introduced to handle special cases:

1. [Additional Port Space](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-spacing-portsSurrounding.html)
2. [Additional Wrapped Edges Spacing](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-layered-wrapping-additionalEdgeSpacing.html)
3. [Individual Spacing](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-spacing-individual.html)
4. [Individual Spacing (ELK Layered)](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-spacing-individual_org-eclipse-elk-layered.html)
5. [Node Self Loop Spacing](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-spacing-nodeSelfLoop.html)

Examples how these spacings influence layout can be found [here](https://rtsys.informatik.uni-kiel.de/elklive/examples.html).

## Other Spacings

Spacing that do not use the base value for spacings but are still called spacing:

1. [Layer Spacing Factor (ELK Graphviz)](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-graphviz-layerSpacingFactor.html)
2. [Sloppy Spline Layer Spacing Factor (ELK Layered, Edge Routing)](https://www.eclipse.org/elk/reference/options/org-eclipse-elk-layered-edgeRouting-splines-sloppy-layerSpacingFactor.html)

## Individual Spacings

Spacings are generally set on compound nodes and affect their direct children. There may be times, though, when a particular element wants to override the spacing settings. This could be solved by simply setting the appropriate spacing properties on that element. If that element is itself a compound node, however, we would introduce ambiguity: should the spacings be applied to the node's children or to the node itself?

The solution is to provide a _individual spacings_ property whose value is a property holder on which the spacing options that should be overridden can be set.


## Remarks

- Insets / paddings / border spacing are an orthogonal concept and thus do not appear here.
- Node spacings do not begin at the border of a node, but at the border of its margin. The margin of a node is the bounding box around a node which includes the node itself, its ports, and its various kinds of labels.
- Edge spacings or spacing that somehow involve edges do not consider edges to have a thickness. Therefore, spacing from a node to an edge is always between the node bounding box and the center of the edge.