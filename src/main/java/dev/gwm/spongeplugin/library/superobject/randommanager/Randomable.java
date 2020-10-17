package dev.gwm.spongeplugin.library.superobject.randommanager;

import java.util.Collections;
import java.util.List;

public interface Randomable {

    default boolean isPrefetch() {
        return false;
    }

    //Sub-classes must return a List of their own sub-classes.
    //Example: getChildren()'s return type of a class C must be List<? extends C>
    default List<? extends Randomable> getChildren() {
        return Collections.emptyList();
    }
}
