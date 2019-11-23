package dev.gwm.spongeplugin.library.event;

import dev.gwm.spongeplugin.library.util.SuperObjectCategory;
import org.spongepowered.api.event.Event;

public interface SuperObjectCategoriesRegistrationEvent extends Event {

    void register(SuperObjectCategory category);

    void unregister(SuperObjectCategory category);
}
