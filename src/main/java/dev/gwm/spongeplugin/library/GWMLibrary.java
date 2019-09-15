package dev.gwm.spongeplugin.library;

import de.randombyte.holograms.api.HologramsService;
import dev.gwm.spongeplugin.library.event.*;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.superobject.randommanager.AbsoluteRandomManager;
import dev.gwm.spongeplugin.library.superobject.randommanager.LevelRandomManager;
import dev.gwm.spongeplugin.library.superobject.randommanager.WeightRandomManager;
import dev.gwm.spongeplugin.library.utils.*;
import me.rojo8399.placeholderapi.PlaceholderService;
import ninja.leaping.configurate.ConfigurationNode;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.AssetManager;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.*;
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
        version = "2.0",
        description = "Library with Super Objects and other utilities",
        dependencies = {
                @Dependency(id = "holograms", optional = true),
                @Dependency(id = "placeholderapi", optional = true)
        },
        authors = {"GWM"/* My contacts:
                         * E-Mail(nazark@tutanota.com),
                         * Telegram(@grewema),
                         * Discord(GWM#2192)*/})
public final class GWMLibrary extends SpongePlugin {

    public static final Version VERSION = new Version(null, 2, 0);

    private static GWMLibrary instance = null;

    public static GWMLibrary getInstance() {
        if (instance == null) {
            throw new IllegalStateException("GWMLibrary is not initialized!");
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
    private Config savedSuperObjectsConfig;
    private Config savedItemsConfig;

    private Language language;

    private Optional<EconomyService> economyService = Optional.empty();
    private Optional<HologramsService> hologramsService = Optional.empty();
    private Optional<PlaceholderService> placeholderService = Optional.empty();
    private Optional<ChunkTicketManager> chunkTicketManager = Optional.empty();
    private SuperObjectsServiceImpl superObjectsService;

    private boolean checkUpdates = true;
    private boolean logRegisteredCategories = true;
    private boolean logRegisteredIdentifiers = true;
    private boolean logLoadedSavedSuperObjects = true;

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
        AssetManager assetManager = Sponge.getAssetManager();
        config = new Config(this, new File(configDirectory, "config.conf"),
                assetManager.getAsset(this, "config.conf"), true, false);
        languageConfig = new Config(this, new File(configDirectory, "language.conf"),
                assetManager.getAsset(this, "translations/en_us.conf"), true, false);
        savedSuperObjectsConfig = new Config(this, new File(configDirectory, "saved_super_objects.conf"),
                assetManager.getAsset(this, "saved_super_objects.conf"), true, false);
        savedItemsConfig = new Config(this, new File(configDirectory, "saved_items.conf"),
                assetManager.getAsset(this, "saved_items.conf"), true, false);
        loadConfigValues();
        language = new Language(this);
        if (checkUpdates) {
            checkUpdates();
        }
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
        Sponge.getEventManager().post(new SuperObjectsRegistrationEventImpl());
        logger.info("StartingServer completed!");
    }

    @Listener
    public void reloadListener(GameReloadEvent event) {
        reload();
        logger.info("Reload completed!");
    }

    @Listener
    public void onStopping(GameStoppingServerEvent event) {
        superObjectsService.shutdownSavedSuperObjects();
        save();
        logger.info("Stopping completed!");
    }

    @Override
    public void reload() {
        config.reload();
        languageConfig.reload();
        savedSuperObjectsConfig.reload();
        savedItemsConfig.reload();
        loadConfigValues();
        if (checkUpdates) {
            checkUpdates();
        }
        superObjectsService.shutdownSavedSuperObjects();
        loadSavedSuperObjects();
        logger.info("Plugin has been reloaded.");
    }

    private void loadConfigValues() {
        try {
            ConfigurationNode checkUpdatesNode = config.getNode("CHECK_UPDATES");
            ConfigurationNode logRegisteredCategoriesNode = config.getNode("LOG_REGISTERED_CATEGORIES");
            ConfigurationNode logRegisteredIdentifiersNode = config.getNode("LOG_REGISTERED_IDENTIFIERS");
            ConfigurationNode logLoadedSavedSuperObjectsNode = config.getNode("LOG_LOADED_SAVED_SUPER_OBJECTS");
            checkUpdates = checkUpdatesNode.getBoolean(true);
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
        Sponge.getEventManager().post(categoriesRegistrationEvent);
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
        Sponge.getEventManager().post(identifiersRegistrationEvent);
        Map<SuperObjectIdentifier, Class<? extends SuperObject>> classes = identifiersRegistrationEvent.getClasses();
        if (logRegisteredIdentifiers) {
            classes.forEach((identifier, value) -> {
                String category = identifier.getCategory().getName();
                String type = identifier.getType();
                logger.info("Registered identifier for \"" + value.getCanonicalName() + "\" with category \"" + category + "\" and type \"" + type + "\"");
            });
        }
        superObjectsService = new SuperObjectsServiceImpl(categories, classes);
        Sponge.getServiceManager().setProvider(this, SuperObjectsService.class, superObjectsService);
    }

    private void loadSavedSuperObjects() {
        ConfigurationNode savedSuperObjectsNode = savedSuperObjectsConfig.getNode("SAVED_SUPER_OBJECTS");
        if (!savedSuperObjectsNode.isVirtual()) {
            for (ConfigurationNode savedSuperObjectNode : savedSuperObjectsNode.getChildrenList()) {
                try {
                    SuperObject superObject = superObjectsService.load(savedSuperObjectNode, true);
                    if (logLoadedSavedSuperObjects) {
                        String category = superObject.category().getName();
                        String type = superObject.type();
                        String id = superObject.id().get();
                        logger.info("Loaded Saved Super Object with category \"" + category + "\", type \"" + type + "\" and id \"" + id + "\"");
                    }
                } catch (Exception e) {
                    logger.warn("Failed to load Saved Super Object!", e);
                }
            }
        }
    }

    private boolean loadEconomyService() {
        economyService = Sponge.getServiceManager().provide(EconomyService.class);
        if (economyService.isPresent()) {
            logger.info("Economy Service has been found!");
            return true;
        }
        logger.warn("Economy Service is not found!");
        return false;
    }

    private boolean loadHologramsService() {
        try {
            hologramsService = Sponge.getServiceManager().provide(HologramsService.class);
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
            placeholderService = Sponge.getServiceManager().provide(PlaceholderService.class);
            if (placeholderService.isPresent()) {
                logger.info("Placeholder Service has been found!");
                return true;
            }
        } catch (NoClassDefFoundError ignored) {}
        logger.warn("Placeholder Service is not found!");
        return false;
    }

    private boolean loadChunkTicketManager() {
        chunkTicketManager = Sponge.getServiceManager().provide(ChunkTicketManager.class);
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

    public SuperObjectsService getSuperObjectsService() {
        return superObjectsService;
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

    public boolean isCheckUpdates() {
        return checkUpdates;
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
