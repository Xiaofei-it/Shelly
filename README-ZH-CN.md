# Shelly

面向业务逻辑的编程库。本库提供了一种全新的编程模式，将业务对象的变化对各个模块的影响通过方法链表示出来。

## 特色

1. 为面向业务逻辑的编程提供一种全新的编程模式。

2. 无论业务逻辑怎么变化，使用本库编写的代码都易于理解和维护。

3. 能够方便地发送网络请求和处理回调，尤其是发送并发请求和连续请求。

4. 能够方便地执行耗时任务和处理回调。

5. 提供强大丰富的数据流控制的API和线程调度的API。

## 预览

//TODO 介绍Domino

在介绍之前，我们先看一个例子。

假设现在你想打印文件夹里所有文件的名字。使用本库，你可以写如下代码：

```
Shelly.<String>createDomino("Print file names")
        .background()
        .flatMap((Function1) (input) -> {
                File[] files = new File(input).listFiles();
                List<String> result = new ArrayList<String>();
                for (File file : files) {
                    result.add(file.getName());
                }
                return result;
        })
        .perform((Action1) (input) -> {
                System.out.println(input);
        })
        .commit();
```

上面的代码用方法链打印文件夹中的文件名。文件夹的路径被传入，`Function1`获取此路径下的所有文件并将文件名传给
`Action1`，`Action1`将文件名打印出来。

我们看一个稍微复杂的例子。假设现在你想使用Retrofit发送HTTP请求，然后

1. 如果服务端的响应成功，那么调用`MyActivity`和`SecondActivity`中的两个函数；

2. 如果服务端的响应失败，那么在屏幕上显示一个toast；

3. 如果在发请求的时候出现错误或者异常，那么打印错误信息。

使用本库，你可以写下面的代码：

```
Shelly.<String>createDomino("Sending request")
        .background()
        .beginRetrofitTask((RetrofitTask) (s) -> {
                return netInterface.test(s);
        })
        .uiThread()
        .onSuccessResult(MainActivity.class, (TargetAction1) (mainActivity, input) -> {
                mainActivity.show(input.string());
        })
        .onSuccessResult(SecondActivity.class, (TargetAction1) (secondActivity, input) -> {
                secondActivity.show(input.string());
        })
        .onResponseFailure(MainActivity.class, (TargetAction1) (mainActivity, input) -> {
                Toast.makeText(
                    mainActivity.getApplicationContext(),
                    input.errorBody().string(),
                    Toast.LENGTH_SHORT
                ).show();
        })
        .onFailure((Action1) (input) -> {
                Log.e("Eric Zhao", "Error", input);
        })
        .endTask()
        .commit();
```

一个URL被传入，Retrofit发送HTTP请求，之后根据不同结果执行相应的操作。

代码中有一些线程调度相关的东西，比如`background()`和`uiThread()`。
`background()`是说下面的操作在后台执行。
`uiThread()`是说下面的操作在主线程（UI线程）执行。

上面的例子中，你可以看出发送HTTP请求后`MainActivity`和`SecondActivity`是如何变化的。
我们在一个地方就可以看到整个世界的变化。


注意，如果不调用Domino，上面这段代码实际上并不会执行任何操作！这段代码做的只是提交并存储Domino，供以后使用。
想要让Domino执行操作，必须调用它。只有调用Domino后，它才会执行操作。

这些只是简单的例子。实际上，本库是非常强大的，将在后面几节介绍。

## 思想

本节简单介绍本库的理论。如果想要看完整版，请查看[THEORY](doc/THEORY.md)。

在面向业务逻辑的编程中，一个特定的业务对象的改变可能会引起各个组件的变化，业务逻辑的复杂性也会增加模块之间的耦合。
为了降低耦合，我们通常使用listeners（observers）或者event bus，这些易于使用并且非常有效，但是有一些缺点，比如
难以维护，也可能有内存泄漏的风险。

为了解决这些问题，我写了Shelly库。

Shelly库提供了一种全新的编程模式，将业务对象的变化对各个模块的影响通过方法链表示出来。在方法链中，每个方法有一个
action参数，这个action执行相应的操作改变特定的组件。方法串起来后就代表了一系列的对各个组件的操作。这样你就能从
这一个地方看到整个世界的变化。

//TODO 创建Domino和使用Domino关系没说清，看看详细用法中有没有说清
创建Domino后，你可以“调用”Domino执行相应的操作。如果一个业务对象发生改变，你只需调用Domino，并且将这个对象传给它，
然后它就会按action序列一个个执行action。

如果要看详细的思想，请看[THEORY](doc/THEORY.md)。这里也会给出关于本库的许多技术术语的定语，比如Domino和数据流。

## 下载

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

## 用法


This section illustrates a brief outline of the usage of the Shelly library. For the details of
the usage, please read the articles listed below:

* [BASIC USAGE](doc/USAGE.md), contains the basic usage, including component registration,
Domino creation and Domino invocation.

* [MORE DOMINOES](doc/MORE_DOMINOES.md), contains the usage of various kinds of Dominoes.

* [DOMINO COMBINATION](doc/DOMINO_COMBINATION.md), illustrates how to merge the results of two
Dominoes and combing two results of two Dominoes into one result.

* [UTILITIES](doc/UTILITIES.md), contains the usage of the utilities provided by the Shelly library.

* [METHODOLOGY](doc/METHODOLOGY.md), illustrates how to use the Shelly library in action.

The Shelly library provides several kinds of Dominoes, including the basic Domino, the Task Domino
and the Retrofit Domino.

The basic Domino, which provides the basic methods for performing various kinds of actions,
for data transformation and for thread scheduling.

The Task Domino provides methods for executing a time-consuming task and performing various
kinds of actions according to the result or the failure of the task execution. The usage of a Task
Domino makes the business logic of your app clear and easy to understand.

The Retrofit Domino provides a convenient pattern for sending an HTTP request and performing
various kinds of actions according to the result or the failure of the request. The
Retrofit Task is very useful in the development of an app, which takes many advantages over the other
architectures for sending HTTP requests.

Also, the Shelly library provides methods for merging the results of two Dominoes and combing two
results of two Dominoes into one result, which is useful especially when it comes to the Retrofit
Domino. These methods allow you to write a Domino which sends two HTTP requests at the same time
and uses the results of the two requests to perform actions. Also, you can write a Domino which
sends an HTTP request and after getting its result, sends another request. These features are inspired
by RxJava.

Moreover, the Shelly library provides some useful utilities, such as the stash to store and
get objects and the tuple class to combine several input together.

The shelly library provides a novel pattern for developing a business-logic-oriented app, which makes
the business logic clear and easy to understand and makes the app easy to maintain.

## License

Copyright (C) 2016 Xiaofei

HermesEventBus binaries and source code can be used according to the
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
