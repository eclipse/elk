## Summary
Topdown Layout introduces the ability to compute hierarchical layouts from the root node down rather than the existing method of performing layout bottom-up. This shakes up some paradigms and allows new types of visualizations. Instead of child nodes determining the size of their parents, child nodes can now be restricted in their size by their parent. Inner layouts are then scaled down to fit their parent available space. This has the result that nodes within a hierarchy are visually more equal.

## Features & Usage
### Topdown Recursive Layout
The core feature of this PR is the ability to compute a hierarchical layout from the root node down instead of up from the leaves. This is implemented within the `RecursiveGraphLayoutEngine` and the details of how to use the feature are outlined below.

#### Simple Topdown Layout
In general topdown layout can be used to scale down inner layouts. In the basic usage nodes on the same hierarchy level are assigned the same size regardless of their content. To get this behaviour all nodes should have the `org.eclipse.elk.topdownLayout` property set to `true`. The graph itself needs its `org.eclipse.elk.topdown.nodeType` to be set to `ROOT_NODE`. Other nodes need to set their node type to `HIERARCHICAL_NODE`, `org.eclipse.elk.nodeSize.fixedGraphSize` to true and `org.eclipse.elk.algorithm` to any algorithm that supports `org.eclipse.elk.nodeSize.fixedGraphSize`. Additionally node sizes can be dynamically determined with the the help of a size approximator.

#### Topdown Layout with Lookahead
Sometimes it is desirable to not layout all nodes on a hierarchy level with equal sizes, but to instead vary the node sizes depending on their content. In topdown layout the node sizes must be defined ahead of their own layout however because they need to be laid out themselves first. In order to accomplish this a `PARALLEL_NODE` node type and an `ITopdownLayoutProvider` is introduced. Layout providers that implement this interface need to be able to predict the output size of their layout before they compute the layout. In the layout these nodes will not be scaled down. Parallel nodes have the further restriction that their parent and children must all be hierarchical nodes. The following properties have to be set on a parallel node:
- `org.eclipse.elk.topdownLayout=true`
- `org.eclipse.elk.topdown.nodeType=PARALLEL_NODE`
- `org.eclipse.elk.algorithm` must be set to an algorithm that implements `ITopdownLayoutProvider`
- `org.eclipse.elk.topdown.hierarchicalNodeWidth` (*optional: defaults work too*)
- `org.eclipse.elk.topdown.hierarchicalNodeAspectRatio` (*optional: defaults work too*)


### Topdown Packing Algorithm
This algorithm is a simple box packing algorithm that places equally sized nodes in a grid and eliminates any whitespace at the end by expanding nodes. It implements the `ITopdownLayoutProvider` interface and using it for topdown layout is its primary purpose although it can also be used on its own. The algorithm has two phases (Node placement and Whitespace elimination) for both of which there is currently one available strategy.


### New Properties
In this section the new topdown layout specific properties are explained in more detail.

#### Topdown Layout 
`org.eclipse.elk.topdownLayout: boolean`
Tells the recursive layout engine that a graph should be laid out using topdown layout. Must be set to the same value for all nodes of the graph. The default value is false.

#### Topdown Scale Factor 
`org.eclipse.elk.topdown.scaleFactor: double`
This property is calculated during topdown layout and is later used to correctly render the diagram. The topdown scale factor defines the amount by which the nodes children (as a group) need to be scaled so that they fit in their available space.

#### Topdown Hierarchical Node Width 
`org.eclipse.elk.topdown.hierarchicalNodeWidth: boolean`
This property is important when using parallel nodes. This value defines the width of each child node. This needs to be set on the parent and applies to all children. This is important so that the `ITopdownLayoutProvider` can provide a size prediction.

#### Topdown Hierarchical Node Aspect Ratio 
`org.eclipse.elk.topdown.hierarchicalNodeAspectRatio: boolean`
This property is important when using parallel nodes. This value defines the aspect ratio of each child node. This is then used together with the previous property to determine the height of each node. This needs to be set on the parent and applies to all children. This is important so that the `ITopdownLayoutProvider` can provide a size prediction.

With both of these properties care must be taken when setting the paddings of the children. The paddings must leave enough space for the node i.e. `padding.left + padding.right 

#### Topdown Node Type
`org.eclipse.elk.topdown.nodeType: TopdownNodeTypes`
There are two node types: `HIERARCHICAL_NODE` and `PARALLEL_NODE`. Hierarchical nodes are the basic building blocks of topdown layout they can generally be used without needing to pay attention to many constraints. Parallel nodes allow constructing topdown layouts with a lookahead allowing nodes to have sizes depending on what is inside them. Using them comes with some restrictions as already outlined.

#### Child Area Width and Child Area Height
`org.eclipse.elk.topdown.childAreaWidth: double`, `org.eclipse.elk.topdown.childAreaHeight: double`
These properties are used internally to determine the size occupied by a layout. The properties are there so that algorithms that already compute these values can store them directly for later use. Otherwise they are computed by the recursive layout engine.

#### Topdown Scale Cap
`org.eclipse.elk.topdown.scaleCap: double`
This value sets an upper limit on the resulting topdown scale factor that is possible. The default value is 1, which means that child nodes will never be scaled up. This means that they may not fully use the available space, but they also do not appear larger (relative with respect to their texts, line widths etc.) than their parents as this seems like generally undesirable behaviour. This property allows some flexibility to configure this within a graph.

#### Topdown Size Approximator
`org.eclipse.elk.topdown.sizeApproximator: TopdownSizeApproximator`
A size approximator can only be set on hierarchical nodes and will use a given ELK node to determine a suitable size for the node before computing its layout. The `COUNT_CHILDREN` size approximator uses `sqrt(number_of_children)` as proportional factor for the node sizes. Size approximators can be made arbitrarily complex.

## Requirements
For the result layouts to be rendered correctly the viewer must support the topdown scale factor property as demonstrated in [klighd-vscode/mka/topdown](https://github.com/kieler/klighd-vscode/tree/mka/topdown). When using KlighD to create topdown diagrams, hierarchical nodes require an explicit child area to be defined so that the viewer will know what part of the rendering the topdown scale factor needs to be applied to.

## Example Diagrams
The below images show some ways that topdown layout can be used.

### Layered, Force and Stress used in hierarchical nodes
![topdown_example_combined1](/images/topdown_example_combined1.png)

### Hierarchical and parallel nodes alternating
SCCharts provide a practical use case for alternating hierarchical and parallel nodes because this structure is already inherent in the diagrams through the alternation of regions and states.
![hierarchical_parallel_example](/images/hierarchical_parallel_example.png)

### Large SCChart

![topdown_example5](/images/controller.png)

### Large mixed example
All these features can be mixed quite flexibly, but it can become quite complex. Here is an example showing a mixed composition of node types and algorithms.
![mixed_example](/images/mixed_example.png)

