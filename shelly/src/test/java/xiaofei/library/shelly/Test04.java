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

import java.util.ArrayList;
import java.util.List;

import xiaofei.library.shelly.function.Action1;
import xiaofei.library.shelly.function.Function1;

/**
 * Created by Xiaofei on 16/6/18.
 */
public class Test04 {

    @Test
    public void testFlatMap() {
        Shelly.createDomino(1, String.class)
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("1: " + input);
                    }
                })
                .flatMap(new Function1<String, List<Integer>>() {
                    @Override
                    public List<Integer> call(String input) {
                        List<Integer> list = new ArrayList<Integer>();
                        list.add(input.charAt(0) - 'A');
                        list.add(input.charAt(0) - 'A' + 10);
                        return list;
                    }
                })
                .target(new Action1<Integer>() {
                    @Override
                    public void call(Integer input) {
                        System.out.println("2: " + input);
                    }
                })
                .map(new Function1<Integer, String>() {
                    @Override
                    public String call(Integer input) {
                        return "" + input + "map";
                    }
                })
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("3: " + input);
                    }
                })
                .commit();
        Shelly.playDomino(1, "A", "B");

    }

    @Test
    public void testFilter() {
        Shelly.createDomino(2, String.class)
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("1: " + input);
                    }
                })
                .filter(new Function1<String, Boolean>() {
                    @Override
                    public Boolean call(String input) {
                        return input.length() == 1;
                    }
                })
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("2: " + input);
                    }
                })
                .commit();
        Shelly.playDomino(2, "A", "B", "AB", "CD");
    }

    @Test
    public void small() {
        Shelly.createDomino(3, String.class)
                .map(new Function1<String, String>() {
                    @Override
                    public String call(String input) {
                        return input;
                    }
                })
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        System.out.println("3: " + input);
                    }
                })
                .commit();
        Shelly.playDomino(3, "A");
    }
}