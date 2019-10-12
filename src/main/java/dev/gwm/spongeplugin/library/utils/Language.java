package dev.gwm.spongeplugin.library.utils;

import com.google.common.reflect.TypeToken;
import dev.gwm.spongeplugin.library.GWMLibrary;
import me.rojo8399.placeholderapi.PlaceholderService;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.asset.AssetManager;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Language {

    private static final String DEFAULT = "ERROR! Unable to get phrase %s! Check your language.conf!";

    private static final String KEY = "%%%s%%";

    private final SpongePlugin plugin;
    private final Optional<ConfigurationNode> defaultNode;

    public Language(SpongePlugin plugin) {
        this.plugin = plugin;
        try {
            AssetManager assetManager = Sponge.getAssetManager();
            Optional<Asset> optionalAsset = plugin.getDefaultTranslation(assetManager);
            if (optionalAsset.isPresent()) {
                defaultNode = Optional.of(HoconConfigurationLoader.builder().
                        setSource(() -> new BufferedReader(new InputStreamReader(optionalAsset.get().getUrl().openStream()))).
                        build().
                        load());
            } else {
                defaultNode = Optional.empty();
                GWMLibrary.getInstance().getLogger().warn("Plugin \"" + plugin.getContainer().getId() + "\" does not have default translation asset!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Language!", e);
        }
    }

    public boolean exists(String path) {
        return !plugin.getLanguageConfig().getNode(path).isVirtual();
    }

    //(Simple) full
    public List<String> getSimpleTranslation(String path, Iterable<Map.Entry<String, ?>> entries) {
        try {
            return getAndCheckNode(path).
                    getList(TypeToken.of(String.class)).
                    stream().
                    map(string -> {
                        for (Map.Entry<String, ?> next : entries) {
                            string = string.replace(String.format(KEY, next.getKey()), next.getValue().toString());
                        }
                        return string;
                    }).
                    collect(Collectors.toList());
        } catch (Exception e) {
            plugin.getLogger().warn("Failed to get phrase list at path \"" + path + "\"!", e);
            return Collections.singletonList(String.format(DEFAULT, path));
        }
    }

    //(Simple) no parameters
    public List<String> getSimpleTranslation(String path) {
        return getSimpleTranslation(path, Collections.emptySet());
    }

    //(Default) full
    public List<Text> getTranslation(String path, Iterable<Map.Entry<String, ?>> entries, Object source, Object observer) {
        try {
            Stream<Text> stream = getSimpleTranslation(path, entries).stream().
                    map(TextSerializers.FORMATTING_CODE::deserialize);
            Optional<PlaceholderService> optionalPlaceholderService = GWMLibrary.getInstance().getPlaceholderService();
            if (optionalPlaceholderService.isPresent()) {
                stream = stream.map(text -> optionalPlaceholderService.get().replacePlaceholders(text, source, observer));
            }
            return stream.collect(Collectors.toList());
        } catch (Exception e) {
            plugin.getLogger().warn("Failed to get text list at path \"" + path + "\"!", e);
            return Collections.singletonList(Text.builder(String.format(DEFAULT, path)).
                    color(TextColors.RED).
                    build());
        }
    }

    //(Default) no observer
    public List<Text> getTranslation(String path, Iterable<Map.Entry<String, ?>> entries, Object source) {
        return getTranslation(path, entries, source, null);
    }

    //(Default) no source and observer
    public List<Text> getTranslation(String path, Iterable<Map.Entry<String, ?>> entries) {
        return getTranslation(path, entries, null, null);
    }

    //(Default) no parameters
    public List<Text> getTranslation(String path, Object source, Object observer) {
        return getTranslation(path, Collections.emptySet(), source, observer);
    }

    //(Default) no parameters and observer
    public List<Text> getTranslation(String path, Object source) {
        return getTranslation(path, Collections.emptySet(), source, null);
    }

    //(Default) no parameters, source and observer
    public List<Text> getTranslation(String path) {
        return getTranslation(path, Collections.emptySet(), null, null);
    }

    //(Default) single parameter
    public List<Text> getTranslation(String path, Map.Entry<String, ?> entry, Object source, Object observer) {
        return getTranslation(path, Collections.singleton(entry), source, observer);
    }

    //(Default) single parameter, no observer
    public List<Text> getTranslation(String path, Map.Entry<String, ?> entry, Object source) {
        return getTranslation(path, Collections.singleton(entry), source, null);
    }

    //(Default) single parameter, no source and observer
    public List<Text> getTranslation(String path, Map.Entry<String, ?> entry) {
        return getTranslation(path, Collections.singleton(entry), null, null);
    }

    private ConfigurationNode getAndCheckNode(String path) {
        ConfigurationNode node = plugin.getLanguageConfig().getNode(path);
        if (node.isVirtual()) {
            if (defaultNode.isPresent()) {
                node = defaultNode.get().getNode(path);
                if (node.isVirtual()) {
                    throw new IllegalArgumentException("Path \"" + path + "\" does not exist in the config!");
                } else {
                    plugin.getLogger().warn("Path \"" + path + "\" does not exist in the config! Using the embedded config!");
                    return node;
                }
            } else {
                throw new IllegalArgumentException("Path \"" + path + "\" does not exist in the config and there is no embedded config!");
            }
        } else {
            return node;
        }
    }
}
