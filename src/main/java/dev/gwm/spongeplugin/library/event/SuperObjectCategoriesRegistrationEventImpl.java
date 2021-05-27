package dev.gwm.spongeplugin.library.event;

import dev.gwm.spongeplugin.library.GWMLibrary;
import dev.gwm.spongeplugin.library.util.SuperObjectCategory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SuperObjectCategoriesRegistrationEventImpl extends AbstractEvent implements SuperObjectCategoriesRegistrationEvent {

    private Set<SuperObjectCategory<?>> categories = new HashSet<>();

    @Override
    public void register(SuperObjectCategory<?> category) {
        categories.add(category);
    }

    @Override
    public void unregister(SuperObjectCategory<?> category) {
        categories.remove(category);
    }

    public Set<SuperObjectCategory<?>> getCategories() {
        return Collections.unmodifiableSet(categories);
    }

    @Override
    public Cause getCause() {
        return GWMLibrary.getInstance().getCause();
    }
}
