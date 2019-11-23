package dev.gwm.spongeplugin.library.superobject;

import dev.gwm.spongeplugin.library.exception.IdFormatException;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.util.GWMLibraryUtils;
import ninja.leaping.configurate.ConfigurationNode;

public abstract class AbstractSuperObject implements SuperObject {

    private final String id;

    public AbstractSuperObject(ConfigurationNode node) {
        try {
            ConfigurationNode idNode = node.getNode("ID");
            if (!idNode.isVirtual()) {
                id = idNode.getString();
            } else {
                id = generateId();
            }
            if (!GWMLibraryUtils.ID_PATTERN.matcher(id).matches()) {
                throw new IdFormatException(id);
            }
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public AbstractSuperObject(String id) {
        this.id = id;
    }

    protected String generateId() {
        return category().getName().toLowerCase() + "_" + type().toLowerCase();
    }

    @Override
    public final String id() {
        return id;
    }
}
