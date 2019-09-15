package dev.gwm.spongeplugin.library.superobject.randommanager;

import dev.gwm.spongeplugin.library.superobject.AbstractSuperObject;
import dev.gwm.spongeplugin.library.utils.GWMLibrarySuperObjectCategories;
import dev.gwm.spongeplugin.library.utils.SuperObjectCategory;
import ninja.leaping.configurate.ConfigurationNode;

import java.util.Optional;

public abstract class AbstractRandomManager<T extends Randomable> extends AbstractSuperObject implements RandomManager<T> {

    public AbstractRandomManager(ConfigurationNode node) {
        super(node);
    }

    public AbstractRandomManager(Optional<String> id) {
        super(id);
    }

    @Override
    public final SuperObjectCategory<RandomManager<?>> category() {
        return GWMLibrarySuperObjectCategories.RANDOM_MANAGER;
    }
}
