# Shelly实战

本文阐述Shelly库的方法论，以及如何在实战中使用Shelly库。

Shelly库提供了一种全新的编程模式，将业务对象的变化对各个模块的影响通过方法链表示出来。在使用Shelly库时，你不应该按原来的方法
构建你的工程。你应该使用一种看似略微丑陋但实际非常有用的模式来构建你的工程。

## 模式

本节阐述在工程中使用Shelly库的模式。

首先，你应该让所有UI组件只进行UI渲染的工作，不进行任何关于业务逻辑的操作。通过Domino实现业务逻辑，Domino根据业务逻辑调用
UI组件的函数改变UI。

第二，你应该始终记住，一个Domino对应一条业务逻辑，所以为每条业务逻辑创建一个Domino。

第三，将相似的业务逻辑归为一组，为这个组创建一个Java类。这个Java类创建组内所有的Domino，其他什么都不做。这个Java类
可以被看作“配置类”。

第四，在调用某个组内的Domino之前，让对应的配置类创建组内所有的Domino。

第五，当某个业务对象改变时，使用`Shelly.playDomino()`将对象传入并且调用对应的Domino来执行操作并且改变组件。

## 例子

本节给出一个例子来更好地理解上面地模式。

假设现在你要创建一个工程来做一个上传下载图片地app。

第一次打开app会让用户进行注册，以后每次都会让用户登录。登录后就可以使用这个app进行上传和下载图片，使用后可以退出。

我们将展示如何创建这个工程，并且展示关于Shelly库部分地模块。

### 创建Domino

First, we divides all of the business logic into two groups. One contains the business logic concerning
the user information, such as signing up, signing in and signing out. The other one contains the
business logic concerning the pictures, such as uploading and downloading pictures.

Second, we create two configuration classes corresponding to the two groups of business logic: `UserService`
and `PictureService` respectively. Each class provides a method for creating Dominoes.

The following is `UserService`:

```
public class UserService {

    public static final Object SIGN_UP = new Object();
    public static final Object SIGN_IN = new Object();
    public static final Object SIGN_OUT = new Object();

    public static void init() {

        // Use Retrofit to create the corresponding network interface.
        final UserNetwork userNetwork = ...;

        // Create the Domino for signing up.
        Shelly.<Pair<String, String>>createDomino(SIGN_UP)
                .background()
                .beginRetrofitTaskKeepingInput(new RetrofitTask<Pair<String,String>, String>() {
                    @Override
                    protected Call<String> getCall(Pair<String, String> input) {
                        return userNetwork.signUp(input.first, input.second);
                    }
                })
                .background()
                .onSuccessResult(new Action2<Pair<String, String>, String>() {
                    @Override
                    public void call(Pair<String, String> input1, String token) {
                        // Store the token.
                    }
                })
                .uiThread()
                .onSuccessResult(HomeActivity.class, new TargetAction2<HomeActivity, Pair<String, String>, String>() {
                    @Override
                    public void call(HomeActivity homeActivity, Pair<String, String> input1, String token) {
                        homeActivity.signUp(input1.first, token);
                    }
                })
                .onResponseFailure(HomeActivity.class, new TargetAction2<HomeActivity, Pair<String, String>, Response<String>>() {
                    @Override
                    public void call(HomeActivity homeActivity, Pair<String, String> input1, Response<String> input2) {
                        try {
                            Toast.makeText(homeActivity, input2.errorBody().string(), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .background()
                .onFailure(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable input) {
                        Log.e("Eric Zhao", "ERROR", input);
                    }
                })
                .endTask()
                .commit();

        // Create the Domino for signing in.
        Shelly.<Pair<String, String>>createDomino(SIGN_IN)
                // ...
                .commit();

        // Create the Domino for signing out.
        Shelly.<Pair<String, String>>createDomino(SIGN_OUT)
                // ...
                .commit();
    }
}
```

In the `init` method, we create the Dominoes for all the business logic concerning the user information.
Also, `UserService` contains some constants, such as `SIGN_IN`, `SIGN_UP` and `SIGN_OUT`. We regard
these constants as Domino labels.

Similarly, we create `PictureService`. The source code is not given here for simplicity.

Note that the above code will not perform any actions! What the code does is simply commit and
store the Domino for later use. To make the Domino perform actions, you should invoke the Domino.
Only after the Domino is invoked will it perform actions.

### Preparation for Domino invocation

In the `onCreate()` of the `Application` class, we write the following code:

```
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        UserService.init();
        PictureService.init();
    }
}
```

In each `onCreate` and `onDestroy` of all the `Activity`s, add `Shelly.register(this)` and `Shelly.unregister(this)`.

### Domino invocation

Whenever you want to invoke a particular Domino, write `Shelly.playDomino()`.

For example, you can write the following in the `onClick` method for signing up:

```
String userName = mUserNameEditText.getText().toString();
String password = mPasswordEditText.getText().toString();
Shelly.playDomino(UserService.SIGN_UP, Pair.create(userName, password));
```

Note the reason why we use constants for Domino labels and put all of the labels in the corresponding
configuration class. In this way, we can easily find all of the Domino invocation of a particular
Domino in the whole project, simply by using the IDE to find all the usages of the corresponding
constant.

## Summary

The above illustrates how to use the Shelly library in action.

There exists one disadvantage: a particular configuration class corresponding to a group
of business logic may be very long because of the complexity of the business logic. The more complex,
the longer the class will be.

Now change your traditional opinion. You should not regard the configuration class as
a traditional class. Instead you should regard it as a configuration file. A configuration file may
be very long. And the class contains a group of business logic. If the business logic is complex,
the class is long for sure. So feel free if the class is extremely long.

There exists several advantages if we write all the business logic in configuration classes:

1. We can see whole the business logic, especially what happens to the whole project after
a particular business object changes.

2. Whenever the business logic is modified, we only need to modify the source code in a single
configuration class.

3. Because UI components are responsible for only the UI rendering, it is flexible to compose them.
