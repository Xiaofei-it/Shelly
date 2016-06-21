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
import xiaofei.library.shelly.internal.Task;

/**
 * Created by Xiaofei on 16/6/21.
 */
public class Test06 {

    @Test
    public void testMerge() {
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
                            .target(new Action1<Character>() {
                                @Override
                                public void call(Character input) {
                                    System.out.println("3: " + Thread.currentThread().getName() + " " + input);
                                }
                            }))
                .onFailure(Shelly.<Integer>createDomino()
                            .newThread()
                            .target(new Action1<Integer>() {
                                @Override
                                public void call(Integer input) {
                                    System.out.println("4: " + Thread.currentThread().getName() + " " + input);
                                }
                            }))
                .finallyDo(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("5: " + Thread.currentThread().getName());
                    }
                })
                .<String>endTask()
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("6: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .target(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("7: " + Thread.currentThread().getName());
                    }
                })
                .commit();
        Shelly.playDomino(1, "A", "BD", "BG");
    }

    @Test
    public void testThrottle() {
        Shelly.<String>createDomino(2)
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("1: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .newThread()
                .throttle(1, TimeUnit.SECONDS)
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("2: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .commit();
        Shelly.<String>createDomino(3)
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("3: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .newThread()
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("4: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .commit();
        Shelly.<String>createDomino(4)
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("5: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .newThread()
                .throttle(1, TimeUnit.SECONDS)
                .target(new Action1<String>() {
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