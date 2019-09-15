package dev.gwm.spongeplugin.library.event;

import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.utils.SuperObjectIdentifier;
import org.spongepowered.api.event.Event;

public interface SuperObjectIdentifiersRegistrationEvent extends Event {

    void register(SuperObjectIdentifier identifier, Class<? extends SuperObject> clazz);

    void unregister(SuperObjectIdentifier identifier);
}
