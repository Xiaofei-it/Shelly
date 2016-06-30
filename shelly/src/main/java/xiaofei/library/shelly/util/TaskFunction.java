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

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import xiaofei.library.shelly.function.Function1;
import xiaofei.library.shelly.function.Function2;
import xiaofei.library.shelly.task.Task;

/**
 * Created by Xiaofei on 16/6/30.
 */
public class TaskFunction<T, R1, R2, U1, U2> implements Function1<T, Triple<Boolean, R2, U2>>, Task.TaskListener<R1, U1> {

    private Task<T, R1, U1> mTask;

    private Function2<T, R1, R2> mFunc1;

    private Function2<T, U1, U2> mFunc2;

    private T mInput;

    private volatile R2 mResult;

    private volatile U2 mError;

    private volatile int mFlag;

    private Lock mLock = new ReentrantLock();

    private Condition mCondition = mLock.newCondition();

    public TaskFunction(Task<T, R1, U1> task, Function2<T, R1, R2> func1, Function2<T, U1, U2> func2) {
        task.setListener(this);
        mTask = task;
        mFunc1 = func1;
        mFunc2 = func2;
    }

    @Override
    public void onFailure(U1 error) {
        mLock.lock();
        mError = mFunc2.call(mInput, error);
        mFlag = 0;
        mCondition.signalAll();
        mLock.unlock();
    }

    @Override
    public void onSuccess(R1 result) {
        mLock.lock();
        mResult = mFunc1.call(mInput, result);
        mFlag = 1;
        mCondition.signalAll();
        mLock.unlock();
    }

    @Override
    public Triple<Boolean, R2, U2> call(T input) {
        mInput = input;
        mResult = null;
        mError = null;
        mFlag = -1;
        mTask.execute(input);
        try {
            mLock.lock();
            while (mFlag == -1) {
                mCondition.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mLock.unlock();
        }
        return new Triple<Boolean, R2, U2>(mFlag == 1, mResult, mError);
    }

}
