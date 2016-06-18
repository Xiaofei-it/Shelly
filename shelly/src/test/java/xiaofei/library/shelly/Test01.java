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

import xiaofei.library.shelly.annotation.DominoTarget;
import xiaofei.library.shelly.function.Action0;
import xiaofei.library.shelly.function.Action1;
import xiaofei.library.shelly.function.Function1;
import xiaofei.library.shelly.function.TargetAction1;

/**
 * Created by Xiaofei on 16/5/30.
 */
public class Test01 {

    private static class A {
        private int i;
        A(int i) {
            this.i = i;
        }
        @DominoTarget("target1")
        public void f(String s) {
            System.out.println("A " + i + " f " + s);
        }

        public void g(String s) {
            System.out.println("A " + i + " g " + s);
        }
    }

    private static class B {
        @DominoTarget("t")
        public void f() {
            System.out.println("B f");
        }
    }
    @Test
    public void case01() {
        Shelly.register(new A(1));
        Shelly.register(new A(2));
        Shelly.createDomino("case01", String.class)
                .target(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("Target action0");
                    }
                })
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("Target action1 " + input);
                    }
                })
                .target(A.class, "target1")
                .target(A.class, new TargetAction1<A, String>() {
                    @Override
                    public void call(A a, String input) {
                        a.g(input);
                    }
                })
                .map(new Function1<String, String>() {
                    @Override
                    public String call(String input) {
                        return "map1 " + input;
                    }
                })
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("After map : " + input);
                    }
                })
                .target(A.class, "target1")
                .map(new Function1<String, String>() {
                    @Override
                    public String call(String input) {
                        return "map2 " + input;
                    }
                })
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("After map2 : " + input);
                    }
                })
                .target(A.class, "target1")
                .commit();
        Shelly.playDomino("case01", "Haha", "Hehe");

        Shelly.createDomino("case03", String.class)
                .target(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("Target action0");
                    }
                })
                .background()
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("cached thread1 : " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .map(new Function1<String, String>() {
                    @Override
                    public String call(String input) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {

                        }
                        System.out.println("map1: " + Thread.currentThread().getName());
                        return "map1" + input;
                    }
                })
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("cached thread2 : " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .map(new Function1<String, String>() {
                    @Override
                    public String call(String input) {
                        System.out.println("map6: " + Thread.currentThread().getName());
                        return "map6" + input;
                    }
                })
                .newThread()
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("new Thread1 : " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("new Thread2 : " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .backgroundQueue()
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("single Thread1 : " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("single Thread2 : " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .background()
                .map(new Function1<String, String>() {
                    @Override
                    public String call(String input) {
                        System.out.println("map3: " + Thread.currentThread().getName());
                        return "map3" + input;
                    }
                })
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("cached thread3 : " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .map(new Function1<String, String>() {
                    @Override
                    public String call(String input) {
                        System.out.println("map4: " + Thread.currentThread().getName());
                        return "map4";
                    }
                })
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("cached thread4 : " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .map(new Function1<String, String>() {
                    @Override
                    public String call(String input) {
                        System.out.println("map5: " + Thread.currentThread().getName());
                        return "map5" + input;
                    }
                })
                .commit();
        Shelly.playDomino("case03", "ABC", "DEF");

        Shelly.createDomino("case04", String.class)
                .backgroundQueue()
                .target(new Action0() {
                    @Override
                    public void call() {
                        try {
                            Thread.sleep(2000);
                            System.out.println("Case 04: target");
                        } catch (InterruptedException e) {

                        }
                    }
                })
                .newThread()
                .target(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("Case 04 : then");
                    }
                })
                .commit();
        Shelly.playDomino("case04");

        Shelly.register(new B());
        Shelly.createDomino("case05", String.class)
                .target(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("Case 05");
                    }
                })
                .newThread()
                .target(new Action1<String>() {
                    @Override
                    public void call(String  object) {
                        System.out.println("Case 05 " + object);
                    }
                })
                .target(B.class, new TargetAction1<B, String>() {
                    @Override
                    public void call(B b, String input) {
                        System.out.println("Case 05 B " + input);
                    }
                })
                .target(B.class, "t")
                .commit();
        Shelly.playDomino("case05");
        Shelly.playDomino("case05", "8", "9");
        Shelly.playDomino("case05", "i", "j");

        Shelly.createDomino("case06", int.class)
                .defaultScheduler()
                .target(new Action1<Integer>() {
                    @Override
                    public void call(Integer object) {
                        System.out.println("Case 06 " + object);
                    }
                })
                .target(B.class, new TargetAction1<B, Integer>() {
                    @Override
                    public void call(B b, Integer input) {
                        System.out.println("Case 06 B " + input);
                    }
                })
                .commit();
        Shelly.playDomino("case06", 8, 9);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {

        }
    }
}
