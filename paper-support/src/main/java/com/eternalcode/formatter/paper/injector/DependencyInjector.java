package com.eternalcode.formatter.paper.injector;

import com.eternalcode.paper.multiversion.LegacyDependencyProvider;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class DependencyInjector {

    private final Map<Class<?>, DependencyContainer<?>> dependencies = new HashMap<>();

    public <T> DependencyInjector tryRegister(Supplier<LegacyDependencyProvider<T>> provider) {
        try {
            LegacyDependencyProvider<T> legacyDependencyProvider = provider.get();

            this.registerUnSafe(legacyDependencyProvider.getType(), legacyDependencyProvider.getDependency());
        }
        catch (NoClassDefFoundError ignored) { }

        return this;
    }

    @SuppressWarnings("unchecked")
    private <T> void registerUnSafe(Class<?> type, T dependency) {
        this.register((Class<T>) type, dependency);
    }

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
            if (constructor.isAnnotationPresent(Deprecated.class)) {
                continue;
            }

            List<Object> parameters = new ArrayList<>();
            Map<Class<?>, Integer> parameterCount = new HashMap<>();

            for (Class<?> parameterType : constructor.getParameterTypes()) {
                parameters.add(this.getDependency(parameterType, parameterCount.getOrDefault(parameterType, 0)));
                parameterCount.merge(parameterType, 1, Integer::sum);
            }

            try {
                return clazz.cast(constructor.newInstance(parameters.toArray(new Object[0])));
            }
            catch (ReflectiveOperationException e) {
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
