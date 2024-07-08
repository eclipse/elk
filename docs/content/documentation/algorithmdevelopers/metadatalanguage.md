---
title: "ELK Metadata Language"
menu:
  main:
    identifier: "ELKMetadataLanguage"
    parent: "AlgorithmDevelopers"
    weight: 30
---

As described [in other parts of the documentation]({{< relref "documentation/tooldevelopers/usingplainjavalayout.md" >}}), the _Eclipse Layout Kernel_ relies on metadata about all available layout algorithms and the layout options they support. Supplying metadata for your layout algorithm is done by writing an _ELK Metadata File_ in our textual metadata language. The file is used by the ELK SDK to generate the following Java classes:

* An `ILayoutMetaDataProvider` that contains `IProperty` objects for each layout option you declare, along with a method that registers these options and layout algorithm categories with the `LayoutMetaDataService`. It is this class that needs to be registered through the `META-INF/services/ILayoutMetaDataProvider` file.
* One `ILayoutMetaDataProvider` for each algorithm you declare. This contains one `IProperty` object for each layout option your algorithm supports (with the configured default value), as well as a method that registers your algorithm and its supported options with the `LayoutMetaDataService`. You should use the `IProperty` objects in this class to retrieve layout option values to ensure that you get correct defaults if an option is not set. These classes do not have to be registered with ELK's extension point because they are automatically registered through the main metadata class.

This page explains how to write a metadata file. See the end of the page for an example metadata file of a very simple layout algorithm. (Pro-tip: Reading the previous sentence in action-movie-trailer-narrator voice makes it more compelling.)


## Creating and Registering Your Metadata File

Follow these steps to add a metadata file to your layout algorithm project:

1. Right-click your project's base package (which should have the same name as your plug-in project), and click _New - File_. Note that the base package cannot be empty (of course, you would never use the default empty package anyway since that's bad style now, would you...).
1. Give your file a sensible name (which is usually the name of your algorithm), and add the file extension `.melk`. Click _Finish_.
1. If Eclipse asks you whether you want to add the _Xtext nature_ to your project, click _Yes_.
1. The ELK SDK needs some basic information in your file to generate your
   `ILayoutMetaDataProvider`:

    ```melk
    package package.the.melk.file.is.in

    bundle {
        // Change the following line according to what you want the
        // package and name of your meta data provider to be
        metadataClass options.MetaDataProvider
    }
    ```

    See below for details on what this is doing.

1. Add a folder `META-INF/services` inside one of your project's source folders (by default, we put it right inside of the `src` folder).
1. In that folder, create a file named `org.eclipse.elk.core.data.ILayoutMetaDataProvider` (the fully qualified name of the `ILayoutMetaDataProvider`).
1. In that file, add a single line of text which contains the fully qualified name of the `ILayoutMetaDataProvider` that was generated for you by the ELK SDK.
1. Save and close the editor.


## The Metadata Language

The ELK metadata language contains everything you need to make the following information available to ELK:

* Which layout algorithms your plug-in provides, and which classes implement them.
* Which layout options your plug-in contributes, and which layout options your algorithms support.
* Which layout algorithm categories your plug-in contributes, and which of them your algorithms belong to.

The basic layout of a metadata file looks something like this:

```plain
<package declaration>

<imports section>

<bundle information>

<metadata declarations>
```

The package declaration and imports section work the same way as they do in Java: the package declaration indicates where the metadata file lies, and the imports import classes used in the file's declarations.The rest of this page will walk you through the remaining parts.

**Note:** Feel free to use comments. The metadata language supports Java-style single-line comments (`//`) and multi-line comments (`/* */`).


### Bundle Information

To be able to turn your metadata file into an `ILayoutMetaDataProvider`, ELK needs some basic information. This is what the bundle information section is all about. Here's what it would look like for a typical metadata file:

```melk
/**
 * An optional bunch of Javadoc that will end up as the Javadoc for the
 * generated class.
 */
bundle {
    // The sub-package and name of the ILayoutMetaDataProvider that will be
    // generated for you. This is required for the generation process to
    // work.
    metadataClass properties.AwesomeOptions

    // A prefix that will automatically be added to the IDs of new layout
    // options declared in this metadata file. To save your a bit of typing.
    // Not strictly required, but highly recommended.
    idPrefix org.eclipse.elk.awesome
}
```


### Options

While [this section of our documentation]({{< relref "documentation/tooldevelopers/graphdatastructure/layoutoptions.md" >}}) describes what layout options are and how to use them, this section focuses on making their mere existence known to the world. Doing so will cause the ELK SDK to generate `IProperty` constants in your generated `ILayoutMetaDataProvider` that you can use [as described]({{< relref "documentation/tooldevelopers/graphdatastructure/layoutoptions.md" >}}) to set layout option values. An option declaration can become rather complex, so here's what it generally looks like (we will go into the missing details below):

```melk
// The option's ID, appended to the idPrefix declared in the bundle information
<deprecated> <modifier> option myLayoutOptionId : <type> {
    // A user-readable label for the layout option
    label "Wobblyness Factor"

    // A short user-readable description. This will also become the property
    // constant's Javadoc comment.
    description
        "Controls the overall wobblyness of edges when connected
         to wobbles."

    // An optional default value used when the option is not configured
    // for a given graph. Of course, the given expression must evaluate
    // to the layout option's type as specified above.
    default = Direction.UNDEFINED

    // Optional lower and upper bounds option values must satisfy to be
    // valid. These must be comparable to the layout option's type as
    // specified above.
    lowerBound = 3
    upperBound = 5

    // Which graph elements the option can be meaningfully applied to.
    targets <targets>

    // An optional list of an arbitrary number of alternate IDs your
    // layout option should be accessible under. You should probably
    // not use this. We introduced this feature to ease the process of
    // transitioning when our project became the Eclipse Layout Kernel,
    // which caused all layout option IDs to change.
    legacyIds the.first.old.id.of.our.option,
              the.second.old.id.of.our.option

    // Some layout options only make sense if another option has a certain
    // value. Add a 'requires' declaration to each such dependency.
    requires some.other.layout.option.id == 42
}
```

Let's go through what the sample code didn't explain:

* `<deprecated>`

    Prepending the option declaration with the `deprecated` keyword will mark the generated property constant as being deprecated in its Javadoc comment.

* `<modifier>`

    Option declarations support the following modifiers, of which one can be used:

    Modifier       | Meaning
    --------       | -------
    `advanced`     | Advanced layout options are not shown in the _Layout_ view by default. The user has to explicitly instruct the view to show advanced layout options.
    `programmatic` | Programmatic layout options are not shown in the _Layout_ view at all, but only set through programmatic layout configuration. The reason is often that they are either highly specialized, or that values of their data type cannot be properly entered in the layout view.
    `output`       | Output options are not really layout options at all, but are used by layout algorithms to annotate the input graph with further layout information that could not be returned otherwise. Not being input options at all, these options are of course not shown in the layout view.
    `global`       | A layout option that is global can be understood as a layout option that guides ELK's implementation of the layout process rather than influencing what a single layout algorithm does. You should not need to declare global options.

* `<type>`

    The data type of a layout option. This can be any valid and visible Java type. Note that primitive types will be replaced by their object counterparts (for example, `int` will become `Integer`).

* `<targets>`

    Defines which graph elements a layout option can meaningfully be applied to. This mainly influences which layout options the _Layout_ view shows depending on the currently selected diagram element. An option can have zero or more targets, defined as a comma-separated list. The following are valid targets:

    Target    | Meaning
    ------    | -------
    `parents` | The option makes sense for nodes that contain further nodes themselves (_parent nodes_).
    `nodes`   | The option makes sense for nodes (which also includes parent nodes).
    `edges`   | The option makes sense for edges.
    `ports`   | The option makes sense for ports.
    `labels`  | The option makes sense for labels.

    Note that there is nothing that stops programmers from setting a port option on a node. All you are saying with your declaration is that doing so won't make any difference because you will only interpret the option when it is set on ports.


#### Grouping Layout Options

Everyone likes things to be neat and tidy. Well, alright, _we_ like things to be neat and tidy, which is why related options can be grouped together:

```melk
group thegroupname {
    // Your options and nested groups go here
}
```

Groups do not just make the metadata file more readable, but also have an effect on the identifiers of your options: all the names of groups your options are in will be placed between the option ID prefix and the option's actual name, thus reflecting the grouping in option IDs as well.


#### A Note on Enumerations

Layout options may well have an enumeration value type. Just as layout options can be declared to be advanced, this may also be true for enumeration values. Take for example a layout algorithm that offers different implementations of a certain feature. The implementation to be used is specified through a layout option that enumerates all possibilities. However, let us assume that some of the implementations are not quite ready for the public. (Homework assignment: work out five reasons why this could be the case.) The corresponding enumeration values can be annotated to make this clear:

```java
public enum Strategy {
    WORKS_ALWAYS,
    WORKS_ON_SUNDAYS,
    @AdvancedPropertyValue
    WORKS_IF_YOU_ARE_CAREFUL,
    @ExperimentalPropertyValue
    WORKS_IN_EXPERIMENTAL_CONDITIONS
}
```


### Categories

Each layout algorithm can be assigned to a layout algorithm category. The categorization is for example used by the layout algorithm selection dialog or the documentation. While ELK already provides a number of predefined categories, this is how you would define your own:

```melk
// The category's ID, appended to the idPrefix declared in the bundle information
category awesome {
    // A short category label that can be shown to users.
    label "Awesome Algorithms"

    description
        "A not-too-long but totally meaningful description of what this category
         of layout algorithms is all about. Will be shown to users."
}
```


### Algorithms

Of course, everything we have done so far was simply in support of the main act: declaring the layout algorithms you have worked hard to develop (just to find out that there's hundreds of little details you got wrong; we've been there...). Here's what an algorithm declaration looks like:

```melk
// The algorithm's ID
algorithm algorithmid(<class>) {
    // User-readable short name and description for the algorithm
    label "Excellent Algorithm"

    description
        "A brief and completely sensible description of what this algorithm
         does. Will be shown to users."

    // Optional name of the ILayoutMetaDataProvider class generated for this
    // algorithm
    metadataClass <metadataClass>

    // Optional validator class called by ELK before actually running the
    // layout algorithm.
    validator <validatorClass>

    // Optional path to an image that provides a preview of the kinds of
    // layouts this algorithm will produce. Relative to the plug-in's
    // root folder.
    preview path/to/preview/image.png

    // Optional ID of a category of layout algorithms this algorithm belongs to.
    category the.category.id

    // List of graph features this algorithm explicitly supports (see below)
    features <features>

    // Declarations for each layout option this algorithm supports (see below)
    <supportedOptions>
}
```

Three details deserve more explanation:

* `class`

    The main class that provides the entry to your layout algorithm. This must be a subclass of `AbstractLayoutProvider` and may have to be imported in your metadata file's import section. `AbstractLayoutProvider` has an `initialize(String)` method that expects a `String` parameter to customize its behavior. You can define that parameter's value for your algorithm like this:

    ```melk
    algorithm myAlgorithm(TheAbstractLayoutProviderSubclass#TheParamterValue)
    ```

* `metadataClass`

    The metadata class will contain the property constants you should use to access your algorithm's property values. If no name is specified, a class with a default name will be generated in the package specified by the metadata file. *This class name must be distinct from any other metadata class name, both for algorithms and for the bundle itself.*

* `validatorClass`

    Layout algorithms may have certain requirements concerning the graphs they are run on, including connectivity, hierarchy, or simlpy configuration. Instead of checking the input graph's validity in the algorithm itself, a validator class can be specified that is run automatically when layout is invoked through the `DiagramLayoutEngine`. The class must implement the `org.eclipse.elk.core.validation.IValidatingGraphElementVisitor` interface.

* `features`

    This is a comma-separated list of structural graph features that an algorithm explicitly supports. Most of these declarations are purely informational, but graph layout engines might decide to change the layout graph passed to your algorithm depending on whether it supports the original graph's features or not. At the time of writing, these are the possible graph features:

    Feature   | Meaning
    ------    | -------
    `clusters`          | **TODO: Document**
    `compound`          | Whether an algorithm supports the layout of hierarchical graphs. If it does not, the ELK graph layout engine will only ever give the algorithm a single level of hierarchy to be laid out at a time, regardless of what the user requests.
    `disconnected`      | Whether an algorithm has explicit support for disconnected components (groups of nodes without connection to other groups of nodes).
    `edge_labels`       | Whether an algorithm knows how to place edge labels or not. A graph layout engine may decide to run a post-processing on your algorithm's result to place edge labels afterwards.
    `inside_self_loops` | Whether an algorithm supports routing a self loop through the inside of a node instead of around it.
    `multi_edges`       | Whether an algorithm supports edges that have the same source and target.
    `ports`             | Whether an algorithm supports edges connected to nodes through explicit ports.
    `self_loops`        | Whether an algorithm supports self loops (edges that connect a node to itself).

* `supportedOptions`

    Algorithms declare which layout options they support, either with their default values as originally declared or with overridden default values. Each supported option ends up as an `IProperty` constant in the algorithm's layout meta data provider, complete with correct default values. These constants should be used to retrieve option values from within the algorithm code.

    An option support declaration looks like this:

    ```melk
    supports the.option.id          // Standard default value
    supports the.option.id = 42     // Overridden default value
    ```


## Example

The following is an example metadata file for a very simple layout algorithm:

```melk
package cds.layout.simple

import cds.layout.simple.SimpleLayoutProvider
import org.eclipse.elk.core.math.ElkPadding

bundle {
    metadataClass options.SimpleMetaDataProvider
    idPrefix cds.layout.simple
}

option reverseInput : boolean {
    label "Reverse Input"
    description
        "True if nodes should be placed in reverse order of their
        appearance in the graph."
    default = false
    targets parents
}

algorithm simple(SimpleLayoutProvider) {
    label "Simple Layout Algorithm"
    description
        "This layout provider places nodes along a horizontal or a vertical line."
    metadataClass options.SimpleOptions

    supports reverseInput
    supports org.eclipse.elk.padding = new ElkPadding(10)
    supports org.eclipse.elk.spacing.edgeEdge = 5
    supports org.eclipse.elk.spacing.edgeNode = 10
    supports org.eclipse.elk.spacing.nodeNode = 10
}
```

Note how we override the default value of several properties for this algorithm.
