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

package xiaofei.library.shelly;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import xiaofei.library.shelly.function.Action0;
import xiaofei.library.shelly.function.Action1;
import xiaofei.library.shelly.function.Function1;
import xiaofei.library.shelly.function.TargetAction0;
import xiaofei.library.shelly.function.TargetAction1;
import xiaofei.library.shelly.internal.DominoCenter;
import xiaofei.library.shelly.internal.Player;
import xiaofei.library.shelly.internal.TargetCenter;
import xiaofei.library.shelly.scheduler.BackgroundQueueScheduler;
import xiaofei.library.shelly.scheduler.BackgroundScheduler;
import xiaofei.library.shelly.scheduler.DefaultScheduler;
import xiaofei.library.shelly.scheduler.NewThreadScheduler;
import xiaofei.library.shelly.scheduler.Scheduler;
import xiaofei.library.shelly.scheduler.UiThreadScheduler;

/**
 * Created by Xiaofei on 16/5/26.
 */
public class Domino {

    private static final DominoCenter DOMINO_CENTER = DominoCenter.getInstance();

    private static final TargetCenter TARGET_CENTER = TargetCenter.getInstance();

    private Player mPlayer;

    private Object mLabel;

    public Domino(Object label) {
        this(label, new Player() {
            @Override
            public Scheduler play(List<Object> input) {
                return new DefaultScheduler(input);
            }
        });
    }

    private Domino(Object label, Player player) {
        mLabel = label;
        mPlayer = player;
    }

    public Object getLabel() {
        return mLabel;
    }

    public Domino target(final Class<?> clazz, final String target) {
        return new Domino(mLabel, new Player() {
            @Override
            public Scheduler play(List<Object> input) {
                final Scheduler scheduler = mPlayer.play(input);
                scheduler.play(new Player() {
                    @Override
                    public Scheduler play(List<Object> input) {
                        TARGET_CENTER.call(clazz, target, input);
                        return scheduler;
                    }
                });
                return scheduler;
            }
        });
    }

    public <T> Domino target(final Class<T> clazz, final TargetAction0<T> targetAction0) {
        return new Domino(mLabel, new Player() {
            @Override
            public Scheduler play(List<Object> input) {
                final Scheduler scheduler = mPlayer.play(input);
                scheduler.play(new Player() {
                    @Override
                    public Scheduler play(List<Object> input) {
                        CopyOnWriteArrayList<Object> objects = TARGET_CENTER.getObjects(clazz);
                        for (Object object : objects) {
                            targetAction0.call(clazz.cast(object));
                        }
                        return scheduler;
                    }
                });
                return scheduler;
            }
        });
    }

    public <T> Domino target(final Class<T> clazz, final TargetAction1<T> targetAction1) {
        return new Domino(mLabel, new Player() {
            @Override
            public Scheduler play(List<Object> input) {
                final Scheduler scheduler = mPlayer.play(input);
                scheduler.play(new Player() {
                    @Override
                    public Scheduler play(List<Object> input) {
                        CopyOnWriteArrayList<Object> objects = TARGET_CENTER.getObjects(clazz);
                        for (Object object : objects) {
                            for (Object singleInput : input) {
                                targetAction1.call(clazz.cast(object), singleInput);
                            }
                        }
                        return scheduler;
                    }
                });
                return scheduler;
            }
        });
    }

    public Domino target(final Action0 action0) {
        return new Domino(mLabel, new Player() {
            @Override
            public Scheduler play(List<Object> input) {
                final Scheduler scheduler = mPlayer.play(input);
                scheduler.play(new Player() {
                    @Override
                    public Scheduler play(List<Object> input) {
                        action0.call();
                        return scheduler;
                    }
                });
                return scheduler;
            }
        });
    }

    public Domino target(final Action1 action1) {
        return new Domino(mLabel, new Player() {
            @Override
            public Scheduler play(List<Object> input) {
                final Scheduler scheduler = mPlayer.play(input);
                scheduler.play(new Player() {
                    @Override
                    public Scheduler play(List<Object> input) {
                        for (Object singleInput : input) {
                            action1.call(singleInput);
                        }
                        return scheduler;
                    }
                });
                return scheduler;
            }
        });
    }

    public Domino background() {
        return new Domino(mLabel, new Player() {
            @Override
            public Scheduler play(List<Object> input) {
                Scheduler scheduler = mPlayer.play(input);
                return new BackgroundScheduler(scheduler);
            }
        });
    }

    /**
     * For unit test only.
     */
    Domino newThread() {
        return new Domino(mLabel, new Player() {
            @Override
            public Scheduler play(List<Object> input) {
                Scheduler scheduler = mPlayer.play(input);
                return new NewThreadScheduler(scheduler);
            }
        });
    }

    /**
     * For unit test only.
     */
    Domino defaultScheduler() {
        return new Domino(mLabel, new Player() {
            @Override
            public Scheduler play(List<Object> input) {
                Scheduler scheduler = mPlayer.play(input);
                return new DefaultScheduler(scheduler);
            }
        });
    }

    public Domino uiThread() {
        return new Domino(mLabel, new Player() {
            @Override
            public Scheduler play(List<Object> input) {
                Scheduler scheduler = mPlayer.play(input);
                return new UiThreadScheduler(scheduler);
            }
        });
    }

    public Domino backgroundQueue() {
        return new Domino(mLabel, new Player() {
            @Override
            public Scheduler play(List<Object> input) {
                Scheduler scheduler = mPlayer.play(input);
                return new BackgroundQueueScheduler(scheduler);
            }
        });
    }

    public Domino map(final Function1 function1) {
        return new Domino(mLabel, new Player() {
            @Override
            public Scheduler play(final List<Object> input) {
                final Scheduler scheduler = mPlayer.play(input);
                final int index = scheduler.block();
                scheduler.schedule(new Runnable() {
                    @Override
                    public void run() {
                        CopyOnWriteArrayList<Object> oldInput = scheduler.getInput(index - 1);
                        CopyOnWriteArrayList<Object> newInput = new CopyOnWriteArrayList<Object>();
                        for (Object singleInput : oldInput) {
                            newInput.add(function1.call(singleInput));
                        }
                        scheduler.unblock(index, newInput);
                    }
                }, false);
                return scheduler;
            }
        });
    }

    public void play(CopyOnWriteArrayList<Object> input) {
        mPlayer.play(input);
    }

    public void commit() {
        DOMINO_CENTER.commit(this);
    }

}
