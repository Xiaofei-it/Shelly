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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import xiaofei.library.shelly.internal.Player;

/**
 * Created by Xiaofei on 16/5/31.
 */
public abstract class Scheduler {

    private static final boolean DEBUG = true;

    private final Inputs mInputs;

    public Scheduler(Object input) {
        mInputs = new Inputs();
        mInputs.add(input);
    }

    public Scheduler(Scheduler scheduler) {
        mInputs = scheduler.mInputs;
    }

    protected Runnable onPlay(final Player player) {
        return new Runnable() {
            private int mIndex = mInputs.size() - 1;
            @Override
            public void run() {
                player.play(mInputs.get(mIndex));
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
        return mInputs.append() - 1;
    }

    public final void unblock(int index, Object object) {
        mInputs.set(index, object);
    }

    public Object getInput(int index) {
        return mInputs.get(index);
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
            for (int i = 0; i < mWaiting; ++i) {
                try {
                    mInputs.lock(i);
                    while (!mInputs.inputSet(i)) {
                        if (DEBUG) {
                            System.out.println(i + " before await " + Thread.currentThread().getName());
                        }
                        mInputs.await(i);
                        if (DEBUG) {
                            System.out.println(i + " after await " + Thread.currentThread().getName());
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    mInputs.unlock(i);
                }
            }
            mRunnable.run();
        }
    }

    private static class Inputs {

        private final CopyOnWriteArrayList<InputWrapper> mInputs = new CopyOnWriteArrayList<InputWrapper>();

        private final CopyOnWriteArrayList<ReentrantLock> mLocks = new CopyOnWriteArrayList<>();

        private final CopyOnWriteArrayList<Condition> mConditions = new CopyOnWriteArrayList<>();

        Inputs() {}

        int append() {
            return addInternal(null);
        }

        int addInternal(InputWrapper inputWrapper) {
            synchronized (this) {
                mInputs.add(inputWrapper);
                ReentrantLock lock = new ReentrantLock();
                mLocks.add(lock);
                mConditions.add(lock.newCondition());
                return mInputs.size();
            }
        }

        void add(Object input) {
            addInternal(new InputWrapper(input));
        }

        boolean inputSet(int index) {
            return mInputs.get(index) != null;
        }
        int size() {
            return mInputs.size();
        }

        Object get(int index) {
            return mInputs.get(index).getInput();
        }

        void set(int index, Object input) {
            mLocks.get(index).lock();
            mInputs.set(index, new InputWrapper(input));
            mConditions.get(index).signalAll();
            if (DEBUG) {
                System.out.println("signal " + Thread.currentThread().getName());
            }
            mLocks.get(index).unlock();
        }

        void lock(int index) {
            mLocks.get(index).lock();
        }

        void unlock(int index) {
            mLocks.get(index).unlock();
        }

        void await(int index) throws InterruptedException {
            mConditions.get(index).await();
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
