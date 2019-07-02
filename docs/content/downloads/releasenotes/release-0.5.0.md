---
title: "0.5.0"
menu:
  main:
    parent: "ReleaseNotes"
    weight: 50
---

* [Release log](https://projects.eclipse.org/projects/modeling.elk/releases/0.5.0)
* [Documentation](http://download.eclipse.org/elk/updates/releases/0.5.0/elk-0.5.0-docs.zip)
* [Update site](http://download.eclipse.org/elk/updates/releases/0.5.0/)
* [Zipped update site](http://download.eclipse.org/elk/updates/releases/0.5.0/elk-0.5.0.zip) (for offline use)
* [Maven central](https://repo.maven.apache.org/maven2/org/eclipse/elk/) (for building pure Java projects that use ELK)
* [Meta data language compiler](http://download.eclipse.org/elk/maven/releases/0.5.0) (for building layout algorithms with Maven)



## Details

Besides the usual bunch of bug fixes, this release adds a number of neat enhancements and feature requests, among them the following:

* ELK Layered's support for self loops has been improved a lot.
* A new [box layout algorithm](https://www.eclipse.org/elk/reference/algorithms/org-eclipse-elk-box.html) improves how nodes can be packed. This is particularly interesting for graphs with many subgraphs which are not connected to each other.
* A new unit testing framework will allow us to test graph algorithms. Future releases will enable the framework and contribute tests.

Some changes may cause clients to break and may change layout results.

View the release [at Eclipse](https://projects.eclipse.org/projects/modeling.elk/releases/0.5.0) for links to the list of closed issues.
