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

import java.util.concurrent.TimeUnit;

import xiaofei.library.shelly.Shelly;
import xiaofei.library.shelly.function.Action0;
import xiaofei.library.shelly.function.Action1;
import xiaofei.library.shelly.function.TargetAction0;
import xiaofei.library.shelly.function.TargetAction1;
import xiaofei.library.shelly.task.Task;

/**
 * Created by Xiaofei on 16/6/21.
 */
public class Test06 {

    @Test
    public void testTask() {
        class A {
            public void f(Character s) {
                System.out.println("A f: " + Thread.currentThread().getName() + " " + s);
            }
            public void g(int s) {
                System.out.println("A g: " + Thread.currentThread().getName() + " " + s);
            }
        }
        Shelly.register(new A());
        Shelly.<String>createDomino(1)
                .beginTask(new Task<String, Character, Integer>() {
                    @Override
                    protected void onExecute(String input) {
                        System.out.println("1: " + Thread.currentThread().getName() + " " + input);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("2: " + Thread.currentThread().getName() + " " + input);
                        if (input.length() == 1) {
                            notifySuccess(input.charAt(0));
                        } else {
                            notifyFailure(input.charAt(0) - 'A');
                        }
                    }
                })
                .onSuccess(Shelly.<Character>createDomino()
                            .background()
                            .perform(new Action1<Character>() {
                                @Override
                                public void call(Character input) {
                                    System.out.println("3: " + Thread.currentThread().getName() + " " + input);
                                }
                            }))
                .newThread()
                .onSuccess(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("4: " + Thread.currentThread().getName());
                    }
                })
                .onSuccess(new Action1<Character>() {
                    @Override
                    public void call(Character input) {
                        System.out.println("5: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .background()
                .onSuccess(A.class, new TargetAction0<A>() {
                    @Override
                    public void call(A a) {
                        System.out.println("6: " + Thread.currentThread().getName());
                        a.f('T');
                    }
                })
                .backgroundQueue()
                .onFailure(Shelly.<Integer>createDomino()
                            .newThread()
                            .perform(new Action1<Integer>() {
                                @Override
                                public void call(Integer input) {
                                    System.out.println("7: " + Thread.currentThread().getName() + " " + input);
                                }
                            }))
                .onFailure(new Action1<Integer>() {
                    @Override
                    public void call(Integer input) {
                        System.out.println("8: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .onFailure(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("9: " + Thread.currentThread().getName());
                    }
                })
                .onSuccess(A.class, new TargetAction1<A, Character>() {
                    @Override
                    public void call(A a, Character input) {
                        System.out.println("10: " + Thread.currentThread().getName() + " " + input);
                        a.f(input);
                    }
                })
                .onFailure(A.class, new TargetAction0<A>() {
                    @Override
                    public void call(A a) {
                        System.out.println("11: " + Thread.currentThread().getName());
                        a.g(1000);
                    }
                })
                .onFailure(A.class, new TargetAction1<A, Integer>() {
                    @Override
                    public void call(A a, Integer input) {
                        System.out.println("12: " + Thread.currentThread().getName() + " " + input);
                        a.g(input);
                    }
                })
                .finallyDo(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("13: " + Thread.currentThread().getName());
                    }
                })
                .<String>endTaskEmpty()
                .perform(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("14: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .perform(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("15: " + Thread.currentThread().getName());
                    }
                })
                .commit();
        Shelly.playDomino(1, "A", "BD", "CG");
    }

    @Test
    public void testTask2() {
        class A {
            public void f(Character s) {
                System.out.println("A f: " + Thread.currentThread().getName() + " " + s);
            }
            public void g(int s) {
                System.out.println("A g: " + Thread.currentThread().getName() + " " + s);
            }
        }
        Shelly.register(new A());
        Shelly.<String>createDomino(3)
                .background()
                .beginTask(new Task<String, Character, Integer>() {
                    @Override
                    protected void onExecute(String input) {
                        System.out.println("1: " + Thread.currentThread().getName() + " " + input);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("2: " + Thread.currentThread().getName() + " " + input);
                        if (input.length() == 1) {
                            notifySuccess(input.charAt(0));
                        } else {
                            notifyFailure(input.charAt(0) - 'A');
                        }
                    }
                })
                .onSuccess(Shelly.<Character>createDomino()
                        .background()
                        .perform(new Action1<Character>() {
                            @Override
                            public void call(Character input) {
                                System.out.println("3: " + Thread.currentThread().getName() + " " + input);
                            }
                        }))
                .newThread()
                .onSuccess(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("4: " + Thread.currentThread().getName());
                    }
                })
                .onSuccess(new Action1<Character>() {
                    @Override
                    public void call(Character input) {
                        System.out.println("5: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .background()
                .onSuccess(A.class, new TargetAction0<A>() {
                    @Override
                    public void call(A a) {
                        System.out.println("6: " + Thread.currentThread().getName());
                        a.f('T');
                    }
                })
                .backgroundQueue()
                .onFailure(Shelly.<Integer>createDomino()
                        .newThread()
                        .perform(new Action1<Integer>() {
                            @Override
                            public void call(Integer input) {
                                System.out.println("7: " + Thread.currentThread().getName() + " " + input);
                            }
                        }))
                .onFailure(new Action1<Integer>() {
                    @Override
                    public void call(Integer input) {
                        System.out.println("8: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .onFailure(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("9: " + Thread.currentThread().getName());
                    }
                })
                .onSuccess(A.class, new TargetAction1<A, Character>() {
                    @Override
                    public void call(A a, Character input) {
                        System.out.println("10: " + Thread.currentThread().getName() + " " + input);
                        a.f(input);
                    }
                })
                .onFailure(A.class, new TargetAction0<A>() {
                    @Override
                    public void call(A a) {
                        System.out.println("11: " + Thread.currentThread().getName());
                        a.g(1000);
                    }
                })
                .onFailure(A.class, new TargetAction1<A, Integer>() {
                    @Override
                    public void call(A a, Integer input) {
                        System.out.println("12: " + Thread.currentThread().getName() + " " + input);
                        a.g(input);
                    }
                })
                .finallyDo(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("13: " + Thread.currentThread().getName());
                    }
                })
                .endTask()
                .perform(new Action1<Character>() {
                    @Override
                    public void call(Character input) {
                        System.out.println("14: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .perform(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("15: " + Thread.currentThread().getName());
                    }
                })
                .commit();
        Shelly.playDomino(3, "A", "BD", "CG");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testThrottle() {
        Shelly.<String>createDomino(2)
                .perform(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("1: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .newThread()
                .throttle(1, TimeUnit.SECONDS)
                .perform(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("2: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .commit();
        Shelly.<String>createDomino(3)
                .perform(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("3: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .newThread()
                .perform(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("4: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .commit();
        Shelly.<String>createDomino(4)
                .perform(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("5: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .newThread()
                .throttle(1, TimeUnit.SECONDS)
                .perform(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("6: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .commit();
        Shelly.playDomino(2, "A");
        Shelly.playDomino(3, "A");
        Shelly.playDomino(4, "A");
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Shelly.playDomino(2, "B");
        Shelly.playDomino(3, "B");
        Shelly.playDomino(4, "B");
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Shelly.playDomino(2, "C");
        Shelly.playDomino(3, "C");
        Shelly.playDomino(4, "C");
    }
}