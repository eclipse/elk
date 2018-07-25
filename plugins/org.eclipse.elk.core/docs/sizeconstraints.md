Size constraints basically restrict the freedom a layout algorithm has in resizing a node subject to its node labels, ports, and port labels. The different values have the following meanings:

Constraint       | Meaning
----------       | -------
`NodeLabels`     | The node can be made large enough to place all node labels without violating spacing constraints.
`Ports`          | The node can be made large enough to place all of its ports without violating spacing constraints.
`PortLabels`     | If `Ports` is active, not only the ports themselves are considered, but also their labels.
`MinimumSize`    | The node must meet a given minimum size specified through the _Node Size Minimum_ option.


### Examples

We start with an example of a node with empty size constraints, which means that automatic layout is not allowed to touch the node's size in any way. Let us assume that the size is set such that the following drawing is produced:

![Empty size constraints](/images/options/node_size_constraints_none.png)

We will now add all size constraints, one by one, to the node to see how we gradually end up with a node size that is calculated to take everything into account.

Adding the `NodeLabels` constraint resizes the node such that it is just large enough for the node's label. Note how the ports and their labels are completely ignored.

![NodeLabels](/images/options/node_size_constraints_nodelabels.png)

Adding the `Ports` constraint ensures that the node is large enough to leave the necessary spacing between the ports. Only the spacing between the ports is considered, though; their labels are not taken into account yet.

![NodeLabels, Ports](/images/options/node_size_constraints_nodelabels_ports.png)

Adding the `PortLabels` constraint as well ensures that the port spacing is applied between a port's label and the next port.

![NodeLabels, Ports, PortLabels](/images/options/node_size_constraints_nodelabels_ports_portlabels.png)

Adding the `MinimumSize` constraint, finally, ensures that the computed node size won't fall below a minimum size configured for the node. In our example, the minimum size might be wider than what the algorithm would have computed.

![Full size constraints](/images/options/node_size_constraints_full.png)
