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

package xiaofei.library.shelly.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Xiaofei on 16/5/26.
 */
public class TargetCenter {

    private static volatile TargetCenter sInstance = null;

    private final ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<Object>> mTargets;

    private TargetCenter() {
        mTargets = new ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<Object>>();
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

    public void register(Object target) {
        synchronized (mTargets) {
            for (Class<?> clazz = target.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
                CopyOnWriteArrayList<Object> targets = mTargets.get(clazz);
                if (targets == null) {
                    mTargets.putIfAbsent(clazz, new CopyOnWriteArrayList<Object>());
                    targets = mTargets.get(clazz);
                }
                targets.add(target);
            }
        }
    }

    public void unregister(Object target) {
        synchronized (mTargets) {
            for (Class<?> clazz = target.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
                CopyOnWriteArrayList<Object> targets = mTargets.get(clazz);
                if (targets == null) {
                    return;
                }
                int size = targets.size();
                for (int i = 0; i < size; ++i) {
                    if (targets.get(i) == target) {
                        targets.remove(i);
                        --i;
                        --size;
                    }
                }
                if (targets.isEmpty()) {
                    mTargets.remove(clazz);
                }
            }
        }
    }

    public boolean isRegistered(Object target) {
        Class<?> clazz = target.getClass();
        CopyOnWriteArrayList<Object> targets = mTargets.get(clazz);
        return targets != null && targets.contains(target);
    }

    public CopyOnWriteArrayList<Object> getTargets(Class<?> clazz) {
        CopyOnWriteArrayList<Object> targets = mTargets.get(clazz);
        return targets == null ? new CopyOnWriteArrayList<Object>() : targets;
    }
}
