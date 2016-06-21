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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import xiaofei.library.shelly.function.Action0;
import xiaofei.library.shelly.function.Action1;
import xiaofei.library.shelly.function.Function1;
import xiaofei.library.shelly.function.Function2;
import xiaofei.library.shelly.function.TargetAction0;
import xiaofei.library.shelly.function.TargetAction1;
import xiaofei.library.shelly.internal.DominoCenter;
import xiaofei.library.shelly.internal.Player;
import xiaofei.library.shelly.internal.TargetCenter;
import xiaofei.library.shelly.internal.Task;
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

    Domino(Object label) {
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

    protected Player<T, R> getPlayer() {
        return mPlayer;
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

    public <U> Domino<T, R> target(final Domino<R, U> domino) {
        return new Domino<T, R>(mLabel, new Player<T, R>() {
            @Override
            public Scheduler<R> play(List<T> input) {
                final Scheduler<R> scheduler = mPlayer.play(input);
                scheduler.play(new Player<R, R>() {
                    @Override
                    public Scheduler<R> play(List<R> input) {
                        domino.mPlayer.play(input);
                        return scheduler;
                    }
                });
                return scheduler;
            }
        });
    }

    public <U> Domino<T, U> dominoMap(final Domino<R, U> domino) {
        return merge((Domino<R, U>[]) new Domino[]{domino});
    }

    public <U> Domino<T, U> merge(Domino<R, U> domino1, Domino<R, U> domino2) {
        return merge((Domino<R, U>[]) new Domino[]{domino1, domino2});
    }

    public <U> Domino<T, U> merge(Domino<R, U> domino1, Domino<R, U> domino2, Domino<R, U> domino3) {
        return merge((Domino<R, U>[]) new Domino[]{domino1, domino2, domino3});
    }

    public <U> Domino<T, U> merge(final Domino<R, U>[] dominoes) {
        return new Domino<T, U>(mLabel, new Player<T, U>() {
            @Override
            public Scheduler<U> play(List<T> input) {
                final Scheduler<R> scheduler = mPlayer.play(input);
                List<Function1<CopyOnWriteArrayList<R>, CopyOnWriteArrayList<U>>> functions =
                        new ArrayList<Function1<CopyOnWriteArrayList<R>, CopyOnWriteArrayList<U>>>();
                for (final Domino<R, U> domino : dominoes) {
                    functions.add(new Function1<CopyOnWriteArrayList<R>, CopyOnWriteArrayList<U>>() {
                        @Override
                        public CopyOnWriteArrayList<U> call(CopyOnWriteArrayList<R> input) {
                            Scheduler<U> scheduler = domino.mPlayer.play(input);
                            return (CopyOnWriteArrayList<U>) scheduler.waitForFinishing();
                        }
                    });
                }
                return scheduler.scheduleFunction(functions);
            }
        });
    }

    //TODO null consideration, what's more? maybe function returns null.

    public <U, S, V> Domino<T, V> combine(Domino<R, U> domino1, Domino<R, S> domino2,
                                          final Function2<U, S, V> combiner) {
        /**
         * 想实现的效果是domino1和domino2分开运行，结果经过combiner结合，得到一堆新的结果
         * 为了实现方便，做如下变化：
         * 1、将domino1与domino2分开运行，返回结果变为一个list，这个单独作为一个结果：domino->reduce->merge
         * 2、将这个结果进行合并：reduce
         * 3、将这个结果展开：flatMap
         */
        return merge(
                domino1.reduce(new Function1<List<U>, Pair<Integer, List<Object>>>() {
                    @Override
                    public Pair<Integer, List<Object>> call(List<U> input) {
                        return new Pair<Integer, List<Object>>(1, (List<Object>) input);
                    }
                }),
                domino2.reduce(new Function1<List<S>, Pair<Integer, List<Object>>>() {
                    @Override
                    public Pair<Integer, List<Object>> call(List<S> input) {
                        return new Pair<Integer, List<Object>>(1, (List<Object>) input);
                    }
                }))
                .reduce(new Function1<List<Pair<Integer, List<Object>>>, List<V>>() {
                    @Override
                    public List<V> call(List<Pair<Integer, List<Object>>> input) {
                        if (input.size() != 2) {
                            throw new IllegalStateException("Unknown error! Please report this to Xiaofei.");
                        }
                        List<V> result = new ArrayList<V>();
                        List<Object> input1, input2;
                        if (input.get(0).first == 1) {
                            input1 = input.get(0).second;
                            input2 = input.get(1).second;
                        } else {
                            input1 = input.get(1).second;
                            input2 = input.get(0).second;
                        }
                        for (Object o1 : input1) {
                            for (Object o2 : input2) {
                                result.add(combiner.call((U) o1, (S) o2));
                            }
                        }
                        return result;
                    }
                })
                .flatMap(new Function1<List<V>, List<V>>() {
                    @Override
                    public List<V> call(List<V> input) {
                        return input;
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

    public <U> Domino<T, U> map(final Function1<R, U> map) {
        return new Domino<T, U>(mLabel, new Player<T, U>() {
            @Override
            public Scheduler<U> play(final List<T> input) {
                final Scheduler<R> scheduler = mPlayer.play(input);
                return scheduler.scheduleFunction(
                        Collections.singletonList(new Function1<CopyOnWriteArrayList<R>, CopyOnWriteArrayList<U>>() {
                            @Override
                            public CopyOnWriteArrayList<U> call(CopyOnWriteArrayList<R> input) {
                                CopyOnWriteArrayList<U> result = new CopyOnWriteArrayList<U>();
                                for (R singleInput : input) {
                                    result.add(map.call(singleInput));
                                }
                                return result;
                            }
                        }));
            }
        });
    }

    public <U> Domino<T, U> flatMap(final Function1<R, List<U>> map) {
        return new Domino<T, U>(mLabel, new Player<T, U>() {
            @Override
            public Scheduler<U> play(final List<T> input) {
                final Scheduler<R> scheduler = mPlayer.play(input);
                return scheduler.scheduleFunction(
                        Collections.singletonList(new Function1<CopyOnWriteArrayList<R>, CopyOnWriteArrayList<U>>() {
                            @Override
                            public CopyOnWriteArrayList<U> call(CopyOnWriteArrayList<R> input) {
                                CopyOnWriteArrayList<U> result = new CopyOnWriteArrayList<U>();
                                for (R singleInput : input) {
                                    result.addAll(map.call(singleInput));
                                }
                                return result;
                            }
                        }));
            }
        });
    }

    public Domino<T, R> filter(final Function1<R, Boolean> filter) {
        return new Domino<T, R>(mLabel, new Player<T, R>() {
            @Override
            public Scheduler<R> play(final List<T> input) {
                final Scheduler<R> scheduler = mPlayer.play(input);
                return scheduler.scheduleFunction(
                        Collections.singletonList(new Function1<CopyOnWriteArrayList<R>, CopyOnWriteArrayList<R>>() {
                            @Override
                            public CopyOnWriteArrayList<R> call(CopyOnWriteArrayList<R> input) {
                                CopyOnWriteArrayList<R> result = new CopyOnWriteArrayList<R>();
                                for (R singleInput : input) {
                                    if (filter.call(singleInput)) {
                                        result.add(singleInput);
                                    }
                                }
                                return result;
                            }
                        }));
            }
        });
    }

    public <U> Domino<T, U> reduce(final Function1<List<R>, U> reducer) {
        return new Domino<T, U>(mLabel, new Player<T, U>() {
            @Override
            public Scheduler<U> play(final List<T> input) {
                final Scheduler<R> scheduler = mPlayer.play(input);
                return scheduler.scheduleFunction(
                        Collections.singletonList(new Function1<CopyOnWriteArrayList<R>, CopyOnWriteArrayList<U>>() {
                            @Override
                            public CopyOnWriteArrayList<U> call(CopyOnWriteArrayList<R> input) {
                                CopyOnWriteArrayList<U> result = new CopyOnWriteArrayList<U>();
                                result.add(reducer.call(input));
                                return result;
                            }
                        }));
            }
        });
    }

    public <U> Domino<T, U> clear() {
        return new Domino<T, U>(mLabel, new Player<T, U>() {
            @Override
            public Scheduler<U> play(final List<T> input) {
                final Scheduler<R> scheduler = mPlayer.play(input);
                return scheduler.scheduleFunction(
                        Collections.singletonList(new Function1<CopyOnWriteArrayList<R>, CopyOnWriteArrayList<U>>() {
                            @Override
                            public CopyOnWriteArrayList<U> call(CopyOnWriteArrayList<R> input) {
                                return new CopyOnWriteArrayList<U>();
                            }
                        }));
            }
        });
    }

    public <U, S> TaskDomino<T, U, S> beginTask(Task<R, U, S> task) {
        return new TaskDomino<T, U, S>(map(new TaskFunction<R, U, S>(task)));
    }

    //TODO task, success, fail, endTask
    public Domino<T, R> throttle(long windowDuration, TimeUnit unit) {
        return null;
    }


    public void play(CopyOnWriteArrayList<T> input) {
        mPlayer.play(input);
    }

    public void commit() {
        DOMINO_CENTER.commit(this);
    }

    private static class TaskFunction<T, R, U> implements Function1<T, Pair<R, U>>, Task.TaskListener<R, U> {

        private Task<T, R, U> mTask;

        private volatile R mResult;

        private volatile U mError;

        private Lock mLock = new ReentrantLock();

        private Condition mCondition = mLock.newCondition();

        TaskFunction(Task<T, R, U> task) {
            task.setListener(this);
            mTask = task;
        }

        @Override
        public void onFailure(U error) {
            mLock.lock();
            mError = error;
            mCondition.signalAll();
            mLock.unlock();
        }

        @Override
        public void onSuccess(R result) {
            mLock.lock();
            mResult = result;
            mCondition.signalAll();
            mLock.unlock();
        }

        @Override
        public Pair<R, U> call(T input) {
            mResult = null;
            mError = null;
            mTask.execute(input);
            try {
                mLock.lock();
                while (mError == null && mResult == null) {
                    mCondition.await();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mLock.unlock();
            }
            return new Pair<R, U>(mResult, mError);
        }

    }

    public static class TaskDomino<T, R, U> extends Domino<T, Pair<R, U>> {

        TaskDomino(Domino<T, Pair<R, U>> domino) {
            super(domino.mLabel, domino.mPlayer);
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
                                            domino.mPlayer.play(newInput);
                                            return scheduler;
                                        }
                                    });
                                    return scheduler;
                                }
                            }
                    ));
        }

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
                                            CopyOnWriteArrayList<U> newInput = new CopyOnWriteArrayList<U>();
                                            for (Pair<R, U> pair : input) {
                                                if (pair.second != null) {
                                                    newInput.add(pair.second);
                                                }
                                            }
                                            domino.mPlayer.play(newInput);
                                            return scheduler;
                                        }
                                    });
                                    return scheduler;
                                }
                            }
                    ));
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
    //TODO map起个名字，lift，super与extend。uithread会阻塞

}
