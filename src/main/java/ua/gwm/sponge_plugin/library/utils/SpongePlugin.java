package ua.gwm.sponge_plugin.library.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;

public abstract class SpongePlugin {

    public void checkUpdates() {
        Sponge.getScheduler().createTaskBuilder().async().execute(() -> {
            try {
                InputStreamReader reader = new InputStreamReader(new URL("https://ore.spongepowered.org/api/projects/" + getContainer().getId()).openStream());
                JsonObject object = new Gson().fromJson(reader, JsonObject.class);
                Version ore_version = Version.parse(object.get("recommended").getAsJsonObject().get("name").getAsJsonPrimitive().getAsString());
                if (ore_version.compareTo(getVersion()) > 0) {
                    getLogger().warn("New version (" + ore_version.toString() + ") available on Ore!");
                }
            } catch (Exception e) {
                getLogger().warn("Exception checking plugin updates on Ore!", e);
            }
        }).submit(this);
    }

    public void save() {

    }

    public void reload() {

    }

    public abstract Version getVersion();

    public abstract Cause getCause();

    public abstract Logger getLogger();

    public abstract PluginContainer getContainer();

    public abstract File getConfigDirectory();

    public abstract Config getConfig();

    public abstract Config getLanguageConfig();

    public abstract Language getLanguage();
}
