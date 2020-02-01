package dev.gwm.spongeplugin.library.util;

import dev.gwm.spongeplugin.library.GWMLibrary;
import dev.gwm.spongeplugin.library.command.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.text.Text;

public class GWMLibraryCommandUtils {

    public static void registerCommands(GWMLibrary instance) {
        Language language = instance.getLanguage();
        Cause cause = instance.getCause();
        CommandSpec helpCommand = CommandSpec.builder().
                description(Text.of("Help command")).
                executor(new HelpCommand(language)).
                build();
        CommandSpec reloadCommand = CommandSpec.builder().
                permission("gwm_library.command.reload").
                description(Text.of("Reload the plugin")).
                executor(new ReloadCommand(language)).
                build();
        CommandSpec saveItemCommand = CommandSpec.builder().
                permission("gwm_library.command.saveitem").
                description(Text.of("Save the item in hand to the config")).
                executor(new SaveItemCommand(language)).
                arguments(
                        GenericArguments.string(Text.of("path")),
                        GenericArguments.flags().flag("n").
                                buildWith(GenericArguments.none())
                ).
                build();
        CommandSpec buyCommand = CommandSpec.builder().
                description(Text.of("Buy a Super Object")).
                executor(new BuyCommand(language, cause)).
                arguments(
                        new SavedSuperObjectCommandElement(Text.of("super-object"), true),
                        GenericArguments.optional(GenericArguments.integer(Text.of("amount")))
                ).
                build();
        CommandSpec giveCommand = CommandSpec.builder().
                description(Text.of("Give a Super Object to a player")).
                executor(new GiveCommand(language)).
                arguments(
                        new SavedSuperObjectCommandElement(Text.of("super-object"), true),
                        GenericArguments.playerOrSource(Text.of("player")),
                        GenericArguments.optional(GenericArguments.integer(Text.of("amount"))),
                        GenericArguments.flags().flag("f").
                                buildWith(GenericArguments.none())
                ).
                build();
        CommandSpec giveEveryoneCommand = CommandSpec.builder().
                description(Text.of("Give a Super Object to all the online players")).
                executor(new GiveEveryoneCommand(language)).
                arguments(
                        new SavedSuperObjectCommandElement(Text.of("super-object"), true),
                        GenericArguments.optional(GenericArguments.integer(Text.of("amount"))),
                        GenericArguments.flags().flag("f").
                                buildWith(GenericArguments.none())
                ).
                build();
        CommandSpec spec = CommandSpec.builder().
                permission("gwm_library.command.base").
                description(Text.of("The basic command")).
                child(helpCommand, "help").
                child(reloadCommand, "reload").
                child(saveItemCommand, "saveitem").
                child(buyCommand, "buy").
                child(giveCommand, "give").
                child(giveEveryoneCommand, "giveeveryone").
                build();
        Sponge.getCommandManager().register(instance, spec,
                "gwmlibrary");
    }
}

