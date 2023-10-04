package com.eternalcode.paper.multiversion;

public interface LegacyDependencyProvider<T> {

    Class<? extends T> getType();

    T getDependency();

}
