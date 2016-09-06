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

package xiaofei.library.shellytest;

import android.util.Log;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import xiaofei.library.shelly.Shelly;
import xiaofei.library.shelly.domino.Domino;
import xiaofei.library.shelly.domino.converter.RetrofitDominoConverter;
import xiaofei.library.shelly.domino.converter.RetrofitDominoConverter2;
import xiaofei.library.shelly.function.Action0;
import xiaofei.library.shelly.function.Action1;
import xiaofei.library.shelly.function.Action2;
import xiaofei.library.shelly.function.Function1;
import xiaofei.library.shelly.function.TargetAction1;
import xiaofei.library.shelly.function.TargetAction2;
import xiaofei.library.shelly.task.RetrofitTask;

/**
 * Created by Xiaofei on 16/6/1.
 */
public class Test {

    public static final Object TOAST_SIMPLE_MESSAGE_1 = new Object();

    public static final Object TOAST_SIMPLE_MESSAGE_2 = new Object();

    public static final Object QUERY_BY_TASK_DOMINO = new Object();

    public static final Object QUERY_BY_TASK_DOMINO_II = new Object();

    public static final Object BLOCKING = new Object();

    public static final Object QUERY_BY_RETROFIT_DOMINO  = new Object();

    public static final Object QUERY_BY_RETROFIT_DOMINO_II  = new Object();

    public static final Object QUERY_ONE_BY_ONE = new Object();

    public static void init() {
        Shelly.<String>createDomino(TOAST_SIMPLE_MESSAGE_1)
                .backgroundQueue()
                .perform(new Action0() {
                    @Override
                    public void call() {
                        try {
                            Thread.sleep(10000);
                            System.out.println("Haha");
                        } catch (InterruptedException e) {

                        }
                    }
                })
                .map(new Function1<String, String>() {
                    @Override
                    public String call(String input) {
                        return "Show a message on the button 10s later: " + input;
                    }
                })
                .uiThread()
                .perform(MainActivity.class, new TargetAction1<MainActivity, String>() {
                    @Override
                    public void call(MainActivity mainActivity, String input) {
                        mainActivity.f(input);
                    }
                }).commit();

        Shelly.<String>createDomino(TOAST_SIMPLE_MESSAGE_2)
                .backgroundQueue()
                .map(new Function1<String, String>() {
                    @Override
                    public String call(String input) {
                        return "Show a message on the button after the activity is started: " + input;
                    }
                })
                .uiThread()
                .perform(MainActivity.class, new TargetAction1<MainActivity, String>() {
                    @Override
                    public void call(MainActivity mainActivity, String input) {
                        mainActivity.f(input);
                    }
                }).commit();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.weather.com.cn/")
                .build();
        final NetInterface netInterface = retrofit.create(NetInterface.class);
        Shelly.<String>createDomino(QUERY_BY_TASK_DOMINO)
                .background()
                .beginTask(new RetrofitTask<String, ResponseBody>() {
                    @Override
                    protected Call<ResponseBody> getCall(String s) {
                        return netInterface.test(s);
                    }
                })
                .convert(new RetrofitDominoConverter<String, ResponseBody>())
                .uiThread()
                .onSuccessResult(MainActivity.class, new TargetAction1<MainActivity, ResponseBody>() {
                    @Override
                    public void call(MainActivity mainActivity, ResponseBody input) {
                        try {
                            mainActivity.toast(input.string());
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
        Shelly.<Long>createDomino(QUERY_BY_TASK_DOMINO_II)
                .map(new Function1<Long, String>() {
                    @Override
                    public String call(Long input) {
                        return Long.toString(input);
                    }
                })
                .background()
                .beginTaskKeepingInput(new RetrofitTask<String, ResponseBody>() {
                    @Override
                    protected Call<ResponseBody> getCall(String s) {
                        return netInterface.test(s);
                    }
                })
                .convert(new RetrofitDominoConverter2<Long, String, ResponseBody>())
                .uiThread()
                .onSuccessResult(MainActivity.class, new TargetAction2<MainActivity, String, ResponseBody>() {
                    @Override
                    public void call(MainActivity mainActivity, String input1, ResponseBody input2) {
                        try {
                            String msg = input2.string();
                            Log.v("EricZhao", "TargetAction2 input1 = " + input1 + " input2 = " + msg);
                            mainActivity.toast(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .onSuccessResult(new Action2<String, ResponseBody>() {
                    @Override
                    public void call(String input1, ResponseBody input2) {
                        try {
                            // You will get nothing from input2 because the ResponseBody can only be read once.
                            Log.v("EricZhao", "Action2 input1 = " + input1 + " input2 = " + input2.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .onFailure(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable input) {
                        Log.e("EricZhao", "Error", input);
                    }
                })
                .endTask()
                .commit();

        Shelly.<String>createDomino(BLOCKING)
                .merge(new Domino[]{Shelly.<String>createAnonymousDomino()
                                .background()
                                .map(new Function1<String, Integer>() {
                                    @Override
                                    public Integer call(String input) {
                                        System.out.println("Map1 " + Thread.currentThread().getName());
                                        try {
                                            Thread.sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        return 3;
                                    }
                                })
                                .uiThread()
                                .map(new Function1<Integer, Integer>() {
                            @Override
                            public Integer call(Integer input) {
                                System.out.println("Map2 " + Thread.currentThread().getName());
                                return input;
                            }
                        })}
                )
                .commit();
        Shelly.<String>createDomino(QUERY_BY_RETROFIT_DOMINO)
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
                            mainActivity.toast(input.string());
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

        Shelly.<Long>createDomino(QUERY_BY_RETROFIT_DOMINO_II)
                .map(new Function1<Long, String>() {
                    @Override
                    public String call(Long input) {
                        return Long.toString(input);
                    }
                })
                .background()
                .beginRetrofitTaskKeepingInput(new RetrofitTask<String, ResponseBody>() {
                    @Override
                    protected Call<ResponseBody> getCall(String s) {
                        return netInterface.test(s);
                    }
                })
                .uiThread()
                .onSuccessResult(MainActivity.class, new TargetAction2<MainActivity, String, ResponseBody>() {
                    @Override
                    public void call(MainActivity mainActivity, String input1, ResponseBody input2) {
                        try {
                            String msg = input2.string();
                            Log.v("EricZhao", "TargetAction2 input1 = " + input1 + " input2 = " + msg);
                            mainActivity.toast(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .onSuccessResult(new Action2<String, ResponseBody>() {
                    @Override
                    public void call(String input1, ResponseBody input2) {
                        try {
                            // You will get nothing from input2 because the ResponseBody can only be read once.
                            Log.v("EricZhao", "Action2 input1 = " + input1 + " input2 = " + input2.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .onFailure(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable input) {
                        Log.e("EricZhao", "Error", input);
                    }
                })
                .endTask()
                .commit();

        Shelly.<Long>createDomino(QUERY_ONE_BY_ONE)
                .map(new Function1<Long, String>() {
                    @Override
                    public String call(Long input) {
                        return Long.toString(input);
                    }
                })
                .background()
                .beginRetrofitTaskKeepingInput(new RetrofitTask<String, ResponseBody>() {
                    @Override
                    protected Call<ResponseBody> getCall(String s) {
                        return netInterface.test(s);
                    }
                })
                .uiThread()
                .onSuccessResult(MainActivity.class, new TargetAction2<MainActivity, String, ResponseBody>() {
                    @Override
                    public void call(MainActivity mainActivity, String input1, ResponseBody input2) {
                        try {
                            String msg = input2.string();
                            Log.v("EricZhao", "TargetAction2 input1 = " + input1 + " input2 = " + msg);
                            mainActivity.toast(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .onSuccessResult(new Action2<String, ResponseBody>() {
                    @Override
                    public void call(String input1, ResponseBody input2) {
                        try {
                            // You will get nothing from input2 because the ResponseBody can only be read once.
                            Log.v("EricZhao", "Action2 input1 = " + input1 + " input2 = " + input2.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .onSuccessResult(new Action2<String, ResponseBody>() {
                    @Override
                    public void call(String input1, ResponseBody input2) {
                        Shelly.playDomino(QUERY_BY_RETROFIT_DOMINO, input1);
                    }
                })
                .onFailure(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable input) {
                        Log.e("EricZhao", "Error", input);
                    }
                })
                .endTask()
                .commit();

    }
}
