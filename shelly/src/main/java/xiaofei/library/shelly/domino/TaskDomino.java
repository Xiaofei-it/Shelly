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

package xiaofei.library.shelly.domino;

import java.util.ArrayList;
import java.util.List;

import xiaofei.library.shelly.Shelly;
import xiaofei.library.shelly.function.Action0;
import xiaofei.library.shelly.function.Action1;
import xiaofei.library.shelly.function.Function1;
import xiaofei.library.shelly.function.TargetAction0;
import xiaofei.library.shelly.function.TargetAction1;
import xiaofei.library.shelly.tuple.Triple;
import xiaofei.library.shelly.util.Player;

/**
 * Created by Xiaofei on 16/6/21.
 */
public class TaskDomino<T, R, U> extends Domino<T, Triple<Boolean, R, U>> {

    protected TaskDomino(Object label, Player<T, Triple<Boolean, R, U>> player) {
        super(label, player);
    }

    private TaskDomino(Domino<T, Triple<Boolean, R, U>> domino) {
        this(domino.getLabel(), domino.getPlayer());
    }

    public TaskDomino<T, R, U> onSuccess(final Domino<R, ?> domino) {
        return new TaskDomino<T, R, U>(
                target(Shelly.<Triple<Boolean, R, U>>createDomino()
                        .reduce(new Function1<List<Triple<Boolean, R, U>>, List<R>>() {
                            @Override
                            public List<R> call(List<Triple<Boolean, R, U>> input) {
                                List<R> result= new ArrayList<R>();
                                for (Triple<Boolean, R, U> triple : input) {
                                    if (triple.first) {
                                        result.add(triple.second);
                                    }
                                }
                                return result;
                            }
                        })
                        .flatMap(new Function1<List<R>, List<R>>() {
                            @Override
                            public List<R> call(List<R> input) {
                                return input;
                            }
                        })
                        .target(domino)
                ));
    }

    public <S> TaskDomino<T, R, U> onSuccess(final Class<? extends S> clazz, final TargetAction0<? super S> targetAction0) {
        return new TaskDomino<T, R, U>(
                reduce(new Function1<List<Triple<Boolean, R, U>>, List<Triple<Boolean, R, U>>>() {
                    @Override
                    public List<Triple<Boolean, R, U>> call(List<Triple<Boolean, R, U>> input) {
                        return input;
                    }
                }).target(clazz, new TargetAction1<S, List<Triple<Boolean, R, U>>>() {
                    @Override
                    public void call(S s, List<Triple<Boolean, R, U>> input) {
                        boolean success = false;
                        for (Triple<Boolean, R, U> triple : input) {
                            if (triple.first) {
                                success = true;
                                break;
                            }
                        }
                        if (success) {
                            targetAction0.call(s);
                        }
                    }
                }).flatMap(new Function1<List<Triple<Boolean, R, U>>, List<Triple<Boolean, R, U>>>() {
                    @Override
                    public List<Triple<Boolean, R, U>> call(List<Triple<Boolean, R, U>> input) {
                        return input;
                    }
                }));
    }

    public <S> TaskDomino<T, R, U> onSuccess(final Class<? extends S> clazz, final TargetAction1<? super S, ? super R> targetAction1) {
        return new TaskDomino<T, R, U>(target(clazz, new TargetAction1<S, Triple<Boolean, R, U>>() {
            @Override
            public void call(S s, Triple<Boolean, R, U> input) {
                if (input.first) {
                    targetAction1.call(s, input.second);
                }
            }
        }));
    }

    public TaskDomino<T, R, U> onSuccess(final Action0 action0) {
        return new TaskDomino<T, R, U>(
                reduce(new Function1<List<Triple<Boolean, R, U>>, List<Triple<Boolean, R, U>>>() {
                    @Override
                    public List<Triple<Boolean, R, U>> call(List<Triple<Boolean, R, U>> input) {
                        return input;
                    }
                }).target(new Action1<List<Triple<Boolean, R, U>>>() {
                    @Override
                    public void call(List<Triple<Boolean, R, U>> input) {
                        boolean success = false;
                        for (Triple<Boolean, R, U> triple : input) {
                            if (triple.first) {
                                success = true;
                                break;
                            }
                        }
                        if (success) {
                            action0.call();
                        }
                    }
                }).flatMap(new Function1<List<Triple<Boolean, R, U>>, List<Triple<Boolean, R, U>>>() {
                    @Override
                    public List<Triple<Boolean, R, U>> call(List<Triple<Boolean, R, U>> input) {
                        return input;
                    }
                }));
    }

    public TaskDomino<T, R, U> onSuccess(final Action1<? super R> action1) {
        return new TaskDomino<T, R, U>(target(new Action1<Triple<Boolean, R, U>>() {
            @Override
            public void call(Triple<Boolean, R, U> input) {
                if (input.first) {
                    action1.call(input.second);
                }
            }
        }));
    }

    //其实是player的高阶函数
    public TaskDomino<T, R, U> onFailure(final Domino<U, ?> domino) {
        return new TaskDomino<T, R, U>(
                target(Shelly.<Triple<Boolean,R,U>>createDomino()
                        .reduce(new Function1<List<Triple<Boolean,R,U>>, List<U>>() {
                            @Override
                            public List<U> call(List<Triple<Boolean, R, U>> input) {
                                List<U> result= new ArrayList<U>();
                                for (Triple<Boolean, R, U> triple : input) {
                                    if (!triple.first) {
                                        result.add(triple.third);
                                    }
                                }
                                return result;
                            }
                        })
                        .flatMap(new Function1<List<U>, List<U>>() {
                            @Override
                            public List<U> call(List<U> input) {
                                return input;
                            }
                        })
                        .target(domino)
                ));
    }

    public <S> TaskDomino<T, R, U> onFailure(final Class<? extends S> clazz, final TargetAction0<? super S> targetAction0) {
        return new TaskDomino<T, R, U>(
                reduce(new Function1<List<Triple<Boolean, R, U>>, List<Triple<Boolean, R, U>>>() {
                    @Override
                    public List<Triple<Boolean, R, U>> call(List<Triple<Boolean, R, U>> input) {
                        return input;
                    }
                }).target(clazz, new TargetAction1<S, List<Triple<Boolean, R, U>>>() {
                    @Override
                    public void call(S s, List<Triple<Boolean, R, U>> input) {
                        boolean failure = false;
                        for (Triple<Boolean, R, U> triple : input) {
                            if (!triple.first) {
                                failure = true;
                                break;
                            }
                        }
                        if (failure) {
                            targetAction0.call(s);
                        }
                    }
                }).flatMap(new Function1<List<Triple<Boolean, R, U>>, List<Triple<Boolean, R, U>>>() {
                    @Override
                    public List<Triple<Boolean, R, U>> call(List<Triple<Boolean, R, U>> input) {
                        return input;
                    }
                }));
    }

    public <S> TaskDomino<T, R, U> onFailure(final Class<? extends S> clazz, final TargetAction1<? super S, ? super U> targetAction1) {
        return new TaskDomino<T, R, U>(target(clazz, new TargetAction1<S, Triple<Boolean, R, U>>() {
            @Override
            public void call(S s, Triple<Boolean, R, U> input) {
                if (!input.first) {
                    targetAction1.call(s, input.third);
                }
            }
        }));
    }

    public TaskDomino<T, R, U> onFailure(final Action0 action0) {
        return new TaskDomino<T, R, U>(
                reduce(new Function1<List<Triple<Boolean, R, U>>, List<Triple<Boolean, R, U>>>() {
                    @Override
                    public List<Triple<Boolean, R, U>> call(List<Triple<Boolean, R, U>> input) {
                        return input;
                    }
                }).target(new Action1<List<Triple<Boolean, R, U>>>() {
                    @Override
                    public void call(List<Triple<Boolean, R, U>> input) {
                        boolean failure = false;
                        for (Triple<Boolean, R, U> triple : input) {
                            if (!triple.first) {
                                failure = true;
                                break;
                            }
                        }
                        if (failure) {
                            action0.call();
                        }
                    }
                }).flatMap(new Function1<List<Triple<Boolean, R, U>>, List<Triple<Boolean, R, U>>>() {
                    @Override
                    public List<Triple<Boolean, R, U>> call(List<Triple<Boolean, R, U>> input) {
                        return input;
                    }
                }));
    }

    public TaskDomino<T, R, U> onFailure(final Action1<? super U> action1) {
        return new TaskDomino<T, R, U>(target(new Action1<Triple<Boolean, R, U>>() {
            @Override
            public void call(Triple<Boolean, R, U> input) {
                if (!input.first) {
                    action1.call(input.third);
                }
            }
        }));
    }

    public TaskDomino<T, R, U> finallyDo(Action0 action0) {
        return new TaskDomino<T, R, U>(target(action0));
    }

    public <S> TaskDomino<T, R, U> finallyDo(Class<? extends S> clazz, TargetAction0<? super S> targetAction0) {
        return new TaskDomino<T, R, U>(target(clazz, targetAction0));
    }

    private Domino<T, Triple<Boolean, R, U>> toDomino() {
        return new Domino<T, Triple<Boolean, R, U>>(getLabel(), getPlayer());
    }

    public Domino<T, R> endTask() {
        return endTask(new Function1<List<Triple<Boolean, R, U>>, List<R>>() {
            @Override
            public List<R> call(List<Triple<Boolean, R, U>> input) {
                List<R> result = new ArrayList<R>();
                for (Triple<Boolean, R, U> triple : input) {
                    if (triple.first) {
                        result.add(triple.second);
                    }
                }
                return result;
            }
        });
    }

    public <S> Domino<T, S> endTaskEmpty() {
        return toDomino().clear();
    }

    public <S> Domino<T, S> endTask(Function1<List<Triple<Boolean, R, U>>, List<S>> reducer) {
        return toDomino().reduce(reducer).flatMap(new Function1<List<S>, List<S>>() {
            @Override
            public List<S> call(List<S> input) {
                return input;
            }
        });
    }

    @Override
    public TaskDomino<T, R, U> background() {
        return new TaskDomino<T, R, U>(super.background());
    }

    /**
     * For unit test only.
     */
    @Override
    TaskDomino<T, R, U> newThread() {
        return new TaskDomino<T, R, U>(super.newThread());
    }

    /**
     * For unit test only.
     */
    @Override
    TaskDomino<T, R, U> defaultScheduler() {
        return new TaskDomino<T, R, U>(super.defaultScheduler());
    }

    @Override
    public TaskDomino<T, R, U> uiThread() {
        return new TaskDomino<T, R, U>(super.uiThread());
    }

    @Override
    public TaskDomino<T, R, U> backgroundQueue() {
        return new TaskDomino<T, R, U>(super.backgroundQueue());
    }

}
