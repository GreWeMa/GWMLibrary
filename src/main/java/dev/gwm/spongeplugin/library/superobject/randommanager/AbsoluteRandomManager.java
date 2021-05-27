package dev.gwm.spongeplugin.library.superobject.randommanager;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.service.permission.Subject;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class AbsoluteRandomManager extends AbstractRandomManager<AbsoluteRandomManager.AbsoluteRandomable> {

    public static final String TYPE = "ABSOLUTE";

    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    public AbsoluteRandomManager(ConfigurationNode node) {
        super(node);
    }

    public AbsoluteRandomManager(String id) {
        super(id);
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public <R extends AbsoluteRandomable> R choose(List<R> randomables, @Nullable Subject subject, boolean fake) {
        R randomable = randomables.size() == 1 ?
                randomables.get(0) :
                randomables.get(random.nextInt(randomables.size()));
        if (randomable.isPrefetch()) {
            return choose((List<R>) randomable.getChildren(), subject, fake);
        }
        return randomable;
    }

    public interface AbsoluteRandomable extends Randomable {

        @Override
        List<? extends AbsoluteRandomable> getChildren();
    }
}
