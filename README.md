# Shelly

A library for business-logic-oriented programming, providing a novel pattern which uses a method
chain to illustrate how each component varies with a business object.

##Features

1. Provide a novel pattern for business-logic-oriented programming.

2. Make the source code of a business-logic-oriented app easy to understand and maintain.

3. Powerful APIs for data
##Phylosophy

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
Also it, as is discussed above, provides an EventBus-like feature for preforming actions on registered
components. Therefore, a method chain provides you with a global view of what happens after the
change of a business object. The method chain is named "Domino" in the Shelly library for it represents
a series of actions to perform one after the other, as the domino game does.

The above is discussing something about the structure of the method chain, i.e. "Domino".
Now let's say something about how to "invoke" the Domino, which is a bit easy.
When a business object is changed, you "invoke" the Domino and pass the business object to it.
Then it performs actions on each corresponding component according to the sequence of the methods.


##Definitions

This section gives the definitions of the technical terms with respect to the Shelly library.

An "action" refers to a sequence of Java statements, the effect of which includes but is not limited
to, performing certain operations on certain components, producing certain outputs, performing
data transformation, and performing thread scheduling. An action is represented by a Java
class or a Java interface, in which there exists a method, which is named "call" and contains the
sequence of Java statements of the action and the invocation of which performs the action.

"Performing an action" refers to executing the sequence of Java statements of the action.

"The input of an action" is a group of objects which is passed to the "call" method as arguments.
The following illustrates the relationship between the input and the performance of an action:

* Suppose the number of arguments the "call" method takes, excluding the arguments representing the
components, is `a`. And suppose the number of the objects contained in the input group is `b`. Then:

*    1. If the "call" method takes no arguments, then the action is performed only once.

>2. If the "call" method takes

A "Domino" is a Java object which, under certain circumstances, performs a sequence of actions.
For the sake of simplicity, a "Domino" may also refer to the Java class of a particular Domino
object.

"Creating a Domino" refers to the operation of building a particular Java instance of a Domino
class. A Domino is usually created by a Java method chain which starts with
`Shelly.createDomino(Object)` or `Shelly.createDomino()` and is followed by various methods provided
by the Shelly library.

"Committing a Domino" refers to the operation of causing the Shelly library to hold a Java reference
of the specified Domino object for later usage.

"Playing a Domino" or "Invoking a Domino" refers to the operation of causing the specified Domino to
perform a sequence of actions. To play a Domino, a group of objects is needed. The group must contain
one or more objects.


of the corresponding
class of the action. The "call" method may take no arguments,



##Downloading

###Gradle

```
compile 'xiaofei.library:shelly:0.2.5-alpha4'
```

###Maven

```
<dependency>
  <groupId>xiaofei.library</groupId>
  <artifactId>shelly</artifactId>
  <version>0.2.5-alpha4</version>
  <type>pom</type>
</dependency>
```

##Usage

The following illustrates the usage of the Shelly library. Here I focus on the basic usage including
component registration, Domino creation and Domino invocation. After reading these, you will have a
basic understanding of the Shelly library.

The Domino discussed below is the basic Domino, which provides the basic methods for
performing different kinds of actions, for data transformation and for thread scheduling.

The Shelly library also provides many other useful Dominoes, including but not limited to:

1. Task Domino, which provides a method for executing a time-consuming task and performs different
kinds of actions according to the result or the failure of the task execution. The usage of a Task
Domino makes the business logic of your app clear and easy to understand.

2. Retrofit Domino, which provides a convenient pattern for sending a HTTP request and performing
different kinds of actions according to the result or the failure of the request. The
Retrofit Task is very useful in the development of an app, which takes many advantages over the other
architectures for sending HTTP requests.

Also, the Shelly library provides methods for merging the results of two Dominoes and combing two
results of two Dominoes into one result, which is useful especially when it comes to the Retrofit
Domino. These methods allow you to write a Domino which sends two HTTP requests at the same time
and uses the results of the two requests to perform actions. Also, you can write a Domino which
sends a HTTP request and after getting its result sends another request. These features are inspired
by RxJava.

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
        // Create a domino labeled "Example" which takes one or more Strings as input
        Shelly.<String>createDomino("Example")
                // Perform an action. The action is performed once.
                .target(new Action0() {
                    @Override
                    public void call() {
                        // Do something
                    }
                })
                // Perform an action which takes the String as input.
                // If one String is passed here, the action is performed once.
                // If two Strings are passed here, the action is performed twice.
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        // Do something
                    }
                })
                // Perform another action which takes the String as input.
                // If one String is passed here, the action is performed once.
                // If two Strings are passed here, the action is performed twice.
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
                // If one String is passed here, one integer will be passed to the following actions.
                // If two Strings are passed here, two integers will be passed to the following actions.
                .map(new Function1<String, Integer>() {
                    @Override
                    public Integer call(String input) {
                        return null;
                    }
                })
                // The following actions will be performed in a queue in background.
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
                // and return a list of Strings. Each String will be passed to the following actions.
                // If an integer is passed here, and the function returns two Strings,
                // then two Strings will be passed to the following actions.
                // If two integers are passed here, and the function takes an integer as input and
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
                // Now the following actions will receive only one single input which is a double.
                .reduce(new Function1<List<String>, Double>() {
                    @Override
                    public Double call(List<String> input) {
                        return null;
                    }
                })
                // Perform an action on all registered instances of MyActivity.
                // If there are two instances, then:
                // If one String is passed here, the action is performed twice.
                // If two Strings are passed here, the action is performed four times.
                .target(MyActivity.class, new TargetAction1<MyActivity, Double>() {
                    @Override
                    public void call(MyActivity myActivity, Double input) {
                        // Do something
                    }
                })
                .commit();
```

Remember to commit the domino finally!

Each domino should be specified a unique label, which is an object, i.e. an Integer, a
String or something else.

More methods will be discussed in the future.

###Domino invocation

When you want to invoke a domino, do the following:

```
Shelly.playDomino("Example", "Single String"); //Pass a single String to the domino

Shelly.playDomino("Example", "First String", "Second String"); //Pass two Strings to the domino
```

###Anonymous Domino

As is shown above, a unique label is needed to indicate the Domino to be invoked,
thus you should specify a unique label when creating a Domino, otherwise the created Domino shall
not be committed.

However, A Domino which do not have a label (anonymous Domino) is also quite useful in that,
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

Moreover, you can merge two anonymous Dominoes. See the following for the details.

###More kinds of Dominoes

The Domino class provides many basic methods. Also you can write derived Dominoes which extend the
class. In the Shelly library, there are already several kinds of derived Dominoes, which are shown
below.

####Task Domino

The Shelly library provides a method for executing a task and processing the result according to the
result or the failure of the task after its execution.

Here is an example.

```
        // Create a domino labeled "LoadingBitmap" which takes a String as input,
        // which is the path of a bitmap.
        Shelly.<String>createDomino("LoadingBitmap")
                // The following actions will be performed in background.
                .background()
                // Execute a task which loads a bitmap according to the path.
                .beginTask(new Task<String, Bitmap, Exception>() {
                    private Bitmap load(String path) throws IOException {
                        if (path == null) {
                            throw new IOException();
                        } else {
                            return null;
                        }
                    }
                    @Override
                    protected void onExecute(String input) {
                        // We load the bitmap.
                        // Remember to call Task.notifySuccess() or Task.notifyFailure() in the end.
                        // Otherwise, the Domino gets stuck here.
                        try {
                            Bitmap bitmap = load(input);
                            notifySuccess(bitmap);
                        } catch (IOException e) {
                            notifyFailure(e);
                        }
                    }
                })
                // The following performs different actions according to the result or the failure
                // of the task.

                // If the execution of the above task succeeds, perform an action.
                .onSuccess(new Action0() {
                    @Override
                    public void call() {
                        // Do something.
                    }
                })
                // If the execution of the above task succeeds,
                // perform an action which takes a bitmap as input.
                .onSuccess(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap input) {
                        // Do something.
                    }
                })
                // The following actions will be performed in the main thread, i.e. the UI thread.
                .uiThread()
                // If the execution of the above task succeeds,
                // perform an action on all registered instances of ImageView.
                .onSuccess(ImageView.class, new TargetAction1<ImageView, Bitmap>() {
                    @Override
                    public void call(ImageView imageView, Bitmap input) {
                        // Do something.
                    }
                })
                // The following actions will be performed in background.
                .background()
                // If the execution of the above task fails, perform an action.
                .onFailure(new Action0() {
                    @Override
                    public void call() {
                        // Do something.
                    }
                })
                // If the execution of the above task fails, print the stack trace fo the exception.
                .onFailure(new Action1<Exception>() {
                    @Override
                    public void call(Exception input) {
                        input.printStackTrace();
                    }
                })
                // If the execution of the above task fails,
                // perform an action on all registered instances of ImageView.
                .onFailure(ImageView.class, new TargetAction1<ImageView, Exception>() {
                    @Override
                    public void call(ImageView imageView, Exception input) {
                        // Do something.
                    }
                })
                .endTask()
                .commit();

```

You may find that after the execution of the task, the result or the exception will be passed to
the following actions, but the original input of the task has been lost. Sometimes we need to know
the original input in the following actions. So you can execute a task using another method.

```
        // Create a domino labeled "LoadingBitmap" which takes a String as input,
        // which is the path of a bitmap.
        Shelly.<String>createDomino("LoadingBitmap 2")
                // The following actions will be performed in background.
                .background()
                // Execute a task which loads a bitmap according to the path.
                .beginTaskKeepingInput(new Task<String, Bitmap, Exception>() {
                    private Bitmap load(String path) throws IOException {
                        if (path == null) {
                            throw new IOException();
                        } else {
                            return null;
                        }
                    }
                    @Override
                    protected void onExecute(String input) {
                        // We load the bitmap.
                        // Remember to call Task.notifySuccess() or Task.notifyFailure() in the end.
                        // Otherwise, the Domino gets stuck here.
                        try {
                            Bitmap bitmap = load(input);
                            notifySuccess(bitmap);
                        } catch (IOException e) {
                            notifyFailure(e);
                        }
                    }
                })
                // The following performs different actions according to the result or the failure
                // of the task.

                // If the execution of the above task succeeds, perform an action.
                .onSuccess(new Action0() {
                    @Override
                    public void call() {
                        // Do something.
                    }
                })
                // If the execution of the above task succeeds,
                // perform an action which takes a bitmap as input.
                .onSuccess(new Action1<Pair<String, Bitmap>>() {
                    @Override
                    public void call(Pair<String, Bitmap> input) {

                    }
                })
                // The following actions will be performed in the main thread, i.e. the UI thread.
                .uiThread()
                // If the execution of the above task succeeds,
                // perform an action on all registered instances of ImageView.
                .onSuccess(ImageView.class, new TargetAction1<ImageView, Pair<String,Bitmap>>() {
                    @Override
                    public void call(ImageView imageView, Pair<String, Bitmap> input) {

                    }
                })
                // The following actions will be performed in background.
                .background()
                // If the execution of the above task fails, perform an action.
                .onFailure(new Action0() {
                    @Override
                    public void call() {
                        // Do something.
                    }
                })
                // If the execution of the above task fails, print the stack trace fo the exception.
                .onFailure(new Action1<Exception>() {
                    @Override
                    public void call(Exception input) {
                        input.printStackTrace();
                    }
                })
                // If the execution of the above task fails,
                // perform an action on all registered instances of ImageView.
                .onFailure(ImageView.class, new TargetAction1<ImageView, Exception>() {
                    @Override
                    public void call(ImageView imageView, Exception input) {
                        // Do something.
                    }
                })
                .endTask()
                // Now the task ends, but the result remains. You can do more in the following.
                .target(new Action1<Pair<String, Bitmap>>() {
                          @Override
                          public void call(Pair<String, Bitmap> input) {

                          }
                })
                .commit();
```

Note that TaskDomino.endTask() will keep the result of the task, thus you can perform more actions
after the execution. See the above for example.

####Retrofit Domino

The Shelly library provides a method for using Retrofit to send a HTTP request and processing the
result according to the result or the failure of the request.

Suppose that we want to use Retrofit to send a HTTP request to get the user information.

```
        Shelly.<String>createDomino("GETTING_USER")
                .background()
                // Return a call for the Retrofit task.
                .beginRetrofitTask(new RetrofitTask<String, User>() {
                    @Override
                    protected Call<User> getCall(String s) {
                        return network.getUser(s);
                    }
                })
                .uiThread()
                // If the request succeed and we get the user information,
                // perform an action.
                .onSuccessResult(new Action0() {
                    @Override
                    public void call() {

                    }
                })
                // If the request succeed and we get the user information,
                // perform an action on MyActivity.
                .onSuccessResult(MyActivity.class, new TargetAction1<MyActivity, User>() {
                    @Override
                    public void call(MyActivity mainActivity, User input) {

                    }
                })
                // If the request succeed but we get an error from the server,
                // perform an action.
                .onResponseFailure(new Action0() {
                    @Override
                    public void call() {

                    }
                })
                // If the request succeed but we get an error from the server,
                // perform an action on MyActivity.
                .onResponseFailure(MyActivity.class, new TargetAction1<MyActivity, Response<User>>() {
                    @Override
                    public void call(MyActivity myActivity, Response<User> input) {

                    }
                })
                // If the request fails, perform an action.
                .onFailure(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable input) {

                    }
                })
                // If the request fails, perform an action on MyActivity.
                .onFailure(MyActivity.class, new TargetAction1<MyActivity, Throwable>() {
                    @Override
                    public void call(MyActivity myActivity, Throwable input) {

                    }
                })
                .endTask()
                .commit();
```

Also, you may find that after the execution of the request, the result or the exception will be
passed to the following actions, but the original input of the task has been lost. Sometimes we
need to know the original input in the following actions. So you can execute a task using another
method.

```
        Shelly.<String>createDomino("GETTING_USER")
                .background()
                // Return a call for the Retrofit task.
                .beginRetrofitTaskKeepingInput(new RetrofitTask<String, User>() {
                    @Override
                    protected Call<User> getCall(String s) {
                        return network.getUser(s);
                    }
                })
                .uiThread()
                // If the request succeed and we get the user information,
                // perform an action.
                .onSuccessResult(new Action0() {
                    @Override
                    public void call() {

                    }
                })
                // If the request succeed and we get the user information,
                // perform an action on MyActivity.
                .onSuccessResult(MyActivity.class, new TargetAction2<MyActivity, String, User>() {
                    @Override
                    public void call(MyActivity myActivity, String input1, User input2) {

                    }
                })
                // If the request succeed but we get an error from the server,
                // perform an action.
                .onResponseFailure(new Action0() {
                    @Override
                    public void call() {

                    }
                })
                // If the request succeed but we get an error from the server,
                // perform an action on MyActivity.
                .onResponseFailure(MyActivity.class, new TargetAction2<MyActivity, String, Response<User>>() {
                    @Override
                    public void call(MyActivity myActivity, String input1, Response<User> input2) {

                    }
                })
                // If the request fails, perform an action.
                .onFailure(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable input) {

                    }
                })
                // If the request fails, perform an action on MyActivity.
                .onFailure(MyActivity.class, new TargetAction1<MyActivity, Throwable>() {
                    @Override
                    public void call(MyActivity myActivity, Throwable input) {

                    }
                })
                .endTask()
                .commit();
```

###The merging and combination of Dominoes

Dominoes can be merged. If we merge two Dominoes whose final data are of the same type, then we get
a new Domino whose initial data is the union of their final data.

```
        Shelly.<String>createDomino("Find *.jpg")
                .background()
                .map(new Function1<String, File>() {
                    @Override
                    public File call(String input) {
                        return new File(input);
                    }
                })
                .flatMap(new Function1<File, List<Bitmap>>() {
                    @Override
                    public List<Bitmap> call(File input) {
                        // Find *.jpg in this folder
                        return null;
                    }
                })
                .commit();
        Shelly.<String>createDomino("Find *.png")
                .background()
                .map(new Function1<String, File>() {
                    @Override
                    public File call(String input) {
                        return new File(input);
                    }
                })
                .flatMap(new Function1<File, List<Bitmap>>() {
                    @Override
                    public List<Bitmap> call(File input) {
                        // Find *.png in this folder
                        return null;
                    }
                })
                .commit();
        Shelly.<String>createDomino("Find *.png and *.jpg")
                .background()
                .merge(Shelly.<String, Bitmap>getDominoByLabel("Find *.png"),
                        Shelly.<String, Bitmap>getDominoByLabel("Find *.jpg"))
                .uiThread()
                .target(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap input) {

                    }
                })
                .commit();
```

Also, you can write the following using anonymous Dominoes.

```
        Shelly.<String>createDomino("Find *.png and *.jpg")
                .background()
                .merge(Shelly.<String>createDomino()
                                .background()
                                .map(new Function1<String, File>() {
                                    @Override
                                    public File call(String input) {
                                        return new File(input);
                                    }
                                })
                                .flatMap(new Function1<File, List<Bitmap>>() {
                                    @Override
                                    public List<Bitmap> call(File input) {
                                        // Find *.jpg in this folder
                                        return null;
                                    }
                                }),
                        Shelly.<String>createDomino()
                                .background()
                                .map(new Function1<String, File>() {
                                    @Override
                                    public File call(String input) {
                                        return new File(input);
                                    }
                                })
                                .flatMap(new Function1<File, List<Bitmap>>() {
                                    @Override
                                    public List<Bitmap> call(File input) {
                                        // Find *.png in this folder
                                        return null;
                                    }
                                })
                )
                .uiThread()
                .target(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap input) {

                    }
                })
                .commit();
```

You can combine two Dominoes into one. Specifically, suppose there are two Dominoes named "Domino A"
and "Domino B", and you can provide a function and combine these two Dominoes into a new Domino
named "Domino C" in the following way: The final data of the two Dominoes are passed into th
function and the function returns new data as the input of Domino C.

The following is an example.

```
        Shelly.<String>createDomino("Login")
                .combine(
                        Shelly.<String>createDomino()
                                .beginRetrofitTask(new RetrofitTask<String, User>() {
                                    @Override
                                    protected Call<User> getCall(String s) {
                                        return network.getUser(s);
                                    }
                                })
                                .endTask(),
                        Shelly.<String>createDomino()
                                .beginRetrofitTask(new RetrofitTask<String, Summary>() {
                                    @Override
                                    protected Call<Summary> getCall(String s) {
                                        return network.getSummary(s);
                                    }
                                })
                                .endTask(),
                        new Function2<User, Summary, Internal>() {
                            @Override
                            public Internal call(User input1, Summary input2) {
                                return null;
                            }
                        }
                )
                .target(new Action1<Internal>() {
                    @Override
                    public void call(Internal input) {

                    }
                })
                .commit();
```

###Domino invocation within a Domino

The Shelly library provides the methods for invoking Dominoes.

The following is an example.

```
        Shelly.<String>createDomino("Login")
                .beginRetrofitTask(new RetrofitTask<String, User>() {
                    @Override
                    protected Call<User> getCall(String s) {
                        return network.getUser(s);
                    }
                })
                .onSuccessResult(
                        Shelly.<User>createDomino()
                                .beginRetrofitTask(new RetrofitTask<User, Summary>() {
                                    @Override
                                    protected Call<Summary> getCall(User user) {
                                        return network.getSummary(user.getId());
                                    }
                                })
                                .onSuccessResult(new Action1<Summary>() {
                                    @Override
                                    public void call(Summary input) {

                                    }
                                })
                                .endTask()
                )
                .endTask()
                .commit();
```

###Tuple input

You may find that in the above examples, all of the Dominoes take only one input each time, but how
about multiple input?

The Shelly library provides you with some tuple classes, in which you can put multiple inputs.

The following is an example.

```
        Shelly.<Triple<Integer, Integer, Double>>createDomino("Add")
                .map(new Function1<Triple<Integer,Integer,Double>, Double>() {
                    @Override
                    public Double call(Triple<Integer, Integer, Double> input) {
                        return input.first + input.second + input.third;
                    }
                })
                .target(new Action1<Double>() {
                    @Override
                    public void call(Double input) {
                        System.out.print(input);
                    }
                })
                .commit();
```

Invoke the Domino with the following statement:

```
Shelly.playDomino("Add", Triple.create(1, 2, 3.0));
```

###Stash

Sometimes, in an action performed by a Domino, you want to save something for later use. Now you can
use the stash actions or stash functions, whose classes are located in the package
`xiaofei.library.shelly.function.stashfunction`. These actions and functions are the same as their
corresponding actions and functions except that they provide additional methods for stashing, with
which you can stash data for later use.

The following is an example:

```
```