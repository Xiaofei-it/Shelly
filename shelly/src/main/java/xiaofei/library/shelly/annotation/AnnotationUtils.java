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

package xiaofei.library.shelly.annotation;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Xiaofei on 16/5/26.
 */
public class AnnotationUtils {

    private AnnotationUtils() {

    }

    public static Map<String, Method> getTargetMethods(Class<?> clazz) {
        Map<String, Method> result = new HashMap<String, Method>();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            DominoTarget dominoTarget = method.getAnnotation(DominoTarget.class);
            if (dominoTarget != null) {
                String value = dominoTarget.value();
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                if (method.getParameterTypes().length > 1) {
                    throw new IllegalStateException("Methods annotated with @DominoTarget should have less than one parameter.");
                }
                result.put(value, method);
            }
        }
        return result;
    }
}
