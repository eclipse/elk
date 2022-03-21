---
title: "Layout Options"
menu:
  main:
    identifier: "Layoutoptions"
    parent: "GraphDataStructure"
    weight: 20
---

Layout options are key-value pairs that configure the behavior of automatic layout algorithms. Let's look at all of the involved classes first before we concentrate on how to actually use them in practice.


## Involved Classes

Layout options are represented by annotating objects with properties. There are two main types we need to cover: the properties themselves, and the objects we can configure through properties.


### Properties

The most important type when it comes to properties is the `IProperty` interface. Instances of that interface describe the abstract attributes of each kind of property has:

1. An identifier that, well, identifies the kind of property.
1. A default value that is used if a property has not been explicitly set.
1. Optional lower and upper bounds that define the valid range of values for that property.

Note the `IProperty` is actually `IProperty<T>`, whose type parameter `T` corresponds to the type of the property's values.

The most important implementation of `IProperty` is the `Property` class. Chances are you won't have to provide your own implementation of `IProperty` since the `Property` class should cover all your needs.


### Property Holders

Classes that implement `IPropertyHolder` are basically maps from properties (described by `IProperty` instances) to property values (objects whose type depends on the property). All graph elements implement the `IPropertyHolder` interface. Thus, every graph element has properties associated with it.

Here's one more detail that will become interesting when we talk about retrieving property values. We just said that property holders are maps from properties to property values. But if properties are described by `IProperty` instances, why not simply say that property holders map `IProperty` instances to property values? To answer that question we have to return to what actually identifies a property: its identifier. Accordingly, if multiple `IProperty` instances share the same identifier, they effectively describe the same property and are treated by property holders as such. Why this is important will become clearer once we get around to retrieving property values below.


## Working With Properties

There are two aspects of working with properties: accessing them (which is what both users and developers of layout algorithms usually do) and defining properties (which is what developers of layout algorithms sometimes do).


### Setting Property Values

To set a property's value on a property holder, use this code:

~~~java
ElkGraphElement graphElement = ...;
graphElement.setProperty(CoreOptions.DEBUG_MODE, true);
~~~

Note that the available property objects used to set property values are


### Retrieving Property Values

To get hold of the properties of a graph element, use code such as this:

```java
ElkGraphElement graphElement = ...;
// Note that we do not use CoreOptions to access the property
// (see below for why that is)
boolean debugMode = graphElement.getProperty(MyAlgorithmOptions.DEBUG_MODE);
```

Of course, since `IProperty` is a generic type, there is no explicit type casting involved here. Also, if the `MyAlgorithmOptions.DEBUG_MODE` property is not set on `graphElement`, the `getProperty(...)` method will simply return the property's default value, which in this case is `false`. Why is it `false`, though? Where does the default value come from?

When the `getProperty(...)` method determines that the property whose value it should retrieve is not actually configured, it asks the passed `IProperty` instance for the default value to be returned. This has an important consequence. Consider the following code:

```java
final IProperty<Boolean> FALSE_DEFAULT = new Property<>("an.excellent.example.property", false);
final IProperty<Boolean> TRUE_DEFAULT = new Property<>("an.excellent.example.property", true);

// Setting the property value to null effectively undefines the
// property for the property holder
propertyHolder.setProperty(FALSE_DEFAULT, null);
System.out.println(propertyHolder.getProperty(FALSE_DEFAULT));
propertyHolder.setProperty(FALSE_DEFAULT, null);
System.out.println(propertyHolder.getProperty(TRUE_DEFAULT));
```

This code will produce the following output:

```plain
false
true
```

This means that you can define multiple `IProperty` instances that describe the same property, but with different default values. That seems like an inconsistency, but it has an important reason. The layout options known to the Eclipse Layout Kernel are registered with the `LayoutMetaDataService`. Most are registered with a global default value. Layout algorithms declare which of those options they support, and in doing so can override that default value. Accordingly, they need their own `IProperty` instance with their own default value to retrieve property values. This is why the ELK metadata tooling generates a separate layout metadata provider for each layout algorithm, complete `IProperty` objects for each supported layout option to retrieve its value with. Which `IProperty` object is used to _set_ the property value does not matter, though.

That is the reason why the first example above does not use `CoreOptions.DEBUG_MODE` to retrieve the property value, but `MyAlgorithmOptions.DEBUG_MODE` (where `MyAlgorithmOptions` is the metadata class generated for your layout algorithm).


### Checking for the Presence of Property Values

We can always call `getProperty(...)` on a graph element and get a (more or less) sensible result, but we may not be able to distinguish a default value from a value explicitly set on that element. If we need to, we can check whether a property was explicitly set through `hasProperty(...)`:

```java
if (propertyHolder.hasProperty(MyAlgorithmOptions.OPTIONAL_GIZMO)) {
    Gizmo g = propertyHolder.getProperty(MyAlgorithmOptions.OPTIONAL_GIZMO);
}
```


## Defining Properties

Most of the time, algorithm developers do not have to worry about declaring their own `IProperty` objects. The options officially supported by a layout algorithm constitute a part of the algorithm's interface and metadata and are thus declared in the algorithm's [metadata file]({{< relref "documentation/algorithmdevelopers/metadatalanguage.md" >}}). The ELK SDK automatically generates the required `IProperty` instances.

The only time custom `IProperty` instances have to be declared is when an algorithm uses internal properties during its execution to pass information between its different phases. Internal options do not have to be declared anywhere since they are not supposed to be set by users anyway. To define such options, simply add new `Property` instances to any one of your internally used classes. Use these instances to set and retrieve options.
