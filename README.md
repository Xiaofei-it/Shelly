# Shelly

A library for business-logic-oriented programming, providing a novel pattern which uses a method
chain to illustrate how each component varies with a business object.

##Gradle

```
compile 'xiaofei.library:shelly:0.2.5-alpha4'
```

##Maven

```
<dependency>
  <groupId>xiaofei.library</groupId>
  <artifactId>shelly</artifactId>
  <version>0.2.5-alpha4</version>
  <type>pom</type>
</dependency>
```

##Phylosophy

In business-logic-oriented programming, a change of a business object may causes changes of many
components and the complexity of business logic will increase coupling between components.
To decrease coupling we usually use listeners (observers) or EventBus, which is effective and easy
to use. However, these techniques have the following disadvantages:

1. The amount of listeners or events increases as the complexity of business logic does.

2. The usage of a listener will cause corresponding components to implement the interface of the
listener, which makes code confusing and complex. What's worse, the abuse of listeners will cause
memory leaks.

3. The usage of EventBus will cause code to be difficult to debug, since it is difficult to know
what happens after the posting of an event and you should find the usages of the Java class of the
specified event in IDE to find all the components receiving the specified event.

To solve the above problems, I composes the Shelly library.

The Shelly library provides a novel pattern which uses a method chain to illustrate how each
component varies with a business object. In the method chain, each method represents the change of
a corresponding component and the chain of methods represents all of the changes of all of the
corresponding components. Thus you can see the change of the "world" in a single file rather than
searching the corresponding classes in the whole project.

The Shelly library provides many methods to compose a method chain even including thread scheduling.