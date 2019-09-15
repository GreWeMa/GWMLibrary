package dev.gwm.spongeplugin.library.utils;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

public class TextSerializer implements TypeSerializer<Text> {

    @Nullable
    @Override
    public Text deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        return TextSerializers.FORMATTING_CODE.deserialize(value.getString());
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable Text obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        value.setValue(TextSerializers.FORMATTING_CODE.serialize(obj));
    }
}
