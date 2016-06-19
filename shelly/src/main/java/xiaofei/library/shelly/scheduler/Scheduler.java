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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import xiaofei.library.shelly.function.Function1;
import xiaofei.library.shelly.internal.Player;

/**
 * Created by Xiaofei on 16/5/31.
 */
public abstract class Scheduler<T> {

    private static final boolean DEBUG = true;

    private final Inputs mInputs;

    public Scheduler(List<T> input) {
        mInputs = new Inputs();
        if (input instanceof CopyOnWriteArrayList) {
            mInputs.add((CopyOnWriteArrayList<Object>) input);
        } else {
            mInputs.add(new CopyOnWriteArrayList<Object>(input));
        }
    }

    public <R> Scheduler(Scheduler<R> scheduler) {
        mInputs = scheduler.mInputs;
    }

    protected Runnable onPlay(final Player<T, ?> player) {
        return new Runnable() {
            private int mIndex = mInputs.size() - 1;
            @Override
            public void run() {
                player.play((CopyOnWriteArrayList<T>) mInputs.get(mIndex));
            }
        };
    }

    protected abstract void onSchedule(Runnable runnable);

    //This method is not thread-safe! But we always call this in a single thread.
    public final <R> Scheduler<R> scheduleRunnable(List<? extends Runnable> runnables) {
        synchronized (this) {
            int size = mInputs.size();
            for (Runnable runnable : runnables) {
                onSchedule(new ScheduledRunnable(runnable, size));
            }
            return (Scheduler<R>) this;
        }
    }

    public final <R> Scheduler<R> scheduleFunction(List<? extends Function1<CopyOnWriteArrayList<T>, CopyOnWriteArrayList<R>>> functions) {
        synchronized (this) {
            int index = block(functions.size());
            for (Function1<CopyOnWriteArrayList<T>, CopyOnWriteArrayList<R>> function : functions) {
                onSchedule(new ScheduledRunnable(new BlockingRunnable<R>(function, index), index));
            }
            return (Scheduler<R>) this;
        }
    }

    //This method is not thread-safe! But we always call this in a single thread.
    public final void play(Player<T, ?> player) {
        synchronized (this) {
            scheduleRunnable(Collections.singletonList(onPlay(player)));
        }
    }

    private int block(int functionNumber) {
        return mInputs.append(functionNumber) - 1;
    }

    public final void unblock(int index, CopyOnWriteArrayList<Object> object) {
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

    private class ScheduledRunnable implements Runnable {

        private Runnable mRunnable;

        private int mWaiting;

        ScheduledRunnable(Runnable runnable, int waiting) {
            mRunnable = runnable;
            mWaiting = waiting;
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

    private class BlockingRunnable<R> implements Runnable {

        private int mIndex;

        private CopyOnWriteArrayList<R> mInput;

        private Function1<CopyOnWriteArrayList<T>, CopyOnWriteArrayList<R>> mFunction;

        BlockingRunnable(Function1<CopyOnWriteArrayList<T>, CopyOnWriteArrayList<R>> function, int index) {
            mFunction = function;
            mIndex = index;
        }

        protected final CopyOnWriteArrayList<T> getPreviousInput() {
            return (CopyOnWriteArrayList<T>) getInput(mIndex - 1);
        }

        @Override
        public final void run() {
            mInput = mFunction.call(getPreviousInput());
            if (mInput == null) {
                throw new IllegalStateException();
            }
            unblock(mIndex, (CopyOnWriteArrayList<Object>) mInput);
        }
    }

    private static class Inputs {

        private final CopyOnWriteArrayList<CopyOnWriteArrayList<Object>> mInputs = new CopyOnWriteArrayList<CopyOnWriteArrayList<Object>>();

        private final CopyOnWriteArrayList<ReentrantLock> mLocks = new CopyOnWriteArrayList<ReentrantLock>();

        private final CopyOnWriteArrayList<Condition> mConditions = new CopyOnWriteArrayList<Condition>();

        private final CopyOnWriteArrayList<Integer> mFunctionNumber = new CopyOnWriteArrayList<Integer>();

        private final CopyOnWriteArrayList<AtomicInteger> mFinishedNumber = new CopyOnWriteArrayList<AtomicInteger>();

        Inputs() {}

        int append(int functionNumber) {
            return addInternal(new CopyOnWriteArrayList<Object>(), functionNumber);
        }

        private int addInternal(CopyOnWriteArrayList<Object> input, int functionNumber) {
            synchronized (this) {
                mInputs.add(input);
                ReentrantLock lock = new ReentrantLock();
                mLocks.add(lock);
                mConditions.add(lock.newCondition());
                mFunctionNumber.add(functionNumber);
                mFinishedNumber.add(new AtomicInteger(0));
                return mInputs.size();
            }
        }

        void add(CopyOnWriteArrayList<Object> input) {
            addInternal(input, 0);
        }

        boolean inputSet(int index) {
            return mFinishedNumber.get(index).get() == mFunctionNumber.get(index);
        }

        int size() {
            return mInputs.size();
        }

        CopyOnWriteArrayList<Object> get(int index) {
            return mInputs.get(index);
        }

        void set(int index, CopyOnWriteArrayList<Object> input) {
            lock(index);
            mInputs.get(index).addAll(input);
            if (mFinishedNumber.get(index).incrementAndGet() == mFunctionNumber.get(index)) {
                mConditions.get(index).signalAll();
            }
            if (DEBUG) {
                System.out.println("signal " + Thread.currentThread().getName());
            }
            unlock(index);
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

}
