---
title: "Connecting to ELK"
menu:
  main:
    parent: "UsingEclipseLayout"
    weight: 10
---

In our [our basic introduction to automatic layout in Eclipse]({{< relref "documentation/tooldevelopers/usingeclipselayout.md" >}}), we have seen how the diagram layout engine needs someone to extract a proper ElkGraph from whatever layout is invoked on. This is what you, as a tool developer, have to supply. While there are already implementations for different graph editing frameworks to build upon, this page describes how doing so from scratch works.

To connect to ELK, there are two things you will have to do:

1. Create an `ILayoutSetup` implementation and register it with our extension point.
1. Create an `IDiagramLayoutConnector` implementation.

The rest of this page will look at each of these in turn.


## The Layout Setup

An `ILayoutSetup` implementation consists of just two methods:

```java
boolean supports(Object object);
Injector createInjector(Module defaultModule);
```

To be able to implement the interface, you may have to add a dependency to `org.eclipse.elk.core.service` to your project. Then register your layout setup with the `org.eclipse.elk.core.service.layoutConnectors` extension point.

### The `supports(...)` Method

This method is called by the diagram layout engine to determine if a given setup instance supports extracting an ElkGraph from the given object. The question now of course becomes what that object is. There are two cases. In the first case, it will be an implementation of `IWorkbenchPart`. This is the case if layout is invoked on a diagram editor. If the setup states that it supports that workbench part, the diagram layout connector it supplies knows how to get its hands at the editor's content and how to turn that content into an ElkGraph. In the second case, the object passed to `supports(...)` will be an object from that editor's content. For GMF editors, this will be an implementation of `IGraphicalEditPart`.

A typical implementation of this method will look something like this:

```java
public boolean supports(final Object object) {
    // This method may be invoked on a whole collection of elements selected
    // in an editor
    if (object instanceof Collection) {
        // Check if we support layout on at least one of the selected objects
        for (Object o : (Collection<?>) object) {
            if (o instanceof SomeDiagramElementClassWeSupport) {
                return true;
            }
        }
        return false;
    }

    // If it is not a collection, it may be either a workbench part we support
    // or the diagram element class we already checked for above
    return object instanceof WorkbenchPartImplementation
        || object instanceof SomeDiagramClassWeSupport;
}
```


### The `createInjector(...)` Method

If the diagram layout engine has determined which setup supports layout on a given object, it will use that setup to get its hands on an injector that can supply implementations of the different components involved in the layout process. The most important of these is an implementation of `IDiagramLayoutConnector`, which we will look at in a minute. A typical implementation will look something like this:

```java
public Injector createInjector(final Module defaultModule) {
    // Modules basically provide a mapping between types and implementations
    // to instantiate whenever an instance of the type is requested. We use
    // the default module supplied by ELK and override that with custom
    // overrides to get our IDiagramLayoutConnector to enter the picture.
    return Guice.createInjector(
        Modules.override(defaultModule).with(new AwesomeLayoutModule()));
}

public static class AwesomeLayoutModule implements Module {
    @Override
    public void configure(final Binder binder) {
        // This is the most important binding
        binder.bind(IDiagramLayoutConnector.class)
              .to(MyAwesomeDiagramLayoutConnector.class);
    }
}
```

An implementation can of course add more bindings. See our section on dependency injection for more information on what makes sense here.


## The Layout Connector

An `IDiagramLayoutConnector` implementation consists of the following methods:

```java
LayoutMapping buildLayoutGraph(IWorkbenchPart workbenchPart, Object diagramPart);
void applyLayout(LayoutMapping mapping, IPropertyHolder settings);
```

These methods pretty much correspond to the beginning and the end of the layout process: extracting the layout graph from whatever layout is invoked on, and applying the layout information back to the diagram. Let's take a look at how these methods should be implemented.

### The `buildLayoutGraph(...)` Method

This is the method that turns a diagram into an ElkGraph that ELK can then work with. It produces an instance of the `LayoutMapping` class, which contains information so important to the layout process that we should list them all:

* The root of the created ElkGraph. This is what is later fed into the recursive diagram layout engine to run automatic layout on.
* The top-level diagram part that layout was originally invoked on. This will usually be something that represents the diagram or a part of it in the diagram editor (think `IGraphicalEditPart` for GMF).
* A bi-directional mapping between diagram parts and the ElkGraph elements that were created for them. This is probably the most important bit: it will later allow us to apply the layout information contained in the ElkGraph back to the correct diagram elements.
* The workbench part layout was invoked on, if any. Admittedly, this is less important, which is why it comes last in this list.

Since this method determines the structure of the graph layout algorithms will be run on, it is what has the most impact on what your results will look like. It is thus a good idea to spend some time (and thought) on how to implement it. You should think about the following two aspects: the layout graph's structure, and the layout configuration.

Regarding the layout graph's structure, the main task is to decide which of your diagram elements map to which kinds of layout graph elements. Should an element be represented as a node? Is it a label? Should my nodes have explicit points for edges to attach to? Do certain nodes contain other nodes? Which diagram elements need to be represented in the layout graph in the first place?

Regarding the layout configuration, your layout connector is expected to build a fully configured ElkGraph (except for some advanced configuration issues, which we will look at on another page). This primarily includes setting the correct layout options that yield the results you want. However, this may also include writing the current coordinates of diagram elements into the ElkGraph. This is mostly important for what we call _interactive layout algorithms_: layout algorithms that take current positions into account when calculating new ones instead of simply calculating new coordinates from scratch.


### The `applyLayout(...)` Method

This method accepts two arguments: the `LayoutMapping` created by `buildLayoutGraph(...)`, and an `IPropertyHolder` that may hold additional options controlling the layout process. These options will usually include things such as whether the application of the layout should be animated and whether the diagram zoomed and positioned such that it is completely visible in the editor.

The most important thing the `applyLayout(...)` implementation will do is to iterate over the diagram elements and to apply the layout results back to them. At best, this just means copying values over. At worst, this will include transforming coordinates from the ElkGraph coordinate system back to whatever coordinate system the diagram editor uses.
