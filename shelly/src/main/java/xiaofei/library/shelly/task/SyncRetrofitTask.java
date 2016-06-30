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

package xiaofei.library.shelly.task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Xiaofei on 16/6/27.
 */
public abstract class SyncRetrofitTask<T, R> extends RetrofitTask<T, R> {
    @Override
    protected void call(Call<R> call) {
        Callback<R> callback = getCallback();
        try {
            Response<R> response = call.execute();
            callback.onResponse(call, response);
        } catch (Throwable t) {
            callback.onFailure(call, t);
        }
    }
}
