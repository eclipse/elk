Size constraints basically restrict the freedom a layout algorithm has in resizing a node subject to its node labels, ports, and port labels. The different values have the following meanings:

Constraint                          | Meaning
----------                          | -------
`DEFAULT_MINIMUM_SIZE`              | Uses a default minimum size if none is specified and size constraints include `MINIMUM_SIZE`.
`MINIMUM_SIZE_ACCOUNTS_FOR_PADDING` | If this option is set and paddings are computed by the algorithm, the minimum size plus the computed padding are a lower bound on the node size. If this option is not set, the minimum size will be applied to the node's whole size regardless of any computed padding. Note that, depending on the algorithm, this option may only apply to non-hierarchical nodes. This option only makes sense if size constraints include `MINIMUM_SIZE`.
`COMPUTE_PADDING`                   | With this option set, the padding of nodes will be computed and returned as part of the algorithm's result. If port labels or node labels are placed, they may influence the size of the padding. Note that, depending on the algorithm, this option may only apply to non-hierarchical nodes. This option is independent of the size constraint set on a node.
`OUTSIDE_NODE_LABELS_OVERHANG`      | If node labels influence the node size, but outside node labels are allowed to overhang, only inside node labels actually influence node size.
`PORTS_OVERHANG`                    | By default, ports only use the space available to them, even if that means violating port spacing settings. If this option is active, port spacings are adhered to, even if that means ports extend beyond node boundaries.
`UNIFORM_PORT_SPACING`              | If port labels are taken into consideration, differently sized labels can result in a different amount of space between different pairs of ports. This option causes all ports to be evenly spaced by enlarging the space between every pair of ports to the larges amount of space between any pair of ports.
`SPACE_EFFICIENT_PORT_LABELS`       | Unless there are exactly two ports at a given port side, outside port labels are usually all placed to the same side of their port. For example, if there are three northern ports, all of their labels will be placed to the right of their ports. If this option is active, the leftmost label will be placed to the left of its port while the others stay on the right side (and similar for the other port sides). This allows the node to be smaller because the node size doesn't have to accommodate as many port labels, but it breaks symmetry.
`FORCE_TABULAR_NODE_LABELS`         | By default, inside node labels will be laid out in three rows of three cells, with no relation between the width of cells in different rows. If this option is enabled, the cells will be treated as cells of a table, with equal columns across all rows. This usually results in larger nodes.
`ASYMMETRICAL`                      | If this option is set, the node sizing and label placement code will not make an attempt to achieve a symmetrical layout. With this option inactive, for example, the space reserved for left inside port labels will be the same as for right inside port labels, which would not be the case otherwise. Deactivating this option will also ensure that center node labels will actually be placed in the center.


### Examples

What follows are examples of some of the size options. Each example shows two nodes: the left has the respective option disabled, the right has it enabled.

#### `MINIMUM_SIZE_ACCOUNTS_FOR_PADDING`

In this example, the darker area is the node's client area which remains once paddings are subtracted from the node's size.

![Minimum size accounts for padding](/images/options/node_size_options_min_size_accounts_for_padding.png)


#### `OUTSIDE_NODE_LABELS_OVERHANG`

![Outside node labels overhang](/images/options/node_size_options_outside_node_labels_overhang.png)


#### `PORTS_OVERHANG`

![Ports overhang](/images/options/node_size_options_ports_overhang.png)


#### `UNIFORM_PORT_SPACING`

![Uniform port spacing](/images/options/node_size_options_uniform_port_spacing.png)


#### `SPACE_EFFICIENT_PORT_LABELS`

![Space efficient port labels](/images/options/node_size_options_space_efficient_port_labels.png)


#### `FORCE_TABULAR_NODE_LABELS`

![Force tabular node labels](/images/options/node_size_options_force_tabular_node_labels.png)


#### `ASYMMETRICAL`

![Asymmetrical](/images/options/node_size_options_asymmetrical.png)
