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
import java.util.concurrent.CopyOnWriteArrayList;

import retrofit2.Response;
import xiaofei.library.shelly.function.Action0;
import xiaofei.library.shelly.function.Action1;
import xiaofei.library.shelly.function.Function1;
import xiaofei.library.shelly.function.TargetAction0;
import xiaofei.library.shelly.function.TargetAction1;
import xiaofei.library.shelly.scheduler.Scheduler;
import xiaofei.library.shelly.util.Player;
import xiaofei.library.shelly.tuple.Triple;

/**
 * Created by Xiaofei on 16/6/27.
 */
public class RetrofitDomino<T, R> extends TaskDomino<T, Response<R>, Throwable> {

    public RetrofitDomino(Object label, Player<T, Triple<Boolean, Response<R>, Throwable>> player) {
        super(label, player);
    }

    private RetrofitDomino(Domino<T, Triple<Boolean, Response<R>, Throwable>> domino) {
        //can use getPlayer(), but not here!
        this(domino.getLabel(), domino.getPlayer());
    }
    @Override
    public RetrofitDomino<T, R> uiThread() {
        return new RetrofitDomino<T, R>(super.uiThread());
    }

    @Override
    public RetrofitDomino<T, R> background() {
        return new RetrofitDomino<T, R>(super.background());
    }

    @Override
    public RetrofitDomino<T, R> backgroundQueue() {
        return new RetrofitDomino<T, R>(super.backgroundQueue());
    }

    public Domino<T, R> endTaskResult() {
        return endTask().filter(new Function1<Response<R>, Boolean>() {
            @Override
            public Boolean call(Response<R> input) {
                return input.isSuccessful();
            }
        }).map(new Function1<Response<R>, R>() {
            @Override
            public R call(Response<R> input) {
                return input.body();
            }
        });
    }

    @Override
    public RetrofitDomino<T, R> finallyDo(Action0 action0) {
        return new RetrofitDomino<T, R>(super.finallyDo(action0));
    }

    @Override
    public <S> RetrofitDomino<T, R> finallyDo(Class<? extends S> clazz, TargetAction0<? super S> targetAction0) {
        return new RetrofitDomino<T, R>(super.finallyDo(clazz, targetAction0));
    }

    @Override
    public RetrofitDomino<T, R> onFailure(Action0 action0) {
        return new RetrofitDomino<T, R>(super.onFailure(action0));
    }

    @Override
    public RetrofitDomino<T, R> onFailure(Action1<? super Throwable> action1) {
        return new RetrofitDomino<T, R>(super.onFailure(action1));
    }

    @Override
    public <S> RetrofitDomino<T, R> onFailure(Class<? extends S> clazz, TargetAction0<? super S> targetAction0) {
        return new RetrofitDomino<T, R>(super.onFailure(clazz, targetAction0));
    }

    @Override
    public <S> RetrofitDomino<T, R> onFailure(Class<? extends S> clazz, TargetAction1<? super S, ? super Throwable> targetAction1) {
        return new RetrofitDomino<T, R>(super.onFailure(clazz, targetAction1));
    }

    @Override
    public RetrofitDomino<T, R> onFailure(Domino<Throwable, ?> domino) {
        return new RetrofitDomino<T, R>(super.onFailure(domino));
    }

    @Deprecated
    @Override
    public RetrofitDomino<T, R> onSuccess(Action0 action0) {
        return new RetrofitDomino<T, R>(super.onSuccess(action0));
    }

    @Deprecated
    @Override
    public RetrofitDomino<T, R> onSuccess(Action1<? super Response<R>> action1) {
        return new RetrofitDomino<T, R>(super.onSuccess(action1));
    }

    @Deprecated
    @Override
    public <S> RetrofitDomino<T, R> onSuccess(Class<? extends S> clazz, TargetAction0<? super S> targetAction0) {
        return new RetrofitDomino<T, R>(super.onSuccess(clazz, targetAction0));
    }

    @Override
    public <S> RetrofitDomino<T, R> onSuccess(Class<? extends S> clazz, TargetAction1<? super S, ? super Response<R>> targetAction1) {
        return new RetrofitDomino<T, R>(super.onSuccess(clazz, targetAction1));
    }

    @Deprecated
    @Override
    public RetrofitDomino<T, R> onSuccess(Domino<Response<R>, ?> domino) {
        return new RetrofitDomino<T, R>(super.onSuccess(domino));
    }


    private static <R> boolean responseSuccess(Triple<Boolean, Response<R>, Throwable> triple) {
        return triple.first && triple.second.isSuccessful() && triple.second.body() != null;
    }

    public RetrofitDomino<T, R> onSuccessResult(final Action0 action0) {
        return new RetrofitDomino<T, R>(
                reduce(new Function1<List<Triple<Boolean, Response<R>, Throwable>>, List<Triple<Boolean, Response<R>, Throwable>>>() {
                    @Override
                    public List<Triple<Boolean, Response<R>, Throwable>> call(List<Triple<Boolean, Response<R>, Throwable>> input) {
                        return input;
                    }
                }).target(new Action1<List<Triple<Boolean, Response<R>, Throwable>>>() {
                    @Override
                    public void call(List<Triple<Boolean, Response<R>, Throwable>> input) {
                        boolean hasResult = false;
                        for (Triple<Boolean, Response<R>, Throwable> triple : input) {
                            if (responseSuccess(triple)) {
                                hasResult = true;
                                break;
                            }
                        }
                        if (hasResult) {
                            action0.call();
                        }
                    }
                }).flatMap(new Function1<List<Triple<Boolean, Response<R>, Throwable>>, List<Triple<Boolean, Response<R>, Throwable>>>() {
                    @Override
                    public List<Triple<Boolean, Response<R>, Throwable>> call(List<Triple<Boolean, Response<R>, Throwable>> input) {
                        return input;
                    }
                }));
    }

    public RetrofitDomino<T, R> onSuccessResult(final Action1<R> action1) {
        return new RetrofitDomino<T, R>(
                target(new Action1<Triple<Boolean, Response<R>, Throwable>>() {
                    @Override
                    public void call(Triple<Boolean, Response<R>, Throwable> input) {
                        if (responseSuccess(input)) {
                            action1.call(input.second.body());
                        }
                    }
                })
        );
    }

    public <S> RetrofitDomino<T, R> onSuccessResult(final Class<? extends S> clazz, final TargetAction0<? super S> targetAction0) {
        return new RetrofitDomino<T, R>(
                reduce(new Function1<List<Triple<Boolean, Response<R>, Throwable>>, List<Triple<Boolean, Response<R>, Throwable>>>() {
                    @Override
                    public List<Triple<Boolean, Response<R>, Throwable>> call(List<Triple<Boolean, Response<R>, Throwable>> input) {
                        return input;
                    }
                }).target(clazz, new TargetAction1<S, List<Triple<Boolean, Response<R>, Throwable>>>() {
                    @Override
                    public void call(S s, List<Triple<Boolean, Response<R>, Throwable>> input) {
                        boolean success = false;
                        for (Triple<Boolean, Response<R>, Throwable> triple : input) {
                            if (responseSuccess(triple)) {
                                success = true;
                                break;
                            }
                        }
                        if (success) {
                            targetAction0.call(s);
                        }
                    }
                }).flatMap(new Function1<List<Triple<Boolean, Response<R>, Throwable>>, List<Triple<Boolean, Response<R>, Throwable>>>() {
                    @Override
                    public List<Triple<Boolean, Response<R>, Throwable>> call(List<Triple<Boolean, Response<R>, Throwable>> input) {
                        return input;
                    }
                }));
    }

    public <S> RetrofitDomino<T, R> onSuccessResult(final Class<? extends S> clazz, final TargetAction1<? super S, R> targetAction1) {
        return new RetrofitDomino<T, R>(getLabel(),
                new Player<T, Triple<Boolean, Response<R>, Throwable>>() {
                    @Override
                    public Scheduler<Triple<Boolean, Response<R>, Throwable>> call(List<T> input) {
                        final Scheduler<Triple<Boolean, Response<R>, Throwable>> scheduler = getPlayer().call(input);
                        scheduler.play(new Player<Triple<Boolean, Response<R>, Throwable>, Triple<Boolean, Response<R>, Throwable>>() {
                            @Override
                            public Scheduler<Triple<Boolean, Response<R>, Throwable>> call(List<Triple<Boolean, Response<R>, Throwable>> input) {
                                CopyOnWriteArrayList<Object> objects = TARGET_CENTER.getObjects(clazz);
                                for (Object object : objects) {
                                    for (Triple<Boolean, Response<R>, Throwable> triple : input) {
                                        if (responseSuccess(triple)) {
                                            targetAction1.call(clazz.cast(object), triple.second.body());
                                        }
                                    }
                                }
                                return scheduler;
                            }
                        });
                        return scheduler;
                    }
                });
    }

    public RetrofitDomino<T, R> onSuccessResult(final Domino<R, ?> domino) {
        return new RetrofitDomino<T, R>(getLabel(),
                new Player<T, Triple<Boolean, Response<R>, Throwable>>() {
                    @Override
                    public Scheduler<Triple<Boolean, Response<R>, Throwable>> call(List<T> input) {
                        final Scheduler<Triple<Boolean, Response<R>, Throwable>> scheduler = getPlayer().call(input);
                        scheduler.play(new Player<Triple<Boolean, Response<R>, Throwable>, Triple<Boolean, Response<R>, Throwable>>() {
                            @Override
                            public Scheduler<Triple<Boolean, Response<R>, Throwable>> call(List<Triple<Boolean, Response<R>, Throwable>> input) {
                                ArrayList<R> newInput = new ArrayList<R>();
                                for (Triple<Boolean, Response<R>, Throwable> triple : input) {
                                    if (responseSuccess(triple)) {
                                        newInput.add(triple.second.body());
                                    }
                                }
                                if (!newInput.isEmpty()) {
                                    domino.getPlayer().call(newInput);
                                }
                                return scheduler;
                            }
                        });
                        return scheduler;
                    }
                });
    }

    private static <R> boolean responseFailure(Triple<Boolean, Response<R>, Throwable> triple) {
        return triple.first && (
                triple.second.isSuccessful() && triple.second.body() == null ||
                        !triple.second.isSuccessful());
    }

    public RetrofitDomino<T, R> onResponseFailure(final Action0 action0) {
        return new RetrofitDomino<T, R>(getLabel(),
                new Player<T, Triple<Boolean, Response<R>, Throwable>>() {
                    @Override
                    public Scheduler<Triple<Boolean, Response<R>, Throwable>> call(List<T> input) {
                        final Scheduler<Triple<Boolean, Response<R>, Throwable>> scheduler = getPlayer().call(input);
                        scheduler.play(new Player<Triple<Boolean, Response<R>, Throwable>, Triple<Boolean, Response<R>, Throwable>>() {
                            @Override
                            public Scheduler<Triple<Boolean, Response<R>, Throwable>> call(List<Triple<Boolean, Response<R>, Throwable>> input) {
                                boolean failure = false;
                                for (Triple<Boolean, Response<R>, Throwable> triple : input) {
                                    if (responseFailure(triple)) {
                                        failure = true;
                                        break;
                                    }
                                }
                                if (failure) {
                                    action0.call();
                                }
                                return scheduler;
                            }
                        });
                        return scheduler;
                    }
                });
    }

    public RetrofitDomino<T, R> onResponseFailure(final Action1<Response<R>> action1) {
        return new RetrofitDomino<T, R>(getLabel(),
                new Player<T, Triple<Boolean, Response<R>, Throwable>>() {
                    @Override
                    public Scheduler<Triple<Boolean, Response<R>, Throwable>> call(List<T> input) {
                        final Scheduler<Triple<Boolean, Response<R>, Throwable>> scheduler = getPlayer().call(input);
                        scheduler.play(new Player<Triple<Boolean, Response<R>, Throwable>, Triple<Boolean, Response<R>, Throwable>>() {
                            @Override
                            public Scheduler<Triple<Boolean, Response<R>, Throwable>> call(List<Triple<Boolean, Response<R>, Throwable>> input) {
                                for (Triple<Boolean, Response<R>, Throwable> triple : input) {
                                    if (responseFailure(triple)) {
                                        action1.call(triple.second);
                                    }
                                }
                                return scheduler;
                            }
                        });
                        return scheduler;
                    }
                });
    }

    public <S> RetrofitDomino<T, R> onResponseFailure(final Class<? extends S> clazz, final TargetAction0<? super S> targetAction0) {
        return new RetrofitDomino<T, R>(getLabel(),
                new Player<T, Triple<Boolean, Response<R>, Throwable>>() {
                    @Override
                    public Scheduler<Triple<Boolean, Response<R>, Throwable>> call(List<T> input) {
                        final Scheduler<Triple<Boolean, Response<R>, Throwable>> scheduler = getPlayer().call(input);
                        scheduler.play(new Player<Triple<Boolean, Response<R>, Throwable>, Triple<Boolean, Response<R>, Throwable>>() {
                            @Override
                            public Scheduler<Triple<Boolean, Response<R>, Throwable>> call(List<Triple<Boolean, Response<R>, Throwable>> input) {
                                boolean failure = false;
                                for (Triple<Boolean, Response<R>, Throwable> triple : input) {
                                    if (responseFailure(triple)) {
                                        failure = true;
                                        break;
                                    }
                                }
                                if (failure) {
                                    CopyOnWriteArrayList<Object> objects = TARGET_CENTER.getObjects(clazz);
                                    for (Object object : objects) {
                                        targetAction0.call(clazz.cast(object));
                                    }
                                }
                                return scheduler;
                            }
                        });
                        return scheduler;
                    }
                });
    }

    public <S> RetrofitDomino<T, R> onResponseFailure(final Class<? extends S> clazz, final TargetAction1<? super S, Response<R>> targetAction1) {
        return new RetrofitDomino<T, R>(getLabel(),
                new Player<T, Triple<Boolean, Response<R>, Throwable>>() {
                    @Override
                    public Scheduler<Triple<Boolean, Response<R>, Throwable>> call(List<T> input) {
                        final Scheduler<Triple<Boolean, Response<R>, Throwable>> scheduler = getPlayer().call(input);
                        scheduler.play(new Player<Triple<Boolean, Response<R>, Throwable>, Triple<Boolean, Response<R>, Throwable>>() {
                            @Override
                            public Scheduler<Triple<Boolean, Response<R>, Throwable>> call(List<Triple<Boolean, Response<R>, Throwable>> input) {
                                CopyOnWriteArrayList<Object> objects = TARGET_CENTER.getObjects(clazz);
                                for (Object object : objects) {
                                    for (Triple<Boolean, Response<R>, Throwable> triple : input) {
                                        if (responseFailure(triple)) {
                                            targetAction1.call(clazz.cast(object), triple.second);
                                        }
                                    }
                                }
                                return scheduler;
                            }
                        });
                        return scheduler;
                    }
                });
    }

    public RetrofitDomino<T, R> onResponseFailure(final Domino<Response<R>, ?> domino) {
        return new RetrofitDomino<T, R>(getLabel(),
                new Player<T, Triple<Boolean, Response<R>, Throwable>>() {
                    @Override
                    public Scheduler<Triple<Boolean, Response<R>, Throwable>> call(List<T> input) {
                        final Scheduler<Triple<Boolean, Response<R>, Throwable>> scheduler = getPlayer().call(input);
                        scheduler.play(new Player<Triple<Boolean, Response<R>, Throwable>, Triple<Boolean, Response<R>, Throwable>>() {
                            @Override
                            public Scheduler<Triple<Boolean, Response<R>, Throwable>> call(List<Triple<Boolean, Response<R>, Throwable>> input) {
                                ArrayList<Response<R>> newInput = new ArrayList<Response<R>>();
                                for (Triple<Boolean, Response<R>, Throwable> triple : input) {
                                    if (responseFailure(triple)) {
                                        newInput.add(triple.second);
                                    }
                                }
                                if (!newInput.isEmpty()) {
                                    domino.getPlayer().call(newInput);
                                }
                                return scheduler;
                            }
                        });
                        return scheduler;
                    }
                });
    }
}
