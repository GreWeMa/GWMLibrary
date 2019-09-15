package dev.gwm.spongeplugin.library.command;

import dev.gwm.spongeplugin.library.superobject.Giveable;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.utils.Language;
import dev.gwm.spongeplugin.library.utils.Pair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Arrays;

public class GiveCommand implements CommandExecutor {

    private final Language language;

    public GiveCommand(Language language) {
        this.language = language;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {
        SuperObject superObject = args.<SuperObject>getOne(Text.of("super-object")).get();
        String superObjectId = superObject.id().get();
        Giveable giveable = (Giveable) superObject;
        Player player = args.<Player>getOne(Text.of("player")).get();
        int amount = args.<Integer>getOne(Text.of("amount")).orElse(1);
        boolean force = args.hasAny("f");
        boolean self = source.equals(player);
        if (self) {
            if (!player.hasPermission("gwm_library.command.give." + superObjectId)) {
                player.sendMessages(language.getTranslation("HAVE_NOT_PERMISSION", player));
                return CommandResult.empty();
            }
        } else {
            if (!source.hasPermission("gwm_library.command.give_others." + superObjectId)) {
                source.sendMessages(language.getTranslation("HAVE_NOT_PERMISSION", source));
                return CommandResult.empty();
            }
        }
        giveable.give(player, amount, force);
        source.sendMessages(language.getTranslation("SUCCESSFULLY_GAVE_SUPER_OBJECT", Arrays.asList(
                new Pair<>("SUPER_OBJECT_ID", superObjectId),
                new Pair<>("PLAYER_NAME", player.getName())
        ), source));
        player.sendMessages(language.getTranslation("SUCCESSFULLY_GOT_SUPER_OBJECT",
                new Pair<>("SUPER_OBJECT_ID", superObjectId),
                player));
        return CommandResult.success();
    }
}
