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

import xiaofei.library.shelly.function.Action1;
import xiaofei.library.shelly.function.Function1;
import xiaofei.library.shelly.function.Function2;

/**
 * Created by Xiaofei on 16/6/20.
 */
public class Test05 {

    @Test
    public void testMerge() {
        Shelly.<String>createDomino(1)
                .newThread()
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("1: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .map(new Function1<String, String>() {
                    @Override
                    public String call(String input) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {

                        }
                        return input + "ha";
                    }
                })
                .commit();
        Shelly.<String>createDomino(2)
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("2: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .background()
                .dominoMap(Shelly.<String, String>getDominoByLabel(1))
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("3: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .map(new Function1<String, String>() {
                    @Override
                    public String call(String input) {
                        return input + "hi";
                    }
                })
                .commit();
        Shelly.playDomino(2, "A", "B");
        Shelly.<String>createDomino(3)
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("4: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .background()
                .merge(Shelly.<String, String>getDominoByLabel(1), Shelly.<String, String>getDominoByLabel(2))
                .newThread()
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("5: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .commit();
        Shelly.playDomino(3, "C", "D");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Shelly.<String>createDomino(4)
                .newThread()
                .target(Shelly.<String, String>getDominoByLabel(1))
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("6: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .commit();
        Shelly.playDomino(4, "E", "F");

    }

    @Test
    public void testCombine() {
        Shelly.<String>createDomino(5)
                .newThread()
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("1: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .map(new Function1<String, Integer>() {
                    @Override
                    public Integer call(String input) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {

                        }
                        return input.charAt(0) - 'A';
                    }
                })
                .commit();
        Shelly.<String>createDomino(6)
                .newThread()
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("2: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .map(new Function1<String, Boolean>() {
                    @Override
                    public Boolean call(String input) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {

                        }
                        return input.equals("A");
                    }
                })
                .commit();
        Shelly.<String>createDomino(7)
                .combine(Shelly.<String, Integer>getDominoByLabel(5),
                        Shelly.<String, Boolean>getDominoByLabel(6),
                        new Function2<Integer, Boolean, String>() {
                            @Override
                            public String call(Integer input1, Boolean input2) {
                                return "" + input1 + input2;
                            }
                        })
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("3: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .commit();
        Shelly.playDomino(7, "A", "B");
    }


    @Test
    public void testCombineNull() {
        Shelly.<String>createDomino(8)
                .newThread()
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("1: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .map(new Function1<String, Integer>() {
                    @Override
                    public Integer call(String input) {
                        return null;
                    }
                })
                .commit();
        Shelly.<String>createDomino(9)
                .newThread()
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("2: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .map(new Function1<String, Boolean>() {
                    @Override
                    public Boolean call(String input) {
                        return true;
                    }
                })
                .commit();
        Shelly.<String>createDomino(10)
                .combine(Shelly.<String, Integer>getDominoByLabel(8),
                        Shelly.<String, Boolean>getDominoByLabel(9),
                        new Function2<Integer, Boolean, String>() {
                            @Override
                            public String call(Integer input1, Boolean input2) {
                                return "" + input1 + input2;
                            }
                        })
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("3: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .commit();
        Shelly.playDomino(10, "A");
    }

}