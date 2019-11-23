package dev.gwm.spongeplugin.library.util;

import java.util.Arrays;
import java.util.Objects;

public class Version implements Comparable<Version> {

    private final String prefix;
    private final int[] array;

    public static Version parse(String string) {
        int hyphenPosition = string.lastIndexOf('-');
        if (hyphenPosition != -1) {
            String prefix = string.substring(0, hyphenPosition);
            String[] splited = string.substring(hyphenPosition + 1).split("\\.");
            int[] array = new int[splited.length];
            for (int i = 0; i < splited.length; i++) {
                array[i] = Integer.parseInt(splited[i]);
            }
            return new Version(prefix, array);
        } else {
            String[] splited = string.split("\\.");
            int[] array = new int[splited.length];
            for (int i = 0; i < splited.length; i++) {
                array[i] = Integer.parseInt(splited[i]);
            }
            return new Version("", array);
        }
    }

    public Version(String prefix, int... array) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix cannot be null!");
        }
        if (array == null) {
            throw new IllegalArgumentException("array cannot be null!");
        }
        if (array.length == 0) {
            throw new IllegalArgumentException("array cannot be empty!");
        }
        this.prefix = prefix;
        this.array = array;
    }

    public Version(int... array) {
        this("", array);
    }

    @Override
    public int compareTo(Version version) {
        int[] array = version.getArray();
        for (int i = 0; i < this.array.length && i < array.length; i++) {
            int compared = Integer.compare(this.array[i], array[i]);
            if (compared != 0) {
                return compared;
            }
        }
        return Integer.compare(this.array.length, array.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Version version = (Version) o;
        return prefix.equals(version.prefix) &&
                Arrays.equals(array, version.array);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(prefix);
        result = 31 * result + Arrays.hashCode(array);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (!prefix.equals("")) {
            builder.append(prefix).append('-');
        }
        builder.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            builder.append('.').append(array[i]);
        }
        return builder.toString();
    }

    public String getPrefix() {
        return prefix;
    }

    public int[] getArray() {
        return array;
    }
}
