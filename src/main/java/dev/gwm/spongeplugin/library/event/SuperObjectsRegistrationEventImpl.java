package dev.gwm.spongeplugin.library.event;

import dev.gwm.spongeplugin.library.GWMLibrary;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class SuperObjectsRegistrationEventImpl extends AbstractEvent implements SuperObjectsRegistrationEvent {

    @Override
    public Cause getCause() {
        return GWMLibrary.getInstance().getCause();
    }
}
