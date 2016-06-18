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

package xiaofei.library.shelly.scheduler;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by Xiaofei on 16/5/31.
 */
public class UiThreadScheduler<T> extends Scheduler<T> {

    private static Handler sHandler = new Handler(Looper.getMainLooper());

    public <R> UiThreadScheduler(Scheduler<R> scheduler) {
        super(scheduler);
    }

    @Override
    protected void onSchedule(Runnable runnable) {
        boolean isMainThread = Looper.getMainLooper() == Looper.myLooper();
        if (isMainThread) {
            runnable.run();
        } else {
            sHandler.post(runnable);
        }
    }
}
