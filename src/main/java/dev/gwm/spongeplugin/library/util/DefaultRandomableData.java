package dev.gwm.spongeplugin.library.util;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DefaultRandomableData {

    private final int level;
    private final Optional<Integer> fakeLevel;
    private final Map<String, Integer> permissionLevels;
    private final Map<String, Integer> fakePermissionLevels;
    private final long weight;
    private final Optional<Long> fakeWeight;
    private final Map<String, Long> permissionWeights;
    private final Map<String, Long> fakePermissionWeights;

    public DefaultRandomableData(ConfigurationNode node) throws ObjectMappingException {
        ConfigurationNode levelNode = node.getNode("LEVEL");
        ConfigurationNode fakeLevelNode = node.getNode("FAKE_LEVEL");
        ConfigurationNode permissionLevelsNode = node.getNode("PERMISSION_LEVELS");
        ConfigurationNode permissionFakeLevelsNode = node.getNode("FAKE_PERMISSION_LEVELS");
        ConfigurationNode weightNode = node.getNode("WEIGHT");
        ConfigurationNode fakeWeightNode = node.getNode("FAKE_WEIGHT");
        ConfigurationNode permissionWeightsNode = node.getNode("PERMISSION_WEIGHTS");
        ConfigurationNode permissionFakeWeightsNode = node.getNode("FAKE_PERMISSION_WEIGHTS");
        level = levelNode.getInt(1);
        if (level <= 0) {
            throw new IllegalArgumentException("Level is equal to or less than 0!");
        }
        if (!fakeLevelNode.isVirtual()) {
            fakeLevel = Optional.of(fakeLevelNode.getInt());
        } else {
            fakeLevel = Optional.empty();
        }
        if (fakeLevel.isPresent() && fakeLevel.get() <= 0) {
            throw new IllegalArgumentException("Fake Level is equal to or less than 0!");
        }
        if (!permissionLevelsNode.isVirtual()) {
            permissionLevels = permissionLevelsNode.getValue(new TypeToken<Map<String, Integer>>(){});
        } else {
            permissionLevels = new HashMap<>();
        }
        if (permissionLevels.values().stream().anyMatch(i -> i <= 0)) {
            throw new IllegalArgumentException("Permission Levels contains one or more levels that are equal to or less than 0!");
        }
        if (!permissionFakeLevelsNode.isVirtual()) {
            fakePermissionLevels = permissionFakeLevelsNode.getValue(new TypeToken<Map<String, Integer>>(){});
        } else {
            fakePermissionLevels = new HashMap<>();
        }
        if (fakePermissionLevels.values().stream().anyMatch(i -> i <= 0)) {
            throw new IllegalArgumentException("Fake Permission Levels contains one or more levels that are equal to or less than 0!");
        }
        weight = weightNode.getLong(1L);
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight is equal to or less than 0!");
        }
        if (!fakeWeightNode.isVirtual()) {
            fakeWeight = Optional.of(fakeWeightNode.getLong());
        } else {
            fakeWeight = Optional.empty();
        }
        if (fakeWeight.isPresent() && fakeWeight.get() <= 0) {
            throw new IllegalArgumentException("Fake Weight is equal to or less than 0!");
        }
        if (!permissionWeightsNode.isVirtual()) {
            permissionWeights = permissionWeightsNode.getValue(new TypeToken<Map<String, Long>>(){});
        } else {
            permissionWeights = new HashMap<>();
        }
        if (permissionWeights.values().stream().anyMatch(i -> i <= 0)) {
            throw new IllegalArgumentException("Permission Weights contains one or more levels that are equal to or less than 0!");
        }
        if (!permissionFakeWeightsNode.isVirtual()) {
            fakePermissionWeights = permissionFakeWeightsNode.getValue(new TypeToken<Map<String, Long>>(){});
        } else {
            fakePermissionWeights = new HashMap<>();
        }
        if (fakePermissionWeights.values().stream().anyMatch(i -> i <= 0)) {
            throw new IllegalArgumentException("Fake Permission Weights contains one or more levels that are equal to or less than 0!");
        }
    }

    public DefaultRandomableData(int level, Optional<Integer> fakeLevel,
                                 Map<String, Integer> permissionLevels, Map<String, Integer> fakePermissionLevels,
                                 long weight, Optional<Long> fakeWeight,
                                 Map<String, Long> permissionWeights, Map<String, Long> fakePermissionWeights) {
        if (level <= 0) {
            throw new IllegalArgumentException("Level is equal to or less than 0!");
        }
        this.level = level;
        if (fakeLevel.isPresent() && fakeLevel.get() <= 0) {
            throw new IllegalArgumentException("Fake Level is equal to or less than 0!");
        }
        this.fakeLevel = fakeLevel;
        if (permissionLevels.values().stream().anyMatch(i -> i <= 0)) {
            throw new IllegalArgumentException("Permission Levels contains one or more levels that are equal to or less than 0!");
        }
        this.permissionLevels = Collections.unmodifiableMap(permissionLevels);
        if (fakePermissionLevels.values().stream().anyMatch(i -> i <= 0)) {
            throw new IllegalArgumentException("Fake Permission Levels contains one or more levels that are equal to or less than 0!");
        }
        this.fakePermissionLevels = Collections.unmodifiableMap(fakePermissionLevels);
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight is equal to or less than 0!");
        }
        this.weight = weight;
        if (fakeWeight.isPresent() && fakeWeight.get() <= 0) {
            throw new IllegalArgumentException("Fake Weight is equal to or less than 0!");
        }
        this.fakeWeight = fakeWeight;
        if (permissionWeights.values().stream().anyMatch(i -> i <= 0)) {
            throw new IllegalArgumentException("Permission Weights contains one or more levels that are equal to or less than 0!");
        }
        this.permissionWeights = Collections.unmodifiableMap(permissionWeights);
        if (fakePermissionWeights.values().stream().anyMatch(i -> i <= 0)) {
            throw new IllegalArgumentException("Fake Permission Weights contains one or more levels that are equal to or less than 0!");
        }
        this.fakePermissionWeights = Collections.unmodifiableMap(fakePermissionWeights);
    }

    public int getLevel() {
        return level;
    }

    public Optional<Integer> getFakeLevel() {
        return fakeLevel;
    }

    public Map<String, Integer> getPermissionLevels() {
        return permissionLevels;
    }

    public Map<String, Integer> getFakePermissionLevels() {
        return fakePermissionLevels;
    }

    public long getWeight() {
        return weight;
    }

    public Optional<Long> getFakeWeight() {
        return fakeWeight;
    }

    public Map<String, Long> getPermissionWeights() {
        return permissionWeights;
    }

    public Map<String, Long> getFakePermissionWeights() {
        return fakePermissionWeights;
    }
}
