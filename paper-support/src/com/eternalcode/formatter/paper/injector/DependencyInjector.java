package com.eternalcode.formatter.paper.injector;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DependencyInjector {

    private final Map<Class<?>, DependencyContainer<?>> dependencies = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> DependencyInjector register(Class<T> clazz, T instance) {
        DependencyContainer<T> container = (DependencyContainer<T>) this.dependencies.get(clazz);

        if (container == null) {
            this.dependencies.put(clazz, new DependencyContainer<>(instance));
            return this;
        }

        container.addExtraValue(instance);
        return this;
    }

    public <T> T newInstance(Class<T> clazz) {
        for (Constructor<?> constructor : clazz.getConstructors()) {
            List<Object> parameters = new ArrayList<>();
            Map<Class<?>, Integer> parameterCount = new HashMap<>();

            for (Class<?> parameterType : constructor.getParameterTypes()) {
                parameters.add(this.getDependency(parameterType, parameterCount.getOrDefault(parameterType, 0)));
                parameterCount.merge(parameterType, 1, Integer::sum);
            }

            try {
                return clazz.cast(constructor.newInstance(parameters.toArray(new Object[0])));
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Failed to instantiate " + clazz, e);
            }
        }

        throw new IllegalStateException("No constructor found for " + clazz);
    }

    @SuppressWarnings("unchecked")
    private <T> T getDependency(Class<T> clazz, int index) {
        DependencyContainer<?> container = this.dependencies.get(clazz);

        if (container != null) {
            return (T) container.getExtraOrNormal(index);
        }

        for (Map.Entry<Class<?>, DependencyContainer<?>> entry : this.dependencies.entrySet()) {
            Class<?> key = entry.getKey();

            if (clazz.isAssignableFrom(key)) {
                return (T) entry.getValue().getExtraOrNormal(index);
            }
        }

        throw new IllegalStateException("No dependency found for " + clazz);
    }

}
