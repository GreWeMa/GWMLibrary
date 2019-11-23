package dev.gwm.spongeplugin.library.exception;

import dev.gwm.spongeplugin.library.util.SuperObjectCategory;

public class SuperObjectConstructionException extends RuntimeException {

    public static final String MESSAGE = "Failed to create a Super Object with category: \"%s\" and type: \"%s\"";

    public SuperObjectConstructionException(SuperObjectCategory category, String type) {
        super(String.format(MESSAGE, category.toString(), type));
    }

    public SuperObjectConstructionException(SuperObjectCategory category, String type, Throwable throwable) {
        super(String.format(MESSAGE, category.toString(), type), throwable);
    }
}
