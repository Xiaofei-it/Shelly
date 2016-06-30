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

package xiaofei.library.shelly.runnable;

import xiaofei.library.shelly.scheduler.Scheduler;
import xiaofei.library.shelly.util.Config;
import xiaofei.library.shelly.util.SchedulerInputs;

/**
 * Created by Xiaofei on 16/6/23.
 */
public class ScheduledRunnable<R> implements Runnable {

    private Scheduler<R> mScheduler;

    private Runnable mRunnable;

    private int mWaiting;

    public ScheduledRunnable(Scheduler<R> scheduler, Runnable runnable, int waiting) {
        mScheduler = scheduler;
        mRunnable = runnable;
        mWaiting = waiting;
    }

    public Runnable getRunnable() {
        return mRunnable;
    }

    public void waitForInput() {
        int waitingIndex = mWaiting - 1;
        SchedulerInputs inputs = mScheduler.getInputs();
        try {
            inputs.lock(waitingIndex);
            while (!inputs.inputSet(waitingIndex)) {
                if (Config.DEBUG) {
                    System.out.println(waitingIndex + " before await " + Thread.currentThread().getName());
                }
                inputs.await(waitingIndex);
                if (Config.DEBUG) {
                    System.out.println(waitingIndex + " after await " + Thread.currentThread().getName());
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            inputs.unlock(waitingIndex);
        }
    }

    public boolean inputSet() {
        return mScheduler.getInputs().inputSet(mWaiting - 1);
    }

    @Override
    public void run() {
        waitForInput();
        mRunnable.run();
    }
}
