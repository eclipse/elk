---
title: "Creating a New Project"
menu:
  main:
    identifier: "CreatingANewProject"
    parent: "AlgorithmDevelopers"
    weight: 20
---

Layout algorithms are developed as _Eclipse Plug-in Projects_. Let's work through creating a new project using our wizard and what that project consists of.


## Creating a New Project

Follow these steps to create a new plug-in:

1. From the _File_ menu, select _New - Project..._.
1. From the _Plug-in Development_ category, select _Plug-in Project_ and click _Next_.
1. Configure your project's basic settings, in particular its name, and click _Next_.

    {{< image src="algdev_creatinganewproject_step1.png" alt="Step 1 of the New Plug-In Project Wizard." >}}

1. Configure the project's properties as you see fit and click _Next_.

    {{< image src="algdev_creatinganewproject_step2.png" alt="Step 2 of the New Plug-In Project Wizard." >}}

1. Select the _Layout Algorithm_ project template. This will cause your project to be initialized with all the necessary files for a simple layout algorithm.

    {{< image src="algdev_creatinganewproject_step3.png" alt="Step 3 of the New Plug-In Project Wizard." >}}

1. The template requires a name for your layout algorithm. This will usually derive from your project's name. Click _Finish_ once you have settled on a name.

    {{< image src="algdev_creatinganewproject_step4.png" alt="Step 4 of the New Plug-In Project Wizard." >}}

The _Package Explorer_ now shows a new project for your plug-in.

{{< image src="algdev_creatinganewproject_project.png" alt="The new project in all its glory." >}}


## The Components of Your New Project

The wizard creates the following things for your:

* A `MANIFEST.MF` file with the necessary dependencies to the Eclipse Layout Kernel so you can use our data structures.

* A file called `ILayoutMetaDataProvider` which registeres your new layout algorithm with ELK so it can be used.

* A `.melk` file which describes your layout algorithm and the options it supports (see [this page]({{< relref "documentation/algorithmdevelopers/metadatalanguage.md" >}}) for more information).

* A sample implementation with a simple basic layout provider. Studying this implementation will teach you a lot about how to develop layout algorithms with ELK (see [this page]({{< relref "documentation/algorithmdevelopers/algorithmimplementation.md" >}}) for more information).

Somewhat strangely, perhaps, the `ILayoutMetaDataProvider` file is located in `/src/META-INF/services` instead of `/META-INF/services`. There is a reason for that. When we start a project in Eclipse, the non-Java content of its source folders is copied into a `bin` folder, which is then put on the class path. Since `/META-INF` is not part of a source folder, putting the service file there would mean that the service is not available when started from Eclipse. If you happen to despise this solution, simply define a separate top-level folder, configure it as a source folder, and move `/src/META-INF` there. Whatever you do, however, you will always end up with two `META-INF` folders in your project.
