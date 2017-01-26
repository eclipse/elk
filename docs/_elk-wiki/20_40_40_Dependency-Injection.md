---
layout: wiki
title: Dependency Injection
type: wiki
parent: Using Eclipse Layout
---
As you have learned, the basic connection between a diagram editor and the Eclipse Layout Kernel is established through an `ILayoutSetup` implementation registered with an extension point. What an `ILayoutSetup` basically does is to provide a Google Guice dependency injector that is then used to instantiate all the classes that play a part in making layout happen in the Eclipse layer.

If you have not used dependency injection yet: ELK will ask the injector to retrieve an implementation of a particular type, and the injector knows which implementation to retrieve. It is the mapping between types and implementations that you can customize in the injector your `ILayoutSetup` returns. By having it retrieve different implementations, you can change how ELK behaves.

The rest of this page tries to give you an idea of what types ELK will request and when you may want to change to different implementations.

# Absolutely Necessary

* **`IDiagramLayoutConnector`**

  This is the one interface you will definitely want to implement. Your connector will tell ELK how to turn your diagrams into a KGraph that ELK knows how to work with. Also, your connector knows how to apply the computed layout information back to your diagram.

# Nice to Have

* **`ILayoutConfigurationStore.Provider`**

  ELK's _Layout View_ allows users to configure layout properties for diagram elements. The layout view needs to know how to actually apply and possibly persist property settings for each element. This is what a `ILayoutConfigurationStore` does. Each configuration store is responsible for a single diagram element. The `ILayoutConfigurationStore.Provider` knows how to retrieve the configuration store for a given element.

  Not every application will want to add support for the layout view. In fact, it can be argued that layout should just work instead of overwhelming the user with too many cryptic options. If you do not need the layout view, you do not need to supply an implementation of `ILayoutConfigurationStore.Provider`.

* **`Provider<LayoutOptionValidator>`**

# Not That Important

* **`LayoutConfigurationManager`**

* **`LayoutPropertySourceProvider`**

* **`DiagramLayoutEngine`**

  The diagram layout engine provides static methods as entry points to layout. Those static methods use your injector to obtain a diagram layout engine instance, which you can use to return a specialized subclass. You will usually not feel the need to do so, though.

* **`IGraphLayoutEngine`**

  Bound to the `RecursiveDiagramLayoutEngine` by default. You will usually not have to provide any special implementations, the reason being that this engine already knows how to invoke layout on hierarchical graphs. But you could!
