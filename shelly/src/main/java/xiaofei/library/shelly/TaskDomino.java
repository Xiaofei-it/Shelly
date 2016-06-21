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
import xiaofei.library.shelly.function.Function1;
import xiaofei.library.shelly.function.TargetAction0;
import xiaofei.library.shelly.internal.Player;
import xiaofei.library.shelly.scheduler.Scheduler;

/**
 * Created by Xiaofei on 16/6/21.
 */
public class TaskDomino<T, R, U> extends Domino<T, Pair<R, U>> {

    TaskDomino(Domino<T, Pair<R, U>> domino) {
        super(domino.getLabel(), domino.getPlayer());
    }

    public xiaofei.library.shelly.TaskDomino<T, R, U> onSuccess(final Domino<R, ?> domino) {
        return new xiaofei.library.shelly.TaskDomino<T, R, U>(
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

    public xiaofei.library.shelly.TaskDomino<T, R, U> onFailure(final Domino<U, ?> domino) {
        return new xiaofei.library.shelly.TaskDomino<T, R, U>(
                new Domino<T, Pair<R, U>>(getLabel(),
                        new Player<T, Pair<R, U>>() {
                            @Override
                            public Scheduler<Pair<R, U>> play(List<T> input) {
                                final Scheduler<Pair<R, U>> scheduler = getPlayer().play(input);
                                scheduler.play(new Player<Pair<R, U>, Pair<R, U>>() {
                                    @Override
                                    public Scheduler<Pair<R, U>> play(List<Pair<R, U>> input) {
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

    public xiaofei.library.shelly.TaskDomino<T, R, U> finallyDo(Action0 action0) {
        return new xiaofei.library.shelly.TaskDomino<T, R, U>(target(action0));
    }

    public <S> xiaofei.library.shelly.TaskDomino<T, R, U> finallyDo(Class<S> clazz, TargetAction0<S> targetAction0) {
        return new xiaofei.library.shelly.TaskDomino<T, R, U>(target(clazz, targetAction0));
    }

    private Domino<T, Pair<R, U>> toDomino() {
        return new Domino<T, Pair<R, U>>(getLabel(), getPlayer());
    }

    public Domino<T, T> endTask() {
        return ((Domino<T, T>) toDomino()).clear();
    }

    public <S> Domino<T, S> endTask(Class<S> clazz) {
        return toDomino().clear();
    }

    public <S> Domino<T, S> endTask(Function1<List<Pair<R, U>>, S> reducer) {
        return toDomino().reduce(reducer);
    }

}
