---
title: "0.7.1"
menu:
  main:
    parent: "ReleaseNotes"
    weight: 71
draft: true
---

* [Release log](https://projects.eclipse.org/projects/modeling.elk/releases/0.7.1)
* [Documentation](https://download.eclipse.org/elk/updates/releases/0.7.1/elk-0.7.1-docs.zip)
* [Update site](https://download.eclipse.org/elk/updates/releases/0.7.1/)
* [Zipped update site](https://download.eclipse.org/elk/updates/releases/0.7.1/elk-0.7.1.zip) (for offline use)
* [Maven central](https://repo.maven.apache.org/maven2/org/eclipse/elk/) (for building pure Java projects that use ELK)



## Details

This is mainly a bugfix release. See GitHub for the full [list of resolved issues](https://github.com/eclipse/elk/milestone/16?closed=1).


### New Features and Enhancements

* [#713](https://github.com/eclipse/elk/pull/713): The Stress and Force layout algorithms now properly place inline edge labels.


### Bugfixes

* [#701](https://github.com/eclipse/elk/issues/701), [#732](https://github.com/eclipse/elk/pull/732): Space is now properly reserved around port labels with fixed position.
* [#682](https://github.com/eclipse/elk/issues/682), [#683](https://github.com/eclipse/elk/pull/683): With non-standard layout directions (other than left-to-right), node label paddings were not applied correctly.
* [#707](https://github.com/eclipse/elk/pull/707): ELK's handling of the short form of layout options could get confused when registering additional layout algorithms, resulting in undefined behavior.
* [#700](https://github.com/eclipse/elk/issues/700), [#731](https://github.com/eclipse/elk/pull/731): ELK Layered now properly interprets hierarchy mode `INCLUDE_CHILDREN` again.
* [#726](https://github.com/eclipse/elk/issues/726): Under certain conditions, ELK Layered failed to route orthogonal edges properly, resulting in slanted edge segments and generally hideous layouts.
* [#696](https://github.com/eclipse/elk/issues/696), [#705](https://github.com/eclipse/elk/pull/705): ELK Layered's interactive mode could cause a `NoSuchElementException`.
* [#680](https://github.com/eclipse/elk/issues/680), [#681](https://github.com/eclipse/elk/pull/681): With external ports and negative port offsets, ELK Layered did not place child nodes correctly.
* [#710](https://github.com/eclipse/elk/pull/710): ELK Force sometimes imported edge labels several times, resulting in weird placements.
