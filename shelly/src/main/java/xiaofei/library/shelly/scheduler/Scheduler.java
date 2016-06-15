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

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import xiaofei.library.shelly.internal.Player;

/**
 * Created by Xiaofei on 16/5/31.
 */
public abstract class Scheduler {

    private final CopyOnWriteArrayList<InputWrapper> mInputs;

    private final Lock mLock = new ReentrantLock();

    private final Condition mCondition = mLock.newCondition();

    public Scheduler(Object input) {
        mInputs = new CopyOnWriteArrayList<InputWrapper>();
        mInputs.add(new InputWrapper(input));
    }

    public Scheduler(Scheduler scheduler) {
        mInputs = scheduler.mInputs;
    }

    protected Runnable onPlay(final Player player) {
        return new Runnable() {
            private int mIndex = mInputs.size() - 1;
            @Override
            public void run() {
                player.play(mInputs.get(mIndex).getInput());
            }
        };
    }

    protected abstract void onSchedule(Runnable runnable);

    public final void schedule(Runnable runnable, boolean lastIncluded) {
        onSchedule(new ScheduledRunnable(runnable, lastIncluded));
    }

    public final void play(Player player) {
        schedule(onPlay(player), true);
    }

    public final int block() {
        mInputs.add(null);
        return mInputs.size() - 1;
    }

    public final void unblock(int index, Object object) {
        mLock.lock();
        mInputs.set(index, new InputWrapper(object));
        mCondition.signalAll();
        mLock.unlock();
        System.out.println("signal " + Thread.currentThread().getName());
    }

    public Object getInput(int index) {
        return mInputs.get(index).getInput();
    }

    private class ScheduledRunnable implements Runnable {

        private Runnable mRunnable;

        private int mWaiting;

        ScheduledRunnable(Runnable runnable, boolean lastIncluded) {
            mRunnable = runnable;
            if (lastIncluded) {
                mWaiting = mInputs.size();
            } else {
                mWaiting = mInputs.size() - 1;
            }
        }

        @Override
        public void run() {
            mLock.lock();
            try {
                for (int i = 0; i < mWaiting; ++i) {
                    while (mInputs.get(i) == null) {
                        System.out.println(i + " before await " + Thread.currentThread().getName());
                        mCondition.await(1000, TimeUnit.MILLISECONDS);
                        System.out.println(i + " after await " + Thread.currentThread().getName());
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mLock.unlock();
            }
            mRunnable.run();
        }
    }

    private static class InputWrapper {
        private final Object mInput;

        InputWrapper(Object input) {
            mInput = input;
        }

        Object getInput() {
            return mInput;
        }
    }
}
