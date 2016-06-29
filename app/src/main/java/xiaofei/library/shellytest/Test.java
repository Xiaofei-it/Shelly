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

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import xiaofei.library.shelly.Shelly;
import xiaofei.library.shelly.domino.converter.RetrofitDominoConverter;
import xiaofei.library.shelly.function.Action0;
import xiaofei.library.shelly.function.Function1;
import xiaofei.library.shelly.function.TargetAction1;
import xiaofei.library.shelly.task.AsyncRetrofitTask;

/**
 * Created by Xiaofei on 16/6/1.
 */
public class Test {
    public static void init() {
        Shelly.<String>createDomino(1)
                .backgroundQueue()
                .target(new Action0() {
                    @Override
                    public void call() {
                        try {
                            Thread.sleep(20000);
                            System.out.println("Haha");
                        } catch (InterruptedException e) {

                        }
                    }
                })
                .map(new Function1<String, String>() {
                    @Override
                    public String call(String input) {
                        return input + "map1";
                    }
                })
                .uiThread()
                .target(MainActivity.class, new TargetAction1<MainActivity, String>() {
                    @Override
                    public void call(MainActivity mainActivity, String input) {
                        mainActivity.f(input);
                    }
                }).commit();

        Shelly.<String>createDomino(2)
                .backgroundQueue()
                .map(new Function1<String, String>() {
                    @Override
                    public String call(String input) {
                        return input + "map2";
                    }
                })
                .uiThread()
                .target(MainActivity.class, new TargetAction1<MainActivity, String>() {
                    @Override
                    public void call(MainActivity mainActivity, String input) {
                        mainActivity.f(input);
                    }
                }).commit();

        Retrofit retrofit = new Retrofit.Builder()
                //这里建议：- Base URL: 总是以/结尾；- @Url: 不要以/开头
                .baseUrl("http://www.weather.com.cn/")
                .build();
        final NetInterface netInterface = retrofit.create(NetInterface.class);
        Shelly.<String>createDomino(3)
                .background()
                .beginTask(new AsyncRetrofitTask<String, ResponseBody>() {
                    @Override
                    protected Call<ResponseBody> getCall(String s) {
                        return netInterface.test(s);
                    }
                })
                .convert(new RetrofitDominoConverter<String, ResponseBody>())
                .uiThread()
                .onResult(MainActivity.class, new TargetAction1<MainActivity, ResponseBody>() {
                    @Override
                    public void call(MainActivity mainActivity, ResponseBody input) {
                        try {
                            mainActivity.toast(input.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .commit();
    }
}
