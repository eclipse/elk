---
layout: wiki
title: Eclipse Layout Kernel Structure
type: wiki
---
At its core, the Eclipse Layout Kernel consists of three layers of technology.

**TODO: Insert pretty diagram.**

1. At its most basic, ELK provides a proper EMF-based graph data structure to represent graphs as well as implementations of a number of automatic layout algorithms. These algorithms can be invoked directly, even though most applications wouldn't want to do so. Everything in here can readily be used in pure Java applications.

1. The ELK core consists of everything necessary to run automatic layout algorithms on a given instance of our graph data structure. This is what most pure-Java applications will want to use since it adds a lot of convenience features, especially when it comes to the layout of more complex graphs.

1. The ELK service layer knows how to connect diagram editors to the ELK core. This is where most Eclipse-based tools will come in: they tell the service layer how to turn their internal diagram data structures into ELK's graph data structure, and pretty much leave the rest to the ELK service.
