package org.gwmdevelopments.sponge_plugin.library.utils;

import org.slf4j.Logger;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;

public abstract class SpongePlugin {

    public void save() {
    }

    public void reload() {
    }

    public abstract Version getVersion();

    public abstract Cause getCause();

    public abstract Logger getLogger();

    public abstract PluginContainer getContainer();

    public abstract File getConfigDirectory();

    public abstract Config getConfig();

    public abstract Config getLanguageConfig();

    public abstract Language getLanguage();
}
