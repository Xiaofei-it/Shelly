# Shelly

A library for business-logic-oriented programming, providing a novel pattern which uses a method
chain to illustrate how each component varies with a business object.

## Features

1. Provides a novel pattern for business-logic-oriented programming.

2. Makes the source code of a business-logic-oriented app easy to understand and maintain, no matter
how the business logic is modified.

3. Convenient for sending HTTP requests and performing callback operations,
especially for sending multiple requests synchronously or sequentially.

4. Convenient for time-consuming tasks and performing callback operations.

5. Powerful APIs for data flow control and thread scheduling.

## Preview

Before the introduction, let's see an example first.

Suppose that you want to use Retrofit to send an HTTP request, and

1. If the response is successful, invoke two particular methods of MyActivity and SecondActivity;

2. If the response is not successful, show a toast on the screen;

3. If something goes wrong when sending request and an exception is thrown, print the message of the error.

Using the Shelly library, you write the following to fulfil the above requirement:

```
Shelly.<String>createDomino("Sending request")
        .background()
        .beginRetrofitTask(new RetrofitTask<String, ResponseBody>() {
            @Override
            protected Call<ResponseBody> getCall(String s) {
                return netInterface.test(s);
            }
        })
        .uiThread()
        .onSuccessResult(MainActivity.class, new TargetAction1<MainActivity, ResponseBody>() {
            @Override
            public void call(MainActivity mainActivity, ResponseBody input) {
                try {
                    mainActivity.show(input.string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        })
        .onSuccessResult(SecondActivity.class, new TargetAction1<SecondActivity, ResponseBody>() {
            @Override
            public void call(SecondActivity secondActivity, ResponseBody input) {
                try {
                    secondActivity.show(input.string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        })
        .onResponseFailure(MainActivity.class, new TargetAction1<MainActivity, Response<ResponseBody>>() {
            @Override
            public void call(MainActivity mainActivity, Response<ResponseBody> input) {
                try {
                    Toast.makeText(
                        mainActivity.getApplicationContext(),
                        input.errorBody().string(),
                        Toast.LENGTH_SHORT
                    ).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        })
        .onFailure(new Action1<Throwable>() {
            @Override
            public void call(Throwable input) {
                Log.e("Eric Zhao", "Error", input);
            }
        })
        .endTask()
        .commit();
```

From the above example, you can see how MainActivity and SecondActivity change according
to the result or the failure of the HTTP request. We can see the changes of each component
from a single place.

Note that the above code will not perform any actions! What the code does is simply commit and
store the Domino for later use. To make the Domino perform actions, you should invoke the Domino.
Only after the Domino is invoked will it perform actions.

This is just a simple example. Actually, the Shelly library is very powerful,
which will be introduced in the following sections.

## Philosophy

In business-logic-oriented programming, a change of a particular business object may cause changes
of various components, and the complexity of business logic will increase coupling between components.
To decrease coupling we usually use listeners (observers) or the event bus, which is easy to use and
also effective. However, these techniques have the following disadvantages:

1. The amount of listeners or events increases as the complexity of business logic does, which makes
the project difficult to maintain.

2. The usage of a particular listener will cause corresponding components to implement the interface
of the listener, which makes code confusing and complicated. What's worse, the abuse of listeners
leads to a potential risk of memory leaking.

3. The usage of the event bus will cause code to be difficult to debug, since it is difficult to
predict and control what happens after the posting of a particular event and you should find the
usages of the Java class of the event in IDE to find all the components receiving the event.

To solve the above problems, I compose the Shelly library.

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

## Definitions

This section gives the definitions of the technical terms with respect to the Shelly library.

### On actions

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
`Shelly.createDomino(Object)` or `Shelly.createDomino()` and is followed by various methods provided
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

## Downloading

### Gradle

```
compile 'xiaofei.library:shelly:0.2.5-alpha4'
```

### Maven

```
<dependency>
  <groupId>xiaofei.library</groupId>
  <artifactId>shelly</artifactId>
  <version>0.2.5-alpha4</version>
  <type>pom</type>
</dependency>
```

## Usage

The following illustrates the usage of the Shelly library. Here I focus on the basic usage including
component registration, Domino creation and Domino invocation. After reading these, you will have a
basic understanding of the Shelly library.

The Domino discussed in the following is the basic Domino, which provides the basic methods for
performing various kinds of actions, for data transformation and for thread scheduling.

Moreover, the Shelly library also provides many other useful Dominoes, including but not limited to:

1. Task Domino,
which provides methods for executing a time-consuming task and performing various
kinds of actions according to the result or the failure of the task execution. The usage of a Task
Domino makes the business logic of your app clear and easy to understand.

2. Retrofit Domino,
which provides a convenient pattern for sending an HTTP request and performing
various kinds of actions according to the result or the failure of the request. The
Retrofit Task is very useful in the development of an app, which takes many advantages over the other
architectures for sending HTTP requests.

For the information about various kinds of Dominoes, please see [HERE](doc/MORE_DOMINOES.md).

Also, the Shelly library provides methods for merging the results of two Dominoes and combing two
results of two Dominoes into one result, which is useful especially when it comes to the Retrofit
Domino. These methods allow you to write a Domino which sends two HTTP requests at the same time
and uses the results of the two requests to perform actions. Also, you can write a Domino which
sends an HTTP request and after getting its result, sends another request. These features are inspired
by RxJava. See [HERE](doc/DOMINO_COMBINATION.md) for more information.

Moreover, the Shelly library provides some useful utilities, such as the stash to store and
get objects and the tuple class to combine several input together. Please see [HERE](doc/UTILITIES.md)
for more information.

The shelly library provides a novel pattern for developing a business-logic-oriented app, which makes
the business logic clear and easy to understand and makes the app easy to maintain. Please see
[HERE](doc/METHODOLOGY.md) for the methodology.

### Component registration

Each component which changes according to the change of a business object should be registered first,
and should be unregistered whenever it is destroyed.

The following is an example of the registration and unregistration of an Activity:

```
public class MyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Shelly.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Shelly.unregister(this);
    }

}
```

### Domino creation

A domino should be created and committed before it takes effect. The following is an example.
And more APIs can be found in the
[Domino](Shelly/blob/master/shelly/src/main/java/xiaofei/library/shelly/domino/Domino.java) class.

```
// Create a domino labeled "Example" which takes one or more Strings as input.
Shelly.<String>createDomino("Example")
        // Perform an action. The action is performed once.
        .target(new Action0() {
            @Override
            public void call() {
                // Do something
            }
        })
        // Perform an action which takes the String as input.
        // If one String is passed to this action, the action is performed once.
        // If two Strings are passed to this action, the action is performed twice.
        .target(new Action1<String>() {
            @Override
            public void call(String input) {
                // Do something
            }
        })
        // Perform another action which takes the String as input.
        // If one String is passed to this action, the action is performed once.
        // If two Strings are passed to this action, the action is performed twice.
        .target(new Action1<String>() {
            @Override
            public void call(String input) {
                // Do something
            }
        })
        // The above actions is performed in the thread in which the domino is invoked.
        // Now the following actions will be performed in background.
        .background()
        // Transform the String into an integer.
        // If one String is passed to this action, one integer will be passed to the following actions.
        // If two Strings are passed to this action, two integers will be passed to the following actions.
        .map(new Function1<String, Integer>() {
            @Override
            public Integer call(String input) {
                return null;
            }
        })
        // The following actions will be performed one after the other in background.
        .backgroundQueue()
        // Use a filter to filter the integers.
        // Only the integers labeled "true" will be passed to the following actions.
        .filter(new Function1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer input) {
                return false;
            }
        })
        // Pass the integer into the function and the function takes an integer as input
        // and return a list of Strings. Each String will be passed to the actions following
        // this action.
        // If one integer is passed to this action, and the function returns two Strings,
        // then two Strings will be passed to the following actions.
        // If two integers are passed to this action, and the function takes an integer as input and
        // returns two Strings, then we get four Strings here,
        // then four Strings will be passed to the following actions.
        .flatMap(new Function1<Integer, List<String>>() {
            @Override
            public List<String> call(Integer input) {
                return null;
            }
        })
        // The following actions will be performed in the main thread, i.e. the UI thread.
        .uiThread()
        // Perform an action on all registered instances of MyActivity.
        .target(MyActivity.class, new TargetAction0<MyActivity>() {
            @Override
            public void call(MyActivity myActivity) {
                // Do something
            }
        })
        // Pass all the Strings into the function and get a single double.
        // Now the following actions will receive only one single input, which is a double.
        .reduce(new Function1<List<String>, Double>() {
            @Override
            public Double call(List<String> input) {
                return null;
            }
        })
        // Perform an action on all registered instances of MyActivity.
        // If there are two instances, then:
        // If one String is passed to this action, the action is performed twice.
        // If two Strings are passed to this action, the action is performed four times.
        .target(MyActivity.class, new TargetAction1<MyActivity, Double>() {
            @Override
            public void call(MyActivity myActivity, Double input) {
                // Do something
            }
        })
        // Commit the Domino for later use.
        .commit();
```

Remember to commit the domino finally!

Each domino should be specified a unique label, which is an object, i.e. an Integer, a
String or something else.

Again, note that the above code will not perform any actions! What the code does is simply commit and
store the Domino for later use. To make the Domino perform actions, you should invoke the Domino.
Only after the Domino is invoked will it perform actions.

### Domino invocation

To invoke a domino, do the following:

```
Shelly.playDomino("Example", "Single String"); // Pass a single String to the domino

Shelly.playDomino("Example", "First String", "Second String"); // Pass two Strings to the domino
```

### Anonymous Domino

As is shown above, a unique label is needed to label the Domino to be invoked,
thus you should specify a unique label when creating a Domino, otherwise the created Domino shall
not be committed.

However, a Domino which do not have a label ("Anonymous Domino") is also quite useful in that,
there exist some situation where you only need to create a Domino but not want to commit it.
For example, you can perform an action on an anonymous Domino.

```
Shelly.<String>createDomino("Example 2")
        .target(new Action1<String>() {
            @Override
            public void call(String input) {

            }
        })
        .target(Shelly.<String>createDomino()
                        .map(new Function1<String, Integer>() {
                            @Override
                            public Integer call(String input) {
                                return null;
                            }
                        })
                        .target(new Action1<Integer>() {
                            @Override
                            public void call(Integer input) {

                            }
                        })
                        .target(new Action0() {
                            @Override
                            public void call() {

                            }
                        })
        )
        .target(new Action1<String>() {
            @Override
            public void call(String input) {

            }
        })
        .commit();
```

Moreover, you can merge or combine two anonymous Dominoes.
See [HERE](doc/DOMINO_COMBINATION.md) for more information.

### More kinds of Dominoes

The Domino class provides many basic methods. Also you can write derived Dominoes which extend the
class. In the Shelly library, there are already several kinds of derived Dominoes, which are shown
below.

The Task Domino provides methods for executing a time-consuming task and performing various
kinds of actions according to the result or the failure of the task execution. The usage of a Task
Domino makes the business logic of your app clear and easy to understand.

The Retrofit Domino provides a convenient pattern for sending an HTTP request and performing
various kinds of actions according to the result or the failure of the request. The
Retrofit Task is very useful in the development of an app, which takes many advantages over the other
architectures for sending HTTP requests.

For the information about various kinds of Dominoes, please see [HERE](doc/MORE_DOMINOES.md).

### Merging and combination of Dominoes

The Shelly library provides methods for merging the results of two Dominoes and combing two
results of two Dominoes into one result, which is useful especially when it comes to the Retrofit
Domino. These methods allow you to write a Domino which sends two HTTP requests at the same time
and uses the results of the two requests to perform actions. Also, you can write a Domino which
sends an HTTP request and after getting its result, sends another request. These features are inspired
by RxJava.

The Shelly library provides the methods for invoking Dominoes within a Domino.

See [HERE](doc/DOMINO_COMBINATION.md) for more information.

### Tuple and stash

The Shelly library provides some useful utilities, such as the stash to store and
get objects and the tuple class to combine several input together. Please see [HERE](doc/UTILITIES.md)
for more information.

### Methodology

The shelly library provides a novel pattern for developing a business-logic-oriented app, which makes
the business logic clear and easy to understand and makes the app easy to maintain. Please see
[HERE](doc/METHODOLOGY.md) for the methodology.

## License

Copyright (C) 2016 Xiaofei

HermesEventBus binaries and source code can be used according to the
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
