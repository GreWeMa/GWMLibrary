package org.gwmdevelopments.sponge_plugin.library.command;

import org.gwmdevelopments.sponge_plugin.library.GWMLibrary;
import org.gwmdevelopments.sponge_plugin.library.command.commands.BlockInfoCommand;
import org.gwmdevelopments.sponge_plugin.library.command.commands.HelpCommand;
import org.gwmdevelopments.sponge_plugin.library.command.commands.ReloadCommand;
import org.gwmdevelopments.sponge_plugin.library.command.commands.SaveCommand;
import org.spongepowered.api.Sponge;
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
        CommandSpec blockInfoCommand = CommandSpec.builder().
                permission("gwm_library.command.blockinfo").
                description(Text.of("Information about block you are looking at.")).
                executor(new BlockInfoCommand()).
                build();
        CommandSpec spec = CommandSpec.builder().
                permission("gwm_crates.command").
                description(Text.of("Main plugin command.")).
                child(helpCommand, "help").
                child(reloadCommand, "reload").
                child(saveCommand, "save").
                child(blockInfoCommand, "blockinfo").
                build();
        Sponge.getCommandManager().register(GWMLibrary.getInstance(), spec,
                "gwmlibrary");
    }
}

