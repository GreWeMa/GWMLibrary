package org.gwmdevelopments.sponge_plugin.library.utils;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.gson.Gson;
import de.randombyte.holograms.api.HologramsService;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.gwmdevelopments.sponge_plugin.library.GWMLibrary;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class GWMLibraryUtils {

    public static final Gson GSON = new Gson();

    public static int getRandomIntLevel(int min, int max) {
        Random random = new Random();
        while (random.nextBoolean() && min < max) {
            min++;
        }
        return min;
    }

    public static Vector3i parseVector3i(ConfigurationNode node) {
        ConfigurationNode xNode = node.getNode("X");
        ConfigurationNode yNode = node.getNode("Y");
        ConfigurationNode zNode = node.getNode("Z");
        if (xNode.isVirtual()) {
            throw new RuntimeException("X node does not exist!");
        }
        if (yNode.isVirtual()) {
            throw new RuntimeException("Y node does not exist!");
        }
        if (zNode.isVirtual()) {
            throw new RuntimeException("Z node does not exist!");
        }
        int x = xNode.getInt();
        int y = yNode.getInt();
        int z = zNode.getInt();
        return new Vector3i(x, y, z);
    }

    public static Vector3d parseVector3d(ConfigurationNode node) {
        ConfigurationNode xNode = node.getNode("X");
        ConfigurationNode yNode = node.getNode("Y");
        ConfigurationNode zNode = node.getNode("Z");
        if (xNode.isVirtual()) {
            throw new RuntimeException("X node does not exist!");
        }
        if (yNode.isVirtual()) {
            throw new RuntimeException("Y node does not exist!");
        }
        if (zNode.isVirtual()) {
            throw new RuntimeException("Z node does not exist!");
        }
        double x = xNode.getDouble();
        double y = yNode.getDouble();
        double z = zNode.getDouble();
        return new Vector3d(x, y, z);
    }

    public static Location<World> parseLocation(ConfigurationNode node) {
        ConfigurationNode xNode = node.getNode("X");
        ConfigurationNode yNode = node.getNode("Y");
        ConfigurationNode zNode = node.getNode("Z");
        ConfigurationNode worldNode = node.getNode("WORLD_NAME");
        if (xNode.isVirtual()) {
            throw new RuntimeException("X node does not exist!");
        }
        if (yNode.isVirtual()) {
            throw new RuntimeException("Y node does not exist!");
        }
        if (zNode.isVirtual()) {
            throw new RuntimeException("Z node does not exist!");
        }
        if (worldNode.isVirtual()) {
            throw new RuntimeException("WORLD_NAME node does not exist!");
        }
        double x = xNode.getDouble();
        double y = yNode.getDouble();
        double z = zNode.getDouble();
        String worldName = worldNode.getString();
        Optional<World> optionalWorld = Sponge.getServer().getWorld(worldName);
        if (!optionalWorld.isPresent()) {
            throw new RuntimeException("World \"" + worldName + "\" does not exist!");
        }
        World world = optionalWorld.get();
        return new Location<>(world, x, y, z);
    }

    public static Optional<List<HologramsService.Hologram>> tryCreateHolograms(
            Location<World> location, Optional<List<Text>> optionalTextList) {
        return tryCreateHolograms(location, optionalTextList, new Vector3d(), 0.2);
    }

    public static Optional<List<HologramsService.Hologram>> tryCreateHolograms(
            Location<World> location, Optional<List<Text>> optionalTextList,
            Vector3d hologramOffset, double multilineHologramsDistance) {
        if (!optionalTextList.isPresent()) {
            return Optional.empty();
        }
        List<Text> textList = optionalTextList.get();
        Optional<HologramsService> optionalHologramsService = GWMLibrary.getInstance().getHologramsService();
        if (!optionalHologramsService.isPresent()) {
            GWMLibrary.getInstance().getLogger().warn("Failed to create hologram, Holograms Service not found!");
            return Optional.empty();
        }
        HologramsService hologramsService = optionalHologramsService.get();
        location.getExtent().loadChunk(location.getChunkPosition(), true);
        Optional<List<HologramsService.Hologram>> optionalHologram = hologramsService.
                createMultilineHologram(location.add(hologramOffset), textList, multilineHologramsDistance);
        if (!optionalHologram.isPresent()) {
            GWMLibrary.getInstance().getLogger().warn("Holograms Service found, but hologram can not be created! :-(");
            return Optional.empty();
        }
        return optionalHologram;
    }
}
