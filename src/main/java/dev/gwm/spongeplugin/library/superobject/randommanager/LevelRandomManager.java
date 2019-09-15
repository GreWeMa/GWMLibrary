package dev.gwm.spongeplugin.library.superobject.randommanager;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.service.permission.Subject;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class LevelRandomManager extends AbstractRandomManager<LevelRandomManager.LevelRandomable> {

    public static final String TYPE = "LEVEL";

    public LevelRandomManager(ConfigurationNode node) {
        super(node);
    }

    public LevelRandomManager(Optional<String> id) {
        super(id);
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public <R extends LevelRandomable> R choose(List<R> randomables, @Nullable Subject subject, boolean fake) {
        Map<Integer, List<R>> sortedRandomables = new HashMap<>();
        for (R randomable : randomables) {
            boolean foundByPermission = false;
            if (subject != null) {
                for (Map.Entry<String, Integer> entry : fake ?
                        randomable.getFakePermissionLevels().entrySet() :
                        randomable.getPermissionLevels().entrySet()) {
                    String permission = entry.getKey();
                    int permissionLevel = entry.getValue();
                    if (subject.hasPermission(permission)) {
                        if (sortedRandomables.containsKey(permissionLevel)) {
                            sortedRandomables.get(permissionLevel).add(randomable);
                            foundByPermission = true;
                            break;
                        } else {
                            List<R> list = new ArrayList<>();
                            list.add(randomable);
                            sortedRandomables.put(permissionLevel, list);
                            foundByPermission = true;
                            break;
                        }
                    }
                }
            }
            if (!foundByPermission) {
                int level = fake ?
                        randomable.getFakeLevel().orElse(randomable.getLevel()) :
                        randomable.getLevel();
                if (sortedRandomables.containsKey(level)) {
                    sortedRandomables.get(level).add(randomable);
                } else {
                    List<R> list = new ArrayList<>();
                    list.add(randomable);
                    sortedRandomables.put(level, list);
                }
            }
        }
        int level;
        while (!sortedRandomables.containsKey(level = getRandomIntLevel())) {
        }
        List<R> actualRandomables = sortedRandomables.get(level);
        R randomable = actualRandomables.get(ThreadLocalRandom.current().nextInt(actualRandomables.size()));
        if (randomable.isPrefetch()) {
            return choose((List<R>) randomable.getChildren(), subject, fake);
        }
        return randomable;
    }

    private int getRandomIntLevel() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int level = 1;
        while (random.nextBoolean()) {
            level++;
        }
        return level;
    }

    public interface LevelRandomable extends Randomable {

        int getLevel();

        Optional<Integer> getFakeLevel();

        Map<String, Integer> getPermissionLevels();

        Map<String, Integer> getFakePermissionLevels();

        @Override
        List<? extends LevelRandomable> getChildren();
    }
}
