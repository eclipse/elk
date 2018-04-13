---
title: "Creating a New Project"
menu:
  main:
    identifier: "CreatingANewProject"
    parent: "AlgorithmDevelopers"
    weight: 20
---

Layout algorithms are developed as _Eclipse Plug-in Projects_. There's a few things to setup to get your project ready, which is what we will work through here.

{{% note title="If you have nothing better to do&hellip;" mode="info" %}}
[This ticket](https://github.com/eclipse/elk/issues/29) is all about adding an _ELK Project Wizard_ to the ELK SDK that will help people create new layout algorithm projects that are already perfectly setup. If you want to make people's lifes better but don't know how, here's your chance!
{{% /note %}}


## Creating a New Plug-in

Follow these steps to create a new plug-in:

1. From the _File_ menu, select _New - Project..._.
1. From the _Plug-in Development_ category, select _Plug-in Project_ and click _Next_.
1. Configure your project's basic settings, in particular its name, and click _Next_.
1. Configure your project's content. The _Properties_ should be somewhat sensible, but don't really matter all that much. In the _Options_ section, uncheck everything (layout algorithm projects normally don't need an activator class, nor do they normally make contributions to the UI). Also, don't create a rich client application. Click _Finish_.

{{< image src="algdev_creatinganewproject_wizard.png" alt="Configuring a new layout algorithm project." >}}

The _Package Explorer_ now shows a new project for your plug-in which we will configure in the next section.


## Setting Up Your Plug-in

Your plug-in needs to declare dependencies to ELK's most important plug-ins: `org.eclipse.elk.graph` (which contains the graph model that will be the input to your layout algorithm) and `org.eclipse.elk.core` (which contains the core ELK code). Follow these steps to add the dependencies:

1. Open the `MANIFEST.MF` file, located in your plug-in's `META_INF` folder. The _Plug-in Manifest Editor_ will open up, which is divided into several pages that you can switch between using the controls at the bottom of the editor.
1. Open the editor's _Dependencies_ tab.
1. Click the left _Add..._ button to add a dependency. In the dialog that pops up, search for the `org.eclipse.elk.graph` plug-in and click _OK_.
1. Repeat for ELK's core plug-in, `org.eclipse.elk.core`.

{{< image src="algdev_creatinganewproject_dependencies.png" alt="Configuring a new layout algorithm project." >}}


## Creating a Layout Provider

While we will discuss the details of implementing the actual layout algorithm  [later]({{< relref "documentation/algorithmdevelopers/algorithmimplementation.md" >}}), it is a good idea to already create its main class now for us to be able to reference it. Create a new Java class with the following properties:

* The package should be your plug-in's source package. That is, it should be named just like the plug-in. (This is not strictly necessary, but a common convention.)
* Give it a name that ends in `LayoutProvider`.
* Its superclass must be set to `org.eclipse.elk.core.AbstractLayoutProvider`.

Your class will look something like this:

```java
package cds.layout.simple;

import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

public class SimpleLayoutProvider extends AbstractLayoutProvider {

    @Override
    public void layout(ElkNode layoutGraph, IElkProgressMonitor progressMonitor) {
        // TODO Auto-generated method stub

    }

}
```
