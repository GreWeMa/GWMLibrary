package dev.gwm.spongeplugin.library.util;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import de.randombyte.holograms.api.HologramsService;
import dev.gwm.spongeplugin.library.GWMLibrary;
import me.rojo8399.placeholderapi.PlaceholderService;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.NotePitch;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.FireworkShape;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.Direction;
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
                                                         Vector3d defaultOffset, double defaultMultilineDistance) throws ObjectMappingException {
        ConfigurationNode linesNode = node.getNode("LINES");
        ConfigurationNode offsetNode = node.getNode("OFFSET");
        ConfigurationNode multilineDistanceNode = node.getNode("MULTILINE_DISTANCE");
        if (linesNode.isVirtual()) {
            throw new IllegalArgumentException("LINES node does not exist!");
        }
        List<Text> lines = linesNode.getList(TypeToken.of(Text.class));
        Vector3d offset = offsetNode.isVirtual() ?
                defaultOffset :
                parseVector3d(offsetNode);
        double multilineDistance = multilineDistanceNode.isVirtual() ?
                defaultMultilineDistance :
                multilineDistanceNode.getDouble();
        return new HologramSettings(lines, offset, multilineDistance);
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

    public static FireworkEffect parseFireworkEffect(ConfigurationNode node) throws ObjectMappingException {
        ConfigurationNode trailNode = node.getNode("TRAIL");
        ConfigurationNode flickerNode = node.getNode("FLICKER");
        ConfigurationNode colorsNode = node.getNode("COLORS");
        ConfigurationNode fadesNode = node.getNode("FADES");
        ConfigurationNode shapeNode = node.getNode("SHAPE");
        FireworkEffect.Builder builder = FireworkEffect.builder();
        if (!trailNode.isVirtual()) {
            builder.trail(trailNode.getBoolean());
        }
        if (!flickerNode.isVirtual()) {
            builder.flicker(flickerNode.getBoolean());
        }
        if (!colorsNode.isVirtual()) {
            List<Color> colors = new ArrayList<>();
            for (ConfigurationNode colorNode : colorsNode.getChildrenList()) {
                colors.add(colorNode.getValue(TypeToken.of(Color.class)));
            }
            builder.colors(colors);
        }
        if (!fadesNode.isVirtual()) {
            List<Color> fades = new ArrayList<>();
            for (ConfigurationNode fadeNode : fadesNode.getChildrenList()) {
                fades.add(fadeNode.getValue(TypeToken.of(Color.class)));
            }
            builder.fades(fades);
        }
        if (!shapeNode.isVirtual()) {
            builder.shape(shapeNode.getValue(TypeToken.of(FireworkShape.class)));
        }
        return builder.build();
    }

    public static ParticleEffect parseParticleEffect(ConfigurationNode node) throws ObjectMappingException {
        ConfigurationNode particleTypeNode = node.getNode("PARTICLE_TYPE");
        ConfigurationNode blockStateNode = node.getNode("BLOCK_STATE");
        ConfigurationNode colorNode = node.getNode("COLOR");
        ConfigurationNode directionNode = node.getNode("DIRECTION");
        ConfigurationNode fireworkEffectsNode = node.getNode("FIREWORK_EFFECTS");
        ConfigurationNode itemStackSnapshotNode = node.getNode("ITEM_STACK_SNAPSHOT");
        ConfigurationNode noteNode = node.getNode("NOTE");
        ConfigurationNode offsetNode = node.getNode("OFFSET");
        ConfigurationNode potionEffectTypeNode = node.getNode("POTION_EFFECT_TYPE");
        ConfigurationNode quantityNode = node.getNode("QUANTITY");
        ConfigurationNode scaleNode = node.getNode("SCALE");
        ConfigurationNode slowHorizontalVelocityNode = node.getNode("SLOW_HORIZONTAL_VELOCITY");
        ConfigurationNode velocityNode = node.getNode("VELOCITY");
        ParticleEffect.Builder builder = ParticleEffect.builder();
        ParticleType particleType = particleTypeNode.getValue(TypeToken.of(ParticleType.class));
        builder.type(particleType);
        if (!blockStateNode.isVirtual()) {
            builder.option(ParticleOptions.BLOCK_STATE, blockStateNode.getValue(TypeToken.of(BlockState.class)));
        }
        if (!colorNode.isVirtual()) {
            builder.option(ParticleOptions.COLOR, colorNode.getValue(TypeToken.of(Color.class)));
        }
        if (!directionNode.isVirtual()) {
            builder.option(ParticleOptions.DIRECTION, Direction.valueOf(directionNode.getString()));
        }
        if (!fireworkEffectsNode.isVirtual()) {
            List<FireworkEffect> fireworkEffects = new ArrayList<>();
            for (ConfigurationNode fireworkEffectNode : fireworkEffectsNode.getChildrenList()) {
                fireworkEffects.add(parseFireworkEffect(fireworkEffectNode));
            }
            builder.option(ParticleOptions.FIREWORK_EFFECTS, fireworkEffects);
        }
        if (!itemStackSnapshotNode.isVirtual()) {
            builder.option(ParticleOptions.ITEM_STACK_SNAPSHOT, GWMLibraryUtils.parseItem(itemStackSnapshotNode).createSnapshot());
        }
        if (!noteNode.isVirtual()) {
            builder.option(ParticleOptions.NOTE, noteNode.getValue(TypeToken.of(NotePitch.class)));
        }
        if (!offsetNode.isVirtual()) {
            builder.option(ParticleOptions.OFFSET, GWMLibraryUtils.parseVector3d(offsetNode));
        }
        if (!potionEffectTypeNode.isVirtual()) {
            builder.option(ParticleOptions.POTION_EFFECT_TYPE, potionEffectTypeNode.getValue(TypeToken.of(PotionEffectType.class)));
        }
        if (!quantityNode.isVirtual()) {
            builder.option(ParticleOptions.QUANTITY, quantityNode.getInt());
        }
        if (!scaleNode.isVirtual()) {
            builder.option(ParticleOptions.SCALE, scaleNode.getDouble());
        }
        if (!slowHorizontalVelocityNode.isVirtual()) {
            builder.option(ParticleOptions.SLOW_HORIZONTAL_VELOCITY, slowHorizontalVelocityNode.getBoolean());
        }
        if (!velocityNode.isVirtual()) {
            builder.option(ParticleOptions.VELOCITY, GWMLibraryUtils.parseVector3d(velocityNode));
        }
        return builder.build();
    }

    public static Enchantment parseEnchantment(ConfigurationNode node) throws ObjectMappingException {
        ConfigurationNode enchantmentNode = node.getNode("ENCHANTMENT");
        ConfigurationNode levelNode = node.getNode("LEVEL");
        if (enchantmentNode.isVirtual()) {
            throw new IllegalArgumentException("ENCHANTMENT node does not exist!");
        }
        EnchantmentType type = enchantmentNode.getValue(TypeToken.of(EnchantmentType.class));
        int level = levelNode.getInt(1);
        return Enchantment.of(type, level);
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

    public static ItemStack parseItem(ConfigurationNode node) throws ObjectMappingException {
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
            LinkedHashMap<Object, Object> nbtMap = (LinkedHashMap<Object, Object>) nbtNode.getValue();
            if (item.toContainer().get(DataQuery.of("UnsafeData")).isPresent()) {
                Map<?, ?> unsafeDataMap = item.toContainer().getMap(DataQuery.of("UnsafeData")).get();
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

    public static List<Text> getMessage(CommandSource source, Optional<List<String>> custom,
                                        Language language, String defaultMessagePath, List<Map.Entry<String, ?>> entries) {
        return custom.map(strings -> strings.stream().
                map(string -> entries.stream().reduce(string, (s, e) ->
                        s.replace("%" + e.getKey() + "%", e.getValue().toString()), String::concat)).
                map(TextSerializers.FORMATTING_CODE::deserialize).
                map(string -> {
                    //Using functional style here with map and orElse results in NoClassDefFoundError
                    Optional<PlaceholderService> optionalPlaceholderService = GWMLibrary.getInstance().getPlaceholderService();
                    if (optionalPlaceholderService.isPresent()) {
                        return optionalPlaceholderService.get().replacePlaceholders(string, source, null);
                    } else {
                        return string;
                    }
                }).
                collect(Collectors.toList())).
                orElse(language.getTranslation(defaultMessagePath, entries, source));
    }
}
