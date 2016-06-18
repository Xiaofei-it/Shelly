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
public class Domino<T, R> {

    private static final DominoCenter DOMINO_CENTER = DominoCenter.getInstance();

    private static final TargetCenter TARGET_CENTER = TargetCenter.getInstance();

    private Player<T, R> mPlayer;

    private Object mLabel;

    public Domino(Object label) {
        this(label, new Player<T, R>() {
            @Override
            public Scheduler<R> play(List<T> input) {
                return (Scheduler<R>) new DefaultScheduler<T>(input);
            }
        });
    }

    private Domino(Object label, Player<T, R> player) {
        mLabel = label;
        mPlayer = player;
    }

    public Object getLabel() {
        return mLabel;
    }

    /**
     * T是原本输入的参数类型，R是将传给下面的参数类型！
     *
     */
    public Domino<T, R> target(final Class<?> clazz, final String target) {
        return new Domino<T, R>(mLabel, new Player<T, R>() {
            @Override
            public Scheduler<R> play(List<T> input) {
                final Scheduler<R> scheduler = mPlayer.play(input);
                scheduler.play(new Player<R, R>() {
                    @Override
                    public Scheduler<R> play(List<R> input) {
                        TARGET_CENTER.call(clazz, target, input);
                        return scheduler;
                    }
                });
                return scheduler;
            }
        });
    }

    public <U> Domino<T, R> target(final Class<U> clazz, final TargetAction0<U> targetAction0) {
        return new Domino<T, R>(mLabel, new Player<T, R>() {
            @Override
            public Scheduler<R> play(List<T> input) {
                final Scheduler<R> scheduler = mPlayer.play(input);
                scheduler.play(new Player<R, R>() {
                    @Override
                    public Scheduler<R> play(List<R> input) {
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

    public <U> Domino<T, R> target(final Class<U> clazz, final TargetAction1<U, R> targetAction1) {
        return new Domino<T, R>(mLabel, new Player<T, R>() {
            @Override
            public Scheduler<R> play(List<T> input) {
                final Scheduler<R> scheduler = mPlayer.play(input);
                scheduler.play(new Player<R, R>() {
                    @Override
                    public Scheduler<R> play(List<R> input) {
                        CopyOnWriteArrayList<Object> objects = TARGET_CENTER.getObjects(clazz);
                        for (Object object : objects) {
                            for (R singleInput : input) {
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

    public Domino<T, R> target(final Action0 action0) {
        return new Domino<T, R>(mLabel, new Player<T, R>() {
            @Override
            public Scheduler<R> play(List<T> input) {
                final Scheduler<R> scheduler = mPlayer.play(input);
                scheduler.play(new Player<R, R>() {
                    @Override
                    public Scheduler<R> play(List<R> input) {
                        action0.call();
                        return scheduler;
                    }
                });
                return scheduler;
            }
        });
    }

    public Domino<T, R> target(final Action1<R> action1) {
        return new Domino<T, R>(mLabel, new Player<T, R>() {
            @Override
            public Scheduler<R> play(List<T> input) {
                final Scheduler<R> scheduler = mPlayer.play(input);
                scheduler.play(new Player<R, R>() {
                    @Override
                    public Scheduler<R> play(List<R> input) {
                        for (R singleInput : input) {
                            action1.call(singleInput);
                        }
                        return scheduler;
                    }
                });
                return scheduler;
            }
        });
    }

    public Domino<T, R> background() {
        return new Domino<T, R>(mLabel, new Player<T, R>() {
            @Override
            public Scheduler<R> play(List<T> input) {
                Scheduler<R> scheduler = mPlayer.play(input);
                return new BackgroundScheduler<R>(scheduler);
            }
        });
    }

    /**
     * For unit test only.
     */
    Domino<T, R> newThread() {
        return new Domino<T, R>(mLabel, new Player<T, R>() {
            @Override
            public Scheduler<R> play(List<T> input) {
                Scheduler<R> scheduler = mPlayer.play(input);
                return new NewThreadScheduler<R>(scheduler);
            }
        });
    }

    /**
     * For unit test only.
     */
    Domino<T, R> defaultScheduler() {
        return new Domino<T, R>(mLabel, new Player<T, R>() {
            @Override
            public Scheduler<R> play(List<T> input) {
                Scheduler<R> scheduler = mPlayer.play(input);
                return new DefaultScheduler<R>(scheduler);
            }
        });
    }

    public Domino<T, R> uiThread() {
        return new Domino<T, R>(mLabel, new Player<T, R>() {
            @Override
            public Scheduler<R> play(List<T> input) {
                Scheduler<R> scheduler = mPlayer.play(input);
                return new UiThreadScheduler<R>(scheduler);
            }
        });
    }

    public Domino<T, R> backgroundQueue() {
        return new Domino<T, R>(mLabel, new Player<T, R>() {
            @Override
            public Scheduler<R> play(List<T> input) {
                Scheduler<R> scheduler = mPlayer.play(input);
                return new BackgroundQueueScheduler<R>(scheduler);
            }
        });
    }

    public <U> Domino<T, U> map(final Function1<R, U> function1) {
        return new Domino<T, U>(mLabel, new Player<T, U>() {
            @Override
            public Scheduler<U> play(final List<T> input) {
                final Scheduler<R> scheduler = mPlayer.play(input);
                final int index = scheduler.block();
                return scheduler.schedule(new Runnable() {
                    @Override
                    public void run() {
                        CopyOnWriteArrayList<Object> oldInput = scheduler.getInput(index - 1);
                        CopyOnWriteArrayList<Object> newInput = new CopyOnWriteArrayList<Object>();
                        for (Object singleInput : oldInput) {
                            newInput.add(function1.call((R) singleInput));
                        }
                        scheduler.unblock(index, newInput);
                    }
                }, false);
            }
        });
    }

    public <U> Domino<T, U> flatMap(final Function1<R, List<U>> function1) {
        return new Domino<T, U>(mLabel, new Player<T, U>() {
            @Override
            public Scheduler<U> play(final List<T> input) {
                final Scheduler<R> scheduler = mPlayer.play(input);
                final int index = scheduler.block();
                return scheduler.schedule(new Runnable() {
                    @Override
                    public void run() {
                        CopyOnWriteArrayList<Object> oldInput = scheduler.getInput(index - 1);
                        CopyOnWriteArrayList<Object> newInput = new CopyOnWriteArrayList<Object>();
                        for (Object singleInput : oldInput) {
                            newInput.addAll(function1.call((R) singleInput));
                        }
                        scheduler.unblock(index, newInput);
                    }
                }, false);
            }
        });
    }

    public Domino<T, R> filter(final Function1<R, Boolean> function1) {
        return new Domino<T, R>(mLabel, new Player<T, R>() {
            @Override
            public Scheduler<R> play(final List<T> input) {
                final Scheduler<R> scheduler = mPlayer.play(input);
                final int index = scheduler.block();
                return scheduler.schedule(new Runnable() {
                    @Override
                    public void run() {
                        CopyOnWriteArrayList<Object> oldInput = scheduler.getInput(index - 1);
                        CopyOnWriteArrayList<Object> newInput = new CopyOnWriteArrayList<Object>();
                        for (Object singleInput : oldInput) {
                            if (function1.call((R) singleInput)) {
                                newInput.add(singleInput);
                            }
                        }
                        scheduler.unblock(index, newInput);
                    }
                }, false);
            }
        });
    }

    //TODO add filterTarget and stash

    public void play(CopyOnWriteArrayList<T> input) {
        mPlayer.play(input);
    }

    public void commit() {
        DOMINO_CENTER.commit(this);
    }

}
