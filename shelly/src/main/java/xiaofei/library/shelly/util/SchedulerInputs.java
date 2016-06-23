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

package xiaofei.library.shelly.util;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Xiaofei on 16/6/23.
 */
public class SchedulerInputs {

    private final CopyOnWriteArrayList<CopyOnWriteArrayList<Object>> mInputs = new CopyOnWriteArrayList<CopyOnWriteArrayList<Object>>();

    private final CopyOnWriteArrayList<ReentrantLock> mLocks = new CopyOnWriteArrayList<ReentrantLock>();

    private final CopyOnWriteArrayList<Condition> mConditions = new CopyOnWriteArrayList<Condition>();

    private final CopyOnWriteArrayList<Integer> mFunctionNumber = new CopyOnWriteArrayList<Integer>();

    private final CopyOnWriteArrayList<AtomicInteger> mFinishedNumber = new CopyOnWriteArrayList<AtomicInteger>();

    public SchedulerInputs() {
    }

    public int append(int functionNumber) {
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

    public void add(CopyOnWriteArrayList<Object> input) {
        addInternal(input, 0);
    }

    public boolean inputSet(int index) {
        return mFinishedNumber.get(index).get() == mFunctionNumber.get(index);
    }

    public int size() {
        return mInputs.size();
    }

    public CopyOnWriteArrayList<Object> get(int index) {
        return mInputs.get(index);
    }

    public void set(int index, CopyOnWriteArrayList<Object> input) {
        lock(index);
        mInputs.get(index).addAll(input);
        if (mFinishedNumber.get(index).incrementAndGet() == mFunctionNumber.get(index)) {
            mConditions.get(index).signalAll();
        }
        if (Config.DEBUG) {
            System.out.println("signal " + Thread.currentThread().getName());
        }
        unlock(index);
    }

    public void lock(int index) {
        mLocks.get(index).lock();
    }

    public void unlock(int index) {
        mLocks.get(index).unlock();
    }

    public void await(int index) throws InterruptedException {
        mConditions.get(index).await();
    }

}
