# 理论基础

本文讨论Shelly库的理论基础，包含Shelly库的思想以及相关技术术语的定义。

## 思想

在面向业务逻辑的编程中，某个特定的业务对象的改变可能会引起各个组件的改变，业务逻辑的复杂性会增加组件之间的耦合。
为了降低耦合，我们通常使用listener（observer）或者event bus，这些易于使用并且非常有效。但是存在以下缺点：

1. listener或者event数量随着业务逻辑的复杂性的增加而增加。这样使工程难以维护。

2. 使用了某个listener，就需要相应的组件实现listener的接口，这样会使代码变得复杂。而且listener的滥用导致内存泄漏。

3. event bus的使用使代码变得难以调试，因为很难预测并且控制event发送之后会发生什么。你必须在IDE中寻找使用event的地方
来确定哪些组件接收了这个event。

为了解决这些问题，我做了Shelly库。

The Shelly library provides a novel pattern which uses a method chain to illustrate how each
component varies with a business object. In the method chain, each method takes an action which
represents the change of a particular component. The chain of methods represents all of the changes
of all of the corresponding components. Thus you can see the change of the whole "world" in a single
file rather than searching the whole project for the corresponding classes.

Specifically, a method chain corresponds to a piece of business logic and a business object. It
illustrates what happens if this business object is changed. And this piece of business logic thus
takes effect.

When the method chain is created, the class of the business object is specified and then each method
is appended to the chain. Each action of each method of the method chain takes some objects as
parameters and perform a particular action.

More attention should be paid to the input of each action. The first action of the method chain
takes the business objects as parameters. Then after it is performed, it passes the objects to the
following action, which also perform a particular action and passes the objects to the following
action. Thus the objects are passed between actions until they are passed to a transformation action,
which takes the objects as a parameter and returns one or more new objects. After the transformation,
the new objects are passed to the following actions.

Now pay attention to the action. The action can be regarded as a method which takes the objects
passed to it as input and executes the statements inside it. Also the Shelly library provides
an EventBus-like feature, in that there exists some special actions which take the registered
components (which should be registered first, usually at the same time when they are
created) and the objects passed to them as input and executes the statements inside.

The Shelly library provides many methods to compose a method chain, including a variety of methods
for performing different actions, methods for data transformation and methods for thread scheduling.
Also it, as is mentioned above, provides an EventBus-like feature for preforming actions on registered
components.

A method chain provides you with a global view of what happens after the change of a business object.
The method chain is named "Domino" in the Shelly library since it represents a series of actions to
perform one after the other, as the domino game does.

After the creation of a Domino, you can "invoke" it to perform the specified actions.
When a business object is changed, you "invoke" the Domino and pass the business object to it.
Then it performs the actions in the action sequence one after the other.

## 定义

本节给出了关于Shelly库的技术术语的定义。

### 关于action

一个“action”（“操作”）指的是一个Java语句的序列，这个序列包括

An "action" refers to a sequence of Java statements, the effect of which includes but is not limited
to, performing certain operations on certain components, producing certain outputs, performing
data transformation, and performing thread scheduling. An action is represented by a Java
class or a Java interface, in which there exists a method which is named "call" and contains the
sequence of Java statements of the action. Such a method may be invoked to perform the corresponding
action.

"Performing an action" refers to executing the sequence of Java statements of the action.

### On Dominoes

A "Domino" is a Java object which, under certain circumstances, performs a sequence of actions.
For the sake of simplicity, a "Domino" may also refer to the Java class of a particular Domino
object.

"Creating a Domino" refers to the operation of building a particular Java instance of a Domino
class. A Domino is usually created by a Java method chain which starts with
`Shelly.createDomino(Object)` or `Shelly.createAnonymousDomino()` and is followed by various methods provided
by the Shelly library.

"Committing a Domino" refers to the operation of causing the Shelly library to hold a Java reference
of the specified Domino object for later use.

"Playing a Domino" or "Invoking a Domino" refers to the operation of causing the specified Domino to
perform a sequence of actions. To play a Domino, a group of objects is needed. The group must contain
one or more objects.

### On input, output and data flow

#### Input of an action

The "input of an action" is a group ("input group") of objects which is passed to the `call` method as arguments.
The following illustrates the relationship between the input and the performance of an action:

Suppose
the number of arguments the `call` method takes, excluding the arguments representing the components, is `a`,
the number of the objects contained in the input group is `b`,
and the number of occasions when the action is performed is `c`, i.e. the action is performed for `c` times.
Then

1. If b = 0 and a = 0, then c = 1.

2. If b = 0 and a > 0, then c = 0.

3. If b > 0 and a = 0, then c = 1.

4. If b > 0 and a > 0, then c = a * b.

#### Output of an action

Before the definition of the "output of an action", the `call` method should be paid more attention
on. As is mentioned above, actions includes but is not limited to performing certain operations
on certain components, producing certain outputs, performing data transformation, and performing
thread scheduling. The action performing data transformation is called the "lifting actions".

The "output of an action" is a group ("output group") of objects, produced in the following way:

1. If the action is not a "lifting action", then the output is exactly the same as the input.

2. If the action is a "lifting action", then the output is produced according to the effect of various
actions. And the number of the objects contained in an output group may be different from the number
of the objects contained in an input group.

#### Types

The "type of the input" is the Java type of elements in the input group.

The "type of the output" is the Java type of elements in the output group.

#### Data flow

Once invoked, a Domino performs a sequence of actions.

The "input of a Domino" is the input of the first action of the action sequence of the Domino.

The "output of a Domino" is the output of the last action of the action sequence of the Domino.

In an action sequence, the output of a previous action is passed to the next one.
Thus the output of a previous action is exactly the same as the input of the next one.

Therefore, the "data flow of a Domino" is the sequence of the input of each action of the Domino,
followed by the output of the Domino.
