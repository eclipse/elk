---
title: "Algorithm Debugging"
menu:
  main:
    identifier: "AlgorithmDebugging"
    parent: "AlgorithmDevelopers"
    weight: 50
---

The Eclipse Layout Kernel SDK provides two views built specifically for debugging your layout algorithm: the layout graph view and the execution time view.


## Layout Graph View

{{< image src="layout_graph_view.png" alt="Layout Graph View" >}}

The layout graph view registers with the `DiagramLayoutEngine` to be notified whenever a layout run finishes and displays the layout graph exactly as it comes out of the layout algorithm, without any modifications applied. Use this view to check if your algorithm works and if you got the coordinate system right.

Note that the layout graph view allows you to export the displayed graph as a PNG image file. It also allows you to display debug graph files (see below).

Open the layout graph view by clicking _Window_ -> _Show View_ -> _Other_ and select _Layout Graph_ from the _Eclipse Diagram Layout_ category.


## Layout Time View

{{< image src="layout_time_view.png" alt="Layout Time View" >}}

The layout time view registers with the `DiagramLayoutEngine` to be notified whenever a layout run finishes and displays which steps the layout run consisted of and, if enabled, how much time each step took to run. Measuring these execution times has to be explicitly enabled in the preferences:

{{< image src="layout_time_view_enable.png" alt="Enabling layout time measurements" >}}

Once enabled, the view displays two values for each item: the _time_ and the _local time_. The former is the amount of time spent for an item and all of its sub-items. Local time is the time spent on the item alone, without any sub-items (this value is omitted for items that do not have any sub-items, since it would be equal to the normal time value anyway).

To take advantage of the layout time view, your algorithm must make proper use of the `IElkProgressMonitor` passed to it. The tree of progress monitors (and sub-monitors) is what gets displayed in the layout time view. Be sure to do something like this:

```java
monitor.begin("My rather good layout algorithm", 2);

executePhase1(monitor.subTask(1));
executePhase2(monitor.subTask(1));

monitor.done();
```

Open the layout graph view by clicking _Window_ -> _Show View_ -> _Other_ and select _Layout Time_ from the _Eclipse Diagram Layout_ category.


## Debug Files

The ELK preference page (see above) contains another setting: _Debug graph output_. Enabling this will cause the `DiagramLayoutEngine` to save the layout graph just before automatic layout is run. The graphs are placed in your user folder, in a subfolder called `elk/diagram_layout_engine`. The graph files can be displayed by the layout graph view, even though that usually is not very helpful since they may not contain any valid layout data yet.
