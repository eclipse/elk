---
title: "Advanced Configuration"
menu:
  main:
    parent: "UsingEclipseLayout"
    weight: 20
---

What we have learned so far about how automatic layout in Eclipse works was comparatively straightforward: the diagram layout engine looks for a diagram layout connector to get its hands at a configured KGraph, invokes the recursive graph layout engine, and asks the diagram layout connector to apply the results back to the original diagram. As you will have guessed, things _can_ become quite a bit more complex than that.

On this page, we will look at customizing how layout runs are executed, including more advanced options as well as building up chains of layout algorithms executed in series as part of a single layout run.


## Parameters

The `Parameters` class is used to control how layout is executed on a graph. So far, we have only looked at doing a single layout pass over a graph. Parameters allow to add further layout runs afterwards, with the result of one layout run fed into the next. To see why this may be interesting, let's take a layout algorithm that places nodes just the way you like it, but produces edge crossings or slanted edges. Parameters would allow us to add a specialized edge routing algorithm that takes care of producing proper edge routes.

Let us examine the `Parameters` class a bit more closely. Here's what it can do:

* It lets you specify layout runs. Of course, each layout algorithm may expect the graph to be configured differently, so each layout run is specified by adding a `LayoutConfigurator` which is used to configure layout options on the graph before the next layout algorithm is run. We will examine layout configurators in a bit more detail below.

* ELK distinguishes layout options that layout algorithms care about from layout options that may have more meaning for the layout engine or your layout connectors. The latter are called _global settings_ and are stored in the parameters as well. This may for example include settings such as whether to animate the application of layout results, so that your diagram's elements neatly morph into their new places. Of course you are free to introduce your own global settings and feed them into your parameters.

Let's come back to layout configurators and see how they work. Basically, each layout configurator added to your parameters can set an arbitrary number of layout options on different elements of your KGraph. Also, it can be configured to clear all previous configuration stored in the graph. To specify the options to set, there are two possibilities. First, you can specify concrete objects in the graph structure to set options on. And second, you can specify whole classes of graph elements to set options on. Here's some example code that should make everything a bit clearer:

```java
LayoutConfigurator configurator = new LayoutConfigurator();

// Configure layout options for a concrete graph object
configurator.configure(myVerySpecialNode)
    .setProperty(CoreOptions.INTERACTIVE, true)
    .setProperty(CoreOptions.PORT_CONSTRAINTS, PortConstraints.FIXED);

// Configure layout options for all nodes
configurator.configure(KNode.class)
    .setProperty(CoreOptions.ALGORITHM, "org.eclipse.elk.layered")
    .setProperty(CoreOptions.SPACING_NODE, 30.0f);
```

This configurator can then be put into a `Parameters` object that can be passed to the `DiagramLayoutEngine`:

```java
DiagramLayoutEngine.Parameters params = new DiagramLayoutEngine.Parameters();
params.addLayoutRun(configurator);
DiagramLayoutEngine.invokeLayout(workbenchPart, diagramPart, params);
```

The concept of using layout configurators to set layout options on your KGraph seems to contradict what we have said so far: that the layout connector is responsible for producing a KGraph with options already set. In fact, these do not contradict each other, but are simply two different (but combinable) ways of doing things. There are basically three ways of going about it:

1. Having your layout connector set all options on your KGraph, and not using any layout configurator. This is the approach we first learned about.

1. Having your layout connector set only some of the options on your KGraph and using at least one layout configurator to fill in the rest.

1. Having your layout connector set no options at all and using at least one layout configurator to configure everything.

In most cases, the first approach will work perfectly fine. Once your layout process consists of more than one layout run, however, you will probably want to go with either option 2 or option 3. Which of these you take depends on what layout options the involved layout algorithms expect.


## Layout Configuration Management

**TODO:** Explain what the layout configuration manager does and what layout configuration stores do.
