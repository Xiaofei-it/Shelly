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

import xiaofei.library.shelly.function.Action0;
import xiaofei.library.shelly.function.Action1;
import xiaofei.library.shelly.function.Function1;
import xiaofei.library.shelly.function.TargetAction;
import xiaofei.library.shelly.internal.DefaultScheduler;
import xiaofei.library.shelly.internal.DominoCenter;
import xiaofei.library.shelly.internal.Player;
import xiaofei.library.shelly.internal.Scheduler;
import xiaofei.library.shelly.internal.TargetCenter;

/**
 * Created by Xiaofei on 16/5/26.
 */
public class Domino {

    private static final DominoCenter DOMINO_CENTER = DominoCenter.getInstance();

    private static final TargetCenter TARGET_CENTER = TargetCenter.getInstance();

    private Player mPlayer;

    private Object mLabel;

    public Domino(Object label) {
        this(label, null);
    }

    private Domino(Object label, Player player) {
        mLabel = label;
        mPlayer = player;
    }

    public Object getLabel() {
        return mLabel;
    }

    public Domino target(final Domino domino) {
        return new Domino(mLabel, new Player() {
            @Override
            public Scheduler play(Object input) {
                domino.play(input);
                return new DefaultScheduler();
            }
        });
    }

    public Domino target(final Class<?> clazz, final String target) {
        return new Domino(mLabel, new Player() {
            @Override
            public Scheduler play(Object input) {
                TARGET_CENTER.call(clazz, target, input);
                return new DefaultScheduler();
            }
        });
    }

    public <T> Domino target(final Class<T> clazz, final TargetAction<T> targetAction) {
        return new Domino(mLabel, new Player() {
            @Override
            public Scheduler play(Object input) {
                List<Object> objects = TARGET_CENTER.getObjects(clazz);
                for (Object object : objects) {
                    targetAction.call((T) object, input);
                }
                return new DefaultScheduler();
            }
        });
    }

    public Domino target(final Action0 action0) {
        return new Domino(mLabel, new Player() {
            @Override
            public Scheduler play(Object input) {
                action0.call();
                return new DefaultScheduler();
            }
        });
    }

    public Domino target(final Action1 action1) {
        return new Domino(mLabel, new Player() {
            @Override
            public Scheduler play(Object input) {
                action1.call(input);
                return new DefaultScheduler();
            }
        });
    }

    public Domino then(final Domino domino) {
        return new Domino(mLabel, new Player() {
            @Override
            public Scheduler play(Object input) {
                final Scheduler scheduler = mPlayer.play(input);
                scheduler.play(new Player() {
                    @Override
                    public Scheduler play(Object input) {
                        domino.play(input);
                        return scheduler;
                    }
                }, input);
                return scheduler;
            }
        });
    }

    public Domino map(final Function1 function1) {
        return new Domino(mLabel, new Player() {
            @Override
            public Scheduler play(Object input) {
                final Scheduler scheduler = mPlayer.play(input);
                return new Scheduler() {
                    @Override
                    public void play(Player player, Object input) {
                        scheduler.play(player, function1.call(input));
                        //player.play(function1.call(input));
                    }
                };
            }
        });
    }
    public Domino then(final Class<?> clazz, final String target) {
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
                }, input);
                return scheduler;
            }
        });
    }

    public <T> Domino then(final Class<T> clazz, final TargetAction<T> targetAction) {
        return new Domino(mLabel, new Player() {
            @Override
            public Scheduler play(Object input) {
                final Scheduler scheduler = mPlayer.play(input);
                scheduler.play(new Player() {
                    @Override
                    public Scheduler play(Object input) {
                        List<Object> objects = TARGET_CENTER.getObjects(clazz);
                        for (Object object : objects) {
                            targetAction.call((T) object, input);
                        }
                        return scheduler;
                    }
                }, input);
                return scheduler;
            }
        });
    }

    public Domino then(final Action0 action0) {
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
                }, input);
                return scheduler;
            }
        });
    }

    public Domino then(final Action1 action1) {
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
                }, input);
                return scheduler;
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
