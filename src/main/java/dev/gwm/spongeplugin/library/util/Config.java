package dev.gwm.spongeplugin.library.util;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Color;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public final class Config {

    private final SpongePlugin plugin;
    private final File file;
    private final boolean autoSave;
    private final int autoSaveInterval;
    private final Optional<Supplier<BufferedReader>> source;
    private final ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode node;
    private ScheduledFuture<?> scheduledFuture = null;

    private Config(SpongePlugin plugin, File file, boolean autoSave, int autoSaveInterval, Optional<Supplier<BufferedReader>> source) {
        this.plugin = plugin;
        this.file = file;
        this.autoSave = autoSave;
        this.autoSaveInterval = autoSaveInterval;
        this.source = source;
        try {
            if (!file.exists()) {
                copyDefaults(file, source);
            }
            loader = HoconConfigurationLoader.builder().setFile(file).build();
            node = loader.load(getDefaultConfigurationOptions());
            if (autoSave) {
                scheduleAutoSave();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize config \"" + file.getAbsolutePath() + "\"!", e);
        }
    }

    public void save() {
        try {
            loader.save(node);
            plugin.getLogger().debug("Config \"" + file.getAbsolutePath() + "\" successfully saved!");
        } catch (Exception e) {
            plugin.getLogger().warn("Failed to save config \"" + file.getAbsolutePath() + "\"!", e);
        }
    }

    public void reload() {
        try {
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false);
            }
            if (!file.exists()) {
                copyDefaults(file, source);
            }
            node = loader.load(getDefaultConfigurationOptions());
            plugin.getLogger().debug("Config \"" + file.getAbsolutePath() + "\" successfully reloaded!");
            if (autoSave) {
                scheduleAutoSave();
            }
        } catch (Exception e) {
            plugin.getLogger().warn("Failed to reload config \"" + file.getAbsolutePath() + "\"!", e);
        }
    }

    private void scheduleAutoSave() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
        scheduledFuture = Executors.newScheduledThreadPool(1).
                scheduleWithFixedDelay(this::save, autoSaveInterval, autoSaveInterval, TimeUnit.SECONDS);
    }

    private static void copyDefaults(File file, Optional<Supplier<BufferedReader>> source) throws IOException {
        if (!file.createNewFile()) {
            throw new RuntimeException("Failed to create config file \"" + file.getAbsolutePath() + "\"!");
        }
        //Rewrite using transferTo
        //https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/Reader.html#transferTo(java.io.Writer)
        source.ifPresent(callable -> {
            try (BufferedReader reader = callable.get();
                 BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
                while (reader.ready()) {
                    writer.write(reader.read());
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to copy the source!", e);
            }
        });
    }

    private TypeSerializerCollection getDefaultTypeSerializers() {
        TypeSerializerCollection serializers = TypeSerializers.getDefaultSerializers().newChild();
        serializers.register(TypeToken.of(Text.class), new TextSerializer());
        serializers.register(TypeToken.of(Color.class), new ColorHexSerializer());
        return serializers;
    }

    private ConfigurationOptions getDefaultConfigurationOptions() {
        return ConfigurationOptions.defaults().setSerializers(getDefaultTypeSerializers());
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
        return getNode().getNode(objects);
    }

    public static class Builder {

        private static final int DEFAULT_AUTO_SAVE_INTERVAL = 600;

        private final SpongePlugin plugin;
        private final File file;
        private boolean autoSave = false;
        private int autoSaveInterval = DEFAULT_AUTO_SAVE_INTERVAL;
        private Optional<Supplier<BufferedReader>> defaults = Optional.empty();

        public Builder(SpongePlugin plugin, File file) {
            this.plugin = plugin;
            this.file = file;
        }

        public Config build() {
            return new Config(plugin, file, autoSave, autoSaveInterval, defaults);
        }

        public Builder setAutoSave(boolean autoSave) {
            this.autoSave = autoSave;
            return this;
        }

        public Builder setAutoSaveInterval(int autoSaveInterval) {
            this.autoSaveInterval = autoSaveInterval;
            return this;
        }

        public Builder setDefaults(Optional<Supplier<BufferedReader>> defaults) {
            this.defaults = defaults;
            return this;
        }

        public Builder loadDefaults(String name) {
            String path = "/assets/" + plugin.getContainer().getId() + "/" + name;
            this.defaults = Optional.of(() ->
                    new BufferedReader(new InputStreamReader(plugin.getClass().getResourceAsStream(path), StandardCharsets.UTF_8)));
            return this;
        }
    }
}
