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

###Component registration

Each component which changes according to the change of a business object should be registered first,
and should be unregistered whenever it is destroyed.

```
Shelly.register(this);
```

```
Shelly.unregister(this);
```

###Domino creation

A domino should be created and committed before it takes effect. Here is an example. And more APIs
can be found in the Domino class.

```
        //Create a domino labeled "Example" which takes one or more Strings as input
        Shelly.<String>createDomino("Example")
                //Perform an action. The action is performed once.
                .target(new Action0() {
                    @Override
                    public void call() {
                        //Do something
                    }
                })
                //Perform an action which takes the String as input.
                //If one String is passed here, the action is performed once.
                //If two Strings are passed here, the action is performed twice.
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        //Do something
                    }
                })
                //Perform another action which takes the String as input.
                //If one String is passed here, the action is performed once.
                //If two Strings are passed here, the action is performed twice.
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        //Do something
                    }
                })
                //The above actions is performed in the thread in which the domino is invoked.
                //Now the following actions will be perform in background.
                .background()
                //Transform the String into an integer.
                //If one String is passed here, one integer will be passed to the following actions.
                //If two Strings are passed here, two integers will be passed to the following actions.
                .map(new Function1<String, Integer>() {
                    @Override
                    public Integer call(String input) {
                        return null;
                    }
                })
                //The following actions will be perform in a queue in background.
                .backgroundQueue()
                //Use a filter to filter the integers.
                //Only the integers labeled "true" will be passed to the following actions.
                .filter(new Function1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer input) {
                        return false;
                    }
                })
                //Pass the integer into the function and the function takes an integer as input
                //and return a list of Strings. Each String will be passed to the following actions.
                //If an integer is passed here, and the function returns two Strings,
                //then two Strings will be passed to the following actions.
                //If two integers are passed here, and the function takes an integer as input and
                //returns two Strings, then we get four Strings here,
                //then four Strings will be passed to the following actions.
                .flatMap(new Function1<Integer, List<String>>() {
                    @Override
                    public List<String> call(Integer input) {
                        return null;
                    }
                })
                //The following actions will be perform in the main thread, i.e. the UI thread.
                .uiThread()
                //Perform an action on all registered instances of MyActivity.
                .target(MyActivity.class, new TargetAction0<MyActivity>() {
                    @Override
                    public void call(MyActivity myActivity) {
                        //Do something
                    }
                })
                //Pass all the Strings into the function and get a single double.
                //Now the following actions will receive only one single input which is a double.
                .reduce(new Function1<List<String>, Double>() {
                    @Override
                    public Double call(List<String> input) {
                        return null;
                    }
                })
                //Perform an action on all registered instances of MyActivity.
                //If there are two instances, then:
                //If one String is passed here, the action is performed twice.
                //If two Strings are passed here, the action is performed four times.
                .target(MyActivity.class, new TargetAction1<MyActivity, Double>() {
                    @Override
                    public void call(MyActivity myActivity, Double input) {
                        //Do something
                    }
                })
                .commit();
```

Remember to commit the domino finally!

Each domino should be specified a unique label, which is an object, i.e. it can be an Integer, a
String or something else.

More methods will be discussed in the future.

####Task Domino

The Shelly library provides a method for executing a task and processing the result according to the
result or the failure of the task after its execution.

####Retrofit Domino

The Shelly library provides a method for executing a task and processing the result according to the
result or the failure of the task after its execution.

####Merge, combine and others

###Domino invocation

When you want to invoke a domino, do the following:

```
Shelly.playDomino("Example", "Single String"); //Pass a single String to the domino

Shelly.playDomino("Example", "First String", "Second String"); //Pass two Strings to the domino
```