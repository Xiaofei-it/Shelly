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

package xiaofei.library.shellytest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Test;

import java.lang.reflect.Type;

/**
 * Created by Xiaofei on 16/7/20.
 */
public class Test02 {

    private static final Gson GSON = new Gson();

    private static class W {
        String w;
    }
    private static class A<T> {
        T data;
    }



    private static class B<T> {
        A<T> get(String s) {
            Type type = new TypeToken<A<T>>() {}.getType();
            A<T> a = GSON.fromJson(s, type);
            return a;
        }
    }

    @Test
    public void f() {
        W w = new W();
        w.w = "Hello";
        A<W> a1 = new A<>();
        a1.data = w;
        B<W> b1 = new B<>();
        String s = GSON.toJson(a1);
        System.out.println(s);
        a1 = b1.get(s);
        //The following will throw an exception.
        System.out.println(a1.data.w);
    }
}