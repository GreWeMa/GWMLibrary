package dev.gwm.spongeplugin.library.utils;

import dev.gwm.spongeplugin.library.superobject.SuperObject;

import java.util.Objects;

public final class SuperObjectIdentifier<T extends SuperObject> {

    private final SuperObjectCategory<T> category;
    private final String type;

    public SuperObjectIdentifier(SuperObjectCategory<T> category, String type) {
        this.category = category;
        this.type = type;
    }

    public SuperObjectCategory<T> getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuperObjectIdentifier<?> that = (SuperObjectIdentifier<?>) o;
        return Objects.equals(category, that.category) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, type);
    }

    @Override
    public String toString() {
        return "SuperObjectIdentifier{" +
                "category=" + category.getName() +
                ", type=" + type +
                '}';
    }
}
