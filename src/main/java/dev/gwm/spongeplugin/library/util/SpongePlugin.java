package dev.gwm.spongeplugin.library.util;

import org.slf4j.Logger;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.asset.AssetManager;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;
import java.util.Optional;

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

    public Optional<Asset> getDefaultTranslation(AssetManager assetManager) {
        return assetManager.getAsset(this, "translations/en_us.conf");
    }

    public abstract Language getLanguage();
}
