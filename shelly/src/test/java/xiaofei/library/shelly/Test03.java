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

/**
 * Created by Xiaofei on 16/5/30.
 */
public class Test03 {

    @Test
    public void f() {
        Shelly.createDomino(1, Integer.class)
                .backgroundQueue()
                .target(new Action1<Integer>() {
                    @Override
                    public void call(Integer input) {
                        System.out.println("cached thread1 : " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .map(new Function1<Integer, String>() {
                    @Override
                    public String call(Integer input) {
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
                        return "map2" + input;
                    }
                })
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("cached thread3 : " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .commit();
        Shelly.playDomino(1, 2);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {

        }
    }

    @Test
    public void g() {

        Shelly.createDomino(2, Integer.class)
                .background()
                .target(new Action1<Integer>() {
                    @Override
                    public void call(Integer input) {
                        System.out.println("back queue : " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .map(new Function1<Integer, String>() {
                    @Override
                    public String call(Integer input) {
                        System.out.println("map1: " + Thread.currentThread().getName());
                        return "map1" + input;
                    }
                })
                .newThread()
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("new thread : " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .commit();
        Shelly.playDomino(2, 2);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {

        }
    }

    @Test
    public void h() {
        Shelly.createDomino("case03", String.class)
                .background()
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
                        System.out.println("Fuck: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .map(new Function1<String, String>() {
                    @Override
                    public String call(String input) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {

                        }
                        System.out.println("map2: " + Thread.currentThread().getName());
                        return "map2" + input;
                    }
                })
                .newThread()
                .map(new Function1<String, String>() {
                    @Override
                    public String call(String input) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {

                        }
                        System.out.println("map3: " + Thread.currentThread().getName());
                        return "map3" + input;
                    }
                })
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("new Thread1 : " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .commit();
        Shelly.playDomino("case03", "ABC");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void i() {
        Shelly.createDomino("case04", String.class)
                .newThread()
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
                        System.out.println("Fuck: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .map(new Function1<String, String>() {
                    @Override
                    public String call(String input) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {

                        }
                        System.out.println("map2: " + Thread.currentThread().getName());
                        return "map2" + input;
                    }
                })
                .commit();
        Shelly.playDomino("case04", "ABC");
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testNull() {
        Shelly.createDomino("Null", Integer.class)
                .target(new Action1<Integer>() {
                    @Override
                    public void call(Integer input) {
                        System.out.println("a " + input);
                    }
                })
                .commit();
        Shelly.playDomino("Null");
        Shelly.playDomino("Null", null);
        Shelly.playDomino("Null", 1);
    }
}