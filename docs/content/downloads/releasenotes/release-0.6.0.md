---
title: "0.6.0"
menu:
  main:
    parent: "ReleaseNotes"
    weight: 60
---

* [Release log](https://projects.eclipse.org/projects/modeling.elk/releases/0.6.0)
* [Documentation](https://download.eclipse.org/elk/updates/releases/0.6.0/elk-0.6.0-docs.zip)
* [Update site](https://download.eclipse.org/elk/updates/releases/0.6.0/)
* [Zipped update site](https://download.eclipse.org/elk/updates/releases/0.6.0/elk-0.6.0.zip) (for offline use)
* [Maven central](https://repo.maven.apache.org/maven2/org/eclipse/elk/) (for building pure Java projects that use ELK)
* [Meta data language compiler](https://download.eclipse.org/elk/maven/releases/0.6.0) (for building layout algorithms with Maven)



## Details

Besides the usual bunch of bug fixes, this release adds a number of neat enhancements and feature requests, among them the following:

* A complete re-implementation of how ELK Layered routes self loops which should fix all of the problems we saw in release 0.5.0.
* A rectangle packing algorithm which tries to place rectangles efficiently.
* A number of features geared at layout algorithm developers, such as:
    * A plug-in project template which initializes layout algorithm projects.
    * A new logging framework and views to debug layout algorithms.
    * A proper unit testing framework for layout algorithms.

Some changes may cause clients to break and may change layout results.

View the release [at Eclipse](https://projects.eclipse.org/projects/modeling.elk/releases/0.6.0) for links to the list of closed issues.
