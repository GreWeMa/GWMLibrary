package dev.gwm.spongeplugin.library.utils;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Color;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Config {

    public static final int DEFAULT_AUTO_SAVE_INTERVAL = 600;

    private final SpongePlugin plugin;
    private final File file;
    private final ConfigurationOptions options;
    private final ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode node;

    public Config(SpongePlugin plugin, File file) {
        this(plugin, file, Optional.empty(), false, false);
    }

    public Config(SpongePlugin plugin, File file, Optional<Asset> asset, boolean create, boolean autoSave) {
        this(plugin, file, asset, create, autoSave, DEFAULT_AUTO_SAVE_INTERVAL);
    }

    public Config(SpongePlugin plugin, File file, Optional<Asset> asset, boolean create, boolean autoSave, int autoSaveInterval) {
        this.plugin = plugin;
        this.file = file;
        try {
            if (create && !file.exists()) {
                if (asset.isPresent()) {
                    asset.get().copyToFile(file.toPath());
                } else if (!file.createNewFile()) {
                    plugin.getLogger().warn("Failed to create config file \"" + file.getAbsolutePath() + "\"!");
                }
            }
            TypeSerializerCollection serializers = TypeSerializers.getDefaultSerializers().newChild();
            serializers.registerType(TypeToken.of(Text.class), new TextSerializer());
            serializers.registerType(TypeToken.of(Color.class), new ColorHexSerializer());
            options = ConfigurationOptions.defaults().setSerializers(serializers);
            loader = HoconConfigurationLoader.builder().setFile(file).build();
            node = loader.load(options);
            if (autoSave) {
                Sponge.getScheduler().createTaskBuilder().async().execute(this::save).
                        interval(autoSaveInterval, TimeUnit.SECONDS).submit(plugin);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize config \"" + file.getAbsolutePath() + "\"!", e);
        }
    }

    public File getFile() {
        return file;
    }

    public ConfigurationLoader<CommentedConfigurationNode> getLoader() {
        return loader;
    }

    public ConfigurationNode getNode() {
        return node;
    }

    public ConfigurationNode getNode(Object... objects) {
        return node.getNode(objects);
    }

    public void save() {
        try {
            loader.save(node);
            plugin.getLogger().debug("Config \"" + file.getAbsolutePath() + "\" successfully saved!");
        } catch (Exception e) {
            plugin.getLogger().warn("Fail saving config \"" + file.getAbsolutePath() + "\"!", e);
        }
    }

    public void reload() {
        try {
            node = loader.load(options);
            plugin.getLogger().debug("Config \"" + file.getAbsolutePath() + "\" successfully reloaded!");
        } catch (Exception e) {
            plugin.getLogger().warn("Fail reloading config \"" + file.getAbsolutePath() + "\"!", e);
        }
    }
}
