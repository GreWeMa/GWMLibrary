package dev.gwm.spongeplugin.library.utils;

import dev.gwm.spongeplugin.library.superobject.SuperObject;

import java.util.function.Predicate;

public class SuperObjectIdEqualsPredicate implements Predicate<SuperObject> {

    private final String id;

    public SuperObjectIdEqualsPredicate(String id) {
        this.id = id;
    }

    @Override
    public boolean test(SuperObject so) {
        return so.id().equals(id);
    }
}