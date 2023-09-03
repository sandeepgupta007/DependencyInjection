package com.company.DependencyInjectionFramework;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DependencyContainer {

    private final Map<Class<?>, Object> singletonObjectPool = new ConcurrentHashMap<>();
    private final Map<Class<?>, Set<Class<?>>> dependenciesToClasses = new ConcurrentHashMap<>();
    private final Map<Class<?>, DependencyModel> classDependencyStoreMap = new ConcurrentHashMap<>();

    public void register(Class<?> classname, Boolean isSingleton, Set<Class<?>> dependentClasses) {
        DependencyModel dependencyModel = new DependencyModel(classname, isSingleton, dependentClasses);
        classDependencyStoreMap.put(classname, dependencyModel);
        for (Class<?> depCls : dependentClasses) {
            dependenciesToClasses.putIfAbsent(depCls, new HashSet<>());
            Set<Class<?>> classList = dependenciesToClasses.get(depCls);
            classList.add(classname);
        }
    }

    public <T> T resolve(Class<T> cls) {
        T instance = cls.cast(singletonObjectPool.get(cls));
        if (instance == null) {
            // resolve dependencies
            try {
                return (T) resolveDependency(cls);
            } catch (NoSuchMethodException | IllegalAccessException |
                    InvocationTargetException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    private Object resolveDependency(Class<?> cls) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Map<Class<?>, Set<Class<?>>> classToDependencyCounter = new HashMap<>();
        getClassToDependencyCounterMap(cls, classToDependencyCounter);

        Queue<Class<?>> resolvedDependentClasses = getAllResolvedDependentClasses(classToDependencyCounter);

        LinkedList<Class<?>> classResolutionOrder = new LinkedList<>();

        extractClassResolutionOrder(classToDependencyCounter, resolvedDependentClasses, classResolutionOrder);

        if (classResolutionOrder.size() != classToDependencyCounter.size())
            return null; // all dependencies were not resolved.

        Map<Class<?>, Object> classToObjectMap = new HashMap<>();
        for (Class<?> currentClass : classResolutionOrder) {
            if (!singletonObjectPool.containsKey(currentClass)) {

                if (classDependencyStoreMap.get(currentClass).getDependenciesList().size() == 0) {
                    classToObjectMap.put(currentClass, currentClass.getConstructor().newInstance());
                }
                else {
                    List<Object> allDependentClassesObjects = new ArrayList<>();
                    for (Class<?> childClass : classDependencyStoreMap.get(currentClass).getDependenciesList()) {
                        allDependentClassesObjects.add(childClass.cast(classToObjectMap.get(childClass)));
                    }
                    Constructor<?> constructor = currentClass.getConstructor(classDependencyStoreMap.get(currentClass).getDependenciesList().toArray(new Class<?>[0]));
                    classToObjectMap.put(currentClass, constructor.newInstance(allDependentClassesObjects.toArray()));
                }
                if (classDependencyStoreMap.get(currentClass).isSingleton()) {
                    singletonObjectPool.put(currentClass, classToObjectMap.get(currentClass));
                }
            }
            else {
                classToObjectMap.put(currentClass, singletonObjectPool.get(currentClass));
            }
        }

        return classToObjectMap.get(cls);
    }

    private void extractClassResolutionOrder(Map<Class<?>, Set<Class<?>>> classToDependencyCounter, Queue<Class<?>> resolvedDependentClasses, LinkedList<Class<?>> classResolutionOrder) {
        while (resolvedDependentClasses.size() > 0) {
            Class<?> currentClass = resolvedDependentClasses.poll();
            classResolutionOrder.add(currentClass);

            if (!dependenciesToClasses.containsKey(currentClass))
                continue;

            for (Class<?> entry : dependenciesToClasses.get(currentClass)) {
                if (!classToDependencyCounter.containsKey(entry))
                    continue;
                classToDependencyCounter.get(entry).remove(currentClass);
                if (classToDependencyCounter.get(entry).size() == 0) {
                    resolvedDependentClasses.add(entry);
                }
            }
        }
    }

    private Queue<Class<?>> getAllResolvedDependentClasses(Map<Class<?>, Set<Class<?>>> map) {
        Queue<Class<?>> dependentClasses = new LinkedList<>();
        for (Map.Entry<Class<?>, Set<Class<?>>> entry : map.entrySet()) {
            if (entry.getValue().size() == 0) {
                dependentClasses.add(entry.getKey());
            }
        }
        return dependentClasses;
    }

    private void getClassToDependencyCounterMap(Class<?> cls, Map<Class<?>, Set<Class<?>>> map) {
        if (cls == null)
            return;

        if (map.containsKey(cls))
            return;

        for (Class<?> dep : classDependencyStoreMap.get(cls).getDependenciesList()) {
            getClassToDependencyCounterMap(dep, map);
        }
        map.put(cls, new HashSet<>(classDependencyStoreMap.get(cls).getDependenciesList()));
    }

}
