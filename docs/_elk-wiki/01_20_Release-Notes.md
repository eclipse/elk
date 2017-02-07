---
layout: wiki
title: Release Notes
type: wiki
parent: Home
---

# Release 0.2.0

A major overhaul of ELK's infrastructure. This is an API breaking release. The following are probably the biggest issues we tackled:

* Refactored the central graph data structure. This is a biggie: we made the whole graph data structure easier to understand and easier to use, but of course broke compatibility with old diagram layout connectors in the process.
* The layout options have been cleaned up. This means that existing code may have to be updated to use the new options.
* We now have generated documentation for layout algorithms and layout options [available at our documentation website](https://eclipse.github.io/elk/).
* A textual language to specify ELK graphs is now part of the ELK SDK.

View the release [at Eclipse](https://projects.eclipse.org/projects/modeling.elk/releases/0.2.0) for links to the list of closed issues.


# Release 0.1.1

A bugfix release.

* Fixed a problem where edges with labels end up being routed in an ambiguous way. ([#96](https://github.com/eclipse/elk/issues/96)).

View the release [at Eclipse](https://projects.eclipse.org/projects/modeling.elk/releases/0.1.1) for links to the list of closed issues.


# Release 0.1.0

The initial release under the new Eclipse umbrella.

View the release [at Eclipse](https://projects.eclipse.org/projects/modeling.elk/releases/0.1.0) for links to the list of closed issues.