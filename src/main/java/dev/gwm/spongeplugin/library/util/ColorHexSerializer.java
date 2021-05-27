package dev.gwm.spongeplugin.library.util;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.util.Color;

public class ColorHexSerializer implements TypeSerializer<Color> {

    @Nullable
    @Override
    public Color deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode node) throws ObjectMappingException {
        return Color.of(java.awt.Color.decode(node.getString()));
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable Color obj, @NonNull ConfigurationNode node) throws ObjectMappingException {
        if (obj == null) {
            node.setValue(null);
        } else {
            node.setValue("#" + String.format("#%02x%02x%02x", obj.getRed(), obj.getGreen(), obj.getBlue()));
        }
    }
}
