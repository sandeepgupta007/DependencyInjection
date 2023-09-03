package com.company.DependencyInjectionFramework;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DependencyModel {
    private final Class<?> clazz;
    private final boolean isSingleton;
    private Set<Class<?>> dependenciesList;

    public DependencyModel(Class<?> clazz, boolean isSingleton, Set<Class<?>> dependenciesSet) {
        this.clazz = clazz;
        this.isSingleton = isSingleton;
        this.dependenciesList = dependenciesSet;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public boolean isSingleton() {
        return isSingleton;
    }

    public Set<Class<?>> getDependenciesList() {
        return dependenciesList;
    }
}
