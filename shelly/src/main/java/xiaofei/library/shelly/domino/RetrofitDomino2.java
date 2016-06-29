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

import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import retrofit2.Response;
import xiaofei.library.shelly.function.Action0;
import xiaofei.library.shelly.function.Action1;
import xiaofei.library.shelly.function.Action2;
import xiaofei.library.shelly.function.Function1;
import xiaofei.library.shelly.function.TargetAction0;
import xiaofei.library.shelly.function.TargetAction1;
import xiaofei.library.shelly.function.TargetAction2;
import xiaofei.library.shelly.scheduler.Scheduler;
import xiaofei.library.shelly.util.Player;
import xiaofei.library.shelly.util.Triple;

/**
 * Created by Xiaofei on 16/6/28.
 */
public class RetrofitDomino2<T, R> extends TaskDomino<T, Pair<T, Response<R>>, Throwable> {

    public RetrofitDomino2(Object label, Player<T, Triple<Boolean, Pair<T, Response<R>>, Throwable>> player) {
        super(label, player);
    }

    private RetrofitDomino2(TaskDomino<T, Pair<T, Response<R>>, Throwable> domino) {
        //can use getPlayer(), but not here!
        this(domino.getLabel(), domino.getPlayer());
    }
    @Override
    public RetrofitDomino2<T, R> uiThread() {
        return new RetrofitDomino2<T, R>(super.uiThread());
    }

    @Override
    public RetrofitDomino2<T, R> background() {
        return new RetrofitDomino2<T, R>(super.background());
    }

    @Override
    public RetrofitDomino2<T, R> backgroundQueue() {
        return new RetrofitDomino2<T, R>(super.backgroundQueue());
    }

    public Domino<T, R> endTaskResult() {
        return endTask().filter(new Function1<Pair<T, Response<R>>, Boolean>() {
            @Override
            public Boolean call(Pair<T, Response<R>> input) {
                return input.second.isSuccessful();
            }
        }).map(new Function1<Pair<T, Response<R>>, R>() {
            @Override
            public R call(Pair<T, Response<R>> input) {
                return input.second.body();
            }
        });
    }

    @Override
    public RetrofitDomino2<T, R> finallyDo(Action0 action0) {
        return new RetrofitDomino2<T, R>(super.finallyDo(action0));
    }

    @Override
    public <S> RetrofitDomino2<T, R> finallyDo(Class<? extends S> clazz, TargetAction0<? super S> targetAction0) {
        return new RetrofitDomino2<T, R>(super.finallyDo(clazz, targetAction0));
    }

    @Override
    public RetrofitDomino2<T, R> onFailure(Action0 action0) {
        return new RetrofitDomino2<T, R>(super.onFailure(action0));
    }

    @Override
    public RetrofitDomino2<T, R> onFailure(Action1<? super Throwable> action1) {
        return new RetrofitDomino2<T, R>(super.onFailure(action1));
    }

    @Override
    public <S> RetrofitDomino2<T, R> onFailure(Class<? extends S> clazz, TargetAction0<? super S> targetAction0) {
        return new RetrofitDomino2<T, R>(super.onFailure(clazz, targetAction0));
    }

    @Override
    public <S> RetrofitDomino2<T, R> onFailure(Class<? extends S> clazz, TargetAction1<? super S, ? super Throwable> targetAction1) {
        return new RetrofitDomino2<T, R>(super.onFailure(clazz, targetAction1));
    }

    @Override
    public RetrofitDomino2<T, R> onFailure(Domino<Throwable, ?> domino) {
        return new RetrofitDomino2<T, R>(super.onFailure(domino));
    }

    @Deprecated
    @Override
    public RetrofitDomino2<T, R> onSuccess(Action0 action0) {
        return new RetrofitDomino2<T, R>(super.onSuccess(action0));
    }

    @Deprecated
    @Override
    public RetrofitDomino2<T, R> onSuccess(Action1<? super Pair<T, Response<R>>> action1) {
        return new RetrofitDomino2<T, R>(super.onSuccess(action1));
    }

    @Deprecated
    @Override
    public <S> RetrofitDomino2<T, R> onSuccess(Class<? extends S> clazz, TargetAction0<? super S> targetAction0) {
        return new RetrofitDomino2<T, R>(super.onSuccess(clazz, targetAction0));
    }

    @Override
    public <S> RetrofitDomino2<T, R> onSuccess(Class<? extends S> clazz, TargetAction1<? super S, ? super Pair<T, Response<R>>> targetAction1) {
        return new RetrofitDomino2<T, R>(super.onSuccess(clazz, targetAction1));
    }

    @Deprecated
    @Override
    public RetrofitDomino2<T, R> onSuccess(Domino<Pair<T, Response<R>>, ?> domino) {
        return new RetrofitDomino2<T, R>(super.onSuccess(domino));
    }

    private static <T, R> boolean responseSuccess(Triple<Boolean, Pair<T, Response<R>>, Throwable> triple) {
        return triple.first && triple.second.second.isSuccessful() && triple.second.second.body() != null;
    }

    public RetrofitDomino2<T, R> onResult(final Action0 action0) {
        return new RetrofitDomino2<T, R>(getLabel(),
                new Player<T, Triple<Boolean, Pair<T, Response<R>>, Throwable>>() {
                    @Override
                    public Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> call(List<T> input) {
                        final Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> scheduler = getPlayer().call(input);
                        scheduler.play(new Player<Triple<Boolean, Pair<T, Response<R>>, Throwable>, Triple<Boolean, Pair<T, Response<R>>, Throwable>>() {
                            @Override
                            public Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> call(List<Triple<Boolean, Pair<T, Response<R>>, Throwable>> input) {
                                boolean hasResult = false;
                                for (Triple<Boolean, Pair<T, Response<R>>, Throwable> triple : input) {
                                    if (responseSuccess(triple)) {
                                        hasResult = true;
                                        break;
                                    }
                                }
                                if (hasResult) {
                                    action0.call();
                                }
                                return scheduler;
                            }
                        });
                        return scheduler;
                    }
                });
    }

    public RetrofitDomino2<T, R> onResult(final Action2<T, R> action2) {
        return new RetrofitDomino2<T, R>(getLabel(),
                new Player<T, Triple<Boolean, Pair<T, Response<R>>, Throwable>>() {
                    @Override
                    public Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> call(List<T> input) {
                        final Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> scheduler = getPlayer().call(input);
                        scheduler.play(new Player<Triple<Boolean, Pair<T, Response<R>>, Throwable>, Triple<Boolean, Pair<T, Response<R>>, Throwable>>() {
                            @Override
                            public Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> call(List<Triple<Boolean, Pair<T, Response<R>>, Throwable>> input) {
                                for (Triple<Boolean, Pair<T, Response<R>>, Throwable> triple : input) {
                                    if (responseSuccess(triple)) {
                                        action2.call(triple.second.first, triple.second.second.body());
                                    }
                                }
                                return scheduler;
                            }
                        });
                        return scheduler;
                    }
                });
    }

    public <S> RetrofitDomino2<T, R> onResult(final Class<? extends S> clazz, final TargetAction0<? super S> targetAction0) {
        return new RetrofitDomino2<T, R>(getLabel(),
                new Player<T, Triple<Boolean, Pair<T, Response<R>>, Throwable>>() {
                    @Override
                    public Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> call(List<T> input) {
                        final Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> scheduler = getPlayer().call(input);
                        scheduler.play(new Player<Triple<Boolean, Pair<T, Response<R>>, Throwable>, Triple<Boolean, Pair<T, Response<R>>, Throwable>>() {
                            @Override
                            public Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> call(List<Triple<Boolean, Pair<T, Response<R>>, Throwable>> input) {
                                boolean hasResult = false;
                                for (Triple<Boolean, Pair<T, Response<R>>, Throwable> triple : input) {
                                    if (responseSuccess(triple)) {
                                        hasResult = true;
                                        break;
                                    }
                                }
                                if (hasResult) {
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

    public <S> RetrofitDomino2<T, R> onResult(final Class<? extends S> clazz, final TargetAction2<? super S, T, R> targetAction2) {
        return new RetrofitDomino2<T, R>(getLabel(),
                new Player<T, Triple<Boolean, Pair<T, Response<R>>, Throwable>>() {
                    @Override
                    public Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> call(List<T> input) {
                        final Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> scheduler = getPlayer().call(input);
                        scheduler.play(new Player<Triple<Boolean, Pair<T, Response<R>>, Throwable>, Triple<Boolean, Pair<T, Response<R>>, Throwable>>() {
                            @Override
                            public Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> call(List<Triple<Boolean, Pair<T, Response<R>>, Throwable>> input) {
                                CopyOnWriteArrayList<Object> objects = TARGET_CENTER.getObjects(clazz);
                                for (Object object : objects) {
                                    for (Triple<Boolean, Pair<T, Response<R>>, Throwable> triple : input) {
                                        if (responseSuccess(triple)) {
                                            targetAction2.call(clazz.cast(object), triple.second.first, triple.second.second.body());
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

    public RetrofitDomino2<T, R> onResult(final Domino<Pair<T, Response<R>>, ?> domino) {
        return new RetrofitDomino2<T, R>(getLabel(),
                new Player<T, Triple<Boolean, Pair<T, Response<R>>, Throwable>>() {
                    @Override
                    public Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> call(List<T> input) {
                        final Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> scheduler = getPlayer().call(input);
                        scheduler.play(new Player<Triple<Boolean, Pair<T, Response<R>>, Throwable>, Triple<Boolean, Pair<T, Response<R>>, Throwable>>() {
                            @Override
                            public Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> call(List<Triple<Boolean, Pair<T, Response<R>>, Throwable>> input) {
                                ArrayList<Pair<T, Response<R>>> newInput = new ArrayList<Pair<T, Response<R>>>();
                                for (Triple<Boolean, Pair<T, Response<R>>, Throwable> triple : input) {
                                    if (responseSuccess(triple)) {
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

    private static <T, R> boolean responseFailure(Triple<Boolean, Pair<T, Response<R>>, Throwable> triple) {
        return triple.first && (
                triple.second.second.isSuccessful() && triple.second.second.body() == null ||
                        !triple.second.second.isSuccessful());
    }

    public RetrofitDomino2<T, R> onResponseFailure(final Action0 action0) {
        return new RetrofitDomino2<T, R>(getLabel(),
                new Player<T, Triple<Boolean, Pair<T, Response<R>>, Throwable>>() {
                    @Override
                    public Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> call(List<T> input) {
                        final Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> scheduler = getPlayer().call(input);
                        scheduler.play(new Player<Triple<Boolean, Pair<T, Response<R>>, Throwable>, Triple<Boolean, Pair<T, Response<R>>, Throwable>>() {
                            @Override
                            public Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> call(List<Triple<Boolean, Pair<T, Response<R>>, Throwable>> input) {
                                boolean failure = false;
                                for (Triple<Boolean, Pair<T, Response<R>>, Throwable> triple : input) {
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

    public RetrofitDomino2<T, R> onResponseFailure(final Action1<Response<R>> action1) {
        return new RetrofitDomino2<T, R>(getLabel(),
                new Player<T, Triple<Boolean, Pair<T, Response<R>>, Throwable>>() {
                    @Override
                    public Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> call(List<T> input) {
                        final Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> scheduler = getPlayer().call(input);
                        scheduler.play(new Player<Triple<Boolean, Pair<T, Response<R>>, Throwable>, Triple<Boolean, Pair<T, Response<R>>, Throwable>>() {
                            @Override
                            public Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> call(List<Triple<Boolean, Pair<T, Response<R>>, Throwable>> input) {
                                for (Triple<Boolean, Pair<T, Response<R>>, Throwable> triple : input) {
                                    if (responseFailure(triple)) {
                                        action1.call(triple.second.second);
                                    }
                                }
                                return scheduler;
                            }
                        });
                        return scheduler;
                    }
                });
    }

    public <S> RetrofitDomino2<T, R> onResponseFailure(final Class<? extends S> clazz, final TargetAction0<? super S> targetAction0) {
        return new RetrofitDomino2<T, R>(getLabel(),
                new Player<T, Triple<Boolean, Pair<T, Response<R>>, Throwable>>() {
                    @Override
                    public Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> call(List<T> input) {
                        final Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> scheduler = getPlayer().call(input);
                        scheduler.play(new Player<Triple<Boolean, Pair<T, Response<R>>, Throwable>, Triple<Boolean, Pair<T, Response<R>>, Throwable>>() {
                            @Override
                            public Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> call(List<Triple<Boolean, Pair<T, Response<R>>, Throwable>> input) {
                                boolean failure = false;
                                for (Triple<Boolean, Pair<T, Response<R>>, Throwable> triple : input) {
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

    public <S> RetrofitDomino2<T, R> onResponseFailure(final Class<? extends S> clazz, final TargetAction1<? super S, Response<R>> targetAction1) {
        return new RetrofitDomino2<T, R>(getLabel(),
                new Player<T, Triple<Boolean, Pair<T, Response<R>>, Throwable>>() {
                    @Override
                    public Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> call(List<T> input) {
                        final Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> scheduler = getPlayer().call(input);
                        scheduler.play(new Player<Triple<Boolean, Pair<T, Response<R>>, Throwable>, Triple<Boolean, Pair<T, Response<R>>, Throwable>>() {
                            @Override
                            public Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> call(List<Triple<Boolean, Pair<T, Response<R>>, Throwable>> input) {
                                CopyOnWriteArrayList<Object> objects = TARGET_CENTER.getObjects(clazz);
                                for (Object object : objects) {
                                    for (Triple<Boolean, Pair<T, Response<R>>, Throwable> triple : input) {
                                        if (responseFailure(triple)) {
                                            targetAction1.call(clazz.cast(object), triple.second.second);
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

    public RetrofitDomino2<T, R> onResponseFailure(final Domino<Response<R>, ?> domino) {
        return new RetrofitDomino2<T, R>(getLabel(),
                new Player<T, Triple<Boolean, Pair<T, Response<R>>, Throwable>>() {
                    @Override
                    public Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> call(List<T> input) {
                        final Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> scheduler = getPlayer().call(input);
                        scheduler.play(new Player<Triple<Boolean, Pair<T, Response<R>>, Throwable>, Triple<Boolean, Pair<T, Response<R>>, Throwable>>() {
                            @Override
                            public Scheduler<Triple<Boolean, Pair<T, Response<R>>, Throwable>> call(List<Triple<Boolean, Pair<T, Response<R>>, Throwable>> input) {
                                ArrayList<Response<R>> newInput = new ArrayList<Response<R>>();
                                for (Triple<Boolean, Pair<T, Response<R>>, Throwable> triple : input) {
                                    if (responseFailure(triple)) {
                                        newInput.add(triple.second.second);
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
