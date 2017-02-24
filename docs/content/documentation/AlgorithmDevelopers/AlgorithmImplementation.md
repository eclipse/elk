---
title: "Algorithm Implementation"
menu:
  main:
    identifier: "AlgorithmImplementation"
    parent: "AlgorithmDevelopers"
    weight: 40
---

Once everything is set up, it is time to actually implement your algorithm. The problem your layout algorithm has to solve can be summarized as follows: given an input graph (possibly with existing coordinates), compute coordinates for all graph elements and routings for all edges (subject to layout properties the graph is annotated with) and annotate the layout graph accordingly. Note that the input graph defines the layout problem, but also carries the resulting coordinate assignment after your algorithm has executed.

While developing your algorithm, you will regularly switch back and forth between doing that and changing [your metadata]({{< ref "documentation/AlgorithmDevelopers/MetadataLanguage.md">}}).


## Your Algorithm's Main Class

However many classes a layout algorithm consists of, it always provides one entry class that inherits from `AbstractLayoutProvider` and implements the single most important method for your algorithm:

```java
void layout(KNode layoutGraph, IElkProgressMonitor progressMonitor);
```

Let's go through the parameters in reverse order (because of reasons). The `progressMonitor` parameter should be used to track progress and check if the user wants to cancel the layout operation. Actually canceling when the user wants to cancel is one of those features that will help your software stand out from many other programs, so take this opportunity to shine!

The `layoutGraph` parameter is more important, however. It defines the layout problem your algorithm should solve, in several ways. First, it defines the structure of the graph to be laid out: which nodes exist, how are they connected, and so on. Second, each graph element can have [layout options]({{< ref "documentation/ToolDevelopers/GraphDataStructure/LayoutOptions.md">}}) attached to it that are supposed to influence what your layout algorithm does with them. And third, each element may have pre-existing [coordinates or bend points]({{< ref "documentation/ToolDevelopers/GraphDataStructure/CoordinateSystem.md">}}) associated with it that your algorithm may want to make use of. Those existing coordinates will be overwritten by your algorithm to hold the new, computed coordinates.

Note that the layout graph may contain nodes that themselves contain further nodes. By default, layout algorithms are only supposed to compute coordinates for the direct children of the `layoutGraph` and then set the size for the `layoutGraph` itself. However, if your layout algorithm supports hierarchical layout, and if hierarchical layout is requested (which is done through the `CoreOptions.HIERARCHY_HANDLING` layout option), you will also compute coordinates for children of children.

There are two more methods your algorithm can, but does not have to implement:

* `void initialize(String parameter);`

    This method can be used to initialize data structures and prepare things. This method is called exactly once when an instance of your `AbstractLayoutProvider` subclass is created. Note that a single instance of your class can be used for multiple layout runs.

    The `parameter` parameter is a bit tricky. Most layout algorithms won't have any need for it, but some may adjust their behavior depending on its value. One example is our interface to the [Graphviz](http://www.graphviz.org/) library that provides different layout algorithms. We only implement a single subclass of `AbstractLayoutProvider`, but each instance is passed a parameter value that indicates which of the Graphviz algorithms it will execute when the `layout(...)` method is called. Of course, which parameter to pass must be defined in [your metadata file]({{< ref "documentation/AlgorithmDevelopers/MetadataLanguage.md">}}).

* `void dispose();`

    Called before your `AbstractLayoutProvider` subclass is thrown towards the garbage collector.


## Layout Options

It is worth reiterating here that it is important which `IProperty` instance your algorithm uses to retrieve the value of a layout option set on a graph element. Since the [ELK metadata tooling]({{< ref "documentation/AlgorithmDevelopers/MetadataLanguage.md">}}) generates a separate class with a complete set of `IProperty` instances for each algorithm, it makes sense to use these instances. The reason is that using them ensures that you get the correct default values configured for your layout algorithm when accessing layout options that were not set on a graph element.
