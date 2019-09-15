package dev.gwm.spongeplugin.library.superobject.randommanager;

import java.util.Collections;
import java.util.List;

public interface Randomable {

    default boolean isPrefetch() {
        return false;
    }

    //Sub-classes have to return a List of themselves their sub-classes.
    //Example: getChildren()'s return type of a class C should be List<? extends C>
    default List<? extends Randomable> getChildren() {
        return Collections.emptyList();
    }
}
