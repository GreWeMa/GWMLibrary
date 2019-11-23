package dev.gwm.spongeplugin.library.superobject.randommanager;

import dev.gwm.spongeplugin.library.superobject.AbstractSuperObject;
import dev.gwm.spongeplugin.library.util.GWMLibrarySuperObjectCategories;
import dev.gwm.spongeplugin.library.util.SuperObjectCategory;
import ninja.leaping.configurate.ConfigurationNode;

public abstract class AbstractRandomManager<T extends Randomable> extends AbstractSuperObject implements RandomManager<T> {

    public AbstractRandomManager(ConfigurationNode node) {
        super(node);
    }

    public AbstractRandomManager(String id) {
        super(id);
    }

    @Override
    public final SuperObjectCategory<RandomManager<?>> category() {
        return GWMLibrarySuperObjectCategories.RANDOM_MANAGER;
    }
}
