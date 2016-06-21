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
import xiaofei.library.shelly.internal.Task;

/**
 * Created by Xiaofei on 16/6/21.
 */
public class Test06 {

    @Test
    public void testMerge() {
        Shelly.createDomino(1, String.class)
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
                .onSuccess(Shelly.createDomino(Character.class)
                            .background()
                            .target(new Action1<Character>() {
                                @Override
                                public void call(Character input) {
                                    System.out.println("3: " + Thread.currentThread().getName() + " " + input);
                                }
                            }))
                .onFailure(Shelly.createDomino(Integer.class)
                            .newThread()
                            .target(new Action1<Integer>() {
                                @Override
                                public void call(Integer input) {
                                    System.out.println("4: " + Thread.currentThread().getName() + " " + input);
                                }
                            }))
                .endTask()
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("5: " + Thread.currentThread().getName() + " " + input);
                    }
                })
                .target(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("6: " + Thread.currentThread().getName());
                    }
                })
                .commit();
        Shelly.playDomino(1, "A", "BD", "BG");
    }

}