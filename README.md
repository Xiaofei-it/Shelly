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

To solve the above problems, I compose the Shelly library.

The Shelly library provides a novel pattern which uses a method chain to illustrate how each
component varies with a business object. In the method chain, each method represents the change of
a corresponding component and the chain of methods represents all of the changes of all of the
corresponding components. Thus you can see the change of the "world" in a single file rather than
searching the corresponding classes in the whole project.

Specifically, a method chain corresponds to a piece of business logic and a business object. It shows
what happens if this business object is changed and this piece of business logic thus takes effect.
Before the method chain is created, the class of the business object is specified and then each method
is added into the chain. Each method of the method chain takes some objects as a parameter and perform
a specific action.

More attention should be paid to the input of each method. The first method of the method chain
takes the business objects as a parameter. Then it passes the objects to the following method, which
also perform a specific action and passes the objects to the following method. Thus the objects are passed
between methods until they are passed to a transformation method, which takes the objects as a parameter
and returns one or more new objects. After the transformation, the new objects are passed to the
following methods.

Now pay attention to the action performed by a method. The action can be regarded as a method which
takes the objects passed to it as a parameter and executes the statements inside it. Also the Shelly
library provides an EventBus-like feature, in that there exists some special actions which take the
registered components (which should be registered first, usually at the same time when they are
created) and the objects passed to them as parameters and executes the statements inside.

The Shelly library provides many methods to compose a method chain, including a variety of methods
for performing different actions, methods for data transformation and methods for thread scheduling.
Also it, as is discussed above, provides a EventBus-like feature for preforming actions on registered
components. Therefore, a method chain provides you with a global view of what happens after the
change of a business object. The method chain is named "Domino" in the Shelly library for it represents
a series of actions to perform one after the other, as the domino game does.

The above is discussing something about the structure of the method chain, i.e. "Domino".
Now let's say something about how to "invoke" the Domino, which is a bit easy.
When a business object is changed, you "invoke" the Domino and pass the business object to it.
Then it performs actions on each corresponding component according to the sequence of the methods.

##Usage

