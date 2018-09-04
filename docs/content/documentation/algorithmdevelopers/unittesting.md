---
title: "Unit Tests"
menu:
  main:
    identifier: "UnitTests"
    parent: "AlgorithmDevelopers"
    weight: 60
---

Layout algorithms are complex pieces of software and, thus, should probably be tested. ELK provides a unit test framework with the following features:

* A distinction between _black box_ and _white box_ tests. Black box tests call a layout algorithm and test the result. White box tests call an algorithm and can examine the algorithm's state. Layout algorithms need to explicitly support white box tests.

* Loading graphs.

* Configurating graphs.

Writing a unit test is rather easy. This page should walk you through it.


## Writing a Unit Test

### Setting Things Up

Unit tests should be placed in a plug-in below the `test` folder that depends on our `org.eclipse.elk.alg.test` plug-in. All class names that end with `Test` are executed during the automatic build. The usual way of writing unit tests is this:

1. Write classes that contain the actual tests. The names of those classes start or end with `ElkTest`.

1. Write a class that references the actual test classes. The name of this class starts or ends only with `Test`.

Assume that you have written two test classes, `NoOverlapsElkTest` and `AwesomeEastereggElkTest`. You will then want to write a class `TheTest` with the following content to actually have the other two test classes executed:

```java
@RunWith(LayoutTestRunner.class)
@TestClasses({NoOverlapsElkTest.class, AwesomeEastereggElkTest.class})
public class TheTest {

}
```

While you could just as well have your test classes executed directly (you'd have to rename them to `NoOverlapsTest` and `AwsomeEastereggTest`, of course), bundling them up in this manner allows the test framework to bundle tests and execute them together to increase performance.

{{% note title="Not Implemented Yet" mode="warning" %}}
The bundling feature is not implemented yet.
{{% /note %}}

### Test Basics

Black box tests specify at least one algorithm they want to test, plus at least one test method to be executed:

```java
@Algorithm(ForceOptions.ALGORITHM_ID)
@UseDefaultConfiguration()
public class BlackBoxTest {

    @Graph
    public ElkNode basicGraph() {
        return TestUtil.buildBasicGraph();
    }
    
    @Test
    public void testNonNull(final ElkNode graph) {
        Assert.assertTrue("Graph is null.", graph != null);
    }

}
```

Note how everything is configured through annotations. What happens here is this:

1. The test framework calls `basicGraph()` to get its hands at the graph to run the test methods on.
1. Since `@UseDefaultConfiguration` is specified, the framework makes sure that nodes and ports have proper initial sizes (if they don't already).
1. The force layouter is run on the test graph.
1. The framework calls `testNonNull(...)` on the laid-out graph.

### Loading Graphs

It would be cumbersome to always generate graphs through methods. This is why the test framework supports another way of specifying graphs, as shown in the following piece of code:

```java
@ImportGraphs
public List<AbstractResourcePath> importGraphs() {
    return Lists.newArrayList(new ModelResourcePath(
        "realworld/ptolemy/hierarchical/continuous_cartracking_CarTracking.elkt"));
}
```

A list of `AbstractResourcePath` instances can specify paths to graph files to be loaded. Both methods and fields can be annotated with this annotation. The most common resource path is probably the `ModelResourcePath`, which is interpreted relative to the ELK models directory (more on how the test framework locates that below). Resource paths can refer to a single file, all files in a directory (simply end the path specification with `/**`) or all files in a directory and all of its descendants (`/**/`).

### Configuring Graphs

While graphs loaded from files usually already have a few properties set on them, we do allow them to be configured further. There are two basic ways for doing so:

```java
@Configurator
public LayoutConfigurator configurator() {
    LayoutConfigurator layoutConfig = new LayoutConfigurator();
    layoutConfig.configure(ElkNode.class).setProperty(TEST_PROPERTY_CONFIGURATOR_METHOD, TEST_PROPERTY_VALUE);
    return layoutConfig;
}

@ConfigMethod
public static void configureStuff(final ElkNode graph) {
    graph.setProperty(TEST_PROPERTY_CONFIGURATION_METHOD, TEST_PROPERTY_VALUE);
}
```

The `@Configurator` annotation can be used on methods and fields that supply `LayoutConfigurator`s. The `@ConfigMethod` annotation, on the other hand, causes a method to be called which can then configure the supplied graph to its heart's content.

### Writing White Box Tests

White box tests are slightly more complex to write because they have to specify the layout processor before or after which they want to be run. For example:

```java
@RunWith(LayoutTestRunner.class)
@Algorithm(LayeredOptions.ALGORITHM_ID)
@RunAfterProcessor(processor = NetworkSimplexLayerer.class)
@RunAfterProcessor(processor = LongestPathLayerer.class)
public class WhiteBoxTest {

}
```

The processors can be specified for all test methods in a class, but can also be overridden per test method. Since layout algorithms can adapt their list of processors to different graphs, it may or may not be a problem for a layout processor to not be executed, thus causing its test to not be executed either. If this is a problem, annotate the test with `@FailIfNotExecuted`.


## Running Tests

From Eclipse, tests can be run as plain Java JUnit tests. Three environment or system variables have to be set for the test framework to work:

* `ELK_REPO`: Fully-qualified path to the directory where the main ELK repository is checked out.
* `MODELS_REPO`: Fully-qualified path to the directory where the `elk-models` repository is checked out.
* `RESULTS_PATH`: If test results or graphs of failed tests should be persisted, this should point to the directory to store them in.


## Supporting White Box Tests

For a layout algorithm to support white box tests, its `AbstractLayoutProvider` subclass needs to implement `IWhiteBoxTestable`. The interface adds a single method: `setTestController(TestController controller)`. This supplies the test controller that controls the white box test run. The layout algorithm must then be sure to call the controller's `notifiyX(...)` methods as its processors are about to run or have just finished running so that tests have a chance to examine the result.
