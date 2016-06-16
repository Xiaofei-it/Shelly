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

import org.junit.Test;

import xiaofei.library.shelly.function.Action0;
import xiaofei.library.shelly.function.Action1;
import xiaofei.library.shelly.function.Function1;

/**
 * Created by Xiaofei on 16/5/30.
 */
public class Test02 {

    @Test
    public void f() {
        Shelly.createDomino(1)
                .target(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("directly then");
                    }
                })
                .commit();
        Shelly.playDomino(1, 2);

        Shelly.createDomino(2)
                .target(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("double target 1");
                    }
                })
                .target(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("double target 2");
                    }
                })
                .commit();
        Shelly.playDomino(2, 2);

        Shelly.createDomino(3)
                .target(new Action1() {
                    @Override
                    public void call(Object input) {
                        System.out.println("action1 " + input);
                    }
                })
                .map(new Function1() {
                    @Override
                    public Object call(Object input) {
                        return input + "haha";
                    }
                })
                .target(new Action1() {
                    @Override
                    public void call(Object input) {
                        System.out.println("after map " + input);
                    }
                })
                .map(new Function1() {
                    @Override
                    public Object call(Object input) {
                        return input + "hihi";
                    }
                })
                .target(new Action1() {
                    @Override
                    public void call(Object input) {
                        System.out.println("after map2 " + input);
                    }
                })
                .commit();
        Shelly.playDomino(3, 2);

        Shelly.createDomino(4)
                .target(new Action1() {
                    @Override
                    public void call(Object input) {
                        System.out.println("action1 " + input);
                    }
                })
                .map(new Function1() {
                    @Override
                    public Object call(Object input) {
                        return "Map " + input;
                    }
                })
                .target(new Action1() {
                    @Override
                    public void call(Object input) {
                        System.out.println("action1 " + input);
                    }
                })
                .commit();
        Shelly.playDomino(4, 2);
    }
}