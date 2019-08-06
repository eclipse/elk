---
title: "Unit Tests"
menu:
  main:
    identifier: "UnitTests"
    parent: "AlgorithmDevelopers"
    weight: 70
---

Layout algorithms are complex pieces of software and, thus, should probably be tested. Besides the usual plain JUnit tests, ELK provides a graph algorithm unit test framework based on JUnit 4. Tests written with that framework basically do three things:

1. Load one or more graphs.

1. Optionally provide a number of graph configurations. If a test class doesn't specify configurations, a default configuration will be activated.

1. Run one ore more tests on each graph.

For each test class, the framework then executes the following steps:

1. Load all the graphs.

1. Produce one copy of each graph for each graph configuration. If no configuration was specified, the original graphs are left untouched.

1. Run layout on all graph copies for each active algorithm and execute the test methods on the results.

Tests are thus basically run on test instances defined by three properties:

* A graph.

* A configuration applied to the graph.

* A layout algorithm run on the graph.

The framework distinguishes _black box_ and _white box_ tests. Black box tests work as described above. White box tests do not execute once layout has finished, but while layout is still running. Layout algorithms need to explicitly support white box tests. A test class can mix black box and white box tests.

Writing unit tests isn't too hard. This page should walk you through writing and running them.


## Adding a Test Class

Unit tests should be placed in a plug-in inside the `test` folder that depends on our `org.eclipse.elk.alg.test` plug-in. All class names that end with `Test` are executed during the automatic build. A minimal test class then looks like this:

```java
@RunWith(LayoutTestRunner.class)
public class MyAlgorithmTest {

}
```

The `@RunWith` annotation specifies that the test should be run with our layout test framework.


## Specifying Graphs

There are several way to specify test graphs. There can be arbitrarily many sources for graphs, and all of the ways to specify test graphs can be combined.

### Supply Graphs Directly

You can specify methods that supply graphs built directly in your test class, like this:

```java
@GraphProvider
public ElkNode produceGraph() {
    // Build a graph here...
}
```

### Load Graphs From Disk

Graphs stored in ELK's [models repository](https://github.com/eclipse/elk-models) can be used directly in tests. You specify graphs to be loaded through lists of `ModelResourcePath`, which accepts paths relative to the models repository:

```java
@GraphResourceProvider
public List<AbstractResourcePath> provideGraphs() {
    List<AbstractResourcePath> paths = new ArrayList<>();

    // A single file
    paths.add(new ModelResourcePath("realworld/ptolemy/hierarchical/continuous_cartracking_CarTracking.elkt"));

    // All files directly contained in a directory
    paths.add(new ModelResourcePath("realworld/ptolemy/hierarchical/**"));

    // All files contained in a directory and its subdirectories
    paths.add(new ModelResourcePath("realworld/ptolemy/**/"));

    // Only .elkt files contained in a directory and its subdirectories
    paths.add(new ModelResourcePath("realworld/ptolemy/**/").withFilter(new FileExtensionFilter("elkt")));

    return paths;
}
```

### Generate Random Graphs

ELK includes a [random graph generator]({{< relref "documentation/algorithmdevelopers/randomgraphs.md">}}) that can also be used to generate test graphs. There are two ways to do so.

1. A method or a field can supply an instance of `GeneratorOptions` which is used to configure the random graph generator:

    ```java
    @RandomGeneratorOptions
    public GeneratorOptions generatorOptions() {
        GeneratorOptions options = new GeneratorOptions();
        
        options.setProperty(options.GRAPH_TYPE, GeneratorOptions.GraphType.TREE);
        // Set more options...
        
        return options;
    }
    ```

1. An `.elkr` file that specifies options can be loaded, similar to how graphs can be loaded:

    ```java
    @RandomGeneratorFile
    public AbstractResourcePath loadRandomGraph() {
        return new ModelResourcePath("path/to/random/graph/file.elkr");
    }
    ```


## Configuring Graphs

If you simply want to layout the graphs as specified and then run your tests on the results, you can skip this step. However, to test particular features and algorithms it is often necessary to customize layout properties. Again, there are several ways to do so.


### Supply Layout Configurators

Layout configurators are objects that can apply properties to graph objects. You can supply them like this:

```java
@ConfiguratorProvider
public LayoutConfigurator configurator() {
    LayoutConfigurator layoutConfig = new LayoutConfigurator();

    layoutConfig.configure(ElkNode.class)
        .setProperty(SOME_PROPERTY, SOME_PROPERTY_VALUE);

    return layoutConfig;
}
```

### Configure Graphs Dynamically

You can also define a method that expects a graph and configures that graph dynamically. That allows you to set your properties only if certain conditions are met, for example.

```java
@Configurator
public void configureStuff(final ElkNode graph) {
    graph.setProperty(SOME_PROPERTY, SOME_PROPERTY_VALUE);
}
```

### Specify Layout Algorithms

Since specifying a layout algorithm is a common scenario, there are special annotations to do so that can be added to a class. To test one or more specific algorithms, use one or more `@Algorithm` annotations:

```java
@RunWith(LayoutTestRunner.class)
@Algorithm(MyTestClassOptions.ALGORITHM_ID)
public class MyAlgorithmTest {

}
```

To test all known algorithms, you can use the `@AllAlgorithms` annotation instead having to specify all algorithms explicitly:

```java
@RunWith(LayoutTestRunner.class)
@AllAlgorithms
public class MyAlgorithmTest {

}
```

Each graph with each configuration is laid out once with each specified algorithm.

### Default Configurations

Many graphs in our models repository refrain from specifying explicit sizes and labels for diagram elements. Often, however, tests need nodes to have a size and labels. Thus, your test class can specify to apply default configurations to all diagram elements:

```java
@RunWith(LayoutTestRunner.class)
@Algorithm(MyTestClassOptions.ALGORITHM_ID)
@DefaultConfiguration(nodes = true, ports = false, edges = false)
public class MyAlgorithmTest {

}
```

The defaults are applied on top of all graph configurations. In more detail, this is what they do:

* Nodes
    * Supply a default size if size constraints are fixed and no size has been set.
    * Add a label if none exists.
    * Configure the label to be centered inside unless another node label configuration was supplied.
* Ports
    * Supply a default size if none has been set.
    * Add a label if non exists.
* Edges
    * Set edge label placement to `CENTER` if it has not been set.


## Black Box Tests

Now that graphs are loaded and configured, black box test methods can examine the layout result:

```java
@Test
public void testGraphSizeSet(final ElkNode graph) {
    Assert.assertTrue(
        "The graph size has not been set by the layout algorithm.",
        graph.getWidth() > 0 && graph.getHeight() > 0);
}
```


## White Box Tests

White box tests ensure that an algorithm's internal state matches the developer's expectations. Here we not only examine the overall layout result, but can look inside intermediate results produced as the algorithm progresses.

A white box test method needs to specify which layout processor(s) it wants to run before or after. It does so like this:

```java
@TestBeforeProcessor(processor = NetworkSimplexLayerer.class, onRootOnly = false)
@TestAfterProcessor(processor = NetworkSimplexLayerer.class, onRootOnly = false)
public void testNetworkSimplexLayerer(LGraph lGraph) {
    // Test things...
}
```

The `onRootOnly` option specifies what to do if the algorithm executes on multiple levels of hierarchy at once. If `onRootOnly` is `true`, the test is only called if the processor is executed on the root graph. Otherwise, it is called whenever the processor is run on any of the hierarchy levels.

Since white box tests are specific to a particular algorithm, we require that the test class is configured to run exactly one layout algorithm through an `@Algorithm` annotation. Test setup will fail if no or multiple algorithms were configured.

By default, white box tests are expected to run at least once per test instance. If this is not the case for a test method, the test will fail. The optional `@FailIfNotExecuted` annotation allows you to change that behaviour.


### Supporting White Box Tests

For a layout algorithm to support white box tests, its `AbstractLayoutProvider` subclass needs to implement `IWhiteBoxTestable`. The interface adds a single method: `setTestController(TestController controller)`. This supplies the test controller that controls the white box test run. The layout algorithm must then be sure to call the controller's `notifiyX(...)` methods as its processors are about to run or have just finished running so that tests have a chance to examine the result.


## Running Tests

### Inside Eclipse

From Eclipse, tests can be run as plain Java JUnit tests (not plug-in tests). Three environment or system variables have to be set for the test framework to work:

* `ELK_REPO`: Absolute path to the directory where the main ELK repository is checked out.
* `MODELS_REPO`: Absolute path to the directory where the `elk-models` repository is checked out.
* `RESULTS_PATH`: If graphs of failed tests should be persisted, this should point to the directory to store them in. If this path is not specified, nothing will be persisted.

### As Part of Automatic Builds

[This page]({{< relref "documentation/contributors/buildingelk.md">}}) describes how to run runit tests as part of automatic builds.


## Differences to Usual JUnit Tests

To keep the implementation simple, we currently don't support the following JUnit features:

* `@Before` methods
* `@After` methods
* Timeouts and expected exceptions

Feel free to add support for these and file a pull request. :)
