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

import xiaofei.library.shelly.action.Action0;

import static org.junit.Assert.assertEquals;

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
    }
}