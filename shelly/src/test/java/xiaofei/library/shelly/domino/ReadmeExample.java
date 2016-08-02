/**
 *
 * Copyright 2016 Xiaofei
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package xiaofei.library.shelly.domino;

import java.io.File;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import xiaofei.library.shelly.Shelly;
import xiaofei.library.shelly.function.Action0;
import xiaofei.library.shelly.function.Action1;
import xiaofei.library.shelly.function.Function1;
import xiaofei.library.shelly.function.TargetAction0;
import xiaofei.library.shelly.function.TargetAction1;
import xiaofei.library.shelly.function.TargetAction2;
import xiaofei.library.shelly.task.RetrofitTask;
import xiaofei.library.shelly.task.Task;
import xiaofei.library.shelly.tuple.Pair;

/**
 * Created by Xiaofei on 16/7/28.
 */
public class ReadmeExample {
    public void f() {
        class MyActivity {}
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
        class Bitmap {}
        class ImageView {}
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
                .target(new Action1<Pair<String, Bitmap>>() {
                    @Override
                    public void call(Pair<String, Bitmap> input) {

                    }
                })
                .commit();
        final Network network = null;

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
        Shelly.<String>createDomino("Find *.jpg")
                .background()
                .map(new Function1<String, File>() {
                    @Override
                    public File call(String input) {
                        return new File(input);
                    }
                })
                .flatMap(new Function1<File, List<File>>() {
                    @Override
                    public List<File> call(File input) {
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
                .flatMap(new Function1<File, List<File>>() {
                    @Override
                    public List<File> call(File input) {
                        // Find *.png in this folder
                        return null;
                    }
                })
                .commit();
        Shelly.<String>createDomino("Find *.png and *.jpg")
                .background()
                .merge(Shelly.<String, File>getDominoByLabel("Find *.png"),
                        Shelly.<String, File>getDominoByLabel("Find *.jpg"))
                .uiThread()
                .target(new Action1<File>() {
                    @Override
                    public void call(File input) {

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
                .flatMap(new Function1<File, List<File>>() {
                    @Override
                    public List<File> call(File input) {
                        // Find *.png in this folder
                        return null;
                    }
                })
                .commit();
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
                                .flatMap(new Function1<File, List<File>>() {
                                    @Override
                                    public List<File> call(File input) {
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
                                .flatMap(new Function1<File, List<File>>() {
                                    @Override
                                    public List<File> call(File input) {
                                        // Find *.png in this folder
                                        return null;
                                    }
                                })
                )
                .uiThread()
                .target(new Action1<File>() {
                    @Override
                    public void call(File input) {

                    }
                })
                .commit();
    }
    class User {
        String getId() {
            return null;
        }
    }
    interface Network {
        Call<User> getUser(String id);
    }
}
