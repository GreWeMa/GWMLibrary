package ua.gwm.sponge_plugin.library.utils;

import org.slf4j.Logger;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import javax.inject.Inject;
import java.io.File;

@Plugin(
        id = "gwm_library",
        name = "GWMLibrary",
        version = "1.0",
        description = "Necessary library to run plugins developed by GWM!",
        authors = {"GWM"/*
                         * Nazar Kalinovskiy
                         * My contacts:
                         * E-Mail(gwm@tutanota.com),
                         * Discord(GWM#2192)*/})
public class GWMLibrary extends SpongePlugin {

    public static final Version VERSION = new Version(null, 1, 0);

    private static GWMLibrary instance = null;

    public static GWMLibrary getInstance() {
        if (instance == null) {
            throw new RuntimeException("GWMLibrary not initialized!");
        }
        return instance;
    }

    @Inject
    private Logger logger;

    @Inject
    private PluginContainer container;

    @Inject
    @ConfigDir(sharedRoot = false)
    private File config_directory;

    private Cause cause;

    private Config config;
    private Config language_config;

    private Language language;

    private boolean check_updates = true;

    @Listener
    public void onConstruction(GameConstructionEvent event) {
        instance = this;
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        if (!config_directory.exists()) {
            config_directory.mkdirs();
        }
        cause = Cause.of(EventContext.empty(), container);
        config = new Config(this, "config.conf", false);
        language_config = new Config(this, "language.conf", false);
        loadConfigValues();
        language = new Language(this);
        if (check_updates) {
            checkUpdates();
        }
        logger.info("\"GamePreInitializationEvent\" completed!");
    }

    @Listener
    public void onStopping(GameStoppingServerEvent event) {
        save();
        logger.info("\"GameStopping\" complete!");
    }

    @Listener
    public void reloadListener(GameReloadEvent event) {
        reload();
        logger.info("\"GameReload\" complete!");
    }

    @Override
    public void save() {
        config.save();
        language_config.save();
        logger.info("All plugin configs have been saved!");
    }

    @Override
    public void reload() {
        config.reload();
        language_config.reload();
        loadConfigValues();
        cause = Cause.of(EventContext.empty(), container);
        if (check_updates) {
            checkUpdates();
        }
        logger.info("Plugin has been reloaded.");
    }

    private void loadConfigValues() {
        check_updates = config.getNode("CHECK_UPDATES").getBoolean(true);
    }

    @Override
    public Version getVersion() {
        return VERSION;
    }

    @Override
    public Cause getCause() {
        return cause;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public PluginContainer getContainer() {
        return container;
    }

    @Override
    public File getConfigDirectory() {
        return config_directory;
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public Config getLanguageConfig() {
        return language_config;
    }

    @Override
    public Language getLanguage() {
        return language;
    }
}
