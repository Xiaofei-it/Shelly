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
public class Test01 {

    private static class A<T> {
        T data;
    }

    private static class B<T> {
        T data;
    }

    @Test
    public void f() {
        Type type = new TypeToken<A<String>>() {}.getType();
        A<String> a1 = new A<>();
        a1.data = "Hello";
        Gson gson = new Gson();
        String s = gson.toJson(a1);
        System.out.println(s);
        A<String> a2 = gson.fromJson(s, type);
        System.out.println(a2.data);
        B<A<String>> b1 = new B<>();
        b1.data = a1;
        type = new TypeToken<B<A<String>>>() {}.getType();
        s = gson.toJson(b1);
        System.out.println(s);
        B<A<String>> b2 = gson.fromJson(s, type);
        System.out.println(b2.data.data);
    }
}