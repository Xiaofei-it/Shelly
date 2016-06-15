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
import java.util.concurrent.atomic.AtomicBoolean;

import xiaofei.library.shelly.internal.Player;

/**
 * Created by Xiaofei on 16/5/31.
 */
public abstract class Scheduler {

    private Object mInput;

    private final CopyOnWriteArrayList<AtomicBoolean> mBlockedRunnables;

    public Scheduler(Object input) {
        mInput = input;
        mBlockedRunnables = new CopyOnWriteArrayList<AtomicBoolean>();
    }

    protected Runnable onPlay(final Player player) {
        return new Runnable() {
            @Override
            public void run() {
                player.play(mInput);
            }
        };
    }

    protected abstract void onSchedule(Runnable runnable);

    public void schedule(Runnable runnable, boolean lastIncluded) {
        onSchedule(new ScheduledRunnable(runnable, lastIncluded));
    }

    public final void play(Player player) {
        schedule(onPlay(player), true);
    }

    public final int block() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(true);
        mBlockedRunnables.add(atomicBoolean);
        return mBlockedRunnables.size() - 1;
    }

    public final void unblock(int index) {
        mBlockedRunnables.get(index).set(false);
    }

    public void setInput(Object input) {
        mInput = input;
    }

    public Object getInput() {
        return mInput;
    }

    private class ScheduledRunnable implements Runnable {

        private Runnable mRunnable;

        private int mWaiting;

        ScheduledRunnable(Runnable runnable, boolean lastIncluded) {
            mRunnable = runnable;
            if (lastIncluded) {
                mWaiting = mBlockedRunnables.size();
            } else {
                mWaiting = mBlockedRunnables.size() - 1;
            }
        }

        @Override
        public void run() {
            for (int i = 0; i < mWaiting; ++i) {
                while (mBlockedRunnables.get(i).get()) {
                }
            }
            mRunnable.run();
        }
    }
}
