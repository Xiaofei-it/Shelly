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
        Shelly.createDomino(1, String.class)
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
        Shelly.createDomino(2, String.class)
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("2: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .background()
                .dominoMap((Domino<String, String>) Shelly.getDominoByLabel(1))
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
        Shelly.createDomino(3, String.class)
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("4: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .background()
                .merge((Domino<String, String>) Shelly.getDominoByLabel(1), (Domino<String, String>) Shelly.getDominoByLabel(2))
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

        Shelly.createDomino(4, String.class)
                .newThread()
                .target((Domino<String, String>) Shelly.getDominoByLabel(1))
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
        Shelly.createDomino(5, String.class)
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
        Shelly.createDomino(6, String.class)
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
        Shelly.createDomino(7, String.class)
                .combine((Domino<String, Integer>) Shelly.getDominoByLabel(5),
                        (Domino<String, Boolean>) Shelly.getDominoByLabel(6),
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

}