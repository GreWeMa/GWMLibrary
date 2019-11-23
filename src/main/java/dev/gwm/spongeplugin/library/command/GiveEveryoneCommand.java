package dev.gwm.spongeplugin.library.command;

import dev.gwm.spongeplugin.library.superobject.Giveable;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.util.Language;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public class GiveEveryoneCommand implements CommandExecutor {

    private final Language language;

    public GiveEveryoneCommand(Language language) {
        this.language = language;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {
        SuperObject superObject = args.<SuperObject>getOne(Text.of("super-object")).get();
        String superObjectId = superObject.id();
        Giveable giveable = (Giveable) superObject;
        int amount = args.<Integer>getOne(Text.of("amount")).orElse(1);
        boolean force = args.hasAny("f");
        if (!source.hasPermission("gwm_library.command.give_everyone." + superObjectId)) {
            source.sendMessages(language.getTranslation("HAVE_NOT_PERMISSION", source));
            return CommandResult.empty();
        }
        Sponge.getServer().getOnlinePlayers().forEach(player -> {
            giveable.give(player, amount, force);
            player.sendMessages(language.getTranslation("SUCCESSFULLY_GOT_SUPER_OBJECT",
                    new ImmutablePair<>("SUPER_OBJECT_ID", superObjectId),
                    player));
        });
        source.sendMessages(language.getTranslation("SUCCESSFULLY_GAVE_SUPER_OBJECT_TO_EVERYONE",
                new ImmutablePair<>("SUPER_OBJECT_ID", superObjectId),
                source));
        return CommandResult.success();
    }
}
