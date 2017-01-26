---
layout: wiki
title: Using Eclipse Layout
type: wiki
parent: Tool Developers
---
Compared to the two more basic variants, using the Eclipse Layout Kernel within Eclipse introduces the largest amount of general magic to the process. This page explains the basics of how the layout kernel performs automatic layouts. Based on these information, sub-pages then describe how to integrate your tool with the layout kernel by filling in the details the overview decided to best leave out.


# How Layout Works in Eclipse

Layout in Eclipse revolves around the `DiagramLayoutEngine`, which in turn builds upon the `IGraphLayoutEngine` interface that layout in pure Java revolves around. It adds a whole layer that is mainly concerned with three things: extracting the graph data from a given diagram editor that automatic layout should be run on, configuring automatic layout, running automatic layout (yes, we are full of surprises), and applying the layout information back to the editor. The layer can be customized to taste through dependency injection. We will come back to that topic in a few minutes.

The `DiagramLayoutEngine` class provides a number of overloaded static `invokeLayout(...)` methods. Contrary to the entry points in `IGraphLayoutEngine`, these methods do not expect a KGraph ready to throw at layout algorithms, but instead expect either the editor layout should be run on (as an `IWorkbenchPart`) or a diagram element displayed in an editor (as a simple `Object`). Extracting a KGraph from this is what connecting your editor to the Eclipse Layout Kernel is all about: your main task is to implement the `IDiagramLayoutConnector` interface whose job is exactly that.

Let's assume that one of the `invokeLayout(...)` methods is called. The layout engine now checks if it knows about a layout connector that can translate the content of the workbench part or the diagram object itself into a KGraph. Layout connectors are registered with the diagram layout engine through the `org.eclipse.elk.core.service.layoutConnectors` extension point. In fact, this is not quite right. It is not the layout connector itself which is registered, but a class that implements `ILayoutSetup`. This class knows which workbench parts and / or diagram parts it supports and how to supply the correct layout connector implementation. This is where we come back to dependency injection: the setup does not create an `IDiagramLayoutConnector` directly, but instead creates an injector that knows how to do so. By configuring the injector appropriately, a lot of details of how the layout process works can be customized by supplying custom implementations of the different components involved in the process.

By now, the diagram layout engine has an `ILayoutConnector` which it can use to get its hands at a KGraph it can work with. In the easy case, your layout connector will have already setup the layout options on your KGraph such that everything is ready to go. However, ELK does support advanced configuration mechanisms which are explained in one of the sub pages.

The diagram layout engine now has a properly configured KGraph that automatic layout can be run on. Incidentally, this is exactly what the `IGraphLayoutEngine` expects, so the diagram layout engine now simply calls the graph layout engine to do the heavy layout lifting. Once that call returns, the KGraph contains all the layout information just calculated.

The only thing that remains to be done is to apply the calculated layout information stored in the KGraph back to the diagram we originally wanted to invoke layout on. This again is the job of the `IDiagramLayoutConnector` implementation the diagram layout engine found at the beginning. Once that job is done, the layout run is complete and the call to `invokeLayout(...)` returns.
