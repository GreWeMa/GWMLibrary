package dev.gwm.spongeplugin.library.superobject.randommanager;

import dev.gwm.spongeplugin.library.utils.Pair;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.service.permission.Subject;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class WeightRandomManager extends AbstractRandomManager<WeightRandomManager.WeightRandomable> {

    public static final String TYPE = "WEIGHT";

    public WeightRandomManager(ConfigurationNode node) {
        super(node);
    }

    public WeightRandomManager(String id) {
        super(id);
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public <R extends WeightRandomable> R choose(List<R> randomables, @Nullable Subject subject, boolean fake) {
        List<Pair<Long, R>> list = new ArrayList<>();
        for (R randomable : randomables) {
            boolean foundByPermission = false;
            if (subject != null) {
                for (Map.Entry<String, Long> entry : fake ?
                        randomable.getFakePermissionWeights().entrySet() :
                        randomable.getPermissionWeights().entrySet()) {
                    String permission = entry.getKey();
                    long permissionWeight = entry.getValue();
                    if (subject.hasPermission(permission)) {
                        list.add(new Pair<>(permissionWeight, randomable));
                        foundByPermission = true;
                        break;
                    }
                }
            }
            if (!foundByPermission) {
                long weight = fake ?
                        randomable.getFakeWeight().orElse(randomable.getWeight()) :
                        randomable.getWeight();
                list.add(new Pair<>(weight, randomable));
            }
        }
        long sum = list.stream().reduce(0L, (l, pair) -> l + pair.getKey(), Long::sum);
        long randomPosition = ThreadLocalRandom.current().nextLong(sum) + 1;
        long weightSum = 0L;
        for (Pair<Long, R> pair : list) {
            weightSum += pair.getKey();
            if (weightSum >= randomPosition) {
                R randomable = pair.getValue();
                if (randomable.isPrefetch()) {
                    return choose((List<R>) randomable.getChildren(), subject, fake);
                }
                return randomable;
            }
        }
        throw new AssertionError("Should never happen because sum >= randomPosition, and at the end of the loop sum == weightSum");
    }

    public interface WeightRandomable extends Randomable {

        long getWeight();

        Optional<Long> getFakeWeight();

        Map<String, Long> getPermissionWeights();

        Map<String, Long> getFakePermissionWeights();

        @Override
        List<? extends WeightRandomable> getChildren();
    }
}
