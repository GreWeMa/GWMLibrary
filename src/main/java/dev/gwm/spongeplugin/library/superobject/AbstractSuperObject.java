package dev.gwm.spongeplugin.library.superobject;

import dev.gwm.spongeplugin.library.exception.IdFormatException;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.utils.GWMLibraryUtils;
import ninja.leaping.configurate.ConfigurationNode;

import java.util.Optional;

public abstract class AbstractSuperObject implements SuperObject{

    private final Optional<String> id;

    public AbstractSuperObject(ConfigurationNode node) {
        try {
            ConfigurationNode idNode = node.getNode("ID");
            if (!idNode.isVirtual()) {
                id = Optional.of(idNode.getString());
            } else {
                id = Optional.empty();
            }
            if (id.isPresent() && !GWMLibraryUtils.ID_PATTERN.matcher(id.get()).matches()) {
                throw new IdFormatException(id.get());
            }
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public AbstractSuperObject(Optional<String> id) {
        this.id = id;
    }

    @Override
    public final Optional<String> id() {
        return id;
    }
}
