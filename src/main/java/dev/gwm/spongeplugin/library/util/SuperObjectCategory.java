package dev.gwm.spongeplugin.library.util;

import dev.gwm.spongeplugin.library.superobject.SuperObject;

import java.util.Objects;

public class SuperObjectCategory<T extends SuperObject> {

    private final String name;

    public SuperObjectCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuperObjectCategory<?> that = (SuperObjectCategory<?>) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
