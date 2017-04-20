---
title: "Using Plain Java Layout"
menu:
  main:
    identifier: "UsingPlainJavaLayout"
    parent: ToolDevelopers
    weight: 30
---

While layout algorithms [can be called directly]({{< relref "documentation/tooldevelopers/usingalgorithmsdirectly.md" >}}), it is usually a better idea not to do so except in the simplest of cases. There are several reasons for that:

1. When calling a layout algorithm directly, your code is hardwired to that implementation and looses a bit of flexibility.

1. Most importantly, as graphs get more complex there are more and more details to be aware of when executing layout. This is especially true for compound graphs: graphs with nodes that contain further graphs themselves.

The best way to invoke automatic layout is through a fully-fledged implementation of `IGraphLayoutEngine`. ELK provides a convenient implementation: the `RecursiveGraphLayoutEngine`. But before we can explain that, we need to provide a big of background regarding the `LayoutMetaDataService`.


## The Layout Meta Data Service

To know about which layout algorithms and layout options are available, ELK keeps a central registry of layout meta data: the `LayoutMetaDataService`. The recursive graph layout engine looks at which layout algorithm should layout a given part of the layout graph and uses the meta data service to obtain an instance of that algorithm.

When running in an Eclipse context, the meta data service is magically populated. In a plain Java application, you will have to populate the service yourself. Layout meta data are provided by `ILayoutMetaDataProvider`s. What you need to do is to obtain an instance of each relevant meta data provider and register it with the meta data service.

One such meta data provider is always automatically registered: `CoreOptions`. This will give you access to our most basic layout algorithms: _Fixed Layout_, _Box Layout_, and _Random Layout_ (at the time of writing). To use more layout algorithms, you will have to find the algorithm's implementation of `ILayoutMetaDataProvider` and register that. For example, using _ELK Layered_ requires the following registration:

```java
LayoutMetaDataService service = LayoutMetaDataService.getInstance();
service.registerLayoutMetaDataProvider(new LayeredMetaDataService());
```


## Using the Recursive Graph Layout Engines

When using the recursive graph layout engine, there is exactly one important method to be aware of:

```java
void layout(KNode layoutGraph, IElkProgressMonitor progressMonitor);
```

This method provides the entry point to automatic layout and applies the layout algorithms as configured in the layout graph (more details below). To get everything working, follow these steps after you have populated the layout meta data service:

1. Create an ElkGraph to be laid out (see our [graph documentation]({{< relref "documentation/tooldevelopers/graphdatastructure.md" >}}) for how to do so). The ElkGraph will be based on the diagram you want your application to display.

1. Configure which layout algorithm should be used to layout your graph and how the layout algorithm should layout your graph by setting [layout options]({{< relref "documentation/tooldevelopers/graphdatastructure/layoutoptions.md" >}}) on the graph's elements. Check the documentation of the layout algorithm you want to use for which layout properties it supports and what they do, exactly.

1. Obtain an instance of `RecursiveGraphLayoutEngine` and call its `layout(...)` method. Pass it the graph to be laid out as well as a progress monitor to track progress. The progress monitor will allow users to see how long layout will take and to cancel the operation. If you do not care to provide your own progress monitor, simply use `BasicProgressMonitor`, which is our default implementation. Once the `layout(...)` method returns, the result will be stored in the layout-related fields of the graph.

1. Either keep the graph layout engine around to be used again or throw it away.
