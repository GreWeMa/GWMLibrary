package dev.gwm.spongeplugin.library.utils;

import dev.gwm.spongeplugin.library.superobject.SuperObject;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class AbstractSuperObjectCommandElement extends CommandElement {

    protected AbstractSuperObjectCommandElement(@Nullable Text key) {
        super(key);
    }

    @Override
    public List<String> complete(CommandSource source, CommandArgs args, CommandContext context) {
        Optional<String> optionalArg = args.nextIfPresent();
        if (optionalArg.isPresent()) {
            String arg = optionalArg.get();
            return getSuperObjects().
                    stream().
                    filter(idFilter(arg)).
                    filter(permissionFilter(source)).
                    filter(categoryFilter()).
                    filter(giveableFilter()).
                    filter(typeFilter()).
                    map(SuperObject::id).
                    collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    protected abstract Collection<? extends SuperObject> getSuperObjects();

    private Predicate<? super SuperObject> idFilter(String arg) {
        return superObject -> superObject.id().startsWith(arg);
    }

    protected Predicate<? super SuperObject> permissionFilter(CommandSource source) {
        return superObject -> source.hasPermission("gwm_library.tab_completion." + superObject.id());
    }

    protected abstract Predicate<? super SuperObject> categoryFilter();

    protected abstract Predicate<? super SuperObject> giveableFilter();

    protected abstract Predicate<? super SuperObject> typeFilter();
}
