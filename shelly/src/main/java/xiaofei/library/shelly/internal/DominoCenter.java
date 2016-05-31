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

package xiaofei.library.shelly.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import xiaofei.library.shelly.Domino;

/**
 * Created by Xiaofei on 16/5/26.
 */
public class DominoCenter {

    private static DominoCenter sInstance = null;

    private HashMap<String, Domino> mDominoes;
    private DominoCenter() {
        mDominoes = new HashMap<String, Domino>();
    }

    public static synchronized DominoCenter getInstance() {
        if (sInstance == null) {
            sInstance = new DominoCenter();
        }
        return sInstance;
    }

    public void commit(Domino domino) {
        String name = domino.getName();
        synchronized (mDominoes) {
            if (mDominoes.containsKey(name)) {
                throw new IllegalStateException("Domino name duplicate! Check whether you have commit a domino with the same name before.");
            }
            mDominoes.put(name, domino);
        }
    }

    public void play(String name, Object input) {
        synchronized (mDominoes) {
            Domino domino = mDominoes.get(name);
            if (domino == null) {
                throw new IllegalStateException("There is no domino named '" + name + "'.");
            }
            List<Object> list = new ArrayList<>();
            list.add(input);
            domino.play(list);
        }
    }

}
