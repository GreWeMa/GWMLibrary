package dev.gwm.spongeplugin.library.event;

import dev.gwm.spongeplugin.library.GWMLibrary;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.util.SuperObjectIdentifier;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SuperObjectIdentifiersRegistrationEventImpl extends AbstractEvent implements SuperObjectIdentifiersRegistrationEvent {

    private final Map<SuperObjectIdentifier, Class<? extends SuperObject>> classes = new HashMap<>();

    @Override
    public void register(SuperObjectIdentifier identifier, Class<? extends SuperObject> clazz) {
        classes.put(identifier, clazz);
    }

    @Override
    public void unregister(SuperObjectIdentifier identifier) {
        classes.remove(identifier);
    }

    public Map<SuperObjectIdentifier, Class<? extends SuperObject>> getClasses() {
        return Collections.unmodifiableMap(classes);
    }

    @Override
    public Cause getCause() {
        return GWMLibrary.getInstance().getCause();
    }
}