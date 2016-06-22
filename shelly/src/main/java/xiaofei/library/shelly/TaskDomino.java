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

import android.support.v4.util.Pair;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import xiaofei.library.shelly.function.Action0;
import xiaofei.library.shelly.function.Action1;
import xiaofei.library.shelly.function.Function1;
import xiaofei.library.shelly.function.TargetAction0;
import xiaofei.library.shelly.function.TargetAction1;
import xiaofei.library.shelly.internal.Player;
import xiaofei.library.shelly.scheduler.Scheduler;

/**
 * Created by Xiaofei on 16/6/21.
 */
public class TaskDomino<T, R, U> extends Domino<T, Pair<R, U>> {

    TaskDomino(Domino<T, Pair<R, U>> domino) {
        super(domino.getLabel(), domino.getPlayer());
    }

    public TaskDomino<T, R, U> onSuccess(final Domino<R, ?> domino) {
        return new TaskDomino<T, R, U>(
                new Domino<T, Pair<R, U>>(getLabel(),
                        new Player<T, Pair<R, U>>() {
                            @Override
                            public Scheduler<Pair<R, U>> play(List<T> input) {
                                final Scheduler<Pair<R, U>> scheduler = getPlayer().play(input);
                                scheduler.play(new Player<Pair<R, U>, Pair<R, U>>() {
                                    @Override
                                    public Scheduler<Pair<R, U>> play(List<Pair<R, U>> input) {
                                        CopyOnWriteArrayList<R> newInput = new CopyOnWriteArrayList<R>();
                                        for (Pair<R, U> pair : input) {
                                            if (pair.first != null) {
                                                newInput.add(pair.first);
                                            }
                                        }
                                        domino.getPlayer().play(newInput);
                                        return scheduler;
                                    }
                                });
                                return scheduler;
                            }
                        }
                ));
    }

    public <S> TaskDomino<T, R, U> onSuccess(final Class<S> clazz, final TargetAction0<S> targetAction0) {
        return new TaskDomino<T, R, U>(
                new Domino<T, Pair<R, U>>(getLabel(),
                        new Player<T, Pair<R, U>>() {
                            @Override
                            public Scheduler<Pair<R, U>> play(List<T> input) {
                                final Scheduler<Pair<R, U>> scheduler = getPlayer().play(input);
                                scheduler.play(new Player<Pair<R, U>, Pair<R, U>>() {
                                    @Override
                                    public Scheduler<Pair<R, U>> play(List<Pair<R, U>> input) {
                                        CopyOnWriteArrayList<Object> objects = TARGET_CENTER.getObjects(clazz);
                                        for (Object object : objects) {
                                            targetAction0.call(clazz.cast(object));
                                        }
                                        return scheduler;
                                    }
                                });
                                return scheduler;
                            }
                        }
                ));
    }

    public TaskDomino<T, R, U> onSuccess(final Class<U> clazz, final TargetAction1<U, R> targetAction1) {
        return new TaskDomino<T, R, U>(
                new Domino<T, Pair<R, U>>(getLabel(),
                        new Player<T, Pair<R, U>>() {
                            @Override
                            public Scheduler<Pair<R, U>> play(List<T> input) {
                                final Scheduler<Pair<R, U>> scheduler = getPlayer().play(input);
                                scheduler.play(new Player<Pair<R, U>, Pair<R, U>>() {
                                    @Override
                                    public Scheduler<Pair<R, U>> play(List<Pair<R, U>> input) {
                                        CopyOnWriteArrayList<Object> objects = TARGET_CENTER.getObjects(clazz);
                                        for (Object object : objects) {
                                            for (Pair<R, U> singleInput : input) {
                                                targetAction1.call(clazz.cast(object), singleInput.first);
                                            }
                                        }
                                        return scheduler;
                                    }
                                });
                                return scheduler;
                            }
                        }
                ));
    }

    public TaskDomino<T, R, U> onSuccess(final Action0 action0) {
        return new TaskDomino<T, R, U>(
                new Domino<T, Pair<R, U>>(getLabel(),
                        new Player<T, Pair<R, U>>() {
                            @Override
                            public Scheduler<Pair<R, U>> play(List<T> input) {
                                final Scheduler<Pair<R, U>> scheduler = getPlayer().play(input);
                                scheduler.play(new Player<Pair<R, U>, Pair<R, U>>() {
                                    @Override
                                    public Scheduler<Pair<R, U>> play(List<Pair<R, U>> input) {
                                        action0.call();
                                        return scheduler;
                                    }
                                });
                                return scheduler;
                            }
                        }
                ));
    }

    public TaskDomino<T, R, U> onSuccess(final Action1<R> action1) {
        return new TaskDomino<T, R, U>(
                new Domino<T, Pair<R, U>>(getLabel(),
                        new Player<T, Pair<R, U>>() {
                            @Override
                            public Scheduler<Pair<R, U>> play(List<T> input) {
                                final Scheduler<Pair<R, U>> scheduler = getPlayer().play(input);
                                scheduler.play(new Player<Pair<R, U>, Pair<R, U>>() {
                                    @Override
                                    public Scheduler<Pair<R, U>> play(List<Pair<R, U>> input) {
                                        for (Pair<R, U> singleInput : input) {
                                            action1.call(singleInput.first);
                                        }
                                        return scheduler;
                                    }
                                });
                                return scheduler;
                            }
                        }
                ));
    }

    //TODO 其实是player的高阶函数
    public TaskDomino<T, R, U> onFailure(final Domino<U, ?> domino) {
        return new TaskDomino<T, R, U>(
                new Domino<T, Pair<R, U>>(getLabel(),
                        new Player<T, Pair<R, U>>() {
                            @Override
                            public Scheduler<Pair<R, U>> play(List<T> input) {
                                final Scheduler<Pair<R, U>> scheduler = getPlayer().play(input);
                                scheduler.play(new Player<Pair<R, U>, Pair<R, U>>() {
                                    @Override
                                    public Scheduler<Pair<R, U>> play(List<Pair<R, U>> input) {
                                        //TODO 此处不必Copy
                                        CopyOnWriteArrayList<U> newInput = new CopyOnWriteArrayList<U>();
                                        for (Pair<R, U> pair : input) {
                                            if (pair.second != null) {
                                                newInput.add(pair.second);
                                            }
                                        }
                                        domino.getPlayer().play(newInput);
                                        return scheduler;
                                    }
                                });
                                return scheduler;
                            }
                        }
                ));
    }

    public <S> TaskDomino<T, R, U> onFailure(final Class<S> clazz, final TargetAction0<S> targetAction0) {
        return new TaskDomino<T, R, U>(
                new Domino<T, Pair<R, U>>(getLabel(),
                        new Player<T, Pair<R, U>>() {
                            @Override
                            public Scheduler<Pair<R, U>> play(List<T> input) {
                                final Scheduler<Pair<R, U>> scheduler = getPlayer().play(input);
                                scheduler.play(new Player<Pair<R, U>, Pair<R, U>>() {
                                    @Override
                                    public Scheduler<Pair<R, U>> play(List<Pair<R, U>> input) {
                                        CopyOnWriteArrayList<Object> objects = TARGET_CENTER.getObjects(clazz);
                                        for (Object object : objects) {
                                            targetAction0.call(clazz.cast(object));
                                        }
                                        return scheduler;
                                    }
                                });
                                return scheduler;
                            }
                        }
                ));
    }

    public <S> TaskDomino<T, R, U> onFailure(final Class<S> clazz, final TargetAction1<S, U> targetAction1) {
        return new TaskDomino<T, R, U>(
                new Domino<T, Pair<R, U>>(getLabel(),
                        new Player<T, Pair<R, U>>() {
                            @Override
                            public Scheduler<Pair<R, U>> play(List<T> input) {
                                final Scheduler<Pair<R, U>> scheduler = getPlayer().play(input);
                                scheduler.play(new Player<Pair<R, U>, Pair<R, U>>() {
                                    @Override
                                    public Scheduler<Pair<R, U>> play(List<Pair<R, U>> input) {
                                        CopyOnWriteArrayList<Object> objects = TARGET_CENTER.getObjects(clazz);
                                        for (Object object : objects) {
                                            for (Pair<R, U> singleInput : input) {
                                                targetAction1.call(clazz.cast(object), singleInput.second);
                                            }
                                        }
                                        return scheduler;
                                    }
                                });
                                return scheduler;
                            }
                        }
                ));
    }

    public TaskDomino<T, R, U> onFailure(final Action0 action0) {
        return new TaskDomino<T, R, U>(
                new Domino<T, Pair<R, U>>(getLabel(),
                        new Player<T, Pair<R, U>>() {
                            @Override
                            public Scheduler<Pair<R, U>> play(List<T> input) {
                                final Scheduler<Pair<R, U>> scheduler = getPlayer().play(input);
                                scheduler.play(new Player<Pair<R, U>, Pair<R, U>>() {
                                    @Override
                                    public Scheduler<Pair<R, U>> play(List<Pair<R, U>> input) {
                                        action0.call();
                                        return scheduler;
                                    }
                                });
                                return scheduler;
                            }
                        }
                ));
    }

    public TaskDomino<T, R, U> onFailure(final Action1<U> action1) {
        return new TaskDomino<T, R, U>(
                new Domino<T, Pair<R, U>>(getLabel(),
                        new Player<T, Pair<R, U>>() {
                            @Override
                            public Scheduler<Pair<R, U>> play(List<T> input) {
                                final Scheduler<Pair<R, U>> scheduler = getPlayer().play(input);
                                scheduler.play(new Player<Pair<R, U>, Pair<R, U>>() {
                                    @Override
                                    public Scheduler<Pair<R, U>> play(List<Pair<R, U>> input) {
                                        for (Pair<R, U> singleInput : input) {
                                            action1.call(singleInput.second);
                                        }
                                        return scheduler;
                                    }
                                });
                                return scheduler;
                            }
                        }
                ));
    }

    public TaskDomino<T, R, U> finallyDo(Action0 action0) {
        return new TaskDomino<T, R, U>(target(action0));
    }

    public <S> TaskDomino<T, R, U> finallyDo(Class<S> clazz, TargetAction0<S> targetAction0) {
        return new TaskDomino<T, R, U>(target(clazz, targetAction0));
    }

    private Domino<T, Pair<R, U>> toDomino() {
        return new Domino<T, Pair<R, U>>(getLabel(), getPlayer());
    }

    public <S> Domino<T, S> endTask() {
        return toDomino().clear();
    }

    public <S> Domino<T, S> endTask(Function1<List<Pair<R, U>>, S> reducer) {
        return toDomino().reduce(reducer);
    }

}
