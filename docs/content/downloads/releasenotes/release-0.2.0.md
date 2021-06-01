---
title: "0.2.0"
menu:
  main:
    parent: "ReleaseNotes"
    weight: -20
---

* [Release log](https://projects.eclipse.org/projects/modeling.elk/releases/0.2.0)
* [Update site](https://download.eclipse.org/elk/updates/releases/0.2.0/)
* [Zipped update site](https://download.eclipse.org/elk/updates/releases/0.2.0/elk-0.2.0.zip) (for offline use)
* [Maven Repository](https://download.eclipse.org/elk/maven/releases/0.2.0) (for meta data language compiler)


## DEtails

A major overhaul of ELK's infrastructure. This is an API breaking release. The following are probably the biggest issues we tackled:

* Refactored the central graph data structure. This is a biggie: we made the whole graph data structure easier to understand and easier to use, but of course broke compatibility with old diagram layout connectors in the process.
* The layout options have been cleaned up. This means that existing code may have to be updated to use the new options.
* We now have generated reference documentation for layout algorithms and layout options.
* A textual language to specify ELK graphs is now part of the ELK SDK.

View the release [at Eclipse](https://projects.eclipse.org/projects/modeling.elk/releases/0.2.0) for links to the list of closed issues.
