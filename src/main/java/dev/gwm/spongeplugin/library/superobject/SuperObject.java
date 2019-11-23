package dev.gwm.spongeplugin.library.superobject;

import dev.gwm.spongeplugin.library.util.SuperObjectCategory;

import java.util.HashSet;
import java.util.Set;

public interface SuperObject {

    default void shutdown() {
    }

    default Set<SuperObject> getInternalSuperObjects() {
        return new HashSet<>();
    }

    SuperObjectCategory<?> category();

    String type();

    String id();
}
