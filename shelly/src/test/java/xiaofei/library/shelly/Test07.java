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

import java.util.concurrent.TimeUnit;

import xiaofei.library.shelly.function.Action0;
import xiaofei.library.shelly.function.Action1;
import xiaofei.library.shelly.function.TargetAction1;
import xiaofei.library.shelly.internal.Task;

/**
 * Created by Xiaofei on 16/6/22.
 */
public class Test07 {

    private static class A {
        private int x;
        A(int x) {
            this.x = x;
        }
        void f(String a) {
            System.out.println("A: " + a + " x = " + x);
        }
    }

    private static class B extends A {
        B(int x) {
            super(x);
        }

    }
    @Test
    public void testMerge() {
        A a1 = new A(1), a2 = new B(2);
        Shelly.register(a1);
        Shelly.register(a1);
        Shelly.register(a2);
        System.out.println(Shelly.isRegistered(a1));
        System.out.println(Shelly.isRegistered(a2));
        Shelly.<String>createDomino(1)
                .target(A.class, new TargetAction1<A, String>() {
                    @Override
                    public void call(A a, String input) {
                        a.f(input);
                    }
                })
                .commit();
        Shelly.playDomino(1, "F");
        Shelly.unregister(a1);
        System.out.println(Shelly.isRegistered(a1));
        System.out.println(Shelly.isRegistered(a2));
        Shelly.playDomino(1, "G");
        Shelly.unregister(a1);
        System.out.println(Shelly.isRegistered(a1));
        System.out.println(Shelly.isRegistered(a2));
        Shelly.playDomino(1, "G");
    }

}