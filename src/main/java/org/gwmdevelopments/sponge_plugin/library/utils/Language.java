package org.gwmdevelopments.sponge_plugin.library.utils;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Language {

    public static final String DEFAULT = "ERROR! Unable to get phrase \"%PATH%\"! Check your \"language.conf\"!";

    private final SpongePlugin plugin;

    public Language(SpongePlugin plugin) {
        this.plugin = plugin;
    }

    public boolean exists(String path) {
        return !plugin.getLanguageConfig().getNode(path).isVirtual();
    }

    public String getPhrase(String path, Pair<String, ?>... pairs) {
        ConfigurationNode node = plugin.getLanguageConfig().getNode(path.toUpperCase());
        try {
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

    public Text getText(String path, Pair<String, ?>... pairs) {
        try {
            return TextSerializers.FORMATTING_CODE.deserialize(getPhrase(path, pairs));
        } catch (Exception e) {
            plugin.getLogger().warn("Failed to get text \"" + path + "\" from language config!", e);
            return Text.builder(DEFAULT.replace("%PATH%", path)).color(TextColors.RED).build();
        }
    }

    public List<String> getPhraseList(String path, Pair<String, ?>... pairs) {
        ConfigurationNode node = plugin.getLanguageConfig().getNode(path.toUpperCase());
        try {
            List<String> list = node.getValue(new TypeToken<List<String>>(){}, new ArrayList<String>());
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
            List<String> list = new ArrayList<String>();
            list.add(DEFAULT.replace("%PATH%", path));
            return list;
        }
    }

    public List<Text> getTextList(String path, Pair<String, ?>... pairs) {
        try {
            return getPhraseList(path, pairs).stream().
                    map(TextSerializers.FORMATTING_CODE::deserialize).
                    collect(Collectors.toList());
        } catch (Exception e) {
            plugin.getLogger().warn("Failed to get text list \"" + path + "\" from language config!", e);
            List<Text> list = new ArrayList<Text>();
            list.add(Text.builder(DEFAULT.replace("%PATH%", path)).color(TextColors.RED).build());
            return list;
        }
    }
}
