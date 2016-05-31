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

import java.util.ArrayList;
import java.util.List;

import xiaofei.library.shelly.annotation.Target;
import xiaofei.library.shelly.function.Action0;
import xiaofei.library.shelly.function.Action1;
import xiaofei.library.shelly.function.Function0;
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

    private String mName;

    public Domino(String name) {
        this(name, null);
    }

    private Domino(String name, Player player) {
        mName = name;
        mPlayer = player;
    }

    public String getName() {
        return mName;
    }

    public Domino target(final Domino domino) {
        return new Domino(mName, new Player() {
            @Override
            public List<Object> play(List<Object> inputs) {
                return domino.play(inputs);
            }
        });
    }

    public Domino target(final Class<?> clazz, final String target) {
        return new Domino(mName, new Player() {
            @Override
            public List<Object> play(List<Object> inputs) {
                return TARGET_CENTER.call(clazz, target, inputs);
            }
        });
    }

    public <T> Domino target(final Class<T> clazz, final TargetAction<T> targetAction) {
        return new Domino(mName, new Player() {
            @Override
            public List<Object> play(List<Object> inputs) {
                List<Object> objects = TARGET_CENTER.getObjects(clazz);
                for (Object object : objects) {
                    targetAction.call((T) object, inputs);
                }
                return new ArrayList<>();
            }
        });
    }

    public Domino target(final Action0 action0) {
        return new Domino(mName, new Player() {
            @Override
            public List<Object> play(List<Object> inputs) {
                action0.call();
                return new ArrayList<>();
            }
        });
    }

    public Domino target(final Action1 action1) {
        return new Domino(mName, new Player() {
            @Override
            public List<Object> play(List<Object> inputs) {
                action1.call(inputs);
                return new ArrayList<>();
            }
        });
    }

    public Domino target(final Function0 function0) {
        return new Domino(mName, new Player() {
            @Override
            public List<Object> play(List<Object> inputs) {
                List<Object> list = new ArrayList<>();
                list.add(function0.call());
                return list;
            }
        });
    }

    public Domino target(final Function1 function1) {
        return new Domino(mName, new Player() {
            @Override
            public List<Object> play(List<Object> inputs) {
                List<Object> result = new ArrayList<>();
                for (Object input : inputs) {
                    result.add(function1.call(input));
                }
                return result;
            }
        });
    }

    public Domino with(final Domino domino) {
        return new Domino(mName, new Player() {
            @Override
            public List<Object> play(List<Object> inputs) {
                List<Object> result = mPlayer.play(inputs);
                result.addAll(domino.play(inputs));
                return result;
            }
        });
    }

    public Domino with(Class<?> clazz, String target) {
        return null;
    }

    public Domino with(final Function1 function1) {
        return new Domino(mName, new Player() {
            @Override
            public List<Object> play(List<Object> inputs) {
                List<Object> result = mPlayer.play(inputs);
                for (Object input : inputs) {
                    result.add(function1.call(input));
                }
                return result;
            }
        });
    }

    public Domino then(final Domino domino) {
        return new Domino(mName, new Player() {
            @Override
            public List<Object> play(List<Object> inputs) {
                List<Object> list = mPlayer.play(inputs);
                List<Object> result = new ArrayList<>();
                for (Object o : list) {
                    List<Object> tmp = domino.play(list);
                    if (tmp != null) {
                        result.addAll(tmp);
                    }
                }
                return result;
            }
        });
    }

    public Domino then(String target) {
        return null;
    }

    public Domino then(final Function1 function1) {
        return new Domino(mName, new Player() {
            @Override
            public List<Object> play(List<Object> inputs) {
                List<Object> tmp = mPlayer.play(inputs);
                List<Object> result = new ArrayList<>();
                for (Object input :tmp) {
                    result.add(function1.call(input));
                }
                return result;
            }
        });
    }

    /**
     * call one by one
     * @param inputs
     * @return
     */
    public List<Object> play(List<Object> inputs) {
        return mPlayer.play(inputs);
    }

    public void commit() {
        DOMINO_CENTER.commit(this);
    }
}
