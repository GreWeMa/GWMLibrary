package ua.gwm.sponge_plugin.library.utils;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.gson.Gson;
import de.randombyte.holograms.api.HologramsService;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import ua.gwm.sponge_plugin.library.GWMLibrary;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class LibraryUtils {

    public static final Gson GSON = new Gson();

    public static int getRandomIntLevel(int min, int max) {
        Random random = new Random();
        while (random.nextBoolean() && min < max) {
            min++;
        }
        return min;
    }

    public static Vector3i parseVector3i(ConfigurationNode node) {
        ConfigurationNode x_node = node.getNode("X");
        ConfigurationNode y_node = node.getNode("Y");
        ConfigurationNode z_node = node.getNode("Z");
        if (x_node.isVirtual()) {
            throw new RuntimeException("X node does not exist!");
        }
        if (y_node.isVirtual()) {
            throw new RuntimeException("Y node does not exist!");
        }
        if (z_node.isVirtual()) {
            throw new RuntimeException("Z node does not exist!");
        }
        int x = x_node.getInt();
        int y = y_node.getInt();
        int z = z_node.getInt();
        return new Vector3i(x, y, z);
    }

    public static Vector3d parseVector3d(ConfigurationNode node) {
        ConfigurationNode x_node = node.getNode("X");
        ConfigurationNode y_node = node.getNode("Y");
        ConfigurationNode z_node = node.getNode("Z");
        if (x_node.isVirtual()) {
            throw new RuntimeException("X node does not exist!");
        }
        if (y_node.isVirtual()) {
            throw new RuntimeException("Y node does not exist!");
        }
        if (z_node.isVirtual()) {
            throw new RuntimeException("Z node does not exist!");
        }
        double x = x_node.getDouble();
        double y = y_node.getDouble();
        double z = z_node.getDouble();
        return new Vector3d(x, y, z);
    }

    public static Location<World> parseLocation(ConfigurationNode node) {
        ConfigurationNode x_node = node.getNode("X");
        ConfigurationNode y_node = node.getNode("Y");
        ConfigurationNode z_node = node.getNode("Z");
        ConfigurationNode world_node = node.getNode("WORLD_NAME");
        if (x_node.isVirtual()) {
            throw new RuntimeException("X node does not exist!");
        }
        if (y_node.isVirtual()) {
            throw new RuntimeException("Y node does not exist!");
        }
        if (z_node.isVirtual()) {
            throw new RuntimeException("Z node does not exist!");
        }
        if (world_node.isVirtual()) {
            throw new RuntimeException("WORLD_NAME node does not exist!");
        }
        double x = x_node.getDouble();
        double y = y_node.getDouble();
        double z = z_node.getDouble();
        String world_name = world_node.getString();
        Optional<World> optional_world = Sponge.getServer().getWorld(world_name);
        if (!optional_world.isPresent()) {
            throw new RuntimeException("World \"" + world_name + "\" does not exist!");
        }
        World world = optional_world.get();
        return new Location<World>(world, x, y, z);
    }

    public static Optional<List<HologramsService.Hologram>> tryCreateHolograms(
            Location<World> location, Optional<List<Text>> optional_text_list) {
        return tryCreateHolograms(location, optional_text_list, new Vector3d(), 0.2);
    }

    public static Optional<List<HologramsService.Hologram>> tryCreateHolograms(
            Location<World> location, Optional<List<Text>> optional_text_list,
            Vector3d hologram_offset, double multiline_holograms_distance) {
        if (!optional_text_list.isPresent()) {
            return Optional.empty();
        }
        List<Text> text_list = optional_text_list.get();
        Optional<HologramsService> optional_holograms_service = GWMLibrary.getInstance().getHologramsService();
        if (!optional_holograms_service.isPresent()) {
            GWMLibrary.getInstance().getLogger().warn("Unable to create hologram, Holograms Service not found!");
            return Optional.empty();
        }
        HologramsService holograms_service = optional_holograms_service.get();
        location.getExtent().loadChunk(location.getChunkPosition(), true);
        Optional<List<HologramsService.Hologram>> optional_hologram = holograms_service.
                createMultilineHologram(location.add(hologram_offset), text_list, multiline_holograms_distance);
        if (!optional_hologram.isPresent()) {
            GWMLibrary.getInstance().getLogger().warn("Holograms Service found, but hologram can not be created! :-(");
            return Optional.empty();
        }
        return optional_hologram;
    }
}
