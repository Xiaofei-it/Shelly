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

package xiaofei.library.shelly.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import xiaofei.library.shelly.annotation.AnnotationUtils;

/**
 * Created by Xiaofei on 16/5/26.
 */
public class TargetCenter {

    private static TargetCenter sInstance = null;

    private final HashMap<Class<?>, HashMap<String, Method>> mMethods;

    private final HashMap<Class<?>, LinkedList<Object>> mObjects;

    private TargetCenter() {
        mMethods = new HashMap<Class<?>, HashMap<String, Method>>();
        mObjects = new HashMap<Class<?>, LinkedList<Object>>();
    }

    public static synchronized TargetCenter getInstance() {
        if (sInstance == null) {
            sInstance = new TargetCenter();
        }
        return sInstance;
    }

    public void register(Object object) {
        Class<?> clazz = object.getClass();
        synchronized (mMethods) {
            HashMap<String, Method> methods = mMethods.get(clazz);
            if (methods == null) {
                mMethods.put(clazz, AnnotationUtils.getTargetMethods(clazz));
            }
        }
        synchronized (mObjects) {
            LinkedList<Object> objects = mObjects.get(clazz);
            if (objects == null) {
                objects = new LinkedList<>();
                mObjects.put(clazz, objects);
            }
            objects.add(object);
        }
    }

    public void unregister(Object object) {
        Class<?> clazz = object.getClass();
        synchronized (mObjects) {
            LinkedList<Object> objects = mObjects.get(clazz);
            if (objects == null || !objects.remove(object)) {
                throw new IllegalArgumentException("Object " + object + " has not been registered.");
            }
            if (objects.isEmpty()) {
                synchronized (mMethods) {
                    mMethods.remove(clazz);
                }
            }
        }
    }

    public List<Object> getObjects(Class<?> clazz) {
        synchronized (mObjects) {
            return Collections.unmodifiableList(mObjects.get(clazz));
        }
    }

    public void call(Class<?> clazz, String target, Object input) {
        Method method;
        synchronized (mMethods) {
            HashMap<String, Method> methods = mMethods.get(clazz);
            if (methods == null) {
                throw new IllegalStateException();
            }
            method = methods.get(target);
        }
        if (method == null) {
            throw new IllegalStateException();
        }
        synchronized (mObjects) {
            LinkedList<Object> objects = mObjects.get(clazz);
            for (Object object : objects) {
                try {
                    method.invoke(object, input);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
