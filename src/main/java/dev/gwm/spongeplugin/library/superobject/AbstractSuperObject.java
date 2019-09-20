package dev.gwm.spongeplugin.library.superobject;

import dev.gwm.spongeplugin.library.exception.IdFormatException;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.utils.GWMLibraryUtils;
import dev.gwm.spongeplugin.library.utils.SuperObjectsService;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractSuperObject implements SuperObject {

    private static AtomicInteger ID_COUNTER = new AtomicInteger();

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
        String base = category().getName().toLowerCase() + "_";
        SuperObjectsService superObjectsService = Sponge.getServiceManager().provide(SuperObjectsService.class).get();
        String id;
        while (superObjectsService.getSuperObjectById(id = base + ID_COUNTER.getAndIncrement()).isPresent()) {}
        return id;
    }

    @Override
    public final String id() {
        return id;
    }
}
