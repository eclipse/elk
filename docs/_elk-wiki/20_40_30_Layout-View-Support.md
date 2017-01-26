---
layout: wiki
title: Layout View Support
type: wiki
parent: Using Eclipse Layout
---
If UI components are installed, ELK contributes the _Layout View_ to the workbench:

![ELK Layout View](graphics/layout_view.png)

The layout view is a kind of properties view specialized for setting layout properties. It listens to selection changes and, if it recognizes that something is an element of a graph ELK could layout, allows users to customize the layout options used to do so.

It is a fair question whether users should be given access to this view or not. At the ELK project, we primarily consider it a feature for developers and perhaps for power users. This is because the amount of layout options available is quite high. Also, the options themselves are often rather technical, requiring rather detailed knowledge of layout algorithms. It is probably not something you would want to give your regular user to play with.

Support for the layout view does not happen automatically, though. On this page, we will discuss how to support it.


# Layout Configuration Stores

**TODO: Write documentation.**
