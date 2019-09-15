package dev.gwm.spongeplugin.library.utils;

import dev.gwm.spongeplugin.library.superobject.SuperObject;
import ninja.leaping.configurate.ConfigurationNode;

import java.util.*;
import java.util.function.Predicate;

public interface SuperObjectsService {

    Set<SuperObjectCategory> getCategories();

    Map<SuperObjectIdentifier, Class<? extends SuperObject>> getClasses();

    Set<SuperObject> getCreatedSuperObjects();

    default Optional<SuperObject> getCreatedSuperObjectById(String id) {
        return getCreatedSuperObjects().stream().filter(new SuperObjectIdEqualsPredicate(id)).findFirst();
    }

    Set<SuperObject> getSavedSuperObjects();

    default Optional<SuperObject> getSavedSuperObjectById(String id) {
        return getSavedSuperObjects().stream().filter(new SuperObjectIdEqualsPredicate(id)).findFirst();
    }

    <T extends SuperObject> T create(SuperObjectCategory<T> category, ConfigurationNode node, boolean save);

    default <T extends SuperObject> T create(SuperObjectCategory<T> category, ConfigurationNode node) {
        return create(category, node, true);
    }

    SuperObject load(ConfigurationNode node, boolean save);

    default Optional<SuperObject> getSuperObjectById(String id) {
        Optional<SuperObject> optionalSuperObject = getCreatedSuperObjectById(id);
        return optionalSuperObject.isPresent() ? optionalSuperObject : getSavedSuperObjectById(id);
    }

    //Removes and shutdowns all Saved Super Objects, and their internal Created Super Objects
    default void shutdownSavedSuperObjects() {
        Iterator<SuperObject> iterator = getSavedSuperObjects().iterator();
        while (iterator.hasNext()) {
            SuperObject superObject = iterator.next();
            superObject.getInternalSuperObjects().forEach(this::shutdownCreatedSuperObject);
            superObject.shutdown();
            iterator.remove();
        }
    }

    default void shutdownCreatedSuperObject(SuperObject target) {
        Set<SuperObject> createdSuperObjects = getCreatedSuperObjects();
        target.getInternalSuperObjects().forEach(this::shutdownCreatedSuperObject);
        if (createdSuperObjects.contains(target)) {
            target.shutdown();
            createdSuperObjects.remove(target);
        }
    }

    //Removes and shutdowns all Created Super Objects which satisfy the predicate,
    //but does not remove their children if they are Saved Super Objects
    default void shutdownCreatedSuperObjects(Predicate<SuperObject> predicate) {
        Set<SuperObject> toRemove = new HashSet<>();
        getCreatedSuperObjects().forEach(superObject -> {
            if (predicate.test(superObject)) {
                toRemove.add(superObject);
            }
        });
        toRemove.forEach(this::shutdownCreatedSuperObject);
    }
}
