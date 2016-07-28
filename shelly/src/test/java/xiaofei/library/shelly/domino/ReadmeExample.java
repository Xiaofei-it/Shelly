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

import java.util.List;

import xiaofei.library.shelly.Shelly;
import xiaofei.library.shelly.function.Action0;
import xiaofei.library.shelly.function.Action1;
import xiaofei.library.shelly.function.Function1;
import xiaofei.library.shelly.function.TargetAction0;
import xiaofei.library.shelly.function.TargetAction1;

/**
 * Created by Xiaofei on 16/7/28.
 */
public class ReadmeExample {
    public class MyActivity{

    }
    public void f() {
        //Create a domino labeled "Example" which takes one or more Strings as input
        Shelly.<String>createDomino("Example")
                //Perform an action. The action is performed once.
                .target(new Action0() {
                    @Override
                    public void call() {
                        //Do something
                    }
                })
                //Perform an action which takes the String as input.
                //If one String is passed here, the action is performed once.
                //If two Strings are passed here, the action is performed twice.
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        //Do something
                    }
                })
                //Perform another action which takes the String as input.
                //If one String is passed here, the action is performed once.
                //If two Strings are passed here, the action is performed twice.
                .target(new Action1<String>() {
                    @Override
                    public void call(String input) {
                        //Do something
                    }
                })
                //The above actions is performed in the thread in which the domino is invoked.
                //Now the following actions will be perform in background.
                .background()
                //Transform the String into an integer.
                //If one String is passed here, one integer will be passed to the following actions.
                //If two Strings are passed here, two integers will be passed to the following actions.
                .map(new Function1<String, Integer>() {
                    @Override
                    public Integer call(String input) {
                        return null;
                    }
                })
                //The following actions will be perform in a queue in background.
                .backgroundQueue()
                //Use a filter to filter the integers.
                //Only the integers labeled "true" will be passed to the following actions.
                .filter(new Function1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer input) {
                        return false;
                    }
                })
                //Pass the integer into the function and the function takes an integer as input
                //and return a list of Strings. Each String will be passed to the following actions.
                //If an integer is passed here, and the function returns two Strings,
                //then two Strings will be passed to the following actions.
                //If two integers are passed here, and the function takes an integer as input and
                //returns two Strings, then we get four Strings here,
                //then four Strings will be passed to the following actions.
                .flatMap(new Function1<Integer, List<String>>() {
                    @Override
                    public List<String> call(Integer input) {
                        return null;
                    }
                })
                //The following actions will be perform in the main thread, i.e. the UI thread.
                .uiThread()
                //Perform an action on all registered instances of MyActivity.
                .target(MyActivity.class, new TargetAction0<MyActivity>() {
                    @Override
                    public void call(MyActivity myActivity) {
                        //Do something
                    }
                })
                //Pass all the Strings into the function and get a single double.
                //Now the following actions will receive only one single input which is a double.
                .reduce(new Function1<List<String>, Double>() {
                    @Override
                    public Double call(List<String> input) {
                        return null;
                    }
                })
                //Perform an action on all registered instances of MyActivity.
                //If there are two instances, then:
                //If one String is passed here, the action is performed twice.
                //If two Strings are passed here, the action is performed four times.
                .target(MyActivity.class, new TargetAction1<MyActivity, Double>() {
                    @Override
                    public void call(MyActivity myActivity, Double input) {
                        //Do something
                    }
                })
                .commit();
    }
}
