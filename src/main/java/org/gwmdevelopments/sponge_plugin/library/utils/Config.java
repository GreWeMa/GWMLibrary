package org.gwmdevelopments.sponge_plugin.library.utils;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.asset.AssetManager;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Config {

    public static final int DEFAULT_AUTO_SAVE_INTERVAL = 600;

    private SpongePlugin plugin;
    private String name;
    private File file;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode node;

    public Config(SpongePlugin plugin, String name, boolean auto_save) {
        this(plugin, name, auto_save, DEFAULT_AUTO_SAVE_INTERVAL);
    }

    public Config(SpongePlugin plugin, String name, boolean auto_save, int auto_save_interval) {
        this.plugin = plugin;
        this.name = name;
        try {
            file = new File(plugin.getConfigDirectory(), name);
            if (!file.exists()) {
                AssetManager assetManager = Sponge.getAssetManager();
                Optional<Asset> optionalAsset = assetManager.getAsset(plugin, name);
                if (optionalAsset.isPresent()) {
                    Asset asset = optionalAsset.get();
                    asset.copyToFile(file.toPath());
                } else {
                    plugin.getLogger().warn("Asset \"" + name + "\" not found!");
                    file.createNewFile();
                }
            }
            loader = HoconConfigurationLoader.builder().setFile(file).build();
            node = loader.load();
            Sponge.getScheduler().createTaskBuilder().delayTicks(1).execute(this::save).
                    submit(plugin);
            if (auto_save) {
                Sponge.getScheduler().createTaskBuilder().async().execute(this::save).
                        interval(auto_save_interval, TimeUnit.SECONDS).submit(plugin);
            }
        } catch (Exception e) {
            plugin.getLogger().warn("Failed to initialize config \"" + name + "\"!", e);
        }
    }

    public String getName() {
        return name;
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
            plugin.getLogger().debug("Config \"" + name + "\" successfully saved!");
        } catch (Exception e) {
            plugin.getLogger().warn("Fail saving config \"" + name + "\"!", e);
        }
    }

    public void reload() {
        try {
            node = loader.load();
            plugin.getLogger().debug("Config \"" + name + "\" successfully reloaded!");
        } catch (Exception e) {
            plugin.getLogger().warn("Fail reloading config \"" + name + "\"!", e);
        }
    }
}
