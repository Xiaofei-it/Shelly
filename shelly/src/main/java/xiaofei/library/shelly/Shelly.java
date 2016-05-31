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

import xiaofei.library.shelly.internal.DominoCenter;
import xiaofei.library.shelly.internal.TargetCenter;

/**
 * Created by Xiaofei on 16/5/26.
 */
public class Shelly {

    //private static final Handler sHandler = new Handler(Looper.getMainLooper());

    private static final TargetCenter TARGET_CENTER = TargetCenter.getInstance();

    private static final DominoCenter DOMINO_CENTER = DominoCenter.getInstance();

    public static void register(Object object) {
        TARGET_CENTER.register(object);
    }

    public static Domino getDomino(String name) {
        return new Domino(name);
    }

    public static void play(final String name, final Object input) {
        DOMINO_CENTER.play(name, input);
    }
}
