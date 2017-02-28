---
title: "Algorithm Developers"
menu:
  main:
    identifier: "AlgorithmDevelopers"
    parent: Documentation
    weight: 30
---

While the layout algorithms implemented in ELK already cover a wide range of layout styles, your particular application may have more specific requirements. In these cases, it may become necessary to implement your own layout algorithm, which is what this part of the documentation is all about.

Implementing your own layout algorithm basically consists of the following steps:

1. Install the _Eclipse Layout Kernel SDK_ into your Eclipse development environment.
1. Create and configure a new Eclipse plug-in project.
1. Create and register a `melk` file that describes your algorithm and its support of layout properties.
1. Implement your algorithm, updating the `melk` file along the way.
1. Debug your algorithm.

Note that we have deliberately left out the part of "pulling your hair out while trying to understand which wretched detail of your complicated algorithm causes it to fail spectacularly" (we've been there...).

**Before you start:**
This section assumes that you have basic knowledge of how the _Eclipse Layout Kernel_ works. You should at least have worked through the [Graph Data Structure]({{< relref "documentation/tooldevelopers/graphdatastructure.md" >}}) and [Using Algorithms Directly]({{< relref "documentation/tooldevelopers/usingalgorithmsdirectly.md" >}}) sections.

**Once you are ready to start:**
Use the navigation bar to the right to work your way through each of those steps.
