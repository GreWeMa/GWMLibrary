package org.gwmdevelopments.sponge_plugin.library.utils;

import com.google.common.reflect.TypeToken;
import me.rojo8399.placeholderapi.PlaceholderService;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.gwmdevelopments.sponge_plugin.library.GWMLibrary;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.asset.AssetManager;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Language {

    public static final String DEFAULT = "ERROR! Unable to get phrase \"%PATH%\"! Check your \"language.conf\"!";

    private final SpongePlugin plugin;
    private Optional<ConfigurationNode> defaultNode = Optional.empty();

    public Language(SpongePlugin plugin) {
        this.plugin = plugin;
        try {
            AssetManager assetManager = Sponge.getAssetManager();
            Optional<Asset> optionalAsset = assetManager.getAsset(plugin, "language.conf");
            if (optionalAsset.isPresent()) {
                Asset asset = optionalAsset.get();
                defaultNode = Optional.of(HoconConfigurationLoader.builder().
                        setSource(() -> new BufferedReader(new InputStreamReader(asset.getUrl().openStream()))).
                        build().load());
            } else {
                plugin.getLogger().warn("Plugin \"" + plugin.getContainer().getId() + "\" does not have \"language.conf\" asset!");
            }
        } catch (Exception e) {
            plugin.getLogger().warn("Failed to initialize Language!", e);
        }
    }

    public boolean exists(String path) {
        return !plugin.getLanguageConfig().getNode(path).isVirtual();
    }

    public String getPhrase(String path, Pair<String, ?>... pairs) {
        try {
            ConfigurationNode node = getAndCheckNode(path);
            String phrase = node.getValue(TypeToken.of(String.class), DEFAULT.replace("%PATH%", path));
            for (Pair<String, ?> pair : pairs) {
                phrase = phrase.replace(pair.getKey(), pair.getValue().toString());
            }
            return phrase;
        } catch (Exception e) {
            plugin.getLogger().warn("Failed to get phrase \"" + path + "\" from language config!", e);
            return DEFAULT.replace("%PATH%", path);
        }
    }

    public Text getText(String path, Object source, Object observer, Pair<String, ?>... pairs) {
        try {
            Text text = TextSerializers.FORMATTING_CODE.deserialize(getPhrase(path, pairs));
            Optional<PlaceholderService> optionalPlaceholderService = GWMLibrary.getInstance().getPlaceholderService();
            if (optionalPlaceholderService.isPresent()) {
                text = optionalPlaceholderService.get().replacePlaceholders(text, source, observer);
            }
            return text;
        } catch (Exception e) {
            plugin.getLogger().warn("Failed to get text \"" + path + "\" from language config!", e);
            return Text.builder(DEFAULT.replace("%PATH%", path)).color(TextColors.RED).build();
        }
    }

    public List<String> getPhraseList(String path, Pair<String, ?>... pairs) {
        try {
            ConfigurationNode node = getAndCheckNode(path);
            List<String> list = node.getValue(new TypeToken<List<String>>(){}, new ArrayList<>());
            for (int i = 0; i < list.size(); i++) {
                String phrase = list.get(i);
                for (Pair<String, ?> pair : pairs) {
                    phrase = phrase.replace(pair.getKey(), pair.getValue().toString());
                }
                list.set(i, phrase);
            }
            return list;
        } catch (Exception e) {
            plugin.getLogger().warn("Failed to get phrase list \"" + path + "\" from language config!", e);
            List<String> list = new ArrayList<>();
            list.add(DEFAULT.replace("%PATH%", path));
            return list;
        }
    }

    public List<Text> getTextList(String path, Object source, Object observer, Pair<String, ?>... pairs) {
        try {
            Stream<Text> stream = getPhraseList(path, pairs).stream().
                    map(TextSerializers.FORMATTING_CODE::deserialize);
            Optional<PlaceholderService> optionalPlaceholderService = GWMLibrary.getInstance().getPlaceholderService();
            if (optionalPlaceholderService.isPresent()) {
                stream = stream.map(text -> optionalPlaceholderService.get().replacePlaceholders(text, source, observer));
            }
            return stream.collect(Collectors.toList());
        } catch (Exception e) {
            plugin.getLogger().warn("Failed to get text list \"" + path + "\" from language config!", e);
            List<Text> list = new ArrayList<>();
            list.add(Text.builder(DEFAULT.replace("%PATH%", path)).color(TextColors.RED).build());
            return list;
        }
    }

    private ConfigurationNode getAndCheckNode(String path) {
        ConfigurationNode node = plugin.getLanguageConfig().getNode(path.toUpperCase());
        if (node.isVirtual()) {
            if (defaultNode.isPresent()) {
                node = defaultNode.get().getNode(path);
                if (node.isVirtual()) {
                    throw new IllegalArgumentException("Path \"" + path + "\" does exist in both Language config and embedded Language config!");
                } else {
                    plugin.getLogger().warn("Path \"" + path + "\" does not exist in Language config! Using embedded Language config.");
                    return node;
                }
            } else {
                throw new IllegalArgumentException("Path \"" + path + "\" does not exist!");
            }
        } else {
            return node;
        }
    }
}
