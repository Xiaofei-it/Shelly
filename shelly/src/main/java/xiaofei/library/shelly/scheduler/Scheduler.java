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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import xiaofei.library.shelly.function.Function1;
import xiaofei.library.shelly.runnable.BlockingRunnable;
import xiaofei.library.shelly.util.Player;
import xiaofei.library.shelly.runnable.ScheduledRunnable;
import xiaofei.library.shelly.util.SchedulerInputs;

/**
 * Created by Xiaofei on 16/5/31.
 *
 * Not thread-safe!!!
 */
public abstract class Scheduler<T> {

    private static final int STATE_RUNNING = 0;

    private static final int STATE_PAUSE = 1;

    //This field will be accessed from different threads. So access it in a synchronized block instead of using volatile.
    private int mState;

    private final SchedulerInputs mInputs;

    public Scheduler(List<T> input) {
        mInputs = new SchedulerInputs();
        if (input instanceof CopyOnWriteArrayList) {
            mInputs.add((CopyOnWriteArrayList<Object>) input);
        } else {
            mInputs.add(new CopyOnWriteArrayList<Object>(input));
        }
        mState = STATE_RUNNING;
    }

    public <R> Scheduler(Scheduler<R> scheduler) {
        mInputs = scheduler.mInputs;
        mState = scheduler.mState;
    }

    public void pause() {
        synchronized (this) {
            mState = STATE_PAUSE;
        }
    }

    protected boolean isRunning() {
        return mState == STATE_RUNNING;
    }

    protected Runnable onPlay(final Player<T, ?> player) {
        return new Runnable() {
            private int mIndex = mInputs.size() - 1;
            @Override
            public void run() {
                player.call((CopyOnWriteArrayList<T>) mInputs.get(mIndex));
            }
        };
    }

    protected abstract void onSchedule(Runnable runnable);

    //This method is not thread-safe! But we always call this in a single thread.
    public final <R> Scheduler<R> scheduleRunnable(List<? extends Runnable> runnables) {
        synchronized (this) {
            if (isRunning()) {
                int size = mInputs.size();
                for (Runnable runnable : runnables) {
                    onSchedule(new ScheduledRunnable<T>(this, runnable, size));
                }
            }
            return (Scheduler<R>) this;
        }
    }

    public final <R> Scheduler<R> scheduleFunction(List<? extends Function1<CopyOnWriteArrayList<T>, CopyOnWriteArrayList<R>>> functions) {
        synchronized (this) {
            if (isRunning()) {
                int index = block(functions.size());
                for (Function1<CopyOnWriteArrayList<T>, CopyOnWriteArrayList<R>> function : functions) {
                    onSchedule(new ScheduledRunnable<T>(this, new BlockingRunnable<T, R>(this, function, index), index));
                }
            }
            return (Scheduler<R>) this;
        }
    }

    //This method is not thread-safe! But we always call this in a single thread.
    public final void play(Player<T, ?> player) {
        synchronized (this) {
            if (isRunning()) {
                scheduleRunnable(Collections.singletonList(onPlay(player)));
            }
        }
    }

    private int block(int functionNumber) {
        return mInputs.append(functionNumber) - 1;
    }

    public void unblock(int index, CopyOnWriteArrayList<Object> object) {
        mInputs.set(index, object);
    }

    public final CopyOnWriteArrayList<Object> waitForFinishing() {
        int index = mInputs.size() - 1;
        try {
            mInputs.lock(index);
            while (!mInputs.inputSet(index)) {
                mInputs.await(index);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mInputs.unlock(index);
        }
        return mInputs.get(index);
    }

    public CopyOnWriteArrayList<Object> getInput(int index) {
        return mInputs.get(index);
    }

    public SchedulerInputs getInputs() {
        return mInputs;
    }

}
