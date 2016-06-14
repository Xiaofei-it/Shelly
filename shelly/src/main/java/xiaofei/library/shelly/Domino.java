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

import xiaofei.library.shelly.action.Action0;
import xiaofei.library.shelly.action.Action1;
import xiaofei.library.shelly.action.TargetAction0;
import xiaofei.library.shelly.action.TargetAction1;
import xiaofei.library.shelly.internal.DominoCenter;
import xiaofei.library.shelly.internal.Player;
import xiaofei.library.shelly.internal.TargetCenter;
import xiaofei.library.shelly.scheduler.CachedThreadScheduler;
import xiaofei.library.shelly.scheduler.DefaultScheduler;
import xiaofei.library.shelly.scheduler.NewThreadScheduler;
import xiaofei.library.shelly.scheduler.Scheduler;
import xiaofei.library.shelly.scheduler.SingleThreadScheduler;
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
            public Scheduler play(Object input) {
                final Object finalInput = input;
                return new DefaultScheduler(null) {
                    @Override
                    protected Object onUpdate(Object input) {
                        return finalInput;
                    }
                };
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
            public Scheduler play(Object input) {
                final Scheduler scheduler = mPlayer.play(input);
                scheduler.play(new Player() {
                    @Override
                    public Scheduler play(Object input) {
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
            public Scheduler play(Object input) {
                final Scheduler scheduler = mPlayer.play(input);
                scheduler.play(new Player() {
                    @Override
                    public Scheduler play(Object input) {
                        List<Object> objects = TARGET_CENTER.getObjects(clazz);
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
            public Scheduler play(Object input) {
                final Scheduler scheduler = mPlayer.play(input);
                scheduler.play(new Player() {
                    @Override
                    public Scheduler play(Object input) {
                        List<Object> objects = TARGET_CENTER.getObjects(clazz);
                        for (Object object : objects) {
                            targetAction1.call(clazz.cast(object), input);
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
            public Scheduler play(Object input) {
                final Scheduler scheduler = mPlayer.play(input);
                scheduler.play(new Player() {
                    @Override
                    public Scheduler play(Object input) {
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
            public Scheduler play(Object input) {
                final Scheduler scheduler = mPlayer.play(input);
                scheduler.play(new Player() {
                    @Override
                    public Scheduler play(Object input) {
                        action1.call(input);
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
            public Scheduler play(final Object finalInput) {
                mPlayer.play(finalInput);
                return new CachedThreadScheduler(finalInput) {
                    @Override
                    protected Object onUpdate(Object input) {
                        return finalInput;
                    }
                };
            }
        });
    }

    /**
     * For unit test only.
     */
    Domino newThread() {
        return new Domino(mLabel, new Player() {
            @Override
            public Scheduler play(final Object finalInput) {
                mPlayer.play(finalInput);
                return new NewThreadScheduler(finalInput) {
                    @Override
                    protected Object onUpdate(Object input) {
                        return finalInput;
                    }
                };
            }
        });
    }

    /**
     * For unit test only.
     */
    Domino defaultScheduler() {
        return new Domino(mLabel, new Player() {
            @Override
            public Scheduler play(final Object finalInput) {
                mPlayer.play(finalInput);
                return new DefaultScheduler(finalInput) {
                    @Override
                    protected Object onUpdate(Object input) {
                        return finalInput;
                    }
                };
            }
        });
    }

    public Domino uiThread() {
        return new Domino(mLabel, new Player() {
            @Override
            public Scheduler play(final Object finalInput) {
                mPlayer.play(finalInput);
                return new UiThreadScheduler(finalInput) {
                    @Override
                    protected Object onUpdate(Object input) {
                        return finalInput;
                    }
                };
            }
        });
    }

    public Domino backgroundQueue() {
        return new Domino(mLabel, new Player() {
            @Override
            public Scheduler play(final Object finalInput) {
                mPlayer.play(finalInput);
                return new SingleThreadScheduler(finalInput) {
                    @Override
                    protected Object onUpdate(Object input) {
                        return finalInput;
                    }
                };
            }
        });
    }

    public void play(Object input) {
        mPlayer.play(input);
    }

    public void commit() {
        DOMINO_CENTER.commit(this);
    }

}
