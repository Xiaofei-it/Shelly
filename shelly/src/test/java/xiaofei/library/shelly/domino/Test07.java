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

package xiaofei.library.shelly.domino;

import org.junit.Test;

import xiaofei.library.shelly.Shelly;
import xiaofei.library.shelly.function.Action1;
import xiaofei.library.shelly.function.Function1;
import xiaofei.library.shelly.function.Function2;
import xiaofei.library.shelly.function.TargetAction1;

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
        int getX() {
            return x;
        }
    }

    private static class B extends A {
        B(int x) {
            super(x);
        }

    }
    @Test
    public void testRegister() {
        A a1 = new A(1), a2 = new B(2);
        Shelly.register(a1);
        Shelly.register(a1);
        Shelly.register(a2);
        System.out.println(Shelly.isRegistered(a1));
        System.out.println(Shelly.isRegistered(a2));
        Shelly.<String>createDomino(1)
                .perform(A.class, new TargetAction1<A, String>() {
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

    @Test
    public void testMap() {
        A a1 = new A(1), a2 = new B(2);
        Shelly.register(a1);
        Shelly.register(a2);
        Shelly.<A>createDomino(2)
                .perform(new Action1<A>() {
                    @Override
                    public void call(A input) {
                        System.out.println("1: " + input.getX());
                    }
                })
                .map(A.class, new Function2<A, A, String>() {
                    @Override
                    public String call(A input1, A input2) {
                        return "" + input1.getX() + " " + input2.getX();
                    }
                })
                .perform(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("2: " + input);
                    }
                })
                .commit();
        Shelly.playDomino(2, a1, a2);
    }

    @Test
    public void testBackgroundQueue() {
        Shelly.<String>createDomino(3)
                .backgroundQueue()
                .perform(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("1: " + Thread.currentThread().getName() + " " + input);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .perform(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("2: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .backgroundQueue()
                .perform(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("3: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .perform(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("4: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .commit();
        Shelly.<String>createDomino(4)
                .backgroundQueue()
                .perform(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("5: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .perform(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("6: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .commit();
        Shelly.playDomino(3, "A");
        Shelly.playDomino(4, "B");
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBugOfHandler() {
        Shelly.<String>createDomino(5)
                .merge(new Domino[]{Shelly.<String>createDomino()
                        .background()
                        .map(new Function1<String, Integer>() {
                            @Override
                            public Integer call(String input) {
                                System.out.println("Map1 " + Thread.currentThread().getName());
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                return 3;
                            }
                        })
                        .uiThread()
                        .map(new Function1<Integer, Integer>() {
                            @Override
                            public Integer call(Integer input) {
                                System.out.println("Map2 " + Thread.currentThread().getName());
                                return input;
                            }
                        })}
                )
                .commit();
        Shelly.playDomino(5, "A");
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() {
        Shelly.<String>createDomino(8)
                .map(new Function1<String, Integer>() {
                    @Override
                    public Integer call(String input) {
                        return 3;
                    }
                })
                .perform(new Action1<Integer>() {
                    @Override
                    public void call(Integer input) {
                        System.out.println(input);
                    }
                })
                .commit();
        Shelly.playDomino(8, "A");
    }

}