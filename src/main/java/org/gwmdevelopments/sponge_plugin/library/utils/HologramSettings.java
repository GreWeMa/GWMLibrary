package org.gwmdevelopments.sponge_plugin.library.utils;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.text.Text;

import java.util.Collections;
import java.util.List;

public class HologramSettings {

    private final List<Text> lines;
    private final Vector3d offset;
    private final double multilineDistance;

    public HologramSettings(List<Text> lines, Vector3d offset, double multilineDistance) {
        this.lines = Collections.unmodifiableList(lines);
        this.offset = offset;
        this.multilineDistance = multilineDistance;
    }

    public List<Text> getLines() {
        return lines;
    }

    public Vector3d getOffset() {
        return offset;
    }

    public double getMultilineDistance() {
        return multilineDistance;
    }
}
