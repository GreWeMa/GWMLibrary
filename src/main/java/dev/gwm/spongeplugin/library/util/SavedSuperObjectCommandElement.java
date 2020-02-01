package dev.gwm.spongeplugin.library.util;

import dev.gwm.spongeplugin.library.GWMLibrary;
import dev.gwm.spongeplugin.library.superobject.Giveable;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.util.service.SuperObjectService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

public class SavedSuperObjectCommandElement extends AbstractSuperObjectCommandElement {

    private final Language language = GWMLibrary.getInstance().getLanguage();

    private final Optional<SuperObjectCategory> category;
    private final boolean onlyGiveable;
    private final Optional<String> type;

    public SavedSuperObjectCommandElement(@Nullable Text key) {
        super(key);
        category = Optional.empty();
        onlyGiveable = false;
        type = Optional.empty();
    }

    public SavedSuperObjectCommandElement(@Nullable Text key,
                                          SuperObjectCategory category) {
        super(key);
        this.category = Optional.of(category);
        onlyGiveable = false;
        type = Optional.empty();
    }

    public SavedSuperObjectCommandElement(@Nullable Text key,
                                          boolean onlyGiveable) {
        super(key);
        category = Optional.empty();
        this.onlyGiveable = onlyGiveable;
        type = Optional.empty();
    }

    public SavedSuperObjectCommandElement(@Nullable Text key,
                                          SuperObjectCategory category, boolean onlyGiveable) {
        super(key);
        this.category = Optional.of(category);
        this.onlyGiveable = onlyGiveable;
        type = Optional.empty();
    }

    public SavedSuperObjectCommandElement(@Nullable Text key,
                                          Optional<SuperObjectCategory> category, boolean onlyGiveable, Optional<String> type) {
        super(key);
        this.category = category;
        this.onlyGiveable = onlyGiveable;
        this.type = type;
    }

    @Nullable
    @Override
    protected SuperObject parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String superObjectId = args.next();
        SuperObject superObject = Sponge.getServiceManager().provide(SuperObjectService.class).get().
                getSavedSuperObjectById(superObjectId).
                orElseThrow(() ->
                        new ArgumentParseException(GWMLibraryUtils.joinText(language.
                                getTranslation("SAVED_SUPER_OBJECT_IS_NOT_FOUND",
                                        new ImmutablePair<>("SUPER_OBJECT_ID", superObjectId),
                                        source)), superObjectId, 0));
        if (category.isPresent() && !category.get().equals(superObject.category())) {
            throw new ArgumentParseException(GWMLibraryUtils.joinText(language.
                    getTranslation("SAVED_SUPER_OBJECT_IS_NOT_OF_REQUIRED_CATEGORY", Arrays.asList(
                            new ImmutablePair<>("SUPER_OBJECT_ID", superObjectId),
                            new ImmutablePair<>("SUPER_OBJECT_CATEGORY", superObject.category().getName()),
                            new ImmutablePair<>("REQUIRED_SUPER_OBJECT_CATEGORY", category.get().getName())
                    ), source)), superObjectId, 0);
        }
        if (onlyGiveable && !(superObject instanceof Giveable)) {
            throw new ArgumentParseException(GWMLibraryUtils.joinText(language.
                    getTranslation("SAVED_SUPER_OBJECT_IS_NOT_GIVEABLE",
                            new ImmutablePair<>("SUPER_OBJECT_ID", superObjectId),
                            source)), superObjectId, 0);
        }
        if (type.isPresent() && !type.get().equals(superObject.type())) {
            throw new ArgumentParseException(GWMLibraryUtils.joinText(language.
                    getTranslation("SAVED_SUPER_OBJECT_IS_NOT_OF_REQUIRED_TYPE", Arrays.asList(
                            new ImmutablePair<>("SUPER_OBJECT_ID", superObjectId),
                            new ImmutablePair<>("SUPER_OBJECT_TYPE", superObject.type()),
                            new ImmutablePair<>("REQUIRED_SUPER_OBJECT_TYPE", type.get())
                    ), source)), superObjectId, 0);
        }
        return superObject;
    }

    @Override
    protected Collection<SuperObject> getSuperObjects() {
        return Sponge.getServiceManager().provide(SuperObjectService.class).get().getSavedSuperObjects();
    }

    @Override
    protected Predicate<? super SuperObject> categoryFilter() {
        return superObject -> !category.isPresent() || category.get().equals(superObject.category());
    }

    @Override
    protected Predicate<? super SuperObject> giveableFilter() {
        return superObject -> !onlyGiveable || superObject instanceof Giveable;
    }

    @Override
    protected Predicate<? super SuperObject> typeFilter() {
        return superObject -> !type.isPresent() || type.get().equals(superObject.type());
    }
}
