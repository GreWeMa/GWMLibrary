package dev.gwm.spongeplugin.library.utils;

import dev.gwm.spongeplugin.library.superobject.SuperObject;
import ninja.leaping.configurate.ConfigurationNode;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class SuperObjectsServiceImpl implements SuperObjectsService {

    private static final String SAVED_SUPER_OBJECT_TYPE = "SAVED";

    private final Set<SuperObjectCategory> categories;
    private final Map<SuperObjectIdentifier, Class<? extends SuperObject>> classes;

    private Set<SuperObject> createdSuperObjects;
    private Set<SuperObject> savedSuperObjects;

    public SuperObjectsServiceImpl(Set<SuperObjectCategory> categories,
                                   Map<SuperObjectIdentifier, Class<? extends SuperObject>> classes) {
        this.categories = Collections.unmodifiableSet(categories);
        this.classes = Collections.unmodifiableMap(classes);
        createdSuperObjects = new HashSet<>();
        savedSuperObjects = new HashSet<>();
    }

    @Override
    public Set<SuperObjectCategory> getCategories() {
        return categories;
    }

    @Override
    public Map<SuperObjectIdentifier, Class<? extends SuperObject>> getClasses() {
        return classes;
    }

    @Override
    public Set<SuperObject> getCreatedSuperObjects() {
        return createdSuperObjects;
    }

    @Override
    public Set<SuperObject> getSavedSuperObjects() {
        return savedSuperObjects;
    }

    @Override
    public <T extends SuperObject> T create(SuperObjectCategory<T> category, ConfigurationNode node, boolean save) {
        ConfigurationNode typeNode = node.getNode("TYPE");
        String type;
        if (!typeNode.isVirtual()) {
            type = typeNode.getString();
        } else {
            Set<SuperObjectIdentifier> identifiersSet = classes.keySet().
                    stream().
                    filter(identifier -> identifier.getCategory().equals(category)).
                    collect(Collectors.toSet());
            if (identifiersSet.size() == 1) { //If there is no ambiguity with the identifier (category has only one type)
                type = identifiersSet.iterator().next().getType();
            } else {
                throw new IllegalArgumentException("TYPE node does not exist!");
            }
        }
        if (type.equals(SAVED_SUPER_OBJECT_TYPE)) {
            ConfigurationNode savedIdNode = node.getNode("SAVED_ID");
            if (savedIdNode.isVirtual()) {
                throw new IllegalArgumentException("SAVED_ID node does not exist!");
            }
            String savedId = savedIdNode.getString();
            try {
                return (T) getSavedSuperObjectById(savedId).
                        orElseThrow(() -> new RuntimeException("Saved Super Object with SAVED_ID \"" + savedId + "\" does not exist!"));
            } catch (ClassCastException e) {
                throw new RuntimeException("Failed to create a Super Object (SSO casting exception)!", e);
            }
        }
        SuperObjectIdentifier identifier = new SuperObjectIdentifier<>(category, type);
        if (!classes.containsKey(identifier)) {
            throw new RuntimeException("Identifier \"" + identifier + "\" is not valid!");
        }
        try {
            T superObject = (T) classes.get(identifier).getConstructor(ConfigurationNode.class).newInstance(node);
            if (save) {
                createdSuperObjects.add(superObject);
            }
            return superObject;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to create a Super Object (reflection exception)!", e);
        } catch (ClassCastException e) {
            throw new RuntimeException("Failed to create a Super Object (casting exception)!", e);
        }
    }

    @Override
    public SuperObject load(ConfigurationNode node, boolean save) {
        ConfigurationNode categoryNode = node.getNode("CATEGORY");
        ConfigurationNode typeNode = node.getNode("TYPE");
        ConfigurationNode idNode = node.getNode("ID");
        if (categoryNode.isVirtual()) {
            throw new IllegalArgumentException("CATEGORY node does not exist!");
        }
        if (idNode.isVirtual()) {
            throw new IllegalArgumentException("ID node does not exist!");
        }
        if (!typeNode.isVirtual() && typeNode.getString().equals("SAVED")) {
            throw new IllegalArgumentException("Saved Super Object cannot have \"SAVED\" type");
        }
        String categoryName = categoryNode.getString();
        String id = idNode.getString();
        if (getSavedSuperObjectById(id).isPresent()) {
            throw new RuntimeException("Saved Super Object id \"" + id + "\" is not unique!");
        }
        for (SuperObjectCategory<?> category : categories) {
            if (category.getName().equals(categoryName)) {
                SuperObject superObject = create(category, node, false);
                if (save) {
                    savedSuperObjects.add(superObject);
                }
                return superObject;
            }
        }
        throw new RuntimeException("Category with name \"" + categoryName + "\" is not found!");
    }
}
