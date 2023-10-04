package com.eternalcode.formatter.paper.injector;

import java.util.ArrayList;
import java.util.List;

class DependencyContainer<T> {

    private final T value;
    private final List<T> extraValues = new ArrayList<>();

    DependencyContainer(T value) {
        this.value = value;
    }

    public void addExtraValue(T value) {
        this.extraValues.add(value);
    }

    public T getExtraOrNormal(int index) {
        if (index == 0) {
            return this.value;
        }

        if (index >= this.extraValues.size()) {
            if (this.extraValues.isEmpty()) {
                return this.value;
            }

            return this.extraValues.get(this.extraValues.size() - 1);
        }

        return this.extraValues.get(index);
    }

}
