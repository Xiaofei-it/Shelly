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

package xiaofei.library.shelly.demo;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import xiaofei.library.shelly.Shelly;
import xiaofei.library.shelly.function.Action1;
import xiaofei.library.shelly.function.Action2;
import xiaofei.library.shelly.function.TargetAction1;
import xiaofei.library.shelly.function.TargetAction2;
import xiaofei.library.shelly.task.RetrofitTask;
import xiaofei.library.shelly.tuple.Pair;
import xiaofei.library.shelly.tuple.Triple;

/**
 * Created by Xiaofei on 16/8/10.
 */
class HomeActivity extends Activity {
    void signUp(String s1, String s2) {}
}

interface UserNetwork {
    Call<String> signUp(String s1, String s2);
}

public class UserService {

    public static final Object SIGN_UP = new Object();
    public static final Object SIGN_IN = new Object();
    public static final Object SIGN_OUT = new Object();

    public static void init() {
        final UserNetwork userNetwork = null;
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
        Shelly.<Pair<String, String>>createDomino(SIGN_IN)
                .commit();
    }
}
