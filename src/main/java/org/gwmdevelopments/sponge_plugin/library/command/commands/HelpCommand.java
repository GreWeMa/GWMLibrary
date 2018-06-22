package org.gwmdevelopments.sponge_plugin.library.command.commands;

import org.gwmdevelopments.sponge_plugin.library.GWMLibrary;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class HelpCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        src.sendMessages(GWMLibrary.getInstance().getLanguage().getTextList("HELP_MESSAGE",
                new Pair<>("%VERSION%", GWMLibrary.VERSION.toString())));
        return CommandResult.success();
    }
}
