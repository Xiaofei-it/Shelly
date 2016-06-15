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

import xiaofei.library.shelly.internal.Player;

/**
 * Created by Xiaofei on 16/5/31.
 */
public abstract class Scheduler {

    private Object mInput;

    private final CopyOnWriteArrayList<InputWrapper> mBlockedRunnables;

    public Scheduler(Object input, Scheduler scheduler) {
        mInput = input;
        if (scheduler == null) {
            mBlockedRunnables = new CopyOnWriteArrayList<InputWrapper>();
        } else {
            mBlockedRunnables = scheduler.mBlockedRunnables;
        }
    }

    protected Runnable onPlay(final Player player) {
        return new Runnable() {
            private int mIndex;
            {
                if (mBlockedRunnables.isEmpty()) {
                    mIndex = -1;
                } else {
                    mIndex = mBlockedRunnables.size() - 1;
                }
            }
            @Override
            public void run() {
                player.play(mIndex == -1 ? mInput : mBlockedRunnables.get(mIndex).getInput());
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
        mBlockedRunnables.add(null);
        return mBlockedRunnables.size() - 1;
    }

    public final void unblock(int index, Object object) {
        mBlockedRunnables.set(index, new InputWrapper(object));
    }

    public Object getInput(int index) {
        if (index == -1) {
            return mInput;
        } else {
            return mBlockedRunnables.get(index).getInput();
        }
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
                while (mBlockedRunnables.get(i) == null) {
                }
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
