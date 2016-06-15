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

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import xiaofei.library.shelly.internal.Player;

/**
 * Created by Xiaofei on 16/5/31.
 */
public abstract class Scheduler {

    private Object mInput;

    private final ArrayList<AtomicBoolean> mBlocked = new ArrayList<AtomicBoolean>();

    public Scheduler(Object input) {
        mInput = input;
    }

    protected Runnable onPlay(final Player player) {
        return new Runnable() {
            int last;
            {
                synchronized (mBlocked) {
                    last = mBlocked.size() - 1;
                }
            }
            @Override
            public void run() {
                synchronized (mBlocked) {
                    for (int i = 0; i <= last; ++i) {
                        while (mBlocked.get(i).get()) {
                        }
                    }
                }
                player.play(mInput);
            }
        };
    }

    public abstract void schedule(Runnable runnable);

    public final void play(Player player) {
        schedule(onPlay(player));
    }

    public final int block() {
        synchronized (mBlocked) {
            AtomicBoolean atomicBoolean = new AtomicBoolean(true);
            mBlocked.add(atomicBoolean);
            return mBlocked.size() - 1;
        }
    }

    public final void unblock(int index) {
        synchronized (mBlocked) {
            mBlocked.get(index).set(false);
        }
    }

    public void setInput(Object input) {
        mInput = input;
    }

    public Object getInput() {
        return mInput;
    }

}
