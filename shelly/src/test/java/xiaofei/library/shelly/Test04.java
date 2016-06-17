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
        Shelly.createDomino(1)
                .target(new Action1<Object>() {
                    @Override
                    public void call(Object input) {
                        System.out.println("1: " + input);
                    }
                })
                .flatMap(new Function1<Object, List<String>>() {
                    @Override
                    public List<String> call(Object input) {
                        List<String> list = new ArrayList<String>();
                        list.add("" + input + "flatMap1");
                        list.add("" + input + "flatMap2");
                        return list;
                    }
                })
                .target(new Action1<Object>() {
                    @Override
                    public void call(Object input) {
                        System.out.println("2: " + input);
                    }
                })
                .map(new Function1<Object, String>() {
                    @Override
                    public String call(Object input) {
                        return "" + input + "map";
                    }
                })
                .target(new Action1<Object>() {
                    @Override
                    public void call(Object input) {
                        System.out.println("3: " + input);
                    }
                })
                .commit();
        Shelly.playDomino(1, "A", "B");

    }
}