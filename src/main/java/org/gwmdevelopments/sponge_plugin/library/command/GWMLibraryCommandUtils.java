package org.gwmdevelopments.sponge_plugin.library.command;

import org.gwmdevelopments.sponge_plugin.library.GWMLibrary;
import org.gwmdevelopments.sponge_plugin.library.command.commands.HelpCommand;
import org.gwmdevelopments.sponge_plugin.library.command.commands.ReloadCommand;
import org.gwmdevelopments.sponge_plugin.library.command.commands.SaveCommand;
import org.gwmdevelopments.sponge_plugin.library.command.commands.SaveItemCommand;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class GWMLibraryCommandUtils {

    public static void registerCommands() {
        CommandSpec helpCommand = CommandSpec.builder().
                description(Text.of("Help command")).
                executor(new HelpCommand()).
                build();
        CommandSpec reloadCommand = CommandSpec.builder().
                permission("gwm_library.command.reload").
                description(Text.of("Reload plugin")).
                executor(new ReloadCommand()).
                build();
        CommandSpec saveCommand = CommandSpec.builder().
                permission("gwm_library.command.save").
                description(Text.of("Save plugin configs")).
                executor(new SaveCommand()).
                build();
        CommandSpec saveItemCommand = CommandSpec.builder().
                permission("gwm_library.command.save_item").
                description(Text.of("Save an item in hand to the config")).
                executor(new SaveItemCommand()).
                arguments(
                        GenericArguments.string(Text.of("path")),
                        GenericArguments.optional(GenericArguments.bool(Text.of("save-nbt")), false)
                ).
                build();
        CommandSpec spec = CommandSpec.builder().
                permission("gwm_crates.command").
                description(Text.of("Main plugin command.")).
                child(helpCommand, "help").
                child(reloadCommand, "reload").
                child(saveCommand, "save").
                child(saveItemCommand, "saveitem").
                build();
        Sponge.getCommandManager().register(GWMLibrary.getInstance(), spec,
                "gwmlibrary");
    }
}

