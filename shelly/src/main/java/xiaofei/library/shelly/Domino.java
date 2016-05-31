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
import xiaofei.library.shelly.internal.DominoCenter;
import xiaofei.library.shelly.internal.Player;
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
            public void playInternal(Object input) {
                domino.play(input);
            }
        });
    }

    public Domino target(final Class<?> clazz, final String target) {
        return new Domino(mLabel, new Player() {
            @Override
            public void playInternal(Object input) {
                TARGET_CENTER.call(clazz, target, input);
            }
        });
    }

    public <T> Domino target(final Class<T> clazz, final TargetAction<T> targetAction) {
        return new Domino(mLabel, new Player() {
            @Override
            public void playInternal(Object input) {
                List<Object> objects = TARGET_CENTER.getObjects(clazz);
                for (Object object : objects) {
                    targetAction.call((T) object, input);
                }
            }
        });
    }

    public Domino target(final Action0 action0) {
        return new Domino(mLabel, new Player() {
            @Override
            public void playInternal(Object input) {
                action0.call();
            }
        });
    }

    public Domino target(final Action1 action1) {
        return new Domino(mLabel, new Player() {
            @Override
            public void playInternal(Object input) {
                action1.call(input);
            }
        });
    }

    public Domino then(final Domino domino) {
        return new Domino(mLabel, new Player() {
            @Override
            public void playInternal(Object input) {
                mPlayer.play(input);
                domino.play(input);
            }
        });
    }

    public Domino map(final Function1 function1) {
        return new Domino(mLabel, new Player() {
            @Override
            protected void playInternal(Object input) {

            }

            @Override
            public void play(Object input) {
                mPlayer.play(input);
                playInternal(function1.call(input));
            }
        });
    }
    public Domino then(final Class<?> clazz, final String target) {
        return new Domino(mLabel, new Player() {
            @Override
            public void playInternal(Object input) {
                mPlayer.play(input);
                TARGET_CENTER.call(clazz, target, input);
            }
        });
    }

    public <T> Domino then(final Class<T> clazz, final TargetAction<T> targetAction) {
        return new Domino(mLabel, new Player() {
            @Override
            public void playInternal(Object input) {
                mPlayer.play(input);
                List<Object> objects = TARGET_CENTER.getObjects(clazz);
                for (Object object : objects) {
                    targetAction.call((T) object, input);
                }
            }
        });
    }

    public Domino then(final Action0 action0) {
        return new Domino(mLabel, new Player() {
            @Override
            public void playInternal(Object input) {
                mPlayer.play(input);
                action0.call();
            }
        });
    }

    public Domino then(final Action1 action1) {
        return new Domino(mLabel, new Player() {
            @Override
            public void playInternal(Object input) {
                mPlayer.play(input);
                action1.call(input);
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
