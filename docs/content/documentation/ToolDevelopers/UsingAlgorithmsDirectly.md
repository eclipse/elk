---
title: "Using Algorithms Directly"
menu:
  main:
    identifier: "UsingAlgorithmsDirectly"
    parent: ToolDevelopers
    weight: 20
---

All layout algorithms implemented in the Eclipse Layout Kernel can be called directly. To do so, you must have an instance of our ElkGraph data structure that can be fed to the layout algorithm, which will then compute a layout for the graph's elements.

**Warning:** This is very low-level stuff. If you want to stay in the pure-Java domain (as opposed to building an Eclipse-based application), you will probably want to use [the next layer of abstraction]({{< ref "documentation/ToolDevelopers/UsingPlainJavaLayout.md" >}}).


## The Entry Point to Layout Algorithms

When using layout algorithms directly, the central most important type you will deal with is the `AbstractLayoutProvider` class. A layout provider basically consists of three methods:

```java
void initialize(String parameter);
void layout(KNode layoutGraph, IElkProgressMonitor progressMonitor);
void dispose();
```

Each layout algorithm provides a subclass of `AbstractLayoutProvider` that is the entry point to the algorithm. Using it is a matter of doing the following:

1. Create an ElkGraph to be laid out (see our [graph documentation]({{< ref "documentation/ToolDevelopers/GraphDataStructure.md" >}}) for how to do so). The KGraph will be based on the diagram you want your application to display.

1. Configure how the layout algorithm should layout your graph by setting [layout options]({{< ref "documentation/ToolDevelopers/GraphDataStructure/LayoutOptions.md" >}}) on the graph's elements. Check the documentation of the layout algorithm you want to use for which layout options it supports and what they do, exactly.

1. Obtain an instance of the layout algorithm's implementation of `AbstractLayoutProvider`. For our layer-based algorithm, for example, this is the `LayeredLayoutProvider`. Be sure to call `initialize(...)` on it. The parameter can be `null` in most cases, unless specified otherwise.

1. Call the `layout(...)` method and pass it the graph to be laid out as well as a progress monitor to track progress. The progress monitor will allow users to see how long layout will take and to cancel the operation. If you do not care to provide your own progress monitor, simply use `BasicProgressMonitor`, which is our default implementation. Once the `layout(...)` method returns, the result will be stored in the layout-related fields of the graph.

1. Either keep your layout provider instance around to be used again for the next layout run, or call `dispose()` on it and throw it away.
