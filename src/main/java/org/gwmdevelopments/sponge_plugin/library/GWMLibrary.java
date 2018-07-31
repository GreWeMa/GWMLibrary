package org.gwmdevelopments.sponge_plugin.library;

import de.randombyte.holograms.api.HologramsService;
import org.gwmdevelopments.sponge_plugin.library.command.GWMLibraryCommandUtils;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.gwmdevelopments.sponge_plugin.library.utils.Config;
import org.gwmdevelopments.sponge_plugin.library.utils.Language;
import org.gwmdevelopments.sponge_plugin.library.utils.SpongePlugin;
import org.gwmdevelopments.sponge_plugin.library.utils.Version;

import javax.inject.Inject;
import java.io.File;
import java.util.Optional;

@Plugin(
        id = "gwm_library",
        name = "GWMLibrary",
        version = "1.3.3",
        description = "Necessary library to run plugins developed by GWM!",
        dependencies = {
                @Dependency(id = "holograms", optional = true)
        },
        authors = {"GWM"/* My contacts:
                         * E-Mail(nazark@tutanota.com),
                         * Telegram(@grewema),
                         * Discord(GWM#2192)*/})
public class GWMLibrary extends SpongePlugin {

    public static final Version VERSION = new Version(null, 1, 3, 3);

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
    private File configDirectory;

    private Cause cause;

    private Config config;
    private Config languageConfig;

    private Language language;

    private Optional<HologramsService> hologramsService = Optional.empty();

    private boolean checkUpdates = true;

    @Listener
    public void onConstruction(GameConstructionEvent event) {
        instance = this;
    }

    @Listener
    public void onPreInitialization(GamePreInitializationEvent event) {
        if (!configDirectory.exists()) {
            configDirectory.mkdirs();
        }
        cause = Cause.of(EventContext.empty(), container);
        config = new Config(this, "config.conf", false);
        languageConfig = new Config(this, "language.conf", false);
        loadConfigValues();
        language = new Language(this);
        if (checkUpdates) {
            checkUpdates();
        }
        logger.info("\"GamePreInitialization\" completed!");
    }

    @Listener
    public void onInitialization(GameInitializationEvent event) {
        GWMLibraryCommandUtils.registerCommands();
        logger.info("\"GameInitialization\" completed!");
    }

    @Listener
    public void onPostInitialization(GamePostInitializationEvent event) {
        loadHologramsService();
        logger.info("\"GamePostInitialization\" completed!");
    }

    @Listener
    public void onStopping(GameStoppingServerEvent event) {
        save();
        logger.info("\"GameStopping\" completed!");
    }

    @Listener
    public void reloadListener(GameReloadEvent event) {
        reload();
        logger.info("\"GameReload\" completed!");
    }

    @Override
    public void save() {
        config.save();
        languageConfig.save();
        logger.info("All plugin configs have been saved!");
    }

    @Override
    public void reload() {
        config.reload();
        languageConfig.reload();
        loadConfigValues();
        cause = Cause.of(EventContext.empty(), container);
        hologramsService = Optional.empty();
        loadHologramsService();
        if (checkUpdates) {
            checkUpdates();
        }
        logger.info("Plugin has been reloaded.");
    }

    private void loadConfigValues() {
        checkUpdates = config.getNode("CHECK_UPDATES").getBoolean(true);
    }

    private boolean loadHologramsService() {
        try {
            hologramsService = Sponge.getServiceManager().provide(HologramsService.class);
            if (hologramsService.isPresent()) {
                logger.info("Holograms Service found!");
                return true;
            }
        } catch (NoClassDefFoundError ignored) {}
        logger.warn("Holograms Service does not found!");
        logger.info("Please install \"Holograms\" plugin (https://ore.spongepowered.org/RandomByte/Holograms) if you want use holograms!");
        return false;
    }

    public Optional<HologramsService> getHologramsService() {
        return hologramsService;
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
        return configDirectory;
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public Config getLanguageConfig() {
        return languageConfig;
    }

    @Override
    public Language getLanguage() {
        return language;
    }

    public boolean isCheckUpdates() {
        return checkUpdates;
    }
}
