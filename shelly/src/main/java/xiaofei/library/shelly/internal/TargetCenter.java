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
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import xiaofei.library.shelly.annotation.AnnotationUtils;

/**
 * Created by Xiaofei on 16/5/26.
 */
public class TargetCenter {

    private static volatile TargetCenter sInstance = null;

    private final ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, Method>> mMethods;

    private final ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<Object>> mObjects;

    private TargetCenter() {
        mMethods = new ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, Method>>();
        mObjects = new ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<Object>>();
    }

    public static TargetCenter getInstance() {
        if (sInstance == null) {
            synchronized (TargetCenter.class) {
                if (sInstance == null) {
                    sInstance = new TargetCenter();
                }
            }
        }
        return sInstance;
    }

    public void register(Object object) {
        synchronized (mObjects) {
            for (Class<?> clazz = object.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
                CopyOnWriteArrayList<Object> objects = mObjects.get(clazz);
                if (objects == null) {
                    mObjects.putIfAbsent(clazz, new CopyOnWriteArrayList<Object>());
                    objects = mObjects.get(clazz);
                }
                objects.add(object);
                //The following must be in a synchronized block.
                //The mMethods modification must follow the mObjects modification.
                synchronized (mMethods) {
                    ConcurrentHashMap<String, Method> methods = mMethods.get(clazz);
                    if (methods == null) {
                        mMethods.putIfAbsent(clazz, new ConcurrentHashMap<String, Method>(AnnotationUtils.getTargetMethods(clazz)));
                    }
                }
            }
        }
    }

    public void unregister(Object object) {
        synchronized (mObjects) {
            for (Class<?> clazz = object.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
                CopyOnWriteArrayList<Object> objects = mObjects.get(clazz);
                if (objects == null) {
                    return;
                }
                int size = objects.size();
                for (int i = 0; i < size; ++i) {
                    if (objects.get(i) == object) {
                        objects.remove(i);
                        --i;
                        --size;
                    }
                }
                //The following must be in a synchronized block.
                synchronized (mMethods) {
                    if (objects.isEmpty()) {
                        mObjects.remove(clazz);
                        mMethods.remove(clazz);
                    }
                }
            }
        }
    }

    public boolean isRegistered(Object object) {
        Class<?> clazz = object.getClass();
        CopyOnWriteArrayList<Object> objects = mObjects.get(clazz);
        return objects != null && objects.contains(object);
    }

    public CopyOnWriteArrayList<Object> getObjects(Class<?> clazz) {
        return mObjects.get(clazz);
    }

    public <T> void call(Class<?> clazz, String target, List<T> input) {
        ConcurrentHashMap<String, Method> methods = mMethods.get(clazz);
        if (methods == null) {
            throw new IllegalStateException("Class " + clazz.getName() + " has not been registered.");
        }
        Method method = methods.get(target);
        if (method == null) {
            throw new IllegalStateException("Class " + clazz.getName() + " has no method matching the target " + target);
        }
        CopyOnWriteArrayList<Object> objects = mObjects.get(clazz);
        if (objects == null) {
            return;
        }
        if (!(input instanceof CopyOnWriteArrayList)) {
            throw new IllegalStateException("An error occurs! Please report this problem to Xiaofei!");
        }
        for (Object object : objects) {
            try {
                if (method.getParameterTypes().length == 0) {
                    method.invoke(object);
                } else {
                    for (T singleInput : input) {
                        method.invoke(object, singleInput);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
