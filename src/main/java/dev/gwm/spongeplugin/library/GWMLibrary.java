package dev.gwm.spongeplugin.library;

import de.randombyte.holograms.api.HologramsService;
import dev.gwm.spongeplugin.library.event.SuperObjectCategoriesRegistrationEventImpl;
import dev.gwm.spongeplugin.library.event.SuperObjectIdentifiersRegistrationEventImpl;
import dev.gwm.spongeplugin.library.event.SuperObjectsRegistrationEventImpl;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.superobject.randommanager.AbsoluteRandomManager;
import dev.gwm.spongeplugin.library.superobject.randommanager.LevelRandomManager;
import dev.gwm.spongeplugin.library.superobject.randommanager.WeightRandomManager;
import dev.gwm.spongeplugin.library.util.*;
import dev.gwm.spongeplugin.library.util.service.SuperObjectService;
import dev.gwm.spongeplugin.library.util.service.SuperObjectServiceImpl;
import me.rojo8399.placeholderapi.PlaceholderService;
import ninja.leaping.configurate.ConfigurationNode;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.world.ChunkTicketManager;

import javax.inject.Inject;
import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Plugin(
        id = "gwm_library",
        name = "GWMLibrary",
        version = "2.5.4",
        description = "Library with Super Objects and other utilities",
        dependencies = {
                @Dependency(id = "holograms", optional = true),
                @Dependency(id = "placeholderapi", optional = true)
        },
        authors = {"GWM"/* My contacts:
                         * E-Mail(nazark@tutanota.com),
                         * Telegram(@gwmdev)*/})
public final class GWMLibrary extends SpongePlugin {

    public static final Version VERSION = new Version(2, 5, 4);

    private static GWMLibrary instance = null;

    public static GWMLibrary getInstance() {
        if (instance == null) {
            throw new IllegalStateException("GWMLibrary is not initialized!");
        }
        return instance;
    }

    private final Game game;
    private final Cause cause;

    private final Logger logger;
    private final PluginContainer container;

    private final File configDirectory;

    private final Config config;
    private final Config languageConfig;
    private final Config savedSuperObjectsConfig;
    private final Config savedItemsConfig;

    private final Language language;

    private boolean logRegisteredCategories = true;
    private boolean logRegisteredIdentifiers = true;
    private boolean logLoadedSavedSuperObjects = true;

    private Optional<EconomyService> economyService = Optional.empty();
    private Optional<HologramsService> hologramsService = Optional.empty();
    private Optional<PlaceholderService> placeholderService = Optional.empty();
    private Optional<ChunkTicketManager> chunkTicketManager = Optional.empty();
    private SuperObjectServiceImpl superObjectService;

    @Inject
    public GWMLibrary(Game game,
                      Logger logger,
                      PluginContainer container,
                      @ConfigDir(sharedRoot = false) File configDirectory) {
        GWMLibrary.instance = this;
        this.game = game;
        cause = Cause.of(EventContext.empty(), container);
        this.logger = logger;
        this.container = container;
        this.configDirectory = configDirectory;
        if (!configDirectory.exists()) {
            logger.info("Config directory does not exist! Trying to create it...");
            if (configDirectory.mkdirs()) {
                logger.info("Config directory successfully created!");
            } else {
                logger.error("Failed to create config directory!");
            }
        }
        config = new Config.Builder(this, new File(configDirectory, "config.conf")).
                loadDefaults("config.conf").
                build();
        languageConfig = new Config.Builder(this, new File(configDirectory, "language.conf")).
                loadDefaults(getDefaultTranslationPath()).
                build();
        savedSuperObjectsConfig = new Config.Builder(this, new File(configDirectory, "saved_super_objects.conf")).
                loadDefaults("saved_super_objects.conf").
                build();
        savedItemsConfig = new Config.Builder(this, new File(configDirectory, "saved_items.conf")).
                build();
        language = new Language(this);
        logger.info("Construction completed!");
    }

    @Listener
    public void onPreInitialization(GamePreInitializationEvent event) {
        loadConfigValues();
        GWMLibraryCommandUtils.registerCommands(this);
        initializeSuperObjectsService();
        logger.info("PreInitialization completed!");
    }

    @Listener
    public void onPostInitialization(GamePostInitializationEvent event) {
        loadEconomyService();
        loadHologramsService();
        loadPlaceholderService();
        loadChunkTicketManager();
        logger.info("PostInitialization completed!");
    }

    @Listener
    public void onStartingServer(GameStartingServerEvent event) {
        loadSavedSuperObjects();
        game.getEventManager().post(new SuperObjectsRegistrationEventImpl());
        logger.info("StartingServer completed!");
    }

    @Listener
    public void reloadListener(GameReloadEvent event) {
        reload();
        logger.info("Reload completed!");
    }

    @Listener
    public void onStopping(GameStoppingServerEvent event) {
        superObjectService.shutdownSavedSuperObjects();
        logger.info("Stopping completed!");
    }

    @Override
    public void reload() {
        config.reload();
        languageConfig.reload();
        savedSuperObjectsConfig.reload();
        savedItemsConfig.reload();
        loadConfigValues();
        superObjectService.shutdownSavedSuperObjects();
        loadSavedSuperObjects();
        logger.info("Plugin has been reloaded.");
    }

    private void loadConfigValues() {
        try {
            ConfigurationNode logRegisteredCategoriesNode = config.getNode("LOG_REGISTERED_CATEGORIES");
            ConfigurationNode logRegisteredIdentifiersNode = config.getNode("LOG_REGISTERED_IDENTIFIERS");
            ConfigurationNode logLoadedSavedSuperObjectsNode = config.getNode("LOG_LOADED_SAVED_SUPER_OBJECTS");
            logRegisteredCategories = logRegisteredCategoriesNode.getBoolean(true);
            logRegisteredIdentifiers = logRegisteredIdentifiersNode.getBoolean(true);
            logLoadedSavedSuperObjects = logLoadedSavedSuperObjectsNode.getBoolean(true);
        } catch (Exception e) {
            logger.error("Failed to load config values!", e);
        }
    }

    private void initializeSuperObjectsService() {
        SuperObjectCategoriesRegistrationEventImpl categoriesRegistrationEvent = new SuperObjectCategoriesRegistrationEventImpl();
        categoriesRegistrationEvent.register(GWMLibrarySuperObjectCategories.RANDOM_MANAGER);
        game.getEventManager().post(categoriesRegistrationEvent);
        Set<SuperObjectCategory> categories = categoriesRegistrationEvent.getCategories();
        if (logRegisteredCategories) {
            categories.forEach(category -> logger.info("Registered category \"" + category + "\""));
        }
        SuperObjectIdentifiersRegistrationEventImpl identifiersRegistrationEvent = new SuperObjectIdentifiersRegistrationEventImpl();
        identifiersRegistrationEvent.register(
                new SuperObjectIdentifier<>(GWMLibrarySuperObjectCategories.RANDOM_MANAGER, LevelRandomManager.TYPE),
                LevelRandomManager.class);
        identifiersRegistrationEvent.register(
                new SuperObjectIdentifier<>(GWMLibrarySuperObjectCategories.RANDOM_MANAGER, WeightRandomManager.TYPE),
                WeightRandomManager.class);
        identifiersRegistrationEvent.register(
                new SuperObjectIdentifier<>(GWMLibrarySuperObjectCategories.RANDOM_MANAGER, AbsoluteRandomManager.TYPE),
                AbsoluteRandomManager.class);
        game.getEventManager().post(identifiersRegistrationEvent);
        Map<SuperObjectIdentifier, Class<? extends SuperObject>> classes = identifiersRegistrationEvent.getClasses();
        if (logRegisteredIdentifiers) {
            classes.forEach((identifier, value) -> {
                String category = identifier.getCategory().getName();
                String type = identifier.getType();
                logger.info("Registered identifier for \"" + value.getCanonicalName() + "\" with category \"" + category + "\" and type \"" + type + "\"");
            });
        }
        superObjectService = new SuperObjectServiceImpl(categories, classes);
        game.getServiceManager().setProvider(this, SuperObjectService.class, superObjectService);
    }

    private void loadSavedSuperObjects() {
        ConfigurationNode savedSuperObjectsNode = savedSuperObjectsConfig.getNode("SAVED_SUPER_OBJECTS");
        if (!savedSuperObjectsNode.isVirtual()) {
            for (ConfigurationNode savedSuperObjectNode : savedSuperObjectsNode.getChildrenList()) {
                try {
                    SuperObject superObject = superObjectService.load(savedSuperObjectNode, true);
                    if (logLoadedSavedSuperObjects) {
                        String category = superObject.category().getName();
                        String type = superObject.type();
                        String id = superObject.id();
                        logger.info("Loaded Saved Super Object with category \"" + category + "\", type \"" + type + "\" and id \"" + id + "\"");
                    }
                } catch (Exception e) {
                    logger.warn("Failed to load Saved Super Object!", e);
                }
            }
        }
    }

    private boolean loadEconomyService() {
        economyService = game.getServiceManager().provide(EconomyService.class);
        if (economyService.isPresent()) {
            logger.info("Economy Service has been found!");
            return true;
        }
        logger.warn("Economy Service is not found!");
        return false;
    }

    private boolean loadHologramsService() {
        try {
            hologramsService = game.getServiceManager().provide(HologramsService.class);
            if (hologramsService.isPresent()) {
                logger.info("Holograms Service has been found!");
                return true;
            }
        } catch (NoClassDefFoundError ignored) {}
        logger.warn("Holograms Service is not found!");
        return false;
    }

    private boolean loadPlaceholderService() {
        try {
            placeholderService = game.getServiceManager().provide(PlaceholderService.class);
            if (placeholderService.isPresent()) {
                logger.info("Placeholder Service has been found!");
                return true;
            }
        } catch (NoClassDefFoundError ignored) {}
        logger.warn("Placeholder Service is not found!");
        return false;
    }

    private boolean loadChunkTicketManager() {
        chunkTicketManager = game.getServiceManager().provide(ChunkTicketManager.class);
        if (chunkTicketManager.isPresent()) {
            logger.info("Chunk Ticket Manager has been found!");
            return true;
        }
        logger.warn("Chunk Ticket Manager is not found! It is not recommended to use holograms when Chunk Ticket Manager is not present, because there is no guarantee of their successful removal.");
        return false;
    }

    public Optional<EconomyService> getEconomyService() {
        return economyService;
    }

    public Optional<HologramsService> getHologramsService() {
        return hologramsService;
    }

    public Optional<PlaceholderService> getPlaceholderService() {
        return placeholderService;
    }

    public Optional<ChunkTicketManager> getChunkTicketManager() {
        return chunkTicketManager;
    }

    public SuperObjectService getSuperObjectService() {
        return superObjectService;
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

    public Config getSavedSuperObjectsConfig() {
        return savedSuperObjectsConfig;
    }

    public Config getSavedItemsConfig() {
        return savedItemsConfig;
    }

    @Override
    public Language getLanguage() {
        return language;
    }

    public boolean isLogRegisteredCategories() {
        return logRegisteredCategories;
    }

    public boolean isLogRegisteredIdentifiers() {
        return logRegisteredIdentifiers;
    }

    public boolean isLogLoadedSavedSuperObjects() {
        return logLoadedSavedSuperObjects;
    }
}
