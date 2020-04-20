---
title: "Getting Started"
menu:
  main:
    identifier: "GettingStarted"
    weight: 20
---

Automatic layout can be a complex thing, but starting to use the _Eclipse Layout Kernel_ can be as easy as installing a few features from our update site.

{{% note title="A Quick Warning" mode="danger" %}}
This guide refers to the pre-0.1.0 version of the Eclipse Layout Kernel. Since then, much has changed. We will try to get this guide updated as soon as possible. In the meantime, you may want to check out the rest of the documentation.
{{% /note %}}


## Installing Necessary Things

First off, you will have to actually install ELK. Head over to our downloads section and paste the URL of our update site into your Eclipse installation. You will discover four features:

* The core feature contains the basic layout kernel infrastructure. This one you definitely need to install.
* Depending on what technology your editor is based on, you may be lucky enough to find that we have already done much of the work necessary to make your editor work with ELK. We provide out-of-the-box interfaces for GMF-based editors, which work well without customization for many editors. Note that prior to release 0.7.0 there has been support for Graphiti editors as well. If you are interested, feel free to check out the old code.
* While ELK comes with a number of layout algorithms, you may also want to use the [Graphviz](http://www.graphviz.org/) library. Installing the corresponding feature allows ELK to use Graphviz if it is installed on your computer.


## Connecting Your Editor to ELK

Congratulations, you have just installed ELK! (wasn't that hard, I know) Now it's time to actually enable automatic layout for your editor. How this is done exactly differs depending on the technology your editor is based on.

ELK works with a graph data structure called the ElkGraph. To enable layout, it needs to know how to extract the ElkGraph corresponding to the diagram the current editor shows. This is done by looking for an `IDiagramLayoutManager` registered for the current editor and asking it to provide the ElkGraph.

What exactly you need to do to connect ELK to your editor depends on the technology your editor is based on.


### GMF-Based Editors

If your editor is GMF-based, you probably won't be having a hard time: ELK already supports GMF-based editors out of the box. In fact, there will be a new layout button on the tool bar. Click it (or press Ctrl+R, L) to check if everything works.

{{< image src="getting_started_layout_button.png" alt="The layout button triggers layout on your diagram." >}}


### Graphiti-Based Editors

If your editor is based on Graphiti, you're in a similar situation as the GMF people: it may just work out of the box. However, the GraphitiDiagramLayoutManager that comes with ELK may not always make the right choices. In fact, you will most likely need to subclass it to customize how exactly the ElkGraph is built, and then register it with ELK so your custom manager is used for your editor. See the next section for how that is done.


### Custom Editors

If your editor is based on some other technology, ELK doesn't have a pre-built layout connection for you; you will have to build it yourself. To do so, create a class [that implements the `IDiagramLayoutManager` interface]({{< relref "documentation/tooldevelopers/usingeclipselayout/connectingtoelk.md" >}}). Your class needs to be registered with ELK:

```
<extension
   point="org.eclipse.elk.core.service.layoutManagers">

   <manager
      class="your.layout.manager.implementation">
   </manager>
</extension>
```


## Customizing Automatic Layout

Chances are that the layouts you're getting at this point are not perfect for your type of diagram. That is to be expected. Luckily, most algorithms can be configured through the use of _layout options_.

The first thing you will want to do is to experiment with the available options. To do so, open your editor as well as the _Layout View_ provided by ELK. The view will show you the options available for the selected diagram elements and allow you to play around with them.

{{< image src="getting_started_layout_view.png" alt="The layout view allows you to play around with many of the available layout options." >}}

Once you have found settings you like, it's time to configure layout such that these settings are always applied. (You don't want your users having to use the layout view, now, do you?) There are different ways to do so, and we have [more in-depth information on them]({{< relref "documentation/tooldevelopers/graphdatastructure/layoutoptions.md" >}}).

The easiest way is to simply register static configurations with ELK. Static configurations are registered with the org.eclipse.elk.core.layoutConfigs extension point. Each static config consists of three parts:

1.  A class name. If ELK encounters an instance of this type, it will apply the static configuration to it. The class name can be the name of a domain model class, or the name of a class of your editor's view model.
2.  An option ID. This is the identifier of the layout option you actually want ELK to set on the element.
3.  An option value. This is the value you want ELK to set on the element for the layout option.

Now, you may think that you don't have all of these information at hand, but you're actually wrong. You can find all of these information by selecting your diagram element, selecting the layout option in the layout view, and pressing the layout view's info button. The dialog that pops up will give you all the information you need.

{{< image src="getting_started_layout_view_info.png" alt="Finding out more about selected elements and layout options through the layout view." >}}
