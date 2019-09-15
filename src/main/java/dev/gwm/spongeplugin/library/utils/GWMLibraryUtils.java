package dev.gwm.spongeplugin.library.utils;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import de.randombyte.holograms.api.HologramsService;
import dev.gwm.spongeplugin.library.GWMLibrary;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.ChunkTicketManager;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class GWMLibraryUtils {

    public static final Pattern ID_PATTERN = Pattern.compile("[a-z]([-_]?[a-z0-9])*");

    private static Map<World, ChunkTicketManager.LoadingTicket> loadingTickets = new HashMap<>();

    private GWMLibraryUtils() {
    }

    public static Vector3i parseVector3i(ConfigurationNode node) {
        ConfigurationNode xNode = node.getNode("X");
        ConfigurationNode yNode = node.getNode("Y");
        ConfigurationNode zNode = node.getNode("Z");
        if (xNode.isVirtual()) {
            throw new IllegalArgumentException("X node does not exist!");
        }
        if (yNode.isVirtual()) {
            throw new IllegalArgumentException("Y node does not exist!");
        }
        if (zNode.isVirtual()) {
            throw new IllegalArgumentException("Z node does not exist!");
        }
        int x = xNode.getInt();
        int y = yNode.getInt();
        int z = zNode.getInt();
        return new Vector3i(x, y, z);
    }

    public static Vector3i parseVector3i(ConfigurationNode node, Vector3i def) {
        try {
            return parseVector3i(node);
        } catch (Exception e) {
            GWMLibrary.getInstance().getLogger().warn("Failed to parse Vector3i!", e);
            return def;
        }
    }

    public static Vector3d parseVector3d(ConfigurationNode node) {
        ConfigurationNode xNode = node.getNode("X");
        ConfigurationNode yNode = node.getNode("Y");
        ConfigurationNode zNode = node.getNode("Z");
        if (xNode.isVirtual()) {
            throw new IllegalArgumentException("X node does not exist!");
        }
        if (yNode.isVirtual()) {
            throw new IllegalArgumentException("Y node does not exist!");
        }
        if (zNode.isVirtual()) {
            throw new IllegalArgumentException("Z node does not exist!");
        }
        double x = xNode.getDouble();
        double y = yNode.getDouble();
        double z = zNode.getDouble();
        return new Vector3d(x, y, z);
    }

    public static Vector3d parseVector3d(ConfigurationNode node, Vector3d def) {
        try {
            return parseVector3d(node);
        } catch (Exception e) {
            GWMLibrary.getInstance().getLogger().warn("Failed to parse Vector3d!", e);
            return def;
        }
    }

    public static Location<World> parseLocation(ConfigurationNode node) {
        ConfigurationNode xNode = node.getNode("X");
        ConfigurationNode yNode = node.getNode("Y");
        ConfigurationNode zNode = node.getNode("Z");
        ConfigurationNode worldNode = node.getNode("WORLD_NAME");
        if (xNode.isVirtual()) {
            throw new IllegalArgumentException("X node does not exist!");
        }
        if (yNode.isVirtual()) {
            throw new IllegalArgumentException("Y node does not exist!");
        }
        if (zNode.isVirtual()) {
            throw new IllegalArgumentException("Z node does not exist!");
        }
        if (worldNode.isVirtual()) {
            throw new IllegalArgumentException("WORLD_NAME node does not exist!");
        }
        double x = xNode.getDouble();
        double y = yNode.getDouble();
        double z = zNode.getDouble();
        String worldName = worldNode.getString();
        Optional<World> optionalWorld = Sponge.getServer().getWorld(worldName);
        if (!optionalWorld.isPresent()) {
            throw new IllegalArgumentException("World \"" + worldName + "\" is not found!");
        }
        World world = optionalWorld.get();
        return new Location<>(world, x, y, z);
    }

    public static Location<World> parseLocation(ConfigurationNode node, Location<World> def) {
        try {
            return parseLocation(node);
        } catch (Exception e) {
            GWMLibrary.getInstance().getLogger().warn("Failed to parse Location!", e);
            return def;
        }
    }

    public static Location<World> parseBlockLocation(ConfigurationNode node) {
        ConfigurationNode xNode = node.getNode("X");
        ConfigurationNode yNode = node.getNode("Y");
        ConfigurationNode zNode = node.getNode("Z");
        ConfigurationNode worldNode = node.getNode("WORLD_NAME");
        if (xNode.isVirtual()) {
            throw new IllegalArgumentException("X node does not exist!");
        }
        if (yNode.isVirtual()) {
            throw new IllegalArgumentException("Y node does not exist!");
        }
        if (zNode.isVirtual()) {
            throw new IllegalArgumentException("Z node does not exist!");
        }
        if (worldNode.isVirtual()) {
            throw new IllegalArgumentException("WORLD_NAME node does not exist!");
        }
        int x = xNode.getInt();
        int y = yNode.getInt();
        int z = zNode.getInt();
        String worldName = worldNode.getString();
        Optional<World> optionalWorld = Sponge.getServer().getWorld(worldName);
        if (!optionalWorld.isPresent()) {
            throw new IllegalArgumentException("World \"" + worldName + "\" is not found!");
        }
        World world = optionalWorld.get();
        return new Location<>(world, x, y, z);
    }

    public static Location<World> parseBlockLocation(ConfigurationNode node, Location<World> def) {
        try {
            return parseBlockLocation(node);
        } catch (Exception e) {
            GWMLibrary.getInstance().getLogger().warn("Failed to parse block Location!", e);
            return def;
        }
    }

    public static HologramSettings parseHologramSettings(ConfigurationNode node,
                                                         Vector3d defaultOffset, double defaultMultilineDistance) {
        ConfigurationNode linesNode = node.getNode("LINES");
        ConfigurationNode offsetNode = node.getNode("OFFSET");
        ConfigurationNode multilineDistanceNode = node.getNode("MULTILINE_DISTANCE");
        if (linesNode.isVirtual()) {
            throw new IllegalArgumentException("LINES node does not exist!");
        }
        try {
            List<Text> lines = linesNode.getList(TypeToken.of(Text.class));
            Vector3d offset = offsetNode.isVirtual() ?
                    defaultOffset :
                    parseVector3d(offsetNode);
            double multilineDistance = multilineDistanceNode.isVirtual() ?
                    defaultMultilineDistance :
                    multilineDistanceNode.getDouble();
            return new HologramSettings(lines, offset, multilineDistance);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse hologram settings!", e);
        }
    }

    public static HologramSettings parseHologramSettings(ConfigurationNode node,
                                                         Vector3d defaultOffset, double defaultMultilineDistance,
                                                         HologramSettings def) {
        try {
            return parseHologramSettings(node, defaultOffset, defaultMultilineDistance);
        } catch (Exception e) {
            GWMLibrary.getInstance().getLogger().warn("Failed to parse hologram settings!", e);
            return def;
        }
    }

    public static Enchantment parseEnchantment(ConfigurationNode node) {
        ConfigurationNode enchantmentNode = node.getNode("ENCHANTMENT");
        ConfigurationNode levelNode = node.getNode("LEVEL");
        if (enchantmentNode.isVirtual()) {
            throw new IllegalArgumentException("ENCHANTMENT node does not exist!");
        }
        try {
            EnchantmentType type = enchantmentNode.getValue(TypeToken.of(EnchantmentType.class));
            int level = levelNode.getInt(1);
            return Enchantment.of(type, level);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse enchantment!", e);
        }
    }

    public static void writeEnchantment(Enchantment enchantment, ConfigurationNode node) {
        ConfigurationNode enchantmentNode = node.getNode("ENCHANTMENT");
        ConfigurationNode levelNode = node.getNode("LEVEL");
        enchantmentNode.setValue(enchantment.getType().getId());
        levelNode.setValue(enchantment.getLevel());
    }

    private static void writeEnchantments(Iterable<Enchantment> enchantments, ConfigurationNode node) {
        List<ConfigurationNode> enchantmentNodes = new ArrayList<>();
        enchantments.forEach(enchantment -> {
            ConfigurationNode enchantmentNode = SimpleConfigurationNode.root();
            writeEnchantment(enchantment, enchantmentNode);
            enchantmentNodes.add(enchantmentNode);
        });
        node.setValue(enchantmentNodes);
    }

    public static ItemStack parseItem(ConfigurationNode node) {
        try {
            ConfigurationNode itemTypeNode = node.getNode("ITEM_TYPE");
            ConfigurationNode quantityNode = node.getNode("QUANTITY");
            ConfigurationNode subIdNode = node.getNode("SUB_ID"); //To be removed in Sponge for 1.13
            ConfigurationNode nbtNode = node.getNode("NBT");
            ConfigurationNode durabilityNode = node.getNode("DURABILITY");
            ConfigurationNode displayNameNode = node.getNode("DISPLAY_NAME");
            ConfigurationNode loreNode = node.getNode("LORE");
            ConfigurationNode enchantmentsNode = node.getNode("ENCHANTMENTS");
            ConfigurationNode hideEnchantmentsNode = node.getNode("HIDE_ENCHANTMENTS");
            ConfigurationNode hideAttributesNode = node.getNode("HIDE_ATTRIBUTES");
            if (itemTypeNode.isVirtual()) {
                throw new IllegalArgumentException("ITEM_TYPE node does not exist!");
            }
            //Mega-shit-code start
            ConfigurationNode tempNode = SimpleConfigurationNode.root();
            tempNode.getNode("ItemType").setValue(itemTypeNode.getString());
            tempNode.getNode("UnsafeDamage").setValue(subIdNode.getInt(0));
            tempNode.getNode("Count").setValue(quantityNode.getInt(1));
            ItemStack item = tempNode.getValue(TypeToken.of(ItemStack.class));
            //Mega-shit-code end; Another not good code start
            if (!nbtNode.isVirtual()) {
                LinkedHashMap nbtMap = (LinkedHashMap) nbtNode.getValue();
                if (item.toContainer().get(DataQuery.of("UnsafeData")).isPresent()) {
                    Map unsafeDataMap = item.toContainer().getMap(DataQuery.of("UnsafeData")).get();
                    nbtMap.putAll(unsafeDataMap);
                }
                DataContainer container = item.toContainer().set(DataQuery.of("UnsafeData"), nbtMap);
                item = ItemStack.builder().fromContainer(container).build();
            }
            //Another not good code end
            if (!durabilityNode.isVirtual()) {
                int durability = durabilityNode.getInt();
                item.offer(Keys.ITEM_DURABILITY, durability);
            }
            if (!displayNameNode.isVirtual()) {
                Text displayName = displayNameNode.getValue(TypeToken.of(Text.class));
                item.offer(Keys.DISPLAY_NAME, displayName);
            }
            if (!loreNode.isVirtual()) {
                List<Text> lore = loreNode.getList(TypeToken.of(Text.class));
                item.offer(Keys.ITEM_LORE, lore);
            }
            if (!enchantmentsNode.isVirtual()) {
                List<Enchantment> itemEnchantments = new ArrayList<>();
                for (ConfigurationNode enchantment_node : enchantmentsNode.getChildrenList()) {
                    itemEnchantments.add(parseEnchantment(enchantment_node));
                }
                item.offer(Keys.ITEM_ENCHANTMENTS, itemEnchantments);
            }
            if (!hideEnchantmentsNode.isVirtual()) {
                item.offer(Keys.HIDE_ENCHANTMENTS, hideEnchantmentsNode.getBoolean());
            }
            if (!hideAttributesNode.isVirtual()) {
                item.offer(Keys.HIDE_ATTRIBUTES, hideAttributesNode.getBoolean());
            }
            return item;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse item!", e);
        }
    }

    public static void writeItemStack(ItemStack item, boolean saveNbt, ConfigurationNode node) {
        ConfigurationNode itemTypeNode = node.getNode("ITEM_TYPE");
        ConfigurationNode quantityNode = node.getNode("QUANTITY");
        ConfigurationNode subIdNode = node.getNode("SUB_ID"); //To be removed in Sponge for 1.13
        ConfigurationNode nbtNode = node.getNode("NBT");
        ConfigurationNode durabilityNode = node.getNode("DURABILITY");
        ConfigurationNode displayNameNode = node.getNode("DISPLAY_NAME");
        ConfigurationNode loreNode = node.getNode("LORE");
        ConfigurationNode enchantmentsNode = node.getNode("ENCHANTMENTS");
        ConfigurationNode hideEnchantmentsNode = node.getNode("HIDE_ENCHANTMENTS");
        ConfigurationNode hideAttributesNode = node.getNode("HIDE_ATTRIBUTES");
        DataContainer container = item.toContainer();
        itemTypeNode.setValue(item.getType().getId());
        quantityNode.setValue(item.getQuantity());
        container.getInt(DataQuery.of("UnsafeDamage")).
                ifPresent(subIdNode::setValue);
        if (saveNbt) {
            container.getMap(DataQuery.of("UnsafeData")).
                    ifPresent(nbtNode::setValue);
        }
        item.get(Keys.ITEM_DURABILITY).
                ifPresent(durabilityNode::setValue);
        item.get(Keys.DISPLAY_NAME).map(TextSerializers.FORMATTING_CODE::serialize).
                ifPresent(displayNameNode::setValue);
        item.get(Keys.ITEM_LORE).
                ifPresent(list ->
                        loreNode.setValue(list.stream().
                                map(TextSerializers.FORMATTING_CODE::serialize).
                                collect(Collectors.toList())));
        item.get(Keys.ITEM_ENCHANTMENTS).
                ifPresent(enchantments -> writeEnchantments(enchantments, enchantmentsNode));
        item.get(Keys.HIDE_ENCHANTMENTS).
                ifPresent(hideEnchantmentsNode::setValue);
        item.get(Keys.HIDE_ATTRIBUTES).
                ifPresent(hideAttributesNode::setValue);
    }

    public static CreatedHologram createHologram(
            Location<World> location, HologramSettings settings, boolean keepLoaded) {
        List<Text> lines = settings.getLines();
        Optional<HologramsService> optionalHologramsService = GWMLibrary.getInstance().getHologramsService();
        if (!optionalHologramsService.isPresent()) {
            throw new RuntimeException("Failed to create hologram, Holograms Service not found!");
        }
        HologramsService hologramsService = optionalHologramsService.get();
        location.getExtent().loadChunk(location.getChunkPosition(), true);
        Optional<ChunkTicketManager.LoadingTicket> optionalLoadingTicket = getTicket(location.getExtent());
        if (keepLoaded) {
            optionalLoadingTicket.ifPresent(loadingTicket -> loadingTicket.forceChunk(location.getChunkPosition()));
        }
        Optional<List<HologramsService.Hologram>> optionalHolograms = hologramsService.
                createMultilineHologram(location.add(settings.getOffset()), lines, settings.getMultilineDistance());
        if (!optionalHolograms.isPresent()) {
            throw new RuntimeException("Holograms Service found, but hologram can not be created!");
        }
        List<HologramsService.Hologram> holograms = optionalHolograms.get();
        if (holograms.isEmpty()) {
            throw new RuntimeException("Created hologram is empty!");
        }
        return new CreatedHologram(holograms, holograms.get(0).getLocation(), optionalLoadingTicket);
    }

    private static Optional<ChunkTicketManager.LoadingTicket> getTicket(World world) {
        if (loadingTickets.containsKey(world)) {
            return Optional.of(loadingTickets.get(world));
        }
        Optional<ChunkTicketManager.LoadingTicket> optionalLoadingTicket =
                GWMLibrary.getInstance().getChunkTicketManager().flatMap(manager -> {
                    //Without registering a callback an exception will be thrown during the ticket creation!
                    manager.registerCallback(GWMLibrary.getInstance(), (i1, i2) -> {});
                    return manager.createTicket(GWMLibrary.getInstance(), world);
                });
        optionalLoadingTicket.ifPresent(loadingTicket -> loadingTickets.put(world, loadingTicket));
        return optionalLoadingTicket;
    }

    public static Optional<Currency> getCurrencyById(EconomyService economyService, String currencyId) {
        return economyService.getCurrencies().stream().
                filter(currency -> currency.getId().equals(currencyId)).
                findFirst();
    }

    public static Text joinText(Iterable<Text> iterable) {
        Text.Builder builder = Text.builder();
        Iterator<Text> iterator = iterable.iterator();
        if (!iterator.hasNext()) {
            return builder.build();
        }
        builder.append(iterator.next());
        while (iterator.hasNext()) {
            builder.
                    append(Text.of('\n')).
                    append(iterator.next());
        }
        return builder.build();
    }

    public static String joinString(Iterable<String> iterable) {
        StringBuilder builder = new StringBuilder();
        Iterator<String> iterator = iterable.iterator();
        if (!iterator.hasNext()) {
            return builder.toString();
        }
        builder.append(iterator.next());
        while (iterator.hasNext()) {
            builder.
                    append('\n').
                    append(iterator.next());
        }
        return builder.toString();
    }
}
