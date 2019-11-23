package dev.gwm.spongeplugin.library.superobject.randommanager;

import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.util.GWMLibrarySuperObjectCategories;
import dev.gwm.spongeplugin.library.util.SuperObjectCategory;
import org.spongepowered.api.service.permission.Subject;

import javax.annotation.Nullable;
import java.util.List;

public interface RandomManager<T extends Randomable> extends SuperObject {

    //R's getChildren() method should return List<R>
    //iterable should be non-empty
    <R extends T> R choose(List<R> randomables, @Nullable Subject subject, boolean fake);

    default <R extends T> R choose(List<R> randomables, boolean fake) {
        return choose(randomables, null, fake);
    }

    default <R extends T> R choose(List<R> randomables) {
        return choose(randomables, null, false);
    }

    @Override
    default SuperObjectCategory<RandomManager<?>> category() {
        return GWMLibrarySuperObjectCategories.RANDOM_MANAGER;
    }
}
